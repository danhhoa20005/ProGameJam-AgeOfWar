package com.ageofwar.views.panels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Panel quản lý việc hiển thị các thông báo tạm thời cho người chơi.
 */
public class MessagePanel extends Table {

    private final Label messageLabel;
    private float messageTimer = 0f;

    /**
     * Khởi tạo MessagePanel.
     * @param skin Skin để tạo Label.
     */
    public MessagePanel(Skin skin) {
        super(skin);
        messageLabel = new Label("", skin);
        messageLabel.setColor(Color.YELLOW);
        messageLabel.setVisible(false); // Ẩn ban đầu
        add(messageLabel).center(); // Thêm label vào panel và căn giữa
    }

    /**
     * Hiển thị một thông báo tạm thời.
     * @param message Nội dung thông báo.
     * @param duration Thời gian hiển thị (giây).
     */
    public void showMessage(String message, float duration) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageTimer = duration;
        Gdx.app.debug("MessagePanel", message); // Ghi log
    }

    /**
     * Cập nhật bộ đếm thời gian và ẩn thông báo khi hết giờ.
     * @param delta Thời gian trôi qua.
     */
    public void update(float delta) {
        if (messageTimer > 0) {
            messageTimer -= delta;
            if (messageTimer <= 0) {
                messageLabel.setVisible(false);
                messageTimer = 0f; // Đảm bảo timer về 0
            }
        }
    }
}
