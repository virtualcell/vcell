package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;

import org.vcell.model.rbm.ComponentStatePattern;
import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.util.gui.VCellIcons;

import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.BondLocal;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.ParticipantMatchLabelLocal;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.ReactionRuleParticipantLocal;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.StateLocal;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.graph.AbstractComponentShape;
import cbit.vcell.graph.MolecularTypeSmallShape;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.ReactionRule.ReactionRuleParticipantType;

@SuppressWarnings("serial")
public class RbmReactionParticipantTreeCellRenderer extends RbmTreeCellRenderer {

	Object obj = null;
	Component owner = null;

	public RbmReactionParticipantTreeCellRenderer(Component owner) {
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
			if (userObject instanceof ReactionRule) {
				ReactionRule rr = (ReactionRule) userObject;
				text = toHtml(rr);
				toolTip = toHtmlWithTip(rr);
				icon = rr.isReversible() ? VCellIcons.rbmReactRuleReversIcon : VCellIcons.rbmReactRuleDirectIcon;
			} else if (userObject instanceof ReactionRuleParticipantLocal) {
				ReactionRuleParticipantLocal rrp = (ReactionRuleParticipantLocal) userObject;
				text = toHtml(rrp,true);
				toolTip = toHtmlWithTip(rrp,true);
				icon = rrp.type == ReactionRuleParticipantType.Reactant ? VCellIcons.rbmReactantIcon : VCellIcons.rbmProductIcon;
			} else if (userObject instanceof MolecularTypePattern) {
				MolecularTypePattern molecularTypePattern = (MolecularTypePattern) userObject;
				text = toHtml(molecularTypePattern, true);
				toolTip = toHtmlWithTip(molecularTypePattern, true);
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
				icon = VCellIcons.rbmComponentGrayIcon;
				if(mcp.getMolecularComponent().getComponentStateDefinitions().size() > 0) {
					icon = VCellIcons.rbmComponentGrayStateIcon;
				}
				if(mcp.isbVisible()) {
					icon = VCellIcons.rbmComponentGreenIcon;
					if(mcp.getMolecularComponent().getComponentStateDefinitions().size() > 0) {
						icon = VCellIcons.rbmComponentGreenStateIcon;
					}
				}
				ComponentStatePattern csp = mcp.getComponentStatePattern();
				if(csp != null && !csp.isAny()) {
					icon = VCellIcons.rbmComponentGreenIcon;
					if(mcp.getMolecularComponent().getComponentStateDefinitions().size() > 0) {
						icon = VCellIcons.rbmComponentGreenStateIcon;
					}
				}
				BioModelNode parent = (BioModelNode) ((BioModelNode) value).getParent().getParent().getParent();
				if(parent == null) {
					icon = VCellIcons.rbmComponentErrorIcon;
					return this;
				}
			} else if (userObject instanceof StateLocal) {
				StateLocal sl = (StateLocal) userObject;
				text = toHtml(sl, true);
				toolTip = toHtmlWithTip(sl,true);
				icon = VCellIcons.rbmComponentStateIcon;
			} else if (userObject instanceof BondLocal) {
				BondLocal bl = (BondLocal)userObject;
				text = toHtml(bl,sel);
				toolTip = toHtmlWithTip(bl,true);
				icon = VCellIcons.rbmBondIcon;
			} else if (userObject instanceof ParticipantMatchLabelLocal) {
				ParticipantMatchLabelLocal pmll = (ParticipantMatchLabelLocal)userObject;
				text = toHtml(pmll,sel);
				toolTip = toHtmlWithTip(pmll,true);
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
			Color bondColor = RbmTreeCellRenderer.bondHtmlColors[mcp.getBondId()];
			g2.setColor(bondColor);
			g2.drawLine(x, y, x, y+2);		// 2 lines, L-shaped
			g2.drawLine(x+1, y, x+1, y+2);
			g2.drawLine(x, y+2, x+7, y+2);
			g2.drawLine(x, y+3, x+7, y+3);
		} else if(mcp.getBondType().equals(BondType.Exists)) {
			g2.setColor(AbstractComponentShape.plusSignGreen);			// draw a green '+' sign
			g2.drawLine(x, y-1, x, y+4);		// vertical only
			g2.drawLine(x-1, y-1, x-1, y+4);
		}
		g2.setColor(colorOld);
	}

}
