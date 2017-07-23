package cbit.vcell.model;

/*�
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
�*/
import cbit.vcell.dictionary.BoundCompound;
import cbit.vcell.dictionary.BoundProtein;
import cbit.vcell.dictionary.CompoundInfo;
import cbit.vcell.dictionary.FormalCompound;
import cbit.vcell.dictionary.FormalProtein;
import cbit.vcell.dictionary.ProteinInfo;
import cbit.vcell.model.Kinetics.KineticsParameter;
import cbit.vcell.model.Model.ModelParameter;
import cbit.vcell.parser.Expression;
import cbit.vcell.units.VCUnitDefinition;
/**
 * This type was created in VisualAge.
 */
public class ModelTest {
/**
 * This method was created by a SmartGuide.
 */
public static Model getExample() throws Exception {

	double FRACTIONAL_VOLUME_ER   = 0.15;
	double SURFACE_TO_VOLUME_ER   = 6;
	double FRACTIONAL_VOLUME_CYTOSOL   = 0.15;
	double SURFACE_TO_VOLUME_CYTOSOL   = 0.25;

	Model model = new Model("model1");
	model.addSpecies(new Species("IP3","Inositol-trisphosphate(1,4,5)"));
	model.addSpecies(new Species("Ca","Calcium"));
	model.addSpecies(new Species("R","IP3-Receptor"));
	model.addSpecies(new Species("RI","IP3-Receptor-Activated"));
	model.addFeature("Extracellular");
	Feature extracellular = (Feature)model.getStructure("Extracellular");
	model.addFeature("Cytosol");
	Feature cytosol = (Feature)model.getStructure("Cytosol");
	Membrane plasmaMembrane = (Membrane)model.getStructure("PlasmaMembrane");
	model.addFeature("ER");
	Feature er = (Feature)model.getStructure("ER");
	Membrane erMembrane = (Membrane)model.getStructure("ER_Membrane");
	
	Species calcium = model.getSpecies("Ca");
	Species ip3 = model.getSpecies("IP3");
	Species r = model.getSpecies("R");
	Species ri = model.getSpecies("RI");
	
	model.addSpeciesContext(calcium,er);
	model.addSpeciesContext(calcium,cytosol);
	model.addSpeciesContext(calcium,extracellular);
	model.addSpeciesContext(ip3,cytosol);
	model.addSpeciesContext(ip3,extracellular);
	model.addSpeciesContext(r,erMembrane);
	model.addSpeciesContext(ri,erMembrane);

	SpeciesContext ip3_cytosol 			= model.getSpeciesContext(ip3,cytosol);
	SpeciesContext ip3_extracellular	= model.getSpeciesContext(ip3,extracellular);
	SpeciesContext calcium_cytosol 		= model.getSpeciesContext(calcium,cytosol);
	SpeciesContext calcium_extracellular = model.getSpeciesContext(calcium,extracellular);
	SpeciesContext calcium_er 			= model.getSpeciesContext(calcium,er);
	SpeciesContext r_erMembrane 		= model.getSpeciesContext(r,erMembrane);
	SpeciesContext ri_erMembrane 		= model.getSpeciesContext(ri,erMembrane);

	SimpleReaction sr;
	SimpleReaction sr1;
	FluxReaction fr;
	
	//
	// CYTOSOL REACTIONS
	//
	double IP3_DEGRADATION        = 0.5; // 1.0/12.0;
	double IP3_DESIRED_INITIAL    = 0.01;
	double IP3_DESIRED_FINAL      = 0.03;
	double PLASMA_MEM_SURFACE_TO_VOLUME_ER = 0.25;
	double IP3_FLUX_TIME_CONSTANT = 0.050;
	double IP3_FLUX_FINAL         = IP3_DEGRADATION * (IP3_DESIRED_FINAL - IP3_DESIRED_INITIAL) *
	                                (1 - FRACTIONAL_VOLUME_ER) / PLASMA_MEM_SURFACE_TO_VOLUME_ER;
	                              
	                         
	//
	//         IP3_DEGRADATION
	//   IP3 -----------------> 
	//     
	//
	//   source(IP3) = IP3_DEGRADATION * IP3_DESIRED_INITIAL
	//

//	model.addReaction("IP3_Volume");
//	reaction = model.getReaction("IP3_Volume");
	sr = new SimpleReaction(model, cytosol, "IP3_DEGRADATION", true);
	sr.setModel(model);
	sr.addReactant(ip3_cytosol,1);
	sr.setKinetics(new MassActionKinetics(sr));
	MassActionKinetics massAct = (MassActionKinetics)sr.getKinetics();
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression("Kdegr1;"));
	massAct.setParameterValue(massAct.getKineticsParameter("Kdegr1"), new Expression(IP3_DEGRADATION));
	model.addReactionStep(sr);

	

	sr = new SimpleReaction(model, cytosol, "IP3_BASAL_CREATION", true);
	sr.setModel(model);
	sr.addProduct(ip3_cytosol,1);
	massAct = new MassActionKinetics(sr);
	sr.setKinetics(massAct);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression("Kdegr2 * IP3i;"));
	massAct.setParameterValue(massAct.getKineticsParameter("Kdegr2"),new Expression(IP3_DEGRADATION));
	massAct.setParameterValue(massAct.getKineticsParameter("IP3i"),new Expression(IP3_DESIRED_INITIAL));
	model.addReactionStep(sr);
		
		
	sr1 = new SimpleReaction(model, cytosol, "IP3_DEGRADATION1", true);
	sr1.setModel(model);
	sr1.addReactant(ip3_cytosol,1);
	sr1.setKinetics(new HMM_IRRKinetics(sr1));
	HMM_IRRKinetics hmmKinetics = (HMM_IRRKinetics)sr1.getKinetics();
	hmmKinetics.setParameterValue(hmmKinetics.getVmaxParameter(),new Expression("10.0")); 
	hmmKinetics.setParameterValue(hmmKinetics.getKmParameter(),new Expression("12.0")); 
	model.addReactionStep(sr1);

	//
	//   flux(IP3) = IP3_FLUX_FINAL * (1 - exp(-t/IP3_FLUX_TIME_CONSTANT))
	//     
//	model.addReaction("IP3_generation");
//	reaction = model.getReaction("IP3_generation");
	fr = new FluxReaction(model, plasmaMembrane, null,"IP3_FLUX", true);
	fr.setModel(model);
	GeneralKinetics genKinetics = new GeneralKinetics(fr);
	fr.setKinetics(genKinetics);
	genKinetics.setParameterValue(genKinetics.getReactionRateParameter(),new Expression("Jfinal * (1 - exp(-t/TAU));"));
	genKinetics.setParameterValue(massAct.getKineticsParameter("Jfinal"),new Expression(0.034));
	genKinetics.setParameterValue(massAct.getKineticsParameter("TAU"),new Expression(IP3_FLUX_TIME_CONSTANT));
	model.addReactionStep(fr);
		
		
	//
	// ER REACTIONS
	//	
	
	
	//
	// IP3 Receptor
	//
	double I1    = 3.33333333;
	double ii1   = 100;
	double i1    = ii1/I1;

	double channel_density = 25;
	double R    = 19.35; //channel_density / (1 + IP3_DESIRED_INITIAL * I1);
	double RI   =  0.65; //channel_density * IP3_DESIRED_INITIAL * I1 * R;
	double TOTAL_CHANNEL = R+RI;
	double channel_flux =143.7;
	double CALCIUM_DIFFUSION        = 180.0;
	double IP3_DIFFUSION            = 250;
	double CALCIUM_CYTOSOL          = 0.050;
	double CALCIUM_ER               = 2500.0;
	double CALCIUM_EXTRACELLULAR_INITIAL = 1000;
	double IP3_EXTRACELLULAR_INITIAL = 10;
	
