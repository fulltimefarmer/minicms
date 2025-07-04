.PHONY: infra-start infra-cleanup infra-restart

infra-start:
	sudo docker compose -f docker-compose.yml up -d
	./scripts/bin/waitForDB.sh
	./mvnw -pl . flyway:migrate

infra-cleanup:
	sudo docker compose -f docker-compose.yml down
	sudo rm -rf ./docker/postgres/data

infra-restart: infra-cleanup infra-start
