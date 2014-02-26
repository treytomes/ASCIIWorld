package asciiWorld.ui;

import org.newdawn.slick.Input;

public enum MouseButton {
	Left (Input.MOUSE_LEFT_BUTTON),
	Middle (Input.MOUSE_MIDDLE_BUTTON),
	Right (Input.MOUSE_RIGHT_BUTTON);
	
	private static final MouseButton[] _values = MouseButton.values();
	private final int _index;
	
	MouseButton(int index) {
		_index = index;
	}
	
	public int index() {
		return _index;
	}
	
	public static MouseButton fromInteger(int index) {
		return _values[index];
	}
}
