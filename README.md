\# Research Nexus



\## Scalable Collaborative Research Platform



Research Nexus is a full-stack collaborative research platform designed to help researchers create projects, manage teams, organize research documents, and collaborate securely.



The system is built with a scalable backend architecture using \*\*Spring Boot, Spring Security, JWT, MySQL, Redis, Docker, Nginx, AWS EC2, and GitHub Actions\*\*.



\---



\## Architecture



```text

&#x20;                        Internet

&#x20;                           |

&#x20;                           v

&#x20;                      AWS EC2

&#x20;                           |

&#x20;                           v

&#x20;                        Nginx

&#x20;                      Port 8090

&#x20;                           |

&#x20;               +-----------+-----------+

&#x20;               |                       |

&#x20;               v                       v

&#x20;       Spring Boot #1          Spring Boot #2

&#x20;         Container               Container

&#x20;               |                       |

&#x20;               +-----------+-----------+

&#x20;                           |

&#x20;                  +--------+--------+

&#x20;                  |                 |

&#x20;                  v                 v

&#x20;                MySQL             Redis

```



The backend runs multiple Spring Boot instances behind Nginx, providing \*\*horizontal scaling and load distribution\*\*.



\---



\## Features



\### Authentication and Security



\* User registration and login

\* JWT-based authentication

\* Spring Security integration

\* Password hashing using BCrypt

\* Protected REST APIs

\* Role-based authorization

\* Project-level access control

\* CORS configuration

\* Secure HTTP response headers



\### Research Project Management



\* Create research projects

\* View owned and accessible projects

\* View project details

\* Manage project members

\* Assign project roles

\* Enforce project-level permissions



\### Document Management



\* Upload research documents

\* Retrieve project documents

\* Organize documents by research project

\* Secure document access

\* Project-based document authorization



\### Collaboration



\* Add members to research projects

\* Manage project membership

\* Owner and member roles

\* Collaborative project workspace



\---



\## Technology Stack



\### Backend



\* Java 21

\* Spring Boot

\* Spring Security

\* JWT

\* Spring Data JPA

\* Hibernate

\* REST APIs



\### Database and Caching



\* MySQL 8

\* Redis



\### DevOps and Infrastructure



\* Docker

\* Docker Compose

\* Nginx

\* AWS EC2

\* GitHub Actions

\* Git



\### Frontend



\* React

\* Vite

\* JavaScript



Frontend repository:



https://github.com/Karthik-nan/research-nexus-frontend



\---



\## Backend Architecture



```text

&#x20;                        Client

&#x20;                           |

&#x20;                           v

&#x20;                         Nginx

&#x20;                           |

&#x20;                 +---------+---------+

&#x20;                 |                   |

&#x20;                 v                   v

&#x20;          Backend Instance 1   Backend Instance 2

&#x20;                 |                   |

&#x20;                 +---------+---------+

&#x20;                           |

&#x20;                   +-------+-------+

&#x20;                   |               |

&#x20;                   v               v

&#x20;                 MySQL           Redis

```



Nginx acts as the \*\*reverse proxy and load balancer\*\*, distributing incoming requests between the two Spring Boot application instances.



Both backend instances use the same application image and connect to the shared MySQL and Redis services.



\---



\## Authentication Flow



```text

User

&#x20;|

&#x20;v

Login Request

&#x20;|

&#x20;v

POST /api/users/login

&#x20;|

&#x20;v

Spring Security

&#x20;|

&#x20;v

Credentials Verified

&#x20;|

&#x20;v

JWT Generated

&#x20;|

&#x20;v

Client

&#x20;|

&#x20;v

Authorization: Bearer <token>

&#x20;|

&#x20;v

Protected REST API

```



Example protected request:



```http

GET /api/users/me

Authorization: Bearer <JWT\_TOKEN>

```



\---



\## API Examples



\### Authentication



```text

POST /api/users/register

POST /api/users/login

GET  /api/users/me

```



