package com.ageofwar.views.renderers;

import com.badlogic.gdx.Gdx; // Thêm import Gdx
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Thêm import SpriteBatch
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
// import com.badlogic.gdx.maps.tiled.TmxMapLoader; // Bỏ vì không load trực tiếp ở đây
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.Disposable; // Implement Disposable

/**
 * Lớp MapRenderer chịu trách nhiệm vẽ bản đồ TiledMap của màn chơi.
 * Nó nhận một đối tượng TiledMap đã được tải và sử dụng OrthogonalTiledMapRenderer để vẽ.
 */
public class MapRenderer implements Disposable { // Implement Disposable
    // Biến lưu trữ bản đồ dạng tile
    private TiledMap map; // final vì được truyền vào và không thay đổi

    // Đối tượng để render bản đồ theo kiểu lưới vuông góc (orthogonal)
    private final OrthogonalTiledMapRenderer mapRenderer; // final

    // Thuộc tính bản đồ (width, height, tilewidth, tileheight,...)
    private MapProperties props; // final

    /**
     * Hàm khởi tạo - nhận vào đối tượng TiledMap đã được tải và SpriteBatch.
     * @param map Đối tượng TiledMap đã được tải (thường từ AssetManager).
     * @param batch SpriteBatch để vẽ map (chia sẻ với các renderer khác).
     */
    public MapRenderer(TiledMap map, SpriteBatch batch) {
        if (map == null) {
            Gdx.app.error("MapRenderer", "Đối tượng TiledMap được truyền vào là null!");
            // Xử lý lỗi, có thể throw exception hoặc dùng map mặc định
            throw new IllegalArgumentException("TiledMap không được null khi khởi tạo MapRenderer.");
        }
        this.map = map;

        // Tạo renderer để vẽ bản đồ, sử dụng batch được chia sẻ
        // Việc chia sẻ batch có thể tối ưu hiệu năng một chút
        this.mapRenderer = new OrthogonalTiledMapRenderer(this.map, batch);

        // Lấy các thuộc tính của bản đồ (số ô, kích thước ô, ...)
        this.props = map.getProperties();
        Gdx.app.log("MapRenderer", "MapRenderer được khởi tạo với bản đồ.");
    }

    public void setMap(TiledMap newMap) {
        if (newMap == null) {
            Gdx.app.error("MapRenderer", "Không thể đặt bản đồ mới vì nó là null!");
            return;
        }
        if (newMap == this.map) {
            // Gdx.app.debug("MapRenderer", "Bản đồ mới giống bản đồ hiện tại, không cần thay đổi.");
            return; // Không cần làm gì nếu map không đổi
        }

        this.map = newMap;
        this.props = newMap.getProperties();
        // Cập nhật map cho OrthogonalTiledMapRenderer
        this.mapRenderer.setMap(this.map);
        Gdx.app.log("MapRenderer", "Đã thay đổi bản đồ đang vẽ.");
        // Có thể cần cập nhật lại kích thước thế giới nếu nó phụ thuộc vào map mới
        // Ví dụ: world.updateBounds(getMapPixelWidth(), getMapPixelHeight());
    }

    /**
     * Hàm render bản đồ, sử dụng camera để xác định vùng nhìn.
     * @param camera OrthographicCamera của màn chơi game.
     */
    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);   // Thiết lập camera cho renderer
        mapRenderer.render();          // Thực hiện render bản đồ
    }

    /**
     * Giải phóng bộ nhớ khi không sử dụng.
     * OrthogonalTiledMapRenderer không tự dispose map, nên ta cần dispose map ở đây.
     */
    @Override
    public void dispose() {
        Gdx.app.log("MapRenderer", "Đang giải phóng MapRenderer...");
        // mapRenderer không cần dispose map nếu map được quản lý bởi AssetManager.
        // Nếu map KHÔNG được quản lý bởi AssetManager (như trong code gốc bạn gửi), thì mới cần gọi map.dispose().
        // Hiện tại, map được quản lý bởi AssetManager nên KHÔNG gọi map.dispose() ở đây.
        mapRenderer.dispose(); // Chỉ dispose renderer.
    }

    /**
     * Trả về chiều rộng thực tế của bản đồ (số ô * kích thước mỗi ô).
     * @return Chiều rộng bản đồ (pixel). Trả về 0 nếu không lấy được thuộc tính.
     */
    public float getMapPixelWidth() {
        try {
            int mapWidthInTiles = props.get("width", Integer.class);
            int tilePixelWidth = props.get("tilewidth", Integer.class);
            return mapWidthInTiles * tilePixelWidth;
        } catch (Exception e) {
            Gdx.app.error("MapRenderer", "Không thể lấy kích thước chiều rộng bản đồ từ properties.", e);
            return 0; // Trả về giá trị an toàn
        }
    }

    /**
     * Trả về chiều cao thực tế của bản đồ (số ô * kích thước mỗi ô).
     * @return Chiều cao bản đồ (pixel). Trả về 0 nếu không lấy được thuộc tính.
     */
    public float getMapPixelHeight() {
        try {
            int mapHeightInTiles = props.get("height", Integer.class);
            int tilePixelHeight = props.get("tileheight", Integer.class);
            return mapHeightInTiles * tilePixelHeight;
        } catch (Exception e) {
            Gdx.app.error("MapRenderer", "Không thể lấy kích thước chiều cao bản đồ từ properties.", e);
            return 0; // Trả về giá trị an toàn
        }
    }

    /**
     * Trả về đối tượng bản đồ đã load (chỉ dùng nếu thực sự cần truy cập trực tiếp).
     * @return Đối tượng TiledMap.
     */
    public TiledMap getMap() {
        return map;
    }
}
