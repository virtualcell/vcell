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
import java.awt.Component;

import javax.swing.JTable;

import org.vcell.util.gui.ColorIcon;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;

import cbit.image.DisplayAdapterService;
import cbit.vcell.geometry.SubVolume;
/**
 * Insert the type's description here.
 * Creation date: (3/31/2001 10:05:14 PM)
 * @author: Jim Schaff
 */
@SuppressWarnings("serial")
public class GeometrySubVolumeTableCellRenderer extends DefaultScrollTableCellRenderer {
	private int[] colormap = DisplayAdapterService.createContrastColorModel();

/**
 * GeometrySubVolumeTableCellRenderer constructor comment.
 */
public GeometrySubVolumeTableCellRenderer() {
	super();
}


public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column) {

	super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
	if (value instanceof SubVolume){
		SubVolume subVolume = (SubVolume)value;
		java.awt.Color handleColor = new java.awt.Color(colormap[subVolume.getHandle()]);
		setIcon(new ColorIcon(15,15,handleColor));
		setText(subVolume.getName());
	}
	return this;
}

}
