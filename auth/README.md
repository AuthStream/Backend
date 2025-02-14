# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.2/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.2/maven-plugin/build-image.html)
* [Docker Compose Support](https://docs.spring.io/spring-boot/3.4.2/reference/features/dev-services.html#features.dev-services.docker-compose)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/3.4.2/reference/messaging/kafka.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.2/reference/web/servlet.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.2/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Security](https://docs.spring.io/spring-boot/3.4.2/reference/web/spring-security.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.4.2/reference/using/devtools.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)

### Docker Compose support
This project contains a Docker Compose file named `compose.yaml`.
In this file, the following services have been defined:

* postgres: [`postgres:latest`](https://hub.docker.com/_/postgres)

Please review the tags of the used images and set them to the same as you're running in production.

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

### Docker network:
Create docker network called auth-network first to run: 
```
    docker network create auth-network
```

### Project Structure
```
.
├── AuthstreamApplication.java
├── application/        # Application Layer (Use Cases, Business Logic)
│   ├── services/       # Service Layer (Transactional Logic)
│   ├── dtos/           # Data Transfer Objects (DTOs)
│   └── mappers/        # Mapping DTOs to Entities
├── domain/             # Domain Layer (Entities, Aggregates, Domain Services)
│   ├── entities/       # Hibernate Entities
│   ├── valueobjects/   # Value Objects (Immutable, No Identity)
│   ├── services/       # Domain Services (Business Logic)
│   ├── exceptions/     # Domain-Specific Exceptions
│   └── events/         # Domain Events
├── infrastructure/     # Persistence & External Systems
│   ├── repositories/   # Hibernate Repositories
│   ├── persistence/    # JPA Configuration
│   ├── external/       # External APIs (Kafka, Redis, etc.)
│   ├── security/       # Security Configurations (JWT, OAuth2)
│   └── config/         # Spring Boot Configurations
├── presentation/       # API Layer
│   ├── controllers/    # REST API Controllers
│   ├── graphql/        # GraphQL Resolvers (Optional)
│   └── websocket/      # WebSocket Controllers (Optional)
├── utils/              # Utility Classes (Helper Methods, Common Utils)
└── constants/          # Constant Values (Static Data)
```