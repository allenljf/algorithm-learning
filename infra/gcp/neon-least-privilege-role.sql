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

-- 2. Neon roles created through the Console, CLI, or API are not Postgres
--    superusers, and neondb_owner has no admin option on them, so
--    `GRANT <APP_ROLE> TO neondb_owner` fails with "permission denied to grant
--    role" and a direct ownership transfer is not possible. Use a shared group
--    role that owns the application objects; both roles inherit ownership
--    through membership, so Flyway DDL works without owner-level attributes.
CREATE ROLE table_owners NOLOGIN;
GRANT USAGE, CREATE ON SCHEMA public TO table_owners;
GRANT table_owners TO neondb_owner;
GRANT table_owners TO <APP_ROLE>;

-- 3. The application schema is owned by the group.
ALTER SCHEMA public OWNER TO table_owners;

-- 4. Transfer every existing application object (tables, sequences, and the
--    Flyway history table) created by prior runs as neondb_owner to the group.
--    Scoped to the current database.
REASSIGN OWNED BY neondb_owner TO table_owners;

-- 5. Confinement check: every column must return false.
--    SELECT rolsuper, rolcreatedb, rolcreaterole, rolreplication, rolbypassrls
--      FROM pg_roles WHERE rolname = '<APP_ROLE>';

-- 6. Ownership check: every application object must be owned by table_owners.
--    SELECT c.relname, pg_get_userbyid(c.relowner) AS owner
--      FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
--     WHERE n.nspname = 'public' AND c.relkind IN ('r', 'S', 'p')
--     ORDER BY c.relname;

COMMIT;

-- Later rotations of the same role do not repeat steps 1 and 2. Reset the login
-- secret as above, and, if ownership may have drifted, re-run step 4.
