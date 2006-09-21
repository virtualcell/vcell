package cbit.render.objects;


/**
 * Insert the type's description here.
 * Creation date: (10/30/2001 2:39:56 PM)
 * @author: John Wagner
 */
public class BoundingBox {
	private double fieldLoX = 0;
	private double fieldHiX = 0;
	private double fieldLoY = 0;
	private double fieldHiY = 0;
	private double fieldLoZ = 0;
	private double fieldHiZ = 0;

/**
 * BoundingBox constructor comment.
 */
public BoundingBox(double loX, double hiX, double loY, double hiY, double loZ, double hiZ) {
	super();
	fieldLoX = loX;
	fieldHiX = hiX;
	fieldLoY = loY;
	fieldHiY = hiY;
	fieldLoZ = loZ;
	fieldHiZ = hiZ;
}

public Vect3d getCenter(){
	return new Vect3d((fieldLoX+fieldHiX)/2.0,(fieldLoY+fieldHiY)/2.0,(fieldLoZ+fieldHiZ)/2.0); 
}

public Vect3d getSize(){
	return new Vect3d(fieldHiX-fieldLoX,fieldHiY-fieldLoY,fieldHiZ-fieldLoZ); 
}

public Vect3d getOrigin(){
	return new Vect3d(fieldLoX,fieldLoY,fieldLoZ); 
}

public static BoundingBox fromNodes(Node[] nodes){
	
	double minX = Double.POSITIVE_INFINITY;
	double maxX = Double.NEGATIVE_INFINITY;
	double minY = Double.POSITIVE_INFINITY;
	double maxY = Double.NEGATIVE_INFINITY;
	double minZ = Double.POSITIVE_INFINITY;
	double maxZ = Double.NEGATIVE_INFINITY;
	
	for (int i=0;i<nodes.length;i++){
		Node node = nodes[i];
		double x = node.getX();
		double y = node.getY();
		double z = node.getZ();
		minX = Math.min(minX,x);
		maxX = Math.max(maxX,x);
		minY = Math.min(minY,y);
		maxY = Math.max(maxY,y);
		minZ = Math.min(minZ,z);
		maxZ = Math.max(maxZ,z);
	}
	return new BoundingBox(minX,maxX,minY,maxY,minZ,maxZ);
}

public static BoundingBox union(BoundingBox box1, BoundingBox box2){
	return new BoundingBox(
			Math.min(box1.getLoX(),box2.getLoX()), Math.max(box1.getHiX(),box2.getHiX()),
			Math.min(box1.getLoY(),box2.getLoY()), Math.max(box1.getHiY(),box2.getHiY()),
			Math.min(box1.getLoZ(),box2.getLoZ()), Math.max(box1.getHiZ(),box2.getHiZ()));
}

/**
 * Gets the hiX property (double) value.
 * @return The hiX property value.
 */
public double getHiX() {
	return fieldHiX;
}


/**
 * Gets the hiY property (double) value.
 * @return The hiY property value.
 */
public double getHiY() {
	return fieldHiY;
}


/**
 * Gets the hiZ property (double) value.
 * @return The hiZ property value.
 */
public double getHiZ() {
	return fieldHiZ;
}


/**
 * Gets the loX property (double) value.
 * @return The loX property value.
 */
public double getLoX() {
	return fieldLoX;
}


/**
 * Gets the loY property (double) value.
 * @return The loY property value.
 */
public double getLoY() {
	return fieldLoY;
}


/**
 * Gets the loZ property (double) value.
 * @return The loZ property value.
 */
public double getLoZ() {
	return fieldLoZ;
}
}