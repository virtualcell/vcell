package org.vcell.visit;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;

import org.vcell.util.PropertyLoader;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.VCDataIdentifier;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.simdata.SimulationData;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationDataIdentifierOldStyle;

public class VisitUtils {
	
	public static File findVisitBinDirectory(){
		File visitBinDir = null;
		String visitBinDirProp = PropertyLoader.getProperty("vcell.visit.installexe", null);
		
		if (visitBinDirProp == null){
			if (ResourceUtil.bMac || ResourceUtil.bLinux) {
				visitBinDirProp = "/home/VCELL/visit/visit2_3_2.linux-x86_64/bin";
			} else {
				visitBinDirProp = "c:\\program files\\LLNL\\VisIt 2.3.2";
			}
		}
		
		System.out.println("visitBinDirProp = " + visitBinDirProp);
		
		if (visitBinDirProp != null && (new File(visitBinDirProp,"visit.exe").exists()|| new File(visitBinDirProp,"visit").exists())) {
			visitBinDir= new File(visitBinDirProp);
		}
		return visitBinDir;
	}
	
	public static File askUserToFindVisItBinDirectory(Component parent){
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Please locate the VisIt executable");
		chooser.showOpenDialog(parent);
		if (!chooser.getSelectedFile().getName().toLowerCase().startsWith("visit")) {
			DialogUtils.showErrorDialog(parent, "Expecting filename to begin with 'visit'");
		}
		File visitBinDir = chooser.getSelectedFile().getParentFile();
		return visitBinDir;
	}
	
	public static String getCannonicalFileName(VCDataIdentifier vcDataIdentifier){
		KeyValue fieldDataKey = null;
		int jobIndex = 0;
		boolean isOldStyle = false;
		if (vcDataIdentifier instanceof VCSimulationDataIdentifier){
			fieldDataKey = ((VCSimulationDataIdentifier)vcDataIdentifier).getSimulationKey();
			jobIndex = ((VCSimulationDataIdentifier)vcDataIdentifier).getJobIndex();
		}else if (vcDataIdentifier instanceof VCSimulationDataIdentifierOldStyle){
			isOldStyle = true;
			fieldDataKey = ((VCSimulationDataIdentifierOldStyle)vcDataIdentifier).getSimulationKey();
			jobIndex = ((VCSimulationDataIdentifierOldStyle)vcDataIdentifier).getJobIndex();
		}else{
			throw new RuntimeException("Unknown VCDataIdentifier type "+vcDataIdentifier.getClass().getName());
		}

		String simlogname = SimulationData.createCanonicalSimLogFileName(fieldDataKey, jobIndex, isOldStyle);
		return simlogname;
	}
	
}