\### Projects



```text

GET  /api/projects

POST /api/projects

GET  /api/projects/{id}

```



\### Members



```text

GET  /api/projects/{id}/members

POST /api/projects/{id}/members

```



\### Documents



```text

GET  /api/documents/project/{id}

POST /api/documents/...

```



\---



\# Docker Deployment



The complete backend infrastructure is containerized using Docker Compose.



The deployment consists of:



```text

research-nexus-1

research-nexus-2

research-nexus-mysql

research-nexus-redis

research-nexus-nginx

```



The two Spring Boot instances use the same Docker image:



```text

research-nexus:latest

```



Nginx exposes the backend through:



```text

Port 8090

```



\---



\# Nginx Load Balancing



Nginx sits in front of the Spring Boot application instances.



```text

&#x20;                    Client

&#x20;                      |

&#x20;                      v

&#x20;                    Nginx

&#x20;                      |

&#x20;             +--------+--------+

&#x20;             |                 |

&#x20;             v                 v

&#x20;         Backend 1         Backend 2

```



Incoming requests are distributed across both application instances.



This provides a basic form of \*\*horizontal scaling\*\*, allowing the backend to handle more concurrent traffic than a single application container.



\---



\# AWS EC2 Deployment



Research Nexus is deployed on an \*\*AWS EC2 instance\*\*.



The EC2 server runs:



\* Docker

\* Docker Compose

\* Nginx

\* Two Spring Boot application containers

\* MySQL

\* Redis



The backend is exposed through Nginx rather than exposing the Spring Boot containers directly.



```text

Internet

&#x20;  |

&#x20;  v

AWS EC2

&#x20;  |

&#x20;  v

Nginx :8090

&#x20;  |

&#x20;  +------ Spring Boot #1

&#x20;  |

&#x20;  +------ Spring Boot #2

```



\---



\# CI/CD Pipeline



Research Nexus uses \*\*GitHub Actions\*\* to automate continuous integration and deployment.



\## Continuous Integration



Every push and pull request targeting the `main` branch triggers the CI build.



```text

Developer

&#x20;   |

&#x20;   | git push / Pull Request

&#x20;   v

GitHub

&#x20;   |

&#x20;   v

GitHub Actions

&#x20;   |

&#x20;   v

Checkout Source

&#x20;   |

&#x20;   v

Setup Java 21

&#x20;   |

&#x20;   v

Maven Build

&#x20;   |

&#x20;   v

Build Successful

```



The application is built using:



```bash

chmod +x mvnw

./mvnw clean package -DskipTests

```



The CI pipeline ensures that the application can be successfully built before deployment.



\---



\## Continuous Deployment



When a change is pushed to the `main` branch and the build succeeds, GitHub Actions automatically deploys the latest version to AWS EC2.



```text

Developer

&#x20;   |

&#x20;   | git push origin main

&#x20;   v

GitHub Repository

&#x20;   |

&#x20;   v

GitHub Actions

&#x20;   |

&#x20;   v

CI Build

&#x20;   |

&#x20;   v

Build Successful

&#x20;   |

&#x20;   v

SSH Connection to EC2

&#x20;   |

&#x20;   v

Pull Latest Code

&#x20;   |

&#x20;   v

Build Docker Images

&#x20;   |

&#x20;   v

Restart Backend Containers

&#x20;   |

&#x20;   v

Nginx

&#x20;   |

&#x20;   v

Live Application

```



The deployment process executes:



```bash

cd \~/research-nexus

git pull origin main



docker compose build research-nexus-1 research-nexus-2



docker compose up -d --no-deps research-nexus-1 research-nexus-2



docker image prune -f

```



This automates the deployment process and eliminates the need to manually rebuild and restart the backend after every successful change.



\---



\# GitHub Actions



The CI/CD workflow is located at:



```text

.github/workflows/ci.yml

```



The workflow contains two jobs:



```text

build

&#x20; |

&#x20; v

deploy

```



