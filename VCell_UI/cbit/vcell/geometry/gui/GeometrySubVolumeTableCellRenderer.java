package cbit.vcell.geometry.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.vcell.geometry.*;
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
	private int[] colormap = cbit.image.render.DisplayAdapterService.createContrastColorModel();
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
		label.setIcon(new cbit.image.ColorIcon(15,15,handleColor));
		label.setText(subVolume.getName());
		return label;
	}else{
		Component component = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
		if (table.getAutoResizeMode() == JTable.AUTO_RESIZE_OFF){
			Dimension dim = component.getPreferredSize();
			int requestedColumnWidth = (int)dim.getWidth()+10;
			javax.swing.table.TableColumn tableColumn = table.getColumnModel().getColumn(column);
			if (requestedColumnWidth>tableColumn.getPreferredWidth()){
				tableColumn.setPreferredWidth(requestedColumnWidth);
				table.invalidate();
			}
		}
		return component;
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
