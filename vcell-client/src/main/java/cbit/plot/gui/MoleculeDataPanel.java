package cbit.plot.gui;

import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.desktop.VCellTransferable;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.parser.Expression;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.parser.SymbolTableEntry;
import cbit.vcell.simdata.LangevinSolverResultSet;
import cbit.vcell.simdata.UiTableExporterToHDF5;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.solver.ode.gui.ClusterSpecificationPanel;
import cbit.vcell.solver.ode.gui.MoleculeSpecificationPanel;
import cbit.vcell.util.ColumnDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.gui.NonEditableDefaultTableModel;
import org.vcell.util.gui.ScrollTable;

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

public class MoleculeDataPanel extends AbstractDataPanel {

    public enum SubStatistic {
        AVG("AVG"),
        MIN("MIN"),
        MAX("MAX"),
        SD("SD");
        public final String uiLabel;
        SubStatistic(String uiLabel) {
            this.uiLabel = uiLabel;
        }
    }

    public static class MoleculeColumnInfo {
        public final String entityName;
        public final MoleculeSpecificationPanel.StatisticSelection statistic;
        public final SubStatistic subStatistic;
        public MoleculeColumnInfo(String entityName,
                                  MoleculeSpecificationPanel.StatisticSelection statistic,
                                  SubStatistic subStatistic) {
            this.entityName = entityName;
            this.statistic = statistic;
            this.subStatistic = subStatistic;
        }
    }


    private class MoleculeTwoRowHeaderRenderer extends DefaultTableCellRenderer {

