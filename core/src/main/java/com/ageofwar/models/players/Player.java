package com.ageofwar.models.players;

import com.ageofwar.models.Era;
import com.badlogic.gdx.Gdx;
import com.ageofwar.configs.GameConfig;

public class Player {
    private PlayerType type;
    private int gold;
    private int experience;
    private int baseHealth;
    private int maxBaseHealth; // Store max health for UI display
    private Era currentEra;
    private float basePositionX; // X-coordinate of the base

    // Cooldown for special ability
    private float specialCooldownTimer = 0f;
    private boolean specialReady = true;

    public Player(PlayerType type, int startGold, int startExp, int startHealth, float baseX) {
        this.type = type;
        this.gold = startGold;
        this.experience = startExp;
        this.maxBaseHealth = startHealth;
        this.baseHealth = startHealth;
        this.currentEra = Era.STONE; // Start at Stone Age
        this.basePositionX = baseX;
        this.specialReady = true;
        this.specialCooldownTimer = 0f;
        Gdx.app.log("Player", type + " created. HP: " + baseHealth + ", Gold: " + gold + ", Era: " + currentEra);
    }

    public void update(float delta) {
        // Update cooldown timer
        if (!specialReady) {
            specialCooldownTimer -= delta;
            if (specialCooldownTimer <= 0) {
                specialReady = true;
                Gdx.app.debug("Player " + type, "Special ability ready.");
            }
        }
    }


    public void addGold(int amount) {
        this.gold += amount;
        if (this.gold < 0) this.gold = 0; // Prevent negative gold
    }

    public void addExperience(int amount) {
        this.experience += amount;
        if (this.experience < 0) this.experience = 0; // Prevent negative XP
    }

    public void takeDamage(int amount) {
        this.baseHealth -= amount;
        if (this.baseHealth < 0) {
            this.baseHealth = 0;
        }
        Gdx.app.debug("Player " + type, "Base took " + amount + " damage. HP left: " + baseHealth);
    }

    public void useSpecial() {
        if (specialReady) {
            specialReady = false;
            specialCooldownTimer = GameConfig.getSpecialAbilityCooldown(currentEra);
            Gdx.app.debug("Player " + type, "Used special. Cooldown started: " + specialCooldownTimer + "s");
        }
    }

    // Getters
    public PlayerType getType() { return type; }
    public int getGold() { return gold; }
    public int getExperience() { return experience; }
    public int getBaseHealth() { return baseHealth; }
    public int getMaxBaseHealth() { return maxBaseHealth; }
    public Era getCurrentEra() { return currentEra; }
    public float getBasePositionX() { return basePositionX; }
    public boolean canUseSpecial() { return specialReady; }
    public float getSpecialCooldownTimer() { return specialCooldownTimer; }
    public float getSpecialCooldownPercent() {
        if (specialReady) return 1f;
        float totalCooldown = GameConfig.getSpecialAbilityCooldown(currentEra);
        if (totalCooldown <= 0) return 1f; // Avoid division by zero
        return 1.0f - (specialCooldownTimer / totalCooldown);
    }

    // Setters
    public void setCurrentEra(Era era) {
        this.currentEra = era;
        // Reset special cooldown when changing era? Optional.
        // specialReady = true;
        // specialCooldownTimer = 0;
    }
    public void setBaseHealth(int health) {
        this.baseHealth = Math.max(0, health); // Ensure health doesn't go below 0
        // Optionally update max health if upgrades increase it permanently
        // this.maxBaseHealth = Math.max(this.maxBaseHealth, this.baseHealth);
    }
}
