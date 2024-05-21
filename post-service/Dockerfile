

FROM jelastic/maven:3.9.5-openjdk-21 AS build

WORKDIR /app

# copy install common module
COPY common-module/pom.xml /app/common-module/pom.xml
COPY common-module/src /app/common-module/src
RUN mvn -f /app/common-module/pom.xml clean install -DskipTests

# copy install post-service
COPY post-service/pom.xml /app/post-service/pom.xml
COPY post-service/src /app/post-service/src

# build post-service
RUN mvn -f /app/post-service/pom.xml clean package -DskipTests

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app/post-service/target/post-service-0.0.1-SNAPSHOT.jar"]



