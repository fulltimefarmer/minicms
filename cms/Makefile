.PHONY: infra-start infra-cleanup infra-restart

infra-start:
	docker compose -f docker-compose.yml up -d
	./scripts/bin/waitForMySQL.sh
	./mvnw -pl . flyway:migrate

infra-cleanup:
	docker compose -f docker-compose.yml down
	rm -rf ./docker/postgres/data

infra-restart: infra-cleanup infra-start
