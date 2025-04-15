package com.ageofwar.controllers;

import com.ageofwar.models.GameModel;
import com.ageofwar.views.Hud;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Điều khiển các cập nhật logic game KHÔNG trực tiếp được thúc đẩy bởi các sự kiện đầu vào thô.
 * Xử lý thời gian, chuyển đổi trạng thái game, có thể kích hoạt AI (mặc dù logic AI nằm trong GameModel).
 * Việc xử lý đầu vào được ủy thác cho InputHandler.
 */
public class GameController {

    private final GameModel model; // Tham chiếu đến model game
    private final Hud hud; // Tham chiếu đến HUD
    // Không còn cần camera/viewport trực tiếp cho input, nhưng có thể cần chúng cho logic khác
    private final OrthographicCamera camera; // Tham chiếu đến camera
    private final Viewport viewport; // Tham chiếu đến viewport

    public GameController(GameModel model, Hud hud, OrthographicCamera camera, Viewport viewport) {
        this.model = model;
        this.hud = hud;
        this.camera = camera;
        this.viewport = viewport;
    }

    /**
     * Cập nhật logic game phụ thuộc vào thời gian hoặc thay đổi trạng thái, nhưng không phải đầu vào thô.
     * Hiện tại, hầu hết logic nằm trong GameModel.update(). Phương thức này có thể được mở rộng sau.
     * @param delta Thời gian kể từ khung hình trước.
     */
    public void update(float delta) {
        // Các ứng dụng tiềm năng trong tương lai:
        // - Kích hoạt các sự kiện cụ thể dựa trên bộ đếm thời gian không phù hợp với GameModel.
        // - Quản lý chuỗi phức tạp.
        // - Thay đổi trạng thái AI cấp cao (nếu logic AI trở nên phức tạp hơn việc sinh lính đơn giản).

        // Hiện tại, vòng lặp cập nhật chính bao gồm:
        // 1. InputHandler.update() (gọi từ GameScreen) -> xử lý di chuyển camera, v.v.
        // 2. GameModel.update() (gọi từ GameScreen) -> xử lý logic game cốt lõi, sinh lính AI, chiến đấu.
        // 3. Hud.update() (gọi từ GameScreen) -> cập nhật các yếu tố UI.
    }

    /**
     * Phương thức ví dụ có thể được gọi bởi InputHandler để phản ứng với các click vào thế giới game.
     * @param worldX Tọa độ X trong thế giới game.
     * @param worldY Tọa độ Y trong thế giới game.
     */
    public void handleWorldClick(float worldX, float worldY) {
        // Triển khai logic chọn lính, đặt trụ, v.v. dựa trên các click vào thế giới game.
        // Điều này giữ cho GameController chịu trách nhiệm *diễn giải* các hành động,
        // trong khi InputHandler chỉ báo cáo sự kiện đầu vào thô.
        // Gdx.app.debug("GameController", "Đã xử lý click thế giới tại: (" + worldX + ", " + worldY + ")");

        // Ví dụ: Nếu đang ở chế độ đặt trụ (trạng thái có thể được quản lý trong GameModel hoặc Hud)
        // if (model.isInTowerPlacementMode()) {
        //    boolean placed = model.buildTower(PlayerType.PLAYER, model.getSelectedTowerType(), worldX);
        //    if(placed) {
        //       model.exitTowerPlacementMode();
        //       hud.updatePlacementModeUI(false); // Cập nhật HUD tương ứng
        //    } else {
        //       hud.showMessage("Không thể đặt trụ ở đây!", 2f);
        //    }
        // }
    }

    // Các phương thức khác để điều khiển luồng game có thể được thêm vào đây.
}