//	model.addReaction("IP3_Receptor");
//	reaction = model.getReaction("IP3_Receptor");
	sr = new SimpleReaction(model, erMembrane, "IP3_BINDING", true);
	sr.setModel(model);
	sr.addReactant(r_erMembrane,2);
	sr.addReactant(ip3_cytosol,3);
	sr.addProduct(ri_erMembrane,1);
	massAct = new MassActionKinetics(sr);
	sr.setKinetics(massAct);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression("ii1;"));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression("i1;"));
	massAct.setParameterValue(massAct.getKineticsParameter("ii1"),new Expression(ii1));
	massAct.setParameterValue(massAct.getKineticsParameter("i1"),new Expression("ii1/I1"));
	massAct.setParameterValue(massAct.getKineticsParameter("I1"),new Expression(I1));
	model.addReactionStep(sr);
	
	fr = new FluxReaction(model, erMembrane, null,"IP3R_FLUX", true);
	fr.setModel(model);
	fr.addCatalyst(ri_erMembrane);
//	fr.addCatalyst(calcium_cytosol);
//	fr.addCatalyst(calcium_er);
//	fr.setInwardFlux(-channel_flux+" * "+ (4/(channel_density*channel_density))+" * pow(RI,3);");
	genKinetics = (GeneralKinetics)fr.getKinetics();
	genKinetics.setParameterValue(genKinetics.getReactionRateParameter(),new Expression("Jchan * ("+calcium_cytosol.getName()+" - "+calcium_er.getName()+") * pow("+ri_erMembrane.getName()+"/Rtotal,3);"));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("Rtotal"),new Expression(TOTAL_CHANNEL));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("Jchan"),new Expression(4.6));
	model.addReactionStep(fr);

	
	//
	// SERCA pump
	//
	double K_serca   = 0.270;
	double pump_coef = (K_serca*K_serca + CALCIUM_CYTOSOL*CALCIUM_CYTOSOL)*
								channel_flux * 4 * RI * RI * RI / 
								(channel_density*channel_density*CALCIUM_CYTOSOL*CALCIUM_CYTOSOL);
	
//	model.addReaction("Serca_Pump");
//	reaction = model.getReaction("Serca_Pump");
	fr = new FluxReaction(model, erMembrane, null,"SERCA_FLUX", true);
	fr.setModel(model);
	genKinetics = (GeneralKinetics)fr.getKinetics();
	genKinetics.setParameterValue(genKinetics.getReactionRateParameter(),new Expression("Vmax * pow("+calcium_cytosol.getName()+",2) / (pow(Kd,2) + pow("+calcium_er.getName()+",2));"));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("Kd"),new Expression(0.7));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("Vmax"),new Expression(77.77));
	model.addReactionStep(fr);
	
	SpeciesContext sc = model.getSpeciesContext(r,erMembrane);
	//sc.setInitialValue(R);
	sc = model.getSpeciesContext(ri,erMembrane);
	//sc.setInitialValue(RI);
	
	sc = model.getSpeciesContext(calcium,er);
	//sc.setInitialValue(CALCIUM_ER);
	sc = model.getSpeciesContext(calcium,cytosol);
	//sc.setInitialValue(CALCIUM_CYTOSOL);
	//sc.setDiffusionRate(CALCIUM_DIFFUSION);
	sc = model.getSpeciesContext(calcium,extracellular);
	//sc.setDiffusionRate(CALCIUM_DIFFUSION);
	//sc.setInitialValue(CALCIUM_EXTRACELLULAR_INITIAL);
	
	sc = model.getSpeciesContext(ip3,cytosol);
	//sc.setInitialValue(IP3_DESIRED_INITIAL);
	//sc.setDiffusionRate(IP3_DIFFUSION);
	sc = model.getSpeciesContext(ip3,extracellular);
	//sc.setDiffusionRate(IP3_DIFFUSION);
	//sc.setInitialValue(IP3_EXTRACELLULAR_INITIAL);
	
	return model;
}
/**
 * This method was created by a SmartGuide.
 */
public static Model getExample_Bound() throws Exception {

	double FRACTIONAL_VOLUME_ER   = 0.15;
	double SURFACE_TO_VOLUME_ER   = 6;
	double FRACTIONAL_VOLUME_CYTOSOL   = 0.15;
	double SURFACE_TO_VOLUME_CYTOSOL   = 0.25;

	org.vcell.util.document.Version version = new org.vcell.util.document.Version("boundModel",new org.vcell.util.document.User("frm",new org.vcell.util.document.KeyValue("227")));
	Model model = new Model(version);

	FormalSpeciesInfo fsi = null;
	DBFormalSpecies dbfs = null;
	DBSpecies dbs = null;
	
	String[] names1 = new String[1];
	names1[0] = "IP3";
	fsi = new CompoundInfo(names1[0]+"_KeggID",names1,names1[0]+"_Formula",names1[0]+"_casID",null);
	dbfs = new FormalCompound(new org.vcell.util.document.KeyValue("0"),(CompoundInfo)fsi);
	dbs = new BoundCompound(new org.vcell.util.document.KeyValue("1"),(FormalCompound)dbfs);
	model.addSpecies(new Species(names1[0],null,dbs));
	
	String[] names2 = new String[1];
	names2[0] = "Calcium";
	fsi = new CompoundInfo(names2[0]+"_KeggID",names2,names2[0]+"_Formula",names2[0]+"_casID",null);
	dbfs = new FormalCompound(new org.vcell.util.document.KeyValue("2"),(CompoundInfo)fsi);
	dbs = new BoundCompound(new org.vcell.util.document.KeyValue("3"),(FormalCompound)dbfs);
	model.addSpecies(new Species(names2[0],null,dbs));

	String keywords = "keword1,keyword2";
	String descr = "Decription Text";
	String[] names3 = new String[1];
	names3[0] = "IP3_Receptor";
	fsi = new ProteinInfo(names3[0]+"_SwissProtID",names3,names3[0]+"_Organism",names3[0]+"_Accession",keywords,descr);
	dbfs = new FormalProtein(new org.vcell.util.document.KeyValue("4"),(ProteinInfo)fsi);
	dbs = new BoundProtein(new org.vcell.util.document.KeyValue("5"),(FormalProtein)dbfs);
	model.addSpecies(new Species(names3[0],null,dbs));

	//names[0] = "IP3_Receptor_Activated";
	//fsi = new ProteinInfo(names[0]+"_SwissProtID",names,names[0]+"_Organism",names[0]+"_Accession",keywords,descr);
	//dbfs = new FormalProtein(new cbit.sql.KeyValue("6"),(ProteinInfo)fsi);
	//dbs = new BoundProtein(new cbit.sql.KeyValue("7"),(FormalProtein)dbfs);
	//make 1 unbound
	model.addSpecies(new Species("IP3_Receptor_Activated",null));

	
	model.addFeature("Extracellular");
	Feature extracellular = (Feature)model.getStructure("Extracellular");
	model.addFeature("Cytosol");
	Feature cytosol = (Feature)model.getStructure("Cytosol");
	Membrane plasmaMembrane = (Membrane)model.getStructure("PlasmaMembrane");
	model.addFeature("ER");
	Feature er = (Feature)model.getStructure("ER");
	Membrane erMembrane = (Membrane)model.getStructure("ER_Membrane");
	
	Species calcium = model.getSpecies("Calcium");
	Species ip3 = model.getSpecies("IP3");
	Species r = model.getSpecies("IP3_Receptor");
	Species ri = model.getSpecies("IP3_Receptor_Activated");
	
	model.addSpeciesContext(calcium,er);
	model.addSpeciesContext(calcium,cytosol);
	model.addSpeciesContext(calcium,extracellular);
	model.addSpeciesContext(ip3,cytosol);
	model.addSpeciesContext(ip3,extracellular);
	model.addSpeciesContext(r,erMembrane);
	model.addSpeciesContext(ri,erMembrane);

	SpeciesContext ip3_cytosol 			= model.getSpeciesContext(ip3,cytosol);
	SpeciesContext ip3_extracellular	= model.getSpeciesContext(ip3,extracellular);
	SpeciesContext calcium_cytosol 		= model.getSpeciesContext(calcium,cytosol);
	SpeciesContext calcium_extracellular = model.getSpeciesContext(calcium,extracellular);
	SpeciesContext calcium_er 			= model.getSpeciesContext(calcium,er);
	SpeciesContext r_erMembrane 		= model.getSpeciesContext(r,erMembrane);
	SpeciesContext ri_erMembrane 		= model.getSpeciesContext(ri,erMembrane);

	SimpleReaction sr;
	SimpleReaction sr1;
	FluxReaction fr;
	
	//
	// CYTOSOL REACTIONS
	//
	double IP3_DEGRADATION        = 0.5; // 1.0/12.0;
	double IP3_DESIRED_INITIAL    = 0.01;
	double IP3_DESIRED_FINAL      = 0.03;
	double PLASMA_MEM_SURFACE_TO_VOLUME_ER = 0.25;
	double IP3_FLUX_TIME_CONSTANT = 0.050;
	double IP3_FLUX_FINAL         = IP3_DEGRADATION * (IP3_DESIRED_FINAL - IP3_DESIRED_INITIAL) *
	                                (1 - FRACTIONAL_VOLUME_ER) / PLASMA_MEM_SURFACE_TO_VOLUME_ER;
	                              
	                         
	//
	//         IP3_DEGRADATION
	//   IP3 -----------------> 
	//     
	//
	//   source(IP3) = IP3_DEGRADATION * IP3_DESIRED_INITIAL
	//

//	model.addReaction("IP3_Volume");
//	reaction = model.getReaction("IP3_Volume");
	sr = new SimpleReaction(model, cytosol, "IP3_DEGRADATION", true);
	sr.addReactant(ip3_cytosol,1);
	sr.setKinetics(new MassActionKinetics(sr));
	MassActionKinetics massAct = (MassActionKinetics)sr.getKinetics();
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression("Kdegr1;"));
	massAct.setParameterValue(massAct.getKineticsParameter("Kdegr1"),new Expression(IP3_DEGRADATION));
	model.addReactionStep(sr);

	

	sr = new SimpleReaction(model, cytosol, "IP3_BASAL_CREATION", true);
	sr.addProduct(ip3_cytosol,1);
	massAct = new MassActionKinetics(sr);
	sr.setKinetics(massAct);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression("Kdegr2 * IP3i;"));
	massAct.setParameterValue(massAct.getKineticsParameter("Kdegr2"),new Expression(IP3_DEGRADATION));
	massAct.setParameterValue(massAct.getKineticsParameter("IP3i"),new Expression(IP3_DESIRED_INITIAL));
	model.addReactionStep(sr);
		
		
	sr1 = new SimpleReaction(model, cytosol, "IP3_DEGRADATION2", true);
	sr1.addReactant(ip3_cytosol,1);
	sr1.setKinetics(new HMM_IRRKinetics(sr1));
	HMM_IRRKinetics hmmKinetics = (HMM_IRRKinetics)sr1.getKinetics();
	hmmKinetics.setParameterValue(hmmKinetics.getVmaxParameter(),new Expression("10.0")); 
	hmmKinetics.setParameterValue(hmmKinetics.getKmParameter(),new Expression("12.0")); 
	model.addReactionStep(sr1);

	//
	//   flux(IP3) = IP3_FLUX_FINAL * (1 - exp(-t/IP3_FLUX_TIME_CONSTANT))
	//     
