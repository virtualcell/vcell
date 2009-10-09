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