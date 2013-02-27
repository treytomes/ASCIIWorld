package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.entities.Entity;

public class HotKeyView extends Border {
	
	private static final Color COLOR_BACKGROUND = new Color(0.2f, 0.0f, 1.0f, 0.5f);
	private static final Color COLOR_HOVER = new Color(0.8f, 0.0f, 1.0f, 0.5f);
	private static final int CORNER_RADIUS = 8;
	private static final int MARGIN = 5;
	
	private Object _entityBinding;
	private int _inventoryIndex;
	private TileView _tileView;

	public HotKeyView(Object entityBinding, int inventoryIndex) throws Exception {
		super(new RoundedRectangle(0, 0, 0, 0, CORNER_RADIUS), COLOR_BACKGROUND, true);
		getMargin().setValue(MARGIN);
		
		_tileView = new TileView(null) {
			public void update(GameContainer container, int delta)
			{
				// Don't do anything.
			}
		};
		setContent(_tileView);
		
		setEntityBinding(entityBinding);
		setInventoryIndex(inventoryIndex);
		
		addMouseOverListener(new MousePositionEvent() {
			@Override
			public void mousePositionChanged(FrameworkElement sender, Vector2f mousePosition) {
				setColor(COLOR_HOVER);	
			}
		});
		
		addMouseOutListener(new MousePositionEvent() {
			@Override
			public void mousePositionChanged(FrameworkElement sender, Vector2f mousePosition) {
				setColor(COLOR_BACKGROUND);
			}
		});
	}
	
	public Object getEntityBinding() {
		return _entityBinding;
	}
	
	public void setEntityBinding(Object value) {
		_entityBinding = value;
		resetBinding();
	}
	
	public Entity getEntity() throws Exception {
		Object binding = getEntityBinding();
		if (binding instanceof Entity) {
			return (Entity)binding;
		} else if (binding instanceof MethodBinding) {
			return (Entity)((MethodBinding)binding).getValue();
		} else {
			throw new Exception("Invalid binding.");
		}
	}
	
	public void setEntity(Entity value) {
		setEntityBinding(value);
	}
	
	public int getInventoryIndex() {
		return _inventoryIndex;
	}
	
	public void setInventoryIndex(int value) {
		_inventoryIndex = value;
		resetBinding();
	}
	
	private void resetBinding() {
		MethodBinding getInventoryBinding = new MethodBinding(_entityBinding, "getInventory");
		MethodBinding getItemAtBinding = new MethodBinding(getInventoryBinding, "getItemAt", getInventoryIndex());
		MethodBinding getTileBinding = new MethodBinding(getItemAtBinding, "getTile");
		
		_tileView.setTile(getTileBinding);
	}
}