package org.vcell.vis.vtk;


import java.io.File;
import java.util.List;

import org.vcell.vis.io.ChomboFiles;
import org.vcell.vis.mapping.chombo.ChomboVtkFileWriter;

import cbit.vcell.resource.NativeLib;
import cbit.vcell.resource.OperatingSystemInfo;
import cbit.vcell.resource.ResourceUtil;


public class MeshTest {
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			long beginLibLoadMS = System.currentTimeMillis();
			OperatingSystemInfo.getInstance();
			ResourceUtil.loadNativeLibraries("D:\\Developer\\eclipse\\workspace_refactor\\VCell_5.4_clean\\nativelibs\\win64");
			NativeLib.VTK.load();
			long endLibLoadMS = System.currentTimeMillis();
			System.out.println("native libs loaded, "+(endLibLoadMS-beginLibLoadMS)+" ms");
//			String meshFile = "C:\\Developer\\eclipse\\workspace\\VCell_5.3_visfull\\SimData\\SimID_85232385_0_.mesh.hdf5";
//			String[] volFiles = new String[] {
//					"C:\\Developer\\eclipse\\workspace\\VCell_5.3_visfull\\SimData\\SimID_85232385_0_000075.feature_EC.vol0.hdf5",
//					"C:\\Developer\\eclipse\\workspace\\VCell_5.3_visfull\\SimData\\SimID_85232385_0_000075.feature_cell.vol0.hdf5",
//			};
//			SimID_85823257_0_.functions
//			SimID_85823257_0_.fvinput
//			SimID_85823257_0_.hdf5
//			SimID_85823257_0_.log
//			SimID_85823257_0_.mesh.hdf5
//			SimID_85823257_0_.tid
//			SimID_85823257_0__0.simtask.xml
//			SimID_85823257_0__16.simtask.xml
//			SimID_85823257_0__exported
//			SimID_85823257_0__exported.zip
//			SimID_85823257_0__subdomain0.dmf
//			SimID_85823257_0__subdomain1.dmf
//			SimID_85823257_0_00.hdf5.zip
//			p:\\schaff\\advisoryMeeting_2014\\SimID_85823257_0_000000.feature_subdomain0.vol0.hdf5");
//			SimID_85823257_0_000000.feature_subdomain1.vol0.hdf5
//			SimID_85823257_0_000010.feature_subdomain0.vol0.hdf5
//			SimID_85823257_0_000010.feature_subdomain1.vol0.hdf5
//			SimID_85823257_0_000020.feature_subdomain0.vol0.hdf5
//			SimID_85823257_0_000020.feature_subdomain1.vol0.hdf5
//			SimID_85823257_0_000030.feature_subdomain0.vol0.hdf5
//			SimID_85823257_0_000030.feature_subdomain1.vol0.hdf5
//			SimID_85823257_0_000040.feature_subdomain0.vol0.hdf5
//			SimID_85823257_0_000040.feature_subdomain1.vol0.hdf5
//			SimID_85823257_0_000050.feature_subdomain0.vol0.hdf5
//			SimID_85823257_0_000050.feature_subdomain1.vol0.hdf5
//			SimID_85823257_0_000060.feature_subdomain0.vol0.hdf5
//			SimID_85823257_0_000060.feature_subdomain1.vol0.hdf5
//			SimID_85823257_0_000070.feature_subdomain0.vol0.hdf5
//			SimID_85823257_0_000070.feature_subdomain1.vol0.hdf5
//			SimID_85823257_0_000080.feature_subdomain0.vol0.hdf5
//			SimID_85823257_0_000080.feature_subdomain1.vol0.hdf5
//			SimID_85823257_0_000090.feature_subdomain0.vol0.hdf5
//			SimID_85823257_0_000090.feature_subdomain1.vol0.hdf5
//			SimID_85823257_0_000100.feature_subdomain0.vol0.hdf5
//			SimID_85823257_0_000100.feature_subdomain1.vol0.hdf5
			
