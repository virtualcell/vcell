/**
 *  The scene is a precompiled collection of spheres and cylinders representing
 * the positions of the sites and links, respectively, in the system at a 
 * single time point. The viewer attaches and detaches Scenes to display 
 * the system at various time points. (I have no idea how efficient this will
 * be.  I might have to change this approach later.)
 * 
 * I'm running out of memory when I have very large simulations. For these, 
 * it makes more sense to store the input file as a string and then use that
 * string to build the scenegraph on the fly.
 */

package cbit.plot.gui.animation.viewers;


import cbit.plot.gui.animation.helpersetup.Colors;
import cbit.plot.gui.animation.helpersetup.IOHelp;

import java.util.HashMap;
import java.util.Scanner;
import java.io.*;

import com.sun.j3d.utils.geometry.*;

import java.util.InputMismatchException;
import javax.media.j3d.*;
import javax.vecmath.*;

public class Scene {

    private final BranchGroup bg = new BranchGroup();
    
    // A map between the site id and the sphere representing that site
    private final HashMap<Integer, double[]> positions = new HashMap<>();
    
    // String with time and time unit
    private String timeStamp;
    
    // A string with all of the information from the file used to create this scene.
    private String sceneInfo = "";
    
    public Scene(){
        bg.setCapability(BranchGroup.ALLOW_DETACH);
    }
    
    public void setTimeStamp(String timeStamp){
        this.timeStamp = timeStamp;
    }
    
    public String getTimeStamp(){
        return timeStamp;
    }
    
    public BranchGroup getBranchGroup(){
        return bg;
    }
    
    private TransformGroup makeSphere(double radius, String color, double x, double y, double z){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        TransformGroup tg = new TransformGroup();
        Transform3D t3d = new Transform3D();
        Appearance app = new Appearance();
        Color3f col = Colors.COLORMAP3D.get(color);
        Material mat = new Material(col, Colors.BLACK3D, col, Colors.BLACK3D, 50.0f);
        
        Vector3f vec = new Vector3f((float)x,(float)y,(float)z);
        Sphere sphere = new Sphere((float)radius);
        
        app.setMaterial(mat);
        sphere.setAppearance(app);
        
        t3d.setTranslation(vec);
        tg.setTransform(t3d);
        
        tg.addChild(sphere);
        return tg;
        // </editor-fold>
    }
    
    private TransformGroup makeLink(double [] pos1, double [] pos2){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        /**
         * For some reason that I don't care to determine, the transforms 
         * defined below do not work if either the two x positions are identical
         * or the two z positions are identical, and in these cases  
         * the system chokes on a BadTransformException.  To avoid this, I'll
         * check to see if they are identical, and if they are, I'll shift pos2
         * by 1e-5 nm, which is a completely negligible distance for the systems
         * under consideration.
         */
        Color3f linkColor = Colors.GRAY3D;
        // This array let me see what links needed to be shifted.
        // Color3f [] shiftColor = {MyCanvas3D.red, MyCanvas3D.green, MyCanvas3D.cyan};
        for(int i=0;i<3;i++){
            if(Math.abs(pos1[i]- pos2[i]) < 5e-6){
                pos2[i] += 1e-5;
                // linkColor = shiftColor[i];
            }
        }
        TransformGroup tg = new TransformGroup();
        Transform3D t3d = new Transform3D();
        Appearance app = new Appearance();
        Material mat = new Material(linkColor, Colors.BLACK3D, linkColor,
                                    Colors.BLACK3D, 50.0f);
        app.setMaterial(mat);
        
        Cylinder cyl = new Cylinder(0.2f, 1f, app);
        
        Vector3d vec1 = new Vector3d((float)pos1[0], (float)pos1[1], (float)pos1[2]);
        Vector3d vec2 = new Vector3d((float)pos2[0], (float)pos2[1], (float)pos2[2]);
        // Now get the orientation
        Vector3d orientation = new Vector3d();
        orientation.sub(vec2, vec1);
        
        // I'll need the angles between the orientation and the y and z axes
        Vector3d projectXZ = new Vector3d();
        projectXZ.x = orientation.x;
        projectXZ.z = orientation.z;
        
        double theta = projectXZ.angle(MyCanvas3D.z_axis);
        if(orientation.x < 0){
            theta = 2.0*Math.PI - theta;
        }
        
        double phi = orientation.angle(MyCanvas3D.y_axis);
        
        t3d.setIdentity();
        
        Transform3D rotation_y = new Transform3D();
        Transform3D rotation_z = new Transform3D();
        Transform3D translation = new Transform3D();
        Transform3D translateUp = new Transform3D();
        Transform3D scale = new Transform3D();
        Vector3d shiftUp = new Vector3d(0,0,0);
        Vector3d translationVec = new Vector3d(0,0,0);
        
        rotation_y.setRotation(new AxisAngle4d(MyCanvas3D.y_axis, theta));
        rotation_z.setRotation(new AxisAngle4d(MyCanvas3D.x_axis, phi));
        shiftUp.y = orientation.length()/2;
        translateUp.setTranslation(shiftUp);
        translationVec = vec1;
        translation.setTranslation(translationVec);
        scale.setScale(new Vector3d(1,orientation.length(), 1));
        
        t3d.mul(translation);
        t3d.mul(rotation_y);
        t3d.mul(rotation_z);
        t3d.mul(translateUp);
        t3d.mul(scale);
        try{
            tg.setTransform(t3d);
        } catch(BadTransformException e){
            System.out.println("Choked when setting transform.");
            System.out.println("Translation vector was " + translationVec.toString());
            System.out.println("Shift up was " + shiftUp.toString());
            System.out.println("pos1 = " + IOHelp.printArray(pos1) + ", pos2 = " + IOHelp.printArray(pos2));
        }
        
        tg.addChild(cyl);
        
        return tg;
        // </editor-fold>
    }

