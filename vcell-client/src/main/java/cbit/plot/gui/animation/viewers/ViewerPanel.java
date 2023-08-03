/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cbit.plot.gui.animation.viewers;

import java.awt.*;
import javax.swing.*;

public class ViewerPanel extends JPanel {

    private MyCanvas3D viewer;

    public ViewerPanel(){
        super();
        this.setLayout(new FlowLayout());
        this.setBackground(Color.black);
        this.setPreferredSize(new Dimension(710,510));
    }

    public MyCanvas3D getViewer(){
        return viewer;
    }

    public void makeViewer(){
        viewer = new MyCanvas3D();
    }

    public void addViewer(boolean showMembrane, boolean showAxes){
        viewer.makeMembrane();
        viewer.showMembrane(showMembrane);
        viewer.showAxes(showAxes);
//        JPanel cpanel = new JPanel();
//        cpanel.setBackground(Color.black);
//        cpanel.setLayout(new FlowLayout());
        this.add(viewer);
        this.validate();
        // this.add(cpanel, "Center");
        // viewer.setupOffScreenCanvas();
    }

    public void setBounds(float xsize, float ysize, float zsize){
        // System.out.println("View panel was passed (xsize, ysize) = (" + xsize + ", " + ysize + ")");
        viewer.setBounds(xsize, ysize, zsize);
    }

    public void removeViewer(){
        if(viewer != null){
            this.remove(viewer);
        }
        this.validate();
        viewer = null;
    }

}
