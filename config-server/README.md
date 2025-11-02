### Procedimiento

- 1.- En el directorio config-repo crear el archivo user-service.yml
```
server:
  port: 8081

spring:
  application:
    name: user-service
  datasource:
    url: jdbc:postgresql://localhost:5432/userdb
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
```
- 2.- Crear un repositorio GIT en  el directorio config-repo
```
 git init
 git add .
 git branch -M main
 git commit -m "create config-repo"

```
- 3.- Crear el archivo application.yml
```
server:
  port: 8084
# Application Name

# -- Application Configuration --
spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          # File URI - Adjust path according to your system:
          # Windows: file:///C:/Users/YourUser/microservices-lab1/config-repo
          uri: file://${user.home}/git_codigo/config-repo
          # Git Configuration
          default-label: main
```
