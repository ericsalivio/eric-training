# Spring AI RAG Tutorial with Ollama and PGVector

This project demonstrates the implementation of Retrieval Augmented Generation (RAG) using Spring AI, Ollama, and PGVector Database. The application serves as a personal assistant that can answer questions about Spring Boot by referencing the Spring Boot Reference Documentation PDF.

## Features

- Uses Spring AI for RAG implementation
- Integrates with Ollama for LLM capabilities
- Stores and retrieves vector embeddings using PGVector
- Automatically processes and ingests Spring Boot documentation
- Provides REST API for question-answering

## Architecture

### RAG Architecture
![RAG Architecture](screenshots/rag_architecture.png)

### Document Ingestion Pipeline
![Document Ingestion Pipeline](screenshots/document_ingestion_pipeline.png)

## Prerequisites

- Java 21
- Docker and Docker Compose
- Ollama installed locally
- Maven

## Setup Instructions

1. **Install Ollama**
   - Follow the installation instructions at [Ollama's official website](https://ollama.ai)
   - Ensure Ollama is running on `http://localhost:11434`

2. **Pull the Mistral Model**
   ```bash
   ollama pull mistral
   ```
   Note: If you skip this step, the application will automatically pull the model when it first starts, which might take a few minutes.

3. **Start PGVector Database**
   ```bash
   docker-compose up -d
   ```
   This will start a PostgreSQL database with PGVector extension on port 5432.

4. **Build the Application**
   ```bash
   ./mvnw clean install
   ```

## Running the Application

1. **Start the Spring Boot Application**
   ```bash
   ./mvnw spring-boot:run
   ```

2. The application will automatically:
   - Initialize the vector store schema
   - Load and process the Spring Boot reference PDF
   - Start the REST API server

## Usage

Send questions about Spring Boot to the API endpoint:

```bash
curl -X POST http://localhost:8080/api/chat \
     -H "Content-Type: text/plain" \
     -d "What is Spring Boot?"
```

## Technical Details

- **Vector Database**: PGVector (PostgreSQL with vector extension)
  - Database: vectordb
  - Username: testuser
  - Password: testpwd
  - Port: 5432

- **LLM Configuration**:
  - Model: Mistral
  - Base URL: http://localhost:11434
  - Initialization timeout: 5 minutes
  - Auto-pulls model if not available locally
  - Pull strategy: when_missing

- **Document Processing**:
  - Uses Apache Tika for PDF reading
  - Implements text splitting for optimal chunk size
  - Automatically ingests documentation on startup

## Project Structure

- `ChatController`: Handles REST API requests
- `DocumentIngestionService`: Processes and stores documentation
- `application.properties`: Contains configuration for Ollama and PGVector
- `compose.yml`: Docker composition for PGVector database

## Troubleshooting

1. Ensure Ollama is running and accessible at http://localhost:11434
2. Verify that the PostgreSQL container is running: `docker ps`
3. Check application logs for any initialization errors
4. Ensure the Mistral model is properly pulled in Ollama

## Dependencies

- Spring Boot 3.4.3
- Spring AI (version 1.0.0-M6)
- PGVector
- Apache Tika
- Spring Boot Docker Compose Support
