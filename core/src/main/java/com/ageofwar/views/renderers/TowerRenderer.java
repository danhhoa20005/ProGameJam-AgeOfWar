package com.ageofwar.views.renderers;

import com.ageofwar.models.towers.Tower;
import com.ageofwar.models.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Lớp TowerRenderer chịu trách nhiệm vẽ các công trình phòng thủ (Towers)
 * của cả người chơi và AI, bao gồm cả thanh máu.
 */
public class TowerRenderer extends BaseRenderer {

    // Màu sắc cho placeholders
    private final Color playerTowerColor = Color.CYAN;
    private final Color aiTowerColor = Color.ORANGE;

    /**
     * Khởi tạo TowerRenderer.
     * @param shapeRenderer ShapeRenderer để vẽ placeholder và thanh máu.
     * @param batch SpriteBatch (dùng sau này cho sprites).
     */
    public TowerRenderer(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        super(shapeRenderer, batch);
    }

    /**
     * Vẽ tất cả các Tower lên màn hình.
     * @param world Đối tượng World chứa danh sách Towers.
     */
    public void render(World world) {
        // Vẽ Trụ (Placeholders)
        drawTowerPlaceholders(world.getPlayerTowers(), playerTowerColor);
        drawTowerPlaceholders(world.getAiTowers(), aiTowerColor);

        // Vẽ Thanh Máu cho Trụ
        drawTowerHealthBars(world.getPlayerTowers());
        drawTowerHealthBars(world.getAiTowers());
    }

    /**
     * Vẽ hình ảnh tạm thời (placeholders) cho một danh sách Tower.
     * @param towers Danh sách Tower cần vẽ.
     * @param color Màu sắc để vẽ.
     */
    private void drawTowerPlaceholders(Array<Tower> towers, Color color) {
        shapeRenderer.setColor(color);
        for (Tower tower : towers) {
            if (tower.isAlive()) {
                Rectangle bounds = tower.getBounds();
                shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
                // Thêm logic vẽ sprite/animation ở đây sau này
            }
        }
    }

    /**
     * Vẽ thanh máu cho một danh sách Tower.
     * @param towers Danh sách Tower cần vẽ thanh máu.
     */
    private void drawTowerHealthBars(Array<Tower> towers) {
        for (Tower tower : towers) {
            if (tower.isAlive()) {
                drawHealthBar(tower.getBounds().x, tower.getBounds().y + tower.getBounds().height + 5,
                    tower.getBounds().width, 5,
                    tower.getHealth(), tower.getMaxHealth());
            }
        }
    }

    // Hàm drawHealthBar được kế thừa từ BaseRenderer
}
