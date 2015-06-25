package org.vcell.util.gui;

import java.awt.Component;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * special renderer to provide tooltip popup for data views where
 * individual elements not readable due to compression truncation
 * @author GWeatherby
 *
 */
public class RenderDataViewerDoubleWithTooltip extends DefaultScrollTableCellRenderer implements SpecialtyTableRenderer {

	private static final long serialVersionUID = org.vcell.util.Serial.serialFromSVNRevision("$Rev$");
	
	/**
	 * types of cells supported
	 */
	private static final Class<?>[] TYPES = {Object.class,Number.class,Double.class};
	
	
	/**
	 * used for forming tooltips -- keep as field to avoid creation / garbage collection while pane active
	 */
	private StringBuilder sb;

	public RenderDataViewerDoubleWithTooltip() {
		this.sb = new StringBuilder(); 
	}

	@Override
	public Collection<Class<?>> supportedTypes() {
		return Arrays.asList(TYPES) ;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if (value instanceof Double) {
			final String EQUALS = "= ";
			sb.append("<html>");
			TableModel tableModel = table.getModel();
			if (column > 0) {
				String header = tableModel.getColumnName(0);
				Object obj = tableModel.getValueAt(row, 0);
				sb.append(header);
				sb.append(EQUALS);
				if (obj instanceof Double ) {
					sb.append( DefaultScrollTableCellRenderer.nicelyFormattedDouble((Double) obj) ); 
				}
				else {
					sb.append(obj);
				}
				sb.append("<br>");
			}
			String cname = tableModel.getColumnName(column);
			String text = getText( );
			sb.append(cname);
			sb.append(EQUALS);
			sb.append(text);
			sb.append("</html>");
			setToolTipText(sb.toString());
			sb.setLength(0);
		}
		return c;
	}
}