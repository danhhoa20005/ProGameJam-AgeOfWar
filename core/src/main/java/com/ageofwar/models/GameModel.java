/**
 * Lớp GameModel là trung tâm quản lý toàn bộ logic và trạng thái của trò chơi Age of War.
 * Nó khởi tạo người chơi (Player và AI), thế giới (World), quản lý vòng lặp cập nhật chính,
 * xử lý các hành động của người chơi, kiểm tra điều kiện thắng/thua, và quản lý tài nguyên (Object Pools).
 * Logic AI được tách ra lớp AIController riêng.
 * Implement Disposable để giải phóng tài nguyên khi không cần thiết.
 */
package com.ageofwar.models;

import com.ageofwar.controllers.AIController; // Import lớp AIController mới
import com.ageofwar.models.players.Player;
import com.ageofwar.models.players.PlayerType;
import com.ageofwar.models.towers.Tower;
import com.ageofwar.models.towers.TowerType;
import com.ageofwar.models.units.Unit;
import com.ageofwar.models.units.UnitType;
import com.ageofwar.systems.SpecialAbilitySystem;
import com.badlogic.gdx.Gdx;
// import com.badlogic.gdx.math.MathUtils; // Không cần MathUtils ở đây nữa
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.ageofwar.configs.*;

public class GameModel implements Disposable {

    private Player player; // Đối tượng người chơi
    private Player aiPlayer; // Đối tượng AI
    private World world; // Đối tượng quản lý các thực thể trong thế giới game (lính, trụ)
    private AIController aiController; // Đối tượng quản lý logic AI
    private SpecialAbilitySystem specialAbilitySystem;

    private boolean gameOver = false; // Cờ trạng thái kết thúc game
    private PlayerType winner = null; // Người chiến thắng (null nếu chưa kết thúc)

    private float timeAccumulator = 0f; // Biến tích lũy thời gian cho việc tạo tài nguyên

    // Pools để tái sử dụng đối tượng hiệu quả
    private final Pool<Unit> unitPool = Pools.get(Unit.class);
    private final Pool<Tower> towerPool = Pools.get(Tower.class);
    // Thêm pools cho Projectile nếu cần

    public GameModel() {
        // Khởi tạo với giá trị mặc định hoặc tải từ cấu hình
    }

    /**
     * Khởi tạo trạng thái ban đầu của trò chơi.
     */
    public void initialize() {
        Gdx.app.log("GameModel", "Đang khởi tạo..."); // Ghi log: Bắt đầu khởi tạo
        player = new Player(PlayerType.PLAYER, GameConfig.STARTING_GOLD, GameConfig.STARTING_XP, GameConfig.BASE_START_HP_PLAYER, GameConfig.PLAYER_BASE_X);
        aiPlayer = new Player(PlayerType.AI, GameConfig.STARTING_GOLD, GameConfig.STARTING_XP, GameConfig.BASE_START_HP_AI, GameConfig.AI_BASE_X);
        world = new World();
        world.setPools(unitPool, towerPool); // Truyền pools vào world để dọn dẹp khi dispose

        // Khởi tạo AIController sau khi đã có aiPlayer và gameModel (this)
        aiController = new AIController(aiPlayer, this);

        specialAbilitySystem = new SpecialAbilitySystem();
        specialAbilitySystem.initialize(
            world.getPlayerUnitsRef(), world.getAiUnitsRef(),
            world.getPlayerTowersRef(), world.getAiTowersRef(),
            unitPool, towerPool
        );

        gameOver = false;
        winner = null;
        timeAccumulator = 0f;

        Gdx.app.log("GameModel", "Khởi tạo hoàn tất."); // Ghi log: Khởi tạo xong
    }


    /**
     * Cập nhật trạng thái trò chơi mỗi khung hình.
     * @param delta Thời gian trôi qua từ khung hình trước (giây).
     */
    public void update(float delta) {
        if (gameOver) return; // Không cập nhật nếu game đã kết thúc

        timeAccumulator += delta;

        // 0. Cập nhật Người chơi (thời gian hồi chiêu)
        player.update(delta);
        aiPlayer.update(delta);


        // 1. Tạo Tài nguyên (Thu nhập thụ động)
        if (timeAccumulator >= GameConfig.GOLD_GENERATION_INTERVAL) {
            player.addGold(GameConfig.GOLD_PER_INTERVAL);
            aiPlayer.addGold(GameConfig.GOLD_PER_INTERVAL); // AI cũng nhận thu nhập thụ động
            timeAccumulator -= GameConfig.GOLD_GENERATION_INTERVAL;
        }

        // 2. Cập nhật Logic AI (thông qua AIController)
        aiController.update(delta); // Gọi update của AIController

        // 3. Cập nhật Thế giới (Lính, Trụ, Đạn)
        world.update(delta, player, aiPlayer, unitPool, towerPool); // Truyền player và pools vào

        // 4. Kiểm tra Điều kiện Thắng/Thua
        checkGameOver();
    }

