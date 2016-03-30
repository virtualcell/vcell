package cbit.vcell.server.bionetgen;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.vcell.model.bngl.ASTModel;
import org.vcell.model.bngl.BNGLParser;
import org.vcell.model.bngl.ParseException;
import org.vcell.model.rbm.NetworkConstraints;
import org.vcell.model.rbm.RbmNetworkGenerator;
import org.vcell.model.rbm.RbmUtils;

import com.ibm.icu.util.StringTokenizer;

import cbit.vcell.bionetgen.BNGOutputFileParser;
import cbit.vcell.bionetgen.BNGOutputSpec;
import cbit.vcell.mapping.BioNetGenUpdaterCallback;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.mapping.TaskCallbackMessage;
import cbit.vcell.mapping.SimulationContext.NetworkGenerationRequirements;
import cbit.vcell.mapping.TaskCallbackMessage.TaskCallbackStatus;

public class BNGExecutorServiceMultipass implements BNGExecutorService {

	private final BNGInput cBngInput;	// compartmental .bng input file
	
	private final Long timeoutDurationMS;
	private transient List<BioNetGenUpdaterCallback> callbacks = null;
	private boolean bStopped = false;
	private BNGExecutorServiceNative onepassBngService;
	private long startTime = -1;
	
	BNGExecutorServiceMultipass(BNGInput cBngInput, Long timeoutDurationMS) {
		this.cBngInput = cBngInput;
		this.timeoutDurationMS = timeoutDurationMS;
	}
	
	@Override
	public void registerBngUpdaterCallback(BioNetGenUpdaterCallback callbackOwner) {
		getCallbacks().add(callbackOwner);
	}

	@Override
	public List<BioNetGenUpdaterCallback> getCallbacks() {
		if(callbacks == null) {
			callbacks = new ArrayList<BioNetGenUpdaterCallback>();
		}
		return callbacks;
	}

	@Override
	public BNGOutput executeBNG() throws BNGException {
		this.startTime = System.currentTimeMillis();
		
		String cBngInputString = cBngInput.getInputString();
		
		NetworkConstraints nc = extractNetworkConstraints(cBngInputString);
		// the "trick" - the modified molecules, species, etc - everything has an extra Site 
		// with the compartments as possible States
		String sBngInputString = preprocessInput(cBngInputString);
		BNGInput sBngInput = new BNGInput(sBngInputString);
		BNGOutput sBngOutput = null;		// output after each iteration
		String sBngOutputString = null;
		
		BNGOutputSpec outputSpec = null;
		
		for (int i = 0; i<nc.getMaxIteration(); i++) {
		
			this.onepassBngService = new BNGExecutorServiceNative(sBngInput, timeoutDurationMS);
			for (BioNetGenUpdaterCallback callback : getCallbacks()){
//				this.onepassBngService.registerBngUpdaterCallback(callback);
			}
			sBngOutput = this.onepassBngService.executeBNG();
			this.onepassBngService = null;
		
			sBngOutputString = sBngOutput.getNetFileContent();		// .net file

			String sBngInputStringOld = sBngInputString;
			String rawSeedSpeciesString = extractSeedSpecies(sBngOutputString);
			
			notifyCallbacks("    Iteration " + i + " ===========================\n" + rawSeedSpeciesString);

			String correctedSeedSpeciesString = processSeedSpecies(rawSeedSpeciesString);
			sBngInputString = prepareNewBnglString(sBngInputString, correctedSeedSpeciesString);
			sBngInput = new BNGInput(sBngInputString);		// the new input for next iteration
			
			if(sBngInputStringOld.equals(sBngInputString)) {
				break;			// early exit condition, 2 consecutive iteration yield the same result
			}
		}
		notifyCallbacks("Done " + nc.getMaxIteration() + " iterations.");
//		outputSpec = BNGOutputFileParser.createBngOutputSpec(sBngOutputString);
//		BNGOutputFileParser.printBNGNetOutput(outputSpec);			// prints all output to console
		return sBngOutput;
	}

	private void notifyCallbacks(String message) {
		for (BioNetGenUpdaterCallback callback : getCallbacks()){
			TaskCallbackMessage newCallbackMessage = new TaskCallbackMessage(TaskCallbackStatus.Notification, message);
			callback.setNewCallbackMessage(newCallbackMessage);
		}
	}

