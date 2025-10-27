# Docker Jenkins Integration Demo

This project demonstrates Jenkins CI/CD pipeline with Docker integration.

## Features
- Automated Docker image building
- Push to Docker Hub
- Health checks
- Multi-stage deployment

## Running Locally
```bash
docker build -t myapp .
docker run -p 3000:3000 myapp
```

## Endpoints
- `GET /` - Main endpoint
- `GET /health` - Health check