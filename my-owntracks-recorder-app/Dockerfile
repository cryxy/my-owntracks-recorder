FROM adoptopenjdk/openjdk11:debianslim-jre
ADD /target/my-owntracks-recorder-app.jar /start/my-owntracks-recorder-app.jar
ADD /target/libs /start/libs
#RUN groupadd -r owntracks -g 998
#RUN useradd -u 998 -r -g owntracks owntracks
#USER owntracks
# local application port
EXPOSE 8080
CMD ["java","-jar","/start/my-owntracks-recorder-app.jar"]