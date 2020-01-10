//@VCellHelper vh
//@ImageJ netij

import org.vcell.imagej.helper.VCellHelper
import java.text.SimpleDateFormat;
import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults;

import org.vcell.imagej.helper.VCellHelper.IJSimStatusJobs
import org.vcell.imagej.helper.VCellHelper.IJDataList
import org.vcell.imagej.helper.VCellHelper.IJData
import org.vcell.imagej.helper.VCellHelper.IJVarInfos
import org.vcell.imagej.helper.VCellHelper.IJVarInfo
//import io.scif.img.SCIFIOImgPlus;
import ij.process.ImageProcessor
import ij.ImagePlus
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.type.numeric.real.FloatType;
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.ChartPanel
import org.jfree.data.category.CategoryDataset
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.data.xy.XYSeries
import javax.swing.JFrame


//public IJDataList getTimePointData(String simulationDataCacheKey,String variableName,int[] timePointIndexes,int simulationJobIndex) throws Exception{

simID = 171795930 //109808709
//jobIndex = 0
int t0Sim = 32;
int t0Exp = 2
int tCount = 27
int ANALYZE_END_TIMEINDEX = 2;
simVar = "fluor"
simVarDataType = VCellHelper.VARTYPE_POSTPROC.PostProcessDataPDEDataContext;// null for SimulationData, "DataProcessingOutputInfo" for postprocessStats
expDataVar = "abp" //Expected to be open ImageJ dataset window
roix = 55
roiy = 55
roixs = 8;
roiys = 8

if(false){waitForSolverGetCacheForData(simID,jobIndex);}

simulationDataCacheKey = getSimulationCacheKey(vh)
//IJDataList bpflDataList = vh.getTimePointData(simulationDataCacheKey, "BP_fl", null, [activateTimeIndex] as int[], simID);
//bpflData = fluorDomainIJDataList.ijData[0].getDoubleData()
/*
cnt = 0
for(int i=0;i<temp.length;i++){
	if(temp[i] != fluorDomainIJDataList.ijData[0].notInDomainValue){
		cnt++;
	}
}
println(cnt)
if(true){return}
*/
//SCIFIOImgPlus<DoubleType> annotatedZProjectedSimPostBleachData =
//	zProjectNormalizeSimData(vh, "Kr_bind .01 ", simulationDataCacheKey, "BP_fl", 0, 0, ANALYZE_BEGIN_TIMEINDEX, ANALYZE_END_TIMEINDEX);

IJVarInfos ijVarInfos = vh.getSimulationInfo(simulationDataCacheKey)
//Get Exp Data - 'TS.nd2, channel 2'
//expData = ij.IJ.getImage().getStack().getPixels(t0+t0Exp)
ij.IJ.selectWindow(expDataVar)
expImage = ij.IJ.getImage()
simImage = null
exp_xsize = ij.IJ.getImage().getWidth()
exp_ysize = ij.IJ.getImage().getHeight()
exp_frames = ij.IJ.getImage().getNFrames()
//fdStackArr = ij.IJ.getImage().getStack().getImageArray()
//SOMETIMES fdStackArr.length != exp_frames, use exp_frames
println("xsize="+exp_xsize+" ysize="+exp_ysize+" frames="+exp_frames+" scancount="+ijVarInfos.scancount)
double[] Kr_Bind_OverrideIndexes = new double[ijVarInfos.scancount]
//new float[tCount*exp_xsize*exp_ysize], 
			fluorImage = ArrayImgs.floats([exp_xsize,exp_ysize,tCount] as long[])//SCIFIOImgPlus<DoubleType> fluorImage = null
			netij.ui().show("Sim fluor",fluorImage)
			simImage = ij.IJ.getImage()
			simImage.setRoi(roix,roiy,roixs,roiys)
			ij.IJ.run("In [+]");
			ij.IJ.run("In [+]");
			ij.IJ.run("In [+]");
