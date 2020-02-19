//@VCellHelper vh
//@ImageJ netij

import org.vcell.imagej.helper.VCellHelper
import java.text.SimpleDateFormat;
import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults;

import org.vcell.imagej.helper.VCellHelper.IJTimeSeriesJobResults
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
import java.awt.BasicStroke

//simID = 109812874
int t0Sim = 16;
int tCount = 27
simVar = "fluor"
simVarDataType = VCellHelper.VARTYPE_POSTPROC.PostProcessDataPDEDataContext;// null for SimulationData, "DataProcessingOutputInfo" for postprocessStats

simulationDataCacheKeyStr = getSimulationCacheKey(vh)
println("simulationDataCacheKeyStr="+simulationDataCacheKeyStr)

simTimes = new int[tCount]
for(int i=0;i<tCount;i++){
	simTimes[i] = t0Sim+i
}

IJVarInfos ijVarInfos = vh.getSimulationInfo(simulationDataCacheKeyStr)
ImagePlus[] simImage = new ImagePlus[ijVarInfos.scancount]
SIM_WIN_NAME = "Sim fluor"
	for(int job=0;job<ijVarInfos.scancount;job++){
		IJDataList fluorIJD = vh.getTimePointData(simulationDataCacheKeyStr, simVar, simVarDataType, simTimes, job);
		println(fluorIJD.ijData.size())
		sim_xsize = fluorIJD.ijData[0].stackInfo.xsize;
		sim_ysize = fluorIJD.ijData[0].stackInfo.ysize;
			fluorImage = ArrayImgs.floats([sim_xsize,sim_ysize,tCount] as long[])
			netij.ui().show(SIM_WIN_NAME+"_"+job,fluorImage)
			simImage[job] = ij.IJ.getImage()
			ij.IJ.run("In [+]");
			ij.IJ.run("In [+]");
			ij.IJ.run("In [+]");
			for(int t=0;t<tCount;t++){
				float[] fluorData = new float[fluorIJD.ijData[t].getDoubleData().length]
				for(int i=0;i<fluorData.length;i++){
					fluorData[i] = fluorIJD.ijData[t].getDoubleData()[i]
				}
				simImage[job].setSlice(t+1)
				float[] srcFloats = simImage[job].getProcessor().getPixels()
				cnt = 0
				for(int y= 0;y<sim_ysize;y++){
					for(int x = 0;x<sim_xsize;x++){
						srcFloats[cnt] = fluorData[cnt]
						cnt++
					}
				}

			}
	}

if(true){return}

/*
IJVarInfos ijVarInfos = vh.getSimulationInfo(simulationDataCacheKey)
ImagePlus[] simImage = new ImagePlus[ijVarInfos.scancount]
exp_xsize = ij.IJ.getImage().getWidth()
exp_ysize = ij.IJ.getImage().getHeight()
exp_frames = ij.IJ.getImage().getNFrames()
//fdStackArr = ij.IJ.getImage().getStack().getImageArray()
//SOMETIMES fdStackArr.length != exp_frames, use exp_frames
println("xsize="+exp_xsize+" ysize="+exp_ysize+" frames="+exp_frames+" scancount="+ijVarInfos.scancount)
double[] Kr_Bind_OverrideIndexes = new double[ijVarInfos.scancount]
//new float[tCount*exp_xsize*exp_ysize],
	SIM_WIN_NAME = "Sim fluor"
	for(int i=0;i<ijVarInfos.scancount;i++){
			fluorImage = ArrayImgs.floats([exp_xsize,exp_ysize,tCount] as long[])//SCIFIOImgPlus<DoubleType> fluorImage = null
			netij.ui().show(SIM_WIN_NAME+"_"+i,fluorImage)
			simImage[i] = ij.IJ.getImage()
//			simImage.setRoi(roix,roiy,roixs,roiys)
			ij.IJ.run("In [+]");
			ij.IJ.run("In [+]");
			ij.IJ.run("In [+]");
	}
double[][][] diffSqrTimeAndJob = new double[tCount][Kr_Bind_OverrideIndexes.length][boxes.length]
double[][] jobSum = new double[Kr_Bind_OverrideIndexes.length][boxes.length]
ROI_EXP_INDEX = 0
ROI_SIM_INDEX = 1
double[][][][] roiMean = new double[tCount][Kr_Bind_OverrideIndexes.length][boxes.length][2]
//Init diffSqr Graph
DIFFSQR_SERIES_PREFIX = "Sim "
XYSeriesCollection diffSqrDataset = new XYSeriesCollection();
for(int i=0;i<Kr_Bind_OverrideIndexes.length;i++){
	diffSqrDataset.addSeries(new XYSeries(DIFFSQR_SERIES_PREFIX+i));
}
//Init roi Graph
ROI_SERIES_PREFIX = "Roi_"
ROI_EXP_NAME = "Exp"
ROI_SIM_NAME = "Sim"
XYSeriesCollection roiDataset = new XYSeriesCollection();
for(int boxIndex=0;boxIndex<boxes.length;boxIndex++){
	for(int job=0;job<Kr_Bind_OverrideIndexes.length;job++){
		if(job == 0){
		roiDataset.addSeries(new XYSeries(ROI_SERIES_PREFIX+ROI_EXP_NAME+"_"+boxIndex+"_"+job))
		}
		roiDataset.addSeries(new XYSeries(ROI_SERIES_PREFIX+ROI_SIM_NAME+"_"+boxIndex+"_"+job))
	}
}

for(int t = 0;t<tCount;t++){
	for(int job=0;job<Kr_Bind_OverrideIndexes.length;job++){
		
	}
}

*/


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
	VCellHelper.VCellModelSearch search = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.bm, "frm", "Photoactivation and Binding 2", "3D Simulation", "Copy of tester",3,"Deterministic");
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