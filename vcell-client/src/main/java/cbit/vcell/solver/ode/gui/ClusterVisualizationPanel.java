package cbit.vcell.solver.ode.gui;

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

    private ClusterSpecificationPanel.ClusterSelection currentSelection = null;

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
                    redrawPlot(sel);        // redraw plot (one plot, multiple curves)
                } catch (ExpressionException e) {
                    throw new RuntimeException(e);
                }
                redrawLegend(sel);      // update legend (one plot, multiple curves)
                updateDataTable(sel);   // update data table
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
    private ClusterPlotPanel getClusterPlotPanel() {      // actual plotting is done here
        if (clusterPlotPanel == null) {
            try {
                clusterPlotPanel = new ClusterPlotPanel();
                clusterPlotPanel.setName("ClusterPlotPanel");
                clusterPlotPanel.addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentShown(ComponentEvent e) {
                        System.out.println("ClusterVisualizationPanel.componentShown() called, height = " + clusterPlotPanel.getHeight());
                        // Only redraw when the panel is actually visible and sized
                        if (currentSelection != null) {
                            try {
                                System.out.println("ClusterVisualizationPanel.componentShown() calling redrawPlot() with current selection: " + currentSelection);
                                redrawPlot(currentSelection);
                            } catch (ExpressionException ex) {
                                ex.printStackTrace();
                            }
                        } else {
                            System.out.println("ClusterVisualizationPanel.componentShown() no current selection, skipping redraw");
                        }
                    }
                });

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return clusterPlotPanel;
    }
    public JPanel getPlot2DDataPanel1() {
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
        currentSelection = sel;
        java.util.List<ColumnDescription> columnDescriptions = sel.columns;
        for(ColumnDescription cd : columnDescriptions) {
            System.out.println("  column name: '" + cd.getName() + "'");
        }
        Hashtable<String, Object> hashTable = new Hashtable<>();
        hashTable.put("columns", sel.columns);      // selected columns
        hashTable.put("resultSet", sel.resultSet);
        hashTable.put("persistentColorMap", persistentColorMap);
        hashTable.put("plotPanel", getClusterPlotPanel());
        hashTable.put("panelHeight", getPlot2DDataPanel1().getHeight());
        hashTable.put("panelWidth", getPlot2DDataPanel1().getWidth());

        AsynchClientTask computeScaledCurvesTask = new AsynchClientTask("Computing scaled curves...",
                        AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
            @Override
            public void run(Hashtable<String, Object> hashTable) throws Exception {

                List<ColumnDescription> columns = (List<ColumnDescription>) hashTable.get("columns");
                ODESolverResultSet srs = (ODESolverResultSet) hashTable.get("resultSet");
                int panelWidth = (Integer) hashTable.get("panelWidth");

                double globalMin = Double.POSITIVE_INFINITY;    // compute global min/max
                double globalMax = Double.NEGATIVE_INFINITY;
                Map<String, double[]> rawCurves = new LinkedHashMap<>();

                for (ColumnDescription cd : columns) {
                    int index = srs.findColumn(cd.getName());
                    double[] y = srs.extractColumn(index);
                    rawCurves.put(cd.getName(), y);

                    for (double v : y) {
                        if (v < globalMin) globalMin = v;
                        if (v > globalMax) globalMax = v;
                    }
                }
                hashTable.put("globalMin", globalMin);
                hashTable.put("globalMax", globalMax);

                int panelHeight = (Integer) hashTable.get("panelHeight");   // scale curves into panel coordinates
                Map<String, int[]> scaledCurves = new LinkedHashMap<>();
                double range = globalMax - globalMin;
                if (range == 0) range = 1; // avoid divide-by-zero

                for (Map.Entry<String, double[]> entry : rawCurves.entrySet()) {
                    String name = entry.getKey();
                    double[] y = entry.getValue();
                    int[] scaled = new int[y.length];       // this is in pixels, which are int
                    for (int i = 0; i < y.length; i++) {
                        double norm = (y[i] - globalMin) / range;
                        scaled[i] = panelHeight - (int) Math.round(norm * panelHeight);
                    }
                    scaledCurves.put(name, scaled);
                }
                hashTable.put("scaledCurves", scaledCurves);
            }
        };
        AsynchClientTask drawCurvesTask = new AsynchClientTask("Drawing curves...",
                        AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
            @Override
            public void run(Hashtable<String, Object> hashTable) throws Exception {

                Map<String, int[]> scaledCurves = (Map<String, int[]>) hashTable.get("scaledCurves");
                Map<String, Color> colorMap = (Map<String, Color>) hashTable.get("persistentColorMap");
                ClusterPlotPanel clusterPlotPanel = (ClusterPlotPanel) hashTable.get("plotPanel");

                clusterPlotPanel.clear();
                for (String name : scaledCurves.keySet()) {
                    int[] yScaled = scaledCurves.get(name);
                    Color c = colorMap.get(name);
                    clusterPlotPanel.addCurve(name, yScaled, c);
                }
                clusterPlotPanel.repaint();
            }
        };
        AsynchClientTask[] tasks = new AsynchClientTask[] { computeScaledCurvesTask, drawCurvesTask };
        ClientTaskDispatcher.dispatch(this, hashTable, tasks, false);


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
    private void updateDataTable(ClusterSpecificationPanel.ClusterSelection sel) {
        System.out.println("ClusterVisualizationPanel.updateDataTable() called");
    }


}
