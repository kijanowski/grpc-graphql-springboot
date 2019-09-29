# Spring Boot based gRPC micro-service

This application is part of [this blog post]() about a gRPC based micro-services architecture 
using GraphQL to gather data from multiple services. 

```
$ ./gradlew clean build

# start the Animal Service
$ java -jar build/libs/grpc-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=animal

# start the Country service
$ java -jar build/libs/grpc-service-0.0.1-SNAPSHOT.jar --spring.profiles.active=country
```