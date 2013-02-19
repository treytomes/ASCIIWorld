package asciiWorld.entities;

import asciiWorld.ui.RootVisualPanel;

public class CanSpeakComponent extends EntityComponent {
	
	private RootVisualPanel _uiRoot;
	private String _text;
	
	public CanSpeakComponent(Entity owner, RootVisualPanel uiRoot, String text) {
		super(owner);

		_uiRoot = uiRoot;
		_text = text;
	}
	
	public RootVisualPanel getUIRoot() {
		return _uiRoot;
	}
	
	public String getText() {
		return _text;
	}
	
	@Override
	public void touched(Entity touchedByEntity) {
		_uiRoot.showMessageBox(true, getText(), getOwner().toString());
	}
}
