package cbit.vcell.geometry;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import org.vcell.util.document.Version;
/**
 * This type was created in VisualAge.
 */
public class GeometryTest {
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.Geometry
 */
public static Geometry getExample(int dim) throws Exception {
	if (dim == 0){
		return new Geometry("getExample()",0);
	}
	if (dim < 1 || dim > 3){
		throw new IllegalArgumentException("expected dimension between 1 and 3");
	}
	Geometry geo = new Geometry("getExample()",dim);
	geo.getGeometrySpec().setOrigin(new org.vcell.util.Origin(-6,-1,-1));
	geo.getGeometrySpec().setExtent(new org.vcell.util.Extent(12.0,2.0,2.0));
//	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("feature1",new cbit.vcell.parser.Expression("1.0;")));
//	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("feature1",new cbit.vcell.parser.Expression("(x+0.5)*(x+0.5)+y*y+(z+1)*(z+1)<0.5;")));
	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("cytosol",new cbit.vcell.parser.Expression("(pow(x,2)+pow(y,2)+pow(z,2)<0.1)||(pow(x-1,2)+pow(y,2)+pow(z,2)<0.1)||(pow(x,2)+pow(y-1,2)+pow(z,2)<0.1)||(pow(x,2)+pow(y,2)+pow(z-1,2)<0.1);")));
	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("extracellular",new cbit.vcell.parser.Expression(1.0)));
	//geo.getGeometrySpec().getFilamentGroup().addCurve("Filament1",new Line(new Coordinate(0,0,0),new Coordinate(1,1,1)));

	cbit.vcell.geometry.surface.GeometrySurfaceUtils.updateGeometricRegions(geo.getGeometrySurfaceDescription());
	
	return geo;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.Geometry
 */
public static Geometry getExample_er_cytsol2D() throws Exception {
	Geometry geo = new Geometry("getExample_er_cytosol()",2);
	geo.getGeometrySpec().setOrigin(new org.vcell.util.Origin(-1,-1,-1));
	geo.getGeometrySpec().setExtent(new org.vcell.util.Extent(2.0,2.0,2.0));
//	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("feature1",new cbit.vcell.parser.Expression("1.0;")));
//	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("feature1",new cbit.vcell.parser.Expression("(x+0.5)*(x+0.5)+y*y+(z+1)*(z+1)<0.5;")));
	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("er",new cbit.vcell.parser.Expression("pow(x+0.5,2)+pow(y,2)<0.5;")));
	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("cytosol",new cbit.vcell.parser.Expression("1.0;")));

	cbit.vcell.geometry.surface.GeometrySurfaceUtils.updateGeometricRegions(geo.getGeometrySurfaceDescription());
		
	return geo;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.Geometry
 */
public static Geometry getExample_er_cytsol3D() throws Exception {
	Geometry geo = new Geometry("getExample_er_cytosol()",3);
	geo.getGeometrySpec().setOrigin(new org.vcell.util.Origin(-1,-1,-1));
	geo.getGeometrySpec().setExtent(new org.vcell.util.Extent(2.0,2.0,2.0));
//	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("feature1",new cbit.vcell.parser.Expression("1.0;")));
	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("er",new cbit.vcell.parser.Expression("x^2+y^2+(z-0.5)^2<0.5;")));
	//geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("er",new cbit.vcell.parser.Expression("(pow(x,2)+pow(y,2)+pow(z,2)<0.1)||(pow(x-1,2)+pow(y,2)+pow(z,2)<0.1)||(pow(x,2)+pow(y-1,2)+pow(z,2)<0.1)||(pow(x,2)+pow(y,2)+pow(z-1,2)<0.1);")));
	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("cytosol",new cbit.vcell.parser.Expression("1.0;")));

	cbit.vcell.geometry.surface.GeometrySurfaceUtils.updateGeometricRegions(geo.getGeometrySurfaceDescription());
	
	return geo;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.Geometry
 */
