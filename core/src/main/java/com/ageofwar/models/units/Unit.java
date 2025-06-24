package com.ageofwar.models.units;

import com.ageofwar.models.Entity;
import com.ageofwar.models.players.PlayerType;
import com.badlogic.gdx.Gdx;
import com.ageofwar.configs.UnitConfig;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.HashMap;
import java.util.Map;

/**
 * Một Unit chịu trách nhiệm vẽ animation cho từng trạng thái.
 * Các Texture được tạo riêng cho mỗi Unit trong init(), và chỉ dispose
 * từng Texture khi toàn bộ GameModel dispose(), không dispose trong reset().
 */
public class Unit extends Entity {
    private UnitType unitType;
    private UnitState currentState;
    private float moveSpeed;
    private boolean moving;
    private boolean facingRight = true;

    private PlayerType playerType;
    private UnitAsset asset;

    private Texture attackSheet;
    private Texture deathSheet;
    private Texture hurtSheet;
    private Texture idleSheet;
    private Texture walkSheet;

    // Cache animations by unit type and state to prevent sharing across different unit types
    private static Map<String, TextureRegion[]> regionCache = new HashMap<>();

    private Animation<TextureRegion> attackAnimationRight;
    private Animation<TextureRegion> attackAnimationLeft;
    private Animation<TextureRegion> deathAnimationRight;
    private Animation<TextureRegion> deathAnimationLeft;
    private Animation<TextureRegion> hurtAnimationRight;
    private Animation<TextureRegion> hurtAnimationLeft;
    private Animation<TextureRegion> idleAnimationRight;
    private Animation<TextureRegion> idleAnimationLeft;
    private Animation<TextureRegion> walkAnimationRight;
    private Animation<TextureRegion> walkAnimationLeft;

    public Unit() {
        super();
        this.currentState = UnitState.IDLE;
        this.moving = false;
    }

    public void init(UnitType unitType,
                     PlayerType owner,
                     int maxHp,
                     int dmg,
                     float atkSpd,
                     float rng,
                     float spd,
                     float x,
                     float y) {
        this.unitType = unitType;
        this.moveSpeed = spd;
        this.moving = false;
        this.currentState = UnitState.IDLE;

        super.initBase(owner, maxHp, dmg, atkSpd, rng, x, y,
            UnitConfig.getUnitWidth(unitType),
            UnitConfig.getUnitHeight(unitType));

        this.playerType = this.getOwnerType();
        this.asset = new UnitAsset();

        // Tạo và cache animation frames
        attackSheet = new Texture(asset.getAssetLink(unitType, UnitState.ATTACK));
        deathSheet  = new Texture(asset.getAssetLink(unitType, UnitState.DEATH));
        hurtSheet   = new Texture(asset.getAssetLink(unitType, UnitState.HURT));
        idleSheet   = new Texture(asset.getAssetLink(unitType, UnitState.IDLE));
        walkSheet   = new Texture(asset.getAssetLink(unitType, UnitState.WALK));

        float frameDuration = 0.1f;
        attackAnimationRight = createAnimation(attackSheet, frameDuration, UnitState.ATTACK, true);
        attackAnimationLeft  = createAnimation(attackSheet, frameDuration, UnitState.ATTACK, false);
        deathAnimationRight  = createAnimation(deathSheet,  frameDuration, UnitState.DEATH, true);
        deathAnimationLeft   = createAnimation(deathSheet,  frameDuration, UnitState.DEATH, false);
        hurtAnimationRight   = createAnimation(hurtSheet,   frameDuration, UnitState.HURT, true);
        hurtAnimationLeft    = createAnimation(hurtSheet,   frameDuration, UnitState.HURT, false);
        idleAnimationRight   = createAnimation(idleSheet,   frameDuration, UnitState.IDLE, true);
        idleAnimationLeft    = createAnimation(idleSheet,   frameDuration, UnitState.IDLE, false);
        walkAnimationRight   = createAnimation(walkSheet,   frameDuration, UnitState.WALK, true);
        walkAnimationLeft    = createAnimation(walkSheet,   frameDuration, UnitState.WALK, false);

        Gdx.app.debug("Unit Init", owner + " " + unitType + " initialized. Speed: " + spd);
    }

    private Animation<TextureRegion> createAnimation(Texture sheet, float delta, UnitState state, boolean facingRight) {
        // Build a unique cache key per unit type, state, owner and facing
        String cacheKey = unitType.name() + "_" + state.name() + "_" + playerType.name() + "_" + (facingRight ? "R" : "L");
        if (!regionCache.containsKey(cacheKey)) {
            TextureRegion[][] tmp = TextureRegion.split(sheet, 64, 64);
            TextureRegion[] regions = tmp[facingRight ? 3 : 2];
            regionCache.put(cacheKey, regions);
        }
        return new Animation<>(delta, regionCache.get(cacheKey));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
    }

    public void move(float deltaX) {
        if (!alive) return;
        if (deltaX > 0) facingRight = true;
        else if (deltaX < 0) facingRight = false;
        position.x += deltaX;
        bounds.x = position.x - bounds.width / 2;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    @Override
    public void reset() {
        super.reset();
        unitType = null;
        moveSpeed = 0;
        moving = false;
        facingRight = true;
    }


    public UnitType getType() { return unitType; }
    public float getMoveSpeed() { return moveSpeed; }
    public boolean isMoving() { return moving; }
    public Animation<TextureRegion> getCurrentAnimation() {
        switch (currentState) {
            case ATTACK: return facingRight ? attackAnimationRight : attackAnimationLeft;
            case WALK:   return facingRight ? walkAnimationRight   : walkAnimationLeft;
            case HURT:   return facingRight ? hurtAnimationRight   : hurtAnimationLeft;
            case DEATH:  return facingRight ? deathAnimationRight  : deathAnimationLeft;
            default:     return facingRight ? idleAnimationRight   : idleAnimationLeft;
        }
    }
    public UnitState getCurrentState() { return currentState; }

    public void setMoving(boolean moving) { this.moving = moving; }
    public void setCurrentState(UnitState currentState) { this.currentState = currentState; }
}
