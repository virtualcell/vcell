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

        tableModel.addRow(new ExportedDataTableModel.ExportMetaData("32489984", "432789", "10/2/3", "N5","http://google.com"));
//        tableModel.addRow(new ExportedDataTableModel.ExportMetaData("Aim Name", "CioModel", "923", "11/2/3","123", "N5", "https://google.com"));
        editorScrollTable.setModel(tableModel);

        scrollPane = new JScrollPane(editorScrollTable);
        scrollPane.setSize(400, 400);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        scrollPane.setMinimumSize(new Dimension(400, 400));

        JLabel jLabel = new JLabel("Click on a cell to copy it's contents to your clipboard.");
        refresh = new JButton("Refresh List");

        JPanel topBar = new JPanel();
        topBar.setLayout(new FlowLayout());
        topBar.add(jLabel);
        topBar.add(refresh);

        this.setLayout(new BorderLayout());
        this.add(topBar, BorderLayout.NORTH);
        this.add(scrollPane);
        updateTableModel();
    }

    public void updateTableModel(){
        try{
            File jsonFile = new File(ResourceUtil.getVcellHome(), ClientRequestManager.EXPORT_METADATA_FILENAME);
            if (jsonFile.exists() && jsonFile.length() != 0){
                LinkedHashMap<String, HashMap<String, String>> jsonHashMap;
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Type type = new TypeToken<LinkedHashMap<String, HashMap<String, String>>>() {}.getType();
                jsonHashMap = gson.fromJson(new FileReader(jsonFile.getAbsolutePath()), type);

                List<String> set = jsonHashMap.keySet().stream().toList();
                ExportedDataTableModel.ExportMetaData lastElement = tableModel.exportMetaData.get(tableModel.exportMetaData.size() - 1);
                for(int i = set.size() - 1; i > -1; i--){
                    if(lastElement.jobID.equals(set.get(i))){
                        break;
                    }
                    HashMap<String, String> addedRow = jsonHashMap.get(set.get(i));
                    ExportedDataTableModel.ExportMetaData newRow = new ExportedDataTableModel.ExportMetaData(addedRow.get("jobID"), addedRow.get("dataID"), addedRow.get("exportDate"), addedRow.get("format"), addedRow.get("uri"));
                    tableModel.addRow(newRow);
                }
            }
            tableModel.refreshData();
        }
        catch (Exception e){
            lg.error("Failed Update Export Viewer Table Model:", e);
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
