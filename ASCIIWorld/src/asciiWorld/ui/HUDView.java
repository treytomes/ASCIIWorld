package asciiWorld.ui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.ASCIIWorldGame;
import asciiWorld.Camera;
import asciiWorld.entities.Entity;
import asciiWorld.entities.HotKeyInfo;
import asciiWorld.entities.HotKeyManager;

public class HUDView extends CanvasPanel {
	
	private static final float ZOOM_INCREMENT = 0.1f;
	
	//private static final String STATE_TITLE = "Gameplay State";
	private static final int MARGIN = 5;
	private static final int BUTTON_WIDTH = 106;
	private static final int BUTTON_HEIGHT = 42;
	
	private Camera _camera;
	private Entity _player;
	
	public HUDView(GameContainer container, StateBasedGame game, HotKeyManager hotkeys) throws Exception {
		super(new Rectangle(0, 0, container.getWidth(), container.getHeight()));
		generateUI(container, game, hotkeys);
	}
	
	public Camera getCamera() {
		return _camera;
	}
	
	public void setCamera(Camera value) {
		_camera = value;
	}
	
	public Entity getPlayer() {
		return _player;
	}
	
	public void setPlayer(Entity value) {
		_player = value;
	}
	
	private void generateUI(GameContainer container, StateBasedGame game, HotKeyManager hotkeys) throws Exception {
		//addChild(createTitleLabel());
		addChild(createInventoryHotKeys(hotkeys));
		addChild(createMenuPanel(container, game));
		addChild(createZoomPanel());
		//addChild(createPlayerPositionLabel());
	}
	
	private HotKeyPanel createInventoryHotKeys(HotKeyManager hotkeys) throws Exception {
		HotKeyPanel panel = new HotKeyPanel(hotkeys);
		
		panel.addItemSelectedListener(new HotKeySelectedEvent() {
			@Override
			public void selected(HotKeyPanel sender, HotKeyInfo info) {
				try {
					getPlayer().setActiveItem(info.getItem());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		return panel;
	}
	
	/*private Label createTitleLabel() throws SlickException {
		return new Label(new Vector2f(MARGIN * 2, MARGIN * 2), STATE_TITLE, Color.red);
	}*/
	
	private StackPanel createMenuPanel(GameContainer container, StateBasedGame game) throws Exception {
		int numMenuButtons = 2;
		int panelWidth = (BUTTON_WIDTH - MARGIN) * numMenuButtons;
		
		StackPanel menuButtonPanel = new StackPanel(new Rectangle(getBounds().getWidth() - panelWidth - MARGIN, MARGIN, panelWidth, BUTTON_HEIGHT * numMenuButtons), Orientation.Vertical);
		menuButtonPanel.addChild(Button.createStateTransitionButton("Main Menu", game, ASCIIWorldGame.STATE_MAINMENU));
		menuButtonPanel.addChild(Button.createActionButton("Exit", new MethodBinding(container, "exit")));

		return menuButtonPanel;
	}
	
	private StackPanel createZoomPanel() throws Exception {
		int numZoomButtons = 2;

		StackPanel zoomButtonPanel = new StackPanel(new Rectangle(getBounds().getWidth() - BUTTON_WIDTH * numZoomButtons - MARGIN, getBounds().getHeight() - BUTTON_HEIGHT - MARGIN, BUTTON_WIDTH * numZoomButtons, BUTTON_HEIGHT));
		zoomButtonPanel.addChild(Button.createActionButton("Zoom +", new MethodBinding(this, "zoomIn")));
		zoomButtonPanel.addChild(Button.createActionButton("Zoom -", new MethodBinding(this, "zoomOut")));
		
		return zoomButtonPanel;
	}
	
	/*private Label createPlayerPositionLabel() throws SlickException {
		MethodBinding getPlayerPositionBinding = new MethodBinding(new MethodBinding(this, "getPlayer"), "getPosition");
		Label playerPositionLabel = new Label(new Vector2f(10, 30), getPlayerPositionBinding, Color.blue);
		playerPositionLabel.setHorizontalContentAlignment(HorizontalAlignment.Left);
		playerPositionLabel.getBounds().setWidth(600);
		return playerPositionLabel;
	}*/
	
	public void zoomIn() {
		if (getCamera() != null) {
			getCamera().setScale(getCamera().getScale() + ZOOM_INCREMENT);
		}
	}
	
	public void zoomOut() {
		if (getCamera() != null) {
			getCamera().setScale(getCamera().getScale() - ZOOM_INCREMENT);
		}
	}
}