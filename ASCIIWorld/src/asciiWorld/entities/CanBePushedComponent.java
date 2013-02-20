package asciiWorld.entities;

public class CanBePushedComponent extends EntityComponent {

	public CanBePushedComponent(Entity owner) {
		super(owner);
	}

	@Override
	public void collided(Entity collidedWithEntity) {
		getOwner().move(collidedWithEntity.getDirection());
	}
}
