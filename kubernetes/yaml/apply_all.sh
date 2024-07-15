#!/bin/bash

# 定义需要遍历的目录列表
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

# 遍历所有目录并应用 YAML 文件
for dir in "${directories[@]}"; do
    echo "Applying YAML files in directory: $dir"
    kubectl apply -f $dir
done

echo "All YAML files have been applied."