//	model.addReaction("IP3_generation");
//	reaction = model.getReaction("IP3_generation");
	fr = new FluxReaction(model, plasmaMembrane, null,"IP3_FLUX", true);
	GeneralKinetics genKinetics = new GeneralKinetics(fr);
	fr.setKinetics(genKinetics);
	genKinetics.setParameterValue(genKinetics.getReactionRateParameter(),new Expression("Jfinal * (1 - exp(-t/TAU));"));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("Jfinal"),new Expression(0.034));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("TAU"),new Expression(IP3_FLUX_TIME_CONSTANT));
	model.addReactionStep(fr);
		
		
	//
	// ER REACTIONS
	//	
	
	
	//
	// IP3 Receptor
	//
	double I1    = 3.33333333;
	double ii1   = 100;
	double i1    = ii1/I1;

	double channel_density = 25;
	double R    = 19.35; //channel_density / (1 + IP3_DESIRED_INITIAL * I1);
	double RI   =  0.65; //channel_density * IP3_DESIRED_INITIAL * I1 * R;
	double TOTAL_CHANNEL = R+RI;
	double channel_flux =143.7;
	double CALCIUM_DIFFUSION        = 180.0;
	double IP3_DIFFUSION            = 250;
	double CALCIUM_CYTOSOL          = 0.050;
	double CALCIUM_ER               = 2500.0;
	double CALCIUM_EXTRACELLULAR_INITIAL = 1000;
	double IP3_EXTRACELLULAR_INITIAL = 10;
	
//	model.addReaction("IP3_Receptor");
//	reaction = model.getReaction("IP3_Receptor");
	sr = new SimpleReaction(model, erMembrane, "IP3_BINDING", true);
	sr.addReactant(r_erMembrane,2);
	sr.addReactant(ip3_cytosol,3);
	sr.addProduct(ri_erMembrane,1);
	massAct = new MassActionKinetics(sr);
	sr.setKinetics(massAct);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression("ii1;"));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression("i1;"));
	massAct.setParameterValue(massAct.getKineticsParameter("ii1"),new Expression(ii1));
	massAct.setParameterValue(massAct.getKineticsParameter("i1"),new Expression("ii1/I1"));
	massAct.setParameterValue(massAct.getKineticsParameter("I1"),new Expression(I1));
	model.addReactionStep(sr);
	
	fr = new FluxReaction(model, erMembrane, null,"IP3R_FLUX", true);
	fr.addCatalyst(ri_erMembrane);
//	fr.addCatalyst(calcium_cytosol);
//	fr.addCatalyst(calcium_er);
//	fr.setInwardFlux(-channel_flux+" * "+ (4/(channel_density*channel_density))+" * pow(RI,3);");
	genKinetics = (GeneralKinetics)fr.getKinetics();
	genKinetics.setParameterValue(genKinetics.getReactionRateParameter(),new Expression("Jchan * ("+calcium_cytosol.getName()+" - "+calcium_er.getName()+") * pow("+ri_erMembrane.getName()+"/Rtotal,3);"));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("Rtotal"),new Expression(TOTAL_CHANNEL));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("Jchan"),new Expression(4.6));
	model.addReactionStep(fr);

	
	//
	// SERCA pump
	//
	double K_serca   = 0.270;
	double pump_coef = (K_serca*K_serca + CALCIUM_CYTOSOL*CALCIUM_CYTOSOL)*
								channel_flux * 4 * RI * RI * RI / 
								(channel_density*channel_density*CALCIUM_CYTOSOL*CALCIUM_CYTOSOL);
	
//	model.addReaction("Serca_Pump");
//	reaction = model.getReaction("Serca_Pump");
	fr = new FluxReaction(model, erMembrane, null,"SERCA_FLUX", true);
	genKinetics = (GeneralKinetics)fr.getKinetics();
	genKinetics.setParameterValue(genKinetics.getReactionRateParameter(),new Expression("Vmax * pow("+calcium_cytosol.getName()+",2) / (pow(Kd,2) + pow("+calcium_er.getName()+",2));"));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("Kd"),new Expression(0.7));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("Vmax"),new Expression(77.77));
	model.addReactionStep(fr);
	
	SpeciesContext sc = model.getSpeciesContext(r,erMembrane);
	//sc.setInitialValue(R);
	sc = model.getSpeciesContext(ri,erMembrane);
	//sc.setInitialValue(RI);
	
	sc = model.getSpeciesContext(calcium,er);
	//sc.setInitialValue(CALCIUM_ER);
	sc = model.getSpeciesContext(calcium,cytosol);
	//sc.setInitialValue(CALCIUM_CYTOSOL);
	//sc.setDiffusionRate(CALCIUM_DIFFUSION);
	sc = model.getSpeciesContext(calcium,extracellular);
	//sc.setDiffusionRate(CALCIUM_DIFFUSION);
	//sc.setInitialValue(CALCIUM_EXTRACELLULAR_INITIAL);
	
	sc = model.getSpeciesContext(ip3,cytosol);
	//sc.setInitialValue(IP3_DESIRED_INITIAL);
	//sc.setDiffusionRate(IP3_DIFFUSION);
	sc = model.getSpeciesContext(ip3,extracellular);
	//sc.setDiffusionRate(IP3_DIFFUSION);
	//sc.setInitialValue(IP3_EXTRACELLULAR_INITIAL);
	
	return model;
}
/**
 * This method was created by a SmartGuide.
 */
