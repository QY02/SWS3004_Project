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
	"context"
	"database/sql"
	"fmt"
	"time"

	_ "github.com/go-sql-driver/mysql"

	"gopkg.in/yaml.v2"
	appsv1 "k8s.io/api/apps/v1"
	corev1 "k8s.io/api/core/v1"
	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/runtime"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
	"sigs.k8s.io/controller-runtime/pkg/log"

	autoscalerv1 "github.com/qy02/SWS3004_Project/api/v1"
	// v1 "github.com/qy02/SWS3004_Project/api/v1"
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
	quit                chan struct{}
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

	fmt.Println("Hello, World!")

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

		//TODO: use database index
		//TODO: get correct index
		midHash := splitRoutingRule(ctx, r, req)
		fmt.Printf("midHash = %d\n", midHash)

		var statefulSetMySqlUser appsv1.StatefulSet
		if err := r.Get(ctx, client.ObjectKey{
			Namespace: req.Namespace,
			Name:      "stateful-set-mysql-user",
		}, &statefulSetMySqlUser); err == nil {
			replicasMySqlUser := *statefulSetMySqlUser.Spec.Replicas
			replicasMySqlUser++
			statefulSetMySqlUser.Spec.Replicas = &replicasMySqlUser
			newPodMySqlUserIndex := replicasMySqlUser - 1
			if err := r.Update(ctx, &statefulSetMySqlUser); err == nil {
				var newPodMySqlUser corev1.Pod
				newPodMySqlUserReady := false
				for j := 0; j < 10; j++ {
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
						logger.Error(err, "unable to fetch NewPodMySqlUser")
					}
					time.Sleep(5 * time.Second)
				}
				if newPodMySqlUserReady {
					newDatabaseAddress := fmt.Sprintf("root:madoh1#786gd$28q9asf@tcp(stateful-set-mysql-user-%d.headless-service-mysql-user.nus-cloud-project.svc.cluster.local:3306)/nus_cloud_project", newPodMySqlUserIndex)
					newDatabase, err := sql.Open("mysql", newDatabaseAddress)
					if err == nil {
						defer newDatabase.Close()
						newDatabaseReady := false
						for j := 0; j < 10; j++ {
							if err := newDatabase.Ping(); err == nil {
								newDatabaseReady = true
								break
							}
						}
						if newDatabaseReady {
							fmt.Println("Successs!!!!!")
						} else {
							fmt.Println("NewDatabase start timeout")
						}
					} else {
						logger.Error(err, "An error occur when connecting to database")
					}
				} else {
					fmt.Println("NewPodMySqlUser start timeout")
				}
			} else {
				logger.Error(err, "unable to update StatefulSetMySqlUser")
			}
		} else {
			if !errors.IsNotFound(err) {
				logger.Error(err, "unable to fetch StatefulSetMySqlUser")
			}
		}

		// ticker = time.NewTicker(30 * time.Second)
		// quit = make(chan struct{})
		// go func() {
		// 	for {
		// 		select {
		// 		case t := <-ticker.C:
		// 			fmt.Println("Tick at", t)

		// 			fmt.Println("Scanning databases for user data")

		// 			var statefulSetMySqlUser appsv1.StatefulSet
		// 			if err := r.Get(ctx, client.ObjectKey{
		// 				Namespace: req.Namespace,
		// 				Name:      "stateful-set-mysql-user",
		// 			}, &statefulSetMySqlUser); err != nil {
		// 				if !errors.IsNotFound(err) {
		// 					logger.Error(err, "unable to fetch StatefulSetMySqlUser")
		// 				}
		// 			}
		// 			replicasMySqlUser := *statefulSetMySqlUser.Spec.Replicas

		// 			for i := 0; i < int(replicasMySqlUser); i++ {
		// 				database_address := fmt.Sprintf("root:madoh1#786gd$28q9asf@tcp(stateful-set-mysql-user-%d.headless-service-mysql-user.nus-cloud-project.svc.cluster.local:3306)/nus_cloud_project", i)
		// 				database, err := sql.Open("mysql", database_address)
		// 				if err != nil {
		// 					logger.Error(err, "An error occur when connecting to database")
		// 				}
		// 				defer database.Close()
		// 				result, err := database.Query("select count(*) from user;")
		// 				if err != nil {
		// 					logger.Error(err, "An error occur when querying")
		// 				}
		// 				result.Next()
		// 				var rowCount int
		// 				err = result.Scan(&rowCount)
		// 				if err != nil {
		// 					logger.Error(err, "An error occur when getting row count")
		// 				}
		// 				fmt.Printf("%d rows in database %d\n", rowCount, i)
		// 				if rowCount > currentMaxTableSize {
		// 					fmt.Println("Row count exceeds max value")
		// 				}
		// 			}

		// 			fmt.Println("Scanning databases for event detailed data")

		// 			var statefulSetMySqlEventDetailedData appsv1.StatefulSet
		// 			if err := r.Get(ctx, client.ObjectKey{
		// 				Namespace: req.Namespace,
		// 				Name:      "stateful-set-mysql-event-detailed-data",
		// 			}, &statefulSetMySqlEventDetailedData); err != nil {
		// 				if !errors.IsNotFound(err) {
		// 					logger.Error(err, "unable to fetch StatefulSetMySqlEventDetailedData")
		// 				}
		// 			}
		// 			replicasMySqlEventDetailedData := *statefulSetMySqlEventDetailedData.Spec.Replicas

		// 			for i := 0; i < int(replicasMySqlEventDetailedData); i++ {
		// 				database_address := fmt.Sprintf("root:madoh1#786gd$28q9asf@tcp(stateful-set-mysql-event-detailed-data-%d.headless-service-mysql-event-detailed-data.nus-cloud-project.svc.cluster.local:3306)/nus_cloud_project", i)
		// 				database, err := sql.Open("mysql", database_address)
		// 				if err != nil {
		// 					logger.Error(err, "An error occur when connecting to database")
		// 				}
		// 				defer database.Close()
		// 				result, err := database.Query("select count(*) from event;")
		// 				if err != nil {
		// 					logger.Error(err, "An error occur when querying")
		// 				}
		// 				result.Next()
		// 				var rowCount int
		// 				err = result.Scan(&rowCount)
		// 				if err != nil {
		// 					logger.Error(err, "An error occur when getting row count")
		// 				}
		// 				fmt.Printf("%d rows in database %d\n", rowCount, i)
		// 				if rowCount > currentMaxTableSize {
		// 					fmt.Println("Row count exceeds max value")
		// 				}
		// 			}

		// 		case <-quit:
		// 			fmt.Println("Ticker stopped")
		// 			return
		// 		}
		// 	}
		// }()

	} else {
		if autoScaler.DeletionTimestamp != nil && controllerutil.ContainsFinalizer(&autoScaler, finalizerName) {

			fmt.Println("AutoScaler deletion")

			controllerutil.RemoveFinalizer(&autoScaler, finalizerName)
			if err := r.Update(ctx, &autoScaler); err != nil {
				logger.Error(err, "failed to remove finalizer")
				return ctrl.Result{}, client.IgnoreNotFound(err)
			}

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

func splitRoutingRule(ctx context.Context, r *AutoScalerReconciler, req ctrl.Request) int {
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

				if index == 0 {
					startHash, ok1 := rule["startHash"].(int)
					endHash, ok2 := rule["endHash"].(int)
					if ok1 && ok2 && endHash-startHash > 1 {
						midHash := (endHash - startHash) / 2

						splitRule1 := map[string]interface{}{
							"startHash": startHash,
							"endHash":   midHash,
							"index":     0,
						}
						splitRule2 := map[string]interface{}{
							"startHash": midHash,
							"endHash":   endHash,
							"index":     2,
						}
						routingRuleList = append(routingRuleList[:indexInList], routingRuleList[indexInList+1:]...)
						routingRuleList = append(routingRuleList, splitRule1, splitRule2)
						applicationYamlMap["routingRuleList"] = routingRuleList
						newApplicationYaml, _ := yaml.Marshal(applicationYamlMap)
						routingRuleConfigMap.Data["application.yaml"] = string(newApplicationYaml)
						if err := r.Update(ctx, &routingRuleConfigMap); err != nil {
							logger.Error(err, "unable to update RoutingRuleConfigMap")
						} else {
							return midHash
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
	return -1
}

// SetupWithManager sets up the controller with the Manager.
func (r *AutoScalerReconciler) SetupWithManager(mgr ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(mgr).
		For(&autoscalerv1.AutoScaler{}).
		Complete(r)
}
