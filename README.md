# cohort-10-java-21690-rehan
Cohort 10 — JAVA Fullstack (JAVA+ReactJS) assignment for Rehan Khalil Ahmed

## About

A contact management system. For now it has register, verify with a code, login and logout.

Spring Boot (Java 17), React (Vite) and SQL Server.

## Setup

You need Java 17, Node.js 22.12 or newer and SQL Server (TCP/IP on, Mixed Mode login).

1. Make the database:

```
sqlcmd -S localhost -E -C -Q "CREATE DATABASE contactsdb"
```

2. Put your own SQL Server password in `backend/src/main/resources/application.properties`:

```
spring.datasource.password=YourPasswordHere
```

3. Start the backend (`http://localhost:8080`):

```
cd backend
.\mvnw spring-boot:run
```

4. Start the frontend in a new terminal (`http://localhost:5173`):

```
cd frontend
npm install
npm run dev
```

## Using it

Register, then enter the 5-digit code that is printed in the backend terminal (`Verification code for ...`). It lasts 5 minutes and you get 5 tries. Press "Resend code" for a new one. After that you can log in.

## Note

The token secret is random at every start, so restarting the backend logs you out. To use your own, set the `JWT_SECRET` environment variable.