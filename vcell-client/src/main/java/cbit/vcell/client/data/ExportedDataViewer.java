package cbit.vcell.client.data;

import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.desktop.biomodel.ApplicationComponents;
import cbit.vcell.client.desktop.biomodel.DocumentEditor;
import cbit.vcell.client.desktop.biomodel.DocumentEditorSubPanel;
import cbit.vcell.client.desktop.simulation.SimulationWindow;
import cbit.vcell.client.server.ConnectionStatus;
import cbit.vcell.client.server.SimStatusEvent;
import cbit.vcell.solver.VCSimulationIdentifier;
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
import java.util.Enumeration;

public class ExportedDataViewer extends DocumentEditorSubPanel implements ActionListener, PropertyChangeListener,MouseListener {

    private JScrollPane scrollPane;

    public ExportedDataTableModel tableModel;
    private EditorScrollTable editorScrollTable;
    private JButton refresh;

    public ExportedDataViewer() {
        editorScrollTable = new EditorScrollTable();
        editorScrollTable.setDefaultEditor(Object.class, null);
        editorScrollTable.addMouseListener(this);
        tableModel = new ExportedDataTableModel(editorScrollTable);

        tableModel.addRow(new ExportedDataTableModel.ExportMetaData("Sim Name", "BioModel", "123", "10/2/3","123", "N5", "https://google.com"));
        tableModel.addRow(new ExportedDataTableModel.ExportMetaData("Aim Name", "CioModel", "923", "11/2/3","123", "N5", "https://google.com"));
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
