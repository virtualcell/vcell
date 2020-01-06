//@VCellHelper vh

#@ File (label="Project File", value="NewProject.txt") projFile

#@ File (label="TimeSeries Data Source",value="/home/vcell/Desktop/Frank_for_ImageJ_VCell/Dicty Actin Cortex/TS.nd2") tsDataFile
#@ String (label="TS Data Channel",value="2") tsDataChanNumStr
#@ String (label="TS Cell ROI [x,y,width,height]",value="[129, 48, 96, 88]") tsCellRoi
#@ String (label="TS BG ROI [x,y,width,height]",value="[20, 30, 50, 50]") tsBGRoi

#@ File (label="Geom/FieldData Source",value="/home/vcell/Desktop/Frank_for_ImageJ_VCell/Dicty Actin Cortex/ZS.nd2") zsDataFile
#@ String (label="Geom/FieldData Channel",value="1") zsDataChanNumStr
// String (label="Geom/FieldData ROI",value="[x,y,width,height]") zsCellRoi

#@ Float (label="Geom smooth factor", value=2.0) geomGuassSigma
#@ String (label="Geom threshold", value="[76,65535]") geomThreshold

#@ File (label="User Fill RoiSet", value="/home/vcell/Desktop/Frank_for_ImageJ_VCell/Dicty Actin Cortex/fillRoiSet.zip") fillSet
#@ File (label="User Clear RoiSet", value="/home/vcell/Desktop/Frank_for_ImageJ_VCell/Dicty Actin Cortex/clearRoiSet.zip") clearSet
#@ File (label="User Nuc RoiSet", value="/home/vcell/Desktop/Frank_for_ImageJ_VCell/Dicty Actin Cortex/nucRoiSet.zip") nucSet



import org.vcell.imagej.helper.VCellHelper.IJGeom
import org.vcell.imagej.helper.VCellHelper
import org.vcell.imagej.helper.VCellHelper.IJFieldData
import net.imglib2.Cursor;
import java.text.SimpleDateFormat;
import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults;

import net.imagej.ImgPlus
import net.imglib2.type.numeric.integer.UnsignedByteType
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imglib2.img.ImagePlusAdapter
import net.imglib2.util.Intervals;
import ij.plugin.frame.RoiManager

ij.IJ.setForegroundColor(255,255,255);
ij.IJ.run("Set Measurements...", "mean min redirect=None decimal=3");

//Get Actin and ABP timeseries images, subtract background , crop to cell ROI
ij.IJ.run("Bio-Formats", "open=["+tsDataFile.getAbsolutePath()+"] color_mode=Default rois_import=[ROI manager] view=Hyperstack stack_order=XYCZT");
tsChannelCount = ij.IJ.getImage().getNChannels()
String TS_NAME = "TS"
ij.IJ.getImage().setTitle(TS_NAME)
ij.IJ.run("Split Channels");

//Remove Unwanted channels
tsDataChanNum = Integer.valueOf(tsDataChanNumStr)
removeUnwantedChannels(tsDataChanNum,tsChannelCount,TS_NAME)

//BG subtract, crop abp and actin
abp_actin_cellROI = parseArrROI(tsCellRoi)//[129, 48, 96, 88] as int[]
abp_actin_bgROI   = parseArrROI(tsBGRoi)  //[20, 30, 50, 50] as int[]
//enhanceAndStats("C2-TS.nd2", "actin",abp_actin_cellROI,abp_actin_bgROI)
enhanceAndStats("C"+tsDataChanNum+"-"+TS_NAME, "abp",abp_actin_cellROI,abp_actin_bgROI)

/*
//Create FieldData from time 0.0 (slice 1) of 'abp'
ij.IJ.selectWindow("abp");
abpImage = ij.IJ.getImage()
fdImage = ij.IJ.createImage("fieldData", "16-bit", abpImage.getWidth(), abpImage.getHeight(), 1)
//fdImage.getStack().deleteSlice(1)
ip = abpImage.getStack().getProcessor(1)
fip = fdImage.getStack().getProcessor(1)
for (x=0;x<abpImage.getWidth();x++){
	for (y=0;y<abpImage.getHeight();y++){
		fip.set(x, y, ip.get(x, y))
	}
}
fdImage.show()
zoomEnhance()
myshorts = fdImage.getStack().getProcessor(1).getPixels()
//myFieldData = new IJFieldData("test",fdImage.getWidth(),fdImage.getHeight(),1,myshorts)
//vh.sendFieldData(myFieldData)
//if(true){return}
*/

