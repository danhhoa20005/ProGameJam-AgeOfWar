/**
 * Lớp này chứa các cấu hình và định nghĩa chỉ số cho các đơn vị lính (Units) trong trò chơi.
 * Nó bao gồm chi phí, máu, sát thương, tốc độ tấn công, tầm đánh, tốc độ di chuyển,
 * phần thưởng kinh nghiệm, kích thước và các phương thức trợ giúp liên quan đến Unit theo từng kỷ nguyên.
 */
package com.ageofwar.configs;

import com.badlogic.gdx.utils.Array;
import com.ageofwar.models.Era;
import com.ageofwar.models.units.UnitType;

public class UnitConfig {

    // --- Kích thước Mặc định (Có thể di chuyển sang lớp cấu hình chung hơn) ---
    private static final float DEFAULT_UNIT_WIDTH = 40;
    private static final float DEFAULT_UNIT_HEIGHT = 60;

    // --- Định nghĩa Unit ---

    /**
     * Lấy kỷ nguyên yêu cầu để tạo ra một loại Unit cụ thể.
     * @param type Loại Unit.
     * @return Kỷ nguyên yêu cầu.
     */
    public static Era getUnitRequiredEra(UnitType type) {
        switch (type) {
            case CAVEMAN: case SLINGERMAN: case DINORIDER: return Era.STONE;
            case SWORDSMAN: case ARCHER: case KNIGHT: return Era.MEDIEVAL;
            case RIFLEMAN: case CANNON: case GRENADIER: return Era.INDUSTRIAL;
            case MARINE: case TANK: case HELICOPTER: return Era.MODERN; // HELICOPTER -> Tên lửa?
            case LASER_TROOPER: case MECH_WARRIOR: case CYBORG: return Era.FUTURE;
            default: return Era.STONE; // Mặc định là Kỷ Nguyên Đá
        }
    }

    /**
     * Lấy chi phí (vàng) để tạo ra một loại Unit cụ thể.
     * @param type Loại Unit.
     * @return Chi phí vàng. (CẦN CÂN BẰNG!)
     */
    public static int getUnitCost(UnitType type) {
        // Chi phí tạm thời - CẦN CÂN BẰNG!
        switch (type) {
            // Đá
            case CAVEMAN: return 50;
            case SLINGERMAN: return 75;
            case DINORIDER: return 150;
            // Trung Cổ
            case SWORDSMAN: return 100;
            case ARCHER: return 125;
            case KNIGHT: return 300;
            // Công Nghiệp
            case RIFLEMAN: return 150;
            case CANNON: return 400;
            case GRENADIER: return 250;
            // Hiện Đại
            case MARINE: return 200;
            case TANK: return 600;
            case HELICOPTER: return 500; // Chi phí Tên Lửa
            // Tương Lai
            case LASER_TROOPER: return 300;
            case MECH_WARRIOR: return 800;
            case CYBORG: return 600;
            default: return 100;
        }
    }

    /**
     * Lấy lượng máu (HP) của một loại Unit cụ thể.
     * @param type Loại Unit.
     * @return Lượng máu. (CẦN CÂN BẰNG!)
     */
    public static int getUnitHealth(UnitType type) {
        // HP tạm thời - CẦN CÂN BẰNG!
        switch (type) {
            case CAVEMAN: return 100;
            case SLINGERMAN: return 70;
            case DINORIDER: return 250;
            case SWORDSMAN: return 180;
            case ARCHER: return 100;
            case KNIGHT: return 400;
            case RIFLEMAN: return 150;
            case CANNON: return 300; // Máu thấp, sát thương cao?
            case GRENADIER: return 180;
            case MARINE: return 220;
            case TANK: return 1000;
            case HELICOPTER: return 400; // HP Tên Lửa
            case LASER_TROOPER: return 250;
            case MECH_WARRIOR: return 1500;
            case CYBORG: return 500;
            default: return 100;
        }
    }

