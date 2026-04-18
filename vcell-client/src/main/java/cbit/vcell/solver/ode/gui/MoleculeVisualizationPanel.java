package cbit.vcell.solver.ode.gui;

import cbit.plot.gui.ClusterDataPanel;
import cbit.plot.gui.ClusterPlotPanel;
import cbit.plot.gui.MoleculeDataPanel;
import cbit.plot.gui.MoleculePlotPanel;
import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.simdata.LangevinSolverResultSet;
import cbit.vcell.solver.OutputTimeSpec;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.TimeStep;
import cbit.vcell.solver.UniformOutputTimeSpec;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.util.ColumnDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.ColorUtil;
import org.vcell.util.gui.SpecialtyTableRenderer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;

public class MoleculeVisualizationPanel extends AbstractVisualizationPanel {

    private static final Logger lg = LogManager.getLogger(MoleculeVisualizationPanel.class);

    private static final int MINMAX_ALPHA = 60;
    private static final int SD_ALPHA     = 90;

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
//                System.out.println(this.getClass().getName() + ".actionPerformed() called with " + e.getActionCommand());
                lg.debug("actionPerformed() called with command: " + e.getActionCommand());
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
                lg.debug("propertyChange() called with property: " + evt.getPropertyName());
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
                lg.debug("valueChanged() called");
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
//                        System.out.println(this.getClass().getSimpleName() + ".componentShown() called, height = " + moleculePlotPanel.getHeight());
                        lg.debug("componentShown() called, height = " + moleculePlotPanel.getHeight());
                    }
                });

                // uncomment these to override the defaulta AbstractPlotPanel renderer options, feel free to add more
