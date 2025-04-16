package com.ageofwar.models;

import com.badlogic.gdx.Gdx;
import com.ageofwar.configs.UnitConfig; // To get dimensions

public class Tower extends Entity {
    private TowerType type;

    public Tower() {
        super();
    }

    public void init(TowerType type, PlayerType owner, int maxHp, int dmg, float atkSpd, float rng, float x, float y) {
        this.type = type;
        // Get dimensions from config based on type
        float width = UnitConfig.getTowerWidth(type);
        float height = UnitConfig.getTowerHeight(type);
        super.initBase(owner, maxHp, dmg, atkSpd, rng, x, y, width, height);
        Gdx.app.debug("Tower Init", owner + " " + type + " initialized at " + x);
    }

    // Towers don't move, so update might only involve cooldown or animations
    @Override
    public void update(float delta) {
        super.update(delta); // Updates attack cooldown
    }

    // Getters
    public TowerType getType() { return type; }

    @Override
    public void reset() {
        super.reset();
        type = null;
    }
}