        private final TableCellRenderer base;
        public MoleculeTwoRowHeaderRenderer(TableCellRenderer base) {
            this.base = base;
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component c = base.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!(c instanceof JLabel)) {
                return c;
            }
            JLabel lbl = (JLabel) c;
            Object id = table.getColumnModel().getColumn(column).getIdentifier();
            if (!(id instanceof MoleculeColumnInfo)) {      // time column w. unit in dark red
                lbl.setText("<html><center>" +"time" +"<br/><font size='2'>" +
                            "<font color='#8B0000'> [seconds]</font></font>" + "</center></html>");
                return lbl;
            }
            MoleculeColumnInfo info = (MoleculeColumnInfo) id;
            String statLabel = "<b>" + info.subStatistic.uiLabel + "</b>";  // Statistic label (bold)
            String unit = "<font color=\"#8B0000\"> [molecules]</font>";    // Unit in dark red
            lbl.setText("<html><center>" + info.entityName + "<br/>" +
                        "<font size='2'>" + statLabel + unit + "</font></center></html>"
            );
            return lbl;
        }
    }

    // ------------------------------------------------------

    ODEDataViewer owner;

    public MoleculeDataPanel(ODEDataViewer owner) {
        super();
        this.owner = owner;
    }
    @Override
    protected void initConnections() throws Exception {
        super.initConnections();
        TableCellRenderer baseHeaderRenderer = getScrollPaneTable().getTableHeader().getDefaultRenderer();
        getScrollPaneTable().getTableHeader().setDefaultRenderer(new MoleculeTwoRowHeaderRenderer(baseHeaderRenderer));
    }

    // ------------------------------------------------------

    public void updateData(MoleculeSpecificationPanel.MoleculeSelection sel, LangevinSolverResultSet lsrs)
            throws ExpressionException {

        if (sel == null) {
            getScrollPaneTable().putClientProperty("MoleculeSelection", null);    // may be useful for tooltip generation
        } else {
            getScrollPaneTable().putClientProperty("MoleculeSelection", sel);
        }

        if (sel == null || lsrs == null || lsrs.isAverageDataAvailable() == false ||    // guard clause
                sel.selectedColumns == null || sel.selectedColumns.isEmpty() ||
                sel.selectedStatistics == null || sel.selectedStatistics.isEmpty()) {

            getNonEditableDefaultTableModel().setDataVector(new Object[][]{}, new Object[]{"No data"});
            getScrollPaneTable().createDefaultColumnsFromModel();
            revalidate();
            repaint();
            return;
        }

        ODESolverResultSet avgRS = lsrs.getAvg();
        ODESolverResultSet minRS = lsrs.getMin();
        ODESolverResultSet maxRS = lsrs.getMax();
        ODESolverResultSet stdRS = lsrs.getStd();
        java.util.List<ColumnDescription> selectedColumns = sel.selectedColumns;
        java.util.List<MoleculeSpecificationPanel.StatisticSelection> selectedStatistics = sel.selectedStatistics;

        int timeIndex = avgRS.findColumn(ReservedVariable.TIME.getName());
        double[] times = avgRS.extractColumn(timeIndex);
        int rowCount = times.length;
        OutputTimeSpec outputTimeSpec = owner.getSimulation().getSolverTaskDescription().getOutputTimeSpec();
        double dt = ((UniformOutputTimeSpec)outputTimeSpec).getOutputTimeStep();    // uniform output time step
        double endingTime = owner.getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime();
        for (int i = 0; i < times.length; i++) {
            times[i] = i * dt;
        }



        // ---------------------------------------------------------------------
        // Build column names
        // ---------------------------------------------------------------------
        java.util.List<String> colNames = new java.util.ArrayList<>();
        java.util.List<MoleculeColumnInfo> metadata = new java.util.ArrayList<>();

        colNames.add("time");
        metadata.add(null); // time has no entity/statistic

        for (ColumnDescription cd : selectedColumns) {
            String base = cd.getName();
            for (MoleculeSpecificationPanel.StatisticSelection statSel : sel.selectedStatistics) {
                switch (statSel) {
                    case AVG:
                        colNames.add(base + " AVG");
                        metadata.add(new MoleculeColumnInfo(base, statSel, SubStatistic.AVG));
                        break;

                    case MIN_MAX:
                        colNames.add(base + " MIN");
                        metadata.add(new MoleculeColumnInfo(base, statSel, SubStatistic.MIN));
                        colNames.add(base + " MAX");
                        metadata.add(new MoleculeColumnInfo(base, statSel, SubStatistic.MAX));
                        break;

                    case SD:
                        colNames.add(base + " SD");
                        metadata.add(new MoleculeColumnInfo(base, statSel, SubStatistic.SD));
                        break;
                }
            }
        }

        // ---------------------------------------------------------------------
        // Build data matrix
        // ---------------------------------------------------------------------
        Object[][] data = new Object[rowCount][colNames.size()];

        for (int r = 0; r < rowCount; r++) {
            int c = 0;
            data[r][c++] = times[r];

            for (ColumnDescription cd : selectedColumns) {
                String baseName = cd.getName();
                // These are guaranteed to exist because of guard clause
                double[] avgCol = avgRS.extractColumn(avgRS.findColumn(baseName));
                double[] minCol = minRS.extractColumn(minRS.findColumn(baseName));
                double[] maxCol = maxRS.extractColumn(maxRS.findColumn(baseName));
                double[] stdCol = stdRS.extractColumn(stdRS.findColumn(baseName));
                for (MoleculeSpecificationPanel.StatisticSelection statSel : selectedStatistics) {
                    switch (statSel) {
                        case AVG:
                            data[r][c++] = avgCol[r];
                            break;
                        case MIN_MAX:
                            data[r][c++] = minCol[r];
                            data[r][c++] = maxCol[r];
                            break;
                        case SD:
                            data[r][c++] = stdCol[r];   // raw SD value
                            break;
                    }
                }
            }
        }
        // Push to model
        getNonEditableDefaultTableModel().setDataVector(
                data,
                colNames.toArray(new String[0])
        );

        // Rebuild columns so we can attach metadata + renderer
        getScrollPaneTable().createDefaultColumnsFromModel();
        for (int i = 0; i < metadata.size(); i++) {
            getScrollPaneTable().getColumnModel().getColumn(i)
                    .setIdentifier(metadata.get(i));
        }
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

    // ------------------------------------------------------

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

            LG.debug("Copying cluster data: rows=" + r + " columns=" + c + " isHDF5=" + isHDF5);

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
            LG.error("Error copying cluster data", ex);
            JOptionPane.showMessageDialog(
                    this,
                    "Error copying cluster data: " + ex.getMessage(),
                    "Copy Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

}
