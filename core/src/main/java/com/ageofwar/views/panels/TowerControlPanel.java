package com.ageofwar.views.panels;

import com.ageofwar.configs.GameConfig;
import com.ageofwar.configs.TowerConfig;
import com.ageofwar.models.Era;
import com.ageofwar.models.GameModel;
import com.ageofwar.models.players.PlayerType;
import com.ageofwar.models.towers.TowerType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * Panel quản lý các nút bấm và thanh cuộn để xây trụ.
 */
public class TowerControlPanel extends Table {

    private final GameModel model;
    private final Skin skin;
    private final Table towerButtonTable;
    private final ScrollPane towerScrollPane;
    private final MessagePanel messagePanel;

    /**
     * Khởi tạo TowerControlPanel.
     * @param model GameModel chính.
     * @param skin Skin cho UI.
     * @param messagePanel Panel để hiển thị thông báo.
     */
    public TowerControlPanel(final GameModel model, Skin skin, MessagePanel messagePanel) {
        super(skin);
        this.model = model;
        this.skin = skin;
        this.messagePanel = messagePanel;

        towerButtonTable = new Table();
        towerScrollPane = new ScrollPane(towerButtonTable, skin);
        towerScrollPane.setFadeScrollBars(false);
        towerScrollPane.setScrollingDisabled(false, true);

        add(towerScrollPane).height(100).fillX().expandX();

        updateButtons(model.getPlayer().getCurrentEra());
    }

    /**
     * Cập nhật các nút bấm xây trụ dựa trên kỷ nguyên.
     * @param currentEra Kỷ nguyên hiện tại của người chơi.
     */
    public void updateButtons(Era currentEra) {
        Gdx.app.debug("TowerControlPanel", "Đang cập nhật nút trụ cho Kỷ Nguyên: " + currentEra);
        towerButtonTable.clearChildren();
        for (final TowerType type : TowerConfig.getTowersForEra(currentEra)) {
            int cost = TowerConfig.getTowerCost(type);
            TextButton button = new TextButton(type.name() + "\n(" + cost + "g)", skin);
            button.getLabel().setAlignment(Align.center);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.debug("TowerControlPanel", "Nút Xây Trụ được nhấn: " + type);
                    // TODO: Triển khai logic đặt trụ (hiện tại chỉ đặt ở vị trí ví dụ)
                    boolean success = model.buildTower(PlayerType.PLAYER, type, GameConfig.PLAYER_BASE_X + 150 + model.getWorld().getTowerCount(PlayerType.PLAYER) * 60);
                    if (!success) {
                        if (model.getPlayer().getGold() < TowerConfig.getTowerCost(type)) {
                            messagePanel.showMessage("Không đủ Vàng!", 2f);
                        } else if (model.getWorld().getTowerCount(PlayerType.PLAYER) >= GameConfig.getMaxTowersForEra(model.getPlayer().getCurrentEra())) {
                            messagePanel.showMessage("Đã đạt giới hạn Trụ!", 2f);
                        } else {
                            messagePanel.showMessage("Chưa thể xây!", 2f);
                        }
                    } else {
                        messagePanel.showMessage("Đã xây Trụ!", 1.5f);
                    }
                }
            });
            towerButtonTable.add(button).width(100).height(80).pad(5);
        }
        towerButtonTable.pack();
    }
}
