package asciiWorld.entities;

import asciiWorld.ui.RootVisualPanel;

public class CanSpeakComponent extends EntityComponent {
	
	private String _text;
	
	public CanSpeakComponent(Entity owner) {
		super(owner);
	}
	
	public String getText() {
		return _text;
	}
	
	public void setText(String value) {
		_text = value;
	}
	
	@Override
	public void touched(Entity touchedByEntity) {
		try {
			RootVisualPanel.get().showMessageBox(true, getText(), getOwner().toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
