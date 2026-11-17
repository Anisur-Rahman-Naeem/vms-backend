# Vehicle Management System — Backend

The VMS backend is a Spring Boot REST API for managing vehicle fleet records. It provides CRUD operations, request validation, consistent application-level error responses, and database health information.

## Features

- **Vehicle REST API**
  - Create, list, retrieve by ID, retrieve by vehicle number, update, and delete vehicles.
  - Normalize vehicle numbers by trimming whitespace and converting them to uppercase.
  - Reject duplicate vehicle numbers during create and update.
- **Vehicle data**
  - Stores vehicle number, company, type, brand, model, status, VIN, model year, fuel type, mileage, assigned driver, department, last service date, and notes.
- **Validation and error handling**
  - Validates required fields and configured field-length limits.
  - Returns structured error responses for validation errors, duplicates, missing vehicles, and bad requests.
- **Database health**
  - `GET /api/health` checks the configured datasource and reports database product and driver details.
  - Spring Boot Actuator health and info endpoints are enabled.
- **Persistence and testing**
  - Spring Data JPA/Hibernate persistence.
  - Includes controller, service, repository, database-health, and CRUD integration tests. The CRUD integration test uses an in-memory H2 database.
- **Frontend integration**
  - CORS is configured for API requests from the frontend. Restrict allowed origins for production.

## Technology Stack

- Java 17.
- Spring Boot 3.3.5.
- Spring Web / Spring MVC.
- Spring Data JPA and Hibernate.
- Jakarta Bean Validation.
- Spring Boot Actuator.
- Maven.
- JDBC drivers for H2, MySQL, PostgreSQL, and Microsoft SQL Server.
- JUnit, Mockito, and Spring Boot Test.

## API Endpoints

| Method | Endpoint | Purpose | Typical responses |
|---|---|---|---|
| `GET` | `/api/vehicles` | List all vehicles | `200 OK` |
| `GET` | `/api/vehicles/{id}` | Retrieve a vehicle by ID | `200 OK`, `404 Not Found` |
| `GET` | `/api/vehicles/by-number/{vehicleNumber}` | Retrieve a vehicle by vehicle number | `200 OK`, `404 Not Found` |
| `POST` | `/api/vehicles` | Create a vehicle | `201 Created`, `400 Bad Request`, `409 Conflict` |
| `PUT` | `/api/vehicles/{id}` | Update a vehicle | `200 OK`, `400 Bad Request`, `404 Not Found`, `409 Conflict` |
| `DELETE` | `/api/vehicles/{id}` | Delete a vehicle | `204 No Content`, `404 Not Found` |
| `GET` | `/api/health` | Check application database connectivity | `200 OK`, `503 Service Unavailable` |

Validation errors use HTTP `400`, duplicate vehicle numbers use `409`, and missing vehicles use `404`. Error bodies include a status and error description; additional fields provide field errors or vehicle details when applicable.

### Example Request

```json
{
  "vehicleNumber": "ABC-123",
  "company": "Datavvy",
  "type": "Van",
  "brand": "Ford",
  "model": "Transit",
  "status": "ACTIVE",
  "vin": "EXAMPLEVIN1234567",
  "year": 2024,
  "fuelType": "DIESEL",
  "mileage": 12000,
  "assignedDriver": "Alex Morgan",
  "department": "Operations",
  "lastServiceDate": "2025-01-15",
  "notes": "Example vehicle record"
}
```


## Getting Started

### Prerequisites

- JDK 17.
- A database server if not using the default H2 database.

### Run the API

From the backend project directory:

```powershell
.\mvnw.cmd spring-boot:run
```

The API listens on port `8080` by default. Check database connectivity at `http://localhost:8080/api/health`.

### Run Tests

```powershell
.\mvnw.cmd test
```

## Deploy to Render with Docker

The backend runs on Java 17. Render's native runtimes do not include Java, so deploy it as a Docker Web Service.

1. Keep `Dockerfile` at the repository root and create a Render **Web Service** using the **Docker** runtime.
2. Set the Dockerfile path to `./Dockerfile` and the Docker build context to `.`. Leave the root directory blank when the backend repository itself is connected.
3. Set the health check path to `/api/health`.
4. Add `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, and optionally `DB_DRIVER` as Render environment variables. Do not commit or import a local `.env` file into the Docker image.
5. Deploy and verify `https://<render-service-host>/api/health` reports the database as connected.

The container uses Render's `PORT` environment variable when provided and defaults to port `8080` for local container runs. The Docker build skips test execution; run `.\mvnw.cmd test` separately before deployment.