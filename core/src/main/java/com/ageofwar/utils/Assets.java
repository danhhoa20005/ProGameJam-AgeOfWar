package com.ageofwar.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas; // If using texture atlases
import com.badlogic.gdx.scenes.scene2d.ui.Skin; // For Scene2D UI

public class Assets {
    public final AssetManager manager = new AssetManager();

    // Define paths to assets (adjust as needed)
    public static final String PLACEHOLDER_TEXTURE = "images/placeholder.png"; // Example path
    public static final String UI_SKIN = "ui/uiskin.json"; // Example path for UI skin

    // Texture references (examples)
    public Texture placeholderTex;
    // public TextureAtlas gameAtlas;
    public Skin uiSkin;

    public void load() {
        // Load assets into the manager
        manager.load(PLACEHOLDER_TEXTURE, Texture.class);
        // manager.load("images/game_assets.atlas", TextureAtlas.class); // Example atlas loading
        manager.load(UI_SKIN, Skin.class); // Load UI skin

        // Add more assets to load here (sounds, music, other textures)
    }

    public void assignAssets() {
        // Assign loaded assets to variables after manager.finishLoading()
        placeholderTex = manager.get(PLACEHOLDER_TEXTURE, Texture.class);
        // gameAtlas = manager.get("images/game_assets.atlas", TextureAtlas.class);
        uiSkin = manager.get(UI_SKIN, Skin.class);
    }

    public void dispose() {
        manager.dispose();
    }

    // --- Static methods to create placeholder textures programmatically ---
    // (Consider moving to a dedicated utility class if it grows large)
    private static com.badlogic.gdx.graphics.Pixmap createPixmap(int width, int height, com.badlogic.gdx.graphics.Color color) {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(width, height, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        return pixmap;
    }

    public static Texture createPlaceholderTexture(int width, int height, com.badlogic.gdx.graphics.Color color) {
        com.badlogic.gdx.graphics.Pixmap pixmap = createPixmap(width, height, color);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    // Example usage within load() or elsewhere if needed:
    // placeholderTex = createPlaceholderTexture(32, 32, com.badlogic.gdx.graphics.Color.RED);
    // Note: Programmatic creation doesn't use the AssetManager directly unless you wrap it.
    // For simplicity now, we assume placeholder.png exists or use ShapeRenderer.
    // If placeholder.png doesn't exist, the AssetManager load will fail.
    // A robust solution involves checking file existence or creating it programmatically
    // BEFORE adding to AssetManager or just using ShapeRenderer initially.
}
