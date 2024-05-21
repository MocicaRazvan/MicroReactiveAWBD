

FROM jelastic/maven:3.9.5-openjdk-21 AS build

WORKDIR /app

# copy install common module
COPY common-module/pom.xml /app/common-module/pom.xml
COPY common-module/src /app/common-module/src
RUN mvn -f /app/common-module/pom.xml clean install -DskipTests

# copy install order-service
COPY training-service/pom.xml /app/training-service/pom.xml
COPY training-service/src /app/training-service/src

# build training-service
RUN mvn -f /app/training-service/pom.xml clean package -DskipTests

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app/training-service/target/training-service-0.0.1-SNAPSHOT.jar"]



