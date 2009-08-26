package net.stoerbild.demo1.resources;

import java.net.URL;

public class ResourceManager {

	/**
	 * Loads and returns resource
	 * 
	 * @param name
	 * @return URL of resource
	 */
	public static URL getResource(String name) {
		return ResourceManager.class.getClassLoader().getResource(name);
	}
	
}
