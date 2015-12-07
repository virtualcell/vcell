package cbit.vcell.solvers.mb;

import java.util.Objects;

import org.vcell.util.BeanUtils;
import org.vcell.util.VCAssert;

import ncsa.hdf.object.Group;
import ncsa.hdf.object.h5.H5ScalarDS;

/**
 * extends VH5Path to include type checking and data conversion. Throws exception if data not found, or
 * not of correct type, and no implemented conversion works.
 *
 * Single value primitives should be retrieved by requesting array and verifying it's a single element
 * @author GWeatherby
 *
 * @param <T> type of returned data. primitives not supported, autoboxing not supported
 */
public class VH5TypedPath<T> extends VH5Path {

	public VH5TypedPath(Group g, Class<T> clzz, String... names) {
		super(g, names);
		Objects.requireNonNull(clzz);
		if (clzz.isPrimitive()) {
			throw new UnsupportedOperationException("Primitive type " + className(clzz) + " not supported");
		}
		if (exc == null) {
			if (target == null) {
				throw new RuntimeException("path " + concat(names) + " not found");
			}
			Objects.requireNonNull(target);
			Class<? extends Object> tclass = target.getClass();
			if (!clzz.isAssignableFrom(tclass)){
				H5ScalarDS sds = BeanUtils.downcast(H5ScalarDS.class, target);
				if (sds != null) {
					convert(sds,clzz,names);
					return;
				}
				if (tclass.isArray()) {
					if (convert(tclass,clzz)) {
						return;
					}
				}

				throw new UnsupportedOperationException(concat(names) +
						" returns " + className(tclass) + " not "
						+ className(clzz));
			}
		}
	}

	/**
	 * @return data cast to appropriate type
	 */
	@SuppressWarnings("unchecked")
	public T get() {
		return (T) super.getData();
	}

	/**
	 * if target is single element array of type clzz, convert it from array to object
	 * @param tclass target type
	 * @param clzz desired type
	 * @return true if conversion occurs
	 */
	private boolean convert(Class<? extends Object> tclass, Class<T> clzz) {
		VCAssert.assertTrue(tclass == target.getClass());
		VCAssert.assertTrue(tclass.isArray());
		if (clzz.isAssignableFrom(tclass.getComponentType())) {
			Object[] o = (Object[]) target;
			if (o.length == 1) {
				target = o[0];
				return true;
			}
		}
		return false;
	}

	/**
	 * convert single element H5ScalarDS -- which reads as one element array
	 * @param sds non null
	 * @param clzz target type
	 * @param names current path, used for error / exception message
	 */
	private void convert(H5ScalarDS sds, Class<T> clzz, String[] names) {
		Object read = null;
		try {
			read = sds.read();
		} catch (Exception e) {
			throw new UnsupportedOperationException(concat(names) + " read exception ",e);
		}
		int rank = sds.getRank();
		if (rank != 1) {
			throw new UnsupportedOperationException(concat(names) +
					" rank " + rank + " !=1 ");
		}
		long d[] = sds.getDims();
		VCAssert.assertTrue(d.length == 1);
		Class<?> readClass = read.getClass();
		VCAssert.assertTrue(readClass.isArray(),"isArray");
		Class<?> returnedType = readClass.getComponentType();


		if (clzz.isArray()) {
			Class<?> desiredType = clzz.getComponentType();
			if (!desiredType.isAssignableFrom(returnedType)) {
				throw new UnsupportedOperationException(concat(names) + " is array of " + className(returnedType)  + " not " + className(desiredType));
			}
			target = read;
			return;
		}
		if (d[0] > 1) {
			throw new UnsupportedOperationException(concat(names) + " is array of length " + d[0] + ", not single value");
		}
		if (!clzz.isAssignableFrom(returnedType)) {
			throw new UnsupportedOperationException(concat(names) + " is array of " + className(returnedType)  + " not " + className(clzz));
		}
		Object[] oarray = (Object[]) read;

		target = oarray[0] ;
		//		int length = oarray.length;
		//		Object dest[] = (Object[]) Array.newInstance(desiredType, length);
		//		for (int i = 0; i < length; i++) {
		//			dest[i] = db[i];
		//		}
	}


}
