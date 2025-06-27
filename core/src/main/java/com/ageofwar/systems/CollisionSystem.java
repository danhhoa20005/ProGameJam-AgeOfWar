package com.ageofwar.systems;

import com.ageofwar.models.units.Unit;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Hệ thống CollisionSystem xử lý việc phát hiện và giải quyết va chạm
 * giữa các units để tránh chồng lên nhau và tạo gameplay mượt mà.
 *
 * Sử dụng thuật toán spatial partitioning để tối ưu hóa hiệu suất
 * khi có nhiều units trên battlefield.
 */
public class CollisionSystem {

    // Khoảng cách tối thiểu giữa các units để tránh collision
    private static final float MIN_UNIT_DISTANCE = 25f;

    // Threshold để xác định khi nào cần collision resolution
    private static final float COLLISION_THRESHOLD = 20f;

    /**
     * Kiểm tra và giải quyết va chạm giữa các units trong một team
     * @param units Danh sách units cần kiểm tra
     * @param delta Thời gian frame để smooth movement
     */
    public static void resolveCollisions(Array<Unit> units, float delta) {
        if (units.size <= 1) return;

        // Sử dụng nested loop để kiểm tra từng cặp units
        for (int i = 0; i < units.size; i++) {
            Unit unitA = units.get(i);
            if (!unitA.isAlive()) continue;

            for (int j = i + 1; j < units.size; j++) {
                Unit unitB = units.get(j);
                if (!unitB.isAlive()) continue;

                resolveUnitCollision(unitA, unitB, delta);
            }
        }
    }

    /**
     * Giải quyết va chạm giữa hai units cụ thể, chỉ di chuyển theo chiều X để giữ units trên ground
     * @param unitA Unit thứ nhất
     * @param unitB Unit thứ hai
     * @param delta Thời gian frame
     */
    private static void resolveUnitCollision(Unit unitA, Unit unitB, float delta) {
        float distanceX = Math.abs(unitB.getX() - unitA.getX()); // Chỉ xét khoảng cách theo X

        if (distanceX < COLLISION_THRESHOLD && distanceX > 0) {
            // Tính toán hướng separation theo X
            float direction = unitB.getX() > unitA.getX() ? 1f : -1f;

            // Tính toán lực đẩy dựa trên độ gần
            float pushForce = (COLLISION_THRESHOLD - distanceX) / COLLISION_THRESHOLD;
            float moveAmount = pushForce * 50f * delta; // 50f là strength của push

            // Di chuyển các units ra xa nhau chỉ theo chiều X
            // UnitA di chuyển ngược hướng
            if (!unitA.isInAttackAnimation()) {
                unitA.move(-direction * moveAmount);
            }

            // UnitB di chuyển theo hướng
            if (!unitB.isInAttackAnimation()) {
                unitB.move(direction * moveAmount);
            }
        }
    }

    /**
     * Kiểm tra xem hai units có va chạm không
     * @param unitA Unit thứ nhất
     * @param unitB Unit thứ hai
     * @return true nếu có va chạm
     */
    public static boolean areUnitsColliding(Unit unitA, Unit unitB) {
        if (!unitA.isAlive() || !unitB.isAlive()) return false;

        Rectangle boundsA = unitA.getBounds();
        Rectangle boundsB = unitB.getBounds();

        return boundsA.overlaps(boundsB);
    }

    /**
     * Tìm vị trí trống gần nhất cho unit mới, chỉ tìm theo chiều X để giữ tất cả units trên cùng ground level
     * @param targetX Tọa độ X mong muốn
     * @param targetY Tọa độ Y mong muốn (sẽ được giữ nguyên - ground_y)
     * @param existingUnits Danh sách units hiện có
     * @param maxSearchRadius Bán kính tìm kiếm tối đa
     * @return Tọa độ trống gần nhất, Y luôn = targetY để giữ units trên ground
     */
    public static float[] findNearestFreePosition(float targetX, float targetY,
                                                Array<Unit> existingUnits,
                                                float maxSearchRadius) {
        float[] result = {targetX, targetY};

        // Kiểm tra vị trí gốc trước
        if (isPositionFree(targetX, targetY, existingUnits)) {
            return result;
        }

        // Tìm kiếm theo chiều X từ trái sang phải
        float searchStep = MIN_UNIT_DISTANCE / 2f;

        for (float offset = searchStep; offset <= maxSearchRadius; offset += searchStep) {
            // Kiểm tra bên phải
            float testXRight = targetX + offset;
            if (isPositionFree(testXRight, targetY, existingUnits)) {
                result[0] = testXRight;
                result[1] = targetY; // Giữ nguyên Y
                return result;
            }

            // Kiểm tra bên trái
            float testXLeft = targetX - offset;
            if (isPositionFree(testXLeft, targetY, existingUnits)) {
                result[0] = testXLeft;
                result[1] = targetY; // Giữ nguyên Y
                return result;
            }
        }

        // Nếu không tìm thấy vị trí trống, trả về vị trí gốc với Y không đổi
        result[1] = targetY; // Đảm bảo Y luôn là ground_y
        return result;
    }

    /**
     * Kiểm tra xem vị trí có trống không (không có unit nào ở đó)
     * Chỉ xét khoảng cách theo chiều X vì tất cả units đều ở cùng ground level (Y)
     * @param x Tọa độ X
     * @param y Tọa độ Y (thường là ground_y)
     * @param existingUnits Danh sách units hiện có
     * @return true nếu vị trí trống
     */
    private static boolean isPositionFree(float x, float y, Array<Unit> existingUnits) {
        // Sử dụng index loop để tránh nested iterator issue
        for (int i = 0; i < existingUnits.size; i++) {
            Unit unit = existingUnits.get(i);
            if (!unit.isAlive()) continue;

            // Chỉ xét khoảng cách theo chiều X vì tất cả units đều ở cùng ground level
            float distanceX = Math.abs(x - unit.getX());

            if (distanceX < MIN_UNIT_DISTANCE) {
                return false;
            }
        }
        return true;
    }
}
