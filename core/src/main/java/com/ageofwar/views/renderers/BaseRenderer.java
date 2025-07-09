package com.ageofwar.views.renderers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Lớp cơ sở trừu tượng cho các renderer cụ thể (Unit, Tower, Base).
 * Chứa các thuộc tính và phương thức chung, ví dụ như hàm vẽ thanh máu.
 */
public abstract class BaseRenderer {

    protected final ShapeRenderer shapeRenderer; // Để vẽ hình dạng
    protected final SpriteBatch batch; // Để vẽ sprite (dùng sau)

    // Màu thanh máu (có thể định nghĩa ở đây hoặc để lớp con tự định nghĩa)
    protected final Color healthBarBgColor = Color.DARK_GRAY;
    protected final Color healthBarFgColor = Color.GREEN;

    /**
     * Khởi tạo BaseRenderer.
     * @param shapeRenderer ShapeRenderer dùng chung.
     * @param batch SpriteBatch dùng chung.
     */
    protected BaseRenderer(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
    }

    /**
     * Hàm trợ giúp chung để vẽ một thanh máu đơn lẻ.
     * @param x Tọa độ X góc dưới trái.
     * @param y Tọa độ Y góc dưới trái.
     * @param width Chiều rộng thanh máu.
     * @param height Chiều cao thanh máu.
     * @param currentHealth Máu hiện tại.
     * @param maxHealth Máu tối đa.
     */
    protected void drawHealthBar(float x, float y, float width, float height, int currentHealth, int maxHealth) {
        if (maxHealth <= 0) return;
        float healthPercentage = (float) currentHealth / maxHealth;
        healthPercentage = Math.max(0f, Math.min(1f, healthPercentage));

        // Vẽ nền
        shapeRenderer.setColor(healthBarBgColor);
        shapeRenderer.rect(x, y, width, height);

        // Vẽ phần máu hiện tại
        shapeRenderer.setColor(healthBarFgColor);
        shapeRenderer.rect(x, y, width * healthPercentage, height);
    }

    // Có thể thêm các phương thức trừu tượng hoặc chung khác nếu cần
    // public abstract void render(...);
}
