package org.vcell.rest.admin;

import cbit.vcell.modeldb.ApiAccessToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.vcell.rest.VCellApiApplication;
import org.vcell.rest.rpc.RpcRestlet;
import org.vcell.rest.server.RestDatabaseService;
import org.vcell.util.document.SpecialUser;
import org.vcell.util.document.User;

import java.util.Arrays;

public class AdminStatsRestlet extends Restlet {
    private static Logger lg = LogManager.getLogger(RpcRestlet.class);
    private RestDatabaseService restDatabaseService;

    public AdminStatsRestlet(Context context, RestDatabaseService restDatabaseService) {
        super(context);
        this.restDatabaseService = restDatabaseService;
    }

    @Override
    public void handle(Request req, Response response) {
        if (req.getMethod().equals(Method.GET)) {
            try {
                VCellApiApplication application = ((VCellApiApplication) getApplication());
                ApiAccessToken token = application.getApiAccessToken(req.getChallengeResponse(), VCellApiApplication.AuthenticationPolicy.ignoreInvalidCredentials);
                String loginUrl = "/" + VCellApiApplication.LOGINFORM +
                        "?" + VCellApiApplication.REDIRECTURL_FORMNAME +
                        "=" + Reference.encode(req.getResourceRef().toUrl().toString());
                String logoutUrl = "/" + VCellApiApplication.LOGOUT +
                        "?" + VCellApiApplication.REDIRECTURL_FORMNAME +
                        "=" + Reference.encode(req.getResourceRef().toUrl().toString());
                if (token == null) {
                    response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
                    response.setEntity("<h2>must login to access this content, " +
                                    "click <a href=\""+loginUrl+"\"> here </a> to log in</h2>" +
                            "<h2>note that it takes 5-10 seconds to generate the report after login - please be patient</h2>", MediaType.TEXT_HTML);
                    return;
                }
                User user = application.getVCellUser(req.getChallengeResponse(), VCellApiApplication.AuthenticationPolicy.prohibitInvalidCredentials);
                SpecialUser.SPECIAL_CLAIM[] mySpecials = application.getSpecialClaims(token);
                if (mySpecials==null || !Arrays.stream(mySpecials).anyMatch(s -> (s == SpecialUser.SPECIAL_CLAIM.admins))) {
                    response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
                    response.setEntity("<h2>account '"+user.getName()+"' has insufficient privilege</h2>" +
                            "<h2> click <a href=\""+logoutUrl+"\"> here </a> to log out</h2>",
                            MediaType.TEXT_HTML);
                    return;
                }
                String htmlReport = restDatabaseService.getBasicStatistics();
                response.setStatus(Status.SUCCESS_OK);
                response.setEntity(htmlReport, MediaType.TEXT_HTML);
            } catch (Exception e) {
                String errMesg = "<html><body>Error RpcRestlet.handle(...) req='" + req.toString() + "' <br>err='" + e.getMessage() + "'</br>" + "</body></html>";
                getLogger().severe(errMesg);
                lg.error(e.getMessage(), e);
                response.setStatus(Status.SERVER_ERROR_INTERNAL);
                response.setEntity(errMesg, MediaType.TEXT_HTML);
            }
        }
    }
}
