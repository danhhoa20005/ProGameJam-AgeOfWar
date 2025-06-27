package com.ageofwar.views.renderers;

import com.ageofwar.models.units.Unit;
import com.ageofwar.models.units.UnitState;
import com.ageofwar.models.World;
import com.ageofwar.systems.VisualEnhancementSystem;
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

    private float stateTime = 0f;
    private final SpriteBatch  batch;

    public UnitRenderer(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        super(shapeRenderer, batch);
        this.batch = batch;
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

        // Sử dụng VisualEnhancementSystem để vẽ thanh máu với collision avoidance
        Array<Unit> allUnits = new Array<>();
        allUnits.addAll(world.getPlayerUnits());
        allUnits.addAll(world.getAiUnits());

        VisualEnhancementSystem.renderEnhancedHealthBars(super.shapeRenderer, allUnits);
    }

    private void drawUnits(Array<Unit> units) {
        for (Unit unit : units) {
            if (!unit.isAlive() && unit.isDeathAnimationCompleted()) continue; // Không vẽ unit đã chết và hoàn thành animation death
            Rectangle b = unit.getBounds();
            Animation<TextureRegion> anim = unit.getCurrentAnimation();

            // Death animation không lặp lại, các animation khác lặp lại
            boolean looping = unit.getCurrentState() != UnitState.DEATH;
            TextureRegion frame = anim.getKeyFrame(stateTime, looping);

            // Draw without flipping; animation frames already represent orientation
            batch.draw(frame, b.x, b.y, b.width, b.height);
        }
    }
}
