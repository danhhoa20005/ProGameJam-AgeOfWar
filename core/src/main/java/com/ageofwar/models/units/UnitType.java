package com.ageofwar.models.units;

// Represents the 16 unit types across 5 eras (adjust as needed)
// Aiming for 3 units per era + 1 extra? Let's stick to 3 per era = 15 types for now.
public enum UnitType {
    // Stone Age (3)
    CAVEMAN,        // Melee basic
    SLINGERMAN,     // Ranged basic
    DINORIDER,      // Melee stronger/faster?

    // Medieval Age (3)
    SWORDSMAN,      // Melee standard
    ARCHER,         // Ranged standard
    KNIGHT,         // Melee tanky/strong

    // Industrial Age (3)
    RIFLEMAN,       // Ranged faster/weaker?
    CANNON,         // Ranged slow/AoE/heavy damage?
    GRENADIER,      // Ranged medium/AoE?

    // Modern Age (3)
    MARINE,         // Ranged standard modern
    TANK,           // Melee/Ranged vehicle, high HP/damage
    HELICOPTER,     // Flying ranged? (Needs Y-axis handling if flying) - Keep ground for now: ROCKET_LAUNCHER?

    // Future Age (3)
    LASER_TROOPER,  // Ranged advanced
    MECH_WARRIOR,   // Melee/Ranged heavy walker
    CYBORG,         // Ranged fast/precise?

    // Add a 16th unit if desired, maybe a super unit for the last era?
    // SUPER_SOLDIER
}
