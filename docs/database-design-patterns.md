# VCell Database Design Patterns

This document describes the database design patterns used throughout VCell for table definitions, CRUD operations, connection management, and access control. New tables should follow these patterns for consistency.

## Architecture Overview

The database layer follows a three-tier pattern:

```
DatabaseServerImpl (public API, wraps TopLevel)
    └── DBTopLevel / AdminDBTopLevel (connection + transaction management)
        └── DbDriver subclasses (SQL generation + execution)
            └── Table subclasses (schema definition + value mapping)
```

## Table Definitions

### Class hierarchy

```
cbit.sql.Table (abstract)
  ├── cbit.vcell.modeldb.VersionTable (abstract) — versioned entities with privacy/curation
  │   ├── BioModelTable
  │   ├── SimulationTable
  │   ├── MathModelTable
  │   ├── GeometryTable
  │   └── ...
  └── Direct subclasses — operational/non-versioned data
      ├── SimulationJobTable
      ├── UserTable
      ├── ApiClientTable
      └── ...
```

**Versioned tables** (`VersionTable` subclasses) store domain model objects (BioModels, Simulations) with built-in `privacy` (access control) and `versionFlag` (curation status) columns.

**Operational tables** (direct `Table` subclasses) store runtime/transactional data like job records, user sessions, etc. These typically don't need privacy or versioning. **The new `vc_optjob` table falls into this category**, similar to `SimulationJobTable`.

### Field declarations

Each table declares its columns as `public final Field` members. The `Field` class carries the column name, data type, and constraints:

```java
public class SimulationJobTable extends Table {
    private static final String TABLE_NAME = "vc_simulationjob";
    public static final String REF_TYPE = "REFERENCES " + TABLE_NAME + "(id)";

    // Singleton instance
    public static final SimulationJobTable table = new SimulationJobTable();

    // Column declarations
    public final Field simRef      = new Field("simRef",      SQLDataType.integer,      "NOT NULL " + SimulationTable.REF_TYPE + " ON DELETE CASCADE");
    public final Field submitDate  = new Field("submitDate",  SQLDataType.date,         "NOT NULL");
    public final Field taskID      = new Field("taskID",      SQLDataType.integer,      "NOT NULL");
    public final Field statusMsg   = new Field("statusMsg",   SQLDataType.varchar_4000, "");
    public final Field serverID    = new Field("serverID",    SQLDataType.varchar_20,   "NOT NULL");
    public final Field pbsJobID    = new Field("pbsJobID",    SQLDataType.varchar_100,  "");

    private SimulationJobTable() {
        super(TABLE_NAME);
        addFields(new Field[] { simRef, submitDate, taskID, ... });
    }
}
```

**Key conventions:**
- Each table class has a `public static final` singleton instance (`table`)
- Fields are `public final` for direct access in SQL generation
- The base `Table` class provides the `id` field (bigint primary key) automatically
- `SQLDataType` enum maps to database-agnostic types: `integer`, `varchar_255`, `varchar_4000`, `date`, `clob_text`, etc.
- Constraints include `NOT NULL`, `REFERENCES`, and `ON DELETE CASCADE`

### Available SQLDataType values

| SQLDataType | Oracle | PostgreSQL |
|---|---|---|
| `integer` | NUMBER | bigint |
| `number_as_integer` | NUMBER | bigint |
| `varchar_10` | VARCHAR2(10) | varchar(10) |
| `varchar_20` | VARCHAR2(20) | varchar(20) |
| `varchar_32` | VARCHAR2(32) | varchar(32) |
| `varchar_64` | VARCHAR2(64) | varchar(64) |
| `varchar_100` | VARCHAR2(100) | varchar(100) |
| `varchar_128` | VARCHAR2(128) | varchar(128) |
| `varchar_255` | VARCHAR2(255) | varchar(255) |
| `varchar_512` | VARCHAR2(512) | varchar(512) |
| `varchar_1024` | VARCHAR2(1024) | varchar(1024) |
| `varchar_2048` | VARCHAR2(2048) | varchar(2048) |
| `varchar_4000` | VARCHAR2(4000) | varchar(4000) |
| `date` | TIMESTAMP | timestamp |
| `clob_text` | CLOB | text |
| `blob_bytea` | BLOB | bytea |
| `numeric` | NUMBER | numeric |
| `char_1` | CHAR(1) | char(1) |

## CRUD Operations

Each `Table` subclass implements three methods for SQL generation:

