package cbit.vcell.client.desktop.simulation;
import cbit.vcell.solver.*;
import javax.swing.*;
/**
 * Insert the type's description here.
 * Creation date: (5/11/2004 1:28:57 PM)
 * @author: Ion Moraru
 */
public class SimulationEditor extends JPanel {
	private JTabbedPane ivjJTabbedPane1 = null;
	private cbit.vcell.solver.ode.gui.MathOverridesPanel ivjMathOverridesPanel1 = null;
	private cbit.vcell.math.gui.MeshSpecificationPanel ivjMeshSpecificationPanel1 = null;
	private cbit.vcell.solver.ode.gui.SolverTaskDescriptionAdvancedPanel ivjSolverTaskDescriptionAdvancedPanel1 = null;
	private cbit.vcell.solver.ode.gui.SolverTaskDescriptionPanel ivjSolverTaskDescriptionPanel1 = null;
	private cbit.vcell.solver.Simulation fieldClonedSimulation = null;

public SimulationEditor() {
	super();
	initialize();
}

/**
 * connEtoC1:  (SimulationEditor.initialize() --> SimulationEditor.makeBold()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.makeBoldTitle();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}

/**
 * Gets the clonedSimulation property (cbit.vcell.solver.Simulation) value.
 * @return The clonedSimulation property value.
 * @see #setClonedSimulation
 */
public cbit.vcell.solver.Simulation getClonedSimulation() {
	return fieldClonedSimulation;
}


/**
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new javax.swing.JTabbedPane();
			ivjJTabbedPane1.setName("JTabbedPane1");
			ivjJTabbedPane1.insertTab("Parameters", null, getMathOverridesPanel1(), null, 0);
			ivjJTabbedPane1.insertTab("Mesh", null, getMeshSpecificationPanel1(), null, 1);
			ivjJTabbedPane1.insertTab("Task", null, getSolverTaskDescriptionPanel1(), null, 2);
			ivjJTabbedPane1.insertTab("Advanced", null, getSolverTaskDescriptionAdvancedPanel1(), null, 3);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane1;
}


/**
 * Return the MathOverridesPanel1 property value.
 * @return cbit.vcell.solver.ode.gui.MathOverridesPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.ode.gui.MathOverridesPanel getMathOverridesPanel1() {
	if (ivjMathOverridesPanel1 == null) {
		try {
			ivjMathOverridesPanel1 = new cbit.vcell.solver.ode.gui.MathOverridesPanel();
			ivjMathOverridesPanel1.setName("MathOverridesPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMathOverridesPanel1;
}


/**
 * Return the MeshSpecificationPanel1 property value.
 * @return cbit.vcell.math.gui.MeshSpecificationPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.math.gui.MeshSpecificationPanel getMeshSpecificationPanel1() {
	if (ivjMeshSpecificationPanel1 == null) {
		try {
			ivjMeshSpecificationPanel1 = new cbit.vcell.math.gui.MeshSpecificationPanel();
			ivjMeshSpecificationPanel1.setName("MeshSpecificationPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMeshSpecificationPanel1;
}


/**
 * Return the SolverTaskDescriptionAdvancedPanel1 property value.
 * @return cbit.vcell.solver.ode.gui.SolverTaskDescriptionAdvancedPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.ode.gui.SolverTaskDescriptionAdvancedPanel getSolverTaskDescriptionAdvancedPanel1() {
	if (ivjSolverTaskDescriptionAdvancedPanel1 == null) {
		try {
			ivjSolverTaskDescriptionAdvancedPanel1 = new cbit.vcell.solver.ode.gui.SolverTaskDescriptionAdvancedPanel();
			ivjSolverTaskDescriptionAdvancedPanel1.setName("SolverTaskDescriptionAdvancedPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSolverTaskDescriptionAdvancedPanel1;
}


/**
 * Return the SolverTaskDescriptionPanel1 property value.
 * @return cbit.vcell.solver.ode.gui.SolverTaskDescriptionPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private cbit.vcell.solver.ode.gui.SolverTaskDescriptionPanel getSolverTaskDescriptionPanel1() {
	if (ivjSolverTaskDescriptionPanel1 == null) {
		try {
			ivjSolverTaskDescriptionPanel1 = new cbit.vcell.solver.ode.gui.SolverTaskDescriptionPanel();
			ivjSolverTaskDescriptionPanel1.setName("SolverTaskDescriptionPanel1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSolverTaskDescriptionPanel1;
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
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SimulationEditor");
		setLayout(new java.awt.BorderLayout());
		setSize(547, 346);
		add(getJTabbedPane1(), "Center");
		initConnections();
		connEtoC1();
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
		JFrame frame = new javax.swing.JFrame();
		SimulationEditor aSimulationEditor;
		aSimulationEditor = new SimulationEditor();
		frame.setContentPane(aSimulationEditor);
		frame.setSize(aSimulationEditor.getSize());
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
 * Comment
 */
private void makeBoldTitle() {
	getJTabbedPane1().setFont(getJTabbedPane1().getFont().deriveFont(java.awt.Font.BOLD));
}


/**
 * Comment
 */
public void prepareToEdit(cbit.vcell.solver.Simulation simulation) {
	try {
		Simulation clonedSimulation = (Simulation)cbit.util.BeanUtils.cloneSerializable(simulation);
		clonedSimulation.refreshDependencies();
		getMathOverridesPanel1().setMathOverrides(clonedSimulation == null ? null : clonedSimulation.getMathOverrides());
		getMeshSpecificationPanel1().setMeshSpecification(clonedSimulation == null ? null : clonedSimulation.getMeshSpecification());
		getSolverTaskDescriptionPanel1().setSolverTaskDescription(clonedSimulation == null ? null : clonedSimulation.getSolverTaskDescription());
		getSolverTaskDescriptionAdvancedPanel1().setSolverTaskDescription(clonedSimulation == null ? null : clonedSimulation.getSolverTaskDescription());
		if(simulation.getMathDescription().isStoch()) //disable time step for stochastic simulation
		{
			getSolverTaskDescriptionPanel1().disableTimeStep();
			getSolverTaskDescriptionAdvancedPanel1().getTimeStepPanel().disableDefaultTimeStep();
		}
		boolean shouldMeshBeEnabled = false;
		MeshSpecification meshSpec = clonedSimulation.getMeshSpecification();
		if(	meshSpec != null && 
			meshSpec.getGeometry() != null &&
			meshSpec.getGeometry().getDimension() > 0){
				shouldMeshBeEnabled = true;
		}

		int meshTabIndex = getJTabbedPane1().indexOfTab("Mesh");
		if(getJTabbedPane1().isEnabledAt(meshTabIndex) != shouldMeshBeEnabled){
			if(!shouldMeshBeEnabled && getJTabbedPane1().getSelectedIndex() == meshTabIndex){
				getJTabbedPane1().setSelectedIndex(0);
			}
			getJTabbedPane1().setEnabledAt(meshTabIndex,shouldMeshBeEnabled);
		}
		// ok, we're ready
		setClonedSimulation(clonedSimulation);
	} catch (Throwable exc) {
		exc.printStackTrace(System.out);
		JOptionPane.showMessageDialog(this, "Could not initialize simulation editor\n"+exc.getMessage(), "Error:", JOptionPane.ERROR_MESSAGE);
	}
}


/**
 * Sets the clonedSimulation property (cbit.vcell.solver.Simulation) value.
 * @param clonedSimulation The new value for the property.
 * @see #getClonedSimulation
 */
private void setClonedSimulation(cbit.vcell.solver.Simulation clonedSimulation) {
	cbit.vcell.solver.Simulation oldValue = fieldClonedSimulation;
	fieldClonedSimulation = clonedSimulation;
	firePropertyChange("clonedSimulation", oldValue, clonedSimulation);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G43DAB1B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359AEDF0D45515D62DB0529AC7849426B2FE54D841460A0A55E9E3076950C2DBECF106E662CC1CE6ACBF3003B653099D265E7D489784889091B416D0C1C1B00252B1DD21869284B410A53BC9B69F1B6C5B5D176CCB5E6EDB763DCDB649A66939773E7B766D66ED92991319B3775DF36EBD671EF34FBD675C3BC115871795194C1588190A107A77198921726388352D7879AA06AB3C16D306F4FF07GF623
	172CD6E8CB1BF34ACE44F34AAA50F02C947AD550AF984DA9FB8B3E772271583AE37011C64E83C06B3AFA7637D34BB9B04A6414107E7A2B65506E8428869CEE45E6ACFF53557DAA3EC145B3B01991F2BA7098CBD3150A4B02B1EB70BC409FF65071E720258B3845D62B0E6B3AA393957CA6E25E017968F8B2D82A4E4DD65BEA748FD34009E51DE8AAD76794826DA460307296745D7D1FE3D976013E7F793C02D2B36863C53146A78AFCC82971737200A205EBE4A19895B9C510C2B61BC37B3E609794A9027289FF25
	291470C3E8B97443AD9877F2B716F9943E6BGDCAD8C3F228763D74277BA007C962667598F4E28FA38C61FC271A77E33FDD79ABD3736D07D8E37A674FCF6C92E79G591376B8363D87E8B783AC86F896A097E0AB40BB4497BC239FC2FB4E2B54CB412094BA978E1F977D11AB121F97615BE6830A0AEB66FDD2440F905D6FCF1ACE982CE7AA20736A3E71BCEA134CCF595EAFEFFE9465BE3E50DCE2E0135C4CD1F3E59B3545093604CD286C6134329B5B12FDB3D9F69B524BBE9D5FE0A9B210ED97B6D8BCAA9FC5A7
	7B01B63C579DE42F974037C9758F0A77937CA69D1E7A69E662B338DF8834AE97738D57CE3616C66BFD286254A2C3BBAC7E65FCF3360B2EE123ABF5AD07BC183735BDF5AD47893E32BDF5AD0F7730359C865ADCG6ADF5EF1FCEEB7C33F34A3272C9A2096408520G4C6DE0BE76706847B374B1BB97D14EC785514FC7901D6B0D6F03160FA9916EF2C8D638100FEF65A2826795F99929E3866258865E24F9E1627BBE10F1118F7191F2D001BB976495BEA253B143715DF8CC04A7D4279064C1C0B00C985F6F4477C2
	ABF232529276F38A1F98C1697362F8CF8639D660888C601B5A657689EC2FCC50BF9BE03DE6876B986677B59F8971224D56A8C983513093EB022ABB311DB3C86C480771DBBB180DEFC3986787FA3C137179FCEC0F0ECF0360434B67A2436C0CF9FAF0EC4E68E4BE73D7977319069DAB5009976EB2741955603F4BBB292F2C6ECCBDBB7D04A7DE976D3F566D1A61794D68B4BEDB53470A6DBDB315E179067CF7EAFB753A896F55E6D8E3E9D7CE5951AEE6634131EA6A3367B919E7DE5A948187637D890B53F5FE6031
	2A76247DD324DF5745EC75B45143A8FEFF9BC0E57C01483891BB4C8428CD44F11C0778C1C8098DFC188F7961C3D6E972B876B5751C76CA5A1CE1028F43392E0703A4EB7809F1FC76B8C509885E2842BBF8C591C2FD897A2DC4169C750693A101497A41B80E3F52901CEBED7C1D6378CC7824D008A71B23CDD169F711B3490BFC9096DDAFC5C38A45AF1940FBB5408F2BB1615E09E2044F34984571055686D95CF62CC3C4884216639005D6F561737AD053FE1D0D820043B54EFCE817F53133B7749EBBFB07C79EC6
	59BDE133D16ED87C606DE664266724521DB8FBF4CD9948F86D8BC9ECD071B2DD6B5728825A5462047A7AA7964C67B4703F8460F1B3DF5FE342F496CFD4A72F17C4111AD233719BA64CDB662FC7F98843B8F0FD2D592D583CA705562A5A1052F71A318DC5DD4CDB6D46B674FAD86D97F3D37BE528ED26EA872A575F0EE1DB137A2C1F9B64E2B57290780B4D76151D03D5535AEB373DD49DDF514DEA2C338E2C73A568FBGE25DECDF1E5F41EA2C85831928E26C0F16F435E4C6B7DD474A6E443E645B2396EA533895
	4BACG1A350759F31ED99F674F4932649348E62836283568770D624EDA36C15BABC4E465B29C2C9833DB3D852F9767A0952D66645BC82E463A6E8739F6G8B997F9A418BA1C191B8D198610F9E2376FB5A0E77AE488D7067A5516F949491C82D5496776D2DD3F5DD7768FD684CC43759285E1C3BABE01E5BC3ED10551330C51679C573EAG9A077659D3EB2C2DBD0978DF61191C8BE86D71985927431E541A649902BF6DC92DC96A9D2CA6E9GDA2607654B6D4558CF4AB90865GF5GAE009840E20E6D4FDA4B27
	E81AFA64F2884A0490A7BA1282902B8B2C969AB3547EAD56A296B3900B47F7F8D26D3012CB56FDFDCA1F4E3BF7523C1A14FEC14ABC2A37D0024FD1399774862883688218833094A05B4BF45FD5F4F6BA5DAF60G6F146A81F7894EEBFDEACC457BCE6A150F48F94B835E85G94372D9963767B60EE65E3796FA9870E31CE2D7F9C690BDA7F32A3B54F63315FEEFE74CE1271ED66CAEA5B4A627479711DE2637CF82858B8BFFED03C1B6201073EAAE6740F0B274E1F1F959B654FF7095E3F5E370B7016E5DEA61B5D
	40A91CCA7B0B72A6B24823D721F0D6ED36972D711429E3AEA8D27DA9CD26DC12DE7F1192E37D67146038ABCB51080FCF557156127D9AAD61051795BE08172366C6B2DF61A2FD7CA49BADA273A9AD5D7CE5A5696CC8F5ED3063FAC06406252882314C07FD3C534E72FE8674B3815683647BE85DCE636DE3C3B85F7542058358B9423C8FED74133BD733769E82FD97C08CE0B140AABF0B611E4FF1EC7B8A52241777637CF60A569B2FB97475C6B5CF65B5C26B8120F171A6BBFB5F0881AE0BE76BF939866B2B654F9A
	EEC831594ECBB857B7D265E88EDDCB4EB41E3F09E7B974F542333A976EF7G76DE16C3E36F339C3A7CC777A16B30F18E4DF30E19833D74BC4F6FCD640D75CBF24DD46645E6A6F38D50D19F46C51BABD34E6A93E69A8BA97D21A4BA5EC37C2415207FA9090E57FB512A27EFF76869C53A79D43735C718EEDB57E6A171091B533EB5DD6AD35F3B54B6A37FEED379F04CC26DFE3C74DDE4D837A4F69768548E9FBA595B5183FD4C7697DB306DF321DFGD09EE0B1F35E24BCBB33B8386886672D7F4673E87EDEFB0D64
	75G4D5FA68D3F0160CF6A7054FF5F507C778C501685D8DE1747306FDB8548B3G35GF600B040FC0165367A5213E81A5C6614420D9020C4DDC8257AB5B65F583B54F3A54CCFD67E6C47E855779AC81DEB64A74B8475CDD2ED0F7E79EED3A2FF97CFE07D36G2D3C9FF2A740A500AEG251F69F7DB69892A9FAE6A4FA98A678B108A12A957AA70C389B48EBAD4370F1DD369B637BFDDCD768C713D257DF40F4AB47C9A02DF2543533D3BC270383F81E835834CD71F3F06B4BF4A51FD97DD1B697D7EC6004A78572C
	4A207B929FCB1F77EE9AB34EFB73460C737E6DE3D36775C5E3696F45BF9C535F0B29DFB5A29C671642DE2C8248855882E09519CF1D987FB775A983FF3A606F43F9973F3AA9CC7E2E09743A6E1B187E2D6E60843E67F8A5C5110218BF439F19407B25C8E10224388FA7300DA2C2DFC021C3D57C49899CA7C53E1720E95D792513551D5522D6E73660BBCFA374C5008C00D59A6DF70436897A65C1B8EFG4EA023B9DA0CFC8673C845E5986098B49A509272C5C399C5E97CB81D8C239A36CC331393255F2BCEB47DDE
	79697B825858B99CD66BA58161FAC86A6D15F94DA7C2644D0162AE4BF6C996F01DB5BF0877456D2C1EE27D31A035912DFB9C57703D798A27841A8679C8C47073B2395D1FD2697C5B78772BAB3C9C4031CD6895FC6A332EEE4C8572B66210C4606064640186DE76C5047064F10177520DBB679F4466702B63E96DE5EE5557DE9BE05D787ED299027B96G9D135F269FD39722E36250E6E8E3FE552E9F13290E59822DD5E2E36E6F540F29126898915AB9922B91C71CF89F6AF9FCCFE539E808E739286A117BD15541
	BAB30EA5C639E831243E73CA09FA0C791F853D3A6D93B47937B019582A8632E9CDDFD43059A66F932BE3EC2D6C9D26D0E2F54C29B6126F431057GBCE1D617AEA8B9E9A0AB883CD5A9063BCD65D439181CD81849595B0E65E4C1FF8DC01EA66715DE5B8CF4CA6FB7CC37FF36EB3AE9B237F4905D2E03EEG1E6BCC668BB567EFD4E6128FB259675C1A3E57196CCF496FC4D950DF8310F71D55CBAF1B7039AB04FEE584F2B1009D408F908748G480C309C00CCC721ED657A550A093E9912ACGC8FAC9A0F9A04289
	A105462FAFDA272AA9D6C712E35FC668535A61C162871BA32975602384DF98C92D87BFEBE37520D506BB0E4C6267A157D4725749497273E4A67FD6629F85F22A7C8C02C7CA2A7C836DCCFEB950428A13EF6918CA7E86A5D97EA6054977F7E2FEDB15D479C102AF0D264A7FFB8713DF8934D814497F52BD157C5CE8327CADD1A67FA162AF5641D479D984DFB118AA7F3F5DCCFE9550668CB21FF95514BE37941A264FADA633F19DD0E21EDC87141B0D6B00BDE6FD9DC0F1EF29FCA35A2385453FCB6EEF749EFF058B
	F1FD3AF72C56D6D6BB648F32F72C27892EF4A8272CEA08597C11B66CA274FB256E7BEC5B6C57312D33AA036ADC6BE2F6B8BA446C3004609A216F516C707D76046E8BF55F875BE75F8E3533AA036A7CF68733032859E12C0379C306E6077E4E046E326EFBEB476C5B614D4E5937035B4D6C30D433C3361B59A1DB33430A6E046ECF6A3E3F1871EF44B7EE87572C4A489BA231D92B7D568DCD2E69E9BD361E448B0573521FD6E278FF26B6E3144A234945F80831F4BCFE6A1A4CE35736840FD39D0C479C839EF47C43
	FCE23C62E66397249D7FD2AF9ECF6B0BBB353AADB3465E785638756FE055471B8C5F418A5DA366BCD506F5B8D1D3D6182AAD144F6FBB757F3BD5F10FE8B6997041D02B4EEF5071216B0BA66ABD20D1DCDB8743E5E938FB34BAE953B0735145C975CA75C8CE59691129FFE320F55405G3661A5986B87188B308CE0A5403A91168B0F74CDD5BF516755C47DC4798684EC95BA3F3F6FC83A79F08B7F0A9B641A3C7D3CCFF1F2FD1A5F0FA455BFF9301602E97421733A126A1652D148EF23B319F7EFD23DF19266B4CC
	B30F460B1F901FF24158E39606DBC9F0E1406DB0B39BDC8D242FA7DA825357935D8163FA429F18DCCF888163FAC28ACCFED7384345564F1B597A973A587A771A98CE6EE0638EE8FA46BA58B8C99B77241B0D7B0515611EF1EB7C4C6C8E463D2F7B9D37CD70AB810A2F0A6DC860AF71D88768537F6B3C4B027F17EF79AB28C8C15B927F6C24A0AB3EC5C103AF998AAAE5178BF6EFBCB621BF1B0537788C7FC781C3B8CE777C66F862ECD2FF5ED804F54886FCEE1C1D0B23C59FCD61570EA0A70A8AA78E1C8B79310F
	1F9FD67844BB570BC5183F4C4628C4CA4BAF426B6E574D79BFD0CB8788BD17609A478EGGB4ABGGD0CB818294G94G88G88G43DAB1B6BD17609A478EGGB4ABGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG818FGGGG
**end of data**/
}
}