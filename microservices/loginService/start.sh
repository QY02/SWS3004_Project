index=$(echo "$POD_NAME" | grep -oE '[0-9]+$')
export MYSQL_HOST="${MYSQL_HOST_PREFIX}${index}${MYSQL_HOST_POSTFIX}"
export REDIS_HOST="${REDIS_HOST_PREFIX}${index}${REDIS_HOST_POSTFIX}"

java -jar loginService-0.0.1-SNAPSHOT.jar