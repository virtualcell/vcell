/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cbit.plot.gui.animation.helpersetup;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ScientificCellRenderer extends DefaultTableCellRenderer {
    
    @Override
    public Component getTableCellRendererComponent(JTable jTable, Object value,
            boolean isSelected, boolean hasFocus, int row, int col){
        Component c = super.getTableCellRendererComponent(jTable, value,
                                                isSelected, hasFocus, row, col);
        
        if( c instanceof JLabel && value instanceof Number){
            JLabel label = (JLabel)c;
            label.setHorizontalAlignment(JLabel.RIGHT);
            Number num = (Number)value;
            String text = IOHelp.scientificFormat.format(num);
            label.setText(text);
        }
        return c;
    }
    
    public static ScientificCellRenderer getRendererInstance(){
        return new ScientificCellRenderer();
    }
    
}
