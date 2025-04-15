package com.ageofwar.models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.ageofwar.configs.GameConfig; // Class to hold constants
import com.ageofwar.configs.UnitConfig; // Class to hold unit/tower definitions

public class GameModel implements Disposable {

    private Player player;
    private Player aiPlayer;
    private World world; // Contains units and towers

    private boolean gameOver = false;
    private PlayerType winner = null;

    private float timeAccumulator = 0f; // For resource generation timer

    // Pools for efficient object reuse
    private final Pool<Unit> unitPool = Pools.get(Unit.class);
    private final Pool<Tower> towerPool = Pools.get(Tower.class);
    // Add pools for projectiles if needed

    public GameModel() {
        // Initialize with default values or load from config
    }

    public void initialize() {
        Gdx.app.log("GameModel", "Initializing...");
        player = new Player(PlayerType.PLAYER, GameConfig.STARTING_GOLD, GameConfig.STARTING_XP, GameConfig.BASE_START_HP_PLAYER, GameConfig.PLAYER_BASE_X);
        aiPlayer = new Player(PlayerType.AI, GameConfig.STARTING_GOLD, GameConfig.STARTING_XP, GameConfig.BASE_START_HP_AI, GameConfig.AI_BASE_X);
        world = new World();
        world.setPools(unitPool, towerPool); // Pass pools to world for cleanup on dispose
        gameOver = false;
        winner = null;
        timeAccumulator = 0f;

        // Pre-populate pools if desired
        // Pools.get(Unit.class).fill(50);
        // Pools.get(Tower.class).fill(10);
        Gdx.app.log("GameModel", "Initialization complete.");
    }


    public void update(float delta) {
        if (gameOver) return;

        timeAccumulator += delta;

        // 0. Update Players (cooldowns)
        player.update(delta);
        aiPlayer.update(delta);


        // 1. Resource Generation (Passive Income)
        if (timeAccumulator >= GameConfig.GOLD_GENERATION_INTERVAL) {
            player.addGold(GameConfig.GOLD_PER_INTERVAL);
            aiPlayer.addGold(GameConfig.GOLD_PER_INTERVAL); // AI also gets passive income
            timeAccumulator -= GameConfig.GOLD_GENERATION_INTERVAL;
        }

        // 2. AI Logic
        updateAI(delta);

        // 3. Update World (Units, Towers, Projectiles)
        world.update(delta, player, aiPlayer, unitPool, towerPool); // Pass players for targeting and pools

        // 4. Check Win/Loss Conditions
        checkGameOver();
    }

    private void updateAI(float delta) {
        // Simple AI: Spawn units periodically if possible
        aiPlayer.aiUpdateTimer += delta;
        if (aiPlayer.aiUpdateTimer >= GameConfig.AI_SPAWN_INTERVAL) {
            aiPlayer.aiUpdateTimer -= GameConfig.AI_SPAWN_INTERVAL;

            // Try to spawn a unit available in the current era
            UnitType typeToSpawn = UnitConfig.getRandomUnitForEra(aiPlayer.getCurrentEra());
            if (typeToSpawn != null) {
                int cost = UnitConfig.getUnitCost(typeToSpawn);
                if (aiPlayer.getGold() >= cost) {
                    spawnUnit(PlayerType.AI, typeToSpawn);
                    Gdx.app.debug("GameModel AI", "AI spawned " + typeToSpawn);
                } else {
                    Gdx.app.debug("GameModel AI", "AI wanted to spawn " + typeToSpawn + " but needs " + cost + " gold, has " + aiPlayer.getGold());
                }
            }

            // Simple AI: Upgrade era if possible
            Era nextEra = Era.getNextEra(aiPlayer.getCurrentEra());
            if (nextEra != null) { // Check if upgrade is possible
                int upgradeCost = GameConfig.getEraUpgradeCost(aiPlayer.getCurrentEra());
                if (upgradeCost > 0 && aiPlayer.getExperience() >= upgradeCost) {
                    upgradeEra(PlayerType.AI);
                    Gdx.app.log("GameModel AI", "AI upgraded to era: " + aiPlayer.getCurrentEra());
                }
            }

            // Simple AI: Use Special if ready and has gold (low chance?)
            if (aiPlayer.canUseSpecial() && aiPlayer.getGold() >= GameConfig.getSpecialAbilityCost(aiPlayer.getCurrentEra())) {
                if (MathUtils.random() < 0.1f) { // 10% chance per AI update interval to use special if ready
                    useSpecialAbility(PlayerType.AI);
                    Gdx.app.log("GameModel AI", "AI used special ability.");
                }
            }
        }
    }


