package cbit.image;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.event.*;
import java.awt.Component;
import java.awt.Container;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.Window;

import javax.swing.SwingUtilities;

import cbit.util.BeanUtils;
/**
 * Insert the type's description here.
 * Creation date: (9/3/00 1:57:50 PM)
 * @author: 
 */
public class ImagePaneView extends javax.swing.JPanel implements cbit.vcell.geometry.gui.VCellDrawable {
	private ImagePaneModel ivjImagePaneModel = null;
	private cbit.vcell.geometry.gui.DrawPaneModel fieldDrawPaneModel = null;
	//
	private transient PanListener panListener = null;
	private transient ZoomListener zoomListener = null;
	//
	private int mode = NO_OP;
	private Point anchorPoint = null;
	private static final int ZOOM_MOUSE_DELTA = 10;
	//
	private static final int NO_OP = 0;
	private static final int PANNING = 1;
	private static final int ZOOMING = 2;
	private boolean fieldForceZoom = false;
	private boolean fieldForcePan = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.awt.event.MouseListener, java.beans.PropertyChangeListener {
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {
			if (e.getSource() == ImagePaneView.this) 
				connEtoC3(e);
		};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == ImagePaneView.this.getImagePaneModel() && (evt.getPropertyName().equals("viewPortImage"))) 
				connEtoC1(evt);
			if (evt.getSource() == ImagePaneView.this.getImagePaneModel() && (evt.getPropertyName().equals("dimension"))) 
				connEtoM1(evt);
		};
	};
/**
 * ImagePaneView constructor comment.
 */
public ImagePaneView() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:19:11 PM)
 * @param panListener cbit.image.PanListener
 */
public synchronized void addPanListener(PanListener l) {
	if (l == null) {
		return;
	}
	panListener = ImagePaneViewMulticaster.add(panListener, l);
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:19:11 PM)
 * @param panListener cbit.image.PanListener
 */
public synchronized void addZoomListener(ZoomListener l) {
	if (l == null) {
		return;
	}
	zoomListener = ImagePaneViewMulticaster.add(zoomListener, l);
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2000 2:04:50 PM)
 * @return java.awt.Point
 * @param pixelPoint java.awt.Point
 */
private Point adjustPointPosition(Point pixelPoint) {
	//Do nothing for now
	return pixelPoint;
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/00 4:46:01 PM)
 */
public void clear() {
	if (getImagePaneModel() != null) {
		getImagePaneModel().setSourceData(null);
	}
	repaint();
}
/**
 * connEtoC1:  (ImagePaneModel.viewPortImage --> ImagePaneView.repaint()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.repaint();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (ImagePaneView.mouse.mouseEntered(java.awt.event.MouseEvent) --> ImagePaneView.requestFocus()V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		Component focusOwner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
		if(focusOwner != null){
			Component focusOwnerTopComponent = null;
			for (Container p = focusOwner.getParent(); p != null; p = p.getParent()) {
				if(p.getParent() == null) {
					focusOwnerTopComponent = p;
					break;
				}
			}
			Component thisTopComponent = null;
			for (Container p = this.getParent(); p != null; p = p.getParent()) {
				if(p.getParent() == null) {
					thisTopComponent = p;
					break;
				}
			}
			if(focusOwnerTopComponent == thisTopComponent){
				this.requestFocus();
			}
		}else{
			this.requestFocus();
		}
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (ImagePaneView.initialize() --> ImagePaneView.imagePaneView_Initialize()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4() {
	try {
		// user code begin {1}
		// user code end
		this.imagePaneView_Initialize();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (ImagePaneModel.dimension --> ImagePaneView.invalidate()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.invalidate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/00 4:30:35 PM)
 * @return cbit.vcell.geometry.DrawPaneModel
 */
public cbit.vcell.geometry.gui.DrawPaneModel getDrawPaneModel() {
	return fieldDrawPaneModel;
}
/**
 * Gets the forcePan property (boolean) value.
 * @return The forcePan property value.
 * @see #setForcePan
 */
public boolean getForcePan() {
	return fieldForcePan;
}
/**
 * Gets the forceZoom property (boolean) value.
 * @return The forceZoom property value.
 * @see #setForceZoom
 */
public boolean getForceZoom() {
	return fieldForceZoom;
}
/**
 * Return the ImagePaneModel1 property value.
 * @return imagescroller.ImagePaneModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public ImagePaneModel getImagePaneModel() {
	if (ivjImagePaneModel == null) {
		try {
			ivjImagePaneModel = new cbit.image.ImagePaneModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImagePaneModel;
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/00 4:30:35 PM)
 * @return java.awt.Point
 * @param p java.awt.Point
 */
