package cbit.gui;
/**
 * The bean information class for cbit.util.ProgressDialog.
 */
public class ProgressDialogBeanInfo extends java.beans.SimpleBeanInfo {
/**
 * Gets the addProgressDialogListener(cbit.util.ProgressDialogListener) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor addProgressDialogListener_cbitutilProgressDialogListenerMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the addProgressDialogListener(cbit.util.ProgressDialogListener) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.gui.ProgressDialogListener.class
			};
			aMethod = getBeanClass().getMethod("addProgressDialogListener", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "addProgressDialogListener", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("newListener");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("addProgressDialogListener(cbit.util.ProgressDialogListener)"); */
		/* aDescriptor.setShortDescription("addProgressDialogListener(cbit.util.ProgressDialogListener)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the cancelButton_actionPerformed(java.awt.event.ActionEvent) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor cancelButton_actionPerformed_javaawteventActionEventMethodEventDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the cancelButton_actionPerformed(java.awt.event.ActionEvent) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				java.util.EventObject.class
			};
			aMethod = (cbit.gui.ProgressDialogListener.class).getMethod("cancelButton_actionPerformed", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod((cbit.gui.ProgressDialogListener.class), "cancelButton_actionPerformed", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("newEvent");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		aDescriptor.setDisplayName("cancelButton_actionPerformed(java.awt.event.ActionEvent)");
		/* aDescriptor.setShortDescription("cancelButton_actionPerformed(java.awt.event.ActionEvent)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the cancelButtonVisible property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor cancelButtonVisiblePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the cancelButtonVisible property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getCancelButtonVisible", aGetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getCancelButtonVisible", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aSetMethodParameterTypes[] = {
					boolean.class
				};
				aSetMethod = getBeanClass().getMethod("setCancelButtonVisible", aSetMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aSetMethod = findMethod(getBeanClass(), "setCancelButtonVisible", 1);
			};
			aDescriptor = new java.beans.PropertyDescriptor("cancelButtonVisible"
			, aGetMethod, aSetMethod);
		} catch (Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("cancelButtonVisible"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("cancelButtonVisible"); */
		/* aDescriptor.setShortDescription("cancelButtonVisible"); */
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
	return cbit.gui.ProgressDialog.class;
}


/**
 * Gets the bean class name.
 * @return java.lang.String
 */
public static java.lang.String getBeanClassName() {
	return "cbit.util.ProgressDialog";
}


public java.beans.BeanDescriptor getBeanDescriptor() {
	java.beans.BeanDescriptor aDescriptor = null;
	try {
		/* Create and return the ProgressDialogBeanInfo bean descriptor. */
		aDescriptor = new java.beans.BeanDescriptor(cbit.gui.ProgressDialog.class);
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
			progressDialogEventSetDescriptor()
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
			addProgressDialogListener_cbitutilProgressDialogListenerMethodDescriptor()
			,main_javalangString__MethodDescriptor()
			,removeProgressDialogListener_cbitutilProgressDialogListenerMethodDescriptor()
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
			cancelButtonVisiblePropertyDescriptor()
			,componentOrientationPropertyDescriptor()
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
 * Gets the progressDialog.cancelButton_actionPerformed(java.util.EventObject) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor progressDialogcancelButton_actionPerformed_javautilEventObjectMethodEventDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the progressDialog.cancelButton_actionPerformed(java.util.EventObject) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				java.util.EventObject.class
			};
			aMethod = (cbit.gui.ProgressDialogListener.class).getMethod("cancelButton_actionPerformed", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod((cbit.gui.ProgressDialogListener.class), "cancelButton_actionPerformed", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("newEvent");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		aDescriptor.setDisplayName("cancelButton_actionPerformed(java.awt.event.ActionEvent)");
		/* aDescriptor.setShortDescription("cancelButton_actionPerformed(java.awt.event.ActionEvent)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the progressDialog event set descriptor.
 * @return java.beans.EventSetDescriptor
 */
public java.beans.EventSetDescriptor progressDialogEventSetDescriptor() {
	java.beans.EventSetDescriptor aDescriptor = null;
	try {
		try {
			/* Try using method descriptors to create the progressDialog event set descriptor. */
			java.beans.MethodDescriptor eventMethodDescriptors[] = {
				progressDialogcancelButton_actionPerformed_javautilEventObjectMethodEventDescriptor()			};
			java.lang.reflect.Method anAddMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class anAddMethodParameterTypes[] = {
					cbit.gui.ProgressDialogListener.class
				};
				anAddMethod = getBeanClass().getMethod("addProgressDialogListener", anAddMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				anAddMethod = findMethod(getBeanClass(), "addProgressDialogListener", 1);
			};
			java.lang.reflect.Method aRemoveMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aRemoveMethodParameterTypes[] = {
					cbit.gui.ProgressDialogListener.class
				};
				aRemoveMethod = getBeanClass().getMethod("removeProgressDialogListener", aRemoveMethodParameterTypes);
			} catch (Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aRemoveMethod = findMethod(getBeanClass(), "removeProgressDialogListener", 1);
			};
			aDescriptor = new java.beans.EventSetDescriptor(
						"progressDialog", 
						cbit.gui.ProgressDialogListener.class, 
						eventMethodDescriptors, anAddMethod, aRemoveMethod);
		} catch (Throwable exception) {
			/* Using method descriptors failed, try using the methods names. */
			handleException(exception);
			java.lang.String eventMethodNames[] = {
				"cancelButton_actionPerformed"			};
			aDescriptor = new java.beans.EventSetDescriptor(getBeanClass(), 
						"progressDialog", 
						cbit.gui.ProgressDialogListener.class, 
						eventMethodNames, 
						"addProgressDialogListener", 
						"removeProgressDialogListener");
		};
		/* aDescriptor.setUnicast(false); */
		/* aDescriptor.setDisplayName("progressDialog"); */
		/* aDescriptor.setShortDescription("progressDialog"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}


/**
 * Gets the removeProgressDialogListener(cbit.util.ProgressDialogListener) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor removeProgressDialogListener_cbitutilProgressDialogListenerMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the removeProgressDialogListener(cbit.util.ProgressDialogListener) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.gui.ProgressDialogListener.class
			};
			aMethod = getBeanClass().getMethod("removeProgressDialogListener", aParameterTypes);
		} catch (Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "removeProgressDialogListener", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("newListener");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("removeProgressDialogListener(cbit.util.ProgressDialogListener)"); */
		/* aDescriptor.setShortDescription("removeProgressDialogListener(cbit.util.ProgressDialogListener)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new Boolean(false)); */
	} catch (Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
}