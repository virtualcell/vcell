/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package org.vcell.util.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import org.vcell.util.BeanUtils;

public abstract class VCellIcons {
	public static HashMap<String,URL> imagePaths = new HashMap<String,URL>();
	
	private static URL getResourceUrl(String resourcePath) {
		URL url = VCellIcons.class.getResource(resourcePath);
		imagePaths.put(resourcePath,url);
		return url;
	}
	
	public static Image makeImage(String resourcePath){
		URL resourceUrl = getResourceUrl(resourcePath);
		if (resourceUrl==null){
			return null;
		}
		return Toolkit.getDefaultToolkit().getImage(resourceUrl);
	}

	public static BufferedImage makeBufferedImage(String resourcePath) throws IOException{
		URL resourceUrl = getResourceUrl(resourcePath);
		if (resourceUrl==null){
			return null;
		}
		return ImageIO.read(resourceUrl);
	}
	
	public static ImageIcon makeIcon(String resourcePath){
		URL resourceUrl = getResourceUrl(resourcePath);
		if (resourceUrl==null){
			return null;
		}
		return new ImageIcon(resourceUrl);
	}
	
	public final static int VCellIconWidth = 16;
	public final static int VCellIconHeight = 16;

	public final static Image panCursorImage = makeImage("/pan.gif");
	public final static Image zoomCursorImage = makeImage("/zoom.gif");
	
	public final static Icon dataSetsIcon = makeIcon("/data_sets_20x20.gif");
	public final static Icon dataExporterIcon = makeIcon("/data_exporter_20x20.gif");
	
	// Attachments, Links
	public final static Icon bookmarkIcon = makeIcon("/icons/bookmark.png");
	public final static Icon certificateIcon = makeIcon("/icons/certificate2.png");
	public final static Icon linkIcon = makeIcon("/icons/link.png");
	public final static Icon noteIcon = makeIcon("/icons/note3.png");

	// Rule Based Modeling
	public final static Icon rbmIcon = makeIcon("/icons/rbm.png");
//	public final static Icon rbmMolecularTypeIcon = makeIcon("/icons/rbm_molecule.png");
	public final static Icon rbmMolecularTypeIcon = makeIcon("/icons/rbm_molecule_type.png");
	public final static Icon rbmMolecularTypeSimpleIcon = makeIcon("/icons/rbm_molecule_type2.png");
	public final static Icon rbmSpeciesGreenIcon = makeIcon("/icons/rbm_species_green.png");
	public final static Icon rbmSpeciesBlueIcon = makeIcon("/icons/rbm_species_blue.png");
	public final static Icon rbmReactRuleDirectIcon = makeIcon("/icons/rbm_react_rule_direct.png");
	public final static Icon rbmReactRuleReversIcon = makeIcon("/icons/rbm_react_rule_revers.png");

//	public final static Icon rbmMolecularComponentIcon = makeIcon("/icons/rbm_molecular_component.gif");
	public final static Icon rbmComponentGreenIcon = makeIcon("/icons/rbm_component_green.gif");
	public final static Icon rbmComponentGreenStateIcon = makeIcon("/icons/rbm_component_greenState.gif");
	public final static Icon rbmComponentGrayIcon = makeIcon("/icons/rbm_component_gray.gif");
	public final static Icon rbmComponentGrayStateIcon = makeIcon("/icons/rbm_component_grayState.gif");
	public final static Icon rbmComponentErrorIcon = makeIcon("/icons/rbm_component_error.gif");

	public final static Icon rbmMolecularComponentErrIcon = makeIcon("/icons/rbm_molecular_component_err.gif");
	public final static Icon rbmComponentStateIcon = makeIcon("/icons/rbm_component_state.png");
	public final static Icon rbmObservableIcon = makeIcon("/icons/rbm_observable.gif");
	public final static Icon rbmReactantIcon = makeIcon("/icons/rbm_reactant.png");
	public final static Icon rbmProductIcon = makeIcon("/icons/rbm_product.png");

	public final static Icon rbmBondIcon = makeIcon("/icons/rbm_bond.gif");
	public final static Icon rbmBondNoneIcon = makeIcon("/icons/rbm_bond_none.png");
	public final static Icon rbmBondExistsIcon = makeIcon("/icons/rbm_bond_exists.png");
	public final static Icon rbmBondPossibleIcon = makeIcon("/icons/rbm_bond_possible.png");

	public final static Icon spatialPointIcon = makeIcon("/icons/spatial_point.png");
	public final static Icon spatialMembraneIcon = makeIcon("/icons/spatial_membrane2.png");
	public final static Icon spatialVolumeIcon = makeIcon("/icons/spatial_volume.png");
	public final static Icon spatialKinematicsIcon = makeIcon("/icons/spatial_kinematic.png");
	public final static Icon spatialLocationIcon = makeIcon("/icons/spatial_location2.png");

	public final static Icon kineticsPropertiesIcon = makeIcon("/icons/kinetics_properties.png");
	public final static Icon editorPropertiesIcon = makeIcon("/icons/editor_properties.png");

