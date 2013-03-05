package asciiWorld.entities;

import asciiWorld.math.Vector3f;

public class PlaceableComponent extends EntityComponent {

	public PlaceableComponent(Entity owner) {
		super(owner);
	}

	@Override
	public void use(Entity source, Vector3f targetChunkPoint) {
		if (!source.getChunk().isSpaceOccupied(targetChunkPoint)) {
			getOwner().moveTo(targetChunkPoint.toVector2f(), targetChunkPoint.z);
			source.getChunk().addEntity(getOwner());
			try {
				getOwner().getContainer().remove(getOwner());
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Unable to remove the entity from it's container.");
			}
		}
	}
}
