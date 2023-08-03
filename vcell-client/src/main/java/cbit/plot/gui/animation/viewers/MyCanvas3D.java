/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cbit.plot.gui.animation.viewers;

import com.sun.j3d.utils.universe.*;
import com.sun.j3d.utils.behaviors.vp.*;
import com.sun.j3d.utils.geometry.*;

import javax.media.j3d.*;
import javax.vecmath.*;

import cbit.plot.gui.animation.helpersetup.Colors;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

class MyCanvas3D extends Canvas3D {

    // We need a simple universe, a branchgroup, and a canvas3d
    SimpleUniverse su;
    BranchGroup rootBG;
    BoundingSphere bounds;
    // Following MUST be static or else it can't be used by the super constructor!
    static GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();

    // Reference to this component's off screen canvas3d
    private Canvas3D offScreenCanvas;
    private ImageComponent2D image2d;

    // Will help to have some predefined color3f objects
    public static Color3f white_dim = new Color3f(0.2f, 0.2f, 0.2f);

    // Predefined vectors
    public static Vector3d x_axis = new Vector3d(1.0,0,0);
    public static Vector3d y_axis = new Vector3d(0,1.0,0);
    public static Vector3d z_axis = new Vector3d(0,0,1.0);

    // Bounds
    private float xsize; // one half of the total membrane size
    private float ysize; // one half of the total membrane size
    private float zsize; // Don't actually use this variable.  Might not need it.

    // Info for time stamp
    private boolean showStamp = false;
    private String timeStamp = " ";
    private final Font stampFont = new Font(Font.SANS_SERIF, Font.PLAIN, 24);

    // Axes
    private final Axes axes;

    // Membrane
    private Membrane membrane = null;

    // Constructor
    public MyCanvas3D(){
        super(config);
        this.setPreferredSize(new Dimension(700,500));

        su = new SimpleUniverse(this);
        rootBG = new BranchGroup();
        rootBG.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        rootBG.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);

        axes = new Axes();

        // View properties
        View view = this.getView();
        view.setFrontClipDistance(0.001);
        view.setBackClipDistance(4000);

        // Set the bounding sphere
        bounds = new BoundingSphere(new Point3d(0,0,0), 10000f);

        // Make a directional white light
        Vector3f lightDirection = new Vector3f(-1.0f, -2.0f, -1.0f);
        DirectionalLight light1 = new DirectionalLight(Colors.WHITE3D, lightDirection);
        light1.setInfluencingBounds(bounds);
        rootBG.addChild(light1);

        // Make ambient dim light
        AmbientLight ambientLight = new AmbientLight(white_dim);
        ambientLight.setInfluencingBounds(bounds);
        rootBG.addChild(ambientLight);

        // TODO: set initial position, add orbit controls
        setInitialPosition(new Point3d(-150, 50, 150));
        setOrbitControls();

        su.addBranchGraph(rootBG);

    }

    public void showStamp(boolean bool){
        showStamp = bool;
        this.update(this.getGraphics());
    }

    public void setupOffScreenCanvas(){
        View view = this.getView();
        offScreenCanvas = new Canvas3D(config, true);
        offScreenCanvas.getScreen3D().setSize(this.getPreferredSize());
        offScreenCanvas.getScreen3D().setPhysicalScreenHeight( this.getPhysicalHeight());
        offScreenCanvas.getScreen3D().setPhysicalScreenWidth( this.getPhysicalWidth());

        RenderedImage renderedImage = new BufferedImage(this.getPreferredSize().width, this.getPreferredSize().height, BufferedImage.TYPE_3BYTE_BGR);
        image2d = new ImageComponent2D(ImageComponent.FORMAT_RGB8, renderedImage);
        image2d.setCapability(ImageComponent2D.ALLOW_IMAGE_READ);

        offScreenCanvas.setOffScreenBuffer(image2d);
        view.addCanvas3D(offScreenCanvas);
    }

    private void setInitialPosition(Point3d point){
        ViewingPlatform vp = su.getViewingPlatform();
        TransformGroup vpTG = vp.getViewPlatformTransform();

        Transform3D vpt3d = new Transform3D();
        vpTG.getTransform(vpt3d);

        vpt3d.lookAt(point, new Point3d(0,0,0), new Vector3d(0,1,0));
        vpt3d.invert();
        vpTG.setTransform(vpt3d);
    }

    private void setOrbitControls(){
        OrbitBehavior orbit = new OrbitBehavior(this, OrbitBehavior.REVERSE_ROTATE | OrbitBehavior.PROPORTIONAL_ZOOM | OrbitBehavior.STOP_ZOOM | OrbitBehavior.REVERSE_TRANSLATE);
        orbit.setSchedulingBounds(bounds);
        orbit.setMinRadius(2.0);
        orbit.setTransFactors(100.0, 100.0);
        su.getViewingPlatform().setViewPlatformBehavior(orbit);
    }

    public void setBounds(float xsize, float ysize, float zsize){
        this.xsize = xsize;
        this.ysize = ysize;
        this.zsize = zsize;
    }

    public void addScene(Scene scene){
        rootBG.addChild(scene.getBranchGroup());
        timeStamp = scene.getTimeStamp();
    }

    public void removeScene(Scene scene){
        rootBG.removeChild(scene.getBranchGroup());
        timeStamp = "";
    }

    public void writeImage(File file, String extension)
            throws IOException{

        offScreenCanvas.renderOffScreenBuffer();
        offScreenCanvas.waitForOffScreenRendering();

        ImageIO.write(image2d.getImage(), extension, file);

    }

    public BufferedImage makeBufferedImage() {

        offScreenCanvas.renderOffScreenBuffer();
        offScreenCanvas.waitForOffScreenRendering();
        BufferedImage img = image2d.getImage();
        if(showStamp){

            Graphics2D g2 = img.createGraphics();
            g2.setColor(Color.white);
            g2.setFont(stampFont);
            if(timeStamp.length() == 0){
                timeStamp = "   ";
            }
            g2.drawString(timeStamp, 20, this.getHeight()-20);
        }
        return img;
    }


    @Override
    public void postRender(){
        // Set up the Graphics2D object for text overlay
        if(showStamp){
            Graphics2D g2 = this.getGraphics2D();
            g2.setColor(Color.white);
            g2.setFont(stampFont);
            if(timeStamp.length() == 0){
                timeStamp = "   ";
            }
            g2.drawString(timeStamp, 20, this.getHeight()-20);
            this.getGraphics2D().flush(false);
        }
    }

    /* ****************  ADD AND REMOVE AXES *******************************/
    public void showAxes(boolean bool){
        if(bool){
            rootBG.addChild(axes);
        } else {
            rootBG.removeChild(axes);
        }
    }

    public Axes getAxes(){
        return axes;
    }

    /* ******************** MEMBRANE METHODS *******************************/

    public void showMembrane(boolean bool){
        if(bool){
            rootBG.addChild(membrane);
        } else {
            rootBG.removeChild(membrane);
        }
    }

    public Membrane getMembrane(){
        return membrane;
    }

    public void makeMembrane(){
        if(membrane == null){
            membrane = new Membrane(xsize, ysize);
        }
    }

}
