# Contributing

## Branch model

- `master`: assessed, released state only.
- `develop`: integration branch for completed work.
- `release`: retained coursework demonstration branch.
- `release-<version>`: versioned release preparation, branched from `develop`.
- `feature/<issue>-<description>`: one scoped change, branched from `develop`.
- `fix/<issue>-<description>`: defect correction, branched from `develop` unless the team agrees otherwise.

Use reviewed Pull Requests for changes to `master` and `develop`. Keep the existing `release` branch.
Git cannot store both `release` and `release/<version>`, so versioned release branches use a hyphen.

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

## Release workflow

`v0.1.0` marks the project foundation and `v0.2.0` the first report release.

1. Merge completed feature PRs into `develop` after another team member reviews them and CI passes.
2. Create `release-0.2.0` from the updated `develop` branch. Set the Maven project version to `0.2.0` on that branch and update the README completion count and release notes.
3. Run `mvn clean verify`, build the Docker image, and run the documented report checks with the agreed world database. Keep commands and sample results as evidence.
4. Open a PR from `release-0.2.0` into `master`. Merge with a merge commit only after teammate review and passing CI.
5. Tag the approved master merge commit `v0.2.0` and publish the GitHub release from that tag. The branch, Maven version and tag must refer to the same release version.
6. Open a PR from `release-0.2.0` back into `develop` to retain release fixes. Require teammate review and passing CI here too. Advance the development snapshot version in a separate feature PR afterward.
7. Delete feature and versioned release branches only when their commits are included in both `master` and `develop`. Keep `main` (if present), `master`, `develop`, `release`, and unfinished branches.

Do not rewrite existing merges or move published tags to repair a missed release step. Prepare the next version through this workflow.

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
