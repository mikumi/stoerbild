/**
 * 
 */
package net.stoerbild.demo1;

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
 * @author mq
 * 
 */
public class CoolBox extends Node {

	/**
	 * the shared node for all boxes
	 */
	private Node sharedNode;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7003865797724738726L;

	private float velocity = 0.0f;
	private final float gravity = 37.8f;
	private final float friction = 2.0f;

	/**
	 * @param name
	 */
	public CoolBox(int xPos, int zPos) {
		// create a copy of shared mesh and set correct position
		final SharedNode box = new SharedNode(getBox());
		box.setLocalTranslation(new Vector3f(5.5f * (xPos - 1), 0, 5.5f * (zPos - 1)));
		// box.setLocalTranslation(new Vector3f(5.0f * (xPos - 1), 0, 5.0f * (zPos - 1)));

		// attach box to scene (this) node and make it opaque
		attachChild(box);
		setRenderQueueMode(Renderer.QUEUE_OPAQUE);
	}

	private Node getBox() {
		if (sharedNode != null)
			return sharedNode;

		// create size vectors for box
		final Vector3f min = new Vector3f(0, 0, 0);
		final Vector3f max = new Vector3f(5, 10, 5);

		// create box
		final Box boxMesh = new Box("box", min, max);
		boxMesh.setModelBound(new BoundingBox());
		boxMesh.updateModelBound();
		boxMesh.setSolidColor(ColorRGBA.black);

		// assign material
		final MaterialState materialState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		materialState.setEmissive(new ColorRGBA(0.05f, 0.05f, 0.05f, 0.5f));
		materialState.setShininess(100);
		setRenderState(materialState);

		// assign mesh to the shared node
		final Node node = new Node();
		node.attachChild(boxMesh);

		assert node != null : "Shared node is not correctly initialized";
		return sharedNode = node;
	}

	/**
	 * update all properties like position for the box each frames
	 */
	public void update(boolean floorEnabled) {
		calcNewPos(Timer.getTimer().getTimePerFrame(), floorEnabled);
	}

	/**
	 * calculate new position, velocity ...
	 * 
	 * @param interpolation
	 */
	private void calcNewPos(float interpolation, boolean floorEnabled) {
		// we are only using Y-Translation (up and down)
		final float currentPos = getLocalTranslation().y;
		if ((Math.abs(velocity) == 0.0f) && (currentPos == 0.0f)) {
			return;
		}
		
		// floor
		if (floorEnabled) {
			// stop box from passing the floor (top-down or bottom-up).
			// The "2" is just for safety reasons. Without it boxes will sometimes pass the floor :-/
			if (((currentPos + (velocity * interpolation * 2) <= 0.0f) && (currentPos > 0.0f))
					|| ((currentPos + (velocity * interpolation * 2) >= 0.0f) && (currentPos < 0.0f))) {
				// location
				velocity = 0;
				setToPos(0.0f);
				return;
			}
		} else {
			// stop the box if it is not really moving anymore
			if ((Math.abs(velocity) < 0.2f) && (Math.abs(currentPos) < 0.3f)) {
				// location
				velocity = 0;
				setToPos(0.0f);
				return;
			}
		}

		// ceiling
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

		// location
		final float newPos = currentPos + (velocity * interpolation);
		setToPos(newPos);
	}

	/**
	 * Translate node according to new Y-Position
	 * 
	 * @param newPos
	 */
	private void setToPos(float newPos) {
		final Vector3f newTrans = getLocalTranslation();
		newTrans.y = newPos;
		setLocalTranslation(newTrans);
	}

	/**
	 * Push box into the air
	 */
	public void push() {
		// velocity = 13.0f;
		velocity = 35.0f;
	}

	/**
	 * Push box into the air
	 * 
	 * @param force
	 */
	public void push(float force) {
		// velocity = 13.0f;
		velocity = force;
	}

}
