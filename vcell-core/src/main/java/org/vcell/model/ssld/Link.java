package org.vcell.model.ssld;

import java.awt.*;
import java.io.*;

import org.vcell.util.springsalad.IOHelp;

public class Link {

    private final Site site1, site2;    // Every link has two sites to which it connects.
    private int index;  // Assign each link an index. This is unnecessary but makes the lists look nice.

    // Make it so link can only be created when given two sites. There's no reason to ever have a dangling bond.
    public Link(Site site1, Site site2) {
        this.site1 = site1;
        this.site2 = site2;
        site1.connectTo(site2);
        site2.connectTo(site1);
    }

    public double getX1() {
        return site1.getX();
    }
    public double getY1() {
        return site1.getY();
    }
    public double getZ1() {
        return site1.getZ();
    }
    public double getX2() {
        return site2.getX();
    }
    public double getY2() {
        return site2.getY();
    }
    public double getZ2() {
        return site2.getZ();
    }
    public double getLength() {
        double dx = getX2() - getX1();
        double dy = getY2() - getY1();
        double dz = getZ2() - getZ1();
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }
    public void setIndex(int index) {
        this.index = index;
    }
    public int getIndex() {
        return index;
    }
    @Override
    public String toString() {
        return "Link " + index + " : Site " + site1.getIndex() + " :: Site " + site2.getIndex();
    }

    public Site getSite1() {
        return site1;
    }
    public Site getSite2() {
        return site2;
    }
    public Site [] getSites() {
        return new Site[]{site1,site2};
    }

    public double [] unitVector() {     // unit vector from Site1 to Site2
        double dx = site2.getX() - site1.getX();
        double dy = site2.getY() - site1.getY();
        double dz = site2.getZ() - site1.getZ();
        double length = Math.sqrt(dx*dx + dy*dy + dz*dz);
        return new double[]{dx/length, dy/length, dz/length};
    }

}
