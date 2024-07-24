package org.vcell.model.ssld;

import java.util.ArrayList;

public class SsldModel {

    public ArrayList<Molecule> molecules = new ArrayList<>();
    public ArrayList<BindingReaction> bindingReactions = new ArrayList<>();
    public ArrayList<TransitionReaction> transitionReactions = new ArrayList<>();
    public ArrayList<AllostericReaction> allostericReactions = new ArrayList<>();
    public BoxGeometry boxGeometry = new BoxGeometry();
    public SystemTimes systemTimes = new SystemTimes();
    public Annotation systemAnnotation = new Annotation();
    public boolean trackClusters;



    // -------------------- molecules
    public void addMolecule(Molecule molecule) {
        molecules.add(molecule);
    }
    public void removeMolecule(Molecule molecule) {
        molecules.remove(molecule);
    }
    public void removeMolecule(int index) {
        molecules.remove(index);
    }

    public ArrayList<Molecule> getMolecules() {
        return molecules;
    }
    public Molecule getMolecule(int index) {
        return molecules.get(index);
    }

    // Retrieve a single molecule by its name. Returns null if no molecules has the given name.
    public Molecule getMolecule(String name) {
        Molecule molecule = null;
        for(Molecule mol : molecules) {
            if(mol.getName().equals(name)) {
                molecule = mol;
                break;
            }
        }
        return molecule;
    }

    public ArrayList<String> getMoleculeNames() {
        ArrayList<String> names = new ArrayList<>();
        for(Molecule molecule : molecules) {
            names.add(molecule.getName());
        }
        return names;
    }

    // ------------------------ binding reactions

}
