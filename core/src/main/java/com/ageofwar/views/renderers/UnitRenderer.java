package com.ageofwar.views.renderers;

import com.ageofwar.models.units.Unit;
import com.ageofwar.models.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * Lớp UnitRenderer chịu trách nhiệm vẽ các đơn vị lính (Units)
 * của cả người chơi và AI, bao gồm cả sprite animations và thanh máu.
 *
 * NOTE:
 *  - SpriteBatch.begin()/end() vẫn do UnitRenderer quản lý.
 *  - ShapeRenderer.begin()/end() phải được gọi bên ngoài (GameRenderer).
 */
public class UnitRenderer extends BaseRenderer {

    private final Color playerColor = Color.BLUE;
    private final Color aiColor     = Color.RED;

    private float stateTime = 0f;

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch  batch;

    public UnitRenderer(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        super(shapeRenderer, batch);
        this.shapeRenderer = shapeRenderer;
        this.batch         = batch;
    }

    /**
     * Vẽ tất cả các Unit lên màn hình.
     *
     * @param world Đối tượng World chứa danh sách Units.
     * @param delta Thời gian trôi qua từ frame trước để tính animation.
     */
    public void render(World world, float delta) {
        stateTime += delta;

        batch.begin();
        drawUnits(world.getPlayerUnits());
        drawUnits(world.getAiUnits());
        batch.end();

        drawUnitHealthBars(world.getPlayerUnits());
        drawUnitHealthBars(world.getAiUnits());
    }

    private void drawUnits(Array<Unit> units) {
        for (Unit unit : units) {
            if (!unit.isAlive()) continue;
            Rectangle b = unit.getBounds();
            Animation<TextureRegion> anim = unit.getCurrentAnimation();
            TextureRegion frame = anim.getKeyFrame(stateTime, true);

            if (unit.isFacingRight()) {
                batch.draw(frame, b.x, b.y, b.width, b.height);
            } else {
                batch.draw(frame, b.x + b.width, b.y, -b.width, b.height);
            }
        }
    }

    private void drawUnitHealthBars(Array<Unit> units) {
        for (Unit unit : units) {
            if (!unit.isAlive()) continue;
            Rectangle b = unit.getBounds();
            drawHealthBar(
                b.x,
                b.y + b.height + 5,
                b.width,
                5,
                unit.getHealth(),
                unit.getMaxHealth()
            );
        }
    }
}
