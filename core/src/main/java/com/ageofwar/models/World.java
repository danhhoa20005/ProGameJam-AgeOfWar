package com.ageofwar.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.ageofwar.configs.GameConfig;
import com.ageofwar.configs.UnitConfig; // Added import
import com.ageofwar.screens.GameScreen; // Added import

public class World implements Disposable {
    private final Array<Unit> playerUnits;
    private final Array<Unit> aiUnits;
    private final Array<Tower> playerTowers;
    private final Array<Tower> aiTowers;
    // Add arrays for projectiles if implementing them

    // Temporary rectangle for collision checks to avoid allocations
    private final Rectangle tempRect1 = new Rectangle();
    private final Rectangle tempRect2 = new Rectangle();


    public World() {
        playerUnits = new Array<>();
        aiUnits = new Array<>();
        playerTowers = new Array<>();
        aiTowers = new Array<>();
    }

    public void addUnit(Unit unit) {
        if (unit.getOwnerType() == PlayerType.PLAYER) {
            playerUnits.add(unit);
        } else {
            aiUnits.add(unit);
        }
    }

    public void addTower(Tower tower) {
        if (tower.getOwnerType() == PlayerType.PLAYER) {
            playerTowers.add(tower);
        } else {
            aiTowers.add(tower);
        }
    }

    public void update(float delta, Player player, Player aiPlayer, Pool<Unit> unitPool, Pool<Tower> towerPool) {
        // 1. Update Player Units & Towers
        updateEntities(delta, playerUnits, aiUnits, aiTowers, aiPlayer, unitPool, player); // Player units attack AI stuff
        updateTowers(delta, playerTowers, aiUnits, unitPool, player); // Player towers attack AI units

        // 2. Update AI Units & Towers
        updateEntities(delta, aiUnits, playerUnits, playerTowers, player, unitPool, aiPlayer); // AI units attack Player stuff
        updateTowers(delta, aiTowers, playerUnits, unitPool, aiPlayer); // AI towers attack Player units

        // 3. Remove dead entities
        cleanupEntities(playerUnits, unitPool);
        cleanupEntities(aiUnits, unitPool);
        cleanupTowers(playerTowers, towerPool);
        cleanupTowers(aiTowers, towerPool);
    }

    // Generic update for units (handles movement, finding target, attacking)
    private void updateEntities(float delta, Array<Unit> units, Array<Unit> enemyUnits, Array<Tower> enemyTowers, Player enemyPlayer, Pool<Unit> pool, Player owner) {
        for (int i = units.size - 1; i >= 0; i--) {
            Unit unit = units.get(i);
            unit.update(delta); // Update internal state (timers, etc.)

            // --- Targeting Logic ---
            Entity target = findTarget(unit, enemyUnits, enemyTowers, enemyPlayer);
            unit.setTarget(target); // Can be null if no target in range

            // --- Movement Logic ---
            if (target != null) {
                // If target in range, stop and attack. Otherwise, move towards target/base.
                float distanceToTarget = Vector2.dst(unit.getX(), unit.getY(), target.getX(), target.getY());
                if (distanceToTarget <= unit.getRange()) {
                    unit.setMoving(false);
                    // Attack if cooldown ready
                    if (unit.canAttack()) {
                        attackTarget(unit, target, pool, owner); // Pass owner for XP gain
                    }
                } else {
                    unit.setMoving(true);
                    // Move towards the target's X position (simple horizontal movement)
                    float moveDirection = (target.getX() > unit.getX()) ? 1 : -1;
                    // Prevent moving past own base if no enemies present? (Optional refinement)
                    unit.move(moveDirection * unit.getMoveSpeed() * delta);

                }
            } else {
                // No target in range, move towards enemy base
                unit.setMoving(true);
                float enemyBaseX = (unit.getOwnerType() == PlayerType.PLAYER) ? GameConfig.AI_BASE_X : GameConfig.PLAYER_BASE_X;
                float moveDirection = (enemyBaseX > unit.getX()) ? 1 : -1;

                // Check if close to enemy base
                float distanceToBase = Math.abs(unit.getX() - enemyBaseX);
                if (distanceToBase <= unit.getRange()) { // Attack base if in range
                    unit.setMoving(false);
                    if (unit.canAttack()) {
                        attackBase(unit, enemyPlayer);
                    }
                } else { // Move towards base
                    unit.move(moveDirection * unit.getMoveSpeed() * delta);
                }
            }

            // Clamp unit position to world bounds (prevent going off-screen)
            // Ensure unit has width getter or use a default width
            float unitWidth = (unit instanceof Unit) ? UnitConfig.getUnitWidth(((Unit)unit).getType()) : 50f; // Example default
            unit.setPosition(MathUtils.clamp(unit.getX(), 0, GameScreen.WORLD_WIDTH - unitWidth), unit.getY());

        }
    }