    /**
     * Kiểm tra xem một trong hai người chơi đã thua chưa.
     */
    private void checkGameOver() {
        if (player.getBaseHealth() <= 0) {
            gameOver = true;
            winner = PlayerType.AI;
            Gdx.app.log("GameModel", "Kết thúc trò chơi! Người thắng: AI"); // Ghi log: AI thắng
        } else if (aiPlayer.getBaseHealth() <= 0) {
            gameOver = true;
            winner = PlayerType.PLAYER;
            Gdx.app.log("GameModel", "Kết thúc trò chơi! Người thắng: Người chơi"); // Ghi log: Người chơi thắng
        }
    }

    // --- Các phương thức công khai để Controller tương tác (spawnUnit, buildTower, upgradeEra, useSpecialAbility) GIỮ NGUYÊN ---
    // Lý do: Các phương thức này đại diện cho các hành động có thể xảy ra trong game,
    // được gọi bởi cả người chơi (thông qua Input/HUD) và AI (thông qua AIController).
    // GameModel là nơi hợp lý để chứa chúng vì chúng thay đổi trạng thái cốt lõi của game.

    /**
     * Sinh một đơn vị lính cho người chơi hoặc AI.
     * @param ownerType Loại người chơi (PLAYER hoặc AI).
     * @param unitType Loại lính cần sinh.
     * @return true nếu sinh thành công, false nếu không đủ điều kiện (vàng, kỷ nguyên).
     */
    public boolean spawnUnit(PlayerType ownerType, UnitType unitType) {
        Player owner = (ownerType == PlayerType.PLAYER) ? player : aiPlayer;
        int cost = UnitConfig.getUnitCost(unitType);
        Era requiredEra = UnitConfig.getUnitRequiredEra(unitType);

        if (owner.getGold() >= cost && owner.getCurrentEra().ordinal() >= requiredEra.ordinal()) {
            owner.addGold(-cost); // Trừ chi phí

            Unit unit = unitPool.obtain(); // Lấy unit từ pool
            unit.init(
                unitType,
                ownerType,
                UnitConfig.getUnitHealth(unitType),
                UnitConfig.getUnitDamage(unitType),
                UnitConfig.getUnitAttackSpeed(unitType),
                UnitConfig.getUnitRange(unitType),
                UnitConfig.getUnitMoveSpeed(unitType),
                (ownerType == PlayerType.PLAYER) ? GameConfig.PLAYER_SPAWN_X : GameConfig.AI_SPAWN_X,
                GameConfig.GROUND_Y
            );
            world.addUnit(unit); // Thêm unit vào thế giới
            Gdx.app.debug("GameModel", ownerType + " đã sinh " + unitType); // Gỡ lỗi: Sinh lính
            return true;
        } else {
            if (owner.getGold() < cost) Gdx.app.debug("GameModel", ownerType + " không thể sinh " + unitType + ": không đủ vàng.");
            if (owner.getCurrentEra().ordinal() < requiredEra.ordinal()) Gdx.app.debug("GameModel", ownerType + " không thể sinh " + unitType + ": chưa đạt kỷ nguyên yêu cầu.");
            return false;
        }
    }

    /**
     * Xây dựng một công trình phòng thủ (trụ).
     * @param ownerType Loại người chơi (PLAYER hoặc AI).
     * @param towerType Loại trụ cần xây.
     * @param positionX Tọa độ X nơi muốn xây trụ.
     * @return true nếu xây thành công, false nếu không đủ điều kiện (vàng, kỷ nguyên, giới hạn trụ).
     */
    public boolean buildTower(PlayerType ownerType, TowerType towerType, float positionX) {
        Player owner = (ownerType == PlayerType.PLAYER) ? player : aiPlayer;
        int cost = TowerConfig.getTowerCost(towerType);
        Era requiredEra = TowerConfig.getTowerRequiredEra(towerType);
        int maxTowers = GameConfig.getMaxTowersForEra(owner.getCurrentEra());

        if (world.getTowerCount(ownerType) >= maxTowers) {
            Gdx.app.debug("GameModel", ownerType + " không thể xây " + towerType + ": đã đạt giới hạn trụ cho kỷ nguyên " + owner.getCurrentEra() + " (" + maxTowers + ")");
            return false;
        }

        // Thêm kiểm tra vị trí xây dựng hợp lệ (ví dụ: không quá gần trụ khác, không trên đường đi chính?)
        // if (!world.isValidTowerPlacement(ownerType, positionX)) {
        //     Gdx.app.debug("GameModel", ownerType + " không thể xây " + towerType + " tại " + positionX + ": Vị trí không hợp lệ.");
        //     return false;
        // }

        if (owner.getGold() >= cost && owner.getCurrentEra().ordinal() >= requiredEra.ordinal()) {
            owner.addGold(-cost);

            Tower tower = towerPool.obtain();
            tower.init(
                towerType,
                ownerType,
                TowerConfig.getTowerHealth(towerType),
                TowerConfig.getTowerDamage(towerType),
                TowerConfig.getTowerAttackSpeed(towerType),
                TowerConfig.getTowerRange(towerType),
                positionX,
                GameConfig.GROUND_Y
            );
            world.addTower(tower);
            Gdx.app.debug("GameModel", ownerType + " đã xây " + towerType + " tại " + positionX);
            return true;
        } else {
            if (owner.getGold() < cost) Gdx.app.debug("GameModel", ownerType + " không thể xây " + towerType + ": không đủ vàng.");
            if (owner.getCurrentEra().ordinal() < requiredEra.ordinal()) Gdx.app.debug("GameModel", ownerType + " không thể xây " + towerType + ": chưa đạt kỷ nguyên yêu cầu.");
            return false;
        }
    }

