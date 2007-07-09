package cbit.vcell.client.desktop.geometry;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.media.opengl.GLJPanel;

import cbit.image.VCImage;
import cbit.render.AxisModelObject;
import cbit.render.JOGLRenderer;
import cbit.render.SurfaceCollectionModelObject;
import cbit.render.VolumeMIP;
import cbit.render.objects.BoundingBox;
import cbit.render.objects.ByteImage;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JSlider;
import java.awt.Insets;
import javax.swing.JLabel;
import java.awt.Component;
import java.beans.PropertyVetoException;

import javax.swing.SwingConstants;

import org.vcell.util.UserCancelException;
import org.vcell.util.gui.DialogUtils;

import cbit.vcell.desktop.controls.AsynchClientTask;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometryTest;
import cbit.vcell.geometry.gui.ResolvedLocationTablePanel;
import cbit.vcell.geometry.gui.SurfaceGenerationTask;
/**
 * Insert the type's description here.
 * Creation date: (6/3/2004 6:01:55 PM)
 * @author: Anuradha Lakshminarayana
 */
public class SurfaceViewerPanel extends javax.swing.JPanel {
	private cbit.vcell.geometry.Geometry fieldGeometry = null;  //  @jve:decl-index=0:
	private JOGLRenderer joglRenderer = new JOGLRenderer();

class IvjEventHandler implements java.beans.PropertyChangeListener {
		public void propertyChange(java.beans.PropertyChangeEvent evt) {
			if (evt.getSource() == SurfaceViewerPanel.this && (evt.getPropertyName().equals("geometry"))) 
				connEtoC1(evt);
		};
	};
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private GLJPanel GLJPanel = null;
	private JPanel jPanel = null;
	private JPanel jPanel1 = null;
	private JButton resetButton = null;
	private JPanel ivjJPanel2 = null;
	private JSlider JSlider1 = null;
	private JButton ivjApplyButton = null;
	private JLabel ivjJLabel1 = null;
	private JPanel ivjJPanel3 = null;
	private JLabel ivjJLabel2 = null;
	private JLabel taubinParameterValueLabel = null;
	private JLabel ivjJLabel = null;
	private ResolvedLocationTablePanel ivjResolvedLocationTablePanel1 = null;
	/**
 * SurfaceViewerPanel constructor comment.
 */
public SurfaceViewerPanel() {
	super();
	initialize();
}

/**
 * connEtoC1:  (SurfaceViewerPanel.geometry --> SurfaceViewerPanel.surfaceViewerPanel_Geometry(Lcbit.vcell.geometry.Geometry;)V)
 * @param arg1 java.beans.PropertyChangeEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.beans.PropertyChangeEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.surfaceViewerPanel_Geometry(this.getGeometry());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
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
		setName("SurfaceViewerPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(469, 560);
		this.add(getJPanel(), BorderLayout.CENTER);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SurfaceViewerPanel aSurfaceViewerPanel;
		aSurfaceViewerPanel = new SurfaceViewerPanel();
		frame.setContentPane(aSurfaceViewerPanel);
		frame.setSize(aSurfaceViewerPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		Geometry geometry = GeometryTest.getExample(3);
		geometry.getGeometrySurfaceDescription().updateAll();
		aSurfaceViewerPanel.setGeometry(geometry);
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
 * Sets the geometry property (cbit.vcell.geometry.Geometry) value.
 * @param geometry The new value for the property.
 * @see #getGeometry
 */
public void setGeometry(cbit.vcell.geometry.Geometry geometry) {
	cbit.vcell.geometry.Geometry oldValue = fieldGeometry;
	fieldGeometry = geometry;
	firePropertyChange("geometry", oldValue, geometry);
}


/**
 * Comment
 */
private void surfaceViewerPanel_Geometry(cbit.vcell.geometry.Geometry arg1) {
	
	if(getGeometry() == null || getGeometry().getGeometrySurfaceDescription()==null || getGeometry().getGeometrySurfaceDescription().getSurfaceCollection()==null){
		joglRenderer.setModelObject(null);
	}else{
		SurfaceCollectionModelObject surfaceCollectionModelObject = new SurfaceCollectionModelObject(getGeometry().getGeometrySurfaceDescription().getSurfaceCollection());
		BoundingBox bbox = surfaceCollectionModelObject.getBoundingBox();
		AxisModelObject axisModelObject = new AxisModelObject(bbox.getSize());
		surfaceCollectionModelObject.addChild(axisModelObject);
		joglRenderer.setModelObject(surfaceCollectionModelObject);
	}
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G670171B4GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135998DD0DC459989C9C80206E63092CDDB1A268EAAF550460ACE5AD11B36E94D34E41A36EA19E926135A58C9559919CEC6B363F2C00801981220460A8D35EAC9C442DFEAA4D8A19085A515897402B51A3C3B5BBB0EFB775E715E3B5C9D10506F5BFD7B6EB86E40460619EF76767BF6777B5D6F7B5EC2EA8E66141758EA88A9A9A7665FDBA504147D15105CBFDCA7D0A4D331D71218BFA0133D64FBD2990C
	B9C0DBF30D3D321A2CBF3393662BE1BE8473976077FE3261CC56C17811040F8C34546D8F6D1B1D4FABCE4127024D6FBAD2856333G85004331CCC57E9B0E9CB071C5A6DEC0090D102DEC4D56A335A62E97666781F2815628FC7DC6981FE11C67379FB3571D7DD2AEA97E09E65B0D67441CA920AD40778E1BE39DF932040CA12F671A2A4CBD19C0DB87004A171271BF9F0731E3406779275361B59AAFB829ACB7BAE5AFD50CC69755FD069AE874D055CF8DAD525EBE98545C12135A3DB4C43581C921B219E467FF5A
	34131DC7C88F4CF7EA08DB60C41E77407C810047AD7CA997627B61378BE0D293FA3E73778ED30F6109BB49E64F8F1E5913C44FAC0D6B37C60B6AD9F758D17AA273094305365F8634DA5DDE5984B084E0GE4813C44E2416BBC8CE3174368D67DFED5698A84CE48AE6D146A22B27CEEEF870A09BBCD1D2A66A2047B7B25260684724C86FC5F217FF99F37C9EE2B70FD5E69DBC951373FEC2BC8E013225C89DB1E4EED31D60F5A0473F6A4653DD91F9E1B53F91FA1313C49A3AD956589F8577E3225E21BF9CE159165
	5DE1202F1FE4B150G3FCB4C786078B29683A9B1F89E2799ECBD4EB301D6EC085898FED64852DF3612D4B7DF17508E5977E5580603DC06C0B0DE16FB994F9541F8D96EE7783CE03CAC03AEA14B9A2055DD30D772787239715E2641BC8720G209060F1002A902C8B09983B5DF964F34658002499A702DE59C5B542777ADC4F43C84306A675A93AA1A9F069A44DABB9E42A93F30D61C29BB8266D0B3058F7820F7729C2B549702A8A1CAE417D251A4E57243A76629A0DB26A1057CF0101BFC044390BDD7BE114A55D
	78F740A599B43A0253173950A797A4BB1C888740EFEE17EC8F5A2B93749F8698316C301B62F99FD28D52C5FBFB3F2A7A0201A154049404504E9116BBA661F7D6C858B8EB84F15BE1DE9396677450FDB16774F81DA83E24C5449DA3AE4C4D75E191B30F8E0B1869597DAD52700BEB9346CCA644EFEF18474A79F07C5D59454EC43978FC19F3F80EFBDB9FCEFC27BEFF2E38E5CE9E2517F97E33162FEC23682BF410F59540DA4B46D3340E476C89C927A2CABF52A04028ABEAF1AE670F18EDCAAC39B730F9D1D8586A
	E357F082FB7F3F007350980FAFE20F59005BA46846BAC4AFC0C96C21812A386007EE52A65D98EB66BDF52B561ED4068F403D6E060B24DB78F4B75EBD49B0B42FA3E850C1EA98DE459325E7B2DEFA5091082684416BBAB766DFB584775ADA3F420DF74229AA8AE54E313618742F32BBC9E56A87213B5520E2F07CAA8F7A4AC7A3E6CE48756CA0F82765A066975E9B64CB58G85B42F9FDC0EA90A8CC5703EE6B69D0831D199603047290731A3A26EDE68EF626E3DBAFE933967DC401628F6E4FFF329EDC5045F177C
	C8746EF119160FA4167DEBACB718F81D4B7AE2B55AA1C1FBE2467BF19F1ED588BCB683EC0F08F8FF490BF411D34CC06FD6E5191B5332734E51EA748B9D8BD2C845183CBE34EC37E7F4DF9C4DEE5A1153DF98C5BB4AB1F9AFFD9C6DE823227F2B9967B6ECB0474EF1EE8B2E5B9307505E2CC79B15AEC861C6BD84B1535E7E81531077DFCF329C0A6BB70E0BBE6B75B65439EA42DE79AAC04704704D065D224F4A72651222C71AAB127513C393DC0E70C454B72B9F6B5A41EDBADFC61E85C0DBB7A16C39ECB4B657F7
	693A6A7432B2E35A224B976BBB0EFB4F378BC637D7530DBE38DCE1E137FE9F4A0BF548C41BF5798DDAE56ADA84FCEBA66D15251CB74533C5334AEDD74CFACAFDC6DC1CFB5FDCD65F9F53770A3D0D02FA7C101D5BEC657ECE53A66BEFDDC9BA26BE33A56ACD53770C587AA7392D0C49284DFA17B9CA4F82701C75740CFE6C1E49E84D58B8B933FE70BE65FA664FAD13717D4BAA065FB6995F3FB434097EE567C5FB25FCD154569A8563299066DB814AAEC11F8C5081F076127063C6DFAB1923F769D32025D0A4F9F0
	DA3292BD04DC4A730BB93FC6AE977905085C5DCC636D903EB4DD772C38B95F776E0CFDFF091B97446D63FAEBAA5E37C220ED85A81B02DE8D208760BCC05A14507D007F1D39F4BF0945E0C86D86DCAF5C6B6E787C0BB2305E6679E60C49D5F0F6818243BD510238A21897CF09DA1953067978C00A185F4C66ED563C25CD748438664A54528FAD9ED7221EF25B3E2CA42F252FAA09EB69E1A5F1ADEDD276F2BC1C915BC18B7AB1E576DA5B29A42A35AFB37DFF7C7C9EF6362ED31DB93BC7B2A41376FB63B91220667E
	8B1AEC53F67B4988FD46DCF352D03B1BAD1E13EAF27D67290975DF24E2FE565520662471BAE62A87ACDAB48A7B8C6AC7F14CBA4A769B126621B3EC144376F3DA327DB729496C48F5FD3399FB87D90A28C1833EBFD3B046FB1AC50FB08C73GC0B6C0DE8A6F71F9EE3EAD04F5518D9FA7F05CE000BAC94ED538F7D72B58DB8573BA00CE0073GA9D709DC7F70F1E6931E57CF3A3C060ABA717E6457ED317D49162BB84F3AD4FBE59BG4F0DF233F88F6987DCDA2A106961C654592A350DD248E8EFBF21E2DF504F95
	6475F6B53B5738FFCD2A28373FE5E796433CEA9E545CF9225E06DF9775B66F46152470F7096B6D26210B360EF966F7483CE86DE8BB6CA865BC1BDA854F95C05FB88FF11F3556441C0136410730C698B9BDF39A9D7DF80844526F1BC6C7F9EFA90D2597CF2317476C673A5D7F1A50ED7B7D79C43EA3AD695BD4ED9A5729539C6BFE3A3C24BAFBE087373B6F1DB7484CF7A82B9EFB02DE6B714974B637497E23BBCDD942E9428FBFF7209F72E13E8EE0D31A4821DA73653D5D24DC663E3B7E67FD3C1E2FEEE175BC0D
	5773928B7FDD06AF1B9F4573D85EE245F2B5500C79224EC746709EBCG73E2002A859013GBDG679708DA579FE8A2F3543AA1B5508F89CB0EC931DC3F232D177726F5EF05081955F75FCC8A967458AA13440C310047CA16B9962CDFDE922D67D5BA6A3796E81BG4A96C29F835084503BD0681795E8607AE154F49906649CE11D27D08EDB42A89A1390576D40D1215B5905B36BEDGF0E9G59B1B46E2377597BD55E42F85F8DB0FC4142F85FBD68903EDB8B347AF4912BF73790AB0E3E9F737B5166395E863E88
	F09E95ADD7120779FDBC163C8EAE9ACBDC87170EA56E832E9D1B3D4EFFE5AC79B7752A3158EFEA9ED7278D4CF999600BDC0075G1BG0A53C5CC35E82F7018CA90CFA7DD9E2C4378E2E7887E4F6949F52D5567FE67FBCD476FA307EA982A9F4F9778B7F47417218698526C09F4341166750C98FC29097F070EF9D226EE0666FD689116BB2B9741F767A2511B5E60903DE92705FB41497642BC4D42EDE66B32E11E3FC8446CA9E7B4CE6DB13FFF633872B13B6D0A7260B67C0F115C0F12B1379FC78D766D8B769F0A
	844C5ECAB530F7D25DEE1DDA71E29855962ECF9FD0F5AF76E43C6FF96FA8565700C65DD45328EB50BB0EBE5E3E88FDF20E79F3AD7CAE8498D90C385B9DF5268E1D0BE76631A193474FEEF4E29E76CBE12FBF68E7A773F35FE37E4F0735EB960B7E6BA987DB6BD562566EE7EB37403AE2003AA578BFB4578431F8E4B2F9949A2A936DD5E30F97EE90BEA45737363D5DD0575254F40857091CD06C7F3D0E6DCACD78060460DAE23ED318E346534BCBF8FF7010C33C0B64AF91326FF3224D3BA9FE3F0A1A942222A655
	5E728DD2FB305336B7892FADA60F1A0CE80F46C6266B310BD1DD9F1CD55723CDDCD73E6F13CB51FD5F19EDDF4B31379A6A1A5FBEE65A68E71F445A287AFA59D612C46EDED35E219839F93EBBB505B6CA877C2A8C1163EE1A40FE4CAE0D1A9D81EF3AD816836400268628E6C4B8163DF6D9F7BD36A7D88B67BD10B17B77AA17A105204EDBE16D4E2BA19F8174G8C8384G52GD6DCADE4FBEC22FE9659F8334EF230A6F995031FFFBBAB39667B612557126D078C7E81587CA347A8FC888DC99EEB4F69A914552AB1
	DCE513767194568B388A06D7123D636C39EB9096AA9E1EAB7F44E230G64FF37CC3C8BBC45F005000B1409F316106479E59919BB3F5CC09257092F1319F562C61238CE641158BA41F1371967EA56C7AE475FC9B097716F3ED312A2F9D88D657D3C74FAD554A6E73CAEE30463EB433B2378DE0A7CE14E7FBF6FBEFF96463CBF12F2DDA37BE646FE57B58F7EAA6FDCCF0D0D587A480E9136390F14077074B07E7BEA9DC47E277A98E1F7094DBE33E6BF0459C1FEE78ECD455E19ED731DC9738AD9EA5E752571F7668E
	663F81404BCBC5DC16BB5E1CA52E867D12AC9B126C6BD2DC98E3A7A2860D76A8F7BB70FCDD2CB1091CF62F83659E0D5973DFD0CB87882A17D62C088CGGACA1GGD0CB818294G94G88G88G670171B42A17D62C088CGGACA1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG428CGGGG
**end of data**/
}

/**
 * This method initializes GLJPanel	
 * 	
 * @return javax.media.opengl.GLJPanel	
 */
private GLJPanel getGLJPanel() {
	if (GLJPanel == null) {
		GLJPanel = new GLJPanel();
		GLJPanel.setPreferredSize(new Dimension(300, 200));
		GLJPanel.setMinimumSize(new Dimension(300, 200));
		GLJPanel.addGLEventListener(joglRenderer);
		GLJPanel.addMouseMotionListener(joglRenderer);
		GLJPanel.addMouseListener(joglRenderer);
	}
	return GLJPanel;
}

/**
 * This method initializes jPanel	
 * 	
 * @return javax.swing.JPanel	
 */
private JPanel getJPanel() {
	if (jPanel == null) {
		jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout());
		jPanel.add(getGLJPanel(), BorderLayout.CENTER);
		jPanel.add(getJPanel1(), BorderLayout.NORTH);
		jPanel.add(getIvjResolvedLocationTablePanel1(), BorderLayout.SOUTH);
	}
	return jPanel;
}

