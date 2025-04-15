package com.ageofwar.controllers;

import com.ageofwar.models.GameModel;
import com.ageofwar.models.players.PlayerType; // Đường dẫn import đã sửa dựa trên code người dùng
import com.ageofwar.screens.GameScreen;
import com.ageofwar.views.Hud;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Xử lý tất cả các sự kiện đầu vào thô (bàn phím, chuột) cho GameScreen.
 */
public class InputHandler extends InputAdapter {

    private final GameModel model; // Tham chiếu đến model game
    private final Hud hud; // Tham chiếu đến HUD
    private final OrthographicCamera camera; // Tham chiếu đến camera
    private final Viewport viewport; // Tham chiếu đến viewport
    private final GameController gameController; // Tham chiếu đến controller nếu cần cho các hành động

    // Các biến trạng thái đầu vào được chuyển đến đây
    private final float cameraPanSpeed = 500f; // Tốc độ di chuyển camera (pixels/giây)
    private final float edgePanThreshold = 50f; // Khoảng cách từ cạnh màn hình để bắt đầu pan (pixels)
    private boolean panningLeft = false; // Trạng thái đang pan sang trái
    private boolean panningRight = false; // Trạng thái đang pan sang phải

    // Vector tạm thời để unproject tọa độ, tránh cấp phát lại
    private final Vector3 touchPos = new Vector3();

    public InputHandler(GameModel model, Hud hud, OrthographicCamera camera, Viewport viewport, GameController gameController) {
        this.model = model;
        this.hud = hud;
        this.camera = camera;
        this.viewport = viewport;
        this.gameController = gameController; // Lưu tham chiếu controller
    }

    /**
     * Được gọi bởi vòng lặp render của GameScreen để cập nhật logic điều khiển bởi input
     * như di chuyển camera.
     * @param delta Thời gian kể từ khung hình trước.
     */
    public void update(float delta) {
        handleCameraPan(delta);
    }

