package cbit.vcell.microscopy.gui.estparamwizard;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.Border;
import javax.swing.*;
import java.awt.*;

/**
 */
public class PlainTableCellRenderer  extends DefaultTableCellRenderer {
    public PlainTableCellRenderer(final Font font) {
        super();
        setFont(font);
        setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 1));
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column)    {
        setValue(value);
        return this;
    }
}