public static Model getExample_Wagner() throws Exception {

	//
	// Constants
	//
	double D = 300.0;
	double R = 500.0;	
	double r = 170.0;
	double LAMBDA = 75.0;
	double vL = 0.0005;
	double vP = 0.1;
	double Is = 0.12;
	double Ih = 1.0;
	double Iw = 0.015;
	double I1h = 0.84;
	double I1w = 0.8;
	double dI = 0.025;
	double dact = 1.2;
	double kP = 0.4;
	double dinh = 1.5;
	double TAU = 4;
	double RI_tot = 0.01;
	double RCact_tot = 0.01;
	double RCinh_tot = 0.01;
	double B_tot = 1400;
	double K = 100;
	double PI = 3.141592653589;
	
	//
	// initial conditions
	// 
	double Ca_init 			= 0.1153;
	double Ca_ER_init		= 10.0;
	double I_init 			= 0.12;
	double RCact_init		= RCact_tot * dact / (Ca_init + dact);
	double RCact_bound_init	= RCact_tot * Ca_init / (Ca_init + dact);
	double RCinh_init		= RCinh_tot * dinh / (Ca_init + dinh);
	double RCinh_bound_init	= RCinh_tot * Ca_init / (Ca_init + dinh);
	double B_init			= B_tot * K / (Ca_init + K);
	double CaB_init			= B_tot * Ca_init / (Ca_init + K);

	//
	// diffusion constants
	//
	double Ca_diff 			= D;
	double Ca_ER_diff		= 0.0;
	double I_diff 			= 0.0;
	double RCact_diff		= 0.0;
	double RCact_bound_diff	= 0.0;
	double RCinh_diff		= 0.0;
	double RCinh_bound_diff	= 0.0;
	double B_diff			= 0.0;
	double CaB_diff			= 0.0;

	//
	// 
	//
	
	Model model = new Model("oocyte3d");

	model.addSpecies(new Species("B","CalciumBufferUnbound"));
	Species B = model.getSpecies("B");
	model.addSpecies(new Species("CaB","CalciumBufferBound"));
	Species CaB = model.getSpecies("CaB");
	model.addSpecies(new Species("IP3Ra","IP3ReceptorCalciumActivationSiteUnbound"));
	Species RCact = model.getSpecies("IP3Ra");
	model.addSpecies(new Species("IP3RaCa","IP3ReceptorCalciumActivationSiteBound"));
	Species RCact_bound = model.getSpecies("IP3RaCa");
	model.addSpecies(new Species("IP3Ri","IP3ReceptorCalciumInhibitionSiteUnbound"));
	Species RCinh = model.getSpecies("IP3Ri");
	model.addSpecies(new Species("IP3RiCa","IP3ReceptorCalciumInhibitionSiteBound"));
	Species RCinh_bound = model.getSpecies("IP3RiCa");
	model.addSpecies(new Species("Ca","Calcium"));
	Species Ca = model.getSpecies("Ca");
	model.addSpecies(new Species("IP3","IP3"));
	Species I = model.getSpecies("IP3");
	
	Feature extracellular = model.addFeature("extracellular");
	Feature cytosol = model.addFeature("cytosol");
	Membrane plasmaMem = model.addMembrane("plasmaMembrane");
	Feature ER = model.addFeature("er");
	Membrane ERmem = model.addMembrane("erMembrane");
	
	
	model.addSpeciesContext(Ca,cytosol);
	SpeciesContext Ca_cyt = model.getSpeciesContext(Ca,cytosol);
	//Ca_cyt.setInitialValue(Ca_init);
	//Ca_cyt.setDiffusionRate(Ca_diff);

	model.addSpeciesContext(I,cytosol);
	SpeciesContext I_cyt = model.getSpeciesContext(I,cytosol);
	//I_cyt.setInitialValue(I_init);
	//I_cyt.setDiffusionRate(I_diff);

	model.addSpeciesContext(Ca,ER);
	SpeciesContext Ca_ER = model.getSpeciesContext(Ca,ER);
	//Ca_ER.setInitialValue(Ca_ER_init);
	//Ca_ER.setDiffusionRate(Ca_ER_diff);

	model.addSpeciesContext(RCact,ERmem);
	SpeciesContext RCact_ERmem = model.getSpeciesContext(RCact,ERmem);
	//RCact_ERmem.setInitialValue(RCact_init);
	//RCact_ERmem.setDiffusionRate(RCact_diff);

	model.addSpeciesContext(RCact_bound,ERmem);
	SpeciesContext RCact_bound_ERmem = model.getSpeciesContext(RCact_bound,ERmem);
	//RCact_bound_ERmem.setInitialValue(RCact_bound_init);
	//RCact_bound_ERmem.setDiffusionRate(RCact_bound_diff);

	model.addSpeciesContext(RCinh,ERmem);
	SpeciesContext RCinh_ERmem = model.getSpeciesContext(RCinh,ERmem);
	//RCinh_ERmem.setInitialValue(RCinh_init);
	//RCinh_ERmem.setDiffusionRate(RCinh_diff);

	model.addSpeciesContext(RCinh_bound,ERmem);
	SpeciesContext RCinh_bound_ERmem = model.getSpeciesContext(RCinh_bound,ERmem);
	//RCinh_bound_ERmem.setInitialValue(RCinh_bound_init);
	//RCinh_bound_ERmem.setDiffusionRate(RCinh_bound_diff);

	model.addSpeciesContext(B,cytosol);
	SpeciesContext B_cyt = model.getSpeciesContext(B,cytosol);
	//B_cyt.setInitialValue(B_init);
	//B_cyt.setDiffusionRate(B_diff);

	model.addSpeciesContext(CaB,cytosol);
	SpeciesContext CaB_cyt = model.getSpeciesContext(CaB,cytosol);
	//CaB_cyt.setInitialValue(CaB_init);
	//CaB_cyt.setDiffusionRate(CaB_diff);

	SimpleReaction sr;
	
	//
	// CYTOSOL REACTIONS
	//
	double K1f = 1.0;
	double K1r = 1.0;
	double K2f = 200.0;
	double K2r = 300.0;
	double K3f = 4.0;
	double K3r = 4.0;

	//
	// Calcium Buffering
	//
	sr = new SimpleReaction(model, cytosol, "CA_BUFFERING", true);
	sr.addReactant(Ca_cyt,1);
	sr.addReactant(B_cyt,1);
	sr.addProduct(CaB_cyt,1);
	MassActionKinetics massAct = new MassActionKinetics(sr);
	sr.setKinetics(massAct);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(1.0));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression("K"));
	massAct.setParameterValue(massAct.getKineticsParameter("K"),new Expression(K));
//	massAct.setFast(true);
	model.addReactionStep(sr);

	//
	// Calcium binding to RCact
	//
	sr = new SimpleReaction(model, ERmem, "CA_BINDING_ACT", true);
	sr.addReactant(Ca_cyt,1);
	sr.addReactant(RCact_ERmem,1);
	sr.addProduct(RCact_bound_ERmem,1);
	massAct = new MassActionKinetics(sr);
	sr.setKinetics(massAct);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(1.0));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression("dact;"));
	massAct.setParameterValue(massAct.getKineticsParameter("dact"),new Expression(dact));
//	massAct.setFast(true);
	model.addReactionStep(sr);

	//
	// Calcium binding to RCinh
	//
	sr = new SimpleReaction(model, ERmem, "CA_BINDING_INH", true);
	sr.addReactant(Ca_cyt,1);
	sr.addReactant(RCinh_ERmem,1);
	sr.addProduct(RCinh_bound_ERmem,1);
	massAct = new MassActionKinetics(sr);
	sr.setKinetics(massAct);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression("1.0/TAU;"));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression("dinh/TAU;"));
	massAct.setParameterValue(massAct.getKineticsParameter("dinh"),new Expression(dinh));
	massAct.setParameterValue(massAct.getKineticsParameter("TAU"),new Expression(TAU));
