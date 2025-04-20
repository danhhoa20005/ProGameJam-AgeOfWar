package com.ageofwar.controllers;

import com.ageofwar.models.GameModel;
import com.ageofwar.models.players.PlayerType;
import com.ageofwar.screens.GameScreen;
import com.ageofwar.utils.CameraManager; // Import CameraManager
import com.ageofwar.views.Hud;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
// import com.badlogic.gdx.graphics.OrthographicCamera; // Không cần nữa
// import com.badlogic.gdx.math.MathUtils; // Không cần nữa
import com.badlogic.gdx.math.Vector3;
// import com.badlogic.gdx.utils.viewport.Viewport; // Không cần nữa

/**
 * Handles all raw input events (keyboard, mouse) for the GameScreen.
 * Delegates camera control to CameraManager.
 */
public class InputHandler extends InputAdapter {

    private final GameModel model;
    private final Hud hud;
    private final CameraManager cameraManager; // *** Thay thế camera và viewport ***
    private final GameController gameController;

    // --- XÓA CÁC BIẾN TRẠNG THÁI PANNING ---
    // private final float cameraPanSpeed = 500f;
    private final float edgePanThreshold = 50f; // Vẫn cần để phát hiện cạnh màn hình
    // private boolean panningLeft = false;
    // private boolean panningRight = false;

    // Vector tạm cho unproject (có thể dùng cái của CameraManager)
    // private final Vector3 touchPos = new Vector3();

    /**
     * Khởi tạo InputHandler.
     * @param model GameModel.
     * @param hud Hud.
     * @param cameraManager CameraManager quản lý camera. // *** Nhận CameraManager ***
     * @param gameController GameController.
     */
    public InputHandler(GameModel model, Hud hud, CameraManager cameraManager, GameController gameController) {
        this.model = model;
        this.hud = hud;
        this.cameraManager = cameraManager; // *** Lưu tham chiếu CameraManager ***
        this.gameController = gameController;
    }

    @Override
    public boolean keyDown(int keycode) {
        boolean handled = false; // Cờ để xem sự kiện có được xử lý không
        if (keycode == Keys.LEFT || keycode == Keys.A) {
            cameraManager.panLeft(true);
            Gdx.app.debug("InputHandler", "KeyDown: Left/A"); // Log đơn giản
            handled = true;
        } else if (keycode == Keys.RIGHT || keycode == Keys.D) {
            cameraManager.panRight(true);
            Gdx.app.debug("InputHandler", "KeyDown: Right/D");
            handled = true;
        }

        // Chỉ trả về true nếu chúng ta thực sự xử lý phím đó
        return handled;
        // return false; // Nếu muốn sự kiện đi tiếp tới processor khác (thường không cần cho phím di chuyển)
    }


    @Override
    public boolean keyUp(int keycode) {
        boolean handled = false;
        if (keycode == Keys.LEFT || keycode == Keys.A) {
            cameraManager.panLeft(false);
            Gdx.app.debug("InputHandler", "KeyUp: Left/A"); // Log sự kiện keyUp
            handled = true;
        } else if (keycode == Keys.RIGHT || keycode == Keys.D) {
            cameraManager.panRight(false);
            Gdx.app.debug("InputHandler", "KeyUp: Right/D");
            handled = true;
        }
        return handled;
        // return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        boolean shouldPanLeft = screenX < edgePanThreshold;
        boolean shouldPanRight = screenX > Gdx.graphics.getWidth() - edgePanThreshold;

        // Chỉ gọi pan nếu trạng thái thay đổi hoặc đang ở cạnh
        if (shouldPanLeft) {
            cameraManager.panLeft(true);
        } else if (shouldPanRight) {
            cameraManager.panRight(true);
        } else {
            // Chỉ gọi pan(false) nếu đang panning để tránh gọi liên tục
            cameraManager.panLeft(false);
            cameraManager.panRight(false);
        }

        return false; // Để các listener khác cũng nhận sự kiện
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // *** Sử dụng phương thức unproject của CameraManager ***
        Vector3 worldCoordinates = cameraManager.unproject(screenX, screenY);

        Gdx.app.debug("InputHandler", "Touch Down at Screen(" + screenX + "," + screenY + ") -> World(" + worldCoordinates.x + "," + worldCoordinates.y + ")");

        // Ủy quyền xử lý click thế giới cho GameController
        gameController.handleWorldClick(worldCoordinates.x, worldCoordinates.y);

        return false; // Để HUD Stage cũng nhận được sự kiện click
    }

    /**
     * Xử lý sự kiện lăn chuột để zoom camera.
     * @param amountX Số lượng cuộn ngang (thường không dùng).
     * @param amountY Số lượng cuộn dọc (-1 cho cuộn lên/vào, +1 cho cuộn xuống/ra).
     * @return true nếu sự kiện đã được xử lý.
     */
    @Override
    public boolean scrolled(float amountX, float amountY) {
        // Gọi phương thức zoom của CameraManager với giá trị cuộn dọc
        cameraManager.zoomCamera(amountY);
        // Trả về true để báo rằng chúng ta đã xử lý sự kiện này
        return true;
    }
}
