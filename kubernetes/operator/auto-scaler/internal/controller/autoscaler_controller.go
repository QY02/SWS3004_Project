/*
Copyright 2024.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package controller

import (
	"bytes"
	"context"
	"database/sql"
	"fmt"
	"time"

	_ "github.com/go-sql-driver/mysql"

	"gopkg.in/yaml.v2"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	metav1 "k8s.io/apimachinery/pkg/apis/meta/v1"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/client-go/kubernetes"
	"k8s.io/client-go/kubernetes/scheme"
	"k8s.io/client-go/rest"
	"k8s.io/client-go/tools/remotecommand"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
	"sigs.k8s.io/controller-runtime/pkg/log"
	gatewayv1 "sigs.k8s.io/gateway-api/apis/v1"

	autoscalerv1 "github.com/qy02/SWS3004_Project/api/v1"
)

// AutoScalerReconciler reconciles a AutoScaler object
type AutoScalerReconciler struct {
	client.Client
	Scheme *runtime.Scheme
}

const (
	createdLabel  = "nus-cloud-project.auto-scaler/created"
	finalizerName = "nus-cloud-project.auto-scaler/finalizer"
)

var (
	currentMaxTableSize int
	ticker              *time.Ticker
	databaseScanOn      bool = false
	quit                chan struct{}
	currentReplica      int32 = 2
)

//+kubebuilder:rbac:groups=auto-scaler.nus-cloud-project,resources=autoscalers,verbs=get;list;watch;create;update;patch;delete
//+kubebuilder:rbac:groups=auto-scaler.nus-cloud-project,resources=autoscalers/status,verbs=get;update;patch
//+kubebuilder:rbac:groups=auto-scaler.nus-cloud-project,resources=autoscalers/finalizers,verbs=update

// Reconcile is part of the main kubernetes reconciliation loop which aims to
// move the current state of the cluster closer to the desired state.
// TODO(user): Modify the Reconcile function to compare the state specified by
// the AutoScaler object against the actual cluster state, and then
// perform operations to make the cluster state reflect the state specified by
// the user.
//
// For more details, check Reconcile and its Result here:
// - https://pkg.go.dev/sigs.k8s.io/controller-runtime@v0.16.3/pkg/reconcile
func (r *AutoScalerReconciler) Reconcile(ctx context.Context, req ctrl.Request) (ctrl.Result, error) {
	logger := log.FromContext(ctx)

	var autoScaler autoscalerv1.AutoScaler

	if err := r.Get(ctx, req.NamespacedName, &autoScaler); err != nil {
		if !errors.IsNotFound(err) {
			logger.Error(err, "unable to fetch AutoScaler")
		}
		return ctrl.Result{}, client.IgnoreNotFound(err)
	}

	_, isUpdate := autoScaler.Labels[createdLabel]
	if !isUpdate {
		fmt.Println("AutoScaler creation")
		if autoScaler.Labels == nil {
			autoScaler.Labels = make(map[string]string)
		}
		autoScaler.Labels[createdLabel] = "true"
		autoScaler.ObjectMeta.Finalizers = append(autoScaler.ObjectMeta.Finalizers, finalizerName)
		if err := r.Update(ctx, &autoScaler); err != nil {
			logger.Error(err, "failed to add createdLabel or finalizer")
			return ctrl.Result{}, client.IgnoreNotFound(err)
		}
		currentMaxTableSize = int(autoScaler.Spec.MaxTableSize)
		fmt.Printf("MaxTableSize set to %d\n", currentMaxTableSize)

		databaseScanOn = true
		timeInterval := 30 * time.Second
		ticker = time.NewTicker(timeInterval)
		quit = make(chan struct{})
		go func() {
			for {
				select {
				case t := <-ticker.C:
					fmt.Println("Tick at", t)

					if !databaseScanOn {
						ticker.Stop()
						return
					}

					fmt.Println("Scanning databases for user data")

					var statefulSetMySqlUser appsv1.StatefulSet
					if err := r.Get(ctx, client.ObjectKey{
						Namespace: req.Namespace,
						Name:      "stateful-set-mysql-user",
					}, &statefulSetMySqlUser); err != nil {
						if !errors.IsNotFound(err) {
							logger.Error(err, "unable to fetch StatefulSetMySqlUser")
						}
					}
					replicasMySqlUser := *statefulSetMySqlUser.Spec.Replicas

					for i := 0; i < int(replicasMySqlUser); i++ {
						database_address := fmt.Sprintf("root:madoh1#786gd$28q9asf@tcp(stateful-set-mysql-user-%d.headless-service-mysql-user.nus-cloud-project.svc.cluster.local:3306)/nus_cloud_project", i)
						database, err := sql.Open("mysql", database_address)
						if err != nil {
							logger.Error(err, "An error occur when connecting to database")
						}
						defer database.Close()
						result, err := database.Query("select count(*) from user;")
						if err != nil {
							logger.Error(err, "An error occur when querying")
						}
						result.Next()
						var rowCount int
						err = result.Scan(&rowCount)
						if err != nil {
							logger.Error(err, "An error occur when getting row count")
						}
						fmt.Printf("%d rows in database %d\n", rowCount, i)
						if rowCount > currentMaxTableSize {
							fmt.Println("Row count exceeds max value")
							ticker.Stop()

							result, err := database.Query("select max(temp.routing_hash) from (select cast(routing_hash as signed) as routing_hash from user order by cast(routing_hash as signed) limit ?) as temp;", rowCount/2)
							if err != nil {
								logger.Error(err, "An error occur when querying")
							}
							result.Next()
							var midHash int
							err = result.Scan(&midHash)
							if err != nil {
								logger.Error(err, "An error occur when getting mid hash")
								midHash = -1
							} else {
								midHash++
							}
							fmt.Printf("midHash = %d\n", midHash)

							createResult, rollBack := createNewUserDatabase(ctx, r, req)

							if createResult || rollBack {
								createResult, rollBack = createNewUserRedis(ctx, r, req, rollBack, i)
							}

							if createResult || rollBack {
								createResult, rollBack = createNewUserMicroservices(ctx, r, req, rollBack)
							}

							if createResult {
								createResult = createNewUserServices(ctx, r, req, currentReplica)
							}

							if createResult {
								createResult = createNewRulesInUserHttpRoute(ctx, r, req, currentReplica)
							}

							if createResult {
								createResult = createNewRulesInEventHttpRoute(ctx, r, req, currentReplica)
							}

							if createResult {
								startHash, endHash := splitRoutingRule(ctx, r, req, i, currentReplica, midHash)
								fmt.Printf("startHash = %d, midHash = %d, endHash = %d\n", startHash, midHash, endHash)

								if (startHash != -1) && (midHash != -1) && (endHash != -1) {
									refreshResult := refreshHashGenerateService(ctx, r, req)
									if refreshResult {
										copyResult := copyData(ctx, i, req, currentReplica, startHash, midHash, endHash)
										if copyResult {
											currentReplica++
										}
									}
								}
							}

							ticker = time.NewTicker(timeInterval)
							break
						}
					}

				case <-quit:
					fmt.Println("Ticker stopped")
					return
				}
			}
		}()

	} else {
		if autoScaler.DeletionTimestamp != nil && controllerutil.ContainsFinalizer(&autoScaler, finalizerName) {

			fmt.Println("AutoScaler deletion")

			controllerutil.RemoveFinalizer(&autoScaler, finalizerName)
			if err := r.Update(ctx, &autoScaler); err != nil {
				logger.Error(err, "failed to remove finalizer")
				return ctrl.Result{}, client.IgnoreNotFound(err)
			}

			databaseScanOn = false
			if ticker != nil {
				ticker.Stop()
				close(quit)
			}

		} else {
			fmt.Println("AutoScaler update")
			currentMaxTableSize = int(autoScaler.Spec.MaxTableSize)
			fmt.Printf("MaxTableSize set to %d\n", currentMaxTableSize)
		}
	}
	return ctrl.Result{}, nil
}

func createNewUserDatabase(ctx context.Context, r *AutoScalerReconciler, req ctrl.Request) (bool, bool) {
	logger := log.FromContext(ctx)
	var statefulSetMySqlUser appsv1.StatefulSet
	if err := r.Get(ctx, client.ObjectKey{
		Namespace: req.Namespace,
		Name:      "stateful-set-mysql-user",
	}, &statefulSetMySqlUser); err == nil {
		replicasMySqlUser := *statefulSetMySqlUser.Spec.Replicas
		if replicasMySqlUser != currentReplica {
			fmt.Println("Replicas is inconsistent, try to roll back")
			statefulSetMySqlUser.Spec.Replicas = &currentReplica
			if err := r.Update(ctx, &statefulSetMySqlUser); err == nil {
				fmt.Println("Roll back success")
			} else {
				logger.Error(err, "Fail to roll back")
			}
			return false, true
		}
		replicasMySqlUser++
		statefulSetMySqlUser.Spec.Replicas = &replicasMySqlUser
		newPodMySqlUserIndex := replicasMySqlUser - 1
		if err := r.Update(ctx, &statefulSetMySqlUser); err == nil {
			var newPodMySqlUser corev1.Pod
			newPodMySqlUserReady := false
			for j := 0; j < 30; j++ {
				if err := r.Get(ctx, client.ObjectKey{
					Namespace: req.Namespace,
					Name:      fmt.Sprintf("stateful-set-mysql-user-%d", newPodMySqlUserIndex),
				}, &newPodMySqlUser); err == nil {
					for _, condition := range newPodMySqlUser.Status.Conditions {
						if condition.Type == corev1.PodReady {
							if condition.Status == corev1.ConditionTrue {
								newPodMySqlUserReady = true
							}
							break
						}
					}
					if newPodMySqlUserReady {
						break
					}
				} else {
					fmt.Printf("try %d: unable to fetch NewPodMySqlUser\n", j)
				}
				time.Sleep(5 * time.Second)
			}
			if newPodMySqlUserReady {
				newDatabaseAddress := fmt.Sprintf("root:madoh1#786gd$28q9asf@tcp(stateful-set-mysql-user-%d.headless-service-mysql-user.nus-cloud-project.svc.cluster.local:3306)/nus_cloud_project", newPodMySqlUserIndex)
				newDatabase, err := sql.Open("mysql", newDatabaseAddress)
				if err == nil {
					defer newDatabase.Close()
					newDatabaseReady := false
					for j := 0; j < 30; j++ {
						if err := newDatabase.Ping(); err == nil {
							newDatabaseReady = true
							break
						}
						fmt.Printf("try %d: database is not ready\n", j)
						time.Sleep(5 * time.Second)
					}
					if newDatabaseReady {
						fmt.Println("Create new user database Success")
						return true, false
					} else {
						fmt.Println("NewDatabase start timeout")
					}
				} else {
					logger.Error(err, "An error occur when connecting to database")
				}
			} else {
				fmt.Println("NewPodMySqlUser start timeout")
				if err := r.Get(ctx, client.ObjectKey{
					Namespace: req.Namespace,
					Name:      "stateful-set-mysql-user",
				}, &statefulSetMySqlUser); err == nil {
					replicasMySqlUser--
					statefulSetMySqlUser.Spec.Replicas = &replicasMySqlUser
					if err := r.Update(ctx, &statefulSetMySqlUser); err == nil {
						fmt.Println("StatefulSetMySqlUser roll back success")
					} else {
						logger.Error(err, "Fail to roll back StatefulSetMySqlUser")
					}
				} else {
					logger.Error(err, "Fail to roll back StatefulSetMySqlUser")
				}
			}
		} else {
			logger.Error(err, "unable to update StatefulSetMySqlUser")
		}
	} else {
		if !errors.IsNotFound(err) {
			logger.Error(err, "unable to fetch StatefulSetMySqlUser")
		}
	}
	return false, false
}

func createNewUserRedis(ctx context.Context, r *AutoScalerReconciler, req ctrl.Request, rollBack bool, indexToSplit int) (bool, bool) {
	logger := log.FromContext(ctx)
	var statefulSetRedis appsv1.StatefulSet
	if err := r.Get(ctx, client.ObjectKey{
		Namespace: req.Namespace,
		Name:      "stateful-set-redis",
	}, &statefulSetRedis); err != nil {
		if !errors.IsNotFound(err) {
			logger.Error(err, "unable to fetch StatefulSetRedis")
		}
		return false, rollBack
	}
	replicasRedis := *statefulSetRedis.Spec.Replicas
	if rollBack || (replicasRedis != currentReplica) {
		fmt.Println("Replicas is inconsistent, try to roll back")
		statefulSetRedis.Spec.Replicas = &currentReplica
		if err := r.Update(ctx, &statefulSetRedis); err == nil {
			fmt.Println("Roll back success")
		} else {
			logger.Error(err, "Fail to roll back")
		}
		return false, true
	}
	replicasRedis++
	statefulSetRedis.Spec.Replicas = &replicasRedis
	newPodRedisIndex := replicasRedis - 1
	if err := r.Update(ctx, &statefulSetRedis); err != nil {
		logger.Error(err, "unable to update StatefulSetRedis")
		return false, false
	}
	var newPodRedis corev1.Pod
	newPodRedisReady := false
	for j := 0; j < 30; j++ {
		if err := r.Get(ctx, client.ObjectKey{
			Namespace: req.Namespace,
			Name:      fmt.Sprintf("stateful-set-redis-%d", newPodRedisIndex),
		}, &newPodRedis); err == nil {
			for _, condition := range newPodRedis.Status.Conditions {
				if condition.Type == corev1.PodReady {
					if condition.Status == corev1.ConditionTrue {
						newPodRedisReady = true
					}
					break
				}
			}
			if newPodRedisReady {
				break
			}
		} else {
			fmt.Printf("try %d: unable to fetch NewPodRedis\n", j)
		}
		time.Sleep(5 * time.Second)
	}
	if !newPodRedisReady {
		fmt.Println("NewPodRedis start timeout")
		if err := r.Get(ctx, client.ObjectKey{
			Namespace: req.Namespace,
			Name:      "stateful-set-redis",
		}, &statefulSetRedis); err == nil {
			replicasRedis--
			statefulSetRedis.Spec.Replicas = &replicasRedis
			if err := r.Update(ctx, &statefulSetRedis); err == nil {
				fmt.Println("StatefulSetRedis roll back success")
			} else {
				logger.Error(err, "Fail to roll back StatefulSetRedis")
			}
		} else {
			logger.Error(err, "Fail to roll back StatefulSetRedis")
		}
		return false, false
	}
	config, err := rest.InClusterConfig()
	if err != nil {
		logger.Error(err, "cannot get in cluster config")
		return false, false
	}
	clientInstance, err := kubernetes.NewForConfig(config)
	if err != nil {
		logger.Error(err, "cannot create client instance")
		return false, false
	}
	command := []string{"/bin/sh", "-c", fmt.Sprintf("redis-cli -h stateful-set-redis-%d.headless-service-redis.nus-cloud-project.svc.cluster.local -p 6379 SAVE && redis-cli -h stateful-set-redis-%d.headless-service-redis.nus-cloud-project.svc.cluster.local -p 6379 --rdb /data/dump.rdb && (redis-cli shutdown nosave || true)", indexToSplit, indexToSplit)}
	apiRequest := clientInstance.CoreV1().RESTClient().Post().Resource("pods").Namespace(req.Namespace).
		Name(fmt.Sprintf("stateful-set-redis-%d", newPodRedisIndex)).SubResource("exec").
		VersionedParams(&corev1.PodExecOptions{
			Command:   command,
			Container: "events-center-redis",
			Stdin:     false,
			Stdout:    true,
			Stderr:    true,
			TTY:       false,
		}, scheme.ParameterCodec)
	executor, err := remotecommand.NewSPDYExecutor(config, "POST", apiRequest.URL())
	if err != nil {
		logger.Error(err, "cannot create connection")
		return false, false
	}
	var commandStdout bytes.Buffer
	var commandStderr bytes.Buffer
	err = executor.StreamWithContext(ctx, remotecommand.StreamOptions{
		Stdout: &commandStdout,
		Stderr: &commandStderr,
		Tty:    false,
	})
	fmt.Println("Copy redis data success")
	fmt.Println("Command std out: " + commandStdout.String())
	fmt.Println("Command std err: " + commandStderr.String())

	return true, false
}

func createNewUserMicroservices(ctx context.Context, r *AutoScalerReconciler, req ctrl.Request, rollBack bool) (bool, bool) {
	logger := log.FromContext(ctx)
	var statefulSetRegister appsv1.StatefulSet
	if err := r.Get(ctx, client.ObjectKey{
		Namespace: req.Namespace,
		Name:      "stateful-set-register",
	}, &statefulSetRegister); err == nil {
		replicasRegister := *statefulSetRegister.Spec.Replicas
		if replicasRegister != currentReplica {
			fmt.Println("Replicas is inconsistent, try to roll back")
			statefulSetRegister.Spec.Replicas = &currentReplica
			if err := r.Update(ctx, &statefulSetRegister); err == nil {
				fmt.Println("Roll back success")
			} else {
				logger.Error(err, "Fail to roll back")
			}
			return false, true
		}
		replicasRegister++
		statefulSetRegister.Spec.Replicas = &replicasRegister
		if err := r.Update(ctx, &statefulSetRegister); err == nil {
			fmt.Println("Increase replica for register success")
		} else {
			logger.Error(err, "unable to update StatefulSetRegister")
			return false, false
		}
	} else {
		if !errors.IsNotFound(err) {
			logger.Error(err, "unable to fetch StatefulSetRegister")
			return false, rollBack
		}
	}

	var statefulSetLogin appsv1.StatefulSet
	if err := r.Get(ctx, client.ObjectKey{
		Namespace: req.Namespace,
		Name:      "stateful-set-login",
	}, &statefulSetLogin); err == nil {
		replicasLogin := *statefulSetLogin.Spec.Replicas
		if replicasLogin != currentReplica {
			fmt.Println("Replicas is inconsistent, try to roll back")
			statefulSetLogin.Spec.Replicas = &currentReplica
			if err := r.Update(ctx, &statefulSetLogin); err == nil {
				fmt.Println("Roll back success")
			} else {
				logger.Error(err, "Fail to roll back")
			}
			return false, true
		}
		replicasLogin++
		statefulSetLogin.Spec.Replicas = &replicasLogin
		if err := r.Update(ctx, &statefulSetLogin); err == nil {
			fmt.Println("Increase replica for login success")
		} else {
			logger.Error(err, "unable to update StatefulSetLogin")
			return false, false
		}
	} else {
		if !errors.IsNotFound(err) {
			logger.Error(err, "unable to fetch StatefulSetLogin")
			return false, rollBack
		}
	}

	var statefulSetOrderRecord appsv1.StatefulSet
	if err := r.Get(ctx, client.ObjectKey{
		Namespace: req.Namespace,
		Name:      "stateful-set-order-record",
	}, &statefulSetOrderRecord); err == nil {
		replicasOrderRecord := *statefulSetOrderRecord.Spec.Replicas
		if replicasOrderRecord != currentReplica {
			fmt.Println("Replicas is inconsistent, try to roll back")
			statefulSetOrderRecord.Spec.Replicas = &currentReplica
			if err := r.Update(ctx, &statefulSetOrderRecord); err == nil {
				fmt.Println("Roll back success")
			} else {
				logger.Error(err, "Fail to roll back")
			}
			return false, true
		}
		replicasOrderRecord++
		statefulSetOrderRecord.Spec.Replicas = &replicasOrderRecord
		if err := r.Update(ctx, &statefulSetOrderRecord); err == nil {
			fmt.Println("Increase replica for order record success")
		} else {
			logger.Error(err, "unable to update StatefulSetOrderRecord")
			return false, false
		}
	} else {
		if !errors.IsNotFound(err) {
			logger.Error(err, "unable to fetch StatefulSetOrderRecord")
			return false, rollBack
		}
	}

	var statefulSetTokenVerification appsv1.StatefulSet
	if err := r.Get(ctx, client.ObjectKey{
		Namespace: req.Namespace,
		Name:      "stateful-set-token-verification",
	}, &statefulSetTokenVerification); err == nil {
		replicasTokenVerification := *statefulSetTokenVerification.Spec.Replicas
		if replicasTokenVerification != currentReplica {
			fmt.Println("Replicas is inconsistent, try to roll back")
			statefulSetTokenVerification.Spec.Replicas = &currentReplica
			if err := r.Update(ctx, &statefulSetTokenVerification); err == nil {
				fmt.Println("Roll back success")
			} else {
				logger.Error(err, "Fail to roll back")
			}
			return false, true
		}
		replicasTokenVerification++
		statefulSetTokenVerification.Spec.Replicas = &replicasTokenVerification
		if err := r.Update(ctx, &statefulSetTokenVerification); err == nil {
			fmt.Println("Increase replica for token verification success")
		} else {
			logger.Error(err, "unable to update StatefulSetTokenVerification")
			return false, false
		}
	} else {
		if !errors.IsNotFound(err) {
			logger.Error(err, "unable to fetch StatefulSetTokenVerification")
			return false, rollBack
		}
	}
	return true, false
}

func createNewUserServices(ctx context.Context, r *AutoScalerReconciler, req ctrl.Request, newPodIndex int32) bool {
	logger := log.FromContext(ctx)
	newRegisterService := corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: req.Namespace,
			Name:      fmt.Sprintf("cluster-ip-service-register-%d", newPodIndex),
		},
		Spec: corev1.ServiceSpec{
			Selector: map[string]string{
				"backend":                      "register",
				"apps.kubernetes.io/pod-index": fmt.Sprintf("%d", newPodIndex),
			},
			Ports: []corev1.ServicePort{
				{
					Port: 25561,
				},
			},
		},
	}
	if err := r.Create(ctx, &newRegisterService); client.IgnoreAlreadyExists(err) != nil {
		logger.Error(err, "unable to create NewRegisterService")
		return false
	}
	fmt.Println("Create NewRegisterService success")

	newLoginService := corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: req.Namespace,
			Name:      fmt.Sprintf("cluster-ip-service-login-%d", newPodIndex),
		},
		Spec: corev1.ServiceSpec{
			Selector: map[string]string{
				"backend":                      "login",
				"apps.kubernetes.io/pod-index": fmt.Sprintf("%d", newPodIndex),
			},
			Ports: []corev1.ServicePort{
				{
					Port: 25560,
				},
			},
		},
	}
	if err := r.Create(ctx, &newLoginService); client.IgnoreAlreadyExists(err) != nil {
		logger.Error(err, "unable to create NewLoginService")
		return false
	}
	fmt.Println("Create NewLoginService success")

	newOrderRecordService := corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: req.Namespace,
			Name:      fmt.Sprintf("cluster-ip-service-order-record-%d", newPodIndex),
		},
		Spec: corev1.ServiceSpec{
			Selector: map[string]string{
				"backend":                      "order-record",
				"apps.kubernetes.io/pod-index": fmt.Sprintf("%d", newPodIndex),
			},
			Ports: []corev1.ServicePort{
				{
					Port: 25566,
				},
			},
		},
	}
	if err := r.Create(ctx, &newOrderRecordService); client.IgnoreAlreadyExists(err) != nil {
		logger.Error(err, "unable to create NewOrderRecordService")
		return false
	}
	fmt.Println("Create NewOrderRecordService success")

	newTokenVerificationService := corev1.Service{
		ObjectMeta: metav1.ObjectMeta{
			Namespace: req.Namespace,
			Name:      fmt.Sprintf("cluster-ip-service-token-verification-%d", newPodIndex),
		},
		Spec: corev1.ServiceSpec{
			Selector: map[string]string{
				"backend":                      "token-verification",
				"apps.kubernetes.io/pod-index": fmt.Sprintf("%d", newPodIndex),
			},
			Ports: []corev1.ServicePort{
				{
					Port: 25562,
				},
			},
		},
	}
	if err := r.Create(ctx, &newTokenVerificationService); client.IgnoreAlreadyExists(err) != nil {
		logger.Error(err, "unable to create NewTokenVerificationService")
		return false
	}
	fmt.Println("Create NewTokenVerificationService success")

	return true
}

func createNewRulesInUserHttpRoute(ctx context.Context, r *AutoScalerReconciler, req ctrl.Request, newPodIndex int32) bool {
	logger := log.FromContext(ctx)
	var userHttpRules gatewayv1.HTTPRoute
	if err := r.Get(ctx, client.ObjectKey{
		Namespace: req.Namespace,
		Name:      "http-route-user",
	}, &userHttpRules); err != nil {
		logger.Error(err, "unable to fetch UserHttpRules")
		return false
	}

	newRuleList := []gatewayv1.HTTPRouteRule{
		{
			Matches: []gatewayv1.HTTPRouteMatch{
				{
					Path: &gatewayv1.HTTPPathMatch{
						Type:  createPointer(gatewayv1.PathMatchPathPrefix),
						Value: createPointer("/register"),
					},
					Headers: []gatewayv1.HTTPHeaderMatch{
						{
							Type:  createPointer(gatewayv1.HeaderMatchExact),
							Name:  gatewayv1.HTTPHeaderName("routingIndex"),
							Value: fmt.Sprintf("%d", newPodIndex),
						},
					},
				},
			},
			BackendRefs: []gatewayv1.HTTPBackendRef{
				{
					BackendRef: gatewayv1.BackendRef{
						BackendObjectReference: gatewayv1.BackendObjectReference{
							Name: gatewayv1.ObjectName(fmt.Sprintf("cluster-ip-service-register-%d", newPodIndex)),
							Port: createPointer(gatewayv1.PortNumber(25561)),
						},
					},
				},
			},
		},
		{
			Matches: []gatewayv1.HTTPRouteMatch{
				{
					Path: &gatewayv1.HTTPPathMatch{
						Type:  createPointer(gatewayv1.PathMatchPathPrefix),
						Value: createPointer("/login"),
					},
					Headers: []gatewayv1.HTTPHeaderMatch{
						{
							Type:  createPointer(gatewayv1.HeaderMatchExact),
							Name:  gatewayv1.HTTPHeaderName("routingIndex"),
							Value: fmt.Sprintf("%d", newPodIndex),
						},
					},
				},
			},
			BackendRefs: []gatewayv1.HTTPBackendRef{
				{
					BackendRef: gatewayv1.BackendRef{
						BackendObjectReference: gatewayv1.BackendObjectReference{
							Name: gatewayv1.ObjectName(fmt.Sprintf("cluster-ip-service-login-%d", newPodIndex)),
							Port: createPointer(gatewayv1.PortNumber(25560)),
						},
					},
				},
			},
		},
		{
			Matches: []gatewayv1.HTTPRouteMatch{
				{
					Path: &gatewayv1.HTTPPathMatch{
						Type:  createPointer(gatewayv1.PathMatchPathPrefix),
						Value: createPointer("/"),
					},
					Headers: []gatewayv1.HTTPHeaderMatch{
						{
							Type:  createPointer(gatewayv1.HeaderMatchExact),
							Name:  gatewayv1.HTTPHeaderName("routingIndex"),
							Value: fmt.Sprintf("%d", newPodIndex),
						},
					},
				},
			},
			BackendRefs: []gatewayv1.HTTPBackendRef{
				{
					BackendRef: gatewayv1.BackendRef{
						BackendObjectReference: gatewayv1.BackendObjectReference{
							Name: gatewayv1.ObjectName(fmt.Sprintf("cluster-ip-service-token-verification-%d", newPodIndex)),
							Port: createPointer(gatewayv1.PortNumber(25562)),
						},
					},
				},
			},
		},
	}

	userHttpRules.Spec.Rules = append(userHttpRules.Spec.Rules, newRuleList...)

	if err := r.Update(ctx, &userHttpRules); err != nil {
		logger.Error(err, "unable to update UserHttpRules")
		return false
	}

	fmt.Println("Creating new rules in user http route success")
	return true
}

func createNewRulesInEventHttpRoute(ctx context.Context, r *AutoScalerReconciler, req ctrl.Request, newPodIndex int32) bool {
	logger := log.FromContext(ctx)
	var eventHttpRules gatewayv1.HTTPRoute
	if err := r.Get(ctx, client.ObjectKey{
		Namespace: req.Namespace,
		Name:      "http-route-event",
	}, &eventHttpRules); err != nil {
		logger.Error(err, "unable to fetch EventHttpRules")
		return false
	}

	newRuleList := []gatewayv1.HTTPRouteRule{
		{
			Matches: []gatewayv1.HTTPRouteMatch{
				{
					Path: &gatewayv1.HTTPPathMatch{
						Type:  createPointer(gatewayv1.PathMatchPathPrefix),
						Value: createPointer("/orderRecord"),
					},
					Headers: []gatewayv1.HTTPHeaderMatch{
						{
							Type:  createPointer(gatewayv1.HeaderMatchExact),
							Name:  gatewayv1.HTTPHeaderName("routingIndex"),
							Value: fmt.Sprintf("%d", newPodIndex),
						},
					},
				},
			},
			BackendRefs: []gatewayv1.HTTPBackendRef{
				{
					BackendRef: gatewayv1.BackendRef{
						BackendObjectReference: gatewayv1.BackendObjectReference{
							Name: gatewayv1.ObjectName(fmt.Sprintf("cluster-ip-service-order-record-%d", newPodIndex)),
							Port: createPointer(gatewayv1.PortNumber(25566)),
						},
					},
				},
			},
		},
	}

	eventHttpRules.Spec.Rules = append(eventHttpRules.Spec.Rules, newRuleList...)

	if err := r.Update(ctx, &eventHttpRules); err != nil {
		logger.Error(err, "unable to update EventHttpRules")
		return false
	}

	fmt.Println("Creating new rules in event http route success")
	return true
}

func copyData(ctx context.Context, indexToSplit int, req ctrl.Request, newPodMySqlUserIndex int32, startHash int, midHash int, endHash int) bool {
	logger := log.FromContext(ctx)
	if config, err := rest.InClusterConfig(); err == nil {
		if clientInstance, err := kubernetes.NewForConfig(config); err == nil {
			command := []string{"/bin/sh", "-c", fmt.Sprintf("mysqldump -h stateful-set-mysql-user-%d.headless-service-mysql-user.nus-cloud-project.svc.cluster.local -P 3306 -u root -p'madoh1#786gd$28q9asf' nus_cloud_project | mysql -h localhost -P 3306 -u root -p'madoh1#786gd$28q9asf' nus_cloud_project", indexToSplit)}
			apiRequest := clientInstance.CoreV1().RESTClient().Post().Resource("pods").Namespace(req.Namespace).
				Name(fmt.Sprintf("stateful-set-mysql-user-%d", newPodMySqlUserIndex)).SubResource("exec").
				VersionedParams(&corev1.PodExecOptions{
					Command:   command,
					Container: "ebs-containers-mysql",
					Stdin:     false,
					Stdout:    true,
					Stderr:    true,
					TTY:       false,
				}, scheme.ParameterCodec)
			if executor, err := remotecommand.NewSPDYExecutor(config, "POST", apiRequest.URL()); err == nil {
				var commandStdout bytes.Buffer
				var commandStderr bytes.Buffer
				err = executor.StreamWithContext(ctx, remotecommand.StreamOptions{
					Stdout: &commandStdout,
					Stderr: &commandStderr,
					Tty:    false,
				})
				if err == nil {
					fmt.Println("Copy user database success")
					fmt.Println("Command std out: " + commandStdout.String())
					fmt.Println("Command std err: " + commandStderr.String())

					return cleanUpData(ctx, indexToSplit, newPodMySqlUserIndex, startHash, midHash, endHash)

				} else {
					logger.Error(err, "command execution failed")
					fmt.Println("Command std out: " + commandStdout.String())
					fmt.Println("Command std err: " + commandStderr.String())
				}
			} else {
				logger.Error(err, "cannot create connection")
			}
		} else {
			logger.Error(err, "cannot create client instance")
		}
	} else {
		logger.Error(err, "cannot get in cluster config")
	}
	return false
}

func cleanUpData(ctx context.Context, indexToSplit int, newPodMySqlUserIndex int32, startHash int, midHash int, endHash int) bool {
	logger := log.FromContext(ctx)

	cleanResultOriginal := false
	cleanResultNew := false

	fmt.Println("Start clean up data in original database")
	databaseToSplitAddress := fmt.Sprintf("root:madoh1#786gd$28q9asf@tcp(stateful-set-mysql-user-%d.headless-service-mysql-user.nus-cloud-project.svc.cluster.local:3306)/nus_cloud_project", indexToSplit)
	databaseToSplit, err := sql.Open("mysql", databaseToSplitAddress)
	if err == nil {
		defer databaseToSplit.Close()
		_, err1 := databaseToSplit.Exec("delete from order_record where cast(substring(full_user_id, 1, 8) as signed) not between ? and ?;", startHash, midHash-1)
		if err1 != nil {
			logger.Error(err1, "An error occur when cleaning up data in table order_record")
		}
		_, err2 := databaseToSplit.Exec("delete from user where cast(routing_hash as signed) not between ? and ?;", startHash, midHash-1)
		if err2 != nil {
			logger.Error(err1, "An error occur when cleaning up data in table user")
		}
		if (err1 == nil) && (err2 == nil) {
			fmt.Println("Clean up data in original database success")
			cleanResultOriginal = true
		}
	} else {
		logger.Error(err, "An error occur when connecting to database")
	}

	fmt.Println("Start clean up data in new database")
	newDatabaseAddress := fmt.Sprintf("root:madoh1#786gd$28q9asf@tcp(stateful-set-mysql-user-%d.headless-service-mysql-user.nus-cloud-project.svc.cluster.local:3306)/nus_cloud_project", newPodMySqlUserIndex)
	newDatabase, err := sql.Open("mysql", newDatabaseAddress)
	if err == nil {
		defer newDatabase.Close()
		_, err1 := newDatabase.Exec("delete from order_record where cast(substring(full_user_id, 1, 8) as signed) not between ? and ?;", midHash, endHash-1)
		if err1 != nil {
			logger.Error(err1, "An error occur when cleaning up data in table order_record")
		}
		_, err2 := newDatabase.Exec("delete from user where cast(routing_hash as signed) not between ? and ?;", midHash, endHash-1)
		if err2 != nil {
			logger.Error(err1, "An error occur when cleaning up data in table user")
		}
		if (err1 == nil) && (err2 == nil) {
			fmt.Println("Clean up data in new database success")
			cleanResultNew = true
		}
	} else {
		logger.Error(err, "An error occur when connecting to database")
	}
	if cleanResultOriginal && cleanResultNew {
		return true
	} else {
		return false
	}
}

func splitRoutingRule(ctx context.Context, r *AutoScalerReconciler, req ctrl.Request, indexToSplit int, newPodIndex int32, midHash int) (int, int) {
	logger := log.FromContext(ctx)
	var routingRuleConfigMap corev1.ConfigMap
	if err := r.Get(ctx, client.ObjectKey{
		Namespace: req.Namespace,
		Name:      "backend-user-routing-hash-generate-springboot-config",
	}, &routingRuleConfigMap); err == nil {
		applicationYaml := routingRuleConfigMap.Data["application.yaml"]
		var applicationYamlMap map[string]interface{}
		if err := yaml.Unmarshal([]byte(applicationYaml), &applicationYamlMap); err == nil {
			routingRuleList, _ := applicationYamlMap["routingRuleList"].([]interface{})
			for indexInList, rule := range routingRuleList {
				rule, _ := rule.(map[interface{}]interface{})
				index, _ := rule["index"].(int)

				if index == indexToSplit {
					startHash, ok1 := rule["startHash"].(int)
					endHash, ok2 := rule["endHash"].(int)
					if ok1 && ok2 && endHash-startHash > 1 {
						splitRule1 := map[string]interface{}{
							"startHash": startHash,
							"endHash":   midHash,
							"index":     indexToSplit,
						}
						splitRule2 := map[string]interface{}{
							"startHash": midHash,
							"endHash":   endHash,
							"index":     newPodIndex,
						}
						routingRuleList = append(routingRuleList[:indexInList], routingRuleList[indexInList+1:]...)
						routingRuleList = append(routingRuleList, splitRule1, splitRule2)
						applicationYamlMap["routingRuleList"] = routingRuleList
						newApplicationYaml, _ := yaml.Marshal(applicationYamlMap)
						routingRuleConfigMap.Data["application.yaml"] = string(newApplicationYaml)
						if err := r.Update(ctx, &routingRuleConfigMap); err != nil {
							logger.Error(err, "unable to update RoutingRuleConfigMap")
						} else {
							return startHash, endHash
						}
					}
					break
				}
			}
		} else {
			logger.Error(err, "unable to parse application")
		}
	} else {
		logger.Error(err, "unable to fetch RoutingRuleConfigMap")
	}
	return -1, -1
}

func refreshHashGenerateService(ctx context.Context, r *AutoScalerReconciler, req ctrl.Request) bool {
	logger := log.FromContext(ctx)
	var deploymentHashGenerate appsv1.Deployment
	if err := r.Get(ctx, client.ObjectKey{
		Namespace: req.Namespace,
		Name:      "deployment-user-routing-hash-generate",
	}, &deploymentHashGenerate); err != nil {
		logger.Error(err, "unable to fetch DeploymentHashGenerate")
		return false
	}

	replicasHashGenerate := *deploymentHashGenerate.Spec.Replicas

	deploymentHashGenerate.Spec.Replicas = createPointer(int32(0))

	if err := r.Update(ctx, &deploymentHashGenerate); err != nil {
		logger.Error(err, "unable to update DeploymentHashGenerate")
		return false
	}

	refreshSuccess := false
	for i := 0; i < 30; i++ {
		time.Sleep(5 * time.Second)
		if err := r.Get(ctx, client.ObjectKey{
			Namespace: req.Namespace,
			Name:      "deployment-user-routing-hash-generate",
		}, &deploymentHashGenerate); err != nil {
			fmt.Printf("try %d: unable to fetch DeploymentHashGenerate\n", i)
			continue
		}

		deploymentHashGenerate.Spec.Replicas = &replicasHashGenerate

		if err := r.Update(ctx, &deploymentHashGenerate); err != nil {
			fmt.Printf("try %d:unable to update DeploymentHashGenerate\n", i)
			continue
		}

		refreshSuccess = true
		break
	}

	if !refreshSuccess {
		fmt.Println("Unable to refresh hash generate service")
		return false
	} else {
		fmt.Println("Refresh hash generate service success")
		return true
	}
}

// SetupWithManager sets up the controller with the Manager.
func (r *AutoScalerReconciler) SetupWithManager(mgr ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(mgr).
		For(&autoscalerv1.AutoScaler{}).
		Complete(r)
}

func createPointer[T any](v T) *T {
	return &v
}
