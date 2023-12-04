package cbit.vcell.client.data;

import cbit.vcell.client.ClientRequestManager;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.desktop.biomodel.ApplicationComponents;
import cbit.vcell.client.desktop.biomodel.DocumentEditor;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.server.SimStatusEvent;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solver.VCSimulationIdentifier;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.document.VCDocument;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;
import org.vcell.util.gui.EditorScrollTable;
import scala.util.parsing.combinator.testing.Str;

import javax.swing.*;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.List;

public class ExportedDataViewer extends DocumentEditorSubPanel implements ActionListener, PropertyChangeListener,MouseListener {

    private JScrollPane scrollPane;

    public ExportedDataTableModel tableModel;
    private EditorScrollTable editorScrollTable;
    private JButton refresh;

    private final static Logger lg = LogManager.getLogger(ExportedDataViewer.class);

    public ExportedDataViewer() {
        editorScrollTable = new EditorScrollTable();
        editorScrollTable.setDefaultEditor(Object.class, null);
        editorScrollTable.addMouseListener(this);
        tableModel = new ExportedDataTableModel(editorScrollTable);

        editorScrollTable.setModel(tableModel);

        scrollPane = new JScrollPane(editorScrollTable);
        scrollPane.setSize(400, 400);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        scrollPane.setMinimumSize(new Dimension(400, 400));

        JLabel jLabel = new JLabel("Click on a cell to copy it's contents to your clipboard.");
        refresh = new JButton("Refresh List");

        JPanel topBar = new JPanel();
        topBar.setLayout(new FlowLayout());
        refresh.addActionListener(this);
        topBar.add(jLabel);
        topBar.add(refresh);

        this.setLayout(new BorderLayout());
        this.add(topBar, BorderLayout.NORTH);
        this.add(scrollPane);
        initalizeTableData();
    }

    public void updateTableModel(){
        try{
            HashMap <String, Object> jsonData = getJsonData();
            if (jsonData != null){
                List<String> set = (ArrayList<String>) jsonData.get("jobIDs");
                String lastElement = tableModel.getRowCount() == 0 ? null: tableModel.exportMetaData.get(tableModel.exportMetaData.size() - 1).jobID;
                for(int i = set.size() - 1; i > -1; i--){
                    if(lastElement != null && lastElement.equals(set.get(i))){
                        break;
                    }
                    addRowFromJson(jsonData, set.get(i));
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
        HashMap<String, Object> jsonData = getJsonData();
        if (jsonData != null){
            List<String> set = (ArrayList<String>) jsonData.get("jobIDs");
            for (String s : set) {
                addRowFromJson(jsonData, s);
            }
        }
        tableModel.refreshData();
    }

    private void addRowFromJson(HashMap<String, Object> jsonData, String s){
        Map<String, String> addedRow = (Map<String, String>) jsonData.get(s);
        ExportedDataTableModel.ExportMetaData newRow = new ExportedDataTableModel.ExportMetaData(addedRow.get("jobID"), addedRow.get("dataID"), addedRow.get("exportDate"), addedRow.get("format"), addedRow.get("uri"));
        tableModel.addRow(newRow);
    }

    public HashMap<String, Object> getJsonData(){
        try{
            File jsonFile = new File(ResourceUtil.getVcellHome(), ClientRequestManager.EXPORT_METADATA_FILENAME);
            if (jsonFile.exists() && jsonFile.length() != 0){
                HashMap<String, Object> jsonHashMap;
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Type type = new TypeToken<HashMap<String, Object>>() {}.getType();
                jsonHashMap = gson.fromJson(new FileReader(jsonFile.getAbsolutePath()), type);
                return jsonHashMap;
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
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }

    @Override
    protected void onSelectedObjectsChange(Object[] selectedObjects) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = editorScrollTable.getSelectedRow();
        int column = editorScrollTable.getSelectedColumn();

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(new StringSelection( (String) editorScrollTable.getValueAt(row, column)), null);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
