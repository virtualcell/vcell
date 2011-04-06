package org.vcell.sybil.util.http.pathwaycommons.search;

/*   PCIDNeighboursRequest  --- by Oliver Ruebenacker, UCHC --- March 2009 to February 2010
 *   Launch a web request using command get_neighbors from Pathway Commons
 */

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.ParserConfigurationException;

import org.vcell.sybil.rdf.JenaIOUtil;
import org.vcell.sybil.util.http.pathwaycommons.PCExceptionResponse;
import org.vcell.sybil.util.http.pathwaycommons.PCRParameter;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;
import org.vcell.sybil.util.text.StringUtil;
import org.xml.sax.SAXException;

public class PCNeighborsRequest extends PathwayCommonsRequest {
	
	protected String id;
	
	public PCNeighborsRequest(int id) { this("" + id); }
	
	public PCNeighborsRequest(String idNew)  { 
		super(PCRParameter.CmdVersion.getNeighbours); 
		id = idNew;
		paras().add(new PCRParameter.Q(id));
		paras().add(PCRParameter.Output.biopax);
	}

	@Override
	public PathwayCommonsResponse response() {
		PathwayCommonsResponse response = null;
		try { 
			URL url = url();
			URLConnection connection = url.openConnection();
			String text = StringUtil.textFromInputStream(connection.getInputStream());
			if(PathwayCommonsUtil.isErrorResponse(text)) {
				response = new PCErrorResponse(this, PCErrorResponse.errorElement(text));
			} else {
				try { 
					response = new PCTextModelResponse(this, text, 
							JenaIOUtil.modelFromText(text, uriBase)); 
				} catch(Throwable t) { response = new PCTextResponse(this, text); }				
			}
		} 
		catch (MalformedURLException e) { response = new PCExceptionResponse(this, e); } 
		catch (IOException e) { response = new PCExceptionResponse(this, e); } 
		catch (SAXException e) { response = new PCExceptionResponse(this, e); } 
		catch (ParserConfigurationException e) { response = new PCExceptionResponse(this, e); } 
		return response;
	}

	@Override
	public String description() { return "neighbor search for " + id; }
	@Override public String shortTitle() { return "neighbors of " + id; }
	
}
