package database;

import io.github.vacton.abyss.Abyss;
import model.PlayerProfile;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.UUID;

public class StatsRepository {
    //TODO: Will implement addEssence, spendEssence, getPower(int), getPower(String), getAllPowers, mapProfile later
    private static final Logger LOGGER = LoggerFactory.getLogger(Abyss.MOD_ID);

    private final DatabaseConnection connection;

    public StatsRepository(DatabaseConnection connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    /**
     * Create a new player profile with default values
     * @param uuid the uuid, usually generated with "offline-player:" + username
     * @param username case-sensitive username
     * @param firstLogin unix-time using long data type
     * @return New player profile with default lastSeen = 0, darknessEssence = 0
     */
    public PlayerProfile loadOrCreateProfile(UUID uuid, String username, long firstLogin) {
        String uuidText = uuid.toString();   // convert once, use everywhere below
        String insertIfMissing = """
            INSERT OR IGNORE INTO "%s" (%s, %s, %s)
            VALUES (?, ?, ?)
            """.formatted(
                PlayerProfile.TABLE_NAME,
                PlayerProfile.UUID_COL,
                PlayerProfile.USERNAME_COL,
                PlayerProfile.FIRST_LOGIN_COL
        );

        String select = """
            SELECT %s, %s, %s, %s, %s
            FROM "%s"
            WHERE %s = ?
            """.formatted(
                PlayerProfile.UUID_COL,
                PlayerProfile.USERNAME_COL,
                PlayerProfile.FIRST_LOGIN_COL,
                PlayerProfile.LAST_SEEN_COL,
                PlayerProfile.DARKNESS_ESSENCE_COL,
                PlayerProfile.TABLE_NAME,
                PlayerProfile.UUID_COL
        );

        try {
            try (PreparedStatement insertStatement = connection.connection().prepareStatement(insertIfMissing)) {
                insertStatement.setString(1, uuidText);
                insertStatement.setString(2, username);
                insertStatement.setLong(3, firstLogin);
                insertStatement.executeUpdate();
            }

            try (PreparedStatement selectStatement = connection.connection().prepareStatement(select)) {
                selectStatement.setString(1, uuidText);

                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        throw new DatabaseException("Player " + uuidText + " missing right after insert");
                    }

                    long lastSeen = resultSet.getLong(PlayerProfile.LAST_SEEN_COL);
                    if (resultSet.wasNull()) {
                        //NULL is the default value in the database itself, but long cannot be null in Java
                        //So we normalize
                        lastSeen = PlayerProfile.DEFAULT_LAST_SEEN;
                    }

                    return new PlayerProfile(
                            UUID.fromString(resultSet.getString(PlayerProfile.UUID_COL)),
                            resultSet.getString(PlayerProfile.USERNAME_COL),
                            resultSet.getLong(PlayerProfile.FIRST_LOGIN_COL),
                            lastSeen,   //This is the normalized one
                            resultSet.getInt(PlayerProfile.DARKNESS_ESSENCE_COL)
                    );
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("loadOrCreateProfile(%s, %s, %d) failed".formatted(uuidText, username, firstLogin), e);
        }
    }

    /***
     * Update lastSeen, typically called on player disconnect
     * @param uuid the uuid of the player
     * @param lastSeen the current time, which is this player's last seen moment
     */
    public void touchLastSeen(UUID uuid, long lastSeen) {
        String uuidText = uuid.toString();
        //Perform SQL here
        String updateLastSeen = """
            UPDATE "%s"
            SET %s = ?
            WHERE %s = ?
            """.formatted(
                PlayerProfile.TABLE_NAME,
                PlayerProfile.LAST_SEEN_COL,
                PlayerProfile.UUID_COL
        );
        try (PreparedStatement preparedStatement = connection.connection().prepareStatement(updateLastSeen)) {
            preparedStatement.setLong(1, lastSeen);
            preparedStatement.setString(2, uuidText);
            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                LOGGER.warn("touchLastSeen({}, {}): no player with that uuid", uuidText, lastSeen);
            }
        } catch (SQLException e) {
            throw new DatabaseException("touchLastSeen(%s, %d) failed".formatted(uuid, lastSeen), e);
        }
    }
}
