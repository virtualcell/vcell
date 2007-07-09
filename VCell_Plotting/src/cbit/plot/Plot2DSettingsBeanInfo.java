package cbit.plot;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * The bean information class for cbit.plot.Plot2DSettings.
 */
public class Plot2DSettingsBeanInfo extends java.beans.SimpleBeanInfo {
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
	return cbit.plot.Plot2DSettings.class;
}
/**
 * Gets the bean class name.
 * @return java.lang.String
 */
public static java.lang.String getBeanClassName() {
	return "cbit.plot.Plot2DSettings";
}
public java.beans.BeanDescriptor getBeanDescriptor() {
	java.beans.BeanDescriptor aDescriptor = null;
	try {
		/* Create and return the Plot2DSettingsBeanInfo bean descriptor. */
		aDescriptor = new java.beans.BeanDescriptor(cbit.plot.Plot2DSettings.class);
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
			showCrosshairPropertyDescriptor()
			,showNodesPropertyDescriptor()
			,snapToNodesPropertyDescriptor()
			,xAutoPropertyDescriptor()
			,xAutoRangePropertyDescriptor()
			,xManualRangePropertyDescriptor()
			,xStretchPropertyDescriptor()
			,yAutoPropertyDescriptor()
			,yAutoRangePropertyDescriptor()
			,yManualRangePropertyDescriptor()
			,yStretchPropertyDescriptor()
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
 * Gets the showCrosshair property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor showCrosshairPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the showCrosshair property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getShowCrosshair", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getShowCrosshair", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setShowCrosshair", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setShowCrosshair", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("showCrosshair"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("showCrosshair"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("showCrosshair"); */
		/* aDescriptor.setShortDescription("showCrosshair"); */
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
 * Gets the showNodes property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor showNodesPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the showNodes property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getShowNodes", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getShowNodes", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setShowNodes", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setShowNodes", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("showNodes"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("showNodes"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("showNodes"); */
		/* aDescriptor.setShortDescription("showNodes"); */
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
 * Gets the snapToNodes property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor snapToNodesPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the snapToNodes property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getSnapToNodes", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getSnapToNodes", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setSnapToNodes", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setSnapToNodes", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("snapToNodes"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("snapToNodes"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("snapToNodes"); */
		/* aDescriptor.setShortDescription("snapToNodes"); */
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
 * Gets the xAuto property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor xAutoPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the xAuto property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getXAuto", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getXAuto", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setXAuto", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setXAuto", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("xAuto"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("xAuto"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("xAuto"); */
		/* aDescriptor.setShortDescription("xAuto"); */
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
 * Gets the xAutoRange property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor xAutoRangePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the xAutoRange property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getXAutoRange", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getXAutoRange", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					org.vcell.util.Range.class
				};
				aSetMethod = getBeanClass().getMethod("setXAutoRange", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setXAutoRange", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("xAutoRange"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("xAutoRange"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("xAutoRange"); */
		/* aDescriptor.setShortDescription("xAutoRange"); */
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
 * Gets the xManualRange property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor xManualRangePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the xManualRange property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getXManualRange", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getXManualRange", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					org.vcell.util.Range.class
				};
				aSetMethod = getBeanClass().getMethod("setXManualRange", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setXManualRange", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("xManualRange"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("xManualRange"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("xManualRange"); */
		/* aDescriptor.setShortDescription("xManualRange"); */
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
 * Gets the xStretch property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor xStretchPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the xStretch property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getXStretch", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getXStretch", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setXStretch", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setXStretch", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("xStretch"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("xStretch"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("xStretch"); */
		/* aDescriptor.setShortDescription("xStretch"); */
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
 * Gets the yAuto property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor yAutoPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the yAuto property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getYAuto", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getYAuto", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setYAuto", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setYAuto", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("yAuto"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("yAuto"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("yAuto"); */
		/* aDescriptor.setShortDescription("yAuto"); */
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
 * Gets the yAutoRange property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor yAutoRangePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the yAutoRange property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getYAutoRange", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getYAutoRange", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					org.vcell.util.Range.class
				};
				aSetMethod = getBeanClass().getMethod("setYAutoRange", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setYAutoRange", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("yAutoRange"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("yAutoRange"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("yAutoRange"); */
		/* aDescriptor.setShortDescription("yAutoRange"); */
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
 * Gets the yManualRange property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor yManualRangePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the yManualRange property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getYManualRange", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getYManualRange", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					org.vcell.util.Range.class
				};
				aSetMethod = getBeanClass().getMethod("setYManualRange", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setYManualRange", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("yManualRange"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("yManualRange"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("yManualRange"); */
		/* aDescriptor.setShortDescription("yManualRange"); */
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
 * Gets the yStretch property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor yStretchPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the yStretch property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getYStretch", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getYStretch", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setYStretch", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setYStretch", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("yStretch"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("yStretch"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("yStretch"); */
		/* aDescriptor.setShortDescription("yStretch"); */
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