    // Update logic specific to towers (cannot move, only find target and attack)
    private void updateTowers(float delta, Array<Tower> towers, Array<Unit> enemyUnits, Pool<Unit> enemyUnitPool, Player owner) {
        for (int i = towers.size - 1; i >= 0; i--) {
            Tower tower = towers.get(i);
            tower.update(delta); // Update attack cooldown

            // Find the closest enemy unit within range
            Unit target = findClosestUnitInRange(tower, enemyUnits);
            tower.setTarget(target); // Can be null

            if (target != null && tower.canAttack()) {
                attackTarget(tower, target, enemyUnitPool, owner);
            }
        }
    }


    // Finds the closest valid target (Unit or Tower or Base) for a given unit
    private Entity findTarget(Unit attacker, Array<Unit> enemyUnits, Array<Tower> enemyTowers, Player enemyPlayer) {
        Entity closestTarget = null;
        float minDistanceSq = Float.MAX_VALUE;

        float detectionRangeSq = attacker.getRange() * attacker.getRange() * 4; // Look a bit further than attack range to start moving
        if (attacker.getRange() < 50) detectionRangeSq = 50*50; // Minimum detection for melee


        // 1. Check Enemy Units
        for (Unit enemy : enemyUnits) {
            if (!enemy.isAlive()) continue;
            float distSq = Vector2.dst2(attacker.getX(), attacker.getY(), enemy.getX(), enemy.getY());
            if (distSq < minDistanceSq && distSq <= detectionRangeSq) {
                minDistanceSq = distSq;
                closestTarget = enemy;
            }
        }

        // 2. Check Enemy Towers
        for (Tower enemyTower : enemyTowers) {
            if (!enemyTower.isAlive()) continue;
            float distSq = Vector2.dst2(attacker.getX(), attacker.getY(), enemyTower.getX(), enemyTower.getY());
            if (distSq < minDistanceSq && distSq <= detectionRangeSq) {
                minDistanceSq = distSq;
                closestTarget = enemyTower;
            }
        }

        // 3. Check Enemy Base (only if no units/towers are closer or in immediate range)
        // Allow targeting base even if slightly outside normal range if nothing else is near
        float baseDistSq = Vector2.dst2(attacker.getX(), attacker.getY(), enemyPlayer.getBasePositionX(), GameConfig.GROUND_Y);
        if (closestTarget == null || minDistanceSq > attacker.getRange() * attacker.getRange()) { // If no close unit/tower found
            if (baseDistSq < minDistanceSq && baseDistSq <= detectionRangeSq * 1.5f) { // Slightly larger check radius for base
                // Create a temporary representation of the base as an Entity if needed
                // Or handle base targeting separately in the main update loop
                // For simplicity here, let's assume the main loop handles base attack if target is null
                // So, don't set the base as a target here, let the unit move towards it.
            }
        }


        return closestTarget; // Can be null
    }

    // Finds the closest enemy unit within a tower's attack range
    private Unit findClosestUnitInRange(Tower tower, Array<Unit> enemyUnits) {
        Unit closestTarget = null;
        float minDistanceSq = tower.getRange() * tower.getRange(); // Only check within actual attack range

        for (Unit enemy : enemyUnits) {
            if (!enemy.isAlive()) continue;
            float distSq = Vector2.dst2(tower.getX(), tower.getY(), enemy.getX(), enemy.getY());
            if (distSq <= minDistanceSq) {
                // Check if this enemy is closer than the current closest target
                if (closestTarget == null || distSq < Vector2.dst2(tower.getX(), tower.getY(), closestTarget.getX(), closestTarget.getY())) {
                    closestTarget = enemy;
                }
            }
        }
        return closestTarget;
    }


