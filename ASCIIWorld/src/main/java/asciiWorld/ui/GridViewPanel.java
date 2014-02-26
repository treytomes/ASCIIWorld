package asciiWorld.ui;

import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;

public class GridViewPanel extends Panel {
	
	private float[] _rowDefinitions;
	private float[] _columnDefinitions;
	
	private FrameworkElement[][] _gridChildren;

	public GridViewPanel(Rectangle bounds, int rows, int columns) {
		super(bounds);

		_rowDefinitions = new float[rows];
		_columnDefinitions = new float[columns];
		
		float rowHeight = 1.0f / rows;
		for (int index = 0; index < rows; index++) {
			_rowDefinitions[index] = rowHeight;
		}
		
		float columnWidth = 1.0f / columns;
		for (int index = 0; index < columns; index++) {
			_columnDefinitions[index] = columnWidth;
		}
		
		_gridChildren = new FrameworkElement[rows][columns];
	}
	
	public GridViewPanel(int rows, int columns) {
		this(new Rectangle(0, 0, 0, 0), rows, columns);
	}
	
	public int getRowCount() {
		return _rowDefinitions.length;
	}
	
	public int getColumnCount() {
		return _columnDefinitions.length;
	}
	
	public float getRowHeight(int index) {
		return _rowDefinitions[index];
	}
	
	public void setRowHeight(int index, float value) {
		_rowDefinitions[index] = value;
		resetBounds();
	}
	
	public float getColumnWidth(int index) {
		return _columnDefinitions[index];
	}
	
	public void setColumnWidth(int index, float value) {
		_columnDefinitions[index] = value;
		resetBounds();
	}
	
	@Override
	public void addChild(FrameworkElement child) throws Exception {
		for (int row = 0; row < getRowCount(); row++) {
			for (int column = 0; column < getColumnCount(); column++) {
				if (_gridChildren[row][column] == null) {
					addChild(child, row, column);
					return;
				}
			}
		}
		
		addChild(child, 0, 0);
	}
	
	public void addChild(FrameworkElement child, int row, int column) throws Exception {
		super.addChild(child);
		
		removeChild(row, column);
		_gridChildren[row][column] = child;
		
		setChildBounds(child);
	}
	
	@Override
	public void removeChild(FrameworkElement child) throws Exception {
		for (int row = 0; row < getRowCount(); row++) {
			for (int column = 0; column < getColumnCount(); column++) {
				if (_gridChildren[row][column] == child) {
					removeChild(row, column);
					return;
				}
			}
		}
	}
	
	public void removeChild(int row, int column) throws Exception {
		if (_gridChildren[row][column] != null) {
			super.removeChild(_gridChildren[row][column]);
			_gridChildren[row][column] = null;
		}
	}

	@Override
	protected void setChildBounds(FrameworkElement child) {
		Rectangle childBounds = child.getBounds();
		float parentWidth = getBounds().getWidth();
		float parentHeight = getBounds().getHeight();
		
		float y = getBounds().getY();
		for (int row = 0; row < getRowCount(); row++) {
			float rowHeight = parentHeight * getRowHeight(row);
			
			float x = getBounds().getX();
			for (int column = 0; column < getColumnCount(); column++) {
				float columnWidth = parentWidth * getColumnWidth(column);
				
				if (_gridChildren[row][column] == child) {
					child.moveTo(new Vector2f(x, y));
					//childBounds.setX(x);
					//childBounds.setY(y);
					childBounds.setWidth(columnWidth);
					childBounds.setHeight(rowHeight);
					
					child.resetBounds();
					
					return;
				}
				
				x += columnWidth;
			}
			
			y += rowHeight;
		}
	}
}
