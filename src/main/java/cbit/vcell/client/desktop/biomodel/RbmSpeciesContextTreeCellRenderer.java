package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.SpeciesPattern.Bond;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.BondLocal;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.ReactionRuleParticipantLocal;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.SpeciesPatternLocal;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.StateLocal;
import cbit.vcell.client.desktop.biomodel.RbmTreeCellEditor.MolecularComponentPatternCellEditor;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.graph.AbstractComponentShape;
import cbit.vcell.graph.GraphConstants;
import cbit.vcell.graph.MolecularTypeSmallShape;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule.ReactionRuleParticipantType;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.model.SpeciesContext;
@SuppressWarnings("serial")
public class RbmSpeciesContextTreeCellRenderer extends RbmTreeCellRenderer {
	
	Object obj = null;
	Component owner = null;
	
	public RbmSpeciesContextTreeCellRenderer(Component owner) {
		super();
		this.owner = owner;
		setBorder(new EmptyBorder(0, 2, 0, 0));		
	}
	
	@Override
	public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		setBorder(null);
		if (value instanceof BioModelNode) {
			BioModelNode node = (BioModelNode)value;
			Object userObject = node.getUserObject();
			obj = userObject;
			String text = null;
			Icon icon = null;
			String toolTip = null;
			if(userObject instanceof SpeciesContext) {
				SpeciesContext sc = (SpeciesContext)userObject;
				text = toHtml(sc);
				toolTip = toHtml(sc);
				if(sc.hasSpeciesPattern()) {
					icon = VCellIcons.rbmSpeciesBlueIcon;
				} else {
					icon = VCellIcons.rbmSpeciesGreenIcon;
				}
			} else if (userObject instanceof MolecularTypePattern) {
				MolecularTypePattern molecularTypePattern = (MolecularTypePattern) userObject;
				text = toHtml(molecularTypePattern, true);
				toolTip = toHtml(molecularTypePattern, true);
				if(owner == null) {
					icon = VCellIcons.rbmMolecularTypeSimpleIcon;;
				} else {
					Graphics gc = owner.getGraphics();
					icon = new MolecularTypeSmallShape(1, 5, molecularTypePattern.getMolecularType(), null, gc, molecularTypePattern.getMolecularType(), null);
				}
			} else if (userObject instanceof MolecularComponentPattern) {
				MolecularComponentPattern mcp = (MolecularComponentPattern) userObject;
				text = toHtml(mcp, true);
				toolTip = toHtmlWithTip(mcp, true);
				icon = VCellIcons.rbmComponentGreenIcon;
				if(mcp.getMolecularComponent().getComponentStateDefinitions().size() > 0) {
					icon = VCellIcons.rbmComponentGreenStateIcon;
				}
			} else if (userObject instanceof StateLocal) {
				// this code is still here but we don't show the states or the bonds in the tree anymore
				StateLocal sl = (StateLocal) userObject;
				text = toHtml(sl, true);
				toolTip = toHtmlWithTip(sl,true);
				icon = VCellIcons.rbmComponentStateIcon;
			} else if (userObject instanceof BondLocal) {
				BondLocal bl = (BondLocal)userObject;
				text = toHtml(bl,sel);
				toolTip = toHtmlWithTip(bl,true);
				icon = VCellIcons.rbmBondIcon;
			} else {
				if(userObject != null) {
					System.out.println(userObject.toString());
					text = userObject.toString();
				} else {
					text = "null user object";
				}
			}
			setText(text);
			setIcon(icon);
			setToolTipText(toolTip == null ? text : toolTip);
		}
		return this;
	}
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		int x = 4;
		int y = 16;
		if(!(obj instanceof MolecularComponentPattern)) {
			return;
		}
		MolecularComponentPattern mcp = (MolecularComponentPattern)obj;
		Graphics2D g2 = (Graphics2D)g;
		Color colorOld = g2.getColor();
		if(mcp.getBondType() == BondType.Specified) {
			Color bondColor = GraphConstants.bondHtmlColors[mcp.getBondId()];
			g2.setColor(bondColor);
			g2.drawLine(x, y, x, y+2);		// 2 lines, L-shaped
			g2.drawLine(x+1, y, x+1, y+2);
			g2.drawLine(x, y+2, x+7, y+2);
			g2.drawLine(x, y+3, x+7, y+3);
		}
		g2.setColor(colorOld);
	}

}