# Splitwise API

![Java](https://img.shields.io/badge/Java_17-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot_4-6DB33F?style=flat&logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=flat&logo=jsonwebtokens&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)
![CI](https://github.com/TurnGui/splitwise-api/actions/workflows/ci.yml/badge.svg)

REST API for splitting shared expenses between groups, built with Spring Boot.

**Live:** https://splitwise-api-production.up.railway.app

## Why this project

Built to demonstrate backend engineering fundamentals for a Java/Spring Boot job search: a layered architecture (Controller -> Service -> Repository), JPA entity relationships (including bidirectional `@ManyToMany` and `@OneToMany`/`@ManyToOne` with JSON serialization handling), JWT-based authentication, a validated multi-strategy expense-splitting engine, and a greedy debt-simplification algorithm. Every line was written and can be explained individually, with no generated boilerplate.

## Features

- **User management** - registration with BCrypt password hashing, full CRUD
- **Groups** - create groups and manage members via a many-to-many relationship
- **Expenses** - three splitting strategies:
  - **Equal** - split evenly, with cent-level remainder distribution
  - **Percentage** - split by custom percentages, validated to sum to 100%
  - **Exact** - split by exact amounts, validated to sum to the total
- **Balance calculation** - net balances between group members, cancelling out opposite debts
- **Debt simplification** - greedy algorithm that minimizes the number of transactions needed to settle all debts in a group
- **Authentication** - JWT-based auth with BCrypt password hashing; all endpoints except registration and login require a valid Bearer token
- **Centralized error handling** - custom exceptions mapped to proper HTTP status codes (404, 400)

## Stack

- Java 17, Spring Boot 4
- Spring Data JPA / Hibernate
- Spring Security + JWT (jjwt)
- PostgreSQL (production) / H2 (local development)
- Lombok
- Maven
- JUnit 5 + Mockito
- Docker / Docker Compose

## Architecture

Layered architecture separating concerns across:

```
Controller  -> handles HTTP requests/responses
Service     -> business logic
Repository  -> data access (Spring Data JPA)
Model       -> JPA entities
DTO         -> request/response payloads decoupled from entities
Exception   -> custom exceptions + centralized handler
Config      -> security and JWT configuration
```

## Running locally

### Option 1 - Docker (recommended)

Requires Docker and Docker Compose.

```bash
docker compose up --build
```

Starts the API on port `8081` and a PostgreSQL container together, with persistent storage.

### Option 2 - Local JVM (H2 in-memory)

Requires Java 17+ and Maven.

```bash
./mvnw spring-boot:run
```

Runs against an in-memory H2 database. Data resets on every restart, which is useful for quick local development.

## API Overview

All endpoints are prefixed as shown. Endpoints other than `POST /users` and `/auth/**` require a `Bearer` token in the `Authorization` header, obtained via login.

### Auth
| Method | Endpoint | Description |
|---|---|---|
| POST | `/auth/login` | Authenticate and receive a JWT |

### Users
| Method | Endpoint | Description |
|---|---|---|
| POST | `/users` | Register a new user (public) |
| GET | `/users` | List all users |
| GET | `/users/{id}` | Get a user by ID |
| PUT | `/users/{id}` | Update a user |
| DELETE | `/users/{id}` | Delete a user |

### Groups
| Method | Endpoint | Description |
|---|---|---|
| POST | `/groups` | Create a group |
| GET | `/groups` | List all groups |
| GET | `/groups/{id}` | Get a group by ID |
| PUT | `/groups/{id}` | Update a group's name |
| DELETE | `/groups/{id}` | Delete a group |
| GET | `/groups/{id}/balances` | Get net balances between members |
| GET | `/groups/{id}/simplify-debts` | Get the minimum set of transactions to settle all debts |

### Expenses
| Method | Endpoint | Description |
|---|---|---|
| POST | `/expenses` | Create an expense (EQUAL, PERCENTAGE, or EXACT split) |
| GET | `/expenses` | List all expenses |
| GET | `/expenses/{id}` | Get an expense by ID |
| PUT | `/expenses/{id}` | Update an expense |
| DELETE | `/expenses/{id}` | Delete an expense |

### Expense Splits
| Method | Endpoint | Description |
|---|---|---|
| POST | `/expense-splits` | Create a split manually |
| GET | `/expense-splits` | List all splits |
| GET | `/expense-splits/{id}` | Get a split by ID |
| PUT | `/expense-splits/{id}` | Update a split |
| DELETE | `/expense-splits/{id}` | Delete a split |

### Example - create an expense with a percentage split

```bash
curl -X POST https://splitwise-api-production.up.railway.app/expenses \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "description": "Rent",
    "amount": 100.00,
    "date": "2026-08-26",
    "groupId": 1,
    "paidById": 1,
    "splitType": "PERCENTAGE",
    "splitDetails": [
      {"userId": 1, "value": 60},
      {"userId": 2, "value": 40}
    ]
  }'
```

## Running tests

```bash
./mvnw test
```

Unit tests cover the service layer (password hashing, CRUD, split calculations, split validation, balance netting, and the debt simplification algorithm) using JUnit 5 and Mockito.

## Roadmap

- [x] Project setup
- [x] Domain model (User, Group, Expense, ExpenseSplit) with JPA relationships
- [x] Repositories (Spring Data JPA)
- [x] Service layer + REST controllers (full CRUD)
- [x] Expense splitting logic (equal, percentage, exact) with validation
- [x] Balance calculation with debt netting
- [x] JWT authentication
- [x] Debt simplification algorithm
- [x] Unit tests (JUnit + Mockito)
- [x] Docker + PostgreSQL
- [x] CI (GitHub Actions)
- [x] Deploy (Railway)
- [ ] API documentation (OpenAPI/Swagger)

## License

Released under the [MIT License](LICENSE).