//Get zseries
ij.IJ.run("Bio-Formats", "open=["+zsDataFile.getAbsolutePath()+"] color_mode=Default rois_import=[ROI manager] view=Hyperstack stack_order=XYCZT");
String ZS_NAME = "ZS"
ij.IJ.getImage().setTitle(ZS_NAME)
zsChannelCount = ij.IJ.getImage().getNChannels()
ij.IJ.run("Split Channels");
//Remove unwanted channels
zsDataChanNum = Integer.valueOf(zsDataChanNumStr)
removeUnwantedChannels(zsDataChanNum,zsChannelCount,ZS_NAME)

//Select abp
ij.IJ.selectWindow("C"+zsDataChanNum+"-"+ZS_NAME);
ij.IJ.getImage().setTitle("FieldData")
geomRaw = ij.IJ.getImage().duplicate()
geomRaw.show()//makes this the current image
geomRaw.setTitle("Geom")
//Smooth
//ij.IJ.run("Despeckle", "stack");
ij.IJ.run("Gaussian Blur...", "sigma="+geomGuassSigma+" stack");
//Get avg background
ij.IJ.makeRectangle(abp_actin_bgROI[0],abp_actin_bgROI[1],abp_actin_bgROI[2],abp_actin_bgROI[3]); //Z Background
ij.IJ.run("Clear Results");
ij.IJ.run("Statistics");
meanZbgArr = textPanel.getResultsTable().getColumnAsVariables("Mean")
println("meanZbgArr="+meanZbgArr[0])
//Subtract BG from Geom
ij.IJ.selectWindow("Geom")
ij.IJ.run("Select None");//apply to whole image
ij.IJ.run("Subtract...", "value="+meanZbgArr[0]+" stack");//BG subtract "Geom"
//Subtract BG from FieldData
ij.IJ.selectWindow("FieldData")
ij.IJ.run("Subtract...", "value="+meanZbgArr[0]+" stack");//BG subtract "FieldData"
//Crop FieldData
ij.IJ.makeRectangle(abp_actin_cellROI[0],abp_actin_cellROI[1],abp_actin_cellROI[2],abp_actin_cellROI[3]);//Z Cell
ij.IJ.run("Crop");
zoomEnhance()
//Crop Geom
ij.IJ.selectWindow("Geom")
ij.IJ.makeRectangle(abp_actin_cellROI[0],abp_actin_cellROI[1],abp_actin_cellROI[2],abp_actin_cellROI[3]);//Z Cell
ij.IJ.run("Crop");
zoomEnhance()
//make Geom threshold mask
ij.IJ.resetMinAndMax();
ij.IJ.resetThreshold();
thresh = parseArrROI(geomThreshold)
ij.IJ.setThreshold(thresh[0],thresh[1]);
ij.IJ.run("Convert to Mask", "method=Default background=Dark black");

//Cleanup holes/stragglers on mask
println(fillSet.getAbsolutePath())
fillUserDefinedROI(255,255,255,fillSet)
fillUserDefinedROI(0,0,0,clearSet)
fillUserDefinedROI(128,128,128,nucSet)

/*
ij.IJ.run("Wand Tool...", "tolerance=0 mode=Legacy");
clearROI(4,-1,-1,false)//make a slice background (erase unwanted mask pieces)
clearROI(26,-1,-1,false)
clearROI(6,67,39,true)//Wand select, fill hole with foreground
clearROI(7,67,39,true)
clearROI(8,67,39,true)
clearROI(9,67,39,true)
clearROI(10,66,38,true)
clearROI(10,57,52,true)
clearROI(11,57,51,true)
clearROI(12,66,36,true)
clearROI(16,50,62,true)
clearROI(17,47,57,true)
clearROI(18,40,54,true)
//Make mucleus different color
ij.IJ.setForegroundColor(128, 128, 128);
clearROI(12,61,48,true)
clearROI(13,61,48,true)
clearROI(14,61,48,true)
clearROI(15,61,48,true)
clearROI(16,61,48,true)
clearROI(17,61,48,true)
clearROI(18,61,48,true)
clearROI(19,59,50,true)
*/

