# Redis Distributed Rate Limiter

A rate limiting service built with **Spring Boot** and **Redis**. It restricts how many times a client can call an API within a time window, and returns a clear error when the limit is exceeded.

---

## How it works

Each request carries a `clientId`. The service tracks how many requests that client has made in the last 60 seconds using Redis. If they exceed the limit, the API returns a `429 Too Many Requests` response.

The limit and window size are configurable in `application.yaml` — no code change needed.

---

## Tech Stack

- Java 17, Spring Boot 4
- Redis 7
- Docker, Docker Compose

---

## Run Locally

**Prerequisites:** Docker Desktop installed and running.

```bash
# 1. Clone
git clone https://github.com/shreyashpandey-dev/redis-distributed-rate-limiter.git
cd redis-distributed-rate-limiter

# 2. Build the JAR
mvnw.cmd package -DskipTests       # Windows
./mvnw package -DskipTests         # Mac/Linux

# 3. Start
docker-compose up --build -d
```

The API is available at `http://localhost:8081`.

---

## API

**POST** `http://localhost:8081/api/rate-limit`

```json
{ "clientId": "user1" }
```

**Allowed (200):**
```json
{
  "allowed": true,
  "remainingRequest": 4,
  "resetInSeconds": 58
}
```

**Limit exceeded (429):**
```json
{
  "allowed": false,
  "message": "Rate Limit exceeded. Try again in 58 seconds"
}
```

---

## Configuration

```yaml
# src/main/resources/application.yaml
rate-limit:
  limit: 5            # requests allowed per window
  window-seconds: 60  # window size in seconds
```

---

## Stop

```bash
docker-compose down
```
