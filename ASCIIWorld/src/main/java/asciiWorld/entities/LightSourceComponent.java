package asciiWorld.entities;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.chunks.Chunk;
import asciiWorld.lighting.TargetedLight;
import asciiWorld.math.RandomFactory;

public class LightSourceComponent extends EntityComponent {

	private static final float DEFAULT_RADIUS = 100.0f;
	
	private int _totalMS;
	private boolean _flicker;
	private float _radius;
	private TargetedLight _light;
	
	public LightSourceComponent(Entity owner) {
		super(owner);
		_totalMS = 0;
		_flicker = false;
		_radius = DEFAULT_RADIUS;
		_light = new TargetedLight(Color.white, _radius, owner);
	}
	
	public Color getColor() {
		return _light.getColor();
	}

	public void setColor(Color value) {
		_light.setColor(value);
	}
	
	public Float getRadius() {
		return _radius;
	}
	
	public void setRange(Float value) {
		_radius = value;
		_light.setRadius(value);
	}
	
	public Boolean getFlicker() {
		return _flicker;
	}
	
	public void setFlicker(Boolean value) {
		_flicker = value;
	}
	
	@Override
	public void afterAddedToChunk(Chunk chunk) {
		chunk.getLights().add(_light);
	}
	
	@Override
	public void beforeRemovedFromChunk(Chunk chunk) {
		chunk.getLights().remove(_light);
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		_totalMS += deltaTime;
		if (_flicker) {
			_light.setRadius(_radius + (float)Math.sin(_totalMS / (RandomFactory.get().nextDouble() * 300.0)) * (float)RandomFactory.get().nextDouble() * _radius * 0.1f);
		}
	}
}
