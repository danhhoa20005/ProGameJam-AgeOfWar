package com.ageofwar.views.renderers;

import com.ageofwar.configs.GameConfig;
import com.ageofwar.models.players.Player;
import com.ageofwar.models.players.PlayerType;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Lớp PlayerBaseRenderer chịu trách nhiệm vẽ căn cứ (Base)
 * của cả người chơi và AI, bao gồm cả thanh máu.
 */
public class PlayerBaseRenderer extends BaseRenderer {

    // Màu sắc cho placeholders
    private final Color baseColorPlayer = Color.NAVY;
    private final Color baseColorAI = Color.MAROON;

    /**
     * Khởi tạo PlayerBaseRenderer.
     * @param shapeRenderer ShapeRenderer để vẽ placeholder và thanh máu.
     * @param batch SpriteBatch (dùng sau này cho sprites).
     */
    public PlayerBaseRenderer(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        super(shapeRenderer, batch);
    }

    /**
     * Vẽ căn cứ của cả hai người chơi.
     * @param player Đối tượng Player của người chơi.
     * @param aiPlayer Đối tượng Player của AI.
     */
    public void render(Player player, Player aiPlayer) {
        // Vẽ Căn cứ (Placeholders)
        drawBasePlaceholder(player);
        drawBasePlaceholder(aiPlayer);

        // Vẽ Thanh Máu cho Căn cứ
        drawBaseHealthBar(player);
        drawBaseHealthBar(aiPlayer);
    }

    /**
     * Vẽ hình ảnh tạm thời cho căn cứ của một người chơi.
     * @param player Người chơi (Player hoặc AI).
     */
    private void drawBasePlaceholder(Player player) {
        Color baseColor = (player.getType() == PlayerType.PLAYER) ? baseColorPlayer : baseColorAI;
        shapeRenderer.setColor(baseColor);
        float baseWidth = 100;
        float baseHeight = 150;
        float baseX = (player.getType() == PlayerType.PLAYER) ? GameConfig.PLAYER_BASE_X - baseWidth / 2 : GameConfig.AI_BASE_X - baseWidth / 2;
        shapeRenderer.rect(baseX, GameConfig.GROUND_Y, baseWidth, baseHeight);
        // Thêm logic vẽ sprite/animation ở đây sau này
    }

    /**
     * Vẽ thanh máu cho căn cứ của một người chơi.
     * @param player Người chơi (Player hoặc AI).
     */
    private void drawBaseHealthBar(Player player) {
        float baseWidth = 100;
        float baseHeight = 150;
        float baseX = (player.getType() == PlayerType.PLAYER) ? GameConfig.PLAYER_BASE_X - baseWidth / 2 : GameConfig.AI_BASE_X - baseWidth / 2;
        drawHealthBar(baseX, GameConfig.GROUND_Y + baseHeight + 5, baseWidth, 10, player.getBaseHealth(), player.getMaxBaseHealth());
    }

    // Hàm drawHealthBar được kế thừa từ BaseRenderer
}
