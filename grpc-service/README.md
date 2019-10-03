# Spring Boot based gRPC micro-service

This application is part of [this blog post](https://blog.softwaremill.com/take-spring-boot-graphql-and-grpc-micro-services-solve-the-n-1-query-issue-with-dataloader-c2fec7b43517) about a gRPC based micro-services architecture 
using GraphQL to gather data from multiple services. 

```
$ ./gradlew clean build

# start the Animal Service
$ java -jar build/libs/grpc-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=animal

# start the Country service
$ java -jar build/libs/grpc-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=country
```
