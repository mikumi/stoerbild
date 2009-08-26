package net.stoerbild.demo1;

import java.util.ArrayList;

import com.jme.scene.Node;
import com.jme.util.Timer;

public class CoolArray extends Node {

	/**
	 * hold all boxes at their position
	 */
	private boolean hold;

	private float pushVelocity;
	private boolean floorEnabled;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6523618470157024778L;

	private final ArrayList<CoolBox> boxes;

	// steps for the actions
	private int currentSnake;
	private int currentWave;
	private int currentRing;
	// timestamps last actions
	private float lastSnakeAction;
	private float lastWaveAction;
	private float lastRingAction;

	// size of the cool array
	int xSize;
	int zSize;

	/**
	 * Constructor
	 * 
	 * @param xSize
	 * @param zSize
	 */
	public CoolArray(int xSize, int zSize) {
		this("", xSize, zSize);
	}

	/**
	 * Constuctor
	 * 
	 * @param name
	 * @param xSize
	 * @param zSize
	 */
	public CoolArray(String name, int xSize, int zSize) {
		super(name);
		this.xSize = xSize;
		this.zSize = zSize;
		// create an array of cool boxes
		boxes = new ArrayList<CoolBox>();
		for (int j = 0; j < zSize; j++) {
			for (int i = 0; i < xSize; i++) {
				final CoolBox box = new CoolBox(i, j);
				attachChild(box);
				boxes.add(box);
			}
		}
		// reset all actions
		resetActions();
		lastSnakeAction = 0;
		lastWaveAction = 0;
		lastRingAction = 0;
		pushVelocity = 35.0f;
		floorEnabled = true;
		hold = false;
	}

	/**
	 * Update the complete array (position of all boxes)
	 */
	public void update() {
		if (hold)
			return;
		for (final CoolBox box : boxes) {
			snake();
			wave();
			ring();
			box.update(floorEnabled);
		}
	}

	/**
	 * Push complete array into the air
	 */
	public void pushAll() {
		if (hold)
			return;
		for (final CoolBox box : boxes) {
			box.push(pushVelocity);
		}
	}
	
	/**
	 * Enable/Disable floor
	 */
	public void switchFloor() {
		floorEnabled = !floorEnabled;
	}

	/**
	 * Push random boxes into the air
	 */
	public void pushRand() {
		if (hold)
			return;
		final double rand = Math.random();
		final int n = (int) Math.round(rand * (boxes.size() - 1));
		boxes.get(n).push(pushVelocity);
	}

	/**
	 * Create a wave going over the array
	 */
	private void wave() {
		if (currentWave < 0)
			return;
		// repeat after wave is finished
		if (currentWave >= zSize) {
			currentWave = 0;
			return;
		}

		if ((Timer.getTimer().getTimeInSeconds() - lastWaveAction) > 0.10f) {
			lastWaveAction = Timer.getTimer().getTimeInSeconds();
			// check which boxes should be pushed
			for (int i = 0; i < boxes.size(); i++) {
				if (getZ(i) == currentWave) {
					boxes.get(i).push(pushVelocity);
				}
			}
			currentWave = currentWave + 1;
		}

	}

	/**
	 * Create a snake going over the array
	 */
	private void snake() {
		if (currentSnake < 0)
			return;
		// repeat after snake is finished
		if (currentSnake >= boxes.size()) {
			currentSnake = 0;
			return;
		}

		// TODO refactor to use getX() and getZ()
		if ((Timer.getTimer().getTimeInSeconds() - lastSnakeAction) > 0.05f) {
			lastSnakeAction = Timer.getTimer().getTimeInSeconds();
			// check which box should be pushed next
			final double row = Math.ceil(currentSnake / xSize);
			if ((row % 2) > 0) {
				final int rowLeft = (currentSnake) % xSize;
				final int boxPush = (int) Math.ceil(currentSnake / xSize) * xSize
						+ (xSize - 1 - rowLeft);
				boxes.get(boxPush).push(pushVelocity);
			} else {
				boxes.get(currentSnake).push(pushVelocity);
			}
			currentSnake = currentSnake + 1;
		}
	}

	/**
	 * Create rings going over the array
	 */
	private void ring() {
		if (currentRing < 0)
			return;

		if ((Timer.getTimer().getTimeInSeconds() - lastRingAction) > 0.1f) {
			lastRingAction = Timer.getTimer().getTimeInSeconds();

			// check which boxes should be pushed
			for (int i = 0; i < boxes.size(); i++) {
				// each ring has 4 walls
				// 1. wall
				if ((getX(i) == currentRing) && (getZ(i) >= currentRing)
						&& (getZ(i) <= zSize - currentRing)) {
					boxes.get(i).push(pushVelocity);
				}
				// 2. wall
				if ((getX(i) == xSize - currentRing) && (getZ(i) >= currentRing)
						&& (getZ(i) <= zSize - currentRing)) {
					boxes.get(i).push(pushVelocity);
				}
				// 3. wall
				if ((getZ(i) == currentRing) && (getX(i) >= currentRing)
						&& (getX(i) <= xSize - currentRing)) {
					boxes.get(i).push(pushVelocity);
				}
				// 4. wall
				if ((getZ(i) == zSize - currentRing) && (getX(i) >= currentRing)
						&& (getX(i) <= xSize - currentRing)) {
					boxes.get(i).push(pushVelocity);
				}
			}

			// next ring
			if (currentRing == 0) {
				currentRing = Math.min(xSize, zSize) / 2;
			} else {
				currentRing = currentRing - 1;
			}
		}
	}

	/**
	 * stop all actions immediately
	 */
	private void resetActions() {
		currentSnake = -1;
		currentWave = -1;
		currentRing = -1;
	}

	/**
	 * start/stop the snake going through the array
	 */
	public void startSnake() {
		if (currentSnake >= 0) {
			currentSnake = -1;
			return;
		}
		currentSnake = 0;
	}

	/**
	 * Start/stop waves going over the array
	 */
	public void startWave() {
		if (currentWave >= 0) {
			currentWave = -1;
			return;
		}
		currentWave = 0;
	}

	/**
	 * Start/stop rings going over the array
	 */
	public void startRings() {
		if (currentRing >= 0) {
			currentRing = -1;
			return;
		}
		currentRing = Math.min(xSize, zSize) / 2;
	}

	/**
	 * Hold/release all boxes at their postion
	 */
	public void hold() {
		hold = !hold;
	}

	/**
	 * Stop all actions immediately
	 */
	public void stopAll() {
		resetActions();
	}

	/**
	 * Change the push velocity
	 */
	public void changeVelocity() {
		if (pushVelocity >= 120)
			pushVelocity = -20;
		else
			pushVelocity += 10;
	}

	/**
	 * Transform i into position in a 2D-array
	 * 
	 * @param i
	 * @return x coordinate
	 */
	private int getX(int i) {
		return (i % xSize);
	}

	/**
	 * Transform i into position in a 2D-array
	 * 
	 * @param i
	 * @return y coordinate
	 */
	private int getZ(int i) {
		return (int) Math.ceil(i / xSize);
	}

}
