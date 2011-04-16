package org.vcell.util.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.vcell.util.BeanUtils;

public abstract class VCellIcons {
	
	public final static int VCellIconWidth = 16;
	public final static int VCellIconHeight = 16;
	// table
	public final static Icon firstPageIcon = new ImageIcon(VCellIcons.class.getResource("/icons/first_page.png"));
	public final static Icon previousPageIcon = new ImageIcon(VCellIcons.class.getResource("/icons/previous_page.png"));
	public final static Icon nextPageIcon = new ImageIcon(VCellIcons.class.getResource("/icons/next_page.png"));
	public final static Icon lastPageIcon = new ImageIcon(VCellIcons.class.getResource("/icons/last_page.png"));
	
	// pathway
	public final static Icon pathwaySelectIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/layout/select.gif"));
	public final static Icon pathwayZoomInIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/layout/zoomin.gif"));
	public final static Icon pathwayZoomOutIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/layout/zoomout.gif"));
	public final static Icon pathwayRandomIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/layout/random.gif"));
	public final static Icon pathwayCircularIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/layout/circular.gif"));
	public final static Icon pathwayAnnealedIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/layout/annealed.gif"));
	public final static Icon pathwayLevelledIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/layout/levelled.gif"));
	public final static Icon pathwayRelaxedIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/layout/relaxed.gif"));
	public final static Icon pathwayReactionsOnlyIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/layout/level1.gif"));
	public final static Icon pathwayReactionNetworkIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/layout/level2.gif"));
	public final static Icon pathwayComponentsIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/layout/level3.gif"));
	
	public final static Icon pathwayReactionIcon = new ImageIcon(VCellIcons.class.getResource("/images/step.gif"));
	public final static Icon pathwayTransportIcon = new ImageIcon(VCellIcons.class.getResource("/images/flux.gif"));
	public final static Icon pathwayReactionWtIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/biopax/transportWithBiochemicalReaction.gif"));
	public final static Icon pathwayEntityIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/biopax/entity.gif"));
	public final static Icon pathwaySmallMoleculeIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/biopax/smallMolecule.gif"));
	public final static Icon pathwayProteinIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/biopax/protein.gif"));
	public final static Icon pathwayComplexIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/biopax/complex.gif"));
	public final static Icon pathwayParticipantsIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/biopax/modification.gif"));

	public final static Icon textNotesIcon = new ImageIcon(VCellIcons.class.getResource("/images/text_16x16.gif"));
	public final static Icon mathTypeIcon = new ImageIcon(VCellIcons.class.getResource("/images/type.gif"));
	public final static Icon applicationIcon = new ImageIcon(VCellIcons.class.getResource("/images/application3_16x16.gif"));
	public final static Icon documentIcon = new ImageIcon(VCellIcons.class.getResource("/icons/document_icon.png"));
	public final static Icon mathModelIcon = new ImageIcon(VCellIcons.class.getResource("/images/math_16x16.gif"));
	
	// application
	public final static Icon geometryIcon = getScaledIcon(new ImageIcon(VCellIcons.class.getResource("/images/geometry2_16x16.gif")));
	public final static Icon simulationIcon = getScaledIcon(new ImageIcon(VCellIcons.class.getResource("/images/run2_16x16.gif")));
	public final static Icon settingsIcon = getScaledIcon(new ImageIcon(VCellIcons.class.getResource("/icons/app_settings.gif")));
	public final static Icon protocolsIcon = getScaledIcon(new ImageIcon(VCellIcons.class.getResource("/icons/app_protocols.png")));
	public final static Icon fittingIcon = getScaledIcon(new ImageIcon(VCellIcons.class.getResource("/icons/app_fitting.gif")));
	
	// model
	public final static Icon tableIcon = getScaledIcon(new ImageIcon(VCellIcons.class.getResource("/icons/table_icon.gif")));
	public final static Icon structureIcon = getScaledIcon(new ImageIcon(VCellIcons.class.getResource("/images/bioModel_16x16.gif")));
	public final static Icon diagramIcon = getScaledIcon(new ImageIcon(VCellIcons.class.getResource("/icons/diagram_icon.gif")));
	
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
	public final static Icon odeQuickRunIcon = new ImageIcon(VCellIcons.class.getResource("/icons/ode_quick_run.png"));
	
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

	private static Icon errorIcon = null;
	private static Icon warningIcon = null;
	private static Icon infoIcon = null;
	public static Icon getErrorIcon() {
		if (errorIcon == null) {
			errorIcon = getScaledIcon(UIManager.getIcon("OptionPane.errorIcon"));
		}
		return errorIcon;
	}

	public static Icon getWarningIcon() {
		if (warningIcon == null) {
			warningIcon = getScaledIcon(UIManager.getIcon("OptionPane.warningIcon"));
		}
		return warningIcon;
	}
	
	public static Icon getInfoIcon() {
		if (infoIcon == null) {
			infoIcon = getScaledIcon(UIManager.getIcon("OptionPane.informationIcon"));
		}
		return infoIcon;
	}
	
	public static void main(java.lang.String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			javax.swing.JFrame frame = new javax.swing.JFrame();
			JPanel panel =  new JPanel(new BorderLayout());
			panel.add(new JLabel("label", odeQuickRunIcon, SwingConstants.LEFT), BorderLayout.CENTER);
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

	private static Icon getScaledIcon(Icon tempIcon) {
		return getScaledIcon(null, tempIcon);
	}
	
	public static Icon getScaledIcon(Component component, Icon tempIcon) {
		if (tempIcon.getIconWidth() == VCellIconWidth && tempIcon.getIconHeight() == VCellIconHeight) {
			return tempIcon;
		}
		
		Image image = null;
		if (tempIcon instanceof ImageIcon) {
			image = ((ImageIcon)tempIcon).getImage();
		} else {
			image = new BufferedImage(tempIcon.getIconWidth(), tempIcon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
			final Graphics imageGraphics = image.getGraphics();
			tempIcon.paintIcon(component, imageGraphics, 0, 0);
		}
		image = image.getScaledInstance(VCellIconWidth, VCellIconHeight, Image.SCALE_SMOOTH);		
		return new ImageIcon(image);
	}

}
