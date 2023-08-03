
package cbit.plot.gui.animation.viewers;

import com.sun.j3d.utils.geometry.Box;
import cbit.plot.gui.animation.helpersetup.Colors;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Vector3f;

public class Membrane extends BranchGroup {
    
    private Color3f innerColor;
    private Color3f outerColor;
    
    private float innerShininess;
    private float outerShininess;
    
    private Appearance innerAppearance;
    private Appearance outerAppearance;
    
    private final float x;  // one half the total x length
    private final float y; // one hald the total y length
    
    public Membrane(float x, float y){
        this.setCapability(BranchGroup.ALLOW_DETACH);
        this.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        this.x = x;
        this.y = y;
        innerColor = Colors.YELLOW3D;
        outerColor = Colors.CYAN3D;
        innerShininess = 50.0f;
        outerShininess = 50.0f;
        buildMembrane();
    }
    
    /* ********************  BUILD THE MEMBRANE ****************************/
    private void buildMembrane(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        float zmem = 0.1f;
        innerAppearance = new Appearance();
        innerAppearance.setMaterial(makeMaterial(innerColor, innerShininess));
        innerAppearance.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
        
        outerAppearance = new Appearance();
        outerAppearance.setMaterial(makeMaterial(outerColor, outerShininess));
        outerAppearance.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
    
        Box box = new Box(x, y, zmem, outerAppearance);
        box.getShape(Box.FRONT).setAppearance(innerAppearance);
        box.setCapability(Box.ENABLE_APPEARANCE_MODIFY);
        
        // Make box transformgroup
        Transform3D boxt3d = new Transform3D();
        boxt3d.setTranslation(new Vector3f(0f,0f,-zmem));
        TransformGroup boxTG = new TransformGroup(boxt3d);
        
        boxTG.addChild(box);
        this.addChild(boxTG);
        // </editor-fold>
    }
    
    /* ****************  RETURN A COLORED MATERIAL ************************/
    private Material makeMaterial(Color3f color, float s){
        return new Material(color, Colors.BLACK3D, color, Colors.BLACK3D, s);
    }
    
    /* **************** INNER COLOR METHODS ***************************/
    
    public void setInnerColor(Color3f color){
        innerColor = color;
        innerAppearance.setMaterial(makeMaterial(innerColor, innerShininess));
    }
    
    public void setInnerColor(String name){
        innerColor = Colors.getColor3fByName(name);
        innerAppearance.setMaterial(makeMaterial(innerColor, innerShininess));
    }
    
    public Color3f getInnerColor(){
        return innerColor;
    }
    
    public String getInnerColorName(){
        return Colors.getNameOfColor3f(innerColor);
    }
    
    /* **************** OUTER COLOR METHODS ***************************/
    
    public void setOuterColor(Color3f color){
        outerColor = color;
        outerAppearance.setMaterial(makeMaterial(outerColor, outerShininess));
    }
    
    public void setOuterColor(String name){
        outerColor = Colors.getColor3fByName(name);
        outerAppearance.setMaterial(makeMaterial(outerColor, outerShininess));
    }
    
    public Color3f getOuterColor(){
        return outerColor;
    }
    
    public String getOuterColorName(){
        return Colors.getNameOfColor3f(outerColor);
    }
    
    /* ************** GET AND SET SHININESS ***************************/
    
    public void setInnerShininess(float s){
        if(s < 1){
            s = 1;
        }
        if(s > 128){
            s = 128;
        }
        innerShininess = s;
        innerAppearance.setMaterial(makeMaterial(innerColor, innerShininess));
    }
    
    public float getInnerShininess(){
        return innerShininess;
    }
    
    public void setOuterShininess(float s){
        if(s < 1){
            s = 1;
        }
        if(s > 128){
            s = 128;
        }
        outerShininess = s;
        outerAppearance.setMaterial(makeMaterial(outerColor, outerShininess));
    }
    
    public float getOuterShininess(){
        return outerShininess;
    }
    
}
