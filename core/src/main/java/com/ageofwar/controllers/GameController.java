package com.ageofwar.controllers;

import com.ageofwar.models.GameModel;
import com.ageofwar.utils.CameraManager; // Import CameraManager
import com.ageofwar.views.Hud;
import com.badlogic.gdx.Gdx; // Thêm nếu dùng Gdx.app

/**
 * Controls game logic updates that are NOT directly driven by raw input events.
 * Handles timing, game state transitions, potentially AI triggers.
 * Input handling is delegated to InputHandler. Camera management is delegated to CameraManager.
 */
public class GameController {

    private final GameModel model;
    private final Hud hud;
    private final CameraManager cameraManager; // *** Thêm tham chiếu CameraManager ***

    /**
     * Khởi tạo GameController.
     * @param model GameModel.
     * @param hud Hud.
     * @param cameraManager CameraManager. // *** Nhận CameraManager ***
     */
    public GameController(GameModel model, Hud hud, CameraManager cameraManager) {
        this.model = model;
        this.hud = hud;
        this.cameraManager = cameraManager; // *** Lưu tham chiếu ***
    }

    /**
     * Updates game logic that depends on time or state changes.
     * @param delta Time since last frame.
     */
    public void update(float delta) {
        // Logic update không đổi
    }

    /**
     * Xử lý khi người dùng click vào thế giới game (được gọi từ InputHandler).
     * @param worldX World X coordinate.
     * @param worldY World Y coordinate.
     */
    public void handleWorldClick(float worldX, float worldY) {
        // Logic xử lý click thế giới (ví dụ: đặt trụ) không đổi
        Gdx.app.debug("GameController", "Handled world click at: (" + worldX + ", " + worldY + ")");
        // ... (logic đặt trụ nếu có)
    }
}
