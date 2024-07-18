# Event Booking Center in Kubernetes on AWS

Team Member:

- Student 1: QIN YAO, t0933301
- Student 2: LAM CHI CHEONG, t0933667
- Student 3: LIU YIQI, t0933248
- Student 4: LYU ZHEKUN, t0933828

## 1. Project Overview

### 1.1 Project Description

Our team has developed an event booking system. In the system, the users can browse, publish and book events. The system
uses a robust cloud-based microservices architecture designed to handle various operations efficiently. It is deployed
on aws using Kubernetes.

### 1.2 System Architecture

The architecture of the system can is described in the following graph:

![Architecture Diagram](总框架图.drawio.svg)

#### It can be simply divided into four parts:
1. Istio Gate Way
2. User Verification
   ![UserVerification Diagram](User Verification.drawio.svg)


3. Event operations
   ![EventOperation Diagram](EventOperation.drawio.svg)


4. Data Storage
   ![Data Diagram](Data.drawio.svg)

In our system, the backend is divided into to modules. User module and event module. In user module, their are login,
register, token verification and order record microservices. In event module, there are event global data, event
detailed data, event publish and booking microservices. The microservice communicate with each other using HTTP
protocol. Besides, we also using Redis and RabbitMQ to synchronous data between microservices.

### 1.3 Key Components
- **Microservices Architecture**: Each service (Register, Login, Booking, Event Publish) is deployed as a microservice
- **Istio Gateway**: Manages external traffic, providing secure communication and traffic management capabilities.
- **Service Mesh**: Istio's service mesh capabilities enable secure, reliable communication between microservices, ensuring observability and policy enforcement.
- **Routing by hash:** We use an algorithm similar to consistent hash to divide the data into several databases. Then
  using service mesh to routing the requests by their hash.
- **Data Storage**: We use MySQL databases to store user and event data, structured to handle high volumes of transactions efficiently.
- **Message Queuing**: RabbitMQ is employed to manage asynchronous communication between services, ensuring reliable data transfer and processing.

## 2. Environment Setup

### 2.1 Prerequisites
List all the prerequisites needed to set up the environment.

Operating system: Ubuntu

Software and libraries:

- Istio
- Kubernetes Gateway API CRDs
- go version 1.22
- OperatorSDK

Please see 2.2 for more information.

### 2.2 Installation
Step-by-step guide to setting up the development environment.
We assume you have:

- git
- docker
- AWS CLI
- Kubectl

Also, we assume that you have AWS Configured and ESK Cluster Setup.

1. **Step 1**: Istio.

    ```bash
    curl -L https://istio.io/downloadIstio | sh -
    cd istio-1.22.3
    export PATH=$PWD/bin:$PATH
    istioctl install --set profile=demo -y
    ```
2. **Step 2**: Kubernetes Gateway API CRDs.
   
    ```bash
    kubectl kustomize "github.com/kubernetes-sigs/gateway-api/config/crd?ref=v1.1.0" | kubectl apply -f -
    ```
3. **Step 3**: go version 1.21.
   
    ```bash
    wget https://go.dev/dl/go1.22.5.linux-amd64.tar.gz
    sudo tar -C /usr/local -xzf go1.22.5.linux-amd64.tar.gz
    export PATH=$PATH:/usr/local/go/bin
    source ~/.profile
    go version
    ```

4. **Step 4**: OperatorSDK.
   
    ```bash
    git clone https://github.com/operator-framework/operator-sdk
    cd operator-sdk
    git checkout master
    make install
    ```
## 3. Application Deployment

Detailed steps to deploy the application.

1. **Step 1**: Apply all the yaml files

    ```bash
    kubectl apply -f "$dir"
    ```
2. **Step 2**: Deploy the operator.
    ```bash
    make deploy
    ```
3. Or you can quickly start the project
    ```bash
    ./start.sh
    ```

