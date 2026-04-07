package cbit.vcell.solver.ode.gui;

import cbit.plot.gui.ClusterDataPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cbit.plot.gui.ClusterPlotPanel;
import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.vcell.util.ColorUtil;
import org.vcell.util.gui.JToolBarToggleButton;
import org.vcell.util.gui.SpecialtyTableRenderer;
import org.vcell.util.gui.VCellIcons;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Path2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.List;


public class ClusterVisualizationPanel extends AbstractVisualizationPanel {

    ODEDataViewer owner;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();

    private final Map<String, Color> persistentColorMap = new LinkedHashMap<>();
    private final java.util.List<Color> globalPalette = new ArrayList<>();
    private int nextColorIndex = 0;

    private ClusterPlotPanel clusterPlotPanel = null;   // here are the plots being drawn
    private ClusterDataPanel clusterDataPanel = null;   // here resides the data table

    class IvjEventHandler implements ActionListener, PropertyChangeListener, ChangeListener, ListSelectionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof Component c && SwingUtilities.isDescendingFrom(c, ClusterVisualizationPanel.this)) {
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
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() == owner.getClusterSpecificationPanel() && "ClusterSelection".equals(evt.getPropertyName())) {
                ClusterSpecificationPanel.ClusterSelection sel = (ClusterSpecificationPanel.ClusterSelection) evt.getNewValue();
                ensureColorsAssigned(sel.columns);
                try {
                    redrawLegend(sel);      // redraw legend (one plot, multiple curves)
                    redrawPlot(sel);        // redraw plot (one plot, multiple curves)
                    redrawDataTable(sel);   // redraw data table
                } catch (ExpressionException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
        }
        @Override
        public void stateChanged(ChangeEvent e) {
            if (e.getSource() instanceof Component c && SwingUtilities.isDescendingFrom(c, ClusterVisualizationPanel.this)) {
                System.out.println(this.getClass().getName() + ".stateChanged() called");
            }
        }
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() instanceof Component c && SwingUtilities.isDescendingFrom(c, ClusterVisualizationPanel.this)) {
                System.out.println(this.getClass().getName() + ".valueChanged() called");
            }
        }
    };

    public ClusterVisualizationPanel(ODEDataViewer odeDataViewer) {
        super();
        this.owner = odeDataViewer;
        initialize();
        initConnections();
        setVisualizationBackground(Color.WHITE);
    }

    // --------------------the abstract class hooks
    @Override
    protected JPanel createPlotPanel() {
        return getClusterPlotPanel();
    }
    @Override
    protected JPanel createDataPanel() {
        return getClusterDataPanel();
    }
    @Override
    protected void setCrosshairEnabled(boolean enabled) {
        getClusterPlotPanel().setCrosshairEnabled(enabled);
    }

    private ClusterPlotPanel getClusterPlotPanel() {      // actual plotting is shown here
        if (clusterPlotPanel == null) {
            try {
                clusterPlotPanel = new ClusterPlotPanel();
                clusterPlotPanel.setName("ClusterPlotPanel");
                clusterPlotPanel.setCoordinateCallback(coords -> {
                    if (coords == null) {
                        clearCrosshairCoordinates();
                    } else {
                        updateCrosshairCoordinates(coords[0], coords[1]);
                    }
                });
                clusterPlotPanel.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentShown(ComponentEvent e) {
                        System.out.println(this.getClass().getSimpleName() + ".componentShown() called, height = " + clusterPlotPanel.getHeight());
                    }
                });
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return clusterPlotPanel;
    }
    public ClusterDataPanel getClusterDataPanel() {               // actual table shown here
        if (clusterDataPanel == null) {
            try {
                clusterDataPanel = new ClusterDataPanel();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return clusterDataPanel;
    }


    public void setVisualizationBackground(Color color) {
        super.setVisualizationBackground(color);
        getClusterPlotPanel().setBackground(color);
        getClusterDataPanel().setBackground(color);
    }

    @Override
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
        owner.getClusterSpecificationPanel().addPropertyChangeListener(ivjEventHandler);
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
        if(owner != null && owner.getSimulation() != null) {
            int jobs = owner.getSimulation().getSolverTaskDescription().getLangevinSimulationOptions().getTotalNumberOfJobs();
            String name = owner.getSimulation().getName();
            String str = "<html><b>" + name + "</b> [" + jobs + " job" + (jobs != 1 ? "s" : "") + "]";
            getBottomLabel().setText(str);
        } else {
            getBottomLabel().setText(" ");
        }
//        simulationModelInfo = owner.getSimulationModelInfo();
//        langevinSolverResultSet = owner.getLangevinSolverResultSet();
        System.out.println(this.getClass().getSimpleName() + ".refreshData() called");
    }

    // ---------------------------------------------------------------------

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
    /*
    ACS = Average Cluster Size
    ACS answers: "If I pick a cluster at random, how many molecules does it contain on average?"
    ACS is the mean size of clusters, computed as:  ACS = Math.sumOver(i, n_i * size_i) / Math.sumOver(i, n_i);
    Unit: molecules per cluster (molecules)

    SD = Standard Deviation of Cluster Size
    SD answers: "How much variability is there in cluster sizes?"
    SD = Math.sqrt( Math.sumOver(i, n_i * Math.pow(size_i - ACS, 2)) / Math.sumOver(i, n_i) )
    Unit: molecules per cluster (molecules)

    ACO = Average Cluster Occupancy
    ACO answers: "If I pick a random molecule from the entire system, how many other molecules are in its cluster on average?"
    ACO = Math.sumOver(i, n_i * size_i * size_i) / Math.sumOver(i, n_i * size_i)
    Unit: molecules per molecule (molecules)

    Example: if clusters are:
        - {5, 5, 5, 5} -> ACS = 5, SD = 0
        - {3, 4, 5, 6} -> ACS = 4.5, SD ≈ 1.12
        - {1, 1, 1, 10} -> ACS = 3.25, SD is large
    SD tells whether the system is:
        - homogeneous (low SD)
        - heterogeneous (high SD)
    ACS alone cannot tell that - SD is the variability measure
     */
    private JComponent createLegendEntry(String name, Color color, ClusterSpecificationPanel.DisplayMode mode) {
        JPanel p = new JPanel();
        p.setName("JPanelClusterColorLegends");
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        String unitSymbol;
        String tooltip;
        switch (mode) {
            case COUNTS:
                // name is the cluster size (e.g., "3")
                unitSymbol = "molecules";
                tooltip = "<html>cluster size <b>" + name + "</b> molecules</html>";
                break;
            case MEAN:
            case OVERALL:
                // name is ACS / SD / ACO
                ClusterSpecificationPanel.ClusterStatistic stat = ClusterSpecificationPanel.ClusterStatistic.valueOf(name);
                unitSymbol = stat.unit();
                tooltip = "<html>" + stat.fullName() + "<br>" + stat.description() + "</html>";
                break;
            default:
                unitSymbol = "";
                tooltip = null;
        }

        // Visible label
        String shortLabel = "<html>" + name + "<font color=\"#8B0000\">" + " [" + unitSymbol + "] " + "</font></html>";
        JLabel line = new JLabel(new LineIcon(color));
        JLabel text = new JLabel(shortLabel);
        line.setBorder(new EmptyBorder(6, 0, 1, 0));
        text.setBorder(new EmptyBorder(1, 8, 6, 0));
        line.setToolTipText(tooltip);
        text.setToolTipText(tooltip);
        p.setToolTipText(tooltip);
        p.add(line);
        p.add(text);
        return p;
    }

    private void redrawPlot(ClusterSpecificationPanel.ClusterSelection sel) throws ExpressionException {
        System.out.println(getClass().getSimpleName() + ".redrawPlot() called, current selection: " + sel);

        ClusterPlotPanel plot = getClusterPlotPanel();

        // ---------------------------------------------------------------------
        // NULL CASE
        // ---------------------------------------------------------------------
        if (sel == null || sel.resultSet == null) {
            plot.clear();
            plot.repaint();
            return;
        }

        List<ColumnDescription> columns = sel.columns;
        ODESolverResultSet srs = sel.resultSet;

        int indexTime = srs.findColumn("t");
        double[] times = srs.extractColumn(indexTime);

        // ---------------------------------------------------------------------
        // FIRST PASS: load all selected columns normally (except SD curve)
        // ---------------------------------------------------------------------
        Map<String, double[]> yMap = new LinkedHashMap<>();

        double globalMin = 0.0;                       // old behavior baseline
        double globalMax = Double.NEGATIVE_INFINITY;

        for (ColumnDescription cd : columns) {
            String name = cd.getName();

            int idx = srs.findColumn(name);
            if (idx < 0) continue;

            double[] y = srs.extractColumn(idx);
            yMap.put(name, y);

            // SD does not draw a line, but its raw values still matter for globalMax
            if (!name.equals("SD")) {
                for (double v : y) {
                    if (v > globalMax) globalMax = v;
                }
            }
        }

        // ---------------------------------------------------------------------
        // SECOND PASS: ACS/SD special logic
        // ---------------------------------------------------------------------
        boolean acsSelected = yMap.containsKey("ACS");
        boolean sdSelected  = yMap.containsKey("SD");

        double[] acs = null;
        double[] sd  = null;

        int idxACS = srs.findColumn("ACS");
        int idxSD  = srs.findColumn("SD");

        // We need ACS if either ACS or SD is selected
        if (idxACS >= 0) {
            acs = (acsSelected ? yMap.get("ACS") : srs.extractColumn(idxACS));
        }

        // We need SD if either ACS or SD is selected
        if (idxSD >= 0) {
            sd = (sdSelected ? yMap.get("SD") : srs.extractColumn(idxSD));
        }

        // If either ACS or SD is selected, scale using ACS±SD
        if ((acsSelected || sdSelected) && acs != null && sd != null) {
            for (int i = 0; i < acs.length; i++) {
                double upper = acs[i] + sd[i];
                double lower = acs[i] - sd[i];

                if (upper > globalMax) globalMax = upper;
                if (lower < globalMin) globalMin = lower;
            }
        }

        // ---------------------------------------------------------------------
        // DRAWING
        // ---------------------------------------------------------------------
        plot.clear();

        // Draw all selected curves EXCEPT SD (SD is envelope only)
        for (ColumnDescription cd : columns) {
            String name = cd.getName();
            if (name.equals("SD")) continue;

            double[] y = yMap.get(name);
            if (y == null) continue;

            Color c = persistentColorMap.get(name);

            // Cluster curves are AVG curves in the new API
            plot.addAvgRenderer(times, y, c, name, /*statTag*/ "AVG");
        }

        // Draw SD envelope if SD is selected
        if (sdSelected && acs != null && sd != null) {
            int n = acs.length;
            double[] upper = new double[n];
            double[] lower = new double[n];

            for (int i = 0; i < n; i++) {
                upper[i] = acs[i] + sd[i];
                lower[i] = acs[i] - sd[i];
            }

            Color sdColor = persistentColorMap.get("SD");

            // SD is a band renderer in the new API
            plot.addSDRenderer(times, lower, upper, sdColor, "SD", /*statTag*/ "SD");
        }

        // ---------------------------------------------------------------------
        // FINALIZE
        // ---------------------------------------------------------------------
        if (globalMin > 0) globalMin = 0;

        plot.setGlobalMinMax(globalMin, globalMax);

        if (times.length > 1) {
            plot.setDt(times[1]);   // times[0] == 0
        }

        plot.repaint();
    }


    private void redrawLegend(ClusterSpecificationPanel.ClusterSelection sel) {
        System.out.println(this.getClass().getSimpleName() + ".redrawLegend() called");
        getLegendContentPanel().removeAll();

        for (ColumnDescription cd : sel.columns) {
            String name = cd.getName();

            Color c;
            if (name.equals("SD")) {
                // SD uses a translucent version of ACS color
                Color cACS = persistentColorMap.get("ACS");
                c = new Color(cACS.getRed(), cACS.getGreen(), cACS.getBlue(), 80);   // alpha 0–255 -> ~30% opacity
            } else {
                // ACS, ACO, COUNTS all use their assigned colors
                c = persistentColorMap.get(name);
            }

            getLegendContentPanel().add(createLegendEntry(name, c, sel.mode));
        }
        getLegendContentPanel().revalidate();
        getLegendContentPanel().repaint();
    }

    private void redrawDataTable(ClusterSpecificationPanel.ClusterSelection sel) throws ExpressionException {
        System.out.println(this.getClass().getSimpleName() + ".updateDataTable() called");
        getClusterDataPanel().updateData(sel);
    }
    public void setSpecialityRenderer(SpecialtyTableRenderer str) {
        getClusterDataPanel().setSpecialityRenderer(str);
    }

    private boolean contains(List<ColumnDescription> list, String name) {
        for (ColumnDescription cd : list) {
            if (cd.getName().equals(name)) return true;
        }
        return false;
    }

// -----------------------------------------------------------------------------------------------------------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            Random rand = new Random();

            int n = 50;
            double xMin = 0.0;
            double xMax = 7.0;
            double dx = (xMax - xMin) / (n - 1);

            double xLower = 0.0;    // these are the limits for the x values of the axis
            double xUpper = 7.5;

            Color sinColor = new Color(31, 119, 180);   // blue
            Color tanColor = new Color(255, 127, 14);   // orange

            // --- SIN series ---
            XYSeries sinMin = new XYSeries("sin-min");
            XYSeries sinMax = new XYSeries("sin-max");
            XYSeries sinMain = new XYSeries("sin");
            XYSeries sinStd = new XYSeries("sin-std");

            for (int i = 0; i < n; i++) {
                double x = xMin + i * dx;
                double y = Math.sin(x);

                double delta = 0.2 + rand.nextDouble() * 0.2;
                double yMin = y - delta;
                double yMax = y + delta;

                double std = 0.08 + rand.nextDouble() * 0.08;

                sinMin.add(x, yMin);
                sinMax.add(x, yMax);
                sinMain.add(x, y);
                sinStd.add(x, y + std);
            }

            // --- TAN series ---
            XYSeries tanMin = new XYSeries("tan-min");
            XYSeries tanMax = new XYSeries("tan-max");
            XYSeries tanMain = new XYSeries("tan");
            XYSeries tanStd = new XYSeries("tan-std");

            for (int i = 0; i < n; i++) {
                double x = xMin + i * dx;
                double y = Math.tan(x);

                if (y > 3) y = 3;
                if (y < -3) y = -3;

                double delta = 0.3 + rand.nextDouble() * 0.3;
                double yMin = y - delta;
                double yMax = y + delta;

                double std = 0.2 + rand.nextDouble() * 0.2;

                tanMin.add(x, yMin);
                tanMax.add(x, yMax);
                tanMain.add(x, y);
                tanStd.add(x, y + std);
            }

            double globalMin = Double.POSITIVE_INFINITY;
            double globalMax = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < n; i++) {
                globalMin = Math.min(globalMin, sinMin.getY(i).doubleValue());
                globalMin = Math.min(globalMin, tanMin.getY(i).doubleValue());
                globalMax = Math.max(globalMax, sinMax.getY(i).doubleValue());
                globalMax = Math.max(globalMax, tanMax.getY(i).doubleValue());
            }
            // Add padding
            double pad = 0.1 * (globalMax - globalMin);
            globalMin -= pad;
            globalMax += pad;

            // --- Datasets ---

            // Dataset 0: sin min/max (for band)
            XYSeriesCollection sinMinMaxDataset = new XYSeriesCollection();
            sinMinMaxDataset.addSeries(sinMax); // upper
            sinMinMaxDataset.addSeries(sinMin); // lower

            // Dataset 1: tan min/max (for band)
            XYSeriesCollection tanMinMaxDataset = new XYSeriesCollection();
            tanMinMaxDataset.addSeries(tanMax); // upper
            tanMinMaxDataset.addSeries(tanMin); // lower

            // Dataset 2: main curves
            XYSeriesCollection mainDataset = new XYSeriesCollection();
            mainDataset.addSeries(sinMain);
            mainDataset.addSeries(tanMain);

            // Dataset 3: std diamonds
            XYSeriesCollection stdDataset = new XYSeriesCollection();
            stdDataset.addSeries(sinStd);
            stdDataset.addSeries(tanStd);

            // --- Chart skeleton ---
            JFreeChart chart = ChartFactory.createXYLineChart(
                    "Min/Max Bands + STD Demo",
                    "x",
                    "y",
                    null,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false
            );
            XYPlot plot = chart.getXYPlot();

            // Transparent backgrounds
            chart.setBackgroundPaint(Color.WHITE);
            plot.setBackgroundPaint(Color.WHITE);
            plot.setOutlinePaint(null);
            plot.setDomainGridlinePaint(new Color(180, 180, 180));  // very light
            plot.setRangeGridlinePaint(new Color(180, 180, 180));

            plot.getDomainAxis().setAutoRange(false);   // lock the axis so that they never resize
            plot.getRangeAxis().setAutoRange(false);
            plot.getDomainAxis().setRange(xLower, xUpper);
            plot.getRangeAxis().setRange(globalMin, globalMax);

            // --- Legend to the right
            chart.getLegend().setPosition(RectangleEdge.RIGHT);
            chart.getLegend().setBackgroundPaint(Color.WHITE);
