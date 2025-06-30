package com.ageofwar.views.renderers;

import com.ageofwar.configs.GameConfig;
import com.ageofwar.models.players.Player;
import com.ageofwar.models.players.PlayerType;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Lớp PlayerBaseRenderer chịu trách nhiệm vẽ căn cứ (Base)
 * của cả người chơi và AI, bao gồm cả thanh máu.
 */
public class PlayerBaseRenderer extends BaseRenderer {

    // Textures cho căn cứ
    private final Texture playerBaseTextureFull;
    private final Texture playerBaseTexture66;
    private final Texture playerBaseTexture33;

    private final Texture aiBaseTextureFull;
    private final Texture aiBaseTexture66;
    private final Texture aiBaseTexture33;

    /**
     * Khởi tạo PlayerBaseRenderer.
     * @param shapeRenderer ShapeRenderer để vẽ placeholder và thanh máu.
     * @param batch SpriteBatch để vẽ sprite.
     */
    public PlayerBaseRenderer(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        super(shapeRenderer, batch);

        // Tự động load hình ảnh căn cứ
        this.playerBaseTextureFull = new Texture("Base/Blue/Base1/1.png");
        this.playerBaseTexture66 = new Texture("Base/Blue/Base1/2.png");
        this.playerBaseTexture33 = new Texture("Base/Blue/Base1/3.png");

        this.aiBaseTextureFull = new Texture("Base/Red/Base1/1.png");
        this.aiBaseTexture66 = new Texture("Base/Red/Base1/2.png");
        this.aiBaseTexture33 = new Texture("Base/Red/Base1/3.png");
    }

    /**
     * Vẽ căn cứ của cả hai người chơi.
     * @param player Đối tượng Player của người chơi.
     * @param aiPlayer Đối tượng Player của AI.
     */
    public void render(Player player, Player aiPlayer) {
        // Vẽ căn cứ bằng hình ảnh
        drawBase(player);
        drawBase(aiPlayer);

        // Vẽ thanh máu cho căn cứ
        drawBaseHealthBar(player);
        drawBaseHealthBar(aiPlayer);
    }

    /**
     * Lấy hình ảnh căn cứ dựa trên mức máu.
     * @param player Người chơi (Player hoặc AI).
     * @return Texture tương ứng với mức máu.
     */
    private Texture getBaseTexture(Player player) {
        float healthPercentage = (float) player.getBaseHealth() / player.getMaxBaseHealth();

        if (healthPercentage > 0.66f) {
            return (player.getType() == PlayerType.PLAYER) ? playerBaseTextureFull : aiBaseTextureFull;
        } else if (healthPercentage > 0.33f) {
            return (player.getType() == PlayerType.PLAYER) ? playerBaseTexture66 : aiBaseTexture66;
        } else {
            return (player.getType() == PlayerType.PLAYER) ? playerBaseTexture33 : aiBaseTexture33;
        }
    }

    /**
     * Vẽ căn cứ bằng hình ảnh.
     * @param player Người chơi (Player hoặc AI).
     */
    private void drawBase(Player player) {
        Texture baseTexture = getBaseTexture(player);
        float baseWidth = 40 * 4f; // Kích thước rộng căn cứ
        float baseHeight = 60 * 4f; // Kích thước cao căn cứ
        float baseX = (player.getType() == PlayerType.PLAYER) ? GameConfig.PLAYER_BASE_X - baseWidth / 2 : GameConfig.AI_BASE_X - baseWidth / 2;

        if (baseTexture != null) {
            batch.begin();
            batch.draw(baseTexture, baseX, GameConfig.GROUND_Y - 7 * 4f, baseWidth, baseHeight);
            batch.end();
        }
    }

    /**
     * Vẽ thanh máu cho căn cứ của một người chơi.
     * @param player Người chơi (Player hoặc AI).
     */
    private void drawBaseHealthBar(Player player) {
        float baseWidth = 20 * 4f; // Chiều rộng thanh máu
        float baseHeight = 30 * 4f; // Chiều cao căn cứ
        float baseX = (player.getType() == PlayerType.PLAYER) ? GameConfig.PLAYER_BASE_X - baseWidth / 2 : GameConfig.AI_BASE_X - baseWidth / 2;

        drawHealthBar(baseX, GameConfig.GROUND_Y + baseHeight + 5, baseWidth, 10, player.getBaseHealth(), player.getMaxBaseHealth());
    }
}
