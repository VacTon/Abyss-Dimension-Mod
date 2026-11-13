package model;

public record PlayerProfile(
        String uuid,
        String username,
        int firstLogin,
        int lastSeen,
        int darknessEssence
) {
    public PlayerProfile {
        if (uuid == null || uuid.isBlank()) {
            throw new IllegalArgumentException("uuid (PK) cannot be null or blank");
        }

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
