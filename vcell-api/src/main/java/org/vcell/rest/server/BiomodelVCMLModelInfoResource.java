package org.vcell.rest.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.wadl.MethodInfo;
import org.restlet.ext.wadl.ParameterInfo;
import org.restlet.ext.wadl.ParameterStyle;
import org.restlet.ext.wadl.RepresentationInfo;
import org.restlet.ext.wadl.RequestInfo;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.vcell.rest.VCellApiApplication;
import org.vcell.util.BigString;
import org.vcell.util.ObjectNotFoundException;
import org.vcell.util.PermissionException;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.User;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.dictionary.DBNonFormalUnboundSpecies;
import cbit.vcell.model.Catalyst;
import cbit.vcell.model.FluxReaction;
import cbit.vcell.model.Membrane;
import cbit.vcell.model.Product;
import cbit.vcell.model.Reactant;
import cbit.vcell.model.ReactionDescription;
import cbit.vcell.model.ReactionDescription.ReactionType;
import cbit.vcell.model.ReactionParticipant;
import cbit.vcell.model.ReactionStep;
import cbit.vcell.model.SpeciesContext;
import cbit.vcell.model.Structure;
import cbit.vcell.xml.XMLSource;
import cbit.vcell.xml.XmlHelper;

public class BiomodelVCMLModelInfoResource extends AbstractServerResource/* implements BiomodelVCMLResource*/ {

	private String biomodelid;
	
	
    @Override
    protected RepresentationInfo describe(MethodInfo methodInfo,
            Class<?> representationClass, Variant variant) {
        RepresentationInfo result = new RepresentationInfo(variant);
        result.setReference("biomodel");
        return result;
    }

    /**
     * Retrieve the account identifier based on the URI path variable
     * "accountId" declared in the URI template attached to the application
     * router.
     */
    @Override
    protected void doInit() throws ResourceException {
        String simTaskIdAttribute = getAttribute(VCellApiApplication.BIOMODELID);

        if (simTaskIdAttribute != null) {
            this.biomodelid = simTaskIdAttribute;
            setName("Resource for biomodel \"" + this.biomodelid + "\"");
            setDescription("The resource describing the simulation task id \"" + this.biomodelid + "\"");
        } else {
            setName("simulation task resource");
            setDescription("The resource describing a simulation task");
        }
    }
	

	@Override
	protected void describeGet(MethodInfo info) {
		super.describeGet(info);
		RequestInfo requestInfo = new RequestInfo();
        List<ParameterInfo> parameterInfos = new ArrayList<ParameterInfo>();
        parameterInfos.add(new ParameterInfo("biomodelid",false,"string",ParameterStyle.TEMPLATE,"VCell biomodel id"));
 		requestInfo.setParameters(parameterInfos);
		info.setRequest(requestInfo);
	}
	
