package org.vcell.util.gui;

import javax.swing.*;


/**
 * Insert the type's description here.
 * Creation date: (3/18/2004 11:39:31 AM)
 * @author: Anuradha Lakshminarayana
 */

 /**
 * @ Original author :  Zafir Anjum
 */

@SuppressWarnings("serial")
public class MultiLineToolTip extends javax.swing.JToolTip {
	
	String tipText;
	JComponent component;
	protected int columns = 0;
	protected int fixedwidth = 0;

	
	public MultiLineToolTip() {
	    updateUI();
	}
	public int getColumns()
	{
		return columns;
	}
	public int getFixedWidth()
	{
		return fixedwidth;
	}
	public void setColumns(int columns)
	{
		this.columns = columns;
		this.fixedwidth = 0;
	}
	public void setFixedWidth(int width)
	{
		this.fixedwidth = width;
		this.columns = 0;
	}
	public void updateUI() {
	    setUI(MultiLineToolTipUI.createUI(this));
	}
}
