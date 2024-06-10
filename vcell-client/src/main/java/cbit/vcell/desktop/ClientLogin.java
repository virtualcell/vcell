package cbit.vcell.desktop;

import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.RequestManager;
import cbit.vcell.client.VCellClient;
import cbit.vcell.client.server.ClientServerInfo;
import cbit.vcell.client.task.AsynchClientTask;
import cbit.vcell.server.Auth0ConnectionUtils;
import cbit.vcell.server.VCellConnectionFactory;
import org.vcell.util.document.User;
import org.vcell.util.document.UserLoginInfo;

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
                    Auth0ConnectionUtils.setShowLoginPopUp(false);
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

    public static AsynchClientTask connectToServer(Auth0ConnectionUtils auth0ConnectionUtils, ClientServerInfo clientServerInfo){
        AsynchClientTask task  = new AsynchClientTask("Connecting to Server", AsynchClientTask.TASKTYPE_NONSWING_BLOCKING) {
            public void run(Hashtable<String, Object> hashTable) throws Exception {
                // try server connection
                boolean login = (boolean)hashTable.get("login");
                boolean isGuest = (boolean)hashTable.get("guest");
                if (login) {
                    if (!isGuest){
                        int numberOfPolls = 0;
                        while(!auth0ConnectionUtils.isVCellIdentityMapped()){
                            if (numberOfPolls==100) {
                                return;
                            }
                            numberOfPolls++;
                            Thread.sleep(5000); // Poll every 5 seconds
                        }
                    }
                    DocumentWindowManager currWindowManager = (DocumentWindowManager)hashTable.get("currWindowManager");
                    ClientServerInfo newClientServerInfo = isGuest ?
                            VCellClient.createClientServerInfo(clientServerInfo, User.VCELL_GUEST_NAME, new UserLoginInfo.DigestedPassword("frmfrm"))
                            : VCellClient.createClientServerInfo(clientServerInfo, auth0ConnectionUtils.getAuth0MappedUser(), null);
                    VCellClient.getInstance().getRequestManager().connectToServer(currWindowManager, newClientServerInfo);
                }
            }
        };
        return task;
    }

}

















