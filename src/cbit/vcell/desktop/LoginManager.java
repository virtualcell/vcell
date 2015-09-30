package cbit.vcell.desktop;

import java.awt.Component;

import cbit.vcell.client.ChildWindowListener;
import cbit.vcell.client.ChildWindowManager;
import cbit.vcell.client.ChildWindowManager.ChildWindow;
import cbit.vcell.client.DocumentWindowManager;
import cbit.vcell.client.task.AsynchClientTask;

public class LoginManager {
		
	/**
	 * {@link AsynchClientTask} key
	 */
	public static final String USERACTION_REGISTER = "USERACTION_REGISTER";
	/**
	 * {@link AsynchClientTask} key
	 */
	public static final String USERACTION_EDITINFO = "USERACTION_EDITINFO";
	/**
	 * {@link AsynchClientTask} key
	 */
	public static final String USERACTION_LOSTPASSWORD = "USERACTION_LOSTPASSWORD";
	
	private LoginPanel loginPanel = null;
	private ChildWindow childWindow = null;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public LoginManager(){
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void showLoginDialog(Component requester, DocumentWindowManager documentWindowManager, final LoginDelegate loginDelegate){
		loginPanel = new LoginPanel(loginDelegate);
		loginPanel.setLoggedInUser(documentWindowManager.getUser());
		
		childWindow = ChildWindowManager.findChildWindowManager(requester).addChildWindow(loginPanel, loginPanel, "VCell Login");
		childWindow.addChildWindowListener(new ChildWindowListener() {
			
			public void closing(ChildWindow childWindow) {
				loginDelegate.userCancel();
			}
		});
		childWindow.setIsCenteredOnParent();
		childWindow.setTitle("Virtual Cell login");
		childWindow.pack();
		childWindow.showModal();
	}
	
	public void close(){
		childWindow.close();
	}
}
