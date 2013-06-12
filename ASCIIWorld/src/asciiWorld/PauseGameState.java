package asciiWorld;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.stateManager.GameState;

public class PauseGameState extends GameState {
	
	private static final Color COLOR_BACKGROUND = new Color(0, 0, 0, 0.5f);
	
	private UnicodeFont _font;
	
	public PauseGameState() {
		try {
			_font = FontFactory.get().getDefaultFont();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int delta) {
		if (container.hasFocus()) {
			try {
				getManager().leave(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void render(GameContainer container, StateBasedGame game, Graphics g) {
		g.setColor(COLOR_BACKGROUND);
		g.fillRect(0, 0, container.getWidth(), container.getHeight());
		
		String text = "Paused";
		float x = (container.getWidth() - _font.getWidth(text)) / 2.0f;
		float y = (container.getHeight() - _font.getHeight(text)) / 2.0f;
		_font.drawString(x, y, text);
		
		g.flush();
	}
}
