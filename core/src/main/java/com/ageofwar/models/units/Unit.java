package com.ageofwar.models.units;

import com.ageofwar.models.Entity;
import com.ageofwar.models.players.PlayerType;
import com.badlogic.gdx.Gdx;
import com.ageofwar.configs.UnitConfig; // To get dimensions
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

public class Unit extends Entity {
    private UnitType unitType;
    private UnitState currentState;
    private float moveSpeed;
    private boolean moving; // For animation state

    PlayerType playerType = this.getOwnerType();
    UnitAsset asset = new UnitAsset();

    private Texture attackSheet = new Texture(asset.getAssetLink(unitType, UnitState.ATTACK));
    private Texture deathSheet = new Texture(asset.getAssetLink(unitType, UnitState.DEATH));
    private Texture hurtSheet = new Texture(asset.getAssetLink(unitType, UnitState.HURT));
    private Texture idleSheet = new Texture(asset.getAssetLink(unitType, UnitState.IDLE));
    private Texture walkSheet = new Texture(asset.getAssetLink(unitType, UnitState.WALK));

    private static Map<UnitState, TextureRegion[]> regionCache = new HashMap<>();

    private Animation<TextureRegion> createAnimation(Texture sheet, float delta, UnitState state) {
        if (!regionCache.containsKey(state)) {
            TextureRegion[][] tmp = TextureRegion.split(sheet, 64, 64);
            regionCache.put(state, tmp[asset.getAnimationRow(playerType, unitType, state) - 1]);
        }
        return new Animation<>(delta, regionCache.get(state));
    }

    private Animation<TextureRegion> attackAnimation = createAnimation(attackSheet, 0.1f, UnitState.ATTACK);
    private Animation<TextureRegion> deathkAnimation = createAnimation(deathSheet, 0.1f, UnitState.DEATH);
    private Animation<TextureRegion> hurtAnimation = createAnimation(hurtSheet, 0.1f, UnitState.HURT);
    private Animation<TextureRegion> idlekAnimation = createAnimation(idleSheet, 0.1f, UnitState.IDLE);
    private Animation<TextureRegion> walkAnimation = createAnimation(walkSheet, 0.1f, UnitState.WALK);

    public Unit() {
        super(); // Call Entity constructor
        moving = false;
    }

    public void init(UnitType unitType, PlayerType owner, int maxHp, int dmg, float atkSpd, float rng, float spd, float x, float y) {
        this.unitType = unitType;
        this.moveSpeed = spd;
        this.moving = false; // Start stationary
        // Get dimensions from config based on type
        float width = UnitConfig.getUnitWidth(unitType);
        float height = UnitConfig.getUnitHeight(unitType);
        super.initBase(owner, maxHp, dmg, atkSpd, rng, x, y, width, height);
        Gdx.app.debug("Unit Init", owner + " " + unitType + " initialized. Speed: " + spd);
    }


    @Override
    public void update(float delta) {
        super.update(delta); // Update attack cooldown
        // Movement is handled by World based on target/state
        switch (currentState) {
            case IDLE:
                //...//update IDLE state
                break;
            case WALK:
                //...//update WALK state
                break;
            case ATTACK:
                //...//update ATTACK state
                break;
            case DEATH:
                this.reset();//remove from world
                break;
        }
    }

    public void move(float deltaX) {
        if (!alive) return;
        position.x += deltaX;
        // Update bounds position
        bounds.x = position.x - bounds.width / 2;
    }


    // Getters
    public UnitType getType() { return unitType; }
    public float getMoveSpeed() { return moveSpeed; }
    public boolean isMoving() { return moving; }


    public Animation<TextureRegion> getCurrentAnimation() {
        UnitState state = this.getCurrentState();
        switch (state) {
            case ATTACK: return attackAnimation;
            case WALK: return walkAnimation;
            case HURT: return hurtAnimation;
            case DEATH: return deathkAnimation;
            default: return idlekAnimation;
        }

    }
    public UnitState getCurrentState() { return currentState; }

    // Setters
    public void setMoving(boolean moving) { this.moving = moving; }
    public void setCurrentState(UnitState currentState) { this.currentState = currentState; }

    @Override
    public void reset() {
        super.reset(); // Reset Entity fields
        unitType = null;
        moveSpeed = 0;
        moving = false;
    }
}
