package com.ageofwar.screens;

import com.ageofwar.AgeOfWarGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen extends ScreenAdapter {

    private final AgeOfWarGame game;
    private Stage stage;
    private Skin skin; // Skin for UI elements

    public MainMenuScreen(final AgeOfWarGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Ensure UI skin is loaded and assets assigned
        try {
            if (!game.assets.manager.isLoaded(com.ageofwar.utils.Assets.UI_SKIN)) {
                game.assets.manager.finishLoadingAsset(com.ageofwar.utils.Assets.UI_SKIN);
            }
            if (game.assets.uiSkin == null) {
                game.assets.assignAssets();
            }
            skin = game.assets.uiSkin;
        } catch (Exception e) {
            Gdx.app.error("MainMenuScreen", "Failed to load UI skin. Using default.", e);
            skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        }

        // --- create a TextButtonStyle using the custom BUTTON_TEXTURE ---
        Texture btnTex = game.assets.buttonTex;
        TextureRegion btnRegion = new TextureRegion(btnTex);
        TextureRegionDrawable upDrawable = new TextureRegionDrawable(btnRegion);
        TextureRegionDrawable downDrawable = new TextureRegionDrawable(btnRegion);
        // You can tint downDrawable if you want a pressed effect:
        // downDrawable.tint(Color.DARK_GRAY);
        TextButton.TextButtonStyle customBtnStyle = new TextButton.TextButtonStyle(
            upDrawable,    // up
            downDrawable,  // down
            null,          // checked
            skin.getFont("default-font")
        );

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label titleLabel = new Label("Cuoc Chien Xuyen The Ky", skin);

        // use the customBtnStyle for all menu buttons
        TextButton startButton    = new TextButton("Bat Dau",    customBtnStyle);
        TextButton settingsButton = new TextButton("Cai Dat (Chua co)", customBtnStyle);
        TextButton quitButton     = new TextButton("Thoat",      customBtnStyle);

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Start button clicked.");
                try {
                    game.setScreen(new GameScreen(game));
                    dispose();
                } catch (Exception e) {
                    Gdx.app.error("MainMenuScreen", "Error switching to GameScreen", e);
                }
            }
        });
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Settings button clicked (Not Implemented).");
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
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1/30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        Gdx.app.log("MainMenuScreen", "Disposing screen.");
        stage.dispose();
    }
}