/**
 * This method initializes jPanel1	
 * 	
 * @return javax.swing.JPanel	
 */
private JPanel getJPanel1() {
	if (jPanel1 == null) {
		GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.fill = GridBagConstraints.NONE;
		gridBagConstraints.weightx = 1.0D;
		gridBagConstraints.gridy = 0;
		jPanel1 = new JPanel();
		jPanel1.setLayout(new GridBagLayout());
		jPanel1.add(getIvjJPanel2(), gridBagConstraints1);
		jPanel1.add(getResetButton(), gridBagConstraints);
	}
	return jPanel1;
}

/**
 * This method initializes resetButton	
 * 	
 * @return javax.swing.JButton	
 */
private JButton getResetButton() {
	if (resetButton == null) {
		resetButton = new JButton();
		resetButton.setText("Home");
		resetButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				joglRenderer.resetCameraAndTrackball();
				getGLJPanel().repaint();
			}
		});
	}
	return resetButton;
}

/**
 * This method initializes ivjJPanel2	
 * 	
 * @return javax.swing.JPanel	
 */
private JPanel getIvjJPanel2() {
	if (ivjJPanel2 == null) {
		GridBagConstraints constraintsJPanel3 = new GridBagConstraints();
		constraintsJPanel3.fill = GridBagConstraints.BOTH;
		constraintsJPanel3.gridx = 1;
		constraintsJPanel3.gridy = 1;
		constraintsJPanel3.insets = new Insets(4, 4, 4, 4);
		GridBagConstraints constraintsJLabel1 = new GridBagConstraints();
		constraintsJLabel1.anchor = GridBagConstraints.EAST;
		constraintsJLabel1.gridx = 0;
		constraintsJLabel1.gridy = 0;
		constraintsJLabel1.weightx = 1.0;
		constraintsJLabel1.insets = new Insets(4, 4, 4, 4);
		ivjJLabel1 = new JLabel();
		ivjJLabel1.setName("JLabel1");
		ivjJLabel1.setText("smoothing");
		GridBagConstraints constraintsApplyButton = new GridBagConstraints();
		constraintsApplyButton.anchor = GridBagConstraints.WEST;
		constraintsApplyButton.gridx = 2;
		constraintsApplyButton.gridy = 0;
		constraintsApplyButton.weightx = 1.0;
		constraintsApplyButton.insets = new Insets(4, 4, 4, 4);
		GridBagConstraints constraintsJSlider1 = new GridBagConstraints();
		constraintsJSlider1.fill = GridBagConstraints.HORIZONTAL;
		constraintsJSlider1.gridx = 1;
		constraintsJSlider1.gridy = 0;
		constraintsJSlider1.insets = new Insets(4, 4, 4, 4);
		ivjJPanel2 = new JPanel();
		ivjJPanel2.setLayout(new GridBagLayout());
		ivjJPanel2.setName("JPanel2");
		ivjJPanel2.add(getJSlider1(), constraintsJSlider1);
		ivjJPanel2.add(getIvjApplyButton(), constraintsApplyButton);
		ivjJPanel2.add(ivjJLabel1, constraintsJLabel1);
		ivjJPanel2.add(getIvjJPanel3(), constraintsJPanel3);
	}
	return ivjJPanel2;
}

