/**
 * 
 */
package net.stoerbild.demo1.coolarray;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SharedNode;
import com.jme.scene.shape.Box;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;
import com.jme.util.Timer;

/**
 * A physical box that can be pushed into the air
 * 
 * @author mq
 */
public class CoolBox extends Node {

	// the shared node for all boxes
	private Node sharedNode;

	private static final long serialVersionUID = -7003865797724738726L;

	private float velocity = 0.0f;
	private final float gravity = 37.8f;
	private final float friction = 2.0f;

	/**
	 * Construtor
	 * 
	 * @param name
	 */
	public CoolBox(final int xPos, final int zPos) {
		// create a copy of shared mesh and set correct position
		final SharedNode box = new SharedNode(getBox());
		box.setLocalTranslation(new Vector3f(5.5f * (xPos - 1), 0, 5.5f * (zPos - 1)));

		// attach box to scene node(=this) and make it opaque
		attachChild(box);
		setRenderQueueMode(Renderer.QUEUE_OPAQUE);
	}

	/**
	 * This box is used for shared nodes. Return existing box or create new one
	 * 
	 * @return shared node
	 */
	private Node getBox() {
		if (sharedNode != null) {
			return sharedNode;
		}

		// create size vectors for box (size: 5x10x5)
		final Vector3f min = new Vector3f(0, 0, 0);
		final Vector3f max = new Vector3f(5, 10, 5);

		// create box
		final Box boxMesh = new Box("sharedbox", min, max);
		boxMesh.setModelBound(new BoundingBox());
		boxMesh.updateModelBound();
		boxMesh.setSolidColor(ColorRGBA.black); // this has no effect?

		// assign material
		final MaterialState materialState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		materialState.setEmissive(new ColorRGBA(0.05f, 0.05f, 0.05f, 0.5f));
		materialState.setShininess(100);
		setRenderState(materialState);

		// assign mesh to the shared node
		final Node node = new Node("sharednode");
		node.attachChild(boxMesh);

		assert node != null : "Shared node is not correctly initialized";
		return sharedNode = node;
	}

	/**
	 * update all properties like position for the box each frame
	 */
	public void update(final boolean floorEnabled) {
		calcNewPos(Timer.getTimer().getTimePerFrame(), floorEnabled);
	}

	/**
	 * calculate new position, velocity ...
	 * 
	 * @param interpolation
	 *            frame averaging
	 * @param floorEnabled
	 */
	private void calcNewPos(final float interpolation, final boolean floorEnabled) {
		// we are only using Y-Translation (up and down)
		final float currentPos = getLocalTranslation().y;
		// return if box is not moving
		if ((Math.abs(velocity) == 0.0f) && (currentPos == 0.0f)) {
			return;
		}

		// floor
		if (floorEnabled) {
			// stop box from passing the floor (top-down or bottom-up).
			// The "2-multiplier" is just for safety reasons. Without it boxes will sometimes pass the floor :-/
			if (((currentPos + (velocity * interpolation * 2) <= 0.0f) && (currentPos > 0.0f))
					|| ((currentPos + (velocity * interpolation * 2) >= 0.0f) && (currentPos < 0.0f))) {
				velocity = 0;
				setToPos(0.0f);
				return;
			}
		} else {
			// stop the box if it is not really moving anymore (snap into the floor)
			if ((Math.abs(velocity) < 0.2f) && (Math.abs(currentPos) < 0.3f)) {
				velocity = 0;
				setToPos(0.0f);
				return;
			}
		}

		// ceiling (bounce-back)
		if (currentPos > 1000f) {
			velocity = -velocity;
		}

		// gravity
		if (currentPos > 0) {
			velocity -= gravity * interpolation;
		} else {
			velocity += gravity * interpolation;
		}

		// friction
		if (velocity > 0) {
			velocity -= friction * interpolation;
		} else {
			velocity += friction * interpolation;
		}

		// new location
		final float newPos = currentPos + (velocity * interpolation);
		setToPos(newPos);
	}

	/**
	 * Translate node according to new Y-Position
	 * 
	 * @param newPos
	 */
	private void setToPos(final float newPos) {
		final Vector3f newTrans = getLocalTranslation();
		newTrans.y = newPos;
		setLocalTranslation(newTrans);
	}

	/**
	 * Push box into the air with fixed force
	 */
	public void push() {
		velocity += 35.0f;
	}

	/**
	 * Push box into the air with custom force
	 * 
	 * @param force
	 */
	public void push(final float force) {
		velocity += force;
	}

}
