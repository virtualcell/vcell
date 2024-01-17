package cbit.vcell.client.data;

import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.resource.ResourceUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.gui.EditorScrollTable;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

public class ExportedDataViewer extends DocumentEditorSubPanel implements ActionListener, PropertyChangeListener {

    private JScrollPane scrollPane;

    public ExportedDataTableModel tableModel;
    private EditorScrollTable editorScrollTable;
    private JButton refresh;
    private JButton copyButton;

    private final static Logger lg = LogManager.getLogger(ExportedDataViewer.class);

    public ExportedDataViewer() {
        editorScrollTable = new EditorScrollTable();
        editorScrollTable.setDefaultEditor(Object.class, null);
        tableModel = new ExportedDataTableModel(editorScrollTable);

        editorScrollTable.setModel(tableModel);

        scrollPane = new JScrollPane(editorScrollTable);
        scrollPane.setSize(400, 400);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        scrollPane.setMinimumSize(new Dimension(400, 400));

        JLabel jLabel = new JLabel("Most recent exports. This list is volatile so save important metadata elsewhere.");
        refresh = new JButton("Refresh List");
        copyButton = new JButton("Copy Export Link");

        JPanel topBar = new JPanel();
        topBar.setLayout(new FlowLayout());
        refresh.addActionListener(this);
        copyButton.addActionListener(this);
        topBar.add(jLabel);
        topBar.add(copyButton);
        topBar.add(refresh);

        this.setLayout(new BorderLayout());
        this.add(topBar, BorderLayout.NORTH);
        this.add(scrollPane);
        initalizeTableData();
    }

    public void updateTableModel(){
        try{
            ExportDataRepresentation jsonData = getJsonData();
            if (jsonData != null){
                List<String> globalJobIDs = jsonData.globalJobIDs;
                String lastElement = tableModel.getRowCount() == 0 ? null: tableModel.tableData.get(tableModel.tableData.size() - 1).jobID;
                for(int i = globalJobIDs.size() - 1; i > -1; i--){
                    // first index is JobID, second is data format
                    String[] tokens = globalJobIDs.get(i).split(",");
                    if(lastElement != null && lastElement.equals(tokens[0])){
                        break;
                    }
                    addRowFromJson(tokens[0], tokens[1], jsonData);
                }
            }
            tableModel.refreshData();
        }
        catch (Exception e){
            lg.error("Failed Update Export Viewer Table Model:", e);
        }
    }

    /* Reason for this function is the for loop order, it matters when doing efficient updating by checking end of row. */
    public void initalizeTableData(){
        ExportDataRepresentation jsonData = getJsonData();
        if (jsonData != null){
            List<String> globalJobIDs = jsonData.globalJobIDs;
            for (String s : globalJobIDs) {
                String[] tokens = s.split(",");
                addRowFromJson(tokens[0], tokens[1], jsonData);
            }
        }
        tableModel.refreshData();
    }

    private void addRowFromJson(String jobID, String dataFormat, ExportDataRepresentation jsonData){
        ExportDataRepresentation.SimulationExportDataRepresentation simData = jsonData.formatData.get(dataFormat).simulationDataMap.get(jobID);
        ExportedDataTableModel.TableData newRow = new ExportedDataTableModel.TableData(
                simData.jobID, simData.dataID, simData.exportDate, dataFormat, simData.uri,
                simData.biomodelName, simData.startAndEndTime, simData.applicationName, simData.simulationName, simData.variables
        );
        tableModel.addRow(newRow);
    }

    public ExportDataRepresentation getJsonData(){
        try{
            File jsonFile = new File(ResourceUtil.getVcellHome(), ClientRequestManager.EXPORT_METADATA_FILENAME);
            String jsonString = new String(Files.readAllBytes(jsonFile.toPath()));
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
            updateTableModel();
        } else if (e.getSource().equals(copyButton)) {
            int row = editorScrollTable.getSelectedRow();
            ExportedDataTableModel.TableData tableData = tableModel.getRowData(row);

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
}
