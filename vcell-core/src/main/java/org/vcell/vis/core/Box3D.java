package org.vcell.vis.core;

public class Box3D {
	public final double x_lo;
	public final double y_lo;
	public final double z_lo;
	public final double x_hi;
	public final double y_hi;
	public final double z_hi;
	
	
	public Box3D(double x_lo, double y_lo, double z_lo, double x_hi, double y_hi, double z_hi) {
		this.x_lo = x_lo;
		this.y_lo = y_lo;
		this.z_lo = z_lo;
		this.x_hi = x_hi;
		this.y_hi = y_hi;
		this.z_hi = z_hi;
	}

	public String toStringKey(){
		return toStringKey(6);
	}
	
	public String toStringKey(int precision){
		String formatString = "%."+precision+"f";
		return "(("+String.format(formatString,x_lo)+","+String.format(formatString,y_lo)+","+String.format(formatString,z_lo)+")" +
				" : ("+String.format(formatString,x_hi)+","+String.format(formatString,y_hi)+","+String.format(formatString,z_hi)+"))";
	}
}