    private void attackTarget(Entity attacker, Entity target, Pool<?> targetPool, Player xpReceiver) {
        if (target != null && target.isAlive()) {
            target.takeDamage(attacker.getDamage());
            attacker.resetAttackCooldown(); // Reset cooldown after attacking
            // Gdx.app.debug("World Combat", attacker.getClass().getSimpleName() + " attacks " + target.getClass().getSimpleName() + " for " + attacker.getDamage() + " damage.");


            if (!target.isAlive()) {
                // Grant XP to the owner of the attacker
                int xpValue = (target instanceof Unit) ? UnitConfig.getUnitXpReward(((Unit)target).getType())
                    : UnitConfig.getTowerXpReward(((Tower)target).getType());
                xpReceiver.addExperience(xpValue);
                Gdx.app.debug("World Combat", xpReceiver.getType() + " gained " + xpValue + " XP for destroying " + target.getClass().getSimpleName());

                // Target died, attacker should look for a new target immediately
                attacker.setTarget(null);

                // Optional: Add visual/sound effect for death
            }
        }
    }

    private void attackBase(Unit attacker, Player enemyPlayer) {
        enemyPlayer.takeDamage(attacker.getDamage());
        attacker.resetAttackCooldown();
        Gdx.app.debug("World Combat", attacker.getOwnerType() + "'s " + attacker.getType() + " attacks " + enemyPlayer.getType() + " base for " + attacker.getDamage() + " damage.");
        // Check for game over is handled in GameModel
    }


    // Remove dead units and return them to the pool
    private void cleanupEntities(Array<Unit> units, Pool<Unit> pool) {
        for (int i = units.size - 1; i >= 0; i--) {
            Unit unit = units.get(i);
            if (!unit.isAlive()) {
                units.removeIndex(i);
                pool.free(unit);
            }
        }
    }

    // Remove dead towers and return them to the pool
    private void cleanupTowers(Array<Tower> towers, Pool<Tower> pool) {
        for (int i = towers.size - 1; i >= 0; i--) {
            Tower tower = towers.get(i);
            if (!tower.isAlive()) {
                towers.removeIndex(i);
                pool.free(tower);
            }
        }
    }

    // --- Special Ability Activation ---
    // MODIFIED: Added xpReceiver parameter
    public void activateSpecialAbility(PlayerType ownerType, Era era, Player targetPlayer, Pool<Unit> unitPool, Pool<Tower> towerPool, Player xpReceiver) {
        Gdx.app.log("World", ownerType + " activating special for Era: " + era);
        Array<Unit> targetUnits = (ownerType == PlayerType.PLAYER) ? aiUnits : playerUnits;
        Array<Tower> targetTowers = (ownerType == PlayerType.PLAYER) ? aiTowers : playerTowers;

        switch (era) {
            case STONE: // Meteor Shower (Damage Area)
                // MODIFIED: Pass xpReceiver
                applyAreaDamage(targetPlayer.getBasePositionX() - 200, 300, targetUnits, targetTowers, targetPlayer, GameConfig.STONE_SPECIAL_DAMAGE, unitPool, towerPool, xpReceiver);
                break;
            case MEDIEVAL: // Arrow Rain (Damage multiple units)
                // MODIFIED: Pass xpReceiver
                applyDamageToMultipleUnits(targetUnits, GameConfig.MEDIEVAL_SPECIAL_TARGETS, GameConfig.MEDIEVAL_SPECIAL_DAMAGE, unitPool, xpReceiver);
                break;
            case INDUSTRIAL: // Defensive Stance (Temporary invulnerability/shield for own units?) - NEEDS CLARIFICATION
                // Or maybe Artillery Barrage (Area Damage like Stone Age?)
                // MODIFIED: Pass xpReceiver
                applyAreaDamage(targetPlayer.getBasePositionX() - 250, 400, targetUnits, targetTowers, targetPlayer, GameConfig.INDUSTRIAL_SPECIAL_DAMAGE, unitPool, towerPool, xpReceiver);
                Gdx.app.log("World", "Industrial Special (Artillery Barrage) activated.");
                break;
            case MODERN: // Airstrike (High damage line or area)
                // MODIFIED: Pass xpReceiver
                applyAreaDamage(targetPlayer.getBasePositionX() - 300, 150, targetUnits, targetTowers, targetPlayer, GameConfig.MODERN_SPECIAL_DAMAGE, unitPool, towerPool, xpReceiver);
                break;
            case FUTURE: // Orbital Strike (Massive damage area)
                // MODIFIED: Pass xpReceiver
                applyAreaDamage(targetPlayer.getBasePositionX() - 350, 500, targetUnits, targetTowers, targetPlayer, GameConfig.FUTURE_SPECIAL_DAMAGE, unitPool, towerPool, xpReceiver);
                break;
        }
        // Add visual effects trigger here
    }

