package org.vcell.client.logicalwindow;

import cbit.vcell.client.laf.VCellFonts;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import org.apache.commons.lang3.StringUtils;

/**
 * factory for creating most appropriate menu buttons
 */
public class LWMenuItemFactory {
	/**
	 * character(s) for each level of menu indentation
	 */
	public static final String MENU_INDENT = "  ";

	public static JMenuItem menuFor(int indentLevel, LWHandle lwh)  {
		return new HndlMenuItem(indentLevel, lwh);
	}
	
	public static JMenuItem menuFor(int indentLevel, LWContainerHandle lch)  {
		return new CntrMenuItem(indentLevel, lch);
	}
	
	
	
	/**
	 * common base class
	 *
	 * @param <T> {@link LWHandle} or {@link LWContainerHandle}
	 */
	@SuppressWarnings("serial")
	private abstract static class BaseMenuItem<T extends LWHandle> extends JMenuItem implements ActionListener{
		
		final T handle;
		BaseMenuItem(int indentLevel, T lwh) {
			super(indentStr(indentLevel) + lwh.menuDescription());
			handle = lwh;
			addActionListener(this);
			switch (handle.getLWModality()) {
			case MODELESS:
				VCellFonts.setStyle(this, Font.PLAIN);
				break;
			case PARENT_ONLY:
				VCellFonts.setStyle(this, Font.BOLD);
				break;
			}
		}
		
		/**
		 * standard indenting of menu items 
		 * @param level
		 * @return string to prepend to menu item
		 */
		private static String indentStr(int level) {
			return StringUtils.repeat(MENU_INDENT, level);
		}
	}
	
	/**
	 * {@link LWHandle} menu
	 */
	@SuppressWarnings("serial")
	private static class HndlMenuItem extends BaseMenuItem<LWHandle> {

		HndlMenuItem(int indentLevel, LWHandle lwh) {
			super(indentLevel, lwh);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			handle.unIconify();
			handle.getWindow().toFront();
		}
	}
	
	/**
	 * {@link LWContainerHandle} menu
	 */
	@SuppressWarnings("serial")
	private static class CntrMenuItem extends BaseMenuItem<LWContainerHandle> {

		CntrMenuItem(int indentLevel, LWContainerHandle lwh) {
			super(indentLevel, lwh);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			LWContainerHandle.positionTopDownTo(handle);
		}
		
	}

	/**
	 * prohibit construction
	 */
	private LWMenuItemFactory() { }

}
