package org.vcell.util.gui;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.vcell.util.BeanUtils;

public abstract class VCellIcons {
	
	public final static Icon textNotesIcon = new ImageIcon(VCellIcons.class.getResource("/images/text_16x16.gif"));
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
	
	// simulation
	public final static Icon newSimIcon = new ImageIcon(VCellIcons.class.getResource("/icons/sim_new.gif"));
	public final static Icon copySimIcon = new ImageIcon(VCellIcons.class.getResource("/icons/sim_copy.gif"));
	public final static Icon delSimIcon = new ImageIcon(VCellIcons.class.getResource("/icons/sim_del.gif"));
	public final static Icon editSimIcon = new ImageIcon(VCellIcons.class.getResource("/icons/sim_edit.gif"));
	public final static Icon runSimIcon = new ImageIcon(VCellIcons.class.getResource("/icons/sim_run.gif"));
	public final static Icon particleRunSimIcon = new ImageIcon(VCellIcons.class.getResource("/icons/particle_run.gif"));
	public final static Icon stopSimIcon = new ImageIcon(VCellIcons.class.getResource("/icons/sim_stop.gif"));
	public final static Icon resultsIcon = new ImageIcon(VCellIcons.class.getResource("/icons/sim_results.jpg"));
	public final static Icon statusDetailscon = new ImageIcon(VCellIcons.class.getResource("/icons/sim_status_details.gif"));
	
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

	public static void main(java.lang.String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			javax.swing.JFrame frame = new javax.swing.JFrame();
			JPanel panel =  new JPanel(new BorderLayout());
			panel.add(new JButton("", tableIcon), BorderLayout.CENTER);
			frame.add(panel);
			frame.addWindowListener(new java.awt.event.WindowAdapter() {
				public void windowClosing(java.awt.event.WindowEvent e) {
					System.exit(0);
				};
			});
			frame.setSize(200,200);
			BeanUtils.centerOnScreen(frame);
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}

}
