package asciiWorld.entities;

public class CanBePickedUpComponent extends EntityComponent {

	public CanBePickedUpComponent(Entity owner) {
		super(owner);
	}

	@Override
	public void touched(Entity touchedByEntity) {
		try {
			touchedByEntity.getInventory().add(getOwner());
		} catch (Exception e) {
			System.out.println(String.format("Unable to add item to %s's inventory.", touchedByEntity.getName()));
		}
	}
}
