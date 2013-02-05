/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.render;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import cbit.vcell.geometry.surface.DistanceMapGenerator;
import cbit.vcell.geometry.surface.Node;

/***
 * This class implements the fast marching method, high accuracy version
 * You are not expected to understand this code
 ***/
public class FastMarchingMethodHA {
	@SuppressWarnings("unused")
	private static final double epsilon = 1e-8;
    // auxiliary constants used for selecting max and min values of certain variables
    private static final double MAX_NUMBER = Double.POSITIVE_INFINITY;
    
    private static final byte STATE_FAR = 1;	// distance not computed yet
    private static final byte STATE_NARROW = 2;	// part of the wave front, in the process of being computed
    private static final byte STATE_FROZEN = 3;	// already computed
    private static final byte STATE_COLD = 4;	// masked out, not to be computed
    
    private static final double tpConst = 1.0/3.0;	// coefficients for the 2nd order polynomial
    private static final double aaConst = 9.0/4.0;

    
    // image matrix dimension, their values are read in from image files
    private final int numX;
    private final int numY;
    private final int numZ;
    
    private final double sqrInvDeltaX;
    private final double sqrInvDeltaY;
    private final double sqrInvDeltaZ;
    private boolean differentZDistance = false;
    
    private final int numXnumY;
    private final int numXMinusOne;
    private final int numYMinusOne;
    private final int numZMinusOne;
    private final int numXMinusTwo;
    private final int numYMinusTwo;
    private final int numZMinusTwo;
    private int arraySize = 0;
    private double[] distanceMap = null;	// distance of each element to the closest membrane, MAX_NUMBER if not computed
    private int[] heapIndex = null;		// indicates position of each element in binary heap, 0 if not in binary heap
    private byte[] state = null;		// indicates state of each point 
    
    private BinaryHeapFast narrowBand = new BinaryHeapFast();
    public class BinaryHeapFast
    {
		private static final double epsilonHeap = 5e-5;
		private static final int DEFAULT_SIZE = 200;
		
		private int currentSize;		// # of elements
		private int[] array;			// the heap array
    	
    	public BinaryHeapFast( )
    	{
    		currentSize = 0;
    		array = new int[DEFAULT_SIZE+1];
    	}

    	private int insert(int globalIndex)
    	{
    		if(currentSize+1 == array.length) {
    			doubleArray();
    		}
    		// percolate up
    		int index;
    		int hole = ++currentSize;
    		array[0] = globalIndex;

    		for(; distanceMap[globalIndex] < distanceMap[array[hole/2]]; hole/=2) {
    			index = array[hole/2];
    			array[hole] = index;
    			heapIndex[index] = hole;
    		}
    		array[hole] = globalIndex;
    		heapIndex[globalIndex] = hole;
    		return hole;
    	}

    	// returns the top of the binary heap (smallest item)
    	private int removeMin()
    	{
     		int holeIndex = 1;
    		int minItem = array[1];
    		array[1] = array[currentSize--];
    		int child = 2;
    		int tmp = array[holeIndex];
    		int position;
    		// percolate down
    		for(; child <= currentSize; holeIndex=child, child=holeIndex*2)
    		{
    			if( child != currentSize && distanceMap[array[child+1]] < distanceMap[array[child]]) {
    				child++;
    			}
    			position = array[child];
    			if(distanceMap[position] < distanceMap[tmp]) {
    				array[holeIndex] = position;
    				heapIndex[position] = holeIndex;
    			} else {
    				break;
    			}
    		}
    		array[holeIndex] = tmp;
    		heapIndex[tmp] = holeIndex;
    		heapIndex[minItem] = 0;			// object is out of the heap
    		return minItem;	
    	}

