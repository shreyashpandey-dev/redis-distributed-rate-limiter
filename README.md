# 🚀 Redis Distributed Rate Limiter

A distributed rate limiting service built using **Spring Boot** and **Redis**, implementing a **fixed-window** throttling algorithm using Redis atomic counters and TTL-based windowing.

The service is stateless and horizontally scalable, with Redis acting as a centralized atomic counter store. The entire system is fully containerized using Docker and Docker Compose for seamless local setup.

---

## 🏗 Architecture Overview

### 🔹 Components

- **Spring Boot REST API**
- **Redis (Distributed Counter Store)**
- **Fixed Window Rate Limiting Algorithm**
- **Global Exception Handling**
- **Dockerized Deployment**

---

### 🔄 Request Flow

1. Client sends request with `clientId`
2. Service generates Redis key: rate:{clientId}
3. Redis performs atomic `INCR`
4. TTL is set only when the counter is created (current count = 1), defining the fixed window duration.
5. If request count exceeds limit → HTTP `429 TOO MANY REQUESTS` returned
6. Otherwise, remaining quota and reset time are returned

---

## ⚙️ Tech Stack

- Java 17
- Spring Boot
- Spring Data Redis
- Redis 7
- Docker
- Docker Compose

---

## 🚀 Run Locally (Recommended)

### ✅ Prerequisites

- Docker installed
- Docker Engine running

---
## 🚀 How to Run This Project After Cloning

### ✅ Prerequisites

- Docker installed
- Docker Engine running

---

### 📥 Step 1: Clone the Repository

```bash
git clone https://github.com/<your-username>/redis-distributed-rate-limiter.git
cd redis-distributed-rate-limiter
```

### 📥 Step 2: Start the Application
```bash
docker-compose up -d --build
```
### This command will:

1)Pull Redis image
2)Build the Spring Boot application image
3)Start Redis container
4)Start the application container
5)Expose the API on port 8080

---

### 📌 Step 4: Access the API


#### Endpoint


#### POST http://localhost:8080/api/rate-limit

```json
{
  "clientId": "user1"
}
```

#### ✅ Succesful response 

```json
{
  "allowed": true,
  "remainingRequests": 4,
  "resetInSeconds": 55
}
```

#### ❌ Rate Limit Exceeded Response
```json
{
  "allowed": false,
  "message": "Rate limit exceeded. Try again in 42 seconds."
}
```
##### HTTP Status: 429 TOO MANY REQUESTS

### 📌 Step 5: Stop the Application 

```bash
docker-compose down
```

#### Also stop your Docker engine