//            chart.getLegend().setFrame(BlockBorder.NONE);

            // --- Renderer 0: sin band ---
            XYDifferenceRenderer sinBandRenderer = new XYDifferenceRenderer();
            Color sinBandColor = new Color(sinColor.getRed(), sinColor.getGreen(), sinColor.getBlue(), 40);
            sinBandRenderer.setPositivePaint(sinBandColor);
            sinBandRenderer.setNegativePaint(sinBandColor);
            sinBandRenderer.setSeriesStroke(0, new BasicStroke(0f));
            sinBandRenderer.setSeriesStroke(1, new BasicStroke(0f));
//            sinBandRenderer.setOutlinePaint(null);
            sinBandRenderer.setSeriesVisibleInLegend(0, false);     // hide sin-max legend entries
            sinBandRenderer.setSeriesVisibleInLegend(1, false);

            plot.setDataset(0, sinMinMaxDataset);
            plot.setRenderer(0, sinBandRenderer);

            // --- Renderer 1: tan band ---
            XYDifferenceRenderer tanBandRenderer = new XYDifferenceRenderer();
            Color tanBandColor = new Color(tanColor.getRed(), tanColor.getGreen(), tanColor.getBlue(), 40);
            tanBandRenderer.setPositivePaint(tanBandColor);
            tanBandRenderer.setNegativePaint(tanBandColor);
            tanBandRenderer.setSeriesStroke(0, new BasicStroke(0f));
            tanBandRenderer.setSeriesStroke(1, new BasicStroke(0f));
