package org.vcell.vis.core;

public enum Face {
	Xm(0),
	Xp(1),
	Ym(2),
	Yp(3),
	Zm(4),
	Zp(5);

	private final int index;
	
	private Face(int index){
		this.index = index;
	}
	
	public static Face fromInteger(int faceNumber) {
		for (Face face : values()){
			if (face.index == faceNumber){
				return face;
			}
		}
		throw new RuntimeException("unexpected face number "+faceNumber+" while parsing boundary triangles");
	}
}