    private void checkGameOver() {
        if (player.getBaseHealth() <= 0) {
            gameOver = true;
            winner = PlayerType.AI;
            Gdx.app.log("GameModel", "Game Over! Winner: AI");
        } else if (aiPlayer.getBaseHealth() <= 0) {
            gameOver = true;
            winner = PlayerType.PLAYER;
            Gdx.app.log("GameModel", "Game Over! Winner: Player");
        }
    }

    // --- Public Methods for Controller Interaction ---

    public boolean spawnUnit(PlayerType ownerType, UnitType unitType) {
        Player owner = (ownerType == PlayerType.PLAYER) ? player : aiPlayer;
        int cost = UnitConfig.getUnitCost(unitType);
        Era requiredEra = UnitConfig.getUnitRequiredEra(unitType);

        if (owner.getGold() >= cost && owner.getCurrentEra().ordinal() >= requiredEra.ordinal()) {
            owner.addGold(-cost); // Deduct cost

            Unit unit = unitPool.obtain();
            unit.init(
                unitType,
                ownerType,
                UnitConfig.getUnitHealth(unitType),
                UnitConfig.getUnitDamage(unitType),
                UnitConfig.getUnitAttackSpeed(unitType),
                UnitConfig.getUnitRange(unitType),
                UnitConfig.getUnitMoveSpeed(unitType),
                // Spawn position depends on owner
                (ownerType == PlayerType.PLAYER) ? GameConfig.PLAYER_SPAWN_X : GameConfig.AI_SPAWN_X,
                GameConfig.GROUND_Y // Spawn units on the ground level
            );
            world.addUnit(unit);
            Gdx.app.debug("GameModel", ownerType + " spawned " + unitType);
            return true;
        } else {
            if (owner.getGold() < cost) Gdx.app.debug("GameModel", ownerType + " cannot spawn " + unitType + ": insufficient gold.");
            if (owner.getCurrentEra().ordinal() < requiredEra.ordinal()) Gdx.app.debug("GameModel", ownerType + " cannot spawn " + unitType + ": era not reached.");
            return false;
        }
    }

    public boolean buildTower(PlayerType ownerType, TowerType towerType, float positionX) {
        Player owner = (ownerType == PlayerType.PLAYER) ? player : aiPlayer;
        int cost = UnitConfig.getTowerCost(towerType);
        Era requiredEra = UnitConfig.getTowerRequiredEra(towerType);
        int maxTowers = GameConfig.getMaxTowersForEra(owner.getCurrentEra());

        // Basic placement validation (prevent building on base, too close to others?)
        // For now, just check cost, era, and tower limit
        if (world.getTowerCount(ownerType) >= maxTowers) {
            Gdx.app.debug("GameModel", ownerType + " cannot build " + towerType + ": tower limit reached for era " + owner.getCurrentEra() + " (" + maxTowers + ")");
            return false;
        }

        if (owner.getGold() >= cost && owner.getCurrentEra().ordinal() >= requiredEra.ordinal()) {
            owner.addGold(-cost);

            Tower tower = towerPool.obtain();
            tower.init(
                towerType,
                ownerType,
                UnitConfig.getTowerHealth(towerType),
                UnitConfig.getTowerDamage(towerType),
                UnitConfig.getTowerAttackSpeed(towerType),
                UnitConfig.getTowerRange(towerType),
                positionX, // Use provided X
                GameConfig.GROUND_Y // Place towers on the ground
            );
            world.addTower(tower);
            Gdx.app.debug("GameModel", ownerType + " built " + towerType + " at " + positionX);
            return true;
        } else {
            if (owner.getGold() < cost) Gdx.app.debug("GameModel", ownerType + " cannot build " + towerType + ": insufficient gold.");
            if (owner.getCurrentEra().ordinal() < requiredEra.ordinal()) Gdx.app.debug("GameModel", ownerType + " cannot build " + towerType + ": era not reached.");
            return false;
        }
    }

