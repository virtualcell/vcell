package cbit.vcell.simdata;

import cbit.vcell.math.InsideVariable;
import cbit.vcell.math.OutsideVariable;
import cbit.vcell.math.Variable;
import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.vcell.util.DataAccessException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;

public class ChomboSimDataReader {
    private static final String HDF5_GROUP_SOLUTION = "/solution";
    private static final String HDF5_GROUP_EXTRAPOLATED_VOLUMES = "/extrapolated_volumes";
    private static final String HDF5_GROUP_DIRECTORY_SEPARATOR = "/";

    /**
     * Creates a relative path to the solution to the variable specified
     *
     * @param varName the name of the variable to path to.
     * @return the relative path
     */
    public static String getVarSolutionPath(String varName){
        return HDF5_GROUP_SOLUTION + HDF5_GROUP_DIRECTORY_SEPARATOR + Variable.getNameFromCombinedIdentifier(varName);
    }

    /**
     * Creates a relative path to the extrapolated values of a given variable name.
     *
     * @param varName name of the variable to path to
     * @return the relative path
     */
    public static String getVolVarExtrapolatedValuesPath(String varName){
        return HDF5_GROUP_EXTRAPOLATED_VOLUMES + HDF5_GROUP_DIRECTORY_SEPARATOR + "__" + Variable.getNameFromCombinedIdentifier(varName) + "_extrapolated__";
    }


