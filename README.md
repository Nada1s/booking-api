# Booking API (Spring Boot)

A clean Spring Boot REST API demonstrating:
- Layered architecture (controller → service → repository)
- RESTful APIs + HTTP status codes
- Hibernate / JPA (MySQL)
- Validation (Jakarta Bean Validation)
- Unit testing with JUnit 5 + Mockito
- Swagger UI (OpenAPI) for interactive API docs

## Tech Stack
- Java (configured in Maven)
- Spring Boot
- Spring Web
- Spring Data JPA (Hibernate)
- MySQL
- Maven
- JUnit 5 + Mockito
- Springdoc OpenAPI (Swagger UI)

## How to Run
1) Configure database settings in:
`src/main/resources/application.properties`

2) Start the application:
```bash
mvn spring-boot:run
```
The API will be available on:

- http://localhost:8080

## Swagger UI
Interactive API documentation:

- http://localhost:8080/swagger-ui/index.html

## Running Tests
```bash
mvn test
```
## API Endpoints
### Doctors
- POST /api/doctors

- GET /api/doctors

- GET /api/doctors/{id}

### Patients
- POST /api/patients

- GET /api/patients

- GET /api/patients?name=...

- GET /api/patients/{id}

### Appointments
- POST /api/appointments

- GET /api/appointments?doctorId=&date=YYYY-MM-DD

- PUT /api/appointments/{id}/reschedule

## Key Business Rule
**No double booking** : a doctor cannot have overlapping appointments.
Overlap rule: `existing.start < new.end` AND `existing.end > new.start`
