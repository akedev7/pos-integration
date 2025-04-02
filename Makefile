# Makefile for PostgreSQL Docker management

# Environment variables (can be overridden)
POSTGRES_USER ?= postgres
POSTGRES_PASSWORD ?= postgres
POSTGRES_DB ?= appdb

.PHONY: up down psql logs clean

up:
	@echo "Starting PostgreSQL container..."
	docker-compose up -d

down:
	@echo "Stopping PostgreSQL container..."
	docker-compose down

psql:
	@echo "Connecting to PostgreSQL with psql..."
	docker-compose exec postgres psql -U $(POSTGRES_USER) -d $(POSTGRES_DB)

logs:
	@echo "Showing container logs..."
	docker-compose logs -f postgres

clean:
	@echo "Removing PostgreSQL container and volumes..."
	docker-compose down -v
	rm -f .env

env:
	@echo "Creating .env file..."
	@echo "POSTGRES_USER=$(POSTGRES_USER)" > .env
	@echo "POSTGRES_PASSWORD=$(POSTGRES_PASSWORD)" >> .env
	@echo "POSTGRES_DB=$(POSTGRES_DB)" >> .env

# Help command
help:
	@echo "Available commands:"
	@echo "  up       - Start the PostgreSQL container"
	@echo "  down     - Stop the PostgreSQL container"
	@echo "  psql     - Connect to PostgreSQL with psql"
	@echo "  logs     - View container logs"
	@echo "  clean    - Remove container and volumes"
	@echo "  env      - Create .env file with current settings"
	@echo "  help     - Show this help message"

migrate:
	./mvnw flyway:migrate