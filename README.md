<br />
<div align="center">
<h3 align="center">PLAZA DE COMIDAS MICROSERVICE</h3>
  <p align="center">
    Microservicio que maneja la lógica de negocio para la plaza de comidas, siguiendo arquitectura hexagonal.
  </p>
</div>

### Built With

* ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
* ![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
* ![Gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
* ![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)


<!-- GETTING STARTED -->
## Getting Started

To get a local copy up and running follow these steps.

### Prerequisites

* JDK 17 [https://jdk.java.net/java-se-ri/17](https://jdk.java.net/java-se-ri/17)
* Gradle [https://gradle.org/install/](https://gradle.org/install/)
* MySQL [https://dev.mysql.com/downloads/installer/](https://dev.mysql.com/downloads/installer/)

### Recommended Tools
* IntelliJ Community [https://www.jetbrains.com/idea/download/](https://www.jetbrains.com/idea/download/)
* Postman [https://www.postman.com/downloads/](https://www.postman.com/downloads/)

### Installation

1. Clone the repo
2. Change directory
   ```sh
   cd ms-plaza
   ```
3. Create a new database in MySQL called plazoleta
4. Create a .env for your enviroment variables 

<!-- USAGE -->
## Usage

1. Right-click the class PlazaComidaApplication and choose Run
2. Open [http://localhost:8082/swagger-ui/index.html](http://localhost:8082/swagger-ui/index.html) in your web browser

<!-- ROADMAP -->
## Tests

- Right-click the test folder and choose Run tests with coverage

## Architecture

This microservice follows Hexagonal Architecture (Ports and Adapters) with the following layers:

- **Domain**: Contains business logic, models, and use cases
- **Application**: Contains DTOs, handlers, and mappers
- **Infrastructure**: Contains controllers, repositories, and external adapters


