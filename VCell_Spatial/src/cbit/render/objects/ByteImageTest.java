package cbit.render.objects;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import org.vcell.util.Extent;
import org.vcell.util.ISize;
import org.vcell.util.Origin;


public class ByteImageTest {
	public static ByteImage getBoxInBox(ISize sampleSize, Extent extent, double sizeRatio) throws ImageException {
		final double boxLoX = (1.0-sizeRatio)/2.0*extent.getX();
		final double boxHiX = extent.getX()-boxLoX;
		final double boxLoY = (1.0-sizeRatio)/2.0*extent.getY();
		final double boxHiY = extent.getX()-boxLoY;
		final double boxLoZ = (1.0-sizeRatio)/2.0*extent.getZ();
		final double boxHiZ = extent.getX()-boxLoZ;
		
		BoundingBox bbox = new BoundingBox(0,extent.getX(),0,extent.getY(),0,extent.getZ());
		SpatialFunction function = new SpatialFunction() {
			public double evaluate(Vect3d vect){
				if ((vect.getX()>boxLoX)&&(vect.getX()<boxHiX)&&
					(vect.getY()>boxLoY)&&(vect.getY()<boxHiY)&&
					(vect.getZ()>boxLoZ)&&(vect.getZ()<boxHiZ)){
					return 1.0;
				}else{
					return 0.0;
				}
			}
		};
				
		AnalyticField field = new AnalyticField(function);
		return createSegmentedImage(sampleSize,bbox,new AnalyticField[] { field });
	}
	
	public static ByteImage getSphereInBox(ISize sampleSize, Extent extent, final double radius) throws ImageException {
		final double cx = extent.getX()/2.0;
		final double cy = extent.getY()/2.0;
		final double cz = extent.getZ()/2.0;
		
		BoundingBox bbox = new BoundingBox(0,extent.getX(),0,extent.getY(),0,extent.getZ());
		SpatialFunction function = new SpatialFunction(){
			public double evaluate(Vect3d v){
				double x = v.getX();
				double y = v.getY();
				double z = v.getZ();
				if ((x-cx)*(x-cx)+(y-cy)*(y-cy)+(z-cz)*(z-cz)<radius*radius){
					return 1.0;
				}else{
					return 0.0;
				}
			}
		};
		AnalyticField field = new AnalyticField(function);
		return createSegmentedImage(sampleSize,bbox,new AnalyticField[] { field });
	}
	
	public static ByteImage createSegmentedImage(ISize sampleSize, BoundingBox boundingBox, AnalyticField[] fields) throws ImageException {

		byte handles[] = new byte[sampleSize.getX()*sampleSize.getY()*sampleSize.getZ()];
		for (int i=0;i<handles.length;i++){
			handles[i] = 0; // background color
		}
		
		//
		// go through Fields
		//
		double ox = boundingBox.getLoX();
		double oy = boundingBox.getLoY();
		double oz = boundingBox.getLoZ();
		double sx = boundingBox.getHiX()-boundingBox.getLoX();
		double sy = boundingBox.getHiY()-boundingBox.getLoY();
		double sz = boundingBox.getHiZ()-boundingBox.getLoZ();
		int numX = sampleSize.getX();
		int numY = sampleSize.getY();
		int numZ = sampleSize.getZ();
		
		int displayIndex = 0;
		for (int k=0;k<sampleSize.getZ();k++){
			double unit_z = (numZ>1)?((double)k)/(numZ-1):0.5;
			double coordZ = oz + sz * unit_z;
			for (int j=0;j<sampleSize.getY();j++){
				double unit_y = (numY>1)?((double)j)/(numY-1):0.5;
				double coordY = oy + sy * unit_y;
				for (int i=0;i<sampleSize.getX();i++){
					double unit_x = (numX>1)?((double)i)/(numX-1):0.5;
					double coordX = ox + sx * unit_x;
					for (int index = 0; index < fields.length; index++) {
						if (fields[index].isInside(new Vect3d(coordX,coordY,coordZ))){
							handles[displayIndex] = (byte)((index+1)&0xff);
							break;
						}
					}
					displayIndex++;
				}
			}
		}

		return new ByteImage(handles,new Extent(sx,sy,sz), sampleSize.getX(),sampleSize.getY(),sampleSize.getZ());
	}
	//
	// field should be scaled from 0.0 to 1.0
	//
	public static ByteImage createSampledImage(ISize sampleSize, BoundingBox boundingBox, AnalyticField field) throws ImageException {

		byte handles[] = new byte[sampleSize.getX()*sampleSize.getY()*sampleSize.getZ()];
		for (int i=0;i<handles.length;i++){
			handles[i] = 0; // background color
		}
		
		//
		// go through Fields
		//
		double ox = boundingBox.getLoX();
		double oy = boundingBox.getLoY();
		double oz = boundingBox.getLoZ();
		double sx = boundingBox.getHiX()-boundingBox.getLoX();
		double sy = boundingBox.getHiY()-boundingBox.getLoY();
		double sz = boundingBox.getHiZ()-boundingBox.getLoZ();
		int numX = sampleSize.getX();
		int numY = sampleSize.getY();
		int numZ = sampleSize.getZ();
		
		int displayIndex = 0;
		for (int k=0;k<sampleSize.getZ();k++){
			double unit_z = (numZ>1)?((double)k)/(numZ-1):0.5;
			double coordZ = oz + sz * unit_z;
			for (int j=0;j<sampleSize.getY();j++){
				double unit_y = (numY>1)?((double)j)/(numY-1):0.5;
				double coordY = oy + sy * unit_y;
				for (int i=0;i<sampleSize.getX();i++){
					double unit_x = (numX>1)?((double)i)/(numX-1):0.5;
					double coordX = ox + sx * unit_x;
					double value = field.evaluate(new Vect3d(coordX,coordY,coordZ));
					if (value<0.0 || value>1.0){
						throw new ImageException("failed to create image, field evaluated to "+value+" at ("+coordX+","+coordY+","+coordZ+")");
					}
					handles[displayIndex++] = (byte)(255.0*value);
				}
			}
		}

		return new ByteImage(handles,new Extent(sx,sy,sz), sampleSize.getX(),sampleSize.getY(),sampleSize.getZ());
	}


}