//Reset tools
ij.IJ.run("Select None");
ij.IJ.setTool("rectangle");

def void fillUserDefinedROI(int r,int g,int b,File userRoiManagerFile){
	RoiManager roiManager = RoiManager.getRoiManager()
	roiManager.reset()
	ij.IJ.setForegroundColor(r,g,b);
	roiManager.runCommand("Open",userRoiManagerFile.getAbsolutePath())
	roiManager.runCommand("Fill")
}

def void removeUnwantedChannels(int keepThisChannel,int channelCount,String nameSuffix){
	for(int i=1;i <= channelCount;i++){
	if(i != keepThisChannel){
		ij.IJ.selectWindow("C"+i+"-"+nameSuffix);
		ij.IJ.getImage().close();		
	}
}
}

def int[] parseArrROI(String csv){
	if(csv.startsWith("[") && csv.endsWith("]")){
		csv = csv.substring(1,csv.length()-1)
	}
	StringTokenizer st = new StringTokenizer(csv,",")
	result = new int[st.countTokens()]
	for(int i=0;i<result.length;i++){
		result[i] = Integer.valueOf(st.nextToken().trim())
	}
	return result
}

/*
//Create IJGeom
ImgPlus<UnsignedByteType> geomImgPlus = ImagePlusAdapter.wrapImgPlus(ij.IJ.getImage())
int xIndex = geomImgPlus.dimensionIndex(Axes.X);
int xsize = (int)geomImgPlus.dimension(xIndex);
int yIndex = geomImgPlus.dimensionIndex(Axes.Y);
int ysize = (int)geomImgPlus.dimension(yIndex);
int assumedZIndex = -1;
for (int i = 0; i < geomImgPlus.numDimensions(); i++) {
	if(geomImgPlus.dimensionIndex(Axes.X)!=i && geomImgPlus.dimensionIndex(Axes.Y)!=i) {
		assumedZIndex = i;
		break;
	}
}
int zsize = (assumedZIndex == -1?1:(int)geomImgPlus.dimension(assumedZIndex));
HashMap<Integer, String> segmentedGeomValuesMapSubvolumeName = new HashMap();
segmentedGeomValuesMapSubvolumeName.put(0, "ec");
segmentedGeomValuesMapSubvolumeName.put(255, "cyt");
segmentedGeomValuesMapSubvolumeName.put(128, "Nuc");
IJGeom overrideGeom = createGeometry(geomImgPlus, xIndex, yIndex, assumedZIndex, xsize, ysize, zsize, segmentedGeomValuesMapSubvolumeName);
println(overrideGeom)

//Create FieldData
ij.IJ.selectWindow("FieldData")
times = [0.0] as double[]
vars = ["BS"] as String[]
xsize = ij.IJ.getImage().getWidth()
ysize = ij.IJ.getImage().getHeight()
zsize = ij.IJ.getImage().getNSlices()
fdData = new short[times.length][vars.length][xsize*ysize*zsize]
fdStackArr = ij.IJ.getImage().getStack().getImageArray()
println(xsize+" "+ysize+" "+zsize+" "+fdStackArr.length)
for(int i=0;i<zsize;i++){
	short[] currShorts = (short[])fdStackArr.getAt(i)
	if(currShorts.length != xsize*ysize){
		throw new Exception("Expecting all z-slices size="+(xsize*ysize)+" but z="+i+" sizes="+currShorts.length)
	}
	System.arraycopy(currShorts,0,fdData[0][0],i*xsize*ysize,currShorts.length)
	println(currShorts.length)
}
ijFieldData = new IJFieldData("ijFDTest", vars,
	xsize,ysize,zsize,
	fdData,
	times,
	26.445,25.625,16.5, //Extent
	0.0,0.0,0.0)//Origin
println(ijFieldData)
*/

