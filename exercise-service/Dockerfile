

FROM jelastic/maven:3.9.5-openjdk-21 AS build

WORKDIR /app

# copy install common module
COPY common-module/pom.xml /app/common-module/pom.xml
COPY common-module/src /app/common-module/src
RUN mvn -f /app/common-module/pom.xml clean install -DskipTests

# copy install exercise-service
COPY exercise-service/pom.xml /app/exercise-service/pom.xml
COPY exercise-service/src /app/exercise-service/src

# build exercise-service
RUN mvn -f /app/exercise-service/pom.xml clean package -DskipTests

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app/exercise-service/target/exercise-service-0.0.1-SNAPSHOT.jar"]



