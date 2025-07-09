/**
 * Lớp trừu tượng cơ sở cho tất cả các thực thể trong trò chơi như Unit (lính) và Tower (trụ).
 * Chứa các thuộc tính và phương thức chung như vị trí, máu, sát thương, khả năng tấn công,
 * trạng thái sống/chết, và quản lý mục tiêu.
 * Implement Poolable để quản lý đối tượng hiệu quả bằng Object Pooling.
 */
package com.ageofwar.models;

import com.ageofwar.models.players.PlayerType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

// Lớp cơ sở cho Unit và Tower
public abstract class Entity implements Poolable {
    protected Vector2 position; // Vị trí của thực thể (thường là tâm hoặc gốc)
    protected Rectangle bounds; // Hình chữ nhật bao quanh, dùng cho hiển thị và va chạm/tương tác đơn giản
    protected int health; // Máu hiện tại
    protected int maxHealth; // Máu tối đa
    protected int damage; // Sát thương gây ra mỗi đòn tấn công
    protected float attackSpeed; // Tốc độ tấn công (số đòn đánh mỗi giây)
    protected float attackCooldown; // Bộ đếm thời gian hồi chiêu tấn công
    protected float range; // Tầm đánh
    protected boolean alive; // Trạng thái sống/chết
    protected PlayerType ownerType; // Người chơi sở hữu thực thể này (PLAYER hoặc AI)
    protected Entity target; // Mục tiêu hiện tại (có thể là null)

    public Entity() {
        position = new Vector2();
        bounds = new Rectangle();
        alive = false; // Ban đầu chưa sống cho đến khi được khởi tạo
    }

    /**
     * Logic khởi tạo chung cho các thực thể con.
     * @param owner Chủ sở hữu (PLAYER/AI).
     * @param maxHp Máu tối đa.
     * @param dmg Sát thương.
     * @param atkSpd Tốc độ tấn công.
     * @param rng Tầm đánh.
     * @param x Tọa độ x ban đầu.
     * @param y Tọa độ y ban đầu (thường là chân của thực thể).
     * @param width Chiều rộng.
     * @param height Chiều cao.
     */
    protected void initBase(PlayerType owner, int maxHp, int dmg, float atkSpd, float rng, float x, float y, float width, float height) {
        this.ownerType = owner;
        this.maxHealth = maxHp;
        this.health = maxHp;
        this.damage = dmg;
        this.attackSpeed = atkSpd;
        this.range = rng;
        this.position.set(x, y);
        // Đặt hình chữ nhật bao quanh: căn giữa theo chiều ngang, gốc ở tọa độ y
        this.bounds.set(x - width / 2, y, width, height);
        this.alive = true;
        this.attackCooldown = 0f; // Sẵn sàng tấn công ngay lúc đầu
        this.target = null;
        // Gdx.app.debug("Entity Init", this.getClass().getSimpleName() + " initialized at (" + x + "," + y + ") HP:" + health); // Gỡ lỗi: Ghi log khởi tạo
    }

    /**
     * Cập nhật trạng thái của thực thể mỗi khung hình.
     * @param delta Thời gian trôi qua kể từ khung hình trước (giây).
     */
    public void update(float delta) {
        if (!alive) return; // Không cập nhật nếu đã chết

        // Cập nhật thời gian hồi chiêu tấn công
        if (attackCooldown > 0) {
            attackCooldown -= delta;
        }
    }

    /**
     * Giảm máu của thực thể khi nhận sát thương.
     * Đặt trạng thái 'alive' thành false nếu máu về 0 hoặc thấp hơn.
     * @param amount Lượng sát thương nhận vào.
     */
    public void takeDamage(int amount) {
        if (!alive) return; // Không nhận sát thương nếu đã chết
        this.health -= amount;
        // Gdx.app.debug("Entity", this.getClass().getSimpleName() + " took " + amount + " damage. HP left: " + health); // Gỡ lỗi: Ghi log nhận sát thương
        if (this.health <= 0) {
            this.health = 0;
            this.alive = false;
            // Gdx.app.debug("Entity", this.getClass().getSimpleName() + " died."); // Gỡ lỗi: Ghi log khi chết
            // Có thể kích hoạt hiệu ứng hình ảnh/âm thanh khi chết ở đây hoặc trong lớp con
        }
    }

    /**
     * Đặt vị trí mới cho thực thể và cập nhật hình chữ nhật bao quanh.
     * @param x Tọa độ x mới.
     * @param y Tọa độ y mới.
     */
    public void setPosition(float x, float y) {
        this.position.set(x, y);
        // Cập nhật vị trí hình chữ nhật bao quanh tương ứng
        this.bounds.setPosition(x - bounds.width / 2, y);
    }

    /**
     * Kiểm tra xem thực thể có thể tấn công hay không (còn sống và đã hồi chiêu).
     * @return true nếu có thể tấn công, false nếu không.
     */
    public boolean canAttack() {
        return alive && attackCooldown <= 0;
    }

    /**
     * Đặt lại thời gian hồi chiêu tấn công dựa trên tốc độ tấn công.
     */
    public void resetAttackCooldown() {
        if (attackSpeed > 0) {
            this.attackCooldown = 1f / attackSpeed; // Thời gian hồi chiêu = 1 / số đòn đánh mỗi giây
        } else {
            this.attackCooldown = Float.MAX_VALUE; // Không thể tấn công nếu tốc độ là 0 hoặc âm
        }
    }

    // --- Getters (Phương thức lấy giá trị) ---
    public float getX() { return position.x; }
    public float getY() { return position.y; }
    public float getWidth() { return bounds.width; }
    public float getHeight() { return bounds.height; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public int getDamage() { return damage; }
    public float getRange() { return range; }
    public boolean isAlive() { return alive; }
    public PlayerType getOwnerType() { return ownerType; }
    public Rectangle getBounds() { return bounds; }
    public Entity getTarget() { return target; }

    // --- Setters (Phương thức đặt giá trị) ---
    public void setTarget(Entity target) { this.target = target; }


    /**
     * Phương thức bắt buộc từ giao diện Poolable.
     * Đặt lại trạng thái của đối tượng về giá trị mặc định khi nó được trả về Pool
     * hoặc được lấy ra từ Pool để tái sử dụng.
     */
    @Override
    public void reset() {
        // Đặt lại trạng thái khi được lấy từ pool hoặc giải phóng
        position.setZero();
        bounds.set(0,0,0,0);
        health = 0;
        maxHealth = 0;
        damage = 0;
        attackSpeed = 0;
        attackCooldown = 0;
        range = 0;
        alive = false;
        ownerType = null;
        target = null;
    }
}
