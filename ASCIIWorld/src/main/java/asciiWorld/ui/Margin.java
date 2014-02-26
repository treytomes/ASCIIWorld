package asciiWorld.ui;

public class Margin {
	
	private int _topMargin;
	private int _bottomMargin;
	private int _leftMargin;
	private int _rightMargin;
	
	public Margin(int topMargin, int bottomMargin, int leftMargin, int rightMargin) {
		_topMargin = topMargin;
		_bottomMargin = bottomMargin;
		_leftMargin = leftMargin;
		_rightMargin = rightMargin;
	}
	
	public Margin(int value) {
		this(value, value, value, value);
	}
	
	public int getTopMargin() {
		return _topMargin;
	}
	
	public void setTopMargin(int value) {
		_topMargin = value;
	}
	
	public int getBottomMargin() {
		return _bottomMargin;
	}
	
	public void setBottomMargin(int value) {
		_bottomMargin = value;
	}
	
	public int getLeftMargin() {
		return _leftMargin;
	}
	
	public void setLeftMargin(int value) {
		_leftMargin = value;
	}
	
	public int getRightMargin() {
		return _rightMargin;
	}
	
	public void setRightMargin(int value) {
		_rightMargin = value;
	}

	public void setValue(int value) {
		setTopMargin(value);
		setBottomMargin(value);
		setLeftMargin(value);
		setRightMargin(value);
	}
}