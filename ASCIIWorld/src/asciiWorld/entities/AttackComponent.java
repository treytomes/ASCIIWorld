package asciiWorld.entities;

import asciiWorld.animations.TileSwingAnimation;
import asciiWorld.math.Vector3f;

public class AttackComponent extends EntityComponent {

	private static final int CLICK_SEARCH_RADIUS = 1;
	
	private int _damageModifier;
	private int _range;
	
	public AttackComponent(Entity owner) {
		super(owner);
		_damageModifier = 1;
		_range = 1;
	}
	
	public Integer getDamageModifier() {
		return _damageModifier;
	}
	
	public void setDamageModifier(Integer value) {
		_damageModifier = value;
	}
	
	public Integer getRange() {
		return _range;
	}
	
	public void setRange(Integer value) {
		_range = value;
	}
	
	@Override
	public void use(Entity source, Vector3f targetChunkPoint) {
		source.addAnimation(TileSwingAnimation.createUseActiveItemAnimation(source, targetChunkPoint));
		
		double distance = Math.floor(Math.abs((source.getOccupiedChunkPoint().copy().subtract(targetChunkPoint.toVector2f())).length()));
		if (distance <= _range) {
			Entity entityAtPoint = source.getChunk().findClosestEntity(targetChunkPoint, CLICK_SEARCH_RADIUS);
			if (entityAtPoint != null) {
				entityAtPoint.takeDamage(source, getDamageModifier() + source.getAttackStrength());
			} /*else {
				
				var tile = source.Chunk[(int)targetChunkPoint.X, (int)targetChunkPoint.Y, 1];
				if (tile != null) {
					throw new EntityException("I do not know how to attack that tile yet.", source.Position);
				} else {
					tile = source.Chunk[(int)targetChunkPoint.X, (int)targetChunkPoint.Y, 1];
					throw new EntityException("I do not know how to attack that tile yet.", source.Position);
				}
			}*/
		}
	}
}
