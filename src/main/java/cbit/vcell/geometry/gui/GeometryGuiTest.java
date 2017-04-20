package cbit.vcell.geometry.gui;

import java.beans.PropertyVetoException;

import org.vcell.util.ISize;

import cbit.image.ImageException;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.CSGPrimitive;
import cbit.vcell.geometry.CSGRotation;
import cbit.vcell.geometry.CSGScale;
import cbit.vcell.geometry.CSGSetOperator;
import cbit.vcell.geometry.GeometryThumbnailImageFactoryAWT;
import cbit.vcell.geometry.CSGSetOperator.OperatorType;
import cbit.vcell.geometry.CSGTranslation;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryException;
import cbit.vcell.geometry.surface.RayCaster;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.render.Vect3d;

public class GeometryGuiTest {

	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			GeometryViewer aGeometryViewer;
			aGeometryViewer = new GeometryViewer();
			frame.setContentPane(aGeometryViewer);
			frame.setSize(aGeometryViewer.getSize());
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			
			Geometry csGeometry = getExampleGeometryCSG();
			
			// try to convert this CSG geometry to image
			ISize imageSize = csGeometry.getGeometrySpec().getDefaultSampledImageSize();
			Geometry imageGeometry = RayCaster.resampleGeometry(new GeometryThumbnailImageFactoryAWT(), csGeometry, imageSize);
			
			// aGeometryViewer.setGeometry(csGeometry);
			aGeometryViewer.setGeometry(imageGeometry);
			frame.setSize(600,600);
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}

	public static Geometry getExampleGeometryCSG() throws PropertyVetoException, ExpressionException, GeometryException, ImageException {
		// translated rotated cube
		CSGPrimitive cube = new CSGPrimitive("cube", CSGPrimitive.PrimitiveType.CUBE);
		CSGRotation rotatedCube = new CSGRotation("Rotation", new Vect3d(1,2,3),Math.PI/4.0);
		rotatedCube.setChild(cube);

		// translated sphere
		CSGTranslation translatedSphere = new CSGTranslation("translation", new Vect3d(0.5,0.5,0.5));
		CSGPrimitive sphere = new CSGPrimitive("sphere", CSGPrimitive.PrimitiveType.SPHERE);
		translatedSphere.setChild(sphere);
		
		// union
		CSGSetOperator csgSetOperator = new CSGSetOperator("difference", OperatorType.DIFFERENCE);
		csgSetOperator.addChild(rotatedCube);
		csgSetOperator.addChild(translatedSphere);
		
		// scaled union
		CSGScale csgScale = new CSGScale("scale", new Vect3d(3,3,3));
		csgScale.setChild(csgSetOperator);
		
		CSGTranslation csgTranslatedUnion = new CSGTranslation("translationUnion", new Vect3d(5,5,5));
		csgTranslatedUnion.setChild(csgScale);
		
		Geometry geometry = new Geometry("csg",3);
		CSGObject csgObject = new CSGObject(null, "obj1", 1);
		csgObject.setRoot(csgTranslatedUnion);
		
		geometry.getGeometrySpec().addSubVolume(new AnalyticSubVolume("background",new Expression(1.0)));
		geometry.getGeometrySpec().addSubVolume(csgObject, true);
		geometry.refreshDependencies();
		geometry.precomputeAll(new GeometryThumbnailImageFactoryAWT());
		
		return geometry;
	}


}
