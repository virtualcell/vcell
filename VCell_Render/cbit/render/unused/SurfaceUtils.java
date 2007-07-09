package cbit.render.unused;

import cbit.render.objects.Node;
import cbit.render.objects.OrigSurface;
import cbit.render.objects.SurfaceCollection;

public class SurfaceUtils {
	/**
	 * Insert the method's description here.
	 * Creation date: (5/14/2004 3:47:53 PM)
	 * @param quadSurfaceCollection cbit.vcell.geometry.surface.SurfaceCollection
	 */
	private static SurfaceCollection getCentroidSurface(SurfaceCollection quadSurfaceCollection) {
		if(quadSurfaceCollection != null && quadSurfaceCollection.getSurfaceCount() > 0){
			OrigSurface quadSurface = (OrigSurface)quadSurfaceCollection.getSurfaces(0);
			OrigSurface quadSurfaceCopy = null;
			try {
				quadSurfaceCopy = (OrigSurface)org.vcell.util.BeanUtils.cloneSerializable(quadSurface);
			}catch (Throwable e){
				e.printStackTrace(System.out);
				throw new RuntimeException(e.getMessage());
			}
			SlowSurface slowQuadSurface = new SlowSurface(quadSurfaceCopy.getInteriorRegionIndex(),quadSurfaceCopy.getExteriorRegionIndex());
			slowQuadSurface.addSurface(quadSurfaceCopy);
			SlowSurface cSurface = slowQuadSurface.createCentroidSurface();
			FastSurface finalSurface = new FastSurface(quadSurfaceCopy.getInteriorRegionIndex(),quadSurfaceCopy.getExteriorRegionIndex());
			finalSurface.addSurface(cSurface);
			finalSurface.addSurface(quadSurface);
			Node nodes[] = finalSurface.getfindAllNodes();
			SurfaceCollection centroidSurfaceCollection = new SurfaceCollection(finalSurface);
			centroidSurfaceCollection.setNodes(nodes);
			//cbit.vcell.geometry.surface.SurfaceCollection centroidSurfaceCollection = new cbit.vcell.geometry.surface.SurfaceCollection(cSurface);
			return centroidSurfaceCollection; //centroidSurfaceCollection;
//	System.out.println("SurfaceCount="+getsurfaceCollection1().getSurfaceCount());
			//cbit.vcell.geometry.surface.Surface cSurface = new cbit.vcell.geometry.surface.Surface(0,1);
			//for(int i=0;i < getsurfaceCollection1().getSurfaceCount();i+= 1){
				//cbit.vcell.geometry.surface.Surface surface = getsurfaceCollection1().getSurfaces(i);
//	System.out.println("Surface "+i+" IneriorRegionIndex="+surface.getInteriorRegionIndex()+" ExteriorRegionIndex="+surface.getExteriorRegionIndex());
				//cSurface.addSurface(surface);
				
			//}
			//return new cbit.vcell.geometry.surface.SurfaceCollection(cSurface);
		}else{
			return null;
		}
		//}
	}

}
