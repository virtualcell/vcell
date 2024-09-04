package org.vcell.model.ssld;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.geometry.AnalyticSubVolume;
import cbit.vcell.geometry.Geometry;
import cbit.vcell.geometry.GeometrySpec;
import cbit.vcell.mapping.*;
import cbit.vcell.model.*;
import cbit.vcell.parser.Expression;
import cbit.vcell.solver.LangevinSimulationOptions;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SolverTaskDescription;
import org.vcell.model.rbm.*;
import org.vcell.solver.langevin.LangevinLngvWriter;
import org.vcell.util.Coordinate;
import org.vcell.util.Extent;
import org.vcell.util.springsalad.Colors;
import org.vcell.util.springsalad.NamedColor;

import java.awt.*;
import java.util.*;
import java.io.BufferedReader;
import java.io.*;
import java.util.List;
import java.util.prefs.Preferences;

import static org.vcell.model.ssld.TransitionReaction.ANY_STATE;
import static org.vcell.model.ssld.TransitionReaction.ANY_STATE_STRING;
import static org.vcell.solver.langevin.LangevinLngvWriter.*;

public class SsldUtils {

    private class Mapping {

        final SsldModel ssldModel;
        final BioModel bioModel;
        SimulationContext simContext;   // set late (after we construct the physiology)

        Map<Site, SiteType> siteToTypeMap = new LinkedHashMap<> ();
        Map<SiteType, Site> typeToSiteMap = new LinkedHashMap<> ();

        Map<Molecule, MolecularType> moleculeToMolecularTypeMap = new LinkedHashMap<>();
        Map<MolecularType, Molecule> molecularTypeToMoleculeMap = new LinkedHashMap<>();
        Map<SiteType, MolecularComponent> typeToComponentMap = new LinkedHashMap<>();
        Map<MolecularComponent, SiteType> componentToTypeMap = new LinkedHashMap<>();
        // ssld initial condition <-> vcell species contexts
        Map<Molecule, SpeciesContext> moleculeToSpeciesContextMap = new LinkedHashMap<>();
        Map<SpeciesContext, Molecule> speciesContextToMoleculeMap = new LinkedHashMap<>();

        Map<Reaction, ReactionRule> reactionToReactionRuleMap = new LinkedHashMap<>();
        Map<ReactionRule, Reaction> reactionRuleToReactionMap = new LinkedHashMap<>();
        Map<ReactionRule, ReactionRuleSpec.Subtype> reactionRuleToSubtype = new LinkedHashMap<>();

        // since we don't accept duplicates, all these maps need cleaning after each reaction
        // because the same molecule may be part of many reactions
        Map<Molecule, ReactantPattern> reactantToReactantPatternOne = new LinkedHashMap<>();   // these are unidirectional
        Map<Molecule, ProductPattern> reactantToProductPatternOne = new LinkedHashMap<>();
        // for binding, the molecule may be the same for both reactants, and we'd have duplicated entries
        Map<Molecule, ReactantPattern> reactantToReactantPatternTwo = new LinkedHashMap<>();   // these are unidirectional
        Map<Molecule, ProductPattern> reactantToProductPatternTwo = new LinkedHashMap<>();
        Map<Molecule, ReactantPattern> conditionToReactantPattern = new LinkedHashMap<>();
        Map<Molecule, ProductPattern> conditionToProductPattern = new LinkedHashMap<>();


        public Mapping(SsldModel ssldModel, BioModel bioModel) {
            this.ssldModel = ssldModel;
            this.bioModel = bioModel;
            this.simContext = null;
        }

        void cleanReactionsMaps() {
            reactantToReactantPatternOne.clear();
            reactantToProductPatternOne.clear();
            reactantToReactantPatternTwo.clear();
            reactantToProductPatternTwo.clear();
            conditionToReactantPattern.clear();
            conditionToProductPattern.clear();
        }

        SsldModel getSsldModel() {
            return ssldModel;
        }
        BioModel getBioModel() {
            return bioModel;
        }
        void set(SimulationContext simContext) {
            if(this.simContext != null) {
                throw new RuntimeException("SsldImporting: the SpringSaLaD application may be set only once");
            }
            this.simContext = simContext;
        }
        SimulationContext getSimulationContext() {
            return simContext;
        }

        void put(Site site, SiteType type) {
            if(siteToTypeMap.containsKey(site)) {
                throw new RuntimeException("siteToTypeMap: Duplicate Key");
            }
            if(typeToSiteMap.containsKey(type)) {
                throw new RuntimeException("typeToSiteMap: Duplicate Key");
            }
            siteToTypeMap.put(site, type);
            typeToSiteMap.put(type, site);
        }
        Site getSite(SiteType type) {
            return typeToSiteMap.get(type);
        }
        SiteType getType(Site site) {
            return siteToTypeMap.get(site);
        }

        void put(Molecule ssldMolecule, MolecularType mt) {
            if(moleculeToMolecularTypeMap.containsKey(ssldMolecule)) {
                throw new RuntimeException("moleculeToMolecularTypeMap: Duplicate Key");
            }
            if(molecularTypeToMoleculeMap.containsKey(mt)) {
                throw new RuntimeException("molecularTypeToMoleculeMap: Duplicate Key");
            }
            moleculeToMolecularTypeMap.put(ssldMolecule, mt);
            molecularTypeToMoleculeMap.put(mt, ssldMolecule);
        }
        Molecule get(MolecularType mt) {
            return molecularTypeToMoleculeMap.get(mt);
        }
        MolecularType get(Molecule ssldMolecule) {
            return moleculeToMolecularTypeMap.get(ssldMolecule);
        }

        void put(SiteType st, MolecularComponent mc) {
            if(typeToComponentMap.containsKey(st)) {
                throw new RuntimeException("typeToComponentMap: Duplicate Key");
            }
            if(componentToTypeMap.containsKey(mc)) {
                throw new RuntimeException("componentToTypeMap: Duplicate Key");
            }
            typeToComponentMap.put(st, mc);
            componentToTypeMap.put(mc, st);
        }
        SiteType get(MolecularComponent mc) {
            return componentToTypeMap.get(mc);
        }
        MolecularComponent get(SiteType st) {
            return typeToComponentMap.get(st);
        }

        void put(Molecule ssldMolecule, SpeciesContext sc) {
            if(moleculeToSpeciesContextMap.containsKey(ssldMolecule)) {
                throw new RuntimeException("moleculeToSpeciesContextMap: Duplicate Key");
            }
            if(speciesContextToMoleculeMap.containsKey(ssldMolecule)) {
                throw new RuntimeException("speciesContextToMoleculeMap: Duplicate Key");
            }
            moleculeToSpeciesContextMap.put(ssldMolecule, sc);
            speciesContextToMoleculeMap.put(sc, ssldMolecule);
        }
        SpeciesContext getSpeciesContext(Molecule ssldMolecule) {
            return moleculeToSpeciesContextMap.get(ssldMolecule);
        }
        Molecule getMolecule(SpeciesContext sp) {
            return speciesContextToMoleculeMap.get(sp);
        }

        void put(Reaction r, ReactionRule rr) {
            if(reactionToReactionRuleMap.containsKey(r)) {
                throw new RuntimeException("reactionToReactionRuleMap: Duplicate Key");
            }
            if(reactionRuleToReactionMap.containsKey(rr)) {
                throw new RuntimeException("reactionRuleToReactionMap: Duplicate Key");
            }
            reactionToReactionRuleMap.put(r, rr);
            reactionRuleToReactionMap.put(rr, r);
        }
        Reaction get(ReactionRule rr) {
            return reactionRuleToReactionMap.get(rr);
        }
        ReactionRule get(Reaction r) {
            return reactionToReactionRuleMap.get(r);
        }

