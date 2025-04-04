# Makefile for POS application
.PHONY: local migrate build run test clean

# Variables
APP_NAME := pos
VERSION := 0.0.1-SNAPSHOT

# Startup Postgres
local:
	@echo Starting docker compose
	docker-compose -f docker-compose.yml up -d --build

# Database migration
migrate:
	@echo "Running database migrations..."
	./mvnw flyway:migrate

# Build the application
build:
	@echo "Building the application..."
	./mvnw clean package -DskipTests

# Run the application locally
run: build
	@echo "Starting the application..."
	java -jar target/${APP_NAME}-${VERSION}.jar

# Run tests
test:
	@echo "Running tests..."
	./mvnw test

# Clean build artifacts
clean:
	@echo "Cleaning up..."
	./mvnw clean
	rm -rf target