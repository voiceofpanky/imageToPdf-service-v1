# ImageToPdf Service v1

A Spring Boot REST API service that converts image files (JPG, PNG, etc.) to PDF format. Built with Java 17, Maven, and Apache PDFBox.

## 📋 Table of Contents

- [Features](#features)
- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Building](#building)
- [Running](#running)
- [API Documentation](#api-documentation)
- [Docker Deployment](#docker-deployment)
- [Configuration](#configuration)
- [Dependencies](#dependencies)
- [Troubleshooting](#troubleshooting)

## ✨ Features

- Convert single image files to PDF format
- Support for JPG, PNG, and other image formats
- REST API with multipart file upload
- Docker containerized deployment
- Configurable file size limits
- Optimized for container environments (JVM tuning)

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.9.6** or higher
- **Docker** (for containerized deployment) - Optional
- **Git** (for version control)

## 📂 Project Structure

```
imageToPdf-service-v1/
├── src/
│   ├── main/
│   │   ├── java/org/example/
│   │   │   ├── Main.java                    # Application entry point
│   │   │   ├── ImageToPdfController.java    # REST controller
│   │   │   └── ImageToPdfService.java       # Business logic
│   │   └── resources/
│   │       └── application.properties       # Configuration file
│   └── test/
│       └── java/                            # Unit tests
├── target/                                  # Compiled artifacts (auto-generated)
├── pom.xml                                  # Maven configuration
├── Dockerfile                               # Docker build configuration
└── README.md                                # This file
```

## 🚀 Installation

### 1. Clone or Download the Project

```bash
# Clone from repository (if applicable)
git clone <repository-url>
cd imageToPdf-service-v1
```

### 2. Verify Prerequisites

```bash
# Check Java version
java -version

# Check Maven version
mvn -version
```

## 🔨 Building

### Build with Maven (local)

```bash
# Clean previous builds and compile
mvn clean install

# Or just compile without tests
mvn clean package -DskipTests
```

**What this does:**
- Downloads all dependencies
- Compiles Java source code
- Packages the application into a JAR file
- Output: `target/imageToPdf-service-v1-1.0-SNAPSHOT.jar`

### Build Docker Image

```bash
# Build the Docker image
docker build -t imagetopdf-service:v1 .
```

**What this does:**
- Creates a multi-stage Docker build:
  - **Stage 1**: Uses Maven to build the JAR file
  - **Stage 2**: Creates a lightweight runtime image with Java 17 JRE
  - Final image size is optimized (~400MB)

**Verify the image was built:**

```bash
docker images | findstr imagetopdf-service
```

## ▶️ Running

### Option 1: Run Locally (Standalone JAR)

```bash
# Navigate to project directory
cd C:\Users\panka\IdeaProjects\imageToPdf-service-v1

# Run the JAR file directly
java -jar target/imageToPdf-service-v1-1.0-SNAPSHOT.jar
```

Expected output:
```
Started Main in X seconds (JVM running for Y.XXX s)
```

### Option 2: Run with Maven

```bash
# Run Spring Boot application
mvn spring-boot:run
```

### Option 3: Run Docker Container

```bash
# Run the Docker container
docker run -p 8080:8080 imagetopdf-service:v1
```

**Options:**
- `-p 8080:8080`: Maps port 8080 from container to host
- `-d`: Run in background
- `-e JAVA_OPTS="..."`: Pass custom JVM options

**Example - Run in background:**
```bash
docker run -d -p 8080:8080 --name imagetopdf imagetopdf-service:v1
```

**Stop the container:**
```bash
docker stop imagetopdf
docker rm imagetopdf
```

## 📡 API Documentation

### Base URL

```
http://localhost:8080
```

### Endpoint: Convert Image to PDF

**Method:** `POST`

**URL:** `/api/pdf/convert`

**Content-Type:** `multipart/form-data`

**Request Parameters:**
| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| file | File | Yes | Image file (JPG, PNG, etc.) |

**Response:**
| Status | Description | Content-Type |
|--------|-------------|--------------|
| 200 OK | PDF file | `application/pdf` |
| 400 Bad Request | Invalid file | `application/json` |
| 413 Payload Too Large | File exceeds max size | `application/json` |
| 500 Internal Server Error | Conversion failed | `application/json` |

### Example Request (cURL)

```bash
# Convert image to PDF
curl -X POST \
  -F "file=@/path/to/image.jpg" \
  http://localhost:8080/api/pdf/convert \
  -o converted.pdf
```

**PowerShell Example:**
```powershell
# Using Invoke-RestMethod
$filePath = "C:\path\to\image.jpg"
$response = Invoke-RestMethod -Uri "http://localhost:8080/api/pdf/convert" `
  -Method Post `
  -Form @{ file = Get-Item $filePath } `
  -OutFile "C:\path\to\output.pdf"
```

### Example Request (JavaScript/Fetch API)

```javascript
const imageFile = document.querySelector('input[type="file"]').files[0];
const formData = new FormData();
formData.append('file', imageFile);

fetch('http://localhost:8080/api/pdf/convert', {
  method: 'POST',
  body: formData
})
.then(response => response.blob())
.then(blob => {
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement('a');
  a.href = url;
  a.download = 'converted.pdf';
  a.click();
})
.catch(error => console.error('Error:', error));
```

## 🐳 Docker Deployment

### Docker Build Details

The `Dockerfile` uses a **multi-stage build** approach:

**Stage 1 (Build Stage):**
- Base image: `maven:3.9.6-eclipse-temurin-17`
- Downloads Maven dependencies
- Compiles source code
- Creates JAR package

**Stage 2 (Runtime Stage):**
- Base image: `eclipse-temurin:17-jre` (lightweight JRE-only image)
- Copies compiled JAR from build stage
- Configures JVM for container environments
- Sets up entrypoint

### JVM Configuration

```dockerfile
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75"
```

- `UseContainerSupport`: Enables container-aware memory limits
- `MaxRAMPercentage=75`: Allocates 75% of container memory to heap

### Docker Compose (Optional)

Create a `docker-compose.yml` file for easier deployment:

```yaml
version: '3.8'

services:
  imagetopdf:
    build: .
    ports:
      - "8080:8080"
    environment:
      - JAVA_OPTS=-XX:+UseContainerSupport -XX:MaxRAMPercentage=75
    restart: unless-stopped
```

**Run with Docker Compose:**
```bash
docker-compose up -d
```

### Pushing to Container Registry

```bash
# Tag the image
docker tag imagetopdf-service:v1 your-registry/imagetopdf-service:v1

# Push to registry (Docker Hub, AWS ECR, etc.)
docker push your-registry/imagetopdf-service:v1
```

## ⚙️ Configuration

### Application Properties

File: `src/main/resources/application.properties`

```properties
# Server Configuration
server.port=8080                                    # Application port

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB       # Maximum file size
spring.servlet.multipart.max-request-size=10MB    # Maximum request size
```

### Customizing Configuration

**For local development:**
```bash
java -jar target/imageToPdf-service-v1-1.0-SNAPSHOT.jar --server.port=9090
```

**For Docker container:**
```bash
docker run -p 9090:8080 \
  -e SERVER_PORT=8080 \
  imagetopdf-service:v1
```

## 📚 Dependencies

### Maven Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| spring-boot-starter-web | 3.2.6 | Spring Boot web framework |
| spring-boot-starter-parent | 3.2.6 | Spring Boot parent POM |
| pdfbox | 2.0.31 | PDF document creation |

**Build Information:**
- Java Compiler: Java 17
- Maven: 3.9.6
- Spring Boot: 3.2.6

### View All Dependencies

```bash
mvn dependency:tree
```

## 🛠️ Troubleshooting

### Issue: Port 8080 Already in Use

**Solution:**
```bash
# Change the port
java -jar target/imageToPdf-service-v1-1.0-SNAPSHOT.jar --server.port=8081

# Or find and kill process using port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Issue: "File exceeds maximum upload size"

**Solution:** Increase max file size in `application.properties`:
```properties
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

### Issue: Docker Build Fails

**Solution:**
```bash
# Clean and rebuild
docker builder prune
docker build --no-cache -t imagetopdf-service:v1 .
```

### Issue: "No converter for return value of type class [B"

**Solution:** Ensure Jackson dependency is included (comes with spring-boot-starter-web)

### Issue: Application Won't Start

**Solution:** Check logs
```bash
# Local
java -jar target/imageToPdf-service-v1-1.0-SNAPSHOT.jar

# Docker
docker logs <container-id>
```

## 📝 Code Overview

### Main.java
- Application entry point (template class)
- Not used by Spring Boot application

### ImageToPdfService.java
- **Core conversion logic**
- Uses Apache PDFBox library
- Accepts image bytes and returns PDF bytes
- Creates PDF with image dimensions
- Handles I/O operations

### ImageToPdfController.java
- **REST API endpoint**
- Handles HTTP POST requests
- Accepts multipart file upload
- Returns PDF as attachment
- Content-Type: `application/pdf`

## 🔐 Security Considerations

- File upload size is limited to 10MB (configurable)
- Input validation on file content
- No file persistence (in-memory processing)
- Consider adding:
  - File type validation (whitelist allowed formats)
  - Authentication/Authorization
  - Rate limiting
  - CORS configuration

## 📄 License

This project is created for demonstration purposes.

## 🤝 Contributing

To contribute:
1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Push to branch
5. Create Pull Request

## 📞 Support

For issues or questions:
1. Check the [Troubleshooting](#troubleshooting) section
2. Review logs
3. Check Maven/Docker output
4. Consult Spring Boot documentation: https://spring.io/projects/spring-boot

---

**Last Updated:** December 26, 2025  
**Version:** 1.0  
**Status:** Production Ready