        void put(ReactionRule rr, ReactionRuleSpec.Subtype st) {
            if(reactionRuleToSubtype.containsKey(rr)) {
                throw new RuntimeException("reactionRuleToSubtype: Duplicate Key");
            }
            reactionRuleToSubtype.put(rr, st);
        }
        ReactionRuleSpec.Subtype getSubtype(ReactionRule rr) {
            return reactionRuleToSubtype.get(rr);
        }


        // -------------------------------------------------------------------------------
        void putMoleculeToReactantPatternOne(Molecule ssldMolecule, ReactantPattern rp) {
            if(reactantToReactantPatternOne.containsKey(ssldMolecule)) {
                throw new RuntimeException("reactantToReactantPatternOne: Duplicate Key");
            }
            reactantToReactantPatternOne.put(ssldMolecule, rp);
        }
        ReactantPattern getReactantPatternOneFromMolecule(Molecule ssldMolecule) {
            return reactantToReactantPatternOne.get(ssldMolecule);
        }
        void putMoleculeToProductPatternOne(Molecule ssldMolecule, ProductPattern pp) {
            if(reactantToProductPatternOne.containsKey(ssldMolecule)) {
                throw new RuntimeException("reactantToProductPatternOne: Duplicate Key");
            }
            reactantToProductPatternOne.put(ssldMolecule, pp);
        }
        ProductPattern getProductPatternOneFromMolecule(Molecule ssldMolecule) {
            return reactantToProductPatternOne.get(ssldMolecule);
        }

        // -------------------------------------------------------------------------------
        void putMoleculeToReactantPatternTwo(Molecule ssldMolecule, ReactantPattern rp) {
            if(reactantToReactantPatternTwo.containsKey(ssldMolecule)) {
                throw new RuntimeException("reactantToReactantPatternTwo: Duplicate Key");
            }
            reactantToReactantPatternTwo.put(ssldMolecule, rp);
        }
        ReactantPattern getReactantPatternTwoFromMolecule(Molecule ssldMolecule) {
            return reactantToReactantPatternTwo.get(ssldMolecule);
        }
        void putMoleculeToProductPatternTwo(Molecule ssldMolecule, ProductPattern pp) {
            if(reactantToProductPatternTwo.containsKey(ssldMolecule)) {
                throw new RuntimeException("reactantToProductPatternTwo: Duplicate Key");
            }
            reactantToProductPatternTwo.put(ssldMolecule, pp);
        }
        ProductPattern getProductPatternTwoFromMolecule(Molecule ssldMolecule) {
            return reactantToProductPatternTwo.get(ssldMolecule);
        }

        // -----------------------------------------------------------------
        void putCondition(Molecule ssldMolecule, ReactantPattern rp) {
            if(conditionToReactantPattern.containsKey(ssldMolecule)) {
                throw new RuntimeException("conditionToReactantPattern: Duplicate Key");
            }
            conditionToReactantPattern.put(ssldMolecule, rp);
        }
        ReactantPattern getConditionReactantPattern(Molecule ssldMolecule) {
            return conditionToReactantPattern.get(ssldMolecule);
        }
        void putCondition(Molecule ssldMolecule, ProductPattern pp) {
            if(conditionToProductPattern.containsKey(ssldMolecule)) {
                throw new RuntimeException("conditionToProductPattern: Duplicate Key");
            }
            conditionToProductPattern.put(ssldMolecule, pp);
        }
        ProductPattern getConditionProductPattern(Molecule ssldMolecule) {
            return conditionToProductPattern.get(ssldMolecule);
        }

        // creation / decay reactions have special needs
        Map<Molecule, DecayReaction> ssldMoleculeToCreationReactions = new LinkedHashMap<>();   // creation only
        Map<Molecule, DecayReaction> ssldMoleculeToDecayReactions = new LinkedHashMap<>();      // destruction only
        Map<Molecule, MolecularType> moleculeToSourceMap = new LinkedHashMap<>();  // molecules being created need a source
        Map<Molecule, MolecularType> moleculeToSinkMap = new LinkedHashMap<>();     // molecules being destroyed need a sink
        Map<Structure, MolecularType> structureToSourceMap = new LinkedHashMap<>(); // structure where the Source species will be located
        Map<Structure, MolecularType> structureToSinkMap = new LinkedHashMap<>();   // structure where the Sink species will be located

        void putMoleculeCreated(Molecule ssldMolecule, DecayReaction ssldReaction) {
            if(ssldMoleculeToCreationReactions.containsKey(ssldMolecule)) {
                throw new RuntimeException("ssldMoleculeToCreationReactions: Duplicate key");
            }
            ssldMoleculeToCreationReactions.put(ssldMolecule, ssldReaction);
        }
        DecayReaction getCreationReactionOfMolecule(Molecule ssldMolecule) {
            return ssldMoleculeToCreationReactions.get(ssldMolecule);
        }
        void putMoleculeDecayed(Molecule ssldMolecule, DecayReaction ssldReaction) {
            if(ssldMoleculeToDecayReactions.containsKey(ssldMolecule)) {
                throw new RuntimeException("ssldMoleculeToDecayReactions: Duplicate key");
            }
            ssldMoleculeToDecayReactions.put(ssldMolecule, ssldReaction);
        }
        DecayReaction getDecayReactionOfMolecule(Molecule ssldMolecule) {
            return ssldMoleculeToDecayReactions.get(ssldMolecule);
        }
        Map<Molecule, DecayReaction> getMoleculeToCreationReactionsMap() {
            return (ssldMoleculeToCreationReactions);
        }
        Map<Molecule, DecayReaction> getMoleculeToDecayReactionsMap() {
            return (ssldMoleculeToDecayReactions);
        }
        // --------------------------------------------------------------------------
        void putMoleculeToSource(Molecule ssldMolecule, MolecularType source) {     // the Source used to create this ssld molecule
            if(moleculeToSourceMap.containsKey(ssldMolecule)) {
                throw new RuntimeException("moleculeToSourceMap: Duplicate key");
            }
            moleculeToSourceMap.put(ssldMolecule, source);
        }
        void putMoleculeToSink(Molecule ssldMolecule, MolecularType sink) {     // the Sink into which this ssld molecule decays
            if(moleculeToSinkMap.containsKey(ssldMolecule)) {
                throw new RuntimeException("moleculeToSinkMap: Duplicate key");
            }
            moleculeToSinkMap.put(ssldMolecule, sink);
        }
        MolecularType getSource(Molecule ssldMolecule) {    // the vcell Source molecule used to create this ssld molecule
            return moleculeToSourceMap.get(ssldMolecule);
        }
        MolecularType getSink(Molecule ssldMolecule) {    // the vcell Sink molecule used to create this ssld molecule
            return moleculeToSinkMap.get(ssldMolecule);
        }
        // ----------------------------------------------------------------------------
        void putStructureOfSource(Structure structure, MolecularType source) {  // this structure will have a Source species context for this molecule
            if(structureToSourceMap.containsKey(structure)) {
                MolecularType thatSource = structureToSourceMap.get(structure);
                if(thatSource != source) {
                    throw new RuntimeException("structureToSourceMap: Different value already present for this key");
                }
                return;
            }
            structureToSourceMap.put(structure, source);
        }
        void putStructureOfSink(Structure structure, MolecularType sink) {  // this structure will have a Sink species context for this molecule
            if(structureToSinkMap.containsKey(structure)) {
                MolecularType thatSink = structureToSinkMap.get(structure);
                if(thatSink == sink) {
                    throw new RuntimeException("structureToSinkMap: Different value already present for this key");
                }
                return;     // already there
            }
            structureToSinkMap.put(structure, sink);
        }
        MolecularType getSource(Structure structure) {
            return structureToSourceMap.get(structure);
        }
        MolecularType getSink(Structure structure) {
            return structureToSinkMap.get(structure);
        }
    }

