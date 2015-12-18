package org.vcell.vis.vtk;

import java.io.File;
import java.util.ArrayList;

import org.vcell.util.document.KeyValue;
import org.vcell.vis.io.CartesianMeshFileReader;
import org.vcell.vis.io.VCellSimFiles;
import org.vcell.vis.mapping.vcell.CartesianMeshVtkFileWriter;
import org.vcell.vis.vcell.CartesianMesh;

import cbit.vcell.resource.ResourceUtil;

public class VCellDataTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ResourceUtil.setNativeLibraryDirectory();

			KeyValue simKey = new KeyValue("1661241954");
			String pathPrefix = "C:\\Users\\schaff\\.vcell\\simdata\\user\\";
			
			File meshFile = new File(pathPrefix + "SimID_"+simKey+"_0_.mesh");
			File meshMetricsFile = new File(pathPrefix + "SimID_"+simKey+"_0_.meshmetrics");
			File subdomainFile = new File(pathPrefix + "SimID_"+simKey+"_0_.subdomains");
			File logFile = new File(pathPrefix + "SimID_"+simKey+"_0_.log");
			File zipFile = new File(pathPrefix + "SimID_"+simKey+"_0_00.zip");
			File postprocessingFile = new File(pathPrefix + "SimID_"+simKey+"_0_.hdf5");
			
			VCellSimFiles vcellFiles = new VCellSimFiles(simKey,0,meshFile,meshMetricsFile,subdomainFile,logFile,postprocessingFile);
			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0000.sim"),0.0);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0001.sim"),0.05);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0002.sim"),0.10);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0003.sim"),0.15);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0004.sim"),0.20);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0005.sim"),0.25);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0006.sim"),0.30);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0007.sim"),0.35);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0008.sim"),0.40);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0009.sim"),0.45);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0010.sim"),0.50);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0011.sim"),0.55);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0012.sim"),0.60);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0013.sim"),0.65);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0014.sim"),0.70);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0015.sim"),0.75);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0016.sim"),0.80);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0017.sim"),0.85);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0018.sim"),0.90);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0019.sim"),0.95);
//			vcellFiles.addDataFileEntry(zipFile,new File("SimID_"+simKey+"_0_0020.sim"),1.0);

			
			// process each domain separately (only have to process the mesh once for each one)
			CartesianMeshVtkFileWriter cartesianMeshVtkFileWriter = new CartesianMeshVtkFileWriter();
			File destinationDirectory = new File("C:\\Developer\\eclipse\\workspace\\VCell_5.3_visfull\\VtkData\\");
			File[] generatedFiles = cartesianMeshVtkFileWriter.writeVtuExportFiles(vcellFiles, destinationDirectory, null);

			boolean bDisplay = true;
			
			if (bDisplay){
				CartesianMeshFileReader reader = new CartesianMeshFileReader();
				CartesianMesh mesh = reader.readFromFiles(vcellFiles);
				ArrayList<String> allDomainNames = new ArrayList<String>();
				allDomainNames.addAll(mesh.getVolumeDomainNames());
				allDomainNames.addAll(mesh.getMembraneDomainNames());
				for (String domain : allDomainNames){
					for (int timeIndex=0;timeIndex<vcellFiles.getTimes().size();timeIndex++){
//						String filename = new File(destinationDirectory,vcellFiles.getCannonicalFilePrefix(domain,timeIndex)+".vtu").getAbsolutePath();
//						vtkUnstructuredGrid vtkgrid = vtkGridUtils.read(filename);
//						vtkgrid.BuildLinks();
//	
//						String varName0 = vtkgrid.GetCellData().GetArrayName(0);
//						String varName1 = vtkgrid.GetCellData().GetArrayName(1);
//						SimpleVTKViewer simpleViewer = new SimpleVTKViewer();
//						simpleViewer.showGrid(vtkgrid, varName0, varName1);
						Thread.sleep(1000);
					}
				}
			}

//			CartesianMeshFileReader reader = new CartesianMeshFileReader();
//			CartesianMesh mesh = reader.readFromFiles(vcellFiles);
//			CartesianMeshMapping cartesianMeshMapping = new CartesianMeshMapping();
//			
//			//String domainName = mesh.meshRegionInfo.getVolumeDomainNames().get(1);
//			String domainName = mesh.meshRegionInfo.getVolumeDomainNames().get(0);
//			
//			System.out.println("making visMesh for domain "+domainName+" of ["+mesh.meshRegionInfo.getVolumeDomainNames()+"]");
//			VisMesh visMesh = cartesianMeshMapping.fromMeshData(mesh, domainName);
//			final int numElements = visMesh.getPolyhedra().size();
//			final double[] data1 = new double[numElements];
//			final double[] data2 = new double[numElements];
//			
//			for (int i=0;i<numElements;i++){
//				data1[i] = Math.sin(i/10000.0);
//				data2[i] = Math.cos(i/10000.0);
//			}
//			VisMeshData visData = new VisMeshData() {
//				private String[] names = new String[] { "data1", "data2" };
//				
//				@Override
//				public String[] getVarNames() {
//					return new String[] { "data1", "data2" };
//				}
//				
//				@Override
//				public double getTime() {
//					return 0.0;
//				}
//				
//				@Override
//				public double[] getData(String var) {
//					if (var.equals("data1")){
//						return data1;
//					}else if (var.equals("data2")){
//						return data2;
//					}else{
//						throw new RuntimeException("var "+var+" not found");
//					}
//				}
//			};
//
//			VisDomain visDomain = new VisDomain("domain1",visMesh,visData);
//			VtkGridUtils vtkGridUtils = new VtkGridUtils();
//			vtkUnstructuredGrid vtkgrid = vtkGridUtils.getVtkGrid(visDomain);
//			vtkgrid = TestVTK.testWindowedSincPolyDataFilter(vtkgrid);
//			String filenameBinary = "testBinary.vtu";
//			vtkGridUtils.writeXML(vtkgrid, filenameBinary, false);
//			vtkgrid = vtkGridUtils.read(filenameBinary);
//			//vtkgrid.s
//			vtkgrid.BuildLinks();
//			SimpleVTKViewer simpleViewer = new SimpleVTKViewer();
//			String[] varNames = visDomain.getVisMeshData().getVarNames();
//			simpleViewer.showGrid(vtkgrid, varNames[0], varNames[1]);
			
			
			System.out.println("ran");
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

}
