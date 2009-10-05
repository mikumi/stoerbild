package net.stoerbild.demo1.resources;

import java.net.URL;

/**
 * Resource Manager uses Classloder for loading resources
 * @author mq
 *
 */
public class ResourceManager {

	/**
	 * Load and return resource
	 * 
	 * @param name
	 * @return URL of resource
	 */
	public static URL getResource(final String name) {
		return ResourceManager.class.getClassLoader().getResource(name);
	}

}
