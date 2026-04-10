package cbit.vcell.solver.ode.gui;

import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.simdata.LangevinSolverResultSet;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.vcell.model.ssld.SsldUtils;
import org.vcell.util.gui.CollapsiblePanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Container;
import java.awt.Component;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Map;

public class MoleculeSpecificationPanel extends AbstractSpecificationPanel {

    public enum DisplayMode {
        MOLECULES("MOLECULES", "Molecules", "Display molecule counts over time"),
        BOUND_SITES("BOUND_SITES", "Bound Sites", "Display number of bound sites over time"),
        FREE_SITES("FREE_SITES", "Free Sites", "Display number of free sites over time"),
        TOTAL_SITES("TOTAL_SITES", "Total Sites", "Display total number of sites over time");

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
        public static  boolean isDisplayModeActionCommand(String cmd) {
            for (MoleculeSpecificationPanel.DisplayMode m : values()) {
                if (m.actionCommand().equals(cmd)) {
                    return true;
                }
            }
            return false;
        }
        public static MoleculeSpecificationPanel.DisplayMode fromActionCommand(String cmd) {
            for (MoleculeSpecificationPanel.DisplayMode m : values()) {
                if (m.actionCommand.equals(cmd)) {
                    return m;
                }
            }
            throw new IllegalArgumentException("Unknown DisplayMode: " + cmd);
        }
    }
    public enum StatisticSelection {

        AVG("STAT_AVG", "Average", "Display the average value over time"),
        MIN_MAX("STAT_MINMAX", "Min / Max", "Display min/max envelope over time"),
        SD("STAT_SD", "Standard Deviation", "Display standard deviation around the mean");

        private final String actionCommand;
        private final String uiLabel;
        private final String tooltip;
        StatisticSelection(String actionCommand, String uiLabel, String tooltip) {
            this.actionCommand = actionCommand;
            this.uiLabel = uiLabel;
            this.tooltip = tooltip;
        }
        public String actionCommand() { return actionCommand; }
        public String uiLabel()       { return uiLabel; }
        public String tooltip()       { return tooltip; }
        public static boolean isStatisticSelectionActionCommand(String cmd) {
            for (MoleculeSpecificationPanel.StatisticSelection s : values()) {
                if (s.actionCommand().equals(cmd)) {
                    return true;
                }
            }
            return false;
        }
        public static StatisticSelection fromActionCommand(String cmd) {
            for (MoleculeSpecificationPanel.StatisticSelection s : values()) {
                if (s.actionCommand.equals(cmd)) {
                    return s;
                }
            }
            throw new IllegalArgumentException("Unknown StatisticSelection: " + cmd);
        }
    }

    public static class MoleculeSelection {
        public final java.util.List<ColumnDescription> selectedColumns;
        public final java.util.List<StatisticSelection> selectedStatistics;
        public final java.util.List<DisplayMode> selectedDisplayModes;
        public MoleculeSelection(
                java.util.List<ColumnDescription> selectedColumns,
                java.util.List<StatisticSelection> selectedStatistics,
                java.util.List<DisplayMode> selectedDisplayModes) {
            this.selectedColumns = selectedColumns;
            this.selectedStatistics = selectedStatistics;
            this.selectedDisplayModes = selectedDisplayModes;
        }
    }

