package com.ageofwar.models;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

// Base class for Units and Towers
public abstract class Entity implements Poolable {
    protected Vector2 position;
    protected Rectangle bounds; // For rendering and simple collision/interaction
    protected int health;
    protected int maxHealth;
    protected int damage;
    protected float attackSpeed; // Attacks per second
    protected float attackCooldown; // Timer for attack readiness
    protected float range;
    protected boolean alive;
    protected PlayerType ownerType;
    protected Entity target; // Current target (can be null)

    public Entity() {
        position = new Vector2();
        bounds = new Rectangle();
        alive = false; // Initially not alive until initialized
    }

    // Common initialization logic
    protected void initBase(PlayerType owner, int maxHp, int dmg, float atkSpd, float rng, float x, float y, float width, float height) {
        this.ownerType = owner;
        this.maxHealth = maxHp;
        this.health = maxHp;
        this.damage = dmg;
        this.attackSpeed = atkSpd;
        this.range = rng;
        this.position.set(x, y);
        this.bounds.set(x - width / 2, y, width, height); // Centered horizontally, base at y
        this.alive = true;
        this.attackCooldown = 0f; // Ready to attack initially
        this.target = null;
        // Gdx.app.debug("Entity Init", this.getClass().getSimpleName() + " initialized at (" + x + "," + y + ") HP:" + health);
    }


    public void update(float delta) {
        if (!alive) return;

        // Update attack cooldown
        if (attackCooldown > 0) {
            attackCooldown -= delta;
        }
    }

    public void takeDamage(int amount) {
        if (!alive) return;
        this.health -= amount;
        // Gdx.app.debug("Entity", this.getClass().getSimpleName() + " took " + amount + " damage. HP left: " + health);
        if (this.health <= 0) {
            this.health = 0;
            this.alive = false;
            // Gdx.app.debug("Entity", this.getClass().getSimpleName() + " died.");
            // Trigger death animation/sound?
        }
    }

    public void setPosition(float x, float y) {
        this.position.set(x, y);
        this.bounds.setPosition(x - bounds.width / 2, y);
    }


    public boolean canAttack() {
        return alive && attackCooldown <= 0;
    }

    public void resetAttackCooldown() {
        if (attackSpeed > 0) {
            this.attackCooldown = 1f / attackSpeed;
        } else {
            this.attackCooldown = Float.MAX_VALUE; // Cannot attack if speed is 0 or less
        }
    }

    // Getters
    public float getX() { return position.x; }
    public float getY() { return position.y; }
    public float getWidth() { return bounds.width; }
    public float getHeight() { return bounds.height; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getDamage() { return damage; }
    public float getRange() { return range; }
    public boolean isAlive() { return alive; }
    public PlayerType getOwnerType() { return ownerType; }
    public Rectangle getBounds() { return bounds; }
    public Entity getTarget() { return target; }

    // Setters
    public void setTarget(Entity target) { this.target = target; }


    @Override
    public void reset() {
        // Reset state when obtained from pool or freed
        position.setZero();
        bounds.set(0,0,0,0);
        health = 0;
        maxHealth = 0;
        damage = 0;
        attackSpeed = 0;
        attackCooldown = 0;
        range = 0;
        alive = false;
        ownerType = null;
        target = null;
    }
}
