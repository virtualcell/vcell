package org.vcell;

import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import org.scijava.Context;
import org.scijava.display.DisplayService;

import javax.swing.*;
import java.io.IOException;


/**
 * Created by kevingaffney on 6/26/17.
 */
public class MasterController {

    private VCellModel model;
    private MainView view;
    private Context context;
    private VCellProjectService vCellProjectService;

    public MasterController(VCellModel model, MainView view, Context context) {
        this.model = model;
        this.view = view;
        this.context = context;
        vCellProjectService = new VCellProjectService();
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
                DatasetIOService datasetIOService = context.getService(DatasetIOService.class);
                model.setVCellProject(vCellProjectService.load(fileChooser.getSelectedFile(), datasetIOService));
            }
        });

        view.addSaveListener(event -> {
            DatasetIOService datasetIOService = context.getService(DatasetIOService.class);
            vCellProjectService.save(model.getVCellProject(), datasetIOService);
        });

        view.addSaveAsListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showSaveDialog(view);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                DatasetIOService datasetIOService = context.getService(DatasetIOService.class);
                vCellProjectService.saveAs(model.getVCellProject(), fileChooser.getSelectedFile(), datasetIOService);
            }
        });

        view.addImportDataListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnVal = fileChooser.showOpenDialog(view);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                DatasetIOService datasetIOService = context.getService(DatasetIOService.class);
                Dataset data = null;
                try {
                    data = datasetIOService.open(fileChooser.getSelectedFile().getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                model.addData(data);
            }
        });

        view.addExportListener(event -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnVal = fileChooser.showSaveDialog(view);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                DatasetIOService datasetIOService = context.getService(DatasetIOService.class);
                Dataset dataset = view.getSelectedDataset().duplicate();
                try {
                    datasetIOService.save(dataset, fileChooser.getSelectedFile().getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        view.addDisplayListener(event -> {
            DisplayService displayService = context.getService(DisplayService.class);
            displayService.createDisplay(view.getSelectedDataset());
        });
    }
}
