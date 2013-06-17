package asciiWorld.lighting;

import org.newdawn.slick.Graphics;

public interface IConvexHull {
	void drawShadowGeometry(Light light);
	void render(Graphics g);
}
