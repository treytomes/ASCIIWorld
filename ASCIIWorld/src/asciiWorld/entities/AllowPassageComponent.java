package asciiWorld.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.chunks.Chunk;
import asciiWorld.tiles.Frame;
import asciiWorld.tiles.IRenderable;

public class AllowPassageComponent extends EntityComponent {

	private static final int DEFAULT_PASSAGE_TIME = 3000;
	
	private boolean _open;
	private int _passageTime;
	private int _timeOpen;
	
	public AllowPassageComponent(Entity owner) {
		super(owner);
		_open = false;
		_passageTime = DEFAULT_PASSAGE_TIME;
		_timeOpen = 0;
	}
	
	public Integer getPassageTime() {
		return _passageTime;
	}
	
	public void setPassageTime(Integer value) {
		_passageTime = value;
	}
	
	@Override
	public void touched(Entity touchedByEntity) {
		_open = true;
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		if (_open) {
			ensureOpenedState();

			_timeOpen += deltaTime;
			if (_timeOpen >= _passageTime) {
				_open = false;
				_timeOpen = 0;
			}
		} else {
			ensureClosedState();
		}
	}

	private void ensureClosedState() {
		Entity owner = getOwner();
		Vector2f chunkPoint = owner.getOccupiedChunkPoint();
		if ((owner.getLayer() == Chunk.LAYER_SKY) && !owner.getChunk().isSpaceOccupied(chunkPoint, Chunk.LAYER_OBJECT)) {
			owner.moveTo(chunkPoint, Chunk.LAYER_OBJECT);
			owner.setIsTranslucent(false);
			owner.getChunk().cacheEntitiesInRange();
			setAlpha(1.0f);
		}
	}

	private void ensureOpenedState() {
		Entity owner = getOwner();
		if (owner.getLayer() == Chunk.LAYER_OBJECT) {
			owner.moveTo(owner.getOccupiedChunkPoint(), Chunk.LAYER_SKY);
			owner.setIsTranslucent(true);
			owner.getChunk().cacheEntitiesInRange();
			setAlpha(0.5f);
		}
	}
	
	private void setAlpha(float alpha) {
		IRenderable renderable = getOwner().getTile().getCurrentFrame();
		if (renderable instanceof Frame) {
			Frame frame = Frame.class.cast(renderable);
			frame.getBackgroundColor().a = alpha;
			frame.getForegroundColor().a = alpha;
		}
	}
}
