# Population Reporting System

[![CI](https://github.com/e-semenyuk/devops-coursework-group-9/actions/workflows/ci.yml/badge.svg)](https://github.com/e-semenyuk/devops-coursework-group-9/actions/workflows/ci.yml)

Coursework repository for **SET09803 DevOps Global Online**, **Group 9**.

The application will query the provided MySQL `world` database and generate the 32 population reports defined in the coursework specification. Individual labs remain in each student's own repository; this repository contains only the assessed group coursework.

## Current status

The repository contains the agreed project foundation:

- Java 17 and Maven build;
- executable, self-contained JAR;
- multi-stage Docker image;
- MySQL development environment with Docker Compose;
- GitHub Actions verification for Maven and Docker;
- GitFlow-compatible branch and contribution guidance;
- reviewed product backlog and project documentation.

Feature implementation will be assigned through GitHub Issues so every contribution is attributable and reviewable.

## Requirements

- JDK 17
- Maven 3.9+
- Docker Desktop with Docker Compose

## Build and test

```bash
mvn clean verify
java -jar target/population-reporting-system.jar
```

## Docker

Start the database and application:

```bash
docker compose up --build
```

The application reads the following environment variables:

| Variable | Default | Description |
| --- | --- | --- |
| `DB_HOST` | `localhost` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `world` | Database name |
| `DB_USER` | `root` | Database user |
| `DB_PASSWORD` | `example` | Database password |

Place the coursework-provided SQL import file in `database/` before starting Compose. SQL data files are intentionally not committed until their distribution terms and required version are confirmed.

## Working agreement

1. Create work from an assigned GitHub Issue.
2. Branch from `develop` using `feature/<issue-number>-short-description`.
3. Keep commits small, meaningful, and authored by the person who did the work.
4. Open a Pull Request into `develop` and link the issue with `Closes #<number>`.
5. Obtain at least one team review and pass CI before merging.
6. Use `release/*` for release preparation and merge approved releases into `master` and back into `develop`.

See [CONTRIBUTING.md](CONTRIBUTING.md), [Product Backlog](docs/PRODUCT_BACKLOG.md), and [Project Plan](docs/PROJECT_PLAN.md).

## Team

| Member | GitHub |
| --- | --- |
| Eugene Semenyuk | [@e-semenyuk](https://github.com/e-semenyuk) |
| Alexandra Gombitova | [@AlexandraGombitova](https://github.com/AlexandraGombitova) |
| Bohdan Katsevych | [@bkatsevych003](https://github.com/bkatsevych003)|
| Jerry Ebanks | [@40799682](https://github.com/40799682) |
| Trust Okunfeyiwa | [@Trust1Ok](https://github.com/Trust1Ok) |
| Mashri Alhumidi | [@mshary-web](https://github.com/mshary-web) |
Additional members and formal Scrum roles will be recorded when confirmed by the team.
