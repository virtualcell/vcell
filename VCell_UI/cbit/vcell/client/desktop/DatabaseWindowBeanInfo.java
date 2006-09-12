package cbit.vcell.client.desktop;
/**
 * The bean information class for cbit.vcell.client.desktop.DatabaseWindow.
 */
public class DatabaseWindowBeanInfo extends java.beans.SimpleBeanInfo {
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
			} catch (java.lang.Throwable exception) {
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
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setComponentOrientation", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("componentOrientation"
			, aGetMethod, aSetMethod);
		} catch (java.lang.Throwable exception) {
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
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new java.lang.Boolean(true)); */
		aDescriptor.setValue("enumerationValues", new java.lang.Object[] {
				"UNKNOWN",java.awt.ComponentOrientation.UNKNOWN,"java.awt.ComponentOrientation.UNKNOWN",
				"LEFT_TO_RIGHT",java.awt.ComponentOrientation.LEFT_TO_RIGHT,"java.awt.ComponentOrientation.LEFT_TO_RIGHT",
				"RIGHT_TO_LEFT",java.awt.ComponentOrientation.RIGHT_TO_LEFT,"java.awt.ComponentOrientation.RIGHT_TO_LEFT",
		});
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the databaseWindowManager property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor databaseWindowManagerPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the databaseWindowManager property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getDatabaseWindowManager", aGetMethodParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getDatabaseWindowManager", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					cbit.vcell.client.DatabaseWindowManager.class
				};
				aSetMethod = getBeanClass().getMethod("setDatabaseWindowManager", aSetMethodParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setDatabaseWindowManager", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("databaseWindowManager"
			, aGetMethod, aSetMethod);
		} catch (java.lang.Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("databaseWindowManager"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("databaseWindowManager"); */
		/* aDescriptor.setShortDescription("databaseWindowManager"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new java.lang.Boolean(true)); */
		aDescriptor.setValue("enumerationValues", new java.lang.Object[] {
		});
	} catch (java.lang.Throwable exception) {
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
	return cbit.vcell.client.desktop.DatabaseWindow.class;
}


/**
 * Gets the bean class name.
 * @return java.lang.String
 */
public static java.lang.String getBeanClassName() {
	return "cbit.vcell.client.desktop.DatabaseWindow";
}


public java.beans.BeanDescriptor getBeanDescriptor() {
	java.beans.BeanDescriptor aDescriptor = null;
	try {
		/* Create and return the DatabaseWindowBeanInfo bean descriptor. */
		aDescriptor = new java.beans.BeanDescriptor(cbit.vcell.client.desktop.DatabaseWindow.class);
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("hidden-state", Boolean.FALSE); */
	} catch (Throwable exception) {
	};
	return aDescriptor;
}


/**
 * Gets the getDatabaseWindowManager() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getDatabaseWindowManagerMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getDatabaseWindowManager() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getDatabaseWindowManager", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getDatabaseWindowManager", 0);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptors[] = {};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("getDatabaseWindowManager()"); */
		/* aDescriptor.setShortDescription("getDatabaseWindowManager()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
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
	} catch (java.lang.Throwable exception) {
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
			getDatabaseWindowManagerMethodDescriptor()
			,getTopLevelWindowManagerMethodDescriptor()
			,main_javalangString__MethodDescriptor()
			,setDatabaseWindowManager_cbitvcellclientDatabaseWindowManagerMethodDescriptor()
			,setWorkArea_javaawtComponentMethodDescriptor()
			,updateConnectionStatus_cbitvcellclientserverConnectionStatusMethodDescriptor()
			,updateMemoryStatus_long_longMethodDescriptor()
			,updateWhileInitializing_intMethodDescriptor()
		};
		return aDescriptorList;
	} catch (java.lang.Throwable exception) {
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
			componentOrientationPropertyDescriptor()
			,databaseWindowManagerPropertyDescriptor()
			,topLevelWindowManagerPropertyDescriptor()
			,workAreaPropertyDescriptor()
		};
		return aDescriptorList;
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return null;
}


/**
 * Gets the getTopLevelWindowManager() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getTopLevelWindowManagerMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getTopLevelWindowManager() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getTopLevelWindowManager", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getTopLevelWindowManager", 0);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptors[] = {};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("getTopLevelWindowManager()"); */
		/* aDescriptor.setShortDescription("getTopLevelWindowManager()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
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
 * Gets the main(java.lang.String[]) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor main_javalangString__MethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the main(java.lang.String[]) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				java.lang.String[].class
			};
			aMethod = getBeanClass().getMethod("main", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "main", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("args");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("main(java.lang.String[])"); */
		/* aDescriptor.setShortDescription("main(java.lang.String[])"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the setDatabaseWindowManager(cbit.vcell.client.DatabaseWindowManager) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor setDatabaseWindowManager_cbitvcellclientDatabaseWindowManagerMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the setDatabaseWindowManager(cbit.vcell.client.DatabaseWindowManager) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.vcell.client.DatabaseWindowManager.class
			};
			aMethod = getBeanClass().getMethod("setDatabaseWindowManager", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "setDatabaseWindowManager", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("databaseWindowManager");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("setDatabaseWindowManager(cbit.vcell.client.DatabaseWindowManager)"); */
		/* aDescriptor.setShortDescription("setDatabaseWindowManager(cbit.vcell.client.DatabaseWindowManager)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the setWorkArea(java.awt.Component) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor setWorkArea_javaawtComponentMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the setWorkArea(java.awt.Component) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				java.awt.Component.class
			};
			aMethod = getBeanClass().getMethod("setWorkArea", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "setWorkArea", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("c");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("setWorkArea(java.awt.Component)"); */
		/* aDescriptor.setShortDescription("setWorkArea(java.awt.Component)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the topLevelWindowManager property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor topLevelWindowManagerPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the topLevelWindowManager property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getTopLevelWindowManager", aGetMethodParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getTopLevelWindowManager", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("topLevelWindowManager"
			, aGetMethod, aSetMethod);
		} catch (java.lang.Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("topLevelWindowManager"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("topLevelWindowManager"); */
		/* aDescriptor.setShortDescription("topLevelWindowManager"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new java.lang.Boolean(true)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the updateConnectionStatus(cbit.vcell.client.server.ConnectionStatus) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor updateConnectionStatus_cbitvcellclientserverConnectionStatusMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the updateConnectionStatus(cbit.vcell.client.server.ConnectionStatus) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.vcell.client.server.ConnectionStatus.class
			};
			aMethod = getBeanClass().getMethod("updateConnectionStatus", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "updateConnectionStatus", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("connStatus");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("updateConnectionStatus(cbit.vcell.client.server.ConnectionStatus)"); */
		/* aDescriptor.setShortDescription("updateConnectionStatus(cbit.vcell.client.server.ConnectionStatus)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the updateMemoryStatus(long, long) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor updateMemoryStatus_long_longMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the updateMemoryStatus(long, long) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				long.class,
				long.class
			};
			aMethod = getBeanClass().getMethod("updateMemoryStatus", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "updateMemoryStatus", 2);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("freeBytes");
			java.beans.ParameterDescriptor aParameterDescriptor2 = new java.beans.ParameterDescriptor();
			aParameterDescriptor2.setName("arg2");
			aParameterDescriptor2.setDisplayName("totalBytes");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1,
				aParameterDescriptor2
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("updateMemoryStatus(long, long)"); */
		/* aDescriptor.setShortDescription("updateMemoryStatus(long, long)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the updateWhileInitializing(int) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor updateWhileInitializing_intMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the updateWhileInitializing(int) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				int.class
			};
			aMethod = getBeanClass().getMethod("updateWhileInitializing", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "updateWhileInitializing", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("i");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("updateWhileInitializing(int)"); */
		/* aDescriptor.setShortDescription("updateWhileInitializing(int)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the workArea property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor workAreaPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the workArea property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					java.awt.Component.class
				};
				aSetMethod = getBeanClass().getMethod("setWorkArea", aSetMethodParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setWorkArea", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("workArea"
			, aGetMethod, aSetMethod);
		} catch (java.lang.Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("workArea"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("workArea"); */
		/* aDescriptor.setShortDescription("workArea"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
		/* aDescriptor.setValue("ivjDesignTimeProperty", new java.lang.Boolean(true)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
}