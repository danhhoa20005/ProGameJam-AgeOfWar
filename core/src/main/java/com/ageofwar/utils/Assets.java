package com.ageofwar.utils;

import com.ageofwar.models.Era; // Import Era
import com.badlogic.gdx.Gdx; // Import Gdx
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap; // Sử dụng ObjectMap của LibGDX thay cho HashMap

public class Assets {
    public final AssetManager manager = new AssetManager();

    // --- ĐỊNH NGHĨA ĐƯỜNG DẪN ---
    public static final String PLACEHOLDER_TEXTURE = "images/placeholder.png";
    public static final String UI_SKIN = "ui/uiskin.json";
    public static final String BUTTON_TEXTURE = "Buttons/Button_Blue_3Slides_Pressed.png"; // Thêm đường dẫn đến nút bấm

    // Đường dẫn đến các file map theo kỷ nguyên (viết thường)
    public static final String MAP_FOLDER = "MAP/"; // Thư mục chứa map
    public static final String MAP_STONE = MAP_FOLDER + "map1.tmx";
    public static final String MAP_MEDIEVAL = MAP_FOLDER + "map_medieval.tmx";
    public static final String MAP_MODERN = MAP_FOLDER + "map_modern.tmx";
    public static final String MAP_FUTURE = MAP_FOLDER + "map_future.tmx";

    // --- BIẾN THAM CHIẾU TÀI NGUYÊN ---
    public Texture placeholderTex;
    public Texture buttonTex; // Biến tham chiếu Texture cho nút bấm
    public Skin uiSkin;
    // Sử dụng ObjectMap để lưu trữ các map theo Era
    public ObjectMap<Era, TiledMap> eraMaps;

    public void load() {
        // Load textures, skin
        manager.load(PLACEHOLDER_TEXTURE, Texture.class);
        manager.load(UI_SKIN, Skin.class);
        manager.load(BUTTON_TEXTURE, Texture.class); // Load Texture cho nút bấm

        // *** TẢI TẤT CẢ BẢN ĐỒ ***
        manager.setLoader(TiledMap.class, new TmxMapLoader(manager.getFileHandleResolver()));
        manager.load(MAP_STONE, TiledMap.class);
//        manager.load(MAP_MEDIEVAL, TiledMap.class);
//        manager.load(MAP_MODERN, TiledMap.class);
//        manager.load(MAP_FUTURE, TiledMap.class);

        Gdx.app.log("Assets", "Đã yêu cầu tải các tài nguyên.");
    }

    /**
     * Gán các tài nguyên đã tải xong vào các biến tham chiếu.
     * Phải được gọi SAU KHI manager.finishLoading() hoặc đảm bảo asset đã tải xong.
     */
    public void assignAssets() {
        Gdx.app.log("Assets", "Bắt đầu gán tài nguyên...");
        placeholderTex = manager.get(PLACEHOLDER_TEXTURE, Texture.class);
        uiSkin = manager.get(UI_SKIN, Skin.class);
        buttonTex = manager.get(BUTTON_TEXTURE, Texture.class); // Gán Texture cho nút bấm

        // *** GÁN CÁC BẢN ĐỒ VÀO MAP ***
        eraMaps = new ObjectMap<>(); // Khởi tạo ObjectMap
        try {
            eraMaps.put(Era.STONE, manager.get(MAP_STONE, TiledMap.class));
//            eraMaps.put(Era.MEDIEVAL, manager.get(MAP_MEDIEVAL, TiledMap.class));
//            eraMaps.put(Era.MODERN, manager.get(MAP_MODERN, TiledMap.class));
//            eraMaps.put(Era.FUTURE, manager.get(MAP_FUTURE, TiledMap.class));
            Gdx.app.log("Assets", "Đã gán các bản đồ vào eraMaps.");
        } catch (Exception e) {
            Gdx.app.error("Assets", "Lỗi khi gán bản đồ từ AssetManager. Kiểm tra đường dẫn và file map.", e);
            // Có thể throw lỗi hoặc để eraMaps thiếu map
        }

        Gdx.app.log("Assets", "Gán tài nguyên hoàn tất.");
    }

    /**
     * Lấy đối tượng TiledMap tương ứng với Era.
     * @param era Kỷ nguyên cần lấy map.
     * @return TiledMap tương ứng, hoặc null nếu không tìm thấy hoặc chưa load.
     */
    public TiledMap getMapForEra(Era era) {
        if (eraMaps == null) {
            Gdx.app.error("Assets", "eraMaps chưa được khởi tạo (assignAssets chưa được gọi?).");
            return null;
        }
        TiledMap map = eraMaps.get(era);
        if (map == null) {
            Gdx.app.error("Assets", "Không tìm thấy bản đồ cho kỷ nguyên: " + era + ". Đã tải và gán đúng cách chưa?");
        }
        return map;
    }


    public void dispose() {
        Gdx.app.log("Assets", "Disposing AssetManager...");
        manager.dispose();
        // eraMaps chứa các đối tượng được quản lý bởi manager, không cần dispose riêng ở đây.
    }
}
