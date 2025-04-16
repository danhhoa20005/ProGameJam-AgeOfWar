package com.ageofwar.configs;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.ageofwar.models.Era;
import com.ageofwar.models.TowerType;
import com.ageofwar.models.UnitType;

import java.util.HashMap;
import java.util.Map;

// Central place for Unit and Tower stats definitions
public class UnitConfig {

    // --- Placeholder Dimensions ---
    private static final float DEFAULT_UNIT_WIDTH = 40;
    private static final float DEFAULT_UNIT_HEIGHT = 60;
    private static final float DEFAULT_TOWER_WIDTH = 50;
    private static final float DEFAULT_TOWER_HEIGHT = 80;


    // --- Unit Definitions ---
    // Use Maps for easier lookup, or keep using switch statements if preferred

    public static Era getUnitRequiredEra(UnitType type) {
        switch (type) {
            case CAVEMAN: case SLINGERMAN: case DINORIDER: return Era.STONE;
            case SWORDSMAN: case ARCHER: case KNIGHT: return Era.MEDIEVAL;
            case RIFLEMAN: case CANNON: case GRENADIER: return Era.INDUSTRIAL;
            case MARINE: case TANK: case HELICOPTER: return Era.MODERN; // HELICOPTER -> ROCKET_LAUNCHER?
            case LASER_TROOPER: case MECH_WARRIOR: case CYBORG: return Era.FUTURE;
            default: return Era.STONE;
        }
    }

    public static int getUnitCost(UnitType type) {
        // Placeholder costs - NEED BALANCING!
        switch (type) {
            // Stone
            case CAVEMAN: return 50;
            case SLINGERMAN: return 75;
            case DINORIDER: return 150;
            // Medieval
            case SWORDSMAN: return 100;
            case ARCHER: return 125;
            case KNIGHT: return 300;
            // Industrial
            case RIFLEMAN: return 150;
            case CANNON: return 400;
            case GRENADIER: return 250;
            // Modern
            case MARINE: return 200;
            case TANK: return 600;
            case HELICOPTER: return 500; // ROCKET_LAUNCHER cost
            // Future
            case LASER_TROOPER: return 300;
            case MECH_WARRIOR: return 800;
            case CYBORG: return 600;
            default: return 100;
        }
    }

    public static int getUnitHealth(UnitType type) {
        // Placeholder HP - NEED BALANCING!
        switch (type) {
            case CAVEMAN: return 100;
            case SLINGERMAN: return 70;
            case DINORIDER: return 250;
            case SWORDSMAN: return 180;
            case ARCHER: return 100;
            case KNIGHT: return 400;
            case RIFLEMAN: return 150;
            case CANNON: return 300; // Lower HP, high damage?
            case GRENADIER: return 180;
            case MARINE: return 220;
            case TANK: return 1000;
            case HELICOPTER: return 400; // ROCKET_LAUNCHER HP
            case LASER_TROOPER: return 250;
            case MECH_WARRIOR: return 1500;
            case CYBORG: return 500;
            default: return 100;
        }
    }

    public static int getUnitDamage(UnitType type) {
        // Placeholder Damage - NEED BALANCING!
        switch (type) {
            case CAVEMAN: return 15;
            case SLINGERMAN: return 10;
            case DINORIDER: return 30;
            case SWORDSMAN: return 25;
            case ARCHER: return 18;
            case KNIGHT: return 40;
            case RIFLEMAN: return 20;
            case CANNON: return 100; // High damage
            case GRENADIER: return 40; // AoE? (Not implemented yet)
            case MARINE: return 30;
            case TANK: return 80;
            case HELICOPTER: return 60; // ROCKET_LAUNCHER damage
            case LASER_TROOPER: return 45;
            case MECH_WARRIOR: return 120;
            case CYBORG: return 70;
            default: return 10;
        }
    }

