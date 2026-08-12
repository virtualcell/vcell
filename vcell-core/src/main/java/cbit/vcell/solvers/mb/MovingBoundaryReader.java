package cbit.vcell.solvers.mb;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.Arrays;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vcell.util.CastingUtils.CastInfo;
import org.vcell.util.CastingUtils;
import org.vcell.util.ProgrammingException;
import org.vcell.util.VCAssert;

import io.jhdf.HdfFile;
import io.jhdf.api.Dataset;
import io.jhdf.api.Group;

/**
 * read results from MovingBoundary hdf5 file.
 * In general, returned objects are not cached by this so
 * most public calls read file and create new objects
 *
 * @author GWeatherby
 */
public class MovingBoundaryReader implements MovingBoundaryTypes {
    private final static Logger lg = LogManager.getLogger(MovingBoundaryReader.class);

    private static final String ELEM = "elements";

    private final String filename;
    private Group root;
    private MeshInfo meshInfo;
    private int lastTimeIndex_;

    private String[] fakeVarNames;
    private TimeInfo timeInfo;
    private PlaneNodes pnodes;
    private final PointIndexTreeAndList pointIndex;
    private HdfFile hdfFile;

    //	private static final Logger lg = LogManager.getLogger(MovingBoundaryReader.class);
    private static final String HDF_SPLIT_CHARS = "{}, ";


    public MovingBoundaryReader(String filename){
        this.filename = filename;
        meshInfo = null;
        timeInfo = null;
        pointIndex = new PointIndexTreeAndList();
        try {
            // held open for the lifetime of this reader: every accessor below reads from it
            hdfFile = new HdfFile(new File(filename).toPath());
            root = hdfFile;
            lastTimeIndex_ = singleInt("lastTimeIndex");
        } catch(Exception e){
            throw new MovingBoundaryResultException("exception reading moving boundary result file " + filename, e);
        }
    }

    public PointIndex getPointIndex(){
        return pointIndex;
    }

    public MeshInfo getMeshInfo(){
        if(meshInfo == null){
            double p = singleDouble("precision");
            double sf = singleDouble("scaleFactor");
            DimensionInfo xdim = getDimInfo('x');
            DimensionInfo ydim = getDimInfo('y');
            int d = lastTimeIndex();
            meshInfo = new MeshInfo(p, sf, xdim, ydim, d);
        }
        return meshInfo;
    }

    public int lastTimeIndex(){
        return lastTimeIndex_;
    }

    void testquery(){
        try {
//		MovingBoundardyVH5TypedPath<H5ScalarDS> path = new MovingBoundardyVH5TypedPath<>(root, H5ScalarDS.class,"boundaries");
//		H5ScalarDS hsd = path.get();
//		hsd.init( );
//		int[] si = hsd.getSelectedIndex();
//		long[] start = hsd.getStartDims();
//		long[] stride = hsd.getStride( );
////		long[] dims = hsd.getDims();
//		long[] sdims = hsd.getSelectedDims();
//		sdims[0] = 1;
//		Object o2 = hsd.read();
//		System.out.println(o2);
//		MovingBoundardyVH5TypedPath<String[]> dpath = new MovingBoundardyVH5TypedPath<>(root, String[].class,"boundaries");
//		String[] d = dpath.get();
//		System.out.println(d);


//		MovingBoundardyVH5Path path = new MovingBoundardyVH5Path(root,"generationTimes");
//		Object o = path.getData();
//		H5ScalarDS hsd = (H5ScalarDS) o;
//		Object o2 = hsd.read();
//		System.out.println(o2);
//		MovingBoundardyVH5TypedPath<H5CompoundDS> dpath = new MovingBoundardyVH5TypedPath<H5CompoundDS>(root, H5CompoundDS.class,"elements");
//		H5CompoundDS cds = dpath.get();
//		cds.init();
//		selectPlane(cds,50,50,0);
//		cds.setMemberSelection(false);
//		cds.selectMember(2);
//		Datatype[] dts = cds.getSelectedMemberTypes();
//		int id = dts[0].open();
//		o = cds.getData( );
//
//		//MovingBoundardyVH5Path path2 = new MovingBoundardyVH5Path(root,"elements","volumePointsX");
//	//	o = path2.getData();
//		System.out.println(o);
//
//		double[] da = getDoubleArray("generationTimes");
//		da = getDoubleArray("elements","volumePointsX");
//		System.out.println(da);
        } catch(Exception e){
            lg.error(e.getMessage(), e);
        }

    }

