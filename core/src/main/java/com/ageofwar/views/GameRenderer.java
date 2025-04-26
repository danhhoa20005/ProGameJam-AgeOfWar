package com.ageofwar.views;

import com.ageofwar.AgeOfWarGame;
import com.ageofwar.models.Era; // Import Era
import com.ageofwar.models.GameModel;
import com.ageofwar.screens.GameScreen;
import com.ageofwar.utils.CameraManager;
import com.ageofwar.views.renderers.MapRenderer;
import com.ageofwar.views.renderers.PlayerBaseRenderer;
import com.ageofwar.views.renderers.TowerRenderer;
import com.ageofwar.views.renderers.UnitRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap; // Import TiledMap
import com.badlogic.gdx.utils.Disposable;

public class GameRenderer implements Disposable {

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final GameModel model;
    private final CameraManager cameraManager;
    private final AgeOfWarGame game; // Cần để truy cập assets

    private MapRenderer mapRenderer;
    private final UnitRenderer unitRenderer;
    private final TowerRenderer towerRenderer;
    private final PlayerBaseRenderer baseRenderer;

    public GameRenderer(AgeOfWarGame game, SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font, GameModel model, CameraManager cameraManager) {
        this.game = game;
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
        this.font = font;
        this.model = model;
        this.cameraManager = cameraManager;

        // Khởi tạo MapRenderer với map ban đầu (Stone Age)
        TiledMap initialMap = null;
        try {
            // Đảm bảo assets đã được tải và gán
            if (game.assets.eraMaps == null) {
                Gdx.app.log("GameRenderer", "Gán assets vì eraMaps là null...");
                // Đảm bảo việc finishLoading đã xảy ra trước khi gọi assignAssets
                if(!game.assets.manager.update()) { // Nếu chưa load xong, chờ
                    game.assets.manager.finishLoading();
                }
                game.assets.assignAssets();
            }
            initialMap = game.assets.getMapForEra(Era.STONE); // Lấy map Stone Age
            if (initialMap == null) {
                throw new IllegalStateException("Không thể lấy bản đồ Stone Age ban đầu từ Assets.");
            }
            this.mapRenderer = new MapRenderer(initialMap, batch);
            Gdx.app.log("GameRenderer", "MapRenderer được khởi tạo thành công với map Stone Age.");
        } catch (Exception e) {
            Gdx.app.error("GameRenderer", "Lỗi nghiêm trọng khi khởi tạo MapRenderer với map ban đầu!", e);
            this.mapRenderer = null; // Đặt là null nếu lỗi
        }

        // Khởi tạo các renderer con khác
        this.unitRenderer = new UnitRenderer(shapeRenderer, batch);
        this.towerRenderer = new TowerRenderer(shapeRenderer, batch);
        this.baseRenderer = new PlayerBaseRenderer(shapeRenderer, batch);
    }

    /**
     * Được gọi khi kỷ nguyên của người chơi thay đổi.
     * Yêu cầu MapRenderer cập nhật bản đồ đang vẽ.
     * @param newEra Kỷ nguyên mới.
     */
    public void onEraChanged(Era newEra) {
        if (mapRenderer == null) {
            Gdx.app.error("GameRenderer", "Không thể thay đổi map vì MapRenderer là null.");
            return;
        }
        Gdx.app.log("GameRenderer", "Nhận được sự kiện thay đổi kỷ nguyên: " + newEra + ". Đang cập nhật map...");
        // Lấy map mới từ Assets
        TiledMap newMap = game.assets.getMapForEra(newEra);
        if (newMap != null) {
            // Yêu cầu MapRenderer sử dụng map mới
            mapRenderer.setMap(newMap);
        } else {
            Gdx.app.error("GameRenderer", "Không tìm thấy bản đồ cho kỷ nguyên " + newEra + " trong Assets!");
            // Giữ nguyên map cũ hoặc xử lý lỗi khác
        }
    }


    public void render(float delta) {
        // 1. Vẽ Map
        if (mapRenderer != null) {
            mapRenderer.render(cameraManager.getCamera()); // Ủy quyền vẽ map
        } else {
            // Vẽ nền thay thế nếu lỗi map
            shapeRenderer.setProjectionMatrix(cameraManager.getCamera().combined);
            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(0,0, GameScreen.WORLD_WIDTH, GameScreen.WORLD_HEIGHT);
            shapeRenderer.end();
        }

        // 2. Vẽ Thực thể Game (giữ nguyên)
        shapeRenderer.setProjectionMatrix(cameraManager.getCamera().combined);
        shapeRenderer.begin(ShapeType.Filled);
        baseRenderer.render(model.getPlayer(), model.getAiPlayer());
        towerRenderer.render(model.getWorld());
        unitRenderer.render(model.getWorld());
        shapeRenderer.end();

        // 3. Vẽ bằng SpriteBatch (nếu cần - giữ nguyên)
        // ...
    }

    @Override
    public void dispose() {
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
    }
}
