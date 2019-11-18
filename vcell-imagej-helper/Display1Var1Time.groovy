#@VCellHelper vcHelper
#@ImageJ imagej

import org.vcell.imagej.helper.VCellHelper
import net.imagej.ImageJ
import org.scijava.ui.DialogPrompt

import org.vcell.imagej.helper.VCellHelper.IJTimeSeriesJobResults
import org.vcell.imagej.helper.VCellHelper.IJVarInfo
import org.vcell.imagej.helper.VCellHelper.IJVarInfos
import org.vcell.imagej.helper.VCellHelper.TimePointData
import org.vcell.imagej.helper.VCellHelper.VCellModelVersionTimeRange
import net.imagej.display.DefaultDatasetView
import net.imglib2.display.ColorTable

import java.text.SimpleDateFormat
import java.awt.Frame

import ij.process.LUT
import net.imglib2.img.array.ArrayImgs
import org.apache.commons.math.stat.descriptive.SummaryStatistics
import ij.IJ

try{
	lastVCellApiPort = vcHelper.findVCellApiServerPort();//search for port that vcell is providing IJ related services on
	System.out.println(lastVCellApiPort);
}catch(Exception e){
	imagej.ui().showDialog(e.getMessage()+"\nCheck VCell running and Tools->'Start FIJI (ImageJ) Service'. is started","Couldn't Communicate with VCell",DialogPrompt.MessageType.ERROR_MESSAGE)
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
	imagej.ui().showDialog("No VCell models found matching search criteria","Search Results Empty",DialogPrompt.MessageType.INFORMATION_MESSAGE)
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
	final currDisp = imagej.display().createDisplay(title,dataImg)
	//imagej.imageDisplay().getActivePosition().setPosition(10,0)
	currDataView = (DefaultDatasetView)currDisp.getActiveView()
	//println(currDataView.getChannelCount())
	ColorTable ct = vcHelper.getVCellColorTable("bg")
	red = new byte[256]
	grn = new byte[256]
	blu = new byte[256]
	for(int i=0;i<256;i++){
		red[i] = ct.get(0,i)
		grn[i] = ct.get(1,i)
		blu[i] = ct.get(2,i)
	}
	vcLut = new LUT(red,grn,blu)
	print(vcLut)
	//currDataView.setColorTable(ct,0)
	println(currDisp.getCanvas().getViewportWidth()+" "+currDisp.getCanvas().getViewportWidth())
	IJ.run("Set... ", "zoom="+((int)(100*Math.rint(400/bsd.xsize)))+" x="+((int)(bsd.xsize/2))+" y="+((int)(bsd.ysize/2)));
	IJ.setSlice((int)(bsd.zsize/2))
	IJ.getImage().setLut(vcLut)
	/*
	allImages = IJ.getImage().getStack()
	print(allImages)
	sumStat = new SummaryStatistics()
	for(int i=0;i<allImages.length;i++){
		if(allImages[i] != null){
			floats = (float[])allImages[i]
			for(int j=0;j<floats.length;j++){
				sumStat.add(floats[j])
			}
		}
	}
	print(sumStat)
	*/
	//stats = IJ.getImage().getStatistics()
	//print(stats)
	mm = imagej.op().stats().minMax(dataImg)
	a = mm.getA().get()
	b = mm.getB().get()
	println(a+" "+b)
	IJ.setMinAndMax(a,b)
	//ij.IJ.run("Rainbow RGB");
	//ij.IJ.run("Enhance Contrast", "saturated=0.35");
	//ij.IJ.run("In [+]", "");
	//ij.IJ.run("In [+]", "");
	//ij.IJ.run("In [+]", "");
	userinterf = imagej.ui().getDefaultUI()
	frames = Frame.getFrames()
	for(int i=0;i<frames.length;i++){
		print(frames[i].getTitle()+" "+frames[i].isDisplayable()+"\n")
		if(frames[i].getTitle().equals(title)){
			print(frames[i])
		}
	}
	/*
	imagej.ui().show(title,dataImg)
	imagej.imageDisplay().getActiveImageDisplay().getCanvas().setZoom(3);
	currDisplay= imagej.display().getDisplay(title)
	println(currDisplay)
	*/
}catch(Exception e){
	imagej.ui().showDialog(e.getMessage(),"Data Error",DialogPrompt.MessageType.ERROR_MESSAGE)
	return
}
