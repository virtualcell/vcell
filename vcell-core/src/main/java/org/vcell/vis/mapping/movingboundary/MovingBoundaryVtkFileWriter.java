package org.vcell.vis.mapping.movingboundary;

import java.io.File;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import org.vcell.util.DataAccessException;
import org.vcell.util.FileUtils;
import org.vcell.vis.io.ChomboFiles;
import org.vcell.vis.io.MovingBoundarySimFiles;
import org.vcell.vis.io.VtuFileContainer;
import org.vcell.vis.io.VtuVarInfo;
import org.vcell.vis.mapping.movingboundary.MovingBoundaryMeshMapping.DomainType;
import org.vcell.vis.vismesh.thrift.MovingBoundaryIndexData;
import org.vcell.vis.vismesh.thrift.MovingBoundaryVolumeIndex;
import org.vcell.vis.vismesh.thrift.VisMesh;
import org.vcell.vis.vtk.VisMeshUtils;
import org.vcell.vis.vtk.VtkService;

import cbit.vcell.math.MathException;
import cbit.vcell.math.VariableType.VariableDomain;
import cbit.vcell.simdata.SimDataConstants;
import cbit.vcell.solvers.mb.MovingBoundaryReader;
import cbit.vcell.solvers.mb.MovingBoundaryTypes.Element;
import cbit.vcell.solvers.mb.MovingBoundaryTypes.Element.Position;
import cbit.vcell.solvers.mb.MovingBoundaryTypes.Plane;

public class MovingBoundaryVtkFileWriter {
	
	public static final String MEMBRANE_DOMAIN_SUFFIX = "_Membrane";

	public interface ProgressListener {
		public void progress(double percentDone);
	}
	


	public File[] writeVtuExportFiles(ChomboFiles chomboFiles, File destinationDirectory, ProgressListener progressListener) throws Exception{
		throw new RuntimeException("VTK export not implemnented");
	}

	public double[] getVtuMeshData(MovingBoundarySimFiles movingBoundarySimFiles, double[] fullRasterData, File destinationDirectory, VtuVarInfo var, int timeIndex) throws Exception {

		MovingBoundaryReader reader = new MovingBoundaryReader(movingBoundarySimFiles.hdf5OutputFile.getAbsolutePath());
		Plane plane = reader.getPlane(timeIndex);
		int numX = plane.getSizeX();
		int numY = plane.getSizeY();

		DoubleBuffer buffer = DoubleBuffer.wrap(new double[numX*numY]);
		for (int j=0;j<numY;j++){
			for (int i=0;i<numX;i++){
				Element element = plane.get(i, j);
				if (element != null){
					if (var.variableDomain==VariableDomain.VARIABLEDOMAIN_VOLUME && var.domainName.equals(reader.getFakeInsideDomainName())){
						if (element.position==Position.BOUNDARY || element.position==Position.INSIDE){
							buffer.put(fullRasterData[i+numX*j]);
							//buffer.put(species.mass);
							//buffer.put(i+j*numX);
							//buffer.put(i);
							//buffer.put(j);
						}else{
							// skip this element, it is "outside" domain.
						}
					}
				}else{
					System.out.println("found empty species at index("+i+","+j+")");
				}
			}
		}
		double[] volumeData = buffer.array();
		
		double[] data = null;
		
		switch (var.variableDomain){
		
			case VARIABLEDOMAIN_CONTOUR:{
				break;
			}
			case VARIABLEDOMAIN_MEMBRANE: {
				/*
				MovingBoundaryIndexData movingBoundaryIndexData = VisMeshUtils.readChomboIndexData(chomboIndexDataFile);
				List<ChomboVisMembraneIndex> cellIndices = new ArrayList<ChomboVisMembraneIndex>();
				for (ChomboSurfaceIndex chomboSurfaceIndex : chomboIndexData.chomboSurfaceIndices){
					cellIndices.add(new SimpleChomboVisMembraneIndex(chomboSurfaceIndex.index));
				}
				data = chomboMeshData.getMembraneCellData(var.name, cellIndices);
				*/
				break;
			}
			case VARIABLEDOMAIN_NONSPATIAL:{
				break;
			}
			case VARIABLEDOMAIN_POSTPROCESSING:{
				break;
			}
			case VARIABLEDOMAIN_UNKNOWN:{
				break;
			}
			case VARIABLEDOMAIN_VOLUME: {
				MovingBoundaryIndexData movingBoundaryIndexData = getMovingBoundaryIndexData(movingBoundarySimFiles, destinationDirectory, var, timeIndex);
				if (var.functionExpression!=null){
					throw new UnsupportedOperationException();
				}else{
					int numElements = movingBoundaryIndexData.movingBoundaryVolumeIndices.size();
					data = new double[numElements];
					int i=0;
					for (MovingBoundaryVolumeIndex mbvIndex : movingBoundaryIndexData.movingBoundaryVolumeIndices){
						data[i++] = volumeData[mbvIndex.index];
					}
				}
				break;
			}
			default:{
				throw new RuntimeException("unsupported variable type "+var.variableDomain.name()+" for variable "+var.name);
			}
		}
		return data;
	}
	
	private File getMovingBoundaryIndexDataFileName(MovingBoundarySimFiles movingBoundarySimFiles, String domainName, int timeIndex) {
		File directory = movingBoundarySimFiles.hdf5OutputFile.getParentFile();
		return new File(directory,movingBoundarySimFiles.getCannonicalFilePrefix(domainName) + "_" + String.format("%06d", timeIndex) + SimDataConstants.MOVINGBOUNDARYINDEX_FILE_EXTENSION);
	}

