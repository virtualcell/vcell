package cbit.vcell.client.desktop.geometry;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;

import cbit.gui.SimpleUserMessage;
import cbit.util.EventDispatchRunWithException;
/**
 * Insert the type's description here.
 * Creation date: (6/3/2004 11:48:54 PM)
 * @author: Ion Moraru
 */
public class GeometrySummaryViewer extends JPanel {

	//
	public static class GeometrySummaryViewerEvent extends ActionEvent{

		private cbit.vcell.geometry.Geometry geometry;
		
		public GeometrySummaryViewerEvent(cbit.vcell.geometry.Geometry argGeom,Object source, int id, String command,int modifiers) {
			super(source, id, command, modifiers);
			geometry = argGeom;
		}
		public cbit.vcell.geometry.Geometry getGeometry(){
			return geometry;
		}
	}
	private cbit.vcell.geometry.gui.GeometrySummaryPanel ivjGeometrySummaryPanel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private JButton ivjJButton1 = null;
    protected transient ActionListener actionListener = null;
	private JButton ivjJButtonViewSurfaces = null;
	private cbit.vcell.geometry.Geometry fieldGeometry = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.beans.PropertyChangeListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GeometrySummaryViewer.this.getJButton1()) 
				connEtoC1(e);
			if (e.getSource() == GeometrySummaryViewer.this.getJButtonViewSurfaces()) 
				connEtoC2(e);
			if (e.getSource() == GeometrySummaryViewer.this.getJButtonOpenGeometry()) 
				connEtoC4(e);
		};
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == GeometrySummaryViewer.this && (evt.getPropertyName().equals("geometry"))) 
				connEtoM1(evt);
			if (evt.getSource() == GeometrySummaryViewer.this && (evt.getPropertyName().equals("geometry"))) 
				connEtoC3(evt);
		};
	};
	private JButton ivjJButtonOpenGeometry = null;

public GeometrySummaryViewer() {
	super();
	initialize();
}

public synchronized void addActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.add(actionListener, l);
}


/**
 * connEtoC1:  (JButton1.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySummaryViewer.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JButtonViewSurfaces.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySummaryViewer.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (GeometrySummaryViewer.geometry --> GeometrySummaryViewer.initSurfaceButton()V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.initSurfaceButton();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JButton2.action.actionPerformed(java.awt.event.ActionEvent) --> GeometrySummaryViewer.refireActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.refireActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoM1:  (GeometrySummaryViewer.geometry --> GeometrySummaryPanel1.geometry)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		getGeometrySummaryPanel1().setGeometry(this.getGeometry());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


protected void fireActionPerformed(ActionEvent e) {
	if (actionListener != null) {
		actionListener.actionPerformed(e);
	}         
}


/**
 * Gets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @return The geometry property value.
 * @see #setGeometry
 */
public cbit.vcell.geometry.Geometry getGeometry() {
	return fieldGeometry;
}


