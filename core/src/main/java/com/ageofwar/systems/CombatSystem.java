package com.ageofwar.systems; // Đặt vào package systems mới

import com.ageofwar.configs.TowerConfig;
import com.ageofwar.configs.UnitConfig;
import com.ageofwar.models.Entity;
import com.ageofwar.models.players.Player;
import com.ageofwar.models.players.PlayerType;
import com.ageofwar.models.towers.Tower;
import com.ageofwar.models.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Hệ thống CombatSystem quản lý logic chiến đấu trong trò chơi.
 * Bao gồm việc tìm mục tiêu cho các thực thể và xử lý kết quả tấn công.
 */
public class CombatSystem {

    /**
     * Tìm mục tiêu hợp lệ gần nhất (Unit, Tower) cho một Unit tấn công.
     * Ưu tiên Unit và Tower trong tầm phát hiện trước căn cứ.
     * @param attacker Unit đang tìm mục tiêu.
     * @param enemyUnits Danh sách Unit đối phương.
     * @param enemyTowers Danh sách Tower đối phương.
     * @return Thực thể mục tiêu gần nhất trong tầm phát hiện, hoặc null.
     */
    public Entity findTargetForUnit(Unit attacker, Array<Unit> enemyUnits, Array<Tower> enemyTowers) {
        Entity closestTarget = null;
        float minDistanceSq = Float.MAX_VALUE;

        // Tầm phát hiện được giảm từ 4x tầm đánh xuống còn tầm đánh + 50 pixels để tránh việc phát hiện quá sớm
        float detectionRange = attacker.getRange() + 50f; // Giảm từ 4x xuống còn +50 pixels
        float detectionRangeSq = detectionRange * detectionRange;
        // Tầm phát hiện tối thiểu cho lính cận chiến
        if (attacker.getRange() < 70) detectionRangeSq = 120*120;


        // 1. Kiểm tra Unit đối phương
        for (Unit enemy : enemyUnits) {
            if (!enemy.isAlive()) continue;
            float distSq = Vector2.dst2(attacker.getX(), attacker.getY(), enemy.getX(), enemy.getY());
            // Ưu tiên mục tiêu gần nhất trong tầm phát hiện và có thể nhìn thấy được
            if (distSq < minDistanceSq && distSq <= detectionRangeSq && hasLineOfSight(attacker, enemy, enemyUnits, enemyTowers)) {
                minDistanceSq = distSq;
                closestTarget = enemy;
            }
        }

        // 2. Kiểm tra Tower đối phương
        for (Tower enemyTower : enemyTowers) {
            if (!enemyTower.isAlive()) continue;
            float distSq = Vector2.dst2(attacker.getX(), attacker.getY(), enemyTower.getX(), enemyTower.getY());
            // Chỉ chọn Tower nếu nó gần hơn Unit đã tìm thấy (hoặc chưa tìm thấy Unit) và trong tầm phát hiện và có thể nhìn thấy được
            if (distSq < minDistanceSq && distSq <= detectionRangeSq && hasLineOfSight(attacker, enemyTower, enemyUnits, enemyTowers)) {
                minDistanceSq = distSq;
                closestTarget = enemyTower;
            }
        }
        // Logic tấn công căn cứ được xử lý ở nơi khác khi closestTarget là null

        return closestTarget; // Có thể là null
    }

    /**
     * Tìm Unit đối phương gần nhất trong tầm bắn thực tế của Tower.
     * @param tower Tower đang tìm mục tiêu.
     * @param enemyUnits Danh sách Unit đối phương.
     * @return Unit đối phương gần nhất trong tầm bắn, hoặc null.
     */
    public Unit findTargetForTower(Tower tower, Array<Unit> enemyUnits) {
        Unit closestTarget = null;
        float minDistanceSq = Float.MAX_VALUE; // Bắt đầu với khoảng cách lớn nhất
        float towerRangeSq = tower.getRange() * tower.getRange(); // Chỉ kiểm tra trong tầm bắn thực tế

        for (Unit enemy : enemyUnits) {
            if (!enemy.isAlive()) continue;
            float distSq = Vector2.dst2(tower.getX(), tower.getY(), enemy.getX(), enemy.getY());
            // Nếu trong tầm bắn VÀ gần hơn mục tiêu hiện tại
            if (distSq <= towerRangeSq && distSq < minDistanceSq) {
                minDistanceSq = distSq;
                closestTarget = enemy;
            }
        }
        return closestTarget;
    }