    public static void getNextDataAtCurrentTimeChombo(double[][] returnValues, ZipFile currentZipFile, String[] varNames, int[][] varIndexes, String[] simDataFileNames, int masterTimeIndex)  throws Exception {
        File tempFile = null;
        try {
            tempFile = createTempHdf5File(currentZipFile, simDataFileNames[masterTimeIndex]);
            try (HdfFile solFile = new HdfFile(tempFile.toPath())) {

            for(int k = 0; k < varNames.length; ++ k) {
                try {
                    boolean bExtrapolatedValue = false;
                    String varName = varNames[k];
                    if (varName.endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX))
                    {
                        bExtrapolatedValue = true;
                        varName = varName.substring(0, varName.lastIndexOf(InsideVariable.INSIDE_VARIABLE_SUFFIX));
                    }
                    else if (varName.endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX))
                    {
                        bExtrapolatedValue = true;
                        varName = varName.substring(0, varName.lastIndexOf(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX));
                    }
                    double[] sol = null;
                    if (bExtrapolatedValue)
                    {
                        sol = readChomboExtrapolatedValues(varName, solFile);
                    }
                    else
                    {
                        Node solObj = findByPath(solFile, getVarSolutionPath(varNames[k]));
                        if (solObj instanceof Dataset) {
                            sol = (double[]) ((Dataset) solObj).getData();
                        }
                    }
                    if (sol != null)
                    {
                        for(int l = 0;l < varIndexes[k].length; ++ l) {
                            int idx = varIndexes[k][l];
                            double val =  sol[idx];
                            returnValues[k][l] = val;
                        }
                    }
                } catch (Exception e) {
                    throw new DataAccessException(e.getMessage(), e);
                }
            }
            }
        } finally {
            if (tempFile != null && !tempFile.delete()) {
                System.err.println("couldn't delete temp file " + tempFile.getAbsolutePath());
            }
        }
    }

    public static void readHdf5SolutionMetaData(InputStream is, Vector<DataBlock> dataBlockList) throws Exception
    {
        File tempFile = null;
        try{
            tempFile = createTempHdf5File(is);
            try (HdfFile solFile = new HdfFile(tempFile.toPath())) {
                Node solutionNode = solFile.getChildren().get("solution");
                if (!(solutionNode instanceof Group)) {
                    return;
                }
                for (Node member : ((Group) solutionNode).getChildren().values())
                {
                    if (!(member instanceof Dataset)){
                        continue;
                    }
                    Dataset dataset = (Dataset)member;
                    int vt = dataset.getAttribute("variable type") == null ? -1
                            : intValue(dataset.getAttribute("variable type").getData());
                    String domain = dataset.getAttribute("domain") == null ? null
                            : stringValue(dataset.getAttribute("domain").getData());
                    String varName = domain == null ? dataset.getName()
                            : domain + Variable.COMBINED_IDENTIFIER_SEPARATOR + dataset.getName();
                    dataBlockList.addElement(cbit.vcell.simdata.DataBlock.createDataBlock(
                            varName, vt, dataset.getDimensions()[0], 0));
                }
            }
        } finally {
            if (tempFile != null && !tempFile.delete()) {
                System.err.println("couldn't delete temp file " + tempFile);
            }
        }
    }

    public static double[] readHdf5VariableSolution(File zipfile, String fileName, String varName) throws Exception{

        File tempFile = null;
        try{
            tempFile = createTempHdf5File(zipfile, fileName);
            try (HdfFile solFile = new HdfFile(tempFile.toPath())) {
                if (varName != null)
                {
                    Node solObj = findByPath(solFile, getVarSolutionPath(varName));
                    if (solObj instanceof Dataset)
                    {
                        return (double[]) ((Dataset) solObj).getData();
                    }
                }
            }
        } finally {
            if (tempFile != null && !tempFile.delete()) {
                System.err.println("couldn't delete temp file " + tempFile.getAbsolutePath());
            }
        }
        return null;
    }

    public static double[] readChomboExtrapolatedValues(String varName, File pdeFile, File zipFile) throws IOException {
        double[] data = null;
        if (zipFile != null && DataSet.isChombo(zipFile)) {
            File tempFile = null;
            try{
                tempFile = createTempHdf5File(zipFile, pdeFile.getName());
                try (HdfFile solFile = new HdfFile(tempFile.toPath())) {
                    data = readChomboExtrapolatedValues(varName, solFile);
                }
            } catch(Exception e) {
                throw new IOException(e.getMessage(), e);
            } finally {
                if (tempFile != null && !tempFile.delete()) {
                    System.err.println("couldn't delete temp file " + tempFile.getAbsolutePath());
                }
            }
        }
        return data;
    }

    private static double[] readChomboExtrapolatedValues(String varName, HdfFile solFile) throws Exception {
        double data[] = null;
        if (varName != null)
        {
            Node solObj = findByPath(solFile, getVolVarExtrapolatedValuesPath(varName));
            if (solObj == null)
            {
                throw new IOException("Extrapolated values for variable '" + varName + "' does not exist in the results.");
            }
            if (solObj instanceof Dataset)
            {
                return (double[]) ((Dataset) solObj).getData();
            }
        }
        return data;
    }

    /** walks a {@code /group/dataset} path, returning null rather than throwing when it is absent */
    private static Node findByPath(HdfFile file, String path) {
        Node node = file;
        for (String segment : path.split(HDF5_GROUP_DIRECTORY_SEPARATOR)) {
            if (segment.isEmpty()) {
                continue;
            }
            if (!(node instanceof Group)) {
                return null;
            }
            node = ((Group) node).getChildren().get(segment);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    private static int intValue(Object value) {
        if (value instanceof int[]) {
            return ((int[]) value)[0];
        }
        if (value instanceof long[]) {
            return (int) ((long[]) value)[0];
        }
        return ((Number) value).intValue();
    }

    private static String stringValue(Object value) {
        if (value instanceof String[]) {
            return ((String[]) value)[0];
        }
        return String.valueOf(value);
    }

    private static File createTempHdf5File(File zipFile, String fileName) throws IOException
    {
        ZipFile zipZipFile = null;
        try
        {
            zipZipFile = DataSet.openZipFile(zipFile);
            return createTempHdf5File(zipZipFile, fileName);
        }
        finally
        {
            try
            {
                if (zipZipFile != null)
                {
                    zipZipFile.close();
                }
            }
            catch (Exception ex)
            {
                // ignore
            }
        }
    }

    private static File createTempHdf5File(ZipFile zipFile, String fileName) throws IOException
    {
        InputStream is = null;
        try
        {
            ZipEntry dataEntry = zipFile.getEntry(fileName);
            is = zipFile.getInputStream((ZipArchiveEntry) dataEntry);
            return createTempHdf5File(is);
        }
        finally
        {
            try
            {
                if (is != null)
                {
                    is.close();
                }
            }
            catch (Exception ex)
            {
                // ignore
            }
        }
    }

    private static File createTempHdf5File(InputStream is) throws IOException
    {
        OutputStream out = null;
        try{
            File tempFile = File.createTempFile("temp", "hdf5");
            out=new FileOutputStream(tempFile);
            byte buf[] = new byte[1024];
            int len;
            while((len=is.read(buf))>0) {
                out.write(buf,0,len);
            }
            return tempFile;
        }
        finally
        {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception ex) {
                // ignore
            }
        }
    }

}