    private static class MoleculeYAxisRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof ODESolverResultSetColumnDescription cd) {
                label.setText(cd.getName());
                if (cd.isTrivial()) {
                    label.setForeground(Color.GRAY);
                } else {
                    // restore normal selection / non-selection colors
                    label.setForeground(isSelected
                            ? list.getSelectionForeground()
                            : list.getForeground());
                }
                label.setToolTipText(buildTooltip(cd));
            }
            return label;
        }
        private String buildTooltip(ColumnDescription cd) {
            StringBuilder sb = new StringBuilder("<html>");
            sb.append("<b>").append(cd.getName()).append("</b> ");
            if (cd instanceof ODESolverResultSetColumnDescription mcd) {
                sb.append("<i>Solver-generated Observable</i>");
            }
            sb.append("</html>");
            return sb.toString();
        }
    }

    class IvjEventHandler implements ActionListener, PropertyChangeListener, ListSelectionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JCheckBox cb && SwingUtilities.isDescendingFrom(cb, MoleculeSpecificationPanel.this)) {
                boolean selected = cb.isSelected();
                System.out.println(this.getClass().getName() + ".actionPerformed() called with action command: " + e.getActionCommand() + " = " + selected);
                String cmd = e.getActionCommand();
                if (DisplayMode.isDisplayModeActionCommand(cmd)) {
                    java.util.List<DisplayMode> displayModes = getSelectedDisplayModes();
                    populateYAxisChoices(displayModes);
                } else if (StatisticSelection.isStatisticSelectionActionCommand(cmd)) {
                    MoleculeSelection sel = new MoleculeSelection(
                            getYAxisChoice().getSelectedValuesList(),
                            getSelectedStatistics(),
                            getSelectedDisplayModes()
                    );
                    System.out.println("MoleculeSelection changed: " + sel.selectedColumns.size() + " columns, " + sel.selectedStatistics.size() + " statistics, " + sel.selectedDisplayModes.size() + " display modes");
                    firePropertyChange("MoleculeSelectionChanged", null, sel);
                }
            }
        }
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getSource() == owner.getMoleculeSpecificationPanel()) {
                System.out.println(this.getClass().getName() + ".IvjEventHandler.propertyChange() called with " + evt.getPropertyName());
            }
        }
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() == MoleculeSpecificationPanel.this.getYAxisChoice()) {
                if (supressEvents || e.getValueIsAdjusting()) {
                    return;
                }
                System.out.println(this.getClass().getName() + ".IvjEventHandler.valueChanged() called");
                MoleculeSelection sel = new MoleculeSelection(
                        getYAxisChoice().getSelectedValuesList(),
                        getSelectedStatistics(),
                        getSelectedDisplayModes()
                );
                System.out.println("MoleculeSelection changed: " + sel.selectedColumns.size() + " columns, " + sel.selectedStatistics.size() + " statistics, " + sel.selectedDisplayModes.size() + " display modes");
                firePropertyChange("MoleculeSelectionChanged", null, sel);
            }
        }
    }
    MoleculeSpecificationPanel.IvjEventHandler ivjEventHandler = new MoleculeSpecificationPanel.IvjEventHandler();

    private final ODEDataViewer owner;
    private LangevinSolverResultSet langevinSolverResultSet = null;
    private SimulationModelInfo simulationModelInfo = null;

    private boolean supressEvents = true;   // flag to suppress events during initialization

    public MoleculeSpecificationPanel(ODEDataViewer owner) {
        super();
        this.owner = owner;
        getYAxisChoice().setCellRenderer(new MoleculeYAxisRenderer());
        initConnections();
    }

    protected void initConnections() {
        JPanel content = getDisplayOptionsPanel().getContentPanel();
        for (Component c : content.getComponents()) {
            if (c instanceof JCheckBox) {
                ((JCheckBox) c).addActionListener(ivjEventHandler); // wire checkbox listeners now that ivjEventHandler is constructed
            }
        }
        getYAxisChoice().addListSelectionListener(ivjEventHandler);
        getYAxisChoice().setModel(getDefaultListModelY());
        this.addPropertyChangeListener(ivjEventHandler);
    }

    // -----------------------------------------------------------

    @Override
    protected CollapsiblePanel getDisplayOptionsPanel() {
        CollapsiblePanel cp = super.getDisplayOptionsPanel();
        JPanel content = cp.getContentPanel();

        if (content.getComponentCount() == 0) {     // Only populate once
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(2, 4, 2, 4);

            JCheckBox firstDisplayModeCheckBox = null;          // GROUP 1: DisplayMode checkboxes
            for (DisplayMode mode : DisplayMode.values()) {
                JCheckBox cb = new JCheckBox(mode.uiLabel());
                cb.setActionCommand(mode.actionCommand());
                cb.setToolTipText(mode.tooltip());
                // can't add action listeners here, the event handler object is stil null
                // will add them in initConnections() after the whole panel is constructed
                if (firstDisplayModeCheckBox == null) {
                    firstDisplayModeCheckBox = cb;   // remember first
                }
                content.add(cb, gbc);
                gbc.gridy++;
            }
            if (firstDisplayModeCheckBox != null) {         // Select default DisplayMode = first enum entry (MOLECULES)
                firstDisplayModeCheckBox.setSelected(true);
            }

            gbc.insets = new Insets(8, 4, 2, 4);    // Spacer + label for statistics group
            JLabel statsLabel = new JLabel("<html><b>Statistics:</b></html>");
            content.add(statsLabel, gbc);
            gbc.gridy++;
            gbc.insets = new Insets(2, 16, 2, 4);

            JCheckBox firstStatisticCheckBox = null;            // GROUP 2: StatisticSelection checkboxes
            for (StatisticSelection stat : StatisticSelection.values()) {
                JCheckBox cb = new JCheckBox(stat.uiLabel());
                cb.setActionCommand(stat.actionCommand());
                cb.setToolTipText(stat.tooltip());
                if (firstStatisticCheckBox == null) {
                    firstStatisticCheckBox = cb;        // remember first
                }
                content.add(cb, gbc);
                gbc.gridy++;
            }
            if (firstStatisticCheckBox != null) {       // Select default StatisticSelection = first enum entry (AVG)
                firstStatisticCheckBox.setSelected(true);
            }
        }
        return cp;
    }

    private void populateYAxisChoices(java.util.List<DisplayMode> modes) {
        JList<ColumnDescription> list = getYAxisChoice();
        DefaultListModel<ColumnDescription> model = getDefaultListModelY();
        // Remember what was selected before we touch the model
        java.util.List<ColumnDescription> previouslySelected = list.getSelectedValuesList();

        // addElement() calls will fire ListDataEvents which will trigger list selection events, which will eventually
        // result in multiple calls to firePropertyChange("MoleculeSelectionChanged", ...) and multiple redraws of
        // the right panels (plot and data table). To avoid firing these events while we're still populating the model,
        // we'll set a flag to suppress events temporarily. The event handler will check this flag and skip firing
        // property changes if it's set.
        supressEvents = true;
        try {
            model.clear();
            list.setEnabled(false);
            for (DisplayMode mode : modes) {
                java.util.List<ColumnDescription> cds = getColumnDescriptionsForMode(mode);
                if (cds == null || cds.size() <= 1) {
                    continue;
                }
                for (ColumnDescription cd : cds) {
                    if (!"t".equals(cd.getName())) {
                        model.addElement(cd);
                    }
                }
            }
        } finally {
            supressEvents = false;   // re-enable events after we're done populating the model, even if an exception occurs
        }
        updateYAxisLabel(model);
        if (!model.isEmpty()) {
            list.setEnabled(true);
            // Re-apply previous selection where possible
            if (!previouslySelected.isEmpty()) {
                java.util.List<Integer> indicesToSelect = new ArrayList<>();
                for (int i = 0; i < model.size(); i++) {
                    ColumnDescription cd = model.get(i);
                    if (previouslySelected.contains(cd)) {
                        indicesToSelect.add(i);
                    }
                }
                if (!indicesToSelect.isEmpty()) {
                    int[] idx = indicesToSelect.stream().mapToInt(Integer::intValue).toArray();
                    list.setSelectedIndices(idx);
                    return; // do NOT force-select index 0
                }
            }
            // Fallback: nothing from previous selection exists anymore
            list.setSelectedIndex(0);
        }
    }

    private java.util.List<ColumnDescription> getColumnDescriptionsForMode(DisplayMode mode) {
        java.util.List<ColumnDescription> list = new ArrayList<>();
        if (langevinSolverResultSet == null || langevinSolverResultSet.metadataMap == null) {
            return list;
        }
        for (Map.Entry<String, SsldUtils.LangevinResult> entry : langevinSolverResultSet.metadataMap.entrySet()) {
            String columnName = entry.getKey();
            SsldUtils.LangevinResult lr = entry.getValue();
            if (lr.qualifier == SsldUtils.Qualifier.NONE) {     // Qualifier.NONE is illegal for all modes
                throw new IllegalArgumentException("Invalid qualifier NONE for column: " + columnName);
            }
            boolean include = false;
            switch (mode) {
                case MOLECULES:
                    include = (lr.site == null || lr.site.isEmpty());   // Molecules = entries with no site
                    break;
                case BOUND_SITES:
                    include = (!lr.site.isEmpty() && lr.qualifier == SsldUtils.Qualifier.BOUND);
                    break;
                case FREE_SITES:
                    include = (!lr.site.isEmpty() && lr.qualifier == SsldUtils.Qualifier.FREE);
                    break;
                case TOTAL_SITES:
                    include = (!lr.site.isEmpty() && lr.qualifier == SsldUtils.Qualifier.TOTAL);
                    break;
            }
            if (include) {
                ColumnDescription cd = langevinSolverResultSet.getColumnDescriptionByName(columnName);
                if (cd != null) {
                    list.add(cd);
                }
            }
        }
        return list;
    }

    // -----------------------------------------------------------

    private void updateYAxisLabel(DefaultListModel<ColumnDescription> model) {
        int count = (model == null) ? 0 : model.getSize();
        String text = "<html><b>" + YAxisLabelText + "</b><span style='color:#8B0000;'>(" + count + " entries)</span></html>";
        yAxisLabel.setText(text);
    }

    public java.util.List<DisplayMode> getSelectedDisplayModes() {
        java.util.List<DisplayMode> list = new ArrayList<>();
        JPanel content = getDisplayOptionsPanel().getContentPanel();
        java.awt.Component[] components = content.getComponents();
        for (Component c : components) {
            if (c instanceof JCheckBox cb && cb.isSelected()) {
                String actionCommand = cb.getActionCommand();
                if (DisplayMode.isDisplayModeActionCommand(actionCommand)) {
                    list.add(DisplayMode.fromActionCommand(actionCommand));
                }
            }
        }
        return list;
    }
    public java.util.List<StatisticSelection> getSelectedStatistics() {
        java.util.List<StatisticSelection> list = new ArrayList<>();
        JPanel content = getDisplayOptionsPanel().getContentPanel();
        java.awt.Component[] components = content.getComponents();
        for (Component c : components) {
            if (c instanceof JCheckBox cb && cb.isSelected()) {
                String actionCommand = cb.getActionCommand();
                if (StatisticSelection.isStatisticSelectionActionCommand(actionCommand)) {
                    list.add(StatisticSelection.fromActionCommand(actionCommand));
                }
            }
        }
        return list;
    }

    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {
        System.out.println(this.getClass().getSimpleName() + ".onSelectedObjectsChange() called with " + selectedObjects.length + " objects");

    }
    public void refreshData() {
        System.out.println(this.getClass().getSimpleName() + ".refreshData() called");
        simulationModelInfo = owner.getSimulationModelInfo();
        langevinSolverResultSet = owner.getLangevinSolverResultSet();
        if(displayOptionsCollapsiblePanel == null) {    // the whole panel should exist already at this point,
                                                        // lazy inilization here may be a bad idea but we'll do it anyway
            System.out.println("displayOptionsCollapsiblePanel is null");
        }
        JPanel content = getDisplayOptionsPanel().getContentPanel();
        for (Component c : content.getComponents()) {
            if (c instanceof JCheckBox cb && cb.isSelected()) {
                // we know that during the initial construction of displayOptionsCollapsiblePanel we select the
                // first DisplayMode and first StatisticSelection checkboxes, so this code will fire once for sure
                // and so we'll properly populate the yAxisChoiceList
                ivjEventHandler.actionPerformed(new ActionEvent(cb, ActionEvent.ACTION_PERFORMED, cb.getActionCommand()));
                break;
            }
        }
    }

}
