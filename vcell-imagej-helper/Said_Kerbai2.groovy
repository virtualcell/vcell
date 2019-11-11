#@VCellHelper vcHelper

import org.vcell.imagej.helper.VCellHelper
import java.text.SimpleDateFormat
import org.vcell.imagej.helper.VCellHelper.IJTimeSeriesJobResults
import org.vcell.imagej.helper.VCellHelper.IJVarInfo
import org.vcell.imagej.helper.VCellHelper.IJVarInfos


lastVCellApiPort = vcHelper.findVCellApiServerPort();//search for port that vcell is providing IJ related services on
System.out.println(lastVCellApiPort);
//SimpleDateFormat easyDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//VCellHelper.VCellModelVersionTimeRange vcellModelVersionTimeRange = new VCellHelper.VCellModelVersionTimeRange(easyDate.parse("2015-06-01 00:00:00"), easyDate.parse("2018-08-01 00:00:00"));
VCellHelper.VCellModelSearch search = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.mm, "KerbaicBITE", "Model5_2D_2C_Circle_R1_15_standard_V4", null, "Simulation1",null,null);
ArrayList<VCellHelper.VCellModelSearchResults> vcellModelSearchResults = vcHelper.getSearchedModelSimCacheKey(false, search,null);
System.out.println(vcellModelSearchResults);
int theCacheKey = Integer.valueOf(vcellModelSearchResults.get(0).getCacheKey()).intValue();
final IJVarInfos ijVarInfos = vcHelper.getSimulationInfo(theCacheKey+"");
ArrayList<String> varList = new ArrayList<>();
final ArrayList<IJVarInfo> ijVarInfo = ijVarInfos.getIjVarInfo();
for (int i = 0; i < ijVarInfo.size(); i++) {
	if(ijVarInfo.get(i).getVariableType().equals("Volume")){
		varList.add(ijVarInfo.get(i).getName());
	}
}
String[] vars = varList.toArray(new String[0]);
int[] dataIndexes = [5269,5281];
double[] times = [5.0,300.0];
//Get the data values using the cache-key
//IJTimeSeriesJobResults getTimeSeries(String[] variableNames, int[] indices, double startTime, int step, double endTime,boolean calcSpaceStats, boolean calcTimeStats, int jobid, int cachekey) throws Exception{
for (int t = 0; t < times.length; t++) {
	IJTimeSeriesJobResults ijTimeSeriesJobResults = vcHelper.getTimeSeries(vars, dataIndexes, times[t], 1, times[t], false, false, 0/*jobid, if parameter scan can be non-zero*/, theCacheKey);
	println("-----Time="+times[t]+"-----");
	if(ijTimeSeriesJobResults.data != null) {//no statistics, just data values
		//ijTimeSeriesJobResults.data[numVars][numDataPoints+1][numTimes] structure of ijTimeSeriesJobResults.data
		//ijTimeSeriesJobResults.data[varIndex][0][0...numTimes-1] contains the timePoints
		//ijTimeSeriesJobResults.data[varIndex][1...numDataPoints][0...numTimes-1] contains the data values at each index for each timePoint
		for (int varIndex = 0; varIndex < ijTimeSeriesJobResults.data.length; varIndex++) {
			for (int dataIndexes1 = 1; dataIndexes1 < ijTimeSeriesJobResults.data[varIndex].length; dataIndexes1++) {
				for (int timeIndexes = 0; timeIndexes < ijTimeSeriesJobResults.data[varIndex][dataIndexes1].length; timeIndexes++) {
					println("var="+vars[varIndex]+" dataIndex="+dataIndexes[dataIndexes1-1]+" val="+ijTimeSeriesJobResults.data[varIndex][dataIndexes1][timeIndexes]);
				}
			}
		}
	}else {//space statistics
		for (int i = 0; i < ijTimeSeriesJobResults.variableNames.length; i++) {
			for (int t2 = 0; t2 < ijTimeSeriesJobResults.times.length; t2++) {
				println(ijTimeSeriesJobResults.variableNames[i]+
					" min="+ijTimeSeriesJobResults.min[i][t2]+" max="+ijTimeSeriesJobResults.max[i][t2]+" mean="+ijTimeSeriesJobResults.unweightedMean[i][t2]+" size="+ijTimeSeriesJobResults.totalSpace[i]);
			}
		}
	}
}