package cbit.vcell.solver.ode.gui;

import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.simdata.LangevinSolverResultSet;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.gui.CollapsiblePanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.Map;

public class ClusterSpecificationPanel extends AbstractSpecificationPanel {

    private static final Logger lg = LogManager.getLogger(ClusterSpecificationPanel.class);

    public enum DisplayMode {
        COUNTS(
                "COUNTS",
                "Cluster Counts",
                "Show the number of clusters of each size"
        ),
        MASS(   // molecule-weighted cluster count (cluster mass distribution)
                "MASS",
                "Cluster Mass",
                "Show the number of molecules contained in clusters of each size (count × size)"
        ),
        MEAN(
                "MEAN",
                "Cluster Mean",
                "At each timepoint: ACS/ACO averaged over runs; SD = SD of ACS across runs"
        ),
        OVERALL(
                "OVERALL",
                "Cluster Overall",
                "At each timepoint: all runs pooled; ACS, SD, ACO computed from the combined sample"
        );
        private final String actionCommand;
        private final String uiLabel;
        private final String tooltip;
        DisplayMode(String actionCommand, String uiLabel, String tooltip) {
            this.actionCommand = actionCommand;
            this.uiLabel = uiLabel;
            this.tooltip = tooltip;
        }
        public String actionCommand() { return actionCommand; }
        public String uiLabel()       { return uiLabel; }
        public String tooltip()       { return tooltip; }
        public static DisplayMode fromActionCommand(String cmd) {
            for (DisplayMode m : values()) {
                if (m.actionCommand.equals(cmd)) {
                    return m;
                }
            }
            throw new IllegalArgumentException("Unknown DisplayMode: " + cmd);
        }
        public static DisplayMode fromActionCommandOrNull(String cmd) {
            try {
                return fromActionCommand(cmd);
            } catch (Exception ex) {
                return null;
            }
        }
    }
    public enum PlotStyle {
        LINE(
                "LINE",
                "Line Plot",
                "Show each cluster size as a line over time"
        ),
        BUBBLE(
                "BUBBLE",
                "Bubble Plot",
                "Show a bubble at each timepoint; bubble size ∝ cluster count or mass"
        );
        private final String actionCommand;
        private final String uiLabel;
        private final String tooltip;
        PlotStyle(String actionCommand, String uiLabel, String tooltip) {
            this.actionCommand = actionCommand;
            this.uiLabel = uiLabel;
            this.tooltip = tooltip;
        }
        public String actionCommand() { return actionCommand; }
        public String uiLabel()       { return uiLabel; }
        public String tooltip()       { return tooltip; }
        public static PlotStyle fromActionCommand(String cmd) {
            for (PlotStyle v : values()) {
                if (v.actionCommand.equals(cmd)) {
                    return v;
                }
            }
            throw new IllegalArgumentException("Unknown DistributionView: " + cmd);
        }
        public static PlotStyle fromActionCommandOrNull(String cmd) {
            try {
                return fromActionCommand(cmd);
            } catch (Exception ex) {
                return null;
            }
        }

    }

    public enum ClusterStatistic {
        ACS(
                "Avg. Cluster Size",
                "Average number of molecules per cluster",
                "molecules"
        ),
        ACO(
                "Avg. Cluster Occupancy",
                "Average size of the cluster that a molecule belongs to (molecule‑centric cluster size)",
                "molecules"
        ),
        SD(
                "SD of Cluster Size",
                "Variability of cluster sizes around the average cluster size (ACS)",
                "molecules"
        );
        private final String fullName;
        private final String description;
        private final String unit;
        ClusterStatistic(String fullName, String description, String unit) {
            this.fullName = fullName;
            this.description = description;
            this.unit = unit;
        }
        public String fullName() {
            return fullName;
        }
        public String description() {
            return description;
        }
        public String unit() {
            return unit;
        }
        // like valueOf() but returns null instead of throwing exception if not found
        public static ClusterStatistic fromString(String s) {
            if (s == null) {
                return null;
            }
            for (ClusterStatistic stat : ClusterStatistic.values()) {
                if (stat.name().equals(s)) {
                    return stat;
                }
            }
            return null;
        }
    }