public java.awt.Point getImagePoint(java.awt.Point pixelPoint) {
	if(getImagePaneModel() != null){
		return getImagePaneModel().getImagePoint(adjustPointPosition(pixelPoint));
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/15/00 2:00:08 PM)
 * @return java.awt.Point
 * @param p java.awt.Point
 */
public java.awt.geom.Point2D.Double getImagePointUnitized(java.awt.Point pixelPoint) {
	return getImagePaneModel().calculateImagePointUnitized(adjustPointPosition(pixelPoint));
}
/**
 * Insert the method's description here.
 * Creation date: (7/8/2003 5:22:16 PM)
 */
public java.awt.Dimension getPreferredSize() {
	if(getImagePaneModel() != null && getImagePaneModel().getDimension() != null){
		return getImagePaneModel().getDimension();
	}
	return new java.awt.Dimension(50,50);
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Comment
 */
private void imagePaneView_Initialize() {
	//This gives us events even if there are no listeners registered
	//This is done as part of the process of consuming events that turn into pan and zoom
	//processMouseEvent and processMouseMotionEvent are overriden to detect pan and zoom
	enableEvents(java.awt.AWTEvent.MOUSE_EVENT_MASK | java.awt.AWTEvent.MOUSE_MOTION_EVENT_MASK);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	this.addMouseListener(ivjEventHandler);
	getImagePaneModel().addPropertyChangeListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ImagePaneView");
		setLayout(null);
		setSize(160, 120);
		initConnections();
		connEtoC4();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (11/11/2000 2:03:02 PM)
 * @return boolean
 * @param pixelPoint java.awt.Point
 */
public boolean isPointOnImage(Point pixelPoint) {
	return getImagePaneModel().isPointOnImage(adjustPointPosition(pixelPoint));
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ImagePaneView aImagePaneView;
		aImagePaneView = new ImagePaneView();
		frame.setContentPane(aImagePaneView);
		frame.setSize(aImagePaneView.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/8/2003 5:22:16 PM)
 */
public void newMethod() {}
/**
 * Insert the method's description here.
 * Creation date: (9/3/00 2:16:31 PM)
 */
public void paintComponent(java.awt.Graphics g) {
	super.paintComponent(g);
	if (getImagePaneModel() != null) {
		java.awt.image.BufferedImage viewportImage = getImagePaneModel().getViewPortImage();
		if (viewportImage != null) {
			//
			//do all this because we can only draw an image the size of the whole viewport
			//
			//java.awt.Rectangle gClipBounds = g.getClip().getBounds();
			java.awt.Rectangle gClipBounds = g.getClipBounds();
			java.awt.Rectangle viewPortBounds = getImagePaneModel().getViewport();
			if (gClipBounds.getWidth() != viewPortBounds.getWidth() || gClipBounds.getHeight() != viewPortBounds.getHeight()) {
	            //Do this to deal with partial window exposure
				//g.setClip(viewPortBounds.x, viewPortBounds.y, viewPortBounds.width, viewPortBounds.height);
				g.drawImage(viewportImage, viewPortBounds.x, viewPortBounds.y, this);
				//g.setClip(gClipBounds);
			} else {
	            //Normal scrolling or zooming
				g.drawImage(viewportImage, gClipBounds.x, gClipBounds.y, this);
			}
		}
	}
	if (fieldDrawPaneModel != null && getImagePaneModel() != null && getImagePaneModel().getDimension() != null) {
		fieldDrawPaneModel.draw(g);
	}
}
/**
 * Comment
 */
private boolean panAndZoom(java.awt.event.MouseEvent mouseEvent) {
	//
	boolean bConsumed = false;
	//
	if (mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_PRESSED) {
		//set anchor point for all operations
		anchorPoint = mouseEvent.getPoint();
		if (getForceZoom() || (mouseEvent.getModifiers() & InputEvent.ALT_MASK) != 0) {
			mode = ZOOMING;
		} else if (getForcePan() || (mouseEvent.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
			mode = PANNING;
		}
		if (mode != NO_OP) {
			bConsumed = true;
		}
	} else if (mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_RELEASED) {
		if (mode != NO_OP) {
			mode = NO_OP;
			bConsumed = true;
		}
	} else if (mode == PANNING) {
		if (mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_DRAGGED) {
			if (panListener != null) {
				int deltaX = mouseEvent.getPoint().x - anchorPoint.x;
				int deltaY = mouseEvent.getPoint().y - anchorPoint.y;
				PanEvent panEvent = new PanEvent(this, 0, deltaX, deltaY);
				panListener.panning(panEvent);
				anchorPoint.x = mouseEvent.getPoint().x - deltaX;
				anchorPoint.y = mouseEvent.getPoint().y - deltaY;
			}
			bConsumed = true;
		}
	} else if (mode == ZOOMING) {
		if (mouseEvent.getID() == java.awt.event.MouseEvent.MOUSE_DRAGGED) {
			if (anchorPoint != null) {
				int zoomDelta = (mouseEvent.getPoint().x - anchorPoint.x) / ZOOM_MOUSE_DELTA;
				if (Math.abs(zoomDelta) > 0 && zoomListener != null) {
					anchorPoint = null;
					ZoomEvent zoomEvent = new ZoomEvent(this, 0, zoomDelta);
					zoomListener.zooming(zoomEvent);
				}
			} else {
				anchorPoint = mouseEvent.getPoint();
			}
			bConsumed = true;
		}
		//
	}
	return bConsumed;
}
/**
 * Insert the method's description here.
 * Creation date: (10/8/00 11:43:51 AM)
 */
protected void processMouseEvent(MouseEvent e) {
	if (!panAndZoom(e)) {
		super.processMouseEvent(e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/8/00 11:47:15 AM)
 */
protected void processMouseMotionEvent(MouseEvent e) {
	if (!panAndZoom(e)) {
		super.processMouseMotionEvent(e);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:51:37 PM)
 */
public synchronized void removePanListener(PanListener l) {
	if (l == null) {
		return;
	}
	panListener = ImagePaneViewMulticaster.remove(panListener, l);
}
/**
 * Insert the method's description here.
 * Creation date: (10/7/00 1:51:37 PM)
 */
public synchronized void removeZoomListener(ZoomListener l) {
	if (l == null) {
		return;
	}
	zoomListener = ImagePaneViewMulticaster.remove(zoomListener, l);
}
/**
 * Insert the method's description here.
 * Creation date: (9/4/00 4:30:35 PM)
 * @param dpm cbit.vcell.geometry.DrawPaneModel
 */
public void setDrawPaneModel(cbit.vcell.geometry.gui.DrawPaneModel drawPaneModel) {
	fieldDrawPaneModel = drawPaneModel;
}
/**
 * Sets the forcePan property (boolean) value.
 * @param forcePan The new value for the property.
 * @see #getForcePan
 */
public void setForcePan(boolean forcePan) {
	boolean oldValue = fieldForcePan;
	fieldForcePan = forcePan;
	firePropertyChange("forcePan", new Boolean(oldValue), new Boolean(forcePan));
}
/**
 * Sets the forceZoom property (boolean) value.
 * @param forceZoom The new value for the property.
 * @see #getForceZoom
 */
public void setForceZoom(boolean forceZoom) {
	boolean oldValue = fieldForceZoom;
	fieldForceZoom = forceZoom;
	firePropertyChange("forceZoom", new Boolean(oldValue), new Boolean(forceZoom));
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC4FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135998DF094E7B563001399F1DA27E9CD9DC6912D93ADC5BA22E225E36CB07507341346A00C931C0CE6ACD86C842D08146A17CBA23912C00050D2CBE944C0AFF09C792549113F7B8B248DB4D3E24C14A8EC6E7612A5FB37175B3D6420A03E375FFEF7FB47DE0EB0D3E69E3B7B5E773D7F773E77DDC85D5BCBAA4BCCF50414D5926D5F3BE5049CBEC24817FF763F27982E631D82B3517D7B8740BE22F8AB60
	F99EE83B546FD5DD55705C83F095F078DC864FDA125FF5C843AF50708C4ACC04C4AD3866052E439A2E9456D4812E8D603C052E3F8ACF2A40C2FB0736EE6CF3B964251F06CDB548C74713C12E3637C4FB56139DE50B9AD1D6DDF7353667B0FC8F83207165643D7F005924EBE8EA2247BBAEA8B6A140CD70F67BGBE0638A0BFA2703304D41DC09EE35DD56A9EC272E1FF160A7B1283798E423B8F606689064FF2A2FE833C1782D41DE436F43F532569BA7A71F7C9E1CB20E2EF8ADB6ACFD29B4E1F0C5B129158E8FA
	C3757BCA9F7AF78150F2GD683948390EB01399E60CD209DA05F771E0027E3DCF1C9010094F404C2CE51973EA079F8915E6DF620E8388FF92F947691C2E37A77EE2B01BEF3815DE71977BE6A131CD6965FE59FAEA7C55FFA48344B40A7C5B99F1BBA2C5497C35638CF28ECAE256C10B5B17F92E57F1B68E5D7547C3C3C52C0F67D0EF5658BB4BE59BA59F9D60C7581B5562B609D7761FE0ABF2262EBCF4571B4979FD1F3863F9B0016F90A6546689E264BE045D2D25B7CF9C3BFE4BF3548346594552142162CCB15
	8BF9B75928CCF3CC1703AAFED007273AFCC155913FA7011687C0736BC9BF56E653698273A0008F60A640E200D5GF927D90ED578CE5FE50E8DF1E1459991C49F9FA6F46F133E576049C715B0B7901495AE6865C73830400D0B3CCC34B5CF7B508763897B562A396F85996778A09F6694C18A82F7AE40ABFCD826EB8AFD7BF0CD18D72943C20087810190E1FC0BFC8760A9F232722F100FD378788ACAAF7121CFE6389160888C601D7A653793682FADE0FFC5D301392E0979618A0F7C5E6743C1DE345B87A5E9AA92
	9AC6CB082F897D9CD5FB07855E7B1A180FFFB309389C784E0D71F9085F2F6363963C28BE973E4EEA6C8DA76EA9047525CDACEFDE9CE5F9632EF90CD857BDE818B7D91043554DB4DF9A1A136B27C1650B3A8DB6335E7E28E3F49E350C7A683F8B1B0C6BAE31E72C19170C4A246F72B46B291F072FE14CC430AB93A02719797AB93E1E662E1313F9162D9F04A151F8DF5C73D44FBD2EDAF856E835D22969CFE9EF2A34DC603B89203625405C5142FC3844B91A247FAE0D479E0D5FFC7A25318F3F17A00371E73ECA67
	23F4C0FD75A3BF1ED37C8C9FD45CFC088F7A60C556E84F7AD11ED647FEA93627D04507206EDDD0E8F28C7F8BBF56A627A8E1E1BC227017F8C5910293F17A2FD4D9F2E4BC94EF99CC56E6BF5AA54DC25D47563F68471A71CA41A02F86AD36C523172AB54B0BFCG14F6C91120C2715BA6F01619622FEBBDE347445F88563C98417EC36703955CEE34A18CF3C178BA36B03222856B793E6EC3BA9FDD859C4EB98B60195D426AF2761F2CAE9BEEFC1D64DE89190C4E166CC76EB3EDEF2135D25D9A2FCB2A538B13463A
	EFD5FB070617292E9B4EE3BEA50DAEEA7C8F107E69BDFA1A5A20D9FDFCF68AE5D8C1F69F40E8AB2B0F0000F4560B3442F0C922C85D8C34452D680BC9E71D4E1ED0AB75C5BCF7BE3A0675A17353919EDABCB64577E3BE672E6DCF220DE87E27F4619A7ADF54755361B61479010B4D0EA52D34276E5464F634D159F45D0F5DEC5D12B6CADF25BD739356FD4A4356EDEF23BD30361D2E333653F57737633A6EFE364E5DCE570D35D3790A360E46628F0D189F6ADCF90D1B6122B6F996F25CEEFF8F3DAF52F9724F6A19
	006B0B5A595C38258FE3D1F7866482384F30DCFA2E064D0DD9D3B92438E6DDF92A99F86C8C55A363CCBC170A3EB9DBCEE33DDEC41939C04BBF4362DCF2CDFFF6B9E4D9728A6A3129456001A9FD2ED1DC76542B70748BE1D99900E690E5717A62946A0B672A06567C714B575FA6F34D59B4A1292D377A75362E5EFB4B34BB052DA51A0D956F46EDDD33F9DBB91DED22AEB6E73901FE93005642036ED3708CC8911937297FFB028A263685A3E563E6951B5A67EC97D49A3EB6466C7863B96CCDE14CD7D969173C9159
	52B822693D74C01F26D75E7225246B76FFCDC633FD665EC953CACD5FA21D5ECA61C6D3A6G55FD2B1B69EE69A8B0DF6DE03A0FFA90AF8485C560C46186EFE924FA6D3F0C7E977435FFE5C037EAC41B779E75B03E5940B3BF46F7FFBF66538CEC9B12420A9A16184D0B25D7D51BC31C90D41015464B5FDF2D75172ACE2801CE466B19FE74078FA62A20B6BBD1BE63A7EAD45D21438AB8F3292CB45FBDF6C46F3BAAEE21216F96ED1EB0C5BB294F16F46A729B7CD68AC04F433F5CF1975854991FC5F6F65EB9175099
	792ABA17575A690CDC96438BAA7E308ECF6D7FFD9F3BD3DA0036484E66BA130435DF8A5F55E78B4C8DGDDGE3GB74F321ABBAE3612B4F373GE6E51093AFA59C04ECFE955157F0F6E95F45A27AD8BD3B886B6B21907665EC103992A08F208420220B69B091C82B03A7EA288375F31BBB594F8DDD093E6DCB7A267B1CF76C33A5FD7B1276D1FB1E1E465EFB93E859GABG72GCA812A3B8B4CCD5D4C2E6F843A535A05D5BDAC39GF79E4E96D7726C905375CF77E8E325A71D0DC35DC67B9E3FE35F5D497BC1DA
	F9130672447EF4F7F6E3F9B7526E4B68B60AE1370CB14C865ACA00BC00CDG55BD85E68B40E08F0B612E60593B0C21C30D5B1D9124772E03BDF8F60940B7830062AE77A26E2B703D3A074D69FE870959A6695E37774D67FEB2DF20B2764D6B8EB4DF207E5CAD251E5F77CB46737BA149F8FEBFA26D23F860213F55B37AF1E96E795EA6251E6F232154FA5E8E996B39B0E42C67E7C2F36B79C0A8151EEF91BC2FFE725ADEF53FAC73321AECEECE61B45A734AC1E2F067384885F842E66EA937D9DB63D1A4D7EBCCEE
	7EF4EA9B57CF9B5B38F19A4FCED90A043DFC329DA55307E234F89D8CA8FCG5521EBFE276ED7387084FF079FDED277D3DA2A7D2FCC277295355566515B5A9643EFCA40B74770DF695761AFB447F4B9A727764DC55958B743F2EA5FDC16537B46A727764D141C5EB70ABC37EF2CBDF82F94396BD2C401D93497FB102B075D9F0B2187ED6F213D525ACB6F92F4DEFAFC9667DEFF188BG3BCBA15ECB6A7BF0EF38176DDD846B17G6482948390205359E42A074DA61300DB5247F8CE9CBDC6589CAE85EC42F8409619
	895838F198FAFD02E237DF0C08622026AA21F7129C15578660D18AD065E0F71268F1B627AF7B5AD2D2E8B63E13948F5FB255BB28FDEEC7FCE64B8AECB451FB50B6359F633E8C202FF2A02E21CFFF27C4BFEEA574CC23741184BA7A3FB1011E19C0C7FD9DE5FA7A938974CA5DFEEA5B5AE34C361DEBD790715B8BD27E4D01B8355F5434677DA58F17E57F75C6B96D6D53BD7776DB7A6DF96F23336A61DE0C5518B3F906BD2662A71D49B36C72D81EA8C05B60E4B32CBB225EDFDD8566D10090C0A6C08E409A97BB23
	DFD71AC81AB3FAD88A8DC2B10B3A76CB6DFB39775E7ED67043DDACAEAB1F7886D975E9F74A3B6186970D47F66DE9797543E571B9E47D8C5AE7F1439D9BE092A083A08BE019BB761B2EE22576F920B29C0A42F9A7559BB0B38EAFD9F1B496A8356DE8AF094571246EFDE54FA7BFCBBC73094A20FE3990C95D2307A346BD7A23087199FDB5B2772CB099C9BDABC8917DEF01B42EEFC5312EF3A106C5G44D3E02E8330F8D8CCDF099C25B1B5082747B701E7847EA5C2E1720BE7D25B7A7CCC7A3FDF144E605DF9DCD2
	94A9007C99FE5B8C46CB11C2AA1262DE1EC19F050509C905AE5570E5336A8C4B7BD5B4459D0B26564B9ACD2FD7FBD47DCD87788DDF8FE9BC6DD1BC27A43FDF66E37677C6EBE33881F9C81285BC7F7E8FD0CB8788CE0E10FD968BGGC49EGGD0CB818294G94G88G88GC4FBB0B6CE0E10FD968BGGC49EGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD08BGGGG
**end of data**/
}
}
