package cbit.render;

import java.awt.Color;
import java.util.Vector;

import javax.media.opengl.GL;

import cbit.render.objects.BoundingBox;
import cbit.render.objects.Node;
import cbit.render.objects.Polygon;
import cbit.render.objects.Quadrilateral;
import cbit.render.objects.Surface;
import cbit.render.objects.Vect3d;

public class SurfaceModelObject extends ModelObject {
	private Surface surface = null;
	private int surfaceColors[] = null;
	public SurfaceModelObject(Surface argSurface){
		if (argSurface==null){
			throw new IllegalArgumentException("surface was null");
		}
		this.surface = argSurface;
		
		//
		// compute bounding box
		//
		Vector<Node> nodes = new Vector<Node>();
		for (int j=0;j<surface.getPolygonCount();j++){
			Polygon polygon = surface.getPolygons(j);
			for (int i=0;i<polygon.getNodeCount();i++){
				nodes.add(polygon.getNodes(i));
			}
		}
		Node[] nodeArray = (Node[])nodes.toArray(new Node[nodes.size()]);
		boundingBox = BoundingBox.fromNodes(nodeArray);
	}

	public void draw0(GL gl) {
		if (isHidden()){
			return;
		}
		if (surfaceColors==null){
	 		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[] { 0.0f, 0.0f, 0.0f, 1.0f}, 0);
	 		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, new float[] { 0.2f, 0.8f, 0.2f, 1.0f}, 0);
		}
		if (!isShowSurfacesWireframe()){
		    gl.glBegin(GL.GL_QUADS);
		}
	    Vect3d normal = new Vect3d();
		for (int j=0;j<surface.getPolygonCount();j++){
			if (isShowSurfacesWireframe()){
				gl.glBegin(GL.GL_LINE_LOOP);
			}
			if (surfaceColors!=null){
				Color color = new Color(surfaceColors[j]);
		 		gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, new float[] { 0.0f, 0.0f, 0.0f, 1.0f}, 0);
				gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, new float[] { color.getRed()/255.0f, color.getGreen()/255.0f, color.getBlue()/255.0f, 1.0f }, 0);
			}
			Quadrilateral quad = (Quadrilateral)surface.getPolygons(j);
			quad.getUnitNormal(normal);
			gl.glNormal3dv(normal.uminus().q, 0);
			Node[] nodes = quad.getNodes();
			gl.glVertex3d(nodes[0].getX(),nodes[0].getY(),nodes[0].getZ());
			gl.glVertex3d(nodes[1].getX(),nodes[1].getY(),nodes[1].getZ());
			gl.glVertex3d(nodes[2].getX(),nodes[2].getY(),nodes[2].getZ());
			gl.glVertex3d(nodes[3].getX(),nodes[3].getY(),nodes[3].getZ());
			if (isShowSurfacesWireframe()){
				gl.glEnd();
			}
		}
		if (!isShowSurfacesWireframe()){
			gl.glEnd();
		}
	}
	
	public int[] getSurfaceColors() {
		return surfaceColors;
	}

	public void setSurfaceColors(int[] surfaceColors) {
		this.surfaceColors = surfaceColors;
	}
}
