# cohort-10-java-21690-rehan
Cohort 10 — JAVA Fullstack (JAVA+ReactJS) assignment for Rehan Khalil Ahmed


## About

This is a contact management system. Right now it has the user part done: register, verify with a code, login and logout. Contacts will be added in the next PRs.

Built with Spring Boot (Java 17) for the backend, React (Vite) for the frontend and SQL Server for the database.

## Folders

- `backend` - Spring Boot project
- `frontend` - React project

## Before you run it

You need Java 17, Node.js and SQL Server installed. SQL Server should have TCP/IP turned on and Mixed Mode login. You don't need to install Maven, the project already has `mvnw`.

## How to run

1. Create the database:

```
sqlcmd -S localhost -E -C -Q "CREATE DATABASE contactsdb"
```

The tables get created by themselves when the backend starts.

2. Open `backend/src/main/resources/application.properties` and put your own SQL Server password here (and change the username too if you don't use `sa`):

```
spring.datasource.password=YourPasswordHere
```

3. Start the backend:

```
cd backend
.\mvnw spring-boot:run
```

It runs on `http://localhost:8080`. On Mac or Linux use `./mvnw spring-boot:run`.

4. In a new terminal, start the frontend:

```
cd frontend
npm install
npm run dev
```

Then open `http://localhost:5173`.

## How it works

Register with a username, password and an email or phone number. After that you go to the verify page and enter the 5-digit code. The code is printed in the backend terminal, in a line like `Verification code for rehan7: 48213`. It works for 5 minutes. If it expires you can press "Resend code" (one new code per minute).

Once you are verified, log in with your username, email or phone. The home page shows your details and has a logout button.

## APIs

- `POST /api/auth/register` - create account
- `POST /api/auth/verify` - check the code
- `POST /api/auth/resend-code` - get a new code
- `POST /api/auth/login` - login, returns a token
- `GET /api/users/me` - current user (needs the token)

## Notes

- The code is only printed in the log for now. Email sending will come later.
- Passwords are saved encrypted.
- The `jwt.secret` in `application.properties` is just a dev value.