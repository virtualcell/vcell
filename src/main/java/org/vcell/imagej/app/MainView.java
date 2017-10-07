package org.vcell.imagej.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionListener;

import org.scijava.Context;
import org.scijava.ui.swing.viewer.SwingDisplayWindow;
import org.scijava.ui.viewer.DisplayPanel;
import org.vcell.imagej.common.gui.InFrameDisplayService;
import org.vcell.imagej.common.gui.ListPanel;
import org.vcell.imagej.common.vcell.ModelSummaryPanel;
import org.vcell.imagej.common.vcell.VCellModel;

import net.imagej.Dataset;
import net.imagej.ui.swing.viewer.image.SwingImageDisplayPanel;

/**
 * Created by kevingaffney on 6/26/17.
 */
public class MainView extends SwingDisplayWindow {
	
	private static final long serialVersionUID = 5324502530475640306L;
	
	// Data model
	private MainModel model;
	
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
    private JMenuItem mniSimulateModel;

    // Panels of datasets
    private ListPanel<Dataset> pnlData;
    private ListPanel<Dataset> pnlGeometry;
    private ListPanel<VCellModel> pnlModels;
    private ListPanel<Dataset> pnlResults;

    public MainView(MainModel model, Context context) {
    	
    	this.model = model;
    	inFrameDisplayService = context.getService(InFrameDisplayService.class);
    	
        initializeContentPane();
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
    	ListPanel<?> listPanel = (ListPanel<?>) tabbedPane.getSelectedComponent();
    	Object item = listPanel.getSelectedItem();
    	if (Dataset.class.isInstance(item)) {
    		return (Dataset) item;
    	}
    	return null;
    }
    
    public VCellModel getSelectedModel() {
    	ListPanel<?> listPanel = (ListPanel<?>) tabbedPane.getSelectedComponent();
    	Object item = listPanel.getSelectedItem();
    	if (VCellModel.class.isInstance(item)) {
    		return (VCellModel) item;
    	}
    	return null;
    }
    
    public void displaySelectedDataset() {
    	Dataset dataset = getSelectedDataset();
    	if (dataset == null) return;
    	displayDataset(dataset);
    }
    
    public void displayDataset(Dataset dataset) {
    	inFrameDisplayService.displayDataset(dataset, this);
    }
    
    public void displayModel(VCellModel vCellModel) {
    	rightPanel.removeAll();
    	rightPanel.add(new ModelSummaryPanel(model, vCellModel));
    	revalidate();
    	repaint();
    }
    
    public void clearListSelection() {
    	pnlData.getList().clearSelection();
    	pnlGeometry.getList().clearSelection();
    	pnlModels.getList().clearSelection();
    	pnlResults.getList().clearSelection();
    }

    private void initializeContentPane() {

        JPanel contentPane = new JPanel();

        BorderLayout borderLayout = new BorderLayout();
        contentPane.setLayout(borderLayout);

        // Project label initialization
        lblProjectDescription = new JLabel(" ", SwingConstants.CENTER);
        Font font = lblProjectDescription.getFont();
        lblProjectDescription.setFont(new Font(font.getFontName(), Font.BOLD, 14));

        // Tabbed pane initialization
        Project project = model.getProject();
        if (project != null) {
            pnlData = new ListPanel<>(project.getData());
            pnlGeometry = new ListPanel<>(project.getGeometry());
            pnlModels = new ListPanel<>(project.getModels());
            pnlResults = new ListPanel<>(project.getResults());
        } else {
            pnlData = new ListPanel<>(new ArrayList<>());
            pnlGeometry = new ListPanel<>(new ArrayList<>());
            pnlModels = new ListPanel<>(new ArrayList<>());
            pnlResults = new ListPanel<>(new ArrayList<>());
        }

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Data", pnlData);
        tabbedPane.addTab("Geometry", pnlGeometry);
        tabbedPane.addTab("Models", pnlModels);
        tabbedPane.addTab("Results", pnlResults);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(lblProjectDescription, BorderLayout.PAGE_START);
        leftPanel.add(tabbedPane, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(300, 400));
        
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(400, 400));
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setBackground(Color.WHITE);
        splitPane.setBorder(null);
        
        contentPane.add(splitPane, BorderLayout.CENTER);

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
        mniSimulateModel = new JMenuItem("Simulate model...");
        mnuModeling.add(mniNewModel);
        mnuModeling.add(mniSimulateModel);
        
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
        mniSimulateModel.setEnabled(enabled);
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
            pnlModels.updateList(project.getModels());
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
    
    public void addSimulateModelListener(ActionListener l) {
    	mniSimulateModel.addActionListener(l);
    }
    
    public void addTabbedPaneChangeListener(ChangeListener l) {
    	tabbedPane.addChangeListener(l);
    }
    
    public void addListSelectionListener(ListSelectionListener l) {
    	pnlData.getList().addListSelectionListener(l);
    	pnlGeometry.getList().addListSelectionListener(l);
    	pnlModels.getList().addListSelectionListener(l);
    	pnlResults.getList().addListSelectionListener(l);
    }
}
