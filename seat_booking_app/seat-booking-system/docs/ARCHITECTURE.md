# Architecture notes

## Seat lifecycle

```text
AVAILABLE --hold--> HELD --confirm--> BOOKED
    ^                  |
    └---- release -----┘
                       └-- expiry --> AVAILABLE
```

The `Seat` entity uses a JPA `@Version` field. On a contested hold, concurrent updates carry the version read by each client; only the first matching update commits. A losing update raises an optimistic-lock exception, which is translated to HTTP `409 Conflict`.

## Real-time synchronization

Seat mutations are performed through REST. After every successful hold, confirm, release, or automated expiry, `SeatBroadcastService` publishes a `SeatUpdateEvent` to `/topic/shows/{showId}`. Clients subscribe with STOMP/SockJS and update only the changed seat.

The WebSocket endpoint is intentionally public: it only broadcasts seat availability. Booking operations remain protected REST endpoints.

## Authentication and logout

Login and registration issue a signed JWT with a one-hour lifetime. The JWT filter validates every protected REST request. On logout, the backend hashes the token and writes the fingerprint to Redis with a TTL equal to the token's remaining lifetime. A matching fingerprint prevents the filter from authenticating that token immediately.

This design avoids persisting raw credentials or raw JWTs and automatically removes revoked-token entries after expiry.
