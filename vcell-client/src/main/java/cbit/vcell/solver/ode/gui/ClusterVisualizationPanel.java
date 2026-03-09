package cbit.vcell.solver.ode.gui;

import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.parser.ExpressionException;
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
    private JPanel ivjPlot2DPanel1 = null;             // here
    private JLabel ivjJLabelBottom = null;
    private JPanel ivjJPanelData = null;
    private JPanel ivjPlot2DDataPanel1 = null;     // here
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
            ivjJPanelPlot.add(getPlot2DPanel1(), "Center");
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
    private JPanel getPlot2DPanel1() {
        if (ivjPlot2DPanel1 == null) {
            try {
                ivjPlot2DPanel1 = new JPanel();
                ivjPlot2DPanel1.setName("Plot2DPanel1");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPlot2DPanel1;
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
        getPlot2DPanel1().setBackground(color);
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
    public static String getUnitSymbol(ClusterSpecificationPanel.ClusterSelection sel) {
        if(ClusterSpecificationPanel.DisplayMode.COUNTS == sel.mode) {


        }
        return "";
    }

    private void redrawPlot(ClusterSpecificationPanel.ClusterSelection sel) throws ExpressionException {
        System.out.println("ClusterVisualizationPanel.redrawPlot() called");
//        java.util.List<ColumnDescription> columnDescriptions = sel.columns;
//        for(ColumnDescription cd : columnDescriptions) {
//            System.out.println("  column name: '" + cd.getName() + "'");
//        }
//        getPlot2DPanel1().removeAllPlots();
//
//        int index = sel.resultSet.findColumn("t");
//        double[] t = sel.resultSet.extractColumn(index);
//
//        for (ColumnDescription cd : sel.columns) {
//            index = sel.resultSet.findColumn(cd.getName());
//            double[] y = sel.resultSet.extractColumn(index);
//            Color c = persistentColorMap.get(cd.getName());
//            getPlot2DPanel1().addLinePlot(cd.getName(), c, t, y);
//        }
//        getPlot2DPanel1().repaint();
    }
    private void redrawLegend(ClusterSpecificationPanel.ClusterSelection sel) {
        System.out.println("ClusterVisualizationPanel.updateLegend() called");
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
