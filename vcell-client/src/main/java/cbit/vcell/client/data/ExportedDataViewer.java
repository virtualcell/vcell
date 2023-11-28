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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;

public class ExportedDataViewer extends DocumentEditorSubPanel implements ActionListener, PropertyChangeListener {

    private JScrollPane scrollPane;
    private JTable exportLinksTable;

    public ExportedDataViewer() {
//        corePanel.setLayout(new GridBagLayout()); // You can set a layout manager for the panel

        String[] columns = {"Sim Name", "BioModel Name", "Sim ID", "Date Exported", "Date Sim Modified", "Format", "Link"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        tableModel.addRow(new Object[]{"Test Sim", "Bio Name", "1720480", "123", "123", "N5", "https://www.google.com"});
        exportLinksTable = new JTable(tableModel);
        exportLinksTable.setDefaultEditor(Object.class, null);
        scrollPane = new JScrollPane(exportLinksTable);
        scrollPane.setSize(400, 400);
        scrollPane.setPreferredSize(new Dimension(400, 400));
        scrollPane.setMinimumSize(new Dimension(400, 400));

        this.setLayout(new BorderLayout());
        this.add(scrollPane);

//        corePanel.setVisible(true);
//        corePanel.setSize(400, 400);

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
//        JFrame jFrame = new JFrame();
//        jFrame.add(exportedDataViewer.scrollPane);
//        jFrame.setSize(400, 400);
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
}
