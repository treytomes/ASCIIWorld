package asciiWorld.ui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.ASCIIWorldGame;
import asciiWorld.Camera;
import asciiWorld.World;
import asciiWorld.entities.EntityFactory;
import asciiWorld.entities.HotKeyInfo;
import asciiWorld.entities.HotKeyManager;
import asciiWorld.entities.PlayerControlComponent;

public class HUDView extends CanvasPanel {
	
	private static final float ZOOM_INCREMENT = 0.1f;
	
	//private static final String STATE_TITLE = "Gameplay State";
	private static final int MARGIN = 5;
	private static final int BUTTON_WIDTH = 106;
	private static final int BUTTON_HEIGHT = 42;
	
	private Camera _camera;
	private World _world;
	private ImmediateWindow _scriptConsole;
	
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
	
	public World getWorld() {
		return _world;
	}
	
	public void setWorld(World value) {
		_world = value;
		_scriptConsole.getContext().addObjectToContext(_world, "world");
		_scriptConsole.getContext().addObjectToContext(_world.getPlayer(), "player");
		assignHUDToPlayerControlComponent();
	}
	
	@Override
	public void setParent(FrameworkElement parent) throws Exception {
		super.setParent(parent);
		assignHUDToPlayerControlComponent();
	}
	
	@Override
	public void update(GameContainer container, int delta) {
		super.update(container, delta);
	}
	
	public void showConsole() {
		if (!isConsoleOpen()) {
			try {
				addChild(_scriptConsole);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void hideConsole() {
		if (isConsoleOpen()) {
			try {
				removeChild(_scriptConsole);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isConsoleOpen() {
		return _scriptConsole.getParent() == this;
	}
	
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
	
	private void assignHUDToPlayerControlComponent() {
		if (getWorld().getPlayer() != null) {
			PlayerControlComponent playerControl = getWorld().getPlayer().findComponent(PlayerControlComponent.class);
			if (playerControl != null) {
				playerControl.setHUD(this);
			}
		}
	}
	
	private void generateUI(GameContainer container, StateBasedGame game, HotKeyManager hotkeys) throws Exception {
		//addChild(createTitleLabel());
		HotKeyPanel hotKeyPanel = createInventoryHotKeys(hotkeys);
		addChild(hotKeyPanel);
		
		final MethodBinding worldTime = new MethodBinding(new MethodBinding(this, "getWorld"), "getWorldTime");
		addChild(new StackPanel(new Rectangle(hotKeyPanel.getBounds().getMaxX(), 0, 400, 32)) {{
			addChild(new Label(worldTime, Color.white) {{
				setHorizontalContentAlignment(HorizontalAlignment.Left);
				setTextWrappingMode(TextWrappingMode.NoWrap);
			}});
		}});
		
		addChild(new HealthMeter(new Rectangle(hotKeyPanel.getBounds().getMaxX(), 32, 400, 32), new MethodBinding(new MethodBinding(this, "getWorld"), "getPlayer")));

		addChild(createMenuPanel(container, game));
		addChild(createZoomPanel());
		//addChild(createPlayerPositionLabel());

		_scriptConsole = new ImmediateWindow(container, new Rectangle(getBounds().getMinX(), getBounds().getMaxY() - 200, getBounds().getWidth(), 200));
		_scriptConsole.hideButtonBar();
		_scriptConsole.getContext().executeScript("Direction = Packages.asciiWorld.Direction");
		_scriptConsole.getContext().executeScript("Chunk = Packages.asciiWorld.chunks.Chunk");
		_scriptConsole.getContext().addObjectToContext(EntityFactory.get(), "EntityFactory");
		_scriptConsole.getContext().executeScript("function getSpawnPoint() { return player.getChunk().findRandomSpawnPoint(Chunk.LAYER_OBJECT); }");
		_scriptConsole.getContext().executeScript("function spawn(resource) { e = EntityFactory.getResource(resource); e.moveTo(getSpawnPoint()); player.getChunk().addEntity(e); }");
		// Hide the script console by default.  It is opened by the PlayerControlComponent.
		//addChild(_scriptConsole);
	}
	
	private HotKeyPanel createInventoryHotKeys(HotKeyManager hotkeys) throws Exception {
		HotKeyPanel panel = new HotKeyPanel(hotkeys);
		
		panel.addItemSelectedListener(new HotKeySelectedEvent() {
			@Override
			public void selected(HotKeyPanel sender, HotKeyInfo info) {
				try {
					getWorld().getPlayer().setActiveItem(info.getItem());
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
		int numItems = 3;

		StackPanel zoomButtonPanel = new StackPanel(new Rectangle(getBounds().getWidth() - BUTTON_WIDTH * numItems - MARGIN, getBounds().getHeight() - BUTTON_HEIGHT - MARGIN, BUTTON_WIDTH * numItems, BUTTON_HEIGHT));
		zoomButtonPanel.addChild(Button.createActionButton("Zoom +", new MethodBinding(this, "zoomIn")));
		zoomButtonPanel.addChild(new Label(new MethodBinding(new MethodBinding(this, "getCamera"), "getScale"), Color.white));
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
}