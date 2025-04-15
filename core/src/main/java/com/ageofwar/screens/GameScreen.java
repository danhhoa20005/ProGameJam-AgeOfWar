package com.ageofwar.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ageofwar.AgeOfWarGame;
import com.ageofwar.controllers.GameController;
import com.ageofwar.models.GameModel;
import com.ageofwar.models.PlayerType; // Import PlayerType
import com.ageofwar.views.GameRenderer;
import com.ageofwar.views.Hud; // Separate class for HUD

public class GameScreen extends ScreenAdapter {

    private final AgeOfWarGame game;
    private final OrthographicCamera gameCamera;
    private final Viewport gameViewport;
    private final GameModel model;
    private final GameRenderer renderer;
    private final GameController controller;
    private final Hud hud; // HUD manages its own stage and UI elements
    private final InputMultiplexer inputMultiplexer;


    // Define game world dimensions (adjust as needed)
    public static final float WORLD_WIDTH = 1600; // Example width
    public static final float WORLD_HEIGHT = 600; // Example height


    public GameScreen(final AgeOfWarGame game) {
        this.game = game;

        gameCamera = new OrthographicCamera();
        // FitViewport maintains aspect ratio, letterboxing if necessary
        gameViewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, gameCamera);
        // Position camera initially - center it or align left
        gameCamera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0);
        gameCamera.update();


        model = new GameModel(); // Initialize the game state container
        // *** FIX: Initialize the model's content BEFORE creating views/controllers that depend on it ***
        model.initialize(); // Create players, world, set starting values

        renderer = new GameRenderer(game.batch, game.shapeRenderer, game.font, model, gameCamera); // Pass resources and model
        hud = new Hud(game.batch, model, game); // Pass SpriteBatch and Model for HUD data (NOW model is initialized)
        controller = new GameController(model, hud, gameCamera, gameViewport); // Pass model and HUD for interactions

        // Handle input from both the game world (controller) and the HUD stage
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(hud.getStage()); // HUD takes priority
        inputMultiplexer.addProcessor(controller);   // Then game world input
        Gdx.input.setInputProcessor(inputMultiplexer);

        Gdx.app.log("GameScreen", "Screen initialized.");
        // model.initialize(); // MOVED UP
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        Gdx.app.log("GameScreen", "Input processor set.");
    }

    @Override
    public void render(float delta) {
        // --- Update ---
        model.update(delta); // Update game logic (unit movement, combat, AI, resources)
        controller.update(delta); // Update controller logic (camera movement)
        hud.update(delta); // Update HUD elements (timers, resource display)

        // Check for game over condition
        if (model.isGameOver()) {
            game.setScreen(new EndGameScreen(game, model.getWinner()));
            dispose();
            return; // Stop rendering this screen
        }


        // --- Render ---
        Gdx.gl.glClearColor(0.5f, 0.7f, 0.9f, 1); // Light blue sky background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update camera and viewport
        // gameViewport.apply(); // Apply viewport changes - already done in resize? Camera update is key.
        gameCamera.update();
        game.batch.setProjectionMatrix(gameCamera.combined); // Set projection for game world rendering
        game.shapeRenderer.setProjectionMatrix(gameCamera.combined);


        // Render game world elements
        renderer.render(delta);


        // Render HUD elements (using its own camera/viewport managed by Stage)
        game.batch.setProjectionMatrix(hud.getStage().getCamera().combined); // Switch projection for HUD
        hud.render(); // Draw the HUD stage

    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width, height);
        // Center camera after viewport update if needed, or maintain position
        // gameCamera.position.set(WORLD_WIDTH / 2f, WORLD_HEIGHT / 2f, 0); // Recenter on resize? Or keep current view? Let's keep current view.
        gameCamera.update();

        hud.resize(width, height); // Update HUD viewport
        Gdx.app.log("GameScreen", "Resized to " + width + "x" + height);
    }

    @Override
    public void hide() {
        // Consider removing the input processor when the screen is hidden
        // Gdx.input.setInputProcessor(null);
        Gdx.app.log("GameScreen", "Screen hidden.");
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameScreen", "Disposing screen.");
        renderer.dispose(); // Dispose resources specific to the renderer if any
        hud.dispose();      // Dispose the HUD stage and its resources
        model.dispose();    // Dispose model resources if any
        // Note: Shared resources like batch, shapeRenderer, font are disposed in AgeOfWarGame
    }
}
