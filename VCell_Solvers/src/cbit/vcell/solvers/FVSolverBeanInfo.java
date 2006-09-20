package cbit.vcell.solvers;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * The bean information class for cbit.vcell.solvers.FVSolver.
 */
public class FVSolverBeanInfo extends java.beans.SimpleBeanInfo {
/**
 * Gets the addSolverListener(cbit.vcell.solver.SolverListener) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor addSolverListener_cbitvcellsolverSolverListenerMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the addSolverListener(cbit.vcell.solver.SolverListener) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.vcell.solvers.SolverListener.class
			};
			aMethod = getBeanClass().getMethod("addSolverListener", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "addSolverListener", 1);
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
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("addSolverListener(cbit.vcell.solver.SolverListener)"); */
		/* aDescriptor.setShortDescription("addSolverListener(cbit.vcell.solver.SolverListener)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the apropos() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor aproposMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the apropos() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("apropos", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "apropos", 0);
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
		/* aDescriptor.setDisplayName("apropos()"); */
		/* aDescriptor.setShortDescription("apropos()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the currentTime property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor currentTimePropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the currentTime property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getCurrentTime", aGetMethodParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getCurrentTime", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("currentTime"
			, aGetMethod, aSetMethod);
		} catch (java.lang.Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("currentTime"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("currentTime"); */
		/* aDescriptor.setShortDescription("currentTime"); */
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
	return cbit.vcell.solvers.FVSolver.class;
}
/**
 * Gets the bean class name.
 * @return java.lang.String
 */
public static java.lang.String getBeanClassName() {
	return "cbit.vcell.solvers.FVSolver";
}
public java.beans.BeanDescriptor getBeanDescriptor() {
	java.beans.BeanDescriptor aDescriptor = null;
	try {
		/* Create and return the FVSolverBeanInfo bean descriptor. */
		aDescriptor = new java.beans.BeanDescriptor(cbit.vcell.solvers.FVSolver.class);
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("hidden-state", Boolean.FALSE); */
	} catch (Throwable exception) {
	};
	return aDescriptor;
}
/**
 * Gets the getCurrentTime() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getCurrentTimeMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getCurrentTime() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getCurrentTime", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getCurrentTime", 0);
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
		/* aDescriptor.setDisplayName("getCurrentTime()"); */
		/* aDescriptor.setShortDescription("getCurrentTime()"); */
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
		java.beans.EventSetDescriptor aDescriptorList[] = {
			solverEventSetDescriptor()
		};
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
			addSolverListener_cbitvcellsolverSolverListenerMethodDescriptor()
			,aproposMethodDescriptor()
			,getCurrentTimeMethodDescriptor()
			,getSimulationIdentifierMethodDescriptor()
			,getStatusMethodDescriptor()
			,getStatusStringMethodDescriptor()
			,removeSolverListener_cbitvcellsolverSolverListenerMethodDescriptor()
			,runMethodDescriptor()
			,startMethodDescriptor()
			,stopMethodDescriptor()
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
			currentTimePropertyDescriptor()
			,simulationIdentifierPropertyDescriptor()
			,statusPropertyDescriptor()
			,statusStringPropertyDescriptor()
		};
		return aDescriptorList;
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return null;
}
/**
 * Gets the getSimulationIdentifier() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getSimulationIdentifierMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getSimulationIdentifier() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getSimulationIdentifier", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getSimulationIdentifier", 0);
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
		/* aDescriptor.setDisplayName("getSimulationIdentifier()"); */
		/* aDescriptor.setShortDescription("getSimulationIdentifier()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the getStatus() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getStatusMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getStatus() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getStatus", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getStatus", 0);
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
		/* aDescriptor.setDisplayName("getStatus()"); */
		/* aDescriptor.setShortDescription("getStatus()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the getStatusString() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor getStatusStringMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the getStatusString() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("getStatusString", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "getStatusString", 0);
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
		/* aDescriptor.setDisplayName("getStatusString()"); */
		/* aDescriptor.setShortDescription("getStatusString()"); */
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
 * Gets the removeSolverListener(cbit.vcell.solver.SolverListener) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor removeSolverListener_cbitvcellsolverSolverListenerMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the removeSolverListener(cbit.vcell.solver.SolverListener) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.vcell.solvers.SolverListener.class
			};
			aMethod = getBeanClass().getMethod("removeSolverListener", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "removeSolverListener", 1);
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
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("removeSolverListener(cbit.vcell.solver.SolverListener)"); */
		/* aDescriptor.setShortDescription("removeSolverListener(cbit.vcell.solver.SolverListener)"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the run() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor runMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the run() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("run", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "run", 0);
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
		/* aDescriptor.setDisplayName("run()"); */
		/* aDescriptor.setShortDescription("run()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the simulationIdentifier property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor simulationIdentifierPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the simulationIdentifier property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getSimulationIdentifier", aGetMethodParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getSimulationIdentifier", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("simulationIdentifier"
			, aGetMethod, aSetMethod);
		} catch (java.lang.Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("simulationIdentifier"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("simulationIdentifier"); */
		/* aDescriptor.setShortDescription("simulationIdentifier"); */
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
 * Gets the solver event set descriptor.
 * @return java.beans.EventSetDescriptor
 */
