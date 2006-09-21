package cbit.vcell.geometry.gui;
/**
 * The bean information class for cbit.vcell.geometry.gui.SurfaceCanvas.
 */
public class SurfaceCanvasBeanInfo extends java.beans.SimpleBeanInfo {
/**
 * Gets the areaOfInterest property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor areaOfInterestPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the areaOfInterest property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getAreaOfInterest", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getAreaOfInterest", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean[][].class
				};
				aSetMethod = getBeanClass().getMethod("setAreaOfInterest", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setAreaOfInterest", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("areaOfInterest"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("areaOfInterest"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("areaOfInterest"); */
		/* aDescriptor.setShortDescription("areaOfInterest"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the bQuickRender property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor bQuickRenderPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the bQuickRender property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getBQuickRender", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getBQuickRender", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setBQuickRender", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setBQuickRender", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("bQuickRender"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("bQuickRender"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("bQuickRender"); */
		/* aDescriptor.setShortDescription("bQuickRender"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the componentOrientation property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor componentOrientationPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the componentOrientation property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getComponentOrientation", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getComponentOrientation", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					java.awt.ComponentOrientation.class
				};
				aSetMethod = getBeanClass().getMethod("setComponentOrientation", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setComponentOrientation", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("componentOrientation"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("componentOrientation"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("componentOrientation"); */
		/* aDescriptor.setShortDescription("componentOrientation"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
		aDescriptor.setValue("enumerationValues", new Object[] {
				"UNKNOWN",java.awt.ComponentOrientation.UNKNOWN,"java.awt.ComponentOrientation.UNKNOWN",
				"LEFT_TO_RIGHT",java.awt.ComponentOrientation.LEFT_TO_RIGHT,"java.awt.ComponentOrientation.LEFT_TO_RIGHT",
				"RIGHT_TO_LEFT",java.awt.ComponentOrientation.RIGHT_TO_LEFT,"java.awt.ComponentOrientation.RIGHT_TO_LEFT",
		});
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the disableRepaint property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor disableRepaintPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the disableRepaint property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getDisableRepaint", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getDisableRepaint", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setDisableRepaint", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setDisableRepaint", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("disableRepaint"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("disableRepaint"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("disableRepaint"); */
		/* aDescriptor.setShortDescription("disableRepaint"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the enableDepthCueing property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor enableDepthCueingPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the enableDepthCueing property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getEnableDepthCueing", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getEnableDepthCueing", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setEnableDepthCueing", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setEnableDepthCueing", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("enableDepthCueing"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("enableDepthCueing"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("enableDepthCueing"); */
		/* aDescriptor.setShortDescription("enableDepthCueing"); */
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
 * Gets the extent property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor extentPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the extent property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getExtent", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getExtent", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					cbit.util.Extent.class
				};
				aSetMethod = getBeanClass().getMethod("setExtent", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setExtent", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("extent"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("extent"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("extent"); */
		/* aDescriptor.setShortDescription("extent"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
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
	return cbit.vcell.geometry.gui.SurfaceCanvas.class;
}


/**
 * Gets the bean class name.
 * @return java.lang.String
 */
public static java.lang.String getBeanClassName() {
	return "cbit.vcell.geometry.gui.SurfaceCanvas";
}


public java.beans.BeanDescriptor getBeanDescriptor() {
	java.beans.BeanDescriptor aDescriptor = null;
	try {
		/* Create and return the SurfaceCanvasBeanInfo bean descriptor. */
		aDescriptor = new java.beans.BeanDescriptor(cbit.vcell.geometry.gui.SurfaceCanvas.class);
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
 * Return the method descriptors for this bean.
 * @return java.beans.MethodDescriptor[]
 */
public java.beans.MethodDescriptor[] getMethodDescriptors() {
	try {
		java.beans.MethodDescriptor aDescriptorList[] = {};
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
			areaOfInterestPropertyDescriptor()
			,bQuickRenderPropertyDescriptor()
			,componentOrientationPropertyDescriptor()
			,disableRepaintPropertyDescriptor()
			,enableDepthCueingPropertyDescriptor()
			,extentPropertyDescriptor()
			,originPropertyDescriptor()
			,showBoundingBoxPropertyDescriptor()
			,surfaceCollectionPropertyDescriptor()
			,surfacesColorsPropertyDescriptor()
			,surfacesShowingPropertyDescriptor()
			,surfacesWireframePropertyDescriptor()
			,surfaceTransparencyPropertyDescriptor()
			,trackballPropertyDescriptor()
			,wireFrameThicknessPropertyDescriptor()
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
 * Gets the origin property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor originPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the origin property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getOrigin", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getOrigin", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					cbit.util.Origin.class
				};
				aSetMethod = getBeanClass().getMethod("setOrigin", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setOrigin", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("origin"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("origin"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("origin"); */
		/* aDescriptor.setShortDescription("origin"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the showBoundingBox property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor showBoundingBoxPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the showBoundingBox property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getShowBoundingBox", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getShowBoundingBox", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setShowBoundingBox", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setShowBoundingBox", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("showBoundingBox"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("showBoundingBox"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("showBoundingBox"); */
		/* aDescriptor.setShortDescription("showBoundingBox"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
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
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					cbit.render.objects.SurfaceCollection.class
				};
				aSetMethod = getBeanClass().getMethod("setSurfaceCollection", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setSurfaceCollection", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("surfaceCollection"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("surfaceCollection"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("surfaceCollection"); */
		/* aDescriptor.setShortDescription("surfaceCollection"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the surfacesColors property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor surfacesColorsPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the surfacesColors property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getSurfacesColors", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getSurfacesColors", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					int[][].class
				};
				aSetMethod = getBeanClass().getMethod("setSurfacesColors", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setSurfacesColors", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("surfacesColors"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("surfacesColors"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("surfacesColors"); */
		/* aDescriptor.setShortDescription("surfacesColors"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the surfacesShowing property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor surfacesShowingPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the surfacesShowing property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getSurfacesShowing", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getSurfacesShowing", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean[].class
				};
				aSetMethod = getBeanClass().getMethod("setSurfacesShowing", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setSurfacesShowing", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("surfacesShowing"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("surfacesShowing"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("surfacesShowing"); */
		/* aDescriptor.setShortDescription("surfacesShowing"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the surfacesWireframe property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor surfacesWireframePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the surfacesWireframe property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getSurfacesWireframe", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getSurfacesWireframe", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean[].class
				};
				aSetMethod = getBeanClass().getMethod("setSurfacesWireframe", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setSurfacesWireframe", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("surfacesWireframe"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("surfacesWireframe"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("surfacesWireframe"); */
		/* aDescriptor.setShortDescription("surfacesWireframe"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the surfaceTransparency property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor surfaceTransparencyPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the surfaceTransparency property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getSurfaceTransparency", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getSurfaceTransparency", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					java.awt.AlphaComposite[].class
				};
				aSetMethod = getBeanClass().getMethod("setSurfaceTransparency", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setSurfaceTransparency", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("surfaceTransparency"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("surfaceTransparency"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("surfaceTransparency"); */
		/* aDescriptor.setShortDescription("surfaceTransparency"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the trackball property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor trackballPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the trackball property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getTrackball", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getTrackball", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					cbit.render.Trackball.class
				};
				aSetMethod = getBeanClass().getMethod("setTrackball", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setTrackball", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("trackball"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("trackball"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("trackball"); */
		/* aDescriptor.setShortDescription("trackball"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the wireFrameThickness property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor wireFrameThicknessPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the wireFrameThickness property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getWireFrameThickness", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getWireFrameThickness", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					double.class
				};
				aSetMethod = getBeanClass().getMethod("setWireFrameThickness", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setWireFrameThickness", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("wireFrameThickness"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("wireFrameThickness"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("wireFrameThickness"); */
		/* aDescriptor.setShortDescription("wireFrameThickness"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
}