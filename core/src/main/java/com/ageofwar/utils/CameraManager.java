package com.ageofwar.utils; // Đặt vào package utils hoặc tương tự

import com.ageofwar.screens.GameScreen; // Để lấy kích thước thế giới
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Lớp CameraManager quản lý việc tạo, cập nhật và điều khiển OrthographicCamera
 * và Viewport cho màn chơi game.
 */
public class CameraManager {

    private final OrthographicCamera camera;
    private final FitViewport viewport;

    // Giới hạn thực tế của thế giới game theo trục X
    private final float minWorldX;
    private final float maxWorldX;

    // Trạng thái điều khiển camera
    private boolean panningLeft = false;
    private boolean panningRight = false;
    private final float cameraPanSpeed = 500f;

    // --- THAM SỐ ZOOM ---
    private static final float ZOOM_STEP = 0.1f; // Độ nhạy khi zoom (giá trị nhỏ hơn -> zoom mượt hơn)
    private static final float MIN_ZOOM = 0.5f;  // Giới hạn zoom vào gần nhất (ví dụ: 0.5x)
    private static final float MAX_ZOOM = 2.0f;  // Giới hạn zoom ra xa nhất (ví dụ: 2.0x)
    // ---------------------

    private final Vector3 unprojectVec = new Vector3();

    public CameraManager(float viewportWidth, float viewportHeight, float minWorldX, float maxWorldX) {
        this.minWorldX = minWorldX;
        this.maxWorldX = maxWorldX;

        camera = new OrthographicCamera();
        viewport = new FitViewport(viewportWidth, viewportHeight, camera);

        // Đặt vị trí và zoom ban đầu
        float initialCamX = (minWorldX + maxWorldX) / 2f;
        float halfPassedViewportWidth = viewportWidth / 2f;
        float minInitialClamp = minWorldX + halfPassedViewportWidth;
        float maxInitialClamp = maxWorldX - halfPassedViewportWidth;
        if (minInitialClamp > maxInitialClamp) {
            minInitialClamp = maxInitialClamp = (minWorldX + maxWorldX) / 2f;
        }
        initialCamX = MathUtils.clamp(initialCamX, minInitialClamp, maxInitialClamp);

        camera.position.set(initialCamX, viewportHeight / 2f, 0);
        camera.zoom = 1.0f; // Zoom mặc định ban đầu
        camera.update();
        Gdx.app.log("CameraManager", "Initial Cam Pos: " + camera.position.x + ", Zoom: " + camera.zoom);
    }


    public void update(float delta) {
        handlePanning(delta);
    }

    /**
     * Xử lý logic lia camera dựa trên trạng thái panning và giới hạn thế giới thực tế.
     * @param delta Thời gian delta.
     */
    private void handlePanning(float delta) {
        if (!panningLeft && !panningRight) return;

        float panAmount = cameraPanSpeed * delta;
        float currentX = camera.position.x;
        // *** QUAN TRỌNG: Tính halfViewportWidth dựa trên zoom hiện tại ***
        // Điều này làm cho giới hạn panning cảm thấy đúng hơn khi zoom
        float halfViewportWidth = (viewport.getWorldWidth() * camera.zoom) / 2f;

        float minCamX = minWorldX + halfViewportWidth;
        float maxCamX = maxWorldX - halfViewportWidth;
        if (minCamX > maxCamX) {
            minCamX = maxCamX = (minWorldX + maxWorldX) / 2f;
        }

        float targetX = currentX;
        boolean shouldMove = false;

        if (panningLeft) {
            targetX = currentX - panAmount;
            shouldMove = true;
        } else if (panningRight) {
            targetX = currentX + panAmount;
            shouldMove = true;
        }

        if (shouldMove) {
            float newX = MathUtils.clamp(targetX, minCamX, maxCamX);
            if (!MathUtils.isEqual(newX, currentX)) {
                camera.position.x = newX;
                camera.update(); // Cập nhật camera sau khi pan
            }
        }
    }

    /**
     * Thay đổi mức độ zoom của camera.
     * @param amount Giá trị từ sự kiện scroll (thường là -1 cho zoom vào, +1 cho zoom ra).
     */
    public void zoomCamera(float amount) {
        // Tính toán mức zoom mới dựa trên hướng cuộn và ZOOM_STEP
        // amount < 0 (cuộn lên) -> zoom vào (giảm camera.zoom)
        // amount > 0 (cuộn xuống) -> zoom ra (tăng camera.zoom)
        float newZoom = camera.zoom + (amount * ZOOM_STEP);

        // Giới hạn giá trị zoom trong khoảng MIN_ZOOM và MAX_ZOOM
        camera.zoom = MathUtils.clamp(newZoom, MIN_ZOOM, MAX_ZOOM);

        Gdx.app.debug("CameraManager", "Zoom changed: " + camera.zoom + " (Amount: " + amount + ")");

        // Cập nhật camera để áp dụng thay đổi zoom
        camera.update();
    }

    public void panLeft(boolean pan) {
        // Thêm log để xem hàm này có được gọi đúng không
        Gdx.app.debug("CameraManager", "panLeft called with: " + pan);
        if (pan) {
            this.panningLeft = true;
            this.panningRight = false; // Đảm bảo tắt hướng kia
        } else {
            // Chỉ tắt nếu đang bật để tránh ghi đè không cần thiết
            if (this.panningLeft) {
                this.panningLeft = false;
            }
        }
    }

    public void panRight(boolean pan) {
        // Thêm log
        // Gdx.app.debug("CameraManager", "panRight called with: " + pan);
        if (pan) {
            this.panningRight = true;
            this.panningLeft = false; // Đảm bảo tắt hướng kia
        } else {
            // Chỉ tắt nếu đang bật
            if (this.panningRight) {
                this.panningRight = false;
            }
        }
    }

    /**
     * Cập nhật kích thước viewport khi cửa sổ thay đổi.
     * @param screenWidth Chiều rộng màn hình mới.
     * @param screenHeight Chiều cao màn hình mới.
     */
    public void resize(int screenWidth, int screenHeight) {
        viewport.update(screenWidth, screenHeight);
        // Không cần đặt lại vị trí camera ở đây trừ khi muốn căn giữa lại sau resize
        // camera.position.set(viewport.getWorldWidth() / 2f, viewport.getWorldHeight() / 2f, 0);
        // camera.update();
    }

    /**
     * Chuyển đổi tọa độ màn hình sang tọa độ thế giới game.
     * @param screenCoords Vector3 chứa tọa độ màn hình (x, y), z thường là 0. Sẽ được cập nhật thành tọa độ thế giới.
     * @return Vector3 đã được cập nhật với tọa độ thế giới.
     */
    public Vector3 unproject(Vector3 screenCoords) {
        viewport.unproject(screenCoords); // Viewport xử lý việc unproject chính xác hơn
        return screenCoords;
    }

    /**
     * Chuyển đổi tọa độ màn hình sang tọa độ thế giới game (phiên bản tiện lợi).
     * @param screenX Tọa độ x màn hình.
     * @param screenY Tọa độ y màn hình.
     * @return Vector3 chứa tọa độ thế giới. Sử dụng vector nội bộ để tránh cấp phát mới.
     */
    public Vector3 unproject(float screenX, float screenY) {
        unprojectVec.set(screenX, screenY, 0);
        return unproject(unprojectVec);
    }


    // --- Getters ---
    public OrthographicCamera getCamera() {
        return camera;
    }

    public Viewport getViewport() {
        return viewport;
    }
}
