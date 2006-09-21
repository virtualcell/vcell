package cbit.render.objects;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import cbit.render.AxisModelObject;
import cbit.render.JOGLRendererTest;
import cbit.render.SurfaceCollectionModelObject;
import cbit.util.Extent;
import cbit.util.ISize;
import cbit.util.Origin;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;

public class ByteImageTest {
	public static void main(String[] args){
		try {
			ISize sampleSize = new ISize(100,100,100);
			Extent extent = new Extent(1,1,1);
			Origin origin = new Origin(0,0,0);
			ByteImage byteImage = getSphereInBox(sampleSize,extent,0.4);
			RegionImage regionImage = new RegionImage(byteImage);
			RegionImage.RegionInfo[] regionInfos = regionImage.getRegionInfos();
			for (int i = 0; i < regionInfos.length; i++) {
				System.out.println("region["+i+"] = "+regionInfos[i].toString());
			}
			SurfaceGenerator surfGen = new SurfaceGenerator();
			SurfaceCollection surfCollection = surfGen.generateSurface(regionImage, 3, extent, origin);
			TaubinSmoothing taubinSmoothing = new TaubinSmoothingWrong();
			TaubinSmoothingSpecification taubinSpec = TaubinSmoothingSpecification.getInstance(0.3);
			taubinSmoothing.smooth(surfCollection,taubinSpec);
			//surfCollection.getSurfaces(0).reverseDirection();
			SurfaceCollectionModelObject surfModelObject = new SurfaceCollectionModelObject(surfCollection);
			AxisModelObject axisModelObject = new AxisModelObject(surfModelObject.getBoundingBox().getSize());
			surfModelObject.addChild(axisModelObject);
			JFrame frame = new JFrame();
			JOGLRendererTest joglRendererTest = new JOGLRendererTest();
			joglRendererTest.setModelObject(surfModelObject);
			frame.getContentPane().add(joglRendererTest);
			frame.setSize(300,300);
			frame.setVisible(true);
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e){
	                System.exit(0);
				}
			});
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
		
	}
	public static ByteImage getBoxInBox(ISize sampleSize, Extent extent, double sizeRatio) throws ImageException {
		double boxLoX = (1.0-sizeRatio)/2.0*extent.getX();
		double boxHiX = extent.getX()-boxLoX;
		double boxLoY = (1.0-sizeRatio)/2.0*extent.getY();
		double boxHiY = extent.getX()-boxLoY;
		double boxLoZ = (1.0-sizeRatio)/2.0*extent.getZ();
		double boxHiZ = extent.getX()-boxLoZ;
		
		BoundingBox bbox = new BoundingBox(0,extent.getX(),0,extent.getY(),0,extent.getZ());
		try {
			Expression exp = new Expression(
					"(x>"+boxLoX+")&&(x<"+boxHiX+")&&"+
					"(y>"+boxLoY+")&&(y<"+boxHiY+")&&"+
					"(z>"+boxLoZ+")&&(z<"+boxHiZ+")");
			AnalyticField field = new AnalyticField(exp);
			return createSegmentedImage(sampleSize,bbox,new AnalyticField[] { field });
		}catch (ExpressionException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public static ByteImage getSphereInBox(ISize sampleSize, Extent extent, double radius) throws ImageException {
		double cx = extent.getX()/2.0;
		double cy = extent.getY()/2.0;
		double cz = extent.getZ()/2.0;
		
		BoundingBox bbox = new BoundingBox(0,extent.getX(),0,extent.getY(),0,extent.getZ());
		try {
			Expression exp = new Expression("(x-"+cx+")^2+(y-"+cy+")^2+(z-"+cz+")^2<"+radius+"^2");
			AnalyticField field = new AnalyticField(exp);
			return createSegmentedImage(sampleSize,bbox,new AnalyticField[] { field });
		}catch (ExpressionException e){
			e.printStackTrace(System.out);
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public static ByteImage createSegmentedImage(ISize sampleSize, BoundingBox boundingBox, AnalyticField[] fields) throws ImageException, ExpressionException {

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
	public static ByteImage createSampledImage(ISize sampleSize, BoundingBox boundingBox, AnalyticField field) throws ImageException, ExpressionException {

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
