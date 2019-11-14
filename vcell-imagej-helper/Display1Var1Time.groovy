#@VCellHelper vcHelper
#@ImageJ ij

import org.vcell.imagej.helper.VCellHelper
import net.imagej.ImageJ
import org.scijava.ui.DialogPrompt

import org.vcell.imagej.helper.VCellHelper.IJTimeSeriesJobResults
import org.vcell.imagej.helper.VCellHelper.IJVarInfo
import org.vcell.imagej.helper.VCellHelper.IJVarInfos
import org.vcell.imagej.helper.VCellHelper.TimePointData
import org.vcell.imagej.helper.VCellHelper.VCellModelVersionTimeRange
import net.imagej.display.DefaultDatasetView

import java.text.SimpleDateFormat
import net.imglib2.img.array.ArrayImgs

try{
	lastVCellApiPort = vcHelper.findVCellApiServerPort();//search for port that vcell is providing IJ related services on
	System.out.println(lastVCellApiPort);
}catch(Exception e){
	ij.ui().showDialog(e.getMessage()+"\nCheck VCell running and Tools->'Start FIJI (ImageJ) Service'. is started","Couldn't Communicate with VCell",DialogPrompt.MessageType.ERROR_MESSAGE)
}

VCellHelper.VCellModelSearch searchCriteria = new VCellHelper.VCellModelSearch(
	VCellHelper.ModelType.bm, "tutorial", "Tutorial_MultiApp", "3D pde", "Simulation4",
	null/*Geom Dimension 0,1,2,3*/,null/*MathType {"Determinsitc","Stochastic"}*/);

SimpleDateFormat easyDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
searchFrom = easyDate.parse("2018-11-13 00:00:00")
searchTo = easyDate.parse("2100-01-01 00:00:00")
searchTimeRange = new VCellModelVersionTimeRange(searchFrom,searchTo)
ArrayList<VCellHelper.VCellModelSearchResults> vcellModelSearchResults = vcHelper.getSearchedModelSimCacheKey(false/*Only Open Models*/, searchCriteria,searchTimeRange);
if(vcellModelSearchResults == null || vcellModelSearchResults.size() == 0){
	ij.ui().showDialog("No VCell models found matching search criteria","Search Results Empty",DialogPrompt.MessageType.INFORMATION_MESSAGE)
	return;
}
int theCacheKey = Integer.valueOf(vcellModelSearchResults.get(0).getCacheKey()).intValue();

var = "C_cyt" //Volume variable name from the variable slection list in Simulation results viewer
timeIndex = [50] as int[] //Time index (not actual time) found by looking at number directly over time slider in VCell simulation results viewer
try{
	//Get data at single time for a single variable
	//TimePointData getTimePointData(String simulationDataCacheKey,String variableName,int[] timePointIndexes,int simulationJobIndex)
	tpd = vcHelper.getTimePointData(theCacheKey as String,var,timeIndex,0)
	println(tpd.getBasicStackDimensions().xsize+" "+tpd.getBasicStackDimensions().ysize+" "+tpd.getBasicStackDimensions().zsize)
	
	data = tpd.getTimePointData()
	bsd = tpd.getBasicStackDimensions()
	//println(bsd.xsize+" "+bsd.ysize+" "+bsd.zsize)
	dataImg = ArrayImgs.doubles( data, bsd.xsize,bsd.ysize,bsd.zsize)
	title = var+" at "+timeIndex[0]
	final currDisp = ij.display().createDisplay(title,dataImg)
	//currDataView = (DefaultDatasetView)currDisp.getActiveView()
	//println(currDataView.getChannelCount())
	println(currDisp.getCanvas().getViewportWidth()+" "+currDisp.getCanvas().getViewportWidth())
	ij.IJ.run("Set... ", "zoom=400 x=50 y=50");
	//ij.IJ.run("In [+]", "");
	//ij.IJ.run("In [+]", "");
	//ij.IJ.run("In [+]", "");
	/*
	ij.ui().show(title,dataImg)
	ij.imageDisplay().getActiveImageDisplay().getCanvas().setZoom(3);
	currDisplay= ij.display().getDisplay(title)
	println(currDisplay)
	*/
}catch(Exception e){
	ij.ui().showDialog(e.getMessage(),"Data Error",DialogPrompt.MessageType.ERROR_MESSAGE)
	return
}
