package cbit.vcell.client.desktop;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.vcell.util.gui.KeySequenceListener;

@SuppressWarnings("serial")
public class DocumentWindowAboutBox extends JDialog {

	private JLabel appName = null;
	private JPanel buttonPane = null;
	private JLabel copyright = null;
	EventHandler eventHandler = new EventHandler();
	private JLabel iconLabel = null;
	private JPanel iconPane = null;
	private JPanel dialogContentPane = null;
	private JButton okButton = null;
	private JPanel textPane = null;
	private JLabel userName = null;
	private JLabel version = null;
	public static String BUILD_NO = "";
	private JLabel buildNumber = null;

	class EventHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == DocumentWindowAboutBox.this.getOkButton())
				try {
					DocumentWindowAboutBox.this.dispose();
				} catch (Throwable throwable) {
					handleException(throwable);
				}
		};
	};

	public static class HierarchyPrinter extends KeySequenceListener {
		
		public String getSequence() { return "hierarchy"; }
		
		public void sequenceTyped() {
			for(Frame frame : Frame.getFrames()) {
				printHierarchy(frame, "");
			}
		}
		
		public void printHierarchy(Component component, String indentation) {
			System.out.println(indentation + component);
			if(component instanceof Container) {
				for(Component child : ((Container) component).getComponents()) {
					printHierarchy(child, indentation + "  ");
				}
				
			}
		}
		
	}
	
	public DocumentWindowAboutBox() {
		super();
		initialize();
		setFocusable(true);
		addKeyListener(new HierarchyPrinter());
	}

	private JLabel getAppName() {
		if (appName == null) {
			try {
				appName = new JLabel();
				appName.setName("AppName");
				appName.setText("Virtual Cell");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return appName;
	}

	public JLabel getBuildNumber() {
		if (buildNumber == null) {
			try {
				buildNumber = new JLabel();
				buildNumber.setName("BuildNumber");
				buildNumber.setText("");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return buildNumber;
	}

	private JPanel getButtonPane() {
		if (buttonPane == null) {
			try {
				buttonPane = new JPanel();
				buttonPane.setName("ButtonPane");
				buttonPane.setLayout(new FlowLayout());
				getButtonPane().add(getOkButton(), getOkButton().getName());
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return buttonPane;
	}

	private JLabel getCopyright() {
		if (copyright == null) {
			try {
				copyright = new JLabel();
				copyright.setName("Copyright");
				copyright.setText("(c) Copyright 2004");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return copyright;
	}

	private JLabel getIconLabel() {
		if (iconLabel == null) {
			try {
				iconLabel = new JLabel();
				iconLabel.setName("IconLabel");
				iconLabel.setIcon(new ImageIcon(getClass().getResource("/images/ccam_sm_colorgr.gif")));
				iconLabel.setText("");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return iconLabel;
	}

	private JPanel getIconPane() {
		if (iconPane == null) {
			try {
				iconPane = new JPanel();
				iconPane.setName("IconPane");
				iconPane.setLayout(new FlowLayout());
				getIconPane().add(getIconLabel(), getIconLabel().getName());
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return iconPane;
	}

	private JPanel getJDialogContentPane() {
		if (dialogContentPane == null) {
			try {
				dialogContentPane = new JPanel();
				dialogContentPane.setName("JDialogContentPane");
				dialogContentPane.setLayout(new BorderLayout());
				getJDialogContentPane().add(getButtonPane(), "South");
				getJDialogContentPane().add(getTextPane(), "Center");
				getJDialogContentPane().add(getIconPane(), "West");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return dialogContentPane;
	}

	private JButton getOkButton() {
		if (okButton == null) {
			try {
				okButton = new JButton();
				okButton.setName("OkButton");
				okButton.setText("OK");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return okButton;
	}

	private JPanel getTextPane() {
		if (textPane == null) {
			try {
				textPane = new JPanel();
				textPane.setName("TextPane");
				textPane.setLayout(getTextPaneGridLayout());
				getTextPane().add(getAppName(), getAppName().getName());
				getTextPane().add(getVersion(), getVersion().getName());
				getTextPane().add(getBuildNumber(), getBuildNumber().getName());
				getTextPane().add(getCopyright(), getCopyright().getName());
				getTextPane().add(getUserName(), getUserName().getName());
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return textPane;
	}

	private GridLayout getTextPaneGridLayout() {
		GridLayout textPaneGridLayout = null;
		try {
			/* Create part */
			textPaneGridLayout = new GridLayout(5, 1);
		} catch (Throwable throwable) {
			handleException(throwable);
		};
		return textPaneGridLayout;
	}

	private JLabel getUserName() {
		if (userName == null) {
			try {
				userName = new JLabel();
				userName.setName("UserName");
				userName.setText("UCHC / NRCAM");
				// user code begin {1}
				// user code end
			} catch (Throwable throwable) {
				// user code begin {2}
				// user code end
				handleException(throwable);
			}
		}
		return userName;
	}

	public JLabel getVersion() {
		if (version == null) {
			try {
				version = new JLabel();
				version.setName("Version");
				version.setText("Version 4.0");
			} catch (Throwable throwable) {
				handleException(throwable);
			}
		}
		return version;
	}

	private void handleException(Throwable exception) {
		System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}

	private void initialize() {
		try {
			setName("DocumentWindowAboutBox");
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setResizable(false);
			setSize(330, 160);
			setTitle("DocumentWindowAboutBox");
			setContentPane(getJDialogContentPane());
			getOkButton().addActionListener(eventHandler);
		} catch (Throwable throwable) {
			handleException(throwable);
		}
	}

	public static void main(String[] args) {
		try {
			DocumentWindowAboutBox aDocumentWindowAboutBox;
			aDocumentWindowAboutBox = new DocumentWindowAboutBox();
			aDocumentWindowAboutBox.setModal(true);
			aDocumentWindowAboutBox.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				};
			});
			aDocumentWindowAboutBox.setVisible(true);
			Insets insets = aDocumentWindowAboutBox.getInsets();
			aDocumentWindowAboutBox.setSize(aDocumentWindowAboutBox.getWidth() + insets.left + insets.right, aDocumentWindowAboutBox.getHeight() + insets.top + insets.bottom);
			aDocumentWindowAboutBox.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of JDialog");
			exception.printStackTrace(System.out);
		}
	}
}