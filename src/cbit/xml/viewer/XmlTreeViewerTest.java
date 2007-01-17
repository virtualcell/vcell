package cbit.xml.viewer;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (8/6/2001 11:34:07 PM)
 * @author: Jim Schaff
 */
public class XmlTreeViewerTest {
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		XmlTreeViewer aXmlTreeViewer;
		aXmlTreeViewer = new XmlTreeViewer();
		frame.setContentPane(aXmlTreeViewer);
		frame.setSize(aXmlTreeViewer.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		//  Automatically open two files and do:
		java.io.InputStreamReader inputStreamReader = new java.io.InputStreamReader(new java.io.FileInputStream("C:\\daniel\\VCML\\math_Ode.xml"));
		//
		org.jdom.input.SAXBuilder saxBuilder = new org.jdom.input.SAXBuilder();
		org.jdom.Document document = null;
		org.jdom.Element root = null;
		//
		try {
			document = saxBuilder.build(inputStreamReader);
		} catch (org.jdom.JDOMException jdomexception) {
			String tempstring = null;
			if (jdomexception.getCause() != null) {
				tempstring = "The following error ocurred while reading the base document:\n" + jdomexception.getCause();
			} else {
				tempstring = "The following error ocurred while reading the base document:\n" + jdomexception.getMessage();
			}
			throw new Exception(tempstring);
		}
		//
		aXmlTreeViewer.setDocument(document);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}

}
}
