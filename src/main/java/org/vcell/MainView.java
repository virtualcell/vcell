package org.vcell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.scijava.Context;
import org.scijava.ui.swing.viewer.SwingDisplayWindow;
import org.scijava.ui.viewer.DisplayPanel;

import net.imagej.Dataset;
import net.imagej.ui.swing.viewer.image.SwingImageDisplayPanel;

/**
 * Created by kevingaffney on 6/26/17.
 */
public class MainView extends SwingDisplayWindow {
	
	private static final long serialVersionUID = 5324502530475640306L;
	
	// Services
	private InFrameDisplayService inFrameDisplayService;
	
	private SwingImageDisplayPanel imagePanel;
	private JPanel rightPanel;
	
    private JTabbedPane tabbedPane;
    private JLabel lblProjectDescription;

    // Menu items under "File"
    private JMenuItem mniNew;
    private JMenuItem mniOpen;
    private JMenuItem mniSave;
    private JMenuItem mniSaveAs;
    private JMenuItem mniImportData;
    private JMenuItem mniImportGeometry;
    private JMenuItem mniImportResultsTimeSeries;
    private JMenuItem mniImportResultsSingle;
    private JMenuItem mniExport;

    // Menu items under "Edit"
    private JMenuItem mniChangeAxes;
    private JMenuItem mniDelete;

    // Menu items under "Analysis"
    private JMenuItem mniCompareDatasets;
    private JMenuItem mniConstructTIRFGeometry;
    private JMenuItem mniConstructTIRFImage;

    // Menu items under "Modeling"
    private JMenuItem mniNewModel;

    // Panels of datasets
    private DatasetListPanel pnlData;
    private DatasetListPanel pnlGeometry;
    private DatasetListPanel pnlResults;

    // Display button
    private JButton btnDisplay;

    public MainView(MainModel model, Context context) {
    	
    	inFrameDisplayService = context.getService(InFrameDisplayService.class);
    	
        initializeContentPane(model);
        initializeMenuBar();
        registerModelChangeListeners(model);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }
    
    @Override
    public void setContent(DisplayPanel panel) {
    	imagePanel = (SwingImageDisplayPanel) panel;
    	rightPanel.removeAll();
        rightPanel.add(imagePanel, BorderLayout.CENTER);
    }
    
    public Dataset getSelectedDataset() {
        DatasetListPanel selectedPanel = (DatasetListPanel) tabbedPane.getSelectedComponent();
        return selectedPanel.getSelectedDataset();
    }
    
    public void displaySelectedDataset() {
    	Dataset dataset = getSelectedDataset();
    	if (dataset == null) return;
    	inFrameDisplayService.displayDataset(dataset, this);
    }

    private void initializeContentPane(MainModel model) {

        JPanel contentPane = new JPanel();

        BorderLayout borderLayout = new BorderLayout();
        contentPane.setLayout(borderLayout);

        // Project label initialization
        lblProjectDescription = new JLabel("", SwingConstants.CENTER);
        contentPane.add(lblProjectDescription, BorderLayout.PAGE_START);

        // Tabbed pane initialization
        Project project = model.getProject();
        if (project != null) {
            pnlData = new DatasetListPanel(project.getData());
            pnlGeometry = new DatasetListPanel(project.getGeometry());
            pnlResults = new DatasetListPanel(project.getResults());
        } else {
            pnlData = new DatasetListPanel(new ArrayList<>());
            pnlGeometry = new DatasetListPanel(new ArrayList<>());
            pnlResults = new DatasetListPanel(new ArrayList<>());
        }

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Data", pnlData);
        tabbedPane.addTab("Geometry", pnlGeometry);
        tabbedPane.addTab("Results", pnlResults);

        // Display button initialization
        btnDisplay = new JButton("Display");
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(tabbedPane, BorderLayout.CENTER);
        leftPanel.add(btnDisplay, BorderLayout.PAGE_END);
        contentPane.add(leftPanel, BorderLayout.LINE_START);
        
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(400, 400));
        contentPane.add(rightPanel, BorderLayout.CENTER);

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
        mnuImport.add(mniImportData);
        mnuImport.add(mniImportGeometry);
        JMenu mnuImportResults = new JMenu("VCell results");
        mniImportResultsSingle = new JMenuItem("Single image...");
        mniImportResultsTimeSeries = new JMenuItem("Time series...");
        mnuImportResults.add(mniImportResultsSingle);
        mnuImportResults.add(mniImportResultsTimeSeries);
        mnuImport.add(mnuImportResults);
        mnuFile.add(mnuImport);

