package cbit.vcell.geometry.surface;
/**
 * The bean information class for cbit.vcell.geometry.surface.GeometrySurfaceDescription.
 */
public class GeometrySurfaceDescriptionBeanInfo extends java.beans.SimpleBeanInfo {
/**
 * Gets the filterCutoffFrequency property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor filterCutoffFrequencyPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the filterCutoffFrequency property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getFilterCutoffFrequency", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getFilterCutoffFrequency", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					java.lang.Double.class
				};
				aSetMethod = getBeanClass().getMethod("setFilterCutoffFrequency", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setFilterCutoffFrequency", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("filterCutoffFrequency"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("filterCutoffFrequency"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		aDescriptor.setConstrained(true);
		/* aDescriptor.setDisplayName("filterCutoffFrequency"); */
		/* aDescriptor.setShortDescription("filterCutoffFrequency"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		aDescriptor.setValue("preferred", new Boolean(true));
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Find the method by comparing (name & parameter size) against the methods in the class.
 * @return java.lang.reflect.Method
 * @param aClass java.lang.Class
 * @param methodName java.lang.String
 * @param parameterCount int
 */
public static java.lang.reflect.Method findMethod(java.lang.Class aClass, java.lang.String methodName, int parameterCount) {
	try {
		/* Since this method attempts to find a method by getting all methods from the class,
	this method should only be called if getMethod cannot find the method. */
		java.lang.reflect.Method methods[] = aClass.getMethods();
		for (int index = 0; index < methods.length; index++){
			java.lang.reflect.Method method = methods[index];
			if ((method.getParameterTypes().length == parameterCount) && (method.getName().equals(methodName))) {
				return method;
			}
		}
	} catch (java.lang.Throwable exception) {
		return null;
	}
	return null;
}


/**
 * Gets the geometricRegions property descriptor.
 * @return java.beans.IndexedPropertyDescriptor
 */
public java.beans.IndexedPropertyDescriptor geometricRegionsPropertyDescriptor() {
	java.beans.IndexedPropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the geometricRegions property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getGeometricRegions", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getGeometricRegions", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					cbit.vcell.geometry.surface.GeometricRegion[].class
				};
				aSetMethod = getBeanClass().getMethod("setGeometricRegions", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setGeometricRegions", 1);
			};
			java.lang.reflect.Method aGetIndexedMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetIndexedMethodParameterTypes[] = {
					int.class
				};
				aGetIndexedMethod = getBeanClass().getMethod("getGeometricRegions", aGetIndexedMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetIndexedMethod = findMethod(getBeanClass(), "getGeometricRegions", 1);
			};
			java.lang.reflect.Method aSetIndexedMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetIndexedMethodParameterTypes[] = {
					int.class,
					cbit.vcell.geometry.surface.GeometricRegion.class
				};
				aSetIndexedMethod = getBeanClass().getMethod("setGeometricRegions", aSetIndexedMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetIndexedMethod = findMethod(getBeanClass(), "setGeometricRegions", 2);
			};
			aDescriptor = new java.beans.IndexedPropertyDescriptor("geometricRegions"
			, aGetMethod, aSetMethod, aGetIndexedMethod, aSetIndexedMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.IndexedPropertyDescriptor("geometricRegions"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		aDescriptor.setConstrained(true);
		/* aDescriptor.setDisplayName("geometricRegions"); */
		/* aDescriptor.setShortDescription("geometricRegions"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		aDescriptor.setValue("preferred", new Boolean(true));
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the geometry property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor geometryPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the geometry property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getGeometry", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getGeometry", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("geometry"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("geometry"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("geometry"); */
		/* aDescriptor.setShortDescription("geometry"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		aDescriptor.setValue("preferred", new Boolean(true));
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Returns the BeanInfo of the superclass of this bean to inherit its features.
 * @return java.beans.BeanInfo[]
 */
public java.beans.BeanInfo[] getAdditionalBeanInfo() {
	java.lang.Class superClass;
	java.beans.BeanInfo superBeanInfo = null;

	try {
		superClass = getBeanDescriptor().getBeanClass().getSuperclass();
	} catch (java.lang.Throwable exception) {
		return null;
	}

	try {
		superBeanInfo = java.beans.Introspector.getBeanInfo(superClass);
	} catch (java.beans.IntrospectionException ie) {}

	if (superBeanInfo != null) {
		java.beans.BeanInfo[] ret = new java.beans.BeanInfo[1];
		ret[0] = superBeanInfo;
		return ret;
	}
	return null;
}


/**
 * Gets the bean class.
 * @return java.lang.Class
 */
public static java.lang.Class getBeanClass() {
	return cbit.vcell.geometry.surface.GeometrySurfaceDescription.class;
}


/**
 * Gets the bean class name.
 * @return java.lang.String
 */
public static java.lang.String getBeanClassName() {
	return "cbit.vcell.geometry.surface.GeometrySurfaceDescription";
}


public java.beans.BeanDescriptor getBeanDescriptor() {
	java.beans.BeanDescriptor aDescriptor = null;
	try {
		/* Create and return the GeometrySurfaceDescriptionBeanInfo bean descriptor. */
		aDescriptor = new java.beans.BeanDescriptor(cbit.vcell.geometry.surface.GeometrySurfaceDescription.class);
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("hidden-state", Boolean.FALSE); */
	} catch (Throwable exception) {
	};
	return aDescriptor;
}


/**
 * Return the event set descriptors for this bean.
 * @return java.beans.EventSetDescriptor[]
 */
public java.beans.EventSetDescriptor[] getEventSetDescriptors() {
	try {
		java.beans.EventSetDescriptor aDescriptorList[] = {};
		return aDescriptorList;
	} catch (Throwable exception) {
		handleException(exception);
	};
	return null;
}


/**
 * Gets the getGeometricRegions(int) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getGeometricRegions_intMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getGeometricRegions(int) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				int.class
			};
			aMethod = getBeanClass().getMethod("getGeometricRegions", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getGeometricRegions", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("index");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("getGeometricRegions(int)"); */
		/* aDescriptor.setShortDescription("getGeometricRegions(int)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		aDescriptor.setValue("preferred", new Boolean(true));
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Return the method descriptors for this bean.
 * @return java.beans.MethodDescriptor[]
 */
public java.beans.MethodDescriptor[] getMethodDescriptors() {
	try {
		java.beans.MethodDescriptor aDescriptorList[] = {
			getGeometricRegions_intMethodDescriptor()
			,setGeometricRegions_int_cbitvcellgeometrysurfaceGeometricRegionMethodDescriptor()
		};
		return aDescriptorList;
	} catch (Throwable exception) {
		handleException(exception);
	};
	return null;
}


/**
 * Return the property descriptors for this bean.
 * @return java.beans.PropertyDescriptor[]
 */
public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
	try {
		java.beans.PropertyDescriptor aDescriptorList[] = {
			filterCutoffFrequencyPropertyDescriptor()
			,geometricRegionsPropertyDescriptor()
			,geometryPropertyDescriptor()
			,regionImagePropertyDescriptor()
			,surfaceCollectionPropertyDescriptor()
			,volumeSampleSizePropertyDescriptor()
		};
		return aDescriptorList;
	} catch (Throwable exception) {
		handleException(exception);
	};
	return null;
}


/**
 * Called whenever the bean information class throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}


/**
 * Gets the regionImage property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor regionImagePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the regionImage property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getRegionImage", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getRegionImage", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("regionImage"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("regionImage"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("regionImage"); */
		/* aDescriptor.setShortDescription("regionImage"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		aDescriptor.setValue("preferred", new Boolean(true));
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the setGeometricRegions(int, cbit.vcell.geometry.surface.GeometricRegion) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor setGeometricRegions_int_cbitvcellgeometrysurfaceGeometricRegionMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the setGeometricRegions(int, cbit.vcell.geometry.surface.GeometricRegion) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				int.class,
				cbit.vcell.geometry.surface.GeometricRegion.class
			};
			aMethod = getBeanClass().getMethod("setGeometricRegions", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "setGeometricRegions", 2);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("index");
			java.beans.ParameterDescriptor aParameterDescriptor2 = new java.beans.ParameterDescriptor();
			aParameterDescriptor2.setName("arg2");
			aParameterDescriptor2.setDisplayName("geometricRegions");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1,
				aParameterDescriptor2
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("setGeometricRegions(int, cbit.vcell.geometry.surface.GeometricRegion)"); */
		/* aDescriptor.setShortDescription("setGeometricRegions(int, cbit.vcell.geometry.surface.GeometricRegion)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		aDescriptor.setValue("preferred", new Boolean(true));
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the surfaceCollection property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor surfaceCollectionPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the surfaceCollection property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getSurfaceCollection", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getSurfaceCollection", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("surfaceCollection"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("surfaceCollection"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("surfaceCollection"); */
		/* aDescriptor.setShortDescription("surfaceCollection"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		aDescriptor.setValue("preferred", new Boolean(true));
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the volumeSampleSize property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor volumeSampleSizePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the volumeSampleSize property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getVolumeSampleSize", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getVolumeSampleSize", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					org.vcell.util.ISize.class
				};
				aSetMethod = getBeanClass().getMethod("setVolumeSampleSize", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setVolumeSampleSize", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("volumeSampleSize"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("volumeSampleSize"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		aDescriptor.setConstrained(true);
		/* aDescriptor.setDisplayName("volumeSampleSize"); */
		/* aDescriptor.setShortDescription("volumeSampleSize"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		aDescriptor.setValue("preferred", new Boolean(true));
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
}