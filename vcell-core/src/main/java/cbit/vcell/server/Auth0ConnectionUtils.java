package cbit.vcell.server;

import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.resource.ResourceUtil;
import com.google.gson.Gson;
import com.nimbusds.oauth2.sdk.ParseException;
import org.vcell.api.client.VCellApiClient;
import org.vcell.restclient.ApiException;
import org.vcell.util.document.UserLoginInfo;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public final class Auth0ConnectionUtils {
    private static String showLoginPopUpIndexString = "showLoginPopUp";

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

    public void auth0SignIn(boolean isGuest) throws ApiException {
        try{
            boolean bIgnoreHostMismatch = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreHostMismatch, false);
            boolean bIgnoreCertProblems = PropertyLoader.getBooleanProperty(PropertyLoader.sslIgnoreCertProblems, false);
            boolean ignoreSSLCertProblems = bIgnoreCertProblems || bIgnoreHostMismatch;
            if (!isGuest) vcellApiClient.authenticateWithAuth0(ignoreSSLCertProblems);
            else vcellApiClient.createDefaultQuarkusClient(ignoreSSLCertProblems);
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

    public void logOut(){
        vcellApiClient.logOut();
    }

    private static Path getAppPropertiesPath() throws IOException {
        Path appProperties = Path.of(ResourceUtil.getVcellHome().getAbsolutePath(), "/properties.json");
        if (!Files.exists(appProperties)) {
            Files.createFile(appProperties);
        }
        return appProperties;
    }

    public static boolean shouldWeShowLoginPopUp() throws IOException {
        Path appPropertiesPath = getAppPropertiesPath();
        Gson gson = new Gson();
        HashMap<String, Object> jsonMap = gson.fromJson(Files.readString(appPropertiesPath), HashMap.class);

        if (jsonMap == null || !jsonMap.containsKey(showLoginPopUpIndexString)) {
            return true;
        }
        boolean loggedIn = (boolean) jsonMap.get(showLoginPopUpIndexString);
        return loggedIn;
    }

    public static void setShowLoginPopUp(boolean showLoginPopUp) {
        Path appPropertiesPath = null;
        try {
            appPropertiesPath = getAppPropertiesPath();
            Gson gson = new Gson();
            HashMap<String, Object> jsonMap = gson.fromJson(Files.readString(appPropertiesPath), HashMap.class);
            jsonMap = jsonMap == null ? new HashMap<>() : jsonMap;
            jsonMap.put(showLoginPopUpIndexString, showLoginPopUp);
            String jsonString = gson.toJson(jsonMap);
            Files.write(appPropertiesPath, jsonString.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
