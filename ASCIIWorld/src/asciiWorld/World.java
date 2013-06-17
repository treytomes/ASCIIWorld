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
	private Chunk _chunk;

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
	}
	
	public Chunk getChunk() {
		return _chunk;
	}
	
	public void setChunk(Chunk value) {
		_chunk = value;
		_chunk.updateAmbientLighting(_worldTime);
	}
	
	public void leave() {
		// Destroy the chunk.
		_chunk.clearEntities();
	}
	
	public void update(GameContainer container, StateBasedGame game, int delta) {
		_worldTime.update(delta / 14); //
		_chunk.update(container, game, delta, _worldTime);
	}
	
	public void render(Graphics g, EntityCamera camera) {
		_chunk.render(g, camera);
	}
}
