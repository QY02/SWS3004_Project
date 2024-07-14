index=$(echo "$POD_NAME" | grep -oE '[0-9]+$')
export POD_INDEX="${index}"
export MYSQL_HOST="${MYSQL_HOST_PREFIX}${index}${MYSQL_HOST_POSTFIX}"

java -jar eventPublishService-0.0.1-SNAPSHOT.jar