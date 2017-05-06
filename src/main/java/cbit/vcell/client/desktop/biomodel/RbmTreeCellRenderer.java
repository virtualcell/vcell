package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;

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

import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.BondLocal;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.ParticipantMatchLabelLocal;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.ReactionRuleParticipantLocal;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.SpeciesPatternLocal;
import cbit.vcell.client.desktop.biomodel.RbmDefaultTreeModel.StateLocal;
import cbit.vcell.graph.AbstractComponentShape;
import cbit.vcell.graph.GraphConstants;
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.common.VCellErrorMessages;
@SuppressWarnings("serial")
public class RbmTreeCellRenderer extends DefaultTreeCellRenderer {
	
	// http://www.w3schools.com/cssref/css_colornames.asp
	public static final Color aliceblue = GraphConstants.aliceblue;
    public static final Color antiquewhite = GraphConstants.antiquewhite;
    public static final Color aqua = GraphConstants.aqua;
    public static final Color aquamarine = GraphConstants.aquamarine;
    public static final Color azure = GraphConstants.azure;
    public static final Color beige = GraphConstants.beige;
    public static final Color bisque = GraphConstants.bisque;
    public static final Color black = GraphConstants.black;
    public static final Color blanchedalmond = GraphConstants.blanchedalmond;
    public static final Color blue = GraphConstants.blue;
    public static final Color blueviolet = GraphConstants.blueviolet;
    public static final Color brown = GraphConstants.brown;
    public static final Color burlywood = GraphConstants.burlywood;
    public static final Color cadetblue = GraphConstants.cadetblue;
    public static final Color chartreuse = GraphConstants.chartreuse;
    public static final Color chocolate = GraphConstants.chocolate;
    public static final Color coral = GraphConstants.coral;
    public static final Color cornflowerblue = GraphConstants.cornflowerblue;
    public static final Color cornsilk = GraphConstants.cornsilk;
    public static final Color crimson = GraphConstants.crimson;
    public static final Color cyan = GraphConstants.cyan;
    public static final Color darkblue = GraphConstants.darkblue;
    public static final Color darkcyan = GraphConstants.darkcyan;
    public static final Color darkgoldenrod = GraphConstants.darkgoldenrod;
    public static final Color darkgray = GraphConstants.darkgray;
    public static final Color darkgreen = GraphConstants.darkgreen;
    public static final Color darkkhaki = GraphConstants.darkkhaki;
    public static final Color darkmagenta = GraphConstants.darkmagenta;
    public static final Color darkolivegreen = GraphConstants.darkolivegreen;
    public static final Color darkorange = GraphConstants.darkorange;
    public static final Color darkorchid = GraphConstants.darkorchid;
    public static final Color darkred = GraphConstants.darkred;
    public static final Color darksalmon = GraphConstants.darksalmon;
    public static final Color darkseagreen = GraphConstants.darkseagreen;
    public static final Color darkslateblue = GraphConstants.darkslateblue;
    public static final Color darkslategray = GraphConstants.darkslategray;
    public static final Color darkturquoise = GraphConstants.darkturquoise;
    public static final Color darkviolet = GraphConstants.darkviolet;
    public static final Color deeppink = GraphConstants.deeppink;
    public static final Color deepskyblue = GraphConstants.deepskyblue;
    public static final Color dimgray = GraphConstants.dimgray;
    public static final Color dodgerblue = GraphConstants.dodgerblue;
    public static final Color firebrick = GraphConstants.firebrick;
    public static final Color floralwhite = GraphConstants.floralwhite;
    public static final Color forestgreen = GraphConstants.forestgreen;
    public static final Color fuchsia = GraphConstants.fuchsia;
    public static final Color gainsboro = GraphConstants.gainsboro;
    public static final Color ghostwhite = GraphConstants.ghostwhite;
    public static final Color gold = GraphConstants.gold;
    public static final Color goldenrod = GraphConstants.goldenrod;
    public static final Color gray = GraphConstants.gray;
    public static final Color green = GraphConstants.green;
    public static final Color greenyellow = GraphConstants.greenyellow;
    public static final Color honeydew = GraphConstants.honeydew;
    public static final Color hotpink = GraphConstants.hotpink;
    public static final Color indianred = GraphConstants.indianred;
    public static final Color indigo = GraphConstants.indigo;
    public static final Color ivory = GraphConstants.ivory;
    public static final Color khaki = GraphConstants.khaki;
    public static final Color lavender = GraphConstants.lavender;
    public static final Color lavenderblush = GraphConstants.lavenderblush;
    public static final Color lawngreen = GraphConstants.lawngreen;
    public static final Color lemonchiffon = GraphConstants.lemonchiffon;
    public static final Color lightblue = GraphConstants.lightblue;
    public static final Color lightcoral = GraphConstants.lightcoral;
    public static final Color lightcyan = GraphConstants.lightcyan;
    public static final Color lightgoldenrodyellow = GraphConstants.lightgoldenrodyellow;
    public static final Color lightgreen = GraphConstants.lightgreen;
    public static final Color lightgrey = GraphConstants.lightgrey;
    public static final Color lightpink = GraphConstants.lightpink;
    public static final Color lightsalmon = GraphConstants.lightsalmon;
    public static final Color lightseagreen = GraphConstants.lightseagreen;
    public static final Color lightskyblue = GraphConstants.lightskyblue;
    public static final Color lightslategray = GraphConstants.lightslategray;
    public static final Color lightsteelblue = GraphConstants.lightsteelblue;
    public static final Color lightyellow = GraphConstants.lightyellow;
    public static final Color lime = GraphConstants.lime;
    public static final Color limegreen = GraphConstants.limegreen;
    public static final Color linen = GraphConstants.linen;
    public static final Color magenta = GraphConstants.magenta;
    public static final Color maroon = GraphConstants.maroon;
    public static final Color mediumaquamarine = GraphConstants.mediumaquamarine;
    public static final Color mediumblue = GraphConstants.mediumblue;
    public static final Color mediumorchid = GraphConstants.mediumorchid;
    public static final Color mediumpurple = GraphConstants.mediumpurple;
    public static final Color mediumseagreen = GraphConstants.mediumseagreen;
    public static final Color mediumslateblue = GraphConstants.mediumslateblue;
    public static final Color mediumspringgreen = GraphConstants.mediumspringgreen;
    public static final Color mediumturquoise = GraphConstants.mediumturquoise;
    public static final Color mediumvioletred = GraphConstants.mediumvioletred;
    public static final Color midnightblue = GraphConstants.midnightblue;
    public static final Color mintcream = GraphConstants.mintcream;
    public static final Color mistyrose = GraphConstants.mistyrose;
    public static final Color moccasin = GraphConstants.moccasin;
    public static final Color navajowhite = GraphConstants.navajowhite;
    public static final Color navy = GraphConstants.navy;
    public static final Color oldlace = GraphConstants.oldlace;
    public static final Color olive = GraphConstants.olive;
    public static final Color olivedrab = GraphConstants.olivedrab;
    public static final Color orange = GraphConstants.orange;
    public static final Color orangered = GraphConstants.orangered;
    public static final Color orchid = GraphConstants.orchid;
    public static final Color palegoldenrod = GraphConstants.palegoldenrod;
    public static final Color palegreen = GraphConstants.palegreen;
    public static final Color paleturquoise = GraphConstants.paleturquoise;
    public static final Color palevioletred = GraphConstants.palevioletred;
    public static final Color papayawhip = GraphConstants.papayawhip;
    public static final Color peachpuff = GraphConstants.peachpuff;
    public static final Color peru = GraphConstants.peru;
    public static final Color pink = GraphConstants.pink;
    public static final Color plum = GraphConstants.plum;
    public static final Color powderblue = GraphConstants.powderblue;
    public static final Color purple = GraphConstants.purple;
    public static final Color red = GraphConstants.red;
    public static final Color rosybrown = GraphConstants.rosybrown;
    public static final Color royalblue = GraphConstants.royalblue;
    public static final Color saddlebrown = GraphConstants.saddlebrown;
    public static final Color salmon = GraphConstants.salmon;
    public static final Color sandybrown = GraphConstants.sandybrown;
    public static final Color seagreen = GraphConstants.seagreen;
    public static final Color seashell = GraphConstants.seashell;
    public static final Color sienna = GraphConstants.sienna;
    public static final Color silver = GraphConstants.silver;
    public static final Color skyblue = GraphConstants.skyblue;
    public static final Color slateblue = GraphConstants.slateblue;
    public static final Color slategray = GraphConstants.slategray;
    public static final Color snow = GraphConstants.snow;
    public static final Color springgreen = GraphConstants.springgreen;
    public static final Color steelblue = GraphConstants.steelblue;
    public static final Color tan = GraphConstants.tan;
    public static final Color teal = GraphConstants.teal;
    public static final Color thistle = GraphConstants.thistle;
    public static final Color tomato = GraphConstants.tomato;
    public static final Color turquoise = GraphConstants.turquoise;
    public static final Color violet = GraphConstants.violet;
    public static final Color wheat = GraphConstants.wheat;
    public static final Color white = GraphConstants.white;
    public static final Color whitesmoke = GraphConstants.whitesmoke;
    public static final Color yellow = GraphConstants.yellow;
    public static final Color yellowgreen = GraphConstants.yellowgreen;
    