public static Geometry getImageExample2D() {

	try {
		byte pixelValue1 = (byte)50;
		byte pixelValue2 = (byte)200;

		byte array[] = new byte[100*100];
		for (int i=0;i<100;i++){
			for (int j=0;j<100;j++){
				if (i>50){
					array[i+100*j] = pixelValue1;
				}else{
					array[i+100*j] = pixelValue2;
				}
			}
		}

		org.vcell.util.Extent extent = new org.vcell.util.Extent(10,10,10);
		cbit.image.VCImage vcImage = new cbit.image.VCImageUncompressed(null,array,extent,100,100,1);

		Geometry geo = new Geometry("getImageExample()",vcImage);
		geo.getGeometrySpec().setOrigin(new org.vcell.util.Origin(-5,-5,-5));

	//	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("featureAnal1",new cbit.vcell.parser.Expression("pow(x,2)+pow(y,2)+pow(z,2)<25;")));
	//	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("feature1",new cbit.vcell.parser.Expression("1.0;")));
	//	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("feature1",new cbit.vcell.parser.Expression("(x+0.5)*(x+0.5)+y*y+(z+1)*(z+1)<0.5;")));
		ImageSubVolume isv1 = geo.getGeometrySpec().getImageSubVolumeFromPixelValue(0xff & (int)pixelValue1);
		isv1.setName("cytosol");
		ImageSubVolume isv2 = geo.getGeometrySpec().getImageSubVolumeFromPixelValue(0xff & (int)pixelValue2);
		isv2.setName("er");

		cbit.vcell.geometry.surface.GeometrySurfaceUtils.updateGeometricRegions(geo.getGeometrySurfaceDescription());
		
		return geo;
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new RuntimeException("Exception constructing test Geometry: "+e.getMessage());
	}
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.Geometry
 */
public static Geometry getImageExample2D(org.vcell.util.document.User user) throws Exception {

	byte pixelValue1 = (byte)50;
	byte pixelValue2 = (byte)200;

	byte array[] = new byte[100*100];
	for (int i=0;i<100;i++){
		for (int j=0;j<100;j++){
			if (i>50){
				array[i+100*j] = pixelValue1;
			}else{
				array[i+100*j] = pixelValue2;
			}
		}
	}
	org.vcell.util.Extent extent = new org.vcell.util.Extent(2,2,2);
	cbit.image.VCImage vcImage = new cbit.image.VCImageUncompressed(null,array,extent,100,100,1);

	Geometry geo = new Geometry("getImageExample("+user+")",vcImage);
	geo.getGeometrySpec().setOrigin(new org.vcell.util.Origin(-1,-1,-1));
	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("featureAnal1",new cbit.vcell.parser.Expression("pow(x+0.5,2)+pow(y,2)+pow(z+1,2)<0.5;")));
//	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("feature1",new cbit.vcell.parser.Expression("1.0;")));
//	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("feature1",new cbit.vcell.parser.Expression("(x+0.5)*(x+0.5)+y*y+(z+1)*(z+1)<0.5;")));
	ImageSubVolume isv = geo.getGeometrySpec().getImageSubVolumeFromPixelValue(pixelValue1);
	isv.setName("featureImg1");
	
//	geo.setVersion(new Version(null,"GeomWithImage",user,new AccessInfo(AccessInfo.PRIVATE_CODE),null,null,null,null,null));
//	geo.getImage().setVersion(new Version(null,"image1",user,new AccessInfo(AccessInfo.PRIVATE_CODE),null,null,null,null,null));


//	System.out.println(geo.getVCML());

	cbit.vcell.geometry.surface.GeometrySurfaceUtils.updateGeometricRegions(geo.getGeometrySurfaceDescription());
		
	return geo;
}
/**
 * This method was created in VisualAge.
 * @return cbit.vcell.geometry.Geometry
 */
public static Geometry getImageExample3D() {

	try {
		byte pixelValue1 = (byte)50;
		byte pixelValue2 = (byte)200;

		byte array[] = new byte[50*50*50];
		for (int i=0;i<50;i++){
			for (int j=0;j<50;j++){
				for (int k=0;k<50;k++){
					if (i>20 && i<30 && j>20 && j<30 && k>20 && k<30){
						array[i+50*j+2500*k] = pixelValue1;
					}else{
						array[i+50*j+2500*k] = pixelValue2;
					}
				}
			}
		}

		org.vcell.util.Extent extent = new org.vcell.util.Extent(10,10,10);
		cbit.image.VCImage vcImage = new cbit.image.VCImageUncompressed(null,array,extent,50,50,50);

		Geometry geo = new Geometry("getImageExample()",vcImage);
		geo.getGeometrySpec().setOrigin(new org.vcell.util.Origin(-5,-5,-5));

	//	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("featureAnal1",new cbit.vcell.parser.Expression("pow(x,2)+pow(y,2)+pow(z,2)<25;")));
	//	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("feature1",new cbit.vcell.parser.Expression("1.0;")));
	//	geo.getGeometrySpec().addSubVolume(new AnalyticSubVolume("feature1",new cbit.vcell.parser.Expression("(x+0.5)*(x+0.5)+y*y+(z+1)*(z+1)<0.5;")));
		ImageSubVolume isv1 = geo.getGeometrySpec().getImageSubVolumeFromPixelValue(0xff & (int)pixelValue1);
		isv1.setName("cytosol");
		ImageSubVolume isv2 = geo.getGeometrySpec().getImageSubVolumeFromPixelValue(0xff & (int)pixelValue2);
		isv2.setName("er");
	
		cbit.vcell.geometry.surface.GeometrySurfaceUtils.updateGeometricRegions(geo.getGeometrySurfaceDescription());
		
		return geo;
	}catch (Throwable e){
		e.printStackTrace(System.out);
		throw new RuntimeException("Exception constructing test Geometry: "+e.getMessage());
	}
}
}