    /**
     * Lấy lượng sát thương gây ra bởi một loại Unit cụ thể mỗi đòn tấn công.
     * @param type Loại Unit.
     * @return Lượng sát thương. (CẦN CÂN BẰNG!)
     */
    public static int getUnitDamage(UnitType type) {
        // Sát thương tạm thời - CẦN CÂN BẰNG!
        switch (type) {
            case CAVEMAN: return 15;
            case SLINGERMAN: return 10;
            case DINORIDER: return 30;
            case SWORDSMAN: return 25;
            case ARCHER: return 18;
            case KNIGHT: return 40;
            case RIFLEMAN: return 20;
            case CANNON: return 100; // Sát thương cao
            case GRENADIER: return 40; // Sát thương diện rộng (AoE)? (Chưa triển khai)
            case MARINE: return 30;
            case TANK: return 80;
            case HELICOPTER: return 60; // Sát thương Tên Lửa
            case LASER_TROOPER: return 45;
            case MECH_WARRIOR: return 120;
            case CYBORG: return 70;
            default: return 10;
        }
    }

    /**
     * Lấy tốc độ tấn công (số đòn đánh mỗi giây) của một loại Unit cụ thể.
     * @param type Loại Unit.
     * @return Tốc độ tấn công. (CẦN CÂN BẰNG!)
     */
    public static float getUnitAttackSpeed(UnitType type) {
        // Số đòn đánh mỗi giây - CẦN CÂN BẰNG!
        switch (type) {
            case CAVEMAN: return 1.0f;
            case SLINGERMAN: return 0.8f;
            case DINORIDER: return 1.2f;
            case SWORDSMAN: return 1.1f;
            case ARCHER: return 0.9f;
            case KNIGHT: return 0.8f;
            case RIFLEMAN: return 1.5f; // Tốc độ bắn nhanh hơn
            case CANNON: return 0.3f; // Tốc độ bắn chậm
            case GRENADIER: return 0.7f;
            case MARINE: return 1.4f;
            case TANK: return 0.6f;
            case HELICOPTER: return 0.8f; // Tốc độ Tên Lửa
            case LASER_TROOPER: return 1.8f;
            case MECH_WARRIOR: return 0.5f;
            case CYBORG: return 1.2f;
            default: return 1.0f;
        }
    }

    /**
     * Lấy tầm đánh của một loại Unit cụ thể.
     * @param type Loại Unit.
     * @return Tầm đánh (pixels). (CẦN CÂN BẰNG!)
     */
    public static float getUnitRange(UnitType type) {
        // Tầm đánh tạm thời - CẦN CÂN BẰNG!
        switch (type) {
            // Unit cận chiến có tầm đánh ngắn
            case CAVEMAN: return 50f;
            case DINORIDER: return 60f;
            case SWORDSMAN: return 55f;
            case KNIGHT: return 65f;
            case MECH_WARRIOR: return 80f; // Cận chiến hạng nặng có thể có tầm với xa hơn một chút

            // Unit đánh xa
            case SLINGERMAN: return 150f;
            case ARCHER: return 200f;
            case RIFLEMAN: return 220f;
            case CANNON: return 300f;
            case GRENADIER: return 180f;
            case MARINE: return 230f;
            case TANK: return 250f; // Tầm bắn của pháo tăng
            case HELICOPTER: return 280f; // Tầm bắn Tên Lửa
            case LASER_TROOPER: return 260f;
            case CYBORG: return 240f;
            default: return 100f;
        }
    }

    /**
     * Lấy tốc độ di chuyển (pixels mỗi giây) của một loại Unit cụ thể.
     * @param type Loại Unit.
     * @return Tốc độ di chuyển. (CẦN CÂN BẰNG!)
     */
    public static float getUnitMoveSpeed(UnitType type) {
        // Pixels mỗi giây - CẦN CÂN BẰNG!
        switch (type) {
            case CAVEMAN: return 60f;
            case SLINGERMAN: return 55f;
            case DINORIDER: return 90f; // Nhanh hơn
            case SWORDSMAN: return 70f;
            case ARCHER: return 65f;
            case KNIGHT: return 50f; // Chậm hơn, trâu bò
            case RIFLEMAN: return 75f;
            case CANNON: return 40f; // Rất chậm
            case GRENADIER: return 60f;
            case MARINE: return 70f;
            case TANK: return 45f; // Xe cộ chậm
            case HELICOPTER: return 80f; // Tốc độ Tên Lửa (unit mặt đất)
            case LASER_TROOPER: return 75f;
            case MECH_WARRIOR: return 40f; // Robot hạng nặng đi chậm
            case CYBORG: return 90f; // Nhanh
            default: return 60f;
        }
    }

