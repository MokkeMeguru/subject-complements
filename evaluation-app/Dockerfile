FROM openjdk:8-alpine

COPY target/uberjar/evaluation-app.jar /evaluation-app/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/evaluation-app/app.jar"]
