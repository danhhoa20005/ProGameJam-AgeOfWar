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

    private final AgeOfWarGame game; // Tham chiếu đến lớp chính của trò chơi
    private Stage stage; // Sân khấu (Stage) để hiển thị UI
    private Skin skin; // Skin để định nghĩa giao diện UI

    /**
     * Hàm khởi tạo MainMenuScreen.
     *
     * @param game Lớp chính của trò chơi (AgeOfWarGame).
     */
    public MainMenuScreen(final AgeOfWarGame game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Gán xử lý đầu vào cho Stage

        try {
            // Kiểm tra tài nguyên skin đã được load chưa
            String uiSkinPath = com.ageofwar.utils.Assets.UI_SKIN + ".png"; // Thêm đuôi .png
            if (!game.assets.manager.isLoaded(uiSkinPath)) {
                game.assets.manager.finishLoadingAsset(uiSkinPath); // Load nếu chưa được load
            }
            // Gán skin từ AssetManager
            if (game.assets.uiSkin == null) {
                game.assets.assignAssets(); // Gán tài nguyên
            }
            skin = game.assets.uiSkin; // Sử dụng skin đã được gán
        } catch (Exception e) {
            Gdx.app.error("MainMenuScreen", "Không thể load skin UI. Sử dụng skin mặc định.", e);
            // Dùng skin mặc định (fallback) nếu xảy ra lỗi
            skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        }

        // Tạo bố cục (Table) để chứa các thành phần UI
        Table table = new Table();
        table.setFillParent(true); // Chiếm toàn bộ màn hình
        table.center(); // Căn giữa các thành phần

        // Tạo các thành phần UI (Label, Button)
        Label titleLabel = new Label("Cuộc Chiến Xuyên Thế Kỷ", skin); // Tiêu đề
        TextButton startButton = new TextButton("Bắt Đầu", skin); // Nút Bắt Đầu
        TextButton settingsButton = new TextButton("Cài Đặt (Chưa có)", skin); // Nút Cài Đặt
        TextButton quitButton = new TextButton("Thoát", skin); // Nút Thoát

        // Xử lý sự kiện khi nhấn nút "Bắt Đầu"
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Nút Bắt Đầu được nhấn.");
                try {
                    game.setScreen(new GameScreen(game)); // Chuyển sang màn hình chơi game
                    dispose(); // Giải phóng màn hình hiện tại
                } catch (Exception e) {
                    Gdx.app.error("MainMenuScreen", "Lỗi khi chuyển sang GameScreen", e);
                }
            }
        });

        // Xử lý sự kiện khi nhấn nút "Cài Đặt"
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Nút Cài Đặt được nhấn (Chưa thực hiện).");
            }
        });

        // Xử lý sự kiện khi nhấn nút "Thoát"
        quitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.log("MainMenuScreen", "Nút Thoát được nhấn.");
                Gdx.app.exit(); // Thoát trò chơi
            }
        });

        // Thêm các thành phần vào bảng
        table.add(titleLabel).padBottom(50).row(); // Thêm tiêu đề với khoảng cách dưới là 50px
        table.add(startButton).width(200).pad(10).row(); // Thêm nút Bắt Đầu
        table.add(settingsButton).width(200).pad(10).row(); // Thêm nút Cài Đặt
        table.add(quitButton).width(200).pad(10).row(); // Thêm nút Thoát

        // Thêm bảng vào Stage
        stage.addActor(table);
        Gdx.app.log("MainMenuScreen", "Màn hình chính đã được khởi tạo.");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage); // Gán xử lý đầu vào cho Stage
    }

    @Override
    public void render(float delta) {
        // Xóa màn hình với màu nền xám đậm
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Hoạt động và vẽ Stage
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Cập nhật Viewport khi kích thước thay đổi
    }

    @Override
    public void hide() {
        // Có thể bỏ xử lý đầu vào nếu cần khi màn hình bị ẩn
    }

    @Override
    public void dispose() {
        Gdx.app.log("MainMenuScreen", "Giải phóng màn hình chính.");
        stage.dispose(); // Giải phóng Stage
    }
}
