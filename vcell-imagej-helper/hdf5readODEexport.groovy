
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.structs.H5G_info_t;
import ncsa.hdf.hdf5lib.structs.H5A_info_t;

import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.ChartPanel
import org.jfree.data.category.CategoryDataset
import org.jfree.data.xy.XYDataset
import org.jfree.data.xy.DefaultXYDataset
import javax.swing.JFrame

      //final String FILE = "/home/vcell/testode_withoutparams.hdf5"; //Solver Suite 5.3 ppimage (Fri Jan 28 04:11:06 EST 2022):non-spatial ODE:Copy of Copy of Copy of IDA
      final String FILE = "/home/vcell/testode.hdf5"; //Solver Suite 5.3 ppimage (Fri Jan 28 04:11:06 EST 2022):non-spatial ODE:Copy of Copy of Copy of IDA
      //final String FILE = "/home/vcell/testode_stoch.hdf5"; //Solver Suite 5.3 ppimage (Fri Jan 28 04:11:06 EST 2022):non-spatial stoch:Copy of Copy of Gibson
      int file_id = -1;       // file identifier 
      int dataset_id = -1;    // dataset identifier
      int status = -1;
       
      // Open an existing file
      file_id = H5.H5Fopen (FILE, HDF5Constants.H5F_ACC_RDONLY, HDF5Constants.H5P_DEFAULT);
      ArrayList<String> attrDescrList = getStringsFromAttribute(file_id,"dataSourceDescr");
      String subTitle = attrDescrList.get(0);
      
      H5G_info_t info = H5.H5Gget_info(file_id);
      println("links="+info.nlinks);
      ArrayList<double[][]> dataSets = new ArrayList<double[][]>();
      ArrayList<ArrayList<String>> attrNamesLists = new ArrayList<ArrayList<String>>();
      ArrayList<ArrayList<String>> attrParamNamesLists = new ArrayList<ArrayList<String>>();
      ArrayList<ArrayList<String>> attrParamValuesLists = new ArrayList<ArrayList<String>>();
      String dataType = "";
      for(int i=0;i<info.nlinks;i++){
      	//H5Lget_name_by_idx(int loc_id, String group_name,int idx_type, int order, long n, int lapl_id)
      	String name = H5.H5Lget_name_by_idx(file_id, "/",HDF5Constants.H5_INDEX_NAME, HDF5Constants.H5_ITER_NATIVE,i, HDF5Constants.H5P_DEFAULT);
      	println(name);
        // Open an existing dataset.
        dataset_id = H5.H5Dopen (file_id, "/"+name+"/data"); 
        //H5A_info_t H5Aget_info_by_name(int loc_id, String obj_name, String attr_name, int lapl_id)
        //String attrName = "paramNames";
        ArrayList<String> attrTypesList = getStringsFromAttribute(dataset_id,"_type");
        dataType = attrTypesList.get(0);
        println("DataType= "+dataType);
        
        ArrayList<String> attrNamesList = getStringsFromAttribute(dataset_id,"dataSetNames");
        attrNamesLists.add(attrNamesList);
        
        ArrayList<String> attrParamNamesList = getStringsFromAttribute(dataset_id,"paramNames");
        if(attrParamNamesList != null){
        	attrParamNamesLists.add(attrParamNamesList);
        }
        ArrayList<String> attrParamValuesList = getStringsFromAttribute(dataset_id,"paramValues");
        if(attrParamValuesList != null){
        	attrParamValuesLists.add(attrParamValuesList);
        }
        

        double[][] data = getDataValues(dataset_id);
        dataSets.add(data);
      
        // Close the dataset.
        status = H5.H5Dclose (dataset_id);

         //if(i==2){
         //	createChart(data,attrNamesList,attrDescrList.get(0));
         //}
      }
   
      // Close the file.
      status = H5.H5Fclose (file_id);

if(dataType.equals("ODE Data Export")){// ODE non-stochastic with or without param scan
      createODEChart(dataSets,attrNamesLists,attrParamNamesLists,attrParamValuesLists,subTitle);	
}else{// ODE stochastic, without param scan
	createStochChart();
}

public void createStochChart(ArrayList<double[][]> dataSets,ArrayList<ArrayList<String>> attrNamesLists,String subTitle){
	String title = "Stoch ODE"+" "+subTitle
    String xAxisLabel = "No. of Particles"
    String yAxisLabel = "Probability Distribution"

}

