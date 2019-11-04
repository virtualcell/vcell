#@VCellHelper vh
//(See https://github.com/virtualcell/vcell/tree/master/vcell-imagej-helper/src/main/java/org/vcell/imagej/helper/VCellHelper.java)

import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.ChartPanel
import org.jfree.data.category.CategoryDataset
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.DefaultXYDataset
import javax.swing.JFrame
import org.vcell.imagej.helper.VCellHelper.IJTimeSeriesJobResults
import org.vcell.imagej.helper.VCellHelper.VCellModelSearch
import org.vcell.imagej.helper.VCellHelper.ModelType
import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults


//Demo chart in ImageJ of line-scan values from VCell simulation results

// BioModel "Tutorial_FRAPbinding" owned by user "tutorial" in application "Spatial" for simulation "FRAP binding"
// values for variable="r" at timePoint=22.0
// line-scan along x-axis at y=0.0
// start and end indexes found by moving mouse over x,y data points (-9.24,0.0) and (9.24,0,0) and
// see index in "info' box at the bottom of the sim results viewer in VCell

//Create list of datapoint indexes to extract from data
startIndex = 1279
endIndex = 1321
int[] dataIndexes = new int[endIndex-startIndex]
for(int i=0;i<dataIndexes.length;i++){
	dataIndexes[i] = startIndex+i
}
//Define list of variables to extract (see variable list in VCell simulation results viewer)
String[] vars = ["r"]

//Define search parameters to find simulation results dataset in VCell:
// enum ModelType {bm,mm,quick}; defined in @VCellHelper, bm(saved BioModels), mm(saved MathModels), quick(currently displayed quickrun sim results  in open model in VCellClient)
// VCellModelSearch(ModelType modelType, String vcellUserID, String modelName(can be null),
//   String applicationName(can be null),String simulationName(can be null, always null for mathmodel), Integer geometryDimension(can be null), String mathType(can be null))
// null values tells search to ignore that search parameter
vcms = new VCellModelSearch(ModelType.bm,
		"tutorial"/*only models owned by this user, if null search all models the VCell logged in user has access to*/,
		"Tutorial_FRAPbinding"/*model names matching*/,
		"Spatial"/*BioModel application names matching, for MathModels this is null*/,
		"FRAP binding"/*simulation names matching*/,
		null/*GeomDim 0,1,2,3*/,null/*String mathType "Deterministic" or "Stochastic"*/)

//Do a search: (for this demo must have already opened public BioModel "Tutorial_FRAPbinding" listed under "Tutorials within VCell client"
// vh.getSearchedModelSimCacheKey(boolean openOnly,VCellModelSearch vcms,VCellModelVersionTimeRange vcmvtr)
/*ArrayList<VCellModelSearchResults>*/vcmsrArrayList = vh.getSearchedModelSimCacheKey(true/*only open models*/,vcms,null/*only saved models withing this date range*/)

//For this search there should only be 1 result, get the search cache-key from the returned list, used for subsequent data operations
/*VCellModelSearchResults extends VCellModelSearch*/vcmsr = vcmsrArrayList.get(0)
/*String*/theCacheKey = vcmsr.getCacheKey();
println(theCacheKey)

//Get the data values using the cache-key
//IJTimeSeriesJobResults getTimeSeries(String[] variableNames, int[] indices, double startTime, int step, double endTime,boolean calcSpaceStats, boolean calcTimeStats, int jobid, int cachekey) throws Exception{
ijTimeSeriesJobResults = vh.getTimeSeries(vars, dataIndexes, 22 as double, 1, 22 as double, false, false, 0/*jobid, if parameter scan can be non-zero*/, theCacheKey as int)
//ijTimeSeriesJobResults.data[numVars][numDataPoints+1][numTimes] structure of ijTimeSeriesJobResults.data
//ijTimeSeriesJobResults.data[varIndex][0][0...numTimes-1] contains the timePoints
//ijTimeSeriesJobResults.data[varIndex][1...numDataPoints][0...numTimes-1] contains the data values at each index for each timePoint
//Demo TimePoints ijTimeSeriesJobResults.data[0][0] == array of timePoints for var="r", array has 1 value for timePoint=22.0
//Demo DataPoints ijTimeSeriesJobResults.data[0][1..numDataPoints][0] iterate over dataPoints at each timePoint for each var to get data for var,times
double[] chartTheseDataPoints = new double[dataIndexes.length]
for(int i=0;i<dataIndexes.length;i++){
	chartTheseDataPoints[i] =
		ijTimeSeriesJobResults.data[0/*index of "r"*/][i+1/*data, skip 0 because it holds copy of times*/][0/*0 always because we had only 1 timePoint=22.0*/]
}
//Create JFreechart x,y axis arrays for plotting x=data indexes, y=dataPoint values
double[][] data = [dataIndexes,chartTheseDataPoints];


//Plot the Data

String title = "test"
String xAxisLabel = "distance"
String yAxisLabel = "val"


xyDataset = new DefaultXYDataset()
xyDataset.addSeries((Comparable) "data1", data)

chart = ChartFactory.createXYLineChart( title,  xAxisLabel,  yAxisLabel, xyDataset)
chartPanel = new ChartPanel(chart)

frame = new JFrame("Chart");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.getContentPane().add(chartPanel)
        //Display the window.
frame.pack();
frame.setVisible(true);

