package cbit.vcell.solver.ode.gui;

import cbit.plot.gui.ClusterDataPanel;
import cbit.plot.gui.ClusterPlotPanel;
import cbit.plot.gui.MoleculeDataPanel;
import cbit.plot.gui.MoleculePlotPanel;
import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.LangevinSolverResultSet;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.util.ColumnDescription;
import org.vcell.util.ColorUtil;
import org.vcell.util.gui.SpecialtyTableRenderer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

public class MoleculeVisualizationPanel extends AbstractVisualizationPanel {

    private final ODEDataViewer owner;
    LangevinSolverResultSet langevinSolverResultSet = null;
    SimulationModelInfo simulationModelInfo = null;
    MoleculeVisualizationPanel.IvjEventHandler ivjEventHandler = new MoleculeVisualizationPanel.IvjEventHandler();

    private final Map<String, Color> persistentColorMap = new LinkedHashMap<>();
    private final java.util.List<Color> globalPalette = new ArrayList<>();
    private int nextColorIndex = 0;

    private MoleculePlotPanel moleculePlotPanel = null;   // here are the plots being drawn
    private MoleculeDataPanel moleculeDataPanel = null;   // here resides the data table


    class IvjEventHandler implements ActionListener, PropertyChangeListener, ListSelectionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof Component c && SwingUtilities.isDescendingFrom(c, MoleculeVisualizationPanel.this)) {
                System.out.println(this.getClass().getName() + ".actionPerformed() called with " + e.getActionCommand());
                // switch selection between plot panel and data panel (located in a JCardLayout)
                String cmd = e.getActionCommand();
                // --- Card switching (plot <-> data) ---
                if (cmd.equals("PlotPanelContainer") || cmd.equals("DataPanelContainer")) {
                    CardLayout cl = (CardLayout) getCardPanel().getLayout();
                    cl.show(getCardPanel(), cmd);                                         // show the plot or the data panel
                    getPlotButton().setSelected(cmd.equals("PlotPanelContainer"));   // update button selection state
                    getDataButton().setSelected(cmd.equals("DataPanelContainer"));
                    getLegendPanel().setVisible(cmd.equals("PlotPanelContainer"));   // show legend only in plot mode
                    getCrosshairCheckBox().setVisible(cmd.equals("PlotPanelContainer"));    // show/hide crosshair checkbox only in plot mode
                    setCrosshairEnabled(cmd.equals("PlotPanelContainer") && getCrosshairCheckBox().isSelected());   // enable/disable crosshair logic
                    return;
                }
                // --- Crosshair checkbox toggled ---
                if (e.getSource() == getCrosshairCheckBox()) {
                    boolean enabled = getCrosshairCheckBox().isSelected();
                    setCrosshairEnabled(enabled);
                    return;
                }
            }
        }
        @Override
        public void propertyChange(PropertyChangeEvent evt) {   // listens to changes in the MoleculeSpecificationPanel
            if (evt.getSource() == owner.getMoleculeSpecificationPanel() && "MoleculeSelectionChanged".equals(evt.getPropertyName())) {
                System.out.println(this.getClass().getName() + ".propertyChange() called with " + evt.getPropertyName());
                // redraw everything based on the new selections
                MoleculeSpecificationPanel.MoleculeSelection sel = (MoleculeSpecificationPanel.MoleculeSelection) evt.getNewValue();
                ensureColorsAssigned(sel.selectedColumns);
                try {
                    redrawLegend(sel);      // redraw legend (one plot, multiple curves)
                    redrawPlot(sel);        // redraw plot (one plot, multiple curves)
                    redrawDataTable(sel);   // redraw data table
                } catch (ExpressionException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() instanceof Component c && SwingUtilities.isDescendingFrom(c, MoleculeVisualizationPanel.this)) {
                System.out.println(this.getClass().getName() + ".valueChanged() called");
            }
        }
    }

    public MoleculeVisualizationPanel(ODEDataViewer owner) {
        super();
        this.owner = owner;
        initialize();
        initConnections();
        setVisualizationBackground(Color.WHITE);
    }


    @Override
    protected JPanel createPlotPanel() {
        return getMoleculePlotPanel();
    }

    @Override
    protected JPanel createDataPanel() {
        return getMoleculeDataPanel();
    }

    @Override
    protected void setCrosshairEnabled(boolean enabled) {
        getMoleculePlotPanel().setCrosshairEnabled(enabled);
    }

    public void setVisualizationBackground(Color color) {
        super.setVisualizationBackground(color);
        getMoleculePlotPanel().setBackground(color);
        getMoleculeDataPanel().setBackground(color);
    }

    private MoleculePlotPanel getMoleculePlotPanel() {      // actual plotting is shown here
        if (moleculePlotPanel == null) {
            try {
                moleculePlotPanel = new MoleculePlotPanel();
                moleculePlotPanel.setName("ClusterPlotPanel");
                moleculePlotPanel.setCoordinateCallback(coords -> {
                    if (coords == null) {
                        clearCrosshairCoordinates();
                    } else {
                        updateCrosshairCoordinates(coords[0], coords[1]);
                    }
                });
                moleculePlotPanel.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentShown(ComponentEvent e) {
                        System.out.println(this.getClass().getSimpleName() + ".componentShown() called, height = " + moleculePlotPanel.getHeight());
                    }
                });
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return moleculePlotPanel;
    }
    public MoleculeDataPanel getMoleculeDataPanel() {               // actual table shown here
        if (moleculeDataPanel == null) {
            try {
                moleculeDataPanel = new MoleculeDataPanel();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return moleculeDataPanel;
    }

    // ---------------------------------------------------------------

    protected void initConnections() {
        initializeGlobalPalette();      // get a stable, high contrast palette
        // group the two buttons so only one stays selected
        ButtonGroup bg = new ButtonGroup();
        bg.add(getPlotButton());
        bg.add(getDataButton());

        // add the shared handler
        getPlotButton().addActionListener(ivjEventHandler);
        getDataButton().addActionListener(ivjEventHandler);

        // crosshair checkbox is plot-specific, so subclass handles it
        getCrosshairCheckBox().addActionListener(ivjEventHandler);

        // listen to the left panel
        owner.getMoleculeSpecificationPanel().addPropertyChangeListener(ivjEventHandler);

    }




    // ----------------------------------------------------------------------


    private void redrawPlot(MoleculeSpecificationPanel.MoleculeSelection sel) throws ExpressionException {
//        ClusterPlotPanel plot = getMoleculePlotPanel();
//        plot.clearAllRenderers();
//
//        if (langevinSolverResultSet == null || !langevinSolverResultSet.isAverageDataAvailable()) {
//            return;
//        }
//
//        ODESimData avgData = langevinSolverResultSet.getAvg();
//        ODESimData minData = langevinSolverResultSet.getMin();
//        ODESimData maxData = langevinSolverResultSet.getMax();
//        ODESimData stdData = langevinSolverResultSet.getStd();
//
//        int indexTime = avgData.findColumn("t");
//        double[] time = avgData.extractColumn(indexTime);
//
//        for (ColumnDescription cd : sel.selectedColumns) {
//            String columnName = cd.getName();
//            Color baseColor = persistentColorMap.get(columnName);
//            Color mMColor = deriveMinMaxColor(baseColor);
//            Color sDColor = deriveSDColor(baseColor);
//
//            MoleculeSpecificationPanel.StatisticSelection ss;
//            // --- AVG ---
//            ss = MoleculeSpecificationPanel.StatisticSelection.AVG;
//            if (sel.selectedStatistics.contains(ss)) {
//                double[] avg = LangevinSolverResultSet.getSeries(avgData, columnName);
//                if (avg != null) {
//                    plot.addAvgRenderer(time, avg, baseColor, columnName, ss);
//                }
//            }
//
//            // --- MIN/MAX ---
//            ss = MoleculeSpecificationPanel.StatisticSelection.MIN_MAX;
//            if (sel.selectedStatistics.contains(ss)) {
//                double[] min = LangevinSolverResultSet.getSeries(minData, columnName);
//                double[] max = LangevinSolverResultSet.getSeries(maxData, columnName);
//                if (min != null && max != null) {
//                    plot.addMinMaxRenderer(time, min, max, mMColor, columnName, ss);
//                }
//            }
//
//            // --- SD ---
//            ss = MoleculeSpecificationPanel.StatisticSelection.SD;
//            if (sel.selectedStatistics.contains(ss)) {
//                double[] avg = LangevinSolverResultSet.getSeries(avgData, columnName);
//                double[] sd  = LangevinSolverResultSet.getSeries(stdData, columnName);
//
//                if (avg != null && sd != null) {
//                    double[] low  = new double[avg.length];
//                    double[] high = new double[avg.length];
//                    for (int i = 0; i < avg.length; i++) {
//                        low[i]  = avg[i] - sd[i];
//                        high[i] = avg[i] + sd[i];
//                    }
//                    plot.addSDRenderer(time, low, high, sDColor, columnName, ss);
//                }
//            }
//        }
//        plot.repaint();
    }

    private void redrawLegend(MoleculeSpecificationPanel.MoleculeSelection sel) throws ExpressionException {

    }

    private void redrawDataTable(MoleculeSpecificationPanel.MoleculeSelection sel) throws ExpressionException {
        System.out.println(this.getClass().getSimpleName() + ".updateDataTable() called");
        getMoleculeDataPanel().updateData(sel, langevinSolverResultSet);

    }

    private void handleException(java.lang.Throwable exception) {
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }
    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {
        System.out.println(this.getClass().getSimpleName() + ".onSelectedObjectsChange() called with " + selectedObjects.length + " objects");

    }
    public void refreshData() {
        System.out.println(this.getClass().getSimpleName() + ".refreshData() called");
        simulationModelInfo = owner.getSimulationModelInfo();
        langevinSolverResultSet = owner.getLangevinSolverResultSet();

    }

    // -------------------------------------------------------------------------------

    private void initializeGlobalPalette() {
        // Use a curated palette from ColorUtil
        globalPalette.clear();
        globalPalette.addAll(Arrays.asList(ColorUtil.TABLEAU20));

        // Reserve ACS and ACO immediately
        ensureColorsAssigned("ACS");
        ensureColorsAssigned("ACO");

        // SD derives from ACS (does NOT consume a palette slot)
        Color acsColor = persistentColorMap.get("ACS");
        Color sdColor = deriveEnvelopeColor(acsColor);
        persistentColorMap.put("SD", sdColor);
    }
    private void ensureColorsAssigned(List<ColumnDescription> columns) {
        // assign colors only when needed, and keep them consistent across updates
        for (ColumnDescription cd : columns) {
            String name = cd.getName();
            ensureColorsAssigned(name);
        }
    }
    private void ensureColorsAssigned(String name) {
        if (!persistentColorMap.containsKey(name)) {
            Color c = globalPalette.get(nextColorIndex % globalPalette.size());
            persistentColorMap.put(name, c);
            nextColorIndex++;
        }
    }
    private Color deriveEnvelopeColor(Color base) {
        return new Color(
                base.getRed(),
                base.getGreen(),
                base.getBlue(),
                80   // smaller number means lighter color (more transparent)
        );
    }

    public void setSpecialityRenderer(SpecialtyTableRenderer str) {
//        getClusterDataPanel().setSpecialityRenderer(str);
    }


}
