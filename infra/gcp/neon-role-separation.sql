-- Neon migration/runtime role separation: ownership, confinement, and grants.
--
-- Run as the Neon project owner (neondb_owner) against the neondb database.
-- Part A creates/reconciles the two dedicated roles and their grants. Part B
-- installs the default privileges and must run while connected as the migration
-- role (or as a role that administers it), because PostgreSQL requires
-- membership in the role the defaults are FOR.
--
-- This file never contains a credential value. After it succeeds, reset each
-- role login secret in the Neon console (Roles -> role -> Reset) or with a
-- private ALTER ROLE statement typed by the operator, then store each value only
-- in GCP Secret Manager as the `algorithm-learning-db-password` secret (runtime)
-- and the `algorithm-learning-db-migration-password` secret (migration). Never
-- commit, paste, or send a value anywhere.
--
-- See infra/gcp/README.md, "Split the migration and runtime roles".

-- =====================================================================
-- Part A - run as the Neon owner (neondb_owner)
-- =====================================================================

BEGIN;

-- These roles must be created at top level with SQL: never through the Neon
-- Console/CLI/API (those roles keep an un-removable neon_superuser membership),
-- and never inside a DO block (a role created that way did not receive the
-- automatic ADMIN grant that lets the creating role administer it). Setting
-- createrole_self_grant also lets the creating role SET ROLE to them.
SET createrole_self_grant = 'set, inherit';

-- 1. Migration role: DDL for Flyway. Run once; skip these statements if the role
--    already exists. It is not a superuser and holds no
--    CREATEDB/CREATEROLE/REPLICATION/BYPASSRLS.
CREATE ROLE algorithm_learning_migrate LOGIN
  NOSUPERUSER
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  NOBYPASSRLS;

-- 2. Shared group role owns the application schema and its objects. Run once;
--    skip this statement if the role already exists. Neon roles created through
--    the Console/CLI/API are not Postgres superusers and neondb_owner has no
--    admin option on them, so ownership is inherited through this group instead
--    of a direct transfer.
CREATE ROLE table_owners NOLOGIN;

GRANT USAGE, CREATE ON SCHEMA public TO table_owners;
GRANT table_owners TO neondb_owner;
GRANT table_owners TO algorithm_learning_migrate;

-- 3. Runtime role: DML only. Run once; skip this statement if the role already
--    exists. It must be created with SQL at top level (see the note above);
--    delete any Console-created role of this name first, then set its login
--    secret separately with ALTER ROLE (section 10 of the spec).
CREATE ROLE algorithm_learning_app LOGIN
  NOSUPERUSER
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  NOBYPASSRLS;

-- 4. It must NOT be a member of table_owners.
REVOKE table_owners FROM algorithm_learning_app;

-- 5. The application schema is owned by the group; every existing object created
--    by prior runs is transferred to it so Flyway DDL works through inherited
--    ownership.
ALTER SCHEMA public OWNER TO table_owners;
REASSIGN OWNED BY neondb_owner TO table_owners;

-- 6. Runtime confinement on the schema: usage without create. The direct grants
--    keep the runtime role working after its group membership is revoked.
REVOKE CREATE ON SCHEMA public FROM algorithm_learning_app;
GRANT USAGE ON SCHEMA public TO algorithm_learning_app;

-- 7. Runtime DML on the current application objects. flyway_schema_history is
--    not part of the serving surface and is revoked.
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO algorithm_learning_app;
REVOKE ALL ON flyway_schema_history FROM algorithm_learning_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO algorithm_learning_app;

COMMIT;

-- =====================================================================
-- Part B - run while connected as algorithm_learning_migrate
-- =====================================================================
-- Future Flyway objects are created by the migration role, so its defaults
-- govern them. Connect as algorithm_learning_migrate (its own connection
-- string). On Neon, neondb_owner already holds an ADMIN membership on that role
-- without SET/INHERIT, so it can act as the role without re-granting ADMIN:
--   GRANT algorithm_learning_migrate TO neondb_owner WITH SET TRUE, INHERIT TRUE;
--   SET ROLE algorithm_learning_migrate;
-- then run the statements below and RESET ROLE. Re-granting ADMIN fails with
-- SQLSTATE 0LP01, and ALTER DEFAULT PRIVILEGES as the un-switched owner fails
-- with SQLSTATE 42501.

ALTER DEFAULT PRIVILEGES FOR ROLE algorithm_learning_migrate IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO algorithm_learning_app;
ALTER DEFAULT PRIVILEGES FOR ROLE algorithm_learning_migrate IN SCHEMA public
  GRANT USAGE, SELECT ON SEQUENCES TO algorithm_learning_app;

-- Fallback: any object that remains owned by the group also grants runtime DML.
ALTER DEFAULT PRIVILEGES FOR ROLE table_owners IN SCHEMA public
  GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO algorithm_learning_app;
ALTER DEFAULT PRIVILEGES FOR ROLE table_owners IN SCHEMA public
  GRANT USAGE, SELECT ON SEQUENCES TO algorithm_learning_app;

-- =====================================================================
-- Verification
-- =====================================================================
-- Confinement: every boolean column must be false for both roles.
--   SELECT rolname, rolsuper, rolcreatedb, rolcreaterole, rolreplication, rolbypassrls
--     FROM pg_roles
--    WHERE rolname IN ('algorithm_learning_app','algorithm_learning_migrate');
-- Ownership: every application object is owned by table_owners.
--   SELECT c.relname, pg_get_userbyid(c.relowner) AS owner
--     FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
--    WHERE n.nspname = 'public' AND c.relkind IN ('r','S','p')
--    ORDER BY c.relname;
-- Runtime DDL-denial probe (connect as algorithm_learning_app; must fail):
--   CREATE TABLE ddl_probe (id int);   -- expected: permission denied for schema public
-- Runtime DML probe (connect as algorithm_learning_app; must succeed):
--   SELECT count(*) FROM problems;
