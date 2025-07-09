package com.ageofwar.views.panels;

import com.ageofwar.configs.GameConfig;
import com.ageofwar.models.Era;
import com.ageofwar.models.GameModel;
import com.ageofwar.models.players.Player;
import com.ageofwar.models.players.PlayerType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * Panel quản lý các nút điều khiển toàn cục (Nâng cấp, Kỹ năng đặc biệt) và thanh hồi chiêu.
 */
public class GlobalControlPanel extends Table {

    private final GameModel model;
    private final Skin skin;
    private final TextButton upgradeEraButton;
    private final TextButton specialAbilityButton;
    private final ProgressBar specialCooldownBar;
    private final MessagePanel messagePanel;
    private final UnitControlPanel unitControlPanel; // Cần để gọi update khi nâng cấp
    private final TowerControlPanel towerControlPanel; // Cần để gọi update khi nâng cấp


    /**
     * Khởi tạo GlobalControlPanel.
     * @param model GameModel chính.
     * @param skin Skin cho UI.
     * @param messagePanel Panel để hiển thị thông báo.
     * @param unitPanel Panel nút lính (để cập nhật khi nâng cấp).
     * @param towerPanel Panel nút trụ (để cập nhật khi nâng cấp).
     */
    public GlobalControlPanel(final GameModel model, Skin skin, MessagePanel messagePanel, UnitControlPanel unitPanel, TowerControlPanel towerPanel) {
        super(skin);
        this.model = model;
        this.skin = skin;
        this.messagePanel = messagePanel;
        this.unitControlPanel = unitPanel;
        this.towerControlPanel = towerPanel;

        upgradeEraButton = new TextButton("Nâng Cấp Kỷ Nguyên (Chi phí: ?)", skin);
        specialAbilityButton = new TextButton("Kỹ Năng Đặc Biệt (Chi phí: ?)", skin);
        specialCooldownBar = new ProgressBar(0f, 1f, 0.01f, false, skin);

        // Listener Nâng cấp
        upgradeEraButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("GlobalControlPanel", "Nút Nâng cấp Kỷ nguyên được nhấn.");
                boolean success = model.upgradeEra(PlayerType.PLAYER);
                if (!success) {
                    messagePanel.showMessage("Không đủ XP hoặc đã đạt kỷ nguyên tối đa!", 2f);
                } else {
                    // *** CẬP NHẬT CÁC PANEL KHÁC KHI NÂNG CẤP THÀNH CÔNG ***
                    Era newEra = model.getPlayer().getCurrentEra();
                    unitControlPanel.updateButtons(newEra);
                    towerControlPanel.updateButtons(newEra);
                    messagePanel.showMessage("Đã nâng cấp Kỷ nguyên!", 1.5f);
                    // Cập nhật lại nút này (chi phí mới) sẽ được thực hiện trong update()
                }
            }
        });

        // Listener Kỹ năng Đặc biệt
        specialAbilityButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("GlobalControlPanel", "Nút Kỹ năng Đặc biệt được nhấn.");
                boolean success = model.useSpecialAbility(PlayerType.PLAYER);
                if (!success) {
                    if (!model.getPlayer().canUseSpecial()) {
                        messagePanel.showMessage("Kỹ năng Đặc biệt đang hồi chiêu!", 2f);
                    } else {
                        messagePanel.showMessage("Không đủ Vàng cho Kỹ năng Đặc biệt!", 2f);
                    }
                } else {
                    messagePanel.showMessage("Đã kích hoạt Kỹ năng Đặc biệt!", 1.5f);
                }
            }
        });

        // Bố trí các nút trong panel này
        add(upgradeEraButton).padRight(5);
        add(specialAbilityButton).padRight(5);
        add(specialCooldownBar).width(100);
    }

    /**
     * Cập nhật trạng thái và nội dung của các nút điều khiển toàn cục.
     * @param player Đối tượng Player của người chơi.
     */
    public void update(Player player) {
        Era currentEra = player.getCurrentEra();
        Era nextEra = Era.getNextEra(currentEra);

        // Cập nhật nút Nâng cấp
        if (nextEra != null) {
            int upgradeCost = GameConfig.getEraUpgradeCost(currentEra);
            upgradeEraButton.setText("Nâng Cấp KN\n(" + upgradeCost + " XP)");
            upgradeEraButton.setDisabled(false);
        } else {
            upgradeEraButton.setText("Đã Đạt KN Tối Đa");
            upgradeEraButton.setDisabled(true);
        }
        upgradeEraButton.getLabel().setAlignment(Align.center);

        // Cập nhật nút Kỹ năng Đặc biệt
        int specialCost = GameConfig.getSpecialAbilityCost(currentEra);
        specialAbilityButton.setText("Kỹ Năng ĐB\n(" + specialCost + " G)");
        specialAbilityButton.getLabel().setAlignment(Align.center);

        // Cập nhật thanh hồi chiêu
        specialCooldownBar.setValue(player.getSpecialCooldownPercent());
        specialAbilityButton.setDisabled(!player.canUseSpecial());
    }
}
