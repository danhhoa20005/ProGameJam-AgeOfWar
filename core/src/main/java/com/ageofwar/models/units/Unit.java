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

    private static Map<UnitState, TextureRegion[]> regionCache = new HashMap<>();

    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> deathAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> walkAnimation;

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
        attackAnimation = createAnimation(attackSheet, frameDuration, UnitState.ATTACK);
        deathAnimation  = createAnimation(deathSheet,  frameDuration, UnitState.DEATH);
        hurtAnimation   = createAnimation(hurtSheet,   frameDuration, UnitState.HURT);
        idleAnimation   = createAnimation(idleSheet,   frameDuration, UnitState.IDLE);
        walkAnimation   = createAnimation(walkSheet,   frameDuration, UnitState.WALK);

        Gdx.app.debug("Unit Init", owner + " " + unitType + " initialized. Speed: " + spd);
    }

    private Animation<TextureRegion> createAnimation(Texture sheet, float delta, UnitState state) {
        if (!regionCache.containsKey(state)) {
            TextureRegion[][] tmp = TextureRegion.split(sheet, 64, 64);
            regionCache.put(state,
                tmp[asset.getAnimationRow(playerType, unitType, state) - 1]
            );
        }
        return new Animation<>(delta, regionCache.get(state));
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        // currentState chỉ đổi khi logic world/CombatSystem gọi setCurrentState(...)
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
        // Chỉ reset trạng thái nội bộ, không dispose texture
        super.reset();
        unitType = null;
        moveSpeed = 0;
        moving = false;
        facingRight = true;
    }

    // Getters và setters khác...

    public UnitType getType() { return unitType; }
    public float getMoveSpeed() { return moveSpeed; }
    public boolean isMoving() { return moving; }
    public Animation<TextureRegion> getCurrentAnimation() {
        switch (currentState) {
            case ATTACK: return attackAnimation;
            case WALK:   return walkAnimation;
            case HURT:   return hurtAnimation;
            case DEATH:  return deathAnimation;
            default:     return idleAnimation;
        }
    }
    public UnitState getCurrentState() { return currentState; }

    public void setMoving(boolean moving) { this.moving = moving; }
    public void setCurrentState(UnitState currentState) { this.currentState = currentState; }
}
