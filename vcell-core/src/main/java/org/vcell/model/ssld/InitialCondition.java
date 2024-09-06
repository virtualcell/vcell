package org.vcell.model.ssld;

import java.util.ArrayList;

public class InitialCondition {

    public final static String RANDOM = "Random";
    public final static String SET = "Set";

    private final Molecule molecule;
    private int number;     // Number of molecules

    // We'll let the user define initial positions.
    // The initial coordinate will always refer to the position of Site 0 in the box.
    // If the user doesn't provide enough ICs, then the rest of the molecules are assigned randomly.
    // If they provide too many, then we ignore the last few.
    private boolean randomIC;
    private final ArrayList<Double> xIC;
    private final ArrayList<Double> yIC;
    private final ArrayList<Double> zIC;

    public InitialCondition(Molecule molecule) {
        this.molecule = molecule;
        number = 0;
        randomIC = true;    // Default is random IC
        xIC = new ArrayList<>();
        yIC = new ArrayList<>();
        zIC = new ArrayList<>();
    }

    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }

    // get and set the number in terms of concentration (volume in nm^3)
    public void setConcentration(double concentration, double volume) {
        this.number = (int)Math.round(602.0*volume*concentration/(1000*1000*1000));
    }
    public double getConcentration(double volume) {
        return 1000.0*1000.0*1000.0*number/(602.0*volume);
    }

    public Molecule getMolecule() {
        return molecule;
    }
    public String getMoleculeName() {
        return molecule.getName();
    }
    public String getMoleculeLocation() {
        return molecule.getLocation();
    }

    public void clearInitialPositions() {
        xIC.clear();
        yIC.clear();
        zIC.clear();
    }
    public void addInitialPosition(double x, double y, double z) {
        if(randomIC) {
            System.out.println("Tried to add IC when set to random!");
            return;
        }
        xIC.add(x);
        yIC.add(y);
        zIC.add(z);
    }
    public void setInitialX(int i, double x) {
        xIC.remove(i);
        xIC.add(i, x);
    }
    public void setInitialY(int i, double y) {
        yIC.remove(i);
        yIC.add(i, y);
    }
    public void setInitialZ(int i, double z) {
        zIC.remove(i);
        zIC.add(i, z);
    }

    // Return whether this operation was successful
    public boolean setAllInitialPositions(String [] xs, String [] ys, String [] zs) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(xs.length != ys.length || ys.length != zs.length) {
            return false;
        } else {
            clearInitialPositions();
            try{
                for(int i=0;i<xs.length;i++) {
                    xIC.add(Double.parseDouble(xs[i]));
                    yIC.add(Double.parseDouble(ys[i]));
                    zIC.add(Double.parseDouble(zs[i]));
                }
            } catch(NumberFormatException nfe) {
                return false;
            }
            return true;
        }
        // </editor-fold>
    }

    public double getInitialX(int i) {
        return xIC.get(i);
    }
    public double getInitialY(int i) {
        return yIC.get(i);
    }
    public double getInitialZ(int i) {
        return zIC.get(i);
    }
    public ArrayList<Double> getXIC() {
        return xIC;
    }
    public ArrayList<Double> getYIC() {
        return yIC;
    }
    public ArrayList<Double> getZIC() {
        return zIC;
    }

    public boolean usingRandomInitialPositions() {  // Return boolean indicating if we're using random positions
        return randomIC;
    }
    public void setUsingRandomInitialPositions(boolean bool) {
        randomIC = bool;
        // Make sure we initialize the initial position array
        if(randomIC) {
            clearInitialPositions();
        } else {
            double z = 0.0;
            if(molecule.getLocation().equals(SystemGeometry.INSIDE)) {
                z = 4; // Just a default above the membrane
            } else if(molecule.getLocation().equals(SystemGeometry.OUTSIDE)) {
                z = -4; // Just a default below the membrane
            }
            for(int i=0;i<number;i++) {
                this.addInitialPosition(0.0, 0.0, z);
            }
        }
    }
    public boolean hasInitialPositions() {
        return !xIC.isEmpty();
    }


}
