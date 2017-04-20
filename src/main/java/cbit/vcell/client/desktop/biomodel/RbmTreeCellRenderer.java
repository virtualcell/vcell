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
import cbit.vcell.model.RbmObservable;
import cbit.vcell.model.ReactionRule;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.common.VCellErrorMessages;
@SuppressWarnings("serial")
public class RbmTreeCellRenderer extends DefaultTreeCellRenderer {
	
	// http://www.w3schools.com/cssref/css_colornames.asp
    public static final Color aliceblue = new Color(0xf0f8ff);
    public static final Color antiquewhite = new Color(0xfaebd7);
    public static final Color aqua = new Color(0x00ffff);
    public static final Color aquamarine = new Color(0x7fffd4);
    public static final Color azure = new Color(0xf0ffff);
    public static final Color beige = new Color(0xf5f5dc);
    public static final Color bisque = new Color(0xffe4c4);
    public static final Color black = new Color(0x000000);
    public static final Color blanchedalmond = new Color(0xffebcd);
    public static final Color blue = new Color(0x0000ff);
    public static final Color blueviolet = new Color(0x8a2be2);
    public static final Color brown = new Color(0xa52a2a);
    public static final Color burlywood = new Color(0xdeb887);
    public static final Color cadetblue = new Color(0x5f9ea0);
    public static final Color chartreuse = new Color(0x7fff00);
    public static final Color chocolate = new Color(0xd2691e);
    public static final Color coral = new Color(0xff7f50);
    public static final Color cornflowerblue = new Color(0x6495ed);
    public static final Color cornsilk = new Color(0xfff8dc);
    public static final Color crimson = new Color(0xdc143c);
    public static final Color cyan = new Color(0x00ffff);
    public static final Color darkblue = new Color(0x00008b);
    public static final Color darkcyan = new Color(0x008b8b);
    public static final Color darkgoldenrod = new Color(0xb8860b);
    public static final Color darkgray = new Color(0xa9a9a9);
    public static final Color darkgreen = new Color(0x006400);
    public static final Color darkkhaki = new Color(0xbdb76b);
    public static final Color darkmagenta = new Color(0x8b008b);
    public static final Color darkolivegreen = new Color(0x556b2f);
    public static final Color darkorange = new Color(0xff8c00);
    public static final Color darkorchid = new Color(0x9932cc);
    public static final Color darkred = new Color(0x8b0000);
    public static final Color darksalmon = new Color(0xe9967a);
    public static final Color darkseagreen = new Color(0x8fbc8f);
    public static final Color darkslateblue = new Color(0x483d8b);
    public static final Color darkslategray = new Color(0x2f4f4f);
    public static final Color darkturquoise = new Color(0x00ced1);
    public static final Color darkviolet = new Color(0x9400d3);
    public static final Color deeppink = new Color(0xff1493);
    public static final Color deepskyblue = new Color(0x00bfff);
    public static final Color dimgray = new Color(0x696969);
    public static final Color dodgerblue = new Color(0x1e90ff);
    public static final Color firebrick = new Color(0xb22222);
    public static final Color floralwhite = new Color(0xfffaf0);
    public static final Color forestgreen = new Color(0x228b22);
    public static final Color fuchsia = new Color(0xff00ff);
    public static final Color gainsboro = new Color(0xdcdcdc);
    public static final Color ghostwhite = new Color(0xf8f8ff);
    public static final Color gold = new Color(0xffd700);
    public static final Color goldenrod = new Color(0xdaa520);
    public static final Color gray = new Color(0x808080);
    public static final Color green = new Color(0x008000);
    public static final Color greenyellow = new Color(0xadff2f);
    public static final Color honeydew = new Color(0xf0fff0);
    public static final Color hotpink = new Color(0xff69b4);
    public static final Color indianred = new Color(0xcd5c5c);
    public static final Color indigo = new Color(0x4b0082);
    public static final Color ivory = new Color(0xfffff0);
    public static final Color khaki = new Color(0xf0e68c);
    public static final Color lavender = new Color(0xe6e6fa);
    public static final Color lavenderblush = new Color(0xfff0f5);
    public static final Color lawngreen = new Color(0x7cfc00);
    public static final Color lemonchiffon = new Color(0xfffacd);
    public static final Color lightblue = new Color(0xadd8e6);
    public static final Color lightcoral = new Color(0xf08080);
    public static final Color lightcyan = new Color(0xe0ffff);
    public static final Color lightgoldenrodyellow = new Color(0xfafad2);
    public static final Color lightgreen = new Color(0x90ee90);
    public static final Color lightgrey = new Color(0xd3d3d3);
    public static final Color lightpink = new Color(0xffb6c1);
    public static final Color lightsalmon = new Color(0xffa07a);
    public static final Color lightseagreen = new Color(0x20b2aa);
    public static final Color lightskyblue = new Color(0x87cefa);
    public static final Color lightslategray = new Color(0x778899);
    public static final Color lightsteelblue = new Color(0xb0c4de);
    public static final Color lightyellow = new Color(0xffffe0);
    public static final Color lime = new Color(0x00ff00);
    public static final Color limegreen = new Color(0x32cd32);
    public static final Color linen = new Color(0xfaf0e6);
    public static final Color magenta = new Color(0xff00ff);
    public static final Color maroon = new Color(0x800000);
    public static final Color mediumaquamarine = new Color(0x66cdaa);
    public static final Color mediumblue = new Color(0x0000cd);
    public static final Color mediumorchid = new Color(0xba55d3);
    public static final Color mediumpurple = new Color(0x9370db);
    public static final Color mediumseagreen = new Color(0x3cb371);
    public static final Color mediumslateblue = new Color(0x7b68ee);
    public static final Color mediumspringgreen = new Color(0x00fa9a);
    public static final Color mediumturquoise = new Color(0x48d1cc);
    public static final Color mediumvioletred = new Color(0xc71585);
    public static final Color midnightblue = new Color(0x191970);
    public static final Color mintcream = new Color(0xf5fffa);
    public static final Color mistyrose = new Color(0xffe4e1);
    public static final Color moccasin = new Color(0xffe4b5);
    public static final Color navajowhite = new Color(0xffdead);
    public static final Color navy = new Color(0x000080);
    public static final Color oldlace = new Color(0xfdf5e6);
    public static final Color olive = new Color(0x808000);
    public static final Color olivedrab = new Color(0x6b8e23);
    public static final Color orange = new Color(0xffa500);
    public static final Color orangered = new Color(0xff4500);
    public static final Color orchid = new Color(0xda70d6);
    public static final Color palegoldenrod = new Color(0xeee8aa);
    public static final Color palegreen = new Color(0x98fb98);
    public static final Color paleturquoise = new Color(0xafeeee);
    public static final Color palevioletred = new Color(0xdb7093);
    public static final Color papayawhip = new Color(0xffefd5);
    public static final Color peachpuff = new Color(0xffdab9);
    public static final Color peru = new Color(0xcd853f);
    public static final Color pink = new Color(0xffc0cb);
    public static final Color plum = new Color(0xdda0dd);
    public static final Color powderblue = new Color(0xb0e0e6);
    public static final Color purple = new Color(0x800080);
    public static final Color red = new Color(0xff0000);
    public static final Color rosybrown = new Color(0xbc8f8f);
    public static final Color royalblue = new Color(0x4169e1);
    public static final Color saddlebrown = new Color(0x8b4513);
    public static final Color salmon = new Color(0xfa8072);
    public static final Color sandybrown = new Color(0xf4a460);
    public static final Color seagreen = new Color(0x2e8b57);
    public static final Color seashell = new Color(0xfff5ee);
    public static final Color sienna = new Color(0xa0522d);
    public static final Color silver = new Color(0xc0c0c0);
    public static final Color skyblue = new Color(0x87ceeb);
    public static final Color slateblue = new Color(0x6a5acd);
    public static final Color slategray = new Color(0x708090);
    public static final Color snow = new Color(0xfffafa);
    public static final Color springgreen = new Color(0x00ff7f);
    public static final Color steelblue = new Color(0x4682b4);
    public static final Color tan = new Color(0xd2b48c);
    public static final Color teal = new Color(0x008080);
    public static final Color thistle = new Color(0xd8bfd8);
    public static final Color tomato = new Color(0xff6347);
    public static final Color turquoise = new Color(0x40e0d0);
    public static final Color violet = new Color(0xee82ee);
    public static final Color wheat = new Color(0xf5deb3);
    public static final Color white = new Color(0xffffff);
    public static final Color whitesmoke = new Color(0xf5f5f5);
    public static final Color yellow = new Color(0xffff00);
    public static final Color yellowgreen = new Color(0x9acd32);
    
