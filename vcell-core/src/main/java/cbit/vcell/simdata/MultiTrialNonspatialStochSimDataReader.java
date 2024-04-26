package cbit.vcell.simdata;

import cbit.vcell.parser.ExpressionException;
import cbit.vcell.solver.ode.ODESimData;
import com.google.common.io.Files;
import ncsa.hdf.object.FileFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5ScalarDS;
import org.vcell.util.ObjectNotFoundException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MultiTrialNonspatialStochSimDataReader {

    public static double[] extractColumn(ODESimData odeSimData, String columnName, SummaryStatisticType summaryStatisticType) throws ExpressionException, ObjectNotFoundException {
        FileFormat hdf5FileFormat = null;
        File to = null;
        try {
            byte[] hdf5FileBytes = odeSimData.getHdf5FileBytes();
            if(hdf5FileBytes != null) {
                to = File.createTempFile("multitrial_nonspatial_stats_", ".hdf5");
                Files.write(hdf5FileBytes, to);
                FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
                if (fileFormat == null){
                    throw new Exception("Cannot find HDF5 FileFormat.");
                }
                // open the file with read-only access
                hdf5FileFormat = fileFormat.createInstance(to.getAbsolutePath(), FileFormat.READ);
                // open the file and retrieve the file structure
                hdf5FileFormat.open();
                Group root = (Group)((javax.swing.tree.DefaultMutableTreeNode)hdf5FileFormat.getRootNode()).getUserObject();
                List<HObject> postProcessMembers = ((Group)root).getMemberList();
                for(HObject nextHObject : postProcessMembers) {
                    System.out.println(nextHObject.getName()+"   "+nextHObject.getClass().getName());
                    H5ScalarDS h5ScalarDS = (H5ScalarDS)nextHObject;
                    h5ScalarDS.init();
                    try {
                        long[] dims = h5ScalarDS.getDims();
                        System.out.println("---"+nextHObject.getName()+" "+nextHObject.getClass().getName()+" Dimensions="+Arrays.toString(dims));
                        Object obj = h5ScalarDS.read();
                        if(dims.length == 2) {
                            double[] columns = new double[(int)dims[1]];
                            for(int row=0;row<dims[0];row++) {
                                System.arraycopy(obj, row*columns.length, columns, 0, columns.length);
                                System.out.println(Arrays.toString(columns));
                            }
                            return null;
    //								return columns;
                        } else {
                            return null;
                        }
                    } catch(Exception e) {
                        return null;
                    }
                }
            }
         } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(hdf5FileFormat != null) {try{hdf5FileFormat.close();}catch(Exception e2) {e2.printStackTrace();}}
            if(to != null){try{to.delete();}catch(Exception e2) {e2.printStackTrace();}}
        }
        return null;
    }

    private void example_reader(ODESimData odeSimData) {
        //
        //Example code for reading stats data from Stochastic multitrial non-histogram
        //
        FileFormat hdf5FileFormat = null;
        File to = null;
        try {
                byte[] hdf5FileBytes = odeSimData.getHdf5FileBytes();
                if(hdf5FileBytes != null) {
                    to = File.createTempFile("multitrial_nonspatial_stats_", ".hdf5");
                    Files.write(hdf5FileBytes, to);
                    FileFormat fileFormat = FileFormat.getFileFormat(FileFormat.FILE_TYPE_HDF5);
                    if (fileFormat == null){
                        throw new Exception("Cannot find HDF5 FileFormat.");
                    }
                    // open the file with read-only access
                    hdf5FileFormat = fileFormat.createInstance(to.getAbsolutePath(), FileFormat.READ);
                    // open the file and retrieve the file structure
                    hdf5FileFormat.open();
                    Group root = (Group)((javax.swing.tree.DefaultMutableTreeNode)hdf5FileFormat.getRootNode()).getUserObject();
                    List<HObject> postProcessMembers = ((Group)root).getMemberList();
                    for(HObject nextHObject:postProcessMembers){
                        //System.out.println(nextHObject.getName()+"\n"+nextHObject.getClass().getName());
                        H5ScalarDS h5ScalarDS = (H5ScalarDS)nextHObject;
                        h5ScalarDS.init();
                        try {
                            long[] dims = h5ScalarDS.getDims();
                            System.out.println("---"+nextHObject.getName()+" "+nextHObject.getClass().getName()+" Dimensions="+ Arrays.toString(dims));
                            Object obj = h5ScalarDS.read();
                            if(dims.length == 2) {
                                //dims[0]=numTimes (will be the same as 'SimTimes' data length)
                                //dims[1]=numVars (will be the same as 'VarNames' data length)
                                //if name='StatMean' this is the same as the default data saved in the odeSolverresultSet
                                double[] columns = new double[(int)dims[1]];
                                for(int row=0;row<dims[0];row++) {
                                    System.arraycopy(obj, row*columns.length, columns, 0, columns.length);
                                    System.out.println(Arrays.toString(columns));
                                }
                            }else {
                                if(obj instanceof double[]) {
                                    System.out.println(Arrays.toString((double[])obj));
                                }else {
                                    System.out.println(Arrays.toString((String[])obj));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(hdf5FileFormat != null) {try{hdf5FileFormat.close();}catch(Exception e2) {e2.printStackTrace();}}
            if(to != null){try{to.delete();}catch(Exception e2) {e2.printStackTrace();}}
        }

    }
}
