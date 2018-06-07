#@VCellHelper vh

import java.net.URL;

println(vh)

vcServerPort = vh.findVCellApiServerPort()
println(vcServerPort)
xmlStr = vh.getApiInfo()
println(xmlStr)
infoUrl = new URL("http://localhost:"+vcServerPort+"/getinfo?type=quick&open=true")
//info = vh.getRawContent(infoUrl)
//println(info)
varUrl = new URL("http://localhost:"+vcServerPort+"/getdata?cachekey=0")
varInfo = vh.getRawContent(varUrl)
println(varInfo)
dataUrl = new URL("http://localhost:"+vcServerPort+"/getdata?cachekey=0&varname=C_cyt&timepoint=0.05")
doc = vh.getDocument(dataUrl)
stackDims = vh.getVCStackDims(doc)
println(stackDims.getTotalSize())
double[] data = vh.getData(doc)
println(data.length)
