package database;

import org.slf4j.Logger;

import java.nio.file.Path;
import java.sql.*;

public class SQLiteTest {
    static private final String TEST_FILE_NAME = "sqlite-test.db";

    static public void testSQLite(Path gameDir) throws SQLException {
        //Set up the file
        Path databaseFile = gameDir.resolve(TEST_FILE_NAME);
        String url = "jdbc:sqlite:" + databaseFile.toAbsolutePath();

        try (Connection connection = DriverManager.getConnection(url)) {
            //First we make this table
            String createTable = """
                    CREATE TABLE IF NOT EXISTS test_entries (
                       id INTEGER PRIMARY KEY,
                       message TEXT NOT NULL
                    )
                    """;

            //Caller will catch this SQLException
            try (Statement statement = connection.createStatement()) {
                statement.execute(createTable);
            }

            // Insert one row,
            String insert = """
                    INSERT INTO test_entries (message)
                    VALUES (?)
                    """;

            //Caller catch SQLException
            //PreparedStatement allows ? placeholder and is safer
            try (PreparedStatement statement = connection.prepareStatement(insert)) {
                //1-based indexing, so this is just parsing the message
                statement.setString(1, "SQLite works!");
                statement.executeUpdate();
            }

            // Read the row back.
            String query = """
                    SELECT message
                    FROM test_entries
                    WHERE id = 1
                    """;

            //We can even try with multiple resources at once
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {
                //No result here
                if (!resultSet.next()) {
                    throw new SQLException("SQLite test row with id = 1 was not found.");
                }

                //Now we check if it's the correct message
                String message = resultSet.getString("message");

                if (!"SQLite works!".equals(message)) {
                    throw new SQLException("Unexpected SQLite test result: " + message);
                }
            }
        }

        //Success!
        System.out.println("SQLite OK");
    }

    static public void testDatabaseConnection(Path gameDir, Logger logger) throws SQLException {
        //Throwaway folder, will not be committed
        //TODO: Disable this function's call before committing
        Path file = gameDir.resolve("abyss-test").resolve("abyss.db");

        DatabaseConnection first = new DatabaseConnection(file);
        DatabaseConnection second = new DatabaseConnection(file);

        try {
            first.initSchema();
            second.initSchema();   // must be harmless the second time

            // write through the first connection, add a dummy user
            try (PreparedStatement preparedStatement = first.connection().prepareStatement(
                    "INSERT OR REPLACE INTO player (uuid, username, first_login) VALUES (?, ?, ?)")) {
                preparedStatement.setString(1, "test-uuid");
                preparedStatement.setString(2, "Steve");
                preparedStatement.setLong(3, 1000L);
                preparedStatement.executeUpdate();
            }

            // read through the second connection to see the inserted entry
            try (Statement statement = second.connection().createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT username FROM player WHERE uuid = 'test-uuid'")) {
                boolean found = resultSet.next() && "Steve".equals(resultSet.getString(1));
                logger.info("SQLite test result: second connection sees first's write = {}", found);
            }

            // seeding twice must not duplicate the powers
            try (Statement statement = second.connection().createStatement();
                 ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM power")) {
                resultSet.next();
                logger.info("SQLite test result: power rows = {} (expected 3)", resultSet.getInt(1));
            }
        } finally {
            //This will run before an exception propagates up to the caller, basically local cleanup
            //These 2 can fail but they handle themselves
            first.close();
            second.close();
        }
    }
}
