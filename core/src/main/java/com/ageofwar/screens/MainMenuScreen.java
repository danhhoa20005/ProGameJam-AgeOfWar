package com.ageofwar.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ageofwar.AgeOfWarGame;

public class MainMenuScreen extends ScreenAdapter {

    private final AgeOfWarGame game;
    private Stage stage;
    private Skin skin; // Skin for UI elements

    public MainMenuScreen(final AgeOfWarGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        try {
            // Ensure assets are loaded AND assigned before accessing them
            if (!game.assets.manager.isLoaded(com.ageofwar.utils.Assets.UI_SKIN)) {
                game.assets.manager.finishLoadingAsset(com.ageofwar.utils.Assets.UI_SKIN); // Load if not loaded
            }
            // Assign assets if not already done (should ideally be done once after loading)
            if (game.assets.uiSkin == null) {
                game.assets.assignAssets();
            }
            skin = game.assets.uiSkin;
        } catch (Exception e) {
            Gdx.app.error("MainMenuScreen", "Failed to load UI skin. Using default.", e);
            // Fallback to a default skin or create one programmatically if needed
            skin = new Skin(Gdx.files.internal("ui/uiskin.json")); // Or handle error differently
        }


        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label titleLabel = new Label("Cuoc Chien Xuyen The Ky", skin); // Use "title" style from skin
        TextButton startButton = new TextButton("Bat Dau", skin); // Use default style
        TextButton settingsButton = new TextButton("Cai Dat (Chua co)", skin);
        TextButton quitButton = new TextButton("Thoat", skin);

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Start button clicked.");
                // Make sure GameScreen exists and is correctly implemented
                try {
                    game.setScreen(new GameScreen(game));
                    dispose(); // Dispose this screen as we move to the next
                } catch (Exception e) {
                    Gdx.app.error("MainMenuScreen", "Error switching to GameScreen", e);
                    // Handle error, maybe show a message to the user
                }
            }
        });

        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Settings button clicked (Not Implemented).");
                // Implement settings screen or functionality later
            }
        });


        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Quit button clicked.");
                Gdx.app.exit();
            }
        });

        table.add(titleLabel).padBottom(50).row();
        table.add(startButton).width(200).pad(10).row();
        table.add(settingsButton).width(200).pad(10).row();
        table.add(quitButton).width(200).pad(10).row();

        stage.addActor(table);
        Gdx.app.log("MainMenuScreen", "Screen initialized.");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1); // Dark grey background
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        // Optional: Can remove input processor if needed when screen is hidden
        // Gdx.input.setInputProcessor(null);
    }


    @Override
    public void dispose() {
        Gdx.app.log("MainMenuScreen", "Disposing screen.");
        stage.dispose();
        // Dispose skin IF it was created programmatically here as a fallback
        // If using AssetManager, the manager handles disposal
        // if (skin != null && !game.assets.manager.contains(game.assets.UI_SKIN)) {
        //     skin.dispose();
        // }
    }
}
