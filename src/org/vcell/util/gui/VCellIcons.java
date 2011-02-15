package org.vcell.util.gui;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public abstract class VCellIcons {
	
	public final static Icon mathTypeIcon = new ImageIcon(VCellIcons.class.getResource("/images/type.gif"));
	public final static Icon applicationIcon = new ImageIcon(VCellIcons.class.getResource("/images/application3_16x16.gif"));
	
	// application
	public final static Icon geometryIcon = new ImageIcon(VCellIcons.class.getResource("/images/geometry2_16x16.gif"));
	public final static Icon simulationIcon = new ImageIcon(VCellIcons.class.getResource("/images/run2_16x16.gif"));
	public final static Icon settingsIcon = new ImageIcon(VCellIcons.class.getResource("/icons/app_settings.gif"));
	public final static Icon protocolsIcon = new ImageIcon(VCellIcons.class.getResource("/icons/app_protocols.png"));
	public final static Icon fittingIcon = new ImageIcon(VCellIcons.class.getResource("/icons/app_fitting.gif"));
	
	// model
	public final static Icon tableIcon = new ImageIcon(VCellIcons.class.getResource("/icons/table_icon.gif"));
	public final static Icon diagramIcon = new ImageIcon(VCellIcons.class.getResource("/icons/diagram_icon.gif"));
	
	private static Icon oldOutputFunctionIcon = null;
	private static Icon outputFunctionIcon = null;
	private static BufferedImage jFrameIconImage = null;
	
	public static Icon getOldOutputFunctionIcon() {
		if (oldOutputFunctionIcon == null) {
			oldOutputFunctionIcon = new ImageIcon(VCellIcons.class.getResource("/icons/old_function_icon.png"));
		}
		return oldOutputFunctionIcon;
	}
	public static Icon getOutputFunctionIcon() {
		if (outputFunctionIcon == null) {
			outputFunctionIcon = new ImageIcon(VCellIcons.class.getResource("/icons/function_icon.png"));
		}
		return outputFunctionIcon;
	}
	
	public static BufferedImage getJFrameImageIcon() {
		if (jFrameIconImage == null) {
			try {
				jFrameIconImage = ImageIO.read(VCellIcons.class.getResource("/icons/vcell.gif"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return jFrameIconImage;
	}

}
