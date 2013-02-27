package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.RoundedRectangle;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.ASCIIWorldGame;
import asciiWorld.Camera;
import asciiWorld.entities.Entity;

public class HUDView extends CanvasPanel {
	
	private static final float ZOOM_INCREMENT = 0.1f;
	
	//private static final String STATE_TITLE = "Gameplay State";
	private static final int MARGIN = 5;
	private static final int BUTTON_WIDTH = 106;
	private static final int BUTTON_HEIGHT = 42;
	
	private Camera _camera;
	private Entity _player;
	
	public HUDView(GameContainer container, StateBasedGame game) throws Exception {
		super(new Rectangle(0, 0, container.getWidth(), container.getHeight()));
		generateUI(container, game);
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
	
	private void generateUI(GameContainer container, StateBasedGame game) throws Exception {
		//addChild(createTitleLabel());
		addChild(createInventoryHotKeys());
		addChild(createMenuPanel(container, game));
		addChild(createZoomPanel());
		//addChild(createPlayerPositionLabel());
	}
	
	private StackPanel createInventoryHotKeys() throws Exception {
		int numPanels = 10;
		int panelSize = 32;
		StackPanel panel = new StackPanel(new Rectangle(0, 0, (panelSize + MARGIN * 2) * numPanels, panelSize + MARGIN * 2), Orientation.Horizontal);

		MethodBinding getPlayerBinding = new MethodBinding(this, "getPlayer");
		MethodBinding getInventoryBinding = new MethodBinding(getPlayerBinding, "getInventory");
		
		for (int index = 0; index < numPanels; index++) {
			Border itemPanel = new Border(new RoundedRectangle(0, 0, panelSize, panelSize, 8), new Color(0.2f, 0.0f, 1.0f, 0.5f), true);
			itemPanel.getMargin().setValue(MARGIN);

			MethodBinding getItemAtBinding = new MethodBinding(getInventoryBinding, "getItemAt", index);
			MethodBinding getTileBinding = new MethodBinding(getItemAtBinding, "getTile");
			itemPanel.setContent(new TileView(getTileBinding));
			panel.addChild(itemPanel);
		}
		
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