FROM quay.io/quarkus/ubi-quarkus-native-image:22.3-java17 as build
WORKDIR /work/
COPY pom.xml .
COPY src src
RUN mvn -e -B -DskipTests package
FROM registry.access.redhat.com/ubi8/ubi-minimal
WORKDIR /work/
COPY --from=build /work/target/*-runner.jar app.jar
EXPOSE 8083
CMD ["java","-jar","/work/app.jar"]