/**
 * This method initializes JSlider1	
 * 	
 * @return javax.swing.JSlider	
 */
private JSlider getJSlider1() {
	if (JSlider1 == null) {
		JSlider1 = new JSlider();
		JSlider1.setName("JSlider1");
		JSlider1.setMaximum(58);
		JSlider1.setMinimum(0);
		JSlider1.setValue(29);
		JSlider1.setMajorTickSpacing(2);
		JSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
			public void stateChanged(javax.swing.event.ChangeEvent e) {
				taubinParameterValueLabel.setText(String.valueOf(getTaubinParameterFromSliderValue()));
			}
		});
	}
	return JSlider1;
}

/**
 * Comment
 */
public void updateSurfaces() throws PropertyVetoException {
	if (getGeometry()==null || getGeometry().getGeometrySurfaceDescription()==null){
		javax.swing.SwingUtilities.invokeLater(new Runnable (){
			public void run(){
				joglRenderer.setModelObject(null);
				getIvjResolvedLocationTablePanel1().setGeometrySurfaceDescription(null);
				getGLJPanel().repaint();
			}
		});
		return;
	}
		
	//getSurfaceCanvas1().setOrigin(getGeometrySurfaceDescription().getGeometry().getOrigin());
	//getSurfaceCanvas1().setExtent(getGeometrySurfaceDescription().getGeometry().getExtent());
	//getSurfaceViewerTool1().setDimension(new Integer(getGeometrySurfaceDescription().getGeometry().getDimension()));
	getGeometry().getGeometrySurfaceDescription().setFilterCutoffFrequency(new Double(getTaubinParameterFromSliderValue()));
	
	java.util.Hashtable hash = new java.util.Hashtable();

	hash.put("geometrySurfaceDescription",getGeometry().getGeometrySurfaceDescription());
	
	AsynchClientTask repaintViewTask = new AsynchClientTask() {
		public String getTaskName() { return "repainting view"; }
		public int getTaskType() { return cbit.vcell.desktop.controls.ClientTask.TASKTYPE_SWING_NONBLOCKING; }
		public void run(java.util.Hashtable hash) throws Exception{
			SurfaceCollectionModelObject surfaceCollectionModelObject = new SurfaceCollectionModelObject(getGeometry().getGeometrySurfaceDescription().getSurfaceCollection());
			BoundingBox bbox = surfaceCollectionModelObject.getBoundingBox();
			AxisModelObject axisModelObject = new AxisModelObject(bbox.getSize());
//			VCImage sampledImage = getGeometry().getGeometrySpec().getSampledImage();
//			ByteImage redByteImage = new ByteImage(sampledImage.getPixels(),sampledImage.getExtent(),sampledImage.getNumX(),sampledImage.getNumY(),sampledImage.getNumZ());
//			ByteImage greenByteImage = new ByteImage(redByteImage);
//			ByteImage blueByteImage = new ByteImage(sampledImage.getPixels(),sampledImage.getExtent(),sampledImage.getNumX(),sampledImage.getNumY(),sampledImage.getNumZ());
//			VolumeMIP volumeMIP = new VolumeMIP(redByteImage,greenByteImage,blueByteImage);
//			axisModelObject.addChild(volumeMIP);
			surfaceCollectionModelObject.addChild(axisModelObject);
			joglRenderer.setModelObject(surfaceCollectionModelObject);
			getIvjResolvedLocationTablePanel1().setGeometrySurfaceDescription(getGeometry().getGeometrySurfaceDescription());
			getGLJPanel().repaint();
		}
		public boolean skipIfAbort() {
			return false;
		}
		public boolean skipIfCancel(UserCancelException e) {
			return false;
		}
	};

	AsynchClientTask tasks[] = null;
	tasks = new AsynchClientTask[] { new SurfaceGenerationTask(),repaintViewTask};

	cbit.vcell.client.task.ClientTaskDispatcher.dispatch(this, hash, tasks, false);
}
/**
 * Comment
 */