    /**
     * A single number at a path, whatever integer or floating width the file used — the writer's
     * choice of int32 vs int64 is not something the callers care about, and it differs between
     * files. Attributes and datasets both arrive as a one-element array.
     */
    private Number singleNumber(String... names){
        MovingBoundaryVH5Path path = new MovingBoundaryVH5Path(root, names);
        Object found = path.getData();
        if(found instanceof Dataset){
            found = ((Dataset) found).getData();
        }
        if(found == null){
            throw new MovingBoundaryResultException(MovingBoundaryVH5Path.concat(names) + " not found");
        }
        if(found.getClass().isArray()){
            if(Array.getLength(found) != 1){
                throw new MovingBoundaryResultException(
                        MovingBoundaryVH5Path.concat(names) + " is not single element array");
            }
            found = Array.get(found, 0);
        }
        if(!(found instanceof Number)){
            throw new MovingBoundaryResultException(
                    MovingBoundaryVH5Path.concat(names) + " is " + found.getClass().getSimpleName() + ", not a number");
        }
        return (Number) found;
    }

    /**
     * An array of numbers at a path. The solver writes these as one variable-length row — a
     * dataset of one element whose element is itself the array — so a single row is unwrapped.
     */
    private double[] getDoubleArray(String... names){
        MovingBoundaryVH5Path path = new MovingBoundaryVH5Path(root, names);
        Object found = path.getData();
        if(found instanceof Dataset){
            found = ((Dataset) found).getData();
        }
        if(found == null){
            throw new MovingBoundaryResultException(MovingBoundaryVH5Path.concat(names) + " not found");
        }
        if(found instanceof double[][]){
            double[][] rows = (double[][]) found;
            if(rows.length != 1){
                throw new MovingBoundaryResultException(
                        MovingBoundaryVH5Path.concat(names) + " has " + rows.length + " rows, expected one");
            }
            return rows[0];
        }
        if(found instanceof double[]){
            return (double[]) found;
        }
        throw new MovingBoundaryResultException(MovingBoundaryVH5Path.concat(names)
                + " is " + found.getClass().getSimpleName() + ", not an array of double");
    }

    private double singleDouble(String... names){
        return singleNumber(names).doubleValue();
    }

    private long singleLong(String... names){
        return singleNumber(names).longValue();
    }

    private int singleInt(String... names){
        return singleNumber(names).intValue();
    }

    private DimensionInfo getDimInfo(char dim){
        char upper = Character.toUpperCase(dim);
        char lower = Character.toLowerCase(dim);
        double st = singleDouble(ELEM, "start" + upper);
        double end = singleDouble(ELEM, "end" + upper);
        double delta = singleDouble(ELEM, "h" + lower);
        String cstr = "num" + upper;
        long c = singleLong(ELEM, cstr);
        String vstr = "mesh" + upper + "values";
        double[] values = getDoubleArray(vstr);
        if(values.length != c){
            throw new MovingBoundaryResultException(cstr + " value " + c + " does not match " + vstr + " array length");
        }
        return new DimensionInfo(st, end, delta, values);
    }

