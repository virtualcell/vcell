package org.vcell.util.importer;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class WebImportPanel extends JPanel {

	public static String[] urlStringOptions = new String[]{
		"http://www.signaling-gateway.org/molecule/query?afcsid=A000037&type=sbPAXExport",
		"http://www.signaling-gateway.org/molecule/query?afcsid=A001778&type=sbPAXExport",
		"http://www.signaling-gateway.org/molecule/query?afcsid=A001750&type=sbPAXExport",
		"http://www.signaling-gateway.org/molecule/query?afcsid=A001852&type=sbPAXExport",
		"http://www.signaling-gateway.org/molecule/query?afcsid=A001046&type=sbPAXExport"};
	
	protected final JComboBox urlInput = new JComboBox(urlStringOptions);
	protected final JLabel urlMessageLabel = new JLabel();

	public WebImportPanel() {
		urlInput.setEditable(true);
		add(urlInput);
		add(urlMessageLabel);
	}
	
	public void setURL(URL url) { 
		setURLString(url.toString());
	}

	public URL getURL() { return getURL(true); }
	
	public URL getURL(boolean addDefaultProtocol) { 
		URL url = null;
		try {
			url = new URL(getURLString());
			urlMessageLabel.setText("");
		} catch (MalformedURLException e) {
			String message = e.getMessage();
			if(addDefaultProtocol) {				
				try {
					String urlStringWithHTTP = "http://" + getURLString();
					url = new URL(urlStringWithHTTP);
					setURLString(urlStringWithHTTP);
					urlMessageLabel.setText("");
				} catch (MalformedURLException e1) {
					urlMessageLabel.setText(message);
				}
			} else {
				urlMessageLabel.setText(message);				
			}
		}
		return url; 
	}
	
	public void setURLString(String urlString) { urlInput.setSelectedItem(urlString); }
	public String getURLString() { return (String) urlInput.getSelectedItem(); }
	
}
