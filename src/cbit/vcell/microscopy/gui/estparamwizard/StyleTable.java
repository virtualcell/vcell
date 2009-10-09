package cbit.vcell.microscopy.gui.estparamwizard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * 
 */
public class StyleTable extends JTable {

    public static Color color = new Color(166, 166, 255); //Preference.getUser().getColor (Preference.REPORT_TABLE_COLOR); // light blue

    public StyleTable(TableModel dm) {
        super(dm);
        setGridColor(color);
        setBorder(BorderFactory.createLineBorder(color, 2));
        setRowHeight(getRowHeight()+5);
        setRowSelectionAllowed(false);
        setDefaultRenderer(("").getClass(), new AnalysisTableRenderer(8));//double precision 8 digits
        JTableHeader header = getTableHeader();
        header.setDefaultRenderer(createPlainTableHeaderRenderer(color));
    }

    public StyleTable() {
        setGridColor(color);
        setBorder(BorderFactory.createLineBorder(color, 2));
        setRowHeight(getRowHeight()+5);
        setRowSelectionAllowed(false);
        setDefaultRenderer(("").getClass(), new AnalysisTableRenderer(8));//double precision 8 digits
        JTableHeader header = getTableHeader();
        header.setDefaultRenderer(createPlainTableHeaderRenderer(color));

    }
    
    /**
     * Create plain table header cell renderer
     * @param color
     * @return
     */
    public static TableCellRenderer createPlainTableHeaderRenderer(final Color color) {
        return new TableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel comp = new JLabel(value.toString(), JLabel.LEFT);
                comp.setBackground(color);
                comp.setOpaque(true);
                comp.setBorder( new EmptyBorder(1, 5, 1, 1));
                // comp.setBorder(BorderFactory.createLineBorder(color, 2));
                return comp;
            }

        };
    }

}
