package cbit.vcell.render;

import java.util.Arrays;
import java.util.Random;

import cbit.vcell.geometry.surface.Node;

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
		double epsilon = 0.00000000001;
		
		if(this.distance + epsilon < otherDistance) {
			return -1;
		} else if(this.distance > otherDistance + epsilon) {
			return 1;
		} else {
			return 0;
		}
	}
}


//@SuppressWarnings({"unused", "unchecked", "rawtypes"})
/***
 * This class is the main class. It implements the fast marching and level set methods  
 ***/
public class FastMarchingMethod {

    // auxiliary constants used for selecting max and min values of certain variables
    private static final double MAX_NUMBER = Double.POSITIVE_INFINITY;
    
    private enum State { far, narrow, frozen }
    private enum RootsType {error, degenerate, real, imaginar}

    // image matrix dimension, their values are read in from image files
    private int numX = 0;
    private int numY = 0;
    private int numZ = 0;
    private int arraySize = 0;
    private double[] distanceMap = null;
    
    private BinaryHeap narrowBand = new BinaryHeap();
    private State[] state = null;
    private PointEx[] points = null;

    // distanceMap array meaning:
    // the non-POSITIVE_INFINITY points represent the initial condition
    // all the rest initialized at Double.POSITIVE_INFINITY
	public FastMarchingMethod(int numX, int numY, int numZ, double[] distanceMap) {
		// index = x + y*numX + z*numX*numY
		this.numX = numX;
		this.numY = numY;
		this.numZ = numZ;
		this.arraySize = numX*numY*numZ;
		
		if(arraySize != distanceMap.length) {
			throw new RuntimeException("Object size different than DistanceMap size");
		}
		this.distanceMap = distanceMap;
		state = new State[numX*numY*numZ];
		Arrays.fill(state, State.far);
		points = new PointEx[numX*numY*numZ];
		Arrays.fill(points, null);

	}
	
	// here is done all the work, returns the completed distanceMap array
	public double[] march() {
		// ---------------- initializations ---------------
		long t1 = System.currentTimeMillis();
		int frozenCount = initializeFrozen();
		for(int i=0; i<arraySize; i++) {
			if(state[i] == State.frozen) {
				manageCandidates(i);
			}
		}
		long t2 = System.currentTimeMillis();
//		System.out.println(" end of initialization, " + frozenCount + " frozen elements, duration " + 
//				(int)((t2-t1)/1000) + " seconds.");
		
		// ----------------- work loop ---------------------
		int max = 0;
		while(!narrowBand.isEmpty()) {
			PointEx frozenElement = (PointEx) narrowBand.removeMin();
			state[frozenElement.getPosition()] = State.frozen;
			
			if(points[frozenElement.getPosition()] != frozenElement) {
				throw new RuntimeException("Element mismatch: must be the same element.");
			}
			
			int distance = (int)(frozenElement.getDistance());
			if(max<distance) {
				max=distance;
				if(max%20 == 0) {
//					System.out.println("Max distance reached " + max + ", narrowBand size is " + narrowBand.size());
				}
			}
			manageCandidates(frozenElement.getPosition());
		}
		
		long t3 = System.currentTimeMillis();
//		System.out.println("---------- " + (int)((t3-t1)/1000) + " seconds ----------");
		
		for(int i=0; i<distanceMap.length; i++) {
			distanceMap[i] = points[i].getDistance();
		}
		return distanceMap;
	}
	
	private void manageCandidates(int currentPosition) {
		int candidatePosition = 0;
		
		candidatePosition = nextOnX(currentPosition);
		manageCandidate(candidatePosition);
		candidatePosition = nextOnY(currentPosition);
		manageCandidate(candidatePosition);
		candidatePosition = nextOnZ(currentPosition);
		manageCandidate(candidatePosition);
		candidatePosition = prevOnX(currentPosition);
		manageCandidate(candidatePosition);
		candidatePosition = prevOnY(currentPosition);
		manageCandidate(candidatePosition);
		candidatePosition = prevOnZ(currentPosition);
		manageCandidate(candidatePosition);
	}

