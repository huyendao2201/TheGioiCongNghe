# Chọn image Java
FROM openjdk:17-jdk-slim

# Thư mục làm việc trong container
WORKDIR /app

# Sao chép file JAR vào container
COPY target/your-app.jar app.jar

# Lệnh để chạy ứng dụng Spring Boot
ENTRYPOINT ["java", "-jar", "app.jar"]
