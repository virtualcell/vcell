/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cbit.plot.gui.animation.viewers;

import cbit.plot.gui.animation.helpersetup.Colors;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.AxisAngle4d;


public class Axes extends BranchGroup {
    
    private final Axis xAxis;
    private final Axis yAxis;
    private final Axis zAxis;
    
    public Axes(){
        this.setCapability(BranchGroup.ALLOW_DETACH);
        xAxis = new Axis(Colors.BLUE3D, 10f);
        yAxis = new Axis(Colors.GREEN3D, 10f);
        zAxis = new Axis(Colors.ORANGE3D, 10f);
        redrawAxes();
    }
    
    /* ******************* GET THE AXES *********************************/
    
    public Axis getXAxis(){
        return xAxis;
    }
    
    public Axis getYAxis(){
        return yAxis;
    }
    
    public Axis getZAxis(){
        return zAxis;
    }
    
    public Axis getAxis(int i){
        switch(i){
            case 0:
                return xAxis;
            case 1:
                return yAxis;
            case 2:
                return zAxis;
            default:
                return null;
        }
    }
    
    /* *******************  REDRAW THE AXES *****************************/
    
    private void redrawAxes(){
        TransformGroup topTG = new TransformGroup();
        topTG.addChild(yAxis);
        
        Transform3D xt3d = new Transform3D();
        xt3d.setRotation(new AxisAngle4d(MyCanvas3D.z_axis, -Math.PI/2));
        TransformGroup xTG = new TransformGroup(xt3d);
        xTG.addChild(xAxis);
        topTG.addChild(xTG);
        
        Transform3D zt3d = new Transform3D();
        zt3d.setRotation(new AxisAngle4d(MyCanvas3D.x_axis, Math.PI/2));
        TransformGroup zTG = new TransformGroup(zt3d);
        zTG.addChild(zAxis);
        topTG.addChild(zTG);
        
        this.addChild(topTG);
    }
}