    /**
     * Lấy lượng kinh nghiệm (XP) nhận được khi tiêu diệt một loại Unit cụ thể.
     * @param type Loại Unit.
     * @return Lượng XP thưởng. (CẦN CÂN BẰNG!)
     */
    public static int getUnitXpReward(UnitType type) {
        // XP nhận được khi unit này bị tiêu diệt - CẦN CÂN BẰNG!
        // Tỷ lệ gần đúng với chi phí/độ khó
        return getUnitCost(type) / 5; // Điểm khởi đầu đơn giản
    }

    /**
     * Lấy chiều rộng của một loại Unit cụ thể.
     * @param type Loại Unit.
     * @return Chiều rộng (pixels).
     */
    public static float getUnitWidth(UnitType type) {
        // Có thể tùy chỉnh kích thước cho từng loại unit sau
        if (type == UnitType.TANK || type == UnitType.MECH_WARRIOR || type == UnitType.CANNON) return DEFAULT_UNIT_WIDTH * 1.5f;
        return DEFAULT_UNIT_WIDTH;
    }

    /**
     * Lấy chiều cao của một loại Unit cụ thể.
     * @param type Loại Unit.
     * @return Chiều cao (pixels).
     */
    public static float getUnitHeight(UnitType type) {
        if (type == UnitType.TANK || type == UnitType.MECH_WARRIOR || type == UnitType.CANNON) return DEFAULT_UNIT_HEIGHT * 1.2f;
        return DEFAULT_UNIT_HEIGHT;
    }


    // --- Phương thức Trợ giúp ---

    // Mảng chứa các loại Unit cho từng kỷ nguyên
    private static final Array<UnitType> stoneUnits = Array.with(UnitType.CAVEMAN, UnitType.SLINGERMAN, UnitType.DINORIDER);
    private static final Array<UnitType> medievalUnits = Array.with(UnitType.SWORDSMAN, UnitType.ARCHER, UnitType.KNIGHT);
    private static final Array<UnitType> industrialUnits = Array.with(UnitType.RIFLEMAN, UnitType.CANNON, UnitType.GRENADIER);
    private static final Array<UnitType> modernUnits = Array.with(UnitType.MARINE, UnitType.TANK, UnitType.HELICOPTER); // HELICOPTER -> Tên lửa
    private static final Array<UnitType> futureUnits = Array.with(UnitType.LASER_TROOPER, UnitType.MECH_WARRIOR, UnitType.CYBORG);

    /**
     * Lấy danh sách các loại Unit có sẵn cho một kỷ nguyên cụ thể.
     * @param era Kỷ nguyên.
     * @return Mảng chứa các UnitType.
     */
    public static Array<UnitType> getUnitsForEra(Era era) {
        switch (era) {
            case STONE: return stoneUnits;
            case MEDIEVAL: return medievalUnits;
            case INDUSTRIAL: return industrialUnits;
            case MODERN: return modernUnits;
            case FUTURE: return futureUnits;
            default: return new Array<>(); // Trả về mảng rỗng nếu không hợp lệ
        }
    }

    /**
     * Lấy một loại Unit ngẫu nhiên mà AI có thể xây dựng trong kỷ nguyên hiện tại của nó.
     * @param era Kỷ nguyên của AI.
     * @return Một UnitType ngẫu nhiên, hoặc null nếu không có Unit nào khả dụng.
     */
    public static UnitType getRandomUnitForEra(Era era) {
        Array<UnitType> availableUnits = getUnitsForEra(era);
        if (availableUnits.size == 0) return null;
        return availableUnits.random(); // Lấy một unit ngẫu nhiên từ danh sách
    }
}