    public TimeInfo getTimeInfo(){
        if(timeInfo == null){
            double rts = singleDouble("requestedTimeStep");
            double et = singleDouble("endTime");
            double rt = singleDouble("runTime");
            double[] gt = getDoubleArray("generationTimes");
            double[] mt = getDoubleArray("moveTimes");
            ArrayList<TimeInfo.TimeStep> tsa = new ArrayList<>();
            {
                double[] steps = getDoubleArray("timeStep");
                double[] times = getDoubleArray("timeStepTimes");
                if(steps.length != times.length){
                    throw new MovingBoundaryResultException("timeStep length " + steps.length + " does not match timesStepTimes length " + times.length);
                }
                for(int i = 0; i < steps.length; i++){
                    TimeInfo.TimeStep ts = new TimeInfo.TimeStep(times[i], steps[i]);
                    tsa.add(ts);
                }
            }

            timeInfo = new TimeInfo(rts, et, rt, gt, mt, tsa);
        }

        return timeInfo;
    }

    private void validateTimeIndex(int t){
        if(t > lastTimeIndex_){
            throw new IndexOutOfBoundsException("time index " + t + " greater than max index " + lastTimeIndex_);
        }
    }

    private PlaneNodes planeNode() throws Exception{
        if(pnodes == null){
            pnodes = new PlaneNodes();
        }
        return pnodes;
    }

    /**
     * @param timeIndex >= 0 and <= {@link #lastTimeIndex()}
     * @return
     */
    public Plane getPlane(int timeIndex){
        VCAssert.assertTrue(timeIndex >= 0, "negative time index");
        validateTimeIndex(timeIndex);
        try {
            MeshInfo mi = getMeshInfo();
            final int numX = mi.xinfo.number();
            final int numY = mi.yinfo.number();
            Element elements[][] = new Element[numX][numY];
            double[] vols;
//			String[] xpoints;
//			String[] ypoints;
            Object[] combined;
            byte[] poz;
            {
                // one time slice of the [time][x][y] compound: jhdf hands back each member shaped
                // [1][numX][numY], which is flattened here in the x-then-y order used below
                Map<String, Object> data = slice(planeNode().elements, "elements",
                        new long[] { timeIndex, 0, 0 }, new int[] { 1, numX, numY });
                vols = (double[]) flatten(member(data, "elements", "volume"), numX * numY, double.class);
                combined = (Object[]) flatten(member(data, "elements", "volumePoints"), numX * numY, Object.class);
                poz = (byte[]) flatten(member(data, "elements", "boundaryPosition"), numX * numY, byte.class);
            }
            double mass[][];
            double conc[][];
            {
                //will need to be a loop later
                mass = new double[1][];
                conc = new double[1][];
                // [time][x][y][species]; only the first species is used today
                int numSpecies = planeNode().species.getDimensions()[3];
                Map<String, Object> data = slice(planeNode().species, "species",
                        new long[] { timeIndex, 0, 0, 0 }, new int[] { 1, numX, numY, numSpecies });
                mass[0] = speciesColumn(member(data, "species", "mass"), numX, numY, 0);
                conc[0] = speciesColumn(member(data, "species", "uNumeric"), numX, numY, 0);
            }

            int i = 0;
            for(int x = 0; x < numX; x++){
                for(int y = 0; y < numY; y++){
//					String xstr = xpoints[i];
//					String ystr = ypoints[i];
//					int[] bnd = buildBoundary(xstr,ystr);
                    int[] bnd = buildBoundary(combined[i]);
//					System.out.println(Arrays.toString(bnd));
                    Element e = new Element(vols[i], poz[i], bnd);
                    for(int sc = 0; sc < mass.length; sc++){
                        Species sp = new Species(mass[sc][i], conc[sc][i]);
                        e.species.add(sp);
                    }

                    elements[x][y] = e;
                    i++;
                }
            }

            PlaneI plane = new PlaneI();
            plane.elements = elements;
            plane.time = getTimeInfo().generationTimes.get(timeIndex);

            return plane;
        } catch(Exception e){
            throw new RuntimeException("Can't read plane for time index " + timeIndex, e);
        }
    }