public void createODEChart(ArrayList<double[][]> dataSets,ArrayList<ArrayList<String>> attrNamesLists,ArrayList<ArrayList<String>> attrParamNamesLists,ArrayList<ArrayList<String>> attrParamValuesLists,String subTitle){
	String title = "ODE"+" "+subTitle
    String xAxisLabel = "time"
    String yAxisLabel = "value"

    xyDataset = new DefaultXYDataset()
 for(int i=0;i<dataSets.size();i++){
	double[][] allData = dataSets.get(i);
	ArrayList<String> varNames = attrNamesLists.get(i);
	for(int j=0;j<allData.length-1;j++){
	  double[][] data = new double[2][];
	  data[0] = allData[0];//time
      data[1] = allData[j+1];
      println("----- "+varNames.get(j+1));
      String paramName = "";
      if(attrParamNamesLists.size() != 0 && attrParamValuesLists.size() != 0){
      	paramName = " "+attrParamNamesLists.get(i)+" "+attrParamValuesLists.get(i);
      }
      xyDataset.addSeries((Comparable) varNames.get(j+1)+paramName, data)
	}
 }
/*
double[][] data = new double[2][];
data[0] = allData[0];
data[1] = allData[1];
xyDataset = new DefaultXYDataset()
xyDataset.addSeries((Comparable) attrNamesList.get(1), data)
double[][] data2 = new double[2][];
data2[0] = allData[0];
data2[1] = allData[2];
xyDataset.addSeries((Comparable) attrNamesList.get(2), data2);

    for(int i=0;i<allData[0].length;i++){
    	print(allData[0][i]+" ");
    }
    println();
    println();
    for(int i=0;i<data[1].length;i++){
    	print(data[1][i]+" ");
    }
    println();
    println();
    for(int i=0;i<data2[1].length;i++){
    	print(data2[1][i]+" ");
    }
    println();
    println();
*/

chart = ChartFactory.createXYLineChart( title,  xAxisLabel,  yAxisLabel, xyDataset)
chartPanel = new ChartPanel(chart)

frame = new JFrame("Chart");
        //frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
frame.getContentPane().add(chartPanel)
        //Display the window.
frame.pack();
frame.setVisible(true);

}


public double[][] getDataValues(int dataset_id){
	long dataTotalSize = H5.H5Dget_storage_size(dataset_id);
	println("TotalDataSizeInBytes="+dataTotalSize);
	int space_id = H5.H5Dget_space(dataset_id);
	int dataDims = H5.H5Sget_simple_extent_ndims(space_id);
	println("dataDims="+dataDims);
	long[] dims = new long[dataDims];
	long[] maxdims = new long[dataDims];
	H5.H5Sget_simple_extent_dims(space_id, dims, maxdims);
	print("Dimensions=");
	int totalNumElements = 0;
	for(int i=0;i<dataDims;i++){
		print((i!=0?",":"")+dims[i]);
		if(i==0){
			totalNumElements = dims[0];
		}else{
			totalNumElements*=dims[i]
		}
	}
	println();
	println("totalNumElements="+totalNumElements);
	H5.H5Sclose(space_id);
	double[] dset_data = new double[totalNumElements];
    int status = H5.H5Dread_double(dataset_id,HDF5Constants.H5T_NATIVE_DOUBLE, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT, dset_data);
    //for(int i=0;i<10;i++){
    //	print(dset_data[i]+" ");
    //}
    //println();
    double[][] returnData = new double[dims[0]][dims[1]];
    for(int i=0;i<dims[0];i++){
    	System.arraycopy(dset_data,(int)(i*dims[1]),returnData[i],0,(int)dims[1]);
    }
    //int dataset_id,int mem_type_id,int mem_space_id, int file_space_id,int xfer_plist_id, double[] buf)
	//H5T_NATIVE_DOUBLE

	return returnData;
}

public ArrayList<String> getStringsFromAttribute(int dataset_id,String attrName){
	if(!H5.H5Aexists(dataset_id,attrName)){
		return null;
	}
      H5A_info_t attrInfo = H5.H5Aget_info_by_name(dataset_id, ".", attrName, HDF5Constants.H5P_DEFAULT);
      int attrid = H5.H5Aopen(dataset_id,attrName,HDF5Constants.H5P_DEFAULT);
      int atypeid = H5.H5Aget_type(attrid);
      int sizeOfEach = H5.H5Tget_size(atypeid);
      long attrTotalSize = H5.H5Aget_storage_size(attrid);
      int attrCount = (attrTotalSize/sizeOfEach);
      if((attrTotalSize%sizeOfEach) != 0){
      	throw new Exception("Unexpected attrTotalSize not multiple of sizeOfEach");
      }
      println("attr="+attrName+" attrTotalSize="+attrTotalSize+" attrSizeOfEach="+sizeOfEach+" attrCount="+attrCount);
      byte[] attrValue = new byte[attrTotalSize];
      H5.H5Aread(attrid, atypeid, attrValue);
      String s = new String(attrValue);
      byte[] attr = new byte[sizeOfEach];
      ArrayList<String> attrNamesList = new ArrayList<String>();
      for(int j=0;j<attrCount;j++){
      	System.arraycopy(attrValue,j*sizeOfEach,attr,0,sizeOfEach);
      	StringBuffer sb = new StringBuffer();
      	for(int k=0;k<sizeOfEach;k++){
      		if(attr[k] != 0){
      			sb.append((char)attr[k]);
      		}else{
      			break;
      		}
      	}
      	println(sb.toString());
      	attrNamesList.add(sb.toString());
      }
      H5.H5Aclose(attrid);
      return attrNamesList;
}