    	// decrease element key if element is present
     	// hole = position in array of the element whose key we want to decrease
    	private void decreaseKey(int hole, double newDistance) {
    		int tmp = array[hole];
    		if(distanceMap[tmp] + epsilonHeap < newDistance) {		// cannot decrease to a larger key
//    			System.out.println("New key " + newDistance +" is larger than current key" + distanceMap[array[hole]] + " at " + hole);
    			return;
//    			throw new RuntimeException("New key is larger than current key");
    		} else if(Math.abs(distanceMap[tmp] - newDistance) < epsilonHeap) {
    			return;		// they're equal, nothing to do
    		}
    		int index;
    		distanceMap[tmp] = newDistance;

    		// we shift down 1 position all elements above hole which have a key larger than our element
    		// similar to "percolate up" the hole
    		for(; hole>1; hole/=2) {
    			if(distanceMap[tmp] < distanceMap[array[hole/2]]) {
        			index = array[hole/2];
    				array[hole] = index;	
    				heapIndex[index] = hole;
    			} else {
    				break;
    			}
    		}
    		array[hole] = tmp;
    		heapIndex[tmp] = hole;
    	}

    	private boolean isEmpty()
    	{
    		return currentSize == 0;
    	}

    	private int size()
    	{
    		return currentSize;
    	}
     
    	private void makeEmpty()
    	{
    		currentSize = 0;
    	}
    	
    	private void doubleArray()
    	{    		
    		int[] newArray = new int[array.length*2];
    		System.arraycopy(array, 0, newArray, 0, array.length);
    		array = newArray;
       	}
    }
    
    // distanceMap array meaning:
    // the non-POSITIVE_INFINITY points represent the initial condition
    // all the rest initialized at Double.POSITIVE_INFINITY
    // if ignoreMask is null we don't ignore anything
	public FastMarchingMethodHA(int numX, int numY, int numZ, double[] distanceMap, boolean[] ignoreMask) {
		this(numX, numY, numZ, (double)1, (double)1, (double)1, distanceMap, ignoreMask);
	}
	// deltaX = samplesX[1]-samplesX[0];
	public FastMarchingMethodHA(int numX, int numY, int numZ, double deltaX, double deltaY, double deltaZ,
			double[] distanceMap, boolean[] ignoreMask) {
		this.numX = numX;		// index = x + y*numX + z*numXnumY
		this.numY = numY;
		this.numZ = numZ;
		
		if(deltaX==0 || deltaY==0 || deltaZ==0) {
			throw new RuntimeException("The distance between voxels cannot be zero");
		}
		double epsilonXY = 0.1;		// tolerances between distances between pixels on various axes (empirical)
		double epsilonXYZ = 0.9;
		double epsilonXZ = 1.1;
		if(Math.abs(deltaX - deltaY) > Math.max(deltaX, deltaY)*epsilonXY) {	// replaced  if(deltaX != deltaY)
			throw new RuntimeException("Distances between the X and Y voxels must be the same");
		}
		if((epsilonXYZ*deltaX > deltaZ) || (epsilonXYZ*deltaY > deltaZ)) {		// replaced  if(deltaX > deltaZ)
			throw new RuntimeException("Distances between the Z voxels must be equal or greater " + 
					"than the distances between voxels on the other axes.");
		}
		if((epsilonXZ*deltaX < deltaZ) || (epsilonXZ*deltaY < deltaZ)) {		// replaced  if(deltaX < deltaZ)
			differentZDistance = true;
		}
		
		this.sqrInvDeltaX = 1 / deltaX / deltaX;		// squared speed on each axis, in voxel units / time units
		this.sqrInvDeltaY = 1 / deltaY / deltaY;
		this.sqrInvDeltaZ = 1 / deltaZ / deltaZ;
		
		this.numXnumY = numX*numY;		// precomputed variables for speed optimization
		this.numXMinusOne = numX-1;
		this.numYMinusOne = numY-1;
		this.numZMinusOne = numZ-1;
		this.numXMinusTwo = numX-2;
		this.numYMinusTwo = numY-2;
		this.numZMinusTwo = numZ-2;
		this.arraySize = numXnumY*numZ;
		this.heapIndex = new int[arraySize];  // zero if element is not in the heap
		
		if(arraySize != distanceMap.length) {
			throw new RuntimeException("Object size different than DistanceMap size");
		}
		this.distanceMap = distanceMap;
		state = new byte[numXnumY*numZ];
		Arrays.fill(state, STATE_FAR);
		if(ignoreMask != null && ignoreMask.length == distanceMap.length) {
			for(int i=0; i<arraySize; i++) {
				if(ignoreMask[i]  == true) {
					state[i] = STATE_COLD;
				}
			}
		}
	}
	
