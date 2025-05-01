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
        // Khởi tạo dữ liệu cho UnitType CAVEMAN
        Map<UnitState, String> cavemen = new HashMap<>();
        cavemen.put(UnitState.ATTACK, "assets/ui/Orc/Orc1/orc1_Attack.png");
        cavemen.put(UnitState.DEATH, "assets/ui/Orc/Orc1/orc1_Death.png");
        cavemen.put(UnitState.HURT, "assets/ui/Orc/Orc1/orc1_Hurt.png");
        cavemen.put(UnitState.IDLE, "assets/ui/Orc/Orc1/orc1_Idle.png");
        cavemen.put(UnitState.WALK, "assets/ui/Orc/Orc1/orc1_Walk.png");

        assetMap.put(UnitType.CAVEMAN, cavemen);

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