	private void manageCandidate(int candidatePosition) {
		if(candidatePosition < 0) {
			return;				// out of bounds, nothing to do
		}
		if(state[candidatePosition] == State.frozen) {
			return;				// already frozen, nothing to do
		}
		double candidateDistance = 0;
		candidateDistance = computeDistance(candidatePosition);
		if(candidateDistance == -1) {
			throw new RuntimeException("Unable to compute distance at position " + candidatePosition);
		}
		PointEx candidate = points[candidatePosition];
		if(state[candidatePosition] == State.narrow) {
			narrowBand.decreaseKey(candidate.getHole(), candidateDistance);			// already part of narrow band, decrease key if possible
		} else {
			state[candidatePosition] = State.narrow;
			candidate.setDistance(candidateDistance);
			narrowBand.insert(candidate);
		}
	}

	private double computeDistance(int candidatePosition) {
		int[] neighborPosition = new int[6];			// _val1
		int[] deepPosition = new int[6];				// _val2  for the high accuracy distance
		
		neighborPosition[0] = prevOnX(candidatePosition);
		neighborPosition[1] = nextOnX(candidatePosition);
		neighborPosition[2] = prevOnY(candidatePosition);
		neighborPosition[3] = nextOnY(candidatePosition);
		neighborPosition[4] = prevOnZ(candidatePosition);
		neighborPosition[5] = nextOnZ(candidatePosition);
		
		deepPosition[0] = prevOnX(prevOnX(candidatePosition));
		deepPosition[1] = nextOnX(nextOnX(candidatePosition));
		deepPosition[2] = prevOnY(prevOnY(candidatePosition));
		deepPosition[3] = nextOnY(nextOnY(candidatePosition));
		deepPosition[4] = prevOnZ(prevOnZ(candidatePosition));
		deepPosition[5] = nextOnZ(nextOnZ(candidatePosition));
		
		double a = 0;		// coefficients for the quadratic equation
		double b = 0;
		double c = -1;
		
		for(int j=0; j<3; j++) {		// 3 dimensions: x y z
			double val1 = MAX_NUMBER;
			double val2 = MAX_NUMBER;
			// TODO: not sure how well this works if we find frozen cells in both directions
			for(int i=0; i<2; i++){		// 2 directions: prev, next
				int nP = neighborPosition[2*j+i];
				if(nP == -1) {
					continue;			// neighbor out of bounds
				}
				if(state[nP] == State.frozen) {	// the only distances we know are for the frozen cells
					double distance1 = getDistanceAt(nP);
					if(distance1 == -1) {		// since State says frozen, the Point element must be in the frozen list
						throw new RuntimeException("Frozen element missing at " +nP);
					}
					if(distance1 < val1) {
						val1 = distance1;
						double distance2 = MAX_NUMBER;
						int dP = deepPosition[2*j+i];
						if(dP == -1) {
							distance2 = MAX_NUMBER;		// neighbor out of bounds, coefficient will be ignored
						} else {
							if(state[dP] == State.frozen) {
								distance2 = getDistanceAt(dP);
							}
						}
						if(distance2!=-1 && distance2<MAX_NUMBER && distance2<=distance1) {
							val2 = distance2;
						} else {
							val2 = MAX_NUMBER;
						}
					}
				}
			}
			if(val2 != MAX_NUMBER) {
				double tp = (1.0/3)*(4*val1-val2);
				double aa = 9.0/4;
				a += aa;
				b -= 2 * aa * tp;
				c+= aa * tp * tp;
			} else 
			if(val1 != MAX_NUMBER) {
				a += 1;
				b -= 2 * val1;
				c += val1 * val1;
			}
		}
		QuadraticRoots r = solveQuadratic(a, b, c);
		if(r.getType() == RootsType.real) {
			double distance = Math.max(r.getRoot1(), r.getRoot2());
			return distance;
		} else if(r.getType() == RootsType.degenerate) {
			double distance = r.getRoot1();
			return distance;
		} else {
			return -1;
		}
	}

