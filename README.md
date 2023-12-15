# admin
the system may be small but it has all the vital organs

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.0/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.0/maven-plugin/reference/html/#build-image)
* [Spring Modulith](https://docs.spring.io/spring-modulith/docs/current/reference/html/)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#appendix.configuration-metadata.annotation-processor)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#actuator)
* [Docker Compose Support](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#features.docker-compose)
* [Spring Data Redis (Access+Driver)](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#data.nosql.redis)
* [Spring for RabbitMQ](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#messaging.amqp)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#using.devtools)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.2.0/reference/htmlsingle/index.html#web)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
* [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)
* [Messaging with Redis](https://spring.io/guides/gs/messaging-redis/)
* [Messaging with RabbitMQ](https://spring.io/guides/gs/messaging-rabbitmq/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Docker Compose support

This project contains a Docker Compose file named `compose.yaml`. In this file, the following services have been
defined:

* mysql: [`mysql:latest`](https://hub.docker.com/_/mysql)
* rabbitmq: [`rabbitmq:latest`](https://hub.docker.com/_/rabbitmq)
* redis: [`redis:latest`](https://hub.docker.com/_/redis)

Please review the tags of the used images and set them to the same as you're running in production.

