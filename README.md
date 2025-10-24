
# Spring Security JWT Boilerplate

A modular Spring Security implementation with JWT authentication that can be easily integrated into any Spring Boot project. This repository was created as a learning project but structured to serve as a reusable security module.

## Overview

The project demonstrates a complete JWT-based authentication system built with Spring Security 6, featuring:
- JWT based authentication
- Role-based access control 
- Modular security package design
- Example REST API with basic CRUD operations

## Quick Start

### 1. Copy Security Package
Copy the entire `security` package from `src/main/java/com/darthchild/aurthor/security` to your project. The package includes:

```
security/
├── AuthController.java
├── SecurityConfig.java
├── JWT/
│   ├── JwtFilter.java
│   └── JwtUtils.java
├── model/
│   ├── Role.java
│   ├── User.java
│   ├── UserDTO.java
│   └── UserPrincipal.java
├── repo/
│   ├── RoleRepository.java
│   └── UserRepository.java
└── service/
├── AuthService.java
└── UserDetailsServiceImpl.java
```

### 2. Required Dependencies
Add these to your `pom.xml`:
```xml

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
</dependency>

<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

### 3. Configuration
Add these properties to your `application.yml`:
```yaml
jwt:
  secret: your-secret-key
  expirationMs: 100000  # token expiration time in milliseconds
```

## Key Features

1. **JWT Authentication**
    - Stateless authentication using JWT tokens
    - Configurable JWT filter
    - Token validation on protected endpoints
2. **Role-Based Access Control**
    - Define roles and permissions
    - Secure endpoints based on user roles
3. **User Management**
    - User registration and authentication
    - Secure password handling with BCrypt

## API Endpoints

### Auth Endpoints
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login and receive JWT token

### Protected Endpoints Example
- `GET /api/books` - Get all books 
- `POST /api/books` - Create book 
- `PUT /api/books/{id}` - Update book 
- `DELETE /api/books/{id}` - Delete book
- `GET /api/admin` - Test endpoint for ADMIN role

## Usage Example

1. **Authentication**: Initially, authenticate to receive a JWT token.
```java
// Login Request
POST /auth/login
{
    "username": "user@example.com",
    "password": "password123"
}

// Response
{
    "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

2. **Using JWT Token**: All further requests to protected endpoints must include the JWT token in the `Authorization` header.
```java
// Add to request headers
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

## Original Purpose

I created this project while learning Spring Security 6, with the specific goal
of creating a modular security implementation that could be reused across 
future Spring Boot projects requiring security.

## Note

Remember to:
- Change the JWT secret key
- Configure CORS settings as per your needs
- Update database configurations
- Modify user roles and permissions according to your requirements
