FROM eclipse-temurin:21-jre
WORKDIR /app

COPY build/libs/inno_canvas_dashboard-*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]