double[][] diffSqrTimeAndJob = new double[tCount][Kr_Bind_OverrideIndexes.length]
double[] jobSum = new double[Kr_Bind_OverrideIndexes.length]
//Init Graph
SERIES_PREFIX = "Sim "
XYSeriesCollection dataset = new XYSeriesCollection();
for(int i=0;i<Kr_Bind_OverrideIndexes.length;i++){
	dataset.addSeries(new XYSeries(SERIES_PREFIX+i));
}
for(int t = 0;t<tCount;t++){
	actualExpTime = t+t0Exp
	actualSimTime = t+t0Sim
	expImage.setSlice(actualExpTime)
	expImage.setRoi(roix,roiy,roixs,roiys)
	//short[] expData = (short[])fdStackArr.getAt(actualExpTime)
	for(int job=0;job<Kr_Bind_OverrideIndexes.length;job++){
		println("t="+t+" "+" job="+job+" expTime="+(actualExpTime)+" simTime="+(actualSimTime))
		//Get Sim Data - PostProcessing 'fluor'
		IJDataList fluorIJD = null
		try{
			fluorIJD = vh.getTimePointData(simulationDataCacheKey, simVar, simVarDataType, [actualSimTime] as int[], job);
		}catch(Exception e){
			println(e.getMessage())
			job--;//try again
			continue;
		}
		float[] fluorData = new float[fluorIJD.ijData[0].getDoubleData().length]
		for(int i=0;i<fluorData.length;i++){
			fluorData[i] = fluorIJD.ijData[0].getDoubleData()[i]
		}
		simStackInfo = fluorIJD.ijData[0].stackInfo
		if(simStackInfo.xsize != exp_xsize || simStackInfo.ysize != exp_ysize){
			throw new Exception("Expecting xy sizes to to be same for Sim and Exp timepoints")
		}
//		if(expData.length != fluorData.length){
//			throw new Exception("Expecting Sim and Exp data arrays to be same length")
//		}
		//Thread.sleep(500)
		ArrayImg<FloatType, FloatArray> simTimePointData = ArrayImgs.floats(fluorData, [simStackInfo.xsize,simStackInfo.ysize,1] as long[])
	//	if(t == 0 && job == 0){
			//ArrayImg<DoubleType, DoubleArray> simAllTimes = ArrayImgs.doubles(fluorData, [simStackInfo.xsize,simStackInfo.ysize,tCount] as long[]);
			//fluorImage = new SCIFIOImgPlus<>(simTimePointData,"Sim fluor",[Axes.X,Axes.Y] as AxisType[]);
			//netij.ui().show("Sim fluor",simTimePointData)
			//simImage = ij.IJ.getImage()
			//simImage.setRoi(roix,roiy,roixs,roiys)
			//ij.IJ.run("In [+]");
			//ij.IJ.run("In [+]");
			//ij.IJ.run("In [+]");
	//	}else 
		if(job == 0){
			ij.IJ.selectWindow("Sim fluor")
			//println(ij.IJ.getImage().getProcessor().getPixels())
			//if(true){return}
			//ImageProcessor ip = ij.IJ.getImage().getStack().getProcessor(t+1)
			//float[] tempf = simTimePointData.update(null).getCurrentStorageArray()
			//println(simTimePointData.update(null).getCurrentStorageArray())
			ij.IJ.getImage().setSlice(t+1)
			float[] srcFloats = ij.IJ.getImage().getProcessor().getPixels()
			cnt = 0
			for(int y= 0;y<exp_ysize;y++){
				for(int x = 0;x<exp_xsize;x++){
					//ip.setf(x,y,fluorData[cnt])
					srcFloats[cnt] = fluorData[cnt]
					cnt++
				}
			}
/*
			srcFloats = ij.IJ.getImage().getProcessor().getPixels()
			cnt=0
			for(int y= 0;y<exp_ysize;y++){
				for(int x = 0;x<exp_xsize;x++){
					//print(ip.getf(x,y)+" ")
					print(srcFloats[cnt])
					cnt++
				}
				println()
			}
*/
		}

		//if(t==1 && job==0){return}
		
		simRA = simTimePointData.randomAccess()
		simSum = 0.0;
		expSum = 0;
		for(int y= 0;y<roiys;y++){
			for(int x = 0;x<roixs;x++){
				xp = x+roix
				yp = y+roiy
				simRA.setPosition(xp,0)
				simRA.setPosition(yp,1)
				simSum+= simRA.get().get()
				expVal = expImage.getStack().getProcessor(actualExpTime).getPixel(xp,yp)
				print(expVal+":"+simRA.get());print(" ")
				expSum+= expVal
			}
				println()
		}
		diffSqrTimeAndJob[t][job] = Math.pow(simSum-expSum,2)/1e9
		jobSum[job]+= diffSqrTimeAndJob[t][job]
		dataset.getSeries(SERIES_PREFIX+job).add(t,diffSqrTimeAndJob[t][job])
		//println(simSum+" "+expSum+" "+diffSqrTimeAndJob[t][job])
	}
	println(diffSqrTimeAndJob)
	//if(t==2){break}
}
minVal = Double.POSITIVE_INFINITY;
minJobIndex = -1
for(int i=0;i<jobSum.length;i++){
	if(jobSum[i] < minVal){
		minJobIndex = i;
		minVal = jobSum[i];
	}
}
println("Minimum Job Index="+minJobIndex+" minimum sum(squared(Sim-Exp))="+minVal)
//Create Chart
JFreeChart chart = ChartFactory.createXYLineChart("Exp VS Sim", "Time", "((Sim-Exp)^2)/1e9", dataset);
chartPanel = new ChartPanel(chart)

