package com.ageofwar;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ageofwar.screens.MainMenuScreen;
import com.ageofwar.utils.Assets;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.Color;

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
        assets = new Assets();

        // Load essential assets needed globally or for the first screen
        assets.load();
        assets.manager.finishLoading(); // Block until loaded for simplicity here
        assets.assignAssets(); // Ensure uiSkin is assigned

        // --- Tạo font tiếng Việt từ Roboto-Regular.ttf ---
        try {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Regular.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter param = new FreeTypeFontGenerator.FreeTypeFontParameter();
            param.size = 20;
            param.borderWidth = 0.5f;
            param.borderColor = Color.DARK_GRAY;
            param.characters = FreeTypeFontGenerator.DEFAULT_CHARS + "âăđêếôơưÂĂĐÊÔƠƯáàảãạấầẩẫậắằẳẵặÁÀẢÃẠẤẦẨẪẬẮẰẲẴẶéèẻẽẹÉÈẺẼẸíìỉĩịÍÌỈĨỊóòỏõọÓÒỎÕỌốồổỗộỐỒỔỖỘớờởỡợỚỜỞỠỢúùủũụÚÙỤŨỤýỳỷỹỵÝỲỶỸỴ";
            font = generator.generateFont(param);
            // Thêm font này vào skin với key 'default-font' (ghi đè nếu có)
            if (assets.uiSkin != null) {
                assets.uiSkin.add("default-font", font, BitmapFont.class);
            }

            // Tạo font nhỏ cho UI nhỏ (small-font)
            FreeTypeFontGenerator.FreeTypeFontParameter smallParam = new FreeTypeFontGenerator.FreeTypeFontParameter();
            smallParam.size = 14;
            smallParam.borderWidth = 0.3f;
            smallParam.borderColor = Color.DARK_GRAY;
            smallParam.characters = param.characters;
            BitmapFont smallFont = generator.generateFont(smallParam);
            if (assets.uiSkin != null) {
                assets.uiSkin.add("small-font", smallFont, BitmapFont.class);
            }

            generator.dispose();
        } catch (Exception e) {
            Gdx.app.error("AgeOfWarGame", "Không thể tạo font tiếng Việt, dùng font mặc định.", e);
            font = new BitmapFont();
        }

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
