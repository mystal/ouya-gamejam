package com.mystal.ouyagamejam;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "OuyaGameJam";
		cfg.useGL20 = false;
		cfg.width = GameSettings.WINDOW_WIDTH;
		cfg.height = GameSettings.WINDOW_HEIGHT;
		
		new LwjglApplication(new OuyaGameJam(), cfg);
	}
}
