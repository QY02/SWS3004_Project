index=$(echo "$POD_NAME" | grep -oE '[0-9]+$')
export REDIS_HOST="${REDIS_HOST_PREFIX}${index}${REDIS_HOST_POSTFIX}"

java -jar tokenVerificationService-0.0.1-SNAPSHOT.jar