package org.vcell.model;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceEvent;
import org.vcell.model.analysis.IAnalysisTaskFactory;

import cbit.vcell.client.server.UserPreferences;


/**
 * This class implements a bundle that uses a dictionary
 * service to check for the proper spelling of a word by
 * checking for its existence in the dictionary. This bundle
 * is more complex than the bundle in Example 3 because it
 * monitors the dynamic availability of the dictionary
 * services. In other words, if the service it is using
 * departs, then it stops using it gracefully, or if it needs
 * a service and one arrives, then it starts using it
 * automatically. As before, the bundle uses the first service
 * that it finds and uses the calling thread of the
 * start() method to read words from standard input.
 * You can stop checking words by entering an empty line, but
 * to start checking words again you must stop and then restart
 * the bundle.
**/
public class Activator implements BundleActivator, ServiceListener
{
	
	private static Activator plugin = null; 
    // Bundle's context.
    private BundleContext m_context = null;
    // The service reference being used.
//    private ServiceReference m_ref = null;
    
    private UserPreferences userPreferences = null;
    // The service object being used.
//    private IAnalysisTaskFactory m_analysisTaskFactory = null;
    
    public Activator(){
    	if (plugin!=null){
    		throw new IllegalStateException("plugin class already exists");
    	}
    	plugin = this;
    }

    public static Activator getDefault() {
    	return plugin;
    }
    
    public UserPreferences getUserPreferences(){
    	return userPreferences;
    }
    
    public void setUserPreferences(UserPreferences argUserPreferences){
    	this.userPreferences = argUserPreferences;
    }
    
    public IAnalysisTaskFactory[] getAnalysisTaskFactories(String analysisTaskType) {
		ServiceReference[] analysisServiceRefs;
		try {
			analysisServiceRefs = m_context.getServiceReferences(IAnalysisTaskFactory.class.getName(), "(AnalysisTaskType="+analysisTaskType+")");
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		if (analysisServiceRefs!=null && analysisServiceRefs.length > 0){
			Vector<IAnalysisTaskFactory> analysisTaskFactories = new Vector<IAnalysisTaskFactory>();
			for (int i = 0; i < analysisServiceRefs.length; i++) {
				 IAnalysisTaskFactory analysisTaskFactory = (IAnalysisTaskFactory) m_context.getService(analysisServiceRefs[i]);
				 analysisTaskFactories.add(analysisTaskFactory);
			}
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
        System.out.println("Starting org.vcell.model.Activator");
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

    /**
     * Implements BundleActivator.stop(). Does nothing since
     * the framework will automatically unget any used services.
     * @param context the framework context for the bundle.
    **/
    public void stop(BundleContext context)
    {
        // NOTE: The service is automatically released.
    }

    /**
     * Implements ServiceListener.serviceChanged(). Checks
     * to see if the service we are using is leaving or tries to get
     * a service if we need one.
     * @param event the fired service event.
    **/
    public void serviceChanged(ServiceEvent event)
    {
        String[] objectClass =
            (String[]) event.getServiceReference().getProperty("objectClass");

        // If a dictionary service was registered, see if we
        // need one. If so, get a reference to it.
        if (event.getType() == ServiceEvent.REGISTERED)
        {
//            if (m_ref == null)
//            {
//                // Get a reference to the service object.
//                m_ref = event.getServiceReference();
//                m_analysisTaskFactory = (IAnalysisTaskFactory) m_context.getService(m_ref);
//            }
        }
        // If a dictionary service was unregistered, see if it
        // was the one we were using. If so, unget the service
        // and try to query to get another one.
        else if (event.getType() == ServiceEvent.UNREGISTERING)
        {
//            if (event.getServiceReference() == m_ref)
//            {
//                // Unget service object and null references.
//                m_context.ungetService(m_ref);
//                m_ref = null;
//                m_analysisTaskFactory = null;
//
//                // Query to see if we can get another service.
//                ServiceReference[] refs = null;
//				try {
//					refs = m_context.getServiceReferences(IAnalysisTaskFactory.class.getName(), "(Language=*)");
//				} catch (InvalidSyntaxException e) {
//					e.printStackTrace();
//				}
//                if (refs != null)
//                {
//                    // Get a reference to the first service object.
//                    m_ref = refs[0];
//                    m_analysisTaskFactory = (IAnalysisTaskFactory) m_context.getService(m_ref);
//                }
//            }
        }
    }

}