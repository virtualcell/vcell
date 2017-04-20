package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
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
import cbit.vcell.graph.MolecularTypeSmallShape;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule.ReactionRuleParticipantType;
import cbit.vcell.model.common.VCellErrorMessages;
import cbit.vcell.model.SpeciesContext;
@SuppressWarnings("serial")
public class RbmMolecularTypeTreeCellRenderer extends RbmTreeCellRenderer {
	
	Component owner = null;

	public RbmMolecularTypeTreeCellRenderer(Component owner) {
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
			String text = null;
			String toolTip = null;
			Icon icon = null;
			if (userObject instanceof MolecularType) {
				MolecularType mt = (MolecularType) userObject;
				text = toHtml(mt, true);
				toolTip = toHtmlWithTip(mt, true);
				if(owner == null) {
					icon = VCellIcons.rbmMolecularTypeSimpleIcon;;
				} else {
					Graphics gc = owner.getGraphics();
					icon = new MolecularTypeSmallShape(1, 4, mt, null, gc, mt, null);
				}
			} else if (userObject instanceof MolecularComponent) {
				BioModelNode parentNode = (BioModelNode) node.getParent();
				MolecularComponent mc = (MolecularComponent) userObject;
				text = toHtml(mc, true);
				toolTip = toHtmlWithTip(mc, true);
				icon = VCellIcons.rbmComponentGreenIcon;
				if(mc.getComponentStateDefinitions().size() > 0) {
					icon = VCellIcons.rbmComponentGreenStateIcon;
				}
				FontMetrics fm = getFontMetrics(getFont());		// here is how to set the cell minimum size !!!
			    int width = fm.stringWidth(text);
			    setMinimumSize(new Dimension(width + 50, fm.getHeight() + 5));
			} else if (userObject instanceof ComponentStateDefinition) {
				ComponentStateDefinition cs = (ComponentStateDefinition) userObject;
				text = toHtml(cs);
				toolTip = toHtmlWithTip(cs);
				icon = VCellIcons.rbmComponentStateIcon;
			} else {
				System.out.println("unknown thingie " + userObject);
			}
			setText(text);
			setIcon(icon);
			setToolTipText(toolTip == null ? text : toolTip);
		}
		return this;
	}

//	@Override
//	protected void paintComponent(Graphics g) {
//		
//		int h = 6;
//		int w = 8;
//		
//        GradientPaint gradientPaint = new GradientPaint(0, 0, Color.LIGHT_GRAY, 0, h, Color.WHITE);
//
//        Graphics2D g2D = (Graphics2D) g;
//        g2D.setPaint(gradientPaint);
//        g2D.fillRect(0, 0, w, h);
//
//        this.setOpaque(false);
//        super.paintComponent(g);
//        this.setOpaque(true);
//	}

}