        mniExport = new JMenuItem("Export...");
        mnuFile.add(mniExport);

        // Edit menu
        JMenu mnuEdit = new JMenu("Edit");
        mniChangeAxes = new JMenuItem("Change axes...");
        mniDelete = new JMenuItem("Delete...");
        mnuEdit.add(mniChangeAxes);
        mnuEdit.add(mniDelete);

        // Analysis menu
        JMenu mnuAnalysis = new JMenu("Analysis");

        mniCompareDatasets = new JMenuItem("Compare datasets...");
        mnuAnalysis.add(mniCompareDatasets);

        JMenu mnuTIRF = new JMenu("TIRF");
        mniConstructTIRFGeometry = new JMenuItem("Construct TIRF geometry...");
        mniConstructTIRFImage = new JMenuItem("Construct TIRF image...");
        mnuTIRF.add(mniConstructTIRFGeometry);
        mnuTIRF.add(mniConstructTIRFImage);
        mnuAnalysis.add(mnuTIRF);

        // Modeling menu
        JMenu mnuModeling = new JMenu("Modeling");
        mniNewModel = new JMenuItem("New model...");
        mnuModeling.add(mniNewModel);
        
        menuBar.add(mnuFile);
        menuBar.add(mnuEdit);
        menuBar.add(mnuAnalysis);
        menuBar.add(mnuModeling);
        setJMenuBar(menuBar);

        setProjectDependentMenuItemsEnabled(false);
    }

    private void setProjectDependentMenuItemsEnabled(boolean enabled) {
        mniSave.setEnabled(enabled);
        mniSaveAs.setEnabled(enabled);
        mniImportData.setEnabled(enabled);
        mniImportGeometry.setEnabled(enabled);
        mniImportResultsSingle.setEnabled(enabled);
        mniImportResultsTimeSeries.setEnabled(enabled);
        mniExport.setEnabled(enabled);
        mniChangeAxes.setEnabled(enabled);
        mniDelete.setEnabled(enabled);
        mniCompareDatasets.setEnabled(enabled);
        mniConstructTIRFGeometry.setEnabled(enabled);
        mniConstructTIRFImage.setEnabled(enabled);
        mniNewModel.setEnabled(enabled);
    }
    
    private void registerModelChangeListeners(MainModel model) {
    	
        model.addDataChangeListener(e -> {
            System.out.println("View heard model data changed.");
            Project project = model.getProject();
            if (project == null) return;
            setProjectDependentMenuItemsEnabled(true);
            lblProjectDescription.setText(project.getTitle());
            pnlData.updateList(project.getData());
            pnlGeometry.updateList(project.getGeometry());
            pnlResults.updateList(project.getResults());
        });
        
        model.addDisplayChangeListener(e -> {
        	System.out.println("View heard model display changed.");
        	displaySelectedDataset();
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

    public void addImportResultsSingleListener(ActionListener l) {
        mniImportResultsSingle.addActionListener(l);
    }

    public void addImportResultsTimeSeriesListener(ActionListener l) {
        mniImportResultsTimeSeries.addActionListener(l);
    }

    public void addExportListener(ActionListener l) {
        mniExport.addActionListener(l);
    }
    
    public void addChangeAxesListener(ActionListener l) {
    	mniChangeAxes.addActionListener(l);
    }

    public void addDeleteListener(ActionListener l) {
        mniDelete.addActionListener(l);
    }
    
    public void addCompareDatasetsListener(ActionListener l) {
    	mniCompareDatasets.addActionListener(l);
    }

    public void addConstructTIRFGeometryListener(ActionListener l) {
        mniConstructTIRFGeometry.addActionListener(l);
    }

    public void addConstructTIRFImageListener(ActionListener l) {
        mniConstructTIRFImage.addActionListener(l);
    }

    public void addNewModelListener(ActionListener l) {
        mniNewModel.addActionListener(l);
    }
    
    public void addDisplayListener(ActionListener l) {
        btnDisplay.addActionListener(l);
    }
}
