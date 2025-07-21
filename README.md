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

## API Endpoints

### Restaurantes

#### Crear Restaurante
- **URL**: `POST /api/v1/restaurantes`
- **Description**: Crea un nuevo restaurante con validaciones de campos y rol de propietario
- **Request Body**:
  ```json
  {
    "nombre": "Restaurante El Buen Sabor",
    "nit": "123456789",
    "direccion": "Calle 123 #45-67, Bogotá",
    "telefono": "+573005698325",
    "urlLogo": "https://example.com/logo.png",
    "idPropietario": "550e8400-e29b-41d4-a716-446655440000"
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "nombre": "Restaurante El Buen Sabor",
    "nit": "123456789",
    "direccion": "Calle 123 #45-67, Bogotá",
    "telefono": "+573005698325",
    "urlLogo": "https://example.com/logo.png",
    "idPropietario": "550e8400-e29b-41d4-a716-446655440000"
  }
  ```

#### Validaciones Implementadas
- ✅ **Campos Obligatorios**: Nombre, NIT, Dirección, Teléfono, URL Logo, ID Propietario
- ✅ **Validación de Rol OWNER**: Comunicación con microservicio de usuarios
- ✅ **Formato NIT**: Solo números, debe ser único
- ✅ **Formato Teléfono**: Máximo 13 caracteres, puede incluir símbolo +
- ✅ **Nombre Restaurante**: No puede contener únicamente números

#### Códigos de Respuesta
- `201`: Restaurante creado exitosamente
- `400`: Datos de entrada inválidos
- `409`: Conflicto - NIT ya existe
- `403`: Usuario no tiene rol de propietario

<!-- ROADMAP -->
## Tests

- Right-click the test folder and choose Run tests with coverage

## Architecture

This microservice follows Hexagonal Architecture (Ports and Adapters) with the following layers:

- **Domain**: Contains business logic, models, and use cases
- **Application**: Contains DTOs, handlers, and mappers
- **Infrastructure**: Contains controllers, repositories, and external adapters

## Microservices Communication

This microservice communicates with the **ms-users** microservice for user validation:

- **ms-plaza**: Port 8082
- **ms-users**: Port 8081

The ms-users microservice must provide the following endpoint:
```
GET /api/v1/users/validate-owner/{userId}
```

Response: `true` if user has OWNER role, `false` otherwise.


