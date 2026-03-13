# Project Title: Java CLI Agenda

Console-based agenda application developed in Java 21 for managing tasks, events, and notes. 
The project implements SQL database persistence, layered architecture, organization by 
functionality, execution through Docker, and testing with JUnit 5.

Project developed as a final team assignment applying good design practices, modularity, and 
separation of concerns.

---

## Features

### Task Management (Eduard Cantos)
- Create, update, and delete tasks
- List all tasks
- List pending and completed tasks
- Mark tasks as completed

### Event Managements (Adrià Quiñoa)
- Create, list, update, and delete events
- List upcoming events

### Note Managements (Marc Fabregat)
- Create, list, update, and delete notes

### Característiques Tècniques
- Layered architecture
- Feature-based organization (task, event, note)
- DAO (Data Access Object) pattern
- Use of DTOs (Data Transfer Object) and Mappers
- Custom exception handling
- CLI application with interactive menus
- Database in Docker container
- Automatic scripts for database creation and data loading
- MongoDB database persistence
- Maven-managed project

---

## Tecnologíes utilitzades

|       Technology       |        Purpose        |
| ---------------------- | --------------------- |
| Java 21                | Business logic        |
| Maven                  | Project management    |
| noSQL                  | Data persistence      |
| MongoDB (in Docker)    | Database              |
| Docker                 | Containerization      |
| JUnit 5                | Testing               |

---

## Prerequisites

Before running the project, make sure you have installed:

- **Java 21**
- **Maven** (or dependency management and compilation)
- **Docker** and **Docker Compose** (for the database)
- **Git** (for cloning the repository)

---

## Installation and Execution Instructions

### 1. Clone the repository

git clone https://github.com/aquinoajimenez/Project-Tasca-S3.04---Developers-Team.git

### 2. Configure the database with Docker

Set up the MongoDB infrastructure using Docker to ensure a unified local environment 
without manual installations.
- The docker-compose.yml file launches a container with MongoDB 6.0.
- The mongo-init.js script automatically initializes the database on first startup.
- The users collection is configured with strict schema validation and unique indexes for 
email and username.
- The tasks collection includes schema validation, enums for priority and status, and a 
reference to the user.
- A test user (Juan Garcia) and a simulated task are automatically created for local 
testing.

Test it locally:
- Open a terminal in the project root folder.
- Run docker compose up -d.
- Verify in Docker Desktop that the cli_agenda_mongo container is running.

### 3. Compile the project with Mave
mvn clean compile

### 4. Run the application

mvn exec:java

### 5. Run the tests

mvn test

### 6. Stop the database

docker-compose down

---

## Project Structure

agenda_cli/
├── gitignore
├── docker-compose.yml
├── mongo-init.js
├── pom.xml
├── README.md
└── src/
    └── main/
    │   └── java/
    │      └── cli/
    │         └── agenda/
    │				├── Main.java
    │				├── application/
    │				│   ├── config/
    │				│   └── menu/
    │				├── common/	
    │				│   ├── exception/
    │				│   ├── mapper/
    │				│   ├── repository/
    │				│   └── utils/
    │				├── tasks/
    │				│   ├── dto/
    │				│   ├── dao/
    │				│   ├── exception/
    │				│   ├── mapper/
    │				│   ├── model/
    │				│   ├── repository/
    │				│   ├── service/
    │				│   └── cli/
    │				├── notes/
    │				│   ├── dto/
    │				│   ├── exception/
    │				│   ├── mapper/
    │				│   ├── model/
    │				│   ├── repository/
    │				│   ├── service/
    │				│   └── cli/
    │				└── events/
    │				│   ├── dto/
    │				│   ├── exception/
    │				│   ├── mapper/
    │				│   ├── model/
    │				│   ├── repository/
    │				│   ├── service/
    │				│   └── cli/
    │				└── infrastructure/	
    │				├── mongo/
    │				│	├── codecs/
    │				│	└── repository/	
    │				│		├── task/
    │				│		├── note/
    │				│		└── event/
    │				└── config/
    └── test/
        └── java/
            └── cli/
                └── agenda/
			├── tasks/
			│   ├── service/
			│   └── repository/
			├── notes/
			│   └── service/
			│   └── repository/
			├── notes/
			│   └── service/
			│   └── repository/
			└── common/
    				└── utils/

---

### 6. Collection Structure

**Collection Tasks**:
text (String - required)
due_date (Date - optional)
priority (ENUM ["LOW", "MEDIUM", "HIGH"] - required)
status (ENUM ["PENDING", "COMPLETED"] - required)
created_at (Date - required)

**Collection Notes**:
title (String - required)
content (String - optional)
category (ENUM ["TRABAJO", "UNIVERSIDAD", "SOCIAL"] - optional)
created_at (Date - required)
updated_at (Date - optional)

**Collection Events**:
title (String - required)
description (String - optional)
start_date (Date - required)
end_date (Date - required)
location (String - optional)
created_at (Date - required)
updated_at (Date - optional)

---

## Design patterns implemented

1. **Singleton**: ensures a single instance of the database connection.
2. **Builder**: in the Task class, allows creating instances with optional parameters and default 
   values in a fluent manner.
3. **Repository + DAO**: `TaskRepository` (interface) defines operations with domain entities, 
   while `TaskDAO` (interface) and `MongoDBTaskDAO` (implementation) manage access to MongoDB, 
   separating business logic from persistence.
4. **Factory Method**: `TaskServiceFactory` centralizes the creation of services and CLIs, 
   reducing coupling and allowing the Main class to act as a simple orchestrator.

---

## Tests performed

Happy Path for each CRUD element.

---

## Authors

Project developed by:

* Adrià Quiñoa
* Eduard Cantos
* Marc Fabregat

---

## License

This project **currently does not have a defined license**.