    public BioModel fromSsld(SsldModel ssldModel) throws Exception {

        Mapping m = importPhysiologyFromSsld(ssldModel);
        BioModel bioModel = m.getBioModel();
//        String newApplicationName = bioModel.getFreeSimulationContextName();
        SimulationContext springSaLaDSimContext = bioModel.addNewSimulationContext("SpringSaLaD",
                SimulationContext.Application.SPRINGSALAD);     // make new default SpringSaLaD application
        // we always import count, so we start with default count
        springSaLaDSimContext.setUsingConcentration(false, true);
        m.set(springSaLaDSimContext);

        importApplicationFromSsld(m);
        importGeometryFromSsld(m);
        importSimulationFromSsld(m);

        return bioModel;
    }

    public void importApplicationFromSsld(Mapping m) throws Exception {
        final BioModel bioModel = m.getBioModel();
        final SsldModel ssldModel = m.getSsldModel();
        SimulationContext simContext = m.getSimulationContext();    // default ssld application

        ReactionRuleSpec[] rrSpecs = simContext.getReactionContext().getReactionRuleSpecs();
        SpeciesContextSpec[] scSpecs = simContext.getReactionContext().getSpeciesContextSpecs();

        for(SpeciesContextSpec scs : scSpecs) {
            importSpeciesContextSpecForSsld(scs, m);
        }
        for(ReactionRuleSpec rrs : rrSpecs) {
            importReactionRuleSpecForSsld(rrs, m);
        }
    }

    private void importSimulationFromSsld(Mapping m) throws Exception {

        final SsldModel ssldModel = m.getSsldModel();
        BoxGeometry ssldBoxGeometry = ssldModel.boxGeometry;
        /* --- SystemTimes
            private double totalTime;
            private double dt;
            private double dtspring;
            private double dtdata;
            private double dtimage;
         */
        SystemTimes ssldSystemTimes = ssldModel.systemTimes;

        final BioModel bioModel = m.getBioModel();
        SimulationContext simContext = m.getSimulationContext();

        SimulationContext.MathMappingCallback callback = new MathMappingCallbackTaskAdapter(null);
        SimulationContext.NetworkGenerationRequirements networkGenerationRequirements = null; // network generation should not be executed
        simContext.refreshMathDescription(callback,networkGenerationRequirements);
        Simulation sim = simContext.addNewSimulation(SimulationOwner.DEFAULT_SIM_NAME_PREFIX, callback, networkGenerationRequirements);

        SolverTaskDescription lstd = sim.getSolverTaskDescription();
        LangevinSimulationOptions lso = lstd.getLangevinSimulationOptions();
        lso.setNPart(ssldBoxGeometry.getNpart());

        // TODO: system times

    }

    private void importGeometryFromSsld(Mapping m) throws Exception {
        /* --- example:
        L_x: 0.1    // micrometers
        L_y: 0.1
        L_z_out: 0.010000000000000009
        L_z_in: 0.09
        Partition Nx: 10    // partitions imported in importSimulationFromSsld(), see above
        Partition Ny: 10
        Partition Nz: 10
         */
        final SsldModel ssldModel = m.getSsldModel();
        BoxGeometry ssldBoxGeometry = ssldModel.boxGeometry;    // double x, y, zin, zout,  int[3] npart
        double x = ssldBoxGeometry.getX();
        double y = ssldBoxGeometry.getY();
        double zin = ssldBoxGeometry.getZin();
        double zout = ssldBoxGeometry.getZout();

        SimulationContext simContext = m.getSimulationContext();
        Geometry geometry = simContext.getGeometry();
        GeometrySpec geometrySpec = geometry.getGeometrySpec();

        // see LangevinLngvWriter #938 for intracellularSubVolume producer (exporter)
        AnalyticSubVolume intracellularSubVolume = (AnalyticSubVolume) geometrySpec.getSubVolumes(0);
        Expression expression = new Expression("z < " + (zin/1000));
        intracellularSubVolume.setExpression(expression);

        // we need micrometers for the extent but all distances in BoxGeometry are in nm, hence the 1000 scaling down
        // note that the alternate zin/1000 + zout/1000 will result in a systemic rounding error
        Extent extent = new Extent(x/1000, y/1000, (zin + zout)/1000);

        // the following call will fire events which will trigger a race condition that will invalidate the
        // GeometrySurfaceDescription.GeometricRegion[] in another thread
        // this, in turn, will trigger an Issue error evaluation within the MathDescription, where the call to
        // GeometricRegion regions[] = geometry.getGeometrySurfaceDescription().getGeometricRegions();
        // will result in a null pointer (MathDescription.gatherIssues(), line #2404)
        // context: the SsldUtils.importFromSsld() is being invoked from ClientRequestManager.updateAferChecking()
        // AsynchClientTask.task1() line #3343
        geometrySpec.setExtent(extent); // this will invalidate the GeometricRegion[] array

        // this is the fix!!!
        // always call updateAll() and recalculateDependencies() when you mess with any class that implements these methods
        geometry.getGeometrySurfaceDescription().updateAll();
    }


    private void importReactionRuleSpecForSsld(ReactionRuleSpec rrs, Mapping m) throws Exception {
        ReactionRule rr = rrs.getReactionRule();
        Reaction ssldReaction = m.get(rr);
        if(!(ssldReaction instanceof BindingReaction)) {
            return;     // only the BindingReaction has extra ssld-specific parameters
        }
        BindingReaction ssldBindingReaction = (BindingReaction)ssldReaction;
        double bondLength = ssldBindingReaction.getBondLength();
        rrs.setFieldBondLength(bondLength);
    }

    private void importSpeciesContextSpecForSsld(SpeciesContextSpec scs, Mapping m) throws Exception {

        Map<MolecularComponentPattern, SiteAttributesSpec> siteAttributesMap = new LinkedHashMap<>();
        Set<MolecularInternalLinkSpec> internalLinkSet = new LinkedHashSet<>();

        SpeciesPattern sp = scs.getSpeciesContext().getSpeciesPattern();
        MolecularTypePattern mtp = sp.getMolecularTypePatterns().get(0);
        MolecularType mt = mtp.getMolecularType();
        List<MolecularComponentPattern> mcpList = mtp.getComponentPatternList();

        Molecule ssldMolecule = m.get(mt);
        List<Site> ssldSiteArray = ssldMolecule.getSiteArray();
        List<SiteType> ssldTypeArray = ssldMolecule.getTypeArray();

        for(MolecularComponentPattern mcp : mcpList) {
            MolecularComponent mc = mcp.getMolecularComponent();
            SiteType ssldType = m.get(mc);
            Site ssldSite = m.getSite(ssldType);

            String ssldStructure = ssldSite.getLocation();  // structure of site, may differ from molecule structure
            Structure structure = m.getBioModel().getModel().getStructure(ssldStructure);
            String colorName = ssldType.getColorName();
            NamedColor namedColor = Colors.getColorByName(colorName);
            Coordinate coordinate = new Coordinate(ssldSite.x, ssldSite.y, ssldSite.z);

            SiteAttributesSpec sas = new SiteAttributesSpec(scs, mcp, structure);
            sas.setCoordinate(coordinate);
            sas.setColor(namedColor);
            sas.setRadius(ssldType.getRadius());
            sas.setDiffusionRate(ssldType.getD());

            siteAttributesMap.put(mcp, sas);
        }
        for(Link ssldLink : ssldMolecule.getLinkArray()) {
            // ssldLink -> ssldSite1 -> ssldType1 -> mc1 -> mcp1 -> mils
            //          -> ssldSite2 -> ssldType2 -> mc2 -> mcp2 ->
            Site ssldSite1 = ssldLink.getSite1();
            Site ssldSite2 = ssldLink.getSite2();
            SiteType ssldType1 = m.getType(ssldSite1);
            SiteType ssldType2 = m.getType(ssldSite2);
            MolecularComponent mc1 = m.get(ssldType1);
            MolecularComponent mc2 = m.get(ssldType2);
            MolecularComponentPattern mcp1 = mtp.getMolecularComponentPattern(mc1);
            MolecularComponentPattern mcp2 = mtp.getMolecularComponentPattern(mc2);

            MolecularInternalLinkSpec mils = new MolecularInternalLinkSpec(scs, mcp1, mcp2);
            internalLinkSet.add(mils);
        }
        scs.setSiteAttributesMap(siteAttributesMap);
        scs.setInternalLinkSet(internalLinkSet);

        SpeciesContextSpec.SpeciesContextSpecParameter countParam = scs.getInitialCountParameter();
        InitialCondition ssldIc = ssldMolecule.getInitialCondition();
        Expression countExp = new Expression(ssldIc.getNumber());
        countParam.setExpression(countExp);

        // TODO: something is wrong here, it shows 0 in vcell UI

    }

