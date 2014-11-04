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

import cbit.vcell.client.desktop.biomodel.RbmTreeCellEditor.MolecularComponentPatternCellEditor;
import cbit.vcell.client.desktop.biomodel.ObservableTreeModel.BondLocal;
import cbit.vcell.client.desktop.biomodel.ObservableTreeModel.SpeciesPatternLocal;
import cbit.vcell.client.desktop.biomodel.ObservableTreeModel.StateLocal;
import cbit.vcell.desktop.BioModelNode;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.util.VCellErrorMessages;
@SuppressWarnings("serial")
public class RbmObservableTreeCellRenderer extends RbmTreeCellRenderer {

	public RbmObservableTreeCellRenderer() {
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
			Icon icon = null;
			String toolTip = null;
			if (userObject instanceof RbmObservable) {
				RbmObservable ob = (RbmObservable) userObject;
				text = ob.getName();
				icon = VCellIcons.rbmObservableIcon;
				toolTip = "Observable: " + text + " (" + ob.getType() + ")";
			} else if (userObject instanceof SpeciesPatternLocal) {
				SpeciesPatternLocal spl = (SpeciesPatternLocal) userObject;
				text = "SpeciesPattern " + spl.index;
				toolTip = toHtmlWithTip(spl,true);
				icon = VCellIcons.rbmProductIcon;
			} else if (userObject instanceof MolecularTypePattern) {
				MolecularTypePattern molecularTypePattern = (MolecularTypePattern) userObject;
				text = toHtml(molecularTypePattern, true);
				toolTip = toHtmlWithTip(molecularTypePattern, true);
				icon = VCellIcons.rbmMolecularTypeIcon;
			} else if (userObject instanceof MolecularComponentPattern) {
				MolecularComponentPattern mcp = (MolecularComponentPattern) userObject;
				text = toHtml(mcp, true);
				toolTip = toHtmlWithTip(mcp, true);
				icon = VCellIcons.rbmMolecularComponentIcon;
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
	
	public static final String toHtmlWithTip(SpeciesPatternLocal spl, boolean bShowWords) {
		String text = "SpeciesPattern " + spl.index;
		String htmlText = text + VCellErrorMessages.RightClickToAddMolecules;
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static final String toHtml(MolecularTypePattern mtp, boolean bShowWords) {
//		return "<html> " + (bShowWords ? "Molecule" : "") + " <b>" + mtp.getMolecularType().getName() + "<sub>" + mtp.getIndex() + "</sub></b></html>";
		return "<html> " + (bShowWords ? "Molecule" : "") + " <b>" + mtp.getMolecularType().getName() + "</b></html>";
	}
	public static final String toHtmlWithTip(MolecularTypePattern mtp, boolean bShowWords) {
		String text = (bShowWords ? "Molecule" : "") + " <b>" + mtp.getMolecularType().getName() + "</b>";
		String htmlText = text + VCellErrorMessages.ClickShowAllComponents;
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static final String toHtml(MolecularComponentPattern mcp, boolean bShowWords) {
		return "<html> " + (bShowWords ? "Component" : "") + " <b>" + mcp.getMolecularComponent().getName() + "</b></html>";
	}
	public static final String toHtmlWithTip(MolecularComponentPattern mcp, boolean bShowWords) {
		String text = (bShowWords ? "Component" : "") + " <b>" + mcp.getMolecularComponent().getName() + "</b>";
		String htmlText = text + VCellErrorMessages.RightClickComponentToEdit;
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static final String toHtml(StateLocal sl, boolean bShowWords) {
		String text = toHtmlWork(sl, bShowWords);
		String htmlText = "<html>" + text + "</html>";
		return htmlText;
	}
	public static final String toHtmlWithTip(StateLocal sl, boolean bShowWords) {
		String text = toHtmlWork(sl, bShowWords);
		String htmlText = text + VCellErrorMessages.RightClickComponentForState;
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	// S(s!1,t!1).S(t) + S(s!2).S(tyr!2) -> S(s,tyr!+) + S(tyr~Y!?).S(tyr~Y)
	public static final String toHtml(BondLocal bl, boolean bSelected) {
		String text = toHtmlWork(bl, bSelected);
		String htmlText = "<html>" + text + "</html>";
		return htmlText;
	}
	public static final String toHtmlWithTip(BondLocal bl, boolean bSelected) {
		String text = toHtmlWork(bl, bSelected);
		String htmlText = text + VCellErrorMessages.RightClickComponentForBond;
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}

	private static final String toHtmlWork(StateLocal sl, boolean bShowWords) {
		String stateText = "";
		MolecularComponentPattern mcp = sl.getMolecularComponentPattern();
		ComponentStatePattern csp = mcp.getComponentStatePattern();
		if (mcp != null /*&& !mcp.isImplied()*/) {
			if (csp == null) {
				if(bShowWords) {
					stateText = "State(-): <b>None</b>";
				} else {
					stateText = "<b>None</b>";
				}
			} else if(csp.isAny()) {
				if(bShowWords) {
					stateText = "State(~): <b>Any</b>";
				} else {
					stateText = "<b>Any</b>";
				}
			} else {
				if(bShowWords) {
					stateText = "State(~): <b>" + csp.getComponentStateDefinition().getName() + "</b>";
				} else {
					stateText = "~ <b>" + csp.getComponentStateDefinition().getName() + "</b>";
				}
			}
		}
		return stateText;
	}
	private static final String toHtmlWork(BondLocal bl, boolean bSelected) {
		MolecularComponentPattern mcp = bl.getMolecularComponentPattern();
		BondType defaultType = BondType.Possible;
		String bondText = " Bond(<b>" + defaultType.symbol + "</b>): " + "<b>" + BondType.Possible.name() + "</b>";
		if (mcp != null) {
			BondType bondType = mcp.getBondType();
			if (bondType == BondType.Specified) {
				Bond bond = mcp.getBond();
				if (bond == null) {
					bondText = "";
				} else {
					int id = mcp.getBondId();
					String colorTextStart = bSelected ? "" : "<font color=" + "\"rgb(" + bondHtmlColors[id].getRed() + "," + bondHtmlColors[id].getGreen() + "," + bondHtmlColors[id].getBlue() + ")\">";
					String colorTextEnd = bSelected ? "" : "</font>";
					
					bondText = colorTextStart + "<b>" + mcp.getBondId() + "</b>" + colorTextEnd;		// <sub>&nbsp;</sub>
					bondText = " Bound(" + bondText + ") to: " + colorTextStart + toHtml(bond) + colorTextEnd;
				}
			} else {
				bondText =  " Bond(<b>" + bondType.symbol + "</b>): " + "<b>" + bondType.name() + "</b>";
			}
		}
		return bondText;
	}
	public static final String toHtml(Bond bond) {	// TODO: must be made private eventually
		String bondText = " Molecule <b>" + bond.molecularTypePattern.getMolecularType().getName();
//		bondText += "<sub>" + bond.molecularTypePattern.getIndex() + "</sub></b> Component <b>";
		bondText += "&nbsp;&nbsp;</b>Component <b>";
		bondText +=	bond.molecularComponentPattern.getMolecularComponent().getName() + "</b>";
		//  ... + "<sub>" + bond.molecularComponentPattern.getMolecularComponent().getIndex() + "</sub>)"
		return bondText;
	}
}
