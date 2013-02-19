package asciiWorld.tiles;

public class StaticText {
	
	private String _text;
	
	public StaticText(String text) {
		_text = text;
	}
	
	public StaticText() {
		this("");
	}
	
	public String getText() {
		return _text;
	}
	
	public void setText(String value) {
		_text = value;
	}
	
	@Override
	public String toString() {
		return getText();
	}
}