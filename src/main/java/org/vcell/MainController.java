package org.vcell;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FilenameUtils;
import org.sbml.jsbml.SBMLDocument;
import org.scijava.Context;
import org.scijava.command.CommandService;
import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.scijava.event.EventService;
import org.scijava.plugin.PluginInfo;
import org.scijava.plugin.PluginService;
import org.scijava.plugin.SciJavaPlugin;
import org.scijava.thread.ThreadService;
import org.scijava.ui.UIService;
import org.scijava.ui.swing.viewer.SwingDisplayWindow;
import org.scijava.ui.viewer.DisplayViewer;
import org.vcell.vcellij.api.SBMLModel;

import com.google.common.util.concurrent.FutureCallback;

import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.autoscale.AutoscaleService;
import net.imagej.autoscale.DataRange;
import net.imagej.display.DatasetView;
import net.imagej.display.ImageDisplayService;
import net.imagej.display.OverlayService;
import net.imagej.ops.OpService;
import net.imagej.plugins.commands.display.interactive.BrightnessContrast;
import net.imagej.ui.swing.viewer.image.SwingImageDisplayViewer;
import net.imglib2.type.numeric.RealType;


/**
 * Created by kevingaffney on 6/26/17.
 */
public class MainController {

    private MainModel model;
    private MainView view;
    private Context context;
    private DatasetService datasetService;
    private DatasetIOService datasetIOService;
    private CommandService commandService;
    private OpService opService;
    private OverlayService overlayService;
    private DisplayService displayService;
    private EventService eventService;
    private UIService uiService;
    private PluginService pluginService;
    private ThreadService threadService;
    private ImageDisplayService imageDisplayService;
    private AutoscaleService autoscaleService;
    private ProjectService projectService;
    private VCellResultService vCellResultService;
    private VCellModelService vCellModelService;

    public MainController(MainModel model, MainView view, Context context) {
        this.model = model;
        this.view = view;
        this.context = context;
        datasetService = context.getService(DatasetService.class);
        datasetIOService = context.getService(DatasetIOService.class);
        commandService = context.getService(CommandService.class);
        opService = context.getService(OpService.class);
        overlayService = context.getService(OverlayService.class);
        displayService = context.getService(DisplayService.class);
        eventService = context.getService(EventService.class);
        uiService = context.getService(UIService.class);
        pluginService = context.getService(PluginService.class);
        threadService = context.getService(ThreadService.class);
        imageDisplayService = context.getService(ImageDisplayService.class);
        autoscaleService = context.getService(AutoscaleService.class);
        projectService = new ProjectService(datasetIOService, opService, displayService);
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
                model.setProject(projectService.load(fileChooser.getSelectedFile()));
            }
        });

        view.addSaveListener(event -> {
            projectService.save(model.getProject());
        });

        view.addSaveAsListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showSaveDialog(view);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                projectService.saveAs(model.getProject(), file);
                model.setProjectTitle(file.getName());
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
            Dataset dataset = view.getSelectedDataset().duplicate();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setSelectedFile(new File(dataset.getName()));
            int returnVal = fileChooser.showSaveDialog(view);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    datasetIOService.save(dataset, fileChooser.getSelectedFile().getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        view.addDeleteListener(event -> {
            Dataset dataset = view.getSelectedDataset();
            int result = JOptionPane.showConfirmDialog(view,
                    "Are you sure you want to delete \"" + dataset.getName() + "\"?",
                    "Delete",
                    JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                model.delete(dataset);
            }
        });
        
        view.addCompareDatasetsListener(event -> {
        	
        	ArrayList<Dataset> datasetList = model.getProject().getData();
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
        		CompareView compareView = new CompareView(datasets);
        		new CompareController(compareView, context);
        		compareView.setVisible(true);
        		for (Dataset dataset : datasets) {
        			displayDataset(dataset, compareView);
        		}
        	}
        });

        view.addConstructTIRFGeometryListener(event -> {

            ArrayList<Dataset> dataList = model.getProject().getData();
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
            ArrayList<Dataset> geometry = model.getProject().getGeometry();
            ArrayList<Dataset> results = model.getProject().getResults();
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
    		VCellModelDialog dialog = new VCellModelDialog(view, vCellModelService);
    		int resultVal = dialog.display();
    		System.out.println(resultVal);
        });
        
        view.addDisplayListener(event -> {
        	Dataset dataset = view.getSelectedDataset();
        	displayDataset(dataset, view);
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
    
    public void displayDataset(Dataset dataset, SwingDisplayWindow window) {
    	
		Display<?> display = displayService.createDisplayQuietly(dataset);

		final SwingImageDisplayViewer finalViewer = getDisplayViewer(display);
		if (finalViewer == null) return;

		threadService.queue(() -> {
			finalViewer.view(window, display);
			uiService.addDisplayViewer(finalViewer);
			window.showDisplay(true);
			display.update();
			
			autoscale(imageDisplayService.getActiveDatasetView());
		});
    }
    
    private void autoscale(DatasetView datasetView) {
		DataRange range = autoscaleService.getDefaultIntervalRange(datasetView.getData());
		datasetView.setChannelRanges(range.getMin(), range.getMax());
		datasetView.getProjector().map();
		datasetView.update();
    }
    
    private SwingImageDisplayViewer getDisplayViewer(Display<?> display) {
    	
    	if (uiService.getDisplayViewer(display) != null) {
			// display is already being shown
			return null;
		}

		final List<PluginInfo<DisplayViewer<?>>> viewers =
			uiService.getViewerPlugins();

		DisplayViewer<?> displayViewer = null;
		for (final PluginInfo<DisplayViewer<?>> info : viewers) {
			// check that viewer can actually handle the given display
			final DisplayViewer<?> viewer = pluginService.createInstance(info);
			if (!(viewer instanceof SwingImageDisplayViewer)) continue;
			displayViewer = viewer;
			break; // found a suitable viewer; we are done
		}
		
		if (displayViewer == null) {
			System.err.println("For UI '" + getClass().getName() +
				"': no suitable viewer for display: " + display);
			return null;
		}
		
		return (SwingImageDisplayViewer) displayViewer;
    }
    
    private void simulateModel() {
    	ModelParameterInputPanel panel = new ModelParameterInputPanel(new ArrayList<>());
        int returnVal = JOptionPane.showConfirmDialog(
                view,
                panel,
                "Model TIRF",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (returnVal == JOptionPane.OK_OPTION) {
        	
        	// Generate SBML document and save locally
            VCellModelService vCellModelService = new VCellModelService();
            vCellModelService.generateSBML(new VCellModel("TIRF_model_test"));
            VCellModel vCellModel = new VCellModel("TIRF_model_test");
            File filepath = Paths.get(projectService.getCurrentProjectRoot().getAbsolutePath(), vCellModel.getName()).toFile();
            
            try {
				vCellModelService.writeSBMLToFile(new VCellModel("TIRF_model_test"), filepath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            VCellService vCellService = new VCellService(vCellResultService);
            SBMLModel sbmlModel = new SBMLModel();
            sbmlModel.setFilepath(filepath.getAbsolutePath());
            
            
            FutureCallback<org.vcell.vcellij.api.Dataset> callback = new FutureCallback<org.vcell.vcellij.api.Dataset>() {
            	
				@Override
				public void onSuccess(org.vcell.vcellij.api.Dataset result) {
					try {
						Dataset datasetImageJ = datasetIOService.open(result.getFilepath());
						model.addResult(datasetImageJ);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				@Override
				public void onFailure(Throwable t) {
					t.printStackTrace();
				}
            };
            
            vCellService.runSimulation(sbmlModel, callback);
        }
    }
}