//	massAct.setFast(false);
	model.addReactionStep(sr);

	//
	// calcium flux through the IP3 Receptor
	//
	FluxReaction fr = new FluxReaction(model, ERmem, null,"IP3R_FLUX", true);
	fr.addCatalyst(I_cyt);
	fr.addCatalyst(RCact_bound_ERmem);
	fr.addCatalyst(RCact_ERmem);
	fr.addCatalyst(RCinh_ERmem);
	fr.addCatalyst(RCinh_bound_ERmem);
	GeneralKinetics genKinetics = new GeneralKinetics(fr);
	fr.setKinetics(genKinetics);
	genKinetics.setParameterValue(genKinetics.getReactionRateParameter(),new Expression("-LAMBDA1*(Ca_er-Ca_cytosol)*pow((IP3_cytosol/(IP3_cytosol+dI))*(IP3RaCa_erMembrane/(IP3RaCa_erMembrane+IP3Ra_erMembrane))*(IP3Ri_erMembrane/(IP3RiCa_erMembrane+IP3Ri_erMembrane)),3);"));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("LAMBDA1"),new Expression(LAMBDA));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("dI"),new Expression(dI));
	model.addReactionStep(fr);

	//
	// calcium flux through the SERCA pump
	//
	fr = new FluxReaction(model, ERmem, null,"SERCA_FLUX", true);
	genKinetics = new GeneralKinetics(fr);
	fr.setKinetics(genKinetics);
	genKinetics.setParameterValue(genKinetics.getReactionRateParameter(),new Expression("LAMBDA2*vP*Ca_cytosol*Ca_cytosol/(kP*kP + Ca_cytosol*Ca_cytosol);"));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("vP"),new Expression(vP));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("kP"),new Expression(kP));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("LAMBDA2"),new Expression(LAMBDA));
	model.addReactionStep(fr);

	//
	// calcium flux through leak
	//
	fr = new FluxReaction(model, ERmem, null,"LEAK_FLUX", true);
	genKinetics = new GeneralKinetics(fr);
	fr.setKinetics(genKinetics);
	genKinetics.setParameterValue(genKinetics.getReactionRateParameter(),new Expression("-LAMBDA3*vL*(Ca_er-Ca_cytosol);"));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("vL"),new Expression(vL));
	genKinetics.setParameterValue(genKinetics.getKineticsParameter("LAMBDA3"),new Expression(LAMBDA));
	model.addReactionStep(fr);

	return model;
}
/**
 * This method was created by a SmartGuide.
 */
public static Model getExample2() throws Exception {

	double A_init = 10.0;
	double B_init = 20.0;
	double C_init = 30.0;
	double D_init = 40.0;
	double A_diff = 11.0;
	double B_diff = 22.0;
	double C_diff = 33.0;
	double D_diff = 44.0;
	
	Model model = new Model("model1");

	model.addSpecies(new Species("A","A"));
	Species A = model.getSpecies("A");
	model.addSpecies(new Species("B","B"));
	Species B = model.getSpecies("B");
	model.addSpecies(new Species("C","C"));
	Species C = model.getSpecies("C");
	model.addSpecies(new Species("D","D"));
	Species D = model.getSpecies("D");
	
	model.addFeature("Cytosol");
	Feature cytosol = (Feature)model.getStructure("Cytosol");
	
	
	model.addSpeciesContext(A,cytosol);
	SpeciesContext A_cyt = model.getSpeciesContext(A,cytosol);
	//A_cyt.setInitialValue(A_init);
	//A_cyt.setDiffusionRate(A_diff);

	model.addSpeciesContext(B,cytosol);
	SpeciesContext B_cyt = model.getSpeciesContext(B,cytosol);
	//B_cyt.setInitialValue(B_init);
	//B_cyt.setDiffusionRate(B_diff);

	model.addSpeciesContext(C,cytosol);
	SpeciesContext C_cyt = model.getSpeciesContext(C,cytosol);
	//C_cyt.setInitialValue(C_init);
	//C_cyt.setDiffusionRate(C_diff);

	model.addSpeciesContext(D,cytosol);
	SpeciesContext D_cyt = model.getSpeciesContext(D,cytosol);
	//D_cyt.setInitialValue(D_init);
	//D_cyt.setDiffusionRate(D_diff);


	SimpleReaction sr;
	
	//
	// CYTOSOL REACTIONS
	//
	double K1 = 1.0;
	double K2 = 2.0;
	double K3 = 3.0;
	double K4 = 4.0;

	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACTION_ABC", true);
	sr.addReactant(A_cyt,1);
	sr.addReactant(B_cyt,1);
	sr.addProduct(C_cyt,1);
	MassActionKinetics massAct = new MassActionKinetics(sr);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(K1));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression(K2));
	massAct.renameParameter(massAct.getForwardRateParameter().getName(),"K1");
	massAct.renameParameter(massAct.getReverseRateParameter().getName(),"K2");
	sr.setKinetics(massAct);
	model.addReactionStep(sr);

	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACION_CDA", true);
	sr.addReactant(C_cyt,1);
	sr.addReactant(D_cyt,1);
	sr.addProduct(A_cyt,1);
	massAct = new MassActionKinetics(sr);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(K3));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression(K4));
	massAct.renameParameter(massAct.getForwardRateParameter().getName(),"K3");
	massAct.renameParameter(massAct.getReverseRateParameter().getName(),"K4");
	sr.setKinetics(massAct);
	model.addReactionStep(sr);

	Diagram diagram = model.getDiagram(cytosol);
	String diagramFile =" { " +
						"   SpeciesContextSpec A_cyt 100 100 " +
						"   SpeciesContextSpec B_cyt 50 200 " +
						"   SpeciesContextSpec C_cyt 200 200 " +
						"   SpeciesContextSpec D_cyt 200 50 " +
						"   SimpleReaction SIMPLE_REACTION_ABC 75 150 " +
						"   SimpleReaction SIMPLE_REACION_CDA 200 125 " +
						"} ";
	org.vcell.util.CommentStringTokenizer st = new org.vcell.util.CommentStringTokenizer(diagramFile);
	diagram.fromTokens(st);
			
	return model;
}
/**
 * This method was created by a SmartGuide.
 */
public static Model getExample2_Fast() throws Exception {

	double A_init = 10.0;
	double B_init = 20.0;
	double C_init = 30.0;
	double D_init = 40.0;
	double E_init = 50.0;
	double F_init = 60.0;
	double A_diff = 11.0;
	double B_diff = 22.0;
	double C_diff = 33.0;
	double D_diff = 44.0;
	double E_diff = 55.0;
	double F_diff = 66.0;
	
	Model model = new Model("model1");

	model.addSpecies(new Species("A","A"));
	Species A = model.getSpecies("A");
	model.addSpecies(new Species("B","B"));
	Species B = model.getSpecies("B");
	model.addSpecies(new Species("C","C"));
	Species C = model.getSpecies("C");
	model.addSpecies(new Species("D","D"));
	Species D = model.getSpecies("D");
	model.addSpecies(new Species("E","E"));
	Species E = model.getSpecies("E");
	model.addSpecies(new Species("F","F"));
	Species F = model.getSpecies("F");
	
	model.addFeature("Cytosol");
	Feature cytosol = (Feature)model.getStructure("Cytosol");
	
	
	model.addSpeciesContext(F,cytosol);
	SpeciesContext F_cyt = model.getSpeciesContext(F,cytosol);
	//F_cyt.setInitialValue(F_init);
	//F_cyt.setDiffusionRate(F_diff);

	model.addSpeciesContext(A,cytosol);
	SpeciesContext A_cyt = model.getSpeciesContext(A,cytosol);
	//A_cyt.setInitialValue(A_init);
	//A_cyt.setDiffusionRate(A_diff);

	model.addSpeciesContext(B,cytosol);
	SpeciesContext B_cyt = model.getSpeciesContext(B,cytosol);
	//B_cyt.setInitialValue(B_init);
	//B_cyt.setDiffusionRate(B_diff);

	model.addSpeciesContext(D,cytosol);
	SpeciesContext D_cyt = model.getSpeciesContext(D,cytosol);
	//D_cyt.setInitialValue(D_init);
	//D_cyt.setDiffusionRate(D_diff);

	model.addSpeciesContext(E,cytosol);
	SpeciesContext E_cyt = model.getSpeciesContext(E,cytosol);
	//E_cyt.setInitialValue(E_init);
	//E_cyt.setDiffusionRate(E_diff);

	model.addSpeciesContext(C,cytosol);
	SpeciesContext C_cyt = model.getSpeciesContext(C,cytosol);
	//C_cyt.setInitialValue(C_init);
	//C_cyt.setDiffusionRate(C_diff);


	SimpleReaction sr;
	
	//
	// CYTOSOL REACTIONS
	//
	double K1f = 1.0;
	double K1r = 1.0;
	double K2f = 200.0;
	double K2r = 300.0;
	double K3f = 4.0;
	double K3r = 4.0;

	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACTION_ABC", true);
	sr.addReactant(A_cyt,1);
	sr.addReactant(B_cyt,1);
	sr.addProduct(C_cyt,1);
	MassActionKinetics massAct = new MassActionKinetics(sr);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(K1f));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression(K1r));
	massAct.getForwardRateParameter().setName("K1f");
	massAct.getReverseRateParameter().setName("K1r");
