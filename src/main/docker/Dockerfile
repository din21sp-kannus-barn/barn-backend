# Start with a base image containing Java runtime
FROM openjdk:17-jdk-alpine

# Add Maintainer Info
LABEL maintainer="yehajri@example.com"

# Install necessary packages
RUN apk add --no-cache freetype fontconfig ttf-dejavu

# Add a volume pointing to /tmp
VOLUME /tmp



# Make port 8080 available to the world outside this container
EXPOSE 8080


# The application's jar file
ARG JAR_FILE=kannus_springboot-0.0.1-SNAPSHOT.jar

# Add the application's jar to the container
ADD ${JAR_FILE} app.jar

# Run the jar file 
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
 