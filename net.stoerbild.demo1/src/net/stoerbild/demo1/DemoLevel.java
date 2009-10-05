/**
 * 
 */
package net.stoerbild.demo1;

import net.stoerbild.demo1.coolarray.CoolArray;
import net.stoerbild.demo1.helper.ResourceManager;
import net.stoerbild.demo1.resources.Resources;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

/**
 * Stoerbild Demo Level. Creates an array of boxes with physical attributes.
 * 
 * @author mq
 */
public class DemoLevel implements ILevel {

	private final Node rootNode;
	private final Renderer renderer;
	private CoolArray coolArray;

	public DemoLevel(final Renderer renderer, final Node rootNode) {
		this.rootNode = rootNode;
		this.renderer = renderer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.stoerbild.demo1.ILevel#init()
	 */
	public void init() {
		// create the cool array
		coolArray = new CoolArray("coolArray1", 20, 20);
		rootNode.attachChild(coolArray);

		// createPlane();
		createLights();

		// set all key actions
		KeyBindingManager.getKeyBindingManager().set("pushall", KeyInput.KEY_F);
		KeyBindingManager.getKeyBindingManager().set("pushrand", KeyInput.KEY_V);
		KeyBindingManager.getKeyBindingManager().set("snake", KeyInput.KEY_X);
		KeyBindingManager.getKeyBindingManager().set("wave", KeyInput.KEY_G);
		KeyBindingManager.getKeyBindingManager().set("rings", KeyInput.KEY_U);
		KeyBindingManager.getKeyBindingManager().set("hold", KeyInput.KEY_H);
		KeyBindingManager.getKeyBindingManager().set("stopall", KeyInput.KEY_O);
		KeyBindingManager.getKeyBindingManager().set("velocity", KeyInput.KEY_M);
		KeyBindingManager.getKeyBindingManager().set("floor", KeyInput.KEY_K);

		// camera settings
		renderer.getCamera().setLocation(new Vector3f(94, 44, 96));
		renderer.getCamera().lookAt(new Vector3f(0, -10, 0), Vector3f.UNIT_Y);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.stoerbild.demo1.ILevel#update()
	 */
	public void update() {
		// handle all key events
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("pushall", false)) {
			coolArray.pushAll();
		}
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("pushrand", true)) {
			coolArray.pushRand();
		}
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("snake", false)) {
			coolArray.switchSnake();
		}
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("wave", false)) {
			coolArray.switchWave();
		}
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("rings", false)) {
			coolArray.switchRings();
		}
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("hold", false)) {
			coolArray.switchHold();
		}
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("stopall", false)) {
			coolArray.stopAll();
		}
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("velocity", false)) {
			coolArray.changeVelocity();
		}
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("floor", false)) {
			coolArray.switchFloor();
		}
		// update the array (calculate position of boxes)
		coolArray.update();
	}

	/**
	 * create all lights for the scene
	 */
	private void createLights() {
		final PointLight light1 = new PointLight();
		light1.setLocation(new Vector3f(-20, 30, -20));
		light1.setDiffuse(new ColorRGBA(0.7f, 0.7f, 0.7f, 1.0f));
		light1.setAttenuate(true);
		light1.setEnabled(true);
		light1.setShadowCaster(true);

		final PointLight light2 = new PointLight();
		light2.setLocation(new Vector3f(10, 50, 80));
		light2.setDiffuse(new ColorRGBA(0.6f, 0.6f, 0.65f, 1.0f));
		light2.setAttenuate(true);
		light2.setEnabled(true);
		light2.setShadowCaster(false);// disabled for performance reasons

		final PointLight light3 = new PointLight();
		light3.setLocation(new Vector3f(-20, 180, -80));
		light3.setDiffuse(new ColorRGBA(0.6f, 0.65f, 0.60f, 1.0f));
		light3.setAttenuate(true);
		light3.setEnabled(true);
		light3.setShadowCaster(true);

		final PointLight light4 = new PointLight();
		light4.setLocation(new Vector3f(50, -10, 20));
		light4.setDiffuse(new ColorRGBA(0.3f, 0.3f, 0.8f, 1.0f));
		light4.setAttenuate(true);
		light4.setEnabled(true);
		light4.setShadowCaster(false); // disabled for performance reasons

		// Attach the lights to a lightState and the lightState to rootNode
		final LightState lightState = renderer.createLightState();
		lightState.setEnabled(true);
		lightState.setGlobalAmbient(new ColorRGBA(.0f, 0.0f, 0.0f, 1f));
		lightState.attach(light1);
		lightState.attach(light2);
		lightState.attach(light3);
		lightState.attach(light4);

		assert lightState != null : "lightState is not initialized";
		rootNode.setRenderState(lightState);
	}

	/**
	 * Create a floor for the scene
	 */
	public void createPlane() {
		// create size vectors for box (100x0x100)
		final Vector3f min = new Vector3f(-100, -10, -100);
		final Vector3f max = new Vector3f(100, -10, 100);

		// create box
		final Box box = new Box("box", min, max);
		box.setModelBound(new BoundingBox());
		box.updateModelBound();

		// attach box to a new scene node and make it opaque
		final Node plane = new Node();
		plane.attachChild(box);
		plane.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
		plane.lockMeshes(); // mesh will not change. improves performance

		// create and set texture
		final TextureState textureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		final Texture texture = TextureManager.loadTexture(
				ResourceManager.getResource(Resources.TEXTURES_COLOR_WHITE_JPG),
				Texture.MinificationFilter.BilinearNearestMipMap,
				Texture.MagnificationFilter.Bilinear);
		textureState.setTexture(texture);
		plane.setRenderState(textureState);
		rootNode.attachChild(plane);
	}

}
