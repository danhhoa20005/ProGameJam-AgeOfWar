package com.ageofwar;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ageofwar.screens.MainMenuScreen;
import com.ageofwar.utils.Assets;

public class AgeOfWarGame extends Game {
    // Shared resources (can be managed by an AssetManager class as well)
    public SpriteBatch batch;
    public ShapeRenderer shapeRenderer; // For placeholders
    public BitmapFont font;
    public Assets assets;

    @Override
    public void create () {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont(); // Use default Arial font for simplicity
        assets = new Assets();

        // Load essential assets needed globally or for the first screen
        assets.load();
        assets.manager.finishLoading(); // Block until loaded for simplicity here

        Gdx.app.log("AgeOfWarGame", "Game created and assets loaded.");
        this.setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render () {
        super.render(); // Important! Delegates render to the current screen
    }

    @Override
    public void dispose () {
        Gdx.app.log("AgeOfWarGame", "Game disposing.");
        // Dispose shared resources
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
        assets.dispose(); // Dispose asset manager

        // Dispose the current screen if it exists
        if (screen != null) {
            screen.dispose();
        }
    }
}
