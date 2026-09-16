# Product Backlog

**Course:** SET09803 DevOps Global Online
**Group:** Group 9
**Project:** Population Reporting System

## Review of the initial draft

Jerry's draft provides strong coverage: it maps all 32 coursework requirements to clearly named user stories, includes basic acceptance criteria, and defines a sensible Definition of Done.

The following refinements are needed before sprint planning:

- add estimates, owners, sprint allocation, and explicit dependencies;
- separate product requirements from enabling technical work;
- add input validation, empty-result behaviour, database-error handling, and exact output-column criteria;
- avoid treating every item as equally urgent;
- reuse query, filtering, limiting, formatting, and test infrastructure across similar reports;
- link every delivered item to a GitHub Issue and Pull Request.

The estimates below are initial relative story points for discussion, not commitments.

## Enabling work (Sprint 0)

| ID | Item | Done when | Points |
| --- | --- | --- | ---: |
| EN-01 | Repository and GitFlow | Shared repository, protected branches, documented PR workflow | 3 |
| EN-02 | Maven executable JAR | `mvn clean verify` produces a runnable self-contained JAR | 3 |
| EN-03 | Docker environment | Application and official world database run through Docker Compose | 5 |
| EN-04 | Continuous integration | GitHub Actions validates tests, JAR, and Docker build | 3 |
| EN-05 | Database access layer | Configurable, tested connection and query execution abstraction | 5 |
| EN-06 | Report model and formatter | Required country/city/capital/population columns render consistently | 5 |
| EN-07 | Scrum/project configuration | Issues, board, roles, DoD, Code of Conduct, and sprint process approved | 3 |

## Product requirements

| ID | User-facing outcome | Priority | Points | Suggested sprint | Depends on |
| --- | --- | --- | ---: | --- | --- |
| PB-01 | Countries in the world by population | High | 3 | 1 | EN-05, EN-06 |
| PB-02 | Countries in a continent by population | High | 3 | 1 | PB-01 |
| PB-03 | Countries in a region by population | High | 3 | 1 | PB-01 |
| PB-04 | Top N populated countries in the world | High | 2 | 1 | PB-01 |
| PB-05 | Top N populated countries in a continent | High | 3 | 1 | PB-02, PB-04 |
| PB-06 | Top N populated countries in a region | High | 3 | 1 | PB-03, PB-04 |
| PB-07 | Cities in the world by population | High | 3 | 2 | EN-05, EN-06 |
| PB-08 | Cities in a continent by population | High | 3 | 2 | PB-07 |
| PB-09 | Cities in a region by population | High | 3 | 2 | PB-07 |
| PB-10 | Cities in a country by population | High | 3 | 2 | PB-07 |
| PB-11 | Cities in a district by population | High | 3 | 2 | PB-07 |
| PB-12 | Top N populated cities in the world | High | 2 | 2 | PB-07 |
| PB-13 | Top N populated cities in a continent | High | 3 | 2 | PB-08, PB-12 |
| PB-14 | Top N populated cities in a region | High | 3 | 2 | PB-09, PB-12 |
| PB-15 | Top N populated cities in a country | High | 3 | 2 | PB-10, PB-12 |
| PB-16 | Top N populated cities in a district | High | 3 | 2 | PB-11, PB-12 |
| PB-17 | Capital cities in the world by population | High | 3 | 3 | EN-05, EN-06 |
| PB-18 | Capital cities in a continent by population | High | 3 | 3 | PB-17 |
| PB-19 | Capital cities in a region by population | High | 3 | 3 | PB-17 |
| PB-20 | Top N populated capital cities in the world | High | 2 | 3 | PB-17 |
| PB-21 | Top N populated capital cities in a continent | High | 3 | 3 | PB-18, PB-20 |
| PB-22 | Top N populated capital cities in a region | High | 3 | 3 | PB-19, PB-20 |
| PB-23 | Population distribution by continent | High | 5 | 3 | EN-05, EN-06 |
| PB-24 | Population distribution by region | High | 5 | 3 | PB-23 |
| PB-25 | Population distribution by country | High | 5 | 3 | PB-23 |
| PB-26 | World population | Medium | 1 | 1 | EN-05 |
| PB-27 | Selected continent population | Medium | 2 | 1 | PB-26 |
| PB-28 | Selected region population | Medium | 2 | 1 | PB-26 |
| PB-29 | Selected country population | Medium | 2 | 1 | PB-26 |
| PB-30 | Selected district population | Medium | 2 | 4 | EN-05 |
| PB-31 | Selected city population | Medium | 2 | 4 | EN-05 |
| PB-32 | Five-language population report | Medium | 5 | 4 | EN-05, PB-26 |

## Shared acceptance criteria

Every report issue must specify its required columns and additionally satisfy these shared criteria:

- results use the official coursework database and correct joins;
- population results are ordered from largest to smallest where required;
- Top N rejects non-positive or non-numeric values and never returns more than N rows;
- filters are parameterised rather than concatenated into SQL;
- empty results produce a clear message rather than an exception;
- database failures are reported without exposing credentials;
- automated tests cover query construction or repository behaviour and report formatting;
- sample output is attached to the Pull Request as evidence.

## Required report columns

- **Country:** Code, Name, Continent, Region, Population, Capital.
- **City:** Name, Country, District, Population.
- **Capital city:** Name, Country, Population.
- **Population distribution:** Area name, total population, city population and percentage, non-city population and percentage.
- **Language:** Language, number of speakers, percentage of world population.

## Definition of Done

- [ ] Acceptance criteria are demonstrably satisfied.
- [ ] The application builds and all automated tests pass.
- [ ] The report produces correct, readable output against the agreed database.
- [ ] Input and empty-result behaviour are handled.
- [ ] A Pull Request links the issue and contains evidence.
- [ ] At least one other team member has reviewed the change.
- [ ] GitHub Actions passes.
- [ ] The change is merged into the correct branch and the issue/board is updated.