/**
 * Return the GeometrySummaryPanel1 property value.
 * @return cbit.vcell.geometry.gui.GeometrySummaryPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.geometry.gui.GeometrySummaryPanel getGeometrySummaryPanel1() {
	if (ivjGeometrySummaryPanel1 == null) {
		try {
			ivjGeometrySummaryPanel1 = new cbit.vcell.geometry.gui.GeometrySummaryPanel();
			ivjGeometrySummaryPanel1.setName("GeometrySummaryPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeometrySummaryPanel1;
}


/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButton1() {
	if (ivjJButton1 == null) {
		try {
			ivjJButton1 = new javax.swing.JButton();
			ivjJButton1.setName("JButton1");
			ivjJButton1.setText("Change Geometry...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButton1;
}


/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOpenGeometry() {
	if (ivjJButtonOpenGeometry == null) {
		try {
			ivjJButtonOpenGeometry = new javax.swing.JButton();
			ivjJButtonOpenGeometry.setName("JButtonOpenGeometry");
			ivjJButtonOpenGeometry.setText("Open Geometry");
			ivjJButtonOpenGeometry.setActionCommand("Open Geometry");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOpenGeometry;
}

/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonViewSurfaces() {
	if (ivjJButtonViewSurfaces == null) {
		try {
			ivjJButtonViewSurfaces = new javax.swing.JButton();
			ivjJButtonViewSurfaces.setName("JButtonViewSurfaces");
			ivjJButtonViewSurfaces.setText("View Surfaces");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonViewSurfaces;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButton1().addActionListener(ivjEventHandler);
	getJButtonViewSurfaces().addActionListener(ivjEventHandler);
	getJButtonOpenGeometry().addActionListener(ivjEventHandler);
	this.addPropertyChangeListener(ivjEventHandler);
}

/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GeometrySummaryViewer");
		setLayout(new java.awt.GridBagLayout());
		setSize(877, 471);

		java.awt.GridBagConstraints constraintsGeometrySummaryPanel1 = new java.awt.GridBagConstraints();
		constraintsGeometrySummaryPanel1.gridx = 0; constraintsGeometrySummaryPanel1.gridy = 0;
		constraintsGeometrySummaryPanel1.gridwidth = 3;
		constraintsGeometrySummaryPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGeometrySummaryPanel1.weightx = 1.0;
		constraintsGeometrySummaryPanel1.weighty = 1.0;
		constraintsGeometrySummaryPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getGeometrySummaryPanel1(), constraintsGeometrySummaryPanel1);

		java.awt.GridBagConstraints constraintsJButton1 = new java.awt.GridBagConstraints();
		constraintsJButton1.gridx = 0; constraintsJButton1.gridy = 1;
		constraintsJButton1.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJButton1.weightx = 1.0;
		constraintsJButton1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJButton1(), constraintsJButton1);

		java.awt.GridBagConstraints constraintsJButtonViewSurfaces = new java.awt.GridBagConstraints();
		constraintsJButtonViewSurfaces.gridx = 1; constraintsJButtonViewSurfaces.gridy = 1;
		constraintsJButtonViewSurfaces.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJButtonViewSurfaces(), constraintsJButtonViewSurfaces);

		java.awt.GridBagConstraints constraintsJButtonOpenGeometry = new java.awt.GridBagConstraints();
		constraintsJButtonOpenGeometry.gridx = 2; constraintsJButtonOpenGeometry.gridy = 1;
		constraintsJButtonOpenGeometry.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJButtonOpenGeometry.weightx = 1.0;
		constraintsJButtonOpenGeometry.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJButtonOpenGeometry(), constraintsJButtonOpenGeometry);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Comment
 */
private void initSurfaceButton() {
	new EventDispatchRunWithException (){
		public Object runWithException() throws Exception{
			boolean bSpatial =
				getGeometry() != null &&
				getGeometry().getDimension() > 0;

			getJButtonViewSurfaces().setEnabled(bSpatial);
			return null;
		}
	}.runEventDispatchThreadSafelyWrapRuntime();

}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame();
		GeometrySummaryViewer aGeometrySummaryViewer;
		aGeometrySummaryViewer = new GeometrySummaryViewer();
		frame.setContentPane(aGeometrySummaryViewer);
		frame.setSize(aGeometrySummaryViewer.getSize());
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


private void refireActionPerformed(ActionEvent e) {
	// relays an action event with this as the source
	//fireActionPerformed(new ActionEvent(this, e.getID(), e.getActionCommand(), e.getModifiers()));
	fireActionPerformed(new GeometrySummaryViewerEvent(getGeometry(),this, e.getID(), e.getActionCommand(), e.getModifiers()));
}


public synchronized void removeActionListener(ActionListener l) {
	actionListener = AWTEventMulticaster.remove(actionListener, l);
}