    public void readSceneString(String sceneString, boolean lowMem){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        // System.out.println("Got into readFile.");
        Scanner sc = null;
        StringBuilder sb = new StringBuilder();

        sc = new Scanner(sceneString);
        Scanner scline;
        TransformGroup tg = null;
 
        scline = new Scanner(sc.nextLine());
        // Skip SceneNumber, i, CurrentTime
        scline.next();scline.next();scline.next();
        timeStamp = scline.next();
        scline.close();

        while(sc.hasNextLine()){
            Integer id = null;
            String nextLine = sc.nextLine();
            sb.append(nextLine).append("\n");
            // Only build the scenegraph if we are not conserving memory
            if(!lowMem){
                scline = new Scanner(nextLine);
                String leader = scline.next();
                if(leader.equals("ID")){
                    // System.out.println("Reading id.");
                    try{
                        id = new Integer(scline.nextInt());
                    } catch(NumberFormatException e){
                        System.out.println("Choked on the id.");
                    }

                    double r = scline.nextDouble();
                    String col = scline.next();
                    double x = scline.nextDouble();
                    double y = scline.nextDouble();
                    double z = scline.nextDouble();
                    positions.put(id, new double[]{x,y,z});
                    tg = makeSphere(r,col,x,y,z);
                    
                } else {
                    // System.out.println("Reading link.");
                    // String dd1 = scline.next();
                    // System.out.println("got to dd1");
                    // System.out.println(dd1);
                    Integer id1 = null;
                    try{
                        id1 = new Integer(scline.nextInt());
                    } catch(NumberFormatException e){
                        System.out.println("Couldn't read id1.");
                    }   
                    scline.next();
                    Integer id2 = new Integer(scline.nextInt());
                    try{
                        double [] pos1 = positions.get(id1);
                        double [] pos2 = positions.get(id2);
                        tg = makeLink(pos1,pos2);
                    } catch(NullPointerException nfe){
                        System.out.println("Hit null pointer. Id1 = " + id1 + ", id2 = " + id2);
                        tg = null;
                    }
                }

                bg.addChild(new BranchGroup());
                scline.close();
            }
        }

        sceneInfo = sb.toString();
        
        // </editor-fold>
    }
    
    public void destroySceneGraph(){
        bg.removeAllChildren();
    }
    
    public void buildSceneGraph(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Scanner sc = new Scanner(sceneInfo);
        Scanner scline;
        TransformGroup tg;

        while(sc.hasNextLine()){
            Integer id = null;
            scline = new Scanner(sc.nextLine());
            if(scline.next().equals("ID")){
                // System.out.println("Reading id.");
                try{
                    id = new Integer(scline.nextInt());
                } catch(NumberFormatException e){
                    System.out.println("Choked on the id.");
                }
                double r = scline.nextDouble();
                String col = scline.next();
                double x = scline.nextDouble();
                double y = scline.nextDouble();
                double z = scline.nextDouble();
                positions.put(id, new double[]{x,y,z});
                tg = makeSphere(r,col,x,y,z);
            } else {
                // System.out.println("Reading link.");
                // String dd1 = scline.next();
                // System.out.println("got to dd1");
                // System.out.println(dd1);
                Integer id1 = null;
                try{
                    id1 = new Integer(scline.nextInt());
                } catch(NumberFormatException e){
                    System.out.println("Couldn't read id1.");
                }   
                scline.next();
                Integer id2 = new Integer(scline.nextInt());
                double [] pos1 = positions.get(id1);
                double [] pos2 = positions.get(id2);
                tg = makeLink(pos1,pos2);
            }
            scline.close();
            bg.addChild(tg);
        }
        sc.close();
        // </editor-fold>
    }

}
