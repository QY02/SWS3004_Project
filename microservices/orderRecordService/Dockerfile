FROM maven:3.9.6-amazoncorretto-17-debian AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn package

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /build/target/orderRecordService-0.0.1-SNAPSHOT.jar .

COPY start.sh .

ENTRYPOINT ["sh", "start.sh"]