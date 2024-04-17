package cbit.vcell.client.data;

import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.export.server.ExportFormat;
import cbit.vcell.graph.GraphConstants;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.resource.ResourceUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.EditorScrollTable;
import org.vcell.util.gui.VCellIcons;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExportedDataViewer extends DocumentEditorSubPanel implements ActionListener, PropertyChangeListener, ListSelectionListener, KeyListener {

    public ExportedDataTableModel tableModel;
    private final EditorScrollTable editorScrollTable;
    private EditorScrollTable parameterScrollTable = new EditorScrollTable();
    private ParameterTableModelExport parameterScrollTableModel;
    private JButton refreshButton;
    private JButton copyButton;
    private JButton deleteButton;

    private final JRadioButton todayInterval = new JRadioButton("Past 24 hours");
    private final JRadioButton monthInterval = new JRadioButton("Past Month");
    private final JRadioButton yearlyInterval = new JRadioButton("Past Year");
    private final JRadioButton anyInterval = new JRadioButton("Any Time");
    private final ButtonGroup timeButtonGroup = new ButtonGroup();

    private final ArrayList<JCheckBox> formatButtonGroup = new ArrayList<>();



    private JTextPane exportVariableText;
    private JTextField searchField;

    private final Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);


    private final static Logger lg = LogManager.getLogger(ExportedDataViewer.class);

    public ExportedDataViewer() {    // =============== middle panel ( with the table, Search sub-panel and Copy / Delete )
        int tableHeight = 300;
        int tableWidth = 400;

        editorScrollTable = new EditorScrollTable();
        editorScrollTable.setDefaultEditor(Object.class, null);
        tableModel = new ExportedDataTableModel(editorScrollTable);

        editorScrollTable.setModel(tableModel);
        editorScrollTable.setRowHeight(25);

        JScrollPane tableScrollPane = new JScrollPane(editorScrollTable);
        tableScrollPane.setSize(tableWidth, tableHeight);
        tableScrollPane.setPreferredSize(new Dimension(tableWidth, tableHeight));
        tableScrollPane.setMinimumSize(new Dimension(tableWidth, tableHeight));
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(loweredEtchedBorder, " Export Table "));

        DefaultScrollTableCellRenderer applicationCellRender = new DefaultScrollTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setText((String)value);
                if (table.getModel() instanceof ExportedDataTableModel) {
                    ExportedDataTableModel.TableData rowData = ((ExportedDataTableModel) table.getModel()).getValueAt(row);
                    if(rowData != null && rowData.applicationType != null) {
                        SimulationContext.Application application = SimulationContext.Application.valueOf(rowData.applicationType);
                        switch (application) {
                            case NETWORK_DETERMINISTIC:
                                setIcon(rowData.nonSpatial ? VCellIcons.appDetNonspIcon : VCellIcons.appDetSpatialIcon);
                                break;
                            case NETWORK_STOCHASTIC:
                                setIcon(rowData.nonSpatial ? VCellIcons.appStoNonspIcon : VCellIcons.appStoSpatialIcon);
                                break;
                            case SPRINGSALAD:
                                setIcon(rowData.nonSpatial ? null : VCellIcons.appSpringSaLaDSpatialIcon);
                                break;
                            case RULE_BASED_STOCHASTIC:
                                setIcon(VCellIcons.appRbmNonspIcon);
                                break;
                    }
//                        setToolTipText(application.getDescription());
                        setHorizontalTextPosition(SwingConstants.RIGHT);
                    }
                }
                return this;
            }
        };
        editorScrollTable.getSelectionModel().addListSelectionListener(this);
        editorScrollTable.getColumnModel().getColumn(1).setCellRenderer(applicationCellRender);

        JPanel searchPane = new JPanel();
        searchPane.setLayout(new GridBagLayout());

        copyButton = new JButton("Copy Link");
        copyButton.setEnabled(false);
        copyButton.setToolTipText("Select a table row and press this button to copy the file location to Clipboard");
        copyButton.addActionListener(this);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
