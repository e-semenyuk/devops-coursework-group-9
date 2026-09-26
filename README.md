# Population Reporting System

[![Master CI](https://img.shields.io/github/actions/workflow/status/e-semenyuk/devops-coursework-group-9/ci.yml?branch=master&label=master)](https://github.com/e-semenyuk/devops-coursework-group-9/actions/workflows/ci.yml?query=branch%3Amaster)
[![Develop CI](https://img.shields.io/github/actions/workflow/status/e-semenyuk/devops-coursework-group-9/ci.yml?branch=develop&label=develop)](https://github.com/e-semenyuk/devops-coursework-group-9/actions/workflows/ci.yml?query=branch%3Adevelop)
![GitHub Release](https://img.shields.io/github/v/release/e-semenyuk/devops-coursework-group-9)
![GitHub License](https://img.shields.io/github/license/e-semenyuk/devops-coursework-group-9)

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

## Project reports 

Please note that instead of providing a screenshot of the results, we have included a link to a file containing the logs.

> 1 requirement of 32 has been implemented, which is 3.125%.

| ID    | Name | Met  | Link |
|-------|------|------|------------|
| 1     | All the countries in the world organised by largest population to smallest. | Yes | [View Results](/docs/reports/pb-01.md#Logs) |


## Requirements

- JDK 17
- Maven 3.9+
- Docker Desktop with Docker Compose

## Build and test

```bash
mvn clean verify
java -jar target/population-reporting-system.jar
java -jar target/population-reporting-system.jar --countries-by-population
```

The `--countries-by-population` report lists every country with its code, name, continent, region, population, and capital, ordered from largest to smallest population.

## Docker

To build the Java image and run a report against MySQL, use the report command as the app's argument. For example:

```bash
docker compose run --build --rm app --countries-by-population
```

Compose starts MySQL if it is not already running and waits for it to become healthy. The report runs in the foreground, prints its results in the terminal, and its temporary app container is removed when it finishes. Run the command again after changing Java code to rebuild and test the updated report. Replace `--countries-by-population` with another supported report command as features are added.

To check only that the app can connect to MySQL, run:

```bash
docker compose up --build
```

The app's default command is `--healthcheck`: it prints `Database connection successful` and exits. MySQL keeps running, so the foreground Compose command remains attached; press `Ctrl+C` to stop it. You can also run `docker compose down` to stop the services. Both methods preserve the database volume and imported data.

The application reads the following environment variables:

| Variable | Default | Description |
| --- | --- | --- |
| `DB_HOST` | `localhost` | MySQL host |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `world` | Database name |
| `DB_USER` | `root` | Database user |
| `DB_PASSWORD` | `example` | Database password |

Place the coursework-provided SQL import file in `database/` before starting Compose. SQL data files are intentionally not committed until their distribution terms and required version are confirmed.

MySQL imports SQL files from `database/` only when initializing a new database volume. You do not need to remove the volume for normal development or after changing Java code. To deliberately recreate the database and re-import the SQL file, run `docker compose down -v` and then start Compose again; this permanently deletes the data stored in the volume.

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
