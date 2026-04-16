package cbit.vcell.solver.ode.gui;

import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.gui.JToolBarToggleButton;
import org.vcell.util.gui.VCellIcons;


import java.awt.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public abstract class AbstractVisualizationPanel extends DocumentEditorSubPanel {

    private static final Logger lg = LogManager.getLogger(AbstractVisualizationPanel.class);

    protected class LineIcon implements Icon {
        private final Color color;
        public LineIcon(Color color) {
            this.color = color;
        }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setStroke(new BasicStroke(6.0f));
            g2.setPaint(color);
            int midY = y + getIconHeight() / 2;
            g2.drawLine(x, midY, x + getIconWidth(), midY);
        }
        @Override
        public int getIconWidth() { return 80; }
        @Override
        public int getIconHeight() {
            return 4;  // more vertical room for a wider stroke
        }
    }

    protected JPanel cardPanel;
    protected JPanel plotPanelContainer;
    protected JPanel dataPanelContainer;
    protected JPanel legendPanel;
    protected JPanel legendContentPanel;
    protected JPanel bottomRightPanel;

    protected JLabel bottomLabel;           // info about the current simulation and # of jobs
    private JLabel timeLabelBottom;         // "time" under the X-axis in the plot
    protected JCheckBox crosshairCheckBox;
    protected JLabel crosshairCoordLabel;

    protected JToolBarToggleButton plotButton;
    protected JToolBarToggleButton dataButton;

    protected CardLayout cardLayout;


    public AbstractVisualizationPanel() {
        super();
        // do NOT call initialize here !!! there are overriden calls to abstract methods that rely on subclass fields
    }

    // initialize calls ovverriden methods that rely on subclass fields, so we can't call it from the constructor.
    // Instead, each subclass must call initialize() after its own constructor has run and its own fields are initialized.
    protected void initialize() {
        setPreferredSize(new Dimension(420, 400));
        setLayout(new BorderLayout());
        setSize(513, 457);
        setBackground(Color.white);

        add(getCardPanel(), BorderLayout.CENTER);
        add(getBottomRightPanel(), BorderLayout.SOUTH);
        add(getLegendPanel(), BorderLayout.EAST);
    }

    // -------------------------
    // Abstract hooks
    // -------------------------
    // Subclass must provide the plot panel (MoleculePlotPanel or ClusterPlotPanel)
    protected abstract JPanel createPlotPanel();
    // Subclass must provide the data panel (MoleculeDataPanel or ClusterDataPanel)
    protected abstract JPanel createDataPanel();
    // Subclass must implement crosshair enabling logic
    protected abstract void setCrosshairEnabled(boolean enabled);

    // -------------------------
    // Card panel
    // -------------------------
    protected JPanel getCardPanel() {
        if (cardPanel == null) {
            cardPanel = new JPanel();
            cardPanel.setName("CardPanel");
            cardLayout = new CardLayout();
            cardPanel.setLayout(cardLayout);
            cardPanel.add(getPlotPanelContainer(), "PlotPanelContainer");
            cardPanel.add(getDataPanelContainer(), "DataPanelContainer");
        }
        return cardPanel;
    }

    private JPanel getPlotPanelContainer() {
        if (plotPanelContainer == null) {
            plotPanelContainer = new JPanel();
            plotPanelContainer.setName("PlotPanelContainer");
            plotPanelContainer.setLayout(new BorderLayout());
            plotPanelContainer.add(createPlotPanel(), BorderLayout.CENTER); // Subclass provides the actual plot panel
            plotPanelContainer.add(getTimeLabelBottom(), BorderLayout.SOUTH);   // Bottom label (e.g., "time")
        }
        return plotPanelContainer;
    }

    private JLabel getTimeLabelBottom() {
        if (timeLabelBottom == null) {
            timeLabelBottom = new JLabel();
            timeLabelBottom.setName("TimeLabelBottom");
            timeLabelBottom.setText("time");
            timeLabelBottom.setForeground(Color.black);
            timeLabelBottom.setHorizontalTextPosition(SwingConstants.CENTER);
            timeLabelBottom.setHorizontalAlignment(SwingConstants.CENTER);
        }
        return timeLabelBottom;
    }

    private JPanel getDataPanelContainer() {
        if (dataPanelContainer == null) {
            dataPanelContainer = new JPanel();
            dataPanelContainer.setName("DataPanelContainer");
            dataPanelContainer.setLayout(new BorderLayout());
            dataPanelContainer.add(createDataPanel(), BorderLayout.CENTER);
        }
        return dataPanelContainer;
    }
    // -------------------------
    // Legend panel
    // -------------------------
    protected JPanel getLegendPanel() {
        if (legendPanel == null) {
            legendPanel = new JPanel();
            legendPanel.setName("LegendPanel");
            legendPanel.setLayout(new BorderLayout());
            legendPanel.add(new JLabel("        "), BorderLayout.SOUTH);    // South spacer (keeps layout stable)

            JLabel labelLegendTitle = new JLabel("Plot Legend:");   // Title label with border
            labelLegendTitle.setBorder(new EmptyBorder(10, 4, 10, 4));
            legendPanel.add(labelLegendTitle, BorderLayout.NORTH);

            JScrollPane scrollPane = new JScrollPane(getLegendContentPanel());  // Scrollpane containing the legend content panel
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
            legendPanel.add(scrollPane, BorderLayout.CENTER);
        }
        return legendPanel;
    }
    protected JPanel getLegendContentPanel() {
        if (legendContentPanel == null) {
            legendContentPanel = new JPanel() {
                @Override
                public Dimension getPreferredSize() {
                    // Reserve space for vertical scrollbar from the start
                    Dimension d = super.getPreferredSize();
                    int scrollbarWidth = UIManager.getInt("ScrollBar.width");
                    return new Dimension(d.width + scrollbarWidth, d.height);
                }
            };
            legendContentPanel.setName("LegendContentPanel");
            legendContentPanel.setLayout(new BoxLayout(legendContentPanel, BoxLayout.Y_AXIS));
        }
        return legendContentPanel;
    }
    // -------------------------
    // Bottom-right panel
    // -------------------------
    private JPanel getBottomRightPanel() {
        if (bottomRightPanel == null) {
            bottomRightPanel = new JPanel();
            bottomRightPanel.setName("BottomRightPanel");
            bottomRightPanel.setLayout(new GridBagLayout());

            GridBagConstraints gbc;
            gbc = new GridBagConstraints();
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(4, 4, 4, 4);
            bottomRightPanel.add(getBottomLabel(), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 2;
            gbc.gridy = 0;
            gbc.insets = new Insets(4, 4, 4, 2);
            bottomRightPanel.add(getCrosshairCheckBox(), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 3;
            gbc.gridy = 0;
            gbc.insets = new Insets(4, 2, 4, 2);
            bottomRightPanel.add(getCrosshairCoordLabel(), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 4;
            gbc.gridy = 0;
            gbc.insets = new Insets(4, 4, 4, 4);
            bottomRightPanel.add(getPlotButton(), gbc);

            gbc = new GridBagConstraints();
            gbc.gridx = 5;
            gbc.gridy = 0;
            gbc.insets = new Insets(4, 4, 4, 4);
            bottomRightPanel.add(getDataButton(), gbc);
        }
        return bottomRightPanel;
    }

    protected JLabel getBottomLabel() {
        if (bottomLabel == null) {
            bottomLabel = new JLabel();
            bottomLabel.setName("BottomLabel");
            bottomLabel.setText(" ");
            bottomLabel.setForeground(Color.blue);
            bottomLabel.setPreferredSize(new Dimension(44, 20));
            bottomLabel.setMinimumSize(new Dimension(44, 20));
            bottomLabel.setFont(new Font("dialog", Font.PLAIN, 12));
        }
        return bottomLabel;
    }

    protected JCheckBox getCrosshairCheckBox() {
        if (crosshairCheckBox == null) {
            crosshairCheckBox = new JCheckBox("Show Crosshair");
            crosshairCheckBox.setName("CrosshairCheckBox");
            crosshairCheckBox.setSelected(true); // default ON
        }
        return crosshairCheckBox;
    }

    protected JLabel getCrosshairCoordLabel() {
        if (crosshairCoordLabel == null) {
            crosshairCoordLabel = new JLabel(emptyCoordText);
            crosshairCoordLabel.setName("CrosshairCoordLabel");
            // no fixed width — dynamic sizing will handle it but we DO want a stable height
            int height = crosshairCoordLabel.getFontMetrics(crosshairCoordLabel.getFont()).getHeight();
            crosshairCoordLabel.setPreferredSize(new Dimension(1, height));
        }
        return crosshairCoordLabel;
    }

    protected JToolBarToggleButton getPlotButton() {
        if (plotButton == null) {
            plotButton = new JToolBarToggleButton();
            plotButton.setName("PlotButton");
            plotButton.setToolTipText("Show plot(s)");
            plotButton.setText("");
            plotButton.setMaximumSize(new Dimension(28, 28));
            plotButton.setMinimumSize(new Dimension(28, 28));
            plotButton.setPreferredSize(new Dimension(28, 28));
            plotButton.setActionCommand("PlotPanelContainer");
            plotButton.setSelected(true);
            plotButton.setIcon(VCellIcons.dataExporterIcon);
        }
        return plotButton;
    }
    protected JToolBarToggleButton getDataButton() {
        if (dataButton == null) {
            dataButton = new JToolBarToggleButton();
            dataButton.setName("DataButton");
            dataButton.setToolTipText("Show data table");
            dataButton.setText("");
            dataButton.setMaximumSize(new Dimension(28, 28));
            dataButton.setMinimumSize(new Dimension(28, 28));
            dataButton.setPreferredSize(new Dimension(28, 28));
            dataButton.setActionCommand("DataPanelContainer");
            dataButton.setIcon(VCellIcons.dataSetsIcon);
        }
        return dataButton;
    }

    protected void initConnections() {
        // empty; override in the subclasses
    }

    public void setVisualizationBackground(Color color) {
        super.setBackground(color);
        getBottomRightPanel().setBackground(color);
        getBottomLabel().setBackground(color);
        getCrosshairCheckBox().setBackground(color);
        getCrosshairCoordLabel().setBackground(color);
        getLegendPanel().setBackground(color);
        getLegendContentPanel().setBackground(color);
        getCardPanel().setBackground(color);
        getDataPanelContainer().setBackground(color);
        getPlotPanelContainer().setBackground(color);
    }


    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {

    }

    // crosshair coordinates and coordinates label management
    public void updateCrosshairCoordinates(double xVal, double yVal) {
        String text = formatCoord(xVal) + ", " + formatCoord(yVal);
        getCrosshairCoordLabel().setText(text);
        adjustCoordLabelWidth(text);
    }
    public void clearCrosshairCoordinates() {
        getCrosshairCoordLabel().setText(emptyCoordText);
        adjustCoordLabelWidth(emptyCoordText);
    }
    private int lastCoordCharCount = emptyCoordText.length();
    private static final String emptyCoordText = "        ";   // enough spaces to reduce jitter when switching between no coord and coord
    private static final DecimalFormat sci = new DecimalFormat("0.000E0", DecimalFormatSymbols.getInstance(Locale.US));
    private static final DecimalFormat fix = new DecimalFormat("0.000", DecimalFormatSymbols.getInstance(Locale.US));
    private static String formatCoord(double v) {
        double av = Math.abs(v);
        return (av >= 0.001)
                ? fix.format(v)
                : sci.format(v);
    }
    private void adjustCoordLabelWidth(String text) {
        int charCount = text.length();
        // Only resize if the number of characters changed
        if (charCount == lastCoordCharCount) {
            return;
        }
        lastCoordCharCount = charCount;
        FontMetrics fm = getCrosshairCoordLabel().getFontMetrics(getCrosshairCoordLabel().getFont());
        int charWidth = fm.charWidth('8');   // good representative width
        int width = charWidth * charCount + 4;   // +4 px padding
        Dimension d = getCrosshairCoordLabel().getPreferredSize();
        getCrosshairCoordLabel().setPreferredSize(new Dimension(width, d.height));
        getCrosshairCoordLabel().revalidate();   // required so GridBagLayout recalculates layout
    }

}