	// here is done all the work, returns the completed distanceMap array
	public void march() {
		// ---------------- initializations ---------------
		long t1 = System.currentTimeMillis();
		initializeFrozen();
		for(int i=0; i<arraySize; i++) {
			if(state[i] == STATE_FROZEN) {
				manageCandidates(i);
			}
		}
		long t2 = System.currentTimeMillis();
		// ----------------- work loop ---------------------
		int position = 0;
		while(!narrowBand.isEmpty()) {
			position = narrowBand.removeMin();
			state[position] = STATE_FROZEN;
			manageCandidates(position);
		}
		long t3 = System.currentTimeMillis();
		System.out.println("               FMM-HA:   " + (int)((t2-t1)/1000) + " sec initialization, " + (int)((t3-t1)/1000) + " sec total.");
	}
	
	private void manageCandidates(int currentPosition) {

		int z = (int)(currentPosition/(numXnumY));
    	int tmp1 = currentPosition - z*numXnumY;
    	int x = (int)(tmp1%numX);
    	int y = (int)(tmp1/numX);

		if(x > 0) {
			manageCandidate(currentPosition-1);
		}
		if(x < numXMinusOne) {
			manageCandidate(currentPosition+1);
		}
		if(y > 0) {
			manageCandidate(currentPosition-numX);
		}
		if(y < numYMinusOne) {
			manageCandidate(currentPosition+numX);
		}
		if(z > 0) {
			manageCandidate(currentPosition - numXnumY);
		}
		if(z < numZMinusOne) {
			manageCandidate(currentPosition + numXnumY);
		}
	}

    private void manageCandidate(int candidatePosition) {
		if(state[candidatePosition] == STATE_FROZEN) {
			return;				// distance already computed, nothing more to do
		}
		if(state[candidatePosition] == STATE_COLD) {
			return;				// we ignore cold points, nothing to do
		}

		double candidateDistance = computeDistanceFast(candidatePosition);

		if(state[candidatePosition] == STATE_NARROW) {
			narrowBand.decreaseKey(heapIndex[candidatePosition], candidateDistance);	// already part of narrow band, decrease key if possible
		} else {
			state[candidatePosition] = STATE_NARROW;
			distanceMap[candidatePosition] = candidateDistance; 
			narrowBand.insert(candidatePosition);
		}
	}

