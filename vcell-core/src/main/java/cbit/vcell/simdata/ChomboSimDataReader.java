package cbit.vcell.simdata;

import cbit.vcell.math.InsideVariable;
import cbit.vcell.math.OutsideVariable;
import cbit.vcell.math.Variable;
import io.jhdf.HdfFile;
import io.jhdf.api.Attribute;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.vcell.util.DataAccessException;

import java.io.*;
import java.util.Map;
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
    public static String getVarSolutionPath(String varName) {
        return HDF5_GROUP_SOLUTION + HDF5_GROUP_DIRECTORY_SEPARATOR + Variable.getNameFromCombinedIdentifier(varName);
    }

    /**
     * Creates a relative path to the extrapolated values of a given variable name.
     *
     * @param varName name of the variable to path to
     * @return the relative path
     */
    public static String getVolVarExtrapolatedValuesPath(String varName) {
        return HDF5_GROUP_EXTRAPOLATED_VOLUMES + HDF5_GROUP_DIRECTORY_SEPARATOR + "__" + Variable.getNameFromCombinedIdentifier(varName) + "_extrapolated__";
    }


    public static void getNextDataAtCurrentTimeChombo(double[][] returnValues, ZipFile currentZipFile, String[] varNames, int[][] varIndexes, String[] simDataFileNames, int masterTimeIndex) throws Exception {
        File tempFile = null;
        try {
            tempFile = createTempHdf5File(currentZipFile, simDataFileNames[masterTimeIndex]);
            try (HdfFile hdfFile = new HdfFile(tempFile)) {
                for (int k = 0; k < varNames.length; ++k) {
                    try {
                        boolean bExtrapolatedValue = false;
                        String varName = varNames[k];
                        if (varName.endsWith(InsideVariable.INSIDE_VARIABLE_SUFFIX)) {
                            bExtrapolatedValue = true;
                            varName = varName.substring(0, varName.lastIndexOf(InsideVariable.INSIDE_VARIABLE_SUFFIX));
                        } else if (varName.endsWith(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX)) {
                            bExtrapolatedValue = true;
                            varName = varName.substring(0, varName.lastIndexOf(OutsideVariable.OUTSIDE_VARIABLE_SUFFIX));
                        }
                        double[] sol = null;
                        if (bExtrapolatedValue) {
                            sol = readChomboExtrapolatedValues(varName, hdfFile);
                        } else {
                            String varPath = getVarSolutionPath(varNames[k]);
                            Dataset dataset = hdfFile.getDatasetByPath(varPath);
                            if (dataset != null) {
                                sol = (double[]) dataset.getData();
                            }
                        }
                        if (sol != null) {
                            for (int l = 0; l < varIndexes[k].length; ++l) {
                                int idx = varIndexes[k][l];
                                double val = sol[idx];
                                returnValues[k][l] = val;
                            }
                        }
                    } catch (Exception e) {
                        throw new DataAccessException(e.getMessage(), e);
                    }
                }
            }
        } finally {
            try {
                if (tempFile != null) {
                    if (!tempFile.delete()) {
                        System.err.println("couldn't delete temp file " + tempFile.getAbsolutePath());
                    }
                }
            } catch(Exception e) {
                // ignore
            }
        }
    }

    public static void readHdf5SolutionMetaData(InputStream is, Vector<DataBlock> dataBlockList) throws Exception {
        File tempFile = null;
        try {
            tempFile = createTempHdf5File(is);

            try (HdfFile hdfFile = new HdfFile(tempFile)) {
                Map<String, Node> solGroups = hdfFile.getChildren();
                for (Node childNode : solGroups.values()) {
                    if (childNode instanceof Group solGroup && solGroup.getName().equals("solution")) {
                        Map<String, Node> memberList = solGroup.getChildren();
                        for (Node member : memberList.values()) {
                            if (!(member instanceof Dataset dataset)) {
                                continue;
                            }
                            String dsname = dataset.getName();
                            int vt = -1;
                            String domain = null;
                            Map<String, Attribute> solAttrList = dataset.getAttributes();
                            for (Attribute attr : solAttrList.values()) {
                                String attrName = attr.getName();
                                if (attrName.equals("variable type")) {
                                    Object obj = attr.getData();
                                    vt = ((int[]) obj)[0];
                                } else if (attrName.equals("domain")) {
                                    Object obj = attr.getData();
                                    domain = ((String[]) obj)[0];
                                }
                            }
                            int[] dims = dataset.getDimensions();
                            String varName = domain == null ? dsname : domain + Variable.COMBINED_IDENTIFIER_SEPARATOR + dsname;
                            dataBlockList.addElement(cbit.vcell.simdata.DataBlock.createDataBlock(varName, vt, dims[0], 0));
                        }
                        break;
                    }
                }
            }
        } finally {
            try {
                if (tempFile != null) {
                    if (!tempFile.delete()) {
                        System.err.println("couldn't delete temp file " + tempFile);
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public static double[] readHdf5VariableSolution(File zipfile, String fileName, String varName) throws Exception {

        File tempFile = null;
        try {
            tempFile = createTempHdf5File(zipfile, fileName);
            try (HdfFile hdfFile = new HdfFile(tempFile)) {
                if (varName != null) {
                    String varPath = getVarSolutionPath(varName);
                    Dataset dataset = hdfFile.getDatasetByPath(varPath);
                    if (dataset != null) {
                        return (double[]) dataset.getData();
                    }
                }
            }
        } finally {
            try {
                if (tempFile != null) {
                    if (!tempFile.delete()) {
                        System.err.println("couldn't delete temp file " + tempFile.getAbsolutePath());
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }

    public static double[] readChomboExtrapolatedValues(String varName, File pdeFile, File zipFile) throws IOException {
        double[] data = null;
        if (zipFile != null && DataSet.isChombo(zipFile)) {
            File tempFile = null;
            try {
                tempFile = createTempHdf5File(zipFile, pdeFile.getName());
                try (HdfFile hdfFile = new HdfFile(tempFile)) {
                    data = readChomboExtrapolatedValues(varName, hdfFile);
                }
            } finally {
                try {
                    if (tempFile != null) {
                        if (!tempFile.delete()) {
                            System.err.println("couldn't delete temp file " + tempFile.getAbsolutePath());
                        }
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        return data;
    }

    private static double[] readChomboExtrapolatedValues(String varName, HdfFile solFile) throws IOException {
        if (varName != null) {
            String varPath = getVolVarExtrapolatedValuesPath(varName);
            Node solObj = solFile.getChild(varPath);
            if (solObj instanceof Dataset dataset) {
                double[] data = (double[]) dataset.getData();
                return data;
            } else {
                throw new IOException("Extrapolated values for variable '" + varName + "' does not exist in the results.");
            }
        }
        return null;
    }

    private static File createTempHdf5File(File zipFile, String fileName) throws IOException {
        ZipFile zipZipFile = null;
        try {
            zipZipFile = DataSet.openZipFile(zipFile);
            return createTempHdf5File(zipZipFile, fileName);
        } finally {
            try {
                if (zipZipFile != null) {
                    zipZipFile.close();
                }
            } catch (Exception ex) {
                // ignore
            }
        }
    }

    private static File createTempHdf5File(ZipFile zipFile, String fileName) throws IOException {
        InputStream is = null;
        try {
            ZipEntry dataEntry = zipFile.getEntry(fileName);
            is = zipFile.getInputStream((ZipArchiveEntry) dataEntry);
            return createTempHdf5File(is);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception ex) {
                // ignore
            }
        }
    }

    private static File createTempHdf5File(InputStream is) throws IOException {
        OutputStream out = null;
        try {
            File tempFile = File.createTempFile("temp", "hdf5");
            out = new FileOutputStream(tempFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            return tempFile;
        } finally {
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
