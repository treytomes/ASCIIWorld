package asciiWorld.entities;

import asciiWorld.math.RandomFactory;

public enum Gender {
	Male,
	Female;
	
	public static Gender random() {
		switch (RandomFactory.get().nextInt(0, 2)) {
		case 0:
			return Male;
		case 1:
			return Female;
		default:
			return null;	
		}
	}
}