frame = new JFrame("Chart");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.getContentPane().add(chartPanel)
        //Display the window.
frame.pack();
frame.setVisible(true);
if(true){return}
//int xyzsize = ijDataList.ijData[0].stackInfo.xsize*ijDataList.ijData[0].stackInfo.ysize*ijDataList.ijData[0].stackInfo.zsize;
println(xyzsize+" "+temp.length+" "+ijDataList.ijData[0].notInDomainValue)
accum = 0.0
cnt = 0
for(int i=0;i<temp.length;i++){
	if(temp[i] != ijDataList.ijData[0].notInDomainValue){
		cnt++;
		accum+= temp[i]
	}
}
println("accum="+accum+" domainCnt="+cnt+" avg="+(accum/cnt)+" notInDomainCount="+fluorDomainIJDataList.ijData[0].notInDomainCount)
//double[] bleachedTimeStack = new double[ANALYZE_COUNT*xysize];
//double[] zProjectedSimNormalizer = getNormalizedZProjected(ijDataList,null);


def void waitForSolverGetCacheForData(String simJobID,int job) throws Exception{
    //Wait for solver to finish
    while(true) {
    	Thread.sleep(5000);
		IJSimStatusJobs ijSimStatusJobs =  vh.getSolverStatus(simJobID);
		ijSimStatus = ijSimStatusJobs.results[job]
		info = ijSimStatus.statusID+" "+ijSimStatus.simulationStatusCode+" "+ijSimStatus.simulationStatusName
		println(info);
		ij.IJ.log(info)
 		String statusName = ijSimStatus.simulationStatusName.toLowerCase();
		if(statusName.equals("finished") || statusName.equals("stopped") || statusName.equals("aborted") || statusName.equals("failed") || statusName.equals("workerevent_completed")) {
			break;
		}
 	}
}

def String getSimulationCacheKey(VCellHelper vh) throws Exception{
	// User can search (with simple wildcard *=any) for VCell Model by:
	// model type {biomodel,mathmodel,quickrun}
	// user name
	// model name
	// application name (if searching for BioModels, not applicable for MathModels)
	// simulation name
	// integer Geometry dimension 0-ODE (1,2,3)-PDE
	// math type 'Deterministic' or 'Stochastic'
	SimpleDateFormat easyDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//VCellHelper.VCellModelSearch search = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.bm, "tutorial", "Tutorial_FRAPbinding", "Spatial", "FRAP binding",null,null);
	VCellHelper.VCellModelSearch search = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.bm, "frm", "Photoactivation and Binding 2", "3D Simulation", "Parameter Scan",3,"Deterministic");
	// version time range (all VCell Models saved on the VCell server have a last date 'saved')
	VCellHelper.VCellModelVersionTimeRange vcellModelVersionTimeRange = new VCellHelper.VCellModelVersionTimeRange(easyDate.parse("2015-06-01 00:00:00"), easyDate.parse("2025-01-01 00:00:00"));
	//Search VCell database for models matching search parameters
	ArrayList<VCellModelSearchResults> vcellModelSearchResults = vh.getSearchedModelSimCacheKey(true, search,vcellModelVersionTimeRange);
	//This search should only find 1
	if(vcellModelSearchResults.size() != 1) {
		ij.IJ.showMessage("Expecting 1 search result for "+search.getModelName()+" but got "+vcellModelSearchResults.size()+", make sure model is open in VCell client");
		throw new Exception("Expecting only 1 model from search results");
	}

	String cacheKey = vcellModelSearchResults.get(0).getCacheKey();