//        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(1, 4, 1, 4);
        searchPane.add(copyButton, gbc);

        deleteButton = new JButton("Delete Export");
        deleteButton.setEnabled(false);
        deleteButton.setToolTipText("Select a table row and press the this button to delete the exported file");
        deleteButton.addActionListener(this);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(1, 4, 1, 4);
        searchPane.add(deleteButton, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1, 4, 1, 4);
        searchPane.add(new JSeparator(SwingConstants.VERTICAL), gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1, 4, 1, 2);
        searchPane.add(new JLabel("Search"), gbc);

        searchField = new JTextField();
        searchField.setEnabled(true);
        searchField.addKeyListener(this);
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1, 2, 1, 2);
        searchPane.add(searchField, gbc);

        JPanel exportPane = new JPanel();       // --------------------------------------------------
        exportPane.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1, 1, 1, 1);
        exportPane.add(tableScrollPane, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
//        gbc.weightx = 1;
//        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(1, 1, 1, 1);
        exportPane.add(searchPane, gbc);


        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, exportPane, exportDetailsPane());
        splitPane.setContinuousLayout(true);
        this.setLayout(new BorderLayout());
        this.add(userOptionsPane(), BorderLayout.NORTH);
        add(splitPane);

        todayInterval.addActionListener(this);
        monthInterval.addActionListener(this);
        yearlyInterval.addActionListener(this);
        anyInterval.addActionListener(this);

        initalizeTableData();
    }

    private JSplitPane exportDetailsPane(){     // ========================== bottom panel ( Export Details panel )
        int height = 200;
        int width = 140;
        exportVariableText = new JTextPane();
        exportVariableText.setEditable(false);
        exportVariableText.setBackground(UIManager.getColor("TextField.inactiveBackground"));

        parameterScrollTableModel = new ParameterTableModelExport(parameterScrollTable);
        parameterScrollTable.setModel(parameterScrollTableModel);

        JScrollPane parameterScrollPane = new JScrollPane(parameterScrollTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        parameterScrollPane.setPreferredSize(new Dimension(width, height));
        parameterScrollPane.setSize(new Dimension(width, height));
        parameterScrollPane.setMaximumSize(new Dimension(width, height));
        parameterScrollPane.setBorder(BorderFactory.createTitledBorder(loweredEtchedBorder, " Parameters "));

        JScrollPane exportDetails = new JScrollPane(exportVariableText, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        exportDetails.setPreferredSize(new Dimension(width, height));
        exportDetails.setSize(new Dimension(width, height));
        exportDetails.setBorder(BorderFactory.createTitledBorder(loweredEtchedBorder, " Properties "));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, exportDetails, parameterScrollPane);
        splitPane.setContinuousLayout(true);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(BorderFactory.createTitledBorder(loweredEtchedBorder, " Export Details "));
        return splitPane;
    }

    private JPanel userOptionsPane(){       // ========================== top panel (user options)
        refreshButton = new JButton("Refresh");

        JPanel timeIntervalSelector = new JPanel();         // ---------- Time Interval sub-panel
        timeIntervalSelector.setBorder(BorderFactory.createTitledBorder(loweredEtchedBorder, " Time Interval "));
        timeIntervalSelector.setLayout(new BoxLayout(timeIntervalSelector, BoxLayout.X_AXIS));

        timeButtonGroup.add(todayInterval);
        timeButtonGroup.add(monthInterval);
        timeButtonGroup.add(yearlyInterval);
        timeButtonGroup.add(anyInterval);
        timeButtonGroup.setSelected(anyInterval.getModel(), true);

        timeIntervalSelector.add(todayInterval);
        timeIntervalSelector.add(monthInterval);
        timeIntervalSelector.add(yearlyInterval);
        timeIntervalSelector.add(anyInterval);

        JPanel exportFormatFilterPanel = new JPanel();      // ------------- Export Type sub-panel
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        exportFormatFilterPanel.setBorder(BorderFactory.createTitledBorder(loweredEtchedBorder, " Export Type "));
        exportFormatFilterPanel.setLayout(new GridBagLayout());
        Set<String> exportNames = new HashSet<>();
        GridBagConstraints gbc = new GridBagConstraints();
        for (ExportFormat format : ExportFormat.values()) {
            String properFormatName = format.name().contains("_") ? format.name().split("_")[1] : format.name();
            if(!exportNames.contains(properFormatName)){
                exportNames.add(properFormatName);
                JCheckBox formatButton = new JCheckBox(properFormatName);
                formatButton.addActionListener(this);
                formatButton.setPreferredSize(new Dimension(120, 10));
                formatButton.setSelected(true);
                exportFormatFilterPanel.add(formatButton);
                formatButtonGroup.add(formatButton);
            }
        }

        JPanel topBar = new JPanel();
        TitledBorder titleOptions = BorderFactory.createTitledBorder(loweredEtchedBorder, " User Options ");
        titleOptions.setTitleJustification(TitledBorder.LEFT);
        titleOptions.setTitlePosition(TitledBorder.TOP);
        topBar.setBorder(titleOptions);

        topBar.setLayout(new GridBagLayout());

        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(1, 1, 5, 1);
        topBar.add(exportFormatFilterPanel, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = new Insets(1, 1, 1, 1);
        topBar.add(timeIntervalSelector, gbc);

        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1, 1, 5, 1);
        topBar.add(new JLabel(), gbc);      // fake element for filling any empty space

        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(9, 1, 7, 4);
//        topBar.add(refreshButton, gbc);
//        refreshButton.addActionListener(this);
        return topBar;
    }

    /* Reason for this function is the for loop order, it matters when doing efficient updating by checking end of row. */
    public void initalizeTableData(){
        ExportDataRepresentation jsonData = getJsonData();
        tableModel.resetData();
        if (jsonData != null){
            Stack<String> globalJobIDs = jsonData.globalJobIDs;
            LocalDateTime pastTime = LocalDateTime.now();
            if (todayInterval.isSelected()){
                pastTime = pastTime.minusDays(1);
            } else if (monthInterval.isSelected()) {
                pastTime = pastTime.minusMonths(1);
            } else if (yearlyInterval.isSelected()) {
                pastTime = pastTime.minusYears(1);
            } else {
                pastTime = pastTime.minusYears(10); //Max date back is 10 years
            }

            Set<String> allowedFormats = new HashSet<>();
            for (JCheckBox button : formatButtonGroup){
                if(button.isSelected()){
                    allowedFormats.add(button.getText());
                }
            }

            while(!globalJobIDs.empty()) {
                String[] tokens = globalJobIDs.pop().split(",");
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime exportDate = LocalDateTime.parse(jsonData.formatData.get(tokens[1]).simulationDataMap.get(tokens[0]).exportDate, dateFormat);
                if (exportDate.isBefore(pastTime)){
                    break;
                }
                if (allowedFormats.contains(tokens[1])){
                    addRowFromJson(tokens[0], tokens[1], jsonData);
                }
            }
        }
        tableModel.refreshData();
    }

    private void addRowFromJson(String jobID, String dataFormat, ExportDataRepresentation jsonData){
        ExportDataRepresentation.SimulationExportDataRepresentation simData = jsonData.formatData.get(dataFormat).simulationDataMap.get(jobID);
        ExportedDataTableModel.TableData newRow = new ExportedDataTableModel.TableData(
                simData.jobID, simData.dataID, simData.exportDate, dataFormat, simData.uri, simData.savedFileName,
                simData.biomodelName, simData.startAndEndTime, simData.applicationName, simData.simulationName, simData.variables,
                simData.differentParameterValues, simData.nonSpatial, simData.applicationType
        );
        tableModel.addRow(newRow);
    }

    public ExportDataRepresentation getJsonData(){
        try{
            File jsonFile = new File(ResourceUtil.getVcellHome(), ClientRequestManager.EXPORT_METADATA_FILENAME);
            String jsonString = jsonFile.exists() ? new String(Files.readAllBytes(jsonFile.toPath())) : "";
            if (jsonFile.exists() && !jsonString.isEmpty()){
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                return gson.fromJson(jsonString, ExportDataRepresentation.class);
            }
            return null;
        }
        catch (Exception e){
            lg.error("Failed to read export metadata JSON:", e);
            return null;
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(refreshButton)){
            initalizeTableData();
        } else if (e.getSource().equals(copyButton)) {
            int row = editorScrollTable.getSelectedRow();
            ExportedDataTableModel.TableData tableData = tableModel.getValueAt(row);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(tableData.link), null);
        } else if(e.getSource().equals(deleteButton)) {
            // TODO: add delete code here

        } else if(e.getSource() instanceof JCheckBox && formatButtonGroup.contains(e.getSource())) {
            initalizeTableData();
        } else if(e.getSource().equals(todayInterval)) {
            initalizeTableData();
        } else if(e.getSource().equals(monthInterval)) {
            initalizeTableData();
        } else if(e.getSource().equals(yearlyInterval)) {
            initalizeTableData();
        } else if(e.getSource().equals(anyInterval)) {
            initalizeTableData();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        int row = editorScrollTable.getSelectedRow();
        ExportedDataTableModel.TableData rowData = tableModel.getValueAt(row);
        copyButton.setEnabled(rowData == null ? false : true);
        deleteButton.setEnabled(rowData == null ? false : true);
        if(rowData == null) {
            parameterScrollTableModel.resetData();
            parameterScrollTableModel.refreshData();
            exportVariableText.setText("");
            return;
        }
        ArrayList<String> differentParameterValues = rowData.differentParameterValues;
        StyleContext styleContext = new StyleContext();
//        AttributeSet attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.)
        StyledDocument doc = new DefaultStyledDocument();
        addColoredText(doc, "\nVariables List: ", GraphConstants.darkgreen);
        addColoredText(doc, rowData.variables, Color.BLACK);
        addColoredText(doc, "\n\nSimulation ID: ", GraphConstants.darkgreen);
        addColoredText(doc, rowData.simID, Color.BLACK);

        if(rowData.name != null && !rowData.name.isEmpty()) {
            addColoredText(doc, "\nName: ", GraphConstants.darkgreen);
            addColoredText(doc, rowData.name, Color.BLACK);
        }
        if(rowData.link != null && !rowData.link.isEmpty() && rowData.link.contains(ExportedDataTableModel.prefix)) {
            addColoredText(doc, "\nLink: ", GraphConstants.darkgreen);
            addColoredText(doc, rowData.link, Color.BLACK);
        }

        parameterScrollTableModel.resetData();
        for(int i =0; i < differentParameterValues.size(); i++){
            String[] parameterTokens = differentParameterValues.get(i).split(":");
            ParameterTableModelExport.ParameterTableData data = new ParameterTableModelExport.ParameterTableData(
                    parameterTokens[0], parameterTokens[1], parameterTokens[2]
            );
            parameterScrollTableModel.addRow(data);
        }
        parameterScrollTableModel.refreshData();
        exportVariableText.setStyledDocument(doc);
        exportVariableText.setOpaque(false);    // we still want the background we chose (TextField.inactiveBackground)
        exportVariableText.updateUI();
    }
    private void addColoredText(StyledDocument doc, String text, Color color) {
        Style style = doc.addStyle("ColoredStyle", null);
        StyleConstants.setForeground(style, color);
        try {
            doc.insertString(doc.getLength(), text, style);
        } catch (BadLocationException e) {
            lg.error(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getSource().equals(searchField)) {
            String searchText = searchField.getText();
            System.out.println(searchText);
            tableModel.setSearchText(searchText);
            tableModel.refreshData();
        }
    }
}