    @SuppressWarnings("unused")
    private int[] buildBoundary(String xvalues, String yvalues){
        String[] xs = StringUtils.split(xvalues, HDF_SPLIT_CHARS);
        String[] ys = StringUtils.split(yvalues, HDF_SPLIT_CHARS);
        final int length = xs.length;
        VCAssert.assertTrue(length == ys.length, "x and y strings same length");
        int[] rval = new int[length];
        for(int i = 0; i < length; i++){
            double x = 0;
            double y = 0;
            double z = 0;
            try {
                x = Double.parseDouble(xs[i]);
                y = Double.parseDouble(ys[i]);
            } catch(NumberFormatException nfe){
                throw new RuntimeException("Invalid coordinates " + xs[i] + " or " + ys[i] + " reading MovingBoundary element boundary");
            }
            Vect3Didx idx = pointIndex.index(x, y, z);
            rval[i] = idx.getIndex();
        }

        return rval;
    }

    /**
     * The boundary of one cell. The solver writes it as a compound of parallel x and y arrays;
     * the previous binding could only surface that as a formatted string, which then had to be
     * parsed back into numbers — these are the numbers themselves.
     */
    private int[] buildBoundary(Object volumePoints) throws Exception{
        if(volumePoints instanceof String){ // an older file, or a binding that stringified it
            return getPointIndexes((String) volumePoints, 2);
        }
        Map<?, ?> point = CastingUtils.downcast(Map.class, volumePoints);
        if(point == null){
            throw new MovingBoundaryResultException("volumePoints is "
                    + (volumePoints == null ? "null" : volumePoints.getClass().getSimpleName())
                    + ", expected a compound of x and y");
        }
        double[] xs = (double[]) point.get("x");
        double[] ys = (double[]) point.get("y");
        VCAssert.assertTrue(xs.length == ys.length, "x and y arrays same length");
        int[] indexes = new int[xs.length];
        for(int i = 0; i < xs.length; i++){
            indexes[i] = pointIndex.index(xs[i], ys[i], 0).getIndex();
        }
        return indexes;
    }

    /**
     * @param clzz      return type
     * @param v         input
     * @param names     available names
     * @param path      info for exception message
     * @param childName to select
     * @return requested data
     * @throws ProgrammingException if wrong type or childName name not in names
     */
    /** one hyperslab of a compound dataset, as its members keyed by name */
    @SuppressWarnings("unchecked")
    private static Map<String, Object> slice(Dataset ds, String path, long[] offset, int[] dimensions){
        Object data = ds.getData(offset, dimensions);
        Map<String, Object> members = CastingUtils.downcast(Map.class, data);
        if(members == null){
            throw new ProgrammingException(path + " is not a compound dataset, found " + data.getClass().getSimpleName());
        }
        return members;
    }

    private static Object member(Map<String, Object> members, String path, String childName){
        Object column = members.get(childName);
        if(column == null){
            throw new ProgrammingException("No " + childName + " in " + StringUtils.join(members.keySet(), ",")
                    + " children of " + path);
        }
        return column;
    }

    /**
     * A member of a [1][numX][numY] slice, flattened to a single array in x-then-y order — the
     * order the plane assembly below walks.
     */
    private static Object flatten(Object column, int length, Class<?> componentType){
        Object flat = Array.newInstance(componentType, length);
        Object plane = Array.get(column, 0); // drop the leading time dimension
        int i = 0;
        for(int x = 0; x < Array.getLength(plane); x++){
            Object row = Array.get(plane, x);
            for(int y = 0; y < Array.getLength(row); y++){
                Array.set(flat, i++, Array.get(row, y));
            }
        }
        if(i != length){
            throw new ProgrammingException("expected " + length + " values, found " + i);
        }
        return flat;
    }

