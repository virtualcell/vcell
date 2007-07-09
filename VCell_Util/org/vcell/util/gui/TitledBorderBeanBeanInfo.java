package org.vcell.util.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * The bean information class for cbit.gui.TitledBorderBean.
 */
public class TitledBorderBeanBeanInfo extends java.beans.SimpleBeanInfo {
/**
 * Gets the borderOpaque property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor borderOpaquePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the borderOpaque property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("isBorderOpaque", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "isBorderOpaque", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("borderOpaque"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("borderOpaque"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("borderOpaque"); */
		/* aDescriptor.setShortDescription("borderOpaque"); */
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
 * Gets the border property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor borderPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the border property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getBorder", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getBorder", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					javax.swing.border.Border.class
				};
				aSetMethod = getBeanClass().getMethod("setBorder", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setBorder", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("border"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("border"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("border"); */
		/* aDescriptor.setShortDescription("border"); */
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
 * Gets the class property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor classPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the class property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getClass", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getClass", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("class"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("class"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("class"); */
		/* aDescriptor.setShortDescription("class"); */
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
	return org.vcell.util.gui.TitledBorderBean.class;
}
/**
 * Gets the bean class name.
 * @return java.lang.String
 */
public static java.lang.String getBeanClassName() {
	return "cbit.gui.TitledBorderBean";
}
public java.beans.BeanDescriptor getBeanDescriptor() {
	java.beans.BeanDescriptor aDescriptor = null;
	try {
		/* Create and return the TitledBorderBeanBeanInfo bean descriptor. */
		aDescriptor = new java.beans.BeanDescriptor(org.vcell.util.gui.TitledBorderBean.class);
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
			borderOpaquePropertyDescriptor()
			,borderPropertyDescriptor()
			,classPropertyDescriptor()
			,titleColorPropertyDescriptor()
			,titleFontPropertyDescriptor()
			,titleJustificationPropertyDescriptor()
			,titlePositionPropertyDescriptor()
			,titlePropertyDescriptor()
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
 * Gets the titleColor property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor titleColorPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the titleColor property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getTitleColor", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getTitleColor", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					java.awt.Color.class
				};
				aSetMethod = getBeanClass().getMethod("setTitleColor", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setTitleColor", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("titleColor"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("titleColor"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("titleColor"); */
		/* aDescriptor.setShortDescription("titleColor"); */
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
 * Gets the titleFont property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor titleFontPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the titleFont property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getTitleFont", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getTitleFont", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					java.awt.Font.class
				};
				aSetMethod = getBeanClass().getMethod("setTitleFont", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setTitleFont", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("titleFont"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("titleFont"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("titleFont"); */
		/* aDescriptor.setShortDescription("titleFont"); */
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
 * Gets the titleJustification property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor titleJustificationPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the titleJustification property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getTitleJustification", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getTitleJustification", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					int.class
				};
				aSetMethod = getBeanClass().getMethod("setTitleJustification", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setTitleJustification", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("titleJustification"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("titleJustification"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("titleJustification"); */
		/* aDescriptor.setShortDescription("titleJustification"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
		aDescriptor.setValue("enumerationValues", new Object[] {
				"DEFAULT_JUSTIFICATION",new Integer(javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION),"javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION",
				"LEFT",new Integer(javax.swing.border.TitledBorder.LEFT),"javax.swing.border.TitledBorder.LEFT",
				"CENTER",new Integer(javax.swing.border.TitledBorder.CENTER),"javax.swing.border.TitledBorder.CENTER",
				"RIGHT",new Integer(javax.swing.border.TitledBorder.RIGHT),"javax.swing.border.TitledBorder.RIGHT",
		});
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the titlePosition property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor titlePositionPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the titlePosition property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getTitlePosition", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getTitlePosition", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					int.class
				};
				aSetMethod = getBeanClass().getMethod("setTitlePosition", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setTitlePosition", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("titlePosition"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("titlePosition"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("titlePosition"); */
		/* aDescriptor.setShortDescription("titlePosition"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
		aDescriptor.setValue("enumerationValues", new Object[] {
				"DEFAULT_POSITION",new Integer(javax.swing.border.TitledBorder.DEFAULT_POSITION),"javax.swing.border.TitledBorder.DEFAULT_POSITION",
				"ABOVE_TOP",new Integer(javax.swing.border.TitledBorder.ABOVE_TOP),"javax.swing.border.TitledBorder.ABOVE_TOP",
				"TOP",new Integer(javax.swing.border.TitledBorder.TOP),"javax.swing.border.TitledBorder.TOP",
				"BELOW_TOP",new Integer(javax.swing.border.TitledBorder.BELOW_TOP),"javax.swing.border.TitledBorder.BELOW_TOP",
				"ABOVE_BOTTOM",new Integer(javax.swing.border.TitledBorder.ABOVE_BOTTOM),"javax.swing.border.TitledBorder.ABOVE_BOTTOM",
				"BOTTOM",new Integer(javax.swing.border.TitledBorder.BOTTOM),"javax.swing.border.TitledBorder.BOTTOM",
				"BELOW_BOTTOM",new Integer(javax.swing.border.TitledBorder.BELOW_BOTTOM),"javax.swing.border.TitledBorder.BELOW_BOTTOM",
		});
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the title property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor titlePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the title property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getTitle", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getTitle", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					java.lang.String.class
				};
				aSetMethod = getBeanClass().getMethod("setTitle", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setTitle", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("title"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("title"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("title"); */
		/* aDescriptor.setShortDescription("title"); */
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
