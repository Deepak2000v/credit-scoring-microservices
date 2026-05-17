# Credit Score Analysis Tool

A production-style microservices application built with **Spring Boot**, **Spring Cloud Gateway**, **Kafka**, **Redis**, and **Angular**. Developed for Swedbank (fictional client) to automate real-time credit score evaluation.

---

## Architecture

```
Angular (4200)
     │
     ▼
Spring Cloud Gateway (8080)   ← Single entry point
     │
     ├──▶ UserManagementMS (8081)   ← Auth, JWT, OAuth2
     │         └── MySQL
     │
     └──▶ CreditScoringServiceMS (8082)  ← Scoring, Cache, Messaging
               ├── MySQL
               ├── Redis Cache
               └── Kafka → Email Notifications
```

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2 |
| API Gateway | Spring Cloud Gateway |
| Security | JWT, OAuth2 (Google) |
| Database | MySQL 8.0 |
| Cache | Redis 7.2 |
| Messaging | Apache Kafka |
| Logging | Log4j2 + Splunk |
| Frontend | Angular 17 |
| Containerisation | Docker, Docker Compose |
| CI/CD | Jenkins (planned) |
| Orchestration | Kubernetes (planned) |

---

## Project Structure

```
credit-scoring-microservices/
├── backend/
│   ├── api-gateway/              ← Spring Cloud Gateway (port 8080)
│   ├── user-management-service/  ← Auth + User CRUD (port 8081)
│   ├── credit-scoring-service/   ← Credit scoring + Redis + Kafka (port 8082)
│   ├── docker-compose.yml
│   └── init.sql
├── frontend/
│   └── credit-score-app/         ← Angular 17 app (port 4200)
├── docs/
├── k8s/
├── .env.example                  ← Copy to .env and fill secrets
└── .gitignore
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 18+ and npm
- Docker Desktop
- MySQL 8 (for local profile)
- Redis (for local profile)
- Apache Kafka (for local profile)

### 1. Clone the repository

```bash
git clone https://github.com/YOUR_USERNAME/credit-scoring-microservices.git
cd credit-scoring-microservices
```

### 2. Set up environment variables

```bash
cp .env.example .env
# Edit .env with your actual credentials
```

### 3. Run with Docker (recommended)

```bash
cd backend
docker-compose up --build
```

All 7 services start automatically in the correct order.

### 4. Run locally in IntelliJ

Set `SPRING_PROFILES_ACTIVE=local` in each service's Run Configuration.

See [docs/local-setup.md](docs/local-setup.md) for full step-by-step instructions.

---

## API Overview

All requests go through the Gateway at `http://localhost:8080`

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | /register | None | Register a new user |
| POST | /login | None | Login, returns JWT token |
| GET | /users/{id} | Bearer | Get user by ID |
| POST | /data | Bearer | Submit financial transaction |
| GET | /data/{userId} | Bearer | Get financial records |
| POST | /score/calculate | Bearer | Calculate credit score |
| GET | /score/{userId} | Bearer | Get latest score (Redis cached) |
| GET | /score/history/{userId} | Bearer | Score history |
| GET | /score/average | Bearer | Average score across all users |

---

## Key Features

- **JWT Authentication** — stateless, validated at the Gateway layer
- **Redis Caching** — credit scores cached to avoid redundant DB calls
- **Kafka Messaging** — async email notifications on score updates
- **Spring Profiles** — `local` for IntelliJ, `docker` for containers
- **Inter-service communication** — CreditScoringMS calls UserManagementMS via WebClient

---

## Roadmap

- [x] Microservices architecture
- [x] JWT + OAuth2 security
- [x] Redis caching
- [x] Kafka messaging
- [x] Docker Compose setup
- [x] Spring profiles (local/docker)
- [x] Angular frontend
- [ ] Jenkins CI/CD pipeline
- [ ] Kubernetes deployment
- [ ] AWS Free Tier deployment

---

## Author

**Deepak Kumar** — [GitHub](https://github.com/Deepak2000v)
