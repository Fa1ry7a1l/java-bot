# Local configuration

[Back to README](../README.md)

The default setup runs both Java applications on the host, with PostgreSQL and Kafka in Docker.

## Ports and persistence

| Service | Host port | Notes |
| --- | --- | --- |
| PostgreSQL | `5432` | Database `scrapper`; local user/password `postgres` |
| Kafka | `9092`, `9093` | Default bootstrap addresses used by the applications |
| ZooKeeper | `2181` | Started as a Kafka dependency |
| Scrapper | `8080` | Subscription API |
| Bot | `8090` | Notification API; Telegram updates use long polling |
| Management | `8081`, `8091` | Health, info and metrics |
| Prometheus / Grafana | `9090` / `3000` | Optional services |

Database and broker data live in named Compose volumes. `docker compose down` stops the infrastructure without deleting those volumes.

Liquibase runs as a separate Compose service. Automatic Liquibase execution inside Scrapper is disabled.

## Application settings

| Application | Setting | Default / purpose |
| --- | --- | --- |
| Bot | `BOT_TOKEN` | Telegram token; replace the placeholder before starting |
| Bot | `app.scrapper` | `http://localhost:8080` |
| Scrapper | `spring.datasource.url` | `jdbc:postgresql://localhost:5432/scrapper` |
| Scrapper | `spring.datasource.username`, `spring.datasource.password` | Local database credentials |
| Scrapper | `app.database-access-type` | `jdbc`; `jpa` selects the alternative storage implementation |
| Scrapper | `app.useQueue` | `true` publishes updates through Kafka; `false` uses HTTP |
| Scrapper | `app.clients.bot.url` | `http://localhost:8090` |
| Scrapper | `app.scheduler.interval` | `PT10s`, delay between polling runs |
| Both | `app.kafka-info.servers` | Host-local Kafka brokers on `9092` and `9093` |
| Both | `app.kafka-info.topic.name` | `updates` |
| Both | `app.retry.type` | `exponential`; constant and linear policies are also implemented |

Override Spring properties with application arguments, for example:

```sh
java -jar scrapper/target/scrapper.jar \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/scrapper \
  --spring.datasource.username=postgres \
  --spring.datasource.password=postgres \
  --app.database-access-type=jdbc
```

The checked-in datasource placeholders also accept `bd_url`, `db_login` and `db_password`; the first name is intentionally documented as spelled in the source.

Selecting HTTP updates changes the Scrapper sender only. Kafka configuration and the Bot listener are still created, so this switch alone is not a Kafka-free startup mode.

The Scrapper configuration includes `app.scheduler.enable`, but the current scheduler does not check that flag.

## HTTP subscription example

After Scrapper has started, register a chat and track a supported link:

```sh
curl -i -X POST http://localhost:8080/tg-chat/123456

curl -i -H 'Tg-Chat-Id: 123456' \
  -H 'Content-Type: application/json' \
  -d '{"link":"https://github.com/Fa1ry7a1l/url-shortener"}' \
  http://localhost:8080/links

curl -i -H 'Tg-Chat-Id: 123456' http://localhost:8080/links
```

The chat ID above is an example. Use your real Telegram chat ID for notification delivery. Scrapper defaults to five requests per minute per client IP and responds with `429` after the limit.

The HTTP update receiver is `POST http://localhost:8090/updates`. Contracts are defined in [sharedDTOs](../sharedDTOs/src/main/java/edu/java/dtos).

## Build and CI notes

The module JARs are `bot/target/bot.jar` and `scrapper/target/scrapper.jar`. Each module's Dockerfile packages an already-built JAR; it does not run Maven.

The existing CI workflows filter on module paths. Changes to root files and `sharedDTOs` alone do not trigger both module pipelines. They also include image publishing, so their presence should not be treated as a build-only check.

This is a course implementation. Review notification failure handling and persistence behavior before using it outside a local demonstration.
