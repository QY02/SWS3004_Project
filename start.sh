#!/bin/bash

cd "$(dirname "$0")/kubernetes/yaml" || { echo "Failed to change directory to kubernetes/yaml"; exit 1; }

kubectl label namespace nus-cloud-project istio-injection=enabled

directories=(
    "namespace"
    "secret"
    "config-map"
    "deployment"
    "network"
    "service"
    "statefulSet"
    "storage-class"
)

for dir in "${directories[@]}"; do
    echo "Applying YAML files in directory: $dir"
    kubectl apply -f "$dir" || { echo "Failed to apply YAML files in $dir"; exit 1; }
done

echo "All YAML files have been applied."

cd .. || { echo "Failed to change directory to .."; exit 1; }

cd operator/auto-scaler || { echo "Failed to change directory to operator/auto-scaler"; exit 1; }
make deploy || { echo "Failed to deploy auto-scaler"; exit 1; }

kubectl apply -f test-auto-scaler

echo "Auto-scaler has been deployed successfully."
