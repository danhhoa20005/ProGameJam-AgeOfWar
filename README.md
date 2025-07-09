# AgeOfWar

## Cây thư mục
```
[Tên Thư Mục Dự Án]/
├── core/
│   ├── src/
│   │   └── com/
│   │       └── ageofwar/
│   │           ├── AgeOfWarGame.java          # Lớp Game chính
│   │           ├── configs/                   # Package chứa các file cấu hình
│   │           │   ├── GameConfig.java
│   │           │   ├── TowerConfig.java       # Đã sửa từ các snippet trước (cần tạo file này)
│   │           │   └── UnitConfig.java
│   │           ├── controllers/               # Package chứa các lớp điều khiển
│   │           │   ├── AIController.java      # Quản lý logic AI
│   │           │   ├── GameController.java    # Điều khiển logic game tổng quát (đã tinh gọn)
│   │           │   └── InputHandler.java      # Xử lý input thô
│   │           ├── models/                    # Package chứa các lớp mô hình dữ liệu và trạng thái
│   │           │   ├── Era.java
│   │           │   ├── GameModel.java         # Model chính (đã tinh gọn)
│   │           │   ├── World.java             # Quản lý thực thể (đã tinh gọn)
│   │           │   ├── Entity.java            # Lớp cơ sở cho Unit/Tower
│   │           │   ├── players/               # Sub-package cho Player
│   │           │   │   ├── Player.java
│   │           │   │   └── PlayerType.java
│   │           │   ├── towers/                # Sub-package cho Tower
│   │           │   │   ├── Tower.java
│   │           │   │   └── TowerType.java
│   │           │   └── units/                 # Sub-package cho Unit
│   │           │       ├── Unit.java
│   │           │       └── UnitType.java
│   │           ├── screens/                   # Package chứa các màn hình game
│   │           │   ├── EndGameScreen.java
│   │           │   ├── GameScreen.java
│   │           │   └── MainMenuScreen.java
│   │           ├── systems/                   # Package chứa các hệ thống xử lý logic cụ thể
│   │           │   ├── CombatSystem.java      # Xử lý logic chiến đấu
│   │           │   └── SpecialAbilitySystem.java # Xử lý logic kỹ năng đặc biệt
│   │           ├── utils/                     # Package chứa các lớp tiện ích
│   │           │   └── Assets.java
│   │           └── views/                     # Package chứa các lớp hiển thị
│   │               ├── GameRenderer.java      # Renderer chính, điều phối (đã tinh gọn)
│   │               ├── Hud.java               # HUD chính, container (đã tinh gọn)
│   │               ├── panels/                # Package chứa các panel con của HUD
│   │               │   ├── AIInfoPanel.java
│   │               │   ├── GlobalControlPanel.java
│   │               │   ├── MessagePanel.java
│   │               │   ├── PlayerInfoPanel.java
│   │               │   ├── TowerControlPanel.java
│   │               │   └── UnitControlPanel.java
│   │               └── renderers/             # Package chứa các renderer con
│   │                   ├── BaseRenderer.java        # Lớp cơ sở cho renderer
│   │                   ├── PlayerBaseRenderer.java  # Vẽ căn cứ
│   │                   ├── TowerRenderer.java       # Vẽ trụ
│   │                   └── UnitRenderer.java        # Vẽ lính
│   └── assets/                            # Thư mục chứa tài nguyên (quan trọng!)
│       ├── images/
│       │   └── placeholder.png            # (Cần tạo hoặc thay thế)
│       ├── ui/
│       │   ├── uiskin.atlas               # (Cần cung cấp)
│       │   ├── uiskin.json                # (Cần cung cấp)
│       │   └── default.fnt                # (Cần cung cấp - hoặc font của skin)
│       └── maps/
│           └── game_map.tmx               # (Cần tạo và load trong Assets.java)
├── lwjgl3/                                # Module chạy game trên Desktop
│   └── src/
│       └── com/
│           └── ageofwar/
│               └── lwjgl3/
│                   └── Lwjgl3Launcher.java
├── build.gradle                           # File cấu hình build chính
├── gradle.properties                      # File thuộc tính Gradle (phiên bản, tên app)
├── settings.gradle                        # File cài đặt module Gradle
├── gradlew                                # Gradle wrapper (Linux/Mac)
├── gradlew.bat                            # Gradle wrapper (Windows)
└── README.md                              # File hướng dẫn
```

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

This project was generated with a template including simple application launchers and an `ApplicationAdapter` extension that draws libGDX logo.

## Platforms

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

This project uses [Gradle](https://gradle.org/) to manage dependencies.
The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.
Useful Gradle tasks and flags:

- `--continue`: when using this flag, errors will not stop the tasks from running.
- `--daemon`: thanks to this flag, Gradle daemon will be used to run chosen tasks.
- `--offline`: when using this flag, cached dependency archives will be used.
- `--refresh-dependencies`: this flag forces validation of all dependencies. Useful for snapshot versions.
- `build`: builds sources and archives of every project.
- `cleanEclipse`: removes Eclipse project data.
- `cleanIdea`: removes IntelliJ project data.
- `clean`: removes `build` folders, which store compiled classes and built archives.
- `eclipse`: generates Eclipse project data.
- `idea`: generates IntelliJ project data.
- `lwjgl3:jar`: builds application's runnable jar, which can be found at `lwjgl3/build/libs`.
- `lwjgl3:run`: starts the application.
- `test`: runs unit tests (if any).

Note that most tasks that are not specific to a single project can be run with `name:` prefix, where the `name` should be replaced with the ID of a specific project.
For example, `core:clean` removes `build` folder only from the `core` project.
