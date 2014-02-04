package org.vcell.vis.core;

public class Vect3D {
	public final double x;
	public final double y;
	public final double z;
	
	public Vect3D(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public String toStringKey(){
		return toStringKey(6);
	}
	
	public String toStringKey(int precision){
		String formatString = "%."+precision+"f";
		return "("+String.format(formatString,x)+","+String.format(formatString,y)+","+String.format(formatString,z)+")";
	}
}