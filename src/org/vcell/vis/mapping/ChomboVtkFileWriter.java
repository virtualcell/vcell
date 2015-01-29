package org.vcell.vis.mapping;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.vcell.util.FileUtils;
import org.vcell.vis.chombo.ChomboDataset;
import org.vcell.vis.chombo.ChomboDataset.ChomboDomain;
import org.vcell.vis.chombo.ChomboMeshData;
import org.vcell.vis.io.ChomboFileReader;
import org.vcell.vis.io.ChomboFiles;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuFileContainer.VtuVarInfo;
import org.vcell.vis.vismesh.VisDataset;
import org.vcell.vis.vismesh.VisMesh;
import org.vcell.vis.vismesh.VisMeshData;
import org.vcell.vis.vtk.VtkGridUtils;

import cbit.vcell.math.VariableType.VariableDomain;
import vtk.vtkUnstructuredGrid;

public class ChomboVtkFileWriter {
	
	public interface ProgressListener {
		public void progress(double percentDone);
	}

	public VtuFileContainer getVtuMeshFiles(ChomboFiles chomboFiles, double time, int timeIndex) throws Exception{
		VtkGridUtils vtkGridUtils = new VtkGridUtils();
		VtuFileContainer vtuFileContainer = new VtuFileContainer();
		HashMap<String, VisMesh> domainMeshMap = new HashMap<String, VisMesh>();
		ChomboDataset chomboDataset = ChomboFileReader.readDataset(chomboFiles,timeIndex);
		for (ChomboDomain chomboDomain : chomboDataset.getDomains()){
			ChomboMeshData chomboMeshData = chomboDomain.getChomboMeshData();
			ChomboMeshMapping chomboMeshMapping = new ChomboMeshMapping();
			VisMesh visMesh = domainMeshMap.get(chomboDomain.getName());
			if (visMesh == null){
				visMesh = chomboMeshMapping.fromMeshData(chomboMeshData, chomboDomain);
				domainMeshMap.put(chomboDomain.getName(),visMesh);
			}
			VisMeshData visMeshData = new ChomboVisMeshData(chomboMeshData, visMesh);
			VisDataset.VisDomain visDomain = new VisDataset.VisDomain(chomboDomain.getName(),visMesh,visMeshData);
			vtkUnstructuredGrid vtkgrid = vtkGridUtils.getVtkGrid(visDomain);
			String domainName = visDomain.getName();
			File tempMeshFile = File.createTempFile("TempMesh", ".vtu");
			try {
				vtkGridUtils.write(vtkgrid, tempMeshFile.getPath());
				vtkgrid.Delete(); // if needed for garbage collection
				byte[] vtuMeshContents = FileUtils.readByteArrayFromFile(tempMeshFile);
				vtuFileContainer.addVtuMesh(new VtuFileContainer.VtuMesh(domainName, timeIndex, vtuMeshContents));
			}finally{
				tempMeshFile.delete();
			}
			
			for (String varName : visDomain.getVisMeshData().getVarNames()){
				String displayName = varName;
				if (!displayName.contains("::")){
					displayName = visDomain.getName()+"::"+displayName;
				}
				vtuFileContainer.addVtuVarInfo(new VtuVarInfo(varName.replace("::", VtkGridUtils.VTKVAR_DOMAINSEPARATOR), displayName, visDomain.getName(), VariableDomain.VARIABLEDOMAIN_VOLUME));
			}
			
			if (chomboMeshData.getVCellSolutions().size() > 0)
			{
				vtkgrid = vtkGridUtils.constructVCellVtkGrid(visMesh, chomboMeshData);
				tempMeshFile = File.createTempFile("TempMesh", ".vtu");
				String membraneDomainName = domainName+"_VCellVariableSolution";
				try {
					vtkGridUtils.write(vtkgrid, tempMeshFile.getPath());
					vtkgrid.Delete();
					byte[] vtuMeshContents = FileUtils.readByteArrayFromFile(tempMeshFile);
					vtuFileContainer.addVtuMesh(new VtuFileContainer.VtuMesh(membraneDomainName, timeIndex, vtuMeshContents));
				}finally{
					tempMeshFile.delete();
				}
				
				for (String varName : chomboMeshData.getAllNames()){
					String displayName = varName;
					if (!displayName.contains("::")){
						displayName = visDomain.getName()+"::"+displayName;
					}
					vtuFileContainer.addVtuVarInfo(new VtuVarInfo(varName.replace("::", VtkGridUtils.VTKVAR_DOMAINSEPARATOR), displayName, membraneDomainName, VariableDomain.VARIABLEDOMAIN_MEMBRANE));
				}
			}
		}
		return vtuFileContainer;
	}

	public File[] writeFiles(ChomboFiles chomboFiles, File destinationDirectory, ProgressListener progressListener) throws Exception{
		if (destinationDirectory==null || !destinationDirectory.isDirectory()){
			throw new RuntimeException("destinationDirectory '"+destinationDirectory+" not valid");
		}
		ArrayList<File> files = new ArrayList<File>();
		int numFiles = chomboFiles.getDomains().size() * chomboFiles.getTimeIndices().size();
		VtkGridUtils vtkGridUtils = new VtkGridUtils();
		HashMap<String, VisMesh> domainMeshMap = new HashMap<String, VisMesh>();
		int filesProcessed = 0;
		for (int timeIndex : chomboFiles.getTimeIndices()){
			ChomboDataset chomboDataset = ChomboFileReader.readDataset(chomboFiles,timeIndex);
			for (ChomboDomain chomboDomain : chomboDataset.getDomains()){
				ChomboMeshData chomboMeshData = chomboDomain.getChomboMeshData();
				ChomboMeshMapping chomboMeshMapping = new ChomboMeshMapping();
				VisMesh visMesh = domainMeshMap.get(chomboDomain.getName());
				if (visMesh == null){
					visMesh = chomboMeshMapping.fromMeshData(chomboMeshData, chomboDomain);
					domainMeshMap.put(chomboDomain.getName(),visMesh);
				}
				VisMeshData visMeshData = new ChomboVisMeshData(chomboMeshData, visMesh);
				VisDataset.VisDomain visDomain = new VisDataset.VisDomain(chomboDomain.getName(),visMesh,visMeshData);
				vtkUnstructuredGrid vtkgrid = vtkGridUtils.getVtkGrid(visDomain);
				File file = new File(destinationDirectory,chomboFiles.getCannonicalFilePrefix(visDomain.getName(),timeIndex)+".vtu");
				vtkGridUtils.write(vtkgrid, file.getPath());
				vtkgrid.Delete(); // if needed for garbage collection
				files.add(file);
				
				if (chomboMeshData.getVCellSolutions().size() > 0)
				{
					String filename = chomboFiles.getSimID() + "_" + chomboFiles.getJobIndex() + "_VCellVariableSolution_" + String.format("%06d",timeIndex);
					File memfile = new File(destinationDirectory,filename+".vtu");
					vtkgrid = vtkGridUtils.constructVCellVtkGrid(visMesh, chomboMeshData);
					vtkGridUtils.write(vtkgrid, memfile.getPath());
					vtkgrid.Delete();
					files.add(memfile);
				}

				filesProcessed++;
				if (progressListener!=null){
					progressListener.progress(((double)filesProcessed)/numFiles);
				}
			}
		}
		return files.toArray(new File[0]);

	}
}
