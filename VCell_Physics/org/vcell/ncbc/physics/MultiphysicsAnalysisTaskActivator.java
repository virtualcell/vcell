package org.vcell.ncbc.physics;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.vcell.modelapp.analysis.IAnalysisTaskFactory;

public class MultiphysicsAnalysisTaskActivator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("starting "+getClass().getName());
        Properties props = new Properties();
        props.put("AnalysisTaskType", MultiphysicsAnalysisTaskXMLPersistence.MultiphysicsAnalysisTaskTag);
        context.registerService(IAnalysisTaskFactory.class.getName(), new MultiphysicsAnalysisTaskFactory(), props);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
