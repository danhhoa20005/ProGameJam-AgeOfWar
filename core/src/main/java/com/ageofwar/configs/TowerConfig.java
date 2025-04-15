/**
 * Lớp này chứa các cấu hình và định nghĩa chỉ số cho các công trình phòng thủ (Towers) trong trò chơi.
 * Nó bao gồm chi phí, máu, sát thương, tốc độ tấn công, tầm bắn,
 * phần thưởng kinh nghiệm, kích thước và các phương thức trợ giúp liên quan đến Tower theo từng kỷ nguyên.
 */
package com.ageofwar.configs;

import com.badlogic.gdx.utils.Array;
import com.ageofwar.models.Era;
import com.ageofwar.models.towers.TowerType;

public class TowerConfig {

    // --- Kích thước Mặc định (Có thể di chuyển sang lớp cấu hình chung hơn) ---
    private static final float DEFAULT_TOWER_WIDTH = 50;
    private static final float DEFAULT_TOWER_HEIGHT = 80;

    // --- Định nghĩa Tower ---

    /**
     * Lấy kỷ nguyên yêu cầu để xây dựng một loại Tower cụ thể.
     * @param type Loại Tower.
     * @return Kỷ nguyên yêu cầu.
     */
    public static Era getTowerRequiredEra(TowerType type) {
        switch (type) {
            case ROCK_TOWER: case EGG_LAUNCHER: case WATCHTOWER: return Era.STONE;
            case ARROW_TOWER: case CATAPULT_TOWER: case BALLISTA_TOWER: return Era.MEDIEVAL;
            case GATLING_TOWER: case CANNON_TOWER: case MORTAR_TOWER: return Era.INDUSTRIAL;
            case MACHINEGUN_TURRET: case MISSILE_TURRET: case TESLA_COIL: return Era.MODERN;
            case LASER_TURRET: case PLASMA_TURRET: case RAILGUN_TURRET: return Era.FUTURE;
            default: return Era.STONE; // Mặc định là Kỷ Nguyên Đá
        }
    }

    /**
     * Lấy chi phí (vàng) để xây dựng một loại Tower cụ thể.
     * @param type Loại Tower.
     * @return Chi phí vàng. (CẦN CÂN BẰNG!)
     */
    public static int getTowerCost(TowerType type) {
        // Chi phí tạm thời - CẦN CÂN BẰNG!
        switch (type) {
            // Đá
            case ROCK_TOWER: return 100;
            case EGG_LAUNCHER: return 150;
            case WATCHTOWER: return 120;
            // Trung Cổ
            case ARROW_TOWER: return 200;
            case CATAPULT_TOWER: return 350;
            case BALLISTA_TOWER: return 400;
            // Công Nghiệp
            case GATLING_TOWER: return 300;
            case CANNON_TOWER: return 500;
            case MORTAR_TOWER: return 450;
            // Hiện Đại
            case MACHINEGUN_TURRET: return 400;
            case MISSILE_TURRET: return 700;
            case TESLA_COIL: return 600;
            // Tương Lai
            case LASER_TURRET: return 600;
            case PLASMA_TURRET: return 900;
            case RAILGUN_TURRET: return 1200;
            default: return 150;
        }
    }

    /**
     * Lấy lượng máu (HP) của một loại Tower cụ thể.
     * @param type Loại Tower.
     * @return Lượng máu. (CẦN CÂN BẰNG!)
     */
    public static int getTowerHealth(TowerType type) {
        // HP tạm thời - CẦN CÂN BẰNG!
        return getTowerCost(type) * 3; // Mối quan hệ đơn giản với chi phí
    }

    /**
     * Lấy lượng sát thương gây ra bởi một loại Tower cụ thể mỗi đòn tấn công.
     * @param type Loại Tower.
     * @return Lượng sát thương. (CẦN CÂN BẰNG!)
     */
    public static int getTowerDamage(TowerType type) {
        // Sát thương tạm thời - CẦN CÂN BẰNG!
        switch (type) {
            case ROCK_TOWER: return 15;
            case EGG_LAUNCHER: return 25; // AoE?
            case WATCHTOWER: return 12; // Nhanh hơn?
            case ARROW_TOWER: return 25;
            case CATAPULT_TOWER: return 60; // AoE
            case BALLISTA_TOWER: return 80; // Đơn mục tiêu cao
            case GATLING_TOWER: return 10; // Nhanh, thấp
            case CANNON_TOWER: return 100; // AoE cao
            case MORTAR_TOWER: return 70; // AoE
            case MACHINEGUN_TURRET: return 20; // Nhanh
            case MISSILE_TURRET: return 150; // Chậm, cao
            case TESLA_COIL: return 40; // Lan/AoE?
            case LASER_TURRET: return 50; // DPS cao?
            case PLASMA_TURRET: return 200; // Chậm, AoE cao
            case RAILGUN_TURRET: return 300; // Rất chậm, đơn mục tiêu cao
            default: return 20;
        }
    }