//	massAct.setFast(false);
	sr.setKinetics(massAct);
	model.addReactionStep(sr);

	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACTION_CDE", true);
	sr.addReactant(C_cyt,1);
	sr.addReactant(D_cyt,1);
	sr.addProduct(E_cyt,1);
	massAct = new MassActionKinetics(sr);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(K2f));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression(K2r));
	massAct.getForwardRateParameter().setName("K2f");
	massAct.getReverseRateParameter().setName("K2r");
//	massAct.setFast(true);
	sr.setKinetics(massAct);
	model.addReactionStep(sr);
			
	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACTION_EAF", true);
	sr.addReactant(E_cyt,1);
	sr.addReactant(A_cyt,1);
	sr.addProduct(F_cyt,1);
	massAct = new MassActionKinetics(sr);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(K3f));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression(K3r));
	massAct.getForwardRateParameter().setName("K3f");
	massAct.getReverseRateParameter().setName("K3r");
//	massAct.setFast(false);
	sr.setKinetics(massAct);
	model.addReactionStep(sr);
			
	return model;
}
/**
 * This method was created by a SmartGuide.
 */
public static Model getExampleForFluorescenceIndicatorProtocol() throws Exception {

	double A_init = 10.0;
	double B_init = 20.0;
	double C_init = 30.0;
	double D_init = 40.0;
	double E_init = 35.0;
	double I_init = 50.0;
	double A_diff = 11.0;
	double B_diff = 22.0;
	double C_diff = 33.0;
	double D_diff = 44.0;
	double E_diff = 55.0;
	double I_diff = 66.0;
	
	Model model = new Model("model1");

	model.addSpecies(new Species("A","A"));
	Species A = model.getSpecies("A");
	model.addSpecies(new Species("B","B"));
	Species B = model.getSpecies("B");
	model.addSpecies(new Species("C","C"));
	Species C = model.getSpecies("C");
	model.addSpecies(new Species("D","D"));
	Species D = model.getSpecies("D");
	model.addSpecies(new Species("E","E"));
	Species E = model.getSpecies("E");
	model.addSpecies(new Species("I","I"));
	Species I = model.getSpecies("I");
	
	model.addFeature("Cytosol");
	Feature cytosol = (Feature)model.getStructure("Cytosol");
	
	model.addSpeciesContext(A,cytosol);
	SpeciesContext A_cyt = model.getSpeciesContext(A,cytosol);
	//A_cyt.setInitialValue(A_init);
	//A_cyt.setDiffusionRate(A_diff);

	model.addSpeciesContext(B,cytosol);
	SpeciesContext B_cyt = model.getSpeciesContext(B,cytosol);
	//B_cyt.setInitialValue(B_init);
	//B_cyt.setDiffusionRate(B_diff);

	model.addSpeciesContext(C,cytosol);
	SpeciesContext C_cyt = model.getSpeciesContext(C,cytosol);
	//C_cyt.setInitialValue(C_init);
	//C_cyt.setDiffusionRate(C_diff);

	model.addSpeciesContext(D,cytosol);
	SpeciesContext D_cyt = model.getSpeciesContext(D,cytosol);
	//D_cyt.setInitialValue(D_init);
	//D_cyt.setDiffusionRate(D_diff);

	model.addSpeciesContext(E,cytosol);
	SpeciesContext E_cyt = model.getSpeciesContext(E,cytosol);
	//E_cyt.setInitialValue(E_init);
	//E_cyt.setDiffusionRate(E_diff);
	
	model.addSpeciesContext(I,cytosol);
	SpeciesContext I_cyt = model.getSpeciesContext(I,cytosol);
	//I_cyt.setInitialValue(I_init);
	//I_cyt.setDiffusionRate(I_diff);

	SimpleReaction sr;
	
	//
	// CYTOSOL REACTIONS
	//
	double K1 = 1.0;
	double K2 = 2.0;
	double K3 = 3.0;
	double K4 = 4.0;

	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACTION_ABC", true);
	sr.addReactant(A_cyt,1);
	sr.addReactant(B_cyt,1);
	sr.addProduct(C_cyt,1);
	MassActionKinetics massAct = new MassActionKinetics(sr);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(K1));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression(K2));
	massAct.renameParameter(massAct.getForwardRateParameter().getName(),"K1");
	massAct.renameParameter(massAct.getReverseRateParameter().getName(),"K2");
	sr.setKinetics(massAct);
	model.addReactionStep(sr);

	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACION_CDE", true);
	sr.addReactant(C_cyt,1);
	sr.addReactant(D_cyt,1);
	sr.addProduct(E_cyt,1);
	massAct = new MassActionKinetics(sr);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(K3));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression(K4));
	massAct.renameParameter(massAct.getForwardRateParameter().getName(),"K3");
	massAct.renameParameter(massAct.getReverseRateParameter().getName(),"K4");
	sr.setKinetics(massAct);
	model.addReactionStep(sr);

	return model;
}
/**
 * This method was created by a SmartGuide.
 */
