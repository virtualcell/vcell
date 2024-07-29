package org.vcell.model.ssld;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.model.*;
import org.vcell.model.rbm.ComponentStateDefinition;
import org.vcell.model.rbm.MolecularComponent;
import org.vcell.model.rbm.MolecularType;
import org.vcell.solver.langevin.LangevinLngvWriter;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.*;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.prefs.Preferences;

import static org.vcell.solver.langevin.LangevinLngvWriter.*;

public class SsldUtils {


    public static BioModel fromSsld(SsldModel ssldModel) throws Exception {

        ModelUnitSystem mus = ModelUnitSystem.createDefaultVCModelUnitSystem();
        BioModel bioModel = new BioModel(null);
        bioModel.setName(ssldModel.getSystemName());
        Model model = new Model("model", mus);
        bioModel.setModel(model);

        // structures
        if(bioModel.getModel().getStructures().length > 0) {
            Structure struct = bioModel.getModel().getStructure(0);
            if (struct != null) {
                bioModel.getModel().removeStructure(struct, true);
            }
        }
        LinkedList<Structure> newstructures = new LinkedList<Structure>();
        Feature intra = new Feature(Structure.SpringStructureEnum.Intracellular.columnName);
        newstructures.add(intra);
        Membrane membr = new Membrane(Structure.SpringStructureEnum.Membrane.columnName);
        newstructures.add(membr);
        Feature extra = new Feature(Structure.SpringStructureEnum.Extracellular.columnName);
        newstructures.add(extra);
        Structure[] structarray = new Structure[newstructures.size()];
        newstructures.toArray(structarray);
        model.setStructures(structarray);

        // molecular types
        for(Molecule ssldMolecule : ssldModel.getMolecules()) {
            MolecularType mt = new MolecularType(ssldMolecule.getName(), model);
            for(Site ssldSite : ssldMolecule.getSiteArray()) {
                SiteType ssldType = ssldSite.getType();
                MolecularComponent mc = new MolecularComponent(ssldType.getName());
                mc.setIndex(ssldSite.getIndex());   // TODO: starts at 1, adjust to start at 0? see export
                for(State ssldState : ssldType.getStates()) {
                    ComponentStateDefinition csd = new ComponentStateDefinition(ssldState.getName());
                    mc.addComponentStateDefinition(csd);
                }
                mt.addMolecularComponent(mc);
            }
            model.getRbmModelContainer().addMolecularType(mt, false);
        }



        return bioModel;
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
                        // TODO: looks like we load the reactions but never save them in the model. CHECK this
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
