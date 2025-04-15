package com.ageofwar.configs;

import com.ageofwar.models.Era;
import com.ageofwar.screens.GameScreen;

public class GameConfig {

    // --- Base Stats ---
    public static final int BASE_START_HP_PLAYER = 2000;
    public static final int BASE_START_HP_AI = 2000; // Adjust for difficulty later
    public static final int BASE_HP_UPGRADE_BONUS = 500; // HP gained per era upgrade

    // --- Starting Conditions ---
    public static final int STARTING_GOLD = 500;
    public static final int STARTING_XP = 0;

    // --- Economy ---
    public static final float GOLD_GENERATION_INTERVAL = 1.0f; // seconds
    public static final int GOLD_PER_INTERVAL = 25;

    // --- Era Progression ---
    public static int getEraUpgradeCost(Era currentEra) {
        switch (currentEra) {
            case STONE: return 500; // XP to reach Medieval
            case MEDIEVAL: return 1500; // XP to reach Industrial
            case INDUSTRIAL: return 3000; // XP to reach Modern
            case MODERN: return 5000; // XP to reach Future
            case FUTURE: return -1; // Cannot upgrade further
            default: return -1;
        }
    }

    // --- Tower Limits ---
    public static int getMaxTowersForEra(Era currentEra) {
        // Example: Allow more towers in later eras
        switch (currentEra) {
            case STONE: return 3;
            case MEDIEVAL: return 4;
            case INDUSTRIAL: return 4;
            case MODERN: return 5;
            case FUTURE: return 5;
            default: return 3;
        }
    }


    // --- Special Abilities ---
    public static int getSpecialAbilityCost(Era currentEra) {
        // Example costs (in Gold)
        switch (currentEra) {
            case STONE: return 500;
            case MEDIEVAL: return 1000;
            case INDUSTRIAL: return 1500;
            case MODERN: return 2000;
            case FUTURE: return 3000;
            default: return 9999;
        }
    }

    public static float getSpecialAbilityCooldown(Era currentEra) {
        // Example cooldowns (in seconds)
        switch (currentEra) {
            case STONE: return 60f;
            case MEDIEVAL: return 70f;
            case INDUSTRIAL: return 80f;
            case MODERN: return 90f;
            case FUTURE: return 100f;
            default: return 60f;
        }
    }

    // Special Ability Effects (Damage values - balance needed!)
    public static final int STONE_SPECIAL_DAMAGE = 300;
    public static final int MEDIEVAL_SPECIAL_DAMAGE = 150; // Per target
    public static final int MEDIEVAL_SPECIAL_TARGETS = 5;
    public static final int INDUSTRIAL_SPECIAL_DAMAGE = 500; // Area damage
    public static final int MODERN_SPECIAL_DAMAGE = 800; // Area damage
    public static final int FUTURE_SPECIAL_DAMAGE = 1500; // Area damage


    // --- AI Settings ---
    public static final float AI_SPAWN_INTERVAL = 3.0f; // How often AI tries to spawn units (adjust for difficulty)

    // --- World Layout ---
    public static final float GROUND_Y = 50; // Y-coordinate of the ground level
    public static final float PLAYER_BASE_X = 100; // Center X of player base
    public static final float AI_BASE_X = GameScreen.WORLD_WIDTH - 100; // Center X of AI base
    public static final float PLAYER_SPAWN_X = PLAYER_BASE_X + 60; // Where player units appear
    public static final float AI_SPAWN_X = AI_BASE_X - 60; // Where AI units appear

}
