package com.ageofwar.systems; // Đặt vào package systems mới

import com.ageofwar.configs.GameConfig;
import com.ageofwar.configs.TowerConfig;
import com.ageofwar.configs.UnitConfig;
import com.ageofwar.models.Era;
import com.ageofwar.models.players.Player;
import com.ageofwar.models.players.PlayerType;
import com.ageofwar.models.towers.Tower;
import com.ageofwar.models.units.Unit;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

/**
 * Hệ thống SpecialAbilitySystem quản lý việc kích hoạt và thực thi
 * hiệu ứng của các kỹ năng đặc biệt theo từng kỷ nguyên.
 */
public class SpecialAbilitySystem {

    // Tham chiếu đến các danh sách và pools cần thiết
    private Array<Unit> playerUnits;
    private Array<Unit> aiUnits;
    private Array<Tower> playerTowers;
    private Array<Tower> aiTowers;
    private Pool<Unit> unitPool;
    private Pool<Tower> towerPool;

    /**
     * Thiết lập các tham chiếu cần thiết cho hệ thống.
     * @param playerUnits Danh sách unit của người chơi.
     * @param aiUnits Danh sách unit của AI.
     * @param playerTowers Danh sách tower của người chơi.
     * @param aiTowers Danh sách tower của AI.
     * @param unitPool Pool Unit.
     * @param towerPool Pool Tower.
     */
    public void initialize(Array<Unit> playerUnits, Array<Unit> aiUnits, Array<Tower> playerTowers, Array<Tower> aiTowers, Pool<Unit> unitPool, Pool<Tower> towerPool) {
        this.playerUnits = playerUnits;
        this.aiUnits = aiUnits;
        this.playerTowers = playerTowers;
        this.aiTowers = aiTowers;
        this.unitPool = unitPool;
        this.towerPool = towerPool;
    }


    /**
     * Kích hoạt hiệu ứng của kỹ năng đặc biệt dựa trên kỷ nguyên.
     * @param ownerType Người chơi sử dụng kỹ năng.
     * @param era Kỷ nguyên của kỹ năng.
     * @param targetPlayer Người chơi đối phương (mục tiêu của kỹ năng).
     * @param xpReceiver Người chơi nhận XP khi tiêu diệt bằng kỹ năng đặc biệt.
     */
    public void activate(PlayerType ownerType, Era era, Player targetPlayer, Player xpReceiver) {
        Gdx.app.log("SpecialAbilitySystem", ownerType + " kích hoạt kỹ năng đặc biệt cho Kỷ Nguyên: " + era); // Ghi log kích hoạt
        Array<Unit> targetUnits = (ownerType == PlayerType.PLAYER) ? aiUnits : playerUnits; // Xác định danh sách unit mục tiêu
        Array<Tower> targetTowers = (ownerType == PlayerType.PLAYER) ? aiTowers : playerTowers; // Xác định danh sách tower mục tiêu

        switch (era) {
            case STONE: // Mưa Thiên Thạch (Sát thương diện rộng)
                applyAreaDamage(targetPlayer.getBasePositionX() - 200, 300, targetUnits, targetTowers, targetPlayer, GameConfig.STONE_SPECIAL_DAMAGE, xpReceiver);
                break;
            case MEDIEVAL: // Mưa Tên (Sát thương nhiều unit)
                applyDamageToMultipleUnits(targetUnits, GameConfig.MEDIEVAL_SPECIAL_TARGETS, GameConfig.MEDIEVAL_SPECIAL_DAMAGE, xpReceiver);
                break;
            case INDUSTRIAL: // Pháo Kích (Sát thương diện rộng)
                applyAreaDamage(targetPlayer.getBasePositionX() - 250, 400, targetUnits, targetTowers, targetPlayer, GameConfig.INDUSTRIAL_SPECIAL_DAMAGE, xpReceiver);
                Gdx.app.log("SpecialAbilitySystem", "Kỹ năng đặc biệt Công Nghiệp (Pháo Kích) đã kích hoạt.");
                break;
            case MODERN: // Không Kích (Sát thương cao)
                applyAreaDamage(targetPlayer.getBasePositionX() - 300, 150, targetUnits, targetTowers, targetPlayer, GameConfig.MODERN_SPECIAL_DAMAGE, xpReceiver);
                break;
            case FUTURE: // Pháo Quỹ Đạo (Sát thương cực lớn)
                applyAreaDamage(targetPlayer.getBasePositionX() - 350, 500, targetUnits, targetTowers, targetPlayer, GameConfig.FUTURE_SPECIAL_DAMAGE, xpReceiver);
                break;
        }
        // Kích hoạt hiệu ứng hình ảnh có thể được thực hiện ở đây hoặc thông qua hệ thống sự kiện
    }