	public final static Icon moveLeftIcon = makeIcon("/icons/move_left.png");
	public final static Icon moveRightIcon = makeIcon("/icons/move_right.png");

	// Chombo
	public final static Icon refLevelNewIcon = makeIcon("/icons/reflevel_new.png");
	public final static Icon refLevelDeleteIcon = makeIcon("/icons/reflevel_delete.png");
	
	// CSG
	public final static Icon csgSphereIcon = makeIcon("/icons/csg_sphere.png");
	public final static Icon csgCubeIcon = makeIcon("/icons/csg_cube.png");
	public final static Icon csgCylinderIcon = makeIcon("/icons/csg_cylinder.png");
	public final static Icon csgConeIcon = makeIcon("/icons/csg_cone.png");
	public final static Icon csgSetUnionIcon = makeIcon("/icons/csg_set_union.png");
	public final static Icon csgSetIntersectionIcon = makeIcon("/icons/csg_set_intersection.png");
	public final static Icon csgSetDifferenceIcon = makeIcon("/icons/csg_set_difference.png");
	public final static Icon csgRotationIcon = makeIcon("/icons/csg_rotation.png");
	public final static Icon csgTranslationIcon = makeIcon("/icons/csg_translation.png");
	public final static Icon csgScaleIcon = makeIcon("/icons/csg_scale.png");
	
	// table
	public final static Icon firstPageIcon = makeIcon("/icons/first_page.png");
	public final static Icon previousPageIcon = makeIcon("/icons/previous_page.png");
	public final static Icon nextPageIcon = makeIcon("/icons/next_page.png");
	public final static Icon lastPageIcon = makeIcon("/icons/last_page.png");
	
	public final static Icon issueErrorIcon = makeIcon("/icons/issueError.png");
	public final static Icon issueWarningIcon = makeIcon("/icons/issueWarning.png");
	public final static Icon issueGoodIcon = makeIcon("/icons/issueGood.png");

	// pathway
	public final static Icon pathwaySelectIcon = makeIcon("/images/layout/select.gif");
	public final static Icon pathwayZoomInIcon = makeIcon("/images/layout/zoomin.gif");
	public final static Icon pathwayZoomOutIcon = makeIcon("/images/layout/zoomout.gif");
	public final static Icon pathwayRandomIcon = makeIcon("/images/layout/random.gif");
	public final static Icon pathwayCircularIcon = makeIcon("/images/layout/circular.gif");
	public final static Icon pathwayAnnealedIcon = makeIcon("/images/layout/annealed.gif");
	public final static Icon pathwayLevelledIcon = makeIcon("/images/layout/levelled.gif");
	public final static Icon pathwayRelaxedIcon = makeIcon("/images/layout/relaxed.gif");
	public final static Icon pathwayReactionsOnlyIcon = makeIcon("/images/layout/level1.gif");
	public final static Icon pathwayReactionNetworkIcon = makeIcon("/images/layout/level2.gif");
	public final static Icon pathwayComponentsIcon = makeIcon("/images/layout/level3.gif");
	public final static Icon glgLayoutIcon = makeIcon("/images/layout/glg3.gif");
	public final static Icon shrinkLayoutIcon = makeIcon("/images/layout/shrink.gif");
	public final static Icon expandLayoutIcon = makeIcon("/images/layout/expand.gif");

	public final static Icon pathwayReactionIcon = makeIcon("/images/step.gif");
	public final static Icon pathwayTransportIcon = makeIcon("/images/flux.gif");
	public final static Icon pathwayReactionWtIcon = makeIcon("/images/biopax/transportWithBiochemicalReaction.gif");
	public final static Icon pathwayEntityIcon = makeIcon("/images/biopax/entity.gif");
	public final static Icon pathwaySmallMoleculeIcon = makeIcon("/images/biopax/smallMolecule.gif");
	public final static Icon pathwayProteinIcon = makeIcon("/images/biopax/protein.gif");
	public final static Icon pathwayComplexIcon = makeIcon("/images/biopax/complex.gif");
	public final static Icon pathwayParticipantsIcon = makeIcon("/images/biopax/modification.gif");

	public final static Icon pathwayLeafIcon = makeIcon("/icons/pathwayLeaf.gif");
	public final static Icon kineticLawIcon = makeIcon("/icons/kineticLaw.gif");
	public final static Icon kineticLaw2Icon = makeIcon("/icons/kineticLaw2.gif");
	public final static Icon sortUpIcon = makeIcon("/images/sortUp.png");
	public final static Icon sortDownIcon = makeIcon("/images/sortDown.png");

