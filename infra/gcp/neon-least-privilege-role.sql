-- Neon least-privilege application role: ownership and grant template.
--
-- Run once as the Neon project owner (neondb_owner) against the neondb
-- database. Replace <APP_ROLE> with the dedicated role name before running.
--
-- This file never contains a credential value. After this template succeeds,
-- set the role login secret in the Neon console (Roles -> <APP_ROLE> -> Reset)
-- or with a private ALTER ROLE statement typed by the operator, then store that
-- value only in GCP Secret Manager as algorithm-learning-db-password. Never
-- commit, paste, or send the value anywhere.
--
-- See infra/gcp/README.md, "Rotate to the least-privilege role".

BEGIN;

-- 1. First rotation only: create a login role confined to the application
--    database and schema. It is not a superuser and holds no
--    CREATEDB/CREATEROLE/REPLICATION/BYPASSRLS.
CREATE ROLE <APP_ROLE> LOGIN
  NOSUPERUSER
  NOCREATEDB
  NOCREATEROLE
  NOREPLICATION
  NOBYPASSRLS;

-- 2. The owner session needs membership in the target role to hand over object
--    ownership; PostgreSQL requires this to change an object's owner.
GRANT <APP_ROLE> TO neondb_owner;

-- 3. The application schema must be owned by the role because Flyway runs DDL
--    (ALTER TABLE requires object ownership) in the migration Job.
ALTER SCHEMA public OWNER TO <APP_ROLE>;

-- 4. Transfer every existing application object created by prior Flyway runs
--    as neondb_owner. Scoped to the current database.
REASSIGN OWNED BY neondb_owner TO <APP_ROLE>;

-- 5. Drop the temporary membership; the role stays confined on its own.
REVOKE <APP_ROLE> FROM neondb_owner;

-- 6. Confinement check: every column must return false.
--    SELECT rolsuper, rolcreatedb, rolcreaterole, rolreplication, rolbypassrls
--      FROM pg_roles WHERE rolname = '<APP_ROLE>';

COMMIT;

-- Later rotations of the same role do not repeat step 1. Reset the login secret
-- as above, and, if ownership may have drifted, re-run steps 2 through 5.