    /**
     * Xử lý việc di chuyển camera dựa trên trạng thái panning (trái/phải).
     * @param delta Thời gian delta.
     */
    private void handleCameraPan(float delta) {
        float panAmount = cameraPanSpeed * delta; // Lượng di chuyển trong khung hình này
        float currentX = camera.position.x; // Vị trí X hiện tại của camera
        // Đảm bảo WORLD_WIDTH có thể truy cập, có thể cần truyền vào hoặc lấy từ model/config
        float worldWidth = GameScreen.WORLD_WIDTH; // Giả sử hằng số có thể truy cập
        float halfViewportWidth = camera.viewportWidth / 2f; // Nửa chiều rộng vùng nhìn thấy

        // Tính toán giới hạn di chuyển của camera
        float minCamX = halfViewportWidth; // Giới hạn trái
        float maxCamX = worldWidth - halfViewportWidth; // Giới hạn phải

        boolean cameraMoved = false; // Cờ kiểm tra xem camera có di chuyển không
        if (panningLeft) {
            // Di chuyển sang trái, giới hạn bởi minCamX và maxCamX
            float newX = MathUtils.clamp(currentX - panAmount, minCamX, maxCamX);
            if (newX != currentX) { // Chỉ cập nhật nếu vị trí mới khác vị trí cũ
                camera.position.x = newX;
                cameraMoved = true;
            }
            // Gdx.app.debug("InputHandler", "Đang pan Trái. Cam X: " + camera.position.x);
        } else if (panningRight) {
            // Di chuyển sang phải, giới hạn bởi minCamX và maxCamX
            float newX = MathUtils.clamp(currentX + panAmount, minCamX, maxCamX);
            if (newX != currentX) { // Chỉ cập nhật nếu vị trí mới khác vị trí cũ
                camera.position.x = newX;
                cameraMoved = true;
            }
            // Gdx.app.debug("InputHandler", "Đang pan Phải. Cam X: " + camera.position.x);
        }

        if (cameraMoved) {
            camera.update(); // Áp dụng thay đổi chỉ khi camera đã di chuyển
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        // Phím di chuyển Camera
        if (keycode == Keys.LEFT || keycode == Keys.A) {
            panningLeft = true;
            panningRight = false; // Đảm bảo chỉ pan một hướng
            Gdx.app.debug("InputHandler", "Nhấn phím: Trái/A - Bật pan Trái");
            return true; // Đã xử lý sự kiện
        }
        if (keycode == Keys.RIGHT || keycode == Keys.D) {
            panningRight = true;
            panningLeft = false; // Đảm bảo chỉ pan một hướng
            Gdx.app.debug("InputHandler", "Nhấn phím: Phải/D - Bật pan Phải");
            return true; // Đã xử lý sự kiện
        }

        // --- Phím Debug ---
        if (keycode == Keys.G) { // Thêm Vàng
            model.getPlayer().addGold(1000);
            Gdx.app.debug("InputHandler", "Debug: Đã thêm 1000 Vàng");
            return true; // Đã xử lý sự kiện
        }
        if (keycode == Keys.X) { // Thêm XP
            model.getPlayer().addExperience(500);
            Gdx.app.debug("InputHandler", "Debug: Đã thêm 500 XP");
            return true; // Đã xử lý sự kiện
        }
        if (keycode == Keys.U) { // Thử Nâng cấp Kỷ nguyên
            model.upgradeEra(PlayerType.PLAYER);
            Gdx.app.debug("Controller", "Debug: Đã thử Nâng cấp Kỷ nguyên");
            // Cần cập nhật các nút HUD sau khi debug nâng cấp
            // hud.updateEraSpecificButtons(); // Tạm thời comment out
            return true; // Đã xử lý sự kiện
        }
        if (keycode == Keys.K) { // Gây sát thương Nhà AI
            model.getAiPlayer().takeDamage(500);
            Gdx.app.debug("InputHandler", "Debug: Đã gây sát thương Nhà AI");
            return true; // Đã xử lý sự kiện
        }

        return false; // Phím không được xử lý
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Keys.LEFT || keycode == Keys.A) {
            panningLeft = false;
            Gdx.app.debug("InputHandler", "Nhả phím: Trái/A - Tắt pan Trái");
            return true; // Đã xử lý sự kiện
        }
        if (keycode == Keys.RIGHT || keycode == Keys.D) {
            panningRight = false;
            Gdx.app.debug("InputHandler", "Nhả phím: Phải/D - Tắt pan Phải");
            return true; // Đã xử lý sự kiện
        }
        return false; // Phím không được xử lý
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // Di chuyển Camera bằng cạnh màn hình với Chuột
        if (screenX < edgePanThreshold) { // Nếu chuột ở cạnh trái
            panningLeft = true;
            panningRight = false;
        } else if (screenX > Gdx.graphics.getWidth() - edgePanThreshold) { // Nếu chuột ở cạnh phải
            panningRight = true;
            panningLeft = false;
        } else { // Nếu chuột ở giữa
            // Chỉ dừng pan nếu nó được khởi tạo bởi cạnh màn hình chuột
            // Điều này ngăn việc pan bằng bàn phím bị dừng bởi di chuyển chuột ở giữa
            // Một giải pháp mạnh mẽ hơn có thể theo dõi *nguồn* của trạng thái panning.
            // Để đơn giản bây giờ, chúng ta dừng cả hai nếu chuột ở giữa.
            panningLeft = false;
            panningRight = false;
        }
        // Chúng ta trả về false để các bộ xử lý khác (như HUD Stage) có thể xử lý mouseMoved nếu cần.
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Chuyển đổi tọa độ màn hình sang tọa độ thế giới
        // Sử dụng vector touchPos đã cache để tránh tạo đối tượng Vector3 mới thường xuyên
        camera.unproject(touchPos.set(screenX, screenY, 0), viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());

        Gdx.app.debug("InputHandler", "Nhấn chuột xuống tại Screen(" + screenX + "," + screenY + ") -> World(" + touchPos.x + "," + touchPos.y + ")");

        // Ví dụ: Ủy thác click thế giới cho GameController hoặc xử lý trực tiếp
        // gameController.handleWorldClick(touchPos.x, touchPos.y);
        // Hoặc trực tiếp:
        // if (model.isInTowerPlacementMode()) {
        //    boolean placed = model.placeTower(PlayerType.PLAYER, model.getSelectedTowerType(), touchPos.x);
        //    if(placed) model.exitTowerPlacementMode();
        //    return true; // Sự kiện đã được xử lý
        // }

        // Trả về false có nghĩa là InputAdapter này không xử lý click.
        // Quan trọng: Điều này cho phép InputMultiplexer chuyển sự kiện đến bộ xử lý tiếp theo (HUD Stage).
        // Nếu chúng ta trả về true ở đây, các nút HUD sẽ không nhận được sự kiện click.
        return false;
    }

    // Các phương thức input khác (touchUp, touchDragged, scrolled) có thể được override nếu cần
}
