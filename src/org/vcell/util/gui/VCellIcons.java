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
	
	// Rule Based Modeling
	public final static Icon rbmIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm.png"));
//	public final static Icon rbmMolecularTypeIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_molecule.png"));
	public final static Icon rbmMolecularTypeIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_molecule_type.png"));
	public final static Icon rbmMolecularTypeSimpleIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_molecule_type2.png"));
	public final static Icon rbmSpeciesGreenIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_species_green.png"));
	public final static Icon rbmSpeciesBlueIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_species_blue.png"));
	public final static Icon rbmReactRuleDirectIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_react_rule_direct.png"));
	public final static Icon rbmReactRuleReversIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_react_rule_revers.png"));

//	public final static Icon rbmMolecularComponentIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_molecular_component.gif"));
	public final static Icon rbmComponentGreenIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_component_green.gif"));
	public final static Icon rbmComponentGreenStateIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_component_greenState.gif"));
	public final static Icon rbmComponentGrayIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_component_gray.gif"));
	public final static Icon rbmComponentGrayStateIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_component_grayState.gif"));
	public final static Icon rbmComponentErrorIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_component_error.gif"));

	public final static Icon rbmMolecularComponentErrIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_molecular_component_err.gif"));
	public final static Icon rbmComponentStateIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_component_state.png"));
	public final static Icon rbmObservableIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_observable.gif"));
	public final static Icon rbmReactantIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_reactant.png"));
	public final static Icon rbmProductIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_product.png"));

	public final static Icon rbmBondIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_bond.gif"));
	public final static Icon rbmBondNoneIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_bond_none.png"));
	public final static Icon rbmBondExistsIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_bond_exists.png"));
	public final static Icon rbmBondPossibleIcon = new ImageIcon(VCellIcons.class.getResource("/icons/rbm_bond_possible.png"));

	public final static Icon spatialMembraneKinematicsIcon = new ImageIcon(VCellIcons.class.getResource("/icons/spatial_membrane_kinematics.png"));
	public final static Icon spatialPointKinematicsIcon = new ImageIcon(VCellIcons.class.getResource("/icons/spatial_point_kinematics.png"));
	public final static Icon spatialPointLocationIcon = new ImageIcon(VCellIcons.class.getResource("/icons/spatial_point_location.png"));

	public final static Icon kineticsPropertiesIcon = new ImageIcon(VCellIcons.class.getResource("/icons/kinetics_properties.png"));
	public final static Icon editorPropertiesIcon = new ImageIcon(VCellIcons.class.getResource("/icons/editor_properties.png"));

	public final static Icon moveLeftIcon = new ImageIcon(VCellIcons.class.getResource("/icons/move_left.png"));
	public final static Icon moveRightIcon = new ImageIcon(VCellIcons.class.getResource("/icons/move_right.png"));

	// Chombo
	public final static Icon refLevelNewIcon = new ImageIcon(VCellIcons.class.getResource("/icons/reflevel_new.png"));
	public final static Icon refLevelDeleteIcon = new ImageIcon(VCellIcons.class.getResource("/icons/reflevel_delete.png"));
	
	// CSG
	public final static Icon csgSphereIcon = new ImageIcon(VCellIcons.class.getResource("/icons/csg_sphere.png"));
	public final static Icon csgCubeIcon = new ImageIcon(VCellIcons.class.getResource("/icons/csg_cube.png"));
	public final static Icon csgCylinderIcon = new ImageIcon(VCellIcons.class.getResource("/icons/csg_cylinder.png"));
	public final static Icon csgConeIcon = new ImageIcon(VCellIcons.class.getResource("/icons/csg_cone.png"));
	public final static Icon csgSetUnionIcon = new ImageIcon(VCellIcons.class.getResource("/icons/csg_set_union.png"));
	public final static Icon csgSetIntersectionIcon = new ImageIcon(VCellIcons.class.getResource("/icons/csg_set_intersection.png"));
	public final static Icon csgSetDifferenceIcon = new ImageIcon(VCellIcons.class.getResource("/icons/csg_set_difference.png"));
	public final static Icon csgRotationIcon = new ImageIcon(VCellIcons.class.getResource("/icons/csg_rotation.png"));
	public final static Icon csgTranslationIcon = new ImageIcon(VCellIcons.class.getResource("/icons/csg_translation.png"));
	public final static Icon csgScaleIcon = new ImageIcon(VCellIcons.class.getResource("/icons/csg_scale.png"));
	
	// table
	public final static Icon firstPageIcon = new ImageIcon(VCellIcons.class.getResource("/icons/first_page.png"));
	public final static Icon previousPageIcon = new ImageIcon(VCellIcons.class.getResource("/icons/previous_page.png"));
	public final static Icon nextPageIcon = new ImageIcon(VCellIcons.class.getResource("/icons/next_page.png"));
	public final static Icon lastPageIcon = new ImageIcon(VCellIcons.class.getResource("/icons/last_page.png"));
	
	public final static Icon issueErrorIcon = new ImageIcon(VCellIcons.class.getResource("/icons/issueError.png"));
	public final static Icon issueWarningIcon = new ImageIcon(VCellIcons.class.getResource("/icons/issueWarning.png"));
	public final static Icon issueGoodIcon = new ImageIcon(VCellIcons.class.getResource("/icons/issueGood.png"));

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
	public final static Icon glgLayoutIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/layout/glg3.gif"));
	public final static Icon shrinkLayoutIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/layout/shrink.gif"));
	public final static Icon expandLayoutIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/layout/expand.gif"));

	public final static Icon pathwayReactionIcon = new ImageIcon(VCellIcons.class.getResource("/images/step.gif"));
	public final static Icon pathwayTransportIcon = new ImageIcon(VCellIcons.class.getResource("/images/flux.gif"));
	public final static Icon pathwayReactionWtIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/biopax/transportWithBiochemicalReaction.gif"));
	public final static Icon pathwayEntityIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/biopax/entity.gif"));
	public final static Icon pathwaySmallMoleculeIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/biopax/smallMolecule.gif"));
	public final static Icon pathwayProteinIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/biopax/protein.gif"));
	public final static Icon pathwayComplexIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/biopax/complex.gif"));
	public final static Icon pathwayParticipantsIcon = new ImageIcon(VCellIcons.class.getResource("/sybil/images/biopax/modification.gif"));

	public final static Icon pathwayLeafIcon = new ImageIcon(VCellIcons.class.getResource("/icons/pathwayLeaf.gif"));
	public final static Icon kineticLawIcon = new ImageIcon(VCellIcons.class.getResource("/icons/kineticLaw.gif"));
	public final static Icon kineticLaw2Icon = new ImageIcon(VCellIcons.class.getResource("/icons/kineticLaw2.gif"));
	public final static Icon sortUpIcon = new ImageIcon(VCellIcons.class.getResource("/images/sortUp.png"));
	public final static Icon sortDownIcon = new ImageIcon(VCellIcons.class.getResource("/images/sortDown.png"));

	public final static Icon textNotesIcon = new ImageIcon(VCellIcons.class.getResource("/images/text_16x16.gif"));
	public final static Icon mathTypeIcon = new ImageIcon(VCellIcons.class.getResource("/images/type.gif"));
	