    /**
     * Nâng cấp lên kỷ nguyên tiếp theo cho người chơi hoặc AI.
     * @param ownerType Loại người chơi (PLAYER hoặc AI).
     * @return true nếu nâng cấp thành công, false nếu không đủ điều kiện (XP, đã ở kỷ nguyên cuối).
     */
    public boolean upgradeEra(PlayerType ownerType) {
        Player owner = (ownerType == PlayerType.PLAYER) ? player : aiPlayer;
        Era currentEra = owner.getCurrentEra();
        Era nextEra = Era.getNextEra(currentEra);

        if (nextEra != null) {
            int cost = GameConfig.getEraUpgradeCost(currentEra);
            if (owner.getExperience() >= cost) {
                owner.addExperience(-cost);
                owner.setCurrentEra(nextEra);
                owner.setBaseHealth(owner.getBaseHealth() + GameConfig.BASE_HP_UPGRADE_BONUS);
                //owner.setMaxBaseHealth(owner.getMaxBaseHealth() + GameConfig.BASE_HP_UPGRADE_BONUS); // Nếu muốn nâng cả máu tối đa
                Gdx.app.log("GameModel", ownerType + " đã nâng cấp lên kỷ nguyên: " + nextEra);
                return true;
            } else {
                Gdx.app.debug("GameModel", ownerType + " không thể nâng cấp kỷ nguyên: cần " + cost + " XP, hiện có " + owner.getExperience());
            }
        } else {
            Gdx.app.debug("GameModel", ownerType + " không thể nâng cấp kỷ nguyên: đã ở kỷ nguyên tối đa.");
        }
        return false;
    }

    /**
     * Sử dụng kỹ năng đặc biệt của kỷ nguyên hiện tại.
     * @param ownerType Loại người chơi (PLAYER hoặc AI).
     * @return true nếu sử dụng thành công, false nếu không đủ điều kiện (vàng, hồi chiêu).
     */
    public boolean useSpecialAbility(PlayerType ownerType) {
        Player owner = (ownerType == PlayerType.PLAYER) ? player : aiPlayer;
        Era currentEra = owner.getCurrentEra();
        int cost = GameConfig.getSpecialAbilityCost(currentEra);

        if (owner.canUseSpecial() && owner.getGold() >= cost) {
            owner.addGold(-cost);
            owner.useSpecial();

            Player targetPlayer = (ownerType == PlayerType.PLAYER) ? aiPlayer : player;
            // Truyền 'owner' làm người nhận XP
            specialAbilitySystem.activate(ownerType, currentEra, targetPlayer, owner); // Truyền owner làm xpReceiver

            Gdx.app.log("GameModel", ownerType + " đã sử dụng kỹ năng đặc biệt cho kỷ nguyên: " + currentEra);
            return true;
        } else {
            if (!owner.canUseSpecial()) Gdx.app.debug("GameModel", ownerType + " không thể sử dụng kỹ năng: đang hồi chiêu.");
            if (owner.getGold() < cost) Gdx.app.debug("GameModel", ownerType + " không thể sử dụng kỹ năng: không đủ vàng.");
            return false;
        }
    }


    // --- Getters để View và Controller lấy thông tin (GIỮ NGUYÊN) ---
    public Player getPlayer() { return player; }
    public Player getAiPlayer() { return aiPlayer; }
    public World getWorld() { return world; }
    public boolean isGameOver() { return gameOver; }
    public PlayerType getWinner() { return winner; }


    /**
     * Giải phóng tài nguyên được quản lý bởi GameModel.
     * Được gọi khi không cần sử dụng GameModel nữa.
     */
    @Override
    public void dispose() {
        Gdx.app.log("GameModel", "Đang giải phóng..."); // Ghi log: Bắt đầu giải phóng
        world.dispose(); // Giải phóng tài nguyên của world
        // AIController không quản lý tài nguyên Disposable trực tiếp nên không cần gọi dispose() trừ khi nó tạo ra thứ gì đó cần giải phóng
        unitPool.clear();
        towerPool.clear();
    }
}
