# Release Notes

## v0.2.0 — Population Reports

18 of 32 report requirements implemented (56.25%).

### Reports

- PB-13 to PB-16: Top N cities by continent, region, country and district.
- PB-17 to PB-19: capital cities by population in the world, a continent and a region.
- PB-20 to PB-22: Top N capital cities in the world, a continent and a region.
- PB-23 to PB-25: population distribution by continent, region and country.
- PB-26: world population.
- PB-27: selected continent population.
- PB-30: selected district population.
- PB-31: selected city population.
- PB-32: five-language population report.

### Process and quality

- MySQL integration tests and coverage reporting (#77).
- Versioned `release-<version>` workflow, with CI for release branches (#66).
- Updated use case diagram (#45).

### Release verification (2026-10-07)

- `docker build` ran `mvn clean verify` with Java 17: 141 tests, 0 failures.
- Docker Compose with MySQL 8.4.11 and the team's `world.sql` fixture from PB-01
  commit `554f835` (239 countries, 4,079 cities):

```text
PB-26                      World | 6,078,749,450
PB-27 Europe               Europe | 730,074,600
PB-30 Scotland             Scotland | 1,429,620
PB-31 Edinburgh            Edinburgh | 450,180
PB-32                      Chinese | 1,191,843,539 | 19.61% (first of 5 rows)
--top-capitals-world 3     Seoul, Jakarta, Ciudad de México
PB-13 Asia 3               Mumbai (Bombay), Seoul, Shanghai
PB-23                      Asia | 3,705,025,700 | 697,604,103 | 18.83% | ...
```

## v0.1.0 — Project Foundation

Java 17 Maven application, executable JAR, multi-stage Docker image, Docker Compose environment, GitHub Actions CI, GitFlow branches, team documentation and the product backlog.