    /**
     * Helper: Áp dụng sát thương diện rộng cho kỹ năng đặc biệt.
     * @param targetCenterX Tọa độ X trung tâm của vùng ảnh hưởng.
     * @param areaWidth Chiều rộng của vùng ảnh hưởng.
     * @param units Danh sách Unit mục tiêu.
     * @param towers Danh sách Tower mục tiêu.
     * @param baseOwner Player sở hữu căn cứ có thể bị ảnh hưởng.
     * @param damage Lượng sát thương gây ra.
     * @param xpReceiver Người chơi nhận XP.
     */
    private void applyAreaDamage(float targetCenterX, float areaWidth, Array<Unit> units, Array<Tower> towers, Player baseOwner, int damage, Player xpReceiver) {
        float startX = targetCenterX - areaWidth / 2f;
        float endX = targetCenterX + areaWidth / 2f;
        Gdx.app.debug("SpecialAbilitySystem", "Áp dụng sát thương diện rộng [" + startX + " - " + endX + "] gây " + damage + " dmg.");


        // Gây sát thương cho units trong vùng
        for (int i = units.size - 1; i >= 0; i--) {
            Unit unit = units.get(i);
            if (unit.getX() >= startX && unit.getX() <= endX) {
                unit.takeDamage(damage);
                if (!unit.isAlive()) {
                    int xpValue = UnitConfig.getUnitXpReward(unit.getType());
                    xpReceiver.addExperience(xpValue);
                    Gdx.app.debug("SpecialAbilitySystem", xpReceiver.getType() + " nhận được " + xpValue + " XP từ kỹ năng đặc biệt (Unit).");
                    units.removeIndex(i);
                    unitPool.free(unit); // Sử dụng pool đã lưu trữ
                }
            }
        }

        // Gây sát thương cho towers trong vùng
        for (int i = towers.size - 1; i >= 0; i--) {
            Tower tower = towers.get(i);
            if (tower.getX() >= startX && tower.getX() <= endX) {
                tower.takeDamage(damage);
                if (!tower.isAlive()) {
                    int xpValue = TowerConfig.getTowerXpReward(tower.getType());
                    xpReceiver.addExperience(xpValue);
                    Gdx.app.debug("SpecialAbilitySystem", xpReceiver.getType() + " nhận được " + xpValue + " XP từ kỹ năng đặc biệt (Tower).");
                    towers.removeIndex(i);
                    towerPool.free(tower); // Sử dụng pool đã lưu trữ
                }
            }
        }

        // Gây sát thương cho căn cứ nếu trong vùng
        if (baseOwner.getBasePositionX() >= startX && baseOwner.getBasePositionX() <= endX) {
            baseOwner.takeDamage(damage);
            Gdx.app.debug("SpecialAbilitySystem", "Căn cứ " + baseOwner.getType() + " nhận " + damage + " sát thương từ kỹ năng đặc biệt.");
        }
    }

    /**
     * Helper: Áp dụng sát thương cho nhiều mục tiêu Unit ngẫu nhiên.
     * @param units Danh sách Unit mục tiêu.
     * @param maxTargets Số lượng mục tiêu tối đa.
     * @param damage Lượng sát thương mỗi mục tiêu.
     * @param xpReceiver Người chơi nhận XP.
     */
    private void applyDamageToMultipleUnits(Array<Unit> units, int maxTargets, int damage, Player xpReceiver) {
        int targetsHit = 0;
        for (int i = units.size - 1; i >= 0 && targetsHit < maxTargets; i--) {
            Unit unit = units.get(i);
            unit.takeDamage(damage);
            targetsHit++;
            Gdx.app.debug("SpecialAbilitySystem", "Kỹ năng đa mục tiêu đánh trúng unit " + unit.getType() + " gây " + damage + " dmg.");
            if (!unit.isAlive()) {
                int xpValue = UnitConfig.getUnitXpReward(unit.getType());
                xpReceiver.addExperience(xpValue);
                Gdx.app.debug("SpecialAbilitySystem", xpReceiver.getType() + " nhận được " + xpValue + " XP từ kỹ năng đặc biệt (Multi-Unit).");
                units.removeIndex(i);
                unitPool.free(unit); // Sử dụng pool đã lưu trữ
            }
        }
        Gdx.app.log("SpecialAbilitySystem", "Kỹ năng đa mục tiêu đã đánh trúng " + targetsHit + " unit.");
    }
}
