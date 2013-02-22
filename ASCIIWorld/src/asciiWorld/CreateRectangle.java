package asciiWorld;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;

public class CreateRectangle {
	
	private RoundedRectangle _rectangle;
	
	public static CreateRectangle from(Rectangle baseRectangle) {
		CreateRectangle creator = new CreateRectangle();
		creator.setRectangle(baseRectangle);
		return creator;
	}
	
	public static CreateRectangle withSize(float width, float height) {
		CreateRectangle creator = new CreateRectangle();
		creator.setRectangle(new RoundedRectangle(0, 0, width, height, 0));
		return creator;
	}
	
	private CreateRectangle() {
		_rectangle = null;
	}
	
	public RoundedRectangle getRectangle() {
		return _rectangle;
	}
	
	public CreateRectangle setRectangle(Rectangle value) {
		_rectangle = new RoundedRectangle(value.getX(), value.getY(), value.getWidth(), value.getHeight(), 0);
		return this;
	}
	
	public CreateRectangle setRectangle(RoundedRectangle value) {
		_rectangle = new RoundedRectangle(value.getX(), value.getY(), value.getWidth(), value.getHeight(), value.getCornerRadius());
		return this;
	}
	
	public CreateRectangle setCornerRadius(float value) {
		_rectangle.setCornerRadius(value);
		return this;
	}
	
	public CreateRectangle scale(float horizontal, float vertical) {
		_rectangle.setWidth(_rectangle.getWidth() * horizontal);
		_rectangle.setHeight(_rectangle.getHeight() * vertical);
		return this;
	}
	
	public CreateRectangle scale(float amount) {
		return scale(amount, amount);
	}
	
	public CreateRectangle centerOn(Rectangle r) {
		_rectangle.setX((r.getWidth() - _rectangle.getWidth()) / 2.0f);
		_rectangle.setY((r.getHeight() - _rectangle.getHeight()) / 2.0f);
		return this;
	}
}
