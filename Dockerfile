FROM openjdk:17-oracle

EXPOSE 8080

COPY target/gcs-file-uploader-0.0.1-SNAPSHOT.jar gcs-file-uploader-0.0.1-SNAPSHOT.jar

ENTRYPOINT ["java","-jar","gcs-file-uploader-0.0.1-SNAPSHOT.jar"]