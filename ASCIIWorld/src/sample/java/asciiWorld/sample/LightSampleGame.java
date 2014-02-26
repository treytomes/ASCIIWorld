package asciiWorld.sample;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.lighting.ConvexHull;
import asciiWorld.lighting.FrameBufferObject;
import asciiWorld.lighting.IConvexHull;
import asciiWorld.lighting.Light;
import asciiWorld.math.RandomFactory;

public class LightSampleGame extends BasicGame {
	
	private static final boolean USE_FRAMEBUFFER = false;
	private static final Color AMBIENT_LIGHT_COLOR = new Color(0, 0, 0, 0.6f);
	
	private List<IConvexHull> _hulls;
	private List<Light> _lights;
	private FrameBufferObject _framebuffer;
	private Image _bg;
	
	private Color _ambientLightColor;

	public LightSampleGame() {
		super("Light Test");
		
		_hulls = new ArrayList<IConvexHull>();
		_hulls.add(new ConvexHull(new Vector2f(150, 200), new Polygon(new float[] { 0, 0, 50, -75, 100, -75, 150, 0, 100, 75, 50, 75 })));
		
		_lights = new ArrayList<Light>();
		_lights.add(new Light(new Vector2f(0, 300), (float)RandomFactory.get().nextDouble() * 200.0f));
		
		for (int x = 0; x < 16; x++) {
			_hulls.add(new ConvexHull(new Vector2f(100 + x * 40, 500), new Polygon(new float[] { 0, 0, 20, 0, 20, 20, 0, 20 })));
		}
		
		_ambientLightColor = AMBIENT_LIGHT_COLOR;
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		try {
			_framebuffer = new FrameBufferObject(container.getWidth(), container.getHeight());
		} catch (Exception e) {
			e.printStackTrace();
		}

	    GL11.glDepthFunc(GL11.GL_LEQUAL); // use less-than or equal depth testing
		
		_bg = new Image("resources/gfx/greenlg.png");
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
	}
	
	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		super.mouseMoved(oldx, oldy, newx, newy);
		_lights.get(0).setPosition(new Vector2f(newx, newy));
	}
	
	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		super.mouseClicked(button, x, y, clickCount);
		
		switch (button) {
		case Input.MOUSE_LEFT_BUTTON:
			_lights.add(0, new Light(new Vector2f(x, y), (float)RandomFactory.get().nextDouble() * 150.0f + 50.0f));
		}
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
	    // TODO: This seems to work without the framebuffer being enabled.  Why?
		if (USE_FRAMEBUFFER) {
			_framebuffer.enable();
		}
		
		GL11.glClearDepth(1.1);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		
		_bg.draw(100, 100);
		
		for (IConvexHull hull : _hulls) {
			hull.render(g);
		}
		
		g.setColor(_ambientLightColor); // ambient light
		g.fillRect(0, 0, container.getWidth(), container.getHeight());
		
		for (Light light : _lights) {
			// Clear the alpha channel of the framebuffer to 0.0.
			GL11.glColorMask(false, false, false, true);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		    
			// Write new framebuffer alpha.
			GL11.glDisable(GL11.GL_BLEND);
	        GL11.glEnable(GL11.GL_DEPTH_TEST);
	        GL11.glColorMask(false, false, false, true);
			light.render(g);
			
			GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ZERO);
	        // Draw shadow geometry.
			for (IConvexHull hull : _hulls) {
				hull.drawShadowGeometry(light);
			}

	        // Draw geometry.
			GL11.glEnable(GL11.GL_DEPTH_TEST);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE);
	        GL11.glColorMask(true, true, true, false);
	        //if (true) { // addlight
	    		for (Light light2 : _lights) {
	    			light2.render(g);
	    		}
	        //}
	        
	        // This part doesn't seem to be necessary.
			/*for (ConvexHull hull : _hulls) {
	            hull.render(g);
			}*/
		}
		
		if (USE_FRAMEBUFFER) {
			_framebuffer.disable();
		}

		if (USE_FRAMEBUFFER) {
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_BLEND);
			_framebuffer.render(g);
		}
		
		// Reset OpenGL settings for Slick.
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	    GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void main(String[] args) {
		try {
			LightSampleGame game = new LightSampleGame();
			AppGameContainer app = new AppGameContainer(game);
			app.setDisplayMode(800, 600, false);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}