    public static float getUnitAttackSpeed(UnitType type) {
        // Attacks per second - NEED BALANCING!
        switch (type) {
            case CAVEMAN: return 1.0f;
            case SLINGERMAN: return 0.8f;
            case DINORIDER: return 1.2f;
            case SWORDSMAN: return 1.1f;
            case ARCHER: return 0.9f;
            case KNIGHT: return 0.8f;
            case RIFLEMAN: return 1.5f; // Faster fire rate
            case CANNON: return 0.3f; // Slow fire rate
            case GRENADIER: return 0.7f;
            case MARINE: return 1.4f;
            case TANK: return 0.6f;
            case HELICOPTER: return 0.8f; // ROCKET_LAUNCHER speed
            case LASER_TROOPER: return 1.8f;
            case MECH_WARRIOR: return 0.5f;
            case CYBORG: return 1.2f;
            default: return 1.0f;
        }
    }

    public static float getUnitRange(UnitType type) {
        // Placeholder Range - NEED BALANCING!
        switch (type) {
            // Melee units have short range
            case CAVEMAN: return 50f;
            case DINORIDER: return 60f;
            case SWORDSMAN: return 55f;
            case KNIGHT: return 65f;
            case MECH_WARRIOR: return 80f; // Heavy melee might have slightly more reach

            // Ranged units
            case SLINGERMAN: return 150f;
            case ARCHER: return 200f;
            case RIFLEMAN: return 220f;
            case CANNON: return 300f;
            case GRENADIER: return 180f;
            case MARINE: return 230f;
            case TANK: return 250f; // Tank cannon range
            case HELICOPTER: return 280f; // ROCKET_LAUNCHER range
            case LASER_TROOPER: return 260f;
            case CYBORG: return 240f;
            default: return 100f;
        }
    }

    public static float getUnitMoveSpeed(UnitType type) {
        // Pixels per second - NEED BALANCING!
        switch (type) {
            case CAVEMAN: return 60f;
            case SLINGERMAN: return 55f;
            case DINORIDER: return 90f; // Faster
            case SWORDSMAN: return 70f;
            case ARCHER: return 65f;
            case KNIGHT: return 50f; // Slower, tanky
            case RIFLEMAN: return 75f;
            case CANNON: return 40f; // Very slow
            case GRENADIER: return 60f;
            case MARINE: return 70f;
            case TANK: return 45f; // Slow vehicle
            case HELICOPTER: return 80f; // ROCKET_LAUNCHER speed (ground unit)
            case LASER_TROOPER: return 75f;
            case MECH_WARRIOR: return 40f; // Slow heavy walker
            case CYBORG: return 90f; // Fast
            default: return 60f;
        }
    }

    public static int getUnitXpReward(UnitType type) {
        // XP gained when this unit is killed - NEED BALANCING!
        // Roughly proportional to cost/difficulty
        return getUnitCost(type) / 5; // Simple starting point
    }

    public static float getUnitWidth(UnitType type) {
        // Can customize size per unit type later
        if (type == UnitType.TANK || type == UnitType.MECH_WARRIOR || type == UnitType.CANNON) return DEFAULT_UNIT_WIDTH * 1.5f;
        return DEFAULT_UNIT_WIDTH;
    }
    public static float getUnitHeight(UnitType type) {
        if (type == UnitType.TANK || type == UnitType.MECH_WARRIOR || type == UnitType.CANNON) return DEFAULT_UNIT_HEIGHT * 1.2f;
        return DEFAULT_UNIT_HEIGHT;
    }


    // --- Tower Definitions ---

    public static Era getTowerRequiredEra(TowerType type) {
        switch (type) {
            case ROCK_TOWER: case EGG_LAUNCHER: case WATCHTOWER: return Era.STONE;
            case ARROW_TOWER: case CATAPULT_TOWER: case BALLISTA_TOWER: return Era.MEDIEVAL;
            case GATLING_TOWER: case CANNON_TOWER: case MORTAR_TOWER: return Era.INDUSTRIAL;
            case MACHINEGUN_TURRET: case MISSILE_TURRET: case TESLA_COIL: return Era.MODERN;
            case LASER_TURRET: case PLASMA_TURRET: case RAILGUN_TURRET: return Era.FUTURE;
            default: return Era.STONE;
        }
    }

