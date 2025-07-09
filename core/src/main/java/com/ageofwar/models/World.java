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
import com.ageofwar.models.units.UnitState;
import com.ageofwar.systems.CombatSystem; // Import hệ thống mới
import com.ageofwar.systems.CollisionSystem;
import com.ageofwar.systems.FormationSystem;
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
     * Cập nhật toàn bộ World mỗi frame.
     */
    public void update(float delta,
                       Player player,
                       Player aiPlayer,
                       Pool<Unit> up,
                       Pool<Tower> tp) {
        // Đảm bảo pools đã được set
        if (unitPool == null) setPools(up, tp);

        // 0. Cập nhật formation và collision để tránh units chồng lên nhau
        FormationSystem.updateFormation(playerUnits, delta);
        FormationSystem.updateFormation(aiUnits, delta);
        CollisionSystem.resolveCollisions(playerUnits, delta);
        CollisionSystem.resolveCollisions(aiUnits, delta);

        // 1. Player units & towers
        updateEntities(delta, playerUnits, aiUnits, aiTowers, aiPlayer, player);
        updateTowers(delta, playerTowers, aiUnits, player);

        // 2. AI units & towers
        updateEntities(delta, aiUnits, playerUnits, playerTowers, player, aiPlayer);
        updateTowers(delta, aiTowers, playerUnits, aiPlayer);

        // 3. Dọn dẹp
        cleanupEntities(playerUnits, unitPool);
        cleanupEntities(aiUnits, unitPool);
        cleanupTowers(playerTowers, towerPool);
        cleanupTowers(aiTowers, towerPool);
    }

    /**
     * Cập nhật di chuyển và tấn công cho nhóm Unit.
     */
    private void updateEntities(float delta,
                                Array<Unit> units,
                                Array<Unit> enemyUnits,
                                Array<Tower> enemyTowers,
                                Player enemyPlayer,
                                Player owner) {
        for (int i = units.size - 1; i >= 0; i--) {
            Unit unit = units.get(i);
            if (!unit.isAlive()) continue;

            unit.update(delta); // cooldown nội bộ

            // Nếu đang trong animation death hoặc hurt, không thực hiện logic di chuyển/tấn công
            if (unit.isInDeathAnimation() || unit.isInHurtAnimation()) continue;

            // Tìm mục tiêu (Unit hoặc Tower) qua CombatSystem
            Entity target = combatSystem.findTargetForUnit(unit, enemyUnits, enemyTowers);
            unit.setTarget(target);

            if (target != null) {
                float dist = Vector2.dst(unit.getX(), unit.getY(), target.getX(), target.getY());
                if (dist <= unit.getRange()) {
                    unit.setMoving(false);
                    if (unit.canAttack()) {
                        combatSystem.resolveAttack(unit, target, owner);
                    } else if (!unit.isInAttackAnimation()) {
                        // Nếu chưa thể tấn công (đang hồi chiêu) và không đang trong animation tấn công
                        unit.setCurrentState(UnitState.IDLE);
                    }
                    // Nếu đang trong animation tấn công, không thay đổi trạng thái
                } else if (!unit.isInAttackAnimation()) {
                    // Chỉ di chuyển nếu không đang trong animation tấn công
                    unit.setMoving(true);
                    unit.setCurrentState(UnitState.WALK);
                    float dir = (target.getX() > unit.getX()) ? 1f : -1f;
                    unit.move(dir * unit.getMoveSpeed() * delta);
                }
            } else {
                // Tấn công base nếu không có target sinh ra
                if (!unit.isInAttackAnimation()) {
                    // Chỉ di chuyển nếu không đang trong animation tấn công
                    unit.setMoving(true);
                    unit.setCurrentState(UnitState.WALK);
                    float baseX = (unit.getOwnerType() == PlayerType.PLAYER)
                        ? GameConfig.AI_BASE_X
                        : GameConfig.PLAYER_BASE_X;
                    float dist = Math.abs(unit.getX() - baseX);
                    if (dist <= unit.getRange()) {
                        unit.setMoving(false);
                        if (unit.canAttack()) {
                            combatSystem.attackBase(unit, enemyPlayer);
                        } else if (!unit.isInAttackAnimation()) {
                            // Nếu chưa thể tấn công (đang hồi chiêu) và không đang trong animation tấn công
                            unit.setCurrentState(UnitState.IDLE);
                        }
                    } else {
                        float dir = (baseX > unit.getX()) ? 1f : -1f;
                        unit.move(dir * unit.getMoveSpeed() * delta);
                    }
                }
            }

            // Giới hạn trong world bounds
            float halfW = UnitConfig.getUnitWidth(unit.getType()) * 0.5f;
            float x = MathUtils.clamp(unit.getX(), halfW, GameScreen.WORLD_WIDTH - halfW);
            unit.setPosition(x, unit.getY());
        }
    }

    /**
     * Cập nhật tấn công cho nhóm Tower.
     */
    private void updateTowers(float delta,
                              Array<Tower> towers,
                              Array<Unit> enemyUnits,
                              Player owner) {
        for (int i = towers.size - 1; i >= 0; i--) {
            Tower tower = towers.get(i);
            if (!tower.isAlive()) continue;

            tower.update(delta);
            Unit target = combatSystem.findTargetForTower(tower, enemyUnits);
            tower.setTarget(target);

            if (target != null && tower.canAttack()) {
                combatSystem.resolveAttack(tower, target, owner);
            }
        }
    }

    /**
     * Dọn dẹp Unit đã chết.
     */
    private void cleanupEntities(Array<Unit> units, Pool<Unit> pool) {
        for (int i = units.size - 1; i >= 0; i--) {
            Unit u = units.get(i);
            // Chỉ xóa unit khi đã chết và animation death đã hoàn thành
            if (!u.isAlive() && u.isDeathAnimationCompleted()) {
                units.removeIndex(i);
                pool.free(u);
            }
        }
    }

    /**
     * Dọn dẹp Tower đã chết.
     */
    private void cleanupTowers(Array<Tower> towers, Pool<Tower> pool) {
        for (int i = towers.size - 1; i >= 0; i--) {
            Tower t = towers.get(i);
            if (!t.isAlive()) {
                towers.removeIndex(i);
                pool.free(t);
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
