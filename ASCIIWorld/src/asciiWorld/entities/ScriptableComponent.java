package asciiWorld.entities;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import asciiWorld.FileHelper;
import asciiWorld.JavascriptContext;
import asciiWorld.Vector3f;
import asciiWorld.chunks.Chunk;

public class ScriptableComponent extends EntityComponent {

	private JavascriptContext _context;
	
	public ScriptableComponent(Entity owner) {
		super(owner);
		
		_context = new JavascriptContext();
		_context.addObjectToContext(this, "me");
	}
	
	private Object executeFunction(String name, Object[] args) {
		Object obj = _context.getObject(name);
		if (obj != Scriptable.NOT_FOUND) {
			if (obj instanceof Function) {
				Function fn = (Function)obj;
				return fn.call(_context.getContext(), _context.getScope(), _context.getScope(), args);
			}
		}
		return null;
	}
	
	@Override
	public void afterAddedToChunk(Chunk chunk) {
		executeFunction("afterAddedToChunk", new Object[] { chunk });
	}
	
	@Override
	public void afterRemovedFromChunk(Chunk chunk) {
		executeFunction("afterRemovedFromChunk", new Object[] { chunk });
	}
	
	@Override
	public void beforeAddedToChunk(Chunk chunk) {
		executeFunction("beforeAddedToChunk", new Object[] { chunk });
	}
	
	@Override
	public void beforeRemovedFromChunk(Chunk chunk) {
		executeFunction("beforeRemovedFromChunk", new Object[] { chunk });
	}
	
	@Override
	public void collided(Entity collidedWithEntity) {
		executeFunction("collided", new Object[] { collidedWithEntity });
	}
	
	@Override
	public void touched(Entity touchedByEntity) {
		executeFunction("touched", new Object[] { touchedByEntity });
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		executeFunction("update", new Object[] { container, game, deltaTime });
	}
	
	@Override
	public void use(Entity source, Vector3f targetChunkPoint) {
		executeFunction("use", new Object[] { source, targetChunkPoint });
	}
	
	public Object executeScript(String scriptText) throws Exception {
		return _context.executeScript(scriptText);
	}
	
	public void loadScript(String path) throws Exception {
		executeScript(FileHelper.readToEnd(path));
	}
}
