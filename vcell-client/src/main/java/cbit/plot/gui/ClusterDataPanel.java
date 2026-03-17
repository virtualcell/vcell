package cbit.plot.gui;

import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.gui.ClusterSpecificationPanel;
import cbit.vcell.util.ColumnDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.gui.NonEditableDefaultTableModel;
import org.vcell.util.gui.ScrollTable;
import org.vcell.util.gui.SpecialtyTableRenderer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ClusterDataPanel extends JPanel {

    private static final Logger LG = LogManager.getLogger(ClusterDataPanel.class);

    private ScrollTable scrollPaneTable;
    private NonEditableDefaultTableModel nonEditableDefaultTableModel = null;

    private JPopupMenu popupMenu = null;
    private JMenuItem miCopyAll = null;
    private JMenuItem miCopyHDF5 = null;
    private static enum CopyAction {copy,copyrow,copyall};

    private final IvjEventHandler ivjEventHandler = new IvjEventHandler();

    class IvjEventHandler implements ActionListener, MouseListener, PropertyChangeListener, ChangeListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getSource() == getScrollPaneTable()) {
                int row = getScrollPaneTable().rowAtPoint(e.getPoint());
                int col = getScrollPaneTable().columnAtPoint(e.getPoint());
                System.out.println("ClusterDataPanel: clicked row=" + row + " col=" + col);
                if (SwingUtilities.isRightMouseButton(e)) {
                    getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }

        @Override public void mousePressed(MouseEvent e) {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}

        @Override
        public void actionPerformed(ActionEvent e) {
            // reserved for future
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            // reserved for future dynamic formatting
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            // reserved for future slider/spinner interactions
        }
    }

    private class ClusterHeaderRenderer extends DefaultTableCellRenderer {

        private final TableCellRenderer base;
        public ClusterHeaderRenderer(TableCellRenderer base) {
            this.base = base;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            // First let ScrollTable’s renderer do its work
            Component c = base.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!(c instanceof JLabel)) {
                return c;   // safety
            }
            JLabel lbl = (JLabel) c;
            String name = value == null ? "" : value.toString();

            // Read mode from table metadata
            ClusterSpecificationPanel.DisplayMode mode = (ClusterSpecificationPanel.DisplayMode)
                            ((JComponent)table).getClientProperty("ClusterDisplayMode");

            // First-time creation: no mode yet
            if (mode == null) {
                lbl.setToolTipText(null);
                return lbl;   // leave ScrollTable’s default header styling intact
            }

            String unit = "";
            String tooltip = "";
            if (column == 0) {
                unit = "seconds";
                tooltip = "Simulation time in seconds";
            } else {
                switch (mode) {
                    case COUNTS:
                        unit = "molecules";
                        tooltip = "Number of clusters of size " + name + " molecules";
                        break;
                    case MEAN:
                        unit = "value";
                        tooltip = "Cluster mean statistic for " + name;
                        break;
                    case OVERALL:
                        unit = "value";
                        tooltip = "Cluster overall statistic for " + name;
                        break;
                }
            }
            lbl.setText("<html>" + name + "<font color=\"#8B0000\"> [" + unit + "]</font></html>");
            lbl.setToolTipText(tooltip);
            return lbl;
        }
    }

    public ClusterDataPanel() {
        super();
        initialize();
    }
    private void initialize() {
        try {
            setName("ClusterDataPanel");
            setLayout(new java.awt.BorderLayout());
            setSize(541, 348);
            add(getScrollPaneTable().getEnclosingScrollPane(), BorderLayout.CENTER);
            JLabel lblNewLabel = new JLabel("<html>To <b>Copy</b> table data or <b>Export</b> as HDF5, select rows/cells and use the right mouse button menu.</html>");
            add(lblNewLabel, BorderLayout.SOUTH);
            initConnections();
//            controlKeys();
        } catch (Throwable exc) {
            handleException(exc);
        }
    }
    private void initConnections() throws java.lang.Exception {
        this.addPropertyChangeListener(ivjEventHandler);
        getScrollPaneTable().addMouseListener(ivjEventHandler);
        getScrollPaneTable().setModel(getNonEditableDefaultTableModel());
        getScrollPaneTable().createDefaultColumnsFromModel();
        TableCellRenderer baseHeaderRenderer = getScrollPaneTable().getTableHeader().getDefaultRenderer();
        getScrollPaneTable().getTableHeader().setDefaultRenderer(new ClusterHeaderRenderer(baseHeaderRenderer));

    }
    private void handleException(java.lang.Throwable exception) {
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    // -----------------------------------------------------------
    private ScrollTable getScrollPaneTable() {
        if (scrollPaneTable == null) {
            try {
                scrollPaneTable = new ScrollTable();
                scrollPaneTable.setName("ScrollPaneTable");
                scrollPaneTable.setCellSelectionEnabled(true);
                scrollPaneTable.setBounds(0, 0, 200, 200);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return scrollPaneTable;
    }

    private NonEditableDefaultTableModel getNonEditableDefaultTableModel() {
        if (nonEditableDefaultTableModel == null) {
            try {
                nonEditableDefaultTableModel = new NonEditableDefaultTableModel();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return nonEditableDefaultTableModel;
    }

    private JPopupMenu getPopupMenu() {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();

            miCopyAll = new JMenuItem("Copy All");
            miCopyAll.addActionListener(e -> copyCells(this, false));
            popupMenu.add(miCopyAll);

            miCopyHDF5 = new JMenuItem("Copy to HDF5");
            miCopyHDF5.addActionListener(e -> copyCells(this,true));
            popupMenu.add(miCopyHDF5);
        }
        return popupMenu;
    }

    // -----------------------------------------------------------------------------------------------
    private static synchronized void copyCells(ClusterDataPanel cdp, boolean isHDF5) {
        try {
            int r = 0;
            int c = 0;
            int[] rows = new int[0];
            int[] columns = new int[0];
            r = cdp.getScrollPaneTable().getRowCount();
            c = cdp.getScrollPaneTable().getColumnCount();
            rows = new int[r];
            columns = new int[c];
            for (int i = 0; i < rows.length; i++){
                rows[i] = i;
            }
            for (int i = 0; i < columns.length; i++){
                columns[i] = i;
            }
            if(rows.length < 1 || columns.length < 1)
            {
                throw new Exception("No table cell is selected.");
            }
            System.out.println("Copying cluster data: rows=" + rows.length + " columns=" + columns.length + " isHDF5=" + isHDF5);





        } catch (Exception ex) {
            LG.error("Error copying cluster data", ex);
            JOptionPane.showMessageDialog(cdp, "Error copying cluster data: " + ex.getMessage(), "Copy Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setSpecialityRenderer(SpecialtyTableRenderer str) {
        // TODO: write some appropriate renderer when we decide what to show in the tooltip
        //  use RendererViewerDoubleWithTooltip for inspiration
//        getScrollPaneTable().setSpecialityRenderer(str);
    }


    public void updateData(ClusterSpecificationPanel.ClusterSelection sel) throws ExpressionException {
        if(sel == null) {
            getScrollPaneTable().putClientProperty("ClusterDisplayMode", null);
        } else {
            getScrollPaneTable().putClientProperty("ClusterDisplayMode", sel.mode);
        }
        if (sel == null || sel.resultSet == null || sel.columns == null || sel.columns.isEmpty()) {
            getNonEditableDefaultTableModel().setDataVector(new Object[][]{}, new Object[]{"No data"});
            getScrollPaneTable().createDefaultColumnsFromModel();
            revalidate();
            repaint();
            return;
        }
        ODESolverResultSet srs = sel.resultSet;
        java.util.List<ColumnDescription> columns = sel.columns;

        int timeIndex = srs.findColumn("t");
        double[] times = srs.extractColumn(timeIndex);
        int rowCount = times.length;

        // column names
        String[] columnNames = new String[1 + columns.size()];
        columnNames[0] = "time";
        for (int i = 0; i < columns.size(); i++) {
            columnNames[i + 1] = columns.get(i).getName();
        }

        // data
        Object[][] data = new Object[rowCount][columnNames.length];
        for (int r = 0; r < rowCount; r++) {
            int c = 0;
            data[r][c++] = times[r];
            for (ColumnDescription cd : columns) {
                int idx = srs.findColumn(cd.getName());
                double[] y = srs.extractColumn(idx);
                data[r][c++] = y[r];
            }
        }

        // update existing model
        getNonEditableDefaultTableModel().setDataVector(data, columnNames);

        // refresh table columns
        getScrollPaneTable().createDefaultColumnsFromModel();
        autoSizeTableColumns(getScrollPaneTable());

        revalidate();
        repaint();
    }

    private void autoSizeTableColumns(JTable table) {
        final int margin = 12;

        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = table.getColumnModel().getColumn(col);

            int maxWidth = 0;

            // header
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                    table, column.getHeaderValue(), false, false, 0, col);
            maxWidth = Math.max(maxWidth, headerComp.getPreferredSize().width);

            // cells
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, col);
                Component comp = table.prepareRenderer(cellRenderer, row, col);
                maxWidth = Math.max(maxWidth, comp.getPreferredSize().width);
            }

            column.setPreferredWidth(maxWidth + margin);
        }
    }
}