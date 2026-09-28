# NeetCode 150 Complete API Import Plan

## Architecture

A canonical checked-in solution manifest joins `problems.json`, independently
implemented Kotlin source, and two authored executable examples per item. The
Kotlin checker derives coverage from that manifest. A standard-library Python
importer loads the manifest and local env file, authenticates in memory, reconciles tags and
problems through REST, reconciles one Kotlin solution per problem, and performs
a paginated detail read-back.

## Files and responsibilities

- `docs/algorithm/neetcode150/`: index, solution source, Kotlin checker,
  cases, and validation record. Existing lessons are optional reference
  material, not an import prerequisite.
- `tools/neetcode150_import.py`: non-secret idempotent API import and
  read-back verifier.
- `tools/test_neetcode150_import.py`: local contract tests using a fake HTTP
  transport; never reads credentials.
- `specs/neetcode150-complete-api-import/`: the durable SDD contract.

## Verification strategy

Run only the task commands: the NeetCode Kotlin/material checker, importer
unit test, importer dry validation, authenticated production import/read-back,
and `git diff --check`. Production output is redacted and persisted only as a
non-secret summary.

## Dependencies and rollout

The existing API is the only external dependency. The importer can be rerun;
its reconciliation precedes every write and a failed authentication retry
stops immediately with a safe diagnostic.
