package com.ageofwar.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
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
import com.ageofwar.models.players.PlayerType; // Enum to indicate winner

public class EndGameScreen extends ScreenAdapter {

    private final AgeOfWarGame game;
    private final Stage stage;
    private final Skin skin; // Keep final
    private final PlayerType winner;

    public EndGameScreen(final AgeOfWarGame game, PlayerType winner) {
        this.game = game;
        this.winner = winner;
        stage = new Stage(new ScreenViewport());

        // --- Load skin safely and assign final variable exactly once ---
        Skin loadedSkin; // Use a temporary variable
        try {
            // Ensure asset is loaded
            if (!game.assets.manager.isLoaded(com.ageofwar.utils.Assets.UI_SKIN)) {
                Gdx.app.log("EndGameScreen", "UI Skin not loaded, finishing loading...");
                game.assets.manager.finishLoadingAsset(com.ageofwar.utils.Assets.UI_SKIN);
            }
            // Ensure assets are assigned
            if (game.assets.uiSkin == null) {
                Gdx.app.log("EndGameScreen", "Assigning assets as uiSkin was null...");
                game.assets.assignAssets();
            }

            // Check AGAIN
            if (game.assets.uiSkin == null) {
                throw new IllegalStateException("Failed to load or assign UI skin from AssetManager.");
            }

            loadedSkin = game.assets.uiSkin;
            Gdx.app.log("EndGameScreen", "Successfully retrieved UI skin from AssetManager.");

        } catch (Exception e) {
            Gdx.app.error("EndGameScreen", "Failed to load UI skin from AssetManager. Using fallback.", e);
            // Fallback assignment
            loadedSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        }
        this.skin = loadedSkin; // Assign the final field 'skin' exactly once
        // --- End of skin loading ---

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        String resultMessage = (winner == PlayerType.PLAYER) ? "Chien Thang!" : "That Bai!";
        Label resultLabel = new Label(resultMessage, skin); // Use a larger font style
        resultLabel.setColor(winner == PlayerType.PLAYER ? Color.GREEN : Color.RED);


        TextButton playAgainButton = new TextButton("Choi Lai", skin);
        TextButton menuButton = new TextButton("Menu Chinh", skin);

        playAgainButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("EndGameScreen", "Play Again clicked.");
                game.setScreen(new GameScreen(game)); // Start a new game
                dispose();
            }
        });

        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("EndGameScreen", "Main Menu clicked.");
                game.setScreen(new MainMenuScreen(game)); // Go back to menu
                dispose();
            }
        });

        table.add(resultLabel).padBottom(50).row();
        table.add(playAgainButton).width(200).pad(10).row();
        table.add(menuButton).width(200).pad(10).row();

        stage.addActor(table);
        Gdx.app.log("EndGameScreen", "Screen initialized. Winner: " + winner);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1); // Dark background
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
        // Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        Gdx.app.log("EndGameScreen", "Disposing screen.");
        stage.dispose();
        // Skin managed by AssetManager or is fallback
    }
}
