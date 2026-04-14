package cbit.plot.gui;

import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.UiTableExporterToHDF5;
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
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.io.File;

public class ClusterDataPanel extends AbstractDataPanel {

    private static final Logger lg = LogManager.getLogger(ClusterDataPanel.class);

    // -------------------------------------------------------------------------
    // Cluster-specific header renderer
    // -------------------------------------------------------------------------
    private class ClusterHeaderRenderer extends DefaultTableCellRenderer {

        private final TableCellRenderer base;

        public ClusterHeaderRenderer(TableCellRenderer base) {
            this.base = base;
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = base.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);

            if (!(c instanceof JLabel)) {
                return c;
            }

            JLabel lbl = (JLabel) c;
            String name = value == null ? "" : value.toString();

            ClusterSpecificationPanel.DisplayMode mode =
                    (ClusterSpecificationPanel.DisplayMode)
                            ((JComponent) table).getClientProperty("ClusterDisplayMode");

            if (mode == null) {
                lbl.setToolTipText(null);
                return lbl;
            }

            String text = "";
            String unit = "";
            String tooltip = "";

            ClusterSpecificationPanel.ClusterStatistic stat =
                    ClusterSpecificationPanel.ClusterStatistic.fromString(name);

            if (column == 0) {
                unit = "seconds";
                text = "<html>" + name + "<font color=\"#8B0000\"> [" + unit + "]</font></html>";
                tooltip = "Simulation time";
            } else {
                switch (mode) {
                    case COUNTS:
                        unit = "molecules";
                        text = "<html>" + name + "<font color=\"#8B0000\"> [" + unit + "]</font></html>";
                        tooltip = "<html>Number of clusters made of <b>" + name + "</b> " + unit + "</html>";
                        break;

                    case MEAN:
                    case OVERALL:
                        if (stat != null) {
                            unit = stat.unit();
                            text = "<html>" + stat.fullName() +
                                    "<font color=\"#8B0000\"> [" + unit + "]</font></html>";
                            tooltip = "<html>" + stat.description() + "</html>";
                        }
                        break;
                }
            }

