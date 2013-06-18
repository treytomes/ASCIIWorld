package asciiWorld;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.chunks.Chunk;
import asciiWorld.entities.Entity;
import asciiWorld.entities.EntityCamera;

public class World {
	
	private DateTime _worldTime;
	private Entity _player;

	public World() {
		_worldTime = new DateTime(12, 0, 0);
	}
	
	public DateTime getWorldTime() {
		return _worldTime;
	}
	
	public Entity getPlayer() {
		return _player;
	}
	
	public void setPlayer(Entity value) {
		_player = value;
		_player.getChunk().setWorld(this);
	}
	
	public Chunk getChunk() {
		return _player.getChunk();
	}
	
	public void leave() {
		// Destroy the chunk.
		_player.getChunk().clearEntities();
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta) {
		_worldTime.update(delta / 14);
		_player.getChunk().update(container, game, delta);
	}
	
	public void render(Graphics g, EntityCamera camera) {
		_player.getChunk().render(g, camera);
	}
}
