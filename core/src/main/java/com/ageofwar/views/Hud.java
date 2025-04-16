package com.ageofwar.views;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport; // Use ScreenViewport for HUD scaling
import com.ageofwar.AgeOfWarGame;
import com.ageofwar.configs.GameConfig;
import com.ageofwar.configs.UnitConfig;
import com.ageofwar.models.*;

public class Hud implements Disposable {

    private final Stage stage;
    private final Skin skin; // Keep final, ensure single assignment below
    private final GameModel model;
    private final AgeOfWarGame game; // To access GameModel indirectly or pass actions

    // Labels to display game info
    private final Label playerGoldLabel;
    private final Label playerExpLabel;
    private final Label playerHealthLabel;
    private final Label aiHealthLabel;
    private final Label playerEraLabel;
    private final Label messageLabel; // For errors like "Not enough gold"

    // Buttons
    private final TextButton upgradeEraButton;
    private final TextButton specialAbilityButton;
    private final ProgressBar specialCooldownBar;

    // Containers for dynamic buttons
    private final Table unitButtonTable;
    private final Table towerButtonTable; // Table to hold tower buttons
    private final ScrollPane unitScrollPane;
    private final ScrollPane towerScrollPane; // Scroll pane for towers


    public Hud(SpriteBatch batch, final GameModel model, final AgeOfWarGame game) {
        this.model = model;
        this.game = game;
        stage = new Stage(new ScreenViewport(), batch); // Use ScreenViewport for resolution independence

        // --- Load skin safely and assign final variable exactly once ---
        Skin loadedSkin; // Use a temporary variable
        try {
            // Ensure asset is loaded
            if (!game.assets.manager.isLoaded(game.assets.UI_SKIN)) {
                Gdx.app.log("Hud", "UI Skin not loaded, finishing loading...");
                game.assets.manager.finishLoadingAsset(game.assets.UI_SKIN);
            }
            // Ensure assets are assigned (should ideally happen once globally after loading)
            if (game.assets.uiSkin == null) {
                Gdx.app.log("Hud", "Assigning assets as uiSkin was null...");
                game.assets.assignAssets(); // This assigns game.assets.uiSkin
            }

            // Check AGAIN if assignAssets worked
            if (game.assets.uiSkin == null) {
                // If still null after trying to load/assign, something is wrong
                throw new IllegalStateException("Failed to load or assign UI skin from AssetManager.");
            }

            loadedSkin = game.assets.uiSkin; // Assign from manager result
            Gdx.app.log("Hud", "Successfully retrieved UI skin from AssetManager.");

        } catch (Exception e) {
            Gdx.app.error("Hud", "Failed to load UI skin from AssetManager. Using fallback.", e);
            // Fallback assignment ONLY in catch block
            loadedSkin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        }
        this.skin = loadedSkin; // Assign the final field 'skin' exactly once here
        // --- End of skin loading ---


        // --- Main HUD Table ---
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.top().pad(10); // Align to top with padding

        // --- Top Row: Player Info ---
        Table playerInfoTable = new Table();
        playerGoldLabel = new Label("Gold: 0", skin);
        playerExpLabel = new Label("XP: 0", skin);
        playerHealthLabel = new Label("Base HP: 0/0", skin);
        playerEraLabel = new Label("Era: STONE", skin);
        playerInfoTable.add(playerHealthLabel).padRight(20);
        playerInfoTable.add(playerGoldLabel).padRight(20);
        playerInfoTable.add(playerExpLabel).padRight(20);
        playerInfoTable.add(playerEraLabel);

        // --- Top Row: AI Info ---
        Table aiInfoTable = new Table();
        aiHealthLabel = new Label("AI HP: 0/0", skin);
        aiInfoTable.add(aiHealthLabel);


        // --- Middle Row: Messages ---
        messageLabel = new Label("", skin); // Initially empty
        messageLabel.setColor(Color.YELLOW);


        // --- Bottom Row: Controls ---
        Table controlTable = new Table();
        controlTable.bottom(); // Align this part to the bottom

        // Unit Buttons (in a ScrollPane)
        unitButtonTable = new Table();
        unitScrollPane = new ScrollPane(unitButtonTable, skin);
        unitScrollPane.setFadeScrollBars(false);
        unitScrollPane.setScrollingDisabled(false, true); // Allow horizontal scroll, disable vertical


        // Tower Buttons (similar setup)
        towerButtonTable = new Table();
        towerScrollPane = new ScrollPane(towerButtonTable, skin);
        towerScrollPane.setFadeScrollBars(false);
        towerScrollPane.setScrollingDisabled(false, true);


        // Upgrade and Special Buttons
        upgradeEraButton = new TextButton("Upgrade Era (Cost: ?)", skin);
        specialAbilityButton = new TextButton("Special (Cost: ?)", skin);
        specialCooldownBar = new ProgressBar(0f, 1f, 0.01f, false, skin); // Progress bar for cooldown


        upgradeEraButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("HUD", "Upgrade Era button clicked.");
                boolean success = model.upgradeEra(PlayerType.PLAYER);
                if (!success) {
                    showMessage("Not enough XP or max era reached!", 2f);
                } else {
                    updateEraSpecificButtons(); // Update buttons for new era
                    showMessage("Era Upgraded!", 1.5f);
                }
            }
        });


        specialAbilityButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.debug("HUD", "Special Ability button clicked.");
                boolean success = model.useSpecialAbility(PlayerType.PLAYER);
                if (!success) {
                    if (!model.getPlayer().canUseSpecial()) {
                        showMessage("Special Ability on Cooldown!", 2f);
                    } else {
                        showMessage("Not enough Gold for Special!", 2f);
                    }
                } else {
                    showMessage("Special Ability Activated!", 1.5f);
                }
            }
        });


        // --- Layouting the main table ---
        mainTable.add(playerInfoTable).expandX().left();
        mainTable.add(aiInfoTable).expandX().right();
        mainTable.row();
        mainTable.add(messageLabel).colspan(2).center().padTop(10); // Message spans both columns
        mainTable.row();
        mainTable.add(controlTable).colspan(2).expandY().bottom().fillX(); // Control table at bottom, spans columns


        // --- Layouting the control table ---
        controlTable.add(unitScrollPane).height(100).fillX().expandX().padRight(10); // Unit buttons on left
        controlTable.add(towerScrollPane).height(100).fillX().expandX().padLeft(10); // Tower buttons on right
        controlTable.row().padTop(10);
        Table upgradeSpecialTable = new Table();
        upgradeSpecialTable.add(upgradeEraButton).padRight(5);
        upgradeSpecialTable.add(specialAbilityButton).padRight(5);
        upgradeSpecialTable.add(specialCooldownBar).width(100); // Add progress bar
        controlTable.add(upgradeSpecialTable).colspan(2).center(); // Center upgrade/special buttons below scroll panes


        stage.addActor(mainTable);

        // Populate buttons initially based on starting era
        updateEraSpecificButtons();
        Gdx.app.log("HUD", "HUD initialized.");
    }

    // Call this when the player upgrades era
    private void updateEraSpecificButtons() {
        Era currentEra = model.getPlayer().getCurrentEra();
        Gdx.app.debug("HUD", "Updating buttons for Era: " + currentEra);

        // --- Update Unit Buttons ---
        unitButtonTable.clearChildren(); // Clear existing buttons
        for (final UnitType type : UnitConfig.getUnitsForEra(currentEra)) {
            int cost = UnitConfig.getUnitCost(type);
            TextButton button = new TextButton(type.name() + "\n(" + cost + "g)", skin); // Use a smaller style if available
            button.getLabel().setAlignment(Align.center); // Center text
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.debug("HUD", "Spawn Unit button clicked: " + type);
                    boolean success = model.spawnUnit(PlayerType.PLAYER, type);
                    if (!success) {
                        if (model.getPlayer().getGold() < UnitConfig.getUnitCost(type)) {
                            showMessage("Not enough Gold!", 2f);
                        } else {
                            showMessage("Cannot build yet!", 2f); // Era requirement etc.
                        }
                    }
                    // No success message needed, unit appears
                }
            });
            unitButtonTable.add(button).width(100).height(80).pad(5); // Add button with padding
        }
        unitButtonTable.pack(); // Recalculate layout


        // --- Update Tower Buttons ---
        towerButtonTable.clearChildren();
        for (final TowerType type : UnitConfig.getTowersForEra(currentEra)) {
            int cost = UnitConfig.getTowerCost(type);
            TextButton button = new TextButton(type.name() + "\n(" + cost + "g)", skin);
            button.getLabel().setAlignment(Align.center);
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.debug("HUD", "Build Tower button clicked: " + type);
                    // TODO: Implement tower placement logic
                    // For now, just try building at a default location or trigger placement mode
                    boolean success = model.buildTower(PlayerType.PLAYER, type, GameConfig.PLAYER_BASE_X + 150 + model.getWorld().getTowerCount(PlayerType.PLAYER) * 60); // Example placement
                    if (!success) {
                        if (model.getPlayer().getGold() < UnitConfig.getTowerCost(type)) {
                            showMessage("Not enough Gold!", 2f);
                        } else if (model.getWorld().getTowerCount(PlayerType.PLAYER) >= GameConfig.getMaxTowersForEra(model.getPlayer().getCurrentEra())){
                            showMessage("Tower limit reached!", 2f);
                        } else {
                            showMessage("Cannot build yet!", 2f);
                        }
                    } else {
                        showMessage("Tower Built!", 1.5f);
                    }
                }
            });
            towerButtonTable.add(button).width(100).height(80).pad(5);
        }
        towerButtonTable.pack();


        // Update Upgrade/Special button text/costs
        updateDynamicButtonLabels();
    }

    // Update labels that change frequently (gold, xp, health, button costs)
    public void update(float delta) {
        Player player = model.getPlayer();
        Player ai = model.getAiPlayer();

        playerGoldLabel.setText("Gold: " + player.getGold());
        playerExpLabel.setText("XP: " + player.getExperience());
        playerHealthLabel.setText("Base HP: " + player.getBaseHealth() + "/" + player.getMaxBaseHealth());
        playerEraLabel.setText("Era: " + player.getCurrentEra().name());
        aiHealthLabel.setText("AI HP: " + ai.getBaseHealth() + "/" + ai.getMaxBaseHealth());


        // Update Upgrade/Special button text based on current era costs
        updateDynamicButtonLabels();


        // Update special cooldown bar
        specialCooldownBar.setValue(player.getSpecialCooldownPercent());
        specialAbilityButton.setDisabled(!player.canUseSpecial()); // Disable button during cooldown


        // Update message label visibility/timer
        updateMessageLabel(delta);


        stage.act(delta);
    }

    private void updateDynamicButtonLabels() {
        Player player = model.getPlayer();
        Era currentEra = player.getCurrentEra();
        Era nextEra = Era.getNextEra(currentEra);

        // Update Upgrade Button
        if (nextEra != null) {
            int upgradeCost = GameConfig.getEraUpgradeCost(currentEra);
            upgradeEraButton.setText("Upgrade Era\n(" + upgradeCost + " XP)");
            upgradeEraButton.setDisabled(false);
        } else {
            upgradeEraButton.setText("Max Era Reached");
            upgradeEraButton.setDisabled(true);
        }
        upgradeEraButton.getLabel().setAlignment(Align.center);


        // Update Special Button
        int specialCost = GameConfig.getSpecialAbilityCost(currentEra);
        specialAbilityButton.setText("Special\n(" + specialCost + " G)");
        specialAbilityButton.getLabel().setAlignment(Align.center);
    }


    public void render() {
        stage.draw();
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update viewport on resize
    }

    // --- Message Handling ---
    private float messageTimer = 0f;

    private void showMessage(String message, float duration) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageTimer = duration;
        Gdx.app.debug("HUD Message", message);
    }

    private void updateMessageLabel(float delta) {
        if (messageTimer > 0) {
            messageTimer -= delta;
            if (messageTimer <= 0) {
                messageLabel.setVisible(false);
            }
        }
    }


    public Stage getStage() {
        return stage;
    }

    @Override
    public void dispose() {
        Gdx.app.log("HUD", "Disposing stage.");
        stage.dispose();
        // Dispose skin IF created programmatically here as a fallback
        // (We use AssetManager now, so this check isn't strictly needed if fallback is also from manager/shared)
        // if (skin != null && !game.assets.manager.contains(game.assets.UI_SKIN)) {
        //     skin.dispose(); // Only dispose if it's the fallback and NOT managed
        // }
    }
}