	public static Color[] bondHtmlColors = {null, 
		blue,
		blueviolet,
		brown,
		burlywood ,
		cadetblue,
		chocolate,
		coral,
		cornflowerblue,
		crimson,
		darkblue,
		darkcyan,
		darkgoldenrod,
		darkgreen,
		darkmagenta,
		darkolivegreen,
		darkorange,
		darkorange,
		darkorchid,
		darkred,
		darksalmon,
		darkviolet,
		deeppink,
		deepskyblue,
		dodgerblue,
		firebrick,
		forestgreen,
		fuchsia,
		goldenrod,
		green,
		hotpink,
		indianred ,
		indigo ,
		magenta,
		maroon,
		mediumblue,
		mediumorchid,
		mediumseagreen,
		mediumvioletred,
		orange,
		orangered,
		royalblue,
		saddlebrown,
		salmon,
		seagreen,
		sienna,
		steelblue,
		teal,
		tomato,
	};
	
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
	private static final String toHtmlWork(ParticipantMatchLabelLocal pmll, boolean bSelected) {
		return "Match <b>" + pmll.getParticipantMatchLabel() + "</b>";
	}
	private static final String toHtmlWorkShort(BondLocal bl) {			// used for tooltips
		MolecularComponentPattern mcp = bl.getMolecularComponentPattern();
		String bondText = "";
		String colorTextStart = "<font color=" + "\"rgb(" + red.getRed() + "," + red.getGreen() + "," + red.getBlue() + ")\">";
		String colorTextEnd = "</font>";
		bondText = colorTextStart + "<b>" + "(unbound)" + "</b>" + colorTextEnd;

		if (mcp != null) {
			BondType bondType = mcp.getBondType();
			if (bondType == BondType.Specified) {
				Bond bond = mcp.getBond();
				if (bond == null) {
					colorTextStart = "<font color=" + "\"rgb(" + red.getRed() + "," + red.getGreen() + "," + red.getBlue() + ")\">";
					colorTextEnd = "</font>";
					bondText = colorTextStart + "<b>" + "bond (missing)" + "</b>" + colorTextEnd;
				} else {
					int id = mcp.getBondId();
					colorTextStart = "<font color=" + "\"rgb(" + bondHtmlColors[id].getRed() + "," + bondHtmlColors[id].getGreen() + "," + bondHtmlColors[id].getBlue() + ")\">";
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