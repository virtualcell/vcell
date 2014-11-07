package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;

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
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule.ReactionRuleParticipantType;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.util.VCellErrorMessages;
@SuppressWarnings("serial")
public class RbmMolecularTypeTreeCellRenderer extends RbmTreeCellRenderer {
	

	public RbmMolecularTypeTreeCellRenderer() {
		super();
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
				icon = VCellIcons.rbmMolecularTypeIcon;
			} else if (userObject instanceof MolecularComponent) {
				BioModelNode parentNode = (BioModelNode) node.getParent();
				icon = VCellIcons.rbmMolecularComponentIcon;
				MolecularComponent mc = (MolecularComponent) userObject;
				text = toHtml(mc, true);
				toolTip = toHtmlWithTip(mc, true);
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

}
