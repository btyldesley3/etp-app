# ETP Backend — Tickets Service

This module implements the **Event Ticketing Platform** backend.  
It exposes REST APIs for event publishing, ticket sales, and validation.  
The backend is built with **Spring Boot 3** and integrates with **OIDC (Keycloak or Cognito)** for authentication.

---

## Tech Stack

- **Language**: Java 21
- **Framework**: Spring Boot 3.x
    - Spring Web (REST controllers)
    - Spring Security (JWT resource server)
    - Spring Data JPA (ORM)
    - Actuator (health/metrics) - ONLY IN PRODUCTION
- **Database**: PostgreSQL
- **Authentication**: OpenID Connect (Keycloak, or AWS Cognito alternative)
- **Build Tool**: Maven
- **Containerization**: Docker/Docker Compose (Postgres, Adminer, Keycloak for local dev)

---

## Project Structure

├── src/main/java/com/etp/tickets
├── controller/ # REST controllers (e.g., /api/v1/events, /tickets, /validate)
│ ├── service/ # Business services
│ ├── repository/ # Spring Data JPA repositories
│ ├── model/ # JPA entities
│ ├── dto/ # Data transfer objects
│ ├── config/ # Security, CORS, etc.
│ └── TicketsApplication.java
│
├── src/main/resources/
│ ├── application.yml # Base config
│ └── application-dev.yml
│
├── docker-compose.yml # Local dev stack (Postgres, Adminer, Keycloak)

## Development Prerequisites

### 1. Setup

- Install **Java 21 (Temurin/OpenJDK)**
- Install **Maven 3.9+**
- Install **Docker & Docker Compose** (for local DB/Keycloak)

Start local dependencies (Postgres, Adminer, Keycloak):
docker-compose up -d

### 2. Compile

Build the project with Maven:
mvn clean package

Run tests:
mvn test

### 3. Run

Run the Spring Boot app locally:
mvn spring-boot:run

The API will be available at:
http://localhost:8080/api/v1/

### 4. Authentication

Default local Keycloak is started by docker-compose.yml

Realm: event-ticket-platform

Update application-dev.yml → spring.security.oauth2.resourceserver.jwt.issuer-uri

Example issuer (local):
http://localhost:9090/realms/event-ticket-platform

### 5. Common API Endpoints

### 5.1 Public

GET /api/v1/published-events → List all published events

### 5.2 Secured (requires login)

GET /api/v1/events → Get events for organizer

POST /api/v1/events → Create new event

GET /api/v1/ticket-types → List ticket types

POST /api/v1/ticket-types → Create new ticket type

POST /api/v1/tickets → Purchase a ticket

POST /api/v1/validate → Validate ticket QR / code