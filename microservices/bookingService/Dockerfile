FROM maven:3.9.6-amazoncorretto-17-debian AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn package

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /build/target/bookingService-0.0.1-SNAPSHOT.jar .

EXPOSE 25567

ENTRYPOINT ["java", "-jar", "bookingService-0.0.1-SNAPSHOT.jar"]