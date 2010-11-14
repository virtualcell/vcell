package org.vcell.util.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * This is gonna to change a typical JTable's look and feel.
 * The header and cell dividers are all in light blue.
 * No divider in header and blue lines as table cell divider.
 * author: Tracy Li
 * version: 1.0
 */
public class StyleTable extends JTable {

    public static Color color = new Color(166, 166, 255); // light blue

    public StyleTable(TableModel dm) {
        super(dm);
        setGridColor(color);
        setBorder(BorderFactory.createLineBorder(color, 2));
        setRowHeight(getRowHeight()+5);
        JTableHeader header = getTableHeader();
        header.setDefaultRenderer(createPlainTableHeaderRenderer(color));
    }

    public StyleTable() {
        setGridColor(color);
        setBorder(BorderFactory.createLineBorder(color, 2));
        setRowHeight(getRowHeight()+5);
        JTableHeader header = getTableHeader();
        header.setDefaultRenderer(createPlainTableHeaderRenderer(color));

    }
    
    /**
     * Create plain table header cell renderer
     */
    public static TableCellRenderer createPlainTableHeaderRenderer(final Color color) {
        return new TableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel comp = new JLabel(value.toString(), JLabel.LEFT);
                comp.setBackground(color);
                comp.setOpaque(true);
                comp.setBorder( new EmptyBorder(1, 5, 1, 1));
                return comp;
            }

        };
    }

}
