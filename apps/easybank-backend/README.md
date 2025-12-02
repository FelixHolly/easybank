# EasyBank Backend

Spring Boot backend application for EasyBank.

## Prerequisites

- Java 21
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

On startup with the dev profile, the application will:
- Automatically create/update database tables (using JPA entities)
- To populate initial customer data, set `SQL_INIT_MODE=always` as environment variable on first run
- After first run, set it to `never` to avoid duplicate data errors

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
- `SQL_INIT_MODE` (default: never)
- `ACTIVE_PROFILE` (default: dev)

## Data Model

### Monetary Values
All monetary fields use `BigDecimal` for precision and to avoid floating-point errors:
- **Account Transactions**: `transactionAmt`, `closingBalance`
- **Loans**: `totalLoan`, `amountPaid`, `outstandingAmount`
- **Cards**: `totalLimit`, `amountUsed`, `availableAmount`

Database columns are defined as `DECIMAL(19,2)` to store values with 2 decimal places.

### Pagination with Metadata
All paginated endpoints return a `PageResponse<T, M>` structure that combines:
- **Spring Data Page**: Paginated content and pagination metadata
- **Custom Summary**: Aggregate data computed from ALL records (not just current page)

**Example Response Structure**:
```json
{
  "page": {
    "content": [...],
    "number": 0,
    "size": 20,
    "totalElements": 100,
    "totalPages": 5,
    "first": true,
    "last": false
  },
  "metadata": {
    "currentBalance": 4280.35,
    "totalCredits": 12500.00,
    "totalDebits": 8219.65,
    "transactionCount": 100
  }
}
```

**Summary Types**:
- **BalanceSummary**: `currentBalance`, `totalCredits`, `totalDebits`, `transactionCount`
- **LoanSummary**: `totalLoanAmount`, `totalOutstanding`, `totalPaid`, `activeLoanCount`, `totalLoanCount`
- **CardSummary**: `totalCreditLimit`, `totalAvailable`, `totalUsed`, `overallUtilization`, `cardCount`

## API Endpoints

### Public Endpoints
- `GET /contact` - Contact information
- `GET /notices` - System notices

### Protected Endpoints (Require Authentication)
- `GET /myAccount` - User account information
- `GET /myLoans?page=0&size=20&sort=loanNumber,asc` - User loans with pagination and summary
- `GET /myCards?page=0&size=20&sort=cardId,asc` - User cards with pagination and summary
- `GET /myBalance?page=0&size=20&sort=transactionDt,desc` - User transactions with pagination and summary

**Pagination Parameters**:
- `page` (default: 0) - Page number (zero-indexed)
- `size` (default: 20) - Items per page
- `sort` (optional) - Sort criteria (e.g., `transactionDt,desc`)
