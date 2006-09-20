package cbit.image;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * This type was created in VisualAge.
 */
public class Neighbor {
	private int index;
	private byte[] pixels;
	private int x;
	private int y;
/**
 * Neighbor constructor comment.
 */
public Neighbor(int argIndex,byte[] argPixels,int argX,int argY) {
	super();
	this.index = argIndex;
	this.pixels = argPixels;
	this.x = argX;
	this.y = argY;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getIndex() {
	return index;
}
/**
 * This method was created in VisualAge.
 * @return byte[]
 */
public byte[] getPixels() {
	return pixels;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getX() {
	return x;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
public int getY() {
	return y;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public String toString() {
	StringBuffer s = new StringBuffer("X="+x+",Y="+y+",Index="+index);
	s.append(",Pixels[");
	for(int c=0;c<pixels.length;c+= 1){
		s.append(pixels[c]);
		if(c < (pixels.length-1)){
			System.out.println(" ");
		}
	}
	s.append("]");
	
	return s.toString();
}
}
