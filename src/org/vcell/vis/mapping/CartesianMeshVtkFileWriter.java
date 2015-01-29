package org.vcell.vis.mapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.vcell.util.FileUtils;
import org.vcell.vis.io.CartesianMeshFileReader;
import org.vcell.vis.io.DataSet;
import org.vcell.vis.io.VCellSimFiles;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuFileContainer.VtuVarInfo;
import org.vcell.vis.vcell.CartesianMesh;
import org.vcell.vis.vismesh.VisDataset;
import org.vcell.vis.vismesh.VisMesh;
import org.vcell.vis.vismesh.VisMeshData;
import org.vcell.vis.vismesh.VisPolygon;
import org.vcell.vis.vismesh.VisPolyhedron;
import org.vcell.vis.vtk.VtkGridUtils;

import cbit.vcell.math.VariableType.VariableDomain;
import vtk.vtkUnstructuredGrid;


public class CartesianMeshVtkFileWriter {
	
	public interface ProgressListener {
		public void progress(double percentDone);
	}

	public VtuFileContainer getVtuMeshFiles(VCellSimFiles vcellFiles, final double time) throws Exception{
		//
		// read the simplified cartesian mesh
		//
		CartesianMeshFileReader reader = new CartesianMeshFileReader();
		CartesianMesh mesh = reader.readFromFiles(vcellFiles);
		final int numVolumeElements = mesh.getSize().getXYZ();
		final int numVolumeRegions = mesh.getNumVolumeRegions();
		final int numMembraneRegions = mesh.getNumMembraneRegions();
		
		//
		// for each volume domain in mesh, extract the associated VisMesh
		//
		List<String> volumeDomainNames = mesh.getVolumeDomainNames();
		HashMap<String, VisMesh> domainMeshMap = new HashMap<String, VisMesh>();
		for (String volumeDomainName : volumeDomainNames){
			CartesianMeshMapping meshMapping = new CartesianMeshMapping();
			VisMesh visMesh = meshMapping.fromMeshData(mesh, volumeDomainName, true);
			domainMeshMap.put(volumeDomainName, visMesh);
		}
		
		//
		// for each membrane domain in mesh, extract the associated VisMesh
		//
		List<String> membraneDomainNames = mesh.getMembraneDomainNames();
		for (String membraneDomainName : membraneDomainNames){
			CartesianMeshMapping meshMapping = new CartesianMeshMapping();
			VisMesh visMesh = meshMapping.fromMeshData(mesh, membraneDomainName, false);
			domainMeshMap.put(membraneDomainName, visMesh);
		}
		
		ArrayList<String> allDomainNames = new ArrayList<String>();
		allDomainNames.addAll(volumeDomainNames);
		allDomainNames.addAll(membraneDomainNames);
		
		final File zipFile = vcellFiles.getZipFile(time);
		File pdeFile = vcellFiles.getZipEntry(time);

		final DataSet dataSet = new DataSet();
		dataSet.read(pdeFile, zipFile);
		String[] dataNames = dataSet.getDataNames();
		
		VtuFileContainer vtuFileContainer = new VtuFileContainer();
		for (String anyDomain : allDomainNames){
			final boolean bVolume = volumeDomainNames.contains(anyDomain);
			//
			// find the globalIndexes for each domain element - use this to extract the domain-only data.
			//
			VisMesh visMesh = domainMeshMap.get(anyDomain);
			int tmpNumCells;
			int[] tmpRegionIndices;
			int[] tmpGlobalIndices;
			if (bVolume){
				if (visMesh.getDimension()==2){
					List<VisPolygon> polygons = visMesh.getPolygons();
					tmpNumCells = polygons.size();
					tmpGlobalIndices = new int[tmpNumCells];
					tmpRegionIndices = new int[tmpNumCells];
					int index = 0;
					for (VisPolygon poly : polygons){
						tmpGlobalIndices[index] = poly.getBoxIndex();
						tmpRegionIndices[index] = poly.getRegionIndex();
						index++;
					}
				}else if (visMesh.getDimension()==3){
					List<VisPolyhedron> polyhedra = visMesh.getPolyhedra();
					tmpNumCells = polyhedra.size();
					tmpGlobalIndices = new int[tmpNumCells];
					tmpRegionIndices = new int[tmpNumCells];
					int index = 0;
					for (VisPolyhedron poly : polyhedra){
						tmpGlobalIndices[index] = poly.getBoxIndex();
						tmpRegionIndices[index] = poly.getRegionIndex();
						index++;
					}
				}else{
					throw new RuntimeException("mesh dimension "+visMesh.getDimension()+" not supported for volume mesh VTK export");
				}
			}else{ // !bVolume
				if (visMesh.getDimension()==3){
					List<VisPolygon> polygons = visMesh.getPolygons();
					tmpNumCells = polygons.size();
					tmpGlobalIndices = new int[tmpNumCells];
					tmpRegionIndices = new int[tmpNumCells];
					int index = 0;
					for (VisPolygon poly : polygons){
						tmpGlobalIndices[index] = poly.getBoxIndex();
						tmpRegionIndices[index] = poly.getRegionIndex();
						index++;
					}
				}else if (visMesh.getDimension()==2){
					throw new RuntimeException("data for 2D mesh membranes not yet supported");
//						List<VisLine> lines = visMesh.getLines();
//						tmpNumCells = lines.size();
//						tmpGlobalIndices = new int[tmpNumCells];
//						tmpRegionIndices = new int[tmpNumCells];
//						int index = 0;
//						for (VisLine line : lines){
//							tmpGlobalIndices[index] = line.getBoxIndex();
//							tmpRegionIndices[index] = line.getRegionIndex();
//							index++;
//						}
				}else{
					throw new RuntimeException("mesh dimension "+visMesh.getDimension()+" not supported for volume mesh VTK export");
				}
			}
			final int[] volumeRegionIndices = tmpRegionIndices;
			final int[] globalIndices = tmpGlobalIndices;
			final int numCells = tmpNumCells;
			//
			// get the variables defined for this domain (always include the global index for now)
			//
			final ArrayList<String> domainVarNames = new ArrayList<String>();
			final String globalIndexVarName = anyDomain+"::globalIndex";
			final String regionIdVarName = anyDomain+"::regionId";
			for (String dataName : dataNames){
				if (dataName.contains("::")){
					if (dataName.startsWith(anyDomain+"::")){
						domainVarNames.add(dataName);
					}else{
						System.out.println("skipping variable "+dataName);
					}
				}else{
					domainVarNames.add(dataName);
				}
			}
			domainVarNames.add(globalIndexVarName);
			domainVarNames.add(regionIdVarName);
			
			VisMeshData visMeshData = new VisMeshData(){
				@Override
				public String[] getVarNames() {
					return domainVarNames.toArray(new String[domainVarNames.size()]);
				}

				@Override
				public double getTime() {
					return time;
				}

				@Override
				public double[] getData(String var) throws IOException {
					int sizeDomain = globalIndices.length;
					double[] domainData = new double[sizeDomain];

					if (var.equals(globalIndexVarName)){
						for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
							domainData[domainIndex] = globalIndices[domainIndex];
						}
					}else if (var.equals(regionIdVarName)){
						for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
							domainData[domainIndex] = volumeRegionIndices[domainIndex];
						}
					}else{
						double[] globalData = dataSet.getData(var, zipFile);
						if (!bVolume && (globalData.length == numMembraneRegions)){
							// skip for now.
							for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
								domainData[domainIndex] = globalData[0];
							}
						}else if (bVolume && (globalData.length == numVolumeRegions)){
							for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
								domainData[domainIndex] = globalData[volumeRegionIndices[domainIndex]];
							}
						}else if (globalData.length == numCells || globalData.length == numVolumeElements){
							for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
								domainData[domainIndex] = globalData[globalIndices[domainIndex]];
							}
						}else{
							for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
								domainData[domainIndex] = -9999999.0;
							}
						}
					}
					return domainData;
				}
				
			};
			
			VisDataset.VisDomain visDomain = new VisDataset.VisDomain(anyDomain,visMesh,visMeshData);
			VtkGridUtils vtkGridUtils = new VtkGridUtils();
			vtkUnstructuredGrid vtkgrid = vtkGridUtils.getVtkGrid(visDomain);
			vtkUnstructuredGrid vtkgridSmoothed = VtkGridUtils.smoothUnstructuredGridSurface(vtkgrid);
			File tempMeshFile = File.createTempFile("TempMeshFile", "vtu");
			try {
				vtkGridUtils.write(vtkgridSmoothed, tempMeshFile.getPath());
				vtkgrid.Delete(); 			// is needed for garbage collection? 
				//vtkgridSmoothed.Delete();	// is needed for garbage collection?   //superfluous with prior delete according to runtime errors with this uncommented?
				byte[] vtuMeshFileContents = FileUtils.readByteArrayFromFile(tempMeshFile);
				vtuFileContainer.addVtuMesh(new VtuFileContainer.VtuMesh(visDomain.getName(), time, vtuMeshFileContents));
				
				for (String varName : visDomain.getVisMeshData().getVarNames()){
					String displayName = varName;
					if (!displayName.contains("::")){
						displayName = visDomain.getName()+"::"+displayName;
					}
					VariableDomain varDomain = VariableDomain.VARIABLEDOMAIN_UNKNOWN;
					if (visDomain.getVisMesh().getDimension()==3){
						varDomain = VariableDomain.VARIABLEDOMAIN_VOLUME;
					}else if (visDomain.getVisMesh().getDimension()==2){
						varDomain = VariableDomain.VARIABLEDOMAIN_MEMBRANE;
					}
					vtuFileContainer.addVtuVarInfo(new VtuVarInfo(varName.replace("::", VtkGridUtils.VTKVAR_DOMAINSEPARATOR), displayName, visDomain.getName(), varDomain));
				}
			} finally {
				tempMeshFile.delete();
			}
			
		}
		return vtuFileContainer;
	}
	
	
	public File[] writeFiles(VCellSimFiles vcellFiles, File destinationDirectory, ProgressListener progressListener) throws Exception{
		if (destinationDirectory==null || !destinationDirectory.isDirectory()){
			throw new RuntimeException("destinationDirectory '"+destinationDirectory+" not valid");
		}
		//
		// read the simplified cartesian mesh
		//
		CartesianMeshFileReader reader = new CartesianMeshFileReader();
		CartesianMesh mesh = reader.readFromFiles(vcellFiles);
		final int numVolumeElements = mesh.getSize().getXYZ();
		final int numVolumeRegions = mesh.getNumVolumeRegions();
		final int numMembraneRegions = mesh.getNumMembraneRegions();
		
		//
		// for each volume domain in mesh, extract the associated VisMesh
		//
		List<String> volumeDomainNames = mesh.getVolumeDomainNames();
		HashMap<String, VisMesh> domainMeshMap = new HashMap<String, VisMesh>();
		for (String volumeDomainName : volumeDomainNames){
			CartesianMeshMapping meshMapping = new CartesianMeshMapping();
			VisMesh visMesh = meshMapping.fromMeshData(mesh, volumeDomainName, true);
			domainMeshMap.put(volumeDomainName, visMesh);
		}
		
		//
		// for each membrane domain in mesh, extract the associated VisMesh
		//
		List<String> membraneDomainNames = mesh.getMembraneDomainNames();
		for (String membraneDomainName : membraneDomainNames){
			CartesianMeshMapping meshMapping = new CartesianMeshMapping();
			VisMesh visMesh = meshMapping.fromMeshData(mesh, membraneDomainName, false);
			domainMeshMap.put(membraneDomainName, visMesh);
		}
		
		ArrayList<String> allDomainNames = new ArrayList<String>();
		allDomainNames.addAll(volumeDomainNames);
		allDomainNames.addAll(membraneDomainNames);
		
		int numFiles = vcellFiles.getTimes().size() * allDomainNames.size();
		ArrayList<File> files = new ArrayList<File>();
		int filesProcessed = 0;
		int timeIndex = 0;
		for (final double time : vcellFiles.getTimes()){
			final File zipFile = vcellFiles.getZipFile(time);
			File pdeFile = vcellFiles.getZipEntry(time);

			final DataSet dataSet = new DataSet();
			dataSet.read(pdeFile, zipFile);
			String[] dataNames = dataSet.getDataNames();
			
			for (String anyDomain : allDomainNames){
				final boolean bVolume = volumeDomainNames.contains(anyDomain);
				//
				// find the globalIndexes for each domain element - use this to extract the domain-only data.
				//
				VisMesh visMesh = domainMeshMap.get(anyDomain);
				int tmpNumCells;
				int[] tmpRegionIndices;
				int[] tmpGlobalIndices;
				if (bVolume){
					if (visMesh.getDimension()==2){
						List<VisPolygon> polygons = visMesh.getPolygons();
						tmpNumCells = polygons.size();
						tmpGlobalIndices = new int[tmpNumCells];
						tmpRegionIndices = new int[tmpNumCells];
						int index = 0;
						for (VisPolygon poly : polygons){
							tmpGlobalIndices[index] = poly.getBoxIndex();
							tmpRegionIndices[index] = poly.getRegionIndex();
							index++;
						}
					}else if (visMesh.getDimension()==3){
						List<VisPolyhedron> polyhedra = visMesh.getPolyhedra();
						tmpNumCells = polyhedra.size();
						tmpGlobalIndices = new int[tmpNumCells];
						tmpRegionIndices = new int[tmpNumCells];
						int index = 0;
						for (VisPolyhedron poly : polyhedra){
							tmpGlobalIndices[index] = poly.getBoxIndex();
							tmpRegionIndices[index] = poly.getRegionIndex();
							index++;
						}
					}else{
						throw new RuntimeException("mesh dimension "+visMesh.getDimension()+" not supported for volume mesh VTK export");
					}
				}else{ // !bVolume
					if (visMesh.getDimension()==3){
						List<VisPolygon> polygons = visMesh.getPolygons();
						tmpNumCells = polygons.size();
						tmpGlobalIndices = new int[tmpNumCells];
						tmpRegionIndices = new int[tmpNumCells];
						int index = 0;
						for (VisPolygon poly : polygons){
							tmpGlobalIndices[index] = poly.getBoxIndex();
							tmpRegionIndices[index] = poly.getRegionIndex();
							index++;
						}
					}else if (visMesh.getDimension()==2){
						throw new RuntimeException("data for 2D mesh membranes not yet supported");
//						List<VisLine> lines = visMesh.getLines();
//						tmpNumCells = lines.size();
//						tmpGlobalIndices = new int[tmpNumCells];
//						tmpRegionIndices = new int[tmpNumCells];
//						int index = 0;
//						for (VisLine line : lines){
//							tmpGlobalIndices[index] = line.getBoxIndex();
//							tmpRegionIndices[index] = line.getRegionIndex();
//							index++;
//						}
					}else{
						throw new RuntimeException("mesh dimension "+visMesh.getDimension()+" not supported for volume mesh VTK export");
					}
				}
				final int[] volumeRegionIndices = tmpRegionIndices;
				final int[] globalIndices = tmpGlobalIndices;
				final int numCells = tmpNumCells;
				//
				// get the variables defined for this domain (always include the global index for now)
				//
				final ArrayList<String> domainVarNames = new ArrayList<String>();
				final String globalIndexVarName = anyDomain+"::globalIndex";
				final String regionIdVarName = anyDomain+"::regionId";
				for (String dataName : dataNames){
					if (dataName.contains("::")){
						if (dataName.startsWith(anyDomain+"::")){
							domainVarNames.add(dataName);
						}else{
							System.out.println("skipping variable "+dataName);
						}
					}else{
						domainVarNames.add(dataName);
					}
				}
				domainVarNames.add(globalIndexVarName);
				domainVarNames.add(regionIdVarName);
				
				VisMeshData visMeshData = new VisMeshData(){
					@Override
					public String[] getVarNames() {
						return domainVarNames.toArray(new String[domainVarNames.size()]);
					}

					@Override
					public double getTime() {
						return time;
					}

					@Override
					public double[] getData(String var) throws IOException {
						int sizeDomain = globalIndices.length;
						double[] domainData = new double[sizeDomain];

						if (var.equals(globalIndexVarName)){
							for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
								domainData[domainIndex] = globalIndices[domainIndex];
							}
						}else if (var.equals(regionIdVarName)){
							for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
								domainData[domainIndex] = volumeRegionIndices[domainIndex];
							}
						}else{
							double[] globalData = dataSet.getData(var, zipFile);
							if (!bVolume && (globalData.length == numMembraneRegions)){
								// skip for now.
								for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
									domainData[domainIndex] = globalData[0];
								}
							}else if (bVolume && (globalData.length == numVolumeRegions)){
								for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
									domainData[domainIndex] = globalData[volumeRegionIndices[domainIndex]];
								}
							}else if (globalData.length == numCells || globalData.length == numVolumeElements){
								for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
									domainData[domainIndex] = globalData[globalIndices[domainIndex]];
								}
							}else{
								for (int domainIndex=0;domainIndex < sizeDomain; domainIndex++){
									domainData[domainIndex] = -9999999.0;
								}
							}
						}
						return domainData;
					}
					
				};
				
				VisDataset.VisDomain visDomain = new VisDataset.VisDomain(anyDomain,visMesh,visMeshData);
				VtkGridUtils vtkGridUtils = new VtkGridUtils();
				vtkUnstructuredGrid vtkgrid = vtkGridUtils.getVtkGrid(visDomain);
				vtkUnstructuredGrid vtkgridSmoothed = VtkGridUtils.smoothUnstructuredGridSurface(vtkgrid);
				File file = new File(destinationDirectory,vcellFiles.getCannonicalFilePrefix(visDomain.getName(),timeIndex)+".vtu");
				vtkGridUtils.write(vtkgridSmoothed, file.getPath());
				vtkgrid.Delete(); 			// is needed for garbage collection? 
				//vtkgridSmoothed.Delete();	// is needed for garbage collection?   //superfluous with prior delete according to runtime errors with this uncommented?
				files.add(file);
				filesProcessed++;
				if (progressListener!=null){
					progressListener.progress(((double)filesProcessed)/numFiles);
				}
			}
			timeIndex++;
		}
		return files.toArray(new File[0]);

	}
	
	

}
