#FROM jelastic/maven:3.9.5-openjdk-21 AS build
#WORKDIR /app
#
#COPY pom.xml .
#
#RUN mvn dependency:go-offline -B
#
#COPY src ./src
#
#RUN mvn package -DskipTests
#
#ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app/target/user-service-0.0.1-SNAPSHOT.jar"]

FROM jelastic/maven:3.9.5-openjdk-21 AS build

WORKDIR /app

# copy install common module
COPY common-module/pom.xml /app/common-module/pom.xml
COPY common-module/src /app/common-module/src
RUN mvn -f /app/common-module/pom.xml clean install -DskipTests

# copy install user-service
COPY user-service/pom.xml /app/user-service/pom.xml
COPY user-service/src /app/user-service/src

# build user-service
RUN mvn -f /app/user-service/pom.xml clean package -DskipTests

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app/user-service/target/user-service-0.0.1-SNAPSHOT.jar"]



