package model;

public record PowerRecord(int powerId, String powerName, int cost, double cooldown) {
    public static final String TABLE_NAME = "power";
    public static final String POWER_ID_COL = "power_id";
    public static final String POWER_NAME_COL = "power_name";
    public static final String COST_COL = "cost";
    public static final String COOLDOWN_COL = "cooldown";

    public PowerRecord {
        if (powerName == null || powerName.isBlank()) {
            throw new IllegalArgumentException("power_name cannot be null or empty");
        }

        if (cost < 0) {
            throw new IllegalArgumentException("cost cannot be negative");
        }
        
        if (cooldown < 0.0) {
            throw new IllegalArgumentException("cooldown cannot be negative");
        }
    }
}
