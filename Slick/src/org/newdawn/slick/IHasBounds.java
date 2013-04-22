package org.newdawn.slick;

import org.newdawn.slick.geom.Rectangle;

/**
 * Indicates an object that has a position and size.
 * 
 * @author ttomes
 *
 */
public interface IHasBounds {
	
	/**
	 * 
	 * @return The position and size of this object. 
	 */
	public Rectangle getBounds();
}
