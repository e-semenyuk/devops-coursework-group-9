# Bug reporting and tracking

Use the **Bug report** form in the GitHub issue chooser for defects in the
application, documentation, build, Docker environment, or CI workflow. Do not
include credentials, connection strings, or personally identifiable data.

## Triage workflow

1. A new report receives `bug` and `status: triage` automatically.
2. During triage, confirm the report is reproducible, choose its severity and
   link duplicates where relevant.
3. When someone starts a fix, replace `status: triage` with
   `status: in progress` and link the implementation PR.
4. After the fix is reviewed, merged, and verified, replace it with
   `status: resolved`, then close the issue.

Use one lifecycle status at a time. `status: backlog` and `status: done` are
reserved for planned project tasks and user stories rather than active bugs.

## Evidence expected

A useful report has repeatable steps, expected and actual behaviour, the
environment, severity, and sanitised evidence such as console output,
screenshots, or a failing test. The issue form captures these consistently.
