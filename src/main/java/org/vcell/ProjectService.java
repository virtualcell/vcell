package org.vcell;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.vfs2.FileName;
import org.sbml.jsbml.SBMLDocument;
import org.sbml.jsbml.SBMLException;
import org.sbml.jsbml.SBMLReader;
import org.sbml.jsbml.SBMLWriter;

import io.scif.services.DatasetIOService;
import net.imagej.Dataset;
import net.imagej.ImgPlus;
import net.imagej.ops.OpService;
import net.imglib2.FinalInterval;
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

				String rootPath = root.getAbsolutePath();
		        File[] dataFiles = Paths.get(rootPath, "data").toFile().listFiles();
		        File[] geometryFiles = Paths.get(rootPath, "geometry").toFile().listFiles();
		        File[] modelDirectories = Paths.get(rootPath, "models").toFile().listFiles();
		        File[] resultsFiles = Paths.get(rootPath, "results").toFile().listFiles();
		        
		        int numFiles = dataFiles.length + geometryFiles.length + modelDirectories.length + resultsFiles.length;
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
		        
		        if (modelDirectories != null) {
		        	for (File modelDirectory : modelDirectories) {
		        		setSubtask(modelDirectory.getName());
		        		
		        		SBMLDocument sbmlDocument = null;
		        		BufferedImage image = null;
		        		File[] modelFiles = modelDirectory.listFiles();
		        		System.out.println(modelFiles.length);
		        		if (modelFiles.length > 2) continue; // Invalid model directory
		        		
		        		for (File modelFile : modelFiles) {
		        			System.out.println(modelFile.getName());
		        			if (FilenameUtils.getExtension(modelFile.getName()).equals("xml")) {
		        				sbmlDocument = new SBMLReader().readSBML(modelFile);
		        				System.out.println("Loaded sbml");
		        			} else if (FilenameUtils.getExtension(modelFile.getName()).equals("png")) {
		        				image = ImageIO.read(modelFile);
		        				System.out.println("Loaded image");
		        			}
		        		}
		        		
		        		if (sbmlDocument != null) {
		        			VCellModel vCellModel = new VCellModel(modelDirectory.getName(), null, sbmlDocument);
		        			vCellModel.setImage(image);
		        			project.getModels().add(vCellModel);
		        			System.out.println("Added model");
		        		}
		        		
		        		numLoaded++;
		        		setProgress(numLoaded * 100 / numFiles);
		        	}
		        }

		        if (resultsFiles != null) {
		            for (File resultsFile : resultsFiles) {
		                try {
		                	setSubtask(resultsFile.getName());
		                	Dataset results = datasetIOService.open(resultsFile.getAbsolutePath());
		                	
		                	// Loading 1-dimensional tif images adds a dimension 
		                	// so must crop out empty dimensions
		                	@SuppressWarnings("unchecked")
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
				
				int numToSave = project.getData().size() 
						+ project.getGeometry().size() 
						+ project.getModels().size() 
						+ project.getResults().size();
		    	int numSaved = 0;
		    	
		    	String rootPath = root.getAbsolutePath();
		        Path dataPath = Paths.get(rootPath, "data");
		        Path geometryPath = Paths.get(rootPath, "geometry");
		        Path modelsPath = Paths.get(rootPath, "models");
		        Path resultsPath = Paths.get(rootPath, "results");

		        try {

		            // Save data
		            Files.createDirectories(dataPath);
		            FileUtils.cleanDirectory(dataPath.toFile());
		            for (Dataset dataset : project.getData()) {
		            	setSubtask(dataset.getName());
		                saveDataset(dataset, dataPath);
		                numSaved++;
		                setProgress(numSaved * 100 / numToSave);
		            }

		            // Save geometry
		            Files.createDirectories(geometryPath);
		            FileUtils.cleanDirectory(geometryPath.toFile());
		            for (Dataset dataset : project.getGeometry()) {
		            	setSubtask(dataset.getName());
		                saveDataset(dataset, geometryPath);
		                numSaved++;
		                setProgress(numSaved * 100 / numToSave);
		            }
		            
		            // Save models
		            Files.createDirectories(modelsPath);
		            FileUtils.cleanDirectory(modelsPath.toFile());
		            for (VCellModel model : project.getModels()) {
		            	setSubtask(model.getName());
		            	saveModel(model, modelsPath);
		            	numSaved++;
		            	setProgress(numSaved * 100 / numToSave);
		            }

		            // Save results
		            Files.createDirectories(resultsPath);
		            FileUtils.cleanDirectory(resultsPath.toFile());
		            for (Dataset dataset : project.getResults()) {
		            	setSubtask(dataset.getName());
		                saveDataset(dataset, resultsPath);
		                numSaved++;
		                setProgress(numSaved * 100 / numToSave);
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
    }
    
    private void saveModel(VCellModel model, Path path) throws IOException, SBMLException, XMLStreamException {
    	
    	Path modelPath = Paths.get(path.toString(), model.getName());
    	Files.createDirectories(modelPath);
        FileUtils.cleanDirectory(modelPath.toFile());
    	
    	Path sbmlPath = Paths.get(modelPath.toString(), model.getName() + ".xml");
    	new SBMLWriter().write(model.getSbmlDocument(), sbmlPath.toFile());
    	
    	Path imagePath = Paths.get(modelPath.toString(), model.getName() + ".png");
    	ImageIO.write(model.getImage(), "png", imagePath.toFile());
    }
    
    // Helper method for debugging
//    private void printAxes(Dataset dataset) {
//    	System.out.println(dataset.getName() + " axes:");
//    	for (int i = 0; i < dataset.numDimensions(); i++) {
//    		System.out.println(i + ": " + dataset.axis(i).type());
//    	}
//    }
}
