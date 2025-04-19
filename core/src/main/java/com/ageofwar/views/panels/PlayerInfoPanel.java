package com.ageofwar.views.panels; // Tạo package mới cho các panel

import com.ageofwar.models.players.Player;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Panel hiển thị thông tin của người chơi (Vàng, XP, Máu, Kỷ nguyên).
 */
public class PlayerInfoPanel extends Table {

    private final Label playerGoldLabel;
    private final Label playerExpLabel;
    private final Label playerHealthLabel;
    private final Label playerEraLabel;

    /**
     * Khởi tạo PlayerInfoPanel.
     * @param skin Skin để tạo các Label.
     */
    public PlayerInfoPanel(Skin skin) {
        super(skin); // Gọi constructor của Table

        // Khởi tạo các Label
        playerGoldLabel = new Label("Vàng: 0", skin);
        playerExpLabel = new Label("XP: 0", skin);
        playerHealthLabel = new Label("Máu Nhà: 0/0", skin);
        playerEraLabel = new Label("Kỷ Nguyên: STONE", skin);

        // Thêm các Label vào Table (Panel)
        add(playerHealthLabel).padRight(20);
        add(playerGoldLabel).padRight(20);
        add(playerExpLabel).padRight(20);
        add(playerEraLabel);
    }

    /**
     * Cập nhật các Label với dữ liệu mới từ Player.
     * @param player Đối tượng Player của người chơi.
     */
    public void update(Player player) {
        playerGoldLabel.setText("Vàng: " + player.getGold());
        playerExpLabel.setText("XP: " + player.getExperience());
        playerHealthLabel.setText("Máu Nhà: " + player.getBaseHealth() + "/" + player.getMaxBaseHealth());
        playerEraLabel.setText("Kỷ Nguyên: " + player.getCurrentEra().name());
    }
}
