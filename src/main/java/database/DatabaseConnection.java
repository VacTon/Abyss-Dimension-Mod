package database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private final Path dbFile;
    private final Connection connection;
    private static final Path MEMORY = Path.of(":memory:");

    /**
     * Construct a DatabaseConnection with a Path to the database file and a long-lived Connection.
     * This connection is final and if it dies, you have to make another DatabaseConnection.
     * @param databasePath: the prepared path built relatively from the gameDir
     */
    public DatabaseConnection(Path databasePath) {
        //The path is pre-built
        boolean inMemory = MEMORY.equals(databasePath);
        String url;

        try {
            if (inMemory) {
                //This is a test, there is no file
                dbFile = null;
                url = "jdbc:sqlite::memory:";
            } else {
                dbFile = databasePath.toAbsolutePath();

                //If the built path has nonexistent folder, this fixes it
                //Otherwise, it does nothing
                Files.createDirectories(dbFile.getParent());
                url = "jdbc:sqlite:" + dbFile;
            }

            connection = DriverManager.getConnection(url);

            try (Statement st = connection.createStatement()) {
                //Enforce foreign key constraints and set timeout so it can wait when locked
                //The unit is ms, so 5000 = 5s
                st.execute("PRAGMA foreign_keys = ON");
                st.execute("PRAGMA busy_timeout = 5000");
            }
        } catch (IOException | SQLException e) {
            throw new DatabaseException("Failed to open database at " + dbFile, e);
        }
    }

    /**
    * Creates the tables Player and Power if they don't exist. If they exist, nothing happens.
     * Throws a DatabaseException on failure of SQL statement creation.
     */
    public void initSchema() {
        try (Statement statement = connection.createStatement()) {
            String createPlayer = """
                    CREATE TABLE IF NOT EXISTS "player" (
                    	"uuid"	TEXT,
                    	"username"	TEXT NOT NULL,
                    	"first_login"	INTEGER NOT NULL CHECK("first_login" >= 0),
                    	"last_seen"	INTEGER DEFAULT NULL CHECK("last_seen" IS NULL OR "last_seen" >= "first_login"),
                    	"darkness_essence"	INTEGER NOT NULL DEFAULT 0 CHECK("darkness_essence" >= 0),
                    	PRIMARY KEY("uuid")
                    )
                    """;
            statement.execute(createPlayer);

            String createPower = """
                    CREATE TABLE IF NOT EXISTS "power" (
                    	"power_id"	INTEGER,
                    	"power_name"	TEXT NOT NULL UNIQUE,
                    	"cost"	INTEGER NOT NULL DEFAULT 0 CHECK("cost" >  0),
                    	"cooldown"	REAL NOT NULL DEFAULT 0.0 CHECK("cooldown" > 0.0),
                    	PRIMARY KEY("power_id")
                    )
                    """;
            statement.execute(createPower);

            //Upsert here
            String seedPowers = """
                    INSERT INTO "power" ("power_id", "power_name", "cost", "cooldown")
                    VALUES
                        (1, 'Haste',     8,  600),
                        (2, 'Explosion', 32, 1200),
                        (3, 'Summon',    16, 300)
                    ON CONFLICT("power_id") DO UPDATE SET
                        "power_name" = excluded."power_name",
                        "cost"       = excluded."cost",
                        "cooldown"   = excluded."cooldown"
                    """;
            statement.execute(seedPowers);
        } catch (SQLException e) {
            throw new DatabaseException("Failed to initialize schema for database at " + dbFile, e);
        }
    }

    /**
     * Close database connection, must be called after finishing all work.
     * A DatabaseException is thrown on failure of closing.
     */
    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to close database at " + dbFile, e);
        }
    }

    /**
     * Package-private in database package, so no outside package can access this connection.
     * @return connection
     */
    Connection connection() {
        return connection;
    }
}
