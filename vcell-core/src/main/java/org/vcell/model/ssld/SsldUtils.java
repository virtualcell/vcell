package org.vcell.model.ssld;

import org.vcell.solver.langevin.LangevinLngvWriter;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.*;
import java.util.Scanner;
import java.util.prefs.Preferences;

import static org.vcell.solver.langevin.LangevinLngvWriter.*;

public class SsldUtils {


    public static SsldModel importSsldFile(File file) {

        SsldModel model = new SsldModel();

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
//                    case MOLECULE_FILES:
//                        Molecule.loadMoleculesFiles(model, sc.next().trim(), model.molecules);
//                        break;
//                    case DECAY_REACTIONS:
//                        DecayReaction.loadReactions(model, new Scanner(sc.next().trim()));
//                        break;
//                    case TRANSITION_REACTIONS:
//                        model.transitionReactions = TransitionReaction.loadReactions(model, new Scanner(sc.next().trim()));
//                        break;
//                    case ALLOSTERIC_REACTIONS:
//                        model.allostericReactions = AllostericReaction.loadReactions(model, new Scanner(sc.next().trim()));
//                        break;
//                    case BINDING_REACTIONS:
//                        model.bindingReactions = BindingReaction.loadReactions(model, new Scanner(sc.next().trim()));
//                        break;
//                    case MOLECULE_COUNTERS:
//                        MoleculeCounter.loadCounters(model, new Scanner(sc.next().trim()));
//                        break;
//                    case STATE_COUNTERS:
//                        StateCounter.loadCounters(model, new Scanner(sc.next().trim()));
//                        break;
//                    case BOND_COUNTERS:
//                        BondCounter.loadCounters(model, new Scanner(sc.next().trim()));
//                        break;
//                    case SITE_PROPERTY_COUNTERS:
//                        SitePropertyCounter.loadCounters(model, new Scanner(sc.next().trim()));
//                        break;
//                    case CLUSTER_COUNTERS:
//                        Scanner xsc = new Scanner(sc.next().trim());
//                        // Skip "Track_Clusters: "
//                        xsc.next();
//                        model.trackClusters = xsc.nextBoolean();
//                        xsc.close();
//                        break;
//                    case SYSTEM_ANNOTATION:
//                        model.systemAnnotation.setAnnotation(sc.next().trim());
//                        break;
//                    case MOLECULE_ANNOTATIONS:
//                        loadMoleculeAnnotations(model, sc.next().trim());
//                        break;
//                    case REACTION_ANNOTATIONS:
//                        loadReactionAnnotations(model, sc.next().trim());
//                        break;
                }
            }
        } catch(IOException ioe) {
            ioe.printStackTrace(System.out);
        } finally {
            if(sc != null){
                sc.close();
            }
            if(br != null){
                try{
                    br.close();
                } catch(IOException bioe){
                    bioe.printStackTrace(System.out);
                }
            }
            if(fr != null){
                try{
                    fr.close();
                } catch(IOException fioe){
                    fioe.printStackTrace(System.out);
                }
            }
        }
        return model;
    }

}
