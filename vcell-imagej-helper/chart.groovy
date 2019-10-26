#@VCellHelper vh

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


start = 1279
end = 1321
int[] indices = new int[end-start]
for(int i=0;i<indices.length;i++){
	indices[i] = start+i
}
String[] vars = ["r"]
vcms = new VCellModelSearch(ModelType.bm,"tutorial","Tutorial_FRAPbinding","Spatial","FRAP binding",null,null)
vcmsr = vh.getSearchedModelSimCacheKey(true,vcms,null)
theCacheKey = vcmsr.get(0).getCacheKey();
println(theCacheKey)

//public IJTimeSeriesJobResults getTimeSeries(String[] variableNames, int[] indices, double startTime, int step, double endTime,boolean calcSpaceStats, boolean calcTimeStats, int jobid, int cachekey) throws Exception{

ijTimeSeriesJobResults = vh.getTimeSeries(vars, indices, 22 as double, 1, 22 as double, false, false, 0, theCacheKey as int)
double[] vals = new double[indices.length]
for(int i=0;i<indices.length;i++){
	vals[i] = ijTimeSeriesJobResults.data[0][i+1][0]
}
double[][] data = [indices,vals];


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

