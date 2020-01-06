//@VCellHelper vh

import org.vcell.imagej.helper.VCellHelper
import org.vcell.imagej.helper.VCellHelper.IJFieldData
import java.text.SimpleDateFormat;
import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults;
import net.imagej.ImgPlus
import net.imglib2.type.numeric.integer.UnsignedByteType
import net.imagej.axis.Axes;
import net.imagej.axis.AxisType;
import net.imglib2.img.ImagePlusAdapter
import net.imglib2.util.Intervals;
import org.vcell.imagej.helper.VCellHelper.IJGeom
import net.imglib2.Cursor;

fieldDataName = "ijFDTest"
fieldDataVarName = "BS"
fieldDataTime = 0.0
HashMap<String,String> simulationParameterOverrides = new HashMap<>();
simulationParameterOverrides.put("Kr_bind", ".01,.1,1.0,10"); //See 'Simulation List Table'->'Edit Simulation'->'Parameters tab'->'Parameter name'
HashMap<String, String> speciesContextInitialConditionsOverrides = new HashMap<>();
speciesContextInitialConditionsOverrides.put("BS", "(0.56 * vcField('"+fieldDataName+"', '"+fieldDataVarName+"', "+fieldDataTime+", 'Volume'))"); //See Application->Specifications->Species
newEndTime = 180.0

//Create IJGeom
ij.IJ.selectWindow("Geom")
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
segmentedGeomValuesMapSubvolumeName.put(0, "EC");
segmentedGeomValuesMapSubvolumeName.put(255, "cell");
segmentedGeomValuesMapSubvolumeName.put(128, "nuc");
IJGeom overrideGeom = createGeometry(geomImgPlus, xIndex, yIndex, assumedZIndex, xsize, ysize, zsize, 26.445,25.625,16.5,segmentedGeomValuesMapSubvolumeName);
println(overrideGeom)

//Create FieldData
ij.IJ.selectWindow("FieldData")
times = [fieldDataTime] as double[]
vars = [fieldDataVarName] as String[]
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
	//println(currShorts.length)
}
ijFieldData = new IJFieldData(fieldDataName, vars,
	xsize,ysize,zsize,
	fdData,
	times,
	26.445,25.625,16.5, //Extent
	0.0,0.0,0.0)//Origin
println(ijFieldData)

if(ijFieldData.xExtent != overrideGeom.extentXYZ[0] || ijFieldData.yExtent != overrideGeom.extentXYZ[1] || ijFieldData.zExtent  != overrideGeom.extentXYZ[2] ||
   ijFieldData.xOrigin != overrideGeom.originXYZ[0] || ijFieldData.yOrigin != overrideGeom.originXYZ[1] || ijFieldData.zOrigin  != overrideGeom.originXYZ[2]){
	throw new Exception("Geometery and FieldData origin/extents don't match")
}
//Send FieldData to VCell
if(false){vh.sendFieldData(ijFieldData)}

//setup model override parameters
String simulationCacheKey = getSimulationCacheKey(vh);
println(simulationCacheKey)

//Change VCell model and Save only
if(false){vh.startVCellSolver(Long.parseLong(simulationCacheKey), overrideGeom, simulationParameterOverrides, speciesContextInitialConditionsOverrides,newEndTime,true);}

//Change VCell model and Save only
simulationCacheKey = getSimulationCacheKey(vh);
println(simulationCacheKey)
if(true){vh.startVCellSolver(Long.parseLong(simulationCacheKey), null, null, null,null,false);}

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

def IJGeom createGeometry(ImgPlus<UnsignedByteType> nonZProjectedSegmentedGeom,int xIndex,int yIndex,int assumedZIndex,int xsize,int ysize,int zsize,double xext,double yext,double zext,HashMap<Integer, String> segmentedGeomValuesMapSubvolumeName) throws Exception{
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
	double[] extent = [xext,yext,zext] as double[];
	IJGeom ijGeom = new IJGeom(subvolumeNames, subvolumePixelValues, xsize, ysize, zsize, origin, extent, vcellSubvolumeHandles);
	return ijGeom;
}
