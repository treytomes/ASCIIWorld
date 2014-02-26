package asciiWorld.animations;

import org.newdawn.slick.Graphics;

public interface IAnimation {

	boolean isAlive();
	void update(double deltaTime);
	void render(Graphics g);
}
