FROM azul/zulu-openjdk-alpine:11-jre
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
#COPY interview.X.csv interview.X.csv
#COPY interview.y.csv interview.y.csv
ENTRYPOINT ["java","-jar","/app.jar"]