    /** one species of a [1][numX][numY][numSpecies] slice, flattened in x-then-y order */
    private static double[] speciesColumn(Object column, int numX, int numY, int species){
        double[] flat = new double[numX * numY];
        Object plane = Array.get(column, 0);
        int i = 0;
        for(int x = 0; x < numX; x++){
            Object row = Array.get(plane, x);
            for(int y = 0; y < numY; y++){
                flat[i++] = ((double[]) Array.get(row, y))[species];
            }
        }
        return flat;
    }

    private static <T> T safeCast(Class<T> clzz, Object obj, String path){
        CastInfo<T> ci = CastingUtils.attemptCast(clzz, obj);
        if(ci.isGood()){
            return ci.get();
        }
        throw new ProgrammingException(ci.castMessage() + " failed for " + path);
    }

    /**
     * HDF data nodes which support planes
     */
    private class PlaneNodes {
        final Dataset elements;
        final Dataset species;

        PlaneNodes() throws Exception{
            elements = new MovingBoundaryVH5TypedPath<Dataset>(root, Dataset.class, "elements").get();
            species = new MovingBoundaryVH5TypedPath<Dataset>(root, Dataset.class, "species").get();
        }

    }

    private static class PlaneI implements Plane {
        Element elements[][];
        double time;

        @Override
        public double getTime(){
            return time;
        }

        @Override
        public int getSizeX(){
            return elements.length;
        }

        @Override
        public int getSizeY(){
            return elements[0].length;
        }

        @Override
        public Element get(int x, int y){
            return elements[x][y];
        }
    }


    public int[] getBoundaryIndexes(int timeIndex){
        try {

            VCAssert.assertTrue(timeIndex >= 0, "negative time index");
            validateTimeIndex(timeIndex);
            MovingBoundaryVH5TypedPath<Dataset> path = new MovingBoundaryVH5TypedPath<>(root, Dataset.class, "boundaries");
            Dataset hsd = path.get();
            String[] data = (String[]) hsd.getData(new long[] { timeIndex }, new int[] { 1 });
            String blob = data[0];
            return getPointIndexes(blob, 0);
        } catch(Exception e){
            throw new RuntimeException("Exception building outer boundary indexes", e);
        }
    }

    /**
     * get indexes for points in style of { x, y } { x, y } ...
     *
     * @param blob      non null
     * @param startfrom where to scan from
     * @return list of index from from {@link #getPointIndex()}
     * @throws Exception
     */
    private int[] getPointIndexes(String blob, int startfrom) throws Exception{
        ArrayList<Integer> builder = new ArrayList<>();
        int endOfSeq = startfrom;
        for(; ; ){
            int startOfSeq = blob.indexOf('{', endOfSeq);
            if(startOfSeq < 0){
                break;
            }
            int comma = blob.indexOf(',', startOfSeq);
            endOfSeq = blob.indexOf('}', comma);
            String xstr = blob.substring(startOfSeq + 1, comma);
            String ystr = blob.substring(comma + 1, endOfSeq);
            //System.out.println(xstr + " " + ystr + " " + startOfSeq + " " + comma + " " + endOfSeq);
            double x = Double.parseDouble(xstr);
            double y = Double.parseDouble(ystr);
            Vect3Didx idx = pointIndex.index(x, y, 0);
            builder.add(idx.getIndex());
        }
        return builder.stream().mapToInt(i -> i).toArray(); // i -> i is converting Integer to int
    }

    /**
     * moving boundary result exception constructor helper
     */
    private String mbrec(String message){
        return "Reading " + filename + ": " + message;
    }

    @SuppressWarnings("serial")
    private class MovingBoundaryResultException extends RuntimeException {

        MovingBoundaryResultException(String message){
            super(mbrec(message));
        }

        protected MovingBoundaryResultException(String message, Throwable cause){
            super(mbrec(message), cause);
        }
    }


    public static String getFakeInsideDomainName(){
        return "fakeInsideDomain";
    }

    public static String getFakeOutsideDomainName(){
        return "fakeOutsideDomain";
    }

    public static String getFakeMembraneDomainName(){
        return "fakeOutsideDomain";
    }
}
