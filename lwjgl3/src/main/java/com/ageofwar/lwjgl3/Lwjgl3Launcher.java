package com.ageofwar.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.ageofwar.AgeOfWarGame;

/** Launches the desktop (LWJGL3) application. */
public class Lwjgl3Launcher {
    public static void main(String[] args) {
        // Temporary fix for GLFW error on some Macs with M1/M2/M3 chips
        // See: https://github.com/libgdx/libgdx/issues/7228
        // if (System.getProperty("os.name").toLowerCase().contains("mac")) {
        // 	System.setProperty("java.awt.headless", "true");
        // }
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new AgeOfWarGame(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("CuocChienXuyenTheKy");
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate);
        //// If you remove the above line and set Vsync to false, you can get unlimited FPS, which can be
        //// useful for testing performance, but can also be very stressful to some hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can cause screen tearing.
        configuration.setWindowedMode(1280, 720); // Default window size
        configuration.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        // Add other configurations like fullscreen, resizable, etc. if needed
        // configuration.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
        // configuration.setResizable(false);
        return configuration;
    }
}