			ChomboFiles chomboFiles4 = new ChomboFiles("SimID_85831561",0,new File("C:\\Developer\\eclipse\\workspace\\VCell_5.3_visfull\\SimData\\SimID_85831561_0_.mesh.hdf5"));
			chomboFiles4.addDataFile("subdomain0", "subdomain0", 0, new File("C:\\Developer\\eclipse\\workspace\\VCell_5.3_visfull\\SimData\\SimID_85831561_0_000000.feature_subdomain0.vol0.hdf5"));
			//chomboFiles4.addDataFile("subdomain1", 0, new File("C:\\Developer\\eclipse\\workspace\\VCell_5.3_visfull\\SimData\\SimID_85831561_0_000000.feature_subdomain1.vol0.hdf5"));
			//chomboFiles4.addDataFile("subdomain0", 1, new File("C:\\Developer\\eclipse\\workspace\\VCell_5.3_visfull\\SimData\\SimID_85831561_0_000001.feature_subdomain0.vol0.hdf5"));
			//chomboFiles4.addDataFile("subdomain1", 1, new File("C:\\Developer\\eclipse\\workspace\\VCell_5.3_visfull\\SimData\\SimID_85831561_0_000001.feature_subdomain1.vol0.hdf5"));
			//chomboFiles4.addDataFile("subdomain0", 2, new File("C:\\Developer\\eclipse\\workspace\\VCell_5.3_visfull\\SimData\\SimID_85831561_0_000002.feature_subdomain0.vol0.hdf5"));
			//chomboFiles4.addDataFile("subdomain1", 2, new File("C:\\Developer\\eclipse\\workspace\\VCell_5.3_visfull\\SimData\\SimID_85831561_0_000002.feature_subdomain1.vol0.hdf5"));
			
			
			ChomboFiles chomboFiles3 = new ChomboFiles("SimID_85303179",0,new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_.mesh.hdf5"));
			chomboFiles3.addDataFile("subdomain0", "subdomain0", 0,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000000.feature_subdomain0.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain1", "subdomain1", 0,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000000.feature_subdomain1.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain0", "subdomain0", 1,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000010.feature_subdomain0.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain1", "subdomain1", 1,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000010.feature_subdomain1.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain0", "subdomain0", 2,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000020.feature_subdomain0.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain1", "subdomain1", 2,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000020.feature_subdomain1.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain0", "subdomain0", 3,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000030.feature_subdomain0.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain1", "subdomain1", 3,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000030.feature_subdomain1.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain0", "subdomain0", 4,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000040.feature_subdomain0.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain1", "subdomain1", 4,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000040.feature_subdomain1.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain0", "subdomain0", 5,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000050.feature_subdomain0.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain1", "subdomain1", 5,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000050.feature_subdomain1.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain0", "subdomain0", 6,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000060.feature_subdomain0.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain1", "subdomain1", 6,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000060.feature_subdomain1.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain0", "subdomain0", 7,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000070.feature_subdomain0.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain1", "subdomain1", 7,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000070.feature_subdomain1.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain0", "subdomain0", 8,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000080.feature_subdomain0.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain1", "subdomain1", 8,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000080.feature_subdomain1.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain0", "subdomain0", 9,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000090.feature_subdomain0.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain1", "subdomain1", 9,  new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000090.feature_subdomain1.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain0", "subdomain0", 10, new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000100.feature_subdomain0.vol0.hdf5"));
			chomboFiles3.addDataFile("subdomain1", "subdomain1", 10, new File("p:\\schaff\\advisorsMeeting_2014\\SimID_85823257_0_000100.feature_subdomain1.vol0.hdf5"));
			
			ChomboFiles chomboFiles = chomboFiles4;
			
			// process each domain separately (only have to process the mesh once for each one)
			ChomboVtkFileWriter chomboVtkFileWriter = new ChomboVtkFileWriter();
			File destinationDirectory = new File("C:\\Developer\\eclipse\\workspace\\VCell_5.3_visfull\\VtkData\\");
			chomboVtkFileWriter.writeVtuExportFiles(chomboFiles, destinationDirectory, null);

			boolean bDisplay = true;
			
			if (bDisplay){
				List<Integer> timeIndices = chomboFiles.getTimeIndices();
				for (String volumeDomainName : chomboFiles.getVolumeDomainNames()){
					for (int timeIndex : timeIndices){
						String filename = new File(destinationDirectory,chomboFiles.getCannonicalFilePrefix(volumeDomainName,timeIndex)+".vtu").getAbsolutePath();
						//vtkUnstructuredGrid vtkgrid = VtkGridUtils.read(filename);
						//vtkgrid.BuildLinks();
						//VtkGridUtils.wr
	
						//String varName0 = vtkgrid.GetCellData().GetArrayName(0);
						//String varName1 = vtkgrid.GetCellData().GetArrayName(1);
//						SimpleVTKViewer simpleViewer = new SimpleVTKViewer();
//						simpleViewer.showGrid(vtkgrid, varName0, varName1);
//						Thread.sleep(1000);
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
}
