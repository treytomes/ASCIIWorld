package asciiWorld.ui;

import org.newdawn.slick.geom.Vector2f;

public interface MouseButtonEvent {
	void mouseButtonChanged(FrameworkElement sender, MouseButton button, Vector2f mousePosition);
}
