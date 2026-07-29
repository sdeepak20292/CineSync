# CineSync Frontend

A React seat-booking client for the CineSync real-time reservation platform. It pairs JWT-protected booking actions with a public STOMP/SockJS channel so every viewer sees seat availability change instantly.

> The companion Spring Boot service lives in the `seat-booking-system` backend project.

## Highlights

- Sign up, sign in, persistent session, and explicit logout
- JWT bearer authentication for hold, confirm, release, and logout operations
- Server-side logout revocation: logged-out tokens are no longer accepted
- Real-time seat updates through STOMP over SockJS (`/topic/shows/{showId}`)
- Five-minute hold countdown with confirm/release controls
- Clear handling for expired sessions, seat conflicts, and unavailable backend services

## Tech stack

- React 18 + Vite
- Native Fetch API
- `@stomp/stompjs` + SockJS
- ESLint

## Run locally

Prerequisites: Node.js 18+ and the backend running on port `8080`.

```bash
npm install
npm run dev
```

Open the URL printed by Vite (normally `http://localhost:5173`). Create an account from the landing screen, choose a show, and select a seat.

## Configuration

The backend defaults to `http://127.0.0.1:8080`. To use another API host, create a local `.env.local` file:

```env
VITE_API_BASE_URL=http://127.0.0.1:8080
```

Do not commit `.env.local` files.

## Validation

```bash
npm run lint
npm run build
```

## Resume-ready summary

Built a React real-time seat reservation UI with JWT-authenticated booking flows, session revocation on logout, and STOMP/SockJS updates that synchronize seat state across concurrent clients.
