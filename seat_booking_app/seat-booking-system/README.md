# CineSync — Real-Time Seat Booking System

A full-stack reservation system that demonstrates the hard part of ticketing platforms: handling simultaneous attempts to reserve the same seat without double booking. The backend combines optimistic database locking, short-lived seat holds, real-time broadcasts, and JWT session revocation.

## Why this project

Booking systems are fundamentally concurrency problems. Two users can view the same available seat and submit a hold at the same moment. CineSync ensures only one request wins and presents the other client with a clear `409 Conflict` response while immediately updating every connected seat map.

## Key capabilities

- JWT registration, login, and protected booking actions
- Redis-backed logout revocation with automatic expiry cleanup
- Optimistic locking (`@Version`) to prevent conflicting seat updates
- Five-minute seat holds, confirmation, voluntary release, and scheduled expiry
- STOMP/SockJS broadcasts for live seat-status synchronization
- MySQL persistence and startup demo data (five shows, 60 seats per show)
- React client with authentication, real-time map updates, and conflict feedback

## Architecture

```text
React + Vite client
  ├─ REST + Bearer JWT ──────────> Spring Boot API ──> MySQL
  └─ STOMP/SockJS subscription ─> Spring broker ────> live seat updates
                                      │
                                      └─────────────> Redis token-revocation TTLs
```

Read [Architecture notes](docs/ARCHITECTURE.md) and the [API reference](docs/API.md) for implementation details.

## Tech stack

| Area | Technology |
| --- | --- |
| Backend | Java 17, Spring Boot 3, Spring Security, Spring Data JPA |
| Data | MySQL, Redis |
| Real time | Spring WebSocket, STOMP, SockJS |
| Frontend | React 18, Vite, STOMP.js |
| Build | Maven, npm |

## Run locally

### 1. Start dependencies

Start MySQL and Redis locally. Redis must be available at `localhost:6379` for logout token revocation.

Create a `seat_booking` MySQL database, or let the configured JDBC URL create it. Set secrets for your session:

```powershell
$env:DB_PASSWORD = "your-mysql-password"
$env:JWT_SECRET = "a-strong-random-secret-with-at-least-32-characters"
```

### 2. Start the API

```bash
mvn spring-boot:run
```

The API starts at `http://localhost:8080`; demo shows and seats are seeded on the first run.

### 3. Start the frontend

From the frontend project directory:

```bash
npm install
npm run dev
```

Open `http://localhost:5173`, register an account, select a show, and book a seat. Open a second browser window to observe the live updates.

## Portfolio talking points

- Designed a concurrency-safe booking workflow using JPA optimistic locking, returning deterministic conflict responses instead of allowing duplicate reservations.
- Built a real-time seat map using STOMP/SockJS broadcasts to synchronize concurrent clients without polling.
- Implemented JWT authentication and Redis-backed token revocation, so logout immediately invalidates an otherwise unexpired token.
- Modelled a hold/confirm/release lifecycle with scheduled expiry to avoid abandoned reservations blocking inventory.

## Security notes

- Never commit real database passwords, JWT secrets, or `.env` files.
- Set `JWT_SECRET` to a unique, high-entropy value outside local development.
- Logout stores only a SHA-256 fingerprint of the token in Redis, never the raw token. The key expires when the JWT would naturally expire.
