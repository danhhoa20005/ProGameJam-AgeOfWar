package com.ageofwar.views.renderers; // Tạo package mới cho các renderer con

import com.ageofwar.models.units.Unit;
import com.ageofwar.models.World; // Để lấy danh sách units
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Lớp UnitRenderer chịu trách nhiệm vẽ các đơn vị lính (Units)
 * của cả người chơi và AI, bao gồm cả thanh máu.
 */
public class UnitRenderer extends BaseRenderer { // Kế thừa BaseRenderer để dùng chung hàm vẽ thanh máu

    // Màu sắc cho placeholders
    private final Color playerColor = Color.BLUE;
    private final Color aiColor = Color.RED;

    /**
     * Khởi tạo UnitRenderer.
     * @param shapeRenderer ShapeRenderer để vẽ placeholder và thanh máu.
     * @param batch SpriteBatch (dùng sau này cho sprites).
     */
    public UnitRenderer(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        super(shapeRenderer, batch); // Gọi constructor của lớp cha
    }

    /**
     * Vẽ tất cả các Unit lên màn hình.
     * @param world Đối tượng World chứa danh sách Units.
     */
    public void render(World world) {
        // Bắt đầu vẽ hình dạng (nếu chưa bắt đầu từ GameRenderer) - Tốt hơn là quản lý begin/end ở GameRenderer
        // shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // Quản lý ở GameRenderer

        // Vẽ Lính (Placeholders)
        drawUnitPlaceholders(world.getPlayerUnits(), playerColor);
        drawUnitPlaceholders(world.getAiUnits(), aiColor);

        // Vẽ Thanh Máu cho Lính
        drawUnitHealthBars(world.getPlayerUnits());
        drawUnitHealthBars(world.getAiUnits());

        // Kết thúc vẽ hình dạng (nếu bắt đầu ở đây)
        // shapeRenderer.end(); // Quản lý ở GameRenderer
    }

    /**
     * Vẽ hình ảnh tạm thời (placeholders) cho một danh sách Unit.
     * @param units Danh sách Unit cần vẽ.
     * @param color Màu sắc để vẽ.
     */
    private void drawUnitPlaceholders(Array<Unit> units, Color color) {
        shapeRenderer.setColor(color);
        for (Unit unit : units) {
            if (unit.isAlive()) {
                Rectangle bounds = unit.getBounds();
                shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
                // Thêm logic vẽ sprite/animation ở đây sau này
                // batch.draw(...)
            }
        }
    }

    /**
     * Vẽ thanh máu cho một danh sách Unit.
     * @param units Danh sách Unit cần vẽ thanh máu.
     */
    private void drawUnitHealthBars(Array<Unit> units) {
        for (Unit unit : units) {
            if (unit.isAlive()) {
                drawHealthBar(unit.getBounds().x, unit.getBounds().y + unit.getBounds().height + 5,
                    unit.getBounds().width, 5,
                    unit.getHealth(), unit.getMaxHealth());
            }
        }
    }

    // Hàm drawHealthBar được kế thừa từ BaseRenderer
}
