# CineSync — Real-Time Seat Booking Platform

CineSync is a full-stack, real-time seat reservation platform built to demonstrate how ticketing systems safely handle concurrent booking attempts.

It combines a React frontend with a Spring Boot backend, MySQL persistence, Redis-backed JWT revocation, and STOMP/SockJS live updates.

## Features

- User registration and JWT-based login
- Secure logout with immediate token revocation through Redis
- Real-time seat availability updates across connected clients
- Five-minute seat holds with countdown timer
- Confirm and release reservation flows
- Conflict-safe booking using optimistic locking
- Automatic release of expired holds
- Demo show and seat data seeded at startup

## Architecture


React + Vite Frontend
  ├── REST API with JWT ───────────────> Spring Boot Backend ──> MySQL
  └── STOMP/SockJS seat subscriptions ─> WebSocket Broker
                                               │
                                               └──────────────> Redis
                                                               JWT revocation TTLs

Tech Stack
Layer	Technology
Frontend	React 18, Vite, Fetch API
Real-time communication	STOMP.js, SockJS, Spring WebSocket
Backend	Java 17, Spring Boot 3, Spring Security
Database	MySQL, Spring Data JPA
Authentication	JWT
Session revocation	Redis
Build tools	npm, Maven

Repository Structure
CineSync/
├── README.md
├── frontend/
│   ├── README.md
│   ├── src/
│   ├── package.json
│   └── vite.config.js
└── backend/
    ├── README.md
    ├── docs/
    │   ├── API.md
    │   └── ARCHITECTURE.md
    ├── src/
    └── pom.xml
How It Works
A user registers or signs in and receives a JWT.
The frontend sends the JWT with protected REST requests.
A user can hold an available seat for five minutes.
The backend uses optimistic locking to ensure only one competing request can update a seat.
Every seat mutation is broadcast through STOMP/SockJS to clients viewing the same show.
The user confirms the hold to book the seat, or releases it.
On logout, the backend stores a SHA-256 token fingerprint in Redis until the JWT naturally expires, immediately preventing reuse of that token.
Run Locally
Prerequisites
Java 17+
Maven
Node.js 18+
MySQL
Redis
1. Configure backend secrets
Set these environment variables before starting the backend:
$env:DB_PASSWORD = "your-mysql-password"
$env:JWT_SECRET = "a-strong-random-secret-with-at-least-32-characters"
Ensure MySQL is running and Redis is available at localhost:6379.
2. Start the backend
cd backend
mvn spring-boot:run
The API runs at:
http://localhost:8080
3. Start the frontend
Open another terminal:
cd frontend
npm install
npm run dev
Open the Vite URL shown in the terminal, usually:
http://localhost:5173
API and Real-Time Events
See:
[Backend API Reference](backend/docs/API.md)
[Backend Architecture Notes](backend/docs/ARCHITECTURE.md)
Resume Highlights
Built a concurrency-safe, real-time seat booking platform using Spring Boot, React, MySQL, Redis, and WebSockets.
Implemented optimistic locking to prevent double booking under simultaneous reservation requests.
Designed JWT authentication with Redis-backed token revocation to invalidate sessions immediately after logout.
Developed STOMP/SockJS real-time seat synchronization, allowing connected users to see booking changes without polling.
Implemented a hold-confirm-release reservation lifecycle with automatic expiry handling.
Security Notes
Do not commit real database passwords, JWT secrets, or .env files.
Use a high-entropy value for JWT_SECRET outside local development.
Raw JWTs are never stored in Redis; only SHA-256 fingerprints are stored until token expiry.
License
This project is intended for learning, demonstration, and portfolio use.
