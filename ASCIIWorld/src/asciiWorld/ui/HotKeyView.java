package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.geom.Vector2f;

import asciiWorld.CreateColor;
import asciiWorld.FontFactory;
import asciiWorld.entities.HotKeyInfo;

public class HotKeyView extends Border {
	
	private static final Color COLOR_BACKGROUND = new Color(0.2f, 0.0f, 1.0f, 0.5f);
	private static final Color COLOR_HOVER = new Color(0.8f, 0.0f, 1.0f, 0.5f);
	private static final Color COLOR_SELECTED = new Color(1.0f, 0.4f, 0.0f, 0.0f);
	private static final int CORNER_RADIUS = 8;
	private static final int MARGIN = 5;
	
	private HotKeyInfo _info;
	private TileView _tileView;
	private Border _selectionBorder;

	public HotKeyView(HotKeyInfo info) throws Exception {
		super(new RoundedRectangle(0, 0, 0, 0, CORNER_RADIUS), COLOR_BACKGROUND, true);
		getMargin().setValue(MARGIN);
		
		_info = info;
		
		_selectionBorder = new Border(new RoundedRectangle(0, 0, 0, 0, CORNER_RADIUS), CreateColor.from(COLOR_SELECTED).getColor(), false) {
			@Override
			public void update(GameContainer container, int delta) {
				// Don't do anything.
			}
		};
		setContent(_selectionBorder);
		
		GridViewPanel grid = new GridViewPanel(4, 4) {
			@Override
			public void update(GameContainer container, int delta) {
				// Don't do anything.
			}
		};
		_selectionBorder.setContent(grid);
		
		_tileView = new TileView(getTileBinding()) {
			@Override
			public void update(GameContainer container, int delta) {
				// Don't do anything.
			}
		};
		grid.addChild(_tileView, 0, 0);
		
		Label label = new Label(FontFactory.get().getResource(10), info.getKeyboardKeyName(), Color.white) {
			@Override
			public void update(GameContainer container, int delta) {
				// Don't do anything.
			}
		};
		grid.addChild(label, 3, 3);
		
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
	
	public HotKeyInfo getHotKeyInfo() {
		return _info;
	}
	
	public void setHotKeyInfo(HotKeyInfo value) {
		_info = value;
	}
	
	private MethodBinding getTileBinding() {
		MethodBinding getHotKeyInfoBinding = new MethodBinding(this, "getHotKeyInfo");
		MethodBinding getItemBinding = new MethodBinding(getHotKeyInfoBinding, "getItem");
		
		//MethodBinding getInventoryIndex = new MethodBinding(this, "getInventoryIndex");
		//MethodBinding getEntity = new MethodBinding(this, "getEntity");
		//MethodBinding getInventoryBinding = new MethodBinding(getEntity, "getInventory");
		//MethodBinding getItemBinding = new MethodBinding(getInventoryBinding, "getItemAt", getInventoryIndex);
		
		MethodBinding getTileBinding = new MethodBinding(getItemBinding, "getTile");
		
		return getTileBinding;
	}

	@Override
	public void update(GameContainer container, int delta) {
		try {
			if (getHotKeyInfo().isActive()) {
				_selectionBorder.getColor().a = 0.5f;
			} else {
				_selectionBorder.getColor().a = 0.0f;
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to update the selection box.");
		}
		
		super.update(container, delta);
	}
}