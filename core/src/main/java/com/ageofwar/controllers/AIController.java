package com.ageofwar.controllers; // Đặt vào package controllers hợp lý hơn

import com.ageofwar.configs.GameConfig;
import com.ageofwar.configs.UnitConfig; // Cần để lấy thông tin đơn vị
import com.ageofwar.models.Era;
import com.ageofwar.models.GameModel;
import com.ageofwar.models.players.Player;
import com.ageofwar.models.players.PlayerType;
import com.ageofwar.models.units.UnitType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

/**
 * Lớp AIController quản lý logic và hành vi của người chơi AI.
 * Nó quyết định khi nào cần sinh lính, nâng cấp kỷ nguyên, hoặc sử dụng kỹ năng đặc biệt
 * dựa trên trạng thái hiện tại của AI và các quy tắc được định nghĩa.
 */
public class AIController {

    private final Player aiPlayer; // Tham chiếu đến đối tượng Player của AI
    private final GameModel gameModel; // Tham chiếu đến GameModel để thực hiện hành động
    private float aiUpdateTimer = 0f; // Biến đếm thời gian cho các hành động định kỳ của AI

    /**
     * Khởi tạo AIController.
     * @param aiPlayer Đối tượng Player của AI.
     * @param gameModel Đối tượng GameModel chính.
     */
    public AIController(Player aiPlayer, GameModel gameModel) {
        this.aiPlayer = aiPlayer;
        this.gameModel = gameModel;
        this.aiUpdateTimer = 0f; // Reset timer khi khởi tạo
    }

    /**
     * Cập nhật logic quyết định của AI. Được gọi từ GameModel.update().
     * @param delta Thời gian trôi qua từ khung hình trước.
     */
    public void update(float delta) {
        // AI đơn giản: Sinh lính định kỳ nếu có thể
        aiUpdateTimer += delta;
        if (aiUpdateTimer >= GameConfig.AI_SPAWN_INTERVAL) {
            aiUpdateTimer -= GameConfig.AI_SPAWN_INTERVAL; // Reset timer bằng cách trừ đi khoảng thời gian

            // 1. Ưu tiên Nâng cấp Kỷ nguyên nếu có thể
            boolean upgraded = tryUpgradeEra();
            if (upgraded) {
                Gdx.app.log("AIController", "AI đã nâng cấp lên kỷ nguyên: " + aiPlayer.getCurrentEra()); // Ghi log: AI nâng cấp
                // Có thể thêm logic khác sau khi nâng cấp (ví dụ: ưu tiên xây trụ mới?)
            }

            // 2. Thử Sử dụng Kỹ năng Đặc biệt (nếu không nâng cấp và có khả năng)
            if (!upgraded) { // Chỉ thử dùng ulti nếu không vừa nâng cấp
                boolean usedSpecial = tryUseSpecialAbility();
                if (usedSpecial) {
                    Gdx.app.log("AIController", "AI đã sử dụng kỹ năng đặc biệt."); // Ghi log: AI dùng kỹ năng
                }
            }

            // 3. Thử Sinh Lính (luôn thử nếu có vàng)
            boolean spawned = trySpawnUnit();
            if (spawned) {
                // Gdx.app.debug("AIController", "AI đã sinh lính thành công."); // Gỡ lỗi (log đã có trong spawnUnit)
            }

            // 4. (Tùy chọn) Thử Xây Trụ
            // boolean builtTower = tryBuildTower();
            // if(builtTower) { ... }
        }
    }

    /**
     * AI thử sinh một đơn vị lính ngẫu nhiên có sẵn trong kỷ nguyên.
     * @return true nếu sinh thành công, false nếu không.
     */
    private boolean trySpawnUnit() {
        UnitType typeToSpawn = UnitConfig.getRandomUnitForEra(aiPlayer.getCurrentEra());
        if (typeToSpawn != null) {
            int cost = UnitConfig.getUnitCost(typeToSpawn);
            if (aiPlayer.getGold() >= cost) {
                // Gọi phương thức của GameModel để thực hiện hành động
                return gameModel.spawnUnit(PlayerType.AI, typeToSpawn);
            } else {
                Gdx.app.debug("AIController", "AI muốn sinh " + typeToSpawn + " nhưng cần " + cost + " vàng, hiện có " + aiPlayer.getGold()); // Gỡ lỗi: AI không đủ vàng
            }
        }
        return false;
    }

    /**
     * AI thử nâng cấp lên kỷ nguyên tiếp theo.
     * @return true nếu nâng cấp thành công, false nếu không.
     */
    private boolean tryUpgradeEra() {
        Era nextEra = Era.getNextEra(aiPlayer.getCurrentEra());
        if (nextEra != null) { // Kiểm tra xem có thể nâng cấp không
            int upgradeCost = GameConfig.getEraUpgradeCost(aiPlayer.getCurrentEra());
            if (upgradeCost > 0 && aiPlayer.getExperience() >= upgradeCost) {
                // Gọi phương thức của GameModel để thực hiện hành động
                return gameModel.upgradeEra(PlayerType.AI);
            }
        }
        return false;
    }

    /**
     * AI thử sử dụng kỹ năng đặc biệt.
     * @return true nếu sử dụng thành công, false nếu không.
     */
    private boolean tryUseSpecialAbility() {
        // AI đơn giản: Sử dụng Kỹ năng Đặc biệt nếu sẵn sàng và có vàng (tỷ lệ thấp?)
        if (aiPlayer.canUseSpecial() && aiPlayer.getGold() >= GameConfig.getSpecialAbilityCost(aiPlayer.getCurrentEra())) {
            if (MathUtils.random() < 0.1f) { // 10% cơ hội mỗi lần cập nhật AI để sử dụng nếu sẵn sàng
                // Gọi phương thức của GameModel để thực hiện hành động
                return gameModel.useSpecialAbility(PlayerType.AI);
            }
        }
        return false;
    }

    /**
     * (Ví dụ - Chưa dùng) AI thử xây một trụ ngẫu nhiên.
     * Cần logic phức tạp hơn để chọn vị trí xây.
     * @return true nếu xây thành công, false nếu không.
     */
    // private boolean tryBuildTower() {
    //     // Logic chọn loại trụ và vị trí xây dựng...
    //     // TowerType typeToBuild = ...;
    //     // float buildPositionX = ...;
    //     // if (aiPlayer.getGold() >= TowerConfig.getTowerCost(typeToBuild) && ...) {
    //     //     return gameModel.buildTower(PlayerType.AI, typeToBuild, buildPositionX);
    //     // }
    //     return false;
    // }
}
