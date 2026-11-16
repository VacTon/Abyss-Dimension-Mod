package model;

import java.util.Objects;
import java.util.UUID;

public record PlayerProfile(
        UUID uuid,
        String username,
        long firstLogin,
        long lastSeen,
        int darknessEssence
) {
    public static final String TABLE_NAME = "player";
    public static final String UUID_COL = "uuid";
    public static final String USERNAME_COL = "username";
    public static final String FIRST_LOGIN_COL = "first_login";
    public static final String LAST_SEEN_COL = "last_seen";
    public static final String DARKNESS_ESSENCE_COL = "darkness_essence";

    public PlayerProfile {
        Objects.requireNonNull(uuid, "uuid (PK) cannot be null");

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("username cannot be null or blank");
        }

        if (firstLogin < 0) {
            throw new IllegalArgumentException("firstLogin cannot be negative");
        }

        if (lastSeen < firstLogin) {
            throw new IllegalArgumentException("lastSeen cannot be smaller than firstLogin");
        }

        if (darknessEssence < 0) {
            throw new IllegalArgumentException("darknessEssence cannot be negative.");
        }
    }
}