### INSERT — `getSQLValueList()`

Generates the VALUES clause for an INSERT statement. Takes a `KeyValue` (the new row's primary key) and the domain object to persist:

```java
public String getSQLValueList(KeyValue key, SimulationJobStatus jobStatus, DatabaseSyntax dbSyntax) {
    return "(" +
        key + "," +
        jobStatus.getVCSimulationIdentifier().getSimulationKey() + "," +
        "current_timestamp" + "," +
        jobStatus.getTaskID() + "," +
        jobStatus.getSchedulerStatus().getDatabaseNumber() + "," +
        "'" + TokenMangler.getSQLEscapedString(statusMsg) + "'" + "," +
        // ... more fields
    ")";
}
```

**Conventions:**
- First value is always the primary key
- Timestamps use `current_timestamp` for server-generated times
- Strings escaped with `TokenMangler.getSQLEscapedString()`
- Dates formatted with `VersionTable.formatDateToOracle()`
- Null values written as literal `null`
- Enum values stored as integers via `.getDatabaseNumber()`

### UPDATE — `getSQLUpdateList()`

Generates the SET clause for an UPDATE statement:

```java
public String getSQLUpdateList(SimulationJobStatus jobStatus, DatabaseSyntax dbSyntax) {
    return
        schedulerStatus + "=" + jobStatus.getSchedulerStatus().getDatabaseNumber() + "," +
        statusMsg + "='" + TokenMangler.getSQLEscapedString(msg) + "'," +
        latestUpdateDate + "=" + "current_timestamp";
    // Note: last field has NO trailing comma
}
```

### SELECT — `getXxx(ResultSet)`

Reads a domain object from a `ResultSet`:

```java
public SimulationJobStatus getSimulationJobStatus(ResultSet rset) throws SQLException {
    KeyValue key = new KeyValue(rset.getBigDecimal(id.toString()));
    KeyValue simRef = new KeyValue(rset.getBigDecimal(this.simRef.toString()));
    Date submitDate = rset.getTimestamp(this.submitDate.toString());
    int schedulerStatusInt = rset.getInt(this.schedulerStatus.toString());
    SchedulerStatus status = SchedulerStatus.fromDatabaseNumber(schedulerStatusInt);
    String statusMsg = rset.getString(this.statusMsg.toString());
    // ... reconstruct domain object
    return new SimulationJobStatus(...);
}
```

**Conventions:**
- Access columns by `field.toString()` (the column name)
- Use `rset.wasNull()` after `getInt()` to distinguish 0 from NULL
- Convert BigDecimal → KeyValue for foreign keys
- Convert int → enum via `fromDatabaseNumber()` static method
- Nullable fields checked with `rset.wasNull()` or null reference check

## DbDriver Layer

DbDriver subclasses contain the actual SQL execution logic for a group of related tables. They are organized by domain area:

- `SimulationJobDbDriver` — simulation job CRUD
- `BioModelDbDriver` — biomodel CRUD + related tables
- `AdminDBTopLevel` — administrative operations

### Standard method signatures

```java
// INSERT
public KeyValue insertSimulationJobStatus(Connection con, SimulationJobStatus jobStatus)
    throws SQLException {
    KeyValue key = keyFactory.getNewKey(con);
    String sql = "INSERT INTO " + SimulationJobTable.table.getTableName() +
                 " " + SimulationJobTable.table.getSQLColumnList() +
                 " VALUES " + SimulationJobTable.table.getSQLValueList(key, jobStatus, dbSyntax);
    executeUpdate(con, sql);
    return key;
}

// UPDATE
public void updateSimulationJobStatus(Connection con, KeyValue key, SimulationJobStatus jobStatus)
    throws SQLException {
    String sql = "UPDATE " + SimulationJobTable.table.getTableName() +
                 " SET " + SimulationJobTable.table.getSQLUpdateList(jobStatus, dbSyntax) +
                 " WHERE " + SimulationJobTable.table.id + "=" + key;
    executeUpdate(con, sql);
}

// SELECT
public SimulationJobStatus[] getSimulationJobStatus(Connection con, KeyValue simKey)
    throws SQLException {
    String sql = "SELECT * FROM " + SimulationJobTable.table.getTableName() +
                 " WHERE " + SimulationJobTable.table.simRef + "=" + simKey;
    Statement stmt = con.createStatement();
    try {
        ResultSet rset = stmt.executeQuery(sql);
        while (rset.next()) {
            results.add(SimulationJobTable.table.getSimulationJobStatus(rset));
        }
    } finally {
        stmt.close();
    }
    return results.toArray(...);
}
```

### Helper method

```java
protected void executeUpdate(Connection con, String sql) throws SQLException {
    if (lg.isDebugEnabled()) lg.debug(sql);
    Statement stmt = con.createStatement();
    try {
        int rowCount = stmt.executeUpdate(sql);
        if (rowCount != 1) {
            throw new SQLException("Expected 1 row, got " + rowCount);
        }
    } finally {
        stmt.close();
    }
}
```

## Connection & Transaction Management

### ConnectionFactory

`org.vcell.db.ConnectionFactory` provides connection pooling. In Quarkus (`vcell-rest`), this is implemented by `AgroalConnectionFactory` which delegates to Agroal/JDBC datasources.

**Usage pattern:**
```java
Object lock = new Object();
Connection con = connectionFactory.getConnection(lock);
try {
    // ... perform operations
    con.commit();
} catch (SQLException e) {
    con.rollback();
    throw e;
} finally {
    connectionFactory.release(con, lock);
}
```

### DBTopLevel / AdminDBTopLevel

These classes wrap DbDriver calls with connection management and retry logic:

```java
public SimulationJobStatus[] getSimulationJobStatus(KeyValue simKey, boolean bEnableRetry)
    throws DataAccessException, SQLException {
    Object lock = new Object();
    Connection con = conFactory.getConnection(lock);
    try {
        return dbDriver.getSimulationJobStatus(con, simKey);
    } catch (Throwable e) {
        handle_DataAccessException_SQLException(e);
        if (bEnableRetry && isBadConnection(con)) {
            conFactory.failed(con, lock);
            return getSimulationJobStatus(simKey, false); // retry once
        }
        throw e;
    } finally {
        conFactory.release(con, lock);
    }
}
```

**Key features:**
- Automatic retry on bad connections (once)
- Connection release in `finally` block
- Exception conversion: `SQLException` → `DataAccessException`

### DatabaseServerImpl

The public API that Quarkus services call. Wraps `DBTopLevel`/`AdminDBTopLevel`:

```java
@ApplicationScoped
public class DatabaseServerImpl {
    private final DBTopLevel dbTopLevel;
    private final AdminDBTopLevel adminDbTopLevel;

    public DatabaseServerImpl(AgroalConnectionFactory connectionFactory, KeyFactory keyFactory) {
        this.dbTopLevel = new DBTopLevel(connectionFactory);
        this.adminDbTopLevel = new AdminDBTopLevel(connectionFactory);
    }

    public SimulationJobStatus[] getSimulationJobStatus(KeyValue simKey)
        throws DataAccessException, SQLException {
        return adminDbTopLevel.getSimulationJobStatus(simKey, true);
    }
}
```

## Key Generation

All primary keys come from a shared database sequence (`newSeq`). The key is generated **before** the INSERT:

```java
KeyValue key = keyFactory.getNewKey(con);  // SELECT nextval('newSeq')
String sql = "INSERT INTO table VALUES (" + key + ", ...)";
```

The `KeyValue` class wraps a `BigDecimal` and is used throughout the codebase for all database IDs.

## Access Control (Versioned Tables Only)

Versioned tables (`VersionTable` subclasses) have two orthogonal concepts:

### Privacy (GroupAccess)

Controls who can read/modify a record:
- `GroupAccess.GROUPACCESS_ALL` (groupid=0) — public, anyone can read
- `GroupAccess.GROUPACCESS_NONE` (groupid=1) — private, owner only
- Other groupid values — shared with a specific group

Enforced in SELECT queries via `DatabasePolicySQL.enforceOwnershipSelect()`.

### Curation Status (VersionFlag)

Indicates model maturity, **not** access control:
- `VersionFlag.Current` (0) — normal working copy
- `VersionFlag.Archived` (1) — frozen, not deletable
- `VersionFlag.Published` (3) — published, not deletable, publicly accessible

**Operational tables like `vc_optjob` do not use privacy or curation status.** Access control is enforced at the REST layer by checking the `ownerRef` field.

## Summary: Creating a New Operational Table

To add a new table following VCell conventions (e.g., `vc_optjob`):

1. **Create `OptJobTable extends Table`** in `cbit.vcell.messaging.db` or `cbit.vcell.modeldb`
   - Declare all fields as `public final Field`
   - Implement `getSQLValueList()`, `getSQLUpdateList()`, `getOptJobStatus(ResultSet)`
   - Add a `public static final OptJobTable table` singleton

2. **Create or extend a DbDriver** with INSERT/UPDATE/DELETE/SELECT methods
   - Use `Table.getSQLColumnList()` and `getSQLValueList()` for INSERT
   - Use `getSQLUpdateList()` for UPDATE
   - Use `getOptJobStatus(ResultSet)` for SELECT
   - Use `keyFactory.getNewKey(con)` for key generation

3. **Add methods to AdminDBTopLevel** (or create a new TopLevel) with connection + retry pattern

4. **Add public methods to DatabaseServerImpl** wrapping the TopLevel calls

5. **Register in `SQLCreateAllTables.getVCellTables()`** — add the table singleton to the array (see below)

6. **Regenerate `init.sql`** using `AdminCli db-create-script` (see below)

7. **Create Oracle DDL** for production deployment (manual migration)

## Authoritative Table Registry

`SQLCreateAllTables.getVCellTables()` in `vcell-server` returns the authoritative list of all active tables in the database. Every table must be registered here — this array drives:

- DDL script generation (`db-create-script`)
- Schema comparison (`db-compare-schema`)
- Full database creation/destruction (`destroyAndRecreateTables`)

Tables are ordered by dependency (referenced tables before referencing tables) so that `CREATE TABLE` and `DROP TABLE` (in reverse) respect foreign key constraints.

**When adding a new table**, insert it at the appropriate position in the array, after any tables it references via foreign keys.

## Database Utilities (AdminCli)

The `vcell-admin` module provides CLI commands (PicoCLI) for database management. These are in `org.vcell.admin.cli.db`.

### `db-create-script` — Generate DDL from Table definitions

Generates a SQL creation script from the Java `Table` definitions registered in `SQLCreateAllTables.getVCellTables()`. This is the canonical way to produce `init.sql` for test/dev PostgreSQL databases.

```bash
AdminCli db-create-script \
  --database-type=postgres \
  --bootstrap-data=false \
  --create-script=vcell-rest/src/main/resources/scripts/init.sql
```

**Options:**
| Option | Description |
|--------|-------------|
| `--database-type` | `oracle` or `postgres` (required) |
| `--create-script` | Output file path (required) |
| `--bootstrap-data` | `true` to include INSERT statements for seed users, groups, API client (required) |
| `-d, --debug` | Enable debug logging |

**What it generates:**
1. `CREATE TABLE` statements for every table in `getVCellTables()` (in dependency order)
2. `CREATE VIEW public.dual` (PostgreSQL ORACLE compatibility)
3. `CREATE SEQUENCE newSeq`
4. `CREATE INDEX` statements for performance-critical lookups
5. (If `--bootstrap-data=true`) INSERT statements for void/admin/test/support users, private/public groups, available status, and default API client

**Important:** The `init.sql` file used by Quarkus testcontainers (`vcell-rest/src/main/resources/scripts/init.sql`) should always be regenerated from this command rather than hand-edited. The Java `Table` classes are the source of truth for schema.

### `db-compare-schema` — Compare live database against Table definitions

Compares the schema of a running database against the Java `Table` definitions. Useful for verifying that a deployed database matches the expected schema, or for identifying drift after manual DDL changes.

```bash
AdminCli db-compare-schema
```

Requires database connection properties to be configured (via environment variables or `PropertyLoader`). Uses `CompareDatabaseSchema.runCompareSchemas()` to introspect the live database and diff against `getVCellTables()`.

**Options:**
| Option | Description |
|--------|-------------|
| `-d, --debug` | Enable debug logging |

### `db-destroy-recreate` — Drop and recreate all tables (interactive)

**DESTRUCTIVE.** Drops all tables, sequences, and views, then recreates them. Prompts via Swing dialog for confirmation. Only for development use.

## Schema Change Workflow

When modifying the database schema (adding tables, columns, or indexes):

1. **Edit the Java `Table` class** — add/modify `Field` declarations
2. **Register new tables** in `SQLCreateAllTables.getVCellTables()` if not already present
3. **Regenerate `init.sql`:**
   ```bash
   AdminCli db-create-script --database-type=postgres --bootstrap-data=false \
     --create-script=vcell-rest/src/main/resources/scripts/init.sql
   ```
4. **Verify** with `db-compare-schema` against a dev database if available
5. **Create production migration DDL** (Oracle `ALTER TABLE` / `CREATE TABLE` statements) for deployed databases
