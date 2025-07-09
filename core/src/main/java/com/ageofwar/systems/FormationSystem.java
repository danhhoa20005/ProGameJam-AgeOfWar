package com.ageofwar.systems;

import com.ageofwar.models.players.PlayerType;
import com.ageofwar.models.units.Unit;
import com.ageofwar.configs.GameConfig;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Hệ thống FormationSystem quản lý việc bố trí và positioning của units
 * để tránh việc các unit chồng lên nhau khi spawn và di chuyển.
 *
 * Hệ thống này sử dụng pattern grid-based formation để sắp xếp units
 * theo hàng và cột, tạo ra formation chiến thuật hợp lý.
 */
public class FormationSystem {

    // Khoảng cách giữa các unit trong formation
    private static final float UNIT_SPACING_X = 35f; // Khoảng cách ngang
    private static final int MAX_UNITS_PER_ROW = 4; // Số unit tối đa mỗi hàng

    // Offsets để tạo formation staggered (lệch hàng)
    private static final float ROW_OFFSET = 15f;

    /**
     * Tính toán vị trí spawn tối ưu cho unit mới để tránh collision
     * @param ownerType Loại người chơi (PLAYER hoặc AI)
     * @param existingUnits Danh sách units hiện có
     * @return Vector2 chứa tọa độ spawn tối ưu
     */
    public static Vector2 calculateSpawnPosition(PlayerType ownerType, Array<Unit> existingUnits) {
        float baseX = (ownerType == PlayerType.PLAYER) ? GameConfig.PLAYER_SPAWN_X : GameConfig.AI_SPAWN_X;
        float baseY = GameConfig.GROUND_Y;

        // Đếm số unit hiện có của cùng team - sử dụng index loop
        int unitCount = 0;
        for (int i = 0; i < existingUnits.size; i++) {
            Unit unit = existingUnits.get(i);
            if (unit.getOwnerType() == ownerType && unit.isAlive()) {
                unitCount++;
            }
        }

        Vector2 idealPosition = calculateFormationPosition(baseX, baseY, unitCount, ownerType);

        // Sử dụng CollisionSystem để tìm vị trí trống gần nhất
        float[] freePosition = CollisionSystem.findNearestFreePosition(
            idealPosition.x, idealPosition.y, existingUnits, 100f
        );

        return new Vector2(freePosition[0], freePosition[1]);
    }

    /**
     * Tính toán vị trí trong formation dựa trên chỉ số unit
     * @param baseX Tọa độ X gốc
     * @param baseY Tọa độ Y gốc (ground_y - tất cả units sẽ có cùng Y này)
     * @param unitIndex Chỉ số của unit trong formation
     * @param ownerType Loại người chơi để xác định hướng formation
     * @return Vector2 vị trí trong formation
     */
    private static Vector2 calculateFormationPosition(float baseX, float baseY, int unitIndex, PlayerType ownerType) {
        int row = unitIndex / MAX_UNITS_PER_ROW;
        int col = unitIndex % MAX_UNITS_PER_ROW;

        float x, y;

        // Tất cả units luôn có cùng Y = ground_y để đảm bảo khoảng cách với ground đều nhau
        y = baseY; // Không thay đổi Y, tất cả units đều spawn trên cùng ground level

        if (ownerType == PlayerType.PLAYER) {
            // Player units: spread towards the right in formation
            x = baseX + (col * UNIT_SPACING_X) + (row * UNIT_SPACING_X * 0.5f);

            // Stagger even rows for more natural formation
            if (row % 2 == 1) {
                x += ROW_OFFSET;
            }
        } else {
            // AI units: spread towards the left in formation
            x = baseX - (col * UNIT_SPACING_X) - (row * UNIT_SPACING_X * 0.5f);

            // Stagger even rows for more natural formation
            if (row % 2 == 1) {
                x -= ROW_OFFSET;
            }
        }

        return new Vector2(x, y);
    }

    /**
     * Cập nhật vị trí units để duy trì formation trong khi di chuyển
     * @param units Danh sách units cần cập nhật
     * @param delta Thời gian frame
     */
    public static void updateFormation(Array<Unit> units, float delta) {
        if (units.size <= 1) return; // Không cần formation nếu có ít hơn 2 units

        // Áp dụng separation force để tránh units quá gần nhau - sử dụng index loop để tránh nested iterator issue
        for (int i = 0; i < units.size; i++) {
            Unit unit = units.get(i);
            if (!unit.isAlive() || unit.isInDeathAnimation()) continue;

            float separationForceX = calculateSeparationForceX(unit, units);

            // Chỉ áp dụng separation force khi units đang không tấn công và chỉ theo chiều X
            if (!unit.isInAttackAnimation() && Math.abs(separationForceX) > 0) {
                float separationStrength = 30f * delta; // Độ mạnh của lực đẩy
                unit.move(separationForceX * separationStrength);
            }
        }
    }

    /**
     * Tính toán lực separation theo chiều X để tránh units quá gần nhau
     * Chỉ áp dụng separation theo chiều ngang, không thay đổi Y để giữ units trên ground
     */
    private static float calculateSeparationForceX(Unit unit, Array<Unit> units) {
        float forceX = 0f;
        float minDistance = 30f; // Khoảng cách tối thiểu giữa các units

        // Sử dụng index loop để tránh nested iterator issue
        for (int i = 0; i < units.size; i++) {
            Unit other = units.get(i);
            if (other == unit || !other.isAlive()) continue;

            float distance = Math.abs(unit.getX() - other.getX()); // Chỉ xét khoảng cách theo X

            if (distance < minDistance && distance > 0) {
                // Tính force theo chiều X
                float direction = unit.getX() > other.getX() ? 1f : -1f; // Hướng đẩy
                float strength = (minDistance - distance) / minDistance; // Strength tỷ lệ nghịch với khoảng cách

                forceX += direction * strength;
            }
        }

        return forceX;
    }

    /**
     * Kiểm tra xem vị trí có hợp lệ để spawn unit không
     * @param position Vị trí cần kiểm tra
     * @param existingUnits Danh sách units hiện có
     * @param minDistance Khoảng cách tối thiểu
     * @return true nếu vị trí hợp lệ
     */
    public static boolean isValidSpawnPosition(Vector2 position, Array<Unit> existingUnits, float minDistance) {
        for (Unit unit : existingUnits) {
            if (!unit.isAlive()) continue;

            float distance = Vector2.dst(position.x, position.y, unit.getX(), unit.getY());
            if (distance < minDistance) {
                return false;
            }
        }
        return true;
    }
}