public java.beans.EventSetDescriptor solverEventSetDescriptor() {
	java.beans.EventSetDescriptor aDescriptor = null;
	try {
		try {
			/* Try using method descriptors to create the solver event set descriptor. */
			java.beans.MethodDescriptor eventMethodDescriptors[] = {
				solversolverAborted_cbitvcellsolverSolverEventMethodEventDescriptor(),
				solversolverPrinted_cbitvcellsolverSolverEventMethodEventDescriptor(),
				solversolverFinished_cbitvcellsolverSolverEventMethodEventDescriptor(),
				solversolverStarted_cbitvcellsolverSolverEventMethodEventDescriptor(),
				solversolverStepped_cbitvcellsolverSolverEventMethodEventDescriptor(),
				solversolverStopped_cbitvcellsolverSolverEventMethodEventDescriptor()			};
			java.lang.reflect.Method anAddMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class anAddMethodParameterTypes[] = {
					cbit.vcell.solvers.SolverListener.class
				};
				anAddMethod = getBeanClass().getMethod("addSolverListener", anAddMethodParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				anAddMethod = findMethod(getBeanClass(), "addSolverListener", 1);
			};
			java.lang.reflect.Method aRemoveMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aRemoveMethodParameterTypes[] = {
					cbit.vcell.solvers.SolverListener.class
				};
				aRemoveMethod = getBeanClass().getMethod("removeSolverListener", aRemoveMethodParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aRemoveMethod = findMethod(getBeanClass(), "removeSolverListener", 1);
			};
			aDescriptor = new java.beans.EventSetDescriptor(
						"solver", 
						cbit.vcell.solvers.SolverListener.class, 
						eventMethodDescriptors, anAddMethod, aRemoveMethod);
		} catch (java.lang.Throwable exception) {
			/* Using method descriptors failed, try using the methods names. */
			handleException(exception);
			java.lang.String eventMethodNames[] = {
				"solverStarted",
				"solverStopped",
				"solverAborted",
				"solverStepped",
				"solverFinished",
				"solverPrinted"			};
			aDescriptor = new java.beans.EventSetDescriptor(getBeanClass(), 
						"solver", 
						cbit.vcell.solvers.SolverListener.class, 
						eventMethodNames, 
						"addSolverListener", 
						"removeSolverListener");
		};
		/* aDescriptor.setUnicast(false); */
		/* aDescriptor.setDisplayName("solver"); */
		/* aDescriptor.setShortDescription("solver"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the solver.solverAborted(cbit.vcell.solver.SolverEvent) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor solversolverAborted_cbitvcellsolverSolverEventMethodEventDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the solver.solverAborted(cbit.vcell.solver.SolverEvent) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.vcell.solvers.SolverEvent.class
			};
			aMethod = (cbit.vcell.solvers.SolverListener.class).getMethod("solverAborted", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod((cbit.vcell.solvers.SolverListener.class), "solverAborted", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("event");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("solverAborted"); */
		/* aDescriptor.setShortDescription("solverAborted"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the solver.solverFinished(cbit.vcell.solver.SolverEvent) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor solversolverFinished_cbitvcellsolverSolverEventMethodEventDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the solver.solverFinished(cbit.vcell.solver.SolverEvent) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.vcell.solvers.SolverEvent.class
			};
			aMethod = (cbit.vcell.solvers.SolverListener.class).getMethod("solverFinished", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod((cbit.vcell.solvers.SolverListener.class), "solverFinished", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("event");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("solverFinished"); */
		/* aDescriptor.setShortDescription("solverFinished"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the solver.solverPrinted(cbit.vcell.solver.SolverEvent) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor solversolverPrinted_cbitvcellsolverSolverEventMethodEventDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the solver.solverPrinted(cbit.vcell.solver.SolverEvent) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.vcell.solvers.SolverEvent.class
			};
			aMethod = (cbit.vcell.solvers.SolverListener.class).getMethod("solverPrinted", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod((cbit.vcell.solvers.SolverListener.class), "solverPrinted", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("event");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("solverPrinted"); */
		/* aDescriptor.setShortDescription("solverPrinted"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the solver.solverStarted(cbit.vcell.solver.SolverEvent) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor solversolverStarted_cbitvcellsolverSolverEventMethodEventDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the solver.solverStarted(cbit.vcell.solver.SolverEvent) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.vcell.solvers.SolverEvent.class
			};
			aMethod = (cbit.vcell.solvers.SolverListener.class).getMethod("solverStarted", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod((cbit.vcell.solvers.SolverListener.class), "solverStarted", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("event");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("solverStarted"); */
		/* aDescriptor.setShortDescription("solverStarted"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the solver.solverStepped(cbit.vcell.solver.SolverEvent) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor solversolverStepped_cbitvcellsolverSolverEventMethodEventDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the solver.solverStepped(cbit.vcell.solver.SolverEvent) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.vcell.solvers.SolverEvent.class
			};
			aMethod = (cbit.vcell.solvers.SolverListener.class).getMethod("solverStepped", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod((cbit.vcell.solvers.SolverListener.class), "solverStepped", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("event");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("solverStepped"); */
		/* aDescriptor.setShortDescription("solverStepped"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the solver.solverStopped(cbit.vcell.solver.SolverEvent) method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor solversolverStopped_cbitvcellsolverSolverEventMethodEventDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the solver.solverStopped(cbit.vcell.solver.SolverEvent) method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {
				cbit.vcell.solvers.SolverEvent.class
			};
			aMethod = (cbit.vcell.solvers.SolverListener.class).getMethod("solverStopped", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod((cbit.vcell.solvers.SolverListener.class), "solverStopped", 1);
		};
		try {
			/* Try creating the method descriptor with parameter descriptors. */
			java.beans.ParameterDescriptor aParameterDescriptor1 = new java.beans.ParameterDescriptor();
			aParameterDescriptor1.setName("arg1");
			aParameterDescriptor1.setDisplayName("event");
			java.beans.ParameterDescriptor aParameterDescriptors[] = {
				aParameterDescriptor1
			};
			aDescriptor = new java.beans.MethodDescriptor(aMethod, aParameterDescriptors);
		} catch (java.lang.Throwable exception) {
			/* Try creating the method descriptor without parameter descriptors. */
			handleException(exception);
			aDescriptor = new java.beans.MethodDescriptor(aMethod);
		};
		/* aDescriptor.setDisplayName("solverStopped"); */
		/* aDescriptor.setShortDescription("solverStopped"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the start() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor startMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the start() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("start", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "start", 0);
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
		/* aDescriptor.setDisplayName("start()"); */
		/* aDescriptor.setShortDescription("start()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
/**
 * Gets the status property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor statusPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the status property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getStatus", aGetMethodParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getStatus", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("status"
			, aGetMethod, aSetMethod);
		} catch (java.lang.Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("status"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("status"); */
		/* aDescriptor.setShortDescription("status"); */
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
 * Gets the statusString property descriptor.
 * @return java.beans.PropertyDescriptor
 */
public java.beans.PropertyDescriptor statusStringPropertyDescriptor() {
	java.beans.PropertyDescriptor aDescriptor = null;
	try {
		try {
			/* Using methods via getMethod is the faster way to create the statusString property descriptor. */
			java.lang.reflect.Method aGetMethod = null;
			try {
				/* Attempt to find the method using getMethod with parameter types. */
				java.lang.Class aGetMethodParameterTypes[] = {};
				aGetMethod = getBeanClass().getMethod("getStatusString", aGetMethodParameterTypes);
			} catch (java.lang.Throwable exception) {
				/* Since getMethod failed, call findMethod. */
				handleException(exception);
				aGetMethod = findMethod(getBeanClass(), "getStatusString", 0);
			};
			java.lang.reflect.Method aSetMethod = null;
			aDescriptor = new java.beans.PropertyDescriptor("statusString"
			, aGetMethod, aSetMethod);
		} catch (java.lang.Throwable exception) {
			/* Since we failed using methods, try creating a default property descriptor. */
			handleException(exception);
			aDescriptor = new java.beans.PropertyDescriptor("statusString"
			, getBeanClass());
		};
		/* aDescriptor.setBound(false); */
		/* aDescriptor.setConstrained(false); */
		/* aDescriptor.setDisplayName("statusString"); */
		/* aDescriptor.setShortDescription("statusString"); */
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
 * Gets the stop() method descriptor.
 * @return java.beans.MethodDescriptor
 */
public java.beans.MethodDescriptor stopMethodDescriptor() {
	java.beans.MethodDescriptor aDescriptor = null;
	try {
		/* Create and return the stop() method descriptor. */
		java.lang.reflect.Method aMethod = null;
		try {
			/* Attempt to find the method using getMethod with parameter types. */
			java.lang.Class aParameterTypes[] = {};
			aMethod = getBeanClass().getMethod("stop", aParameterTypes);
		} catch (java.lang.Throwable exception) {
			/* Since getMethod failed, call findMethod. */
			handleException(exception);
			aMethod = findMethod(getBeanClass(), "stop", 0);
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
		/* aDescriptor.setDisplayName("stop()"); */
		/* aDescriptor.setShortDescription("stop()"); */
		/* aDescriptor.setExpert(false); */
		/* aDescriptor.setHidden(false); */
		/* aDescriptor.setValue("preferred", new java.lang.Boolean(false)); */
	} catch (java.lang.Throwable exception) {
		handleException(exception);
	};
	return aDescriptor;
}
}
