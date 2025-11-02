# eStatement Service

An Account Statement Processing System built with Spring Boot that handles electronic statements in various formats.

## Overview

This service provides functionality for processing, managing, and generating account statements in multiple formats (CSV, PDF, Excel). It includes secure authentication using JWT and comprehensive data validation.

## Technologies Used

- **Java Version:** 17
- **Framework:** Spring Boot 3.5.6
- **Database:** MySQL
- **Security:** Spring Security with JWT
- **Documentation:** Spring Boot Starter
- **Build Tool:** Maven

### Key Dependencies

- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Security
- Spring Boot Starter Validation
- JWT (JSON Web Token)
- Apache Commons CSV
- iText 7 (PDF Processing)
- Apache POI (Excel Processing)
- Project Lombok
- MySQL Connector

## Features

- Secure authentication and authorization using JWT
- Account statement processing and storage
- Multiple format support:
  - CSV import/export
  - PDF generation
  - Excel file handling
- Data validation
- RESTful API endpoints
- File upload handling

## Getting Started

### Prerequisites

- JDK 17 or later
- Maven 3.6 or later
- MySQL Server

### Installation

1. Clone the repository:
   ```bash
   git clone [repository-url]
   ```

2. Configure MySQL database in `src/main/resources/application.properties`

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Project Structure

```
src/
├── main/
│   ├── java/         # Java source files
│   └── resources/    # Configuration files
├── test/
│   └── java/         # Test files
└── uploads/          # Uploaded statement files
```

## Building

To build the project:

```bash
mvn clean package
```

This will create an executable JAR file in the `target` directory.

## Testing

Run the tests using:

```bash
mvn test
```

## File Upload Directory

The service uses an `uploads` directory to store uploaded statement files. Each file is given a unique UUID as its name to prevent conflicts.

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

## License

[Add your license information here]

## Support

[Add support contact information here]