package org.vcell.vis.io;

import cbit.vcell.math.Variable;
import cbit.vcell.solvers.CartesianMeshChombo;
import cbit.vcell.solvers.CartesianMeshChombo.FeaturePhaseVol;
import io.jhdf.HdfFile;
import io.jhdf.api.Attribute;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;
import io.jhdf.api.Node;
import org.vcell.vis.chombo.*;
import org.vcell.vis.chombo.ChomboBoundaries.BorderCellInfo;
import org.vcell.vis.chombo.ChomboBoundaries.MeshMetrics;
import org.vcell.vis.chombo.ChomboBoundaries.SurfaceTriangle;
import org.vcell.vis.core.Face;
import org.vcell.vis.core.Vect3D;
import org.vcell.vis.io.ChomboFiles.ChomboFileEntry;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class ChomboFileReader {
    //private static final String METRICS_DATASET = "metrics";
    private static final String MESH_ATTR_DIMENSION = "dimension";
    private static final String MESH_ATTR_ORIGIN = "origin";
    private static final String MESH_ATTR_EXTENT = "extent";

    public static File createTempHdf5File(InputStream is) throws IOException
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

    public static List<DataBlock> readHdf5SolutionMetaData(InputStream is) throws Exception {
        File tempFile = null;

        try {
            tempFile = createTempHdf5File(is);
            try (HdfFile hdfFile = new HdfFile(tempFile)) {
                List<DataBlock> dataBlockList = new ArrayList<>();
                Group solGroup = hdfFile.getParent();
                Map<String, Node> childNodes = solGroup.getChildren();
                for (Node node : childNodes.values()) {
                    if (!(node instanceof Dataset dataset)) {
                        continue;
                    }
                    String dsname = dataset.getName();
                    int vt = -1;
                    String domain = null;
                    Map<String, Attribute> solAttrList = dataset.getAttributes();
                    for (Attribute attr : solAttrList.values()) {
                        String attrName = attr.getName();
                        if (attrName.equals("variable type")) {
                            int[] data = (int[]) attr.getData();
                            vt = data[0];
                        } else if (attrName.equals("domain")) {
                            String[] data = (String[]) attr.getData();
                            domain = data[0];
                        }
                    }
                    int[] dims = dataset.getDimensions();
                    String varName = domain == null ? dsname : domain + Variable.COMBINED_IDENTIFIER_SEPARATOR + dsname;
                    dataBlockList.add(DataBlock.createDataBlock(varName, vt, dims[0], 0));
                }
                return dataBlockList;
            }
        } finally {
            try {
                if (tempFile != null) {
                    if (!tempFile.delete()) {
                        System.err.println("couldn't delete temp file " + tempFile);
                    }
                }
            } catch(Exception e) {
                // ignore
            }
        }
    }


    /**
	 * Z = boolean
	 [B = byte
	 [S = short
	 [I = int
	 [J = long
	 [F = float
	 [D = double
	 [C = char
	 [L = any non-primitives(Object)
	 * @author schaff
	 *
	 */
	static abstract class DataColumn {
		private String colName;
		public DataColumn(String name){
			this.colName = name;
		}
		public abstract int getNumRows();
		public abstract double getValue(int index);
	}

	static class IntColumn extends DataColumn {
		int[] data;
		public IntColumn(String name, int[] data){
			super(name);
			this.data = data;
		}
		@Override
		public int getNumRows(){
			return data.length;
		}
		@Override
		public double getValue(int index){
			return data[index];
		}
	}

	static class LongColumn extends DataColumn {
		long[] data;
		public LongColumn(String name, long[] data){
			super(name);
			this.data = data;
		}
		@Override
		public int getNumRows(){
			return data.length;
		}
		@Override
		public double getValue(int index){
			return data[index];
		}
	}

	static class DoubleColumn extends DataColumn {
		double[] data;
		public DoubleColumn(String name, double[] data){
			super(name);
			this.data = data;
		}
		@Override
		public int getNumRows(){
			return data.length;
		}
		@Override
		public double getValue(int index){
			return data[index];
		}
	}

    private static ChomboMeshData readMesh(String meshFileName, String vol0FileName) throws Exception {

        ChomboMesh chomboMesh = new ChomboMesh();
        File meshFile = new File(meshFileName);
        try (HdfFile hdfFile = new HdfFile(meshFile)) {
            if (!(hdfFile.getParent().getChild("mesh") instanceof Group meshGroup)) {
                throw new RuntimeException("mesh group not found");
            }
            chomboMesh.setDimension(getIntAttribute(meshGroup, MESH_ATTR_DIMENSION));
            chomboMesh.setExtent(getVect3DAttribute(meshGroup, MESH_ATTR_EXTENT, 1.0));
            chomboMesh.setOrigin(getVect3DAttribute(meshGroup, MESH_ATTR_ORIGIN, 0.0));

			// it's very wasteful here, but what can I do?
			CartesianMeshChombo cartesianMeshChombo = CartesianMeshChombo.readMeshFile(new File(meshFileName));
			for (FeaturePhaseVol fpv : cartesianMeshChombo.getFeaturePhaseVols())
			{
				chomboMesh.addFeaturePhase(fpv.feature, fpv.iphase);
			}
			
			//DataColumn[] metricsColumns = getDataTable(meshGroup,METRICS_DATASET);
			if (chomboMesh.getDimension()==2){
				DataColumn[] segmentColumns = getDataTable(meshGroup,"segments");
				DataColumn[] verticesColumns = getDataTable(meshGroup,"vertices");
				ChomboBoundaries boundaries = chomboMesh.getBoundaries();
				int numVertices = verticesColumns[0].getNumRows();
				int numSegments = segmentColumns[0].getNumRows();
				for (int i=0;i<numVertices;i++){
					double x = verticesColumns[0].getValue(i);
					double y = verticesColumns[1].getValue(i);
					double z = 0.0;
					boundaries.addPoint(new ChomboBoundaries.Point(x,y,z));
				}
				for (int i=0;i<numSegments;i++){
					int v1 = (int)segmentColumns[1].getValue(i);
					int v2 = (int)segmentColumns[2].getValue(i);
                    int chomboIndex = i; // THIS COULD BE WRONG - is the chomboIndex one-to-one with the line segments? ... if not should be in the HDF5 file.
                    boundaries.addSegment(new ChomboBoundaries.Segment(chomboIndex, v1, v2));
				}
			}else if (chomboMesh.getDimension()==3){
				DataColumn[] surfaceTriangleColumns = getDataTable(meshGroup,"surface triangles");
				ChomboBoundaries boundaries = chomboMesh.getBoundaries();
				int numTriangles = surfaceTriangleColumns[0].getNumRows();
				for (int row=0;row<numTriangles;row++){
					int index = (int)surfaceTriangleColumns[0].getValue(row);
					int faceNumber = (int)surfaceTriangleColumns[1].getValue(row);
					int neighborIndex = (int)surfaceTriangleColumns[2].getValue(row); // not used currently
					double x0 = surfaceTriangleColumns[3].getValue(row);
                    double y0 = surfaceTriangleColumns[4].getValue(row);
					double z0 = surfaceTriangleColumns[5].getValue(row);
					int p0_index = boundaries.getOrCreatePoint(x0,y0,z0);
					double x1 = surfaceTriangleColumns[6].getValue(row);
					double y1 = surfaceTriangleColumns[7].getValue(row);
					double z1 = surfaceTriangleColumns[8].getValue(row);
					int p1_index = boundaries.getOrCreatePoint(x1,y1,z1);
                    double x2 = surfaceTriangleColumns[9].getValue(row);
                    double y2 = surfaceTriangleColumns[10].getValue(row);
					double z2 = surfaceTriangleColumns[11].getValue(row);
					int p2_index = boundaries.getOrCreatePoint(x2,y2,z2);
					Face face = Face.fromInteger(faceNumber);
					SurfaceTriangle surfaceTriangle = new SurfaceTriangle(index, face, p0_index,p1_index,p2_index);
                    boundaries.addSurfaceTriangle(surfaceTriangle);
				}
				DataColumn[] metricsColumns = getDataTable(meshGroup,"membrane elements");
				MeshMetrics meshMetrics = boundaries.getMeshMetrics();
				int numMeshMetrics = metricsColumns[0].getNumRows();
				for (int row=0;row<numMeshMetrics;row++){
					int index = (int)metricsColumns[0].getValue(row);
					int level = (int)metricsColumns[1].getValue(row);
					int i = (int)metricsColumns[2].getValue(row); // not used currently
					int j = (int)metricsColumns[3].getValue(row); // not used currently
					int k = (int)metricsColumns[4].getValue(row); // not used currently
					double x = metricsColumns[5].getValue(row);
					double y = metricsColumns[6].getValue(row);
					double z = metricsColumns[7].getValue(row);
					Vect3D center = new Vect3D(x,y,z);
					double normalX = metricsColumns[8].getValue(row);
					double normalY = metricsColumns[9].getValue(row);
					double normalZ = metricsColumns[10].getValue(row);
					Vect3D normal = new Vect3D(normalX,normalY,normalZ);
					double volumeFraction = metricsColumns[11].getValue(row);
					double areaFraction = metricsColumns[12].getValue(row);
					int membraneId = (int)metricsColumns[13].getValue(row);
					int cornerPhaseMask = (int)metricsColumns[14].getValue(row);
					BorderCellInfo borderCellInfo = new BorderCellInfo(index,level,i,j,k,center,normal,volumeFraction,areaFraction,membraneId,cornerPhaseMask);
					meshMetrics.addBorderCellInfo(borderCellInfo);
				}
			}else{
				throw new Exception("failed to read chombo file, unexpected mesh dimension "+chomboMesh.getDimension());
            }
        }
        File vol0File = new File(vol0FileName);
        try (HdfFile hdfFile = new HdfFile(vol0File)) {
            Group rootGroup = hdfFile.getParent();
            if (!(hdfFile.getParent().getChild("level_0") instanceof Group level0Group)) {
                throw new RuntimeException("level_0 group not found");
            }
            double time = getDoubleAttribute(level0Group, "time");
            ChomboMeshData chomboMeshData = new ChomboMeshData(chomboMesh, time);

            int numComponents = getIntAttribute(rootGroup, "num_components");
            int numLevels = getIntAttribute(rootGroup, "num_levels");
			int fractionComponentIndex = -1;
			for (int i=0;i<numComponents;i++){
				String compName = getStringAttribute(rootGroup, "component_"+i);
				chomboMeshData.addComponentName(compName);
				if (compName.equals("fraction-0")){
					fractionComponentIndex = i;
					chomboMeshData.setFraction0ComponentIndex(fractionComponentIndex);
                }
            }
            for (int i=0;i<numLevels;i++){
                Group levelGroup = (Group) rootGroup.getChild("level_"+i);
                int refinement = 2;
                if (i==0){
                    refinement = 1;
                }
                ChomboLevel chomboLevel = new ChomboLevel(chomboMesh, i, refinement);
                DataColumn[] boxColumns = getDataTable(levelGroup, "boxes");
                int[] lo_i, lo_j, lo_k, hi_i, hi_j, hi_k;
                if (chomboMesh.getDimension()==2){
                    lo_i = ((IntColumn)boxColumns[0]).data;
                    lo_j = ((IntColumn)boxColumns[1]).data;
                    hi_i = ((IntColumn)boxColumns[2]).data;
                    hi_j = ((IntColumn)boxColumns[3]).data;
                    lo_k = new int[boxColumns[0].getNumRows()];
                    hi_k = new int[boxColumns[0].getNumRows()];
                }else{
                    lo_i = ((IntColumn)boxColumns[0]).data;
                    lo_j = ((IntColumn)boxColumns[1]).data;
                    lo_k = ((IntColumn)boxColumns[2]).data;
                    hi_i = ((IntColumn)boxColumns[3]).data;
                    hi_j = ((IntColumn)boxColumns[4]).data;
                    hi_k = ((IntColumn)boxColumns[5]).data;
                }
                for (int b=0; b<boxColumns[0].getNumRows(); b++){
                    ChomboBox chomboBox = new ChomboBox(chomboLevel,lo_i[b],hi_i[b],lo_j[b],hi_j[b],lo_k[b],hi_k[b],chomboMesh.getDimension());
                    chomboLevel.addBox(chomboBox);
                }
                chomboMesh.addLevel(chomboLevel);
                //
                // read the variables
                //
                DataColumn[] data = getDataTable(levelGroup,"data:datatype=0");
                DataColumn[] offsets = getDataTable(levelGroup,"data:offsets=0");
                ChomboLevelData chomboLevelData = new ChomboLevelData(i,fractionComponentIndex,((DoubleColumn)data[0]).data,((LongColumn)offsets[0]).data);
                chomboMeshData.addLevelData(chomboLevelData);
            }

            readMembraneVarData(chomboMeshData, rootGroup);

            return chomboMeshData;
        }
    }
	
	public static ChomboDataset readDataset(ChomboFiles chomboFiles, int timeIndex) throws Exception{
		String meshFilename = chomboFiles.getMeshFile().getPath();
        ChomboDataset chomboDataset = new ChomboDataset();
		int domainOrdinal = 0;
		for (ChomboFileEntry cfe : chomboFiles.getEntries(timeIndex))
		{
			File domainFile = cfe.getFile();
			ChomboMeshData chomboMeshData = readMesh(meshFilename, domainFile.getPath());
			ChomboMesh chomboMesh = chomboMeshData.getMesh();
			ChomboDataset.ChomboCombinedVolumeMembraneDomain chomboCombinedVolumeMembraneDomain = new ChomboDataset.ChomboCombinedVolumeMembraneDomain(cfe,chomboMesh,chomboMeshData,domainOrdinal);
			chomboDataset.addDomain(chomboCombinedVolumeMembraneDomain);
			domainOrdinal++;
        }
        return chomboDataset;
    }

    //
    // Membrane data are stored as a UCHC (or vcell) extension to the normal Chombo Data.
    // ChomboMembraneVarData was formally called VCellSolution by Fei.
    //
    private static void readMembraneVarData(ChomboMeshData chomboMeshData, Group rootGroup) {
        // I added solution and extrapolated_volumes group to hold all the solutions from vcell
        String[] groups = new String[]{"solution"/*, "extrapolated_volumes"*/};
        for (String group : groups) {
            try {
                Group vcellGroup = (Group) rootGroup.getChild(group);
                if (vcellGroup != null) {
                    Map<String, Node> children = vcellGroup.getChildren();
                    for (Node c : children.values()) {
                        if (c instanceof Dataset dataset) {
                            String name = dataset.getName();
                            Map<String, Attribute> solAttrList = dataset.getAttributes();
                            String domain = null;
                            for (Attribute attr : solAttrList.values()) {
                                String attrName = attr.getName();
                                if (attrName.equals("domain")) {
                                    domain = (String) attr.getData();
                                    break;
                                }
                            }
                            ChomboMembraneVarData vcellSolution = new ChomboMembraneVarData(name, domain, (double[]) dataset.getData());
                            chomboMeshData.addMembraneVarData(vcellSolution);
                        }
                    }
				}
			}
			catch (Exception ex)
			{
				// it is ok if there is no vcell group
			}
        }
    }

    private static Attribute getAttribute(Group group, String name) throws Exception {
        Map<String, Attribute> attributes = group.getAttributes();
        for (Attribute attr : attributes.values()) {
            if (attr.getName().equals(name)) {
                return attr;
            }
        }
        throw new RuntimeException("failed to find attribute " + name);
    }

    private static double getDoubleAttribute(Group group, String name) throws Exception {
        Attribute attr = getAttribute(group, name);
        return (Double) attr.getData();
    }

    private static float getFloatAttribute(Group group, String name) throws Exception {
        Attribute attr = getAttribute(group, name);
        return (Float) attr.getData();
    }

    private static int getIntAttribute(Group group, String name) throws Exception {
        Attribute attr = getAttribute(group, name);
        return ((Integer) attr.getData());
    }

    private static String getStringAttribute(Group group, String name) throws Exception {
        Attribute attr = getAttribute(group, name);
        return (String) attr.getData();
    }

    private static Vect3D getVect3DAttribute(Group group, String name, double defaultZ) throws Exception {
        String str = getStringAttribute(group, name);
        return parseAttrString(str, defaultZ);
    }

	private static DataColumn[] getDataTable(Group group, String name) throws Exception{
        Map<String, Node> childNodes = group.getChildren();
        for (Node member : childNodes.values()) {
            if (member.getName().equals(name) && member instanceof Dataset dataset) {
                if (dataset.isCompound()) {
                    Map<String, Object> compoundData = (Map<String, Object>) dataset.getData();
                    ArrayList<DataColumn> dataColumns = new ArrayList<DataColumn>();
                    for (String key : compoundData.keySet()) {
                        Object column = compoundData.get(key);
                        if (column instanceof int[]) {
                            dataColumns.add(new IntColumn(key, (int[]) column));
                        } else if (column instanceof double[]) {
                            dataColumns.add(new DoubleColumn(key, (double[]) column));
                        } else {
                            throw new RuntimeException("unexpected type '" + column.getClass().getName() + "' for group member '" + name + "'");
                        }
                    }
                    return dataColumns.toArray(new DataColumn[0]);
                } else {
                    Object column = dataset.getData();
                    if (column instanceof int[]) {
                        return new DataColumn[]{new IntColumn("col", (int[]) column)};
                    } else if (column instanceof double[]) {
                        return new DataColumn[]{new DoubleColumn("col", (double[]) column)};
                    } else if (column instanceof long[]) {
                        return new DataColumn[]{new LongColumn("col", (long[]) column)};
                    } else {
                        throw new RuntimeException("unexpected type '" + column.getClass().getName() + "' for group member '" + name + "'");
                    }
                }
            }
        }
		throw new RuntimeException("group member '"+name+"' not found");
    }

	private static Vect3D parseAttrString(String attrString, double defaultZ){
		StringTokenizer st = new StringTokenizer(attrString, "{,} ");
		List<Double> valueList = new ArrayList<Double>();
		while (st.hasMoreTokens())
		{
			String token = st.nextToken();
			valueList.add(Double.parseDouble(token));
		}
		if (valueList.size()==2){
			return new Vect3D(valueList.get(0),valueList.get(1),defaultZ);
		}else if (valueList.size()==3){
			return new Vect3D(valueList.get(0),valueList.get(1),valueList.get(2));
		}else{
			throw new RuntimeException("cannot parse, unexpected array size "+valueList.size());
		}
	}
}
