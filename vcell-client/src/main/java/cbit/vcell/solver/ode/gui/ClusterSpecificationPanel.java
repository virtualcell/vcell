package cbit.vcell.solver.ode.gui;

import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.simdata.LangevinSolverResultSet;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.vcell.util.gui.CollapsiblePanel;

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
import java.util.LinkedHashMap;
import java.util.Map;

public class ClusterSpecificationPanel extends AbstractSpecificationPanel {

    public enum DisplayMode {
        COUNTS(
                "COUNTS",
                "Cluster Counts",
                "Show the number of clusters of each size"
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
        public final java.util.List<ColumnDescription> columns;
        public final ODESolverResultSet resultSet;
        public ClusterSelection(DisplayMode mode, java.util.List<ColumnDescription> columns, ODESolverResultSet resultSet) {
            this.mode = mode;
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
                case MEAN, OVERALL -> {
                    ClusterStatistic stat = ClusterStatistic.valueOf(name);
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
                System.out.println(this.getClass().getName() + ".actionPerformed() called. Source is JRadioButton: " + rb.getText());
                DisplayMode mode = DisplayMode.fromActionCommand(cmd);
                populateYAxisChoices(mode);
            }
        }
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if(evt.getSource() == ClusterSpecificationPanel.this) {
                System.out.println(this.getClass().getName() + ".IvjEventHandler.propertyChange() called");
            }
        }
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() == ClusterSpecificationPanel.this.getYAxisChoice()) {
                if(suppressEvents || e.getValueIsAdjusting()) {
                    return; // ignore events triggered during initialization
                }
                System.out.println(this.getClass().getName() + ".valueChanged() called. Source is YAxisChoice JList. Selected values: " + getYAxisChoice().getSelectedValuesList());
                enforceAcsSdAcoRule();
                // extract selected ColumnDescriptions
                java.util.List<ColumnDescription> selected = getYAxisChoice().getSelectedValuesList();
                DisplayMode mode = getCurrentDisplayMode();
                ODESolverResultSet srs = getResultSetForMode(mode);
                // set property to inform the list about current mode (needed for renderer)
                yAxisChoiceList.putClientProperty("ClusterDisplayMode", mode);
                // fire the event upward
                firePropertyChange("ClusterSelection", null, new ClusterSelection(mode, selected, srs));
            }
        }
    };

    private final ODEDataViewer owner;
    private LangevinSolverResultSet langevinSolverResultSet = null;
    private SimulationModelInfo simulationModelInfo = null;

    private final Map<DisplayMode, Integer> yAxisCounts = new LinkedHashMap<>();
    ClusterSpecificationPanel.IvjEventHandler ivjEventHandler = new ClusterSpecificationPanel.IvjEventHandler();

    private boolean suppressEvents = false;   // to prevent event firing during programmatic changes to the UI

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

            JRadioButton rbCounts = new JRadioButton(DisplayMode.COUNTS.uiLabel());
            JRadioButton rbMean = new JRadioButton(DisplayMode.MEAN.uiLabel());
            JRadioButton rbOverall = new JRadioButton(DisplayMode.OVERALL.uiLabel());

            rbCounts.setActionCommand(DisplayMode.COUNTS.actionCommand());
            rbMean.setActionCommand(DisplayMode.MEAN.actionCommand());
            rbOverall.setActionCommand(DisplayMode.OVERALL.actionCommand());

            rbCounts.setToolTipText(DisplayMode.COUNTS.tooltip());
            rbMean.setToolTipText(DisplayMode.MEAN.tooltip());
            rbOverall.setToolTipText(DisplayMode.OVERALL.tooltip());

            group.add(rbCounts);
            group.add(rbMean);
            group.add(rbOverall);

            rbCounts.setSelected(true);     // select the first option by default, which will populate the y-axis choices

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(2, 4, 2, 4);
            gbc.gridy = 0;
            content.add(rbCounts, gbc);
            gbc.gridy = 1;
            content.add(rbMean, gbc);
            gbc.gridy = 2;
            content.add(rbOverall, gbc);
        }
        return cp;
    }

//    @Override
//    protected JList getYAxisChoice() {
//        if ((yAxisChoiceList == null)) {
//            yAxisChoiceList = new JList();
//            yAxisChoiceList.setName("YAxisChoice");
//            yAxisChoiceList.setBounds(0, 0, 160, 120);
//            yAxisChoiceList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//            yAxisChoiceList.setCellRenderer(new DefaultListCellRenderer() {
//                @Override
//                public Component getListCellRendererComponent(JList<?> list, Object value, int index,
//                                    boolean isSelected, boolean cellHasFocus) {
//                    JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//
//                    if (value instanceof ODESolverResultSetColumnDescription cd) {
//                        String name = cd.getName();
//                        label.setText(name);
//
//                        if (cd.isTrivial()) {   // gray out trivial entries
//                            label.setForeground(Color.GRAY);
//                        } else {
//                            label.setForeground(isSelected
//                                    ? list.getSelectionForeground()
//                                    : list.getForeground());
//                        }
//
//                        DisplayMode mode = (DisplayMode)        // determine tooltip based on DisplayMode
//                                ((JComponent) list).getClientProperty("ClusterDisplayMode");
//                        if (mode == null) {
//                            label.setToolTipText(null);
//                            return label;
//                        }
//                        switch (mode) {
//                            case COUNTS:
//                                // cluster size <b>X</b> molecules
//                                label.setText(name);
//                                label.setToolTipText(
//                                        "<html>Number of Clusters of size: <b>" + name + "</b> " + "<font color=\"#8B0000\">" + "[molecules] </font></html>"
//                                );
//                                break;
//                            case MEAN:
//                            case OVERALL:
//                                ClusterStatistic stat = ClusterStatistic.valueOf(name);
//                                label.setText(stat.fullName);
//                                String tooltip = "<html>" + stat.description + "<font color=\"#8B0000\">" + " [" + stat.unit + "] </font></html>";
//                                label.setToolTipText(tooltip);
//                                break;
//                        }
//                    }
//                    return label;
//                }
//            });
//        }
//        return yAxisChoiceList;
//    }

    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {
        System.out.println(this.getClass().getSimpleName() + ".onSelectedObjectsChange() called with " + selectedObjects.length + " objects");
    }

    public void refreshData() {
        simulationModelInfo = owner.getSimulationModelInfo();
        langevinSolverResultSet = owner.getLangevinSolverResultSet();
        yAxisCounts.clear();
        if (langevinSolverResultSet != null) {
            yAxisCounts.put(DisplayMode.COUNTS, countColumns(langevinSolverResultSet.getClusterCounts()));
            yAxisCounts.put(DisplayMode.MEAN, countColumns(langevinSolverResultSet.getClusterMean()));
            yAxisCounts.put(DisplayMode.OVERALL, countColumns(langevinSolverResultSet.getClusterOverall()));
        }
        System.out.println(this.getClass().getSimpleName() + ".refreshData() called");

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
    private ODESolverResultSet getResultSetForMode(DisplayMode mode) {
        if (langevinSolverResultSet == null) {
            return null;
        }
        return switch (mode) {
            case COUNTS -> langevinSolverResultSet.getClusterCounts();
            case MEAN   -> langevinSolverResultSet.getClusterMean();
            case OVERALL-> langevinSolverResultSet.getClusterOverall();
        };
    }

    private ColumnDescription[] getColumnDescriptionsForMode(DisplayMode mode) {
        ODESolverResultSet srs = getResultSetForMode(mode);
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
}
