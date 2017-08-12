package cbit.vcell.message.server.console;

import java.awt.Dialog.ModalityType;

import javax.swing.JDialog;

public class HostPortDialogTest {


	public static void main(String[] args) {
		HostPortDialog hpd = new HostPortDialog(null);
		hpd.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		hpd.setModalityType(ModalityType.APPLICATION_MODAL);
		hpd.setVisible(true);
		System.out.println(hpd.getHost());
		System.out.println(hpd.getPort());
		hpd.dispose();
	}

}
