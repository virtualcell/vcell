package org.vcell.model.ssld;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import org.vcell.util.springsalad.Colors;
import org.vcell.util.springsalad.NamedColor;
import org.vcell.util.springsalad.IOHelp;

public class SiteType {

    private String typeName;
    private double radius; // radius in nm
    private double D;      // diffusion constant in um^2/s
    private NamedColor color;

    private final Molecule molecule;    // the molecule this type belongs to.

     // Types can have internal states. For example, a catalytic site type might
     // have two states, active and inactive.  Phosphorylation sites would have
     // two states, phospho and dephos. All types have at least one state.
    private ArrayList<State> states = new ArrayList<>();


    /**
     *                  MEMBRANE ANCHOR DESIGNATION
     * The membrane-anchor is a special, built-in type used to indicate that
     * a site is permanently bound to the membrane and only undergoes 2D
     * diffusion in the membrane.  Also, these sites are forbidden from having
     * any reactions (other than molecule-level creation/decay reactions).
     * The site is otherwise treated as any other site.  For example, the user
     * could define several membrane anchor site for one molecule.  However,
     * there can only ever be one membrane anchor type for a given molecule.
     */
    public static String ANCHOR = "Anchor";

    /******************************************************************\
     *                         CONSTRUCTORS                           *
     *  @param typeName The name of this type.                        *
     *  @param molecule The molecule this site type belongs to        *
    \******************************************************************/

    public SiteType(Molecule molecule, String typeName) {
        //Just give some defaults
        this.typeName = typeName;
        this.molecule = molecule;
        radius = 1;
        D = 1;
        color = Colors.RED;
        states.add(new State(this, "State0"));
    }

    public void setName(String typeName) {
        this.typeName = typeName;
    }

    public void setRadius(double r) {
        radius = r;
    }

    public void setD(double D) {
        this.D = D;
    }

    public void setColor(String colorName) {
        color = Colors.getColorByName(colorName);
    }

    public void setColor(NamedColor color) {
        this.color = color;
    }

    public String getName() {
        return typeName;
    }
    public double getRadius() {
        return radius;
    }
    public double getReactionRadius() {
        // assumes radius in nanometers
        // Either 1.5x radius or radius+2 nm, whichever is smaller, but not smaller than radius+0.5 nm
        double minRadius = Math.max(0.5+radius,  1.5*radius);
        double reactionRadius = Math.min(minRadius, radius+2);
        return reactionRadius;
    }
    public double getD() {
        return D;
    }
    public Color getColor() {
        return color.getColor();
    }
    public String getColorName() {
        return color.getName();
    }
    @Override
    public String toString() {
        return typeName;
    }

    public Molecule getMolecule() {
        return molecule;
    }
    public String getMoleculeName() {
        return molecule.getName();
    }

    public ArrayList<State> getStates() {
        return states;
    }
    public State getState(String name) {
        State s = null;
        for (State state : states) {
            if (name.equals(state.getName())) {
                s = state;
                break;
            }
        }
        return s;
    }
    public State getState(int index) {
        return states.get(index);
    }
    public void setStateArray(ArrayList<State> stateArray) {
        states = stateArray;
    }
    public void addState(State state) {
        states.add(state);
    }
    public void removeState(State state) {
        states.remove(state);
    }


    public static SiteType readType(Molecule mol, String s){
        SiteType tempType = new SiteType(mol, "TempName");
        Scanner sc = new Scanner(s);
        if(!sc.next().equals("TYPE:")){
            System.out.println("ERROR: Type line did not begin with \"TYPE:\"");
        }
        while(sc.hasNext()){
            String scnext = sc.next();
            switch(scnext){
                case "Name":{
                    tempType.setName(IOHelp.getNameInQuotes(sc));
                    break;
                }
                case "Radius":{
                    tempType.setRadius(Double.parseDouble(sc.next()));
                    break;
                }
                case "D":{
                    tempType.setD(Double.parseDouble(sc.next()));
                    break;
                }
                case "Color":{
                    tempType.setColor(sc.next());
                    break;
                }
                case "STATES":{
                    ArrayList<State> tempStates = new ArrayList<>();
                    while(sc.hasNext()){
                        tempStates.add(new State(tempType, IOHelp.getNameInQuotes(sc)));
                    }
                    tempType.setStateArray(tempStates);
                    break;
                }
                default:{
                    System.out.println("Type reader received unexpected input: " + scnext);
                }
            }
        }
        sc.close();
        return tempType;
    }



}
