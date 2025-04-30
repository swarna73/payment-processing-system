# Payment Processing System

A secure and scalable backend system for processing digital payments asynchronously.  
This project is part of my fintech-focused engineering manager portfolio.

---

## 🚀 Features

- ✅ User authentication with JWT
- ✅ Role-based access control (`USER`, `ADMIN`)
- ✅ PostgreSQL integration
- 🔄 Asynchronous payment processing (coming soon)
- 🧾 Transaction logging and retry logic (upcoming)

---

## 🔧 Tech Stack

- **Java 17**
- **Spring Boot 3**
- **Spring Security (JWT)**
- **Spring Data JPA**
- **PostgreSQL**
- **Lombok**

---

## 🔐 Authentication Flow

- `POST /api/auth/login` – Login with email and password, returns JWT token
- Secure endpoints are protected via JWT-based authentication

---

## 🛠 Setup

1. **Clone the repo**  
   ```bash
   git clone https://github.com/swarna73/payment-processing-system.git
   cd payment-processing-system

2.   Configure DB
Create a PostgreSQL database named paymentsystem
Update src/main/resources/application.properties with your DB credentials:


spring.datasource.username=postgres
spring.datasource.password=your_password


	3.	Run the app
./mvnw spring-boot:run
 
