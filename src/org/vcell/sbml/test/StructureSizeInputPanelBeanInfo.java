package org.vcell.sbml.test;
/**
 * The bean information class for cbit.vcell.vcml.StructureSizeInputPanel.
 */
public class StructureSizeInputPanelBeanInfo extends java.beans.SimpleBeanInfo {
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
	return org.vcell.sbml.test.StructureSizeInputPanel.class;
}


/**
 * Gets the bean class name.
 * @return java.lang.String
 */
public static java.lang.String getBeanClassName() {
	return "cbit.vcell.vcml.StructureSizeInputPanel";
}


public java.beans.BeanDescriptor getBeanDescriptor() {
	java.beans.BeanDescriptor aDescriptor = null;
	try {
		/* Create and return the StructureSizeInputPanelBeanInfo bean descriptor. */
		aDescriptor = new java.beans.BeanDescriptor(org.vcell.sbml.test.StructureSizeInputPanel.class);
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
		java.beans.MethodDescriptor aDescriptorList[] = {
			getStructures_intMethodDescriptor()
			,main_javalangString__MethodDescriptor()
			,setStructures_int_cbitvcellmodelStructureMethodDescriptor()
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
			componentOrientationPropertyDescriptor()
			,selectedStructureNamePropertyDescriptor()
			,structureSizePropertyDescriptor()
			,structuresPropertyDescriptor()
		};
		return aDescriptorList;
	} catch (Throwable exception) {
		handleException(exception);
	};
	return null;
}


/**
 * Gets the getStructures(int) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getStructures_intMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getStructures(int) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				int.class
			};
			aMethod = getBeanClass().getMethod("getStructures", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getStructures", 1);
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
		/* aDescriptor.setDisplayName("getStructures(int)"); */
		/* aDescriptor.setShortDescription("getStructures(int)"); */
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
		} catch (Throwable exception) {
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
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("main(java.lang.String[])"); */
		/* aDescriptor.setShortDescription("main(java.lang.String[])"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the selectedStructureName property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor selectedStructureNamePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the selectedStructureName property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getSelectedStructureName", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getSelectedStructureName", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					java.lang.String.class
				};
				aSetMethod = getBeanClass().getMethod("setSelectedStructureName", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setSelectedStructureName", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("selectedStructureName"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("selectedStructureName"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("selectedStructureName"); */
		/* aDescriptor.setShortDescription("selectedStructureName"); */
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
 * Gets the setStructures(int, cbit.vcell.model.Structure) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor setStructures_int_cbitvcellmodelStructureMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the setStructures(int, cbit.vcell.model.Structure) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				int.class,
				cbit.vcell.model.Structure.class
			};
			aMethod = getBeanClass().getMethod("setStructures", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "setStructures", 2);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("index");
			java.beans.ParameterDescriptor aParameterDescriptor2 = new java.beans.ParameterDescriptor();
			aParameterDescriptor2.setName("arg2");
			aParameterDescriptor2.setDisplayName("structures");
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
		/* aDescriptor.setDisplayName("setStructures(int, cbit.vcell.model.Structure)"); */
		/* aDescriptor.setShortDescription("setStructures(int, cbit.vcell.model.Structure)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the structureSize property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor structureSizePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the structureSize property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getStructureSize", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getStructureSize", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					double.class
				};
				aSetMethod = getBeanClass().getMethod("setStructureSize", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setStructureSize", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("structureSize"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("structureSize"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("structureSize"); */
		/* aDescriptor.setShortDescription("structureSize"); */
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
 * Gets the structures property descriptor.
 * @return java.beans.IndexedPropertyDescriptor
 */
public java.beans.IndexedPropertyDescriptor structuresPropertyDescriptor() {
	java.beans.IndexedPropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the structures property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getStructures", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getStructures", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					cbit.vcell.model.Structure[].class
				};
				aSetMethod = getBeanClass().getMethod("setStructures", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setStructures", 1);
			};
			java.lang.reflect.Method aGetIndexedMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetIndexedMethodParameterTypes[] = {
					int.class
				};
				aGetIndexedMethod = getBeanClass().getMethod("getStructures", aGetIndexedMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetIndexedMethod = findMethod(getBeanClass(), "getStructures", 1);
			};
			java.lang.reflect.Method aSetIndexedMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetIndexedMethodParameterTypes[] = {
					int.class,
					cbit.vcell.model.Structure.class
				};
				aSetIndexedMethod = getBeanClass().getMethod("setStructures", aSetIndexedMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetIndexedMethod = findMethod(getBeanClass(), "setStructures", 2);
			};
			aDescriptor = new java.beans.IndexedPropertyDescriptor("structures"
			, aGetMethod, aSetMethod, aGetIndexedMethod, aSetIndexedMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.IndexedPropertyDescriptor("structures"
			, getBeanClass());
		};
		aDescriptor.setBound(true);
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("structures"); */
		/* aDescriptor.setShortDescription("structures"); */
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