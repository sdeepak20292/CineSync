# API reference

Base URL: `http://localhost:8080`

## Authentication

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| POST | `/api/auth/register` | No | Creates an account and returns a JWT |
| POST | `/api/auth/login` | No | Authenticates an account and returns a JWT |
| POST | `/api/auth/logout` | Bearer JWT | Revokes the current JWT until it expires |

Login/register payload:

```json
{ "username": "alex", "password": "secure-password" }
```

Successful login/register response:

```json
{ "token": "<jwt>", "username": "alex", "expiresInSeconds": 3600 }
```

## Shows and seats

| Method | Endpoint | Auth | Description |
| --- | --- | --- | --- |
| GET | `/api/shows` | No | Lists all shows |
| GET | `/api/shows/{showId}/seats` | No | Gets the current seat map |
| POST | `/api/shows/{showId}/seats/{seatId}/hold` | Bearer JWT | Holds an available seat for five minutes |
| POST | `/api/shows/{showId}/seats/{seatId}/confirm` | Bearer JWT | Confirms the caller's hold |
| POST | `/api/shows/{showId}/seats/{seatId}/release` | Bearer JWT | Releases the caller's hold |

Protected endpoints require:

```http
Authorization: Bearer <jwt>
```

Contested or invalid seat transitions return `409 Conflict` with an `error` message. Expired or revoked JWTs return `401 Unauthorized`.

## Real-time channel

Connect to the SockJS endpoint `/ws`, then subscribe to:

```text
/topic/shows/{showId}
```

Messages contain the changed seat ID, seat number, and updated status.
