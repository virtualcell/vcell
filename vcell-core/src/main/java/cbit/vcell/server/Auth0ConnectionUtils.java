package cbit.vcell.server;

import cbit.vcell.resource.PropertyLoader;
import com.nimbusds.oauth2.sdk.ParseException;
import org.vcell.api.client.VCellApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.util.document.UserLoginInfo;

import java.io.IOException;
import java.net.URISyntaxException;

public final class Auth0ConnectionUtils {

    private final VCellApiClient vcellApiClient;
    public Auth0ConnectionUtils(VCellApiClient vcellApiClient) {this.vcellApiClient = vcellApiClient;}

    public boolean isVCellIdentityMapped() throws ApiException {
        return vcellApiClient.isVCellIdentityMapped();
    }

    public void mapVCellIdentityToAuth0Identity(UserLoginInfo userLoginInfo) {
        try{
            vcellApiClient.mapUserToAuth0(userLoginInfo.getUserName(), userLoginInfo.getDigestedPassword().getClearTextPassword());
        } catch (ApiException e){
            throw new RuntimeException(e);
        }
    }

    public void auth0SignIn() throws ApiException {
        try{
            boolean bIgnoreHostMismatch = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreHostMismatch, false);
            boolean bIgnoreCertProblems = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreCertProblems, false);
            boolean ignoreSSLCertProblems = bIgnoreCertProblems || bIgnoreHostMismatch;
            vcellApiClient.authenticateWithAuth0(ignoreSSLCertProblems);
        } catch (ApiException | URISyntaxException | IOException | ParseException e){
            throw new RuntimeException(e);
        }
    }

    public String getAuth0MappedUser() throws ApiException {
        try {
            return vcellApiClient.getVCellUserNameFromAuth0Mapping();
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
    }
}
