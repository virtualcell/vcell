package cbit.vcell.geometry.concept;

public class ThreeSpacePointImmutable implements ThreeSpacePoint {
	private final double x;
	private final double y;
	private final double z;

	public ThreeSpacePointImmutable(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	

	@Override
	public int hashCode() {
		return Double.hashCode(x)^Double.hashCode(x)^Double.hashCode(x);
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ThreeSpacePoint)) {
			return false;
		}
		ThreeSpacePoint rhs = (ThreeSpacePoint) obj;
		return x == rhs.getX() && y == rhs.getY() && z == rhs.getZ(); 
	}


	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getZ() {
		return z;
	}

}
