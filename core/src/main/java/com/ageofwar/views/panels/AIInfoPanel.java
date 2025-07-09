package com.ageofwar.views.panels;

import com.ageofwar.models.players.Player;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Panel hiển thị thông tin của AI (hiện tại chỉ có máu).
 */
public class AIInfoPanel extends Table {

    private final Label aiHealthLabel;

    /**
     * Khởi tạo AIInfoPanel.
     * @param skin Skin để tạo Label.
     */
    public AIInfoPanel(Skin skin) {
        super(skin);
        aiHealthLabel = new Label("Máu AI: 0/0", skin);
        add(aiHealthLabel);
    }

    /**
     * Cập nhật Label máu AI.
     * @param aiPlayer Đối tượng Player của AI.
     */
    public void update(Player aiPlayer) {
        aiHealthLabel.setText("Máu AI: " + aiPlayer.getBaseHealth() + "/" + aiPlayer.getMaxBaseHealth());
    }
}
