package asciiWorld.entities;

import org.newdawn.slick.Color;

import asciiWorld.tiles.Frame;
import asciiWorld.tiles.IRenderable;

public class VillagerComponent extends EntityComponent {

	private static final Color COLOR_MALE = new Color(0.4f, 0.6f, 1.0f);
	private static final Color COLOR_FEMALE = Color.pink;
	
	private CanSpeakComponent _speaker;
	
	public VillagerComponent(Entity owner) {
		super(owner);
		
		_speaker = new CanSpeakComponent(owner);
		_speaker.setText("Hello, my name is _____!");
		getOwner().getComponents().add(_speaker);
		
		getOwner().setGender(Gender.random());
		assignGenderColor();
	}

	private void assignGenderColor() {
		IRenderable renderable = getOwner().getTile().getCurrentFrame();
		if (renderable instanceof Frame) {
			Frame frame = Frame.class.cast(renderable);
			frame.setForegroundColor(getGenderColor());
		}
	}
	
	private Color getGenderColor() {
		return (getOwner().getGender() == Gender.Male) ? COLOR_MALE : COLOR_FEMALE;
	}
}
