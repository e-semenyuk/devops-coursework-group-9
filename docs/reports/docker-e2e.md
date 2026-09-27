# Docker end-to-end verification

Verified on 2026-09-27 using application source at `c08fde4`, containing issues
#20–#32 except #18/#19, plus the previously completed capital reports.

The existing multi-stage Dockerfile built and tested the application with Java 17
and Maven, then packaged it in the Java 17 runtime image. All 114 automated tests
passed. Docker Desktop 4.84.0 / Engine 29.6.2 / Compose 5.3.1 ran the application
and MySQL 8.4.11 as separate containers on the Compose network.

## Data and isolation

The isolated Compose project was `group9-codex-e2e`, with its own
`group9-codex-e2e_mysql-data` volume. No database port was published to the host.
The fixture was the team's `database/world.sql` from PB-01 commit
`554f835ba9cc32299627a8529ada1650fb6905a5`, mounted read-only in MySQL's
initialisation directory. Import produced 239 countries and 4,079 cities.

This SQL file is still supplied by the PB-01 branch. For a normal local run, put
that agreed coursework file at `database/world.sql` before creating a new Compose
database volume, as described in [the database guide](../../database/README.md).
Existing MySQL volumes do not rerun initialisation scripts.

## Container results

Every invocation below ran with `docker compose run --rm -T --no-deps app`
against the healthy test database. Top N and capital results were checked for
row count, expected first row, and descending populations.

| Requirement | Arguments | Result |
| --- | --- | --- |
| Health check | `--healthcheck` | Database connection successful |
| PB-13 / #20 | `PB-13 Europe 5` | 5 cities; Moscow first |
| PB-14 / #21 | `PB-14 "British Islands" 5` | 5 cities; London first |
| PB-15 / #22 | `PB-15 "United Kingdom" 5` | 5 cities; London first |
| PB-16 / #23 | `PB-16 Scotland 5` | 4 available cities; Glasgow, Edinburgh, Aberdeen, Dundee |
| PB-17 / #24 | `--capitals-world` | 232 capitals; Seoul first |
| PB-18 / #25 | `--capitals-continent Europe` | 46 capitals; Moscow first |
| PB-19 / #26 | `--capitals-region "British Islands"` | London and Dublin |
| PB-20 / #27 | `--top-capitals-world 5` | 5 capitals; Seoul first |
| PB-21 / #28 | `--top-capitals-continent Europe 5` | 5 capitals; Moscow first |
| PB-22 / #29 | `--top-capitals-region "British Islands" 5` | 2 available capitals |
| PB-23 / #30 | `PB-23` | 7 continent groups |
| PB-24 / #31 | `PB-24` | 25 region groups |
| PB-25 / #32 | `PB-25` | 239 country rows |

All three distribution reports reconcile to independent database queries:
total population **6,078,749,450**, recorded city population **1,429,559,884**.
Every row satisfies total = city + non-city population. Zero-total rows display
N/A percentages. The country report names the three inconsistent source records
(Cocos Islands, Gibraltar and Singapore); see [PB-25](pb-25.md).

## Error and empty-result checks

- Unknown continent: one `No cities found.` message, no report table, exit 0.
- SQL-like country name: treated as a literal, returning no cities.
- Zero N and overflowing N: exit 2.
- Unexpected distribution argument: exit 2.
- Incorrect database password supplied only to a disposable application container:
  exit 1, clear error message, no password or JDBC exception disclosure.

The final test run printed `ALL DOCKER END-TO-END CHECKS PASSED`.
The normal Compose definitions and Dockerfile required no changes.

## Running a report yourself

Once the agreed SQL fixture is in `database/` and the database is initialised:

```bash
docker compose up -d --wait db
docker compose run --build --rm app PB-16 Scotland 5
docker compose run --rm app PB-25
```

The test run used the same services with a local override for the isolated fixture
mount and removal of host port publishing. Its output logs and helper script are
in the ignored `target/docker-e2e-results/` and `target/docker-e2e.ps1` locally.

