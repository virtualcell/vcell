package cbit.image.gui;

import cbit.vcell.simdata.SourceDataInfo;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * The bean information class for cbit.image.ImagePlaneManagerPanel.
 */
public class ImagePlaneManagerPanelBeanInfo extends java.beans.SimpleBeanInfo {
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
 * Gets the curveEditorToolPanelVisible property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor curveEditorToolPanelVisiblePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the curveEditorToolPanelVisible property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getCurveEditorToolPanelVisible", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getCurveEditorToolPanelVisible", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setCurveEditorToolPanelVisible", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setCurveEditorToolPanelVisible", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("curveEditorToolPanelVisible"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("curveEditorToolPanelVisible"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("curveEditorToolPanelVisible"); */
		/* aDescriptor.setShortDescription("curveEditorToolPanelVisible"); */
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
 * Gets the curveRenderer property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor curveRendererPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the curveRenderer property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getCurveRenderer", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getCurveRenderer", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					cbit.vcell.geometry.CurveRenderer.class
				};
				aSetMethod = getBeanClass().getMethod("setCurveRenderer", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setCurveRenderer", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("curveRenderer"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("curveRenderer"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("curveRenderer"); */
		/* aDescriptor.setShortDescription("curveRenderer"); */
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
 * Gets the curveRendererSelection property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor curveRendererSelectionPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the curveRendererSelection property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getCurveRendererSelection", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getCurveRendererSelection", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("curveRendererSelection"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("curveRendererSelection"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("curveRendererSelection"); */
		/* aDescriptor.setShortDescription("curveRendererSelection"); */
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
 * Gets the curveRendererSelectionValid property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor curveRendererSelectionValidPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the curveRendererSelectionValid property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getCurveRendererSelectionValid", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getCurveRendererSelectionValid", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("curveRendererSelectionValid"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("curveRendererSelectionValid"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("curveRendererSelectionValid"); */
		/* aDescriptor.setShortDescription("curveRendererSelectionValid"); */
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
 * Gets the curveValueProvider property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor curveValueProviderPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the curveValueProvider property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getCurveValueProvider", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getCurveValueProvider", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					cbit.vcell.simdata.CurveValueProvider.class
				};
				aSetMethod = getBeanClass().getMethod("setCurveValueProvider", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setCurveValueProvider", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("curveValueProvider"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("curveValueProvider"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("curveValueProvider"); */
		/* aDescriptor.setShortDescription("curveValueProvider"); */
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
 * Gets the displayAdapterServicePanel property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor displayAdapterServicePanelPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the displayAdapterServicePanel property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getDisplayAdapterServicePanel", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getDisplayAdapterServicePanel", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("displayAdapterServicePanel"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("displayAdapterServicePanel"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("displayAdapterServicePanel"); */
		/* aDescriptor.setShortDescription("displayAdapterServicePanel"); */
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
 * Gets the displayAdapterServicePanelVisible property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor displayAdapterServicePanelVisiblePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the displayAdapterServicePanelVisible property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getDisplayAdapterServicePanelVisible", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getDisplayAdapterServicePanelVisible", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setDisplayAdapterServicePanelVisible", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setDisplayAdapterServicePanelVisible", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("displayAdapterServicePanelVisible"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("displayAdapterServicePanelVisible"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("displayAdapterServicePanelVisible"); */
		/* aDescriptor.setShortDescription("displayAdapterServicePanelVisible"); */
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
 * Gets the enableDrawingTools(boolean) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor enableDrawingTools_booleanMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the enableDrawingTools(boolean) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				boolean.class
			};
			aMethod = getBeanClass().getMethod("enableDrawingTools", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "enableDrawingTools", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("enable");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("enableDrawingTools(boolean)"); */
		/* aDescriptor.setShortDescription("enableDrawingTools(boolean)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
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
	return cbit.image.gui.ImagePlaneManagerPanel.class;
}
/**
 * Gets the bean class name.
 * @return java.lang.String
 */
public static java.lang.String getBeanClassName() {
	return "cbit.image.ImagePlaneManagerPanel";
}
public java.beans.BeanDescriptor getBeanDescriptor() {
	java.beans.BeanDescriptor aDescriptor = null;
	try {
		/* Create and return the ImagePlaneManagerPanelBeanInfo bean descriptor. */
		aDescriptor = new java.beans.BeanDescriptor(cbit.image.gui.ImagePlaneManagerPanel.class);
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("hidden-state", Boolean.FALSE); */
	} catch (Throwable exception) {
	};
	return aDescriptor;
}
/**
 * Gets the getCurveRenderer() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getCurveRendererMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getCurveRenderer() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getCurveRenderer", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getCurveRenderer", 0);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptors[] = {};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("getCurveRenderer()"); */
		/* aDescriptor.setShortDescription("getCurveRenderer()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the getCurveValueProvider() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getCurveValueProviderMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getCurveValueProvider() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getCurveValueProvider", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getCurveValueProvider", 0);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptors[] = {};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("getCurveValueProvider()"); */
		/* aDescriptor.setShortDescription("getCurveValueProvider()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the getDisplayAdapterServicePanel() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getDisplayAdapterServicePanelMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getDisplayAdapterServicePanel() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getDisplayAdapterServicePanel", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getDisplayAdapterServicePanel", 0);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptors[] = {};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("getDisplayAdapterServicePanel()"); */
		/* aDescriptor.setShortDescription("getDisplayAdapterServicePanel()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the getDisplayAdapterServicePanelVisible() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getDisplayAdapterServicePanelVisibleMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getDisplayAdapterServicePanelVisible() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getDisplayAdapterServicePanelVisible", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getDisplayAdapterServicePanelVisible", 0);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptors[] = {};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("getDisplayAdapterServicePanelVisible()"); */
		/* aDescriptor.setShortDescription("getDisplayAdapterServicePanelVisible()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
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
 * Gets the getImagePlaneManager() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getImagePlaneManagerMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getImagePlaneManager() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getImagePlaneManager", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getImagePlaneManager", 0);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptors[] = {};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("getImagePlaneManager()"); */
		/* aDescriptor.setShortDescription("getImagePlaneManager()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
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
			enableDrawingTools_booleanMethodDescriptor()
			,getCurveRendererMethodDescriptor()
			,getCurveValueProviderMethodDescriptor()
			,getDisplayAdapterServicePanelMethodDescriptor()
			,getDisplayAdapterServicePanelVisibleMethodDescriptor()
			,getImagePlaneManagerMethodDescriptor()
			,getModeMethodDescriptor()
			,getSourceDataInfoMethodDescriptor()
			,setCurveRenderer_cbitvcellgeometryguiCurveRendererMethodDescriptor()
			,setCurveValueProvider_cbitvcellsimdataguiCurveValueProviderMethodDescriptor()
			,setDisplayAdapterServicePanelVisible_booleanMethodDescriptor()
			,setMode_intMethodDescriptor()
			,setSourceDataInfo_cbitimageSourceDataInfoMethodDescriptor()
		};
		return aDescriptorList;
	} catch (Throwable exception) {
		handleException(exception);
	};
	return null;
}
/**
 * Gets the getMode() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getModeMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getMode() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getMode", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getMode", 0);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptors[] = {};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("getMode()"); */
		/* aDescriptor.setShortDescription("getMode()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Return the property descriptors for this bean.
 * @return java.beans.PropertyDescriptor[]
 */
public java.beans.PropertyDescriptor[] getPropertyDescriptors() {
	try {
		java.beans.PropertyDescriptor aDescriptorList[] = {
			componentOrientationPropertyDescriptor()
			,curveEditorToolPanelVisiblePropertyDescriptor()
			,curveRendererPropertyDescriptor()
			,curveRendererSelectionPropertyDescriptor()
			,curveRendererSelectionValidPropertyDescriptor()
			,curveValueProviderPropertyDescriptor()
			,displayAdapterServicePanelPropertyDescriptor()
			,displayAdapterServicePanelVisiblePropertyDescriptor()
			,imagePlaneManagerPropertyDescriptor()
			,modePropertyDescriptor()
			,sourceDataInfoPropertyDescriptor()
			,spatialProjectionJCheckBoxPropertyDescriptor()
		};
		return aDescriptorList;
	} catch (Throwable exception) {
		handleException(exception);
	};
	return null;
}
/**
 * Gets the getSourceDataInfo() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getSourceDataInfoMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getSourceDataInfo() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getSourceDataInfo", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getSourceDataInfo", 0);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptors[] = {};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("getSourceDataInfo()"); */
		/* aDescriptor.setShortDescription("getSourceDataInfo()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
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
 * Gets the imagePlaneManager property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor imagePlaneManagerPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the imagePlaneManager property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getImagePlaneManager", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getImagePlaneManager", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("imagePlaneManager"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("imagePlaneManager"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("imagePlaneManager"); */
		/* aDescriptor.setShortDescription("imagePlaneManager"); */
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
 * Gets the mode property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor modePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the mode property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getMode", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getMode", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					int.class
				};
				aSetMethod = getBeanClass().getMethod("setMode", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setMode", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("mode"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("mode"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("mode"); */
		/* aDescriptor.setShortDescription("mode"); */
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
 * Gets the setCurveRenderer(cbit.vcell.geometry.gui.CurveRenderer) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor setCurveRenderer_cbitvcellgeometryguiCurveRendererMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the setCurveRenderer(cbit.vcell.geometry.gui.CurveRenderer) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.vcell.geometry.CurveRenderer.class
			};
			aMethod = getBeanClass().getMethod("setCurveRenderer", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "setCurveRenderer", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("newValue");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("setCurveRenderer(cbit.vcell.geometry.gui.CurveRenderer)"); */
		/* aDescriptor.setShortDescription("setCurveRenderer(cbit.vcell.geometry.gui.CurveRenderer)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the setCurveValueProvider(cbit.vcell.simdata.gui.CurveValueProvider) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor setCurveValueProvider_cbitvcellsimdataguiCurveValueProviderMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the setCurveValueProvider(cbit.vcell.simdata.gui.CurveValueProvider) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.vcell.simdata.CurveValueProvider.class
			};
			aMethod = getBeanClass().getMethod("setCurveValueProvider", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "setCurveValueProvider", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("curveValueProvider");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("setCurveValueProvider(cbit.vcell.simdata.gui.CurveValueProvider)"); */
		/* aDescriptor.setShortDescription("setCurveValueProvider(cbit.vcell.simdata.gui.CurveValueProvider)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the setDisplayAdapterServicePanelVisible(boolean) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor setDisplayAdapterServicePanelVisible_booleanMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the setDisplayAdapterServicePanelVisible(boolean) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				boolean.class
			};
			aMethod = getBeanClass().getMethod("setDisplayAdapterServicePanelVisible", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "setDisplayAdapterServicePanelVisible", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("arg1");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("setDisplayAdapterServicePanelVisible(boolean)"); */
		/* aDescriptor.setShortDescription("setDisplayAdapterServicePanelVisible(boolean)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the setMode(int) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor setMode_intMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the setMode(int) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				int.class
			};
			aMethod = getBeanClass().getMethod("setMode", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "setMode", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("mode");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("setMode(int)"); */
		/* aDescriptor.setShortDescription("setMode(int)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the setSourceDataInfo(cbit.image.SourceDataInfo) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor setSourceDataInfo_cbitimageSourceDataInfoMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the setSourceDataInfo(cbit.image.SourceDataInfo) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.vcell.simdata.SourceDataInfo.class
			};
			aMethod = getBeanClass().getMethod("setSourceDataInfo", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "setSourceDataInfo", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("sourceDataInfo");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("setSourceDataInfo(cbit.image.SourceDataInfo)"); */
		/* aDescriptor.setShortDescription("setSourceDataInfo(cbit.image.SourceDataInfo)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the sourceDataInfo property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor sourceDataInfoPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the sourceDataInfo property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getSourceDataInfo", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getSourceDataInfo", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					cbit.vcell.simdata.SourceDataInfo.class
				};
				aSetMethod = getBeanClass().getMethod("setSourceDataInfo", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setSourceDataInfo", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("sourceDataInfo"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("sourceDataInfo"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("sourceDataInfo"); */
		/* aDescriptor.setShortDescription("sourceDataInfo"); */
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
 * Gets the spatialProjectionJCheckBox property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor spatialProjectionJCheckBoxPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the spatialProjectionJCheckBox property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getSpatialProjectionJCheckBox", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getSpatialProjectionJCheckBox", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("spatialProjectionJCheckBox"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("spatialProjectionJCheckBox"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("spatialProjectionJCheckBox"); */
		/* aDescriptor.setShortDescription("spatialProjectionJCheckBox"); */
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
 * Gets the user2DPreference property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor user2DPreferencePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the user2DPreference property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getUser2DPreference", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getUser2DPreference", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setUser2DPreference", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setUser2DPreference", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("user2DPreference"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("user2DPreference"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("user2DPreference"); */
		/* aDescriptor.setShortDescription("user2DPreference"); */
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