	private double getDistanceAt(int nP) {
		double distance = points[nP].getDistance();
		if( distance< MAX_NUMBER) {
			return distance;
		} else {
			return -1;
		}
	}

	private int initializeFrozen() {
		int frozenCount = 0;
		int index = 0;
		for(int z=0; z<numZ; z++) {
			for(int y=0; y<numY; y++) {
				for(int x=0; x<numX; x++) {
					index = x + y*numX + z*numX*numY;
					double distance = distanceMap[index];
					
					points[index] = new PointEx(distance, index);
					if(distance < MAX_NUMBER) {
						state[index] = State.frozen;
						frozenCount++;
					}
				}
			}
		}
		return frozenCount;
	}

	// returns -1 if out of bounds
	// for the "next" voxels we only check the right boundary
	// for the "prev" voxels we only check the left boundary
	// index = x + y*numX + z*numX*numY;
    public int nextOnX(int position) {
    	if(position == -1) {
    		return -1;			// properly propagate error in recursive calls
    	}
    	int x = getX(position) + 1;
    	if(x >= numX) {
    		return -1;
    	}
    	int newPosition = position+1;
    	return newPosition;
    }
    public int prevOnX(int position) {
    	if(position == -1) {
    		return -1;
    	}
    	int x = getX(position) - 1;
    	if(x < 0) {
    		return -1;
    	}
    	int newPosition = position-1;
   		return newPosition;
    }
    public int nextOnY(int position) {
    	if(position == -1) {
    		return -1;
    	}
    	int y = getY(position) + 1;
    	if(y >= numY) {
    		return -1;
    	}
    	int newPosition = position+numX;
   		return newPosition;
    }
    public int prevOnY(int position) {
    	if(position == -1) {
    		return -1;
    	}
    	int y = getY(position) - 1;
    	if(y < 0) {
    		return -1;
    	}
    	int newPosition = position-numX;
   		return newPosition;
    }
    public int nextOnZ(int position) {
    	if(position == -1) {
    		return -1;
    	}
    	int z = getZ(position) + 1;
    	if(z >= numZ) {
    		return -1;
    	}
    	int newPosition = position+numX*numY;
   		return newPosition;
    }
    public int prevOnZ(int position) {
    	if(position == -1) {
    		return -1;
    	}
    	int z = getZ(position) - 1;
    	if(z < 0) {
    		return -1;
    	}
    	int newPosition = position-numX*numY;
   		return newPosition;
    }
    
    // Quadratic Equation Solver   a.x^2 + b.x + c = 0
    private QuadraticRoots solveQuadratic(double a, double b, double c) {
    	
    	QuadraticRoots r = new QuadraticRoots();
    	// check for degenerate roots
    	if ( a==0 && b==0 ) {
    		r.setType(RootsType.error);	// cannot solve c = 0
    		return r;
    	} 
    	if ( a==0 && b!=0 ) {
    		r.setRoot1(-c/b);
    		r.setType(RootsType.degenerate);
    		return r;
    	}
    	double discriminant = discriminant(a,b,c);		// discriminant of quadratic equation
    	if ( discriminant >= 0.0 ) {					// two real roots
    		r.setRoot1((double)(-b/2.0/a-(double)Math.sqrt(discriminant)/2.0/a));
    		r.setRoot2((double)(-b/2.0/a+(double)Math.sqrt(discriminant)/2.0/a));
    		r.setType(RootsType.real);
    		return r;
    	} else {
    		r.setRoot1((double)(-b/2.0/a));				// two complex roots
    		r.setRoot2((double)(Math.sqrt(-discriminant)/2.0/a));
    		r.setType(RootsType.imaginar);
    		return r;
    	}
    }
    private static double discriminant(double a, double b, double c) {
    	return (double)(b*b-4.0*a*c);
    }