/*
	try{
		//make sure original sim result is open
		VCellHelper.IJDataList ijDataList = vh.getTimePointData(cacheKey, null, null, 0);
	}catch(Exception e){
		ij.IJ.showMessage("Make sure Application '"+search.getApplicationName()+"' -> Simulation '"+search.getSimulationName()+"' results viewer is open");
		throw e
	}
*/
	return cacheKey;

}
/*
	def SCIFIOImgPlus<DoubleType> zProjectNormalizeSimData(VCellHelper vh,
		String newImgPlusName,String simulationDataCachekey,String varToAnalyze,int preBleachTimeIndex,int solverStatus_jobIndex,int ANALYZE_BEGIN_TIMEINDEX,int ANALYZE_END_TIMEINDEX) throws Exception{
		
		int ANALYZE_COUNT = ANALYZE_END_TIMEINDEX - ANALYZE_BEGIN_TIMEINDEX+1;
	    // Get the SIMULATION pre-bleach timepoint data for normalizing
	    VCellHelper.IJDataList ijDataList = vh.getTimePointData(simulationDataCachekey, varToAnalyze, [preBleachTimeIndex] as int[], solverStatus_jobIndex);
		int xysize = ijDataList.ijData[0].stackInfo.xsize*ijDataList.ijData[0].stackInfo.ysize;
		double[] bleachedTimeStack = new double[ANALYZE_COUNT*xysize];
//	    checkSizes(timePointData.getBasicStackDimensions(), xsize, xysize, zsize);
//    	URL dataUrl = new URL("http://localhost:"+vh.findVCellApiServerPort()+"/"+"getdata"+"?"+"cachekey"+"="+cachekey+"&"+"varname"+"="+varToAnalyze+"&"+"timeindex"+"="+(int)(0)+"&"+"jobid="+ijSolverStatus.getJobIndex());
	    double[] zProjectedSimNormalizer = getNormalizedZProjected(ijDataList,null);
//showAndZoom(ij, "Sim Data PreBleach", ArrayImgs.doubles(simNormalizer, xsize,ysize), 4);
	    
	    // Get the SIMULATION post-bleach data to analyze
	    for(int timeIndex = ANALYZE_BEGIN_TIMEINDEX;timeIndex<=ANALYZE_END_TIMEINDEX;timeIndex++){
	    	ijDataList = vh.getTimePointData(simulationDataCachekey, varToAnalyze, [timeIndex] as int[], solverStatus_jobIndex);
//	    	checkSizes(timePointData.getBasicStackDimensions(), xsize, xysize, zsize);
//	    	dataUrl = new URL("http://localhost:"+vh.findVCellApiServerPort()+"/"+"getdata"+"?"+"cachekey"+"="+cachekey+"&"+"varname"+"="+varToAnalyze+"&"+"timeindex"+"="+(int)timeIndex+"&"+"jobid="+ijSolverStatus.getJobIndex());
	 		double[] data = getNormalizedZProjected(ijDataList,zProjectedSimNormalizer);
		    System.arraycopy(data, 0, bleachedTimeStack, (timeIndex-ANALYZE_BEGIN_TIMEINDEX)*xysize, data.length);
    	}

    	//Turn VCell data into iterableinterval
//    	FinalInterval zProjectedSimDataSize = FinalInterval.createMinSize(new long[] {0,0,0, xsize,ysize,ANALYZE_COUNT});//xyzt:origin and xyzt:size
		ArrayImg<DoubleType, DoubleArray> simImgs = ArrayImgs.doubles(bleachedTimeStack, [ijDataList.ijData[0].stackInfo.xsize,ijDataList.ijData[0].stackInfo.ysize,ANALYZE_COUNT] as long[]);
		SCIFIOImgPlus<DoubleType> annotatedZProjectedSimPostBleachData = new SCIFIOImgPlus<>(simImgs,newImgPlusName,[Axes.X,Axes.Y,Axes.TIME] as AxisType[]);
//    	RandomAccessibleInterval<DoubleType> simExtracted = ij.op().transform().crop(annotatedSimData, simExtractInterval);   	
//showAndZoom(ij, "Sim Data "+diffusionRate, annotatedZProjectedSimPostBleachData, 4);
		return annotatedZProjectedSimPostBleachData;
	}
*/
    def double[] getNormalizedZProjected(VCellHelper.IJDataList ijDataList,double[] normalizer) throws Exception{
		VCellHelper.BasicStackDimensions basicStackDimensions = ijDataList.ijData[0].stackInfo;//VCellHelper.getDimensions(nodes.item(0));
 		//Sum pixel values in Z direction to match experimental data (open pinhole confocal, essentially brightfield)
		int xysize = basicStackDimensions.xsize*basicStackDimensions.ysize;
		double[] normalizedData = new double[xysize];
		for (int i = 0; i < ijDataList.ijData[0].getDoubleData().length; i++) {
			normalizedData[(i%xysize)]+= ijDataList.ijData[0].getDoubleData()[i];
		}
		if(normalizer != null) {
			for (int i = 0; i < normalizedData.length; i++) {
				if(normalizedData[i] != 0) {
					normalizedData[i] = (normalizedData[i]+Double.MIN_VALUE)/(normalizer[i]+Double.MIN_VALUE);
				}
			}
		}
		return normalizedData;
    }
