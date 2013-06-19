package asciiWorld.entities;

public class RecipeComponent extends EntityComponent {

	private String _entityType;
	private String[] _requirements;
	
	public RecipeComponent(Entity owner) {
		super(owner);
		_entityType = null;
		_requirements = null;
	}
	
	public String getEntityType() {
		return _entityType;
	}
	
	public void setEntityType(String value) {
		_entityType = value;
	}
}