	private double computeDistanceFast(int candidatePosition) {
    	int z = (int)(candidatePosition/(numXnumY));
    	int tmp1 = candidatePosition - z*numXnumY;
    	int x = (int)(tmp1%numX);
    	int y = (int)(tmp1/numX);

		double a = 0;		// coefficients for the quadratic equation
		double b = 0;
		double c = -1;

		double val1;
		double val2;
		// ==================== X direction =======================
		val1 = MAX_NUMBER;
		val2 = MAX_NUMBER;
		if (x >= 2) {					// X minus -------------- second order
    		int nP = candidatePosition - 1;
    		int dP = candidatePosition - 2;
			if(state[nP] == STATE_FROZEN) {	// the only distances we know are for the frozen cells
				double distance1 = distanceMap[nP];
				if(distance1 < val1) {
					val1 = distance1;
					if(state[dP] == STATE_FROZEN) {
						double distance2 = distanceMap[dP];
						if(distance2 <= distance1) {
							val2 = distance2;
						}
					}
				}
			}
		} else if (x == 1) {		// X minus --------------- first order
       		int nP = candidatePosition - 1;
			if(state[nP] == STATE_FROZEN) {
				double distance1 = distanceMap[nP];
				if(distance1 < val1) {
					val1 = distance1;
				}
			}
    	}
		if (x < numXMinusTwo) {			// X plus ------------------ second order
    		int nP = candidatePosition + 1;
    		int dP =  candidatePosition + 2;
			if(state[nP] == STATE_FROZEN) {
				double distance1 = distanceMap[nP];
				if(distance1 < val1) {
					val1 = distance1;
					if(state[dP] == STATE_FROZEN) {
						double distance2 = distanceMap[dP];
						if(distance2 <= distance1) {
							val2 = distance2;
						} else {
							val2 = MAX_NUMBER;
						}
					} else {
						val2 = MAX_NUMBER;
					}
				}
			}
		} else if (x < numXMinusOne) {	// X plus ---------------- first order
			
      		int nP = candidatePosition + 1;
			if(state[nP] == STATE_FROZEN) {
				double distance1 = distanceMap[nP];
				if(distance1 < val1) {
					val1 = distance1;
					val2 = MAX_NUMBER;
				}
			}
    	}
		// update a,b,c for X direction
		if(val2 != MAX_NUMBER) {
			double tp = tpConst*(4*val1-val2);
			double aatp = aaConst*tp;
			a += aaConst * sqrInvDeltaX;
			b -= 2 * aatp * sqrInvDeltaX;
			c+= aatp * tp * sqrInvDeltaX;
		} else 
		if(val1 != MAX_NUMBER) {
			a += sqrInvDeltaX;
			b -= 2 * val1 * sqrInvDeltaX;
			c += val1 * val1 * sqrInvDeltaX;
		}
		// ============================ Y direction ===================================
		val1 = MAX_NUMBER;
		val2 = MAX_NUMBER;
		if (y >= 2) {				// Y minus -------------- second order
    		int nP = candidatePosition - numX;
    		int dP = nP - numX;
			if(state[nP] == STATE_FROZEN) {
				double distance1 = distanceMap[nP];
				if(distance1 < val1) {
					val1 = distance1;
					if(state[dP] == STATE_FROZEN) {
						double distance2 = distanceMap[dP];
						if(distance2 <= distance1) {
							val2 = distance2;
						}
					}
				}
			}
		} else if (y == 1) {		// Y minus --------------- first order
       		int nP = candidatePosition - numX;
			if(state[nP] == STATE_FROZEN) {
				double distance1 = distanceMap[nP];
				if(distance1 < val1) {
					val1 = distance1;
				}
			}
    	}
		if (y < numYMinusTwo) {			// Y plus ------------------ second order
    		int nP = candidatePosition + numX;
    		int dP =  nP + numX;
			if(state[nP] == STATE_FROZEN) {
				double distance1 = distanceMap[nP];
				if(distance1 < val1) {
					val1 = distance1;
					if(state[dP] == STATE_FROZEN) {
						double distance2 = distanceMap[dP];
						if(distance2 <= distance1) {
							val2 = distance2;
						} else {
							val2 = MAX_NUMBER;
						}
					} else {
						val2 = MAX_NUMBER;
					}
				}
			}
		} else if (y < numYMinusOne) {	// Y plus ---------------- first order
      		int nP = candidatePosition + numX;
			if(state[nP] == STATE_FROZEN) {
				double distance1 = distanceMap[nP];
				if(distance1 < val1) {
					val1 = distance1;
					val2 = MAX_NUMBER;
				}
			}
    	}
		// update a,b,c for Y direction
		if(val2 != MAX_NUMBER) {
			double tp = tpConst*(4*val1-val2);
			double aatp = aaConst*tp;
			a += aaConst * sqrInvDeltaY;
			b -= 2 * aatp * sqrInvDeltaY;
			c += aatp * tp * sqrInvDeltaY;
		} else 
		if(val1 != MAX_NUMBER) {
			a += sqrInvDeltaY;
			b -= 2 * val1 * sqrInvDeltaY;
			c += val1 * val1 * sqrInvDeltaY;
		}
		
		// ============================== Z direction =======================================
		val1 = MAX_NUMBER;
		val2 = MAX_NUMBER;
		if (z >= 2) {					// Z minus -------------- second order
    		int nP = candidatePosition - numXnumY;
    		int dP = nP - numXnumY;
			if(state[nP] == STATE_FROZEN) {
				double distance1 = distanceMap[nP];
				if(distance1 < val1) {
					val1 = distance1;
					if(state[dP] == STATE_FROZEN) {
						double distance2 = distanceMap[dP];
						if(distance2 <= distance1) {
							val2 = distance2;
						}
					}
				}
			}
		} else if (z == 1) {		// Z minus --------------- first order
       		int nP = candidatePosition - numXnumY;
			if(state[nP] == STATE_FROZEN) {
				double distance1 = distanceMap[nP];
				if(distance1 < val1) {
					val1 = distance1;
				}
			}
    	}
		if (z < numZMinusTwo) {			// Z plus ------------------ second order
    		int nP = candidatePosition + numXnumY;
    		int dP =  nP + numXnumY;
			if(state[nP] == STATE_FROZEN) {
				double distance1 = distanceMap[nP];
				if(distance1 < val1) {
					val1 = distance1;
					if(state[dP] == STATE_FROZEN) {
						double distance2 = distanceMap[dP];
						if(distance2 <= distance1) {
							val2 = distance2;
						} else {
							val2 = MAX_NUMBER;
						}
					} else {
						val2 = MAX_NUMBER;
					}
				}
			}
		} else if (z < numZMinusOne) {	// Z plus ---------------- first order
			
      		int nP = candidatePosition + numXnumY;
			if(state[nP] == STATE_FROZEN) {
				double distance1 = distanceMap[nP];
				if(distance1 < val1) {
					val1 = distance1;
					val2 = MAX_NUMBER;
				}
			}
    	}
		// update a,b,c for Z direction
		// use low accuracy on Z axis if distance between pixels is different from the distance on X, Y
		if(val2 != MAX_NUMBER && !differentZDistance) {
			double tp = tpConst*(4*val1-val2);
			double aatp = aaConst*tp;
			a += aaConst * sqrInvDeltaZ;
			b -= 2 * aatp * sqrInvDeltaZ;
			c+= aatp * tp * sqrInvDeltaZ;
		} else 
		if(val1 != MAX_NUMBER) {
			a += sqrInvDeltaZ;
			b -= 2 * val1 * sqrInvDeltaZ;
			c += val1 * val1 * sqrInvDeltaZ;
		}
		return solveQuadratic(a, b, c);
	}

