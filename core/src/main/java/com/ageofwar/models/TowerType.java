package com.ageofwar.models;

// Represents the 15 tower types across 5 eras (3 per era)
public enum TowerType {
    // Stone Age (3)
    ROCK_TOWER,     // Basic single target
    EGG_LAUNCHER,   // Slow, small AoE?
    WATCHTOWER,

    // Medieval Age (3)
    ARROW_TOWER,    // Standard single target
    CATAPULT_TOWER, // Slow, AoE damage
    BALLISTA_TOWER, // High damage, single target, slow?

    // Industrial Age (3)
    GATLING_TOWER,  // Fast, low damage
    CANNON_TOWER,   // Slow, high damage/AoE
    MORTAR_TOWER,   // High arc, AoE

    // Modern Age (3)
    MACHINEGUN_TURRET, // Fast standard
    MISSILE_TURRET, // Slow, high damage, maybe anti-air/vehicle bonus?
    TESLA_COIL,     // Chain lightning? Short range AoE?

    // Future Age (3)
    LASER_TURRET,   // Continuous beam? High single target DPS
    PLASMA_TURRET,  // Slow, large AoE, high damage
    RAILGUN_TURRET  // Very high damage, single target, very slow
}
