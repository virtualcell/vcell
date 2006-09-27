package cbit.plot;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * The bean information class for cbit.plot.Plot2D.
 */
public class Plot2DBeanInfo extends java.beans.SimpleBeanInfo {
/**
 * Gets the change event set descriptor.
 * @return java.beans.EventSetDescriptor
 */
public java.beans.EventSetDescriptor changeEventSetDescriptor() {
	java.beans.EventSetDescriptor aDescriptor = null;
	try {
		try {
			/* Try using method descriptors to create the change event set descriptor. */
			java.beans.MethodDescriptor eventMethodDescriptors[] = {
				changestateChanged_javaxswingeventChangeEventMethodEventDescriptor()			};
			java.lang.reflect.Method anAddMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class anAddMethodParameterTypes[] = {
					javax.swing.event.ChangeListener.class
				};
				anAddMethod = getBeanClass().getMethod("addChangeListener", anAddMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				anAddMethod = findMethod(getBeanClass(), "addChangeListener", 1);
			};
			java.lang.reflect.Method aRemoveMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aRemoveMethodParameterTypes[] = {
					javax.swing.event.ChangeListener.class
				};
				aRemoveMethod = getBeanClass().getMethod("removeChangeListener", aRemoveMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aRemoveMethod = findMethod(getBeanClass(), "removeChangeListener", 1);
			};
			aDescriptor = new java.beans.EventSetDescriptor(
						"change", 
						javax.swing.event.ChangeListener.class, 
						eventMethodDescriptors, anAddMethod, aRemoveMethod);
		} catch (Throwable exception) {
			/* Using method descriptors failed, try using the methods names. */
			handleException(exception);
			java.lang.String eventMethodNames[] = {
				"stateChanged"			};
			aDescriptor = new java.beans.EventSetDescriptor(getBeanClass(), 
						"change", 
						javax.swing.event.ChangeListener.class, 
						eventMethodNames, 
						"addChangeListener", 
						"removeChangeListener");
		};
		/* aDescriptor.setUnicast(false); */
		/* aDescriptor.setDisplayName("change"); */
		/* aDescriptor.setShortDescription("change"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the change.stateChanged(javax.swing.event.ChangeEvent) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor changestateChanged_javaxswingeventChangeEventMethodEventDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the change.stateChanged(javax.swing.event.ChangeEvent) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				javax.swing.event.ChangeEvent.class
			};
			aMethod = (javax.swing.event.ChangeListener.class).getMethod("stateChanged", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod((javax.swing.event.ChangeListener.class), "stateChanged", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("e");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("stateChanged"); */
		aDescriptor.setShortDescription("Data or settings changed - refresh needed");
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		aDescriptor.setValue("preferred", new Boolean(true));
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the columnTitles property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor columnTitlesPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the columnTitles property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getColumnTitles", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getColumnTitles", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("columnTitles"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("columnTitles"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("columnTitles"); */
		/* aDescriptor.setShortDescription("columnTitles"); */
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
 * Gets the dataValuesByRow property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor dataValuesByRowPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the dataValuesByRow property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getDataValuesByRow", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getDataValuesByRow", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("dataValuesByRow"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("dataValuesByRow"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("dataValuesByRow"); */
		/* aDescriptor.setShortDescription("dataValuesByRow"); */
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
	return cbit.plot.Plot2D.class;
}
/**
 * Gets the bean class name.
 * @return java.lang.String
 */
public static java.lang.String getBeanClassName() {
	return "cbit.plot.Plot2D";
}
public java.beans.BeanDescriptor getBeanDescriptor() {
	java.beans.BeanDescriptor aDescriptor = null;
	try {
		/* Create and return the Plot2DBeanInfo bean descriptor. */
		aDescriptor = new java.beans.BeanDescriptor(cbit.plot.Plot2D.class);
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
		java.beans.EventSetDescriptor aDescriptorList[] = {
			changeEventSetDescriptor()
		};
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
		java.beans.MethodDescriptor aDescriptorList[] = {
			getNumberOfPlotsMethodDescriptor()
			,getNumberOfVisiblePlotsMethodDescriptor()
			,getPlotDatasMethodDescriptor()
			,getPlotNamesMethodDescriptor()
			,getTitleMethodDescriptor()
			,getXDataRangeMethodDescriptor()
			,getXLabelMethodDescriptor()
			,getYDataRangeMethodDescriptor()
			,getYLabelMethodDescriptor()
			,isVisiblePlot_intMethodDescriptor()
			,setVisiblePlots_boolean__MethodDescriptor()
			,setVisiblePlots_int_booleanMethodDescriptor()
		};
		return aDescriptorList;
	} catch (Throwable exception) {
		handleException(exception);
	};
	return null;
}
/**
 * Gets the getNumberOfPlots() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getNumberOfPlotsMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getNumberOfPlots() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getNumberOfPlots", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getNumberOfPlots", 0);
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
		/* aDescriptor.setDisplayName("getNumberOfPlots()"); */
		/* aDescriptor.setShortDescription("getNumberOfPlots()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the getNumberOfVisiblePlots() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getNumberOfVisiblePlotsMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getNumberOfVisiblePlots() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getNumberOfVisiblePlots", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getNumberOfVisiblePlots", 0);
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
		/* aDescriptor.setDisplayName("getNumberOfVisiblePlots()"); */
		/* aDescriptor.setShortDescription("getNumberOfVisiblePlots()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the getPlotDatas() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getPlotDatasMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getPlotDatas() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getPlotDatas", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getPlotDatas", 0);
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
		/* aDescriptor.setDisplayName("getPlotDatas()"); */
		/* aDescriptor.setShortDescription("getPlotDatas()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the getPlotNames() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getPlotNamesMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getPlotNames() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getPlotNames", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getPlotNames", 0);
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
		/* aDescriptor.setDisplayName("getPlotNames()"); */
		/* aDescriptor.setShortDescription("getPlotNames()"); */
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
			columnTitlesPropertyDescriptor()
			,dataValuesByRowPropertyDescriptor()
			,maxPlotDataSizePropertyDescriptor()
			,numberOfPlotsPropertyDescriptor()
			,numberOfVisiblePlotsPropertyDescriptor()
			,plotDatasPropertyDescriptor()
			,plotNamesPropertyDescriptor()
			,titlePropertyDescriptor()
			,visiblePlotColumnTitlesPropertyDescriptor()
			,visiblePlotDataValuesByRowPropertyDescriptor()
			,visiblePlotIndicesPropertyDescriptor()
			,visiblePlotsPropertyDescriptor()
			,XDataRangePropertyDescriptor()
			,XLabelPropertyDescriptor()
			,YDataRangePropertyDescriptor()
			,YLabelPropertyDescriptor()
		};
		return aDescriptorList;
	} catch (Throwable exception) {
		handleException(exception);
	};
	return null;
}
/**
 * Gets the getTitle() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getTitleMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getTitle() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getTitle", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getTitle", 0);
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
		/* aDescriptor.setDisplayName("getTitle()"); */
		/* aDescriptor.setShortDescription("getTitle()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the getXDataRange() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getXDataRangeMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getXDataRange() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getXDataRange", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getXDataRange", 0);
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
		/* aDescriptor.setDisplayName("getXDataRange()"); */
		/* aDescriptor.setShortDescription("getXDataRange()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the getXLabel() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getXLabelMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getXLabel() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getXLabel", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getXLabel", 0);
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
		/* aDescriptor.setDisplayName("getXLabel()"); */
		/* aDescriptor.setShortDescription("getXLabel()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the getYDataRange() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getYDataRangeMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getYDataRange() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getYDataRange", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getYDataRange", 0);
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
		/* aDescriptor.setDisplayName("getYDataRange()"); */
		/* aDescriptor.setShortDescription("getYDataRange()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the getYLabel() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getYLabelMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getYLabel() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getYLabel", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getYLabel", 0);
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
		/* aDescriptor.setDisplayName("getYLabel()"); */
		/* aDescriptor.setShortDescription("getYLabel()"); */
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
 * Gets the isVisiblePlot(int) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor isVisiblePlot_intMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the isVisiblePlot(int) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				int.class
			};
			aMethod = getBeanClass().getMethod("isVisiblePlot", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "isVisiblePlot", 1);
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
		/* aDescriptor.setDisplayName("isVisiblePlot(int)"); */
		/* aDescriptor.setShortDescription("isVisiblePlot(int)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the maxPlotDataSize property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor maxPlotDataSizePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the maxPlotDataSize property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getMaxPlotDataSize", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getMaxPlotDataSize", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("maxPlotDataSize"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("maxPlotDataSize"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("maxPlotDataSize"); */
		/* aDescriptor.setShortDescription("maxPlotDataSize"); */
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
 * Gets the numberOfPlots property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor numberOfPlotsPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the numberOfPlots property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getNumberOfPlots", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getNumberOfPlots", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("numberOfPlots"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("numberOfPlots"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("numberOfPlots"); */
		/* aDescriptor.setShortDescription("numberOfPlots"); */
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
 * Gets the numberOfVisiblePlots property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor numberOfVisiblePlotsPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the numberOfVisiblePlots property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getNumberOfVisiblePlots", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getNumberOfVisiblePlots", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("numberOfVisiblePlots"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("numberOfVisiblePlots"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("numberOfVisiblePlots"); */
		/* aDescriptor.setShortDescription("numberOfVisiblePlots"); */
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
 * Gets the plotDatas property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor plotDatasPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the plotDatas property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getPlotDatas", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getPlotDatas", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("plotDatas"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("plotDatas"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("plotDatas"); */
		/* aDescriptor.setShortDescription("plotDatas"); */
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
 * Gets the plotNames property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor plotNamesPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the plotNames property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getPlotNames", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getPlotNames", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("plotNames"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("plotNames"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("plotNames"); */
		/* aDescriptor.setShortDescription("plotNames"); */
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
 * Gets the setVisiblePlots(boolean[]) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor setVisiblePlots_boolean__MethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the setVisiblePlots(boolean[]) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				boolean[].class
			};
			aMethod = getBeanClass().getMethod("setVisiblePlots", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "setVisiblePlots", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("visiblePlots");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("setVisiblePlots(boolean[])"); */
		/* aDescriptor.setShortDescription("setVisiblePlots(boolean[])"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the setVisiblePlots(int, boolean) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor setVisiblePlots_int_booleanMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the setVisiblePlots(int, boolean) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				int.class,
				boolean.class
			};
			aMethod = getBeanClass().getMethod("setVisiblePlots", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "setVisiblePlots", 2);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("index");
			java.beans.ParameterDescriptor aParameterDescriptor2 = new java.beans.ParameterDescriptor();
			aParameterDescriptor2.setName("arg2");
			aParameterDescriptor2.setDisplayName("visiblePlots");
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
		/* aDescriptor.setDisplayName("setVisiblePlots(int, boolean)"); */
		/* aDescriptor.setShortDescription("setVisiblePlots(int, boolean)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
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
		aDescriptor.setValue("preferred", new Boolean(true));
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the visiblePlotColumnTitles property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor visiblePlotColumnTitlesPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the visiblePlotColumnTitles property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getVisiblePlotColumnTitles", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getVisiblePlotColumnTitles", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("visiblePlotColumnTitles"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("visiblePlotColumnTitles"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("visiblePlotColumnTitles"); */
		/* aDescriptor.setShortDescription("visiblePlotColumnTitles"); */
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
 * Gets the visiblePlotDataValuesByRow property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor visiblePlotDataValuesByRowPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the visiblePlotDataValuesByRow property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getVisiblePlotDataValuesByRow", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getVisiblePlotDataValuesByRow", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("visiblePlotDataValuesByRow"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("visiblePlotDataValuesByRow"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("visiblePlotDataValuesByRow"); */
		/* aDescriptor.setShortDescription("visiblePlotDataValuesByRow"); */
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
 * Gets the visiblePlotIndices property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor visiblePlotIndicesPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the visiblePlotIndices property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getVisiblePlotIndices", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getVisiblePlotIndices", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("visiblePlotIndices"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("visiblePlotIndices"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("visiblePlotIndices"); */
		/* aDescriptor.setShortDescription("visiblePlotIndices"); */
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
 * Gets the visiblePlots property descriptor.
 * @return java.beans.IndexedPropertyDescriptor
 */
public java.beans.IndexedPropertyDescriptor visiblePlotsPropertyDescriptor() {
	java.beans.IndexedPropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the visiblePlots property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean[].class
				};
				aSetMethod = getBeanClass().getMethod("setVisiblePlots", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setVisiblePlots", 1);
			};
			java.lang.reflect.Method aGetIndexedMethod = null;
			java.lang.reflect.Method aSetIndexedMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetIndexedMethodParameterTypes[] = {
					int.class,
					boolean.class
				};
				aSetIndexedMethod = getBeanClass().getMethod("setVisiblePlots", aSetIndexedMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetIndexedMethod = findMethod(getBeanClass(), "setVisiblePlots", 2);
			};
			aDescriptor = new java.beans.IndexedPropertyDescriptor("visiblePlots"
			, aGetMethod, aSetMethod, aGetIndexedMethod, aSetIndexedMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.IndexedPropertyDescriptor("visiblePlots"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("visiblePlots"); */
		/* aDescriptor.setShortDescription("visiblePlots"); */
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
 * Gets the XDataRange property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor XDataRangePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the XDataRange property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getXDataRange", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getXDataRange", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("XDataRange"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("XDataRange"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("XDataRange"); */
		/* aDescriptor.setShortDescription("XDataRange"); */
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
 * Gets the XLabel property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor XLabelPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the XLabel property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getXLabel", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getXLabel", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("XLabel"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("XLabel"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("XLabel"); */
		/* aDescriptor.setShortDescription("XLabel"); */
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
 * Gets the YDataRange property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor YDataRangePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the YDataRange property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getYDataRange", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getYDataRange", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("YDataRange"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("YDataRange"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("YDataRange"); */
		/* aDescriptor.setShortDescription("YDataRange"); */
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
 * Gets the YLabel property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor YLabelPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the YLabel property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getYLabel", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getYLabel", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("YLabel"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("YLabel"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("YLabel"); */
		/* aDescriptor.setShortDescription("YLabel"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		aDescriptor.setValue("preferred", new Boolean(true));
		/* aDescriptor.setValue("ivjDesignTimeProperty", new Boolean(true)); */
		aDescriptor.setValue("enumerationValues", new Object[] {
		});
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
}