//	public final static Icon applicationIcon = new ImageIcon(VCellIcons.class.getResource("/images/application3_16x16.gif"));
	public final static Icon appDetSpatialIcon = new ImageIcon(VCellIcons.class.getResource("/images/app_det_spatial_28x16.png"));
	public final static Icon appDetNonspIcon = new ImageIcon(VCellIcons.class.getResource("/images/app_det_nonsp_28x16.png"));
	public final static Icon appStoSpatialIcon = new ImageIcon(VCellIcons.class.getResource("/images/app_sto_spatial_28x16.png"));
	public final static Icon appStoNonspIcon = new ImageIcon(VCellIcons.class.getResource("/images/app_sto_nonsp_28x16.png"));
	public final static Icon appRbmNonspIcon = new ImageIcon(VCellIcons.class.getResource("/images/app_rbm_nonsp_28x16.png"));
	
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
	
	//Window Manager
	public final static Icon SHOW_WINDOWS = new ImageIcon(VCellIcons.class.getResource("/icons/showWindows.png"));
	
	
	public final static Icon addIcon = new ImageIcon(VCellIcons.class.getResource("/images/add.gif"));
	public final static Icon deleteIcon = new ImageIcon(VCellIcons.class.getResource("/images/delete.gif"));
	
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

}
