# Contributing

## Branch model

- `master`: assessed, released state only.
- `develop`: integration branch for completed work.
- `release`: stable release-preparation branch required by the coursework checklist.
- `feature/<issue>-<description>`: one scoped change, branched from `develop`.
- `fix/<issue>-<description>`: defect correction, branched from `develop` unless the team agrees otherwise.

Direct commits to `master`, `develop`, and `release` should be avoided after branch protection is enabled.

## Standard workflow

```bash
git switch develop
git pull --ff-only
git switch -c feature/12-country-report

# Work and test
mvn clean verify

git add <specific-files>
git commit -m "feat(report): add countries by population query"
git push -u origin feature/12-country-report
```

Open a Pull Request into `develop`, link its issue, request review, and wait for CI. The author must not approve their own Pull Request.

## Commit conventions

Use focused commits with a conventional prefix:

- `feat`: functionality;
- `fix`: defect correction;
- `test`: automated tests;
- `docs`: documentation;
- `ci`: GitHub Actions or build automation;
- `build`: Maven, Docker, or dependency configuration;
- `refactor`: restructuring without a behaviour change.

Commit count is not a goal. Each member should make meaningful, independently attributable contributions across issues, commits, reviews, and documentation.

## Definition of Done

A change is complete when its acceptance criteria are met, relevant tests pass, CI is green, documentation is current, another member has reviewed the Pull Request, and the linked issue is closed by the merge.