/**
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(cbit.vcell.geometry.Geometry geometry) {
	cbit.vcell.geometry.Geometry oldValue = fieldGeometry;
	fieldGeometry = geometry;
	firePropertyChange("geometry", oldValue, geometry);
}

public void setChangeGeometryEnabled(boolean enabledOrNot)
{
	getJButton1().setEnabled(enabledOrNot);
}

public void setOpenGeometryEnabled(boolean enabledOrNot)
{
	getJButtonOpenGeometry().setEnabled(enabledOrNot);
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GEFFBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BAEBF0DC55F9AE98629F26D186C3CC71940608D4A18AB829B3B50DCBDD46B3D50ACA08AAD237482DD2A8389D11F18737B12DA7BD7B9056DA12A530F1C4B16084A7C8D8DE7430ADA14B369EAB2455832D2535AC0FCD70556E15F625FD696E554BDEC97D3E7338FB35FBD7B21E30B35F5CFB3EF33E67794EF73EF36E12C36FBDD8E8B29FA244D4C8782F41C4086FA3C25EF978697B852E71039C9B517DFE
	8570BAA935DA615985FDD997F3EC65E4E43A885AD7211D6F4E317D925E8F12316959937012C20E8B7A1EFAEB78FF161773F2C44859C75BCF58CA60F9G609A60701979814A7F092D0263D7F33CG1319107515B866BFECC7B8EE970CA9821C9B40D749462F02A7D3600E26C6BE6E6A57B2486E3FD14C2548C747D3C0BE2735D63167D1725F264CAA1475FF7BCBB8CDB334D5GB45EC21ACF1CC31CEBE26C1CE7442F5A27BDF2A0E077847CF2C835FB6568049A0E584764F0D0D615392626CE7EB6B8958CCA4A5C10
	DF1E1195C2AABF24BE5FDFC4F902BD40FF9345597CA8B7G6F2B81B6FCA8704FD07C9EFCBF1EE32BBCAEECBD7BC1B337E578461F115C3A2035AC052DAD47190D3E63F1DB3347F7185F2273F2C2C17F27C3DF96C09EG294E319D85E881F81B464349498F6159BA2236070341F028B592E98BF8153E30D78E40FBD39374F05C006C89ABDEC2581C9F5DDFED204FF240686CDF180E79A423DE4CFF664023A46FCF6FB56FB370C9DE468D73F0B573C524BA6E93A6FBAC256C346A257139D476A522171DB5792C2550C0
	768B3FFB56124E79EC56495ED90DF3BDCF67FABB3C1BF8FCB07CD7A83E7AC49C4FE2350C62315D8CFD8F1E9031B1FCC06852EBFD081457FE5D508F6B1FDCEB365A198EC76C093AF40DA36FAEBB13D92A69F2096283BABC5365A7D497EC27C15FDEG96DF27D5DC3B3E0FA02E81B6GEC8248855885E02D91B1B6BF7951CD4618CBD25436A9FF408B8B0A511ED68E43D31ED59549990A2AD248A38FC90ADF9A8948D142471CD75087A3CB689CB476BDA023D38E490A247A43A160AE414A1515A89B53273C0EE39419
	763A7DC1998484A3C4703D289CC4FFC8D175E244AB29F2FC846B7FCC41B919160600A3B000F766974F947A6B98585F8C502B79E1BB6557A3ABA1B95054549B8ECFCCC55CE88959D003FEDE2039639A3C4F56889FFFCEF3F8BE3477E8FC4A22EF6878B87C9ED49FB211D8E3377BB1BF17540A187959300899C769F7C875337798464C70674FDAEAEAD92CF455A62E1D3BA8CF540B355F748F6BE8CBEA0D574F2D824F95CBE4D8FE0F7C8FE8F37537B3B8D79150FFADC0E62D70F1F774A80B59B6A9AA0BA86DD7A040
	E4EF5C63CC4FCF47B1565057D5BCF65E81A886F8CF3365B3BAE69B70AFGB8D48731816085D8DBA7FC1A673FE620270D73DA4E96A173BD033EAAC35F7E5CD01665A6E46D63BA159A74959A68F510342838FF4953301DBA64089C7242CB1477F52A480B2F6F5130C653C7719148876D30G239AFED045B5AB292A629F19D265C1D9D57D2131F87F95AAABBAB592092792A1EBC4C57D42B3A15C2EB9EECC4535648907C2B21DD40D04777B69DA1683F29014EE8FCF05D4068FCF61FA181067F8AE09CE3DC6B0978426
	B0AF313AE223043E0CA8FEAC93B03591EBBD2E73FB76D768FC3489669F6B236D70AC2893EBF666FD31E60FDD7F96597AF944EC34672CFF782B665EFA36762E554757AC5349BBED2C7BB84DA99C9FE53A9EF33F894F94258DDFAB2F4DA13FD5A0E79DC0E63DD8ABAFCEE33F48C7FC113407838166D24D57FFB7D30EF3A3CFCE491046B17175E87E7B4719B7927A0638AFD97F3F4C20AF833A1C99BB093E3CFBDC540F5BC01FBC003D754C1F4DA719BF5838CB93E2DC9634738148A923F1EF87443896689FBE15BCAE
	6F940E7B11B6EE16772741B39DA06B949B67BE0D639E880AF11527591AEFB99D9FEF57648E06443887F97FE67E4CBE4D7831F9FA258AE307562A635234B4EB0F4EC07CB7B5F53BA470BE2BC17F176EA3B83E603428B50F9F20FBD28368G60EE90F17663D2D1EB2E1B48A069CA2EA5D5DD9DE8E0FA24B544632C7A5A0C0545066BBA4AFC867A729BC4DCFCB62B5F6FDA23513047CF37DABE27A5F37AB8E43857677643F3542FC4D5A7A40AD9B17F3F1CC3FDF1AF66E8DE1BEC21B58B5A3A9B64D6B7C22D5408782A
	21C3BA5D3D0DECCFDD8BCF8B65777EC49D66919A20F676F0494AE8D8894A5ECA742CDD1AD16DB4E759DBE9F7873ED709F938B0F3921E0A8C1A4A2D89643D29694FD48DB13FBFF430056B167D68C324F971F7E6239AFFCD194F3C3311793B7C63385F377EF807B9970079E055047041D598336EE361036A18118F32BF96BEF86916FD4064BA82C2EEBE702CE892F28FC70C643A201F516D89085A568B388DCDC21F4F03988F622045F2650B877439B2370971BAF4864ECBE7845D1AA5F44C3F5FF9D76F5FA2698EC3
	7F2E7D37B1F34B99FE06BA9377EFE54E8EF3F30E70EFD6D0683B91466CBCA34E0D074E611366FCA24E8A314BC26E2A6CCD246260DD06F2291CE6DEEBB547656E7E679D6622FCA1B796927EDD8FE31EE99676BE9F525B4BF439510ABE77077C6A6014B2AAF96436A9D58D07AA2B0658FEEFADCEBC7B941D0D57ECC74EA657EF6CCC70G4D9BB5E75919402661B3A93ED707E7BA57D2BC36FD5017FDD654313747F09EEB4EC1BD8C60850881ECG58F2CE640B87666A498A6784E7884A77109498DC32410BFA7DBA7A
	8776E45E3EFCDDBFBF4C8FCF0EA77AE17B3925366FCEE8B33AE712685ECDE8D71CDFCA476625EC906DB74857ECFE0838B738B6A4F2F64DF9111B67BF516766ADE57366629431646332571C0F47D279277F45F37309855CDB32206F85003DG95AD308FG8C8344DA047F1F3FFEF6A57F5384608E37832E8B766276443AC973571D93376667F5C9F409FE5E5032140E794F93937E5B54A26A696E402D69B01CC41728C301218EFD11A24E435AAAF478C5922FC4B05621A43892DDF32BD13C3DF5250EA45FE509DCE1
	9F1B72A7DDE0514AC15870EE19383FF2370A5AE17AA22E693550CE875852AA56F47AA246D5AE34F781943565580E81F481F8815634E9F7CCB74E5CE4DC31DDA6B92A18CF1EDE51A7463E4CDB11AE23EDA99D336F18896D5B8AFDF9G3B814A5BF3ECA781DCGEA3B306F4E9BEDAB5927DDBD000D626CE230F8183E3705EEADEE5312686C896D356DCB69583D435B45B8676950379900617614A0AE8F5A3B5BC5ACBFFCG6B778A87648EG97C044A17A3EC77B307E459A9C6BEB2C1DD9DF43817DB99E697169F244
	F5E9F6A46FBFABC10453B0992E83C6F7857B9256479715E163CFB64FEBE3294F31F7470C4F3177450C4F318F44DEE7F860213F75927DFF92DB7E1C7BD84C681C7B8E357D07074BA86FE8D40E52056510D409773D2462B9AF693C7BA9C4A7775BC1726A380D0F69D0436D351A0F65A123F5144667A15DE08E59BA7A96FEE8A01B21BF9720G405A813922C32C1F0E7995777D0E59E5767D06055473F2EE41F8DE9C8BF84E09062794AC15167A3EE721C26B0BAFD227AA8751CDEC0C1B522B12B2A6A74D5D95CA4F7A
	D251FB97D24DAF1B033F18504FC11DB6873E18519CB8BA186F07BB126700717B03001E5FC90D5FC04408DF0073D9451F6BF4FC993FD3C19D3F3EF80C3CDF36DC2E4DB0500F55137FCE7718AD9D2C1EB4E978D7A8BECB07673571C571ADA7977A9ABF91317468E26AD8787E22F1AC7CE0B1F5ACBC3938F2ACBC3518BA96F296D70E056796170F0542D0B17735EBD48987DD351C375D145A56D3A6E3DB4F18D25B5AE6DA59569ED3EADB87CCAB5BFA4504360C783DF0EEE637C34C4647C37A782C57EC2FAB46FB3500
	B4971ED2614C5209F36FAC96E7428DB06F1BG72GF6FD424E062C8E7AAE3DAF9A05DD97DCB99811BD6485CADBD6AAE8DD5076822C86C8875856A94ED79BCF995C0551AA29D6534B77A66E0F3C0A2FE538410603F19C5E1D7123D814537419F47D7DB0E1C2845F07E3748CD05D857BEB175807FFBAA476612EAE91635FB5271E774DE663F9FF42EC3C8FEDB3AF3F8FE51BD345A653EFB1A6F4DE2F691C9F93BA27EBB8BF2D0DF7C35B6A943827A3E2DC39067B2A0FFE8301F6CC43B51509F1EB1C222658F511E8B9
	67D55D7BED9775F5406F9B180C7B3FD499AC3FCD960B7C369DECAE6F96F1DDFB8DEBD26DBC09B7B4D050ABFEEF1BB456CBD7892B639F27B59D52F7F50BBA7EA54AF395345783EC6C96674C595F0AF3E666B79FA26BC346F700796EF9F3D6B74B5F855D71F3E67E788EB313F92AD4482C6C4931DD6B216732D27DDD902E0347AC2CAEE57D77AC6947D8DBA47A7E3FDE520F7ABE771A3E7F55A57D68238FB8BD336D295F885B76BE3511843E3FBA657F86B2FA18CD597C197642FD261D0FDD373035FB77905E772B30
	302C3D781F0822F6641E56CBEB65A1876751551B3C3F096F3A822E4218C8AFDB4B58373AB73E37B15EAD0EB86F1F47846F6D863CAD89D040F93F4979113EB8EFE6472A48D2BBBC9DF1D97F9A9132CA7A12E595A648BA8AE3CE823CAF62ACC956B7FD68EB091F69A00B063C1A0D5F7EA4AE77C219106BB610ABF86E63FCFDB0A65647EC44F61ACEAEDB1FB3C9F7DDB7372E6F38C53AA73EB09D2BED9EAF41753821AF3166F91262B37A92EB1E17351C30897AAABE956770BF6C44BDAD866D75G9B8132GF682D87B
	451EB1E229A1AB54586EF02497B61500EE9BE076B5155E5AFFB67EEA1FD8FB1B7E7C913279CEC74A7B2B0AFEB66F0D7CD9797CFDA6FCB27BFE65C47BFC50379AA083E0ABC0AEC001E65FA316EAE6DF07572F4291DB727868D785E19CEE47F1B49684CC479FDD517F2F287C0F82E6D38A9D090B69D662525D93533CD7F5C95CBD35C05FFBD49F4890C47CF61DCF4609F5F02107E87E7AE909705711EFFF03B87E5EE196EBABD117D92EC316CE176C60D18B5327F2DE68C3AE9869A3E47F20975F994D61C9C153E8C8
	331C8D7F549767F3DB19609339E9BE3B4EB3BE077B3145E26EAFCB626FD93A77D70A3F7C7D3D24644B144162756B1D29EB38BF6EB42E6152BB0DEB38C7BA173FAB78DEE76AEF620FF76A3F09337576E2BF6E2999B0FF5BGF6826C85A89F90EB2D336B57042EB503F556619D4333837ED3C79572BFF42636355639727FFB1A1C78CDF0A48CE5F39079EB7FABF062FC296188C5723B84A77AC8710F79D4B6D41C691D98EF81F91422191D168BC46BFF4E799BF88EC963BCB3E29A89AB422AEEA8357AC74661446216
	4634B831DF526745EA70CF4B407277D8CC2EFDG77BBB70C0DG2C834884588A309D20G008C66588E81D483B8G068182G2BG52G5281B68F0A79383727AA156E006445A11D94C5726334F07DCF4C6B754F86FE79034B6B4F685E1E54536D861AA2774D501DAD57539D811A1A956818FDBF6BCF9DBF7F593FF27C7C221FFEE706F9F74FC578B9546242F3E5F8F4B4AAEBF1DA64AA57F04E28AB9C75E365424EA09DB447BB5CECCFE638EE0A3B4AF17C8CBD10DAD767404A3A8E8C98477A70C0F22CFFB6E09C6B
	1E81FD2CB31CEFG577998444230B6AE2861AEEBB80572C45CA53FD775E978D92A97623BE494A8708BD42F990A9B66385BF5384B9C37E6G7D8DA67B420A203DEBGEB51D1FFA0405ADF23E37CA1F03F660FFB290E7E0824537BFE8DF7D91B8B47FC727CB867757343F0F51349639AA61347394B45385BAD82F739DC0C6BB009FAD87AED89E15FDD7DD2003A0C610F4C16467142E5047D877B9E73BF40B373E5D22812B75AFDD2E8CCBEAA2E826CF63BCACA31DAB9AAEE8F200D17855A1044F6D4A1E55A57FAA775
	DE7EF4FD136579EFD84C446AA42F31E8F292AB060413945208F1928BC453AC7E5B512A0768A4D9D80A7922D8A26A192AC5FDED35678E0F61FF18E8CDB5446B1821F8BD43F22535107A9C7033C3A25F392D789776D439FAB0A885822A9418E88DF9B1EF374D29F2FCAF3495A27F289843BBD9DFF9A16ABD2E2379FFD0CB8788D74168EB3990GG10AFGGD0CB818294G94G88G88GEFFBB0B6D74168EB3990GG10AFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB85
	86GGGG81G81GBAGGG7390GGGG
**end of data**/
}
}