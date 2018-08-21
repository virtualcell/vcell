package org.vcell.restopt;

import org.restlet.Request;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.ext.wadl.ApplicationInfo;
import org.restlet.ext.wadl.WadlApplication;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.routing.Router;
import org.vcell.optimization.OptServerImpl;
import org.vcell.restopt.server.OptimizationRunServerResource;
import org.vcell.restopt.server.OptimizationServerResource;

public class VCellOptApplication extends WadlApplication {
		
	public static final String OPTIMIZATION = "optimization";
	public static final String RUNOPTIMIZATION = "run";
	public static final String OPTIMIZATIONID = "optimizationid";
			
	private OptServerImpl optServerImpl = null;
	
	@Override
	protected Variant getPreferredWadlVariant(Request request) {
		return new Variant(MediaType.APPLICATION_WADL);
	}
	
	public OptServerImpl getOptServerImpl() {
		return optServerImpl;
	}
	
	@Override
	protected Representation createHtmlRepresentation(ApplicationInfo applicationInfo) {
		return super.createHtmlRepresentation(applicationInfo);
	}

	public VCellOptApplication( OptServerImpl optServerImpl) {
        setName("RESTful VCell OPT application");
        setDescription("Optimization API");
        setOwner("VCell Project/UCHC");
        setAuthor("VCell Team");
		setStatusService(new VCellStatusService());
		this.optServerImpl = optServerImpl;
	}
	

	@Override  
    public Restlet createInboundRoot() {  
    	
	    System.setProperty("java.net.preferIPv4Stack", "true");
	    
		Router rootRouter = new Router(getContext());
	    rootRouter.attach("/"+OPTIMIZATION, OptimizationRunServerResource.class);
	    rootRouter.attach("/"+OPTIMIZATION+"/{"+OPTIMIZATIONID+"}", OptimizationServerResource.class);
	    
	    return rootRouter;
    }  
	

}
