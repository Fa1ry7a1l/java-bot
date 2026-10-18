# Link Tracker

A Telegram bot that tracks updates to GitHub repositories and Stack Overflow questions. Written in Java 21 with Spring Boot, PostgreSQL and Kafka.

Users subscribe to links in Telegram. A separate polling service checks external APIs and sends change notifications back to their chats.

## Components

| Module | Responsibility |
| --- | --- |
| `bot` | Telegram commands, Scrapper HTTP client and notification delivery |
| `scrapper` | Link subscriptions, PostgreSQL persistence, scheduled polling and update publishing |
| `sharedDTOs` | Request and response contracts shared by both applications |
| `migrations` | Liquibase changelog and database schema |

The Bot calls Scrapper over HTTP to manage subscriptions. Scrapper checks supported links on a schedule and publishes updates through Kafka by default. Its update sender can also use the Bot's HTTP endpoint.

The project includes JDBC and JPA storage implementations, configurable HTTP retry policies, request rate limiting in Scrapper, tests using WireMock and Testcontainers, and Prometheus metrics.

## Local setup

Requirements: JDK 21, Docker with Compose, and a Telegram bot token. The Maven wrapper is included; on Windows use `mvnw.cmd` instead of `./mvnw`.

Build both applications from the repository root:

```sh
./mvnw -DskipTests package
```

Start PostgreSQL:

```sh
docker compose up -d postgresql
docker compose exec postgresql pg_isready -U postgres -d scrapper
```

Wait for the readiness command to succeed, then apply migrations and start the two Kafka brokers used by the default application settings:

```sh
docker compose run --rm liquibase-migrations
docker compose up -d kafka1 kafka2
```

Compose starts ZooKeeper as a dependency. Wait for the brokers to finish starting before running the applications.

Start Scrapper in one terminal:

```sh
java -jar scrapper/target/scrapper.jar
```

Set `BOT_TOKEN` in another terminal and start the Bot. In a POSIX shell:

```sh
export BOT_TOKEN='replace-with-your-bot-token'
java -jar bot/target/bot.jar
```

In PowerShell, set it with `$env:BOT_TOKEN = 'replace-with-your-bot-token'`.

Compose contains infrastructure services only; it does not launch the Java applications. See [local configuration](docs/local-setup.md) for settings, ports and HTTP examples.

## Try the bot

| Command | Action |
| --- | --- |
| `/start` | Register the current Telegram chat |
| `/help` | Show available commands |
| `/track https://github.com/owner/repository` | Subscribe to a GitHub repository |
| `/track https://stackoverflow.com/questions/123/title` | Subscribe to a Stack Overflow question |
| `/list` | List subscriptions with their numbers |
| `/untrack 1` | Remove the subscription at position 1 |

Scheduled checks run every 10 seconds by default. The integration checks external activity timestamps; accepting a URL does not mean its website is supported for polling.

## Checks

With JDK 21 and a running Docker daemon:

```sh
./mvnw verify
./mvnw checkstyle:check
```

Some classes ending in `Test` start Testcontainers, so even `./mvnw test` can require Docker. A build with `-DskipTests` is a packaging check, not a successful test run.

## Monitoring

| Application | API port | Health | Prometheus metrics |
| --- | --- | --- | --- |
| Scrapper | `8080` | `http://localhost:8081/health` | `http://localhost:8081/metrics` |
| Bot | `8090` | `http://localhost:8091/health` | `http://localhost:8091/metrics` |

The Compose file also includes optional Prometheus and Grafana services. Its Prometheus scrape targets use the Linux Docker bridge address `172.17.0.1`; adjust them for your host before enabling the monitoring stack.

## Project background

A backend course project based on the [Java backend template](https://github.com/sanyarnd/java-course-2023-backend-template). The repository retains its original module-specific CI workflows.
