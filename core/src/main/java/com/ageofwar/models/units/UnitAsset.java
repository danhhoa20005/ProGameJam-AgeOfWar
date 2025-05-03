package com.ageofwar.models.units;

import com.ageofwar.models.players.PlayerType;

import java.util.HashMap;
import java.util.Map;

public class UnitAsset {

    private Map<UnitType, Map<UnitState, String>> assetMap;

    public UnitAsset() {
        assetMap = new HashMap<>(); // Khởi tạo assetMap
        this.initialize();
    }

    public void initialize() {
        // Khởi tạo dữ liệu cho UnitType ORC
        Map<UnitState, String> orcAssets = new HashMap<>();
        orcAssets.put(UnitState.ATTACK, "ui/Orc/Orc1/Attack.png");
        orcAssets.put(UnitState.DEATH,  "ui/Orc/Orc1/Death.png");
        orcAssets.put(UnitState.HURT,   "ui/Orc/Orc1/Hurt.png");
        orcAssets.put(UnitState.IDLE,   "ui/Orc/Orc1/Idle.png");
        orcAssets.put(UnitState.WALK,   "ui/Orc/Orc1/Walk.png");
        assetMap.put(UnitType.ORC, orcAssets);

        // Khởi tạo dữ liệu cho UnitType PREDATOR_PLANT
        Map<UnitState, String> predatorPlantAssets = new HashMap<>();
        predatorPlantAssets.put(UnitState.ATTACK, "ui/PredatorPlant/Plant1/Attack.png");
        predatorPlantAssets.put(UnitState.DEATH,  "ui/PredatorPlant/Plant1/Death.png");
        predatorPlantAssets.put(UnitState.HURT,   "ui/PredatorPlant/Plant1/Hurt.png");
        predatorPlantAssets.put(UnitState.IDLE,   "ui/PredatorPlant/Plant1/Idle.png");
        predatorPlantAssets.put(UnitState.WALK,   "ui/PredatorPlant/Plant1/Walk.png");
        assetMap.put(UnitType.PREDATOR_PLANT, predatorPlantAssets);

        // Khởi tạo dữ liệu cho UnitType SLIME
        Map<UnitState, String> slimeAssets = new HashMap<>();
        slimeAssets.put(UnitState.ATTACK, "ui/Slime/Slime1/Attack.png");
        slimeAssets.put(UnitState.DEATH,  "ui/Slime/Slime1/Death.png");
        slimeAssets.put(UnitState.HURT,   "ui/Slime/Slime1/Hurt.png");
        slimeAssets.put(UnitState.IDLE,   "ui/Slime/Slime1/Idle.png");
        slimeAssets.put(UnitState.WALK,   "ui/Slime/Slime1/Walk.png");
        assetMap.put(UnitType.SLIME, slimeAssets);

        // Khởi tạo dữ liệu cho UnitType VAMPIRE
        Map<UnitState, String> vampireAssets = new HashMap<>();
        vampireAssets.put(UnitState.ATTACK, "ui/Vampire/Vampires1/Attack.png");
        vampireAssets.put(UnitState.DEATH,  "ui/Vampire/Vampires1/Death.png");
        vampireAssets.put(UnitState.HURT,   "ui/Vampire/Vampires1/Hurt.png");
        vampireAssets.put(UnitState.IDLE,   "ui/Vampire/Vampires1/Idle.png");
        vampireAssets.put(UnitState.WALK,   "ui/Vampire/Vampires1/Walk.png");
        assetMap.put(UnitType.VAMPIRE, vampireAssets);

        // TODO: Thêm dữ liệu cho các UnitType khác tại đây
    }

    public String getAssetLink(UnitType type, UnitState state) {
        if (assetMap == null || assetMap.isEmpty()) {
            throw new IllegalStateException("Asset map chưa được khởi tạo. Hãy gọi phương thức initialize().");
        }
        Map<UnitState, String> stateMap = assetMap.get(type);
        if (stateMap == null) {
            throw new IllegalArgumentException("UnitType không hợp lệ: " + type);
        }
        String path = stateMap.get(state);
        if (path == null) {
            throw new IllegalArgumentException("UnitState không hợp lệ: " + state + " cho UnitType: " + type);
        }
        return path;
    }

    public int getAnimationRow(PlayerType playerType, UnitType unitType, UnitState state) {
        // Trả về hàng animation dựa trên PlayerType
        if (playerType == PlayerType.PLAYER) {
            return 4;
        } else {
            return 3;
        }
    }
}
