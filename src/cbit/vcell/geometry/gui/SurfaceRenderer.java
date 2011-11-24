/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;
import java.util.ArrayList;
import java.util.Collections;

import org.vcell.util.Extent;
import org.vcell.util.Origin;

import cbit.vcell.geometry.surface.Polygon;
import cbit.vcell.geometry.surface.SurfaceCollection;
import cbit.vcell.render.Trackball;
import cbit.vcell.render.Vect3d;
/**
 * Insert the type's description here.
 * Creation date: (7/20/2004 11:31:33 AM)
 * @author: Jim Schaff
 */
public class SurfaceRenderer {

	public static class BoundingBoxInfo {
		public cbit.vcell.render.Vect3d[] boundingBoxCorners;
		public double minZ;
		public double maxZ;
		public double depthCue;
		public double xScale;
		public double xOffset;
		public double yScale;
		public double yOffset;
	}
	public org.vcell.util.Origin fieldOrigin = null;
	public org.vcell.util.Extent fieldExtent = null;
	public cbit.vcell.render.Trackball fieldTrackball = null;
	private java.awt.Font axisLabelFont = null;

/**
 * SurfaceRenderer constructor comment.
 */
public SurfaceRenderer(Trackball argTrackball, Extent argExtent, Origin argOrigin) {
	super();
	this.fieldTrackball = argTrackball;
	this.fieldExtent = argExtent;
	this.fieldOrigin = argOrigin;
}


/**
 * Insert the method's description here.
 * Creation date: (10/11/2005 7:46:34 AM)
 */
private void calculateProjectedCentroid(Polygon polygon,cbit.vcell.render.Vect3d centroid,cbit.vcell.render.Vect3d centroidProj) {
	
	int polygonDim = polygon.getNodeCount();
	centroid.set(0,0,0);
	for (int k = 0; k < polygonDim; k++){
		cbit.vcell.geometry.surface.Node node = polygon.getNodes(k);
		centroid.add(new Vect3d(node.getX(),node.getY(),node.getZ()));
	}
	centroid.scale(1.0/polygonDim);
	getTrackball().getCamera().projectPoint(centroid,centroidProj);
	
}


/**
 * Insert the method's description here.
 * Creation date: (10/12/2005 7:27:33 AM)
 */
public BoundingBoxInfo createBoundingBoxInfo(int width,int height,boolean bEnableDepthCueing) {
	
		Vect3d boundingBoxCorners[] = {
			new Vect3d(fieldOrigin.getX()+0*fieldExtent.getX(), fieldOrigin.getY()+0*fieldExtent.getY(), fieldOrigin.getZ()+0*fieldExtent.getZ()), 
			new Vect3d(fieldOrigin.getX()+0*fieldExtent.getX(), fieldOrigin.getY()+0*fieldExtent.getY(), fieldOrigin.getZ()+1*fieldExtent.getZ()), 
			new Vect3d(fieldOrigin.getX()+0*fieldExtent.getX(), fieldOrigin.getY()+1*fieldExtent.getY(), fieldOrigin.getZ()+0*fieldExtent.getZ()), 
			new Vect3d(fieldOrigin.getX()+0*fieldExtent.getX(), fieldOrigin.getY()+1*fieldExtent.getY(), fieldOrigin.getZ()+1*fieldExtent.getZ()), 
			new Vect3d(fieldOrigin.getX()+1*fieldExtent.getX(), fieldOrigin.getY()+0*fieldExtent.getY(), fieldOrigin.getZ()+0*fieldExtent.getZ()), 
			new Vect3d(fieldOrigin.getX()+1*fieldExtent.getX(), fieldOrigin.getY()+0*fieldExtent.getY(), fieldOrigin.getZ()+1*fieldExtent.getZ()), 
			new Vect3d(fieldOrigin.getX()+1*fieldExtent.getX(), fieldOrigin.getY()+1*fieldExtent.getY(), fieldOrigin.getZ()+0*fieldExtent.getZ()), 
			new Vect3d(fieldOrigin.getX()+1*fieldExtent.getX(), fieldOrigin.getY()+1*fieldExtent.getY(), fieldOrigin.getZ()+1*fieldExtent.getZ())
		};
		double minZ = Double.MAX_VALUE;
		double maxZ = -Double.MAX_VALUE;
		Vect3d proj = new Vect3d();
		for (int i = 0; i < boundingBoxCorners.length; i++){
			getTrackball().getCamera().projectPoint(boundingBoxCorners[i],proj);
			minZ = Math.min(minZ,proj.getZ());
			maxZ = Math.max(maxZ,proj.getZ());
		}
		
		double depthCue = (bEnableDepthCueing)?(0.6/(maxZ-minZ)):(0.0);
				
		double tempXScale = 0.9*width/1;
		double tempYScale = 0.9*height/1;
		double xScale = Math.min(tempXScale,tempYScale);
		double yScale = Math.min(tempXScale,tempYScale);
		double xOffset = width/2;
		double yOffset = height/2;


		BoundingBoxInfo bbi = new BoundingBoxInfo();
		bbi.boundingBoxCorners = boundingBoxCorners;
		bbi.minZ = minZ;
		bbi.maxZ = maxZ;
		bbi.depthCue = depthCue;
		bbi.xScale = xScale;
		bbi.xOffset = xOffset;
		bbi.yScale = yScale;
		bbi.yOffset = yOffset;

		
		return bbi;

	
	
	
}


/**
 * Insert the method's description here.
 * Creation date: (7/7/2004 11:25:42 AM)
 * @param g java.awt.Graphics
 */
public PolygonInfo[] createProjectedScreenPolygons(SurfaceCollection surfaceCollection,int[] quickRenderSkip,BoundingBoxInfo bbi) {

	int xPoints[] = new int[4];
	int yPoints[] = new int[4];

	ArrayList<PolygonInfo> polygonInfoList = new ArrayList<PolygonInfo>(1000);
	Vect3d centroid = new Vect3d();
	Vect3d centroidProj = new Vect3d();
	Vect3d unitNormal = new Vect3d();
	Vect3d cameraVector = new Vect3d(0,0,1);
	Vect3d cameraVectorScene = new Vect3d();
	getTrackball().getCamera().unProjectPoint(cameraVector,cameraVectorScene);
	cameraVectorScene.unit();

	for (int i = 0; i < surfaceCollection.getSurfaceCount(); i++){
		cbit.vcell.geometry.surface.Surface surface = surfaceCollection.getSurfaces(i);
		for (int j = 0; j < surface.getPolygonCount(); j++){
			
			if(	quickRenderSkip != null &&
				(quickRenderSkip[i] == 0
				||
				j%quickRenderSkip[i] != 0
				)){
					
				continue;
			}
				
			cbit.vcell.geometry.surface.Polygon polygon = surface.getPolygons(j);
			
			//find centroid depth along user line of sight
			calculateProjectedCentroid(polygon,centroid,centroidProj);
			double depth = centroidProj.getZ();
			
			//depthCue color scale
			float notFlatShadeColorScale = (float) (1.0 - bbi.depthCue*(depth-bbi.minZ));
			
			// calculate flat shading scale, adjust due to normal
			polygon.getUnitNormal(unitNormal);
			double dot = unitNormal.dot(cameraVectorScene);
			float flatShadeColorScale = (float)(0.5*notFlatShadeColorScale + 0.5*Math.pow(Math.abs(dot),2));
			
			//make sure color scale between 0 and 1
			notFlatShadeColorScale = (float)Math.min(1.0, Math.max(0.0, notFlatShadeColorScale));
			flatShadeColorScale = (float)Math.min(1.0, Math.max(0.0, flatShadeColorScale));
			
			polygonInfoList.add(
				new PolygonInfo(
					createScreenPolygon(polygon,xPoints,yPoints,bbi.xOffset,bbi.xScale,bbi.yOffset,bbi.yScale),
					depth,flatShadeColorScale,notFlatShadeColorScale,i,j));
		}
	}
	
	Collections.sort(polygonInfoList);
	PolygonInfo[] polygonInfoArr = new PolygonInfo[polygonInfoList.size()];
	polygonInfoList.toArray(polygonInfoArr);
	return polygonInfoArr;

}


/**
 * Insert the method's description here.
 * Creation date: (10/11/2005 8:12:13 AM)
 * @return cbit.vcell.geometry.surface.Polygon
 */
private java.awt.Polygon createScreenPolygon(cbit.vcell.geometry.surface.Polygon polygon,int[] xPoints,int[] yPoints,double xOffset,double xScale,double yOffset,double yScale) {
	
	Vect3d point = new Vect3d();
	Vect3d proj = new Vect3d();
	int polygonDim = polygon.getNodeCount();
	for (int k = 0; k < polygonDim; k++){
		cbit.vcell.geometry.surface.Node node = polygon.getNodes(k);
		point.set(node.getX(),node.getY(),node.getZ());
		getTrackball().getCamera().projectPoint(point,proj);
		xPoints[k] = (int)(xOffset + xScale*proj.getX());
		yPoints[k] = (int)(yOffset + yScale*proj.getY());
	}
	return new java.awt.Polygon(xPoints,yPoints,4);
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 11:35:26 AM)
 * @return cbit.util.Extent
 */
public org.vcell.util.Extent getExtent() {
	return fieldExtent;
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 11:35:12 AM)
 * @return cbit.util.Origin
 */
public org.vcell.util.Origin getOrigin() {
	return fieldOrigin;
}


/**
 * Insert the method's description here.
 * Creation date: (7/20/2004 11:38:45 AM)
 * @return cbit.vcell.render.Trackball
 */
public cbit.vcell.render.Trackball getTrackball() {
	return fieldTrackball;
}

/**
 * Insert the method's description here.
 * Creation date: (11/26/2003 12:40:37 PM)
 * @param g2d java.awt.Graphics
 */
private void paintBoundingBox(java.awt.Graphics g, double xScale, double yScale, double xOffset, double yOffset, double depthCue, boolean bDrawFrontOnly) {
	try {

		java.awt.Graphics2D g2d = (java.awt.Graphics2D)g;

		double ox = fieldOrigin.getX();
		double oy = fieldOrigin.getY();
		double oz = fieldOrigin.getZ();
		double ex = fieldExtent.getX();
		double ey = fieldExtent.getY();
		double ez = fieldExtent.getZ();
		Vect3d boundingBoxCorners[] = {
			new Vect3d(ox+0*ex, oy+0*ey, oz+0*ez),
			new Vect3d(ox+0*ex, oy+0*ey, oz+1*ez),
			new Vect3d(ox+0*ex, oy+1*ey, oz+0*ez),
			new Vect3d(ox+0*ex, oy+1*ey, oz+1*ez),
			new Vect3d(ox+1*ex, oy+0*ey, oz+0*ez),
			new Vect3d(ox+1*ex, oy+0*ey, oz+1*ez),
			new Vect3d(ox+1*ex, oy+1*ey, oz+0*ez),
			new Vect3d(ox+1*ex, oy+1*ey, oz+1*ez),
		};
		double cornerZ[] = new double[8];
		double minZ = Double.POSITIVE_INFINITY;
		double maxZ = Double.NEGATIVE_INFINITY;
		Vect3d proj = new Vect3d();
		for (int i = 0; i < boundingBoxCorners.length; i++){
			getTrackball().getCamera().projectPoint(boundingBoxCorners[i],proj);
			minZ = Math.min(minZ,proj.getZ());
			maxZ = Math.max(maxZ,proj.getZ());
			cornerZ[i] = proj.getZ();
		}

		double frontThreshold = (minZ + 0.001*(maxZ-minZ));	
		
		Vect3d pt1a = new Vect3d();
		Vect3d pt2a = new Vect3d();
		Vect3d pt1b = new Vect3d();
		Vect3d pt2b = new Vect3d();
		Vect3d pt1c = new Vect3d();
		Vect3d pt2c = new Vect3d();
		Vect3d pt1d = new Vect3d();
		Vect3d pt2d = new Vect3d();
		Vect3d proj1 = new Vect3d();
		Vect3d proj2 = new Vect3d();
		java.awt.geom.Line2D.Double line = new java.awt.geom.Line2D.Double();
		//
		// Draw coordinate Axis (break up into many pieces so that distance cueing will work nicely)
		//
		for (int direction=0;direction<3;direction++){
			double deltaX = 0.0;
			double deltaY = 0.0;
			double deltaZ = 0.0;
			boolean bRenderA = true;
			boolean bRenderB = true;
			boolean bRenderC = true;
			boolean bRenderD = true;
			
			switch (direction){
				case 0:{  // yz plane (go in x)
					deltaX = ex;
					pt1a.set(ox   ,oy   ,oz   ); // 0 to 4
					pt1b.set(ox   ,oy   ,oz+ez); // 1 to 5
					pt1c.set(ox   ,oy+ey,oz   ); // 2 to 6
					pt1d.set(ox   ,oy+ey,oz+ez); // 3 to 7
					bRenderA = (!bDrawFrontOnly || (cornerZ[0]<frontThreshold || cornerZ[4]<frontThreshold)&&bDrawFrontOnly);
					bRenderB = (!bDrawFrontOnly || (cornerZ[1]<frontThreshold || cornerZ[5]<frontThreshold)&&bDrawFrontOnly);
					bRenderC = (!bDrawFrontOnly || (cornerZ[2]<frontThreshold || cornerZ[6]<frontThreshold)&&bDrawFrontOnly);
					bRenderD = (!bDrawFrontOnly || (cornerZ[3]<frontThreshold || cornerZ[7]<frontThreshold)&&bDrawFrontOnly);
					break;
				}
				case 1:{ // xz plane (go in y)
					deltaY = ey;
					pt1a.set(ox   ,oy   ,oz   ); // 0 to 2
					pt1b.set(ox   ,oy   ,oz+ez); // 1 to 3
					pt1c.set(ox+ex,oy   ,oz   ); // 4 to 6
					pt1d.set(ox+ex,oy   ,oz+ez); // 5 to 7
					bRenderA = (!bDrawFrontOnly || (cornerZ[0]<frontThreshold || cornerZ[2]<frontThreshold)&&bDrawFrontOnly);
					bRenderB = (!bDrawFrontOnly || (cornerZ[1]<frontThreshold || cornerZ[3]<frontThreshold)&&bDrawFrontOnly);
					bRenderC = (!bDrawFrontOnly || (cornerZ[4]<frontThreshold || cornerZ[6]<frontThreshold)&&bDrawFrontOnly);
					bRenderD = (!bDrawFrontOnly || (cornerZ[5]<frontThreshold || cornerZ[7]<frontThreshold)&&bDrawFrontOnly);
					break;
				}
				case 2:{ // xy plane (go in z)
					deltaZ = ez;
					pt1a.set(ox   ,oy   ,oz   ); // 0 to 1
					pt1b.set(ox   ,oy+ey,oz   ); // 2 to 3
					pt1c.set(ox+ex,oy   ,oz   ); // 4 to 5
					pt1d.set(ox+ex,oy+ey,oz   ); // 6 to 7
					bRenderA = (!bDrawFrontOnly || (cornerZ[0]<frontThreshold || cornerZ[1]<frontThreshold)&&bDrawFrontOnly);
					bRenderB = (!bDrawFrontOnly || (cornerZ[2]<frontThreshold || cornerZ[3]<frontThreshold)&&bDrawFrontOnly);
					bRenderC = (!bDrawFrontOnly || (cornerZ[4]<frontThreshold || cornerZ[5]<frontThreshold)&&bDrawFrontOnly);
					bRenderD = (!bDrawFrontOnly || (cornerZ[6]<frontThreshold || cornerZ[7]<frontThreshold)&&bDrawFrontOnly);
					break;
				}
			}
			int numSteps = 20;
			double STEP = 1.0/numSteps;
			for (int i = 0; i < numSteps; i++){
				if (bRenderA){
					pt2a.set(pt1a.getX()+deltaX*STEP, pt1a.getY()+deltaY*STEP, pt1a.getZ()+deltaZ*STEP);
					if (getTrackball().getCamera().projectLine(pt1a,pt2a,proj1,proj2)){
						double pt1x = xOffset + xScale*proj1.getX();
						double pt1y = yOffset + yScale*proj1.getY();
						double pt2x = xOffset + xScale*proj2.getX();
						double pt2y = yOffset + yScale*proj2.getY();
						double avgZ = (proj1.getZ()+proj2.getZ())/2.0;
						float colorScale = (float) (1.0 - depthCue*(avgZ-minZ));
						colorScale = (float)Math.min(1.0, Math.max(0.0, colorScale));
						g2d.setColor(new java.awt.Color(colorScale, colorScale, colorScale));
						line.setLine(pt1x,pt1y,pt2x,pt2y);
						g2d.draw(line);
						//Draw axis labels
						if(i==0){
							if(axisLabelFont == null){
								axisLabelFont = g2d.getFont().deriveFont(java.awt.Font.BOLD).deriveFont(160.0f);
							}
							g2d.setFont(axisLabelFont);
							cbit.vcell.render.Vect3d dirVect = new cbit.vcell.render.Vect3d(pt2x-pt1x,pt2y-pt1y,0);
							if(dirVect.getX() != 0 || dirVect.getY() != 0){
								dirVect.unit();
								dirVect.scale(300);
								g2d.drawString((direction==0?"X":(direction==1?"Y":"Z")),(float)(pt1x+dirVect.getX()),(float)(pt1y+dirVect.getY()));
							}
						}
					}
					pt1a.set(pt2a);
				}
				if (bRenderB){
					pt2b.set(pt1b.getX()+deltaX*STEP, pt1b.getY()+deltaY*STEP, pt1b.getZ()+deltaZ*STEP);
					if (getTrackball().getCamera().projectLine(pt1b,pt2b,proj1,proj2)){
						double pt1x = xOffset + xScale*proj1.getX();
						double pt1y = yOffset + yScale*proj1.getY();
						double pt2x = xOffset + xScale*proj2.getX();
						double pt2y = yOffset + yScale*proj2.getY();
						double avgZ = (proj1.getZ()+proj2.getZ())/2.0;
						float colorScale = (float) (1.0 - depthCue*(avgZ-minZ));
						colorScale = (float)Math.min(1.0, Math.max(0.0, colorScale));
						g2d.setColor(new java.awt.Color(colorScale, colorScale, colorScale));
						line.setLine(pt1x,pt1y,pt2x,pt2y);
						g2d.draw(line);
					}
					pt1b.set(pt2b);
				}
				if (bRenderC){
					pt2c.set(pt1c.getX()+deltaX*STEP, pt1c.getY()+deltaY*STEP, pt1c.getZ()+deltaZ*STEP);
					if (getTrackball().getCamera().projectLine(pt1c,pt2c,proj1,proj2)){
						double pt1x = xOffset + xScale*proj1.getX();
						double pt1y = yOffset + yScale*proj1.getY();
						double pt2x = xOffset + xScale*proj2.getX();
						double pt2y = yOffset + yScale*proj2.getY();
						double avgZ = (proj1.getZ()+proj2.getZ())/2.0;
						float colorScale = (float) (1.0 - depthCue*(avgZ-minZ));
						colorScale = (float)Math.min(1.0, Math.max(0.0, colorScale));
						g2d.setColor(new java.awt.Color(colorScale, colorScale, colorScale));
						line.setLine(pt1x,pt1y,pt2x,pt2y);
						g2d.draw(line);
					}
					pt1c.set(pt2c);
				}
				if (bRenderD){
					pt2d.set(pt1d.getX()+deltaX*STEP, pt1d.getY()+deltaY*STEP, pt1d.getZ()+deltaZ*STEP);
					if (getTrackball().getCamera().projectLine(pt1d,pt2d,proj1,proj2)){
						double pt1x = xOffset + xScale*proj1.getX();
						double pt1y = yOffset + yScale*proj1.getY();
						double pt2x = xOffset + xScale*proj2.getX();
						double pt2y = yOffset + yScale*proj2.getY();
						double avgZ = (proj1.getZ()+proj2.getZ())/2.0;
						float colorScale = (float) (1.0 - depthCue*(avgZ-minZ));
						colorScale = (float)Math.min(1.0, Math.max(0.0, colorScale));
						g2d.setColor(new java.awt.Color(colorScale, colorScale, colorScale));
						line.setLine(pt1x,pt1y,pt2x,pt2y);
						g2d.draw(line);
					}
					pt1d.set(pt2d);
				}
			}
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (11/26/2003 12:40:37 PM)
 * @param g2d java.awt.Graphics
 */
protected void paintSurface(
	PolygonInfo[] sortedScreenPolygons,
	BoundingBoxInfo bbi,
	java.awt.Graphics g,int[] quickRenderSkip,
	java.awt.Color[][] surfacePolygonColors,
	boolean[] isSurfaceShowing,boolean[] isSurfaceWireframe,
	java.awt.AlphaComposite[] surfaceTransparency,
	boolean bShowBoundingBox,
	boolean[][] highlitedSurfacePolygons,
	java.awt.Color highlightColor,
	java.awt.Stroke wireFrameThickness
	) {

	try{
		// Draw coordinate Axis (Back) (break up into many pieces so that distance cueing will work nicely)
		if(bShowBoundingBox){
			paintBoundingBox(g,bbi.xScale,bbi.yScale,bbi.xOffset,bbi.yOffset,bbi.depthCue,false);
		}

		renderSurfacesSorted((java.awt.Graphics2D)g,sortedScreenPolygons,quickRenderSkip,surfacePolygonColors,isSurfaceShowing,isSurfaceWireframe,surfaceTransparency,highlitedSurfacePolygons,highlightColor,wireFrameThickness);

		// Draw coordinate Axis (Front) (break up into many pieces so that distance cueing will work nicely)
		if(bShowBoundingBox){
			paintBoundingBox(g,bbi.xScale,bbi.yScale,bbi.xOffset,bbi.yOffset,bbi.depthCue,true);
		}

	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (7/7/2004 11:25:42 AM)
 * @param g java.awt.Graphics
 */
private void renderSurfacesSorted(
	java.awt.Graphics2D g2d,
	PolygonInfo[] polygonInfoArr,
	int[] quickRenderSkip,
	java.awt.Color[][] surfacePolygonColors,
	boolean[] isSurfaceShowing,
	boolean[] isSurfaceWireframe,
	java.awt.AlphaComposite[] surfaceTransparency,
	boolean[][] highlitedSurfacePolygons,
	java.awt.Color highlightColor,
	java.awt.Stroke wireFrameThickness) {


	java.awt.Stroke strokeOrig = g2d.getStroke();
	try{
		//make "draw" thicker
		if(isSurfaceWireframe != null && wireFrameThickness != null){
			g2d.setStroke(wireFrameThickness);
		}
		
		for(int i = 0;i<polygonInfoArr.length;i+= 1){
			
			PolygonInfo polygonInfo = polygonInfoArr[i];
			if(	quickRenderSkip != null &&
				(quickRenderSkip[polygonInfo.surfaceIndex] == 0
				||
				polygonInfo.polygonIndex%quickRenderSkip[polygonInfo.surfaceIndex] != 0
				)){
					
				continue;
			}
			if(isSurfaceShowing != null && !isSurfaceShowing[polygonInfo.surfaceIndex]){
				continue;
			}
			java.awt.Polygon polygon = polygonInfo.polygon;
			
					if(surfaceTransparency != null && surfaceTransparency[polygonInfo.surfaceIndex] != g2d.getComposite()){
						g2d.setComposite(surfaceTransparency[polygonInfo.surfaceIndex]);
					}
					
					if(surfacePolygonColors == null || polygonInfo.surfaceIndex >= surfacePolygonColors.length){
						g2d.setColor(java.awt.Color.gray);
					}else if(polygonInfo.polygonIndex >= surfacePolygonColors[polygonInfo.surfaceIndex].length){
						g2d.setColor(surfacePolygonColors[polygonInfo.surfaceIndex][0]);
					}else{
						g2d.setColor(surfacePolygonColors[polygonInfo.surfaceIndex][polygonInfo.polygonIndex]);
					}

					if(isSurfaceWireframe != null && isSurfaceWireframe[polygonInfo.surfaceIndex]){
						g2d.draw(polygon);
					}else{
						g2d.fill(polygon);
					}
					if(highlitedSurfacePolygons != null && highlitedSurfacePolygons[polygonInfo.surfaceIndex][polygonInfo.polygonIndex]){
						g2d.setColor(highlightColor);
						//if(surfacePolygonColors[polygonInfo.surfaceIndex][polygonInfo.polygonIndex].getRed() == surfacePolygonColors[polygonInfo.surfaceIndex][polygonInfo.polygonIndex].getGreen() &&
							//surfacePolygonColors[polygonInfo.surfaceIndex][polygonInfo.polygonIndex].getRed() == surfacePolygonColors[polygonInfo.surfaceIndex][polygonInfo.polygonIndex].getBlue() &&
							//surfacePolygonColors[polygonInfo.surfaceIndex][polygonInfo.polygonIndex].getBlue() == surfacePolygonColors[polygonInfo.surfaceIndex][polygonInfo.polygonIndex].getGreen()){
								//g2d.setColor(java.awt.Color.red);
						//}else{
							//g2d.setColor(java.awt.Color.white);
						//}
						g2d.fill(polygon);
					}
		}
	}finally{
		g2d.setStroke(strokeOrig);
	}
}


}
