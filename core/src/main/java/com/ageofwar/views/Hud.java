/**
 * Lớp Hud (Heads-Up Display) là container chính cho các thành phần giao diện người dùng (UI)
 * trong màn chơi game. Nó khởi tạo, sắp xếp và quản lý các panel UI con
 * (thông tin người chơi/AI, điều khiển lính/trụ, điều khiển toàn cục, thông báo).
 * Sử dụng Scene2D của LibGDX để tạo và quản lý Stage.
 * Implement Disposable để giải phóng tài nguyên của Stage.
 */
package com.ageofwar.views;

import com.ageofwar.models.players.Player; // Vẫn cần để truyền vào panel con
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ageofwar.AgeOfWarGame;
import com.ageofwar.models.GameModel;
import com.ageofwar.views.panels.*; // Import các panel mới

public class Hud implements Disposable {

    private final Stage stage;
    private final Skin skin; // Vẫn cần skin để truyền cho các panel
    private final GameModel model;
    private final AgeOfWarGame game;

    // Các panel con quản lý các phần của UI
    private final PlayerInfoPanel playerInfoPanel;
    private final AIInfoPanel aiInfoPanel;
    private final UnitControlPanel unitControlPanel;
    private final TowerControlPanel towerControlPanel;
    private final GlobalControlPanel globalControlPanel;
    private final MessagePanel messagePanel;

    // --- XÓA CÁC BIẾN THÀNH VIÊN CỦA WIDGET ĐÃ CHUYỂN VÀO PANEL CON ---
    // private final Label playerGoldLabel;
    // ... (xóa các label, button, table, scrollpane khác)


    public Hud(SpriteBatch batch, final GameModel model, final AgeOfWarGame game) {
        this.model = model;
        this.game = game;
        stage = new Stage(new ScreenViewport(), batch);

        // --- Tải skin an toàn (Giữ nguyên logic tải skin) ---
        Skin loadedSkin;
        try {
            if (!game.assets.manager.isLoaded(game.assets.UI_SKIN)) {
                Gdx.app.log("Hud", "UI Skin chưa được tải, đang hoàn tất tải...");
                game.assets.manager.finishLoadingAsset(game.assets.UI_SKIN);
            }
            if (game.assets.uiSkin == null) {
                Gdx.app.log("Hud", "Gán assets vì uiSkin là null...");
                game.assets.assignAssets();
            }
            if (game.assets.uiSkin == null) {
                throw new IllegalStateException("Không thể tải hoặc gán UI skin từ AssetManager.");
            }
            loadedSkin = game.assets.uiSkin;
            Gdx.app.log("Hud", "Lấy UI skin từ AssetManager thành công.");
        } catch (Exception e) {
            Gdx.app.error("Hud", "Không thể tải UI skin từ AssetManager. Sử dụng fallback.", e);
            loadedSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        }
        this.skin = loadedSkin;
        // --- Kết thúc tải skin ---


        // --- KHỞI TẠO CÁC PANEL CON ---
        messagePanel = new MessagePanel(skin); // Tạo message panel trước để truyền vào các control panel
        playerInfoPanel = new PlayerInfoPanel(skin);
        aiInfoPanel = new AIInfoPanel(skin);
        unitControlPanel = new UnitControlPanel(model, skin, messagePanel);
        towerControlPanel = new TowerControlPanel(model, skin, messagePanel);
        // Truyền unit/tower control panel vào global để nó có thể gọi update khi nâng cấp
        globalControlPanel = new GlobalControlPanel(model, skin, messagePanel, unitControlPanel, towerControlPanel);


        // --- Bảng HUD Chính ---
        Table mainTable = new Table(); // Không cần truyền skin vào đây nữa
        mainTable.setFillParent(true);
        mainTable.top().pad(10);

        // --- Bố trí các PANEL CON vào bảng chính ---
        mainTable.add(playerInfoPanel).expandX().left(); // Thêm panel thông tin người chơi
        mainTable.add(aiInfoPanel).expandX().right();   // Thêm panel thông tin AI
        mainTable.row();
        mainTable.add(messagePanel).colspan(2).center().padTop(10); // Thêm panel thông báo
        mainTable.row();

        // Bảng điều khiển dưới cùng
        Table controlTable = new Table(); // Bảng tạm để chứa các control panel
        controlTable.add(unitControlPanel).fillX().expandX().padRight(10); // Thêm panel nút lính
        controlTable.add(towerControlPanel).fillX().expandX().padLeft(10);  // Thêm panel nút trụ
        controlTable.row().padTop(10);
        controlTable.add(globalControlPanel).colspan(2).center(); // Thêm panel nút toàn cục

        // Thêm bảng điều khiển vào bảng chính
        mainTable.add(controlTable).colspan(2).expandY().bottom().fillX();


        stage.addActor(mainTable); // Thêm bảng chính vào stage

        // Không cần gọi updateEraSpecificButtons ở đây nữa, các panel tự khởi tạo nút
        Gdx.app.log("HUD", "HUD đã được khởi tạo với các panel con.");
    }

    // --- XÓA PHƯƠNG THỨC updateEraSpecificButtons() ---


    /**
     * Cập nhật các panel con cần cập nhật mỗi khung hình.
     * @param delta Thời gian trôi qua.
     */
    public void update(float delta) {
        Player player = model.getPlayer();
        Player ai = model.getAiPlayer();

        // Gọi update cho các panel cần thiết
        playerInfoPanel.update(player);
        aiInfoPanel.update(ai);
        globalControlPanel.update(player); // Cập nhật nút nâng cấp/ulti và cooldown
        messagePanel.update(delta); // Cập nhật timer thông báo

        stage.act(delta); // Cập nhật stage
    }

    // --- XÓA PHƯƠNG THỨC updateDynamicButtonLabels() và updateMessageLabel() ---


    /**
     * Vẽ HUD lên màn hình.
     */
    public void render() {
        stage.draw();
    }

    /**
     * Cập nhật viewport khi cửa sổ thay đổi kích thước.
     */
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    /**
     * Hiển thị một thông báo (ủy quyền cho MessagePanel).
     * @param message Nội dung thông báo.
     * @param duration Thời gian hiển thị.
     */
    public void showMessage(String message, float duration) {
        messagePanel.showMessage(message, duration); // Gọi hàm của panel con
    }


    /**
     * Trả về Stage để thiết lập input.
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Giải phóng tài nguyên.
     */
    @Override
    public void dispose() {
        Gdx.app.log("HUD", "Đang giải phóng stage.");
        stage.dispose();
        // Các panel con là Actor, sẽ được dispose cùng Stage
        // Skin không dispose ở đây
    }
}
