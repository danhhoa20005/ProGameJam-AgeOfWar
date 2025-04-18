package com.ageofwar.views.panels;

import com.ageofwar.configs.UnitConfig;
import com.ageofwar.models.Era;
import com.ageofwar.models.GameModel;
import com.ageofwar.models.players.PlayerType;
import com.ageofwar.models.units.UnitType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

/**
 * Panel quản lý các nút bấm và thanh cuộn để sinh lính.
 */
public class UnitControlPanel extends Table {

    private final GameModel model; // Cần model để gọi hành động và lấy dữ liệu
    private final Skin skin;
    private final Table unitButtonTable; // Bảng chứa các nút thực tế
    private final ScrollPane unitScrollPane;
    private final MessagePanel messagePanel; // Để hiển thị thông báo lỗi

    /**
     * Khởi tạo UnitControlPanel.
     * @param model GameModel chính.
     * @param skin Skin cho UI.
     * @param messagePanel Panel để hiển thị thông báo.
     */
    public UnitControlPanel(final GameModel model, Skin skin, MessagePanel messagePanel) {
        super(skin);
        this.model = model;
        this.skin = skin;
        this.messagePanel = messagePanel; // Lưu tham chiếu

        unitButtonTable = new Table();
        unitScrollPane = new ScrollPane(unitButtonTable, skin);
        unitScrollPane.setFadeScrollBars(false);
        unitScrollPane.setScrollingDisabled(false, true); // Chỉ cuộn ngang

        // Thêm ScrollPane vào UnitControlPanel (là một Table)
        add(unitScrollPane).height(100).fillX().expandX(); // Đặt kích thước và layout

        // Cập nhật nút lần đầu
        updateButtons(model.getPlayer().getCurrentEra());
    }

    /**
     * Cập nhật các nút bấm sinh lính dựa trên kỷ nguyên.
     * @param currentEra Kỷ nguyên hiện tại của người chơi.
     */
    public void updateButtons(Era currentEra) {
        Gdx.app.debug("UnitControlPanel", "Đang cập nhật nút lính cho Kỷ Nguyên: " + currentEra);
        unitButtonTable.clearChildren(); // Xóa nút cũ
        for (final UnitType type : UnitConfig.getUnitsForEra(currentEra)) {
            int cost = UnitConfig.getUnitCost(type);
            TextButton button = new TextButton(type.name() + "\n(" + cost + "g)", skin);
            button.getLabel().setAlignment(Align.center);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.debug("UnitControlPanel", "Nút Sinh Lính được nhấn: " + type);
                    boolean success = model.spawnUnit(PlayerType.PLAYER, type);
                    if (!success) {
                        if (model.getPlayer().getGold() < UnitConfig.getUnitCost(type)) {
                            messagePanel.showMessage("Không đủ Vàng!", 2f);
                        } else {
                            messagePanel.showMessage("Chưa thể tạo!", 2f);
                        }
                    }
                }
            });
            unitButtonTable.add(button).width(100).height(80).pad(5);
        }
        unitButtonTable.pack(); // Tính toán lại layout
    }

    // Panel này không cần hàm update(delta) riêng vì nó chỉ cập nhật khi kỷ nguyên thay đổi (được gọi từ bên ngoài)
}
