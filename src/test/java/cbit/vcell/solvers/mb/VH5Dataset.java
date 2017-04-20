package cbit.vcell.solvers.mb;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.vcell.util.BeanUtils;
import org.vcell.util.VCAssert;

import edu.uchc.connjur.wb.ExecutionTrace;
import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.hdf5lib.exceptions.HDF5LibraryException;
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Datatype;
import ncsa.hdf.object.h5.H5CompoundDS;
import ncsa.hdf.object.h5.H5Datatype;
import ncsa.hdf.object.h5.H5ScalarDS;
public class VH5Dataset {
	private final Dataset dataset;

	public VH5Dataset(Dataset dataset) {
		super();
		this.dataset = dataset;
		dataset.init();
	}

	public void info( ) {
		try {
		System.out.println(dataset.getName());
		System.out.println(dataset.getFullName());
		H5ScalarDS sds = BeanUtils.downcast(H5ScalarDS.class,dataset);
		if (sds != null) {
			info(sds);
		}
		H5CompoundDS cds = BeanUtils.downcast(H5CompoundDS.class,dataset);
		int rank = dataset.getRank();
		long[] d = dataset.getDims( );
		long[] s = dataset.getSelectedDims();
		for (int i = 0; i < rank; i ++) {
			s[i] = d[i];
		}
		if (cds != null) {
			info(cds);
		}
		else {
			Object obj = dataset.read();
			analyze(obj);
		}


		 String[] names = dataset.getDimNames();
		 if (names == null) {
			 names = new String[rank];
		 }
		 long[] dims = dataset.getMaxDims();
		 for (int i = 0; i < rank ; i++) {
			 System.out.println("n " + StringUtils.defaultString(names[i]) + " has " + dims[i]);
		 }
		 System.out.println("current dims " + Arrays.toString(dataset.getDims()));
		 System.out.println("chunk size " + Arrays.toString(dataset.getChunkSize()));
		 System.out.println("selected " + Arrays.toString(dataset.getSelectedDims()));
		 System.out.println("start " + Arrays.toString(dataset.getStartDims()));
		 System.out.println("stride " + Arrays.toString(dataset.getStride()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void info(H5ScalarDS ds) throws Exception {
		Datatype dt = ds.getDatatype();
		 System.out.println(dt.getFullName());
		 System.out.println(dt.getDatatypeDescription());
		 int n = dt.toNative();
		 Datatype nt = new H5Datatype(n);
//		 dt = dt.getBasetype();
		 System.out.println(nt.getFullName());
		 System.out.println(nt.getDatatypeDescription());
		 System.out.println(H5Client.parseMeta(dt));

//		 ds.init();
//		 int did = ds.open();
//		 ds.read();
//
//		 int cdt = H5.H5Tcreate(HDF5Constants.H5T_COMPOUND,128);
//		 int vtype = H5.H5Tvlen_create(cdt);
//		 int ndims = ds.getRank();
//		 long dims[] = new long[ndims];
//		 long maxdims[] = new long[ndims];
//		 int space = H5.H5Dget_space(did);
//		 H5.H5Sget_simple_extent_dims(space, dims,maxdims);
//		 System.out.println(StringUtils.join(dims));
//		 long bsize = H5.H5Dvlen_get_buf_size_long(did,vtype,space);
//
//		 double bdata[][] = new double[2][(int)dims[0]];
//		 int status = H5.H5Dread(did,vtype,HDF5Constants.H5S_ALL,HDF5Constants.H5S_ALL,HDF5Constants.H5P_DEFAULT,bdata);
//		 System.out.println(status);

	}
	public void info(H5CompoundDS ds) throws Exception {
		Datatype dt = ds.getDatatype();
		String[] mn = ds.getMemberNames();
		 System.out.println(ArrayUtils.toString(mn));
		 System.out.println(dt.getFullName());
		 System.out.println(dt.getDatatypeDescription());
		 Object obj = ds.read();
		 Collection<?> coll = BeanUtils.downcast(Collection.class, obj);
		 VCAssert.assertTrue(coll.size() == mn.length, "collection matches names");
		 int i  = 0;
		 for (Object o : coll) {
			 System.out.println(mn[i++] + " ");
			 analyze(o);
		 }
	}

	public void meta( ) {

		 try {
			@SuppressWarnings("unchecked")
			List<Object> meta = dataset.getMetadata();
			for (Object obj : meta) {
				System.out.println(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void analyze(Object o) {
		Objects.requireNonNull(o);
		Vector<?> v = BeanUtils.downcast(Vector.class, o);
		if (v != null) {
			analyzeVector(v);
			return;
		}
		Class<? extends Object> clzz = o.getClass();
		boolean ia = clzz.isArray();
		if (ia) {
			System.out.println("array  of " + clzz.getComponentType().getName());
		}

		boolean prim = clzz.isPrimitive();
		if (prim)  {
			System.out.println("primitive " + clzz.getTypeName());
			return;
		}
		System.out.println("Object type " + ExecutionTrace.justClassName(o));
	}

	private void analyzeVector(Vector<?> v) {
		Objects.requireNonNull(v);
		for (int i = 0 ; i < v.size(); i++) {
			Object vo = v.get(i);
			System.out.println("Vector index " + i + " object type " + ExecutionTrace.justClassName(vo));
			analyze(vo);
		}
	}



}
