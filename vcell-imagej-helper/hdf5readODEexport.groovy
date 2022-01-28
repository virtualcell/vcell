
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.structs.H5G_info_t;
import ncsa.hdf.hdf5lib.structs.H5A_info_t;

      final String FILE = "C:\\Users\\frm\\Documents\\testode.hdf5";
      int file_id = -1;       // file identifier 
      int dataset_id = -1;    // dataset identifier
      int status = -1;
       
      // Open an existing file
      file_id = H5.H5Fopen (FILE, HDF5Constants.H5F_ACC_RDONLY, HDF5Constants.H5P_DEFAULT);

      H5G_info_t info = H5.H5Gget_info(file_id);
      println("links="+info.nlinks);
      for(int i=0;i<info.nlinks;i++){
      	//H5Lget_name_by_idx(int loc_id, String group_name,int idx_type, int order, long n, int lapl_id)
      	String name = H5.H5Lget_name_by_idx(file_id, "/",HDF5Constants.H5_INDEX_NAME, HDF5Constants.H5_ITER_NATIVE,i, HDF5Constants.H5P_DEFAULT);
      	println(name);
      // Open an existing dataset.
      dataset_id = H5.H5Dopen (file_id, "/"+name+"/data"); 
      //H5A_info_t H5Aget_info_by_name(int loc_id, String obj_name, String attr_name, int lapl_id)
      String attrName = "paramNames";
      H5A_info_t attrInfo = H5.H5Aget_info_by_name(dataset_id, ".", attrName, HDF5Constants.H5P_DEFAULT);
      int attrid = H5.H5Aopen(dataset_id,"paramNames",HDF5Constants.H5P_DEFAULT);
      long attrSize = H5.H5Aget_storage_size(attrid);
      println("attr="+attrName+" attrDataSize="+attrInfo.data_size+" "+attrSize);
      //H5FD_MEM_DEFAULT
      byte[] buf = new byte[20];
      //int attrFlag = H5.H5Aread(attrid, HDF5Constants.H5FD_MEM_DEFAULT,buf);
      //String str = new String(buf);
      //println(attrFlag+" "+str);
      H5.H5Aclose(attrid);
       // Close the dataset.
      status = H5.H5Dclose (dataset_id);
      }
   
      // Close the file.
      status = H5.H5Fclose (file_id);

