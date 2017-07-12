package org.vcell;

import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.display.OverlayService;
import net.imagej.ops.OpService;
import org.apache.commons.io.FilenameUtils;
import org.sbml.jsbml.SBMLDocument;
import org.scijava.Context;
import org.scijava.command.CommandService;
import org.scijava.display.Display;
import org.scijava.display.DisplayService;
import org.vcell.vcellij.api.SBMLModel;

import javax.swing.*;
import javax.xml.stream.XMLStreamException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


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
    private ProjectService projectService;
    private VCellResultService vCellResultService;

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
        projectService = new ProjectService(datasetIOService, opService, displayService);
        vCellResultService = new VCellResultService(opService, datasetService);
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

        view.addSubtractBackgroundListener(event -> {
            Display activeDisplay = displayService.getActiveDisplay();
            int returnVal = JOptionPane.showConfirmDialog(
                    view,
                    "Select rectangle that contains only background, then press OK",
                    "Background selection",
                    JOptionPane.OK_CANCEL_OPTION);
            if (returnVal == JOptionPane.OK_OPTION) {
                System.out.println(overlayService.getOverlays().toArray().length);
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

        view.addModelTIRFListener(event -> {
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
                SBMLDocument sbmlDocument = vCellModelService.generateSBML(new VCellModel("TIRF_model_test"));
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
                org.vcell.vcellij.api.Dataset datasetVC = vCellService.runSimulation(sbmlModel);
                
                try {
					Dataset datasetImageJ = datasetIOService.open(datasetVC.getFilepath());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
        });

        view.addDisplayListener(event -> {
            displayService.createDisplay(view.getSelectedDataset());
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
}
