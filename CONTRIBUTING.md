# Contributing

## Branch model

- `master`: assessed, released state only.
- `develop`: integration branch for completed work.
- `release`: permanent promotion branch between `develop` and `master`.
- `feature/<issue>-<description>`: one scoped change, branched from `develop`.
- `fix/<issue>-<description>`: defect correction, branched from `develop` unless the team agrees otherwise.

Use reviewed Pull Requests for changes to `master` and `develop`. Keep the existing `release` branch.
The team does not create a separate versioned release branch for each delivery. This keeps
the required `master`, `develop`, and `release` branches visible and gives every release
the same, auditable promotion path.

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

`release` is the team's permanent promotion branch. For every release, follow this exact
sequence:

1. Merge completed feature PRs into `develop` after another team member reviews them and CI passes.
2. In a reviewed PR to `develop`, prepare the release metadata: Maven version, README completion count, release notes, and verification evidence.
3. Promote the reviewed `develop` tip with a PR from `develop` to `release`. Require one independent review and green CI; do not push directly to `release`.
4. Run `mvn clean verify`, build the Docker image, and run the documented report checks with the agreed world database. Keep commands and sample results as evidence.
5. Promote the approved `release` tip with a PR from `release` to `master`. Merge with a merge commit only after teammate review and passing CI.
6. Tag that exact `master` merge commit (for example, `v0.2.0`) and publish the GitHub release from the tag. The Maven version and tag must refer to the same release version.
7. Immediately open and merge a PR from `master` back into `develop`. This is required even if no file content changes, because it preserves the `master` release merge commit in `develop` ancestry.

Do not rewrite existing merges, force-push protected branches, move published tags, or delete
`master`, `develop`, or `release` to repair a missed release step. Delete a completed feature
branch only after its changes are included in both `master` and `develop`.

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
