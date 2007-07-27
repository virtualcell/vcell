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
		//some extra process here to enable/disable proper time steps.
		if(simulation.getSolverTaskDescription().getSolverDescription().compareEqual(SolverDescription.StochGibson))
		{
			getSolverTaskDescriptionPanel1().disableTimeStep();
			getSolverTaskDescriptionAdvancedPanel1().getTimeStepPanel().setEnabled(false);
		}
		else if(simulation.getSolverTaskDescription().getSolverDescription().compareEqual(SolverDescription.HybridEuler)||
				simulation.getSolverTaskDescription().getSolverDescription().compareEqual(SolverDescription.HybridMilstein)||
				simulation.getSolverTaskDescription().getSolverDescription().compareEqual(SolverDescription.HybridMilAdaptive))
		{
			getSolverTaskDescriptionPanel1().enableTimeStep();
			getSolverTaskDescriptionAdvancedPanel1().getTimeStepPanel().setEnabled(true);
			getSolverTaskDescriptionAdvancedPanel1().getTimeStepPanel().disableMinAndMaxTimeStep();
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
	D0CB838494G88G88GCE01B8B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E1359A8BD0D457B553A12D13319D13219D1A5004311A1046A6245106B4F6E292B35204CE4C4751CE2DB0131F6DE0EA7C05C4543B4B72D1D0D6C0C5A515A6A60528ABA40830FC327229BF08A490A4A9A6BCF69F30326C2E3B8FD89025675C7B6E5B4772960C9318B9F35FBD675CFB6EB9775CF34E3D8B49BD94112253679222CBA1725FC99DA119C788796FE33F3C1D63FCEF2D4AA02A3FFF836CA6D7CE9820
	1D8134F98763B20CE4FBD1BA744321AFC1FF9FFC6710DDC531C57091C44E9C205D376CF44964F23EBA466524517E60C0A6343BGA2G07ED8234C6723301BC993FD846F3506989F9F99D725CBF10AF630A21FF86E096401CF50CFFA1345B2964DB4D65B2DF476D11E443337D7A1DB80FEACE8E796B59D80B5C96100D3A556BD1560BF41DB846877DA8GD4BE197CA82F925A2A663E1E9AEB17DDB28DDAC50743E4F558C527E432095EBE4965B6F96D7D838EC1323B1CE6F30B725DE033CBAE8F31ED40798F8C2653
	7988B98A7D3594B7B385E5C64277A200958A3EA59571259BD7E5B48258B6F2BD2B7FD3A56B51FE75D1F2B86B1E6DD9C1749C5D48741B33512F27711148547DF4CFFEEEC25B47834D30E9D5C69EC095C08740A840C16A8B6147BE0676D417D4676A6FF7B9CF395D358E1B2749E5939D70EDB683C546FDA9DADD9E9BA1EC3FD78C9C55D84FE440463D7E3D47B11BC4FE4A77FE5E178F12150F5C2ECF5330494A48C1FD68A6E60BF91B7CB6E1328F8D86139D3BE93CEF0E17FD1128E54713F0FD0A066C24B5F7696365
	F9B628E49FFE9B771A503D4E05EF1D6C9F8C3F0362FBD5F86627F7D01F413E8FE8AB5F663E513E0B2F2551B01B98CB6E5034C358D3BF51DB92599A5A93835732044A8CC98CDC4B8B949F1A9838162FD379DA66GAD7D1DD5994C3F6EAE46F36B007E8C008800E80095G9BG525F65BE765A3163B768E34D02C72A9D30BBEC22073031F71B76C2AB7AA40FF05669158427D5EC93BCF6214BA1FA094CF31F89ED50B5EE5CBC6A7BD610F1DAF40A9EFAD0E1F621DF14C40F1771ACB06DC69E0FC8292D76FE9184743B
	891F770F269C54CF70CADF39ED02A47AB9987D99936E49205086B34284704D6C72DC895A2B907437GB4ABF6309C47794E0B9E2768B01B9BDD2E3E81F7ABEAC2A25EC5BB175158E1036F51F7390D17FC0238D7211F244CB3F4BCDBB5CF3D5D0A4B97BC437C0C2526E2ECB6EE66BE73E6BB77197A1D3FA5C7174D527499771BF7690FEEE63ED23FB9706C98691C38AE568FCBE93F4173EB5C2CFD36260E958F5F300C646FB97FAEE52FCE1462DEB9E00DB3GA2B7F39B7F6EF8817359DA41ABF2AF3D60818793EDFE
	0B33F57E195AC6272CFBB96DAF5A4CED75DDEA3B063DFFA8E0B2FCD4464D5861C60059A432987310B888A931DEF40BCE9BFCF8E55AC34568EB72B96DF6A9E396D03C9B4EF59D9CA42F02FF3C984F1EA0C99EFB5700A4360812E4F7767869CFD3D95E01AE37BFA4F0D94F96E37CF58D4139D6781FAF46B3E1F5B91DA25D9CE508CCFF011EC951A17643226BDC83CE0961171760DE750943F2CC78EBC9AA41B36D98407842EA03A8818BA037475E8FDB0EA10A14A761F9ED9A4CD359E8B56CB756B819C0ABC862E7EF
	6883FE768E0F5CCF96FD572719BB42665E263FA5091D1350A47F59E3EB0A7DC4FB6DAF5258A0633DEC2D6789649A92D81CB0DFF71E40F966437CB1G71C95C57BF7F9469BC1E48CEDE67F2B818A9959BD716625CDE714A0088E1988357F945EE3525598134B659060CFE2E94ED68D0453C7AADE84374B4DE7BA5A6B17B99373036F08B338353EBFD915A1A56E71705C141E7728E013F184D671A85D8B52B3D9E25719379E7ED61B556AB4328F38C746381923770FDF9E1A72F31C27BA249C25DDDC1EBC963D6360E
	522D7EFDF12C6FCEE6B60DAEC3199250C236F2FBBED32A0E73273CDE1755CEB7C33605F4C23DEF8CB7FCE287345DF60FD7BA8B874B476DB6F682570BB9C8C64BB939A4ADD356B58C64AE85C8267CE194EFF75AA53B60300F0805C54CFE0FB5635E758BFDE22D4BE1EB35CB8EA035B1DB4C4E31483A46BEB81BD40DFD2D572AB7E7E4D975695B188D0A3779ED916238BBBD8C0045210593EA2C51ED7E78BFEB5B44DC40EA0F0BA3386EB95B82EB92016267EF8B2CC97EBE4CEB1245C0ABA4F1F23EBCDD0EFEB2937A
	11G0BGD6GA482646962647DC9AB7B14CCD10F1CF5C21960949CAD6382902F8B4A12D94C107BCF16257018C1F8BCBE1996E807D2102F6EB787745938068963CC81FDA9E09C53FBEE851E2319C00B84D8843092006863B27281ACFA2E7B791355D3695E00813E55D58738B3F0DE6B82E3AA0CE7754ACB743CF5425C92G43C5799097867DA8G265325910C31AFAB7D9E5ACFD07A7F9C8E4C73487B4366477411C099BFE42EE436EDAE8F1E9FAF16EB6747CB655A7951DA3E1B61E18EF5D54C69FD651367CFEF39
	D67EFC0F6A7D273DD9F4EE2FD774524D2E97A4C1263DA16DA19AF974F3A81CE51B6590E75AF61927C1F25515A8B29F2A882E7F6FAB3475DFD801F1576B9A70D845C09D1F2E48D3E8FEAFBCAB097D389CB9B7527112606991A75868A5BA1E51020DDFD9914C0ECC57FF8CE2BD609006DD83924432E4FCD3518D713CEF04FEA1C0B3008DG6BE396EF9F9E42FC578D978E182E45ADDAC93B8147AE71A96FA1403F82A091A08FE865GCCE6E7A54636F310A63BC49B663792D6EF040D286B0D888313978BEDBCG0B0B
	C5C37CFDA391F045A9F172FADEB7213ECA7EB489C31259DC6B42DC5F4814E3B9744AD03ABC3E9746329CFA8F1DB3827A51GCB81D85E70FD44F3683CFBE71328ADF7EA665078D61FFEF58ABB4F1929F17E3B68A31129CC669A9F17F986683724221D8E8E67861C5585FA968B993DE99C9D77901F347C7431F1F4DC6FE9031A7E70B8FA0AEABC536D398F39EEC94FC5914702312075C10C2CD3025CAE3AB7D4B74730AF15597DC87AFBC433EE716FAE50199D160E70372343E95C76EB2E226DBB20AF814CCC63B173
	78504D3D3FD45F6438416FBD0E656FABBE1A57E15DBA59FE8CBFC271D1AABC735F390A7FAE84DAC1BA4F6B73AA5177C3209F81908D3094E0ADC066F61E5B464C45E40A5C566AF2B7C200F228C2AA53EF1D6F665E251ECC63FEB27F8F3FA151BF7ECC1F9144CF8A36B37F3048ED7B5CD01DBFFFFFD60D7A3901B693A08AA086E0A54086C53FA56623CCBFAC6ACFC912E06D2595A4D72E4DAE8E795198F418EE4BC6A653ADFD4744DCAB57DE23389779BB589EEDD770CE0AAFD761595E2D273E0A7DD620C56F603EBA
	04E52B6CC737283E9F983A517B7D4DG1331F8DAE548F75C4A60F9EFE125F65EFB2AD2BB6F47D4CE1E5717D6863F97AF2FD45F0B19DF5DDA03F1AEBF83728BC087009BA0A4037B14D875BE73A98DFFEA3075E05E45D7B7094BBFD39DDC578B55D33F5535D763BD274BA5C92EFE1C1F633F2D467D12DCEE0A14EB20EA3411475E53ABB1D699EF2F46B869903BA91A551D89A33C6E0C48D06A4CD13C7344C2BF81401889B64864B40952FA21BF93A08A60E505764ED5AD1F41B982F1B8E7B05F085560774B4F9E5512
	1192440F0349502AE137ABF67AE9CD703D8A2D19FA2FFED5C36F29E06356E137DCAF4D2E41FA48555D6D95951F382F4628604EFA1BDDDEBB56D919BBB14FA6DFAD18E47D09327DD85D5372855E1B1B8429774220687158ED22175E6ECBE43AF8GFF3FEA923D3D985B6C5DF62B7C2C2B62E920EFA3ADAE874C50AAF87B6AC52F55E3F7CF646B7DA0985FA95BA01A43A673335AABF2D4DDFB3502EEF8FF8905B69200715CF6DD4D33C866C9C2BB6C62BC8E2266A95C45F85C5006A8BCE775EA1EF019A781DA1245EB
	44022B388FF5A25ED3F9AE9A92F9AE4AFF6057A47FD0259EE309D6AE4A4BE2F62F4F7257E35C7F1249DBBBBEA193FF8B73D2DB19A01B1AFA866CE67344FD62F5CC7CA8FF0771E571BAE679B51A6F219F83901F456B525034E28DD97DB037ACC5F337191C272EF3B9090A1C4746D0CE31B1AE638CC027114BD953ED3E811D027B8D57ED8ED1F4B3F2193F50D15D209F83902F48DCE62A3DD91963FC104BFEA3D951D7117D978355F7B7688B50391B57CB07ACF86EFC508F850886D88A70AAC09200B19B6EAD59BC87
	D4FCD688ED1BF0D92E18581BA14D8200E417841A87BC025DA93178755A68E4B5C5FD767858578EFDD6BB1C3FCE5F5B33836B41D60A77E5875603FF3B466B4190202D4E6671F349754964B7660C175F1943659FA3B81F1493A83F0C62EF4989147F449817BF93E8EBF3387C7B49E472DBF3474B6F4D6572F768F13E515CC079B994BFA3B7D0FE380E4B8F85DAE2AE171F10BC197C0EBD63653B77F079CD74F74510BD01725BA8FE561EC079F186AEBF9CE8E4AF77190FAD41F3CB09E56A5CE236E85781964B44BA20
	4122DD871C37286BG798DC61E5723BCDAB0FC1B856FEF6C9E5FA4B8059E55BBD652A82F9DECFB79BB56F75790B7837A61FB394D3F388E0DDCD7FE237AFE655A745731C953AA03693C6ABA37C324E207D3E3080B05FE3CE287FCBD656B2AD2FDBFB3B67DF6D8B52DB2184E0B8837C302E2078FF45C9F0C7B389D0CFA3F6E7BD55F33F553EF074727D58653B959406D103F0F5B215B406DE0D16C501262573DC3757D1AE17A6D30F5DAE5F46E439AB6679A2F479A77CD2C69D9BDB6C86305A4F4511FD6A2018F3272
	E9224C31D1298D93024E51BEB6F10E16B37EB916AB35A3491BB887639FBC6B67BFE0607C39C1799FBC077C2C3E2838466B0B42BC7E4697F3C97D8ED6F0641C66BBD8622581FD27ACA3A44FDFD3AEBC39A91D4D737C7F547F3BE53C33C72F531887A1DA9E3FD8B58FDB5F41B1257E819A43EDD16A31627D9CE7B1F0DC6FFE6E23B5575479B282707377CF7E9B832B23329B5106B1407BAAC07A01380C8200D2004683BC972EBBB7D97D441ED77D75137CFF87CDE895B67E64798F030D07DB78B9E1D03850F5D934CA
	2DC20F627785E3EAFDBAE1AD52014975E16374BA753899B0A67C0646B598546322E04C62A94631F8719575299540DB144CF15FD05CDA406D54F39B9CEC8CDECF3C5FB8F5BDF124D13B1EA8E91CD8CF14B5EA5793D50D935F95CE0E71750BFA3E7E2AB13E7E9C9D476D57F13E7D0A1E7F52F1BE174257E160FCCF98B8CEB0A87369799DCC78C875BB6EC53BCD6AE578FC5FCEBF7E0C08BAC01F7DDF670725783FFC73561094096C707F3313C48CF80B0286AF9992C967178BFEEFAC9AD31F4D44DB3F557C9F850435
	87591E9B727DE71379F3AF7D9D5C82780EFCFEAE22CF9C1B442FDB7A8507C3929CFD271CB674715AE1C9743FF37517617CDE4EA393655F4F4BF05D17D5E37E8FD0CB8788C446E9586E8EGGB4ABGGD0CB818294G94G88G88GCE01B8B6C446E9586E8EGGB4ABGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA88FGGGG
**end of data**/
}
}