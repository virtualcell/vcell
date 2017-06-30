package org.vcell;

import io.scif.services.DatasetIOService;
import javafx.stage.FileChooser;
import net.imagej.Dataset;
import net.imagej.DatasetService;
import net.imagej.ops.OpService;
import org.scijava.Context;
import org.scijava.command.Command;
import org.scijava.command.CommandModule;
import org.scijava.command.CommandService;
import org.scijava.display.DisplayService;
import org.scijava.plugin.PluginService;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


/**
 * Created by kevingaffney on 6/26/17.
 */
public class MainController {

    private VCellModel model;
    private MainView view;
    private Context context;
    private VCellProjectService vCellProjectService;
    private DatasetService datasetService;
    private DatasetIOService datasetIOService;
    private CommandService commandService;

    public MainController(VCellModel model, MainView view, Context context) {
        this.model = model;
        this.view = view;
        this.context = context;
        vCellProjectService = new VCellProjectService();
        datasetService = context.getService(DatasetService.class);
        datasetIOService = context.getService(DatasetIOService.class);
        commandService = context.getService(CommandService.class);
        addActionListenersToView();
    }

    private void addActionListenersToView() {

        view.addNewListener(event -> {
            model.setVCellProject(new VCellProject("New project"));
        });

        view.addOpenListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fileChooser.showOpenDialog(view);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                model.setVCellProject(vCellProjectService.load(fileChooser.getSelectedFile(), datasetIOService));
            }
        });

        view.addSaveListener(event -> {
            vCellProjectService.save(model.getVCellProject(), datasetIOService);
        });

        view.addSaveAsListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showSaveDialog(view);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                vCellProjectService.saveAs(model.getVCellProject(), file, datasetIOService);
                model.setVCellProjectTitle(file.getName());
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

        view.addImportResultsListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            if (presentOpenFileChooser(fileChooser)) {
                Dataset dataset = getDatasetFromFile(fileChooser.getSelectedFile());
                model.addResult(dataset);
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

        view.addConstructTIRFGeometry(event -> {
            System.out.println("Construct TIRF geometry started");
            Dataset dataset = view.getSelectedDataset();
            DisplayService displayService = context.getService(DisplayService.class);
            if (displayService.getDisplays(dataset).isEmpty()) {
                displayService.createDisplay(dataset);
            }
            Future<CommandModule> commandModuleFuture = commandService.run(ConstructTIRFGeometry.class, true);
//            try {
//                System.out.println("About to get");
//                commandModuleFuture.get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
        });

        view.addDisplayListener(event -> {
            DisplayService displayService = context.getService(DisplayService.class);
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
