package cbit.vcell.solver.ode.gui;

import cbit.vcell.client.data.ODEDataViewer;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.math.ODESolverResultSetColumnDescription;
import cbit.vcell.simdata.LangevinSolverResultSet;
import cbit.vcell.solver.SimulationModelInfo;
import cbit.vcell.solver.ode.ODESolverResultSet;
import cbit.vcell.util.ColumnDescription;
import org.vcell.util.gui.CollapsiblePanel;

import java.awt.*;
import java.beans.PropertyChangeListener;
import javax.swing.*;

@SuppressWarnings("serial")
public abstract class AbstractSpecificationPanel extends DocumentEditorSubPanel {

    // ------------------------------
    // Shared UI components
    // ------------------------------
    protected CollapsiblePanel displayOptionsCollapsiblePanel = null;

    protected JScrollPane jScrollPaneYAxis = null;
    protected static final String YAxisLabelText = "Y Axis: ";
    protected JLabel yAxisLabel = null;

    protected JList yAxisChoiceList = null;
    protected DefaultListModel<ColumnDescription> defaultListModelY = null;

    // ------------------------------
    // Constructor
    // ------------------------------
    public AbstractSpecificationPanel() {
        super();
        initialize();
    }

    // ------------------------------
    // Initialization (shared layout)
    // ------------------------------
    private void initialize() {
        System.out.println(this.getClass().getSimpleName() + ".initialize() called");

        setPreferredSize(new Dimension(213, 600));
        setLayout(new GridBagLayout());
        setSize(248, 604);
        setMinimumSize(new Dimension(125, 300));

        // X-axis label
        JLabel xAxisLabel = new JLabel("<html><b>X Axis: </b></html>");
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(4, 4, 0, 4);
        add(xAxisLabel, gbc);

        // X-axis textbox
        JTextField xAxisTextBox = new JTextField("time [seconds]");
        xAxisTextBox.setEnabled(false);
        xAxisTextBox.setEditable(false);
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 4, 4, 4);
        add(xAxisTextBox, gbc);

        // Y-axis label
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(4, 4, 0, 4);
        gbc.gridx = 0; gbc.gridy = 2;
        add(getYAxisLabel(), gbc);

        // Display options panel (content added by subclasses)
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(4, 4, 5, 4);
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(getDisplayOptionsPanel(), gbc);

        // Y-axis list
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 4, 4, 4);
        add(getJScrollPaneYAxis(), gbc);
    }


    // ------------------------------
    // Shared Y-axis label
    // ------------------------------
    protected JLabel getYAxisLabel() {
        if (yAxisLabel == null) {
            yAxisLabel = new JLabel();
            yAxisLabel.setName("YAxisLabel");
            String text = "<html><b>" + YAxisLabelText + "</b></html>";
            yAxisLabel.setText(text);
        }
        return yAxisLabel;
    }

    // ------------------------------
    // Shared Display Options container
    // (subclasses populate content)
    // ------------------------------
    protected CollapsiblePanel getDisplayOptionsPanel() {
        if (displayOptionsCollapsiblePanel == null) {
            displayOptionsCollapsiblePanel =
                    new CollapsiblePanel("Display Options", true);

            JPanel content = displayOptionsCollapsiblePanel.getContentPanel();
            content.setLayout(new GridBagLayout());
        }
        return displayOptionsCollapsiblePanel;
    }

    // ------------------------------
    // Shared Y-axis scroll pane
    // ------------------------------
    protected JScrollPane getJScrollPaneYAxis() {
        if (jScrollPaneYAxis == null) {
            jScrollPaneYAxis = new JScrollPane();
            jScrollPaneYAxis.setName("JScrollPaneYAxis");
            jScrollPaneYAxis.setViewportView(getYAxisChoice());
            jScrollPaneYAxis.setMinimumSize(new Dimension(100, 120));
            jScrollPaneYAxis.setPreferredSize(new Dimension(100, 120));
        }
        return jScrollPaneYAxis;
    }

    // ------------------------------
    // Shared Y-axis list
    // ------------------------------
    protected JList<ColumnDescription> getYAxisChoice() {
        if (yAxisChoiceList == null) {
            yAxisChoiceList = new JList();
            yAxisChoiceList.setName("YAxisChoice");
            yAxisChoiceList.setBounds(0, 0, 160, 120);
            yAxisChoiceList.setSelectionMode(
                    ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
            );
            yAxisChoiceList.setCellRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> list, Object value, int index,
                        boolean isSelected, boolean cellHasFocus) {

                    JLabel label = (JLabel) super.getListCellRendererComponent(
                            list, value, index, isSelected, cellHasFocus
                    );
                    if (value instanceof ColumnDescription cd) {
                        label.setText(cd.getName());
                    }
                    return label;
                }
            });
        }
        return yAxisChoiceList;
    }

    // ------------------------------
    // Shared Y-axis model
    // ------------------------------
    protected DefaultListModel<ColumnDescription> getDefaultListModelY() {
        if (defaultListModelY == null) {
            defaultListModelY = new DefaultListModel<>();
        }
        return defaultListModelY;
    }

    // ------------------------------
    // Shared exception handler
    // ------------------------------
    protected void handleException(Throwable exception) {
        System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }
}