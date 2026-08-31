# RealWorld Example App

> ### Spring Boot codebase containing real world examples (CRUD, auth, advanced patterns, etc) that adheres to the [RealWorld](https://github.com/gothinkster/realworld) spec and API.

This codebase was created to demonstrate a fully fledged fullstack application built with [Spring Boot](https://spring.io/projects/spring-boot)
including CRUD operations, authentication, routing, pagination, and more.

We've gone to great lengths to adhere to the Spring community styleguides & best practices.

For more information on how to this works with other frontends/backends, head over to
the [RealWorld](https://github.com/gothinkster/realworld) repo.

# How it works

This application basically uses Spring Boot with Java 25 with some other modules known to development community:

* Spring MVC
* Spring Data JPA
* Hibernate
* Jackson for JSON
* H2 in memory database
* Auth0 java-jwt

### Project structure:

```
application/            -> business orchestration layer
+-- web/                -> web layer models and controllers
domain/                 -> core business implementation layer
+-- model/              -> core business entity models
+-- feature/            -> all features logic implementation
+-- validator/          -> model validation implementation 
+-- exception/          -> all business exceptions
infrastructure/         -> technical details layer
+-- configuration/      -> dependency injection configuration
+-- repository/         -> adapters for domain repositories
+-- provider/           -> adapters for domain providers
+-- web/                -> web layer infrastructure models and security
```

# Getting started

### Start local server

```shell
 ./mvnw spring-boot:run
 ```

The server should be running at http://localhost:8080

### Running the application tests

```shell
./mvnw test 
```

Tests also produce a JaCoCo coverage report at `target/site/jacoco/index.html`. The build fails when
bundle line coverage drops below the threshold configured in `pom.xml`.

### Running postman collection tests

```shell
./collections/run-api-tests.sh
```

### Building jar file

```shell
./mvnw package
```

### Building native executable

GraalVM is necessary for building native executable, more information about setting up GraalVM can be found
in [Spring Boot native image guides](https://docs.spring.io/spring-boot/reference/packaging/native-image/index.html)
and database engine need to be changed.

```shell
./mvnw package -Pnative
```

#### Database changes can be made to the application.properties file.

```properties
# Database configuration
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
```

## Help

Improvements are welcome, feel free to contribute.
