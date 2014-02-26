package asciiWorld.chunks;

import org.newdawn.slick.Color;

import asciiWorld.DateTime;
import asciiWorld.math.MathHelper;

public class DayNightCycleComponent extends ChunkComponent {

	private static final Color COLOR_DAY = Color.white;
	private static final Color COLOR_NIGHT = Color.black;
	
	private static final float MINUTES_PER_HOUR = 60.0f;
	private static final float HOURS_PER_DAY = 24.0f;
	private static final double TWO_PI = 2.0 * Math.PI;
	
	public DayNightCycleComponent(Chunk owner) {
		super(owner);
	}
	
	private DateTime getWorldTime() {
		return getOwner().getWorld().getWorldTime();
	}
	
	private float getHour() {
		DateTime worldTime = getWorldTime();
		return (float)((worldTime.getHour() - 6) + (float)worldTime.getMinute() / MINUTES_PER_HOUR);
	}
	
	private float getAmbientLightWeight() {
		float hourRatio = getHour() / HOURS_PER_DAY;
		return (float)(Math.sin(TWO_PI * hourRatio) + 1.0f) / 2.0f;
	}

	@Override
	public void update(int deltaTime) {
		getOwner().setAmbientLightColor(MathHelper.smoothStep(COLOR_NIGHT, COLOR_DAY, getAmbientLightWeight()));
	}
}
