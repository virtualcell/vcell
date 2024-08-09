package cbit.vcell.desktop;

import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.VCellClientMain;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.server.Auth0ConnectionUtils;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.vcell.DependencyConstants;
import org.vcell.util.document.User;
import org.vcell.util.gui.VCellIcons;
import scala.util.parsing.combinator.testing.Str;

import javax.swing.*;
import java.util.Hashtable;

public class ClientLogin {

    public enum LoginOptions {
        FULL_DIALOG,
        STANDARD_LOGIN,
        GUEST_LOGIN
    }


    public static String showFullLoginPanel(){
        String SKIP = "Skip", GUEST = "Guest", LOGIN = "Login";
        String[] options = new String[] {SKIP, GUEST, LOGIN};
        int result = JOptionPane.showOptionDialog(null,
                """
                        Welcome to The Virtual Cell!
                        
                        For non-guest logins, VCell now uses `Auth0`,
                        a browser based way to securely login to VCell.

                        (Note: If you do not log out, `Auth0` will
                        automatically log you in when you reopen VCell)
                        
                        Please select one of the following login options.""","Login to VCell",
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, VCellIcons.makeIcon("/icons/vcell.png"), options, "Login");
        return result < 0 ? SKIP : options[result];
    }

    public static String showAcceptLoginPanel(){
        String OKAY = "Okay", CANCEL = "Cancel";
        String[] options = new String[] {OKAY, CANCEL};
        int result = JOptionPane.showOptionDialog(null, "VCell is going to redirect you to your browser to login. Do you wish to proceed?",
                "VCell Login", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, "Okay");
        return result < 0 ? CANCEL : options[result];
    }

    public static boolean doesTheUserWantToLogin(String answer){
        return switch (answer) {
            case "Guest", "Login", "Okay" -> true;
            default -> false;
        };
    }

    public static boolean doesTheUserWantToBeGuest(String answer){
        return switch (answer) {
            case "Guest" -> true;
            default -> false;
        };
    }

    public static AsynchClientTask popupLogin(){
        return popupLogin(LoginOptions.FULL_DIALOG);
    }

    public static AsynchClientTask popupLogin(LoginOptions loginOption){
        AsynchClientTask task = new AsynchClientTask("Popup Login...", AsynchClientTask.TASKTYPE_SWING_BLOCKING) {
            @Override
            public void run(Hashtable<String, Object> hashTable) throws Exception {
                boolean showPopupMenu = Auth0ConnectionUtils.shouldWeShowLoginPopUp();
                hashTable.put("login", true);
                hashTable.put("guest", false);
                if (!showPopupMenu) return;
                String result = LoginOptions.FULL_DIALOG.equals(loginOption) ?
                        ClientLogin.showFullLoginPanel() : LoginOptions.STANDARD_LOGIN.equals(loginOption) ?
                        "Login" : LoginOptions.GUEST_LOGIN.equals(loginOption) ?
                        "Guest" : "Skip";
                hashTable.put("login", ClientLogin.doesTheUserWantToLogin(result));
                hashTable.put("guest", ClientLogin.doesTheUserWantToBeGuest(result));
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

    @Inject @Named(DependencyConstants.VCELL_QUARKUS_API_HOST)
    static String hostname;

    @Inject
    public static AsynchClientTask connectToServer(Auth0ConnectionUtils auth0ConnectionUtils,
                                                   ClientServerInfo clientServerInfo){

        AsynchClientTask task  = new AsynchClientTask("Connecting to Server", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
            public void run(Hashtable<String, Object> hashTable) throws Exception {


//                String hostname = VCellClientMain.injector.getInstance(Key.get(String.class, Names.named(DependencyConstants.VCELL_QUARKUS_API_HOST)));
                // try server connection
                boolean login = (boolean)hashTable.get("login");
                boolean isGuest = (boolean)hashTable.get("guest");
                if (login) {
                    if (!isGuest){
                        int numberOfPolls = 0;
                        while(!auth0ConnectionUtils.isVCellIdentityMapped()){
                            if (numberOfPolls==100) {
                                String message = "Please restart the VCell application for we can't find your UserID. " +
                                        "If you have not created/claimed " +
                                        "a UserID on the website https://" + hostname + "/profile, please do so.";
                                JOptionPane.showMessageDialog(null, message,
                                        "Restart the Application", JOptionPane.ERROR_MESSAGE, null);
                                return;
                            }
                            numberOfPolls++;
                            Thread.sleep(5000); // Poll every 5 seconds
                        }
                        Auth0ConnectionUtils.setShowLoginPopUp(false);
                    }
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

















