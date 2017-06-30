package org.vcell;

import net.imagej.Dataset;
import net.miginfocom.layout.AC;
import org.scijava.Context;
import org.scijava.display.Display;
import org.scijava.ui.UserInterface;
import org.scijava.ui.swing.viewer.SwingDisplayPanel;
import org.scijava.ui.swing.viewer.SwingDisplayWindow;
import org.scijava.ui.viewer.AbstractDisplayViewer;
import org.scijava.ui.viewer.DisplayWindow;
import sun.jvm.hotspot.debugger.cdbg.DoubleType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by kevingaffney on 6/26/17.
 */
public class MainView extends SwingDisplayWindow {

    private JTabbedPane tabbedPane;
    private JLabel lblImageDescription;
    private JLabel lblProjectDescription;

    // Menu items under "File"
    private JMenuItem mniNew;
    private JMenuItem mniOpen;
    private JMenuItem mniSave;
    private JMenuItem mniSaveAs;
    private JMenuItem mniImportData;
    private JMenuItem mniImportGeometry;
    private JMenuItem mniImportResults;
    private JMenuItem mniExport;

    // Menu items under "Edit"
    private JMenuItem mniDelete;

    // Menu items under "Experiments"
    private JMenuItem mniConstructTIRFGeometry;

    // Panels of datasets
    private DatasetListPanel pnlData;
    private DatasetListPanel pnlGeometry;
    private DatasetListPanel pnlResults;

    // Display button
    private JButton btnDisplay;

    private Context context;

    public MainView(VCellModel model, Context context) {
        this.context = context;
        initializeContentPane(model);
        initializeMenuBar();
        registerModelChangeListener(model);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    private void initializeContentPane(VCellModel model) {

        JPanel contentPane = new JPanel();

        BorderLayout borderLayout = new BorderLayout();
        contentPane.setLayout(borderLayout);

        // Project label initialization
        lblProjectDescription = new JLabel("", SwingConstants.CENTER);
        contentPane.add(lblProjectDescription, BorderLayout.PAGE_START);

        // Tabbed pane initialization
        VCellProject project = model.getVCellProject();
        if (project != null) {
            pnlData = new DatasetListPanel(project.getExperimentalDatasets());
            pnlGeometry = new DatasetListPanel(project.getGeometryDefinitions());
            pnlResults = new DatasetListPanel(project.getvCellResults());
        } else {
            pnlData = new DatasetListPanel(new ArrayList<>());
            pnlGeometry = new DatasetListPanel(new ArrayList<>());
            pnlResults = new DatasetListPanel(new ArrayList<>());
        }

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Data", pnlData);
        tabbedPane.addTab("Geometry", pnlGeometry);
        tabbedPane.addTab("Results", pnlResults);
        tabbedPane.setPreferredSize(new Dimension(300, 300));
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        // Display button initialization
        btnDisplay = new JButton("Display");
        contentPane.add(btnDisplay, BorderLayout.PAGE_END);

        setContentPane(contentPane);
    }

    private void initializeMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu mnuFile = new JMenu("File");

        mniNew = new JMenuItem("New project...");
        mniOpen = new JMenuItem("Open...");
        mniSave = new JMenuItem("Save");
        mniSaveAs = new JMenuItem("Save as...");

        mnuFile.add(mniNew);
        mnuFile.add(mniOpen);
        mnuFile.add(mniSave);
        mnuFile.add(mniSaveAs);

        JMenu mnuImport = new JMenu("Import");

        mniImportData = new JMenuItem("Experimental data...");
        mniImportGeometry = new JMenuItem("Geometry definition...");
        mniImportResults = new JMenuItem("VCell results...");
        mnuImport.add(mniImportData);
        mnuImport.add(mniImportGeometry);
        mnuImport.add(mniImportResults);
        mnuFile.add(mnuImport);

        mniExport = new JMenuItem("Export...");
        mnuFile.add(mniExport);

        // Delete menu
        JMenu mnuEdit = new JMenu("Edit");
        mniDelete = new JMenuItem("Delete...");
        mnuEdit.add(mniDelete);

        // Experiments menu
        JMenu mnuExperiments = new JMenu("Experiments");

        JMenu mnuTIRF = new JMenu("TIRF");
        mniConstructTIRFGeometry = new JMenuItem("Construct TIRF geometry...");
        mnuTIRF.add(mniConstructTIRFGeometry);
        mnuExperiments.add(mnuTIRF);

        menuBar.add(mnuFile);
        menuBar.add(mnuEdit);
        menuBar.add(mnuExperiments);
        setJMenuBar(menuBar);

        setProjectDependentMenuItemsEnabled(false);
    }

    public void setProjectDependentMenuItemsEnabled(boolean enabled) {
        mniSave.setEnabled(enabled);
        mniSaveAs.setEnabled(enabled);
        mniImportData.setEnabled(enabled);
        mniImportGeometry.setEnabled(enabled);
        mniImportResults.setEnabled(enabled);
        mniExport.setEnabled(enabled);
        mniDelete.setEnabled(enabled);
        mniConstructTIRFGeometry.setEnabled(enabled);
    }

    public Dataset getSelectedDataset() {
        DatasetListPanel selectedPanel = (DatasetListPanel) tabbedPane.getSelectedComponent();
        return selectedPanel.getSelectedDataset();
    }

    private void registerModelChangeListener(VCellModel model) {
        model.addChangeListener(e -> {
            System.out.println("View heard model changed.");
            VCellProject project = model.getVCellProject();
            if (project == null) return;
            setProjectDependentMenuItemsEnabled(true);
            lblProjectDescription.setText(project.getTitle());
            pnlData.updateList(project.getExperimentalDatasets());
            pnlGeometry.updateList(project.getGeometryDefinitions());
            pnlResults.updateList(project.getvCellResults());
        });
    }

    public void addNewListener(ActionListener l) {
        mniNew.addActionListener(l);
    }

    public void addOpenListener(ActionListener l) {
        mniOpen.addActionListener(l);
    }

    public void addSaveListener(ActionListener l) {
        mniSave.addActionListener(l);
    }

    public void addSaveAsListener(ActionListener l) {
        mniSaveAs.addActionListener(l);
    }

    public void addImportDataListener(ActionListener l) {
        mniImportData.addActionListener(l);
    }

    public void addImportGeometryListener(ActionListener l) {
        mniImportGeometry.addActionListener(l);
    }

    public void addImportResultsListener(ActionListener l) {
        mniImportResults.addActionListener(l);
    }

    public void addExportListener(ActionListener l) {
        mniExport.addActionListener(l);
    }

    public void addDeleteListener(ActionListener l) {
        mniDelete.addActionListener(l);
    }

    public void addConstructTIRFGeometry(ActionListener l) {
        mniConstructTIRFGeometry.addActionListener(l);
    }

    public void addDisplayListener(ActionListener l) {
        btnDisplay.addActionListener(l);
    }
}
