#@VCellHelper vh

import java.net.URL;
import org.vcell.imagej.helper.VCellHelper.VCellModelSearch
import org.vcell.imagej.helper.VCellHelper.ModelType
import org.vcell.imagej.helper.VCellHelper.VCellModelSearchResults

println(vh)

vcServerPort = vh.findVCellApiServerPort()
println(vcServerPort)
xmlStr = vh.getApiInfo()
//println(xmlStr)
infoUrl = new URL("http://localhost:"+vcServerPort+"/getinfo?type=quick&open=true")
info = vh.getRawContent(infoUrl)
println(infoUrl.toString())
println(info)
vcms = new VCellModelSearch(ModelType.quick,null,null,null,null,null,null)
vcmsr = vh.getSearchedModelSimCacheKey(true,vcms,null)
theCacheKey = vcmsr.get(0).getCacheKey();
println(theCacheKey)
varUrl = new URL("http://localhost:"+vcServerPort+"/getdata?cachekey="+theCacheKey)
varInfoRaw = vh.getRawContent(varUrl)
println(varUrl.toString())
println(varInfoRaw)
varInfo = vh.getSimulationInfo(theCacheKey)
println("# vars:")
println(varInfo.getIjVarInfo().size())
dataUrl = new URL("http://localhost:"+vcServerPort+"/getdata?cachekey="+theCacheKey+"&varname=C_cyt&timeindex=1")
doc = vh.getDocument(dataUrl)
println(dataUrl.toString())
println(vh.documentToString(doc))
tpIndexes = [1] as int[]
tpd = vh.getTimePointData(theCacheKey,"C_cyt",tpIndexes,0)
double[] data = tpd.getTimePointData()
println("data length:")
println(data.length)