Below you will find the README.md for both the frontend and backend for ETP (Event Ticket Platform) condensed in to this README.md

Project brief based on and adapted from the Devtiro community (www.devtiro.com)

# Frontend

This is the **frontend application** for the event ticket platform project, built with
**React**, **TypeScript**, and **Vite**. It provides the user interface
and communicates with the backend APIs.

## Tech Stack

-   [React](https://react.dev/) +
    [TypeScript](https://www.typescriptlang.org/)\
-   [Vite](https://vitejs.dev/) for fast builds and hot reloading\
-   [Tailwind CSS](https://tailwindcss.com/) for styling\
-   [React Router](https://reactrouter.com/) for client-side routing\
-   [OIDC Client](https://github.com/authts/oidc-client-ts) for
    authentication\
-   [Lucide React](https://lucide.dev/) for icons\
-   [JSON Server](https://github.com/typicode/json-server) (via
    `db.json`) for local mock API data

## Project Structure

    FrontEnd/
    ├── public/             # Static assets served as-is
    ├── src/                # Application source code
    │   ├── assets/         # Images, fonts, and other static assets
    │   ├── components/     # Reusable UI components (buttons, forms, modals, etc.)
    │   ├── domain/         # Domain-specific models, DTOs, or feature logic
    │   ├── hooks/          # Custom React hooks for state or API handling
    │   ├── lib/            # Utility libraries (API clients, helpers, constants)
    │   ├── pages/          # Page-level components mapped to routes
    │   ├── index.css       # Global styles
    │   ├── main.tsx        # Application entry point
    │   └── vite-env.d.ts   # Vite/TypeScript type declarations
    ├── dist/               # Production build output
    ├── node_modules/       # Installed dependencies
    ├── components.json     # Shadcn/UI configuration
    ├── db.json             # JSON Server mock API
    ├── index.html          # HTML entry template
    ├── package.json        # Project metadata and dependencies
    ├── vite.config.ts      # Vite configuration
    └── tsconfig*.json      # TypeScript configurations

## Development

### Prerequisites

-   Node.js (\>= 18 recommended)\
-   npm or yarn

### Install dependencies

``` bash
npm install
```

### Run the development server

``` bash
npm run dev
```

App will be available at <http://localhost:5173>.

### Build for production

``` bash
npm run build
```

### Preview production build

``` bash
npm run preview
```

## Authentication

The project integrates **OIDC (OpenID Connect)** for authentication
using `react-oidc-context`. Update configuration in the codebase (`lib/`
or `config` depending on setup) with your identity provider details.

## Linting & Formatting

-   ESLint configuration: `eslint.config.js`\
-   Prettier configuration: `.prettierrc`

# ETP Backend — Tickets Service

This module implements the **Event Ticketing Platform** backend
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