public static Model getExampleForFluorescenceLabelProtocol() throws Exception {

	double A_init = 10.0;
	double B_init = 20.0;
	double C_init = 30.0;
	double D_init = 40.0;
	double E_init = 35.0;
	double A_diff = 11.0;
	double B_diff = 22.0;
	double C_diff = 33.0;
	double D_diff = 44.0;
	double E_diff = 44.0;
	
	Model model = new Model("model1");

	model.addSpecies(new Species("A","A"));
	Species A = model.getSpecies("A");
	model.addSpecies(new Species("B","B"));
	Species B = model.getSpecies("B");
	model.addSpecies(new Species("C","C"));
	Species C = model.getSpecies("C");
	model.addSpecies(new Species("D","D"));
	Species D = model.getSpecies("D");
	model.addSpecies(new Species("E","E"));
	Species E = model.getSpecies("E");
	
	model.addFeature("Cytosol");
	Feature cytosol = (Feature)model.getStructure("Cytosol");
	
	
	model.addSpeciesContext(A,cytosol);
	SpeciesContext A_cyt = model.getSpeciesContext(A,cytosol);
	//A_cyt.setInitialValue(A_init);
	//A_cyt.setDiffusionRate(A_diff);

	model.addSpeciesContext(B,cytosol);
	SpeciesContext B_cyt = model.getSpeciesContext(B,cytosol);
	//B_cyt.setInitialValue(B_init);
	//B_cyt.setDiffusionRate(B_diff);

	model.addSpeciesContext(C,cytosol);
	SpeciesContext C_cyt = model.getSpeciesContext(C,cytosol);
	//C_cyt.setInitialValue(C_init);
	//C_cyt.setDiffusionRate(C_diff);

	model.addSpeciesContext(D,cytosol);
	SpeciesContext D_cyt = model.getSpeciesContext(D,cytosol);
	//D_cyt.setInitialValue(D_init);
	//D_cyt.setDiffusionRate(D_diff);

	model.addSpeciesContext(E,cytosol);
	SpeciesContext E_cyt = model.getSpeciesContext(E,cytosol);
	//E_cyt.setInitialValue(E_init);
	//E_cyt.setDiffusionRate(E_diff);
	

	SimpleReaction sr;
	
	//
	// CYTOSOL REACTIONS
	//
	double K1 = 1.0;
	double K2 = 2.0;
	double K3 = 3.0;
	double K4 = 4.0;

	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACTION_ABC", true);
	sr.addReactant(A_cyt,1);
	sr.addReactant(B_cyt,1);
	sr.addProduct(C_cyt,1);
	MassActionKinetics massAct = new MassActionKinetics(sr);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(K1));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression(K2));
	massAct.renameParameter(massAct.getForwardRateParameter().getName(),"K1");
	massAct.renameParameter(massAct.getReverseRateParameter().getName(),"K2");
	sr.setKinetics(massAct);
	model.addReactionStep(sr);

	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACION_CDE", true);
	sr.addReactant(C_cyt,1);
	sr.addReactant(D_cyt,1);
	sr.addProduct(E_cyt,1);
	massAct = new MassActionKinetics(sr);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(K3));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression(K4));
	massAct.renameParameter(massAct.getForwardRateParameter().getName(),"K3");
	massAct.renameParameter(massAct.getReverseRateParameter().getName(),"K4");
	sr.setKinetics(massAct);
	model.addReactionStep(sr);

	return model;
}
/**
 * This method was created by a SmartGuide.
 */
public static Model getExampleHMM() throws Exception {

	double A_init = 10.0;
	double B_init = 20.0;
	double C_init = 30.0;
	double D_init = 40.0;
	double A_diff = 11.0;
	double B_diff = 22.0;
	double C_diff = 33.0;
	double D_diff = 44.0;
	
	Model model = new Model("model1");

	model.addSpecies(new Species("A","A"));
	Species A = model.getSpecies("A");
	model.addSpecies(new Species("B","B"));
	Species B = model.getSpecies("B");
	model.addSpecies(new Species("C","C"));
	Species C = model.getSpecies("C");
	model.addSpecies(new Species("D","D"));
	Species D = model.getSpecies("D");
	
	model.addFeature("Cytosol");
	Feature cytosol = (Feature)model.getStructure("Cytosol");
	
	
	model.addSpeciesContext(A,cytosol);
	SpeciesContext A_cyt = model.getSpeciesContext(A,cytosol);
	//A_cyt.setInitialValue(A_init);
	//A_cyt.setDiffusionRate(A_diff);

	model.addSpeciesContext(B,cytosol);
	SpeciesContext B_cyt = model.getSpeciesContext(B,cytosol);
	//B_cyt.setInitialValue(B_init);
	//B_cyt.setDiffusionRate(B_diff);

	model.addSpeciesContext(C,cytosol);
	SpeciesContext C_cyt = model.getSpeciesContext(C,cytosol);
	//C_cyt.setInitialValue(C_init);
	//C_cyt.setDiffusionRate(C_diff);

	model.addSpeciesContext(D,cytosol);
	SpeciesContext D_cyt = model.getSpeciesContext(D,cytosol);
	//D_cyt.setInitialValue(D_init);
	//D_cyt.setDiffusionRate(D_diff);



	SimpleReaction sr;
	
	//
	// CYTOSOL REACTIONS
	//
	double K1 = 1.0;
	double K2 = 2.0;
	double K3 = 3.0;
	double K4 = 4.0;

	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACTION_ABC", true);
	sr.addReactant(A_cyt,1);
//	sr.addReactant(B_cyt,1);
	sr.addProduct(B_cyt,1);
	HMM_IRRKinetics hmmKinetics = new HMM_IRRKinetics(sr);
	hmmKinetics.setParameterValue(hmmKinetics.getKmParameter(),new Expression("1.0"));
	hmmKinetics.setParameterValue(hmmKinetics.getVmaxParameter(),new Expression("20.0"));
	//hmmKinetics.setParameterValue("K1",Double.toString(K1));
	//hmmKinetics.setParameterValue("K2",Double.toString(K2));
	sr.setKinetics(hmmKinetics);
	model.addReactionStep(sr);

	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACION_CDA", true);
	sr.addReactant(C_cyt,1);
	sr.addReactant(D_cyt,1);
	sr.addProduct(A_cyt,1);
	MassActionKinetics massAct = new MassActionKinetics(sr);
	massAct = new MassActionKinetics(sr);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression("3.0"));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression("4.0"));
	//massAct.setParameterValue("K3",Double.toString(K3));
	//massAct.setParameterValue("K4",Double.toString(K4));
	sr.setKinetics(massAct);
	model.addReactionStep(sr);

	Diagram diagram = model.getDiagram(cytosol);
	String diagramFile =" { " +
						"   SpeciesContextSpec A_cyt 100 100 " +
						"   SpeciesContextSpec B_cyt 50 200 " +
						"   SpeciesContextSpec C_cyt 200 200 " +
						"   SpeciesContextSpec D_cyt 200 50 " +
						"   SimpleReaction SIMPLE_REACTION_ABC 75 150 " +
						"   SimpleReaction SIMPLE_REACION_CDA 200 125 " +
						"} ";
	org.vcell.util.CommentStringTokenizer st = new org.vcell.util.CommentStringTokenizer(diagramFile);
	diagram.fromTokens(st);
			
	return model;
}
/**
 * This method was created by a SmartGuide.
 */
