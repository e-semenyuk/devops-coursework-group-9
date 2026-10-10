# Population Reporting System

[![Master CI](https://img.shields.io/github/actions/workflow/status/e-semenyuk/devops-coursework-group-9/ci.yml?branch=master&label=master)](https://github.com/e-semenyuk/devops-coursework-group-9/actions/workflows/ci.yml?query=branch%3Amaster)
[![Develop CI](https://img.shields.io/github/actions/workflow/status/e-semenyuk/devops-coursework-group-9/ci.yml?branch=develop&label=develop)](https://github.com/e-semenyuk/devops-coursework-group-9/actions/workflows/ci.yml?query=branch%3Adevelop)
[![codecov](https://codecov.io/gh/e-semenyuk/devops-coursework-group-9/graph/badge.svg?branch=master)](https://codecov.io/gh/e-semenyuk/devops-coursework-group-9)
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
6. Promote each release through `develop` → `release` → `master` → `develop`; tag the released `master` merge commit and publish the GitHub release.

See [CONTRIBUTING.md](CONTRIBUTING.md), [Product Backlog](docs/PRODUCT_BACKLOG.md), [Project Plan](docs/PROJECT_PLAN.md), and [Bug reporting](docs/BUG_REPORTING.md).

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

## Implemented reports

**14 of 32 requirements have been implemented (43.75%).**

| Requirement | Report | Implemented | Evidence |
| --- | --- | --- | --- |
| PB-01 | Countries in the world by population | Yes | [Results](docs/reports/pb-01.md) |
| PB-13 | Top N cities in a continent | Yes | [Results](docs/reports/pb-13.md) |
| PB-14 | Top N cities in a region | Yes | [Results](docs/reports/pb-14.md) |
| PB-15 | Top N cities in a country | Yes | [Results](docs/reports/pb-15.md) |
| PB-16 | Top N cities in a district | Yes | [Results](docs/reports/pb-16.md) |
| PB-17 | Capital cities in the world | Yes | [Results](docs/reports/pb-17.md) |
| PB-18 | Capital cities in a continent | Yes | [Results](docs/reports/pb-18.md) |
| PB-19 | Capital cities in a region | Yes | [Results](docs/reports/pb-19.md) |
| PB-20 | Top N capital cities in the world | Yes | [Results](docs/reports/pb-20.md) |
| PB-21 | Top N capital cities in a continent | Yes | [Results](docs/reports/pb-21.md) |
| PB-22 | Top N capital cities in a region | Yes | [Results](docs/reports/pb-22.md) |
| PB-23 | Population distribution by continent | Yes | [Results](docs/reports/pb-23.md) |
| PB-24 | Population distribution by region | Yes | [Results](docs/reports/pb-24.md) |
| PB-25 | Population distribution by country | Yes | [Results](docs/reports/pb-25.md) |

Capital-city report commands and verification evidence are documented in
[PB-17](docs/reports/pb-17.md), [PB-18](docs/reports/pb-18.md), [PB-19](docs/reports/pb-19.md), [PB-20](docs/reports/pb-20.md), [PB-21](docs/reports/pb-21.md), [PB-22](docs/reports/pb-22.md).
