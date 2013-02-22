package asciiWorld.ui;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Rectangle;

/**
 * ListView's are always vertical, and don't change the height of their child elements.
 * 
 * @author ttomes
 *
 */
public class ListView extends StackPanel {

	private static final Color COLOR_ROW_TEXT = Color.yellow;
	private static final Color COLOR_BACKGROUND_DEFAULT = Color.gray;
	private static final Color COLOR_BACKGROUND_SELECTED = Color.blue;
	
	private List<ListViewItemSelectedEvent> _itemSelectedListeners;

	private Iterable<?> _itemsSource;
	private Object _selectedItem;
	private Button _selectedButton;

	public ListView() {
		super(Orientation.Vertical);
		
		_itemSelectedListeners = new ArrayList<ListViewItemSelectedEvent>();
		
		_itemsSource = null;
		_selectedItem = null;
		_selectedButton = null;
	}
	
	public void addItemSelectedListener(ListViewItemSelectedEvent listener) {
		_itemSelectedListeners.add(listener);
	}
	
	public void removeItemSelectedListener(ListViewItemSelectedEvent listener) {
		_itemSelectedListeners.remove(listener);
	}
	
	public Iterable<?> getItemsSource() {
		return _itemsSource;
	}
	
	public Object getSelectedItem() {
		return _selectedItem;
	}
	
	public void setItemsSource(Iterable<?> value) {
		_itemsSource = value;
		populateChildren();
	}
	
	@Override
	protected void setVerticalOrientationBounds() {
		float myWidth = getBounds().getWidth() - 2;
		
		float x = getBounds().getX() + 1;
		float y = getBounds().getY() + 1;
		for (FrameworkElement child : getChildren()) {
			child.getBounds().setX(x + child.getMargin().getLeftMargin());
			child.getBounds().setY(y + child.getMargin().getTopMargin());
			child.getBounds().setWidth(myWidth - child.getMargin().getLeftMargin() - child.getMargin().getRightMargin());
			
			y += child.getBounds().getHeight() + 1;
		}
	}
	
	private void populateChildren() {
		clearChildren();
		
		for (Object item : getItemsSource()) {
			try {
				addChild(createItemButton(item));
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Unable to add an item to the ListView.");
			}
		}
	}
	
	private void clearChildren() {
		List<FrameworkElement> children = getChildren();
		for (int index = 0; index < children.size(); index++) {
			try {
				removeChild(children.get(index));
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Unable to remove a child from the ListView.");
			}
		}
	}
	
	private Button createItemButton(final Object item) throws Exception {
		Button btn;
		btn = new Button(item, new Rectangle(0, 0, 0, 30));
		btn.setCornerRadius(0);
		btn.setForegroundColor(COLOR_ROW_TEXT);
		btn.setBackgroundColor(COLOR_BACKGROUND_DEFAULT);
		btn.setHorizontalContentAlignment(HorizontalAlignment.Left);
		btn.addClickListener(new ButtonClickedEvent() {
			@Override
			public void click(Button button) {
				if (_selectedItem == item) {
					_selectedItem = null;
					button.setBackgroundColor(COLOR_BACKGROUND_DEFAULT);
				} else {
					if (_selectedButton != null) {
						_selectedButton.setBackgroundColor(COLOR_BACKGROUND_DEFAULT);
					}
					_selectedItem = item;
					_selectedButton = button;
					button.setBackgroundColor(COLOR_BACKGROUND_SELECTED);
					handleItemSelected();
				}
			}
		});
		return btn;
	}
	
	private void handleItemSelected() {
		for (ListViewItemSelectedEvent l : _itemSelectedListeners) {
			l.itemSelected(this, getSelectedItem());
		}		
	}
}
