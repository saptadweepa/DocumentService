# Document Service Application

This application provides document and author management capabilities through REST APIs.

## Features
1. **Author Management**: Create, view, edit, and delete authors.
2. **Document Management**: Create, view, edit, and delete documents.

### Security: Authentication and Authorization
- Any user can create documents.
- Only admin users can create authors.
- All endpoints are secured using username and password authentication.
- for testing purpose, two users are created by default

1. Admin user: username: admin password: adminpassword
2. Regular user: username: user password: userpassword

### Accessing REST Endpoints
The REST endpoints are available via [Swagger UI](http://localhost:8080/swagger-ui.html).
The swagger ui will ask for username and password, the user details provided above can be used.

### Kafka Integration
- All author and document lifecycle updates are published to Kafka.
- New authors and documents can also be created or deleted using Kafka events.
- To test creation and deletion via Kafka, access the `/api/v1/event-trigger` endpoints through Swagger UI.

## Running the Application

### Prerequisites
1. Docker installed
2. Java 17 installed

### Steps to Run
1. Navigate to the application folder: `/docker`
2. Run the command: `docker-compose up`
3. Start the application: `./gradlew bootRun`

## Technical Details
1. **Database**: The application uses PostgreSQL.
2. **Event Messaging**: Kafka is used for sending and receiving events.

## Testing
The application features are tested end-to-end with comprehensive integration tests.
test can be run using `./gradlew.bat test` following `docker-compose up`