    public static int getTowerCost(TowerType type) {
        // Placeholder costs - NEED BALANCING!
        switch (type) {
            // Stone
            case ROCK_TOWER: return 100;
            case EGG_LAUNCHER: return 150;
            case WATCHTOWER: return 120;
            // Medieval
            case ARROW_TOWER: return 200;
            case CATAPULT_TOWER: return 350;
            case BALLISTA_TOWER: return 400;
            // Industrial
            case GATLING_TOWER: return 300;
            case CANNON_TOWER: return 500;
            case MORTAR_TOWER: return 450;
            // Modern
            case MACHINEGUN_TURRET: return 400;
            case MISSILE_TURRET: return 700;
            case TESLA_COIL: return 600;
            // Future
            case LASER_TURRET: return 600;
            case PLASMA_TURRET: return 900;
            case RAILGUN_TURRET: return 1200;
            default: return 150;
        }
    }

    public static int getTowerHealth(TowerType type) {
        // Placeholder HP - NEED BALANCING!
        return getTowerCost(type) * 3; // Simple relation to cost
    }

    public static int getTowerDamage(TowerType type) {
        // Placeholder Damage - NEED BALANCING!
        switch (type) {
            case ROCK_TOWER: return 15;
            case EGG_LAUNCHER: return 25; // AoE?
            case WATCHTOWER: return 12; // Faster?
            case ARROW_TOWER: return 25;
            case CATAPULT_TOWER: return 60; // AoE
            case BALLISTA_TOWER: return 80; // Single target high
            case GATLING_TOWER: return 10; // Fast low
            case CANNON_TOWER: return 100; // High AoE
            case MORTAR_TOWER: return 70; // AoE
            case MACHINEGUN_TURRET: return 20; // Fast
            case MISSILE_TURRET: return 150; // Slow high
            case TESLA_COIL: return 40; // Chain/AoE?
            case LASER_TURRET: return 50; // High DPS?
            case PLASMA_TURRET: return 200; // Slow High AoE
            case RAILGUN_TURRET: return 300; // Very Slow High Single
            default: return 20;
        }
    }

    public static float getTowerAttackSpeed(TowerType type) {
        // Attacks per second - NEED BALANCING!
        switch (type) {
            case ROCK_TOWER: return 1.0f;
            case EGG_LAUNCHER: return 0.5f; // Slow AoE
            case WATCHTOWER: return 1.5f; // Faster basic
            case ARROW_TOWER: return 1.2f;
            case CATAPULT_TOWER: return 0.4f; // Slow AoE
            case BALLISTA_TOWER: return 0.3f; // Very slow single
            case GATLING_TOWER: return 4.0f; // Very fast
            case CANNON_TOWER: return 0.5f; // Slow AoE
            case MORTAR_TOWER: return 0.6f; // Slow AoE
            case MACHINEGUN_TURRET: return 3.0f; // Fast
            case MISSILE_TURRET: return 0.4f; // Slow high damage
            case TESLA_COIL: return 1.0f; // Moderate AoE/Chain?
            case LASER_TURRET: return 2.0f; // Represents high DPS
            case PLASMA_TURRET: return 0.3f; // Slow AoE
            case RAILGUN_TURRET: return 0.2f; // Very slow single
            default: return 1.0f;
        }
    }

