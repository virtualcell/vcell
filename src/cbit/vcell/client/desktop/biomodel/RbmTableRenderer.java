package cbit.vcell.client.desktop.biomodel;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingConstants;

import org.vcell.model.rbm.MolecularComponentPattern;
import org.vcell.model.rbm.MolecularComponentPattern.BondType;
import org.vcell.model.rbm.MolecularType;
import org.vcell.model.rbm.MolecularTypePattern;
import org.vcell.model.rbm.RbmNetworkGenerator.CompartmentMode;
import org.vcell.model.rbm.RbmUtils;
import org.vcell.model.rbm.SpeciesPattern;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;

public class RbmTableRenderer extends DefaultScrollTableCellRenderer {
		
	public RbmTableRenderer() {
		super();
		setHorizontalTextPosition(SwingConstants.LEFT);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {	
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (value instanceof MolecularType) {
			String text = RbmUtils.toBnglString((MolecularType)value, null, CompartmentMode.hide);
			setText(text);
		} else if (value instanceof SpeciesPattern) {
			String text;
			if (isSelected) {
				text = RbmUtils.toBnglString((SpeciesPattern) value, null, CompartmentMode.hide);
			} else {
				text = "<html>" + RbmTableRenderer.toHtml((SpeciesPattern) value, isSelected) + "</html>";
			}
			setText(text);
		}
		return this;
	}

	public static String toHtml(SpeciesPattern speciesPattern, boolean bSelected) {
		StringBuilder buffer = new StringBuilder("");
		List<MolecularTypePattern> molecularTypePatterns = speciesPattern.getMolecularTypePatterns();
		for (int i = 0; i < molecularTypePatterns.size(); ++ i) {
			if (i > 0) {
				buffer.append(".");
			}
			MolecularTypePattern molecularTypePattern = molecularTypePatterns.get(i);
			buffer.append(molecularTypePattern.getMolecularType().getName());
			buffer.append("(");
			List<MolecularComponentPattern> componentPatterns = molecularTypePattern.getComponentPatternList();
			boolean bAddComma = false;
			for (MolecularComponentPattern mcp : componentPatterns) {
				if (mcp.isImplied()) {
					continue;
				}
				if (bAddComma) {
					buffer.append(",");
				}
				boolean bShowColor = false;
				if (mcp.getBondType() == BondType.Specified && !bSelected) {
					bShowColor = true;
					Color color = RbmTreeCellRenderer.bondHtmlColors[mcp.getBondId()];
					buffer.append("<font color=\"rgb(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")\">");
				}
				buffer.append(mcp.getMolecularComponent().getName());
				if (mcp.getComponentStatePattern() != null) {
					buffer.append(RbmUtils.toBnglString(mcp.getComponentStatePattern().getComponentStateDefinition()));
				}
				switch (mcp.getBondType()) {
				case Exists:
					buffer.append("!+");
					break;
				case None:
					break;
				case Possible:
					buffer.append("!?");
					break;
				case Specified:
					buffer.append("!" + mcp.getBondId());
					break;
				}
				if (bShowColor) {
					buffer.append("</font>");
				}
				bAddComma = true;
			}
			buffer.append(")");
		}
		return buffer.toString();
	}
}
