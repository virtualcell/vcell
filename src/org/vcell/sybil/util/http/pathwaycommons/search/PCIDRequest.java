package org.vcell.sybil.util.http.pathwaycommons.search;

/*   PCIDRequest  --- by Oliver Ruebenacker, UCHC --- March 2009
 *   Launch a web request using command get_record_by_cpathID from Pathway Commons
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.vcell.sybil.rdf.JenaIOUtil;
import org.vcell.sybil.util.http.pathwaycommons.PCExceptionResponse;
import org.vcell.sybil.util.http.pathwaycommons.PCRParameter;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;
import org.vcell.sybil.util.text.StringUtil;

public class PCIDRequest extends PathwayCommonsRequest {
	
	protected String id;
	
	public PCIDRequest(String id)  { 
		super(PCRParameter.CmdVersion.getRecordByCPID); 
		this.id = id;
		paras().add(new PCRParameter.Q(id));
		paras().add(PCRParameter.Output.biopax);
	}

	public PathwayCommonsResponse response() {
		try { 
			URL url = url();
			URLConnection connection = url.openConnection();
			String text = StringUtil.textFromInputStream(connection.getInputStream());
			try { return new PCTextModelResponse(this, text, JenaIOUtil.modelFromText(text)); } 
			catch(Throwable t) { return new PCTextResponse(this, text); }
		} 
		catch (MalformedURLException e) { return new PCExceptionResponse(this, e); } 
		catch (IOException e) { return new PCExceptionResponse(this, e); } 
	}

	public String description() { return "cpath id search for " + id; }
	@Override public String shortTitle() { return "ID " + id; }
	
}