	@Override
	public void stopBNG() throws Exception {
		if (onepassBngService!=null){
			onepassBngService.stopBNG();
		}
		this.bStopped = true;
	}

	@Override
	public boolean isStopped() {
		return bStopped;
	}

	@Override
	public long getStartTime() {
		return startTime;
	}
	
	// ----------------------------------------------------------------------------------------------------------
	// generate_network({max_iter=>5,max_agg=>10,overwrite=>1})
	//
	private static NetworkConstraints extractNetworkConstraints(String cBngInputString) {
		NetworkConstraints nc = new NetworkConstraints();
		
		String s1 = cBngInputString.substring(cBngInputString.indexOf("max_iter=>") + "max_iter=>".length());
		s1 = s1.substring(0, s1.indexOf(","));
		int maxi = Integer.parseInt(s1);
		nc.setMaxIteration(maxi);
		
		String s2 = cBngInputString.substring(cBngInputString.indexOf("max_agg=>") + "max_agg=>".length());
		s2 = s2.substring(0, s2.indexOf(","));
		int maxa = Integer.parseInt(s2);
		nc.setMaxMoleculesPerSpecies(maxa);

		return nc;
	}
	// parse the compartmental bngl file and produce the "trick"
	// where each molecule has an extra Site with the compartments as possible States
	// a reserved name will be used for this Site
	//
	private static String preprocessInput(String cBngInputString) {
				
		// get rid of the default generate network command...
		String prolog = cBngInputString.substring(0, cBngInputString.indexOf("max_iter=>") + "max_iter=>".length());
		String epilog = cBngInputString.substring(cBngInputString.indexOf("max_iter=>") + "max_iter=>".length());
		epilog = epilog.substring(epilog.indexOf(","));
		
		String sInputString = prolog + "1" + epilog;
		return sInputString;
	}
	
	// output - the resulting .net file we received from bngl.exe
	//  we extract the seed species from the .net file,insert them in the .bngl file
	//  in preparation for the next round
	//
	private static String extractSeedSpecies(String output) {
		// extract the species from the output
		boolean inSpeciesBlock = false;
		StringBuilder rawSeedSpecies = new StringBuilder();
		StringTokenizer st = new StringTokenizer(output, "\n");
		while (st.hasMoreElements()) {
			String nextToken = st.nextToken().trim();

			if(nextToken.equals("begin species")) {
				inSpeciesBlock = true;
				continue;
			} else if(nextToken.equals("end species")) {
				inSpeciesBlock = false;
				break;		// nothing more to do, we got all our species
			}
			if(inSpeciesBlock == false) {
				continue;
			}
			// inside species block;
			rawSeedSpecies.append(nextToken);
			rawSeedSpecies.append("\n");
		}
		String rawSeedSpeciesString = rawSeedSpecies.toString();
		System.out.println(rawSeedSpeciesString);
		return rawSeedSpeciesString;
	}
	
	// process the seed species extracted from the .net file (see if they belong to the right compartment
	// and adjust them accordingly if they don't)
	//
	private static String processSeedSpecies(String rawSeedSpeciesString) {
//		TaskCallbackMessage message = new TaskCallbackMessage(TaskCallbackStatus.Notification, speciesStr);
//		sc.appendToConsole(message);
		String correctedSeedSpeciesString = new String(rawSeedSpeciesString);
		return correctedSeedSpeciesString;
	}
	
	// sBngInputStringOld - .the bngl file we used for the current iteration (which we just finished)
	// insert the new set of seed species in a new string, otherwise identical
	// inside the species block in preparation for the next iteration
	//
	private static String prepareNewBnglString(String sBngInputStringOld, String seedSpecies) {
		String prologue = sBngInputStringOld.substring(0, sBngInputStringOld.indexOf("begin seed species"));
		String epilogue = sBngInputStringOld.substring(sBngInputStringOld.indexOf("end seed species"));
		
		String sBngInputStringNew = prologue;
		sBngInputStringNew += "begin seed species\n";
		sBngInputStringNew += seedSpecies;
		sBngInputStringNew += epilogue;
		return sBngInputStringNew;
	}
}

//ASTModel astModel;
//try {
//	astModel = RbmUtils.importBnglFile(new StringReader(cBngInputString));
//} catch (ParseException e) {
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//	return "";
//}
//
//System.out.println(astModel.toBNGL());