private double getTaubinParameterFromSliderValue() {
	//
	// map slider value to scale
	//
	// normalizedSliderValue = (value-minimum)/(maximum-minimum)
	// cutoffFrequency = 0.6 - (normalizedSliderValue*0.58)
	//
	//
	double minimum = getJSlider1().getMinimum();
	double maximum = getJSlider1().getMaximum();
	double value = getJSlider1().getValue();
	value = (value-minimum)/(maximum-minimum);  // normalized (0,1)
	double cutoffFrequency = 0.6 - (value*0.58); //0.6*Math.exp(-value*3.4);
	//System.out.println("cutoff = "+cutoffFrequency);
	return Math.round(cutoffFrequency*100.0)/100.0;
}

/**
 * This method initializes ivjApplyButton	
 * 	
 * @return javax.swing.JButton	
 */
private JButton getIvjApplyButton() {
	if (ivjApplyButton == null) {
		ivjApplyButton = new JButton();
		ivjApplyButton.setName("ApplyButton");
		ivjApplyButton.setText("Apply");
		ivjApplyButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				try {
					SurfaceViewerPanel.this.updateSurfaces();
				}catch (Exception e2){
					e2.printStackTrace(System.out);
					DialogUtils.showErrorDialog(SurfaceViewerPanel.this,e2.getMessage());
				}
			}
		});
	}
	return ivjApplyButton;
}
/**
 * This method initializes ivjJPanel3	
 * 	
 * @return javax.swing.JPanel	
 */
