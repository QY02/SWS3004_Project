# 使用官方的 MySQL 镜像作为基础镜像
FROM mysql:latest

# 将本地 SQL 文件拷贝到容器内
COPY CreateTables.sql /docker-entrypoint-initdb.d/


# 暴露 MySQL 端口
EXPOSE 3306

# 容器启动时执行的命令
CMD ["mysqld"]