    public Mapping importPhysiologyFromSsld(SsldModel ssldModel) throws Exception {

        ModelUnitSystem mus = ModelUnitSystem.createDefaultVCModelUnitSystem();
        BioModel bioModel = new BioModel(null);
        bioModel.setName(ssldModel.getSystemName());
        Model model = new Model("model", mus);
        bioModel.setModel(model);

        Mapping m = new Mapping(ssldModel, bioModel);

        // structures
        if(bioModel.getModel().getStructures().length > 0) {
            Structure struct = bioModel.getModel().getStructure(0);
            if (struct != null) {
                bioModel.getModel().removeStructure(struct, true);
            }
        }
        LinkedList<Structure> newstructures = new LinkedList<>();
        Feature intra = new Feature(Structure.SpringStructureEnum.Intracellular.columnName);
        newstructures.add(intra);
        Membrane membr = new Membrane(Structure.SpringStructureEnum.Membrane.columnName);
        newstructures.add(membr);
        Feature extra = new Feature(Structure.SpringStructureEnum.Extracellular.columnName);
        newstructures.add(extra);
        Structure[] structarray = new Structure[newstructures.size()];
        newstructures.toArray(structarray);
        model.setStructures(structarray);

        // ---------- Molecular Types
        for(Molecule ssldMolecule : ssldModel.getMolecules()) {
            MolecularType mt = new MolecularType(ssldMolecule.getName(), model);
            for(Site ssldSite : ssldMolecule.getSiteArray()) {
                SiteType ssldType = ssldSite.getType();
                // in vcell we only accept a 1 to 1 relationship between type and site
                m.put(ssldSite, ssldType);

                MolecularComponent mc = new MolecularComponent(ssldType.getName());
                // site index starts at 1 in vcell, while it starts at 0 in ssld
                // see also LangevinLngvWriter.writeSpeciesInfo()
                // see also MolecularInternalLinkSpec.writeLink()
                mc.setIndex(ssldSite.getIndex()+1);
                for(State ssldState : ssldType.getStates()) {
                    ComponentStateDefinition csd = new ComponentStateDefinition(ssldState.getName());
                    mc.addComponentStateDefinition(csd);
                }
                m.put(ssldType, mc);
                mt.addMolecularComponent(mc);
            }
            // TODO: if membrane molecule, anchor it to membrane
            m.put(ssldMolecule, mt);
            model.getRbmModelContainer().addMolecularType(mt, false);
        }

        // if we have creation / decay reactions we must make the reserved molecules Sink and Source.
        for(Molecule ssldMolecule : ssldModel.getMolecules()) {
            DecayReaction ssldReaction = ssldMolecule.getDecayReaction();
            if(ssldReaction != null && ssldReaction.getCreationRate() != 0) {
                m.putMoleculeCreated(ssldMolecule, ssldReaction);
            }
            if(ssldReaction != null && ssldReaction.getDecayRate() != 0) {
                m.putMoleculeDecayed(ssldMolecule, ssldReaction);
            }
        }
        MolecularType mtSource = null;  // make the molecular type for Source and Sink if needed
        MolecularType mtSink = null;
        if(!m.getMoleculeToCreationReactionsMap().isEmpty()) {
            mtSource = new MolecularType(SpeciesContextSpec.SourceMoleculeString, model);
            model.getRbmModelContainer().addMolecularType(mtSource, false);
        }
        if(!m.getMoleculeToDecayReactionsMap().isEmpty()) {
            mtSink = new MolecularType(SpeciesContextSpec.SinkMoleculeString, model);
            model.getRbmModelContainer().addMolecularType(mtSink, false);
        }
        for(Molecule ssldMolecule : ssldModel.getMolecules()) {
            DecayReaction ssldReaction = ssldMolecule.getDecayReaction();
            if(ssldReaction != null && ssldReaction.getCreationRate() != 0) {
                m.putMoleculeToSource(ssldMolecule, mtSource);
                Structure struct = bioModel.getModel().getStructure(ssldMolecule.getLocation());
                m.putStructureOfSource(struct, mtSource);

            }
            if(ssldReaction != null && ssldReaction.getDecayRate() != 0) {
                m.putMoleculeToSink(ssldMolecule, mtSink);
                Structure struct = bioModel.getModel().getStructure(ssldMolecule.getLocation());
                m.putStructureOfSink(struct, mtSink);
            }
        }

        // ---------- Species Contexts
        // the Source and the Sink species contexts, if exist
        // actually... we don't need species contextx for Sink and Source
//        if(mtSource != null) {
//            for(Map.Entry<Structure, MolecularType> entry : m.structureToSourceMap.entrySet()) {
//                Structure struct = entry.getKey();
//                MolecularType mt = entry.getValue();
//                if(mtSource != mt) {
//                    throw new RuntimeException("The 'Source' molecular type is duplicated");
//                }
//                MolecularTypePattern mtp = new MolecularTypePattern(mt, false);     // we shouldn't have any components
//                SpeciesPattern sp = new SpeciesPattern();
//                sp.addMolecularTypePattern(mtp);
//                Species species = new Species(mt.getName(), mt.getName());
//                SpeciesContext speciesContext = new SpeciesContext(species, struct, sp);
//                model.addSpecies(speciesContext.getSpecies());
//                model.addSpeciesContext(speciesContext);
//            }
//        }
//        if(mtSink != null) {
//            for(Map.Entry<Structure, MolecularType> entry : m.structureToSinkMap.entrySet()) {
//                Structure struct = entry.getKey();
//                MolecularType mt = entry.getValue();
//                if(mtSink != mt) {
//                    throw new RuntimeException("The 'Sink' molecular type is duplicated");
//                }
//                MolecularTypePattern mtp = new MolecularTypePattern(mt, false);     // we shouldn't have any components
//                SpeciesPattern sp = new SpeciesPattern();
//                sp.addMolecularTypePattern(mtp);
//                Species species = new Species(mt.getName(), mt.getName());
//                SpeciesContext speciesContext = new SpeciesContext(species, struct, sp);
//                model.addSpecies(speciesContext.getSpecies());
//                model.addSpeciesContext(speciesContext);
//            }
//        }
        // make the concrete species (SpeciesContexts)
        for(Molecule ssldMolecule : ssldModel.getMolecules()) {
            MolecularType mt = m.get(ssldMolecule);
            Structure struct = bioModel.getModel().getStructure(ssldMolecule.getLocation());
            List<MolecularComponentPattern> mcpList = new ArrayList<> ();
            ArrayList<Site> ssldSiteList = ssldMolecule.getSiteArray();
            for(Site ssldSite : ssldSiteList) {
                SiteType ssldType = ssldSite.getType();
                MolecularComponent mc = m.get(ssldType);
                State ssldState = ssldSite.getInitialState();
                ComponentStateDefinition csd = mc.getComponentStateDefinition(ssldState.getName());
                ComponentStatePattern csp = new ComponentStatePattern(csd);
                MolecularComponentPattern mcp = new MolecularComponentPattern(mc);
                mcp.setComponentStatePattern(csp);
                mcp.setBondType(MolecularComponentPattern.BondType.None);
                mcpList.add(mcp);
            }
            MolecularTypePattern mtp = new MolecularTypePattern(mt, false);
            mtp.setComponentPatterns(mcpList);

            SpeciesPattern sp = new SpeciesPattern();
            sp.addMolecularTypePattern(mtp);
            Species species = new Species(mt.getName(), mt.getName());
            SpeciesContext speciesContext = new SpeciesContext(species, struct, sp);    // makes name from species and struct names
            speciesContext.setName(mt.getName());       // we want species context name identical with molecule name
            model.addSpecies(speciesContext.getSpecies());
            model.addSpeciesContext(speciesContext);
            m.put(ssldMolecule, speciesContext);
        }

        // ---------- Allosteric Reactions
        for(AllostericReaction ssldAllostericReaction : ssldModel.getAllostericReactions()) {
            m.cleanReactionsMaps();
            boolean reversible = false;
            // note that the transition and the condition apply to the same molecule
            Molecule ssldTransitionMolecule = ssldAllostericReaction.getMolecule();
//            Site ssldAllostericSite = ssldAllostericReaction.getAllostericSite();   // allosteric condition
//            State ssldAllostericState = ssldAllostericReaction.getAllostericState();
//            Site ssldTransitionSite = ssldAllostericReaction.getSite();             // transitioning site
//            State ssldInitialTransitionState = ssldAllostericReaction.getInitialState();
//            State ssldFinalTransitionState = ssldAllostericReaction.getFinalState();
            String location = ssldTransitionMolecule.getLocation(); // reaction happens in the same structure, no transport
            double ssldKf = ssldAllostericReaction.getRate();

            Structure structure = model.getStructure(location);
            Expression kf = new Expression(ssldKf);
            ReactionRule reactionRule = new ReactionRule(model, ssldAllostericReaction.getName(), structure, reversible);
            RbmKineticLaw.RateLawType rateLawType = RbmKineticLaw.RateLawType.MassAction;
            reactionRule.setKineticLaw(new RbmKineticLaw(reactionRule, rateLawType));
            reactionRule.getKineticLaw().setLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate, kf);

            SpeciesPattern spReactant = getSpeciesPattern(ssldTransitionMolecule, m);
            SpeciesPattern spProduct = getSpeciesPattern(ssldTransitionMolecule, m);
            ReactantPattern rp = new ReactantPattern(spReactant, structure);
            ProductPattern pp = new ProductPattern(spProduct, structure);

            m.putMoleculeToReactantPatternOne(ssldTransitionMolecule, rp);
            m.putMoleculeToProductPatternOne(ssldTransitionMolecule, pp);
            // at this point everything is trivial
            // now we start adjusting the binding sites
            adjustAllostericReactionSites(ssldAllostericReaction, m);

            reactionRule.addReactant(rp);
            reactionRule.addProduct(pp);
            bioModel.getModel().getRbmModelContainer().addReactionRule(reactionRule);
            m.put(ssldAllostericReaction, reactionRule);
        }

