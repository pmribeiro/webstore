FROM java
ADD hystrix-dashboard-*.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 7979
ENTRYPOINT ["java","-jar","/app.jar"]