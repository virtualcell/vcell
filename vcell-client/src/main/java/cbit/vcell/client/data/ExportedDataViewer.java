package cbit.vcell.client.data;

import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
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

public class ExportedDataViewer extends DocumentEditorSubPanel implements ActionListener, PropertyChangeListener, ListSelectionListener {

    public ExportedDataTableModel tableModel;
    private final EditorScrollTable editorScrollTable;
    private EditorScrollTable parameterScrollTable = new EditorScrollTable();
    private ParameterTableModelExport parameterScrollTableModel;
    private JButton refresh;
    private JButton copyButton;

    private final JRadioButton todayInterval = new JRadioButton("Past 24 hours");
    private final JRadioButton monthInterval = new JRadioButton("Past Month");
    private final JRadioButton yearlyInterval = new JRadioButton("Past Year");
    private final JRadioButton anyInterval = new JRadioButton("Any Time");
    private final ButtonGroup timeButtonGroup = new ButtonGroup();


    private JTextPane exportVariableText;
    private final Border loweredEtchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);


    private final static Logger lg = LogManager.getLogger(ExportedDataViewer.class);

    public ExportedDataViewer() {
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
                        //setText("");
//                        setToolTipText(application.getDescription());
                        setHorizontalTextPosition(SwingConstants.RIGHT);
                    }
                }
                return this;
            }
        };



        editorScrollTable.getSelectionModel().addListSelectionListener(this);
        editorScrollTable.getColumnModel().getColumn(1).setCellRenderer(applicationCellRender);


        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tableScrollPane, exportDetailsPane());
        splitPane.setContinuousLayout(true);
        this.setLayout(new BorderLayout());
        this.add(userOptionsPane(), BorderLayout.NORTH);
        add(splitPane);