	private int initializeFrozen() {
		int frozenCount = 0;
		int index = 0;
		for(int z=0; z<numZ; z++) {
			for(int y=0; y<numY; y++) {
				for(int x=0; x<numX; x++) {
					index = x + y*numX + z*numXnumY;
					if(state[index] == STATE_COLD) {
						continue;
					}
					double distance = distanceMap[index];
					if(distance < MAX_NUMBER) {
						state[index] = STATE_FROZEN;
						frozenCount++;
					}
				}
			}
		}
		return frozenCount;
	}

	// index = x + y*numX + z*numXnumY;
	private int getX(int position) {
    	int z = (int)(position/(numXnumY));
    	int tmp1 = position - z*numXnumY;
    	int x = (int)(tmp1%numX);
    	return x;
	}
	private int getY(int position) {
    	int z = (int)(position/(numXnumY));
    	int tmp1 = position - z*numXnumY;
    	int y = (int)(tmp1/numX);
    	return y;
	}
	private int getZ(int position) {
    	int z = (int)(position/(numXnumY));
    	return z;
	}
    
    // Quadratic Equation Solver   a.x^2 + b.x + c = 0
    private double solveQuadratic(double a, double b, double c) {
    	
    	if ( a==0 && b==0 ) {			// cannot solve c=0
    		throw new RuntimeException("Unable to solve ax2+bx+c=0, a=" + a + " b=" + b + " c=" + c);
    	} 
    	if ( a==0 && b!=0 ) {
    		return -c/b;				// degenerate roots
    	}
    	double discriminant = b*b-4.0*a*c;		// discriminant of quadratic equation
    	// TODO: add "if discriminant between -epsilon and +epsilon we still have a -b/2/a root"
		double aTimesTwo = 2*a;
    	if ( discriminant >= 0 ) {		// two real roots
    		double d = Math.sqrt(discriminant)/aTimesTwo;
    		double e = -b/aTimesTwo;
			double r1 = e-d;
    		double r2 = e+d;
    		return Math.max(r1, r2);
    	} else {						// complex roots
    		return -b/aTimesTwo;
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
		distanceMap[3] =  1.4142135;			// sqrt(2) - diagonal of a square
		distanceMap[4] = 1;
		distanceMap[5] =  1.4142135;
		distanceMap[6] =  1.4142135;
		distanceMap[7] =  1.7320508;			// sqrt(3) - diagonal of a cube
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

		FastMarchingMethodHA fmm = new FastMarchingMethodHA(numX, numY, numZ, distanceMap, null);
		
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
		long length = fmm.numX*fmm.numY*fmm.numZ;
		for(index=0; index<length; index++) {
			System.out.println(index + ": " + distanceMap[index]);
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
		
		fmm = new FastMarchingMethodHA(numX, numY, numZ, distanceMap, null);
		
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
		
		fmm = new FastMarchingMethodHA(numX, numY, numZ, distanceMap, null);
		fmm.march();

		
		final int numIterations = 500000;
		double errorThreshold = 0.51;
		int errorCount = 0;
		Random rand = new Random();
		
		try {
		BufferedWriter out = new BufferedWriter(new FileWriter("c:\\TEMP\\FFM.3D"));
		out.write("x y z value\n");

		for(int j=0; j<numIterations; j++) {
			x = (int)(rand.nextDouble()*100);
			y = (int)(rand.nextDouble()*100);
			z = (int)(rand.nextDouble()*100);
			i = x + y*numX + z*numX*numY;
			p = new Vect3d(x, y, z);
			d1 = DistanceMapGenerator.distanceToTriangle3d(p, A, B, C);
			double delta = Math.abs(d1-distanceMap[i]);

			String line = new String(x + " " + y + " " + z + " " + (int)(delta*100) + "\n");
			out.write(line);

			if(delta > errorThreshold) {
				System.out.println(x + ", " + y + ", " + z + " - delta: " + delta);
				errorCount++;
			}
		}
		out.close();
		System.out.println("Delta above threshold " + errorThreshold + " occured in " + errorCount + " cases out of " + numIterations);
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("A few random examples: ffm distance vs. exact distance");
		for(int j=0; j<5; j++) {
			x = (int)(1 + rand.nextDouble()*98);
			y = (int)(1 + rand.nextDouble()*98);
			z = (int)(1 + rand.nextDouble()*98);
			i = x + y*numX + z*numX*numY;
			p = new Vect3d(x, y, z);
			d1 = DistanceMapGenerator.distanceToTriangle3d(p, A, B, C);
			System.out.println(x + ", " + y + ", " + z + " - ffm dist: " + distanceMap[i] + ",   exact dist: " + d1);
		}

		System.out.println("");
		System.out.println(" -------------------- TEST 4 - negative entries ----------------------- ");
		
		p = null;
		d1 = 0;

		numX = 10;
		numY = 10;
		numZ = 10;
		numItems = numX*numY*numZ;

		distanceMap = new double[numItems];
		Arrays.fill(distanceMap, MAX_NUMBER);
		
		Node AA = new Node(0.501, 0.5, 0.5);
		Node BB = new Node(0.5, 0.501, 0.5);
		Node CC = new Node(0.5, 0.5, 0.501);
		
		A = new Vect3d(AA);
		B = new Vect3d(BB);
		C = new Vect3d(CC);
		
		distanceMap[0] = -0.86602540378443864676372317075294;
		distanceMap[1] = 0.86602540378443864676372317075294;
		distanceMap[10] = 0.86602540378443864676372317075294;
		distanceMap[11] = 0.86602540378443864676372317075294;
		distanceMap[100] = 0.86602540378443864676372317075294;
		distanceMap[101] = 0.86602540378443864676372317075294;
		distanceMap[110] = 0.86602540378443864676372317075294;
		distanceMap[111] = 0.86602540378443864676372317075294;
		
		fmm = new FastMarchingMethodHA(numX, numY, numZ, distanceMap, null);
		fmm.march();

		x=5;
		y=5;
		z=5;
		i = x + y*numX + z*numX*numY;
		p = new Vect3d(x, y, z);
		Node pp = new Node(x, y, z);
		d1 = DistanceMapGenerator.distanceToTriangle3d(p, A, B, C);
		double d2 = DistanceMapGenerator.distanceToTriangleExperimental(pp, AA, BB, CC, "c:\\TEMP\\triangle.3D");
		System.out.println(x + ", " + y + ", " + z + " - ffm dist: " + distanceMap[i] + ",   exact dist: " + d1 +
				",   exper dist: " + d2	);
		
		
		x=2;
		y=1;
		z=0;
		i = x + y*numX + z*numX*numY;
		p = new Vect3d(x, y, z);
		pp = new Node(x, y, z);
		d1 = DistanceMapGenerator.distanceToTriangle3d(p, A, B, C);
		d2 = DistanceMapGenerator.distanceToTriangleExperimental(pp, AA, BB, CC, "c:\\TEMP\\triangle.3D");
		System.out.println(x + ", " + y + ", " + z + " - ffm dist: " + distanceMap[i] + ",   exact dist: " + d1 +
				",   exper dist: " + d2);


		
		System.out.println("");
		System.out.println(" -------------------- TEST 5 ----------------------- ");
		
		p = null;
		d1 = 0;

		numX = 10;
		numY = 10;
		numZ = 10;
		numItems = numX*numY*numZ;

		distanceMap = new double[numItems];
		Arrays.fill(distanceMap, MAX_NUMBER);
		
		AA = new Node(0.01, 0, 0);
		BB = new Node(0, 0.01, 0);
		CC = new Node(0, 0, 0.01);
		
		A = new Vect3d(AA);
		B = new Vect3d(BB);
		C = new Vect3d(CC);
		
		distanceMap[0] = 0;
		distanceMap[1] = 1;
		distanceMap[10] = 1;
		distanceMap[11] = 1.4142135623730950488016887242097;
		distanceMap[100] = 1;
		distanceMap[101] = 1.4142135623730950488016887242097;
		distanceMap[110] = 1.4142135623730950488016887242097;
		distanceMap[111] = 1.7320508075688772935274463415059;
		
		fmm = new FastMarchingMethodHA(numX, numY, numZ, distanceMap, null);
		fmm.march();

		x=5;
		y=5;
		z=5;
		i = x + y*numX + z*numX*numY;
		p = new Vect3d(x, y, z);
		pp = new Node(x, y, z);
		d1 = DistanceMapGenerator.distanceToTriangle3d(p, A, B, C);
		d2 = DistanceMapGenerator.distanceToTriangleExperimental(pp, AA, BB, CC, "c:\\TEMP\\triangle.3D");
		System.out.println(x + ", " + y + ", " + z + " - ffm dist: " + distanceMap[i] + ",   exact dist: " + d1 +
				",   exper dist: " + d2	);
		
		
		x=2;
		y=1;
		z=0;
		i = x + y*numX + z*numX*numY;
		p = new Vect3d(x, y, z);
		pp = new Node(x, y, z);
		d1 = DistanceMapGenerator.distanceToTriangle3d(p, A, B, C);
		d2 = DistanceMapGenerator.distanceToTriangleExperimental(pp, AA, BB, CC, "c:\\TEMP\\triangle.3D");
		System.out.println(x + ", " + y + ", " + z + " - ffm dist: " + distanceMap[i] + ",   exact dist: " + d1 +
				",   exper dist: " + d2);

		
		System.out.println("");
		System.out.println(" -------------------- TEST 6 - narrow vertical column ---------------------- ");

		p = null;
		d1 = 0;

		numX = 2;
		numY = 2;
		numZ = 10;
		numItems = numX*numY*numZ;

		distanceMap = new double[numItems];
		Arrays.fill(distanceMap, MAX_NUMBER);
		
		distanceMap[0] = -0.3;
		distanceMap[1] = -0.3;
		distanceMap[2] = -0.3;
		distanceMap[3] = -0.3;
		distanceMap[4] = 0.7;
		distanceMap[5] = 0.7;
		distanceMap[6] = 0.7;
		distanceMap[7] = 0.7;
		
		fmm = new FastMarchingMethodHA(numX, numY, numZ, distanceMap, null);
		fmm.march();

		for(int j=8; j<numItems; j++) {
			System.out.println(j + ":  " + distanceMap[j]);
		}

		System.out.println("");
		System.out.println(" -------------------- TEST 7 - 2D test, numZ must be 1 ---------------------- ");

		p = null;
		d1 = 0;

		numX = 500;
		numY = 500;
		numZ = 1;
		numItems = numX*numY*numZ;

		distanceMap = new double[numItems];
		Arrays.fill(distanceMap, MAX_NUMBER);
		
		x=0;
		y=0;
		double radius = 200;
		for(int alpha=0; alpha<360; alpha++) {
			double rad = Math.PI*(double)alpha/180.0;
			x = (int)(250.0+radius*Math.cos(rad));
			y = (int)(250.0+radius*Math.sin(rad));
			i = x + y*numX;
			distanceMap[i] = 0;
		}
		try {		// save some points in a VisIt compatible format
			BufferedWriter out = new BufferedWriter(new FileWriter("c:\\TEMP\\FFM2D1" + ".3D"));
			out.write("x y z value\n");
			
			for(int j=0; j<distanceMap.length; j++) {
				x = DistanceMapGenerator.getX(j, numX, numY);
				y = DistanceMapGenerator.getY(j, numX, numY);
				z = DistanceMapGenerator.getZ(j, numX, numY);
				if(distanceMap[j] < MAX_NUMBER) {
						out.write(x + " " + y + " " + z + " " + (int)(distanceMap[j]*10) + "\n");
				} 
			}
			out.close();
			} catch (IOException e) {
			}		
		
		fmm = new FastMarchingMethodHA(numX, numY, numZ, distanceMap, null);
		fmm.march();

		
		try {		// save some points in a VisIt compatible format
		BufferedWriter out = new BufferedWriter(new FileWriter("c:\\TEMP\\FFM2D2" + ".3D"));
		out.write("x y z value\n");
		
		for(int j=0; j<distanceMap.length; j++) {
			x = DistanceMapGenerator.getX(j, numX, numY);
			y = DistanceMapGenerator.getY(j, numX, numY);
			z = DistanceMapGenerator.getZ(j, numX, numY);
			if(distanceMap[j] < MAX_NUMBER) {
					out.write(x + " " + y + " " + z + " " + (int)(distanceMap[j]*10) + "\n");
			} 
		}
		out.close();
		} catch (IOException e) {
		}	
		
		System.out.println("");
		System.out.println(" -------------------- TEST 8 - anisotropic ---------------------- ");
		
		numX = 2;
		numY = 2;
		numZ = 5;
		numItems = numX*numY*numZ;

		distanceMap = new double[numItems];
		Arrays.fill(distanceMap, MAX_NUMBER);
		distanceMap[0] = 0;		// initial condition (exact distances from origin)
		distanceMap[1] = 1;
		distanceMap[2] = 1;
		distanceMap[3] = 1.4142135623730950488016887242097;			// sqrt(2) - diagonal of a square
		distanceMap[4] = 2;
		distanceMap[5] = 2.2360679774997896964091736687313;			// sqrt(5)
		distanceMap[6] = 2.2360679774997896964091736687313;	
		distanceMap[7] = 2.4494897427831780981972840747059;			// sqrt(6)
		// ----- next points distances for the above initial conditions
		// 			FMM-plain			FMM-HA				exact computed:
		//  8:		4.00000000			N/A					4.00000000
		//  9:		4.19691079								4.12310562		// sqrt(17)
		// 10:
		// 11:		4.38072927								4.24264068
		
		// 12:		6.00000000								6.00000000
		// 13:		6.16836145								6.08276253		// sqrt(37)
		// 14:
		// 15:		6.32866014								6.16441400		// sqrt(16+2)
		
		fmm = new FastMarchingMethodHA(numX, numY, numZ, 1, 1, 2, distanceMap, null);
		
		System.out.println("Verifying indexes manipulation; index = x + y*numX + z*numX*numY");

		fmm.march();

		length = fmm.numX*fmm.numY*fmm.numZ;
		for(index=0; index<length; index++) {
			System.out.println(index + ": " + distanceMap[index]);
		}


		System.out.println("");
		System.out.println(" -------------------- TEST 9 - LONG test ---------------------- ");
		
		numX = 300;
		numY = 300;
		numZ = 300;
		numItems = numX*numY*numZ;

		distanceMap = new double[numItems];
		Arrays.fill(distanceMap, MAX_NUMBER);
		
		distanceMap[0] = 0;
		distanceMap[1] = 1;
		distanceMap[300] = 1;
		distanceMap[301] = 1.4142135623730950488016887242097;
		distanceMap[90000] = 1;
		distanceMap[90001] = 1.4142135623730950488016887242097;
		distanceMap[90300] = 1.4142135623730950488016887242097;
		distanceMap[90301] = 1.7320508075688772935274463415059;
		
		fmm = new FastMarchingMethodHA(numX, numY, numZ, distanceMap, null);
		fmm.march();


		
		
		System.out.println("Done");
	}
}




