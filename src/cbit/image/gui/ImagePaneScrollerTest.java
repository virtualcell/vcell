/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.image.gui;
import org.vcell.util.Range;

import cbit.image.DisplayAdapterService;
import cbit.image.SourceDataInfo;

/**
 * Insert the type's description here.
 * Creation date: (10/5/00 6:10:39 PM)
 * @author: 
 */
public class ImagePaneScrollerTest {
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 2:08:24 PM)
 * @return cbit.image.SourceDataInfo
 */
public final static SourceDataInfo getExampleSDI(String type, int w, int h) {
	int numZ = 20;
	SourceDataInfo sdi = null;
	if (type.equals("rgb")) {
		int[] pixels = new int[w * h * numZ];
		for (int z = 0; z < numZ; z += 1) {
			for (int y = 0; y < h; y += 1) {
				for (int x = 0; x < w; x += 1) {
					if (y % 2 == 0) {
						if (x % 2 == 0) {
							pixels[x + (y * w) + (z * w * h)] = 0x00ff0000;
						}
					} else {
						if (x % 2 != 0) {
							pixels[x + (y * w) + (z * w * h)] = 0x00ff0000;
						}
					}
					if (x >= w / 3 && x < w / 2 && y >= h / 3 && y < h / 1.5) {
						pixels[x + (y * w) + (z * w * h)] = 0xFF * z / numZ * 0xFFFF;
					}
				}
			}
		}
		//
		sdi = new SourceDataInfo(SourceDataInfo.INT_RGB_TYPE, pixels, new org.vcell.util.Extent(1, 2, 3), new org.vcell.util.Origin(0, -5, 10), null, 0, w, 1, h, w, numZ, h * w);
	} else if (type.equals("double")) {
		double[] pixels = new double[w * h * numZ];
		for (int z = 0; z < numZ; z += 1) {
			for (int y = 0; y < h; y += 1) {
				for (int x = 0; x < w; x += 1) {
					pixels[x + (y * w) + (z * w * h)] = -150 + ((double) x / ((double) w - 1)) * (800);
					if (y > h - (h / 4)) {
						pixels[x + (y * w) + (z * w * h)] = Double.POSITIVE_INFINITY;
					}
					if (x >= w / 3 && x < w / 2 && y >= h / 3 && y < h / 1.5) {
						pixels[x + (y * w) + (z * w * h)] = 300 * z / numZ;
					}
				}
			}
		}
		sdi = new SourceDataInfo(SourceDataInfo.RAW_VALUE_TYPE, pixels, new org.vcell.util.Extent(1, 2, 3), new org.vcell.util.Origin(0, -5, 10), new Range(-150, 650), 0, w, 1, h, w, numZ, h * w);
	} else if (type.equals("index")) {
		byte[] pixels = new byte[w * h * numZ];
		for (int i = 0; i < pixels.length; i += 1) {
			pixels[i] = (byte) (i % 256);
		}
		sdi = new SourceDataInfo(SourceDataInfo.INDEX_TYPE, pixels, new org.vcell.util.Extent(1, 2, 3), new org.vcell.util.Origin(0, -5, 10), null, 0, w, 1, h, w, numZ, h * w);
	} else if(type.equals("facecube")){
		numZ = 50;
		w = 50;
		h = 50;
		int[] pixels = new int[w*h*numZ];
		int[] facePixels = new int[w*h];
		java.awt.image.BufferedImage face = new java.awt.image.BufferedImage(w,h,java.awt.image.BufferedImage.TYPE_INT_RGB);
		java.awt.Graphics2D g2d = face.createGraphics();
		g2d.setRenderingHint(java.awt.RenderingHints.KEY_FRACTIONALMETRICS,java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g2d.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setFont(new java.awt.Font("SansSerif",java.awt.Font.PLAIN,6));
		//g2d.scale(2,2);
		//java.awt.Font smallFont = g2d.getFont().deriveFont(10);
		//g2d.setFont(smallFont);
		int xStart = 0;
		int xEnd = 0;
		int xIncr = 0;
		int yStart = 0;
		int yEnd = 0;
		int yIncr = 0;
		int zStart = 0;
		int zEnd = 0;
		int zIncr = 0;
		for(int i=0;i<2;i+= 1){
			g2d.setColor(java.awt.Color.red);
			g2d.fillRect(0,0,w,h);
			g2d.setColor(java.awt.Color.white);
			switch(i){
				case 0:
				xStart = 0;
				xEnd = w;
				xIncr = 1;
				yStart = 0;
				yEnd = w*h;
				yIncr = w;
				zStart = 0;
				zEnd = w*h*numZ;
				zIncr = w*h;
				g2d.drawString("- FRNT(-) +",0,15);
				g2d.drawString("- FRNT (+) +",0,h-2);
				break;
				case 1:
				xStart = 0;
				xEnd = w;
				xIncr = 1;
				yStart = 0;
				yEnd = w*h;
				yIncr = w;
				zStart = 0;
				zEnd = w*h*numZ;
				zIncr = w*h;
				g2d.drawString("- BTM(-) +",0,15);
				g2d.drawString("- BTM (+) +",0,h-2);
				break;
				case 2:
				break;
				case 3:
				break;
				case 4:
				break;
				case 5:
				break;
				default:
			}
			face.getRGB(0,0,w,h,facePixels,0,w);
			for(int z = zStart;z != zEnd;z+= zIncr*(zEnd-zStart < 0?-1:1)){
				int index = 0;
				for(int y = yStart;y != yEnd;y+= yIncr*(yEnd-yStart < 0?-1:1)){
					for(int x = xStart;x != xEnd;x+= xIncr*(xEnd-xStart < 0?-1:1)){
						pixels[x+(y)+(z)] = facePixels[index];
						index+= 1;
					}
				}
			}
		}
		sdi = new SourceDataInfo(SourceDataInfo.INT_RGB_TYPE, pixels, new org.vcell.util.Extent(1, 2, 3), new org.vcell.util.Origin(0, -5, 10), null, 0, w, 1, h, w, numZ, h * w);
	}
	//
	return sdi;
}


/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	// Insert code to start the application here.
	//Create default image
	int w = Integer.valueOf(args[2]).intValue();
	int h = Integer.valueOf(args[3]).intValue();
	String type = args[0];
	SourceDataInfo sdi = getExampleSDI(type, w, h);
	DisplayAdapterService das = null;
	if (type.equals("double")) {
		das = new DisplayAdapterService();
		das.setActiveScaleRange(new Range(0, 450));
		das.setValueDomain(new Range(-50, 550));
		das.addColorModelForValues(DisplayAdapterService.createGrayColorModel(), DisplayAdapterService.createGraySpecialColors(), DisplayAdapterService.GRAY);
		das.addColorModelForValues(DisplayAdapterService.createBlueRedColorModel(), DisplayAdapterService.createBlueRedSpecialColors(), DisplayAdapterService.BLUERED);
		das.setActiveColorModelID(DisplayAdapterService.GRAY);
	} else if (type.equals("index")) {
		das = new DisplayAdapterService();
		das.addColorModelForIndexes(DisplayAdapterService.createGrayColorModel(), DisplayAdapterService.GRAY);
		das.addColorModelForIndexes(DisplayAdapterService.createBlueRedColorModel(), DisplayAdapterService.BLUERED);
		das.setActiveColorModelID(DisplayAdapterService.GRAY);
	}
	//

	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ImagePaneScroller aImagePaneScroller;
		aImagePaneScroller = new ImagePaneScroller();
		aImagePaneScroller.getImagePaneModel().setSourceData(sdi);
		aImagePaneScroller.getImagePaneModel().setDisplayAdapterService(das);
		//aImagePaneScroller.getImagePaneModel().setBackgroundColor(java.awt.Color.yellow);
		if (args[1].equals("true")) {
			aImagePaneScroller.getImagePaneModel().setMode(ImagePaneModel.MESH_MODE);
		}
		javax.swing.JPanel jp = new javax.swing.JPanel();
		jp.setLayout(new java.awt.BorderLayout());
		jp.add(aImagePaneScroller);
		frame.setContentPane(jp);
		frame.setSize(aImagePaneScroller.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
}
