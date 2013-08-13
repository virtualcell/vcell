package org.restlet.example.ext.oauth.server.external;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.Form;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

/**
 * Defines the an example resource that needs authentication.
 * 
 * @author Bret K. Ikehara
 */
public class RestrictedResource extends ServerResource {

  @Override
  protected Representation get() throws ResourceException {
    
    ClientResource cr = new ClientResource("http://localhost:8080/oauth/token_auth");
    
//    Form form = new Form();
//    form.add("access_token", getQueryValue("access_token"));
//    form.add("token_type", getQueryValue("token_type"));

    JSONObject resp = new JSONObject();
    try {
      resp.put("access_token", getQueryValue("access_token"));
      resp.put("token_type", getQueryValue("token_type"));
    }
    catch (JSONException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    
      Representation rep = cr.post(new JsonRepresentation(resp));
      
      try {
        JSONObject json = new JSONObject(rep.getText());
        
        String username = json.getString("username");

        return new StringRepresentation("Authenticated: " + username);
      }
      catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    return new EmptyRepresentation();
  }
}
