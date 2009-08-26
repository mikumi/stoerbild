package net.stoerbild.demo1;

/**
 * Interface for game levels
 * 
 * @author mq
 * 
 */
public interface ILevel {

	/**
	 * Initialize level
	 */
	public void init();

	/**
	 * Update level each frame
	 */
	public void update();

}
