package org.vcell.sybil.gui.graphinfo;

/*   SBWrapperRenderer  --- by Oliver Ruebenacker, UCHC --- February to November 2009
 *   Renders table cells containing RDFNodes
 */

import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.DefaultTableCellRenderer;

import org.vcell.sybil.models.sbbox.imp.SBWrapper;

public class SBWrapperRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1129859274668534510L;

	protected Map<RDFNodeStyle.FontStyle, Font> fontMap = new HashMap<RDFNodeStyle.FontStyle, Font>();
	
	public Font font(RDFNodeStyle.FontStyle style) {
		Font font = fontMap.get(style);
		if(font == null) {
			font = style.deriveFont(getFont());
			fontMap.put(style, font);
		}
		return font;
	}

	@Override
	public void setValue(Object value) {
		SBWrapper sb = (SBWrapper) value;
		setFont(font(RDFNodeStyle.fontStyle(sb.resource())));
		setForeground(RDFNodeStyle.color(sb.resource()));			
		String nodeText = sb.label();
		setText("<html>" + nodeText + "</html>");
		setToolTipText(nodeText);
	}

}
