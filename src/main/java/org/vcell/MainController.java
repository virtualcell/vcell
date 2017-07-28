package org.vcell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.apache.commons.io.FilenameUtils;
import org.scijava.Context;

import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;


/**
 * Created by kevingaffney on 6/26/17.
 */
public class MainController {

    private MainModel model;
    private MainView view;
    private Context context;
    private DatasetService datasetService;
    private DatasetIOService datasetIOService;
    private OpService opService;
    private InFrameDisplayService inFrameDisplayService;
    private ProjectService<?> projectService;
    private VCellResultService vCellResultService;
    private VCellModelService vCellModelService;

    public MainController(MainModel model, MainView view, Context context) {
        this.model = model;
        this.view = view;
        this.context = context;
        datasetService = context.getService(DatasetService.class);
        datasetIOService = context.getService(DatasetIOService.class);
        opService = context.getService(OpService.class);
        inFrameDisplayService = context.getService(InFrameDisplayService.class);
        projectService = new ProjectService<>(datasetIOService, opService);
        vCellResultService = new VCellResultService(opService, datasetService);
    	vCellModelService = new VCellModelService();
        addActionListenersToView();
    }

    private void addActionListenersToView() {

        view.addNewListener(event -> {
            model.setProject(new Project("New project"));
        });

        view.addOpenListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fileChooser.showOpenDialog(view);
            
            if (returnVal == JFileChooser.APPROVE_OPTION) {
            	Task<Project, String> loadTask = projectService.load(fileChooser.getSelectedFile());
            	
            	loadTask.addPropertyChangeListener(propertyChangeEvent -> {
            		
            		if (propertyChangeEvent.getPropertyName().equals(Task.STATE)
            				&& loadTask.getState() == SwingWorker.StateValue.DONE) {
            			try {
							model.setProject(loadTask.get());
						} catch (InterruptedException | ExecutionException e) {
							e.printStackTrace();
						}
            		}
            	});
                
            	executeTaskWithProgressDialog(loadTask, "Loading...", false);
            }
        });

        view.addSaveListener(event -> {
            Task<Void, String> saveTask = projectService.save(model.getProject());
            executeTaskWithProgressDialog(saveTask, "Saving...", false);
        });

        view.addSaveAsListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showSaveDialog(view);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                model.setProjectTitle(file.getName());
                Task<Void, String> saveAsTask = projectService.saveAs(model.getProject(), file);
                executeTaskWithProgressDialog(saveAsTask, "Saving...", false);
            }
        });

        view.addImportDataListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (presentOpenFileChooser(fileChooser)) {
                Dataset dataset = getDatasetFromFile(fileChooser.getSelectedFile());
                model.addData(dataset);
            }
        });

        view.addImportGeometryListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (presentOpenFileChooser(fileChooser)) {
                Dataset dataset = getDatasetFromFile(fileChooser.getSelectedFile());
                model.addGeometry(dataset);
            }
        });

        view.addImportResultsSingleListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (presentOpenFileChooser(fileChooser)) {
                Dataset dataset = getDatasetFromFile(fileChooser.getSelectedFile());
                model.addResult(dataset);
            }
        });

        view.addImportResultsTimeSeriesListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (presentOpenFileChooser(fileChooser)) {
                try {
                    Dataset dataset = vCellResultService.importCsv(fileChooser.getSelectedFile());
                    model.addResult(dataset);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

        view.addExportListener(event -> {
            Dataset dataset = view.getSelectedDataset();
            if (dataset == null) {
            	JOptionPane.showMessageDialog(
            			view, 
            			"Please select a dataset to export.", 
            			"No dataset selected", 
            			JOptionPane.PLAIN_MESSAGE);            	
            	return;
            }
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(dataset.getName()));
            int returnVal = fileChooser.showSaveDialog(view);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    datasetIOService.save(dataset.duplicate(), fileChooser.getSelectedFile().getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        view.addChangeAxesListener(event -> {
        	Dataset dataset = view.getSelectedDataset();
            if (dataset == null) {
            	JOptionPane.showMessageDialog(
            			view, 
            			"Please select a dataset to edit.", 
            			"No dataset selected", 
            			JOptionPane.PLAIN_MESSAGE);
            	return;
            }
        	ChangeAxesPanel panel = new ChangeAxesPanel(dataset);
        	int returnVal = JOptionPane.showConfirmDialog(
        			view, 
        			panel, 
        			"Change Axes", 
        			JOptionPane.OK_CANCEL_OPTION, 
        			JOptionPane.PLAIN_MESSAGE);
        	
        	if (returnVal == JOptionPane.OK_OPTION) {
        		model.changeAxes(dataset, panel.getSelectedAxisTypes());
        	}
        });

        view.addDeleteListener(event -> {
            Dataset dataset = view.getSelectedDataset();
            if (dataset == null) {
            	JOptionPane.showMessageDialog(
            			view, 
            			"Please select a dataset to delete.", 
            			"No dataset selected", 
            			JOptionPane.PLAIN_MESSAGE);            	
            	return;
            }
            int result = JOptionPane.showConfirmDialog(
            		view,
                    "Are you sure you want to delete \"" + dataset.getName() + "\"?",
                    "Delete",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                model.delete(dataset);
            }
        });
        
        view.addCompareDatasetsListener(event -> {
        	
        	List<Dataset> datasetList = model.getProject().getData();
        	datasetList.addAll(model.getProject().getGeometry());
        	datasetList.addAll(model.getProject().getResults());
        	Dataset[] datasetArray = datasetList.toArray(new Dataset[datasetList.size()]);
        	
        	DatasetSelectionPanel panel = new DatasetSelectionPanel();
        	String descriptionA = "Dataset A:";
        	String descriptionB = "Dataset B:";
        	panel.addComboBox(datasetArray, descriptionA);
        	panel.addComboBox(datasetArray, descriptionB);
        	int returnVal = JOptionPane.showConfirmDialog(
        			view, 
        			panel, 
        			"Select datasets to compare",
        			JOptionPane.OK_CANCEL_OPTION,
        			JOptionPane.PLAIN_MESSAGE);
        	
        	if (returnVal == JOptionPane.OK_OPTION) {
        		ArrayList<Dataset> datasets = new ArrayList<>();
        		datasets.add(panel.getSelectedDatasetForDescription(descriptionA));
        		datasets.add(panel.getSelectedDatasetForDescription(descriptionB));
        		
        		
        		if (!Datasets.areSameSize(datasets.toArray(new Dataset[datasets.size()]), 0, 1)) {
        			JOptionPane.showMessageDialog(
        					view, 
        					"The selected datasets are not the same size.", 
        					"Incompatible datasets", 
        					JOptionPane.ERROR_MESSAGE);
        			return;
        		}
        		
        		CompareView compareView = new CompareView(datasets);
        		new CompareController(compareView, model, context);
        		compareView.setVisible(true);
        		for (Dataset dataset : datasets) {
        			inFrameDisplayService.displayDataset(dataset, compareView);
        		}
        	}
        });

        view.addConstructTIRFGeometryListener(event -> {

            List<Dataset> dataList = model.getProject().getData();
            Dataset[] dataArray = dataList.toArray(new Dataset[dataList.size()]);

            ConstructTIRFGeometryInputPanel panel = new ConstructTIRFGeometryInputPanel(dataArray);
            int returnVal = JOptionPane.showConfirmDialog(
                    view,
                    panel,
                    "Construct TIRF Geometry",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (returnVal == JOptionPane.OK_OPTION) {
                Dataset dataset = panel.getData();
                int sliceIndex = panel.getSliceIndex();
                double wavelength = panel.getWavelength();
                double angle = panel.getAngle();
                double zSpacing = panel.getZSpacing();
                Dataset geometry = (Dataset) opService.run(
                        "constructTIRFGeometry", dataset, sliceIndex, wavelength, angle, zSpacing);
                String baseName = FilenameUtils.getBaseName(dataset.getName());
                String extension = FilenameUtils.getExtension(dataset.getName());
                geometry.setName(baseName + "_geometry." + extension);
                model.addGeometry(geometry);
            }
        });

        view.addConstructTIRFImageListener(event -> {
            List<Dataset> geometry = model.getProject().getGeometry();
            List<Dataset> results = model.getProject().getResults();
            Dataset[] geometryArray = geometry.toArray(new Dataset[geometry.size()]);
            Dataset[] resultsArray = results.toArray(new Dataset[results.size()]);
            ConstructTIRFImageInputPanel panel = new ConstructTIRFImageInputPanel(geometryArray, resultsArray);

            int returnVal = JOptionPane.showConfirmDialog(
                    view,
                    panel,
                    "Construct TIRF Image",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (returnVal == JOptionPane.OK_OPTION) {
                Dataset selectedGeometry = panel.getGeometry();
                Dataset selectedMembraneResults = panel.getMembraneResults();
                Dataset selectedVolumeResults = panel.getVolumeResults();
                double wavelength = panel.getWavelength();
                double angle = panel.getAngle();
                double zSpacing = panel.getZSpacing();
                double xySpacing = panel.getXSpacing() * panel.getYSpacing();
                Dataset result = (Dataset) opService.run(
                        "constructTIRFImage", selectedGeometry, selectedMembraneResults, selectedVolumeResults,
                        wavelength, angle, zSpacing, xySpacing);
                String baseName = FilenameUtils.getBaseName(selectedGeometry.getName());
                if (baseName.endsWith("_geometry")) {
                    baseName = baseName.substring(0, baseName.length() - "_geometry".length());
                }
                String extension = FilenameUtils.getExtension(selectedGeometry.getName());
                result.setName(baseName + "_constructed_TIRF." + extension);
                model.addResult(result);
            }
        });

        view.addNewModelListener(event -> {
    		VCellModelSelectionDialog dialog = new VCellModelSelectionDialog(view, vCellModelService);
    		int resultVal = dialog.display();
    		dialog.cancelFuture(); // Dialog is no longer visible, stop trying to get models
    		if (resultVal == JOptionPane.OK_OPTION) {
    			VCellModel selectedModel = dialog.getSelectedModel();
    			if (selectedModel != null) {
    				model.addModel(selectedModel);
    			}
    		}
        });
        
        view.addSimulateModelListener(event -> {
        	System.out.println("sim model");
        });
        
        view.addTabbedPaneChangeListener(event -> {
        	view.clearListSelection();
        });
        
        view.addListSelectionListener(event -> {
        	if (!event.getValueIsAdjusting()) {
        		Object selected = ((JList<?>) event.getSource()).getSelectedValue();
        		if (Dataset.class.isInstance(selected)) {
        			view.displayDataset((Dataset) selected);
        		} else if (VCellModel.class.isInstance(selected)) {
        			view.displayModel((VCellModel) selected);
        		}
        	}
        });
    }

    private boolean presentOpenFileChooser(JFileChooser chooser) {
        int returnVal = chooser.showOpenDialog(view);
        return (returnVal == JFileChooser.APPROVE_OPTION);
    }

    private Dataset getDatasetFromFile(File file) {
        DatasetIOService datasetIOService = context.getService(DatasetIOService.class);
        Dataset dataset = null;
        try {
            dataset = datasetIOService.open(file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataset;
    }
    

    private void executeTaskWithProgressDialog(Task<?, String> task, String dialogTitle, boolean indeterminate) {
    	ProgressDialog dialog = new ProgressDialog(view, dialogTitle, indeterminate);
        dialog.setTask(task);
        task.execute();
        dialog.setVisible(true);
    }
    
//    private void simulateModel() {
//    	ModelParameterInputPanel panel = new ModelParameterInputPanel(new ArrayList<>());
//        int returnVal = JOptionPane.showConfirmDialog(
//                view,
//                panel,
//                "Model TIRF",
//                JOptionPane.OK_CANCEL_OPTION,
//                JOptionPane.PLAIN_MESSAGE);
//        if (returnVal == JOptionPane.OK_OPTION) {
//        	
//        	// Generate SBML document and save locally
//            VCellModelService vCellModelService = new VCellModelService();
//            vCellModelService.generateSBML(new VCellModel("TIRF_model_test"));
//            VCellModel vCellModel = new VCellModel("TIRF_model_test");
//            File filepath = Paths.get(projectService.getCurrentProjectRoot().getAbsolutePath(), vCellModel.getName()).toFile();
//            
//            try {
//				vCellModelService.writeSBMLToFile(new VCellModel("TIRF_model_test"), filepath);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (XMLStreamException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            
//            VCellService vCellService = new VCellService(vCellResultService);
//            SBMLModel sbmlModel = new SBMLModel();
//            sbmlModel.setFilepath(filepath.getAbsolutePath());
//            
//            
//            FutureCallback<org.vcell.vcellij.api.Dataset> callback = new FutureCallback<org.vcell.vcellij.api.Dataset>() {
//            	
//				@Override
//				public void onSuccess(org.vcell.vcellij.api.Dataset result) {
//					try {
//						Dataset datasetImageJ = datasetIOService.open(result.getFilepath());
//						model.addResult(datasetImageJ);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//				
//				@Override
//				public void onFailure(Throwable t) {
//					t.printStackTrace();
//				}
//            };
//            
//            vCellService.runSimulation(sbmlModel, callback);
//        }
//    }
}