/*
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
	VCellHelper.VCellModelSearch search = new VCellHelper.VCellModelSearch(VCellHelper.ModelType.bm, "frm", "Photoactivation and Binding 2", "3D Simulation", "Parameter Scan",2,"Deterministic");
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
//	try{
//		//make sure original sim result is open
//		VCellHelper.IJDataList ijDataList = vh.getTimePointData(cacheKey, null, null, 0);
//	}catch(Exception e){
//		ij.IJ.showMessage("Make sure Application '"+search.getApplicationName()+"' -> Simulation '"+search.getSimulationName()+"' results viewer is open");
//		throw e
//	}

	return cacheKey;

}

def IJGeom createGeometry(ImgPlus<UnsignedByteType> nonZProjectedSegmentedGeom,int xIndex,int yIndex,int assumedZIndex,int xsize,int ysize,int zsize,HashMap<Integer, String> segmentedGeomValuesMapSubvolumeName) throws Exception{
	//Create byte array that defines vcell subvolumes ids (handles)
	byte[] vcellSubvolumeHandles = new byte[(int)Intervals.numElements(nonZProjectedSegmentedGeom)];
	Cursor<UnsignedByteType> nonZProjectedSegmentedGeomCursor = nonZProjectedSegmentedGeom.localizingCursor();
	while(nonZProjectedSegmentedGeomCursor.hasNext()){
		UnsignedByteType segmentedGeomValue = (UnsignedByteType) nonZProjectedSegmentedGeomCursor.next();
		int currentZ = (assumedZIndex==-1?0:nonZProjectedSegmentedGeomCursor.getIntPosition(assumedZIndex));
		int currXYPixelIndex = nonZProjectedSegmentedGeomCursor.getIntPosition(yIndex)*xsize + nonZProjectedSegmentedGeomCursor.getIntPosition(xIndex);
		int currXYZPixelIndex = currXYPixelIndex + (assumedZIndex==-1?0:currentZ*(xsize*ysize));
		vcellSubvolumeHandles[currXYZPixelIndex] = (byte)segmentedGeomValue.get();
	}
	//Define new Geometry with domain equal to xyz pixel size for ease of use
	String[] subvolumeNames = segmentedGeomValuesMapSubvolumeName.values().toArray(new String[0]);
	Integer[] subvolumePixelValues = segmentedGeomValuesMapSubvolumeName.keySet().toArray(new Integer[0]);
	double[] origin = [0,0,0] as double[];
	double[] extent = [xsize,ysize,zsize] as double[];
	IJGeom ijGeom = new IJGeom(subvolumeNames, subvolumePixelValues, xsize, ysize, zsize, origin, extent, vcellSubvolumeHandles);
	return ijGeom;
}
*/

def clearROI(int slice,int x,int y,boolean bFill){
	ij.IJ.setSlice(slice);
	if(x==-1 || y == -1){
		ij.IJ.run("Select All");
	}else{
		ij.IJ.doWand(x, y);
	}
	if(bFill){
		ij.IJ.run("Fill", "slice");
	}else{
		ij.IJ.run("Clear", "slice");
	}
}

def void zoomEnhance(){
ij.IJ.run("In [+]");
ij.IJ.run("In [+]");
ij.IJ.run("In [+]");
ij.IJ.run("Enhance Contrast", "saturated=0.35");
}

def void enhanceAndStats(String imageWindowTitle,String newTitle, int[] cellROI,int[] bgROI) {
ij.IJ.selectWindow(imageWindowTitle);
ij.IJ.getImage().setTitle(newTitle)

textPanel = ij.IJ.getTextPanel()

ij.IJ.makeRectangle(bgROI[0],bgROI[1],bgROI[2],bgROI[3]); //Background
ij.IJ.run("Clear Results");
ij.IJ.run("Statistics");
meanBGArr = textPanel.getResultsTable().getColumnAsVariables("Mean")

ij.IJ.makeRectangle(cellROI[0],cellROI[1],cellROI[2],cellROI[3]); //Cell
//ij.IJ.run("Clear Results");
//ij.IJ.run("Statistics");

ij.IJ.run("Crop");
zoomEnhance()
//minArr = textPanel.getResultsTable().getColumnAsVariables("Min")
//maxArr = textPanel.getResultsTable().getColumnAsVariables("Max")
//println(newTitle+" "+minArr[0]+" "+maxArr[0]+" bgMean="+meanBGArr[0])

ij.IJ.run("Subtract...", "value="+meanBGArr[0]+" stack");
}
