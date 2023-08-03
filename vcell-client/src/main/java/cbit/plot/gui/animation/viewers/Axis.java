
package cbit.plot.gui.animation.viewers;

//import com.sun.j3d.utils.geometry.Cone;
//import com.sun.j3d.utils.geometry.Cylinder;
import cbit.plot.gui.animation.helpersetup.Colors;
import com.sun.j3d.utils.geometry.Cone;
import com.sun.j3d.utils.geometry.Cylinder;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3d;
import java.util.ArrayList;

public class Axis extends TransformGroup {
    
    private Color3f color;
    // Extends in + and - directions, so total length is 2X this length
    private float length;
    private float thickness;
    
    private float tickSpacing;
    private float tickMarkSize;
    
    private Appearance appearance;
    private BranchGroup cylinder;
    private BranchGroup cone;
    private final ArrayList<BranchGroup> ticks = new ArrayList<>();
    
    public Axis(Color3f color, float length){
        super();
        this.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
        this.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
        this.color = color;
        this.length = length;
        tickSpacing = 1f;
        thickness = 0.1f;
        tickMarkSize = 0.2f;
        initialize();
    }
    
    /* ********** SET AND GET THE VARIOUS OPTIONS *************************/
    public void setColor(Color3f color){
        this.color = color;
        appearance.setMaterial(new Material(color, Colors.BLACK3D, color, Colors.BLACK3D, 40.0f));
    }
    
    public Color3f getColor(){
        return color;
    }
    
    public void setColorByName(String name){
        color = Colors.getColor3fByName(name);
        appearance.setMaterial(new Material(color, Colors.BLACK3D, color, Colors.BLACK3D, 40.0f));
    }
    
    public String getColorName(){
        return Colors.getNameOfColor3f(color);
    }
    
    public void setLength(float length){
        this.length = length;
        buildCylinder();
        buildTicks();
    }
    
    public float getLength(){
        return length;
    }
    
    public void setThickness(float thickness){
        this.thickness = thickness;
        buildCylinder();
    }
    
    public float getThickness(){
        return thickness;
    }
    
    public void setTickMarkSize(float size){
        this.tickMarkSize = size;
        buildTicks();
    }
    
    public float getTickMarkSize(){
        return tickMarkSize;
    }
    
    public void setTickSpacing(float spacing){
        this.tickSpacing = spacing;
        buildTicks();
    }
    
    public float getTickSpacing(){
        return tickSpacing;
    }

    
    
    
    /* **************  INITIALIZE COMPONENTS ***************************/
    private void initialize(){
        appearance = new Appearance();
        appearance.setMaterial(new Material(color, Colors.BLACK3D, color, Colors.BLACK3D, 40.0f));
        appearance.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        
        buildTicks();
        buildCylinder();
    }
    
    private void clearTicks(){
        for(BranchGroup tick : ticks){
            this.removeChild(tick);
        }
        ticks.clear();
        this.removeChild(cone);
    }
    
    private void buildTicks(){
        clearTicks();
        for(float i=-length;i<=length;i+=tickSpacing){
            Vector3d shift = new Vector3d(0,i,0);
            Transform3D t3d = new Transform3D();
            t3d.setTranslation(shift);
            TransformGroup tickTG = new TransformGroup(t3d);
            Cylinder cyl = new Cylinder(tickMarkSize, 0.1f, appearance);
            cyl.setCapability(Cylinder.ENABLE_APPEARANCE_MODIFY);
            tickTG.addChild(cyl);
            BranchGroup tick = new BranchGroup();
            tick.setCapability(BranchGroup.ALLOW_DETACH);
            tick.addChild(tickTG);
            ticks.add(tick);
            this.addChild(tick);
        }
        Vector3d coneShift = new Vector3d(0,length + tickSpacing/2 + 0.05f*length/2,0);
        Transform3D coneT3D = new Transform3D();
        coneT3D.setTranslation(coneShift);
        TransformGroup coneTG = new TransformGroup(coneT3D);
        coneTG.addChild(new Cone(tickMarkSize,0.05f*length,appearance));
        cone = new BranchGroup();
        cone.setCapability(BranchGroup.ALLOW_DETACH);
        cone.addChild(coneTG);
        this.addChild(cone);
    }
    
    /* **************  BUILD THE CYLINDER ******************************/
    private void buildCylinder(){
        this.removeChild(cylinder);
        cylinder = new BranchGroup();
        cylinder.addChild(new Cylinder(thickness, 2*length + tickSpacing, appearance));
        cylinder.setCapability(BranchGroup.ALLOW_DETACH);
        this.addChild(cylinder);
    }
}
