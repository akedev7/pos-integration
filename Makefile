# Makefile for POS application
.PHONY: local migrate build verify clean all

all: postgres migrate build app
	echo "Everything is ready"

postgres:
	@echo Starting Postgres
	docker-compose -f docker-compose-postgres.yml up -d --build

migrate:
	@echo "Running database migrations..."
	./mvnw flyway:migrate

build:
	@echo "Building POS integration package..."
	./mvnw clean package -DskipTests

app:
	@echo Starting POS app
	docker-compose -f docker-compose.yml up -d --build

verify:
	./mvnw verify

test:
	@echo "Running tests..."
	./mvnw test

# Clean build artifacts
clean:
	@echo "Cleaning up..."
	./mvnw clean
	rm -rf target

stop:
	@echo "Stopping containers..."
	docker-compose -f docker-compose.yml down
	docker-compose -f docker-compose-postgres.yml down

rebuild: clean build app
