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
	"fmt"

	"k8s.io/apimachinery/pkg/api/errors"
	"k8s.io/apimachinery/pkg/runtime"
	"k8s.io/client-go/util/retry"
	ctrl "sigs.k8s.io/controller-runtime"
	"sigs.k8s.io/controller-runtime/pkg/client"
	"sigs.k8s.io/controller-runtime/pkg/log"

	appsv1 "k8s.io/api/apps/v1"
	autoscalingv1alpha1 "github.com/shinnryuu-github/operator/api/v1alpha1"
)

// AutoScalerReconciler reconciles a AutoScaler object
type AutoScalerReconciler struct {
	client.Client
	Scheme *runtime.Scheme
}

//+kubebuilder:rbac:groups=autoscaling.my.domain,resources=autoscalers,verbs=get;list;watch;create;update;patch;delete
//+kubebuilder:rbac:groups=autoscaling.my.domain,resources=autoscalers/status,verbs=get;update;patch
//+kubebuilder:rbac:groups=autoscaling.my.domain,resources=autoscalers/finalizers,verbs=update

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
    _ = log.FromContext(ctx)

    // Fetch the AutoScaler instance
    autoScaler := &autoscalingv1alpha1.AutoScaler{}
    err := r.Get(ctx, req.NamespacedName, autoScaler)
    if err != nil {
        if errors.IsNotFound(err) {
            return ctrl.Result{}, nil
        }
        return ctrl.Result{}, err
    }

    // List all StatefulSets in the namespace
    statefulSetList := &appsv1.StatefulSetList{}
    err = r.List(ctx, statefulSetList, &client.ListOptions{Namespace: autoScaler.Namespace})
    if err != nil {
        return ctrl.Result{}, err
    }

    // Iterate over all StatefulSets and manage their replicas
    for _, statefulSet := range statefulSetList.Items {
        desiredReplicas := calculateDesiredReplicas(autoScaler, &statefulSet)

        if *statefulSet.Spec.Replicas != desiredReplicas {
            err = retry.RetryOnConflict(retry.DefaultRetry, func() error {
                statefulSet.Spec.Replicas = &desiredReplicas
                return r.Update(ctx, &statefulSet)
            })
            if err != nil {
                return ctrl.Result{}, err
            }
        }

        // Update the AutoScaler status
        autoScaler.Status.CurrentReplicas = *statefulSet.Spec.Replicas
        autoScaler.Status.DesiredReplicas = desiredReplicas
        err = r.Status().Update(ctx, autoScaler)
        if err != nil {
            return ctrl.Result{}, err
        }
    }

    return ctrl.Result{}, nil
}

func calculateDesiredReplicas(autoScaler *autoscalingv1alpha1.AutoScaler, statefulSet *appsv1.StatefulSet) int32 {
    // Implement your logic here to calculate the desired replicas based on metrics
    // This is a placeholder for demonstration purposes
    return autoScaler.Spec.MinReplicas
}

// SetupWithManager sets up the controller with the Manager.
func (r *AutoScalerReconciler) SetupWithManager(mgr ctrl.Manager) error {
	return ctrl.NewControllerManagedBy(mgr).
		For(&autoscalingv1alpha1.AutoScaler{}).
		Owns(&appsv1.StatefulSet{}).
		Complete(r)
}
