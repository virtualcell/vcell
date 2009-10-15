package cbit.vcell.microscopy.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class BestParameterTableRenderer extends DefaultTableCellRenderer {
	private NumberFormat format;
    
    public BestParameterTableRenderer(int precision) {
        super();
        setFont(new Font("Arial", Font.PLAIN, 11));
        setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 1));
        //set double precision
        format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(precision);
        format.setMinimumFractionDigits(precision);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column)    
    {
        setValue(value);
        if(value != null)
        {
        	setToolTipText(value.toString());
        }
//    	if(value != null && column != AnalysisTableModel.COLUMN_PARAM_NAME)
//		{
//			setText(format.format(value));
//		}
    	
    	if(value == null)
		{
			setBackground(new Color(238,238,238)); //light purple
			setForeground(new Color(238,238,238));
		}else
		{
			setBackground(table.getBackground());
			setForeground(Color.black);
		}
        return this;
    }

}
