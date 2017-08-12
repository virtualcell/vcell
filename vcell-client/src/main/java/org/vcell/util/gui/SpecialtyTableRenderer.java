package org.vcell.util.gui;

import java.util.Collection;

import javax.swing.table.TableCellRenderer;

/**
 * a specialty renderer for table cells
 * @author GWeatherby
 *
 */
public interface SpecialtyTableRenderer extends TableCellRenderer {
	
	/**
	 * @return collection of types supported by this renderer
	 */
	public Collection<Class<?>> supportedTypes( );
}
