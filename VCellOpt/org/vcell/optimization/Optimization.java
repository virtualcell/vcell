package org.vcell.optimization;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.vcell.model.analysis.IAnalysisTaskFactory;

import cbit.vcell.modelopt.ParameterEstimationTaskXMLPersistence;

public class Optimization implements BundleActivator {

		/*
		 * (non-Javadoc)
		 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
		 */
		public void start(BundleContext context) throws Exception {
	        System.out.println("Starting org.vcell.optimization.Optimization");
	        Properties props = new Properties();
	        props.put("AnalysisTaskType", ParameterEstimationTaskXMLPersistence.ParameterEstimationTaskTag);
	        context.registerService(IAnalysisTaskFactory.class.getName(), new ParameterEstimationTaskFactoryImpl(), props);
		}

		/*
		 * (non-Javadoc)
		 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
		 */
		public void stop(BundleContext context) throws Exception {
		}

	}
