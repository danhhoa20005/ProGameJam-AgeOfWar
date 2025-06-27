package com.ageofwar.systems;

import com.ageofwar.models.units.Unit;
import com.ageofwar.models.units.UnitState;
import com.ageofwar.models.players.PlayerType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Hệ thống VisualEnhancementSystem cải thiện việc hiển thị units và UI elements
 * để giải quyết vấn đề overlap và tăng clarity cho người chơi.
 *
 * Bao gồm các features như dynamic health bar positioning, unit highlighting,
 * và visual separation indicators.
 */
public class VisualEnhancementSystem {

    // Colors for different teams
    private static final Color PLAYER_HEALTH_BAR_COLOR = new Color(0.2f, 0.8f, 0.2f, 0.8f); // Green
    private static final Color AI_HEALTH_BAR_COLOR = new Color(0.8f, 0.2f, 0.2f, 0.8f); // Red
    private static final Color HEALTH_BAR_BACKGROUND = new Color(0.3f, 0.3f, 0.3f, 0.6f); // Dark gray

    // Health bar dimensions
    private static final float HEALTH_BAR_HEIGHT = 4f;
    private static final float HEALTH_BAR_BORDER = 1f;
    private static final float HEALTH_BAR_OFFSET_Y = 8f;

    /**
     * Vẽ thanh máu nâng cao với positioning thông minh để tránh overlap
     * @param shapeRenderer ShapeRenderer để vẽ shapes
     * @param units Danh sách units
     */
    public static void renderEnhancedHealthBars(ShapeRenderer shapeRenderer, Array<Unit> units) {
        if (shapeRenderer == null || units == null) return;

        // Tính toán vị trí thanh máu tối ưu cho tất cả units
        Array<HealthBarInfo> healthBars = calculateHealthBarPositions(units);

        // Vẽ từng thanh máu - sử dụng index loop để tránh nested iterator issue
        for (int i = 0; i < healthBars.size; i++) {
            HealthBarInfo info = healthBars.get(i);
            drawHealthBar(shapeRenderer, info);
        }
    }

    /**
     * Tính toán vị trí tối ưu cho tất cả thanh máu
     */
    private static Array<HealthBarInfo> calculateHealthBarPositions(Array<Unit> units) {
        Array<HealthBarInfo> healthBars = new Array<>();

        // Sử dụng index loop để tránh nested iterator issue
        for (int i = 0; i < units.size; i++) {
            Unit unit = units.get(i);
            if (!unit.isAlive() || unit.getCurrentState() == UnitState.DEATH) continue;

            Rectangle bounds = unit.getBounds();
            float baseY = bounds.y + bounds.height + HEALTH_BAR_OFFSET_Y;

            // Tìm vị trí Y tối ưu để tránh overlap
            float optimalY = findOptimalHealthBarY(bounds.x, baseY, bounds.width, healthBars);

            Color barColor = (unit.getOwnerType() == PlayerType.PLAYER) ?
                PLAYER_HEALTH_BAR_COLOR : AI_HEALTH_BAR_COLOR;

            HealthBarInfo info = new HealthBarInfo(
                bounds.x, optimalY, bounds.width, HEALTH_BAR_HEIGHT,
                unit.getHealth(), unit.getMaxHealth(), barColor
            );

            healthBars.add(info);
        }

        return healthBars;
    }

    /**
     * Tìm vị trí Y tối ưu cho thanh máu để tránh overlap
     */
    private static float findOptimalHealthBarY(float x, float baseY, float width, Array<HealthBarInfo> existing) {
        float testY = baseY;
        boolean hasOverlap;
        int maxAttempts = 5;
        int attempts = 0;

        do {
            hasOverlap = false;
            attempts++;

            // Sử dụng index loop để tránh nested iterator issue
            for (int i = 0; i < existing.size; i++) {
                HealthBarInfo other = existing.get(i);
                // Kiểm tra overlap trên cả X và Y axis
                boolean xOverlap = !(x > other.x + other.width || x + width < other.x);
                boolean yOverlap = Math.abs(testY - other.y) < HEALTH_BAR_HEIGHT + 2f;

                if (xOverlap && yOverlap) {
                    hasOverlap = true;
                    testY += HEALTH_BAR_HEIGHT + 3f; // Di chuyển lên cao hơn
                    break;
                }
            }
        } while (hasOverlap && attempts < maxAttempts);

        return testY;
    }

    /**
     * Vẽ một thanh máu với background và border
     */
    private static void drawHealthBar(ShapeRenderer shapeRenderer, HealthBarInfo info) {
        float healthPercent = (float) info.currentHealth / info.maxHealth;
        healthPercent = Math.max(0f, Math.min(1f, healthPercent)); // Clamp between 0 and 1

        // Vẽ background (border)
        shapeRenderer.setColor(HEALTH_BAR_BACKGROUND);
        shapeRenderer.rect(
            info.x - HEALTH_BAR_BORDER,
            info.y - HEALTH_BAR_BORDER,
            info.width + 2 * HEALTH_BAR_BORDER,
            info.height + 2 * HEALTH_BAR_BORDER
        );

        // Vẽ nền thanh máu (dark)
        shapeRenderer.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        shapeRenderer.rect(info.x, info.y, info.width, info.height);

        // Vẽ thanh máu hiện tại
        if (healthPercent > 0) {
            shapeRenderer.setColor(info.color);
            shapeRenderer.rect(
                info.x,
                info.y,
                info.width * healthPercent,
                info.height
            );
        }
    }

    /**
     * Vẽ indicators để hiển thị trạng thái units
     * @param batch SpriteBatch để vẽ text/sprites
     * @param units Danh sách units
     */
    public static void renderUnitIndicators(SpriteBatch batch, Array<Unit> units) {
        // Có thể thêm các indicators như:
        // - Unit selection rings
        // - Status effect icons
        // - Formation lines
        // - Attack range circles

        // TODO: Implement visual indicators if needed
    }

    /**
     * Kiểm tra xem units có đang overlap không để hiển thị warning
     * @param units Danh sách units cần kiểm tra
     * @return số lượng cặp units đang overlap
     */
    public static int countOverlappingUnits(Array<Unit> units) {
        int overlapCount = 0;

        for (int i = 0; i < units.size; i++) {
            Unit unitA = units.get(i);
            if (!unitA.isAlive()) continue;

            for (int j = i + 1; j < units.size; j++) {
                Unit unitB = units.get(j);
                if (!unitB.isAlive()) continue;

                if (CollisionSystem.areUnitsColliding(unitA, unitB)) {
                    overlapCount++;
                }
            }
        }

        return overlapCount;
    }

    /**
     * Class để lưu thông tin thanh máu
     */
    private static class HealthBarInfo {
        public float x, y, width, height;
        public int currentHealth, maxHealth;
        public Color color;

        public HealthBarInfo(float x, float y, float width, float height,
                           int currentHealth, int maxHealth, Color color) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.currentHealth = currentHealth;
            this.maxHealth = maxHealth;
            this.color = color;
        }
    }
}
