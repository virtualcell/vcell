package cbit.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
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
public class ScopedExpressionTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
	private ExpressionCanvas expressionCanvas = new ExpressionCanvas();	
/**
 * GeometrySubVolumeTableCellRenderer constructor comment.
 */
public ScopedExpressionTableCellRenderer() {
	super();
}
public Component getTableCellRendererComponent(JTable table,Object value,boolean isSelected,boolean hasFocus,int row,int column) {

	if (value instanceof cbit.vcell.parser.ScopedExpression){
		JLabel fakeLabel = (JLabel)super.getTableCellRendererComponent(table,"",isSelected,hasFocus,row,column);
		cbit.vcell.parser.ScopedExpression scopedExpression = (cbit.vcell.parser.ScopedExpression)value;
		if (scopedExpression.getExpression()!=null){
			expressionCanvas.setNameScope(scopedExpression.getNameScope());
			expressionCanvas.setExpression(scopedExpression.getExpression());
			expressionCanvas.setForeground(fakeLabel.getForeground());
			expressionCanvas.setBackground(fakeLabel.getBackground());
			try {
				cbit.vcell.parser.ExpressionPrintFormatter expPrintFormatter = new cbit.vcell.parser.ExpressionPrintFormatter(((cbit.vcell.parser.ScopedExpression)value).getExpression(),expressionCanvas.getNameScope());
				Dimension dim = expPrintFormatter.getSize((Graphics2D)table.getGraphics());
				int requestedRowHeight = (int)dim.getHeight()+table.getRowMargin()+10;
				//
				// using introspection to introduce variable row height availlable only in Java 1.3 or later
				//
				Class classArray_int[] = { int.class };
				Class classArray_int_int[] = { int.class, int.class };
				Class classArray_empty[] = new Class[0];
				java.lang.reflect.Method getRowHeight_1_2 = null;
				java.lang.reflect.Method setRowHeight_1_2 = null;
				java.lang.reflect.Method getRowHeight_1_3 = null;
				java.lang.reflect.Method setRowHeight_1_3 = null;
				try {
					getRowHeight_1_2 = table.getClass().getMethod("getRowHeight",classArray_empty);
					setRowHeight_1_2 = table.getClass().getMethod("setRowHeight",classArray_int);
				}catch (NoSuchMethodException e){
					e.printStackTrace(System.out);
					getRowHeight_1_2 = null;
					setRowHeight_1_2 = null;	
				}
				try {
					getRowHeight_1_3 = table.getClass().getMethod("getRowHeight",classArray_int);
					setRowHeight_1_3 = table.getClass().getMethod("setRowHeight",classArray_int_int);
				}catch (NoSuchMethodException e){
					//System.out.println("didn't find Java 1.3 methods ... this is expected");
					getRowHeight_1_3 = null;
					setRowHeight_1_3 = null;	
				}
				try {
					if (getRowHeight_1_3 != null){
						//
						// Java 1.3 or later size rows individually
						//
						int currentRowHeight = ((Integer)getRowHeight_1_3.invoke(table,new Object[] { new Integer(row) })).intValue();
						// int currentRowHeight = table.getRowHeight(row);
						if (requestedRowHeight != currentRowHeight){
							setRowHeight_1_3.invoke(table,new Object[] { new Integer(row), new Integer(requestedRowHeight) });
							//table.setRowHeight(row, requestedRowHeight);
						}
					}else if (getRowHeight_1_2 != null){
						//
						// Java 1.2
						//
						int currentRowHeight = ((Integer)getRowHeight_1_2.invoke(table,new Object[0])).intValue();
						// int currentRowHeight = table.getRowHeight();
						if (requestedRowHeight>currentRowHeight){
							setRowHeight_1_2.invoke(table,new Object[] { new Integer(requestedRowHeight) });
							//table.setRowHeight(requestedRowHeight);
						}
					}else{
						System.out.println("GeometrySubVolumeTableCellRenderer.getTableCellRendererComponent() ... introspection failure");
					}
				}catch (IllegalAccessException e){
					e.printStackTrace(System.out);
				}catch (java.lang.reflect.InvocationTargetException e){
					e.printStackTrace(System.out);
				}
				if (table.getAutoResizeMode() == table.AUTO_RESIZE_OFF){
					int requestedColumnWidth = (int)dim.getWidth()+10;
					javax.swing.table.TableColumn tableColumn = table.getColumnModel().getColumn(column);
					if (requestedColumnWidth>tableColumn.getPreferredWidth()){
						tableColumn.setPreferredWidth(requestedColumnWidth);
						table.invalidate();
					}
				}
				expressionCanvas.setMinimumSize(new Dimension((int)dim.getWidth()+10,(int)dim.getHeight()+10));
			}catch (cbit.vcell.parser.ExpressionException e){
				e.printStackTrace(System.out);
			}
			expressionCanvas.setForeground(fakeLabel.getForeground());
			expressionCanvas.setBackground(fakeLabel.getBackground());
			expressionCanvas.setBorder(fakeLabel.getBorder());
		    return expressionCanvas;
		}else{ // scopedExpression.getExpression() is null
			Component component = super.getTableCellRendererComponent(table,"",isSelected,hasFocus,row,column);
			if (table.getAutoResizeMode() == table.AUTO_RESIZE_OFF){
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
	}else{
		Component component = super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
		if (table.getAutoResizeMode() == table.AUTO_RESIZE_OFF){
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
	setText((value == null) ? "" : value.toString());
}
}