	// index = x + y*numX + z*numX*numY;
	private int getX(int position) {
    	int z = (int)(position/(numX*numY));
    	int tmp1 = position - z*numX*numY;
    	int x = (int)(tmp1%numX);
    	return x;
	}
	private int getY(int position) {
    	int z = (int)(position/(numX*numY));
    	int tmp1 = position - z*numX*numY;
    	int y = (int)(tmp1/numX);
    	return y;
	}
	private int getZ(int position) {
    	int z = (int)(position/(numX*numY));
    	return z;
	}

    private class QuadraticRoots {
    	private RootsType type;
    	private double root1;
    	private double root2;
		public void setType(RootsType type) {
			this.type = type;
		}
		public void setRoot1(double root1) {
			this.root1 = root1;
		}
		public void setRoot2(double root2) {
			this.root2 = root2;
		}
		public RootsType getType() {
			return type;
		}
		public double getRoot1() {
			return root1;
		}
		public double getRoot2() {
			return root2;
		}
    }
    
    
    public static void main(String[] args) {
  	
		int numX = 2;
		int numY = 2;
		int numZ = 5;
		int numItems = numX*numY*numZ;

		double distanceMap[] = new double[numItems];
		Arrays.fill(distanceMap, MAX_NUMBER);
		distanceMap[0] = 0;		// initial condition (exact distances from origin)
		distanceMap[1] = 1;
		distanceMap[2] = 1;
		distanceMap[3] = 1.4142135;			// sqrt(2) - diagonal of a square
		distanceMap[4] = 1;
		distanceMap[5] = 1.4142135;
		distanceMap[6] = 1.4142135;
		distanceMap[7] = 1.7320508;			// sqrt(3) - diagonal of a cube
		// ----- next points distances for the above initial conditions
		// 			FMM results vs. 	FMMHA results vs. 	exact computed results:
		//  8:		2.000000			2.000000			2.000000
		//  9:		2.350700 (0.12)		2.204818 (0.03)		2.236067	sqrt(5)
		// 10:		2.350700			2.204818			2.236067
		// 11:		2.642763 (0.20)		2.459807 (0.01)		2.449489	sqrt(6)
		// 12:		3.000000			2.999999			3.000000
		// 13:		3.303524 (0.14)		3.129413 (0.04)		3.162277	sqrt(10)
		// 14:		3.303524			3.129413			3.162277	sqrt(10)
		// 15:		3.569388 (0.25)		3.339079 (0.02)		3.316624	sqrt(2+9)

		FastMarchingMethod fmm = new FastMarchingMethod(numX, numY, numZ, distanceMap);
		
		System.out.println("Verifying indexes manipulation; index = x + y*numX + z*numX*numY");
		int index = 0;
		for(int z=0; z<numZ; z++) {
			for(int y=0; y<numY; y++) {
				for(int x=0; x<numX; x++) {
					index = x + y*numX + z*numX*numY;
					System.out.println(x + ", " + y + ", " + z + ":  " + index + "      ");
					System.out.println(fmm.getX(index) + ", " + fmm.getY(index) + ", " + fmm.getZ(index));
				}
			}
		}
		System.out.println("x, y, z:  index");
		fmm.march();
		
		System.out.println("");
		System.out.println(" -------------------- TEST 1 - accuracy test ----------------------- ");
		for(index=0; index<fmm.points.length; index++) {
			System.out.println(index + ": " + fmm.points[index].getDistance());
		}

		System.out.println("");
		System.out.println(" -------------------- TEST 2 - large array ----------------------- ");
		
		numX = 100;
		numY = 100;
		numZ = 100;
		numItems = numX*numY*numZ;

		distanceMap = new double[numItems];
		Arrays.fill(distanceMap, MAX_NUMBER);
		// initial condition (a small cylinder) at 20, 20, 20
		int x = 50;
		int y = 50;
		int z = 50;
		int i = x + y*numX + z*numX*numY;
		distanceMap[i] = 0;
		distanceMap[i+1] = 0;
		distanceMap[i+2] = 0;
		
		distanceMap[i-1] = 1;	// the tips
		distanceMap[i+3] = 1;
		
		i = x + (y+1)*numX + z*numX*numY;	// closest neighbors on XZ plane
		distanceMap[i] = 0;
		distanceMap[i+1] = 1;
		distanceMap[i+2] = 1;
		i = x + (y-1)*numX + z*numX*numY;
		distanceMap[i] = 0;
		distanceMap[i+1] = 1;
		distanceMap[i+2] = 1;

		i = x + y*numX + (z+1)*numX*numY;	// closest neighbors on XY plane
		distanceMap[i] = 0;
		distanceMap[i+1] = 1;
		distanceMap[i+2] = 1;
		i = x + y*numX + (z-1)*numX*numY;
		distanceMap[i] = 0;
		distanceMap[i+1] = 1;
		distanceMap[i+2] = 1;
		
		fmm = new FastMarchingMethod(numX, numY, numZ, distanceMap);
		
		long t1 = System.currentTimeMillis();
		fmm.march();
		long t2 = System.currentTimeMillis();
		System.out.println("---------- " + (int)((t2-t1)/1000) + " seconds ----------");
		

		System.out.println("");
		System.out.println(" -------------------- TEST 3 - compare accuracy ----------------------- ");
		
		Vect3d p = null;
		double d1 = 0;

		numX = 100;
		numY = 100;
		numZ = 100;
		numItems = numX*numY*numZ;

		distanceMap = new double[numItems];
		Arrays.fill(distanceMap, MAX_NUMBER);
		
		Vect3d A = new Vect3d(0, 0, 0);
		Vect3d B = new Vect3d(1, 0, 0);
		Vect3d C = new Vect3d(0, 1, 0);
		
		i = (int)(A.getX() + A.getY()*numX + A.getZ()*numX*numY);
		distanceMap[i] = 0;
		i = (int)(B.getX() + B.getY()*numX + B.getZ()*numX*numY);
		distanceMap[i] = 0;
		i = (int)(C.getX() + C.getY()*numX + C.getZ()*numX*numY);
		distanceMap[i] = 0;
		
		fmm = new FastMarchingMethod(numX, numY, numZ, distanceMap);
		distanceMap = fmm.march();

		int numIterations = 10000;
		double errorThreshold = 0.35;
		int errorCount = 0;
		Random rand = new Random();
		for(int j=0; j<numIterations; j++) {
			x = (int)(3+rand.nextDouble()*96);
			y = (int)(3+rand.nextDouble()*96);
			z = (int)(3+rand.nextDouble()*96);
			i = x + y*numX + z*numX*numY;
			p = new Vect3d(x, y, z);
			d1 = Vect3d.distanceToTriangle3d(p, A, B, C);
			double delta = Math.abs(d1-distanceMap[i]);
			if(delta > errorThreshold) {
				System.out.println(x + ", " + y + ", " + z + " - delta: " + delta);
				errorCount++;
			}
		}
		System.out.println("Delta above threshold " + errorThreshold + " occured in " + errorCount + " cases out of " + numIterations);

		System.out.println("A few random examples: ffm distance vs. exact distance");
		for(int j=0; j<5; j++) {
			x = (int)(1 + rand.nextDouble()*98);
			y = (int)(1 + rand.nextDouble()*98);
			z = (int)(1 + rand.nextDouble()*98);
			i = x + y*numX + z*numX*numY;
			p = new Vect3d(x, y, z);
			d1 = Vect3d.distanceToTriangle3d(p, A, B, C);
			System.out.println(x + ", " + y + ", " + z + " - ffm dist: " + distanceMap[i] + ",   exact dist: " + d1);
		}

		
		System.out.println("Done");
	}
}




