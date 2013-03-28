package asciiWorld.lighting;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Vector2f;

public class LightTestGame extends BasicGame {
	
	private List<ConvexHull> _hulls;
	private List<Light> _lights;
	private FrameBufferObject _framebuffer;
	private Image _bg;

	public LightTestGame() {
		super("Light Test");
		
		_hulls = new ArrayList<ConvexHull>();
		_hulls.add(new ConvexHull(new Vector2f(150, 200), new Polygon(new float[] { 0, 0, 50, -75, 100, -75, 150, 0, 100, 75, 50, 75 })));
		
		_lights = new ArrayList<Light>();
		_lights.add(new Light(new Vector2f(0, 300), 200.0f));
		
		for (int x = 0; x < 16; x++) {
			_hulls.add(new ConvexHull(new Vector2f(100 + x * 40, 500), new Polygon(new float[] { 0, 0, 20, 0, 20, 20, 0, 20 })));
		}
	}

	@Override
	public void init(GameContainer container) throws SlickException {
		try {
			_framebuffer = new FrameBufferObject(container.getWidth(), container.getHeight());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		_bg = new Image("resources/gfx/greenlg.png");
	}

	@Override
	public void update(GameContainer container, int delta)
			throws SlickException {
		_lights.get(0).setPosition(new Vector2f(Mouse.getX(), Mouse.getY()));
	}
	
	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		// TODO Auto-generated method stub
		super.mouseClicked(button, x, y, clickCount);
		
		switch (button) {
		case Input.MOUSE_LEFT_BUTTON:
			_lights.add(0, new Light(new Vector2f(x, y), 200.0f));
		}
	}

	@Override
	public void render(GameContainer container, Graphics g)
			throws SlickException {
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
	    GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT |GL11.GL_STENCIL_BUFFER_BIT);

	    // Use less-than or equal depth testing
	    GL11.glDepthFunc(GL11.GL_LEQUAL);
		
		//GL11.glPushAttrib(GL11.GL_ACCUM_BUFFER_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_CURRENT_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_ENABLE_BIT |
		//		GL11.GL_LIGHTING_BIT | GL11.GL_TEXTURE_BIT | GL11.GL_TRANSFORM_BIT);
		_framebuffer.enable();
		
		GL11.glClearDepth(1.1);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT | GL11.GL_STENCIL_BUFFER_BIT);
		
		_bg.draw(0, 0);
		
		for (ConvexHull hull : _hulls) {
			hull.render(g);
		}
		
		for (Light light : _lights) {
			// Clear the alpha channel of the framebuffer to 0.0
			GL11.glColorMask(false, false, false, true);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		    
			// Write new framebuffer alpha
			GL11.glDisable(GL11.GL_BLEND);
	        GL11.glEnable(GL11.GL_DEPTH_TEST);
	        GL11.glColorMask(false, false, false, true);
			light.render(g);
			
			GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ZERO);
	        // Draw shadow geometry
			for (ConvexHull hull : _hulls) {
				hull.drawShadowGeometry(light);
			}

	        // Draw geometry
			GL11.glEnable(GL11.GL_DEPTH_TEST);
	        GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_DST_ALPHA, GL11.GL_ONE);
	        GL11.glColorMask(true, true, true, false);
	        if (true) { // addlight:
	    		for (Light light2 : _lights) {
	    			light2.render(g);
	    		}
	        }
			for (ConvexHull hull : _hulls) {
	            hull.render(g);
			}
		}

		_framebuffer.disable();

		GL11.glDisable(GL11.GL_DEPTH_TEST);
	    GL11.glDisable(GL11.GL_BLEND);
	    //GL11.glPopAttrib();

		g.clear();
		

		// Render the fbo on top of the color buffer
	    _framebuffer.render();
	}
	
	public static void main(String[] args) {
		try {
			LightTestGame game = new LightTestGame();
			AppGameContainer app = new AppGameContainer(game);
			app.setDisplayMode(800, 600, false);
			app.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
}