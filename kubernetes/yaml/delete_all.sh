#!/bin/bash

# 定义需要遍历的目录列表
directories=(
    "storage-class"
    "statefulSet"
    "service"
    "network"
    "deployment"
    "config-map"
    "secret"
    "namespace"
)

# 遍历所有目录并应用 YAML 文件
for dir in "${directories[@]}"; do
    echo "Deleting YAML files in directory: $dir"
    kubectl delete -f $dir
done

echo "All YAML files have been deleted."