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
	_ "github.com/go-sql-driver/mysql"
	"time"

	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/runtime"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/controller/controllerutil"
	"sigs.k8s.io/controller-runtime/pkg/log"

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

		ticker = time.NewTicker(2 * time.Second)
		quit = make(chan struct{})
		go func() {
			for {
				select {
				case t := <-ticker.C:
					fmt.Println("Tick at", t)
					database_address := "root:madoh1#786gd$28q9asf@tcp(stateful-set-mysql-user-0.stateful-set-mysql-user.nus-cloud-project.svc.cluster.local:3306)/nus_cloud_project"
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
					fmt.Printf("%d rows", rowCount)
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

// SetupWithManager sets up the controller with the Manager.
func (r *AutoScalerReconciler) SetupWithManager(mgr ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(mgr).
		For(&autoscalerv1.AutoScaler{}).
		Complete(r)
}