    /**
     * Lấy tốc độ tấn công (số đòn đánh mỗi giây) của một loại Tower cụ thể.
     * @param type Loại Tower.
     * @return Tốc độ tấn công. (CẦN CÂN BẰNG!)
     */
    public static float getTowerAttackSpeed(TowerType type) {
        // Số đòn đánh mỗi giây - CẦN CÂN BẰNG!
        switch (type) {
            case ROCK_TOWER: return 1.0f;
            case EGG_LAUNCHER: return 0.5f; // AoE chậm
            case WATCHTOWER: return 1.5f; // Cơ bản nhanh hơn
            case ARROW_TOWER: return 1.2f;
            case CATAPULT_TOWER: return 0.4f; // AoE chậm
            case BALLISTA_TOWER: return 0.3f; // Đơn mục tiêu rất chậm
            case GATLING_TOWER: return 4.0f; // Rất nhanh
            case CANNON_TOWER: return 0.5f; // AoE chậm
            case MORTAR_TOWER: return 0.6f; // AoE chậm
            case MACHINEGUN_TURRET: return 3.0f; // Nhanh
            case MISSILE_TURRET: return 0.4f; // Chậm, sát thương cao
            case TESLA_COIL: return 1.0f; // AoE/Lan vừa phải?
            case LASER_TURRET: return 2.0f; // Đại diện cho DPS cao
            case PLASMA_TURRET: return 0.3f; // AoE chậm
            case RAILGUN_TURRET: return 0.2f; // Đơn mục tiêu rất chậm
            default: return 1.0f;
        }
    }

    /**
     * Lấy tầm bắn của một loại Tower cụ thể.
     * @param type Loại Tower.
     * @return Tầm bắn (pixels). (CẦN CÂN BẰNG!)
     */
    public static float getTowerRange(TowerType type) {
        // Tầm bắn tạm thời - CẦN CÂN BẰNG!
        switch (type) {
            case ROCK_TOWER: return 200f;
            case EGG_LAUNCHER: return 250f;
            case WATCHTOWER: return 220f;
            case ARROW_TOWER: return 250f;
            case CATAPULT_TOWER: return 350f; // AoE tầm xa
            case BALLISTA_TOWER: return 400f; // Đơn mục tiêu tầm xa
            case GATLING_TOWER: return 180f; // Nhanh, tầm ngắn hơn
            case CANNON_TOWER: return 300f;
            case MORTAR_TOWER: return 400f; // Bắn gián tiếp tầm xa
            case MACHINEGUN_TURRET: return 220f;
            case MISSILE_TURRET: return 450f; // Tầm rất xa
            case TESLA_COIL: return 150f; // AoE/Lan tầm ngắn
            case LASER_TURRET: return 280f;
            case PLASMA_TURRET: return 350f;
            case RAILGUN_TURRET: return 500f; // Tầm xa nhất
            default: return 200f;
        }
    }

    /**
     * Lấy lượng kinh nghiệm (XP) nhận được khi phá hủy một loại Tower cụ thể.
     * @param type Loại Tower.
     * @return Lượng XP thưởng. (CẦN CÂN BẰNG!)
     */
    public static int getTowerXpReward(TowerType type) {
        // XP nhận được khi tower này bị phá hủy - CẦN CÂN BẰNG!
        return getTowerCost(type) / 4; // Điểm khởi đầu đơn giản
    }

    /**
     * Lấy chiều rộng của một loại Tower cụ thể.
     * @param type Loại Tower.
     * @return Chiều rộng (pixels).
     */
    public static float getTowerWidth(TowerType type) {
        // Có thể tùy chỉnh kích thước cho từng loại tower sau
        return DEFAULT_TOWER_WIDTH;
    }

    /**
     * Lấy chiều cao của một loại Tower cụ thể.
     * @param type Loại Tower.
     * @return Chiều cao (pixels).
     */
    public static float getTowerHeight(TowerType type) {
        if (type == TowerType.BALLISTA_TOWER || type == TowerType.RAILGUN_TURRET) return DEFAULT_TOWER_HEIGHT * 1.3f; // Các tower cao hơn
        return DEFAULT_TOWER_HEIGHT;
    }

    // --- Phương thức Trợ giúp ---

    // Mảng chứa các loại Tower cho từng kỷ nguyên
    private static final Array<TowerType> stoneTowers = Array.with(TowerType.ROCK_TOWER, TowerType.EGG_LAUNCHER, TowerType.WATCHTOWER);
    private static final Array<TowerType> medievalTowers = Array.with(TowerType.ARROW_TOWER, TowerType.CATAPULT_TOWER, TowerType.BALLISTA_TOWER);
    private static final Array<TowerType> industrialTowers = Array.with(TowerType.GATLING_TOWER, TowerType.CANNON_TOWER, TowerType.MORTAR_TOWER);
    private static final Array<TowerType> modernTowers = Array.with(TowerType.MACHINEGUN_TURRET, TowerType.MISSILE_TURRET, TowerType.TESLA_COIL);
    private static final Array<TowerType> futureTowers = Array.with(TowerType.LASER_TURRET, TowerType.PLASMA_TURRET, TowerType.RAILGUN_TURRET);

    /**
     * Lấy danh sách các loại Tower có sẵn cho một kỷ nguyên cụ thể.
     * @param era Kỷ nguyên.
     * @return Mảng chứa các TowerType.
     */
    public static Array<TowerType> getTowersForEra(Era era) {
        switch (era) {
            case STONE: return stoneTowers;
            case MEDIEVAL: return medievalTowers;
            case INDUSTRIAL: return industrialTowers;
            case MODERN: return modernTowers;
            case FUTURE: return futureTowers;
            default: return new Array<>(); // Trả về mảng rỗng nếu không hợp lệ
        }
    }
}