//        this.add(tableScrollPane, BorderLayout.CENTER);
//        this.add(parameterScrollPane, BorderLayout.SOUTH);
        initalizeTableData();
    }

    private JSplitPane exportDetailsPane(){
        int height = 200;
        int width = 140;
        exportVariableText = new JTextPane();
        exportVariableText.setEditable(false);

        parameterScrollTableModel = new ParameterTableModelExport(parameterScrollTable);
        parameterScrollTable.setModel(parameterScrollTableModel);

        JScrollPane parameterScrollPane = new JScrollPane(parameterScrollTable, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        parameterScrollPane.setPreferredSize(new Dimension(width, height));
        parameterScrollPane.setSize(new Dimension(width, height));
        parameterScrollPane.setBorder(BorderFactory.createTitledBorder(loweredEtchedBorder, " Parameters "));
//
//        parameterScrollTable.setModel(new ParameterTableModelExport(parameterScrollTable));
//        parameterScrollTable.setPreferredSize(new Dimension(width, height));


        JScrollPane exportDetails = new JScrollPane(exportVariableText, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        exportDetails.setPreferredSize(new Dimension(width, height));
        exportDetails.setSize(new Dimension(width, height));
        exportDetails.setBorder(BorderFactory.createTitledBorder(loweredEtchedBorder, " Variables "));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, exportVariableText, parameterScrollPane);
        splitPane.setContinuousLayout(true);
        splitPane.setBorder(BorderFactory.createTitledBorder(loweredEtchedBorder, " Export Details "));
        return splitPane;
    }

    private JPanel userOptionsPane(){
        refresh = new JButton("Refresh");
        copyButton = new JButton("Copy Export Link");

        JPanel timeIntervalSelector = new JPanel();
        timeIntervalSelector.setBorder(BorderFactory.createTitledBorder(loweredEtchedBorder, " Time Interval "));
        timeIntervalSelector.setLayout(new BoxLayout(timeIntervalSelector, BoxLayout.Y_AXIS));

        timeButtonGroup.add(todayInterval);
        timeButtonGroup.add(monthInterval);
        timeButtonGroup.add(yearlyInterval);
        timeButtonGroup.add(anyInterval);

        timeIntervalSelector.add(todayInterval);
        timeIntervalSelector.add(monthInterval);
        timeIntervalSelector.add(yearlyInterval);
        timeIntervalSelector.add(anyInterval);

        JPanel topBar = new JPanel();
        topBar.setLayout(new FlowLayout());
        refresh.addActionListener(this);
        copyButton.addActionListener(this);
        topBar.add(timeIntervalSelector);
        topBar.add(copyButton);
        topBar.add(refresh);

        topBar.setBorder(BorderFactory.createTitledBorder(loweredEtchedBorder, " User Options "));
        topBar.setPreferredSize(new Dimension(400, 150));

        return topBar;
    }

//    public void updateTableModel(){
//        try{
//            ExportDataRepresentation jsonData = getJsonData();
//            if (jsonData != null){
//                List<String> globalJobIDs = jsonData.globalJobIDs;
//                String lastElement = tableModel.getRowCount() == 0 ? null: tableModel.tableData.get(tableModel.tableData.size() - 1).jobID;
//                for(int i = globalJobIDs.size() - 1; i > -1; i--){
//                    // first index is JobID, second is data format
//                    String[] tokens = globalJobIDs.get(i).split(",");
//                    if(lastElement != null && lastElement.equals(tokens[0])){
//                        break;
//                    }
//                    addRowFromJson(tokens[0], tokens[1], jsonData);
//                }
//            }
//            tableModel.refreshData();
//        }
//        catch (Exception e){
//            lg.error("Failed Update Export Viewer Table Model:", e);
//        }
//    }

    /* Reason for this function is the for loop order, it matters when doing efficient updating by checking end of row. */
    public void initalizeTableData(){
        ExportDataRepresentation jsonData = getJsonData();
        tableModel.resetData();
        if (jsonData != null){
            Stack<String> globalJobIDs = jsonData.globalJobIDs;
            ButtonModel buttonModel = timeButtonGroup.getSelection();
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

            while(!globalJobIDs.empty()) {
                String[] tokens = globalJobIDs.pop().split(",");
                DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime exportDate = LocalDateTime.parse(jsonData.formatData.get(tokens[1]).simulationDataMap.get(tokens[0]).exportDate, dateFormat);
                if (exportDate.isBefore(pastTime)){
                    break;
                }
                addRowFromJson(tokens[0], tokens[1], jsonData);
            }
        }
        tableModel.refreshData();
    }

    private void addRowFromJson(String jobID, String dataFormat, ExportDataRepresentation jsonData){
        ExportDataRepresentation.SimulationExportDataRepresentation simData = jsonData.formatData.get(dataFormat).simulationDataMap.get(jobID);
        ExportedDataTableModel.TableData newRow = new ExportedDataTableModel.TableData(
                simData.jobID, simData.dataID, simData.exportDate, dataFormat, simData.uri,
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

    public static void main(String[] args) {
        ExportedDataViewer exportedDataViewer = new ExportedDataViewer();
        exportedDataViewer.setPreferredSize(new Dimension(1000, 600));

        JDialog viewSpeciesDialog = new JDialog();
        JFrame jFrame = new JFrame();

        JOptionPane pane = new JOptionPane(exportedDataViewer, JOptionPane.PLAIN_MESSAGE, 0, null, new Object[] {"Close"});
        viewSpeciesDialog = pane.createDialog(jFrame, "View Exported Data");
        viewSpeciesDialog.setModal(false);
        viewSpeciesDialog.setResizable(true);
        viewSpeciesDialog.setVisible(true);
        jFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(refresh)){
            initalizeTableData();
        } else if (e.getSource().equals(copyButton)) {
            int row = editorScrollTable.getSelectedRow();
            ExportedDataTableModel.TableData tableData = tableModel.getValueAt(row);

            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(new StringSelection(tableData.link), null);
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
        ArrayList<String> differentParameterValues = rowData.differentParameterValues;
        ArrayList<String> actualParameterValues = rowData.setParameters;
        StyleContext styleContext = new StyleContext();
//        AttributeSet attributeSet = styleContext.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.)
        StyledDocument doc = new DefaultStyledDocument();
        addColoredText(doc, "\nVARIABLES:  ", Color.GREEN);
        addColoredText(doc, rowData.variables, Color.BLACK);


        parameterScrollTableModel.resetData();
        for(int i =0; i < differentParameterValues.size(); i++){
            String[] parameterTokens = differentParameterValues.get(i).split(":");
            String[] tokensSet = actualParameterValues.get(i).split(":");
            ParameterTableModelExport.ParameterTableData data = new ParameterTableModelExport.ParameterTableData(
                    parameterTokens[0], parameterTokens[1], parameterTokens[2]
            );
            parameterScrollTableModel.addRow(data);
        }
        parameterScrollTableModel.refreshData();
//        exportDetails.setText(
//                rowData.variables +
//                "\n \nSet Parameter Values: " + actualParameterValues +
//                "\n \nDefault Parameter Values: " + defaultParameterValues);
        exportVariableText.setStyledDocument(doc);
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
}
