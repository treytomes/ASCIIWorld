package asciiWorld.chunks;

public class ChunkComponent {

	private Chunk _owner;
	
	public ChunkComponent(Chunk owner) {
		_owner = owner;
	}
	
	public Chunk getOwner() {
		return _owner;
	}
	
	public void update(int deltaTime) { }
}
