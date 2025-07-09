package com.ageofwar.screens;

import com.ageofwar.AgeOfWarGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class MainMenuScreen extends ScreenAdapter {
    private final AgeOfWarGame game;
    private Stage stage;
    private Skin skin;
    private Texture bgTexture; // background texture
    private float animationTime = 0f; // For animations

    public MainMenuScreen(AgeOfWarGame game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());

        // Add background image with tint effect
        bgTexture = new Texture(Gdx.files.internal("Background/Background.png"));
        Image bgImage = new Image(new TextureRegion(bgTexture));
        bgImage.setFillParent(true);
        // Add a slight dark tint to make text more readable
        bgImage.setColor(0.8f, 0.8f, 0.9f, 1f);
        stage.addActor(bgImage);
        Gdx.input.setCatchBackKey(true);

        // --- setup UI skin ---
        try {
            if (this.game.assets.uiSkin == null) {
                this.game.assets.assignAssets();
            }
            skin = this.game.assets.uiSkin;
        } catch (Exception e) {
            Gdx.app.error("MainMenuScreen", "Failed to load UI skin. Using default.", e);
            skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        }

        // Override default font with Unicode-capable fonts for Vietnamese
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));

            // Title font (larger with shadow effect)
            FreeTypeFontGenerator.FreeTypeFontParameter titleParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
            titleParam.size = 32; // Increased size
            titleParam.shadowOffsetX = 2;
            titleParam.shadowOffsetY = 2;
            titleParam.shadowColor = Color.BLACK;
            titleParam.borderWidth = 1;
            titleParam.borderColor = Color.DARK_GRAY;
            titleParam.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "âăđêếôơưÂĂĐÊÔƠƯáàảãạấầẩẫậắằẳẵặÁÀẢÃẠẤẦẨẪẬẮẰẲẴẶéèẻẽẹÉÈẺẼẸíìỉĩịÍÌỈĨỊóòỏõọÓÒỎÕỌốồổỗộỐỒỔỖỘớờởỡợỚỜỞỠỢúùủũụÚÙỤŨỤýỳỷỹỵÝỲỶỸỴ";
            BitmapFont titleFont = generator.generateFont(titleParam);
            skin.add("title-font", titleFont, BitmapFont.class);

            // Button font (with subtle border)
            FreeTypeFontGenerator.FreeTypeFontParameter btnParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
            btnParam.size = 20; // Slightly larger
            btnParam.borderWidth = 0.5f;
            btnParam.borderColor = Color.DARK_GRAY;
            btnParam.characters = titleParam.characters;
            BitmapFont btnFont = generator.generateFont(btnParam);
            skin.add("btn-font", btnFont, BitmapFont.class);

            generator.dispose();
        } catch (Exception e) {
            Gdx.app.error("MainMenuScreen", "Vietnamese fonts not found, using default fonts.", e);
        }

        // --- create a TextButtonStyle using the custom UP_BUTTON_TEXTURE ---
        Texture upBtnTex = this.game.assets.upButtonTex;
        Texture downBtnTex = this.game.assets.downButtonTex;
        TextureRegion upBtnRegion = new TextureRegion(upBtnTex);
        TextureRegion downBtnRegion = new TextureRegion(downBtnTex);
        TextureRegionDrawable upDrawable = new TextureRegionDrawable(upBtnRegion);
        TextureRegionDrawable downDrawable = new TextureRegionDrawable(downBtnRegion);
        downDrawable.tint(Color.DARK_GRAY);
        TextButton.TextButtonStyle customBtnStyle = new TextButton.TextButtonStyle(
            upDrawable,    // up
            downDrawable,  // down
            null,          // checked
            skin.getFont("btn-font")
        );

        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Use custom label style with Unicode font
        LabelStyle titleStyle = new LabelStyle(skin.getFont("title-font"), Color.WHITE);
        Label titleLabel = new Label("Cuộc Chiến Xuyên Thế Kỷ", titleStyle);

        // use the customBtnStyle for all menu buttons
        TextButton startButton    = new TextButton("Bắt Đầu",    customBtnStyle);
        TextButton settingsButton = new TextButton("Cài Đặt (Chưa có)", customBtnStyle);
        TextButton quitButton     = new TextButton("Thoát",      customBtnStyle);

        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Start button clicked.");
                try {
                    MainMenuScreen.this.game.setScreen(new GameScreen(MainMenuScreen.this.game));
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
        if (bgTexture != null) bgTexture.dispose();
    }
}
