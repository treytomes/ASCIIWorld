package asciiWorld.entities;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

public class AttackOtherComponent extends EntityComponent {

	private String _typeToAttack;
	
	public AttackOtherComponent(Entity owner) {
		super(owner);
		_typeToAttack = null;
	}
	
	public String getTypeToAttack() {
		return _typeToAttack;
	}
	
	public void setTypeToAttack(String value) {
		_typeToAttack = value;
	}
	
	private Entity findNearestVisibleTarget(String targetEntityType) {
		for (Entity entity : getOwner().getChunk().getEntities()) {
			if (entity.getType().equals(targetEntityType)) {
				if (getOwner().getOccupiedChunkPoint().distance(entity.getOccupiedChunkPoint()) <= getOwner().getRangeOfVision()) {
					return entity;
				}
			}
		}
		return null;
	}
	
	private Entity findNearestVisibleTarget() {
		return findNearestVisibleTarget(getTypeToAttack());
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game, int deltaTime) {
		Entity targetEntity = findNearestVisibleTarget();
		if (targetEntity == null) {
			return;
		}
		double distance = Math.floor(Math.abs(targetEntity.getOccupiedChunkPoint().copy().subtract(getOwner().getOccupiedChunkPoint()).length()));

		// Find a weapon and attack with it.
		for (Entity item : getOwner().getInventory()) {
			AttackComponent attackComponent = item.findComponent(AttackComponent.class);
			if (attackComponent != null) {
				if (distance <= attackComponent.getRange()) {
					attackWithItem(targetEntity, item);
					return;
				}
			}
		}

		// No weapon was found; try to use your own body as a weapon.
		if (distance <= 1) {
			targetEntity.takeDamage(getOwner(), getOwner().getAttackStrength());
		}
	}
	
	private void attackWithItem(Entity attackMe, Entity withThis) {
		getOwner().setActiveItem(withThis);
		getOwner().useActiveItem(attackMe.getOccupiedChunkPoint(), attackMe.getLayer());
	}

}
