package cbit.vcell.desktop;

import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.VCellClientMain;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.resource.PropertyLoader;
import cbit.vcell.server.Auth0ConnectionUtils;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.vcell.DependencyConstants;
import org.vcell.dependency.client.VCellClientModule;
import org.vcell.util.document.User;

import javax.swing.*;
import java.util.Hashtable;

public class ClientLogin {


    public static int showLoginPanel(){
        int answer = JOptionPane.showOptionDialog(null,
                "VCell is going to redirect you to your browser to login. Do you wish to proceed?","Login Popup",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] {"Login", "Offline", "Guest"}, "Login");
        return answer;
    }

    public static boolean doesTheUserWantToLogin(int answer){
        return !(answer == JOptionPane.NO_OPTION || answer == JOptionPane.CLOSED_OPTION);
    }

    public static boolean doesTheUserWantToBeGuest(int answer){
        return (answer == JOptionPane.CANCEL_OPTION);
    }


    public static AsynchClientTask popupLogin(){
        AsynchClientTask task = new AsynchClientTask("Popup Login...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
            @Override
            public void run(Hashtable<String, Object> hashTable) throws Exception {
                boolean showPopupMenu = Auth0ConnectionUtils.shouldWeShowLoginPopUp();
                hashTable.put("login", true);
                hashTable.put("guest", false);
                if(showPopupMenu){
                    int accept = ClientLogin.showLoginPanel();
                    hashTable.put("login", ClientLogin.doesTheUserWantToLogin(accept));
                    hashTable.put("guest", ClientLogin.doesTheUserWantToBeGuest(accept));
                }
            }
        };
        return task;
    }



    public static AsynchClientTask loginWithAuth0(Auth0ConnectionUtils auth0ConnectionUtils){
        AsynchClientTask task = new AsynchClientTask("Login With Auth0...", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
            @Override
            public void run(Hashtable<String, Object> hashTable) throws Exception {
                if ((boolean)hashTable.get("login")) {
                    boolean isGuest = (boolean)hashTable.get("guest");
                    auth0ConnectionUtils.auth0SignIn(isGuest);
                }
            }
        };
        return task;
    }

    @Inject
    public static AsynchClientTask connectToServer(Auth0ConnectionUtils auth0ConnectionUtils,
                                                   ClientServerInfo clientServerInfo){

        AsynchClientTask task  = new AsynchClientTask("Connecting to Server", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
            public void run(Hashtable<String, Object> hashTable) throws Exception {
                String hostname = VCellClientMain.injector.getInstance(Key.get(String.class, Names.named(DependencyConstants.VCELL_QUARKUS_API_HOST)));
                // try server connection
                boolean login = (boolean)hashTable.get("login");
                boolean isGuest = (boolean)hashTable.get("guest");
                if (login) {
                    if (!isGuest){
                        int numberOfPolls = 0;
                        while(!auth0ConnectionUtils.isVCellIdentityMapped()){
                            if (numberOfPolls==100) {
                                String message = "Please restart the VCell application for we can't find your user. " +
                                        "If you have not created " +
                                        "a user on the website https://" + hostname + ", please do so. And if you have an existing user please " +
                                        "follow the conversion process on the website.";
                                JOptionPane.showMessageDialog(null, message,
                                        "Restart the Application", JOptionPane.ERROR_MESSAGE, null);
                                return;
                            }
                            numberOfPolls++;
                            Thread.sleep(5000); // Poll every 5 seconds
                        }
                    }
                    Auth0ConnectionUtils.setShowLoginPopUp(false);
                    DocumentWindowManager currWindowManager = (DocumentWindowManager)hashTable.get("currWindowManager");
                    ClientServerInfo newClientServerInfo = isGuest ?
                            VCellClient.createClientServerInfo(clientServerInfo, User.VCELL_GUEST_NAME)
                            : VCellClient.createClientServerInfo(clientServerInfo, auth0ConnectionUtils.getAuth0MappedUser());
                    VCellClient.getInstance().getRequestManager().connectToServer(currWindowManager, newClientServerInfo);
                }
            }
        };
        return task;
    }

}

