	private enum ITEM_NAMES {structure,species,reactions};
	@Get("text/html")
	public StringRepresentation get_html() {
		try {
//			VCellApiApplication application = ((VCellApiApplication)getApplication());
//			User vcellUser = application.getVCellUser(getChallengeResponse(),AuthenticationPolicy.ignoreInvalidCredentials);
			
			User modelBrickUser = new User("ModelBrick", new KeyValue("101222366"));//Create User so we don't need authentication for ModelBricks
			BioModel bm = getBiomodel(new KeyValue(biomodelid), modelBrickUser);
			final SpeciesContext[] speciesContexts = bm.getModel().getSpeciesContexts();
			final ReactionStep[] reactionSteps = bm.getModel().getReactionSteps();
			TreeMap<String,ArrayList<Object>[]> structName_Species_Reactions_TS = new TreeMap();// This contains a structure name mapped to Species and ReactionSteps
			//Store SpeciesContexts belonging to each Structure
			for (int i = 0; i < speciesContexts.length; i++) {
				final String name = speciesContexts[i].getStructure().getName();
				ArrayList<Object>[] items = structName_Species_Reactions_TS.get(name);
				if( items == null) {
					items = new ArrayList[] {new ArrayList<Structure>(), new ArrayList<SpeciesContext>(),new ArrayList<ReactionStep>()};
					items[ITEM_NAMES.structure.ordinal()].add(speciesContexts[i].getStructure());
					structName_Species_Reactions_TS.put(name, items);
				}
				items[ITEM_NAMES.species.ordinal()].add(speciesContexts[i]);
			}
			//Store ReactionStep belonging to each structure
			for (int i = 0; i < reactionSteps.length; i++) {
				final String name = reactionSteps[i].getStructure().getName();
				ArrayList<Object>[] items = structName_Species_Reactions_TS.get(name);
				if( items == null) {
					items = new ArrayList[] {new ArrayList<Structure>(), new ArrayList<SpeciesContext>(),new ArrayList<ReactionStep>()};
					items[ITEM_NAMES.structure.ordinal()].add(reactionSteps[i].getStructure());
					structName_Species_Reactions_TS.put(name, items);
				}
				items[ITEM_NAMES.reactions.ordinal()].add(reactionSteps[i]);
			}
			//Create html
			StringBuffer sb = new StringBuffer();
			sb.append("<strong>Compartments, Species, Reactions</strong><br/>");
			//iterate through all the structures and get the stored SpeciesContexts and ReactionSteps
			final Iterator<String> iterator = structName_Species_Reactions_TS.keySet().iterator();
			while(iterator.hasNext()) {
				final String structName = iterator.next();//Get the structure name
				final ArrayList<Object>[] items = structName_Species_Reactions_TS.get(structName);//Get the stored items for the structure
				sb.append("<ul><li> Compartment <strong>\""+structName+"\"</strong>.  &nbsp; (type: "+((Structure)items[ITEM_NAMES.structure.ordinal()].get(0)).getTypeName()+")<ul>");
				//Add html for SpeciesContexts
				for (int i = 0; i < items[ITEM_NAMES.species.ordinal()].size(); i++) {
					sb.append("<li>Species <strong>\""+StringEscapeUtils.escapeHtml(((SpeciesContext)items[ITEM_NAMES.species.ordinal()].get(i)).getName())+"\"</strong></li>");
				}
				//Add html for ReactionSteps
				for (int i = 0; i < items[ITEM_NAMES.reactions.ordinal()].size(); i++) {
					final ReactionStep rxStep = (ReactionStep)items[ITEM_NAMES.reactions.ordinal()].get(i);
					ReactionDescription reactionDescription = createReactionDescription(rxStep, bm.getVersion().getVersionKey(), ((Structure)items[ITEM_NAMES.structure.ordinal()].get(0)).getKey());
					sb.append("<li>Reaction <strong>\""+rxStep.getName()+"\"</strong> &nbsp; "+rxStep.getClass().getSimpleName()+" &nbsp; ("+StringEscapeUtils.escapeHtml(reactionDescription.toString())+")</li>");
				}
				sb.append("</ul></li></ul>");
			}
			return new StringRepresentation(sb.toString(), MediaType.TEXT_HTML);
		} catch (Exception e) {
			e.printStackTrace();
			if(e instanceof ResourceException) {
				throw e;
			}
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}

	
	private BioModel getBiomodel(KeyValue bmKey,User vcellUser) {
		RestDatabaseService restDatabaseService = ((VCellApiApplication)getApplication()).getRestDatabaseService();
		try {
//			VCellApiApplication application = ((VCellApiApplication)getApplication());
			BigString cachedVcml = restDatabaseService.getBioModelXML(bmKey, vcellUser);
			
			BioModel bioModel = XmlHelper.XMLToBioModel(new XMLSource(cachedVcml.toString()));
			return bioModel;
		} catch (PermissionException e) {
			e.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_UNAUTHORIZED, "permission denied to requested resource");
		} catch (ObjectNotFoundException e) {
			e.printStackTrace();
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND, "biomodel not found");
		} catch (Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage());
		}
	}
	
	//Copied from DBReactionWizrdPanel and altered to display speciescontext names in ReactionStep text representation
	public static ReactionDescription createReactionDescription(ReactionStep rxStep,KeyValue bmid, KeyValue structRef) {
		ReactionType rxType = null;
		if(rxStep instanceof FluxReaction){
			if (rxStep.isReversible()){
				rxType = ReactionType.REACTTYPE_FLUX_REVERSIBLE;
			}else{
				rxType = ReactionType.REACTTYPE_FLUX_IRREVERSIBLE;
			}
		}else{
			if (rxStep.isReversible()){
				rxType = ReactionType.REACTTYPE_SIMPLE_REVERSIBLE;
			}else{
				rxType = ReactionType.REACTTYPE_SIMPLE_IRREVERSIBLE;
			}
		}
		ReactionDescription dbfr = new ReactionDescription(rxStep.getName(),rxType,rxStep.getKey(),bmid,structRef);
		//
		ReactionParticipant[] rpArr = rxStep.getReactionParticipants();
		for(int i=0;i < rpArr.length;i+= 1){
			DBNonFormalUnboundSpecies dbnfu = new DBNonFormalUnboundSpecies(rpArr[i].getSpeciesContext().getName());
			char role;
			if(rpArr[i] instanceof Reactant){
				role = ReactionDescription.RX_ELEMENT_REACTANT;
			}else if(rpArr[i] instanceof Product){
				role = ReactionDescription.RX_ELEMENT_PRODUCT;
			}else if(rpArr[i] instanceof Catalyst){
				role = ReactionDescription.RX_ELEMENT_CATALYST;
			}else{
				throw new RuntimeException("Unsupported ReationParticiapnt="+rpArr[i].getClass().getName());
			}
			dbfr.addReactionElement(dbnfu,rpArr[i].getSpeciesContext().getName(),rpArr[i].getSpeciesContext().getStructure().getName(),rpArr[i].getStoichiometry(),role);
		}
		if(dbfr.isFluxReaction()){//make sure flux is in right direction
			Structure outsideStruct = rxStep.getModel().getStructureTopology().getOutsideFeature((Membrane)rxStep.getStructure());
			String defaultOutsideSCName = dbfr.getOrigSpeciesContextName(dbfr.getFluxIndexOutside());
			for(int i=0;i < rpArr.length;i+= 1){
				if(rpArr[i].getSpeciesContext().getName().equals(defaultOutsideSCName)){
					if(!rpArr[i].getStructure().equals(outsideStruct)){
						dbfr.swapFluxSCNames();
					}
					break;
				}
			}
		}
		return dbfr;
	}

}
