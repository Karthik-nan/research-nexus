# 🚀 Research Nexus

<p align="center">

## Scalable Collaborative Research Platform

**Research Nexus** is a full-stack collaborative research platform designed to help researchers create projects, manage teams, organize research documents, and collaborate securely.

<br>

**Spring Boot · Spring Security · JWT · MySQL · Redis · Docker · Nginx · AWS EC2 · GitHub Actions · React**

<br>

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge\&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-brightgreen?style=for-the-badge\&logo=springboot)
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge\&logo=springsecurity)
![MySQL](https://img.shields.io/badge/MySQL-8-blue?style=for-the-badge\&logo=mysql)
![Redis](https://img.shields.io/badge/Redis-red?style=for-the-badge\&logo=redis)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge\&logo=docker)
![AWS](https://img.shields.io/badge/AWS-EC2-orange?style=for-the-badge\&logo=amazonaws)
![Nginx](https://img.shields.io/badge/Nginx-009639?style=for-the-badge\&logo=nginx)
![GitHub Actions](https://img.shields.io/badge/GitHub%20Actions-2088FF?style=for-the-badge\&logo=githubactions)
![React](https://img.shields.io/badge/React-61DAFB?style=for-the-badge\&logo=react)

</p>

---

# 🏗️ Architecture

```text
                         🌍 Internet
                              │
                              ▼
                         ☁️ AWS EC2
                              │
                              ▼
                         ┌─────────┐
                         │  Nginx  │
                         │  :8090  │
                         └────┬────┘
                              │
                 ┌────────────┴────────────┐
                 │                         │
                 ▼                         ▼
        ┌─────────────────┐       ┌─────────────────┐
        │  Spring Boot #1 │       │  Spring Boot #2 │
        │    Container    │       │    Container    │
        └────────┬────────┘       └────────┬────────┘
                 │                         │
                 └────────────┬────────────┘
                              │
                    ┌─────────┴─────────┐
                    │                   │
                    ▼                   ▼
                ┌───────┐           ┌───────┐
                │ MySQL │           │ Redis │
                └───────┘           └───────┘
```

The backend runs multiple Spring Boot instances behind Nginx, providing **horizontal scaling and load distribution**.

---

# ✨ Features

## 🔐 Authentication & Security

* User registration and login
* JWT-based authentication
* Spring Security integration
* Password hashing using BCrypt
* Protected REST APIs
* Role-based authorization
* Project-level access control
* CORS configuration
* Secure HTTP response headers

---

## 🔬 Research Project Management

* Create research projects
* View owned and accessible projects
* View project details
* Manage project members
* Assign project roles
* Enforce project-level permissions

---

## 📄 Document Management

* Upload research documents
* Retrieve project documents
* Organize documents by research project
* Secure document access
* Project-based document authorization

---

## 👥 Collaboration

* Add members to research projects
* Manage project membership
* Owner and member roles
* Collaborative project workspace

---

# 🛠️ Technology Stack

## Backend

| Technology          | Usage                 |
| ------------------- | --------------------- |
| ☕ Java 21           | Backend               |
| 🌱 Spring Boot      | Application framework |
| 🔐 Spring Security  | Security              |
| 🎫 JWT              | Authentication        |
| 🗄️ Spring Data JPA | Data access           |
| 🔧 Hibernate        | ORM                   |
| 🌐 REST APIs        | Communication         |

## Database & Caching

| Technology | Usage            |
| ---------- | ---------------- |
| 🐬 MySQL 8 | Primary database |
| ⚡ Redis    | Caching          |

## DevOps & Infrastructure

| Technology        | Usage                         |
| ----------------- | ----------------------------- |
| 🐳 Docker         | Containerization              |
| 🐳 Docker Compose | Infrastructure                |
| 🌐 Nginx          | Reverse proxy / Load balancer |
| ☁️ AWS EC2        | Deployment                    |
| ⚙️ GitHub Actions | CI/CD                         |
| 🔀 Git            | Version control               |

## Frontend

| Technology    | Usage                |
| ------------- | -------------------- |
| ⚛️ React      | Frontend             |
| ⚡ Vite        | Build tool           |
| 🟨 JavaScript | Programming language |

---

# 🧩 Backend Architecture

```text
                         Client
                           │
                           ▼
                         Nginx
                           │
                ┌──────────┴──────────┐
                │                     │
                ▼                     ▼
         Backend Instance 1    Backend Instance 2
                │                     │
                └──────────┬──────────┘
                           │
                    ┌──────┴──────┐
                    │             │
                    ▼             ▼
                  MySQL         Redis
```

Nginx acts as the **reverse proxy and load balancer**, distributing incoming requests between the two Spring Boot application instances.

Both backend instances use the same application image and connect to the shared MySQL and Redis services.

---

# 🔐 Authentication Flow

```text
User
 │
 ▼
Login Request
 │
 ▼
POST /api/users/login
 │
 ▼
Spring Security
 │
 ▼
Credentials Verified
 │
 ▼
JWT Generated
 │
 ▼
Client
 │
 ▼
Authorization: Bearer <token>
 │
 ▼
Protected REST API
```

### Protected Request

```http
GET /api/users/me
Authorization: Bearer <JWT_TOKEN>
```

---

# 🌐 API Examples

## Authentication

```text
POST /api/users/register
POST /api/users/login
GET  /api/users/me
```

## Projects

```text
GET  /api/projects
POST /api/projects
GET  /api/projects/{id}
```

## Members

```text
GET  /api/projects/{id}/members
POST /api/projects/{id}/members
```

## Documents

```text
GET  /api/documents/project/{id}
POST /api/documents/...
```

---

# 🐳 Docker Deployment

The complete backend infrastructure is containerized using **Docker Compose**.

### Containers

```text
┌──────────────────────────────────────────┐
│              Docker Compose              │
│                                          │
│  ┌────────────────────────────────────┐  │
│  │        research-nexus-nginx        │  │
│  └──────────────────┬─────────────────┘  │
│                     │                    │
│        ┌────────────┴────────────┐       │
│        │                         │       │
│        ▼                         ▼       │
│ research-nexus-1        research-nexus-2│
│        │                         │       │
│        └────────────┬────────────┘       │
│                     │                    │
│              ┌──────┴──────┐             │
│              ▼             ▼             │
│     research-nexus-    research-nexus-  │
│         mysql              redis         │
│                                          │
└──────────────────────────────────────────┘
```

The two Spring Boot instances use the same Docker image:

```text
research-nexus:latest
```

Nginx exposes the backend through:

```text
Port 8090
```

---

# ⚖️ Nginx Load Balancing

Nginx sits in front of the Spring Boot application instances.

```text
                    Client
                      │
                      ▼
                  ┌───────┐
                  │ Nginx │
                  └───┬───┘
                      │
             ┌────────┴────────┐
             │                 │
             ▼                 ▼
        Backend 1          Backend 2
```

Incoming requests are distributed across both application instances.

This provides a basic form of **horizontal scaling**, allowing the backend to handle more concurrent traffic than a single application container.

---

# ☁️ AWS EC2 Deployment

Research Nexus is deployed on an **AWS EC2 instance**.

The EC2 server runs:

* Docker
* Docker Compose
* Nginx
* Two Spring Boot application containers
* MySQL
* Redis

The backend is exposed through Nginx rather than exposing the Spring Boot containers directly.

```text
Internet
   │
   ▼
AWS EC2
   │
   ▼
Nginx :8090
   │
   ├──────────────► Spring Boot #1
   │
   └──────────────► Spring Boot #2
```

---

# 🔄 CI/CD Pipeline

Research Nexus uses **GitHub Actions** to automate continuous integration and deployment.

## Continuous Integration

Every push and pull request targeting the `main` branch triggers the CI build.

```text
Developer
   │
   │ git push / Pull Request
   ▼
GitHub
   │
   ▼
GitHub Actions
   │
   ▼
Checkout Source
   │
   ▼
Setup Java 21
   │
   ▼
Maven Build
   │
   ▼
Build Successful
```

The application is built using:

```bash
chmod +x mvnw
./mvnw clean package -DskipTests
```

The CI pipeline ensures that the application can be successfully built before deployment.

---

# 🚀 Continuous Deployment

When a change is pushed to the `main` branch and the build succeeds, GitHub Actions automatically deploys the latest version to AWS EC2.

```text
Developer
   │
   │ git push origin main
   ▼
GitHub Repository
   │
   ▼
GitHub Actions
   │
   ▼
CI Build
   │
   ▼
Build Successful
   │
   ▼
SSH Connection to EC2
   │
   ▼
Pull Latest Code
   │
   ▼
Build Docker Images
   │
   ▼
Restart Backend Containers
   │
   ▼
Nginx
   │
   ▼
🚀 Live Application
```

### Deployment Commands

```bash
cd ~/research-nexus

git pull origin main

docker compose build research-nexus-1 research-nexus-2

docker compose up -d --no-deps research-nexus-1 research-nexus-2

docker image prune -f
```

This automates the deployment process and eliminates the need to manually rebuild and restart the backend after every successful change.

---

# ⚙️ GitHub Actions

The CI/CD workflow is located at:

```text
.github/workflows/ci.yml
```

The workflow contains two jobs:

```text
build
  │
  ▼
deploy
```

The deployment job depends on the successful completion of the build job:

```yaml
needs: build
```

Deployment is restricted to pushes on the `main` branch:

```yaml
if: github.event_name == 'push' && github.ref == 'refs/heads/main'
```

This prevents pull requests from automatically deploying to the production EC2 environment.

---

# 🔑 Deployment Secrets

EC2 deployment credentials are securely stored using **GitHub Actions Secrets**.

```text
EC2_HOST
EC2_USER
EC2_SSH_KEY
```

The private SSH key and server credentials are not committed to the repository.

GitHub Actions injects these secrets only during the deployment process.

---

# 💻 Local Development

## Prerequisites

Install:

```text
Java 21
Maven
MySQL
Redis
Docker
Docker Compose
Git
```

---

## 📥 Clone the Repository

```bash
git clone https://github.com/Karthik-nan/research-nexus.git

cd research-nexus
```

---

## ▶️ Run with Maven

### Linux / macOS

```bash
./mvnw spring-boot:run
```

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

---

## 🐳 Run with Docker Compose

Start the complete infrastructure:

```bash
docker compose up -d
```

Check running containers:

```bash
docker ps
```

Stop the infrastructure:

```bash
docker compose down
```

---

# 📁 Project Structure

```text
research-nexus/
│
├── src/
│   └── main/
│       ├── java/
│       └── resources/
│
├── nginx/
│   └── nginx.conf
│
├── .github/
│   └── workflows/
│       └── ci.yml
│
├── docker-compose.yml
├── Dockerfile
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

# 📈 Scalability

The application demonstrates **horizontal backend scaling**.

### Traditional Single Instance

```text
Client
  │
  ▼
Backend
```

### Research Nexus

```text
Client
  │
  ▼
Nginx
  │
  ├──────────────► Backend Instance 1
  │
  └──────────────► Backend Instance 2
```

Both instances run the same Spring Boot application and share the same MySQL and Redis infrastructure.

This architecture allows additional backend instances to be introduced as application traffic grows.

---

# ⚡ Redis

Redis is included as a shared infrastructure component for caching and future distributed application capabilities.

The architecture is designed so that multiple Spring Boot instances can communicate with the same Redis service rather than maintaining separate instance-local state.

---

# 🗄️ Database

Research Nexus uses **MySQL 8** as its primary relational database.

The application uses:

* Spring Data JPA
* Hibernate
* Repository-based data access
* Project and member relationships
* Persistent user and project data

The MySQL database runs as a Docker container in the EC2 deployment.

---

# 🛡️ Security

The application implements:

* JWT authentication
* Spring Security
* BCrypt password hashing
* Protected REST endpoints
* Role-based authorization
* Project-level authorization
* CORS configuration
* Secure HTTP response headers

Protected endpoints require a valid JWT:

```text
Authorization: Bearer <JWT_TOKEN>
```

---

# 🔮 Future Improvements

* Kubernetes deployment
* Automated database migrations
* Automated test execution in CI
* Docker image registry
* Zero-downtime deployments
* Monitoring and observability
* Centralized logging
* WebSocket-based real-time collaboration
* Infrastructure as Code
* Automatic cloud scaling
* Production-grade secret management

---

# 🔗 Related Repositories

## Frontend

<p>
<a href="https://github.com/Karthik-nan/research-nexus-frontend">
<img src="https://img.shields.io/badge/Frontend-GitHub-181717?style=for-the-badge&logo=github">
</a>
</p>

## Backend

<p>
<a href="https://github.com/Karthik-nan/research-nexus">
<img src="https://img.shields.io/badge/Backend-GitHub-181717?style=for-the-badge&logo=github">
</a>
</p>

---

# 👨‍💻 Author

## Karthik Nandagiri

### Interests

* Java Backend Development
* Spring Boot
* Distributed Systems
* System Design
* Cloud Computing
* DevOps
* AI and LLM Integration

---

<p align="center">

## ⭐ Project

**If you find Research Nexus useful or interesting, consider giving the repository a star.**

</p>