private JPanel getIvjJPanel3() {
	if (ivjJPanel3 == null) {
		GridBagConstraints constraintsJLabel = new GridBagConstraints();
		constraintsJLabel.insets = new Insets(4, 4, 4, 4);
		constraintsJLabel.gridy = 0;
		constraintsJLabel.gridx = 2;
		ivjJLabel = new JLabel();
		ivjJLabel.setName("JLabel");
		ivjJLabel.setText("more");
		GridBagConstraints constraintsJLabel3 = new GridBagConstraints();
		constraintsJLabel3.insets = new Insets(4, 4, 4, 4);
		constraintsJLabel3.gridy = 0;
		constraintsJLabel3.weightx = 1.0;
		constraintsJLabel3.gridx = 1;
		taubinParameterValueLabel = new JLabel();
		taubinParameterValueLabel.setName("JLabel3");
		taubinParameterValueLabel.setMaximumSize(new Dimension(60, 14));
		taubinParameterValueLabel.setMinimumSize(new Dimension(60, 14));
		taubinParameterValueLabel.setPreferredSize(new Dimension(60, 14));
		taubinParameterValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		taubinParameterValueLabel.setHorizontalTextPosition(SwingConstants.CENTER);
		taubinParameterValueLabel.setText("0.3");
		taubinParameterValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		GridBagConstraints constraintsJLabel2 = new GridBagConstraints();
		constraintsJLabel2.insets = new Insets(4, 4, 4, 4);
		constraintsJLabel2.gridy = 0;
		constraintsJLabel2.gridx = 0;
		ivjJLabel2 = new JLabel();
		ivjJLabel2.setName("JLabel2");
		ivjJLabel2.setText("less");
		ivjJPanel3 = new JPanel();
		ivjJPanel3.setLayout(new GridBagLayout());
		ivjJPanel3.setName("JPanel3");
		ivjJPanel3.setPreferredSize(new Dimension(20, 20));
		ivjJPanel3.add(ivjJLabel2, constraintsJLabel2);
		ivjJPanel3.add(taubinParameterValueLabel, constraintsJLabel3);
		ivjJPanel3.add(ivjJLabel, constraintsJLabel);
	}
	return ivjJPanel3;
}

/**
 * This method initializes ivjResolvedLocationTablePanel1	
 * 	
 * @return cbit.vcell.geometry.gui.ResolvedLocationTablePanel	
 */
private ResolvedLocationTablePanel getIvjResolvedLocationTablePanel1() {
	if (ivjResolvedLocationTablePanel1 == null) {
		ivjResolvedLocationTablePanel1 = new ResolvedLocationTablePanel();
		ivjResolvedLocationTablePanel1.setName("ResolvedLocationTablePanel1");
		ivjResolvedLocationTablePanel1.setPreferredSize(new Dimension(300, 100));
		ivjResolvedLocationTablePanel1.setMinimumSize(new Dimension(100, 100));
	}
	return ivjResolvedLocationTablePanel1;
}
}