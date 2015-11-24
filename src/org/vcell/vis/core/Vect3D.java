package org.vcell.vis.core;

import org.vcell.util.Matchable;

public class Vect3D implements Matchable {
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

	@Override
	public boolean compareEqual(Matchable obj) {
		if (obj instanceof Vect3D){
			Vect3D other = (Vect3D)obj;
			if (x!=other.x || y!=other.y || z!=other.z){
				return false;
			}
			return true;
		}
		return false;
	}
}