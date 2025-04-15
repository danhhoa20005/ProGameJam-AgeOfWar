/**
 * Lớp GameRenderer chịu trách nhiệm chính điều phối việc vẽ màn chơi game.
 * Nó quản lý việc vẽ bản đồ nền và ủy quyền việc vẽ các thực thể (Units, Towers, Bases)
 * và thanh máu cho các lớp renderer chuyên biệt khác.
 * Implement Disposable để quản lý tài nguyên renderer.
 */
package com.ageofwar.views;

import com.ageofwar.AgeOfWarGame;
import com.ageofwar.models.GameModel;
// import com.ageofwar.models.Entity; // Không cần trực tiếp nữa
// import com.ageofwar.models.players.Player; // Không cần trực tiếp nữa
// import com.ageofwar.models.players.PlayerType; // Không cần trực tiếp nữa
import com.ageofwar.screens.GameScreen;
import com.ageofwar.views.renderers.PlayerBaseRenderer; // Import renderer mới
import com.ageofwar.views.renderers.TowerRenderer; // Import renderer mới
import com.ageofwar.views.renderers.UnitRenderer; // Import renderer mới
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont; // Vẫn cần nếu vẽ chữ
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
// import com.badlogic.gdx.math.Rectangle; // Không cần trực tiếp nữa
// import com.badlogic.gdx.utils.Array; // Không cần trực tiếp nữa
import com.badlogic.gdx.utils.Disposable;
// import com.ageofwar.configs.GameConfig; // Không cần trực tiếp nữa

public class GameRenderer implements Disposable {

    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont font;
    private final GameModel model;
    private final OrthographicCamera camera;
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final AgeOfWarGame game;

    // Các renderer chuyên biệt
    private final UnitRenderer unitRenderer;
    private final TowerRenderer towerRenderer;
    private final PlayerBaseRenderer baseRenderer;

    public GameRenderer(AgeOfWarGame game, SpriteBatch batch, ShapeRenderer shapeRenderer, BitmapFont font, GameModel model, OrthographicCamera camera) {
        this.game = game;
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
        this.font = font;
        this.model = model;
        this.camera = camera;

        // Khởi tạo Map Renderer
        TiledMap loadedMap = game.assets.gameMap;
        if (loadedMap == null) {
            Gdx.app.error("GameRenderer", "Bản đồ game là null!");
            this.mapRenderer = null;
        } else {
            this.mapRenderer = new OrthogonalTiledMapRenderer(loadedMap, batch);
        }

        // *** KHỞI TẠO CÁC RENDERER CON ***
        this.unitRenderer = new UnitRenderer(shapeRenderer, batch);
        this.towerRenderer = new TowerRenderer(shapeRenderer, batch);
        this.baseRenderer = new PlayerBaseRenderer(shapeRenderer, batch); // Đổi tên lớp BaseRenderer cũ
    }

    /**
     * Thực hiện vẽ toàn bộ màn chơi game cho một khung hình.
     * @param delta Thời gian trôi qua từ khung hình trước.
     */
    public void render(float delta) {
        // 1. Vẽ Map
        if (mapRenderer != null) {
            mapRenderer.setView(camera);
            mapRenderer.render();
        } else {
            // Vẽ nền thay thế nếu lỗi map
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeType.Filled);
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(0,0, GameScreen.WORLD_WIDTH, GameScreen.WORLD_HEIGHT);
            shapeRenderer.end();
        }

        // 2. Vẽ Thực thể Game (ủy quyền cho các renderer con)
        // Cần quản lý begin/end cho ShapeRenderer và SpriteBatch ở đây
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeType.Filled);

        // Gọi các renderer con để vẽ phần của chúng (bao gồm cả thanh máu)
        baseRenderer.render(model.getPlayer(), model.getAiPlayer());
        towerRenderer.render(model.getWorld());
        unitRenderer.render(model.getWorld());

        shapeRenderer.end(); // Kết thúc ShapeRenderer

        // 3. Vẽ bằng SpriteBatch (nếu cần)
        // batch.setProjectionMatrix(camera.combined);
        // batch.begin();
        // // Gọi các hàm vẽ sprite từ các renderer con nếu chúng có
        // // unitRenderer.renderSprites(model.getWorld());
        // // towerRenderer.renderSprites(model.getWorld());
        // // baseRenderer.renderSprites(model.getPlayer(), model.getAiPlayer());
        // batch.end();
    }

    /**
     * Giải phóng các tài nguyên mà GameRenderer sở hữu trực tiếp.
     */
    @Override
    public void dispose() {
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }
        // Các renderer con không tự quản lý tài nguyên Disposable chính (batch, shapeRenderer)
        // nên không cần gọi dispose() cho chúng.
    }
}