    public static float getTowerRange(TowerType type) {
        // Placeholder Range - NEED BALANCING!
        switch (type) {
            case ROCK_TOWER: return 200f;
            case EGG_LAUNCHER: return 250f;
            case WATCHTOWER: return 220f;
            case ARROW_TOWER: return 250f;
            case CATAPULT_TOWER: return 350f; // Long range AoE
            case BALLISTA_TOWER: return 400f; // Long range single
            case GATLING_TOWER: return 180f; // Shorter range fast
            case CANNON_TOWER: return 300f;
            case MORTAR_TOWER: return 400f; // Long range indirect fire
            case MACHINEGUN_TURRET: return 220f;
            case MISSILE_TURRET: return 450f; // Very long range
            case TESLA_COIL: return 150f; // Short range AoE/Chain
            case LASER_TURRET: return 280f;
            case PLASMA_TURRET: return 350f;
            case RAILGUN_TURRET: return 500f; // Longest range
            default: return 200f;
        }
    }

    public static int getTowerXpReward(TowerType type) {
        // XP gained when this tower is destroyed - NEED BALANCING!
        return getTowerCost(type) / 4; // Simple starting point
    }

    public static float getTowerWidth(TowerType type) {
        // Can customize size per tower type later
        return DEFAULT_TOWER_WIDTH;
    }
    public static float getTowerHeight(TowerType type) {
        if (type == TowerType.BALLISTA_TOWER || type == TowerType.RAILGUN_TURRET) return DEFAULT_TOWER_HEIGHT * 1.3f; // Taller towers
        return DEFAULT_TOWER_HEIGHT;
    }


    // --- Helper Methods ---

    private static final Array<UnitType> stoneUnits = Array.with(UnitType.CAVEMAN, UnitType.SLINGERMAN, UnitType.DINORIDER);
    private static final Array<UnitType> medievalUnits = Array.with(UnitType.SWORDSMAN, UnitType.ARCHER, UnitType.KNIGHT);
    private static final Array<UnitType> industrialUnits = Array.with(UnitType.RIFLEMAN, UnitType.CANNON, UnitType.GRENADIER);
    private static final Array<UnitType> modernUnits = Array.with(UnitType.MARINE, UnitType.TANK, UnitType.HELICOPTER); // HELICOPTER -> ROCKET_LAUNCHER
    private static final Array<UnitType> futureUnits = Array.with(UnitType.LASER_TROOPER, UnitType.MECH_WARRIOR, UnitType.CYBORG);

    public static Array<UnitType> getUnitsForEra(Era era) {
        switch (era) {
            case STONE: return stoneUnits;
            case MEDIEVAL: return medievalUnits;
            case INDUSTRIAL: return industrialUnits;
            case MODERN: return modernUnits;
            case FUTURE: return futureUnits;
            default: return new Array<>(); // Empty
        }
    }

    // Get a random unit the AI can build in its current era
    public static UnitType getRandomUnitForEra(Era era) {
        Array<UnitType> availableUnits = getUnitsForEra(era);
        if (availableUnits.size == 0) return null;
        return availableUnits.random(); // Get a random unit from the list
    }


    private static final Array<TowerType> stoneTowers = Array.with(TowerType.ROCK_TOWER, TowerType.EGG_LAUNCHER, TowerType.WATCHTOWER);
    private static final Array<TowerType> medievalTowers = Array.with(TowerType.ARROW_TOWER, TowerType.CATAPULT_TOWER, TowerType.BALLISTA_TOWER);
    private static final Array<TowerType> industrialTowers = Array.with(TowerType.GATLING_TOWER, TowerType.CANNON_TOWER, TowerType.MORTAR_TOWER);
    private static final Array<TowerType> modernTowers = Array.with(TowerType.MACHINEGUN_TURRET, TowerType.MISSILE_TURRET, TowerType.TESLA_COIL);
    private static final Array<TowerType> futureTowers = Array.with(TowerType.LASER_TURRET, TowerType.PLASMA_TURRET, TowerType.RAILGUN_TURRET);

    public static Array<TowerType> getTowersForEra(Era era) {
        switch (era) {
            case STONE: return stoneTowers;
            case MEDIEVAL: return medievalTowers;
            case INDUSTRIAL: return industrialTowers;
            case MODERN: return modernTowers;
            case FUTURE: return futureTowers;
            default: return new Array<>(); // Empty
        }
    }
}