public static Model getExampleWithCurrent() throws Exception {

	double A_init = 10.0;
	double B_init = 20.0;
	double C_init = 30.0;
	double D_init = 40.0;
	double A_ec_init = 10.0;
	double B_ec_init = 20.0;
	double C_ec_init = 30.0;
	double D_ec_init = 40.0;
	double Vmax = 1.0;
	double Kd_Bcyt = 0.02;
	
	Model model = new Model("modelElectrical");

	model.addSpecies(new Species("A","A"));
	Species A = model.getSpecies("A");
	model.addSpecies(new Species("B","B"));
	Species B = model.getSpecies("B");
	model.addSpecies(new Species("C","C"));
	Species C = model.getSpecies("C");
	model.addSpecies(new Species("D","D"));
	Species D = model.getSpecies("D");
	
	model.addFeature("extracellular");
	Feature extracellular = (Feature)model.getStructure("extracellular");
	model.addFeature("cytosol");
	Feature cytosol = (Feature)model.getStructure("cytosol");
	Membrane PM = (Membrane)model.getStructure("PM");
	
	
	model.addSpeciesContext(A,cytosol);
	SpeciesContext A_cyt = model.getSpeciesContext(A,cytosol);
	//A_cyt.setInitialValue(A_init);

	model.addSpeciesContext(B,cytosol);
	SpeciesContext B_cyt = model.getSpeciesContext(B,cytosol);
	//B_cyt.setInitialValue(B_init);

	model.addSpeciesContext(C,cytosol);
	SpeciesContext C_cyt = model.getSpeciesContext(C,cytosol);
	//C_cyt.setInitialValue(C_init);

	model.addSpeciesContext(D,cytosol);
	SpeciesContext D_cyt = model.getSpeciesContext(D,cytosol);
	//D_cyt.setInitialValue(D_init);

	model.addSpeciesContext(A,extracellular);
	SpeciesContext A_ec = model.getSpeciesContext(A,cytosol);
	//A_cyt.setInitialValue(A_ec_init);

	model.addSpeciesContext(B,extracellular);
	SpeciesContext B_ec = model.getSpeciesContext(B,cytosol);
	//B_cyt.setInitialValue(B_ec_init);

	model.addSpeciesContext(C,extracellular);
	SpeciesContext C_ec = model.getSpeciesContext(C,cytosol);
	//C_cyt.setInitialValue(C_ec_init);

	model.addSpeciesContext(D,extracellular);
	SpeciesContext D_ec = model.getSpeciesContext(D,cytosol);
	//D_cyt.setInitialValue(D_ec_init);


	SimpleReaction sr;
	
	//
	// CYTOSOL REACTIONS
	//
	double K1 = 1.0;
	double K2 = 2.0;
	double K3 = 3.0;
	double K4 = 4.0;

	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACTION_ABC", true);
	sr.addReactant(A_cyt,1);
	sr.addReactant(B_cyt,1);
	sr.addProduct(C_cyt,1);
	MassActionKinetics massAct = new MassActionKinetics(sr);
	sr.setKinetics(massAct);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(K1));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression(K2));
	massAct.renameParameter(massAct.getForwardRateParameter().getName(),"K1");
	massAct.renameParameter(massAct.getReverseRateParameter().getName(),"K2");
	model.addReactionStep(sr);

	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACION_CDA", true);
	sr.addReactant(C_cyt,1);
	sr.addReactant(D_cyt,1);
	sr.addProduct(A_cyt,1);
	massAct = new MassActionKinetics(sr);
	sr.setKinetics(massAct);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(K3));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression(K4));
	massAct.renameParameter(massAct.getForwardRateParameter().getName(),"K3");
	massAct.renameParameter(massAct.getReverseRateParameter().getName(),"K4");
	model.addReactionStep(sr);

	Diagram diagram = model.getDiagram(cytosol);
	String diagramFile =" { " +
						"   SpeciesContextSpec A_cyt 100 100 " +
						"   SpeciesContextSpec B_cyt 50 200 " +
						"   SpeciesContextSpec C_cyt 200 200 " +
						"   SpeciesContextSpec D_cyt 200 50 " +
						"   SimpleReaction SIMPLE_REACTION_ABC 75 150 " +
						"   SimpleReaction SIMPLE_REACION_CDA 200 125 " +
						"} ";
	org.vcell.util.CommentStringTokenizer st = new org.vcell.util.CommentStringTokenizer(diagramFile);
	diagram.fromTokens(st);
			
	FluxReaction fr;
	
	//
	// PlasmaMembrane REACTIONS
	//
	fr = new FluxReaction(model, PM, null,"A Flux", true);
	fr.addCatalyst(B_cyt);
	GHKKinetics ghk = new GHKKinetics(fr);
	fr.setKinetics(ghk);
	KineticsParameter chargeValenceParameter = fr.getKinetics().getKineticsParameterFromRole(Kinetics.ROLE_ChargeValence);
	if (chargeValenceParameter!=null){
		chargeValenceParameter.setExpression(new Expression(2));
	}
	//ghk.setPermeability(new Expression("(Vmax*B_cyt/(Kd_Bcyt+B_cyt))"));
	ghk.setParameterValue(ghk.getPermeabilityParameter(),new Expression(8.314e-2));
	//ghk.setParameterValue("Vmax",Double.toString(Vmax));
	//ghk.setParameterValue("Kd_Bcyt",Double.toString(Kd_Bcyt));
	model.addReactionStep(fr);

	//diagram = model.getDiagram(PM);
	//String diagramFile =" { " +
						//"   SpeciesContextSpec A_cyt 100 100 " +
						//"   SpeciesContextSpec B_cyt 50 200 " +
						//"   SpeciesContextSpec C_cyt 200 200 " +
						//"   SpeciesContextSpec D_cyt 200 50 " +
						//"   SimpleReaction SIMPLE_REACTION_ABC 75 150 " +
						//"   SimpleReaction SIMPLE_REACION_CDA 200 125 " +
						//"} ";
	//cbit.vcell.math.CommentStringTokenizer st = new cbit.vcell.math.CommentStringTokenizer(diagramFile);
	//diagram.fromTokens(st);
			
	return model;
}
/**
 * This method was created by a SmartGuide.
 */
public static Model getExample_GlobalParams() throws Exception {

	double A_init = 10.0;
	double B_init = 20.0;
	double C_init = 30.0;
	double D_init = 40.0;
	double A_diff = 11.0;
	double B_diff = 22.0;
	double C_diff = 33.0;
	double D_diff = 44.0;
	
	Model model = new Model("model1");

	model.addSpecies(new Species("A","A"));
	Species A = model.getSpecies("A");
	model.addSpecies(new Species("B","B"));
	Species B = model.getSpecies("B");
	model.addSpecies(new Species("C","C"));
	Species C = model.getSpecies("C");
	model.addSpecies(new Species("D","D"));
	Species D = model.getSpecies("D");
	
	model.addFeature("Cytosol");
	Feature cytosol = (Feature)model.getStructure("Cytosol");
	
	
	model.addSpeciesContext(A,cytosol);
	SpeciesContext A_cyt = model.getSpeciesContext(A,cytosol);

	model.addSpeciesContext(B,cytosol);
	SpeciesContext B_cyt = model.getSpeciesContext(B,cytosol);

	model.addSpeciesContext(C,cytosol);
	SpeciesContext C_cyt = model.getSpeciesContext(C,cytosol);

	model.addSpeciesContext(D,cytosol);
	SpeciesContext D_cyt = model.getSpeciesContext(D,cytosol);

	// add global parameters to the model
	VCUnitDefinition volumeReactionRateUnit = model.getUnitSystem().getVolumeReactionRateUnit();
	ModelParameter gp1 = model.new ModelParameter("Kon_1", new Expression(3.0), Model.ROLE_UserDefined, volumeReactionRateUnit);
	gp1.setDescription("first global parameter");
	model.addModelParameter(gp1);
	ModelParameter gp2 = model.new ModelParameter("Koff_1", new Expression(4.0), Model.ROLE_UserDefined, volumeReactionRateUnit);
	gp2.setDescription("second global parameter");
	model.addModelParameter(gp2);
	
	SimpleReaction sr;
	
	//
	// CYTOSOL REACTIONS
	//
	double K1 = 1.0;
	double K2 = 2.0;
	double K3 = 3.0;
	double K4 = 4.0;

	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACTION_ABC", true);
	sr.setModel(model);
	sr.addReactant(A_cyt,1);
	sr.addReactant(B_cyt,1);
	sr.addProduct(C_cyt,1);
	MassActionKinetics massAct = new MassActionKinetics(sr);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(K1));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression(K2));
	massAct.renameParameter(massAct.getForwardRateParameter().getName(),"K1");
	massAct.renameParameter(massAct.getReverseRateParameter().getName(),"K2");
	sr.setKinetics(massAct);
	model.addReactionStep(sr);

	sr = new SimpleReaction(model, cytosol, "SIMPLE_REACION_CDA", true);
	sr.setModel(model);
	sr.addReactant(C_cyt,1);
	sr.addReactant(D_cyt,1);
	sr.addProduct(A_cyt,1);
	massAct = new MassActionKinetics(sr);
	massAct.setParameterValue(massAct.getForwardRateParameter(),new Expression(K3));
	massAct.setParameterValue(massAct.getReverseRateParameter(),new Expression(K4));
	massAct.renameParameter(massAct.getForwardRateParameter().getName(),"K3");
	massAct.renameParameter(massAct.getReverseRateParameter().getName(),"K4");
	sr.setKinetics(massAct);
	model.addReactionStep(sr);

	Diagram diagram = model.getDiagram(cytosol);
	String diagramFile =" { " +
						"   SpeciesContextSpec A_cyt 100 100 " +
						"   SpeciesContextSpec B_cyt 50 200 " +
						"   SpeciesContextSpec C_cyt 200 200 " +
						"   SpeciesContextSpec D_cyt 200 50 " +
						"   SimpleReaction SIMPLE_REACTION_ABC 75 150 " +
						"   SimpleReaction SIMPLE_REACION_CDA 200 125 " +
						"} ";
	org.vcell.util.CommentStringTokenizer st = new org.vcell.util.CommentStringTokenizer(diagramFile);
	diagram.fromTokens(st);
			
	return model;
}
}
