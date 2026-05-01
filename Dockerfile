FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY target/MovieReservationSystem-0.0.1-SNAPSHOT.jar /app/moviereservationsystem.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "moviereservationsystem.jar"]