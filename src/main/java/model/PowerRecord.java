package model;

public record PowerRecord(int power_id, String power_name, int cost, double cooldown) {
    public PowerRecord {
        if (power_name == null || power_name.isBlank()) {
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