    public static class ClusterSelection {  // used to communicate y-list selection to the ClusterVisualizationPanel
        public final DisplayMode mode;
        public final PlotStyle plotStyle;
        public final java.util.List<ColumnDescription> columns;
        public final LangevinSolverResultSet resultSet;
        public ClusterSelection(DisplayMode mode, PlotStyle plotStyle, java.util.List<ColumnDescription> columns, LangevinSolverResultSet resultSet) {
            this.mode = mode;
            this.plotStyle = plotStyle;
            this.columns = columns;
            this.resultSet = resultSet;
        }
    }

    private static class ClusterYAxisRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof ODESolverResultSetColumnDescription cd) {
                label.setText(cd.getName());
                if (cd.isTrivial()) {
                    label.setForeground(Color.GRAY);
                } else {
                    label.setForeground(isSelected
                            ? list.getSelectionForeground()
                            : list.getForeground());
                }
                DisplayMode mode = (DisplayMode)
                        ((JComponent) list).getClientProperty("ClusterDisplayMode");
                label.setToolTipText(buildTooltip(cd, mode));
            }
            return label;
        }
        private String buildTooltip(ODESolverResultSetColumnDescription cd, DisplayMode mode) {
            if (mode == null) {
                return null;
            }
            String name = cd.getName();
            return switch (mode) {
                case COUNTS ->
                        "<html>Number of clusters of size <b>" + name +
                                "</b> <font color=\"#8B0000\">[molecules]</font></html>";
                case MASS ->
                        "<html>Number of molecules in the clusters of size <b>" + name +
                                "</b> <font color=\"#8B0000\">[molecules]</font></html>";
                case MEAN, OVERALL -> {
                    ClusterStatistic stat = ClusterStatistic.fromString(name);
                    if(stat == null) {
                        lg.error("Unknown column name in ClusterYAxisRenderer: " + name);
                        yield name; // fallback to just showing the name without tooltip
                    }
                    yield "<html>" + stat.description +
                            "<font color=\"#8B0000\"> [" + stat.unit + "]</font></html>";
                }
            };
        }
    }

    class IvjEventHandler implements ActionListener, PropertyChangeListener, ListSelectionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String cmd = e.getActionCommand();
            if (e.getSource() instanceof JRadioButton rb && SwingUtilities.isDescendingFrom(rb, ClusterSpecificationPanel.this)) {
                lg.debug("actionPerformed() called. Source is JRadioButton: {}", rb.getText());
                // DysplayMode radio buttons
                DisplayMode maybeMode = DisplayMode.fromActionCommandOrNull(cmd);
                if(maybeMode != null) {
                    DisplayMode mode = maybeMode;
                    boolean enable = (mode == DisplayMode.COUNTS || mode == DisplayMode.MASS);
                    rbLinePlot.setEnabled(enable);
                    rbBubblePlot.setEnabled(enable);
                    if (!enable) {
                        rbLinePlot.setSelected(true);   // force line plot
                        rbBubblePlot.setSelected(false);
                    }
                    // set property to inform the list about current mode (needed for renderer)
                    // moved here from valueChanged() because the tooltip of the y-axis choices needs to be updated
                    // immediately when the mode changes, even before any selection is made in the list
                    yAxisChoiceList.putClientProperty("ClusterDisplayMode", mode);
                    populateYAxisChoices(mode);
                    return;
                }
                // PlotStyle radio buttons
                PlotStyle maybePlot = PlotStyle.fromActionCommandOrNull(cmd);
                if(maybePlot != null) {
                    // plot style changed; mode and selection stay the same
                    DisplayMode mode = getCurrentDisplayMode();
                    PlotStyle plotStyle = maybePlot;
                    java.util.List<ColumnDescription> selected = getYAxisChoice().getSelectedValuesList();
                    firePropertyChange("ClusterSelection", null, new ClusterSelection(mode, plotStyle, selected, langevinSolverResultSet));
                    return;
                }
            }
        }
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if(evt.getSource() == ClusterSpecificationPanel.this) {
                lg.debug("propertyChange() called. Source is ClusterSpecificationPanel. Property name: {}, old value: {}, new value: {}",
                        evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
            }
        }
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() == ClusterSpecificationPanel.this.getYAxisChoice()) {
                if(suppressEvents || e.getValueIsAdjusting()) {
                    return; // ignore events triggered during initialization
                }
                lg.debug("valueChanged() called. Source is YAxisChoice JList. Selected values: {}", getYAxisChoice().getSelectedValuesList());
                enforceAcsSdAcoRule();
                // extract selected ColumnDescriptions
                java.util.List<ColumnDescription> selected = getYAxisChoice().getSelectedValuesList();
                DisplayMode mode = getCurrentDisplayMode();
                PlotStyle plotStyle = getCurrentPlotStyle();
//                ODESolverResultSet srs = getResultSetForMode(mode);
                // moved this to actionPerformed() where it belongs, it was being called too late here
//                // set property to inform the list about current mode (needed for renderer)
//                yAxisChoiceList.putClientProperty("ClusterDisplayMode", mode);
                // fire the event upward
                firePropertyChange("ClusterSelection", null, new ClusterSelection(mode, plotStyle, selected, langevinSolverResultSet));
            }
        }
    };

    private final ODEDataViewer owner;
    private LangevinSolverResultSet langevinSolverResultSet = null;
    private SimulationModelInfo simulationModelInfo = null;

    private final Map<DisplayMode, Integer> yAxisCounts = new LinkedHashMap<>();
    ClusterSpecificationPanel.IvjEventHandler ivjEventHandler = new ClusterSpecificationPanel.IvjEventHandler();

    private boolean suppressEvents = false;   // to prevent event firing during programmatic changes to the UI