	public final static Icon textNotesIcon = makeIcon("/images/text_16x16.gif");
	public final static Icon mathTypeIcon = makeIcon("/images/type.gif");
	
//	public final static Icon applicationIcon = makeIcon("/images/application3_16x16.gif");
	public final static Icon appDetSpatialIcon = makeIcon("/images/app_det_spatial_28x16.png");
	public final static Icon appDetNonspIcon = makeIcon("/images/app_det_nonsp_28x16.png");
	public final static Icon appStoSpatialIcon = makeIcon("/images/app_sto_spatial_28x16.png");
	public final static Icon appStoNonspIcon = makeIcon("/images/app_sto_nonsp_28x16.png");
	public final static Icon appRbmNonspIcon = makeIcon("/images/app_rbm_nonsp_28x16.png");
	
	public final static Icon documentIcon = makeIcon("/icons/document_icon.png");
	public final static Icon mathModelIcon = makeIcon("/images/math_16x16.gif");
	
	// application
	public final static Icon geometryIcon = getScaledIcon(makeIcon("/images/geometry2_16x16.gif"));
	public final static Icon simulationIcon = getScaledIcon(makeIcon("/images/run2_16x16.gif"));
	public final static Icon settingsIcon = getScaledIcon(makeIcon("/icons/app_settings.gif"));
	public final static Icon protocolsIcon = getScaledIcon(makeIcon("/icons/app_protocols.png"));
	public final static Icon fittingIcon = getScaledIcon(makeIcon("/icons/app_fitting.gif"));
	
	// model
	public final static Icon tableIcon = getScaledIcon(makeIcon("/icons/table_icon.gif"));
	public final static Icon structureIcon = getScaledIcon(makeIcon("/images/bioModel_16x16.gif"));
	public final static Icon diagramIcon = getScaledIcon(makeIcon("/icons/diagram_icon.gif"));
	public final static Icon modelCuratedIcon = makeIcon("/icons/curatedComponent.png");
	
	private static Icon oldOutputFunctionIcon = null;
	private static Icon outputFunctionIcon = null;
	private static BufferedImage jFrameIconImage = null;
	
	// simulation
	public final static Icon newSimIcon = makeIcon("/icons/sim_new.gif");
	public final static Icon copySimIcon = makeIcon("/icons/sim_copy.gif");
	public final static Icon delSimIcon = makeIcon("/icons/sim_del.gif");
	public final static Icon editSimIcon = makeIcon("/icons/sim_edit.gif");
	public final static Icon runSimIcon = makeIcon("/icons/sim_run.gif");
	public final static Icon particleRunSimIcon = makeIcon("/icons/particle_run.gif");
	public final static Icon stopSimIcon = makeIcon("/icons/sim_stop.gif");
	public final static Icon resultsIcon = makeIcon("/icons/sim_results.jpg");
	public final static Icon statusDetailscon = makeIcon("/icons/sim_status_details.gif");
	public final static Icon odeQuickRunIcon = makeIcon("/icons/ode_quick_run.png");
	public final static Icon pythonQuickRunIcon = makeIcon("/icons/python_quick_run.png");
	
	//Window Manager
	public final static Icon SHOW_WINDOWS = makeIcon("/icons/showWindows.png");
	
	
	public final static Icon addIcon = makeIcon("/images/add.gif");
	public final static Icon deleteIcon = makeIcon("/images/delete.gif");
	
	public static Icon getOldOutputFunctionIcon() {
		if (oldOutputFunctionIcon == null) {
			oldOutputFunctionIcon = makeIcon("/icons/old_function_icon.png");
		}
		return oldOutputFunctionIcon;
	}
	public static Icon getOutputFunctionIcon() {
		if (outputFunctionIcon == null) {
			outputFunctionIcon = makeIcon("/icons/function_icon.png");
		}
		return outputFunctionIcon;
	}
	
	public static BufferedImage getJFrameImageIcon() {
		if (jFrameIconImage == null) {
			try {
				jFrameIconImage = makeBufferedImage("/icons/vcell.gif");
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
	
	private static Icon getScaledIcon(Icon tempIcon) {
		return getScaledIcon(null, tempIcon);
	}
	
	public static Icon getScaledIcon(Component component, Icon tempIcon) {
		if (tempIcon == null) {
			return null;
		}
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
	
	public static Icon addIcon(Icon icon1, Icon icon2) {
		int separatorWidth = 3;
		Image image1 = ((ImageIcon) icon1).getImage(); 
		Image image2  = ((ImageIcon) icon2).getImage();
		int w = icon1.getIconWidth() + icon2.getIconWidth() + separatorWidth;
		int h = Math.max(icon1.getIconHeight(), icon2.getIconHeight());
		Image image = new BufferedImage(w, h,  BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.drawImage(image1, 0, 0, null);
		g2.drawImage(image2, icon1.getIconWidth() + separatorWidth, 0, null);
		g2.dispose();
		ImageIcon icon = new ImageIcon(image);
		return icon;
	}

	// ---------------------------------------------------------------------------------------------------

	public static void main(java.lang.String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			javax.swing.JFrame frame = new javax.swing.JFrame();
			JPanel panel =  new JPanel(new BorderLayout());
			panel.add(new JLabel("label", refLevelDeleteIcon, SwingConstants.LEFT), BorderLayout.CENTER);
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
