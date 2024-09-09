package org.vcell.model.ssld;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import org.vcell.util.springsalad.IOHelp;

public class Site {

    // Position, given in nm.
    double x = 0;
    double y = 0;
    double z = 0;

    // Every site is part of a molecule. Give each site a reference to its
    // molecule and an index to identify it within the molecule.
    private final Molecule molecule;
    private int index;

    private SiteType type;      // Every site has a type, which determines all its properties.
    private State initialState; // The initial state of the site

    // Keep a list of the sites connected to this site.  This makes it easy
    // to determine if all sites in a molecule are connected.
    private final ArrayList<Site> connectedSites = new ArrayList<>();
    // A boolean to tell us if we've checked the connectivity of this site.
    private boolean checked = false;

    private String location = null;     // Sites are not assigned a location initially
    private boolean positionOK = true;  // Boolean to tell us if the site is positioned correctly

    private final SitePropertyCounter sitePropertyCounter;  // Each site has its own site property counter

    public Site(Molecule molecule, SiteType type) {
        this.molecule = molecule;
        this.type = type;
        this.sitePropertyCounter = new SitePropertyCounter(this);
    }

    public Site(Molecule molecule) {
        this.molecule = molecule;
        this.sitePropertyCounter = new SitePropertyCounter(this);
    }

    @Override
    public String toString() {
        return "Site " + index + " : " + getTypeName();
    }

    public Molecule getMolecule() {
        return molecule;
    }
    public void setIndex(int i) {
        index = i;
    }
    public int getIndex() {
        return index;
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setZ(double z) {
        this.z = z;
    }
    public void setPosition(double x, double y, double z) {
        setX(x); setY(y); setZ(z);
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
    }
    public double [] getPosition() {
        return new double[]{x,y,z};
    }
    public void translate(double dx, double dy, double dz) {
        x += dx;
        y += dy;
        z += dz;
    }

    public void setType(SiteType type) {
        this.type = type;
    }
    public SiteType getType() {
        return type;
    }
    public double getRadius() {
        return type.getRadius();
    }
    public double getD() {
        return type.getD();
    }
    public String getTypeName() {
        return type.getName();
    }
    public void setInitialState(State state) {
        this.initialState = state;
    }
    public State getInitialState() {
        return initialState;
    }

    public void setLocation(String location) {
        if(location.equals(SystemGeometry.INSIDE) || location.equals(SystemGeometry.OUTSIDE) || location.equals(SystemGeometry.MEMBRANE)) {
            this.location = location;
        } else {
            System.out.println("Tried to set site to an invalid location. Given string " + location + ".");
        }
    }
    public void setPositionOK(boolean bool) {
        positionOK = bool;
    }
    public String getLocation() {
        return location;
    }
    public boolean getPositionOK() {
        return positionOK;
    }

    public void connectTo(Site site) {
        if(!connectedSites.contains(site)) {
            connectedSites.add(site);
        }
    }
    public boolean hasLink() {
        return !connectedSites.isEmpty();
    }
    public void clearConnectedSites() {
        connectedSites.clear();
    }
    public ArrayList<Site> getConnectedSites() {
        return connectedSites;
    }
    public void setChecked(boolean bool) {
        checked = bool;
    }
    public boolean getChecked() {
        return checked;
    }
    public SitePropertyCounter getPropertyCounter() {
        return sitePropertyCounter;
    }

    public boolean equals(Site s) {
        if(this.getPosition() == s.getPosition()) {
            return true;
        }
        return false;
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
