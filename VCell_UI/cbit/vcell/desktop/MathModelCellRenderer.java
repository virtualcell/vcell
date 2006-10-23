package cbit.vcell.desktop;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import cbit.util.document.MathModelInfo;
import cbit.util.document.User;
import cbit.util.document.VersionFlag;
/**
 * Insert the type's description here.
 * Creation date: (7/27/2000 6:30:41 PM)
 * @author: 
 */
import javax.swing.*;
 
public class MathModelCellRenderer extends VCellBasicCellRenderer {
	private User sessionUser = null;

/**
 * MyRenderer constructor comment.
 */
public MathModelCellRenderer(User argSessionUser) {
	super();
	this.sessionUser = argSessionUser;
}


/**
 * Insert the method's description here.
 * Creation date: (7/27/2000 6:41:57 PM)
 * @return java.awt.Component
 */
public java.awt.Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
	JLabel component = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
	//
	try {
		if (value instanceof BioModelNode) {
			BioModelNode node = (BioModelNode) value;
			boolean bLoaded = false;
			if (node.getUserObject() instanceof User && node.getChildCount()>0 && (((BioModelNode)node.getChildAt(0)).getUserObject() instanceof String) && ((BioModelNode)(node.getChildAt(0).getChildAt(0))).getUserObject() instanceof MathModelInfo){
				//
				// Check if node is a User, with at least one child which is a string (BioModel name)
				// and if the child's child is a BioModelInfo node
				//
				String label = null;
				if (sessionUser != null && sessionUser.compareEqual((User)node.getUserObject())) {
					label = "My MathModels ("+((User)node.getUserObject()).getName()+")";
				} else {
					label = ((User)node.getUserObject()).getName()+"                             ";
				}
				component.setToolTipText("User Name");
				component.setText(label);
				
				if (isLoaded((User)node.getUserObject())) {
					bLoaded = true;
				}
			}else if(node.getUserObject() instanceof MathModelInfo){
				MathModelInfo mathModelInfo = (MathModelInfo)node.getUserObject();
				if(mathModelInfo.getVersion().getFlag().compareEqual(VersionFlag.Archived)){
					component.setText("(Archived) "+component.getText());
				}else if(mathModelInfo.getVersion().getFlag().compareEqual(VersionFlag.Published)){
					component.setText("(Published) "+component.getText());
				}
			}
		}
	}catch (Throwable e){
		e.printStackTrace(System.out);
	}
	//
	return component;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 9:29:31 AM)
 * @return boolean
 * @param geometryInfo cbit.vcell.geometry.GeometryInfo
 * @deprecated
 */
protected boolean isLoaded(MathModelInfo mathModelInfo) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 9:29:31 AM)
 * @return boolean
 * @param geometryInfo cbit.vcell.geometry.GeometryInfo
 * @deprecated
 */
protected boolean isLoaded(User user) {
	return false;
}


/**
 * Insert the method's description here.
 * Creation date: (5/8/01 8:35:45 AM)
 * @return javax.swing.Icon
 * @param nodeUserObject java.lang.Object
 */
protected void setComponentProperties(JLabel component, MathModelInfo mathModelInfo) {
		
	super.setComponentProperties(component, mathModelInfo);
	//cbit.vcell.numericstest.TestSuiteInfo tsInfo;
	//try {
		//tsInfo = cbit.vcell.numericstest.TSHelper.getTestSuiteInfo(mathModelInfo.getVersion().getAnnot());
	//} catch (cbit.vcell.xml.XmlParseException e) {
		//e.printStackTrace(System.out);
		//throw new RuntimeException("Error reading annotation for mathModel : "+mathModelInfo.getVersion().getName());
	//}
	//if (tsInfo != null) {
		//if (!selected){
			//component.setForeground(java.awt.Color.blue);
		//}
		//component.setText(component.getText()+" ("+cbit.vcell.numericstest.TestSuiteInfo.TESTSUITE_TAG_DONT_CHANGE+" "+tsInfo.getVersion()+")");
	//}
}
}