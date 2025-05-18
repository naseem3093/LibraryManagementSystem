# 📚 Library Management System — Microservices Edition

![CI](https://img.shields.io/github/actions/workflow/status/naseem3093/LibraryManagementSystem/ci.yml?style=for-the-badge)
![License](https://img.shields.io/github/license/naseem3093/LibraryManagementSystem?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-17-blue?style=for-the-badge\&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring Boot-3.x-6DB33F?style=for-the-badge\&logo=spring)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=for-the-badge\&logo=mysql)

> **A production‑ready, modular Library Management System built with Spring Boot microservices and secured, scalable architecture.**

---

## ✨ Features at a Glance

| Module                   | Key Capabilities                                             |
| ------------------------ | ------------------------------------------------------------ |
| **Book Service**         | CRUD books, search/filter, bulk import via CSV               |
| **Member Service**       | Register members, profile management, role‑based auth        |
| **Borrow Service**       | Issue/return books, transactional guarantees, inventory sync |
| **Fine Service**         | Real‑time fine calculation, scheduled reminders              |
| **Notification Service** | Email/SMS/WebPush via asynchronous event bus                 |

*Fully stateless services* communicate over **REST** & **RabbitMQ** events, exposing **OpenAPI 3** specs for easy integration.

---

## 🏛️ Architecture

```mermaid
flowchart LR
    UI[React / Angular / Vue]
    subgraph Backend
        A[API Gateway]
        B[Auth Service]
        Book[Book Service]
        Mem[Member Service]
        Bor[Borrow Service]
        Fine[Fine Service]
        Notif[Notification Service]
    end
    DB[(MySQL | PostgreSQL)]
    MQ[[RabbitMQ]]

    UI --> A
    A -->|JWT| B
    A --> Book & Mem & Bor & Fine & Notif
    Book & Mem & Bor & Fine & Notif --> DB
    Book & Mem & Bor & Fine & Notif --> MQ
    Notif -->|Events| MQ
```

*Generated diagrams are auto‑synced in CI/CD.*

---

## 🚀 Quick Start

```bash
# 1. Clone
$ git clone https://github.com/naseem3093/LibraryManagementSystem.git
$ cd LibraryManagementSystem

# 2. Spin up infrastructure (Docker Compose)
$ docker compose up -d mysql rabbitmq

# 3. Build & run all services
$ ./mvnw clean install -DskipTests
$ docker compose up --build

# 4. Explore APIs
→ http://localhost:8080/swagger-ui.html
```

Need just one service?

```bash
$ cd book-service && ./mvnw spring-boot:run
```

---

## 🧰 Tech Stack

* **Java 17**, **Spring Boot 3**, **Spring Cloud**
* **MySQL 8** with **Flyway** migrations
* **RabbitMQ** for event‑driven comms
* **OpenAPI 3** docs with **SpringDoc**
* **Docker / Docker Compose** for containerisation
* **GitHub Actions** CI + **SonarCloud** quality gates

---

## 🧪 Tests & Quality Gates

```bash
# Run unit + integration tests
$ ./mvnw verify
```

* 90%+ **line coverage** enforced
* Mutation testing via **PIT**
* Static analysis (**Checkstyle**, **SpotBugs**)

---

## 🌐 Deployment

| Environment | Branch      | URL                                                            |
| ----------- | ----------- | -------------------------------------------------------------- |
| **Dev**     | `develop`   | [https://dev.library‑ms.io](https://dev.library‑ms.io)         |
| **Staging** | `release/*` | [https://staging.library‑ms.io](https://staging.library‑ms.io) |
| **Prod**    | `main`      | [https://library‑ms.io](https://library‑ms.io)                 |

Deploys are automated through **GitHub Actions → Docker Hub → Kubernetes (Helm)**.

---

## 📈 Roadmap

* [ ] GraphQL gateway
* [ ] ElasticSearch full‑text search
* [ ] Kafka migration for high‑throughput events
* [ ] Server‑sent events (SSE) for live notifications

---

## 🤝 Contributing

1. Fork the repo & create your branch (`git checkout -b feat/amazing-feature`)
2. Write tests and follow the **Conventional Commits** spec
3. Submit a pull request 🚀

See [`CONTRIBUTING.md`](CONTRIBUTING.md) for full guidelines.

---

## 💬 Community & Support

* **Discussions**: GitHub → *Questions & Ideas*
* **Slack**: `#library-ms` on the Spring Community workspace
* **Email**: [maintainer@example.com](mailto:maintainer@example.com)

---

## 📝 License

Distributed under the **MIT License**. See [`LICENSE`](LICENSE) for more information.

---

> Built with ❤️  by **Naseem the Great** and contributors.")}
