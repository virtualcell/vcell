package org.vcell;

import io.scif.services.DatasetIOService;
import net.imagej.Dataset;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by kevingaffney on 6/27/17.
 */
public class VCellProjectService {

    private File currentProjectRoot;

    public VCellProject load(File root, DatasetIOService datasetIOService) {

        VCellProject project = new VCellProject(root.getName());

        File[] dataFiles = Paths.get(root.getAbsolutePath(), "data").toFile().listFiles();
        File[] geometryFiles = Paths.get(root.getAbsolutePath(), "geometry").toFile().listFiles();
        File[] resultsFiles = Paths.get(root.getAbsolutePath(), "results").toFile().listFiles();

        if (dataFiles != null) {
            for (File dataFile : dataFiles) {
                try {
                    project.getExperimentalDatasets().add(datasetIOService.open(dataFile.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (geometryFiles != null) {
            for (File geometryFile : geometryFiles) {
                try {
                    project.getGeometryDefinitions().add(datasetIOService.open(geometryFile.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (resultsFiles != null) {
            for (File resultsFile : resultsFiles) {
                try {
                    project.getvCellResults().add(datasetIOService.open(resultsFile.getAbsolutePath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        currentProjectRoot = root;

        return project;
    }

    public void saveAs(VCellProject project, File root, DatasetIOService datasetIOService) {

        Path dataPath = Paths.get(root.getAbsolutePath(), "data");
        Path geometryPath = Paths.get(root.getAbsolutePath(), "geometry");
        Path resultsPath = Paths.get(root.getAbsolutePath(), "results");

        try {

            // Save data
            Files.createDirectories(dataPath);
            for (Dataset dataset : project.getExperimentalDatasets()) {
                Path path = Paths.get(dataPath.toString(), dataset.getName());
                datasetIOService.save(dataset.duplicate(), path.toString());
            }

            // Save geometry
            Files.createDirectories(geometryPath);
            for (Dataset dataset : project.getGeometryDefinitions()) {
                Path path = Paths.get(dataPath.toString(), dataset.getName());
                datasetIOService.save(dataset.duplicate(), path.toString());
            }

            // Save results
            Files.createDirectories(resultsPath);
            for (Dataset dataset : project.getvCellResults()) {
                Path path = Paths.get(dataPath.toString(), dataset.getName());
                datasetIOService.save(dataset.duplicate(), path.toString());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(VCellProject project, DatasetIOService datasetIOService) {
        saveAs(project, currentProjectRoot, datasetIOService);
    }
}
