## Prerequisites

- Java 17
- [Gradle 7.5.1](https://gradle.org/install/)
- [Docker](https://docs.docker.com/get-docker/)

## Application start up guide
- Build a project.
```
gradle clean build
```
- Project external dependencies are the following:
    - PostgreSQL as a database

  Run the following command to start up the database in a Docker container:
```
docker-compose -f docker\banking-system\docker-compose-local.yml up -d
```
- Run the following command to initialize database structure and fill it with initial data
```
gradle liquibaseUpdate
```
- Run the [application](/src/main/java/com/anastasia/maryina/banksystem/BankAccountsManagementApp.java), play with it.
- To stop docker container run
```
docker-compose -f docker-compose-local.yml down
docker-compose -f docker-compose-local.yml down -v (use -v flag to remove volumes attached to container)
```
