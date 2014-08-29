package cbit.vcell.geometry.gui;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.vcell.util.gui.ColorIcon;
import org.vcell.util.gui.VCellIcons;

import cbit.image.DisplayAdapterService;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.geometry.CSGHomogeneousTransformation;
import cbit.vcell.geometry.CSGNode;
import cbit.vcell.geometry.CSGObject;
import cbit.vcell.geometry.CSGPrimitive;
import cbit.vcell.geometry.CSGRotation;
import cbit.vcell.geometry.CSGScale;
import cbit.vcell.geometry.CSGSetOperator;
import cbit.vcell.geometry.CSGTransformation;
import cbit.vcell.geometry.CSGTranslation;
import cbit.vcell.render.Vect3d;

@SuppressWarnings("serial")
public class CSGObjectTreeCellRenderer extends DefaultTreeCellRenderer {		
			
	static class CSGNodeLabel {
		String text;
		Icon icon;
	}

	private static int[] colormap;
	public CSGObjectTreeCellRenderer() {
		super();
		setBorder(new EmptyBorder(0, 2, 0, 0));
	}
	public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value instanceof BioModelNode) {
			BioModelNode node = (BioModelNode)value;
			Object userObj = node.getUserObject();
			CSGObjectTreeCellRenderer.CSGNodeLabel csgNodeLabel = new CSGObjectTreeCellRenderer.CSGNodeLabel();
			CSGObjectTreeCellRenderer.getCSGNodeLabel(userObj, csgNodeLabel);
			setText(csgNodeLabel.text);
			setIcon(csgNodeLabel.icon);
		}
		return this;
	}
	
	static int[] getColorMap() {
		if (colormap == null) {
			colormap = DisplayAdapterService.createContrastColorModel();
		}
		return colormap;
	}
	static CSGNodeLabel getCSGNodeLabel(Object object, CSGNodeLabel csgNodeLabel) {
		if (object instanceof CSGObject) {
			CSGObject csgObject = (CSGObject) object;
			csgNodeLabel.text = csgObject.getName();		
			java.awt.Color handleColor = new java.awt.Color(getColorMap()[csgObject.getHandle()]);
			csgNodeLabel.icon = new ColorIcon(15,15,handleColor);
		} else if (object instanceof CSGNode) {
			CSGNode csgNode = (CSGNode)object;
			csgNodeLabel.text = csgNode.getName();
			if (csgNode instanceof CSGPrimitive) {
				CSGPrimitive csgPrimitive = (CSGPrimitive)csgNode;
				switch (csgPrimitive.getType()) {
				case CONE:
					csgNodeLabel.icon = VCellIcons.csgConeIcon;
					break;
				case CUBE:
					csgNodeLabel.icon = VCellIcons.csgCubeIcon;
					break;
				case CYLINDER:
					csgNodeLabel.icon = VCellIcons.csgCylinderIcon;
					break;
				case SPHERE:
					csgNodeLabel.icon = VCellIcons.csgSphereIcon;
					break;
				}
				return csgNodeLabel;
			}
			if (csgNode instanceof CSGSetOperator) {
				CSGSetOperator csgSetOperator = (CSGSetOperator)csgNode;
				switch (csgSetOperator.getOpType()) {
				case DIFFERENCE:
					csgNodeLabel.icon = VCellIcons.csgSetDifferenceIcon;				
					break;
				case INTERSECTION:
					csgNodeLabel.icon = VCellIcons.csgSetIntersectionIcon;				
					break;
				case UNION:
					csgNodeLabel.icon = VCellIcons.csgSetUnionIcon;
					break;
				}
			}
			if (csgNode instanceof CSGTransformation) {
				if (csgNode instanceof CSGRotation) {
					CSGRotation csgRotation = (CSGRotation)csgNode;
					Vect3d axis = csgRotation.getAxis();
					double radius = csgRotation.getRotationRadians();
					csgNodeLabel.text += ", radian=" + radius + ", axis=" + CSGObjectPropertiesPanel.getVect3dDescription(axis);
					csgNodeLabel.icon = VCellIcons.csgRotationIcon;
				} else if (csgNode instanceof CSGTranslation) {
					CSGTranslation csgTranslation = (CSGTranslation)csgNode;
					Vect3d translation = csgTranslation.getTranslation();
					csgNodeLabel.text += ", Translation=" + CSGObjectPropertiesPanel.getVect3dDescription(translation);
					csgNodeLabel.icon = VCellIcons.csgTranslationIcon;				
				} else if (csgNode instanceof CSGScale) {
					CSGScale csgScale = (CSGScale) csgNode;
					Vect3d scale = csgScale.getScale();
					csgNodeLabel.text += ", Scale=" + CSGObjectPropertiesPanel.getVect3dDescription(scale);				
					csgNodeLabel.icon = VCellIcons.csgScaleIcon;
				} else if (csgNode instanceof CSGHomogeneousTransformation){
					csgNodeLabel.icon = null;
				}
			}
		}
		return null;
	}
}