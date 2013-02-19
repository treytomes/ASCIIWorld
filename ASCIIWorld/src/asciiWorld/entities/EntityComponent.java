package asciiWorld.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.Chunk;

public class EntityComponent {
	
	private Entity _owner;
	
	public EntityComponent(Entity owner) {
		_owner = owner;
	}
	
	public Entity getOwner() {
		return _owner;
	}
	
	public void beforeAddedToChunk(Chunk chunk) { }
	
	public void afterAddedToChunk(Chunk chunk) { }
	
	public void beforeRemovedFromChunk(Chunk chunk) { }
	
	public void afterRemovedFromChunk(Chunk chunk) { }
	
	public void update(GameContainer container, StateBasedGame game, int deltaTime) { }
	
	public void use(Entity source, Vector2f targetChunkPoint) { }
	
	public void touched(Entity touchedByEntity) { }
	
	public void collided(Entity collidedWithEntity) { }
}