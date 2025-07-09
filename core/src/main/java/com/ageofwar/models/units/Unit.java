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

    // Timer để quản lý thời gian hiển thị animation tấn công
    private float attackAnimationTimer = 0f;
    private static final float ATTACK_ANIMATION_DURATION = 0.6f; // 600ms để hiển thị animation tấn công

    // Timer để quản lý thời gian hiển thị animation hurt và death
    private float hurtAnimationTimer = 0f;
    private static final float HURT_ANIMATION_DURATION = 0.4f; // 400ms để hiển thị animation hurt

    private float deathAnimationTimer = 0f;
    private static final float DEATH_ANIMATION_DURATION = 1.0f; // 1000ms để hiển thị animation death
    private boolean deathAnimationCompleted = false;

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
        // Both Left and Right animations now use the same row based on playerType
        // This ensures units always display the correct facing for their side
        attackAnimationRight = createAnimation(attackSheet, frameDuration, UnitState.ATTACK, true);
        attackAnimationLeft  = createAnimation(attackSheet, frameDuration, UnitState.ATTACK, true); // Same row as Right
        deathAnimationRight  = createAnimation(deathSheet,  frameDuration, UnitState.DEATH, true);
        deathAnimationLeft   = createAnimation(deathSheet,  frameDuration, UnitState.DEATH, true);  // Same row as Right
        hurtAnimationRight   = createAnimation(hurtSheet,   frameDuration, UnitState.HURT, true);
        hurtAnimationLeft    = createAnimation(hurtSheet,   frameDuration, UnitState.HURT, true);   // Same row as Right
        idleAnimationRight   = createAnimation(idleSheet,   frameDuration, UnitState.IDLE, true);
        idleAnimationLeft    = createAnimation(idleSheet,   frameDuration, UnitState.IDLE, true);   // Same row as Right
        walkAnimationRight   = createAnimation(walkSheet,   frameDuration, UnitState.WALK, true);
        walkAnimationLeft    = createAnimation(walkSheet,   frameDuration, UnitState.WALK, true);   // Same row as Right
        hurtAnimationLeft    = createAnimation(hurtSheet,   frameDuration, UnitState.HURT, true);   // Same row as Right
        idleAnimationRight   = createAnimation(idleSheet,   frameDuration, UnitState.IDLE, true);
        idleAnimationLeft    = createAnimation(idleSheet,   frameDuration, UnitState.IDLE, true);   // Same row as Right
        walkAnimationRight   = createAnimation(walkSheet,   frameDuration, UnitState.WALK, true);
        walkAnimationLeft    = createAnimation(walkSheet,   frameDuration, UnitState.WALK, true);   // Same row as Right

        Gdx.app.debug("Unit Init", owner + " " + unitType + " initialized. Speed: " + spd);
    }

    private Animation<TextureRegion> createAnimation(Texture sheet, float delta, UnitState state, boolean facingRight) {
        // Build a unique cache key per unit type, state, and owner
        // facingRight parameter is now ignored since we always use the same row for each playerType
        String cacheKey = unitType.name() + "_" + state.name() + "_" + playerType.name();
        if (!regionCache.containsKey(cacheKey)) {
            TextureRegion[][] tmp = TextureRegion.split(sheet, 64, 64);
            // Use the correct row based on playerType, not facingRight
            // Player units use row 3 (index 3), Enemy units use row 2 (index 2)
            int row = asset.getAnimationRow(playerType, unitType, state);
            TextureRegion[] regions = tmp[row];
            regionCache.put(cacheKey, regions);
        }
        return new Animation<>(delta, regionCache.get(cacheKey));
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        // Cập nhật timer animation death (ưu tiên cao nhất)
        if (deathAnimationTimer > 0) {
            deathAnimationTimer -= delta;
            if (deathAnimationTimer <= 0) {
                deathAnimationCompleted = true;
                // Giữ nguyên trạng thái DEATH sau khi animation hoàn thành
            }
            return; // Không xử lý các timer khác khi đang trong animation death
        }

        // Cập nhật timer animation hurt
        if (hurtAnimationTimer > 0) {
            hurtAnimationTimer -= delta;
            if (hurtAnimationTimer <= 0) {
                // Animation hurt đã hoàn thành, chuyển về trạng thái phù hợp
                if (moving) {
                    currentState = UnitState.WALK;
                } else {
                    currentState = UnitState.IDLE;
                }
            }
            return; // Không xử lý attack timer khi đang trong animation hurt
        }

        // Cập nhật timer animation tấn công
        if (attackAnimationTimer > 0) {
            attackAnimationTimer -= delta;
            if (attackAnimationTimer <= 0) {
                // Animation tấn công đã hoàn thành, chuyển về trạng thái phù hợp
                if (moving) {
                    currentState = UnitState.WALK;
                } else {
                    currentState = UnitState.IDLE;
                }
            }
        }
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
        attackAnimationTimer = 0f; // Reset attack animation timer
        hurtAnimationTimer = 0f; // Reset hurt animation timer
        deathAnimationTimer = 0f; // Reset death animation timer
        deathAnimationCompleted = false; // Reset death animation state
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

    /**
     * Bắt đầu animation tấn công với thời gian hiển thị cố định.
     */
    public void startAttackAnimation() {
        currentState = UnitState.ATTACK;
        attackAnimationTimer = ATTACK_ANIMATION_DURATION;
    }

    /**
     * Kiểm tra xem unit có đang trong animation tấn công không.
     */
    public boolean isInAttackAnimation() {
        return attackAnimationTimer > 0;
    }

    /**
     * Kiểm tra xem unit có đang trong animation hurt không.
     */
    public boolean isInHurtAnimation() {
        return hurtAnimationTimer > 0;
    }

    /**
     * Kiểm tra xem unit có đang trong animation death không.
     */
    public boolean isInDeathAnimation() {
        return deathAnimationTimer > 0;
    }

    /**
     * Kiểm tra xem animation death đã hoàn thành chưa.
     */
    public boolean isDeathAnimationCompleted() {
        return deathAnimationCompleted;
    }

    @Override
    public void takeDamage(int amount) {
        if (!alive) return; // Không nhận sát thương nếu đã chết
        if (deathAnimationCompleted) return; // Không nhận sát thương nếu animation death đã hoàn thành

        this.health -= amount;

        if (this.health <= 0) {
            this.health = 0;
            this.alive = false;
            // Kích hoạt animation death
            currentState = UnitState.DEATH;
            deathAnimationTimer = DEATH_ANIMATION_DURATION;
            deathAnimationCompleted = false;
            // Reset các timer khác
            attackAnimationTimer = 0f;
            hurtAnimationTimer = 0f;
        } else {
            // Nếu không chết, kích hoạt animation hurt (nếu không đang trong animation death)
            if (currentState != UnitState.DEATH && deathAnimationTimer <= 0) {
                currentState = UnitState.HURT;
                hurtAnimationTimer = HURT_ANIMATION_DURATION;
                // Reset attack animation timer khi bị hurt
                attackAnimationTimer = 0f;
            }
        }
    }
}
