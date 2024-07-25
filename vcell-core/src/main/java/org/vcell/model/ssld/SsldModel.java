package org.vcell.model.ssld;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.prefs.Preferences;

import org.vcell.util.springsalad.IOHelp;

public class SsldModel {

    public final static String DEFAULT_FOLDER = "Default Folder";
    public final static String DEFAULT_SYSTEM_NAME = "New Model";




    public ArrayList<Molecule> molecules = new ArrayList<>();
    public ArrayList<BindingReaction> bindingReactions = new ArrayList<>();
    public ArrayList<TransitionReaction> transitionReactions = new ArrayList<>();
    public ArrayList<AllostericReaction> allostericReactions = new ArrayList<>();
    public final BoxGeometry boxGeometry;
    public final SystemTimes systemTimes;

    public final Annotation systemAnnotation;
    public boolean trackClusters;

    private File file;
    private String systemName;      // The system name (same as the file name, usually)
    private File defaultFolder;
    private Preferences pref;       // User preferences


    public SsldModel(String systemName) {
        this.systemName = systemName;
        boxGeometry = new BoxGeometry();
        systemTimes = new SystemTimes();
        systemAnnotation = new Annotation();
        pref = Preferences.userRoot();

        String defaultFolderLocation = pref.get(DEFAULT_FOLDER, null);
        if(defaultFolderLocation != null) {
            defaultFolder = new File(defaultFolderLocation);
        } else {
            defaultFolder = null;
        }
        trackClusters = true;
    }
    public SsldModel() {
        this(DEFAULT_SYSTEM_NAME);
    }


    public File getFile() {             // file
        return file;
    }
    public void setFile(File file) {
        this.file = file;
        String filename = file.getName();
        this.setSystemName(filename.substring(0, filename.length()-4));
    }
    public File getDefaultFolder() {    // default folder
        return defaultFolder;
    }
    public void setDefaultFolder(File folder) {
        defaultFolder = folder;
        writeDefaultFolder();
    }
    private void writeDefaultFolder() {
        pref.put(DEFAULT_FOLDER, defaultFolder.getAbsolutePath());
    }
    public void setSystemName(String name) {    // system name
        this.systemName = name;
    }
    public String getSystemName() {
        return systemName;
    }

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
    // Add a single reaction
    public void addBindingReaction(BindingReaction reaction){
        bindingReactions.add(reaction);
    }

    // Remove a single binding reaction
    public void removeBindingReaction(BindingReaction reaction){
        bindingReactions.remove(reaction);
    }

    // Remove a binding reaction based on its index
    public void removeBindingReaction(int index){
        bindingReactions.remove(index);
    }

    // Get the entire binding reaction array
    public ArrayList<BindingReaction> getBindingReactions(){
        return bindingReactions;
    }

    // Get a binding reaction by its index
    public BindingReaction getBindingReaction(int index){
        return bindingReactions.get(index);
    }

    // Get a binding reaction by name
    public BindingReaction getBindingReaction(String name){
        BindingReaction bindingReaction = null;
        for(BindingReaction reaction : bindingReactions){
            if(reaction.getName().equals(name)){
                bindingReaction = reaction;
            }
        }
        return bindingReaction;
    }

    // ------------------------ transition reactions
    public ArrayList<TransitionReaction> getTransitionReactions(){
        return transitionReactions;
    }
    // Get transition reaction by index
    public TransitionReaction getTransitionReaction(int index){
        return transitionReactions.get(index);
    }

    // Get transition reaction by name
    public TransitionReaction getTransitionReaction(String name){
        TransitionReaction transitionReaction = null;
        for(TransitionReaction reaction : transitionReactions){
            if(reaction.getName().equals(name)){
                transitionReaction = reaction;
                break;
            }
        }
        return transitionReaction;
    }



    // ------------------------ allosteric reactions
    public AllostericReaction getAllostericReaction(String name){
        AllostericReaction reaction = null;
        for(AllostericReaction rxn : allostericReactions){
            if(rxn.getName().equals(name)){
                reaction = rxn;
                break;
            }
        }
        return reaction;
    }



    // ------------------------ annotations
    private void loadMoleculeAnnotations(String string) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Scanner sc = new Scanner(string);
        sc.useDelimiter("Annotation:");
        while(sc.hasNext()){
            String moleculeInput = sc.next();
            if(moleculeInput != null && moleculeInput.length() != 0) {
                Scanner molSc = new Scanner(moleculeInput);
                Molecule molecule = this.getMolecule(IOHelp.getNameInQuotes(molSc));
                Annotation a = molecule.getAnnotation();
                StringBuilder sb = new StringBuilder();
                // finish line
                molSc.nextLine();
                // skip "{"
                molSc.nextLine();
                while(molSc.hasNextLine()) {
                    String line = molSc.nextLine();
                    if(line.equals("}")){
                        break;
                    } else {
                        sb.append(line).append("\n");
                    }
                }
                a.setAnnotation(sb.toString());
                molSc.close();
            }
        }
        sc.close();
        // </editor-fold>
    }
    private void loadReactionAnnotations(String string) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Scanner sc = new Scanner(string);
        sc.useDelimiter("Annotation:");
        while(sc.hasNext()) {
            String rInput = sc.next();
            if(rInput != null && rInput.length() != 0) {
                Scanner rSc = new Scanner(rInput);
                String rxnName = IOHelp.getNameInQuotes(rSc);
                Reaction rxn = this.getTransitionReaction(rxnName);
                if(rxn == null) {
                    rxn = this.getAllostericReaction(rxnName);
                    if(rxn == null) {
                        rxn = this.getBindingReaction(rxnName);
                        if(rxn == null) {
                            System.out.println("ERROR: Tried to read in annotation a non-existent reaction.");
                        }
                    }
                }
                if(rxn != null) {
                    Annotation a = rxn.getAnnotation();
                    StringBuilder sb = new StringBuilder();
                    rSc.nextLine();
                    rSc.nextLine();
                    while(rSc.hasNextLine()) {
                        String line = rSc.nextLine();
                        if(line.equals("}")) {
                            break;
                        } else {
                            sb.append(line).append("\n");
                        }
                    }
                    a.setAnnotation(sb.toString());
                }
                rSc.close();
            }
        }
        sc.close();
        // </editor-fold>
    }

}
