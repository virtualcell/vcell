package cbit.render;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import cbit.render.objects.ByteImage;
import cbit.render.objects.ByteImageTest;
import cbit.render.objects.RegionImage;
import cbit.render.objects.SurfaceCollection;
import cbit.render.objects.SurfaceGenerator;
import cbit.render.objects.TaubinSmoothing;
import cbit.render.objects.TaubinSmoothingSpecification;
import cbit.render.objects.TaubinSmoothingWrong;
import cbit.util.Extent;
import cbit.util.ISize;
import cbit.util.Origin;

public class RenderTests {

/**
 * @param args
 */
public static void main(String[] args){
	try {
		ISize sampleSize = new ISize(100,100,100);
		Extent extent = new Extent(1,1,1);
		Origin origin = new Origin(0,0,0);
		ByteImage byteImage = ByteImageTest.getSphereInBox(sampleSize,extent,0.4);
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

}