            lbl.setText(text);
            lbl.setToolTipText(tooltip);
            return lbl;
        }
    }

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    public ClusterDataPanel() {
        super();
    }

    // -------------------------------------------------------------------------
    // Hook up cluster-specific header renderer
    // -------------------------------------------------------------------------
    @Override
    protected void initConnections() throws Exception {
        super.initConnections();

        TableCellRenderer baseHeaderRenderer =
                getScrollPaneTable().getTableHeader().getDefaultRenderer();

        getScrollPaneTable().getTableHeader()
                .setDefaultRenderer(new ClusterHeaderRenderer(baseHeaderRenderer));
    }

    // -------------------------------------------------------------------------
    // Override copy handler
    // -------------------------------------------------------------------------
    @Override
    protected void onCopyCells(boolean isHDF5) {
        copyCells(isHDF5);
    }

    // -------------------------------------------------------------------------
    // Instance version of old static copyCells()
    // -------------------------------------------------------------------------
    private void copyCells(boolean isHDF5) {
        try {
            int r = getScrollPaneTable().getRowCount();
            int c = getScrollPaneTable().getColumnCount();

            if (r < 1 || c < 1) {
                throw new Exception("No table cell is selected.");
            }

            int[] rows = new int[r];
            int[] columns = new int[c];
            for (int i = 0; i < r; i++) rows[i] = i;
            for (int i = 0; i < c; i++) columns[i] = i;

            lg.debug("Copying cluster data: rows=" + r + " columns=" + c + " isHDF5=" + isHDF5);

            boolean bHistogram = false;
            String blankCellValue = "-1";
            boolean bHasTimeColumn = true;

            StringBuffer buffer = new StringBuffer();

            if (isHDF5) {
                int columnCount = c;
                int rowCount = r;

                String[] columnNames = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    columnNames[i] = getScrollPaneTable().getColumnName(i);
                }

                Object[][] rowColValues = new Object[rowCount][columnCount];
                for (int i = 0; i < rowCount; i++) {
                    for (int j = 0; j < columnCount; j++) {
                        rowColValues[i][j] = getScrollPaneTable().getValueAt(i, j);
                    }
                }

                UiTableExporterToHDF5.exportTableToHDF5(
                        bHistogram,
                        blankCellValue,
                        columns,
                        rows,
                        "t",
                        "hdf5DescriptionText",
                        columnNames,
                        null,
                        null,
                        rowColValues
                );
            }

            // Column headers
            for (int i = 0; i < c; i++) {
                buffer.append(getScrollPaneTable().getColumnName(i));
                if (i < c - 1) buffer.append("\t");
            }

            Expression[] resolvedValues =
                    new Expression[c - (bHasTimeColumn ? 1 : 0)];

            // Rows
            for (int i = 0; i < r; i++) {
                buffer.append("\n");
                for (int j = 0; j < c; j++) {
                    Object cell = getScrollPaneTable().getValueAt(i, j);
                    cell = (cell != null ? cell : "");

                    buffer.append(cell.toString());
                    if (j < c - 1) buffer.append("\t");

                    if (!cell.equals("") && (!bHasTimeColumn || j > 0)) {
                        resolvedValues[j - (bHasTimeColumn ? 1 : 0)] =
                                new Expression(((Double) cell).doubleValue());
                    }
                }
            }

            VCellTransferable.ResolvedValuesSelection rvs =
                    new VCellTransferable.ResolvedValuesSelection(
                            new SymbolTableEntry[c - 1],
                            null,
                            resolvedValues,
                            buffer.toString()
                    );

            VCellTransferable.sendToClipboard(rvs);

        } catch (Exception ex) {
            lg.error("Error copying cluster data", ex);
            JOptionPane.showMessageDialog(
                    this,
                    "Error copying cluster data: " + ex.getMessage(),
                    "Copy Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void updateData(ClusterSpecificationPanel.ClusterSelection sel)
            throws ExpressionException {

        if (sel == null) {
            getScrollPaneTable().putClientProperty("ClusterDisplayMode", null);
        } else {
            getScrollPaneTable().putClientProperty("ClusterDisplayMode", sel.mode);
        }

        if (sel == null || sel.resultSet == null ||
                sel.columns == null || sel.columns.isEmpty()) {

            getNonEditableDefaultTableModel()
                    .setDataVector(new Object[][]{}, new Object[]{"No data"});

            getScrollPaneTable().createDefaultColumnsFromModel();
            revalidate();
            repaint();
            return;
        }

        ODESolverResultSet srs = sel.resultSet;
        java.util.List<ColumnDescription> columns = sel.columns;

        int timeIndex = srs.findColumn(ReservedVariable.TIME.getName());
        double[] times = srs.extractColumn(timeIndex);
        int rowCount = times.length;

        String[] columnNames = new String[1 + columns.size()];
        columnNames[0] = "time";
        for (int i = 0; i < columns.size(); i++) {
            columnNames[i + 1] = columns.get(i).getName();
        }

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

        getNonEditableDefaultTableModel().setDataVector(data, columnNames);
        getScrollPaneTable().createDefaultColumnsFromModel();
        autoSizeTableColumns(getScrollPaneTable());

        revalidate();
        repaint();
    }

    // -------------------------------------------------------------------------
    // autoSizeTableColumns() — unchanged
    // -------------------------------------------------------------------------
    private void autoSizeTableColumns(JTable table) {
        final int margin = 12;

        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = table.getColumnModel().getColumn(col);

            int maxWidth = 0;

            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                    table, column.getHeaderValue(), false, false, 0, col);
            maxWidth = Math.max(maxWidth, headerComp.getPreferredSize().width);

            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, col);
                Component comp = table.prepareRenderer(cellRenderer, row, col);
                maxWidth = Math.max(maxWidth, comp.getPreferredSize().width);
            }

            column.setPreferredWidth(maxWidth + margin);
        }
    }

    public void setSpecialityRenderer(SpecialtyTableRenderer str) {
        // TODO: write some appropriate renderer when we decide what to show in the tooltip
        //  use RendererViewerDoubleWithTooltip for inspiration
//    getScrollPaneTable().setSpecialityRenderer(str);
    }

}