The deployment job depends on the successful completion of the build job:



```yaml

needs: build

```



Deployment is restricted to pushes on the `main` branch:



```yaml

if: github.event\_name == 'push' \&\& github.ref == 'refs/heads/main'

```



This prevents pull requests from automatically deploying to the production EC2 environment.



\---



\# Deployment Secrets



EC2 deployment credentials are securely stored using \*\*GitHub Actions Secrets\*\*.



The workflow uses:



```text

EC2\_HOST

EC2\_USER

EC2\_SSH\_KEY

```



The private SSH key and server credentials are not committed to the repository.



GitHub Actions injects these secrets only during the deployment process.



\---



\# Local Development



\## Prerequisites



Install:



\* Java 21

\* Maven

\* MySQL

\* Redis

\* Docker

\* Docker Compose

\* Git



\---



\## Clone the Repository



```bash

git clone https://github.com/Karthik-nan/research-nexus.git

cd research-nexus

```



\---



\## Run with Maven



Linux/macOS:



```bash

./mvnw spring-boot:run

```



Windows:



```powershell

.\\mvnw.cmd spring-boot:run

```



\---



\## Run with Docker Compose



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



\---



\# Project Structure



```text

research-nexus/

|

+-- src/

|   +-- main/

|       +-- java/

|       +-- resources/

|

+-- nginx/

|   +-- nginx.conf

|

+-- .github/

|   +-- workflows/

|       +-- ci.yml

|

+-- docker-compose.yml

+-- Dockerfile

+-- pom.xml

+-- mvnw

+-- mvnw.cmd

+-- README.md

```



\---



\# Scalability



The application demonstrates \*\*horizontal backend scaling\*\*.



A traditional single-instance deployment would look like:



```text

Client

&#x20; |

&#x20; v

Backend

```



Research Nexus instead uses:



```text

Client

&#x20; |

&#x20; v

Nginx

&#x20; |

&#x20; +------ Backend Instance 1

&#x20; |

&#x20; +------ Backend Instance 2

```



Both instances run the same Spring Boot application and share the same MySQL and Redis infrastructure.



This architecture allows additional backend instances to be introduced as application traffic grows.



\---



\# Redis



Redis is included as a shared infrastructure component for caching and future distributed application capabilities.



The architecture is designed so that multiple Spring Boot instances can communicate with the same Redis service rather than maintaining separate instance-local state.



\---



\# Database



Research Nexus uses \*\*MySQL 8\*\* as its primary relational database.



The application uses:



\* Spring Data JPA

\* Hibernate

\* Repository-based data access

\* Project and member relationships

\* Persistent user and project data



The MySQL database runs as a Docker container in the EC2 deployment.



\---



\# Security



The application implements:



\* JWT authentication

\* Spring Security

\* BCrypt password hashing

\* Protected REST endpoints

\* Role-based authorization

\* Project-level authorization

\* CORS configuration

\* Secure HTTP response headers



Protected endpoints require a valid JWT:



```text

Authorization: Bearer <JWT\_TOKEN>

```



\---



\# Future Improvements



\* Kubernetes deployment

\* Automated database migrations

\* Automated test execution in CI

\* Docker image registry

\* Zero-downtime deployments

\* Monitoring and observability

\* Centralized logging

\* WebSocket-based real-time collaboration

\* Infrastructure as Code

\* Automatic cloud scaling

\* Production-grade secret management



\---



\# Related Repository



\## Frontend



https://github.com/Karthik-nan/research-nexus-frontend



\## Backend



https://github.com/Karthik-nan/research-nexus



\---



\# Author



\*\*Karthik Nandagiri\*\*



\### Interests



\* Java Backend Development

\* Spring Boot

\* Distributed Systems

\* System Design

\* Cloud Computing

\* DevOps

\* AI and LLM Integration



\---



\## ⭐ Project



If you find Research Nexus useful or interesting, consider giving the repository a star.