	public RbmTreeCellRenderer() {
		super();
		setBorder(new EmptyBorder(0, 2, 0, 0));		
	}
	
	public static final String toHtml(SpeciesContext sc) {
		String text = sc.getName();
		String htmlText = "Species: <b>" + text + "</b>";
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static final String toHtmlWithTip(SpeciesContext sc) {
		String text = sc.getName();
		String htmlText = "Species: <b>" + text + "</b>";
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static final String toHtml(ReactionRule rr) {
		String text = rr.getName();
		String htmlText = "Reaction Rule: <b>" + text + "</b>";
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static final String toHtmlWithTip(ReactionRule rr) {
		String text = rr.getName();
		String htmlText = "Reaction Rule: <b>" + text + "</b>";
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static final String toHtml(RbmObservable o) {
		String text = o.getName();
		String htmlText = "Observable: <b>" + text + "</b>" + " (" + o.getType() + ")";
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static final String toHtmlWithTip(RbmObservable o) {
		String text = o.getName();
		String htmlText = "Observable: <b>" + text + "</b>" + " (" + o.getType() + ")";
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static final String toHtml(ReactionRuleParticipantLocal rrp, boolean bShowWords) {
		String text = rrp.speciesPattern.getSpeciesPattern().toString();
		String htmlText = rrp.type.toString() + " " + rrp.index + ": <b>" + text + "</b>";
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static final String toHtmlWithTip(ReactionRuleParticipantLocal rrp, boolean bShowWords) {
		String text = rrp.speciesPattern.getSpeciesPattern().toString();
		text =  rrp.type.toString() + " " + rrp.index + ": " + text;
		String htmlText = text + VCellErrorMessages.RightClickToAddMolecules;
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static final String toHtml(SpeciesPatternLocal spl, boolean bShowWords) {
		String text = spl.speciesPattern.toString();
		String htmlText = "SpeciesPattern " + spl.index + ": " + "<b>" + text + "</b>";
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static final String toHtmlWithTip(SpeciesPatternLocal spl, boolean bShowWords) {
		String text = spl.speciesPattern.toString();
		text = "SpeciesPattern " + spl.index + ": " + text;
		String htmlText = text + VCellErrorMessages.RightClickToAddMolecules;
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	
	public static final String toHtml(MolecularType mt, boolean bShowWords) {
		String text = (bShowWords ? MolecularType.typeName : "") + " <b>" + mt.getName() + "</b>";
		String htmlText = "<html>" + text + "</html>";
		return htmlText;
	}
	public static final String toHtmlWithTip(MolecularType mt, boolean bShowWords) {
		String text = (bShowWords ? MolecularType.typeName : "") + " <b>" + mt.getName() + "</b>";
		String htmlText = text + VCellErrorMessages.TripleClickOrRightClick;
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static String toHtml(MolecularComponent mc, boolean bShowWords) {
//		String text = (bShowWords ? "Component" : "") + " <b>" + mc.getName() + "<sub>" + mc.getIndex() + "</sub></b>";
		String text = (bShowWords ? MolecularComponent.typeName : "") + " <b>" + mc.getName() + "</b>";
		String htmlText = "<html>" + text + "</html>";
		return htmlText;
	}
	public static String toHtmlWithTip(MolecularComponent mc, boolean bShowWords) {
//		String text = (bShowWords ? "Component" : "") + " <b>" + mc.getName() + "<sub>" + mc.getIndex() + "</sub></b>";
		String text = (bShowWords ? MolecularComponent.typeName : "") + " <b>" + mc.getName() + "</b>";
		String htmlText = text + VCellErrorMessages.TripleClickOrRightClick;
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static String toHtml(ComponentStateDefinition cs) {
		String text = ComponentStateDefinition.typeName + " <b>" + cs.getName() + "</b>";
		String htmlText = "<html>" + text + "</html>";
		return htmlText;
	}
	public static String toHtmlWithTip(ComponentStateDefinition cs) {
		String text = ComponentStateDefinition.typeName + " <b>" + cs.getName() + "</b>";
		String htmlText = text + VCellErrorMessages.TripleClickOrRightClick;
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
// ----------------------------------------------------------------------------------------------------
	
	public static final String toHtml(MolecularTypePattern mtp, boolean bShowWords) {
//		return "<html> " + (bShowWords ? "Molecule" : "") + " <b>" + mtp.getMolecularType().getName() + "<sub>" + mtp.getIndex() + "</sub></b></html>";
		return "<html> " + (bShowWords ? MolecularType.typeName : "") + " <b>" + mtp.getMolecularType().getName() + "</b></html>";
	}
	public static final String toHtmlWithTip(MolecularTypePattern mtp, boolean bShowWords) {
		String text = (bShowWords ? MolecularType.typeName : "") + " <b>" + mtp.getMolecularType().getName() + "</b>";
		String htmlText = text + VCellErrorMessages.ClickShowAllComponents;
		htmlText = "<html>" + htmlText + "</html>";
		return htmlText;
	}
	public static final String toHtml(MolecularComponentPattern mcp, boolean bShowWords) {
		String text = (bShowWords ? MolecularComponent.typeName : "") + " <b>" + mcp.getMolecularComponent().getName() + "</b>";
		MolecularComponent mc = mcp.getMolecularComponent();
		return "<html> " + text + "</html>";
	}
	public static final String toHtmlWithTip(MolecularComponentPattern mcp, boolean bShowWords) {
		String text = (bShowWords ? MolecularComponent.typeName : "") + " <b>" + mcp.getMolecularComponent().getName() + "</b>";
		MolecularComponent mc = mcp.getMolecularComponent();
		
		if(mc.getComponentStateDefinitions().size() > 0) {	// we don't show the state if nothing to choose from
			StateLocal sl = new StateLocal(mcp);
			text += "&#160;&#160;&#160;" + toHtmlWorkShort(sl);
		}
		BondLocal bl = new BondLocal(mcp);
		text += "&#160;&#160;&#160;" + toHtmlWorkShort(bl);
		
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
	
	public static final String toHtml(ParticipantMatchLabelLocal pmll, boolean bSelected) {
		String text = toHtmlWork(pmll, bSelected);
		String htmlText = "<html>" + text + "</html>";
		return htmlText;
	}
	public static final String toHtmlWithTip(ParticipantMatchLabelLocal pmll, boolean bSelected) {
		String text = toHtmlWork(pmll, bSelected);
		String htmlText = text;
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
					stateText = ComponentStateDefinition.typeName + "(-): <b>None</b>";
				} else {
					stateText = "<b>None</b>";
				}
			} else if(csp.isAny()) {
				if(bShowWords) {
					stateText = ComponentStateDefinition.typeName + "(~): <b>Any</b>";
				} else {
					stateText = "<b>Any</b>";
				}
			} else {
				if(bShowWords) {
					stateText = ComponentStateDefinition.typeName + "(~): <b>" + csp.getComponentStateDefinition().getName() + "</b>";
				} else {
					stateText = "~ <b>" + csp.getComponentStateDefinition().getName() + "</b>";
				}
			}
		}
		return stateText;
	}
	private static final String toHtmlWorkShort(StateLocal sl) {
		String stateText = "";
		MolecularComponentPattern mcp = sl.getMolecularComponentPattern();
		ComponentStatePattern csp = mcp.getComponentStatePattern();
		if (mcp != null /*&& !mcp.isImplied()*/) {
			if (mcp.getMolecularComponent().getComponentStateDefinitions().size() == 0) {
				// stateText = ComponentStateDefinition.typeName + ": <b>n/a</b>"
				;		// no states possible because none defined
			} else if(csp != null && csp.isAny()) {
				stateText = ComponentStateDefinition.typeName + ": <b>" + ComponentStatePattern.strAny + "</b>";
//				stateText = ComponentStateDefinition.typeName + ": " + ComponentStatePattern.strAny;
			} else if(csp != null && csp.getComponentStateDefinition() != null) {
				stateText = ComponentStateDefinition.typeName + ": <b>" + csp.getComponentStateDefinition().getName() + "</b>";
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
					String colorTextStart = bSelected ? "" : "<font color=" + "\"rgb(" + GraphConstants.bondHtmlColors[id].getRed() + "," + GraphConstants.bondHtmlColors[id].getGreen() + "," + GraphConstants.bondHtmlColors[id].getBlue() + ")\">";
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
	private static final String toHtmlWork(ParticipantMatchLabelLocal pmll, boolean bSelected) {
		return "Match <b>" + pmll.getParticipantMatchLabel() + "</b>";
	}
	private static final String toHtmlWorkShort(BondLocal bl) {			// used for tooltips
		MolecularComponentPattern mcp = bl.getMolecularComponentPattern();
		String bondText = "";
		String colorTextStart = "<font color=" + "\"rgb(" + GraphConstants.red.getRed() + "," + GraphConstants.red.getGreen() + "," + GraphConstants.red.getBlue() + ")\">";
		String colorTextEnd = "</font>";
		bondText = colorTextStart + "<b>" + "(unbound)" + "</b>" + colorTextEnd;

		if (mcp != null) {
			BondType bondType = mcp.getBondType();
			if (bondType == BondType.Specified) {
				Bond bond = mcp.getBond();
				if (bond == null) {
					colorTextStart = "<font color=" + "\"rgb(" + GraphConstants.red.getRed() + "," + GraphConstants.red.getGreen() + "," + GraphConstants.red.getBlue() + ")\">";
					colorTextEnd = "</font>";
					bondText = colorTextStart + "<b>" + "bond (missing)" + "</b>" + colorTextEnd;
				} else {
					int id = mcp.getBondId();
					colorTextStart = "<font color=" + "\"rgb(" + GraphConstants.bondHtmlColors[id].getRed() + "," + GraphConstants.bondHtmlColors[id].getGreen() + "," + GraphConstants.bondHtmlColors[id].getBlue() + ")\">";
					colorTextEnd = "</font>";
					bondText = colorTextStart + "<b>" + "Bond<sub>" + id + "</sub></b>" + colorTextEnd;		// <sub>&nbsp;</sub>
				}
			} else if(bondType == BondType.None) {
				bondText =  "Unbound";
//				bondText =  "<b>" + "unbound" + "</b>";
			} else if(bondType == BondType.Exists) {
				Color c = AbstractComponentShape.plusSignGreen;
				colorTextStart = "<font color=" + "\"rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")\">";
				colorTextEnd = "</font>";
				bondText = colorTextStart + "<b>" + mcp.getBondType().symbol + "</b>" + colorTextEnd;	// green '+'
				bondText = "Bond: '" + bondText + "'";
			} else if(bondType == BondType.Possible) {
				Color c = Color.gray;
				colorTextStart = "<font color=" + "\"rgb(" + c.getRed() + "," + c.getGreen() + "," + c.getBlue() + ")\">";
				colorTextEnd = "</font>";
				bondText = colorTextStart + "<b>" + mcp.getBondType().symbol + "</b>" + colorTextEnd;	// gray '?'
				bondText = "Bond: '" + bondText + "'";
			}
		}
		return bondText;
	}
	public static final String toHtml(Bond bond) {	// TODO: must be made private eventually
		String bondText = " " + MolecularType.typeName + " <b>" + bond.molecularTypePattern.getMolecularType().getName();
//		bondText += "<sub>" + bond.molecularTypePattern.getIndex() + "</sub></b> Component <b>";
		bondText += "&nbsp;&nbsp;&nbsp;</b>" + MolecularComponent.typeName + " <b>";
		bondText +=	bond.molecularComponentPattern.getMolecularComponent().getDisplayName() + "</b>";
		//  ... + "<sub>" + bond.molecularComponentPattern.getMolecularComponent().getIndex() + "</sub>)"
		return bondText;
	}
	
	
	
	
}