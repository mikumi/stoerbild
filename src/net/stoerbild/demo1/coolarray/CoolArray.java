package net.stoerbild.demo1.coolarray;

import java.util.ArrayList;

import com.jme.scene.Node;
import com.jme.util.Timer;

/**
 * Represents an array of boxes
 * 
 * @author mq
 */
public class CoolArray extends Node {

	// hold all boxes at their position
	private boolean hold;

	private float pushVelocity;
	private boolean floorEnabled;

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
	public CoolArray(final int xSize, final int zSize) {
		this("", xSize, zSize);
	}

	/**
	 * Constuctor
	 * 
	 * @param name
	 *            The name of the array scene node
	 * @param xSize
	 * @param zSize
	 */
	public CoolArray(final String name, final int xSize, final int zSize) {
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
		// as box-meshes do not change this will improve performance for about 20%
		lockMeshes();

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
		if (hold) {
			return;
		}
		// update running array actions
		snake();
		wave();
		ring();
		// calculate & update new positions
		for (final CoolBox box : boxes) {
			box.update(floorEnabled);
		}
	}

	/**
	 * Push whole array into the air
	 */
	public void pushAll() {
		if (hold) {
			return;
		}
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
	 * Push a random box into the air
	 */
	public void pushRand() {
		if (hold) {
			return;
		}
		final double rand = Math.random();
		final int n = (int) Math.round(rand * (boxes.size() - 1));
		boxes.get(n).push(pushVelocity);
	}

	/**
	 * A wave going over the array
	 */
	private void wave() {
		if (currentWave < 0) {
			return;
		}
		// restart wave after it is finished
		if (currentWave >= zSize) {
			currentWave = 0;
			return;
		}

		// push the according boxes into the air
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
		if (currentSnake < 0) {
			return;
		}
		// restart snake after it is finished
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
		if (currentRing < 0) {
			return;
		}

		if ((Timer.getTimer().getTimeInSeconds() - lastRingAction) > 0.1f) {
			lastRingAction = Timer.getTimer().getTimeInSeconds();

			// check which boxes should be pushed
			boolean[] pushedYet = new boolean[xSize * zSize];
			for (int i = 0; i < boxes.size(); i++) {
				// each ring has 4 walls
				// 1. wall
				if ((getX(i) == currentRing) && (getZ(i) >= currentRing)
						&& (getZ(i) <= zSize - currentRing)) {
					if (!pushedYet[i]) {
						boxes.get(i).push(pushVelocity);
						pushedYet[i] = true;
					}
				}
				// 2. wall
				if ((getX(i) == xSize - currentRing) && (getZ(i) >= currentRing)
						&& (getZ(i) <= zSize - currentRing)) {
					if (!pushedYet[i]) {
						boxes.get(i).push(pushVelocity);
						pushedYet[i] = true;
					}
				}
				// 3. wall
				if ((getZ(i) == currentRing) && (getX(i) >= currentRing)
						&& (getX(i) <= xSize - currentRing)) {
					if (!pushedYet[i]) {
						boxes.get(i).push(pushVelocity);
						pushedYet[i] = true;
					}
				}
				// 4. wall
				if ((getZ(i) == zSize - currentRing) && (getX(i) >= currentRing)
						&& (getX(i) <= xSize - currentRing)) {
					if (!pushedYet[i]) {
						boxes.get(i).push(pushVelocity);
						pushedYet[i] = true;
					}
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
	public void switchSnake() {
		if (currentSnake >= 0) {
			currentSnake = -1;
			return;
		}
		currentSnake = 0;
	}

	/**
	 * Start/stop waves going over the array
	 */
	public void switchWave() {
		if (currentWave >= 0) {
			currentWave = -1;
			return;
		}
		currentWave = 0;
	}

	/**
	 * Start/stop rings going over the array
	 */
	public void switchRings() {
		if (currentRing >= 0) {
			currentRing = -1;
			return;
		}
		currentRing = Math.min(xSize, zSize) / 2;
	}

	/**
	 * Hold/release all boxes at their postion
	 */
	public void switchHold() {
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
		if (pushVelocity >= 120) {
			pushVelocity = -20;
		} else {
			pushVelocity += 10;
		}
	}

	/**
	 * Transform a list position into position in a 2D-array
	 * 
	 * @param i
	 *            list position
	 * @return x array x-coordinate
	 */
	private int getX(final int i) {
		return (i % xSize);
	}

	/**
	 * Transform a list position into position in a 2D-array
	 * 
	 * @param i
	 *            list position
	 * @return y array y-coordinate
	 */
	private int getZ(final int i) {
		return (int) Math.ceil(i / xSize);
	}

}
