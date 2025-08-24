# Financial API

A modern RESTful financial services application built with Spring Boot, featuring secure transaction management, account handling, and real-time statistics.

## Features

- **Secure Authentication**: JWT-based authentication system
- **Account Management**: Create and manage financial accounts
- **Transaction Processing**: Secure transaction handling with validation
- **Real-time Statistics**: Transaction and account analytics
- **Privacy Controls**: Data protection and privacy management
- **RESTful API**: Well-documented endpoints with OpenAPI/Swagger
- **API Documentation**: Interactive Swagger UI documentation
- **Monitoring**: Prometheus metrics and logging

## Technology Stack

### Core
- Java 17
- Spring Boot 3.1.3
- Spring Security with JWT
- Spring Data JPA
- H2 Database
- OpenAPI/Swagger 3
- Lombok
- JUnit & Spring Test

### DevOps & Monitoring
- Docker & Docker Compose
- GitHub Actions CI/CD
- SonarCloud Code Analysis
- Prometheus Monitoring
- Logback with JSON formatting
- JaCoCo Code Coverage

## Prerequisites

- Java 17 or higher
- Maven
- Docker (optional)
- Git

## Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/b4udo/FinancialAPI.git
   cd FinancialAPI
   ```

2. **Build the application**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The application will be available at http://localhost:8080

4. **Access the API Documentation**
   Open Swagger UI at http://localhost:8080/swagger-ui.html

5. **Authentication**
   1. Register a new user:
      ```bash
      curl -X POST http://localhost:8080/api/auth/register \
        -H "Content-Type: application/json" \
        -d '{"username": "youruser", "password": "yourpassword"}'
      ```
   2. Login to get JWT token:
      ```bash
      curl -X POST http://localhost:8080/api/auth/login \
        -H "Content-Type: application/json" \
        -d '{"username": "youruser", "password": "yourpassword"}'
      ```
   3. Use the token in subsequent requests:
      ```bash
      curl -X GET http://localhost:8080/api/your-endpoint \
        -H "Authorization: Bearer your-jwt-token"
      ```

## Docker Support

Run the entire application stack using Docker Compose:

```bash
docker-compose up -d
```

## Security

- JWT-based authentication
- Role-based access control
- Secure password handling
- HTTPS support
- Input validation
- XSS protection

## Monitoring

- Prometheus metrics at `/actuator/prometheus`
- Health checks at `/actuator/health`
- Structured JSON logging
- Transaction monitoring
- Performance metrics

## Testing

Run the test suite:

```bash
mvn test
```

The test suite includes:
- Unit tests for all components
- Integration tests for controllers
- Security tests for JWT authentication
- Repository tests with H2 in-memory database

## API Documentation

Access the API documentation through Swagger UI at `/swagger-ui.html` when running the application.

## CI/CD Pipeline

The project includes a comprehensive GitHub Actions pipeline with three main stages:

### 1. Code Quality
- Java code formatting check with Prettier
- SonarCloud analysis for code quality and security
- Code coverage thresholds verification

### 2. Testing
- Unit tests execution
- Integration tests
- Code coverage analysis with JaCoCo

### 3. Build
- Maven package creation
- JAR artifact generation
- Artifact upload for deployment

The pipeline runs automatically on:
- Push to main branch
- Pull request creation/update
- Manual trigger available

Quality Gates:
- All tests must pass
- Code coverage thresholds must be met
- No code style violations
- SonarCloud quality gates must pass

## Project Structure

```
├── src/
│   ├── main/
│   │   ├── java/             # Application code
│   │   │   └── financial/
│   │   │       ├── config/   # Configuration classes
│   │   │       ├── controller/ # REST controllers
│   │   │       ├── model/    # Domain models
│   │   │       ├── repository/ # Data repositories
│   │   │       ├── security/ # Security configuration
│   │   │       └── service/  # Business logic
│   │   └── resources/        # Application configuration
│   └── test/                 # Test files
├── docker-compose.yml        # Docker composition
├── pom.xml                   # Maven configuration
└── README.md                 # This file
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Install dependencies:
   ```bash
   # Backend
   mvn clean install

   # Frontend
   cd src/main/frontend
   npm install
   ```
4. Ensure code quality:
   ```bash
   mvn spotless:apply  # Format Java code
   mvn prettier:write  # Format with Prettier
   mvn verify         # Run all checks including tests
   ```
5. Commit your changes (the CI pipeline will verify your changes)
6. Push to your branch
7. Open a Pull Request

Make sure your contributions meet the following criteria:
- All tests pass
- Code is properly formatted
- New features include tests
- Documentation is updated
- Commits follow conventional commit format

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

- **b4udo** - [GitHub Profile](https://github.com/b4udo)
