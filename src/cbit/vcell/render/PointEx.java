package cbit.vcell.render;

/***
 * FastMarchingMethod high accuracy class
 ***/

class PointEx {
	private static final double MAX_NUMBER = Double.POSITIVE_INFINITY;
    private double distance;
    private int position;		// index in the image map
    private int hole;			// index in the binary heap; -1 is meaningless and indicates some error
    
    public PointEx(double distance, int position) {
		this.setDistance(distance);
		this.setPosition(position);
		this.setHole(-1);
    }
    public PointEx(int position) {
		this.setDistance(MAX_NUMBER);
		this.setPosition(position);
		this.setHole(-1);
    }
    
    public void setDistance(double distance) {
    	this.distance = distance;
    }
    public double getDistance() {
    	return this.distance;
    }
    public void setPosition(int position) {
    	this.position = position;
    }
    public int getPosition() {
    	return this.position;
    }
    public void setHole(int hole) {
		this.hole = hole;
	}
	public int getHole() {
		return hole;
	}

	public int compareTo(double otherDistance) {
		double epsilon = 0.00000001;
		
		if(this.distance + epsilon < otherDistance) {
			return -1;
		} else if(this.distance > otherDistance + epsilon) {
			return 1;
		} else {
			return 0;
		}
	}
}