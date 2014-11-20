package cbit.vcell.desktop;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import cbit.vcell.client.ChildWindowListener;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.DocumentWindowManager;

public class LoginManager {
		
	public static final String USERACTION_LOGIN = "USERACTION_LOGIN";
	public static final String USERACTION_REGISTER = "USERACTION_REGISTER";
	public static final String USERACTION_EDITINFO = "USERACTION_EDITINFO";
	public static final String USERACTION_LOSTPASSWORD = "USERACTION_LOSTPASSWORD";
	public static final String USERACTION_CANCEL = "USERACTION_CANCEL";
	
	private LoginPanel loginPanel = null;
	private ChildWindow childWindow = null;
	
	public LoginManager(){
	}
	
	public void showLoginDialog(Component requester, DocumentWindowManager documentWindowManager, final LoginDelegate loginDelegate){
		loginPanel = new LoginPanel(loginDelegate);
		loginPanel.setLoggedInUser(documentWindowManager.getUser());
		
		childWindow = ChildWindowManager.findChildWindowManager(requester).addChildWindow(loginPanel, loginPanel, "VCell Login");
		childWindow.addChildWindowListener(new ChildWindowListener() {
			
			public void closing(ChildWindow childWindow) {
				loginDelegate.userCancel();
			}
			
			public void closed(ChildWindow childWindow) {
			}
		});
		childWindow.setSize(315, 320);
		childWindow.setIsCenteredOnScreen();
		childWindow.setTitle("Virtual Cell login");
		childWindow.showModal();
	}
	
	public void close(){
		childWindow.close();
	}
}
