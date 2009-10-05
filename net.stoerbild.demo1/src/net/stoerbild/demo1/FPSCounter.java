package net.stoerbild.demo1;

import com.jme.util.Timer;

/**
 * FPSCounter will calculate averaged fps values
 * 
 * @author mq
 */
public class FPSCounter {

	// Update interval for fps in seconds
	private float updateRate = 1.0f;

	// time passed since last update
	private float lastUpdate = 0.0f;

	// last average fps
	private int averageFps = 0;

	private int fpsSum = 0;
	private int fpsCount = 0;

	/**
	 * Constructor
	 * 
	 * @param updateRate
	 */
	public FPSCounter(final int updateRate) {
		setUpdateRate(updateRate);
	}

	/**
	 * Get averaged fps.<br>
	 * 
	 * @param timer
	 *            if timer is null last average fps is returned
	 * @return average fps
	 */
	public int getAveragedFps(final Timer timer) {
		if (timer == null) {
			return averageFps;
		}
		if (lastUpdate > getUpdateRate()) {
			averageFps = fpsSum / fpsCount;
			lastUpdate = 0;
			fpsSum = 0;
			fpsCount = 0;
		}
		lastUpdate += timer.getTimePerFrame();
		fpsCount += 1;
		fpsSum += timer.getFrameRate();
		return averageFps;
	}

	/**
	 * Set the update rate for the fps counter
	 * 
	 * @param updateRate
	 *            the updateRate to set
	 */
	public void setUpdateRate(final float updateRate) {
		this.updateRate = updateRate;
	}

	/**
	 * @return the updateRate
	 */
	public float getUpdateRate() {
		return updateRate;
	}

}
