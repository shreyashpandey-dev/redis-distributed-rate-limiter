# Redis Distributed Rate Limiter

A distributed rate limiting service built with **Spring Boot** and **Redis**. It enforces a per-client request limit using a **Sliding Window algorithm** — meaning the limit applies to any rolling time window, not just fixed intervals. This prevents clients from bursting at window boundaries, which is a known weakness of simpler Fixed Window approaches.

Each client's request timestamps are stored in a **Redis Sorted Set (ZSET)**. On every request, expired entries are evicted and the remaining count determines whether to allow or reject the request. The response also tells the client exactly how many seconds until a slot frees up.

The limit and window size are fully configurable — no code change needed.

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