//    private JCheckBox cbLinePlot;
//    private JCheckBox cbBubblePlot;
    private JRadioButton rbLinePlot;
    private JRadioButton rbBubblePlot;

    public ClusterSpecificationPanel(ODEDataViewer odeDataViewer) {
        super();
        this.owner = odeDataViewer;
        getYAxisChoice().setCellRenderer(new ClusterYAxisRenderer());
        initConnections();
    }

    private void initConnections() {
        JPanel content = getDisplayOptionsPanel().getContentPanel();
        for (Component c : content.getComponents()) {
            if (c instanceof JRadioButton rb) {
                rb.addActionListener(ivjEventHandler);
            }
        }
        getYAxisChoice().addListSelectionListener(ivjEventHandler);
        getYAxisChoice().setModel(getDefaultListModelY());
        this.addPropertyChangeListener(ivjEventHandler);
    }

    // --------------------------------------------------------------

    private void populateYAxisChoices(DisplayMode mode) {
        DefaultListModel<ColumnDescription> model = getDefaultListModelY();
        suppressEvents = true;    // prevent firing events while we update the list model
        try {
            model.clear();
            getYAxisChoice().setEnabled(false);
            updateYAxisLabel(mode);
            ColumnDescription[] cds = getColumnDescriptionsForMode(mode);
            if (cds == null || cds.length <= 1) {
                return;
            }
            for (ColumnDescription cd : cds) {
                if (!"t".equals(cd.getName())) {
                    model.addElement(cd);
                }
            }
        } finally {
            suppressEvents = false;
        }
        if (!model.isEmpty()) {
            getYAxisChoice().setEnabled(true);
            getYAxisChoice().setSelectedIndex(0);   // triggers valueChanged()
        }

    }

    private void updateYAxisLabel(DisplayMode mode) {
        int count = yAxisCounts.getOrDefault(mode, 0);
        String text = "<html><b>" + YAxisLabelText + "</b><span style='color:#8B0000;'>(" + count + " entries)</span></html>";
        yAxisLabel.setText(text);
    }

    private void enforceAcsSdAcoRule() {
        DisplayMode mode = getCurrentDisplayMode();
        if (mode != DisplayMode.MEAN && mode != DisplayMode.OVERALL) {
            return; // rule applies only in these modes
        }
        java.util.List<ColumnDescription> selected = getYAxisChoice().getSelectedValuesList();
        boolean acs = contains(selected, "ACS");
        boolean sd  = contains(selected, "SD");
        boolean aco = contains(selected, "ACO");
        if (!acs && sd && aco) {
            // conflict: SD + ACO without ACS
            int anchor = getYAxisChoice().getAnchorSelectionIndex();
            ColumnDescription clicked = (ColumnDescription) getYAxisChoice().getModel().getElementAt(anchor);
            String name = clicked.getName();
            if (name.equals("SD")) {
                deselect("ACO");
            } else if (name.equals("ACO")) {
                deselect("SD");
            } else if (name.equals("ACS")) {
                // Ctrl-click on ACS caused conflict -> SD must go
                deselect("SD");
            }
        }
    }

    private boolean contains(java.util.List<ColumnDescription> list, String name) {
        for (ColumnDescription cd : list) {
            if (cd.getName().equals(name)) return true;
        }
        return false;
    }

    private void deselect(String name) {
        DefaultListModel<ColumnDescription> model = getDefaultListModelY();
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            ColumnDescription cd = model.get(i);
            if (cd.getName().equals(name)) {
                getYAxisChoice().removeSelectionInterval(i, i);
                return;
            }
        }
    }

    @Override
    protected CollapsiblePanel getDisplayOptionsPanel() {
        CollapsiblePanel cp = super.getDisplayOptionsPanel();
        JPanel content = cp.getContentPanel();

        if (content.getComponentCount() == 0) {     // Only populate once
            ButtonGroup group = new ButtonGroup();
            ButtonGroup plotStyleGroup = new ButtonGroup();

            JRadioButton rbCounts = new JRadioButton(DisplayMode.COUNTS.uiLabel());
            JRadioButton rbMass = new JRadioButton(DisplayMode.MASS.uiLabel());
            JRadioButton rbMean = new JRadioButton(DisplayMode.MEAN.uiLabel());
            JRadioButton rbOverall = new JRadioButton(DisplayMode.OVERALL.uiLabel());
            rbLinePlot = new JRadioButton(PlotStyle.LINE.uiLabel());
            rbBubblePlot = new JRadioButton(PlotStyle.BUBBLE.uiLabel());

            rbCounts.setActionCommand(DisplayMode.COUNTS.actionCommand());
            rbMass.setActionCommand(DisplayMode.MASS.actionCommand());
            rbMean.setActionCommand(DisplayMode.MEAN.actionCommand());
            rbOverall.setActionCommand(DisplayMode.OVERALL.actionCommand());
            rbLinePlot.setActionCommand(PlotStyle.LINE.actionCommand());
            rbBubblePlot.setActionCommand(PlotStyle.BUBBLE.actionCommand());

            rbCounts.setToolTipText(DisplayMode.COUNTS.tooltip());
            rbMass.setToolTipText(DisplayMode.MASS.tooltip());
            rbMean.setToolTipText(DisplayMode.MEAN.tooltip());
            rbOverall.setToolTipText(DisplayMode.OVERALL.tooltip());
            rbLinePlot.setToolTipText(PlotStyle.LINE.tooltip());
            rbBubblePlot.setToolTipText(PlotStyle.BUBBLE.tooltip());

            group.add(rbCounts);
            group.add(rbMass);
            group.add(rbMean);
            group.add(rbOverall);
            plotStyleGroup.add(rbLinePlot);
            plotStyleGroup.add(rbBubblePlot);

            rbCounts.setSelected(true);     // select the first option by default, which will populate the y-axis choices
            rbLinePlot.setSelected(true);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(2, 4, 2, 4);
            gbc.gridy = 0;
            content.add(rbCounts, gbc);
            gbc.gridy = 1;
            content.add(rbMass, gbc);
            gbc.gridy = 2;
            content.add(rbMean, gbc);
            gbc.gridy = 3;
            content.add(rbOverall, gbc);

            gbc.gridy++;
            gbc.insets = new Insets(8, 4, 2, 4);    // spacer + label
            JLabel viewLabel = new JLabel("<html><b>Visualization:</b></html>");
            content.add(viewLabel, gbc);

            gbc.gridy++;
            gbc.insets = new Insets(2, 16, 2, 4);
            content.add(rbLinePlot, gbc);
            gbc.gridy++;
            content.add(rbBubblePlot, gbc);
        }
        return cp;
    }

    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {
        lg.debug("onSelectedObjectsChange() called. Number of selected objects: {}", selectedObjects.length);
    }

    public void refreshData() {
        simulationModelInfo = owner.getSimulationModelInfo();
        langevinSolverResultSet = owner.getLangevinSolverResultSet();
        yAxisCounts.clear();
        if (langevinSolverResultSet != null) {
            yAxisCounts.put(DisplayMode.COUNTS, countColumns(langevinSolverResultSet.getClusterCounts()));
            yAxisCounts.put(DisplayMode.MASS, countColumns(langevinSolverResultSet.getClusterMass()));
            yAxisCounts.put(DisplayMode.MEAN, countColumns(langevinSolverResultSet.getClusterMean()));
            yAxisCounts.put(DisplayMode.OVERALL, countColumns(langevinSolverResultSet.getClusterOverall()));
        }
        lg.debug("refreshData() called. SimulationModelInfo: {}, LangevinSolverResultSet: {}, YAxisCounts: {}",
                simulationModelInfo, langevinSolverResultSet, yAxisCounts);

        // find the selected radio button inside the collapsible panel and fire event as if it were just selected by mouse click
        // which will populate the y-axis choices based on the new data
        JPanel content = displayOptionsCollapsiblePanel.getContentPanel();
        for (Component c : content.getComponents()) {
            if (c instanceof JRadioButton rb && rb.isSelected()) {
                ivjEventHandler.actionPerformed(new ActionEvent(rb, ActionEvent.ACTION_PERFORMED, rb.getActionCommand()));
                break;
            }
        }
    }
    private int countColumns(ODESolverResultSet srs) {
        if (srs == null) return 0;
        ColumnDescription[] cds = srs.getColumnDescriptions();
        if (cds == null) return 0;
        return cds.length > 1 ? cds.length-1 : 0; // subtract one for time column, but don't return negative if no column at all
    }

    public static ODESolverResultSet getResultSetForMode(LangevinSolverResultSet lsrs, DisplayMode mode) {
        if (lsrs == null) {
            return null;
        }
        return switch (mode) {
            case COUNTS -> lsrs.getClusterCounts();
            case MASS -> lsrs.getClusterMass();
            case MEAN   -> lsrs.getClusterMean();
            case OVERALL-> lsrs.getClusterOverall();
        };
    }

    private ColumnDescription[] getColumnDescriptionsForMode(DisplayMode mode) {
        ODESolverResultSet srs = getResultSetForMode(langevinSolverResultSet, mode);
        return (srs == null ? null : srs.getColumnDescriptions());
    }
    private DisplayMode getCurrentDisplayMode() {
        JPanel content = displayOptionsCollapsiblePanel.getContentPanel();
        for (Component c : content.getComponents()) {
            if (c instanceof JRadioButton rb && rb.isSelected()) {
                return DisplayMode.valueOf(rb.getActionCommand());
            }
        }
        return DisplayMode.COUNTS; // default fallback
    }
    private PlotStyle getCurrentPlotStyle() {
        if (rbLinePlot.isSelected()) {
            return PlotStyle.LINE;
        } else if (rbBubblePlot.isSelected()) {
            return PlotStyle.BUBBLE;
        } else {
            lg.warn("getCurrentPlotStyle() could not determine selected plot style, defaulting to LINE");
            return PlotStyle.LINE; // default fallback
        }
    }
}
