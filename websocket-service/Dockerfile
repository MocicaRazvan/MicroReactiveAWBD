FROM jelastic/maven:3.9.5-openjdk-21 AS build
WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app/target/websocket-service-0.0.1-SNAPSHOT.jar"]

