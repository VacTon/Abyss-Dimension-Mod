package database;

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
}
