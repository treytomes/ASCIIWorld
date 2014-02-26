package asciiWorld.entities;

import org.newdawn.slick.geom.Vector2f;

import asciiWorld.Direction;
import asciiWorld.math.RandomFactory;

public class KnockBackComponent extends EntityComponent {

	public KnockBackComponent(Entity owner) {
		super(owner);
	}

	@Override
	public void afterDamaged(Entity damagedByEntity, int amount) {
		Vector2f vector = getOwner().getOccupiedChunkPoint().copy().subtract(damagedByEntity.getOccupiedChunkPoint());
		if ((vector.x != 0) && (vector.y != 0)) {
			// Pick one to zero out.
			if (RandomFactory.get().nextDouble() <= 0.5) {
				vector.x = 0;
			} else {
				vector.y = 0;
			}
		}
		Direction knockBackDirection = Direction.fromVector2f(vector.normalise());
		getOwner().knockBack(knockBackDirection);
	}
}