    // Helper for area damage specials
    private void applyAreaDamage(float targetCenterX, float areaWidth, Array<Unit> units, Array<Tower> towers, Player baseOwner, int damage, Pool<Unit> unitPool, Pool<Tower> towerPool, Player xpReceiver) {
        float startX = targetCenterX - areaWidth / 2f;
        float endX = targetCenterX + areaWidth / 2f;
        Gdx.app.debug("World Special", "Applying area damage [" + startX + " - " + endX + "] for " + damage + " dmg.");


        // Damage units in area
        for (int i = units.size - 1; i >= 0; i--) {
            Unit unit = units.get(i);
            if (unit.getX() >= startX && unit.getX() <= endX) {
                unit.takeDamage(damage);
                if (!unit.isAlive()) {
                    int xpValue = UnitConfig.getUnitXpReward(unit.getType());
                    xpReceiver.addExperience(xpValue); // Use the passed xpReceiver
                    Gdx.app.debug("World Special", xpReceiver.getType() + " gained " + xpValue + " XP for special kill (Unit).");
                    units.removeIndex(i);
                    unitPool.free(unit);
                }
            }
        }

        // Damage towers in area
        for (int i = towers.size - 1; i >= 0; i--) {
            Tower tower = towers.get(i);
            if (tower.getX() >= startX && tower.getX() <= endX) {
                tower.takeDamage(damage);
                if (!tower.isAlive()) {
                    int xpValue = UnitConfig.getTowerXpReward(tower.getType());
                    xpReceiver.addExperience(xpValue); // Use the passed xpReceiver
                    Gdx.app.debug("World Special", xpReceiver.getType() + " gained " + xpValue + " XP for special kill (Tower).");
                    towers.removeIndex(i);
                    towerPool.free(tower);
                }
            }
        }

        // Damage base if in area
        if (baseOwner.getBasePositionX() >= startX && baseOwner.getBasePositionX() <= endX) {
            baseOwner.takeDamage(damage); // Base takes damage too
            Gdx.app.debug("World Special", "Base " + baseOwner.getType() + " took " + damage + " special damage.");
        }
    }

    // Helper for multi-target specials
    private void applyDamageToMultipleUnits(Array<Unit> units, int maxTargets, int damage, Pool<Unit> unitPool, Player xpReceiver) {
        int targetsHit = 0;
        // Iterate randomly or just first N units
        for (int i = units.size - 1; i >= 0 && targetsHit < maxTargets; i--) {
            Unit unit = units.get(i);
            unit.takeDamage(damage);
            targetsHit++;
            Gdx.app.debug("World Special", "Multi-target special hit unit " + unit.getType() + " for " + damage + " dmg.");
            if (!unit.isAlive()) {
                int xpValue = UnitConfig.getUnitXpReward(unit.getType());
                xpReceiver.addExperience(xpValue); // Use the passed xpReceiver
                Gdx.app.debug("World Special", xpReceiver.getType() + " gained " + xpValue + " XP for special kill (Multi-Unit).");
                units.removeIndex(i);
                unitPool.free(unit);
            }
        }
        Gdx.app.log("World Special", "Multi-target special hit " + targetsHit + " units.");
    }


    // --- Getters for Renderer ---
    public Array<Unit> getPlayerUnits() { return playerUnits; }
    public Array<Unit> getAiUnits() { return aiUnits; }
    public Array<Tower> getPlayerTowers() { return playerTowers; }
    public Array<Tower> getAiTowers() { return aiTowers; }

    public int getTowerCount(PlayerType ownerType) {
        return (ownerType == PlayerType.PLAYER) ? playerTowers.size : aiTowers.size;
    }


    @Override
    public void dispose() {
        // Clear arrays and potentially return objects to pools if not already done
        // Free units and towers back to pool explicitly if needed upon world disposal
        for(Unit u : playerUnits) unitPool.free(u); // Assuming unitPool is accessible or passed in
        for(Unit u : aiUnits) unitPool.free(u);
        for(Tower t : playerTowers) towerPool.free(t); // Assuming towerPool is accessible or passed in
        for(Tower t : aiTowers) towerPool.free(t);

        playerUnits.clear();
        aiUnits.clear();
        playerTowers.clear();
        aiTowers.clear();
        // Pools themselves are usually managed elsewhere or statically
    }

    // Need access to pools for disposal, ideally pass them in constructor or keep static access
    private Pool<Unit> unitPool;
    private Pool<Tower> towerPool;

    public void setPools(Pool<Unit> up, Pool<Tower> tp) { // Method to set pools after creation if needed
        this.unitPool = up;
        this.towerPool = tp;
    }
}
