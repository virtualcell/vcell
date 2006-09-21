package cbit.vcell.simdata;

import cbit.util.Coordinate;
import cbit.util.CoordinateIndex;
import cbit.vcell.mesh.CartesianMesh;

public class DataResampler {

	/**
	 * Insert the method's description here.
	 * Creation date: (10/27/2003 5:07:42 PM)
	 * @return double[]
	 * @param data double[]
	 * @param sourceMesh cbit.vcell.solvers.CartesianMesh
	 * @param targetMesh cbit.vcell.solvers.CartesianMesh
	 */
	public static double[] resample1DSpatial(double[] sourceData, CartesianMesh sourceMesh, CartesianMesh refMesh) {
		if (sourceData.length!=sourceMesh.getNumVolumeElements()){
			throw new RuntimeException("must be volume data, data length doesn't match number of volume elements");
		}
		// for volume samples:
		//
		//  loop through volumeIndexes from refMesh
		//      Coordinate refCoordinate = refMesh.getCoordinate(volumeIndex);
		//          Coordinate fractionalIndex = sourceMesh.getFractionCoordinateIndex(Coordinate refCoordinate);
		//              ....interpolate in z
		//              start with integer portion of fractionIndex
	
		double resampledData[] = new double[refMesh.getSizeX()];
		for (int i = 0; i < resampledData.length; i++) {
			Coordinate refCoordinate = refMesh.getCoordinateFromVolumeIndex(i);
			Coordinate fractionalIndex = sourceMesh.getFractionalCoordinateIndex(refCoordinate);
			int ceil_x;
			int floor_x;
	
			if (fractionalIndex.getX() < 0) {
				floor_x = 0;			
				ceil_x = 1;
			} else if (fractionalIndex.getX() > sourceMesh.getSizeX()-1) {
				floor_x = sourceMesh.getSizeX()-2;
				ceil_x =  sourceMesh.getSizeX()-1;
			} else {
				ceil_x = (int)Math.ceil(fractionalIndex.getX());
				floor_x = (int)Math.floor(fractionalIndex.getX());
			}
			
			double fract_x = fractionalIndex.getX() - floor_x;
			
			CoordinateIndex coord_1 = new CoordinateIndex(floor_x, 0, 0);  // ***** SHOULD Y,Z BE 0 OR 1 ???? *****
			CoordinateIndex coord_2 = new CoordinateIndex(ceil_x, 0, 0);
			
			int volIndx1 = sourceMesh.getVolumeIndex(coord_1);
			int volIndx2 = sourceMesh.getVolumeIndex(coord_2);
	
			boolean bBoth1_2 = false;
			boolean bNoneOf1_2 = false;
			boolean bOne = false;
			boolean bTwo = false;
			boolean bOneOf1_2 = false;		
			
			// Get data values from 'data' using vol_indices.
			// use it to compute a, b, 	 ** Interpolation in X **
			if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) == refMesh.getSubVolumeFromVolumeIndex(i) && 
				sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) != refMesh.getSubVolumeFromVolumeIndex(i)) {
				bOneOf1_2 = true;
				bOne = true;
				volIndx2 = volIndx1;
			} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) != refMesh.getSubVolumeFromVolumeIndex(i) && 
						sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) == refMesh.getSubVolumeFromVolumeIndex(i)) {
				bOneOf1_2 = true;
				bTwo = true;
				volIndx1 = volIndx2;
			} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) != refMesh.getSubVolumeFromVolumeIndex(i) &&
					sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) != refMesh.getSubVolumeFromVolumeIndex(i)) {	
				// throw new RuntimeException("Either subvolume in sourceMesh not equal to refMesh subVol cannot be handled at this time!");
				bNoneOf1_2 = true;
			} else {
				bBoth1_2 = true;
			}
					
			// Interpolate in X - final resampledSourceData value
			if (bBoth1_2) {
				resampledData[i] = sourceData[volIndx1] + fract_x*(sourceData[volIndx2] - sourceData[volIndx1]);
			}
			
			if (bOneOf1_2) {
				if (bOne) {
					resampledData[i] = sourceData[volIndx1];
				} else if (bTwo) {
					resampledData[i] = sourceData[volIndx2];
				}
			}
			// Can there be a situation where bNoneOf1_2 is true, ie, both points are not 
			// in the specific subvolume of refMesh?? If so, what is the value of resampledData in sourceMesh?
			// Should we take it as the value of data at the point closest to this point within the specific region
			// in refMesh? If so, how does one find such a point?
			//
			if (bNoneOf1_2) {
				// resampledData[i] = BLAH_BLAH;
				throw new RuntimeException("Cannot handle the case where both points are not in ref subvolume");
			}		
		}
		return resampledData;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/27/2003 5:07:42 PM)
	 * @return double[]
	 * @param data double[]
	 * @param sourceMesh cbit.vcell.solvers.CartesianMesh
	 * @param targetMesh cbit.vcell.solvers.CartesianMesh
	 */
	public static double[] resample2DSpatial(double[] sourceData, CartesianMesh sourceMesh, CartesianMesh refMesh) {
		if (sourceData.length!=sourceMesh.getNumVolumeElements()){
			throw new RuntimeException("must be volume data, data length doesn't match number of volume elements");
		}
		// for volume samples:
		//
		//  loop through volumeIndexes from refMesh
		//      Coordinate refCoordinate = refMesh.getCoordinate(volumeIndex);
		//          Coordinate fractionalIndex = sourceMesh.getFractionCoordinateIndex(Coordinate refCoordinate);
		//              ....interpolate in y
		//              start with integer portion of fractionIndex
	
		double resampledData[] = new double[refMesh.getSizeX()*refMesh.getSizeY()*refMesh.getSizeZ()];
		for (int i = 0; i < resampledData.length; i++) {
			Coordinate refCoordinate = refMesh.getCoordinateFromVolumeIndex(i);
			Coordinate fractionalIndex = sourceMesh.getFractionalCoordinateIndex(refCoordinate);
			int ceil_x;
			int floor_x;
			int ceil_y;
			int floor_y;
	
			if (fractionalIndex.getX() < 0) {
				floor_x = 0;			
				ceil_x = 1;
			} else if (fractionalIndex.getX() > sourceMesh.getSizeX()-1) {
				floor_x = sourceMesh.getSizeX()-2;
				ceil_x =  sourceMesh.getSizeX()-1;
			} else {
				ceil_x = (int)Math.ceil(fractionalIndex.getX());
				floor_x = (int)Math.floor(fractionalIndex.getX());
			}
			if (fractionalIndex.getY() < 0) {
				floor_y = 0;			
				ceil_y = 1;
			} else if (fractionalIndex.getY() > sourceMesh.getSizeY()-1) {
				floor_y = sourceMesh.getSizeY()-2;
				ceil_y =  sourceMesh.getSizeY()-1;
			} else {
				ceil_y = (int)Math.ceil(fractionalIndex.getY());
				floor_y = (int)Math.floor(fractionalIndex.getY());
			}		
			
			double fract_x = fractionalIndex.getX() - floor_x;
			double fract_y = fractionalIndex.getY() - floor_y;
			
			CoordinateIndex coord_1 = new CoordinateIndex(floor_x, floor_y, 0);  // ***** SHOULD THIS BE 0 OR 1 ???? *****
			CoordinateIndex coord_2 = new CoordinateIndex(ceil_x, floor_y, 0);
			CoordinateIndex coord_3 = new CoordinateIndex(floor_x, ceil_y, 0);
			CoordinateIndex coord_4 = new CoordinateIndex(ceil_x, ceil_y, 0);
			
			int volIndx1 = sourceMesh.getVolumeIndex(coord_1);
			int volIndx2 = sourceMesh.getVolumeIndex(coord_2);
			int volIndx3 = sourceMesh.getVolumeIndex(coord_3);
			int volIndx4 = sourceMesh.getVolumeIndex(coord_4);
	
			boolean bBoth1_2 = false;
			boolean bNoneOf1_2 = false;
			boolean bOne = false;
			boolean bTwo = false;
			boolean bOneOf1_2 = false;
	
			boolean bBoth3_4 = false;
			boolean bNoneOf3_4 = false;
			boolean bThree = false;
			boolean bFour = false;
			boolean bOneOf3_4 = false;
				
			// Get data values from 'data' using vol_indices.
			// use it to compute a, b, 	 ** Interpolation in X **
			if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) == refMesh.getSubVolumeFromVolumeIndex(i) && 
				sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) != refMesh.getSubVolumeFromVolumeIndex(i)) {
				bOneOf1_2 = true;
				bOne = true;
				volIndx2 = volIndx1;
			} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) != refMesh.getSubVolumeFromVolumeIndex(i) && 
						sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) == refMesh.getSubVolumeFromVolumeIndex(i)) {
				bOneOf1_2 = true;
				bTwo = true;
				volIndx1 = volIndx2;
			} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) != refMesh.getSubVolumeFromVolumeIndex(i) &&
					sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) != refMesh.getSubVolumeFromVolumeIndex(i)) {	
				// throw new RuntimeException("Either subvolume in sourceMesh not equal to refMesh subVol cannot be handled at this time!");
				bNoneOf1_2 = true;
			} else {
				bBoth1_2 = true;
			}
					
			if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx3) == refMesh.getSubVolumeFromVolumeIndex(i) && 
				sourceMesh.getSubVolumeFromVolumeIndex(volIndx4) != refMesh.getSubVolumeFromVolumeIndex(i)) {
				bOneOf3_4 = true;
				bThree = true;
				volIndx4 = volIndx3;
			} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx3) != refMesh.getSubVolumeFromVolumeIndex(i) && 
						sourceMesh.getSubVolumeFromVolumeIndex(volIndx4) == refMesh.getSubVolumeFromVolumeIndex(i)) {
				bOneOf3_4 = true;
				bFour = true;
				volIndx3 = volIndx4;
			} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx3) != refMesh.getSubVolumeFromVolumeIndex(i) &&
				sourceMesh.getSubVolumeFromVolumeIndex(volIndx4) != refMesh.getSubVolumeFromVolumeIndex(i)) {
				// throw new RuntimeException("Either subvolume in sourceMesh not equal to refMesh subVol cannot be handled at this time!");
				bNoneOf3_4 = true;
			} else {
				bBoth3_4 = true;
			}
	
			// First order interpolation if 4, 3, 2 points are within the ref subVolume.	
			if ( (bBoth1_2 && bBoth3_4) || (bBoth1_2 && bOneOf3_4) || (bOneOf1_2 && bBoth3_4) || (bOneOf1_2 && bOneOf3_4) ) {
				// Interpolate in X (first order)
				double val_a = sourceData[volIndx1] + fract_x*(sourceData[volIndx2] - sourceData[volIndx1]);
				double val_b = sourceData[volIndx3] + fract_x*(sourceData[volIndx4] - sourceData[volIndx3]);
	
				// Interpolate in Y - final resampledSourceData value
				resampledData[i] = val_a + fract_y*(val_b - val_a);
			}
	
			// 0th order interpolation if only one point is in ref subVol.
			if ( (bBoth1_2 && bNoneOf3_4) ) {
				resampledData[i] = sourceData[volIndx1] + fract_x*(sourceData[volIndx2] - sourceData[volIndx1]);
			}
			if ( (bNoneOf1_2 && bBoth3_4) ) {
				resampledData[i] = sourceData[volIndx3] + fract_x*(sourceData[volIndx4] - sourceData[volIndx3]);
			}
				
			if ( (bOneOf1_2 && bNoneOf3_4) ) {
				if (bOne) {
					resampledData[i] = sourceData[volIndx1];
				} else if (bTwo) {
					resampledData[i] = sourceData[volIndx2];
				}
			}
	
			if ( (bNoneOf1_2 && bOneOf3_4) ) {
				if (bThree) {
					resampledData[i] = sourceData[volIndx3];
				} else if (bFour) {
					resampledData[i] = sourceData[volIndx4];
				}
			}
			//
			// Can there be a situation where bNoneOf1_2 && bNoneOf3_4 are both true, ie, all four points are not 
			// in the specific subvolume of refMesh?? If so, what is the value of resampledData in sourceMesh?
			// Should we take it as the value of data at the point closest to this point within the specific region
			// in refMesh? If so, how does one find such a point?
			//
			if (bNoneOf1_2 && bNoneOf3_4) {
				// resampledData[i] = BLAH_BLAH;
				throw new RuntimeException("Cannot handle the case where all 4 points are not in ref subvolume1");
			}
		}
		return resampledData;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/27/2003 5:07:42 PM)
	 * @return double[]
	 * @param data double[]
	 * @param sourceMesh cbit.vcell.solvers.CartesianMesh
	 * @param targetMesh cbit.vcell.solvers.CartesianMesh
	 */
	public static double[] resample3DSpatial(double[] sourceData, CartesianMesh sourceMesh, CartesianMesh refMesh) {
		if (sourceData.length!=sourceMesh.getNumVolumeElements()){
			throw new RuntimeException("must be volume data, data length doesn't match number of volume elements");
		}
		// for volume samples:
		//
		//  loop through volumeIndexes from refMesh
		//      Coordinate refCoordinate = refMesh.getCoordinate(volumeIndex);
		//          Coordinate fractionalIndex = sourceMesh.getFractionCoordinateIndex(Coordinate refCoordinate);
		//              ....interpolate in z
		//              start with integer portion of fractionIndex
	
		double resampledData[] = new double[refMesh.getSizeX()*refMesh.getSizeY()*refMesh.getSizeZ()];
		for (int i = 0; i < resampledData.length; i++) {
			Coordinate refCoordinate = refMesh.getCoordinateFromVolumeIndex(i);
			Coordinate fractionalIndex = sourceMesh.getFractionalCoordinateIndex(refCoordinate);
			int ceil_x;
			int floor_x;
			int ceil_y;
			int floor_y;
			int ceil_z;
			int floor_z;			
	
			if (fractionalIndex.getX() < 0) {
				floor_x = 0;			
				ceil_x = 1;
			} else if (fractionalIndex.getX() > sourceMesh.getSizeX()-1) {
				floor_x = sourceMesh.getSizeX()-2;
				ceil_x =  sourceMesh.getSizeX()-1;
			} else {
				ceil_x = (int)Math.ceil(fractionalIndex.getX());
				floor_x = (int)Math.floor(fractionalIndex.getX());
			}
			if (fractionalIndex.getY() < 0) {
				floor_y = 0;			
				ceil_y = 1;
			} else if (fractionalIndex.getY() > sourceMesh.getSizeY()-1) {
				floor_y = sourceMesh.getSizeY()-2;
				ceil_y =  sourceMesh.getSizeY()-1;
			} else {
				ceil_y = (int)Math.ceil(fractionalIndex.getY());
				floor_y = (int)Math.floor(fractionalIndex.getY());
			}
			if (fractionalIndex.getZ() < 0) {
				floor_z = 0;			
				ceil_z = 1;
			} else if (fractionalIndex.getZ() > sourceMesh.getSizeZ()-1) {
				floor_z = sourceMesh.getSizeZ()-2;
				ceil_z =  sourceMesh.getSizeZ()-1;
			} else {
				ceil_z = (int)Math.ceil(fractionalIndex.getZ());
				floor_z = (int)Math.floor(fractionalIndex.getZ());
			}			
	
			double fract_x = fractionalIndex.getX() - floor_x;
			double fract_y = fractionalIndex.getY() - floor_y;
			double fract_z = fractionalIndex.getZ() - floor_z;
	
			CoordinateIndex coord_1 = new CoordinateIndex(floor_x, floor_y, floor_z);
			CoordinateIndex coord_2 = new CoordinateIndex(ceil_x, floor_y, floor_z);
			CoordinateIndex coord_3 = new CoordinateIndex(floor_x, floor_y, ceil_z);
			CoordinateIndex coord_4 = new CoordinateIndex(ceil_x, floor_y, ceil_z);	
			CoordinateIndex coord_5 = new CoordinateIndex(floor_x, ceil_y, ceil_z);
			CoordinateIndex coord_6 = new CoordinateIndex(ceil_x, ceil_y, ceil_z);
			CoordinateIndex coord_7 = new CoordinateIndex(floor_x, ceil_y, floor_z);
			CoordinateIndex coord_8 = new CoordinateIndex(ceil_x, ceil_y, floor_z);
			
			int volIndx1 = sourceMesh.getVolumeIndex(coord_1);
			int volIndx2 = sourceMesh.getVolumeIndex(coord_2);
			int volIndx3 = sourceMesh.getVolumeIndex(coord_3);
			int volIndx4 = sourceMesh.getVolumeIndex(coord_4);
			int volIndx5 = sourceMesh.getVolumeIndex(coord_5);		
			int volIndx6 = sourceMesh.getVolumeIndex(coord_6);
			int volIndx7 = sourceMesh.getVolumeIndex(coord_7);
			int volIndx8 = sourceMesh.getVolumeIndex(coord_8);
			
			// Get data values from 'sourceData' using vol_indices.
			// use it to compute a, b, c, d, then e & f	 ** Interpolation in X **
			if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) == refMesh.getSubVolumeFromVolumeIndex(i) && 
				sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) != refMesh.getSubVolumeFromVolumeIndex(i)) {
				volIndx2 = volIndx1;
			} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) != refMesh.getSubVolumeFromVolumeIndex(i) && 
						sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) == refMesh.getSubVolumeFromVolumeIndex(i)) {
				volIndx1 = volIndx2;
			} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx1) != refMesh.getSubVolumeFromVolumeIndex(i) &&
					sourceMesh.getSubVolumeFromVolumeIndex(volIndx2) != refMesh.getSubVolumeFromVolumeIndex(i)) {	
				throw new RuntimeException("Either subvolume in sourceMesh not equal to refMesh subVol cannot be handled at this time!");
			}
			if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx3) == refMesh.getSubVolumeFromVolumeIndex(i) && 
				sourceMesh.getSubVolumeFromVolumeIndex(volIndx4) != refMesh.getSubVolumeFromVolumeIndex(i)) {
				volIndx4 = volIndx3;
			} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx3) != refMesh.getSubVolumeFromVolumeIndex(i) && 
						sourceMesh.getSubVolumeFromVolumeIndex(volIndx4) == refMesh.getSubVolumeFromVolumeIndex(i)) {
				volIndx3 = volIndx4;
			} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx3) != refMesh.getSubVolumeFromVolumeIndex(i) &&
				sourceMesh.getSubVolumeFromVolumeIndex(volIndx4) != refMesh.getSubVolumeFromVolumeIndex(i)) {
				throw new RuntimeException("Either subvolume in sourceMesh not equal to refMesh subVol cannot be handled at this time!");
			}
			if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx5) == refMesh.getSubVolumeFromVolumeIndex(i) && 
				sourceMesh.getSubVolumeFromVolumeIndex(volIndx6) != refMesh.getSubVolumeFromVolumeIndex(i)) {
				volIndx6 = volIndx5;
			} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx5) != refMesh.getSubVolumeFromVolumeIndex(i) && 
						sourceMesh.getSubVolumeFromVolumeIndex(volIndx6) == refMesh.getSubVolumeFromVolumeIndex(i)) {
				volIndx5 = volIndx6;
			} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx5) != refMesh.getSubVolumeFromVolumeIndex(i) &&
				sourceMesh.getSubVolumeFromVolumeIndex(volIndx6) != refMesh.getSubVolumeFromVolumeIndex(i)) {
				throw new RuntimeException("Either subvolume in sourceMesh not equal to refMesh subVol cannot be handled at this time!");
			}
			if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx7) == refMesh.getSubVolumeFromVolumeIndex(i) && 
				sourceMesh.getSubVolumeFromVolumeIndex(volIndx8) != refMesh.getSubVolumeFromVolumeIndex(i)) {
				volIndx8 = volIndx7;
			} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx7) != refMesh.getSubVolumeFromVolumeIndex(i) && 
						sourceMesh.getSubVolumeFromVolumeIndex(volIndx8) == refMesh.getSubVolumeFromVolumeIndex(i)) {
				volIndx7 = volIndx8;
			} else if (sourceMesh.getSubVolumeFromVolumeIndex(volIndx7) != refMesh.getSubVolumeFromVolumeIndex(i) &&
				sourceMesh.getSubVolumeFromVolumeIndex(volIndx8) != refMesh.getSubVolumeFromVolumeIndex(i)) {
				throw new RuntimeException("Either subvolume in sourceMesh not equal to refMesh subVol cannot be handled at this time!");
			}
			double val_a = sourceData[volIndx1] + fract_x*(sourceData[volIndx2] - sourceData[volIndx1]);
			double val_b = sourceData[volIndx3] + fract_x*(sourceData[volIndx4] - sourceData[volIndx3]);
			double val_c = sourceData[volIndx5] + fract_x*(sourceData[volIndx6] - sourceData[volIndx5]);
			double val_d = sourceData[volIndx7] + fract_x*(sourceData[volIndx8] - sourceData[volIndx7]);
	
			// Interpolate in Y
			double val_e = val_a + fract_y*(val_d - val_a);
			double val_f = val_b + fract_y*(val_c - val_b);
	
			// Interpolate in Z - final resampledSourceData value
			resampledData[i] = val_e + fract_z*(val_f - val_e);
		}
		return resampledData;
	}

}
