package cbit.vcell.solver.ode.gui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYDifferenceRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import cbit.plot.gui.ClusterPlotPanel;
import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.client.task.ClientTaskDispatcher;
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
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Path2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.List;


public class ClusterVisualizationPanel extends DocumentEditorSubPanel {

    ODEDataViewer owner;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();

    private final Map<String, Color> persistentColorMap = new LinkedHashMap<>();
    private final java.util.List<Color> globalPalette = new ArrayList<>();
    private int nextColorIndex = 0;

    private JPanel ivjJPanel1 = null;
    private JPanel ivjJPanelPlot = null;
    private ClusterPlotPanel clusterPlotPanel = null;   // here are the plots being drawn
    private JLabel ivjJLabelBottom = null;
    private JPanel ivjJPanelData = null;
    private JPanel ivjPlot2DDataPanel1 = null;          // here resides the data table
    private JPanel bottomRightPanel = null;
    private JPanel ivjJPanelLegend = null;
    private JScrollPane ivjPlotLegendsScrollPane = null;
    private JPanel ivjJPanelPlotLegends = null;
    private JLabel bottomLabel = null;
    private JToolBarToggleButton ivjPlotButton = null;
    private JToolBarToggleButton ivjDataButton = null;

    class IvjEventHandler implements ActionListener, PropertyChangeListener, ChangeListener, ListSelectionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof Component c && SwingUtilities.isDescendingFrom(c, ClusterVisualizationPanel.this)) {
                String cmd = e.getActionCommand();
                if (cmd.equals("JPanelPlot") || cmd.equals("JPanelData")) {
                    CardLayout cl = (CardLayout) ivjJPanel1.getLayout();
                    cl.show(ivjJPanel1, cmd);                               // show the plot or the data panel
                    ivjPlotButton.setSelected(cmd.equals("JPanelPlot"));    // update button selection state
                    ivjDataButton.setSelected(cmd.equals("JPanelData"));
                    ivjJPanelLegend.setVisible(cmd.equals("JPanelPlot"));   // show legend only in plot mode
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
                System.out.println("ClusterVisualizationPanel.IvjEventHandler.stateChanged() called");
            }
        }
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() instanceof Component c && SwingUtilities.isDescendingFrom(c, ClusterVisualizationPanel.this)) {
                System.out.println("ClusterVisualizationPanel.IvjEventHandler.valueChanged() called");
            }
        }
    };

    public ClusterVisualizationPanel(ODEDataViewer odeDataViewer) {
        super();
        this.owner = odeDataViewer;
        initialize();
    }

    private void initialize() {
        setPreferredSize(new Dimension(420, 400));
        setLayout(new BorderLayout());
        setSize(513, 457);
        add(getJPanel1(), "Center");
        add(getBottomRightPanel(), "South");
        add(getJPanelLegend(), "East");
        setBackground(Color.white);
        initConnections();
    }

    private JPanel getJPanel1() {
        if (ivjJPanel1 == null) {
            ivjJPanel1 = new JPanel();
            ivjJPanel1.setName("JPanel1");
            ivjJPanel1.setLayout(new CardLayout());
            ivjJPanel1.add(getJPanelPlot(), getJPanelPlot().getName());
            ivjJPanel1.add(getJPanelData(), getJPanelData().getName());
        }
        return ivjJPanel1;
    }
    private JPanel getJPanelPlot() {
        if (ivjJPanelPlot == null) {
            ivjJPanelPlot = new JPanel();
            ivjJPanelPlot.setName("JPanelPlot");
            ivjJPanelPlot.setLayout(new BorderLayout());
            ivjJPanelPlot.add(getClusterPlotPanel(), "Center");
            ivjJPanelPlot.add(getJLabelBottom(), "South");
        }
        return ivjJPanelPlot;
    }
    private JPanel getJPanelData() {
        if (ivjJPanelData == null) {
            ivjJPanelData = new JPanel();
            ivjJPanelData.setName("JPanelData");
            ivjJPanelData.setLayout(new BorderLayout());
            ivjJPanelData.add(getPlot2DDataPanel1(), "Center");
        }
        return ivjJPanelData;
    }

    private JLabel getJLabelBottom() {
        if (ivjJLabelBottom == null) {
            ivjJLabelBottom = new JLabel();
            ivjJLabelBottom.setName("JLabelBottom");
            ivjJLabelBottom.setText("t");
            ivjJLabelBottom.setForeground(Color.black);
            ivjJLabelBottom.setHorizontalTextPosition(SwingConstants.CENTER);
            ivjJLabelBottom.setHorizontalAlignment(SwingConstants.CENTER);
        }
        return ivjJLabelBottom;
    }
    private ClusterPlotPanel getClusterPlotPanel() {      // actual plotting is shown here
        if (clusterPlotPanel == null) {
            try {
                clusterPlotPanel = new ClusterPlotPanel();
                clusterPlotPanel.setName("ClusterPlotPanel");
                clusterPlotPanel.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentShown(ComponentEvent e) {
                        System.out.println("ClusterVisualizationPanel.componentShown() called, height = " + clusterPlotPanel.getHeight());
                    }
                });

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return clusterPlotPanel;
    }
    public JPanel getPlot2DDataPanel1() {               // actual table shown here
        if (ivjPlot2DDataPanel1 == null) {
            try {
                ivjPlot2DDataPanel1 = new JPanel();
                ivjPlot2DDataPanel1.setName("Plot2DDataPanel1");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPlot2DDataPanel1;
    }

    // ---------------------------------------------------------------------
    private JPanel getBottomRightPanel() {
        if (bottomRightPanel == null) {
            bottomRightPanel = new JPanel();
            bottomRightPanel.setName("JPanelBottom");
            bottomRightPanel.setLayout(new GridBagLayout());

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 1; gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(4, 4, 4, 4);
            bottomRightPanel.add(getJBottomLabel(), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 2; gbc.gridy = 0;
            gbc.insets = new Insets(4, 4, 4, 4);
            bottomRightPanel.add(getPlotButton(), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 3; gbc.gridy = 0;
            gbc.insets = new Insets(4, 4, 4, 4);
            bottomRightPanel.add(getDataButton(), gbc);
        }
        return bottomRightPanel;
    }
    private JLabel getJBottomLabel() {
        if (bottomLabel == null) {
            bottomLabel = new JLabel();
            bottomLabel.setName("JBottomLabel");
            bottomLabel.setText(" ");
            bottomLabel.setForeground(Color.blue);
            bottomLabel.setPreferredSize(new Dimension(44, 20));
            bottomLabel.setFont(new Font("dialog", 0, 12));
            bottomLabel.setMinimumSize(new Dimension(44, 20));
        }
        return bottomLabel;
    }
    private JToolBarToggleButton getPlotButton() {
        if (ivjPlotButton == null) {
            ivjPlotButton = new JToolBarToggleButton();
            ivjPlotButton.setName("PlotButton");
            ivjPlotButton.setToolTipText("Show plot(s)");
            ivjPlotButton.setText("");
            ivjPlotButton.setMaximumSize(new Dimension(28, 28));
            ivjPlotButton.setActionCommand("JPanelPlot");
            ivjPlotButton.setSelected(true);
            ivjPlotButton.setPreferredSize(new Dimension(28, 28));
            ivjPlotButton.setIcon(VCellIcons.dataExporterIcon);
            ivjPlotButton.setMinimumSize(new Dimension(28, 28));
        }
        return ivjPlotButton;
    }
    private JToolBarToggleButton getDataButton() {
        if (ivjDataButton == null) {
            ivjDataButton = new JToolBarToggleButton();
            ivjDataButton.setName("DataButton");
            ivjDataButton.setToolTipText("Show data");
            ivjDataButton.setText("");
            ivjDataButton.setMaximumSize(new Dimension(28, 28));
            ivjDataButton.setActionCommand("JPanelData");
            ivjDataButton.setIcon(VCellIcons.dataSetsIcon);
            ivjDataButton.setPreferredSize(new Dimension(28, 28));
            ivjDataButton.setMinimumSize(new Dimension(28, 28));
        }
        return ivjDataButton;
    }

    private JPanel getJPanelLegend() {
        if (ivjJPanelLegend == null) {
            ivjJPanelLegend = new JPanel();
            ivjJPanelLegend.setName("JPanelLegend");
            ivjJPanelLegend.setLayout(new BorderLayout());
            getJPanelLegend().add(new JLabel("        "), "South");
            JLabel labelLegendTitle = new JLabel("Plot Legend:");
            labelLegendTitle.setBorder(new EmptyBorder(10, 4, 10, 4));
            getJPanelLegend().add(labelLegendTitle, "North");
            getJPanelLegend().add(getPlotLegendsScrollPane(), "Center");
        }
        return ivjJPanelLegend;
    }

    private JScrollPane getPlotLegendsScrollPane() {
        if (ivjPlotLegendsScrollPane == null) {
            ivjPlotLegendsScrollPane = new JScrollPane();
            ivjPlotLegendsScrollPane.setName("PlotLegendsScrollPane");
            ivjPlotLegendsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            ivjPlotLegendsScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
            getPlotLegendsScrollPane().setViewportView(getJPanelPlotLegends());
        }
        return ivjPlotLegendsScrollPane;
    }
    private JPanel getJPanelPlotLegends() {
        if (ivjJPanelPlotLegends == null) {
            ivjJPanelPlotLegends = new JPanel();
            ivjJPanelPlotLegends.setName("JPanelPlotLegends");
            ivjJPanelPlotLegends.setLayout(new BoxLayout(ivjJPanelPlotLegends, BoxLayout.Y_AXIS));
            ivjJPanelPlotLegends.setBounds(0, 0, 72, 360);
        }
        return ivjJPanelPlotLegends;
    }

    public void setBackground(Color color) {
        super.setBackground(color);
        getBottomRightPanel().setBackground(color);
        getJBottomLabel().setBackground(color);
        getJPanelLegend().setBackground(color);
        getJPanelPlotLegends().setBackground(color);
        getJPanel1().setBackground(color);
        getClusterPlotPanel().setBackground(color);
        getPlot2DDataPanel1().setBackground(color);
        getJPanelData().setBackground(color);
        getJPanelPlot().setBackground(color);

    }

        private void initConnections() {
        initializeGlobalPalette();      // get a stable, high contrast palette
        // group the two buttons so only one stays selected
        ButtonGroup bg = new ButtonGroup();
        bg.add(getPlotButton());
        bg.add(getDataButton());

        // add the shared handler
        getPlotButton().addActionListener(ivjEventHandler);
        getDataButton().addActionListener(ivjEventHandler);

        // listen to the left panel
        owner.getClusterSpecificationPanel().addPropertyChangeListener(ivjEventHandler);
    }


    private void handleException(java.lang.Throwable exception) {
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {
        System.out.println("ClusterVisualizationPanel.onSelectedObjectsChange() called with " + selectedObjects.length + " objects");
    }

    public void refreshData() {
//        simulationModelInfo = owner.getSimulationModelInfo();
//        langevinSolverResultSet = owner.getLangevinSolverResultSet();
        System.out.println("ClusterVisualizationPanel.refreshData() called");
    }

    // ---------------------------------------------------------------------

    private void initializeGlobalPalette() {
        // Use a curated palette from ColorUtil
        globalPalette.clear();
        globalPalette.addAll(Arrays.asList(ColorUtil.TABLEAU20));
    }
    private void ensureColorsAssigned(List<ColumnDescription> columns) {
        // assign colors only when needed, and keep them consistent across updates
        for (ColumnDescription cd : columns) {
            String name = cd.getName();
            if (!persistentColorMap.containsKey(name)) {
                Color c = globalPalette.get(nextColorIndex % globalPalette.size());
                persistentColorMap.put(name, c);
                nextColorIndex++;
            }
        }
    }
    private JComponent createLegendEntry(String name, Color color, ClusterSpecificationPanel.DisplayMode mode) {
        JPanel p = new JPanel();
        p.setName("JPanelClusterColorLegends");
        BoxLayout bl = new BoxLayout(p, BoxLayout.Y_AXIS);
        p.setLayout(bl);
        p.setBounds(0, 0, 72, 360);
        p.setOpaque(false);

        String unitSymbol = "";
        if(ClusterSpecificationPanel.DisplayMode.COUNTS == mode) {
            unitSymbol = "molecules";
        }
        String shortLabel = "<html>" + name + "<font color=\"#8B0000\">" + " [" + unitSymbol + "] " + "</font></html>";

        JLabel line = new JLabel(new LineIcon(color));
        JLabel text = new JLabel(shortLabel);
        line.setBorder(new EmptyBorder(6,0,1,0));
        text.setBorder(new EmptyBorder(1,8,6,0));
        p.add(line);
        p.add(text);

        return p;
    }
    public class LineIcon implements Icon {
        private final Color color;
        public LineIcon(Color color) {
            this.color = color;
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setStroke(new BasicStroke(3.0f));
            g2.setPaint(color);
            int midY = y + getIconHeight() / 2;
            g2.drawLine(x, midY, x + getIconWidth(), midY);
        }
        @Override
        public int getIconWidth() { return 50; }
        @Override
        public int getIconHeight() {
            return 4;  // more vertical room for a wider stroke
        }
    }

    private void redrawPlot(ClusterSpecificationPanel.ClusterSelection sel) throws ExpressionException {
        System.out.println("ClusterVisualizationPanel.redrawPlot() called, current selection: " + sel);
        if (sel != null) {
            System.out.println("ClusterVisualizationPanel.redrawPlot() mode: " + sel.mode + ", columns: " + sel.columns.size() + ", resultSet: " + (sel.resultSet != null ? "present" : "null"));
        } else {
            System.out.println("ClusterVisualizationPanel.redrawPlot() selection is null");
        }
        System.out.println("ClusterVisualizationPanel.redrawPlot(), height = " + getClusterPlotPanel().getHeight());
//        currentSelection = sel;
        List<ColumnDescription> columns = sel.columns;
        ODESolverResultSet srs = sel.resultSet;

        int indexTime = srs.findColumn("t");
        double[] times = srs.extractColumn(indexTime);

//        double globalMin = Double.POSITIVE_INFINITY;
        double globalMin = 0;       // these are counts, so min is always 0
        double globalMax = Double.NEGATIVE_INFINITY;
        clusterPlotPanel.clear();

        for (ColumnDescription cd : columns) {
            int index = srs.findColumn(cd.getName());
            double[] y = srs.extractColumn(index);
            for (double v : y) {
//                if (v < globalMin) globalMin = v;
                if (v > globalMax) globalMax = v;
            }
            Color c = persistentColorMap.get(cd.getName());
            clusterPlotPanel.addCurve(cd.getName(), y, c);
        }
        clusterPlotPanel.setGlobalMinMax(globalMin, globalMax);
        clusterPlotPanel.setDt(times[1]);   // times[0] == 0;
        clusterPlotPanel.repaint();
    }
    private void redrawLegend(ClusterSpecificationPanel.ClusterSelection sel) {
        System.out.println("ClusterVisualizationPanel.redrawLegend() called");
        getJPanelPlotLegends().removeAll();
        for (ColumnDescription cd : sel.columns) {
            Color c = persistentColorMap.get(cd.getName());
            getJPanelPlotLegends().add(createLegendEntry(cd.getName(), c, sel.mode));
        }
        getJPanelPlotLegends().revalidate();
        getJPanelPlotLegends().repaint();
    }
    private void redrawDataTable(ClusterSpecificationPanel.ClusterSelection sel) throws ExpressionException {
        System.out.println("ClusterVisualizationPanel.updateDataTable() called");

        JPanel container = getPlot2DDataPanel1();
        container.removeAll();
        container.setLayout(new BorderLayout());

        if (sel == null || sel.resultSet == null || sel.columns == null || sel.columns.isEmpty()) {
            container.add(new JLabel("No data to display"), BorderLayout.CENTER);
            container.revalidate();
            container.repaint();
            return;
        }

        ODESolverResultSet srs = sel.resultSet;
        java.util.List<ColumnDescription> columns = sel.columns;

        // time column
        int timeIndex = srs.findColumn("t");
        double[] times = srs.extractColumn(timeIndex);
        int rowCount = times.length;

        // column names: t + one per selected column
        String[] columnNames = new String[1 + columns.size()];
        columnNames[0] = "t";
        for (int i = 0; i < columns.size(); i++) {
            ColumnDescription cd = columns.get(i);
            // you can decorate with mode if you want, e.g. "[COUNTS] name"
            columnNames[i + 1] = cd.getName();
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

        JTable table = new JTable(data, columnNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);

        autoSizeTableColumns(table);    // resize to make it look better

        JScrollPane scrollPane = new JScrollPane(table);
        container.add(scrollPane, BorderLayout.CENTER);

        container.revalidate();
        container.repaint();

    }
    public void setSpecialityRenderer(SpecialtyTableRenderer str) {
        // TODO: implement this
//        getPlot2DDataPanel1().setSpecialityRenderer(str);
    }

    private void autoSizeTableColumns(JTable table) {
        final int margin = 14;  // some breathing room

        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = table.getColumnModel().getColumn(col);

            int maxWidth = 0;

            // header width
            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                    table, column.getHeaderValue(), false, false, 0, col);
            maxWidth = Math.max(maxWidth, headerComp.getPreferredSize().width);

            // cell widths
            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, col);
                Component comp = table.prepareRenderer(cellRenderer, row, col);
                maxWidth = Math.max(maxWidth, comp.getPreferredSize().width);
            }

            column.setPreferredWidth(maxWidth + margin);
        }
    }


    // =================================================== Evaluate JFreeChart capabilities with a simple demo ===

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
