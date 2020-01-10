#@VCellHelper vh
#@ImageJ ij
import org.vcell.imagej.helper.VCellHelper
import org.vcell.imagej.helper.VCellHelper.IJDataList
import org.vcell.imagej.helper.VCellHelper.IJData
import net.imglib2.img.array.ArrayImgs

import org.vcell.imagej.helper.VCellHelper.IJTimeSeriesJobResults
import org.vcell.imagej.helper.VCellHelper.VCellModelSearch
import org.vcell.imagej.helper.VCellHelper.ModelType
import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults
//import org.vcell.imagej.helper.VCellHelper.TimePointData

vcms = new VCellModelSearch(ModelType.bm,"mblinov","Monkeyflower_pigmentation","Pattern_formation","Wildtype",null,null)
//format is "username","BioModelName","ApplicationName","SimulationName",null,null). The BioModel must be open in VCell.
vcmsr = vh.getSearchedModelSimCacheKey(false,vcms,null) 
//false enables operating with model not currently open
theCacheKey = vcmsr.get(0).getCacheKey();
println(theCacheKey)

var = "A"
//enter varialble name (from simulation results)
time= [500]
//enter timeindex (for fixed timestep timeindex will be time*saved_timesteps

IJDataList tpd = vh.getTimePointData(theCacheKey,var,VCellHelper.VARTYPE_POSTPROC.NotPostProcess,time as int[],0)

data = tpd.ijData[0].getDoubleData()
//println (data);
//if (true) {return}
bsd = tpd.ijData[0].stackInfo
println(bsd.xsize+" "+bsd.ysize)
testimg = ArrayImgs.doubles( data, bsd.xsize,bsd.ysize )
ij.ui().show(var+"_"+time[0],testimg)
//can change var+"_"+time[0] to make custom name for the image.