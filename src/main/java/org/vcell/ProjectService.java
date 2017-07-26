package org.vcell;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.Intervals;

/**
 * Created by kevingaffney on 6/27/17.
 */
public class ProjectService<T extends RealType<T>> {

    private File currentProjectRoot;
    private DatasetIOService datasetIOService;
    private OpService opService;

    public ProjectService(DatasetIOService datasetIOService, OpService opService) {
        this.datasetIOService = datasetIOService;
        this.opService = opService;
    }
    
    public File getCurrentProjectRoot() {
    	return currentProjectRoot;
    }

    public Task<Project, String> load(File root) {
    	
    	final Task<Project, String> task = new Task<Project, String>() {
    		
			@Override
			protected Project doInBackground() throws Exception {
				
				Project project = new Project(root.getName());

		        File[] dataFiles = Paths.get(root.getAbsolutePath(), "data").toFile().listFiles();
		        File[] geometryFiles = Paths.get(root.getAbsolutePath(), "geometry").toFile().listFiles();
		        File[] resultsFiles = Paths.get(root.getAbsolutePath(), "results").toFile().listFiles();
		        
		        int numFiles = dataFiles.length + geometryFiles.length + resultsFiles.length;
		        int numLoaded = 0;

		        if (dataFiles != null) {
		            for (File dataFile : dataFiles) {
		                try {
		                	setSubtask(dataFile.getName());
		                	Dataset data = datasetIOService.open(dataFile.getAbsolutePath());
		                    project.getData().add(data);
		                    numLoaded++;
		                    setProgress(numLoaded * 100 / numFiles); 
		                } catch (IOException e) {
		                    e.printStackTrace();
		                }
		            }
		        }

		        if (geometryFiles != null) {
		            for (File geometryFile : geometryFiles) {
		                try {
		                	setSubtask(geometryFile.getName());
		                    Dataset geometry = datasetIOService.open(geometryFile.getAbsolutePath());

		                    // Geometry datasets are saved as 8-bit images so we must convert back to 1-bit
		                    if (geometry.firstElement() instanceof UnsignedByteType) {
		                        @SuppressWarnings("unchecked")
								Img<UnsignedByteType> img = (Img<UnsignedByteType>) geometry.getImgPlus().getImg();
		                        Img<BitType> converted = opService.convert().bit(img);
		                        ImgPlus<BitType> convertedImgPlus = new ImgPlus<>(converted, geometry.getName());
		                        geometry.setImgPlus(convertedImgPlus);
		                    }
		                    
		                    project.getGeometry().add(geometry);
		                    numLoaded++;
		                    setProgress(numLoaded * 100 / numFiles); 

		                } catch (IOException e) {
		                    e.printStackTrace();
		                }
		            }
		        }

		        if (resultsFiles != null) {
		            for (File resultsFile : resultsFiles) {
		                try {
		                	setSubtask(resultsFile.getName());
		                	Dataset results = datasetIOService.open(resultsFile.getAbsolutePath());
		                	
		                	// Loading 1-dimensional tif images adds a dimension 
		                	// so must crop out empty dimensions
		                	ImgPlus<T> imgPlus = (ImgPlus<T>) results.getImgPlus();
		                	int numDimensions = imgPlus.numDimensions();
		                	long[] dimensions = new long[2 * imgPlus.numDimensions()];
		                	for (int i = 0; i < numDimensions; i++) {
		                		dimensions[i] = 0;
		                		dimensions[i + numDimensions] = imgPlus.dimension(i) - 1;
		                	}
		                	FinalInterval interval = Intervals.createMinMax(dimensions);
		                	ImgPlus<T> cropped = opService.transform().crop(imgPlus, interval, true);
		                	results.setImgPlus(cropped);
		                	
		                    project.getResults().add(results);
		                    numLoaded++;
		                    setProgress(numLoaded * 100 / numFiles); 
		                    printAxes(results);
		                } catch (IOException e) {
		                    e.printStackTrace();
		                }
		            }
		        }

		        currentProjectRoot = root;
		        return project;
			}
		};
		
		return task;
    }
    

    public Task<Void, String> saveAs(Project project, File root) {
    	
    	final Task<Void, String> task = new Task<Void, String>() {
    		
			@Override
			protected Void doInBackground() throws Exception {
				
				int numDatasets = project.getData().size() + project.getGeometry().size() + project.getResults().size();
		    	int numSaved = 0;
		    	
		        Path dataPath = Paths.get(root.getAbsolutePath(), "data");
		        Path geometryPath = Paths.get(root.getAbsolutePath(), "geometry");
		        Path resultsPath = Paths.get(root.getAbsolutePath(), "results");

		        try {

		            // Save data
		            Files.createDirectories(dataPath);
		            FileUtils.cleanDirectory(dataPath.toFile());
		            for (Dataset dataset : project.getData()) {
		            	setSubtask(dataset.getName());
		                saveDataset(dataset, dataPath);
		                numSaved++;
		                setProgress(numSaved * 100 / numDatasets);
		            }

		            // Save geometry
		            Files.createDirectories(geometryPath);
		            FileUtils.cleanDirectory(geometryPath.toFile());
		            for (Dataset dataset : project.getGeometry()) {
		            	setSubtask(dataset.getName());
		                saveDataset(dataset, geometryPath);
		                numSaved++;
		                setProgress(numSaved * 100 / numDatasets);
		            }

		            // Save results
		            Files.createDirectories(resultsPath);
		            FileUtils.cleanDirectory(resultsPath.toFile());
		            for (Dataset dataset : project.getResults()) {
		            	printAxes(dataset);
		            	setSubtask(dataset.getName());
		                saveDataset(dataset, resultsPath);
		                numSaved++;
		                setProgress(numSaved * 100 / numDatasets);
		            }

		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		        
		        currentProjectRoot = root;
		    	return null;
			}
			
			@Override
			protected void process(List<String> chunks) {
				super.process(chunks);
			}
    	};
    	
    	return task;
    }

    public Task<Void, String> save(Project project) {
        return saveAs(project, currentProjectRoot);
    }

    private void saveDataset(Dataset dataset, Path path) throws IOException {

        Dataset datasetToSave = dataset.duplicate();

        // SCIFIO cannot save 1-bit images so we must convert to 8-bit
        if (datasetToSave.firstElement() instanceof BitType) {
            @SuppressWarnings("unchecked")
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
    	System.out.println(dataset.getName() + " axes:");
    	for (int i = 0; i < dataset.numDimensions(); i++) {
    		System.out.println(i + ": " + dataset.axis(i).type());
    	}
    }
}