    public boolean upgradeEra(PlayerType ownerType) {
        Player owner = (ownerType == PlayerType.PLAYER) ? player : aiPlayer;
        Era currentEra = owner.getCurrentEra();
        Era nextEra = Era.getNextEra(currentEra);

        if (nextEra != null) { // Check if there is a next era
            int cost = GameConfig.getEraUpgradeCost(currentEra);
            if (owner.getExperience() >= cost) {
                owner.addExperience(-cost);
                owner.setCurrentEra(nextEra);
                // Potentially upgrade base health/appearance here too
                owner.setBaseHealth(owner.getBaseHealth() + GameConfig.BASE_HP_UPGRADE_BONUS); // Example HP bonus on upgrade
                //owner.setMaxBaseHealth(owner.getMaxBaseHealth() + GameConfig.BASE_HP_UPGRADE_BONUS); // Also upgrade Max HP
                Gdx.app.log("GameModel", ownerType + " upgraded to era: " + nextEra);
                return true;
            } else {
                Gdx.app.debug("GameModel", ownerType + " cannot upgrade era: needs " + cost + " XP, has " + owner.getExperience());
            }
        } else {
            Gdx.app.debug("GameModel", ownerType + " cannot upgrade era: already at max era.");
        }
        return false;
    }

    public boolean useSpecialAbility(PlayerType ownerType) {
        Player owner = (ownerType == PlayerType.PLAYER) ? player : aiPlayer;
        Era currentEra = owner.getCurrentEra();
        int cost = GameConfig.getSpecialAbilityCost(currentEra); // Assuming specials cost gold or have cooldown

        if (owner.canUseSpecial() && owner.getGold() >= cost) {
            owner.addGold(-cost); // Or use cooldown mechanism
            owner.useSpecial(); // Mark special as used (for cooldown)

            Player targetPlayer = (ownerType == PlayerType.PLAYER) ? aiPlayer : player;
            // MODIFIED: Pass 'owner' as the xpReceiver
            world.activateSpecialAbility(ownerType, currentEra, targetPlayer, unitPool, towerPool, owner); // Delegate effect to World

            Gdx.app.log("GameModel", ownerType + " used special ability for era: " + currentEra);
            return true;
        } else {
            if (!owner.canUseSpecial()) Gdx.app.debug("GameModel", ownerType + " cannot use special: cooldown active.");
            if (owner.getGold() < cost) Gdx.app.debug("GameModel", ownerType + " cannot use special: insufficient gold.");
            return false;
        }
    }


    // --- Getters for View and Controller ---
    public Player getPlayer() { return player; }
    public Player getAiPlayer() { return aiPlayer; }
    public World getWorld() { return world; }
    public boolean isGameOver() { return gameOver; }
    public PlayerType getWinner() { return winner; }


    @Override
    public void dispose() {
        Gdx.app.log("GameModel", "Disposing...");
        world.dispose(); // Dispose world resources (if any)
        // Clear pools - Note: This makes objects unusable if obtained again without re-initialization.
        // It's often better to let the application exit handle final cleanup if pools are static.
        // However, if GameModel can be recreated, clearing is safer.
        unitPool.clear();
        towerPool.clear();
        // Dispose other resources if the model manages them directly
    }
}
