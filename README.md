# Library Management REST API

A Library Management backend developed using Java, Spring Boot, Spring Web, Spring Data JPA, Hibernate and MySQL.

## 1. Project architecture

The project follows the required layered architecture:

`Controller -> Service -> Repository -> MySQL Database`

- **Entity:** Java classes mapped to database tables.
- **Controller:** receives REST API requests.
- **Service interface and implementation:** contains the application/business logic.
- **Repository:** uses Spring Data JPA to perform database operations.
- **GlobalExceptionHandler:** returns common error responses using `@ControllerAdvice`.

## 2. Technology used

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA / Hibernate
- MySQL
- Maven
- Lombok
- Postman

## 3. Database setup

Create the database in MySQL:

```sql
CREATE DATABASE library_management;
```

The application uses the database name `library_management`. Hibernate creates/updates the tables from the entity classes because `spring.jpa.hibernate.ddl-auto=update` is configured.

Open `src/main/resources/application.properties` and set your local database username and password, or provide them as environment variables:

- `DB_URL` (default: `jdbc:mysql://localhost:3306/library_management`)
- `DB_USERNAME` (default: `root`)
- `DB_PASSWORD` (default: password)

Do not commit real database passwords or other secrets to GitHub.

## 4. Run the project in Eclipse

1. Start MySQL and create the `library_management` database.
2. Open Eclipse.
3. Select **File -> Import -> Maven -> Existing Maven Projects**.
4. Select the extracted `LibraryManagementAPI` folder.
5. Finish the import and wait for Maven dependencies to download.
6. Check `application.properties` and set your MySQL credentials.
7. Right-click `LibraryManagementApplication.java` and select **Run As -> Java Application** (or **Spring Boot App**, if available).
8. The application runs at `http://localhost:8085`.

## 5. REST API endpoints

### Book Management

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/books` | Add a book |
| GET | `/api/books` | Get all books |
| GET | `/api/books/{id}` | Get one book |
| PUT | `/api/books/{id}` | Update a book |
| DELETE | `/api/books/{id}` | Delete a book |
| GET | `/api/books/category/{category}` | Search by category |

Example POST body:

```json
{
  "title": "Java Programming",
  "author": "Herbert Schildt",
  "isbn": "9780071809252",
  "category": "Programming",
  "totalCopies": 5,
  "availableCopies": 5
}
```

### Student Management

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/students` | Register a student |
| GET | `/api/students` | Get all students |
| GET | `/api/students/{id}` | Get one student |
| PUT | `/api/students/{id}` | Update a student |
| DELETE | `/api/students/{id}` | Delete a student |

Example POST body:

```json
{
  "studentName": "Arpan Patra",
  "email": "arpan@example.com",
  "department": "ECE",
  "phoneNumber": "1234567890"
}
```

### Book Issue Management

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/issues/{studentId}/{bookId}` | Issue a book |
| GET | `/api/issues` | Get all issue records |
| GET | `/api/issues/student/{studentId}` | Get issues for a student |
| PUT | `/api/issues/return/{issueId}` | Return an issued book |
| GET | `/api/issues/overdue` | Get overdue books |

The issue endpoint does not need a request body. Example:

`POST http://localhost:8085/api/issues/1/1`

The application assigns the issue date automatically and sets the due date 14 days later. The 14-day period is the default used in this implementation because the assignment does not specify a borrowing duration.

## 6. Business rules implemented

1. A book cannot be issued if `availableCopies` is zero.
2. The student and book must exist before issuing.
3. Issuing a book decreases `availableCopies` by one.
4. Returning a book increases `availableCopies` by one.
5. A returned book cannot be returned again.
6. Every issue receives an issue date and due date.
7. A student can have a maximum of three active issues.
8. An overdue issue has a due date before today's date and status `ISSUED`.
9. A book or student with existing issue records cannot be deleted, to avoid breaking the issue history.

Issue and return operations are transactional so the issue record and available copy count are updated together.

## 7. Validation and HTTP status codes

- Required text fields are validated.
- Student email format is validated.
- ISBN must be unique.
- Student email must be unique.
- Invalid request data returns **400 Bad Request**.
- Missing book, student or issue returns **404 Not Found**.
- Duplicate ISBN/email returns **409 Conflict**.
- No available copies returns **400 Bad Request**.
- A student exceeding the three-active-issue limit returns **400 Bad Request**.
- Deleting a book/student referenced by issue records returns **409 Conflict**.

The global exception handler is in `exceptions/GlobalExceptionHandler.java` and uses `@ControllerAdvice`.

## 8. Postman testing

Import this file into Postman:

`postman/LibraryManagementAPI.postman_collection.json`

The collection uses `baseUrl`, `bookId`, `studentId`, `issueId` and `outOfStockBookId` variables. First create a book and a student, then copy the IDs returned by the POST responses into the collection variables. Test the normal requests and the negative requests marked with an expected HTTP status. For the maximum-three-issues test, create three active issues for the same student before sending the fourth issue request. For the second-return test, return an issue successfully once and then repeat the same return request. To test the overdue endpoint immediately, you may set an active issue's `due_date` to a past date in MySQL before calling `/api/issues/overdue`. Test book/student deletion using records that have no issue history.

Save screenshots of successful and invalid API responses in the `screenshots` folder after testing. Export the collection from Postman as part of the submission.

## 9. Git and GitHub workflow

Create a GitHub repository named `LibraryManagementAPI`. From the project folder, use commands similar to:

```bash
git init
git add .
git commit -m "Initial Spring Boot project"
git branch -M development
git checkout -b feature/book-management
# Add and test Book entity, controller, service and repository
git add .
git commit -m "Add book management APIs"
git checkout development
git merge feature/book-management
```

Then add your GitHub remote and push the branches:

```bash
git remote add origin YOUR_GITHUB_REPOSITORY_URL
git push -u origin development
git push -u origin feature/book-management
```

Push the final code to GitHub. Do not put database passwords in the repository.
