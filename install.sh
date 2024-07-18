#!/bin/bash

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Step 1: Install Istio
if ! command_exists istioctl; then
    echo "Installing Istio..."
    curl -L https://istio.io/downloadIstio | sh -
    cd istio-1.22.3 || { echo "Failed to change directory to istio-1.22.3"; exit 1; }
    export PATH=$PWD/bin:$PATH
    istioctl install --set profile=demo -y
    cd ..
else
    echo "Istio is already installed."
fi

# Step 2: Install Kubernetes Gateway API CRDs
if ! kubectl get crds | grep -q "gateways.gateway.networking.k8s.io"; then
    echo "Installing Kubernetes Gateway API CRDs..."
    kubectl kustomize "github.com/kubernetes-sigs/gateway-api/config/crd?ref=v1.1.0" | kubectl apply -f -
else
    echo "Kubernetes Gateway API CRDs are already installed."
fi

# Step 3: Install Go 1.21
if ! command_exists go || [[ "$(go version)" != *"go1.21.12"* ]]; then
    echo "Installing Go 1.21..."
    wget https://go.dev/dl/go1.21.12.linux-amd64.tar.gz
    sudo tar -C /usr/local -xzf go1.21.12.linux-amd64.tar.gz
    export PATH=$PATH:/usr/local/go/bin
    source ~/.profile
else
    echo "Go 1.21 is already installed."
fi

# Step 4: Install OperatorSDK
if ! command_exists operator-sdk; then
    echo "Installing OperatorSDK..."
    git clone https://github.com/operator-framework/operator-sdk
    cd operator-sdk || { echo "Failed to change directory to operator-sdk"; exit 1; }
    git checkout master
    make install
    cd ..
else
    echo "OperatorSDK is already installed."
fi

echo "All steps have been executed."
