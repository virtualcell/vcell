package cbit.image.gui;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
import java.awt.event.*;
import java.awt.Point;

/**
 * Insert the type's description here.
 * Creation date: (9/3/00 1:57:50 PM)
 * @author: 
 */
public class ImagePaneView extends javax.swing.JPanel implements cbit.vcell.geometry.gui.VCellDrawable {
	private ImagePaneModel ivjImagePaneModel = null;
	private cbit.vcell.geometry.DrawPaneModel fieldDrawPaneModel = null;
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
		this.requestFocus();
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
public cbit.vcell.geometry.DrawPaneModel getDrawPaneModel() {
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
			ivjImagePaneModel = new cbit.image.gui.ImagePaneModel();
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
public void setDrawPaneModel(cbit.vcell.geometry.DrawPaneModel drawPaneModel) {
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
	D0CB838494G88G88G4D0171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135998BD0D4579595952BC934258D2624D5CB93622FDA6D0431CE0A99BBE39327B5B324C3DB2663B4E6DA27CE062436F1D2273D3B08286B8715C4BAB546C4C0845C0288B804E4C4D04128590938E59A1B7816FD40565DFD6BFBEFE1D99668B9773E7B76313EF503B3BAF33C6F1EF36F791FFB4FDDC849FB738AAD5692C2AC05C47B57E4A16468B1C2BA332EB7F25CEC3F53C68C7F5E87B8C86ACE6C00F19E
	500A687CFAF8AF0C7B8132G07638E49E9ABA63D61D28D1F2761B9D82C04EC20EBA6050FEA388E185F861885B0CFE26B33E05CCDA5CFEDBE272D3BB5A713ECFDC1366EC3BE861E9CCA83ECEF0BB69EA7EFD9B685D056C22AA76ED1E13E98G0DAFA0D77EFB8C57BB6E74FD5C637228F60FCF68931B1BBBF1F088FE516991878939FD97F93C915EC57790D28973AD9457B006FCB360FBB5C00E0E2FA2082F151D368E005BB237652313AD1A2E5D434F12CDFDBF5DFDA00EADA11959B0CF0E5A12DC65AA7887757B5D
	B274EF9E5008623415G1C83688688817C93E807C968C4950C2DAE35CD7279A4FFEBA0F0416B162FC9EE518B5F4D4DC051F01F09BD126CA60445F4D93846C41F7B815B775C0477B11FE4567378AE7EECA949DB71ADEB1109CF72B2072D3395660BCCA56A93A67B4D383257A96373EF3C6CFF9323EC3279CCD121096C233FBCD314277149B748AED3B1564F509CA806EF5C077B99FEA545779B70AC97BF24F81CC70036D16539513D1F6BF2F54782D2DC7738299F521E1FE5ED8FB29DEE85E3F5798E4D3B14A01339
	DB57E59145E79870CC17DA0A47F9B6504A861CB616DF618A2C4D9418E7G2C864881588AD0BC886B86F90E2DB9712F2F18E38EC1D6AF84BDDE37A89336B7DC768E0CE2C81505CE3F228A7E9E51A9489E41659595222D19D206BEF00D5B17C2F33F87E4DC967D22AC289E498F5C851F280A3242564CA9BB08EBE411D23BBCBE9184788204731DDBF698C62F2028B783EEC19523AB98FDFE997AE4C0F082C7E0G5F4CAFCF1FC4FF35037D37G3C3A9F0E16A33FCFC459AFFA1B1B2FCA521DE0208BADA12B87514F7B
	07716CC8026F34C16E636FD5A20E041C36529067F3237C1001CF3B2787559764A1DEE3BF9843BDB5303EA344736635EE1EB76D7B16101A173FE91AB733A1075DA116AF11D0EC7D2C22FCD1370C90BF5BDB473AA7D04B9D2171F5D693B22F3B71E746FFA6A423B0E6DE10E0BD737341AA0C59DA50E7A3G99623EFE347CB84B5D8B02A272EC7DD406C4935DD14FB3BDB3C8B10C7B34DAA95474E7346514D68AFC4F8374834C9E62BEECA45DB17A97E9BC76EB7CA6F2DE1A7B7053F1B2B8FF6E2BC4BECA844CD74995
	F8CF098322DFED9783225F8D9F0AC61BD9017234BA6E1574BDF3A8BEGF55F860526687847AB30B685D515BD2E20AA5E90D555636F0B523FCBE5A9C1D7A0FAE4F0D9CFD520DD52A0543D3EFED18556CC0F64770BB4E87A960D3E04562C6895FD20F41B94742B8C3F62A476B2F744A16D4CF87664CE02B56F8D6279437A03E582B6C181997A82F9880FB032AB0C75DCBEDCEA70D196449B7B1CBC987307F8DD8E1E60F5D996FE06E4FF99301A5DADE95FFF4C6A8833DAF10723F549F44A2CB457FDA9BDBBB43C42F4
	4D6D40FC0AE9DDE87C8F13CBF7779BE974006675F1259AE5A4036CB400E5E1DE9FD527104E4FA22DB05AA42F173999E8DB29AF7EB6D6E230E7DD1879A21ABBC72A30BE9471EED004A39E8F45CFF41F9F2BBA94C3F3EA7EE7740AAA743F57F01EAE9EC6192F925EBB368C33B335EB18491DBD4CE433F5DFD8793AED9A3DB842464A08F1DDE981DF670830B330DFDB172C2D7BC384575D3171F5695A3A1591A6FF3D360E4562CF27B0BFE8DF79BFE1C0885915C148716666AB68FDAF6BA79F23F7822EBFBB427B4649
	23988BAF4C138152C7F8AE3D340F770D29F7B2C95E663A22F8BD704A9126C76EC8B417E667376EE431CE2DC1192523E07FA80F737CAA635D552AA8D20F07DE1BDA8C9C5546DCE3381B55FBE06C75480A5A8907C108476B73EA54976FD58D2D796337C75EA3776B33D9C2B2DBC7AE99EDDDFDE044EA0BE3EB4B2856870DC6EDBDAFF595315EE63F0577D969C04F81E035D0EDBD8D23CF8AAA221D7EFF512FE2EAD7E2246C4220EA27671C7D9A256167A95D0EBFDF4633C946FCD554CBD2CFD029BC655474DEF0783C
	26577A258BC84B5897D6335EBE65C03FF56F9853776CD8D46F45DFF795E483B05D27DB396EC930A6EB0C6B7E9A45FB7C9E55A3F8BDE1317294536B506768FF0F31763F6CB42CF2EA7DDEEB81671B8FBCAB13B85F4C824C2781586610E41506C537B9DD5AC3ED8E889E3F0A2CB4DE29363D5A79F29B7824693C7ECAF3568D9D15DF6B1D989FD755BE2ABB1C309E6C39A8AB4DF7CB0E99FD37CB18EA6A3BD91B7B2C9B12184F36A5C5FDD7728DD7C1A5G3B8FD307E35F8267A7C5FB112EC97776A52CC77E8B2D3D7E
	C92CC73668783FD33CEA40B37BD3C6791BB289E81BA6713E6EA7F62C7D0E49CE1B9BA082108A3092A0E7B22F39D72A6BC9023E39933352AFF8EF0C3B88F97FDA033E063BCB1BBFD903BE26F79761677A69DABC1773C1665EA950338334G5C1A42F5A8BB1DD0070BA1D39D181FDF30447AB9B2E53CEF53E266ECDF6EBD7B6CB17355B17B18BDD36A706C4D81DABE402EE430892083408D10124C6DD2CEFF1450AE2C6AAE298DF09DF0373445768E3A2E0FD89F4C46EF5F33AF56464CE433FD178B9ECC5E4DFB7645
	4A4BB6157704AD511B5DDCDEE642FD3949E6B1FC6B8C46B09FE8FB27C27CG4E83388112GB22672982EBD7D61D70CE1AB0D5B3D91E46F2EC5740EDD8BFCF381986E75886236433CE49A6F53370F925D3642316877232393F91FCC94180C279F2A8C664FF576787D7BAF6C667D7B4BF6737E7DB776038C8FBC0C2FFACEFF45FE7F7E7E0F76787DFDC3EDFCBDDBEA4D753CD0EB2EE7C76D7D75F454465373DD0277554F5EB9C077AB0A2850E4EB97D4C1237DDEFD1B183CB92E8BBE11701E5BE25D2D2D3928CAED75
	3A5CC76A625B18DAE7EE6393F5F8F7AAD2D06E91E36D18DFD72A5322F550290ABED4072DD9C8772B025CA75E630765F4BF23455B3F2AAE1E2F182DEB4759FAD6470F969871F5BA7EA51B81FF2DCE57E5C7FDFC5F586A4DFDF330BE3EEFCA6B937B667D7A783E292CCF6C1B3A7A7B7B6679E1FCD7FA05A1A928C2AFBA8D4F20DF696F47F3B0F7CCE3E7E572B47616E07D5272C16CFBFBE541876CEE84449E921A0EFBF70D703D1BE07DB600D2401F836886E03D09A542FB13EC40ED1B4EF976FDD0CEF89FAE796C9E
	174F9E9C70598597B43DEE0F5A5CFCBD6875DE55D4A56CCD72B165D51B82F7AD406D947EA689D570BEFD71D38B4806D74D5FA49B3BC62C2194E6DFFACA34E7CB2BF29530F7502C11DD5A3EDC20974FC05C2A91631B927D78BAE1F79A23EF99C7C77F1F9AC7BFB6EA24233E2D96A35DB10EDEE8584FECFB311C5B367D45E5447B236438FFF3681E217D26260D47FFBE5732A54D31131D6DC511877BAD7D6D896FE33D6A8FE92CD64E086DE1FFCC7159B3E2FB58F6BDCF56832D76EB3C0735B4E00E2542FC9940BA00
	0DGE42653F6F4A63F23F36AEBC902BB3ACB8ADC05E2769A0EDFE65F1C11877BDB411AA29E17152B96122CE96DF15F0635B3D9BC9C5A1835FE2EA55A0724B622FDC9C0CB8748864885588230E3167E1B6E599AE65FC5280CD6D595FA7A698B189B070F2CA89A8B14591695A1FA9C57983E2FC49EFEAF5173D0E5B03F6CE908FFC69FECB0BF230FB4185F516FB65C3FD7A8EF085FAB54B498FF8BE4F17DF59356F5A94470ACC0B70097A0C90F692A068FD8CCCD62F9515D07F7847EA5C265721FEC0CEFEBC6E3623F
	DFACE9443733CBD2D549077CB9FEC5A346CB15829449F0D90D68A35953572F32259A7E39C6BC9F3DE2AFC5B35C6F1A626B3539A931DEEFB4515FF400DF57D0C06379E6935ED3D2EF2FA26A76EFEFAA56F11D0AC3D2BCF87F7D9FD0CB8788D52AEA9BA08BGGC49EGGD0CB818294G94G88G88G4D0171B4D52AEA9BA08BGGC49EGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGDA8BGGGG
**end of data**/
}
}
