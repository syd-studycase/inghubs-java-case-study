# ING Hubs Türkiye - Case Study (Brokerage Firm Challenge) for Java Backend Developer

## Technologies

- Java 17
- Spring Boot 3.x
- Spring Security and OAuth2 Resource Server
- Spring Data JPA
- H2
- Keycloak (Authentication)
- Docker and Docker Compose
- Maven

## Running the Project

### Prerequisites

- Docker and Docker Compose
- JDK 17

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/syd-studycase/inghubs-java-case-study.git
   cd inghubs-java-case-study
   ```

2. Start the services using Docker Compose:
   ```bash
   docker-compose up -d
   ```

3. Configure Keycloak:
    - Open `http://localhost:8181/` in your browser
    - Log in to the Keycloak admin panel using the credentials `admin/admin`
    - Realm settings will be imported after `docker-compose up -d command`
    - You can see the predefined **roles** for customers and admin users

4. Start microservices in order using environment variable as `spring.profiles.active=dev`
    - Config Server, Discovery Server, ..., Gateway Service

    | Service   | URL            | API Documentation |
    |-----------|----------------|------------------|
    | Gateway   | localhost:9090 | -                |
    | Config    | localhost:8888 | -                |
    | Discovery | localhost:8761 | -                |
    | Asset     | localhost:8083 | localhost:8083/swagger-ui/index.html  |
    | Order     | localhost:8082 | localhost:8082/swagger-ui/index.html  |

5. API testing:
    - You can test the Order Service APIs using Postman or any other API testing tool
    - Use the access token retrieved from Keycloak in the format `Authorization: Bearer {token}` for each request
    - For retrieved access token 
      - Access Token URL -> http://localhost:8181/realms/inghubs/protocol/openid-connect/token
      - Client ID -> inghubs-api
      - Client Secret -> qdq1WVRMcDFNWrmMKfwS6xB4H1iNFtfO
      - Username -> admin1
      - Password -> 123456

--- 

