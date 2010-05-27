package cbit.vcell.microscopy.gui.estparamwizard;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.*;

import cbit.vcell.microscopy.EstimatedParameterTableModel;

import java.awt.*;
import java.text.NumberFormat;

/**
 */
public class AnalysisTableRenderer  extends DefaultTableCellRenderer {
	
	private NumberFormat format;
    
    public AnalysisTableRenderer(int precision) {
        super();
        setFont(new Font("Arial", Font.PLAIN, 11));
        //set double precision
        format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(precision);
        format.setMinimumFractionDigits(0);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column)    
    {
    	super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    	setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 1));
        if(value != null)
        {
        	setToolTipText(value.toString());
        }
    	if(value != null && (value instanceof Double || value instanceof Float || value instanceof Integer))
		{
			setText(format.format(value));
		}
    	
    	if(value == null)
		{
			setBackground(new Color(238,238,238)); //light purple
			setForeground(new Color(238,238,238));
		}else
		{
			if (isSelected) {
    			setBackground(table.getSelectionBackground());
    		} else {
    			setBackground(table.getBackground());
    		}
			setForeground(Color.black);
		}
        return this;
    }
}