package cbit.vcell.solvers.mb;

import java.util.List;
import java.util.Objects;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.vcell.util.BeanUtils;
import org.vcell.util.VCAssert;

import ncsa.hdf.hdf5lib.exceptions.HDF5Exception;
import ncsa.hdf.object.Attribute;
import ncsa.hdf.object.DataFormat;
import ncsa.hdf.object.Group;
import ncsa.hdf.object.HObject;
import ncsa.hdf.object.h5.H5CompoundDS;
import org.apache.log4j.Logger;

/**
 * Class to recursively parse HDF5 file seeking requested data
 * This fails quietly if path is invalid. It supports attributes as last element of the path,
 * and simple compound and scalar HDF5 types
 * @author GWeatherby
 */
public class VH5Path {

	protected Object target;
	protected Exception exc;
	protected static final Logger lg = Logger.getLogger(VH5Path.class);


	/**
	 * @param g staring point, not null
	 * @param names path to search
	 */
	public VH5Path(Group g, String ...names) {
		target = null;
		exc = null;
		try {
			target = walk(g,names,0);
		} catch (Exception e) {
			exc = e;
			if (lg.isWarnEnabled()) {
				lg.warn("Error retrieving " + concat(names),exc);
			}
		}
	}

	/**
	 * @return located data or null if not found
	 */
	public Object getData( ) {
		return target;
	}

	/**
	 * @return description of type found, or "fail" if not found
	 */
	public String foundType( ) {
		if (target != null) {
			return target.getClass( ).getSimpleName();
		}
		return "fail";
	}

	/**
	 * @return true if no exception occurred
	 */
	public boolean isGood( ) {
		return exc == null;

	}
	/**
	 * @return exception stored while processing, or null if none
	 */
	public Exception getExc() {
		return exc;
	}


	/**
	 * @param names not null
	 * @return names as single path
	 */
	public static String concat(String[] names) {
		Objects.requireNonNull(names);
		return StringUtils.join(names,'/');
	}
	/**
	 * concat names and indicate specific element
	 * @param names
	 * @param current
	 * @return concat(names) + current
	 */
	protected static String concat(String[] names, String current) {
		return concat(names) + ", element " + current;
	}

	protected static String className(Object obj) {
		if (obj != null) {
			return className(obj.getClass());
		}
		return "null";
	}

	protected static String className(Class<?> clzz) {
		if (clzz != null) {
			return clzz.getSimpleName();
		}
		return "null";
	}

	/**
	 * @param index
	 * @param steps non-null
	 * @return true if index refers to last element in steps
	 */
	private static boolean lastIndex(int index, String[] steps) {
		return index  + 1 == steps.length;
	}

	/**
	 * find next object in sequence
	 * @param hobj previous element in sequence
	 * @param steps name of each step
	 * @param index current step
	 * @return next object path, if present
	 * @throws HDF5Exception
	 */
	private static Object walk(Object hobj, String[] steps, int index) throws Exception {
		final boolean isLastIndex = lastIndex(index,steps);
		final String finding = steps[index];
		Group g = BeanUtils.downcast(Group.class, hobj);
		if (g != null) {
			List<HObject> ml = g.getMemberList();
			for (HObject sub : ml) {
//				String p = sub.getPath();
//				String name = sub.getName();
//				String full = sub.getFullName();
				if (finding.equals(sub.getName())) {
					if (isLastIndex) {
						return sub;
					}
					return walk(sub,steps,index+1);
				}
			}
		}
		H5CompoundDS cds = BeanUtils.downcast(H5CompoundDS.class, hobj);
		if (cds != null) {
			cds.read();
			String[] mn = cds.getMemberNames();

			for (int i = 0; i < mn.length ; i++) {
				if (finding.equals(mn[i]) ) {
					Object c = cds.read();
					Vector<?> vec = BeanUtils.downcast(Vector.class, c);
					if (vec != null) {
						VCAssert.assertTrue(i < vec.size( ), "Disconnect between H5CompoundDS.getMemberNames( )  and returned Vector");
						Object child = vec.get(i);
						if (isLastIndex) {
							return child;
						}
					}
					else {
						throw new UnsupportedOperationException("Unsupported H5CompoundDS subtype " + className(c) );
					}
				}
			}

		}
		if (isLastIndex) {
			DataFormat df= BeanUtils.downcast(DataFormat.class, hobj);
			if (df != null  && df.hasAttribute()) {
				try {
					@SuppressWarnings("unchecked")
					List<Object> meta = df.getMetadata();
					for (Object o : meta) {
						Attribute a = BeanUtils.downcast(Attribute.class, o);
						if (a != null) {
							if (finding.equals(a.getName())) {
								return a.getValue();
							}
						}
						else {
							lg.warn(concat(steps,finding) + " fetching metadata unexpected type " + className(o));
						}

					}
				} catch (Exception e) {
					throw new RuntimeException(concat(steps,finding) + " fetching metadata",e);
				}
			}
		}

		return null;
	}

}
