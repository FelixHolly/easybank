# EasyBank Backend

Spring Boot backend application for EasyBank.

## Prerequisites

- Java 25
- Maven
- Docker and Docker Compose

## Getting Started

### 1. Start the MySQL Database

The project includes a Docker Compose configuration for the MySQL database. Start it with:

```bash
docker-compose up -d
```

This will:
- Start a MySQL container named `springsecurity`
- Expose MySQL on port `3306`
- Create the `easybank` database
- Persist data in a Docker volume

To stop the database:

```bash
docker-compose down
```

To stop and remove all data:

```bash
docker-compose down -v
```

### 2. Run the Application

```bash
./mvnw spring-boot:run
```

Or if using Maven installed globally:

```bash
mvn spring-boot:run
```

### 3. Database Initialization

On startup, the application will:
- Automatically create database tables (using JPA entities)
- Execute `src/main/resources/data.sql` to populate initial customer data

## Default Login Credentials

- **Email:** felix@example.com
- **Password:** password

## Configuration

Environment variables can be set to override defaults in `application.properties`:

- `DATABASE_HOST` (default: localhost)
- `DATABASE_PORT` (default: 3306)
- `DATABASE_NAME` (default: easybank)
- `DATABASE_USERNAME` (default: root)
- `DATABASE_PASSWORD` (default: root)

## API Endpoints

### Public Endpoints
- `GET /contact` - Contact information
- `GET /notices` - System notices

### Protected Endpoints (Require Authentication)
- `GET /myAccount` - User account information
- `GET /myLoans` - User loans
- `GET /myCards` - User cards
- `GET /myBalance` - User balance
