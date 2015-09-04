package org.vcell.stochtest;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.vcell.util.FileUtils;

import com.google.gson.Gson;

public class StochtestFileUtils {

	public static class MinMaxHelp {
		public double min,max,diff;
		public MinMaxHelp(double[] data) {
			min=data[0];
			max=data[0];
			for (int i = 0; i < data.length; i++) {
				if(data[i] < min){min = data[i];}
				if(data[i] > max){max = data[i];}
			}
			diff = ((max-min)==0?1:max-min);
		}
	}

	public static TimeSeriesMultitrialData readData(File file) throws IOException {
		Gson gson = new Gson();
		FileReader reader = new FileReader(file);
		TimeSeriesMultitrialData sampleData = gson.fromJson(reader, TimeSeriesMultitrialData.class);
		return sampleData;
	}

	public static void writeData(TimeSeriesMultitrialData sampleData, File file) throws IOException {
		Gson gson = new Gson();
		String json = gson.toJson(sampleData);
		FileUtils.writeByteArrayToFile(json.toString().getBytes(), file);
	}

	public static File getStochtestRunDataFile(File baseDir, StochtestRun stochtestRun){
		return new File(baseDir,"stochtestrun_"+stochtestRun.key+".json");
	}

	public static File createDirFile(File baseDir, StochtestRun stochtestRun){
		
		File dirFile = new File(baseDir,"stochtestrun_"+stochtestRun.key);
		if(!dirFile.exists()){
			dirFile.mkdirs();
		}
		return dirFile;
	}

	public static void clearDir(File dirName){
		File[] files = dirName.listFiles();
		for (int i = 0; files!=null && i < files.length; i++) {
			if(files[i].isDirectory()){
				clearDir(files[i]);
			}else{
				files[i].delete();
			}
		}
		dirName.delete();
	}

	public static void writeMessageTofile(File file,String message){
		try{
			FileUtils.writeByteArrayToFile(message.getBytes(), file);
		}catch(Exception e2){
			e2.printStackTrace();
		}	
	}

	public static void writeChiSquareTest(File file, TimeSeriesMultitrialData sampleData1, TimeSeriesMultitrialData sampleData2) throws Exception{
		
		StringBuffer sb = new StringBuffer();
		for (int varIndex=0; varIndex<sampleData1.varNames.length; varIndex++){
			String varName = sampleData1.varNames[varIndex];
			sb.append("\""+varName+"\"");
			for (int timeIndex = 0; timeIndex < sampleData1.times.length; timeIndex++) {
				double xs_value = TimeSeriesMultitrialData.chiSquaredTest(sampleData1.getVarTimeData(sampleData1.varNames[varIndex],timeIndex),sampleData2.getVarTimeData(varName,timeIndex));
				sb.append(","+xs_value);
			}
			sb.append("\n");
		}
		FileUtils.writeByteArrayToFile(sb.toString().getBytes(), file);
	}

	public static void writeKolmogorovSmirnovTest(File file, TimeSeriesMultitrialData sampleData1, TimeSeriesMultitrialData sampleData2) throws Exception{
		
		StringBuffer sb = new StringBuffer();
		for (int varIndex=0; varIndex<sampleData1.varNames.length; varIndex++){
			String varName = sampleData1.varNames[varIndex];
			sb.append("\""+varName+"\"");
			for (int timeIndex = 0; timeIndex < sampleData1.times.length; timeIndex++) {
				double ks_value =TimeSeriesMultitrialData.kolmogorovSmirnovTest(sampleData1.getVarTimeData(sampleData1.varNames[varIndex],timeIndex), sampleData2.getVarTimeData(varName,timeIndex));
				sb.append(","+ks_value);
			}
			sb.append("\n");
		}
		FileUtils.writeByteArrayToFile(sb.toString().getBytes(), file);
	}

	public static void writeVarDiffData(File file,TimeSeriesMultitrialData sampleData1, TimeSeriesMultitrialData sampleData2) throws Exception{
		
		StringBuffer sb = new StringBuffer();
		for (int varIndex=0; varIndex<sampleData1.varNames.length; varIndex++){
			String varName = sampleData1.varNames[varIndex];
			sb.append("\""+varName+"\"");
			double[] varData1 = sampleData1.getMeanTrajectory(varName);
			double[] varData2 = sampleData2.getMeanTrajectory(varName);
			StochtestFileUtils.MinMaxHelp minmaxStoch = new StochtestFileUtils.MinMaxHelp(varData1);
			for (int i = 0; i < varData1.length; i++) {
				double diffOfMeans = (varData1[i]/minmaxStoch.diff)-(varData2[i]/minmaxStoch.diff);
				sb.append(","+varData1[i]);
				sb.append(","+varData2[i]);
				sb.append(","+diffOfMeans);
			}
			sb.append("\n");
		}
		FileUtils.writeByteArrayToFile(sb.toString().getBytes(), file);
	}

}
