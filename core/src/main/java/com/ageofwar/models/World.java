/**
 * Lớp World quản lý tất cả các thực thể động trong trò chơi (Units và Towers) cho cả người chơi và AI.
 * Nó chịu trách nhiệm cập nhật trạng thái của các thực thể này, xử lý di chuyển cơ bản,
 * dọn dẹp các thực thể đã chết. Logic chiến đấu và kỹ năng đặc biệt được ủy quyền cho các hệ thống riêng.
 * Implement Disposable để giải phóng tài nguyên.
 */
package com.ageofwar.models;

import com.ageofwar.models.players.Player;
import com.ageofwar.models.players.PlayerType;
import com.ageofwar.models.towers.Tower;
import com.ageofwar.models.units.Unit;
import com.ageofwar.systems.CombatSystem; // Import hệ thống mới
// import com.ageofwar.systems.SpecialAbilitySystem; // Không cần trực tiếp ở đây nữa
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
// import com.badlogic.gdx.math.Rectangle; // Không cần tempRect nữa nếu không dùng ở đây
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import com.ageofwar.configs.*;
import com.ageofwar.screens.GameScreen;

public class World implements Disposable {
    private final Array<Unit> playerUnits;
    private final Array<Unit> aiUnits;
    private final Array<Tower> playerTowers;
    private final Array<Tower> aiTowers;

    private Pool<Unit> unitPool;
    private Pool<Tower> towerPool;

    // Các hệ thống xử lý logic
    private final CombatSystem combatSystem;
    // SpecialAbilitySystem sẽ được quản lý bởi GameModel hoặc nơi gọi kỹ năng

    public World() {
        playerUnits = new Array<>();
        aiUnits = new Array<>();
        playerTowers = new Array<>();
        aiTowers = new Array<>();

        combatSystem = new CombatSystem(); // Khởi tạo hệ thống chiến đấu
    }

    /**
     * Thêm một Unit vào danh sách quản lý của World.
     * @param unit Unit cần thêm.
     */
    public void addUnit(Unit unit) {
        if (unit.getOwnerType() == PlayerType.PLAYER) {
            playerUnits.add(unit);
        } else {
            aiUnits.add(unit);
        }
    }

    /**
     * Thêm một Tower vào danh sách quản lý của World.
     * @param tower Tower cần thêm.
     */
    public void addTower(Tower tower) {
        if (tower.getOwnerType() == PlayerType.PLAYER) {
            playerTowers.add(tower);
        } else {
            aiTowers.add(tower);
        }
    }

    /**
     * Cập nhật trạng thái của tất cả các thực thể trong World.
     * @param delta Thời gian trôi qua từ khung hình trước.
     * @param player Đối tượng người chơi.
     * @param aiPlayer Đối tượng AI.
     * @param unitPool Pool quản lý đối tượng Unit.
     * @param towerPool Pool quản lý đối tượng Tower.
     */
    public void update(float delta, Player player, Player aiPlayer, Pool<Unit> unitPool, Pool<Tower> towerPool) {
        // 1. Cập nhật Lính & Trụ của Người chơi
        updateEntities(delta, playerUnits, aiUnits, aiTowers, aiPlayer, player); // Lính Player
        updateTowers(delta, playerTowers, aiUnits, player); // Trụ Player

        // 2. Cập nhật Lính & Trụ của AI
        updateEntities(delta, aiUnits, playerUnits, playerTowers, player, aiPlayer); // Lính AI
        updateTowers(delta, aiTowers, playerUnits, aiPlayer); // Trụ AI

        // 3. Loại bỏ các thực thể đã chết
        cleanupEntities(playerUnits, unitPool);
        cleanupEntities(aiUnits, unitPool);
        cleanupTowers(playerTowers, towerPool);
        cleanupTowers(aiTowers, towerPool);
    }

    /**
     * Phương thức cập nhật chung cho các Unit (di chuyển và ủy quyền chiến đấu).
     * @param delta Thời gian delta.
     * @param units Danh sách Unit cần cập nhật.
     * @param enemyUnits Danh sách Unit đối phương.
     * @param enemyTowers Danh sách Tower đối phương.
     * @param enemyPlayer Đối tượng Player đối phương.
     * @param owner Chủ sở hữu của các Unit trong danh sách `units`.
     */
    private void updateEntities(float delta, Array<Unit> units, Array<Unit> enemyUnits, Array<Tower> enemyTowers, Player enemyPlayer, Player owner) {
        for (int i = units.size - 1; i >= 0; i--) {
            Unit unit = units.get(i);
            unit.update(delta); // Cập nhật nội bộ (cooldown)

            // --- Ủy quyền tìm mục tiêu cho CombatSystem ---
            Entity target = combatSystem.findTargetForUnit(unit, enemyUnits, enemyTowers);
            unit.setTarget(target);

            // --- Logic Di Chuyển (vẫn ở đây vì liên quan trực tiếp đến Unit) ---
            if (target != null) {
                float distanceToTarget = Vector2.dst(unit.getX(), unit.getY(), target.getX(), target.getY());
                if (distanceToTarget <= unit.getRange()) { // Trong tầm đánh
                    unit.setMoving(false);
                    if (unit.canAttack()) {
                        // --- Ủy quyền xử lý tấn công cho CombatSystem ---
                        combatSystem.resolveAttack(unit, target, owner);
                        // Không cần kiểm tra target chết ở đây nữa, CombatSystem đã xử lý
                    }
                } else { // Ngoài tầm đánh, di chuyển tới mục tiêu
                    unit.setMoving(true);
                    float moveDirection = (target.getX() > unit.getX()) ? 1 : -1;
                    unit.move(moveDirection * unit.getMoveSpeed() * delta);
                }
            } else { // Không có mục tiêu (Unit/Tower), di chuyển/tấn công căn cứ địch
                unit.setMoving(true);
                float enemyBaseX = (unit.getOwnerType() == PlayerType.PLAYER) ? GameConfig.AI_BASE_X : GameConfig.PLAYER_BASE_X;
                float distanceToBase = Math.abs(unit.getX() - enemyBaseX);

                if (distanceToBase <= unit.getRange()) { // Trong tầm đánh căn cứ
                    unit.setMoving(false);
                    if (unit.canAttack()) {
                        // --- Ủy quyền tấn công căn cứ cho CombatSystem ---
                        combatSystem.attackBase(unit, enemyPlayer);
                    }
                } else { // Di chuyển về căn cứ
                    float moveDirection = (enemyBaseX > unit.getX()) ? 1 : -1;
                    unit.move(moveDirection * unit.getMoveSpeed() * delta);
                }
            }

            // Giới hạn vị trí unit trong phạm vi thế giới
            float unitWidth = UnitConfig.getUnitWidth(unit.getType());
            unit.setPosition(MathUtils.clamp(unit.getX(), unitWidth / 2, GameScreen.WORLD_WIDTH - unitWidth / 2), unit.getY());
        }
    }

