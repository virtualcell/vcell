package org.vcell.modelapp;

import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.vcell.modelapp.analysis.IAnalysisTaskFactory;

public class Activator implements BundleActivator, ServiceListener {

	private static Activator plugin = null; 
	   private BundleContext m_context = null;
	    public Activator(){
	    	if (plugin!=null){
	    		throw new IllegalStateException("plugin class already exists");
	    	}
	    	plugin = this;
	    }
	    public static Activator getDefault() {
	    	return plugin;
	    }
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

    public IAnalysisTaskFactory[] getAnalysisTaskFactories(String analysisTaskType) {
//		ServiceReference[] analysisServiceRefs;
//		try {
//			analysisServiceRefs = m_context.getServiceReferences(IAnalysisTaskFactory.class.getName(), "(AnalysisTaskType="+analysisTaskType+")");
//		} catch (InvalidSyntaxException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e.getMessage());
//		}
//		if (analysisServiceRefs!=null && analysisServiceRefs.length > 0){
//			Vector<IAnalysisTaskFactory> analysisTaskFactories = new Vector<IAnalysisTaskFactory>();
//			for (int i = 0; i < analysisServiceRefs.length; i++) {
//				 IAnalysisTaskFactory analysisTaskFactory = (IAnalysisTaskFactory) m_context.getService(analysisServiceRefs[i]);
//				 analysisTaskFactories.add(analysisTaskFactory);
//			}
//			return analysisTaskFactories.toArray(new IAnalysisTaskFactory[analysisTaskFactories.size()]);
//		}else{
//			throw new RuntimeException("Plugin error: couldn't find analysis plugin for type '"+analysisTaskType+"'");
//		}
    	Vector<IAnalysisTaskFactory> analysisTaskFactories = new Vector<IAnalysisTaskFactory>();
    	IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint("org.vcell.modelapp","AnalysisTask");
    	IExtension[] extensions = extensionPoint.getExtensions();
    	for (int i = 0; i < extensions.length; i++) {
    		IConfigurationElement[] extensionElements = extensions[i].getConfigurationElements();
			if (extensionElements!=null && extensionElements.length>0){
				try {
					Object object = extensionElements[0].createExecutableExtension("class");
					analysisTaskFactories.add((IAnalysisTaskFactory)object);
				} catch (CoreException e1) {
					e1.printStackTrace();
				}
			}
		}
    	if (analysisTaskFactories.size()>0){
    		return analysisTaskFactories.toArray(new IAnalysisTaskFactory[analysisTaskFactories.size()]);
		}else{
			throw new RuntimeException("Plugin error: couldn't find analysis plugin for type '"+analysisTaskType+"'");
		}
   }
    /**
     * Implements BundleActivator.start(). Adds itself
     * as a listener for service events, then queries for 
     * available dictionary services. If any dictionaries are
     * found it gets a reference to the first one available and
     * then starts its "word checking loop". If no dictionaries
     * are found, then it just goes directly into its "word checking
     * loop", but it will not be able to check any words until a
     * dictionary service arrives; any arriving dictionary service
     * will be automatically used by the client if a dictionary is
     * not already in use. Once it has dictionary, it reads words
     * from standard input and checks for their existence in the
     * dictionary that it is using.
     * (NOTE: It is very bad practice to use the calling thread
     * to perform a lengthy process like this; this is only done
     * for the purpose of the tutorial.)
     * @param context the framework context for the bundle.
    **/
    public void start(BundleContext context) throws Exception
    {
		System.out.println("starting "+getClass().getName());
        m_context = context;

        // Listen for events pertaining to analysisTask services.
        m_context.addServiceListener(this,
            "(&(objectClass=" + IAnalysisTaskFactory.class.getName() + ")" +
            "(AnalysisTaskType=*))");

//        // Query for any service references matching any analysisTask type.
//        ServiceReference[] refs = m_context.getServiceReferences(
//            IAnalysisTaskFactory.class.getName(), "(AnalysisTaskType=*)");
//
//        // If we found any analysis Task services, then just get
//        // a reference to the first one so we can use it.
//        if (refs != null)
//        {
//            m_ref = refs[0];
//            m_analysisTaskFactory = (IAnalysisTaskFactory) m_context.getService(m_ref);
//        }
    }
	public void serviceChanged(ServiceEvent event) {
		// TODO Auto-generated method stub
		
	}

}
