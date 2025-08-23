# Financial API

A modern financial services application built with Spring Boot and React, featuring secure transaction management, account handling, and real-time statistics.

## Features

- **Secure Authentication**: JWT-based authentication system
- **Account Management**: Create and manage financial accounts
- **Transaction Processing**: Secure transaction handling with validation
- **Real-time Statistics**: Transaction and account analytics
- **Privacy Controls**: Data protection and privacy management
- **Modern React Frontend**: Responsive and user-friendly interface
- **API Documentation**: OpenAPI/Swagger documentation
- **Monitoring**: Prometheus metrics and logging

## Technology Stack

### Backend
- Java 17
- Spring Boot 3.1.3
- Spring Security
- Spring Data JPA
- H2 Database
- JWT Authentication
- OpenAPI/Swagger
- Lombok
- JUnit & Spring Test

### Frontend
- React
- TypeScript
- Node.js 18
- Modern UI Components

### DevOps & Monitoring
- Docker & Docker Compose
- GitHub Actions CI/CD
- SonarCloud Code Analysis
- Prometheus Monitoring
- Logback with JSON formatting
- JaCoCo Code Coverage

## Prerequisites

- Java 17 or higher
- Node.js 18 or higher
- Maven
- Docker (optional)
- Git

## Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/b4udo/FinancialAPI.git
   cd FinancialAPI
   ```

2. **Build the backend**
   ```bash
   mvn clean install
   ```

3. **Set up the frontend**
   ```bash
   cd src/main/frontend
   npm install
   npm start
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The application will be available at:
   - Backend: http://localhost:8080
   - Frontend: http://localhost:3000
   - Swagger UI: http://localhost:8080/swagger-ui.html

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
# Backend tests
mvn test

# Frontend tests
cd src/main/frontend
npm test
```

## API Documentation

Access the API documentation through Swagger UI at `/swagger-ui.html` when running the application.

## CI/CD Pipeline

The project includes a comprehensive GitHub Actions pipeline with three main stages:

### 1. Code Quality
- **Backend:**
  - Java code formatting check with Prettier
  - SonarCloud analysis for code quality and security
- **Frontend:**
  - TypeScript/JavaScript linting with ESLint
  - Code formatting check with Prettier
  - Style files (CSS/SCSS) formatting verification

### 2. Testing
- **Backend:**
  - Unit tests execution
  - Integration tests
  - Code coverage analysis with JaCoCo
- **Frontend:**
  - Component tests
  - Integration tests
  - Coverage reporting

### 3. Build
- **Frontend:**
  - Dependencies installation
  - Production build creation
- **Backend:**
  - Maven package creation
  - JAR artifact generation
- **Artifacts:**
  - Automated artifact upload
  - Version tracking

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
│   │   ├── frontend/         # React frontend application
│   │   ├── java/             # Java backend code
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
   # Backend
   mvn spotless:apply  # Format Java code
   mvn prettier:write  # Format with Prettier
   mvn verify         # Run all checks

   # Frontend
   npm run prettier:write  # Format code
   npm run lint           # Check code quality
   npm test              # Run tests
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
