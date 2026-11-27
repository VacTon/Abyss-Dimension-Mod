CREATE TABLE IF NOT EXISTS player (
    uuid TEXT,
    username TEXT NOT NULL,
    first_login INTEGER NOT NULL CHECK(first_login >= 0),
    last_seen INTEGER DEFAULT NULL CHECK(last_seen IS NULL OR last_seen >= first_login),
    darkness_essence INTEGER NOT NULL DEFAULT 0 CHECK(darkness_essence >= 0),
    PRIMARY KEY(uuid)
);

CREATE TABLE IF NOT EXISTS power (
    power_id INTEGER,
    power_name TEXT NOT NULL UNIQUE,
    cost INTEGER NOT NULL DEFAULT 0 CHECK(cost > 0),
    cooldown REAL NOT NULL DEFAULT 0.0 CHECK(cooldown > 0.0),
    PRIMARY KEY(power_id)
);

-- Seed / Upsert powers
INSERT INTO power (power_id, power_name, cost, cooldown)
VALUES
    (1, 'Haste',     8,  600),
    (2, 'Explosion', 32, 1200),
    (3, 'Summon',    16, 300)
ON CONFLICT(power_id) DO UPDATE SET
    power_name = excluded.power_name,
    cost       = excluded.cost,
    cooldown   = excluded.cooldown;