//                moleculePlotPanel.setStepAvg(true);
//                moleculePlotPanel.setStepBand(true);

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return moleculePlotPanel;
    }
    public MoleculeDataPanel getMoleculeDataPanel() {               // actual table shown here
        if (moleculeDataPanel == null) {
            try {
                moleculeDataPanel = new MoleculeDataPanel(owner);
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
        lg.debug("redrawPlot() called, current selection: " + sel);

        MoleculePlotPanel plot = getMoleculePlotPanel();
        plot.clearAllRenderers();
        if (sel == null || langevinSolverResultSet == null || !langevinSolverResultSet.isAverageDataAvailable()) {
            plot.repaint();
            return;
        }

        ODESimData avgData = langevinSolverResultSet.getAvg();
        ODESimData minData = langevinSolverResultSet.getMin();
        ODESimData maxData = langevinSolverResultSet.getMax();
        ODESimData stdData = langevinSolverResultSet.getStd();

        int indexTime = avgData.findColumn("t");
        double[] time = avgData.extractColumn(indexTime);
        // time may have suffered systematic numerical precision issues, so we reconstruct it based on the
        // uniform output time step and the number of rows in the data
        OutputTimeSpec outputTimeSpec = owner.getSimulation().getSolverTaskDescription().getOutputTimeSpec();
        double dt = ((UniformOutputTimeSpec)outputTimeSpec).getOutputTimeStep();    // uniform output time step
        double endingTime = owner.getSimulation().getSolverTaskDescription().getTimeBounds().getEndingTime();
        for (int i = 0; i < time.length; i++) {
            time[i] = i * dt;
        }

        // ------------------------------------------------------------
        // FIRST PASS: collect all Y arrays and compute global min/max
        // ------------------------------------------------------------
        double globalMin = 0.0;                       // same baseline as clusters
        double globalMax = Double.NEGATIVE_INFINITY;

        // Store arrays so we don’t re-extract later
        class Series {
            double[] avg;
            double[] min;
            double[] max;
            double[] sd;
        }
        Map<String, Series> map = new LinkedHashMap<>();

        for (ColumnDescription cd : sel.selectedColumns) {
            String name = cd.getName();
            Series s = new Series();

            s.avg = LangevinSolverResultSet.getSeries(avgData, name);
            s.min = LangevinSolverResultSet.getSeries(minData, name);
            s.max = LangevinSolverResultSet.getSeries(maxData, name);
            s.sd  = LangevinSolverResultSet.getSeries(stdData, name);

            map.put(name, s);

            if (s.avg != null) {            // AVG contributes to globalMax
                for (double v : s.avg) {
                    if (v > globalMax) globalMax = v;
                }
            }
            if (s.min != null) {            // MIN/MAX contributes to globalMin/globalMax
                for (double v : s.min) {
                    if (v < globalMin) globalMin = v;
                }
            }
            if (s.max != null) {
                for (double v : s.max) {
                    if (v > globalMax) globalMax = v;
                }
            }
            if (s.avg != null && s.sd != null) {        // SD envelope contributes to globalMin/globalMax
                for (int i = 0; i < s.avg.length; i++) {
                    double upper = s.avg[i] + s.sd[i];
                    double lower = s.avg[i] - s.sd[i];
                    if (upper > globalMax) globalMax = upper;
                    if (lower < globalMin) globalMin = lower;
                }
            }
        }

        // ------------------------------------------------------------
        // SECOND PASS: add renderers in correct order
        // SD → MINMAX → AVG
        // ------------------------------------------------------------
        for (ColumnDescription cd : sel.selectedColumns) {
            String name = cd.getName();
            Series s = map.get(name);

            Color baseColor = persistentColorMap.get(name);
            Color mMColor = deriveMinMaxColor(baseColor);
            Color sDColor = deriveSDColor(baseColor);

            // --- SD band ---
            if (sel.selectedStatistics.contains(MoleculeSpecificationPanel.StatisticSelection.SD)
                    && s.avg != null && s.sd != null) {
                int n = s.avg.length;
                double[] low  = new double[n];
                double[] high = new double[n];
                for (int i = 0; i < n; i++) {
                    low[i]  = s.avg[i] - s.sd[i];
                    high[i] = s.avg[i] + s.sd[i];
                }
                plot.addSDRenderer(time, low, high, sDColor, name, MoleculeSpecificationPanel.StatisticSelection.SD);
            }

            // --- MIN/MAX band ---
            if (sel.selectedStatistics.contains(MoleculeSpecificationPanel.StatisticSelection.MIN_MAX)
                    && s.min != null && s.max != null) {
                plot.addMinMaxRenderer(time, s.min, s.max, mMColor, name, MoleculeSpecificationPanel.StatisticSelection.MIN_MAX);
            }

            // --- AVG line ---
            if (sel.selectedStatistics.contains(MoleculeSpecificationPanel.StatisticSelection.AVG)
                    && s.avg != null) {
                plot.addAvgRenderer(time, s.avg, baseColor, name, MoleculeSpecificationPanel.StatisticSelection.AVG);
            }
        }

        // ------------------------------------------------------------
        // FINALIZE
        // ------------------------------------------------------------
        if (globalMin > 0) globalMin = 0;
        plot.setGlobalMinMax(globalMin, globalMax);
        if (time.length > 1) {
            plot.setDt(time[1] - time[0]);   // times[0] == 0
        }
        plot.repaint();
    }

    public Color deriveMinMaxColor(Color base) {
        return new Color(
                base.getRed(),
                base.getGreen(),
                base.getBlue(),
                MINMAX_ALPHA
        );
    }

    public Color deriveSDColor(Color base) {
        return new Color(
                base.getRed(),
                base.getGreen(),
                base.getBlue(),
                SD_ALPHA
        );
    }

    private void redrawLegend(MoleculeSpecificationPanel.MoleculeSelection sel) throws ExpressionException {
        lg.debug("redrawLegend() called with selection: " + sel);
        JPanel legend = getLegendContentPanel();
        legend.removeAll();
        for (ColumnDescription cd : sel.selectedColumns) {
            String name = cd.getName();
            Color c = persistentColorMap.get(name);     // // assigned color for this molecule
            if (c == null) {
                // fallback or auto-assign if needed
                c = Color.GRAY;
            }
            legend.add(createLegendEntry(name, c));
        }
        legend.revalidate();
        legend.repaint();
    }
    private JComponent createLegendEntry(String name, Color color) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        String unitSymbol = "molecules";
        String tooltip = "<html><b>" + name + "</b><br>" + unitSymbol + "</html>";
        JLabel line = new JLabel(new LineIcon(color));

//        JLabel text = new JLabel("<html>" + name + " <font color=\"#8B0000\">[" + unitSymbol + "]</font></html>");
        JLabel text = new JLabel("<html>" + name + " <font color=\"#8B0000\"></font></html>");
        line.setBorder(new EmptyBorder(5, 6, 1, 0));    // top and bottom add a little vertical distance between selected entities
        text.setBorder(new EmptyBorder(1, 6, 5, 0));
        line.setToolTipText(tooltip);
        text.setToolTipText(tooltip);
        p.setToolTipText(tooltip);

        line.setAlignmentX(Component.LEFT_ALIGNMENT);   // force full-width expansion of the label, so that hovering
                                                        // anywhere on the line triggers the tooltip and hover behavior
        line.setMaximumSize(new Dimension(Integer.MAX_VALUE, line.getPreferredSize().height));  // align label to the left
        line.setHorizontalAlignment(SwingConstants.LEFT);   // force the icon itself to left-align inside the label
        line.setHorizontalTextPosition(SwingConstants.LEFT);

        text.setAlignmentX(Component.LEFT_ALIGNMENT);
        text.setMaximumSize(new Dimension(Integer.MAX_VALUE, text.getPreferredSize().height));
        text.setHorizontalAlignment(SwingConstants.LEFT);   // force align the text itself to the left inside the label
        text.setHorizontalTextPosition(SwingConstants.LEFT);
        p.add(line);
        p.add(text);

        // hover behavior - install the same listener on p, line, and text so that hovering anywhere on the legend entry triggers it
        MouseAdapter hover = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                getMoleculePlotPanel().setHoveredSeriesName(name);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // As soon as we leave line/text, we are no longer "over an entity"
                getMoleculePlotPanel().setHoveredSeriesName(null);
            }
        };
        line.addMouseListener(hover);
        text.addMouseListener(hover);

        return p;
    }

    private void redrawDataTable(MoleculeSpecificationPanel.MoleculeSelection sel) throws ExpressionException {
        lg.debug("redrawDataTable() called with selection: " + sel);
        getMoleculeDataPanel().updateData(sel, langevinSolverResultSet);
    }

    private void handleException(java.lang.Throwable exception) {
        lg.error("Uncaught exception: " + exception.getMessage(), exception);
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {
        lg.debug("onSelectedObjectsChange() called with " + selectedObjects.length + " objects");
    }

    // called directly from ODEDataViewer when a new value for vcDataIdentifier is set
    // TODO: this is a little unreliable since it depends on the order of refreshData() calls in ODEDataViewer
    // the right way to do it would be to add the simulationModelInfo and langevinSolverResultSet to MoleculeSelection object,
    // the way it's done in ClusterVisualizationPanel, so that they are always in sync with the selections in the left panel
    public void refreshData() {
        lg.debug("refreshData() called");
        simulationModelInfo = owner.getSimulationModelInfo();
        langevinSolverResultSet = owner.getLangevinSolverResultSet();

        if(owner != null && owner.getSimulation() != null) {
            int jobs = owner.getSimulation().getSolverTaskDescription().getLangevinSimulationOptions().getTotalNumberOfJobs();
            String name = owner.getSimulation().getName();
            String str = "<html><b>" + name + "</b> [" + jobs + " job" + (jobs != 1 ? "s" : "") + "]";
            getBottomLabel().setText(str);
        } else {
            getBottomLabel().setText(" ");
        }
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
                60   // smaller number means lighter color (more transparent)
        );
    }

    public void setSpecialityRenderer(SpecialtyTableRenderer str) {
//        getMoleculeDataPanel().setSpecialityRenderer(str);
    }


}
