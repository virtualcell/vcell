package org.restlet.example.ext.oauth.server.external;

import java.util.HashMap;
import java.util.Map;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.ContextTemplateLoader;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import freemarker.template.Configuration;

public class IndexResource extends ServerResource {

  public static final String REDIRECT_URL = "http://localhost:8080/sample/popup";

  @Get("text/html")
  public Representation get() {

    Configuration config = new Configuration();
    config.setTemplateLoader(new ContextTemplateLoader(getContext(), "clap://system/external/"));

    Map<String, String> map = new HashMap<String, String>();
    String url =
        String
            .format(
                "http://localhost:8080/oauth/authorize?client_id=%s&response_type=%s&scope=%s&redirect_uri=%s",
                ExternalApplication.clientID, "code", "default", REDIRECT_URL);

    TemplateRepresentation rep =
        new TemplateRepresentation("index.html", config, MediaType.TEXT_HTML);

    map.put("url", url);
    map.put("authenticated", "no");
    rep.setDataModel((Object) map);

    return rep;
  }
}