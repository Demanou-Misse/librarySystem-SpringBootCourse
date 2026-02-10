# 📚 LibrarySystem — Enterprise Java Backend Application

## Project Overview

LibrarySystem is a backend application built with **modern Java and Spring Boot**, designed to manage a digital library system including books, users, borrowing workflows, and access control.

The project focuses on **clean architecture**, **secure API design**, and **enterprise-grade backend best practices**.  
It goes beyond basic CRUD operations by emphasizing **maintainability, scalability, and correctness**, following patterns commonly used in real-world Java applications.

---

## Key Features

- User, book, and loan management
- Secure authentication and authorization using JWT
- Role-Based Access Control (RBAC)
- RESTful API with proper HTTP semantics
- Pagination, sorting, and dynamic filtering
- DTO-based API design to protect domain models
- Centralized exception handling
- File upload, download, and preview support
- Fully documented API with Swagger / OpenAPI

---

## Tech Stack

### Backend (Core)

- **Java 17 / Java 21**  
  Modern Java features and improved performance.

- **Spring Boot 3.x**  
  Application bootstrap, inversion of control (IoC), and auto-configuration.

- **Spring Data JPA**  
  Repository abstraction for data access and persistence.

- **Hibernate**  
  ORM implementation for object–relational mapping.

---

### Security & Authentication

- **Spring Security**  
  Security filter chain configuration and request authorization.

- **JWT (JSON Web Tokens)**  
  Stateless authentication mechanism.

- **RBAC (Role-Based Access Control)**  
  Authorization based on roles:
  - USER
  - LIBRARIAN
  - ADMIN

---

### API & Data Handling

- **Spring Web (REST)**  
  RESTful API using standard HTTP verbs and status codes.

- **DTO Pattern**  
  Clear separation between persistence models and API contracts.

- **MapStruct**  
  Type-safe and performant mapping between entities and DTOs.

- **Bean Validation (Jakarta Validation)**  
  Input validation using annotations such as `@Valid`, `@NotNull`, and `@Size`.

- **Pagination & Sorting**  
  Efficient handling of large datasets using `Pageable` and `Sort`.

- **Dynamic Filtering**  
  Advanced querying using Specifications / search criteria.

- **Jackson**  
  JSON serialization and deserialization.

---

## Architecture & Design Principles

### N-Tier Architecture

The application follows a strict layered architecture:

- **Controller Layer**  
  Handles HTTP requests and responses.

- **Service Layer**  
  Contains business logic and application rules.

- **Repository Layer**  
  Manages database access and persistence.

This separation improves **maintainability, testability, and scalability**.

---

### Clean Code & Best Practices

- SOLID principles
- Clear responsibility per class
- No business logic inside controllers
- Domain rules enforced at service level

---

### Global Exception Handling

All exceptions are handled centrally using `@RestControllerAdvice`, providing:

- Consistent error responses
- Meaningful HTTP status codes
- Unified API response structure

---

## Testing & Quality Assurance

- **JUnit 5**  
  Unit testing of services and business logic.

- **Mockito**  
  Mocking dependencies to isolate components.

- **Integration Testing**  
  Validation of repository and persistence behavior.

---

## File & Media Management

- Custom file storage system
- Upload, download, and preview of documents (PDFs, images)
- Secure handling of file metadata and paths

---

## API Documentation

The API is fully documented using **Swagger / OpenAPI 3**.

After starting the application, the documentation is available at: /swagger-ui.html


---

## Prerequisites & Installation

### Requirements

- Java 17 or Java 21
- Maven
- PostgreSQL (or H2 for local testing)

### Run the application

``bash
mvn clean install
mvn spring-boot:run

## What This Project Demonstrates

- Strong foundation in enterprise Java backend development  
- Secure REST API design with authentication and authorization  
- Clean architecture and professional project structure  
- Proper use of DTOs, mapping, validation, and centralized error handling  
- Readiness for more advanced systems and complex domains  

---

## Next Steps

LibrarySystem serves as a solid foundation project.

The next project, **LernEmpire**, builds on this base by introducing:

- A complex domain-driven algorithm based on spaced repetition  
- Stronger architectural boundaries and clearer domain modeling  
- Advanced data modeling with full learning history traceability  
- Full-stack implementation and DevOps-oriented delivery  

---

## Contact

GitHub: Demanou-Misse  
Email: missedemanou@gmail.com

---
Keep pushing your limits. SpringBoot unlocked 🚀