    /**
     * Thực hiện hành động tấn công từ attacker tới target.
     * @param attacker Thực thể tấn công.
     * @param target Thực thể bị tấn công.
     * @param xpReceiver Người chơi nhận XP nếu mục tiêu bị tiêu diệt.
     * @return true nếu mục tiêu bị tiêu diệt bởi đòn đánh này, false nếu không.
     */
    public boolean resolveAttack(Entity attacker, Entity target, Player xpReceiver) {
        boolean targetKilled = false;
        if (target != null && target.isAlive()) {
            target.takeDamage(attacker.getDamage()); // Gây sát thương
            attacker.resetAttackCooldown(); // Đặt lại thời gian hồi chiêu sau khi tấn công

            // Bắt đầu animation tấn công cho Unit
            if (attacker instanceof Unit) {
                ((Unit) attacker).startAttackAnimation();
            }

            // Gdx.app.debug("CombatSystem", attacker.getClass().getSimpleName() + " tấn công " + target.getClass().getSimpleName() + " gây " + attacker.getDamage() + " sát thương.");

            if (!target.isAlive()) { // Nếu mục tiêu chết sau đòn đánh
                targetKilled = true;
                // Cộng XP cho người chơi sở hữu attacker
                int xpValue = 0;
                if (target instanceof Unit) {
                    xpValue = UnitConfig.getUnitXpReward(((Unit)target).getType());
                } else if (target instanceof Tower) {
                    xpValue = TowerConfig.getTowerXpReward(((Tower)target).getType());
                }
                xpReceiver.addExperience(xpValue);
                Gdx.app.debug("CombatSystem", xpReceiver.getType() + " nhận được " + xpValue + " XP vì đã phá hủy " + target.getClass().getSimpleName());

                // Mục tiêu đã chết, attacker cần tìm mục tiêu mới
                attacker.setTarget(null);
            }
        }
        return targetKilled;
    }

    /**
     * Thực hiện hành động tấn công căn cứ đối phương.
     * @param attacker Unit tấn công.
     * @param enemyPlayer Player đối phương (chủ sở hữu căn cứ).
     */
    public void attackBase(Unit attacker, Player enemyPlayer) {
        enemyPlayer.takeDamage(attacker.getDamage()); // Gây sát thương cho căn cứ
        attacker.resetAttackCooldown(); // Đặt lại hồi chiêu
        attacker.startAttackAnimation(); // Bắt đầu animation tấn công
        Gdx.app.debug("CombatSystem", attacker.getOwnerType() + " " + attacker.getType() + " tấn công căn cứ " + enemyPlayer.getType() + " gây " + attacker.getDamage() + " sát thương.");
    }

    /**
     * Kiểm tra xem attacker có thể nhìn thấy target không (không bị cản bởi đồng đội).
     * @param attacker Unit đang tìm mục tiêu.
     * @param target Mục tiêu cần kiểm tra.
     * @param enemyUnits Units của địch để kiểm tra va chạm.
     * @param enemyTowers Towers của địch để kiểm tra va chạm.
     * @return true nếu có thể nhìn thấy target, false nếu bị cản.
     */
    private boolean hasLineOfSight(Unit attacker, Entity target, Array<Unit> enemyUnits, Array<Tower> enemyTowers) {
        // Chỉ kiểm tra line of sight cho unit cận chiến (tầm đánh nhỏ)
        if (attacker.getRange() > 100) return true; // Unit tầm xa không cần kiểm tra line of sight

        float attackerX = attacker.getX();
        float targetX = target.getX();

        // Nếu attacker đang ở phía sau target (theo hướng tiến quân), không được tấn công
        if (attacker.getOwnerType() == PlayerType.PLAYER) {
            // Player units di chuyển từ trái sang phải
            if (attackerX < targetX - 30) return false; // Cần ở gần hoặc phía trước target
        } else {
            // AI units di chuyển từ phải sang trái
            if (attackerX > targetX + 30) return false; // Cần ở gần hoặc phía trước target
        }

        // NOTE: Tạm thời bỏ qua việc kiểm tra đồng đội cản đường vì cần thêm tham số
        // Có thể thêm logic này sau nếu cần
        return true; // Không bị cản, có thể tấn công
    }
}
