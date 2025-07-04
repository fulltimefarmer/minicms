.PHONY: infra-start infra-cleanup infra-restart

infra-start:
	docker compose -f docker-compose.yml up -d
	./scripts/bin/waitForDB.sh
	./mvnw -pl . flyway:migration

infra-cleanup:
	docker compose -f docker-compose.yml down
	rm -rf ./docker/postgres/data

infra-restart: infra-cleanup infra-start