//            tanBandRenderer.setOutlinePaint(null);
            tanBandRenderer.setSeriesVisibleInLegend(0, false);
            tanBandRenderer.setSeriesVisibleInLegend(1, false);

            plot.setDataset(1, tanMinMaxDataset);
            plot.setRenderer(1, tanBandRenderer);

            // --- Renderer 2: main curves ---
            XYLineAndShapeRenderer lineRenderer = new XYLineAndShapeRenderer(true, false);
            lineRenderer.setSeriesPaint(0, sinColor);
            lineRenderer.setSeriesPaint(1, tanColor);
            lineRenderer.setSeriesStroke(0, new BasicStroke(2f));
            lineRenderer.setSeriesStroke(1, new BasicStroke(2f));
            lineRenderer.setSeriesVisibleInLegend(0, true);       // keep the main curves visible in Legend
            lineRenderer.setSeriesVisibleInLegend(1, true);

            plot.setDataset(2, mainDataset);
            plot.setRenderer(2, lineRenderer);

            // --- Renderer 3: STD diamonds ---
            XYLineAndShapeRenderer stdRenderer = new XYLineAndShapeRenderer(false, true);
            Shape diamond = createDiamondShape(4);

            stdRenderer.setSeriesShape(0, diamond);
            stdRenderer.setSeriesShape(1, diamond);
            stdRenderer.setSeriesPaint(0, sinColor.darker());
            stdRenderer.setSeriesPaint(1, tanColor.darker());
            stdRenderer.setSeriesVisibleInLegend(0, false);
            stdRenderer.setSeriesVisibleInLegend(1, false);

            plot.setDataset(3, stdDataset);
            plot.setRenderer(3, stdRenderer);

            // --- ChartPanel ---
            ChartPanel panel = new ChartPanel(chart);
            panel.setOpaque(true);      // must be opaque for white to show
            panel.setBackground(Color.WHITE);

            // --- checkboxes to control display
            JPanel controls = new JPanel();
            JCheckBox cbAvg = new JCheckBox("Averages", true);
            JCheckBox cbMinMax = new JCheckBox("Min-Max", false);
            JCheckBox cbStd = new JCheckBox("STD", false);
            controls.add(cbAvg);
            controls.add(cbMinMax);
            controls.add(cbStd);

            // Averages (dataset 2)
            cbAvg.addActionListener(e -> {
                boolean on = cbAvg.isSelected();
                plot.getRenderer(2).setSeriesVisible(0, on);
                plot.getRenderer(2).setSeriesVisible(1, on);
                lineRenderer.setSeriesVisibleInLegend(0, true);     // force legend visible
                lineRenderer.setSeriesVisibleInLegend(1, true);
            });

            // Min-Max (datasets 0 and 1)
            cbMinMax.addActionListener(e -> {
                boolean on = cbMinMax.isSelected();
                plot.setRenderer(0, on ? sinBandRenderer : null);
                plot.setRenderer(1, on ? tanBandRenderer : null);
            });

            // STD (dataset 3)
            cbStd.addActionListener(e -> {
                boolean on = cbStd.isSelected();
                stdRenderer.setSeriesVisible(0, on);
                stdRenderer.setSeriesVisible(1, on);
            });

            // --- Frame ---
            JFrame frame = new JFrame("JFreeChart Min/Max/STD Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel, BorderLayout.CENTER);
            frame.add(controls, BorderLayout.SOUTH);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static Shape createDiamondShape(int size) {
        Path2D.Double p = new Path2D.Double();
        p.moveTo(0, -size);
        p.lineTo(size, 0);
        p.lineTo(0, size);
        p.lineTo(-size, 0);
        p.closePath();
        return p;
    }

}
