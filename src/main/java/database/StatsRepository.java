package database;

import io.github.vacton.abyss.Abyss;
import model.PlayerProfile;
import model.PowerRecord;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class StatsRepository {
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

                    return mapProfile(resultSet);
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

    /***
     * Add essence to a specific player by uuid
     * @param uuid the player uuid
     * @param amount the amount to add, must be >= 0
     * @return new darkness essence balance
     */
    public int addEssence(UUID uuid, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("addEssence(%s, %d) cannot add negative essence".formatted(uuid, amount));
        }

        String uuidText = uuid.toString();
        String updateEssence = """
            UPDATE "%s"
            SET %s = %s + ?
            WHERE %s = ?
            RETURNING %s;
            """.formatted(
                PlayerProfile.TABLE_NAME,
                PlayerProfile.DARKNESS_ESSENCE_COL,
                PlayerProfile.DARKNESS_ESSENCE_COL,
                PlayerProfile.UUID_COL,
                PlayerProfile.DARKNESS_ESSENCE_COL
        );

        try (PreparedStatement preparedStatement = connection.connection().prepareStatement(updateEssence)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, uuidText);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("No rows updated in addEssence");
                }

                //Get new balance
                return resultSet.getInt(PlayerProfile.DARKNESS_ESSENCE_COL);
            }
        } catch (SQLException e) {
            throw new DatabaseException("addEssence(%s, %d) failed".formatted(uuid, amount), e);
        }
    }

    /***
     * Spend essence of a specific player by uuid
     * @param uuid the uuid of the player having this essence
     * @param amount the amount to spend, >= 0
     * @return true if exactly 1 row changed
     */
    public boolean spendEssence(UUID uuid, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("spendEssence(%s, %d) cannot spend negative essence".formatted(uuid, amount));
        }

        String uuidText = uuid.toString();
        String updateEssence = """
            UPDATE "%s"
            SET %s = %s - ?
            WHERE %s = ?
            """.formatted(
                PlayerProfile.TABLE_NAME,
                PlayerProfile.DARKNESS_ESSENCE_COL,
                PlayerProfile.DARKNESS_ESSENCE_COL,
                PlayerProfile.UUID_COL
        );

        try (PreparedStatement preparedStatement = connection.connection().prepareStatement(updateEssence)) {
            preparedStatement.setInt(1, amount);
            preparedStatement.setString(2, uuidText);
            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                LOGGER.warn("spendEssence({}, {}): no player with that uuid", uuidText, amount);
            }

            return rows == 1;
        } catch (SQLException e) {
            //If the constraint darkness_essence >= 0 is violated, it will be caught here
            throw new DatabaseException("spendEssence(%s, %d) failed".formatted(uuid, amount), e);
        }
    }

    /**
     * Get all powers defined in DatabaseConnection.initSchema()
     * @return all powers available in the database
     */
    public List<PowerRecord> getAllPowers() {
        //This uses a list to not depend too much on implementation details. It just has to implement a List
        List<PowerRecord> powerList = new ArrayList<>();
        String selectPowers = """
            SELECT *
            FROM "%s"
            """.formatted(
                PowerRecord.TABLE_NAME
        );

        try (Statement statement = connection.connection().createStatement();
             ResultSet resultSet = statement.executeQuery(selectPowers)) {

            while (resultSet.next()) {
                powerList.add(mapPowerRecord(resultSet));
            }

            if (powerList.isEmpty()) {
                LOGGER.warn("getAllPowers() found no powers");
            }

            return powerList;
        } catch (SQLException e) {
            throw new DatabaseException("getAllPowers() failed", e);
        }
    }

    /**
     * Get a power by its power ID
     * @param powerId the id of the power, starting from 1
     * @return the power record matching powerId
     */
    public PowerRecord getPower(int powerId) {
        String findPower = """
            SELECT *
            FROM "%s"
            WHERE %s = ?
            """.formatted(
                PowerRecord.TABLE_NAME,
                PowerRecord.POWER_ID_COL
        );

        try (PreparedStatement preparedStatement = connection.connection().prepareStatement(findPower)) {
            preparedStatement.setInt(1, powerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Cannot find this powerId = " + powerId);
                }

                return mapPowerRecord(resultSet);
            }
        } catch (SQLException e) {
            throw new DatabaseException("getPower(%d) failed".formatted(powerId), e);
        }
    }

    /**
     * Get a power by its power name, which is unique
     * @param powerName the name of the power
     * @return the power record matching powerName
     */
    public PowerRecord getPower(String powerName) {
        String findPower = """
            SELECT *
            FROM "%s"
            WHERE %s = ?
            """.formatted(
                PowerRecord.TABLE_NAME,
                PowerRecord.POWER_NAME_COL
        );

        try (PreparedStatement preparedStatement = connection.connection().prepareStatement(findPower)) {
            preparedStatement.setString(1, powerName);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    throw new SQLException("Cannot find this powerName = " + powerName);
                }

                return mapPowerRecord(resultSet);
            }
        } catch (SQLException e) {
            throw new DatabaseException("getPower(%s) failed".formatted(powerName), e);
        }
    }

    private PowerRecord mapPowerRecord(ResultSet resultSet) throws SQLException {
        return new PowerRecord(
                resultSet.getInt(PowerRecord.POWER_ID_COL),
                resultSet.getString(PowerRecord.POWER_NAME_COL),
                resultSet.getInt(PowerRecord.COST_COL),
                resultSet.getInt(PowerRecord.COOLDOWN_COL)
        );
    }

    private PlayerProfile mapProfile(ResultSet resultSet) throws SQLException {
        return new PlayerProfile(
                UUID.fromString(resultSet.getString(PlayerProfile.UUID_COL)),
                resultSet.getString(PlayerProfile.USERNAME_COL),
                resultSet.getLong(PlayerProfile.FIRST_LOGIN_COL),
                resultSet.getLong(PlayerProfile.LAST_SEEN_COL),   //This is the normalized one
                resultSet.getInt(PlayerProfile.DARKNESS_ESSENCE_COL)
        );
    }
}
