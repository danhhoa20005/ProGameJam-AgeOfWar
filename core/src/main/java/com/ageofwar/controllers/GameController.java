package com.ageofwar.controllers;

import com.ageofwar.models.PlayerType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ageofwar.models.GameModel;
import com.ageofwar.screens.GameScreen; // For WORLD_WIDTH
import com.ageofwar.views.Hud;

public class GameController extends InputAdapter {

    private final GameModel model;
    private final Hud hud; // May need access to HUD state (e.g., tower placement mode)
    private final OrthographicCamera camera;
    private final Viewport viewport; // Needed for unprojecting coordinates

    private final float cameraPanSpeed = 500f; // Pixels per second
    private final float edgePanThreshold = 50f; // Pixels from edge to start panning

    private boolean panningLeft = false;
    private boolean panningRight = false;


    public GameController(GameModel model, Hud hud, OrthographicCamera camera, Viewport viewport) {
        this.model = model;
        this.hud = hud;
        this.camera = camera;
        this.viewport = viewport;
    }

    public void update(float delta) {
        handleCameraPan(delta);
    }

    private void handleCameraPan(float delta) {
        float panAmount = cameraPanSpeed * delta;
        float currentX = camera.position.x;
        float halfViewportWidth = camera.viewportWidth / 2f;

        // Calculate camera boundaries
        float minCamX = halfViewportWidth; // Left boundary
        float maxCamX = GameScreen.WORLD_WIDTH - halfViewportWidth; // Right boundary

        if (panningLeft) {
            camera.position.x = MathUtils.clamp(currentX - panAmount, minCamX, maxCamX);
            // Gdx.app.debug("Controller", "Panning Left. Cam X: " + camera.position.x);
        } else if (panningRight) {
            camera.position.x = MathUtils.clamp(currentX + panAmount, minCamX, maxCamX);
            // Gdx.app.debug("Controller", "Panning Right. Cam X: " + camera.position.x);
        }
        camera.update(); // Apply changes
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.LEFT || keycode == Keys.A) {
            panningLeft = true;
            panningRight = false; // Ensure only one direction at a time
            Gdx.app.debug("Controller", "Key Down: Left/A - Panning Left ON");
            return true;
        }
        if (keycode == Keys.RIGHT || keycode == Keys.D) {
            panningRight = true;
            panningLeft = false; // Ensure only one direction at a time
            Gdx.app.debug("Controller", "Key Down: Right/D - Panning Right ON");
            return true;
        }
        // --- Debug Keys ---
        if (keycode == Keys.G) { // Add Gold
            model.getPlayer().addGold(1000);
            Gdx.app.debug("Controller", "Debug: Added 1000 Gold");
            return true;
        }
        if (keycode == Keys.X) { // Add XP
            model.getPlayer().addExperience(500);
            Gdx.app.debug("Controller", "Debug: Added 500 XP");
            return true;
        }
        if (keycode == Keys.U) { // Try Upgrade Era
            model.upgradeEra(PlayerType.PLAYER);
            Gdx.app.debug("Controller", "Debug: Attempted Era Upgrade");
            // Need to update HUD buttons after debug upgrade
            //hud.updateEraSpecificButtons();
            return true;
        }
        if (keycode == Keys.K) { // Damage AI Base
            model.getAiPlayer().takeDamage(500);
            Gdx.app.debug("Controller", "Debug: Damaged AI Base");
            return true;
        }


        return false; // Key not handled
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Keys.LEFT || keycode == Keys.A) {
            panningLeft = false;
            Gdx.app.debug("Controller", "Key Up: Left/A - Panning Left OFF");
            return true;
        }
        if (keycode == Keys.RIGHT || keycode == Keys.D) {
            panningRight = false;
            Gdx.app.debug("Controller", "Key Up: Right/D - Panning Right OFF");
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        // Edge Panning with Mouse
        if (screenX < edgePanThreshold) {
            panningLeft = true;
            panningRight = false;
        } else if (screenX > Gdx.graphics.getWidth() - edgePanThreshold) {
            panningRight = true;
            panningLeft = false;
        } else {
            panningLeft = false;
            panningRight = false;
        }
        return false; // Don't consume the event, let UI handle it if needed
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // Handle clicks on the game world (e.g., for tower placement if implemented)
        // Convert screen coordinates to world coordinates
        Vector3 worldCoordinates = camera.unproject(new Vector3(screenX, screenY, 0), viewport.getScreenX(), viewport.getScreenY(), viewport.getScreenWidth(), viewport.getScreenHeight());

        Gdx.app.debug("Controller", "Touch Down at Screen(" + screenX + "," + screenY + ") -> World(" + worldCoordinates.x + "," + worldCoordinates.y + ")");

        // Example: If in tower placement mode, try to place tower here
        // if (isInTowerPlacementMode) {
        //    model.buildTower(PlayerType.PLAYER, selectedTowerType, worldCoordinates.x);
        //    exitTowerPlacementMode();
        //    return true; // Event handled
        // }

        return false; // Event not handled by game world click
    }

    // Other input methods (touchUp, touchDragged, scrolled) can be overridden if needed
}