        // ---------- Transition Reactions
        for(TransitionReaction ssldReaction : ssldModel.getTransitionReactions()) {
            m.cleanReactionsMaps();
            boolean reversible = false;
            Molecule ssldTransitionMolecule = ssldReaction.getMolecule();
            Molecule ssldConditionalMolecule = ssldReaction.getConditionalMolecule();   //
            String location = ssldTransitionMolecule.getLocation(); // reaction happens in the same structure, no transport
            Structure structure = model.getStructure(location);

            double ssldKf = ssldReaction.getRate();
            Expression kf = new Expression(ssldKf);
            ReactionRule reactionRule = new ReactionRule(model, ssldReaction.getName(), structure, reversible);
            RbmKineticLaw.RateLawType rateLawType = RbmKineticLaw.RateLawType.MassAction;
            reactionRule.setKineticLaw(new RbmKineticLaw(reactionRule, rateLawType));
            reactionRule.getKineticLaw().setLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate, kf);

            SpeciesPattern spReactant = getSpeciesPattern(ssldConditionalMolecule, ssldTransitionMolecule, m);
            SpeciesPattern spProduct = getSpeciesPattern(ssldConditionalMolecule, ssldTransitionMolecule, m);
            ReactantPattern rp = new ReactantPattern(spReactant, structure);
            ProductPattern pp = new ProductPattern(spProduct, structure);

            if(ssldConditionalMolecule != null) {
                m.putCondition(ssldConditionalMolecule, rp);
                m.putCondition(ssldConditionalMolecule, pp);
            }
            m.putMoleculeToReactantPatternOne(ssldTransitionMolecule, rp);
            m.putMoleculeToProductPatternOne(ssldTransitionMolecule, pp);
            // at this point everything is trivial
            // now we start adjusting the binding sites
            adjustTransitionReactionSites(ssldReaction, m);

