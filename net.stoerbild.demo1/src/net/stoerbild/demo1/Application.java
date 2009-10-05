package net.stoerbild.demo1;

import net.stoerbild.demo1.resources.ResourceManager;
import net.stoerbild.demo1.resources.Resources;

import com.jme.app.SimplePassGame;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.pass.DirectionalShadowMapPass;
import com.jme.renderer.pass.RenderPass;
import com.jme.renderer.pass.ShadowedRenderPass;
import com.jme.scene.state.CullState;
import com.jme.scene.state.ZBufferState;
import com.jmex.audio.AudioSystem;
import com.jmex.audio.AudioTrack;
import com.jmex.audio.MusicTrackQueue;
import com.jmex.audio.MusicTrackQueue.RepeatType;
import com.jmex.effects.glsl.BloomRenderPass;

/**
 * Stoerbild tech demo main application. configures display system, audio, levels...
 * 
 * @author mq
 */
public class Application extends SimplePassGame {

	// FPS update rate in seconds
	private static final int FPS_UPDATE_RATE = 1;

	// FPS Statistics
	private final FPSCounter fpsCounter;

	// The name of the application
	private final String appName;

	// Current game level
	private ILevel level;

	/**
	 * Constructor
	 * 
	 * @param appName
	 *            for the application
	 */
	public Application(final String appName) {
		super();
		samples = 2; // FSAA
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
		assert display != null : "display is not correctly initialized";
		assert rootNode != null : "rootNode is not correctly initialized";
		
		display.setVSyncEnabled(false); // disable vsync for performance reasons

		display.getRenderer().setBackgroundColor(ColorRGBA.black);

		// z-buffer
		// TODO checkout: what exactly does z buffer do?
		final ZBufferState zBuffer = display.getRenderer().createZBufferState();
		zBuffer.setEnabled(true);
		zBuffer.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
		rootNode.setRenderState(zBuffer);

		// cull all backface triangles to improve performance
		final CullState cullState = display.getRenderer().createCullState();
		cullState.setCullFace(CullState.Face.Back);
		rootNode.setRenderState(cullState);

		// Set application window title and load demo level
		updateWindowTitle();
		level = new DemoLevel(display.getRenderer(), rootNode);
		level.init();

		// demo level is already locked internally, but locking root node will increase performance
		// for an additional 5-10%
		rootNode.lockMeshes();

		// create all render passes.
		createPassManager();

		// initialize sound & music
		final AudioTrack track = AudioSystem.getSystem().createAudioTrack(
				ResourceManager.getResource(Resources.MUSIC_CASIO_PAYA_MP3), false);
		System.out.println(Resources.MUSIC_CASIO_PAYA_MP3);
		final MusicTrackQueue queue = AudioSystem.getSystem().getMusicQueue();
		queue.setCrossfadeinTime(0);
		queue.setRepeatType(RepeatType.ONE);
		queue.addTrack(track);
		//queue.play();

		//MouseInput.get().setCursorVisible(true);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jme.app.BaseSimpleGame#simpleUpdate()
	 */
	@Override
	protected void simpleUpdate() {
		assert level != null : "level is not correctly initialized";

		updateWindowTitle();
		level.update();

		//AudioSystem.getSystem().update();
	}

	/**
	 * Update the application window title including current fps
	 */
	private void updateWindowTitle() {
		assert fpsCounter != null : "fpsCounter is not correctly initialized";
		// Confirmed: Setting window title every frame does not have an impact on performance
		display.setTitle(appName + " (" + fpsCounter.getAveragedFps(timer) + ")");
	}

	private void createPassManager() {
		// render pass
		final RenderPass renderPass = new RenderPass();
		renderPass.add(rootNode);

		// volume shadow
		final ShadowedRenderPass shadowPass = new ShadowedRenderPass();
		shadowPass.add(rootNode);
		shadowPass.setRenderShadows(true);
		shadowPass.setLightingMethod(ShadowedRenderPass.LightingMethod.Additive);
		shadowPass.addOccluder(rootNode);
		shadowPass.setRenderVolume(false);
		//shadowPass.setShadowColor(ColorRGBA.black);

		// directional shadow map
		final DirectionalShadowMapPass directionalShadowMapPass = new DirectionalShadowMapPass(
				new Vector3f(100, -100, 100));
		directionalShadowMapPass.setViewDistance(1000);
		directionalShadowMapPass.add(rootNode);
		directionalShadowMapPass.setViewTarget(new Vector3f(100, -50, 100));
		directionalShadowMapPass.addOccluder(rootNode);

		// bloom effect
		final BloomRenderPass bloomPass = new BloomRenderPass(cam, 4);
		bloomPass.add(rootNode);
		bloomPass.setEnabled(true);
		bloomPass.setUseCurrentScene(true);
		bloomPass.setBlurIntensityMultiplier(1.7f);
		bloomPass.setBlurSize(0.001f);

		pManager.add(renderPass);
		//pManager.add(directionalShadowMapPass);
		pManager.add(shadowPass);
		// pManager.add(bloomPass);
	}

}