	private File getVtuMeshFileName(File directory, MovingBoundarySimFiles movingBoundarySimFiles, String domainName, int timeIndex) {
		return new File(directory,movingBoundarySimFiles.getCannonicalFilePrefix(domainName) + "_" + String.format("%06d" , timeIndex) +".vtu");
	}
	
	public MovingBoundaryIndexData getMovingBoundaryIndexData(MovingBoundarySimFiles movingBoundarySimFiles, File destinationDirectory, VtuVarInfo var, int timeIndex) throws Exception {
		String domainName = var.domainName;
		File movingBoundaryIndexDataFile = getMovingBoundaryIndexDataFileName(movingBoundarySimFiles, domainName, timeIndex);
		if (!movingBoundaryIndexDataFile.exists()){
			writeEmptyMeshFiles(movingBoundarySimFiles, timeIndex, destinationDirectory, null);
		}
		MovingBoundaryIndexData movingBoundaryIndexData = VisMeshUtils.readMovingBoundaryIndexData(movingBoundaryIndexDataFile);
		return movingBoundaryIndexData;
	}

	
	public VtuFileContainer getEmptyVtuMeshFiles(MovingBoundarySimFiles movingBoundaryFiles, int timeIndex, File destinationDirectory) throws IOException, MathException, DataAccessException {
		//
		// find mesh for each file at time 0
		//
		File hdf5OutputFile = movingBoundaryFiles.hdf5OutputFile;
		MovingBoundaryReader reader = new MovingBoundaryReader(hdf5OutputFile.getAbsolutePath());

		//
		// for each domain in cartesian mesh, get the mesh file
		//
		ArrayList<String> domains = new ArrayList<String>();
		domains.add(reader.getFakeInsideDomainName());
//		domains.add(reader.getFakeOutsideDomainName());
//		domains.add(reader.getFakeMembraneDomainName());
		
		boolean bMeshFileMissing = false;
		for (String domainName : domains){
			File file = getVtuMeshFileName(destinationDirectory, movingBoundaryFiles, domainName, timeIndex);
			if (!file.exists()){
				bMeshFileMissing = true;
			}
		}
		
		if (bMeshFileMissing){
			writeEmptyMeshFiles(movingBoundaryFiles, timeIndex, destinationDirectory, null);
		}

		VtuFileContainer vtuFileContainer = new VtuFileContainer();
		for (int i=0;i<domains.size();i++){
			String domainName = domains.get(i);
			File vtuMeshFile = getVtuMeshFileName(destinationDirectory, movingBoundaryFiles, domainName, timeIndex);
			byte[] vtuMeshFileContents = FileUtils.readByteArrayFromFile(vtuMeshFile);
			double time = reader.getTimeInfo().generationTimes.get(timeIndex);
			vtuFileContainer.addVtuMesh(new VtuFileContainer.VtuMesh(domainName, time, vtuMeshFileContents));
		}
		return vtuFileContainer;
	}

	public File[] writeEmptyMeshFiles(MovingBoundarySimFiles movingBoundaryFiles, int timeIndex, File destinationDirectory, ProgressListener progressListener) throws IOException, MathException, DataAccessException {
		
		HashMap<String, VisMesh> domainMeshMap = new HashMap<String, VisMesh>();
		ArrayList<String> allDomainNames = new ArrayList<String>();

		
		File hdf5OutputFile = movingBoundaryFiles.hdf5OutputFile;
		MovingBoundaryReader reader = new MovingBoundaryReader(hdf5OutputFile.getAbsolutePath());
			
		//
		// for inside volume domain in mesh, extract the associated VisMesh
		//
		{
		String insideDomainName = reader.getFakeInsideDomainName();
		MovingBoundaryMeshMapping insideMeshMapping = new MovingBoundaryMeshMapping();
		VisMesh insideVisMesh = insideMeshMapping.fromReader(reader, DomainType.INSIDE, timeIndex);
		domainMeshMap.put(insideDomainName, insideVisMesh);
		allDomainNames.add(insideDomainName);
		}
//		{
//		String outsideDomainName = reader.getFakeOutsideDomainName();
//		MovingBoundaryMeshMapping outsideMeshMapping = new MovingBoundaryMeshMapping();
//		VisMesh outsideVisMesh = outsideMeshMapping.fromReader(reader, DomainType.OUTSIDE, timeIndex);
//		domainMeshMap.put(outsideDomainName, outsideVisMesh);
//		allDomainNames.add(outsideDomainName);
//		}
//		{
//		String membraneDomainName = reader.getFakeMembraneDomainName();
//		MovingBoundaryMeshMapping membraneMeshMapping = new MovingBoundaryMeshMapping();
//		VisMesh membraneVisMesh = membraneMeshMapping.fromReader(reader, DomainType.MEMBRANE, timeIndex);
//		domainMeshMap.put(membraneDomainName, membraneVisMesh);
//		allDomainNames.add(membraneDomainName);
//		}
						
		int numFiles = allDomainNames.size();
		ArrayList<File> files = new ArrayList<File>();
		int filesProcessed = 0;
			
		for (final String domainName : allDomainNames){
			//
			// find the globalIndexes for each domain element - use this to extract the domain-only data.
			//
			VisMesh visMesh = domainMeshMap.get(domainName);
			File vtuFile = getVtuMeshFileName(destinationDirectory, movingBoundaryFiles, domainName, timeIndex);
			File mbIndexDataFileName = getMovingBoundaryIndexDataFileName(movingBoundaryFiles, domainName, timeIndex);
			VtkService.getInstance().writeMovingBoundaryVtkGridAndIndexData(visMesh, domainName, vtuFile, mbIndexDataFileName);
			files.add(vtuFile);
			filesProcessed++;
			if (progressListener!=null){
				progressListener.progress(((double)filesProcessed)/numFiles);
			}
		}
		return files.toArray(new File[0]);

	}

}
