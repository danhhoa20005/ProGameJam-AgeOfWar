package com.ageofwar.screens;

import com.ageofwar.AgeOfWarGame;
import com.ageofwar.configs.GameConfig;
import com.ageofwar.controllers.GameController;
import com.ageofwar.controllers.InputHandler;
import com.ageofwar.models.Era;
import com.ageofwar.models.GameModel;
import com.ageofwar.utils.CameraManager;
import com.ageofwar.views.GameRenderer;
import com.ageofwar.views.Hud;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;

public class GameScreen extends ScreenAdapter {

    private final AgeOfWarGame game;
    private final CameraManager cameraManager;
    private final GameModel model;
    private final GameRenderer renderer;
    private final GameController controller;
    private final InputHandler inputHandler;
    private final Hud hud;
    private final InputMultiplexer inputMultiplexer;

    public static final float WORLD_WIDTH = 1600;
    public static final float WORLD_HEIGHT = 600;

    // Biến để theo dõi kỷ nguyên cuối cùng đã biết
    private Era lastKnownPlayerEra = null;

    public GameScreen(final AgeOfWarGame game) {
        this.game = game;

        float worldBuffer = 50f;
        float minX = GameConfig.PLAYER_BASE_X - worldBuffer;
        float maxX = GameConfig.AI_BASE_X + worldBuffer;
        cameraManager = new CameraManager(WORLD_WIDTH/2, WORLD_HEIGHT/2, minX, maxX);

        model = new GameModel();
        try {
            model.initialize();
        } catch (Exception e) {
            Gdx.app.error("GameScreen", "Lỗi khi khởi tạo GameModel!", e);
            throw new RuntimeException("Không thể khởi tạo GameModel", e);
        }

        // Khởi tạo renderer SAU khi model đã initialize (để lấy era ban đầu nếu cần)
        renderer = new GameRenderer(game, game.batch, game.shapeRenderer, game.font, model, cameraManager);
        // Lưu kỷ nguyên ban đầu
        if (model.getPlayer() != null) { // Kiểm tra null an toàn
            lastKnownPlayerEra = model.getPlayer().getCurrentEra();
        }


        hud = new Hud(game.batch, model, game);
        controller = new GameController(model, hud, cameraManager);
        inputHandler = new InputHandler(model, hud, cameraManager, controller);

        // Input Multiplexer Setup
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputHandler); // InputHandler trước
        inputMultiplexer.addProcessor(hud.getStage()); // Stage sau
        Gdx.input.setInputProcessor(inputMultiplexer);

        Gdx.app.log("GameScreen", "Screen initialized.");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        // Cập nhật lại lastKnownEra khi màn hình được hiển thị lại (phòng trường hợp quay lại từ màn hình khác)
        if (model != null && model.getPlayer() != null) {
            lastKnownPlayerEra = model.getPlayer().getCurrentEra();
        }
        Gdx.app.log("GameScreen", "Input processor set.");
    }

    @Override
    public void render(float delta) {
        // Kiểm tra model null
        if (model == null || model.getPlayer() == null) {
            Gdx.app.error("GameScreen", "Model chưa được khởi tạo đúng cách.");
            return;
        }

        // --- Update ---
        cameraManager.update(delta);
        controller.update(delta);
        model.update(delta);
        hud.update(delta);

        // *** KIỂM TRA THAY ĐỔI KỶ NGUYÊN VÀ CẬP NHẬT RENDERER ***
        Era currentPlayerEra = model.getPlayer().getCurrentEra();
        if (currentPlayerEra != lastKnownPlayerEra) {
            Gdx.app.log("GameScreen", "Phát hiện thay đổi kỷ nguyên từ " + lastKnownPlayerEra + " sang " + currentPlayerEra);
            lastKnownPlayerEra = currentPlayerEra; // Cập nhật kỷ nguyên đã biết
            if (renderer != null) { // Kiểm tra renderer không null
                renderer.onEraChanged(lastKnownPlayerEra); // Thông báo cho renderer
            }
        }

        // Check game over
        if (model.isGameOver()) {
            game.setScreen(new EndGameScreen(game, model.getWinner()));
            dispose();
            return;
        }

        // --- Render ---
        Gdx.gl.glClearColor(0.5f, 0.7f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(cameraManager.getCamera().combined);
        game.shapeRenderer.setProjectionMatrix(cameraManager.getCamera().combined);

        renderer.render(delta); // renderer sẽ tự vẽ map đúng

        game.batch.setProjectionMatrix(hud.getStage().getCamera().combined);
        hud.render();
    }

    @Override
    public void resize(int width, int height) {
        if (cameraManager != null) cameraManager.resize(width, height); // Kiểm tra null nếu có thể lỗi khởi tạo
        if (hud != null) hud.resize(width, height);
        Gdx.app.log("GameScreen", "Resized to " + width + "x" + height);
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameScreen", "Disposing screen.");
        // Giải phóng tài nguyên theo thứ tự ngược lại hoặc kiểm tra null
        // if (inputHandler != null) { /* dispose nếu cần */ }
        // if (controller != null) { /* dispose nếu cần */ }
        if (hud != null) hud.dispose();
        if (renderer != null) renderer.dispose();
        if (model != null) model.dispose();
        // cameraManager không cần dispose
    }

    @Override
    public void hide() {
        Gdx.app.log("GameScreen", "Screen hidden.");
    }
}
