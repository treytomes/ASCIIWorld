package asciiWorld;

import org.newdawn.slick.Color;

public class CreateColor {
	
	private Color _color;
	
	public static CreateColor from(Color baseColor) {
		CreateColor creator = new CreateColor();
		creator.setColor(baseColor);
		return creator;
	}
	
	private CreateColor() {
		_color = null;
	}
	
	public Color getColor() {
		return _color;
	}
	
	public CreateColor setColor(Color value) {
		_color = new Color(value);
		return this;
	}
	
	public CreateColor changeAlphaTo(float value) {
		getColor().a = value;
		return this;
	}
}
