package net.stoerbild.demo1;

import com.jme.app.SimplePassGame;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.pass.DirectionalShadowMapPass;
import com.jme.renderer.pass.RenderPass;
import com.jme.renderer.pass.ShadowedRenderPass;
import com.jme.scene.state.CullState;
import com.jme.scene.state.ZBufferState;
import com.jmex.effects.glsl.BloomRenderPass;

public class Application extends SimplePassGame {

	/**
	 * FPS update rate in seconds
	 */
	private static final int FPS_UPDATE_RATE = 1;

	/**
	 * FPS Statistics
	 */
	private final FPSCounter fpsCounter;

	/**
	 * The name of the application
	 */
	private final String appName;

	/**
	 * Current game level
	 */
	private ILevel level;

	/**
	 * Constructor
	 * 
	 * @param appName
	 *            for the application
	 */
	public Application(String appName) {
		super();
		samples = 3; // FSAA
		stencilBits = 8; // shadow calculation
		this.appName = appName;
		fpsCounter = new FPSCounter(FPS_UPDATE_RATE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.app.BaseSimpleGame#simpleInitGame()
	 */
	@Override
	protected void simpleInitGame() {
		display.setVSyncEnabled(false); // disable vsync for performance reasons

		display.getRenderer().setBackgroundColor(ColorRGBA.black);

		// z-buffer
		// TODO checkout: what does z buffer exactly do?
		final ZBufferState zBuffer = display.getRenderer().createZBufferState();
		zBuffer.setEnabled(true);
		zBuffer.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		rootNode.setRenderState(zBuffer);

		// cull all backface triangles to improve performance
		final CullState cullState = display.getRenderer().createCullState();
		cullState.setCullFace(CullState.Face.Back);
		rootNode.setRenderState(cullState);

		updateTitle();
		level = new DemoLevel(display.getRenderer(), rootNode);
		level.init();

		// as meshes do not change this will improve performance for about 20%
		rootNode.lockMeshes(); 

		// create all render passes.
		createPassManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.app.BaseSimpleGame#simpleUpdate()
	 */
	@Override
	protected void simpleUpdate() {
		assert level != null : "level is not correctly initialized";

		updateTitle();
		level.update();
	}

	/**
	 * Update the application title including current fps
	 */
	private void updateTitle() {
		assert fpsCounter != null : "fpsCounter is not correctly initialized";
		// TODO Investigate: update app title / calculate averge fps -> each frame bad performance?
		display.setTitle(appName + " (" + fpsCounter.getAveragedFps(timer) + ")");
	}

	private void createPassManager() {
		final RenderPass renderPass = new RenderPass();
		renderPass.add(rootNode);

		final ShadowedRenderPass shadowPass = new ShadowedRenderPass();
		shadowPass.add(rootNode);
		shadowPass.setRenderShadows(true);
		shadowPass.setLightingMethod(ShadowedRenderPass.LightingMethod.Additive);
		shadowPass.addOccluder(rootNode);

		final DirectionalShadowMapPass sPass = new DirectionalShadowMapPass(new Vector3f(10, -10,
				10));
		sPass.setViewDistance(100);
		sPass.add(rootNode);
		sPass.setViewTarget(new Vector3f(10, -5, 10));
		sPass.addOccluder(rootNode);
		
		final BloomRenderPass bloomPass = new BloomRenderPass(cam, 4);
		bloomPass.add(rootNode);
		bloomPass.setEnabled(true);
		bloomPass.setUseCurrentScene(true);
		bloomPass.setBlurIntensityMultiplier(1.7f);
		bloomPass.setBlurSize(0.001f);

		pManager.add(renderPass);
		// pManager.add(sPass);
		pManager.add(shadowPass);
		// pManager.add(bloomPass);
	}

}