    /**
     * Logic cập nhật dành riêng cho Tower (ủy quyền chiến đấu).
     * @param delta Thời gian delta.
     * @param towers Danh sách Tower cần cập nhật.
     * @param enemyUnits Danh sách Unit đối phương.
     * @param owner Chủ sở hữu của các Tower trong danh sách `towers`.
     */
    private void updateTowers(float delta, Array<Tower> towers, Array<Unit> enemyUnits, Player owner) {
        for (int i = towers.size - 1; i >= 0; i--) {
            Tower tower = towers.get(i);
            tower.update(delta); // Cập nhật cooldown

            // --- Ủy quyền tìm mục tiêu cho CombatSystem ---
            Unit target = combatSystem.findTargetForTower(tower, enemyUnits);
            tower.setTarget(target);

            if (target != null && tower.canAttack()) {
                // --- Ủy quyền xử lý tấn công cho CombatSystem ---
                combatSystem.resolveAttack(tower, target, owner);
            }
        }
    }

    /**
     * Dọn dẹp các Unit đã chết khỏi danh sách và trả chúng về Pool.
     * @param units Danh sách Unit cần dọn dẹp.
     * @param pool Pool của Unit.
     */
    private void cleanupEntities(Array<Unit> units, Pool<Unit> pool) {
        // Giữ nguyên logic này
        for (int i = units.size - 1; i >= 0; i--) {
            Unit unit = units.get(i);
            if (!unit.isAlive()) {
                units.removeIndex(i);
                pool.free(unit);
            }
        }
    }

    /**
     * Dọn dẹp các Tower đã chết khỏi danh sách và trả chúng về Pool.
     * @param towers Danh sách Tower cần dọn dẹp.
     * @param pool Pool của Tower.
     */
    private void cleanupTowers(Array<Tower> towers, Pool<Tower> pool) {
        // Giữ nguyên logic này
        for (int i = towers.size - 1; i >= 0; i--) {
            Tower tower = towers.get(i);
            if (!tower.isAlive()) {
                towers.removeIndex(i);
                pool.free(tower);
            }
        }
    }


    // --- Getters để Renderer sử dụng (Giữ nguyên) ---
    public Array<Unit> getPlayerUnits() { return playerUnits; }
    public Array<Unit> getAiUnits() { return aiUnits; }
    public Array<Tower> getPlayerTowers() { return playerTowers; }
    public Array<Tower> getAiTowers() { return aiTowers; }
    public int getTowerCount(PlayerType ownerType) {
        return (ownerType == PlayerType.PLAYER) ? playerTowers.size : aiTowers.size;
    }


    /**
     * Giải phóng tài nguyên được quản lý bởi World.
     */
    @Override
    public void dispose() {
        // Giữ nguyên logic dispose này
        if (unitPool != null) {
            for(Unit u : playerUnits) unitPool.free(u);
            for(Unit u : aiUnits) unitPool.free(u);
        }
        if (towerPool != null) {
            for(Tower t : playerTowers) towerPool.free(t);
            for(Tower t : aiTowers) towerPool.free(t);
        }
        playerUnits.clear();
        aiUnits.clear();
        playerTowers.clear();
        aiTowers.clear();
        // CombatSystem và SpecialAbilitySystem không quản lý Disposable nên không cần dispose
    }

    /**
     * Thiết lập các đối tượng Pool cho World.
     * @param up Pool Unit.
     * @param tp Pool Tower.
     */
    public void setPools(Pool<Unit> up, Pool<Tower> tp) {
        // Giữ nguyên logic này
        this.unitPool = up;
        this.towerPool = tp;
        // Có thể cần truyền pools vào CombatSystem nếu nó cần giải phóng đối tượng trực tiếp (hiện tại chưa cần)
        // combatSystem.setPools(up, tp);
    }

    // Hàm mới để cung cấp danh sách cho SpecialAbilitySystem
    public Array<Unit> getPlayerUnitsRef() { return playerUnits; }
    public Array<Unit> getAiUnitsRef() { return aiUnits; }
    public Array<Tower> getPlayerTowersRef() { return playerTowers; }
    public Array<Tower> getAiTowersRef() { return aiTowers; }
}
