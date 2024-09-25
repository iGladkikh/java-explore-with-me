FROM eclipse-temurin:21-jre-jammy
ENV JAVA_OPTS '-XX:+UseG1GC -Xms512m -Xmx1024m'
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]
