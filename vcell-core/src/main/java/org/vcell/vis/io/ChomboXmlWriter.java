package org.vcell.vis.io;

import java.io.IOException;

import org.jdom.Element;
import org.vcell.util.ISize;
import org.vcell.vis.chombo.ChomboBox;
import org.vcell.vis.chombo.ChomboLevel;
import org.vcell.vis.chombo.ChomboLevel.Covering;
import org.vcell.vis.chombo.ChomboMeshData;

import cbit.util.xml.XmlUtil;

public class ChomboXmlWriter {

	public static void write(ChomboMeshData chomboMeshData, String filename) throws IOException{
		Element rootNode = new Element("root");
		Element meshNode = new Element("mesh");
		meshNode.setAttribute("dimension", Integer.toString(chomboMeshData.getMesh().getDimension()));
		rootNode.addContent(meshNode);
		Element levelListNode = new Element("levelList");
		meshNode.addContent(levelListNode);
		for (ChomboLevel chomboLevel : chomboMeshData.getMesh().getLevels()){
			Element levelNode = new Element("level");
			levelNode.setAttribute("info", "level="+Integer.toString(chomboLevel.getLevel()));
			levelListNode.addContent(levelNode);
			Element boxListNode = new Element("boxList");
			levelNode.addContent(boxListNode);
			for (ChomboBox chomboBox : chomboLevel.getBoxes()){
				int minZ = 0;
				int maxZ = 0;
				Element boxNode = new Element("box");
				boxNode.setAttribute("info", "("+chomboBox.getMinX()+","+chomboBox.getMinY()+","+minZ+") ("+chomboBox.getMaxX()+","+chomboBox.getMaxY()+","+maxZ+")");
				boxListNode.addContent(boxNode);
			}
	
			Element coveringNode = new Element("covering");
		    levelNode.addContent(coveringNode);
		    Covering covering = chomboLevel.getCovering();
		    int[] levelMap = covering.getLevelMap();
		    int[] boxNumberMap = covering.getBoxNumberMap();
		    int[] boxIndexMap = covering.getBoxIndexMap();
		    ISize size = chomboLevel.getSize();
		    int numX = size.getX();
		    int numY = size.getY();
		    {
		    Element levelMapNode = new Element("levelMap");
		    levelMapNode.setAttribute("info","level="+chomboLevel.getLevel());
		    StringBuffer levelMapText = new StringBuffer("\n");
		    for (int j=0;j<numY;j++){
		    	levelMapText.append("[");
		    	for (int i=0;i<numX;i++){
		    		levelMapText.append(levelMap[i + j*numX]+" ");
		    	}
		    	levelMapText.append("]\n");
		    }
		    levelMapNode.setText(levelMapText.toString());
		    coveringNode.addContent(levelMapNode);
		    }
		    {
		    Element boxNumberMapNode = new Element("boxNumberMap");
		    boxNumberMapNode.setAttribute("info","level="+chomboLevel.getLevel());
		    StringBuffer boxNumberMapText = new StringBuffer("\n");
		    for (int j=0;j<numY;j++){
		    	boxNumberMapText.append("[");
		    	for (int i=0;i<numX;i++){
		    		boxNumberMapText.append(boxNumberMap[i + j*numX]+" ");
		    	}
		    	boxNumberMapText.append("]\n");
		    }
		    boxNumberMapNode.setText(boxNumberMapText.toString());
		    coveringNode.addContent(boxNumberMapNode);
		    }
		    {
		    Element boxIndexMapNode = new Element("boxIndexMap");
		    boxIndexMapNode.setAttribute("info","level="+chomboLevel.getLevel());
		    StringBuffer boxIndexMapText = new StringBuffer("\n");
		    for (int j=0;j<numY;j++){
		    	boxIndexMapText.append("[");
		    	for (int i=0;i<numX;i++){
		    		boxIndexMapText.append(boxIndexMap[i + j*numX]+" ");
		    	}
		    	boxIndexMapText.append("]\n");
		    }
		    boxIndexMapNode.setText(boxIndexMapText.toString());
		    coveringNode.addContent(boxIndexMapNode);
		    }
		}
		String xmlString = XmlUtil.xmlToString(rootNode, true);
		XmlUtil.writeXMLStringToFile(xmlString, filename, true);
	}

}
