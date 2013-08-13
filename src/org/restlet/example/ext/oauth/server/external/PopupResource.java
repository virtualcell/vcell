package org.restlet.example.ext.oauth.server.external;

import java.util.logging.Level;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.ContextTemplateLoader;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.util.Series;
import freemarker.template.Configuration;

public class PopupResource extends ServerResource {

  public static final String REDIRECT_URL = "http://localhost:8080/sample/popup";

  @Get("text/html")
  public Representation get() {

    String code = getQueryValue("code");
    String content = null;
    try {
      ClientResource cr = new ClientResource("http://localhost:8080/oauth/token");

      cr.setChallengeResponse(ChallengeScheme.HTTP_BASIC, ExternalApplication.clientID,
          ExternalApplication.clientSecret);

      Form form = new Form();
      form.add("client_id", ExternalApplication.clientID);
      form.add("client_secret", ExternalApplication.clientSecret);
      form.add("grant_type", "authorization_code");
      form.add("redirect_uri", REDIRECT_URL);
      form.add("code", code);
      String params = form.encode();

      getLogger().log(Level.INFO, "/oauth/token encoded query: " + params);

      Representation rep = cr.post(form, MediaType.APPLICATION_WWW_FORM);

      content = rep.getText();
      getLogger().log(Level.INFO, content);
    }
    catch (Exception e) {
      getLogger().log(Level.WARNING, "Failed to get access_token: " + e.getMessage(), e);
      content = "";
    }

    getLogger().log(Level.INFO, "response: " + content);

    Series<CookieSetting> cs = getResponse().getCookieSettings();
    try {
      JSONObject json = new JSONObject(content);
      cs.add(0, new CookieSetting("token_type", json.getString("token_type")));
      cs.add(1, new CookieSetting("access_token", json.getString("access_token")));
      cs.add(2, new CookieSetting("refresh_token", json.getString("refresh_token")));
      setStatus(Status.SUCCESS_OK);
    }
    catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    Configuration config = new Configuration();
    config.setTemplateLoader(new ContextTemplateLoader(getContext(), "clap://system/external/"));
    TemplateRepresentation rep =
        new TemplateRepresentation("popup.html", config, MediaType.TEXT_HTML);
    return rep;
  }
}