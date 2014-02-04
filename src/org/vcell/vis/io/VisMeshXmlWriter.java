package org.vcell.vis.io;

import java.io.IOException;

import org.jdom.Element;
import org.vcell.vis.vismesh.VisMesh;
import org.vcell.vis.vismesh.VisPoint;

import cbit.util.xml.XmlUtil;

public class VisMeshXmlWriter {

	public static void write(VisMesh visMesh, String filename) throws IOException{
			Element rootNode = new Element("root");
			Element meshNode = new Element("vismesh");
			meshNode.setAttribute("info", "dim="+visMesh.getDimension()+", extent="+visMesh.getExtent().toStringKey()+", origin="+visMesh.getOrigin().toStringKey());
			rootNode.addContent(meshNode);
	
		    Element pointListNode = new Element("pointList");
		    pointListNode.setAttribute("numPoints", Integer.toString(visMesh.getPoints().size()));
		    meshNode.addContent(pointListNode);
		    for (VisPoint point : visMesh.getPoints()){
		    	Element pointNode = new Element("point");
		    	pointNode.setAttribute("info","("+String.format("%.6f",point.x)+","+String.format("%.6f",point.y)+","+String.format("%.6f",point.z)+")");
	   	        pointListNode.addContent(pointNode);
		    }
			
			//		Element levelListNode = new Element("levelList");
	//		meshNode.addContent(levelListNode);
	//		for (MeshLevel meshLevel : meshData.getMesh().getMeshLevels()){
	//			Element levelNode = new Element("level");
	//			levelNode.setAttribute("level", Integer.toString(meshLevel.getLevel()));
	//			levelListNode.addContent(levelNode);
	//			Element boxListNode = new Element("boxList");
	//			levelNode.addContent(boxListNode);
	//			for (Box box : meshLevel.getBoxes()){
	//				int minZ = 0;
	//				int maxZ = 0;
	//				Element boxNode = new Element("box");
	//				boxNode.setAttribute("values", "("+box.getMinX()+","+box.getMinY()+","+minZ+") ("+box.getMaxX()+","+box.getMaxY()+","+maxZ+")");
	//				boxListNode.addContent(boxNode);
	//			}
	//		}
			String xmlString = XmlUtil.xmlToString(rootNode, true);
			XmlUtil.writeXMLStringToFile(xmlString, filename, true);
		}

}
