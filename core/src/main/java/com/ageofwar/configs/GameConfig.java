/**
 * Lớp này chứa các cấu hình và hằng số cho trò chơi Age of War.
 * Nó định nghĩa các giá trị ban đầu, thông số kinh tế, chi phí nâng cấp, giới hạn,
 * khả năng đặc biệt, cài đặt AI và bố cục thế giới trò chơi.
 */
package com.ageofwar.configs;

import com.ageofwar.models.Era;
import com.ageofwar.screens.GameScreen;

public class GameConfig {

    // --- Chỉ số Cơ bản ---
    public static final int BASE_START_HP_PLAYER = 2000; // Máu khởi đầu của người chơi
    public static final int BASE_START_HP_AI = 2000; // Máu khởi đầu của AI (Điều chỉnh theo độ khó sau)
    public static final int BASE_HP_UPGRADE_BONUS = 500; // Lượng máu nhận được khi nâng cấp kỷ nguyên

    // --- Điều kiện Khởi đầu ---
    public static final int STARTING_GOLD = 500; // Vàng khởi đầu
    public static final int STARTING_XP = 0; // Kinh nghiệm khởi đầu

    // --- Kinh tế ---
    public static final float GOLD_GENERATION_INTERVAL = 1.0f; // Giây (Thời gian giữa mỗi lần tạo vàng)
    public static final int GOLD_PER_INTERVAL = 25; // Lượng vàng mỗi lần tạo

    // --- Tiến trình Kỷ nguyên ---
    public static int getEraUpgradeCost(Era currentEra) {
        // Trả về chi phí nâng cấp kỷ nguyên dựa trên kỷ nguyên hiện tại (tính bằng XP)
        switch (currentEra) {
            case STONE: return 500; // XP để lên Trung Cổ
            case MEDIEVAL: return 1500; // XP để lên Công Nghiệp
            case INDUSTRIAL: return 3000; // XP để lên Hiện Đại
            case MODERN: return 5000; // XP để lên Tương Lai
            case FUTURE: return -1; // Không thể nâng cấp thêm
            default: return -1;
        }
    }

    // --- Giới hạn Trụ ---
    public static int getMaxTowersForEra(Era currentEra) {
        // Ví dụ: Cho phép nhiều trụ hơn ở các kỷ nguyên sau
        switch (currentEra) {
            case STONE: return 3;
            case MEDIEVAL: return 4;
            case INDUSTRIAL: return 4;
            case MODERN: return 5;
            case FUTURE: return 5;
            default: return 3;
        }
    }


    // --- Khả năng Đặc biệt ---
    public static int getSpecialAbilityCost(Era currentEra) {
        // Chi phí ví dụ (tính bằng Vàng)
        switch (currentEra) {
            case STONE: return 500;
            case MEDIEVAL: return 1000;
            case INDUSTRIAL: return 1500;
            case MODERN: return 2000;
            case FUTURE: return 3000;
            default: return 9999; // Giá trị mặc định hoặc không hợp lệ
        }
    }

    public static float getSpecialAbilityCooldown(Era currentEra) {
        // Thời gian hồi chiêu ví dụ (tính bằng giây)
        switch (currentEra) {
            case STONE: return 60f;
            case MEDIEVAL: return 70f;
            case INDUSTRIAL: return 80f;
            case MODERN: return 90f;
            case FUTURE: return 100f;
            default: return 60f;
        }
    }

    // Hiệu ứng Khả năng Đặc biệt (Giá trị sát thương - cần cân bằng!)
    public static final int STONE_SPECIAL_DAMAGE = 300; // Sát thương đặc biệt Kỷ Nguyên Đá
    public static final int MEDIEVAL_SPECIAL_DAMAGE = 150; // Sát thương đặc biệt Kỷ Nguyên Trung Cổ (trên mỗi mục tiêu)
    public static final int MEDIEVAL_SPECIAL_TARGETS = 5; // Số mục tiêu tối đa của kỹ năng đặc biệt Trung Cổ
    public static final int INDUSTRIAL_SPECIAL_DAMAGE = 500; // Sát thương đặc biệt Kỷ Nguyên Công Nghiệp (sát thương diện rộng)
    public static final int MODERN_SPECIAL_DAMAGE = 800; // Sát thương đặc biệt Kỷ Nguyên Hiện Đại (sát thương diện rộng)
    public static final int FUTURE_SPECIAL_DAMAGE = 1500; // Sát thương đặc biệt Kỷ Nguyên Tương Lai (sát thương diện rộng)


    // --- Cài đặt AI ---
    public static final float AI_SPAWN_INTERVAL = 3.0f; // Tần suất AI cố gắng tạo lính (điều chỉnh theo độ khó)

    // --- Bố cục Thế giới ---
    public static final float GROUND_Y = 50; // Tọa độ Y của mặt đất
    public static final float PLAYER_BASE_X = 100; // Tọa độ X trung tâm của căn cứ người chơi
    public static float AI_BASE_X = GameScreen.WORLD_WIDTH - 100; // Tọa độ X trung tâm của căn cứ AI
    public static final float PLAYER_SPAWN_X = PLAYER_BASE_X + 60; // Nơi lính người chơi xuất hiện
    public static float AI_SPAWN_X = AI_BASE_X - 60; // Nơi lính AI xuất hiện

}
