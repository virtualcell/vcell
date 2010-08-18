package org.vcell.smoldyn.test;

import org.vcell.smoldyn.simulation.Simulation;


/**
 * @author mfenwick
 *
 */
public class SimulationWriterTest {
	
	
	public static void main(String[] args) {
		String name = "SmoldynTest";
		String base = "C:\\";//put it in the test folder
		Simulation simulation = new SimulationExample().getExample();
//		simulation = new FoxSheepExample().getExample();
		SmoldynWriteTestUtilities.findFilesAndPrintConfigFile(name, base, simulation);
	}
	
	

	
//	private static void thisIsAnExampleOfGettingDataIntoAVcellCompatibleFormat(String pathname, int dim) {
//
//		// TODO create vcell geometry
//		cbit.vcell.geometry.Geometry vcellgeometry = new cbit.vcell.geometry.Geometry("vcgeometry", dim);
//		BufferedWriter bw = null;
//		try {
//			Expression bgexp = new Expression("x>=0.0 && x<=200.0 && y>=0.0 && y<=100.0 && z>=0.0 && z<=100.0");
//			AnalyticSubVolume bgsubvol = new AnalyticSubVolume("background", bgexp);
//			Expression surface1expresion = new Expression("(50.0-x)^2 + (50.0-y)^2 +(50.0-z)^2 < 35.0^2");
//			AnalyticSubVolume subvolume1 = new AnalyticSubVolume("surface1", surface1expresion);
//			vcellgeometry.getGeometrySpec().addSubVolume(bgsubvol);
//			vcellgeometry.getGeometrySpec().addSubVolume(subvolume1);
//			bw = new BufferedWriter(new FileWriter(new File(pathname)));
//			String vcgeomstringxml = XmlHelper.geometryToXML(vcellgeometry);
//			bw.write(vcgeomstringxml);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if (bw != null) {
//				try {
//					bw.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}

}
