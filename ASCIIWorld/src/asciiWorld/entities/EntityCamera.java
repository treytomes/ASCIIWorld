package asciiWorld.entities;

import org.newdawn.slick.IHasBounds;

import asciiWorld.Camera;

public class EntityCamera extends Camera {

	private Entity _focusEntity;
	
	public EntityCamera(IHasBounds viewport, Entity focusEntity, float scale) {
		super(viewport, focusEntity, scale);
		_focusEntity = focusEntity;
	}
	
	public Entity getFocusEntity() {
		return _focusEntity;
	}
}
