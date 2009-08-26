package net.stoerbild.demo1;

import java.net.URL;

import net.stoerbild.demo1.resources.ResourceManager;
import net.stoerbild.demo1.resources.Resources;

import com.jme.app.AbstractGame.ConfigShowMode;

public class Main {

	private static final String APP_NAME = "Stoerbild - Demo 1";

	/**
	 * Entry Point
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// Configure application and start
		final Application app = new Application(APP_NAME);
		final URL logo = ResourceManager.getResource(Resources.IMAGES_SB_LOGO_JPG);
		app.setConfigShowMode(ConfigShowMode.AlwaysShow, logo);
		app.start();
	}

}