            reactionRule.addReactant(rp);
            reactionRule.addProduct(pp);
            bioModel.getModel().getRbmModelContainer().addReactionRule(reactionRule);
            m.put(ssldReaction, reactionRule);
        }
        // ---------- BindingReaction
        for(BindingReaction ssldReaction : ssldModel.getBindingReactions()) {
            m.cleanReactionsMaps();
//            boolean reversible = ssldReaction.getkoff() != 0 ? true : false;
            boolean reversible = true;      // in springsalad, binding reactions must be reversible
            Molecule ssldReactantOne = ssldReaction.getMolecule(0);
            Molecule ssldReactantTwo = ssldReaction.getMolecule(1);
            String locationOne = ssldReactantOne.getLocation();
            String locationTwo = ssldReactantTwo.getLocation();
            Structure structure;
            if(locationOne.contentEquals(SystemGeometry.MEMBRANE) || locationTwo.contentEquals(SystemGeometry.MEMBRANE)) {
                // reaction is on the Membrane if at least one reactant is on the Membrane, the product stay on the membrane
                // actually the membrane molecule must be anchored to the membrane
                structure = model.getStructure(SystemGeometry.MEMBRANE);
            } else {
                // both reactants must be in the same compartment, the product will also stay there
                structure = model.getStructure(locationOne);
            }

            double ssldKf = ssldReaction.getkon();
            Expression kf = new Expression(ssldKf);
            double ssldKr = ssldReaction.getkoff();
            Expression kr = new Expression(ssldKr);
            ReactionRule reactionRule = new ReactionRule(model, ssldReaction.getName(), structure, reversible);
            RbmKineticLaw.RateLawType rateLawType = RbmKineticLaw.RateLawType.MassAction;
            reactionRule.setKineticLaw(new RbmKineticLaw(reactionRule, rateLawType));
            reactionRule.getKineticLaw().setLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate, kf);
            reactionRule.getKineticLaw().setLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionReverseRate, kr);

            SpeciesPattern spReactantOne = getSpeciesPattern(ssldReactantOne, m);
            SpeciesPattern spReactantTwo = getSpeciesPattern(ssldReactantTwo, m);
            SpeciesPattern spProduct = getSpeciesPattern(ssldReactantOne, ssldReactantTwo, m);

            ReactantPattern rpOne = new ReactantPattern(spReactantOne, model.getStructure(locationOne));
            ReactantPattern rpTwo = new ReactantPattern(spReactantTwo, model.getStructure(locationTwo));
            ProductPattern pp = new ProductPattern(spProduct, structure);

            m.putMoleculeToReactantPatternOne(ssldReactantOne, rpOne);
            m.putMoleculeToReactantPatternTwo(ssldReactantTwo, rpTwo);
            m.putMoleculeToProductPatternOne(ssldReactantOne, pp);      // there is only one product pattern
            m.putMoleculeToProductPatternTwo(ssldReactantTwo, pp);
            // at this point everything is trivial
            // now we start adjusting the binding sites
            adjustReactantSite(ssldReaction, 0, m);
            adjustReactantSite(ssldReaction, 1, m);
            adjustProductSite(ssldReaction, m);

            reactionRule.addReactant(rpOne);
            reactionRule.addReactant(rpTwo);
            reactionRule.addProduct(pp);
            bioModel.getModel().getRbmModelContainer().addReactionRule(reactionRule);
            m.put(ssldReaction, reactionRule);
        }

        // ---------- Decay reactions
        // in vcell creation and destruction reactions are separate
        // ssls decay reactions are being stored in the molecule rather than the model
        if(!m.getMoleculeToCreationReactionsMap().isEmpty()) {
            for (Map.Entry<Molecule, DecayReaction> entry : m.ssldMoleculeToCreationReactions.entrySet()) {
                // mtSource -> ssldMolecule  (Creation Reaction)
                Molecule ssldMolecule = entry.getKey();
                DecayReaction ssldReaction = entry.getValue();  // the reaction name is the name of the molecule
                double ssldRate = ssldReaction.getCreationRate();
                Expression kf = new Expression(ssldRate);
                String suffix = "_Creation"; // we add a suffix to reaction names (because we may make 2 distinct reactions)

                String location = ssldMolecule.getLocation();
                Structure structure = model.getStructure(location);
                boolean reversible = false;
                ReactionRule reactionRule = new ReactionRule(model, ssldReaction.getName() + suffix, structure, reversible);
                RbmKineticLaw.RateLawType rateLawType = RbmKineticLaw.RateLawType.MassAction;
                reactionRule.setKineticLaw(new RbmKineticLaw(reactionRule, rateLawType));
                reactionRule.getKineticLaw().setLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate, kf);

                SpeciesPattern spReactant = new SpeciesPattern();
                MolecularTypePattern mtpReactant = new MolecularTypePattern(mtSource);
                spReactant.addMolecularTypePattern(mtpReactant);
                ReactantPattern rp = new ReactantPattern(spReactant, structure);
                reactionRule.addReactant(rp);

                SpeciesContext sc = m.getSpeciesContext(ssldMolecule);
                SpeciesPattern spSpeciesContext = sc.getSpeciesPattern();
                // sp must not be ambiguous! it must be a concrete species! all sites must be unbound and with specified states
                SpeciesPattern spProduct = new SpeciesPattern(model, spSpeciesContext);
                ProductPattern pp = new ProductPattern(spProduct, structure);
                reactionRule.addProduct(pp);
                bioModel.getModel().getRbmModelContainer().addReactionRule(reactionRule);
            }
        }
        if(!m.getMoleculeToDecayReactionsMap().isEmpty()) {
            for (Map.Entry<Molecule, DecayReaction> entry : m.getMoleculeToDecayReactionsMap().entrySet()) {
                // ssldMolecule -> mtSink (Decay reaction)
                Molecule ssldMolecule = entry.getKey();
                DecayReaction ssldReaction = entry.getValue();  // the reaction name is the name of the molecule
                double ssldRate = ssldReaction.getDecayRate();
                Expression kf = new Expression(ssldRate);
                String suffix = "_Decay";

                String location = ssldMolecule.getLocation();
                Structure structure = model.getStructure(location);
                boolean reversible = false;
                ReactionRule reactionRule = new ReactionRule(model, ssldReaction.getName() + suffix, structure, reversible);
                RbmKineticLaw.RateLawType rateLawType = RbmKineticLaw.RateLawType.MassAction;
                reactionRule.setKineticLaw(new RbmKineticLaw(reactionRule, rateLawType));
                reactionRule.getKineticLaw().setLocalParameterValue(RbmKineticLaw.RbmKineticLawParameterType.MassActionForwardRate, kf);

                SpeciesContext sc = m.getSpeciesContext(ssldMolecule);
                SpeciesPattern spSpeciesContext = sc.getSpeciesPattern();
                // sp must not be ambiguous! it must be a concrete species! all sites must be unbound and with specified states
                SpeciesPattern spReactant = new SpeciesPattern(model, spSpeciesContext);
                ReactantPattern rp = new ReactantPattern(spReactant, structure);
                reactionRule.addReactant(rp);

                SpeciesPattern spProduct = new SpeciesPattern();
                MolecularTypePattern mtpProduct = new MolecularTypePattern(mtSink);
                spProduct.addMolecularTypePattern(mtpProduct);
                ProductPattern pp = new ProductPattern(spProduct, structure);
                reactionRule.addProduct(pp);
                bioModel.getModel().getRbmModelContainer().addReactionRule(reactionRule);
            }
        }
        return m;
    }

    private void adjustAllostericReactionSites(AllostericReaction ssldReaction, Mapping m) {
        Molecule ssldTransitionMolecule = ssldReaction.getMolecule();
        Site ssldAllostericSite = ssldReaction.getAllostericSite();   // allosteric condition
        State ssldAllostericState = ssldReaction.getAllostericState();
        Site ssldTransitionSite = ssldReaction.getSite();             // transitioning site
        State ssldInitialTransitionState = ssldReaction.getInitialState();
        State ssldFinalTransitionState = ssldReaction.getFinalState();
        String location = ssldTransitionMolecule.getLocation(); // reaction happens in the same structure, no transport
        if(ssldAllostericSite == ssldTransitionSite) {
            throw new RuntimeException("Importing AllostericReaction: the transition and allosteric sites cannot be the same");
        }
        SiteType ssldAllostericSiteType = ssldAllostericSite.getType();
        SiteType ssldTransitionSiteType = ssldTransitionSite.getType();

        // all bonds are in the "Possible" BondType (represented as "?")
        SpeciesPattern spReactant = m.getReactantPatternOneFromMolecule(ssldTransitionMolecule).getSpeciesPattern();
        MolecularTypePattern mtpReactant = spReactant.getMolecularTypePatterns(ssldTransitionMolecule.getName()).get(0);
        MolecularComponentPattern mcpTransitionReactant = mtpReactant.getMolecularComponentPattern(ssldTransitionSiteType.getName());
        String ssldInitialStateName = ssldInitialTransitionState.getName();
        ComponentStateDefinition csdInitial = mcpTransitionReactant.getMolecularComponent().getComponentStateDefinition(ssldInitialStateName);
        ComponentStatePattern cspTransitionReactant = new ComponentStatePattern(csdInitial);
        mcpTransitionReactant.setComponentStatePattern(cspTransitionReactant);
        MolecularComponentPattern mcpAllostericReactant = mtpReactant.getMolecularComponentPattern(ssldAllostericSiteType.getName());
        String ssldAllostericStateName = ssldAllostericState.getName();     // must be explicit!
        ComponentStateDefinition csdAllosteric = mcpAllostericReactant.getMolecularComponent().getComponentStateDefinition(ssldAllostericStateName);
        ComponentStatePattern cspAllostericReactant = new ComponentStatePattern(csdAllosteric);
        mcpAllostericReactant.setComponentStatePattern(cspAllostericReactant);

        SpeciesPattern spProduct = m.getProductPatternOneFromMolecule(ssldTransitionMolecule).getSpeciesPattern();
        MolecularTypePattern mtpProduct = spProduct.getMolecularTypePatterns(ssldTransitionMolecule.getName()).get(0);
        MolecularComponentPattern mcpTransitionProduct = mtpProduct.getMolecularComponentPattern(ssldTransitionSiteType.getName());
        String ssldFinalStateName = ssldFinalTransitionState.getName();
        ComponentStateDefinition csdFinal = mcpTransitionProduct.getMolecularComponent().getComponentStateDefinition(ssldFinalStateName);
        ComponentStatePattern cspTransitionProduct = new ComponentStatePattern(csdFinal);
        mcpTransitionProduct.setComponentStatePattern(cspTransitionProduct);
        MolecularComponentPattern mcpAllostericProduct = mtpProduct.getMolecularComponentPattern(ssldAllostericSiteType.getName());
        // csdAllosteric is the same for the reactant and the product (is it? what id the user wants to edit one???)
        ComponentStatePattern cspAllostericProduct = new ComponentStatePattern(csdAllosteric);
        mcpAllostericProduct.setComponentStatePattern(cspAllostericProduct);
    }

    private void adjustTransitionReactionSites(TransitionReaction ssldReaction, Mapping m) {
        Molecule ssldConditionalMolecule = ssldReaction.getConditionalMolecule();
        Molecule ssldTransitionMolecule = ssldReaction.getMolecule();

        int transitionMoleculeIndexInSpeciesPattern;
        if(ssldConditionalMolecule != null) {
            if(!ssldReaction.getCondition().equals(TransitionReaction.BOUND_CONDITION)) {
                throw new RuntimeException("Inconsistent transition reaction");
            }
            if(ssldTransitionMolecule != ssldConditionalMolecule) {
                transitionMoleculeIndexInSpeciesPattern = 0;
            } else {
                transitionMoleculeIndexInSpeciesPattern = 1;
            }
            SiteType conditionalType = ssldReaction.getConditionalType();
            State ssldConditionalState = ssldReaction.getConditionalState();

            SpeciesPattern spReactant = m.getConditionReactantPattern(ssldConditionalMolecule).getSpeciesPattern();
            MolecularTypePattern mtpConditionReactant = spReactant.getMolecularTypePatterns(ssldConditionalMolecule.getName()).get(0);
            MolecularComponentPattern mcpConditionReactant = mtpConditionReactant.getMolecularComponentPattern(conditionalType.getName());
            if(ssldConditionalState != TransitionReaction.ANY_STATE) {
                String ssldConditionalStateName = ssldConditionalState.getName();
                ComponentStateDefinition csdConditionReactant = mcpConditionReactant.getMolecularComponent().getComponentStateDefinition(ssldConditionalStateName);
                ComponentStatePattern cspConditionalReactant = new ComponentStatePattern(csdConditionReactant);
                mcpConditionReactant.setComponentStatePattern(cspConditionalReactant);
            }
            mcpConditionReactant.setBondType(MolecularComponentPattern.BondType.Exists);
            mcpConditionReactant.setBond(null);

            SpeciesPattern spProduct = m.getConditionProductPattern(ssldConditionalMolecule).getSpeciesPattern();
            MolecularTypePattern mtpConditionProduct = spProduct.getMolecularTypePatterns(ssldConditionalMolecule.getName()).get(0);
            MolecularComponentPattern mcpConditionProduct = mtpConditionProduct.getMolecularComponentPattern(conditionalType.getName());
            if(ssldConditionalState != TransitionReaction.ANY_STATE) {
                String ssldConditionalStateName = ssldConditionalState.getName();
                ComponentStateDefinition csdConditionProduct = mcpConditionProduct.getMolecularComponent().getComponentStateDefinition(ssldConditionalStateName);
                ComponentStatePattern cspConditionalProduct = new ComponentStatePattern(csdConditionProduct);
                mcpConditionProduct.setComponentStatePattern(cspConditionalProduct);
            }
            mcpConditionProduct.setBondType(MolecularComponentPattern.BondType.Exists);
            mcpConditionProduct.setBond(null);
        } else {
            transitionMoleculeIndexInSpeciesPattern = 0;
        }
        SiteType ssldSiteType = ssldReaction.getType();
        State ssldInitialState = ssldReaction.getInitialState();
        State ssldFinalState = ssldReaction.getFinalState();

        SpeciesPattern spReactant = m.getReactantPatternOneFromMolecule(ssldTransitionMolecule).getSpeciesPattern();
        MolecularTypePattern mtpTransitionReactant = spReactant.getMolecularTypePatterns(ssldTransitionMolecule.getName()).get(transitionMoleculeIndexInSpeciesPattern);
        MolecularComponentPattern mcpTransitionReactant = mtpTransitionReactant.getMolecularComponentPattern(ssldSiteType.getName());
        String ssldInitialStateName = ssldInitialState.getName();
        ComponentStateDefinition csdInitial = mcpTransitionReactant.getMolecularComponent().getComponentStateDefinition(ssldInitialStateName);
        ComponentStatePattern cspTransitionReactant = new ComponentStatePattern(csdInitial);
        mcpTransitionReactant.setComponentStatePattern(cspTransitionReactant);
        // bond is already set to Any
        if(ssldReaction.getCondition().equals(TransitionReaction.NO_CONDITION)) {
            for(MolecularComponentPattern mcp : mtpTransitionReactant.getComponentPatternList()) {
                if(mcp == mcpTransitionReactant) {
                    continue;
                }
                mcp.setBondType(MolecularComponentPattern.BondType.None);
            }
        }

        SpeciesPattern spProduct = m.getProductPatternOneFromMolecule(ssldTransitionMolecule).getSpeciesPattern();
        MolecularTypePattern mtpTransitionProduct = spProduct.getMolecularTypePatterns(ssldTransitionMolecule.getName()).get(transitionMoleculeIndexInSpeciesPattern);
        MolecularComponentPattern mcpTransitionProduct = mtpTransitionProduct.getMolecularComponentPattern(ssldSiteType.getName());
        String ssldFinalStateName = ssldFinalState.getName();
        ComponentStateDefinition csdFinal = mcpTransitionProduct.getMolecularComponent().getComponentStateDefinition(ssldFinalStateName);
        ComponentStatePattern cspTransitionProduct = new ComponentStatePattern(csdFinal);
        mcpTransitionProduct.setComponentStatePattern(cspTransitionProduct);
        System.out.println("  " + ssldReaction.getCondition());
        if(ssldReaction.getCondition().equals(TransitionReaction.NO_CONDITION)) {
            for(MolecularComponentPattern mcp : mtpTransitionProduct.getComponentPatternList()) {
                if(mcp == mcpTransitionProduct) {
                    continue;
                }
                mcp.setBondType(MolecularComponentPattern.BondType.None);
            }
        }
    }

    private void adjustProductSite(BindingReaction ssldReaction, Mapping m) {
        Molecule ssldMoleculeOne = ssldReaction.getMolecule(0);
        SiteType ssldSiteTypeOne = ssldReaction.getType(0);
        State ssldStateOne = ssldReaction.getState(0);
        SpeciesPattern spProduct = m.getProductPatternOneFromMolecule(ssldMoleculeOne).getSpeciesPattern();
        List<MolecularTypePattern> mtpListOne = spProduct.getMolecularTypePatterns(ssldMoleculeOne.getName());
        MolecularTypePattern mtpProductOne = mtpListOne.get(0);
        MolecularComponentPattern mcpProductOne = mtpProductOne.getMolecularComponentPattern(ssldSiteTypeOne.getName());
        String ssldStateOneName = ssldStateOne.getName();
        if(!ssldStateOneName.contentEquals(ANY_STATE_STRING)) {
            ComponentStateDefinition csd = mcpProductOne.getMolecularComponent().getComponentStateDefinition(ssldStateOneName);
            ComponentStatePattern cspProductOne = new ComponentStatePattern(csd);
            mcpProductOne.setComponentStatePattern(cspProductOne);
        }
        Molecule ssldMoleculeTwo = ssldReaction.getMolecule(1);
        SiteType ssldSiteTypeTwo = ssldReaction.getType(1);
        State ssldStateTwo = ssldReaction.getState(1);
        SpeciesPattern spProductTwo = m.getProductPatternTwoFromMolecule(ssldMoleculeTwo).getSpeciesPattern();
        if(spProduct != spProductTwo) {     // we load spProductTwo just to verify consistency
            throw new RuntimeException("Instances of the same species pattern should be equal");
        }
        List<MolecularTypePattern> mtpListTwo = spProduct.getMolecularTypePatterns(ssldMoleculeTwo.getName());
        MolecularTypePattern mtpProductTwo;
        if(ssldMoleculeOne == ssldMoleculeTwo) {
            mtpProductTwo = mtpListTwo.get(1);
        } else {
            mtpProductTwo = mtpListTwo.get(0);
        }
        MolecularComponentPattern mcpProductTwo = mtpProductTwo.getMolecularComponentPattern(ssldSiteTypeTwo.getName());
        String ssldStateTwoName = ssldStateTwo.getName();
        if(!ssldStateTwoName.contentEquals(ANY_STATE_STRING)) {
            ComponentStateDefinition csd = mcpProductTwo.getMolecularComponent().getComponentStateDefinition(ssldStateTwoName);
            ComponentStatePattern cspProductTwo = new ComponentStatePattern(csd);
            mcpProductTwo.setComponentStatePattern(cspProductTwo);
        }
        // we know for sure that this is the only explicit bond in each of the 2 molecular type patterns of the product
        int bondId = 1;     // correct would be:  sp.nextBondId();
        mcpProductOne.setBondId(bondId);    // this also sets the BondType to Specified
        mcpProductTwo.setBondId(bondId);
        // very abusive way of bypassing the fact that the Bond constructor is private
        mcpProductOne.setBond(SpeciesPattern.generateBond(mtpProductTwo, mcpProductTwo));
        mcpProductTwo.setBond(SpeciesPattern.generateBond(mtpProductOne, mcpProductOne));
    }
    private void adjustReactantSite(BindingReaction ssldReaction, int reactantIndex, Mapping m) {
        Molecule ssldMolecule = ssldReaction.getMolecule(reactantIndex);
        SiteType ssldSiteType = ssldReaction.getType(reactantIndex);
        State ssldState = ssldReaction.getState(reactantIndex);
        SpeciesPattern spReactant;
        if(reactantIndex == 0) {
            spReactant = m.getReactantPatternOneFromMolecule(ssldMolecule).getSpeciesPattern();
        } else {
            spReactant = m.getReactantPatternTwoFromMolecule(ssldMolecule).getSpeciesPattern();
        }
        MolecularTypePattern mtpReactant = spReactant.getMolecularTypePatterns(ssldMolecule.getName()).get(0);
        MolecularComponentPattern mcpReactant = mtpReactant.getMolecularComponentPattern(ssldSiteType.getName());
        String ssldStateName = ssldState.getName();
        if(!ssldStateName.contentEquals(ANY_STATE_STRING)) {
            ComponentStateDefinition csd = mcpReactant.getMolecularComponent().getComponentStateDefinition(ssldStateName);
            ComponentStatePattern cspReactant = new ComponentStatePattern(csd);
            mcpReactant.setComponentStatePattern(cspReactant);
        }
        mcpReactant.setBondType(MolecularComponentPattern.BondType.None);
        mcpReactant.setBond(null);
    }

    private SpeciesPattern getSpeciesPattern(Molecule moleculeOne, Molecule moleculeTwo, Mapping m) {
        if(moleculeOne == null) {
            // binding reaction products have 2 molecules
            // conditional transition reactions have 2 molecules, condition being the first
            // all the rest have one molecule
            return getSpeciesPattern(moleculeTwo, m);
        }
        SpeciesPattern sp = new SpeciesPattern();
        MolecularTypePattern mtpOne = getMolecularTypePattern(moleculeOne, m);      // condition molecule
        sp.addMolecularTypePattern(mtpOne);
        MolecularTypePattern mtpTwo = getMolecularTypePattern(moleculeTwo, m);      // transition molecule
        sp.addMolecularTypePattern(mtpTwo);
        return sp;
    }
    private SpeciesPattern getSpeciesPattern(Molecule molecule, Mapping m) {
        SpeciesPattern sp = new SpeciesPattern();
        MolecularTypePattern mtp = getMolecularTypePattern(molecule, m);
        sp.addMolecularTypePattern(mtp);
        return sp;
    }
    private MolecularTypePattern getMolecularTypePattern(Molecule molecule, Mapping m) {
        MolecularTypePattern mtp = new MolecularTypePattern(m.get(molecule));
        return mtp;
    }

    public static SsldModel importSsldFile(File file) {

        SsldModel model = new SsldModel();
        model.setFile(file);

        BufferedReader br = null;
        FileReader fr = null;
        Scanner sc = null;
        try{
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            sc = new Scanner(br);
            sc.useDelimiter("\\*\\*\\*");
            while(sc.hasNext()) {
                String next = sc.next().trim();
                switch(next) {
                    case TIME_INFORMATION:
                        model.systemTimes.loadData(sc.next().trim());
                        break;
                    case SPATIAL_INFORMATION:
                        model.boxGeometry.loadData(sc.next().trim());
                        break;
                    case MOLECULES:
                        model.molecules = Molecule.loadMolecules(sc.next().trim());
                        break;
                    case MOLECULE_FILES:
                        Molecule.loadMoleculesFiles(model, sc.next().trim(), model.molecules);
                        break;
                    case DECAY_REACTIONS:
                        DecayReaction.loadReactions(model, new Scanner(sc.next().trim()));
                        break;
                    case TRANSITION_REACTIONS:
                        model.transitionReactions = TransitionReaction.loadReactions(model, new Scanner(sc.next().trim()));
                        break;
                    case ALLOSTERIC_REACTIONS:
                        model.allostericReactions = AllostericReaction.loadReactions(model, new Scanner(sc.next().trim()));
                        break;
                    case BINDING_REACTIONS:
                        model.bindingReactions = BindingReaction.loadReactions(model, new Scanner(sc.next().trim()));
                        break;
                    case MOLECULE_COUNTERS:
                        MoleculeCounter.loadCounters(model, new Scanner(sc.next().trim()));
                        break;
                    case STATE_COUNTERS:
                        StateCounter.loadCounters(model, new Scanner(sc.next().trim()));
                        break;
                    case BOND_COUNTERS:
                        BondCounter.loadCounters(model, new Scanner(sc.next().trim()));
                        break;
                    case SITE_PROPERTY_COUNTERS:
                        SitePropertyCounter.loadCounters(model, new Scanner(sc.next().trim()));
                        break;
                    case CLUSTER_COUNTERS:
                        Scanner xsc = new Scanner(sc.next().trim());
                        // Skip "Track_Clusters: "
                        xsc.next();
                        model.trackClusters = xsc.nextBoolean();
                        xsc.close();
                        break;
                    case SYSTEM_ANNOTATION:
                        model.systemAnnotation.setAnnotation(sc.next().trim());
                        break;
                    case MOLECULE_ANNOTATIONS:
                        model.loadMoleculeAnnotations(sc.next().trim());
                        break;
                    case REACTION_ANNOTATIONS:
                        model.loadReactionAnnotations(sc.next().trim());
                        break;
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace(System.out);
        } finally {
            if(sc != null) {
                sc.close();
            }
            if(br != null) {
                try {
                    br.close();
                } catch(IOException bioe) {
                    bioe.printStackTrace(System.out);
                }
            }
            if(fr != null) {
                try {
                    fr.close();
                } catch(IOException fioe) {
                    fioe.printStackTrace(System.out);
                }
            }
        }
        return model;
    }

}
