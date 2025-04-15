package com.ageofwar.models.units;

import com.ageofwar.models.Entity;
import com.ageofwar.models.players.PlayerType;
import com.badlogic.gdx.Gdx;
import com.ageofwar.configs.UnitConfig; // To get dimensions

public class Unit extends Entity {
    private UnitType type;
    private float moveSpeed;
    private boolean moving; // For animation state

    public Unit() {
        super(); // Call Entity constructor
        moving = false;
    }

    public void init(UnitType type, PlayerType owner, int maxHp, int dmg, float atkSpd, float rng, float spd, float x, float y) {
        this.type = type;
        this.moveSpeed = spd;
        this.moving = false; // Start stationary
        // Get dimensions from config based on type
        float width = UnitConfig.getUnitWidth(type);
        float height = UnitConfig.getUnitHeight(type);
        super.initBase(owner, maxHp, dmg, atkSpd, rng, x, y, width, height);
        Gdx.app.debug("Unit Init", owner + " " + type + " initialized. Speed: " + spd);
    }


    @Override
    public void update(float delta) {
        super.update(delta); // Update attack cooldown
        // Movement is handled by World based on target/state
    }

    public void move(float deltaX) {
        if (!alive) return;
        position.x += deltaX;
        // Update bounds position
        bounds.x = position.x - bounds.width / 2;
    }


    // Getters
    public UnitType getType() { return type; }
    public float getMoveSpeed() { return moveSpeed; }
    public boolean isMoving() { return moving; }


    // Setters
    public void setMoving(boolean moving) { this.moving = moving; }


    @Override
    public void reset() {
        super.reset(); // Reset Entity fields
        type = null;
        moveSpeed = 0;
        moving = false;
    }
}
