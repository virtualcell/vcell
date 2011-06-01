/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.geometry.gui;
import cbit.vcell.geometry.*;
import java.awt.Graphics2D;
import javax.swing.JLabel;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.JTable;
/**
 * Insert the type's description here.
 * Creation date: (3/31/2001 10:05:14 PM)
 * @author: Jim Schaff
 */
public class GeometrySubVolumeTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
	private int[] colormap = cbit.image.DisplayAdapterService.createContrastColorModel();

/**
 * GeometrySubVolumeTableCellRenderer constructor comment.
 */
public GeometrySubVolumeTableCellRenderer() {
	super();
}


public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column) {

	if (value instanceof SubVolume){
		SubVolume subVolume = (SubVolume)value;
		JLabel label = (JLabel)super.getTableCellRendererComponent(table,"",isSelected,hasFocus,row,column);
		java.awt.Color handleColor = new java.awt.Color(colormap[subVolume.getHandle()]);
		label.setIcon(new org.vcell.util.gui.ColorIcon(15,15,handleColor));
		label.setText(subVolume.getName());
		return label;
	}else{
		return super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
	}
}


protected void setValue(Object value) {
	if (value instanceof SubVolume){
		setText(((SubVolume)value).getName());
	}else{
		setText((value == null) ? "" : value.toString());
	}
}
}
