package com.ageofwar.views;

import com.ageofwar.models.*;
import com.ageofwar.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.ageofwar.configs.GameConfig;

public class GameRenderer implements Disposable {

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final GameModel model;
    private final OrthographicCamera camera;

    // Colors for placeholders
    private final Color playerColor = Color.BLUE;
    private final Color aiColor = Color.RED;
    private final Color playerTowerColor = Color.CYAN;
    private final Color aiTowerColor = Color.ORANGE;
    private final Color healthBarBgColor = Color.DARK_GRAY;
    private final Color healthBarFgColor = Color.GREEN;
    private final Color groundColor = Color.BROWN;
    private final Color baseColorPlayer = Color.NAVY;
    private final Color baseColorAI = Color.MAROON;

    public GameRenderer(SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font, GameModel model, OrthographicCamera camera) {
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
        this.font = font;
        this.model = model;
        this.camera = camera;
    }

    public void render(float delta) {
        // Use ShapeRenderer for placeholders
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);

        // Draw Ground
        shapeRenderer.setColor(groundColor);
        shapeRenderer.rect(0, 0, GameScreen.WORLD_WIDTH, GameConfig.GROUND_Y); // Use GameScreen constant

        // Draw Bases (simple rectangles for now)
        drawBasePlaceholder(model.getPlayer());
        drawBasePlaceholder(model.getAiPlayer());

        // Draw Towers (Placeholders)
        drawEntityPlaceholders(model.getWorld().getPlayerTowers(), playerTowerColor);
        drawEntityPlaceholders(model.getWorld().getAiTowers(), aiTowerColor);

        // Draw Units (Placeholders)
        drawEntityPlaceholders(model.getWorld().getPlayerUnits(), playerColor);
        drawEntityPlaceholders(model.getWorld().getAiUnits(), aiColor);


        shapeRenderer.end();

        // --- Render Health Bars --- (Can also be done with ShapeRenderer)
        shapeRenderer.begin(ShapeType.Filled);
        drawHealthBars(model.getWorld().getPlayerTowers());
        drawHealthBars(model.getWorld().getAiTowers());
        drawHealthBars(model.getWorld().getPlayerUnits());
        drawHealthBars(model.getWorld().getAiUnits());
        drawBaseHealthBar(model.getPlayer());
        drawBaseHealthBar(model.getAiPlayer());
        shapeRenderer.end();


        // Use SpriteBatch for text or actual sprites later
        // batch.setProjectionMatrix(camera.combined);
        // batch.begin();
        // font.draw(batch, "Game Screen", 100, 100);
        // batch.end();
    }

    // Generic method to draw placeholders for any entity list
    private <T extends Entity> void drawEntityPlaceholders(Array<T> entities, Color color) {
        shapeRenderer.setColor(color);
        for (T entity : entities) {
            if (entity.isAlive()) {
                Rectangle bounds = entity.getBounds();
                // Offset Y slightly so base is on the ground line
                shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }
    }

    // Generic method to draw health bars
    private <T extends Entity> void drawHealthBars(Array<T> entities) {
        for (T entity : entities) {
            if (entity.isAlive()) {
                drawHealthBar(entity.getBounds().x, entity.getBounds().y + entity.getBounds().height + 5, // Position above entity
                    entity.getBounds().width, 5, // Size of health bar
                    entity.getHealth(), entity.getMaxHealth());
            }
        }
    }

    private void drawBasePlaceholder(Player player) {
        Color baseColor = (player.getType() == PlayerType.PLAYER) ? baseColorPlayer : baseColorAI;
        shapeRenderer.setColor(baseColor);
        // Draw a simple rectangle for the base
        float baseWidth = 100; // Example width
        float baseHeight = 150; // Example height
        float baseX = (player.getType() == PlayerType.PLAYER) ? GameConfig.PLAYER_BASE_X - baseWidth / 2 : GameConfig.AI_BASE_X - baseWidth / 2;
        shapeRenderer.rect(baseX, GameConfig.GROUND_Y, baseWidth, baseHeight);
    }

    private void drawBaseHealthBar(Player player) {
        float baseWidth = 100; // Must match placeholder width
        float baseHeight = 150; // Must match placeholder height
        float baseX = (player.getType() == PlayerType.PLAYER) ? GameConfig.PLAYER_BASE_X - baseWidth / 2 : GameConfig.AI_BASE_X - baseWidth / 2;
        drawHealthBar(baseX, GameConfig.GROUND_Y + baseHeight + 5, baseWidth, 10, player.getBaseHealth(), player.getMaxBaseHealth());
    }


    // Helper to draw a single health bar
    private void drawHealthBar(float x, float y, float width, float height, int currentHealth, int maxHealth) {
        if (maxHealth <= 0) return; // Avoid division by zero
        float healthPercentage = (float) currentHealth / maxHealth;
        healthPercentage = Math.max(0f, Math.min(1f, healthPercentage)); // Clamp between 0 and 1

        // Draw background
        shapeRenderer.setColor(healthBarBgColor);
        shapeRenderer.rect(x, y, width, height);

        // Draw foreground
        shapeRenderer.setColor(healthBarFgColor);
        shapeRenderer.rect(x, y, width * healthPercentage, height);
    }


    @Override
    public void dispose() {
        // Dispose resources created solely by the renderer if any
        // Shared resources (batch, shapeRenderer, font) are disposed in AgeOfWarGame
    }
}
