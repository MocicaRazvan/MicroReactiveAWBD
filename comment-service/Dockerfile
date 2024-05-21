

FROM jelastic/maven:3.9.5-openjdk-21 AS build

WORKDIR /app

# copy install common module
COPY common-module/pom.xml /app/common-module/pom.xml
COPY common-module/src /app/common-module/src
RUN mvn -f /app/common-module/pom.xml clean install -DskipTests

# copy install comment-service
COPY comment-service/pom.xml /app/comment-service/pom.xml
COPY comment-service/src /app/comment-service/src

# build comment-service
RUN mvn -f /app/comment-service/pom.xml clean package -DskipTests

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app/comment-service/target/comment-service-0.0.1-SNAPSHOT.jar"]



