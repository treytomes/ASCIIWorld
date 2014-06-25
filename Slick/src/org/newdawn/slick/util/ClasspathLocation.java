package org.newdawn.slick.util;

import java.io.InputStream;
import java.net.URL;

/**
 * A resource location that searches the class path.
 * 
 * @author kevin
 */
public class ClasspathLocation implements ResourceLocation {
	
	/**
	 * The class to load resources relative to.
	 */
	private Class<?> resourceClass;
	
	/**
	 * Load resources relative to the given class.
	 * 
	 * @param resourceClass The class to load resources relative to.
	 */
	public ClasspathLocation(Class<?> resourceClass) {
		this.resourceClass = resourceClass;
	}
	
	/**
	 * Load resources from the Slick class path.
	 */
	public ClasspathLocation() {
		this(ResourceLoader.class);
	}
	
	/**
	 * @see org.newdawn.slick.util.ResourceLocation#getResource(java.lang.String)
	 */
	public URL getResource(String ref) {
		String cpRef = ref.replace('\\', '/');
		return this.resourceClass.getClassLoader().getResource(cpRef);
	}

	/**
	 * @see org.newdawn.slick.util.ResourceLocation#getResourceAsStream(java.lang.String)
	 */
	public InputStream getResourceAsStream(String ref) {
		String cpRef = ref.replace('\\', '/');
		InputStream stream = this.resourceClass.getClassLoader().getResourceAsStream(cpRef);
		if (stream == null) {
			stream = this.resourceClass.getResourceAsStream(cpRef);
		}
		return stream;
	}
}
