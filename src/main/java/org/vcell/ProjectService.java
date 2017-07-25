package org.vcell;

import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.scijava.display.DisplayService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by kevingaffney on 6/27/17.
 */
public class ProjectService {

    private File currentProjectRoot;
    private DatasetIOService datasetIOService;
    private OpService opService;
    private DisplayService displayService;

    public ProjectService(DatasetIOService datasetIOService, OpService opService, DisplayService displayService) {
        this.datasetIOService = datasetIOService;
        this.opService = opService;
        this.displayService = displayService;
    }
    
    public File getCurrentProjectRoot() {
    	return currentProjectRoot;
    }

    public Project load(File root) {

        Project project = new Project(root.getName());

        File[] dataFiles = Paths.get(root.getAbsolutePath(), "data").toFile().listFiles();
        File[] geometryFiles = Paths.get(root.getAbsolutePath(), "geometry").toFile().listFiles();
        File[] resultsFiles = Paths.get(root.getAbsolutePath(), "results").toFile().listFiles();

        if (dataFiles != null) {
            for (File dataFile : dataFiles) {
                try {
                	Dataset data = datasetIOService.open(dataFile.getAbsolutePath());
//                	printAxes(data);
                    project.getData().add(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (geometryFiles != null) {
            for (File geometryFile : geometryFiles) {
                try {
                    Dataset geometry = datasetIOService.open(geometryFile.getAbsolutePath());

                    // Geometry datasets are saved as 8-bit images so we must convert back to 1-bit
                    if (geometry.firstElement() instanceof UnsignedByteType) {
                        Img<UnsignedByteType> img = (Img<UnsignedByteType>) geometry.getImgPlus().getImg();
                        Img<BitType> converted = opService.convert().bit(img);
                        ImgPlus<BitType> convertedImgPlus = new ImgPlus<>(converted, geometry.getName());
                        geometry.setImgPlus(convertedImgPlus);
                    }
                    
//                    printAxes(geometry);
                    project.getGeometry().add(geometry);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (resultsFiles != null) {
            for (File resultsFile : resultsFiles) {
                try {
                	Dataset results = datasetIOService.open(resultsFile.getAbsolutePath());
//                	printAxes(results);
                    project.getResults().add(results);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        currentProjectRoot = root;

        return project;
    }

    public void saveAs(Project project, File root) {

        Path dataPath = Paths.get(root.getAbsolutePath(), "data");
        Path geometryPath = Paths.get(root.getAbsolutePath(), "geometry");
        Path resultsPath = Paths.get(root.getAbsolutePath(), "results");

        try {

            // Save data
            Files.createDirectories(dataPath);
            FileUtils.cleanDirectory(dataPath.toFile());
            for (Dataset dataset : project.getData()) {
                saveDataset(dataset, dataPath);
            }

            // Save geometry
            Files.createDirectories(geometryPath);
            FileUtils.cleanDirectory(geometryPath.toFile());
            for (Dataset dataset : project.getGeometry()) {
                saveDataset(dataset, geometryPath);
            }

            // Save results
            Files.createDirectories(resultsPath);
            FileUtils.cleanDirectory(resultsPath.toFile());
            for (Dataset dataset : project.getResults()) {
                saveDataset(dataset, resultsPath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        currentProjectRoot = root;
    }

    public void save(Project project) {
        saveAs(project, currentProjectRoot);
    }

    private void saveDataset(Dataset dataset, Path path) throws IOException {

        Dataset datasetToSave = dataset.duplicate();

        // SCIFIO cannot save 1-bit images so we must convert to 8-bit
        if (datasetToSave.firstElement() instanceof BitType) {
            Img<BitType> img = (Img<BitType>) dataset.getImgPlus().getImg();
            Img<UnsignedByteType> converted = opService.convert().uint8(img);
            ImgPlus<UnsignedByteType> convertedImgPlus = new ImgPlus<>(converted, dataset.getName());
            datasetToSave.setImgPlus(convertedImgPlus);
        }

        String name = dataset.getName();
        if (FilenameUtils.getExtension(name).isEmpty()) {
            name += ".tif"; // Default save extension
        }

        Path filePath = Paths.get(path.toString(), name);
        datasetIOService.save(datasetToSave, filePath.toString());
        
//        printAxes(datasetToSave);
    }
    
    private void printAxes(Dataset dataset) {
    	System.out.println(dataset.getName());
    	System.out.println("0: " + dataset.axis(0).type());
    	System.out.println("1: " + dataset.axis(1).type());
    	System.out.println("2: " + dataset.axis(2).type());
    }
}
