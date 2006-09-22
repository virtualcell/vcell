package cbit.vcell.desktop.controls;
/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.beans.PropertyVetoException;

import cbit.image.VCImage;
import cbit.util.DataAccessException;
/**
 * Insert the type's description here.
 * Creation date: (1/16/01 10:50:11 AM)
 * @author: Jim Schaff
 */
public class ImageWorkspace extends AbstractWorkspace {
	private boolean bSaveReplaceInProgress = false;
	protected transient cbit.vcell.desktop.controls.WorkspaceListener aWorkspaceListener = null;
	private cbit.image.VCImage fieldImage = null;
	private java.io.File currentDirectory = null;

/**
 * Workspace constructor comment.
 */
public ImageWorkspace() {
	super();
	addPropertyChangeListener(this);
	addVetoableChangeListener(this);
}


/**
 * Use this constructor for test purposes.
 *
 * This is the only way of specifying a BioModel that doesn't
 * come from the database.
 */
public ImageWorkspace(VCImage image) {
	super();
	this.fieldImage = image;
	addPropertyChangeListener(this);
	addVetoableChangeListener(this);
}


/**
 * Gets the image property (cbit.image.VCImage) value.
 * @return The image property value.
 * @see #setImage
 */
public VCImage getImage() {
	return fieldImage;
}


/**
 * This method gets called when a bound property is changed.
 * @param evt A PropertyChangeEvent object describing the event source 
 *   	and the property that has changed.
 */
public void propertyChange(java.beans.PropertyChangeEvent evt) {
	super.propertyChange(evt);
}


/**
 * Sets the image property (cbit.image.VCImage) value.
 * @param image The new value for the property.
 * @see #getImage
 */
public void setImage(cbit.image.VCImage vcImage) throws DataAccessException, PropertyVetoException {
	
	setImage0(vcImage);
}


/**
 * Sets the image property (cbit.image.VCImage) value.
 * @param image The new value for the property.
 * @see #getImage
 */
private void setImage0(cbit.image.VCImage image) throws DataAccessException, PropertyVetoException {
	cbit.image.VCImage oldImage = fieldImage;

	fireVetoableChange("image", oldImage, image);
	
	fieldImage = image;
	if (oldImage!=null){
		oldImage.removePropertyChangeListener(this);
	}
	if (image!=null){
		image.addPropertyChangeListener(this);
	}
	firePropertyChange("image", oldImage, image);
}


	/**
	 * This method gets called when a constrained property is changed.
	 *
	 * @param     evt a <code>PropertyChangeEvent</code> object describing the
	 *   	      event source and the property that has changed.
	 * @exception PropertyVetoException if the recipient wishes the property
	 *              change to be rolled back.
	 */
public void vetoableChange(java.beans.PropertyChangeEvent evt) throws PropertyVetoException {
}
}