
package cbit.plot.gui.animation.viewers;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.InputMismatchException;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.event.*;

import cbit.plot.gui.animation.helpersetup.Fonts;
import cbit.plot.gui.animation.helpersetup.IOHelp;
import cbit.plot.gui.animation.helpersetup.PopUp;
//import cbit.plot.gui.animation.helpersetup.Simulation;

public class ViewerGUI extends JFrame implements ActionListener,
        ChangeListener, PropertyChangeListener, ItemListener {

    /* **************  MENU BAR COMPONENTS ********************************/
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu optionsMenu;
    private JMenu saveImageMenu;
    private JMenu saveVideoMenu;
    private JMenu axesMenu;
    private JMenu membraneMenu;
    private JMenuItem closeItem;
    private JMenuItem pngItem;
    private JMenuItem jpegItem;
    private JMenuItem gifItem;
    private JMenuItem aviItem;
    private JMenuItem quicktimeItem;
    private JCheckBoxMenuItem timeStampItem;
    private JCheckBoxMenuItem loadToBufferItem;

    private JCheckBoxMenuItem showAxesItem;
    private JMenuItem axesOptionsItem;

    private JCheckBoxMenuItem showMembraneItem;
    private JMenuItem membraneOptionsItem;

    /* ************** CONTROL PANEL COMPONENTS *****************************/
    private JToggleButton loopButton;
    private JButton backButton;
    private JButton forwardButton;
    private JButton playButton;
    private JSpinner fpsSpinner;

    /* **************  PANEL TO HOLD THE 3D CANVAS *************************/

    private ViewerPanel viewPanel;

    /* **************  JSLIDER AND SYSTEM TIME TEXTFIELD ********************/
    private JSlider slider;
    private JLabel timeLabel;
    private JTextField timeTF;

    /* ************* COMBOBOX TO CHOOSE CERTAIN RUNS ************************/
    private JComboBox runBox;

    /* **************  ARRAY LIST OF 3D SCENES TO SHOW **********************/
    private ArrayList<Scene> scenes = new ArrayList<>();

    /* ************** THE SIMULATION WE'RE SHOWING **************************/
    //private final Simulation simulation;

    /* *************  TIMER AND RELATED FIELDS ******************************/
    private Timer timer;
    private int fps;
    private boolean loopVideo;

    /* ************  FOLDER HOLDING VIEWER FILES ****************************/
    private File viewerFolder;
    private File [] viewerFile;

    /* *********** FOLDERS TO SAVE IMAGES AND VIDEOS ************************/
    private File imageFolder;
    private File videoFolder;

    /* ************ BOOLEAN INDICATING TO USE REDUCED MEMORY ****************/
    private boolean reduceMemory = true;

    /* *********** BOOLEAN INDICATING TO SHOW TIME STAMPS *******************/
    private boolean addTimeStamp = true;

    /* ************ FIELDS USED TO SET THE UNITS AND SELECTED TIMES *********/
    // Scale factor to convert from seconds to the more natural unit of the
    // problem, either s, ms, us, or ns.
    private int unitScale;
    private String units;
    private double dtimage;
    private double selectedTime;

    /* ******  INFORMATION ABOUT THE SLIDER *******************************/
    private int currentValue; // Current value of the slider
    private int ticks = 0;  // Total ticks on the slider
    // Actual number of files loaded.  To be compared to the ticks to see if
    // any files we expected are missing.
    private int totalScenes = 0;

    /* ******** THE IMAGELOADER (DEFINED INNER CLASS) **********************/
    private ImageLoader loader = null;

    /* ******** PROGRESS WINDOW AND RELATED FIELDS *************************/

    private ProgressWindow progressWindow;
    private boolean doneLoading = false;
    private int filesLoaded = 0;

    /* *************  GLOBAL FILE NAME *************************************/
    private final String title;

    /* ***************** CONSTRUCTOR **************************************/

    public ViewerGUI(String title/*, Simulation simulation*/){
        super();
        this.title = title;
        //this.simulation = simulation;
        System.out.println("ViewergUI");
        buildMenuBar();
        initializeTimer();
        loadViewerFiles();

        Container c = this.getContentPane();
        c.add(buildRunBox(), "North");
        c.add(buildViewAndSlider(), "Center");
        c.add(buildControlPanel(), "South");

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.pack();
        this.setVisible(false);

    }

    /* **************** CONSTRUCT MENU BAR *******************************/

    private void buildMenuBar(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        menuBar = new JMenuBar();

        fileMenu = new JMenu("File");
        saveImageMenu = new JMenu("Export Image");
        saveVideoMenu = new JMenu("Export Video");
        optionsMenu = new JMenu("Options");
        axesMenu = new JMenu("Axes");
        membraneMenu = new JMenu("Membrane");

        pngItem = new JMenuItem("PNG");
        jpegItem = new JMenuItem("JPEG");
        gifItem = new JMenuItem("GIF");
        aviItem = new JMenuItem("AVI");
        quicktimeItem = new JMenuItem("QuickTime");
        closeItem = new JMenuItem("Close Viewer");
        axesOptionsItem = new JMenuItem("Axes Options");
        membraneOptionsItem = new JMenuItem("Membrane Options");

        timeStampItem = new JCheckBoxMenuItem("Show Time Stamps");
        timeStampItem.setSelected(addTimeStamp);
        loadToBufferItem = new JCheckBoxMenuItem("Load Scenes To Buffer");
        loadToBufferItem.setSelected(!reduceMemory);
        showAxesItem = new JCheckBoxMenuItem("Show Axes");
        showAxesItem.setSelected(true);
        showMembraneItem = new JCheckBoxMenuItem("Show Membrane");
        showMembraneItem.setSelected(true);

        pngItem.addActionListener(this);
        jpegItem.addActionListener(this);
        gifItem.addActionListener(this);
        aviItem.addActionListener(this);
        quicktimeItem.addActionListener(this);
        closeItem.addActionListener(this);
        axesOptionsItem.addActionListener(this);
        membraneOptionsItem.addActionListener(this);

        timeStampItem.addItemListener(this);
        loadToBufferItem.addItemListener(this);
        showAxesItem.addItemListener(this);
        showMembraneItem.addItemListener(this);

        saveImageMenu.add(pngItem);
        saveImageMenu.add(jpegItem);
        saveImageMenu.add(gifItem);
        saveVideoMenu.add(aviItem);
        saveVideoMenu.add(quicktimeItem);

        fileMenu.add(saveImageMenu);
        fileMenu.add(saveVideoMenu);
        fileMenu.add(closeItem);

        axesMenu.add(showAxesItem);
        axesMenu.add(axesOptionsItem);

        membraneMenu.add(showMembraneItem);
        membraneMenu.add(membraneOptionsItem);

        optionsMenu.add(timeStampItem);
        optionsMenu.add(loadToBufferItem);
        optionsMenu.add(axesMenu);
        optionsMenu.add(membraneMenu);

        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);

        this.setJMenuBar(menuBar);
        // </editor-fold>
    }

    /* *************** CONSTRUCT CONTROL PANEL ***************************/

    private JPanel buildControlPanel(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        loopButton = new JToggleButton("Loop");
        loopButton.setSelected(false);

        backButton = new JButton("-");
        forwardButton = new JButton("+");
        playButton = new JButton("PLAY");
        playButton.setPreferredSize(new Dimension(100,50));

        loopButton.addActionListener(this);
        backButton.addActionListener(this);
        forwardButton.addActionListener(this);
        playButton.addActionListener(this);

        fpsSpinner = new JSpinner();
        fpsSpinner.setModel(new SpinnerNumberModel(1,1,60,1));
        fpsSpinner.setValue(fps);
        fpsSpinner.addChangeListener(this);

        JPanel p = new JPanel();
        p.add(loopButton);
        p.add(backButton);
        p.add(playButton);
        p.add(forwardButton);
        p.add(new JLabel("FPS", JLabel.RIGHT));
        p.add(fpsSpinner);

        return p;
        // </editor-fold>
    }

    /* ************** BUILD VIEW PANEL AND SLIDER PANEL *******************/

    private JPanel buildViewAndSlider(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        viewPanel = new ViewerPanel();
        timeTF = new JTextField("", 10);
        timeTF.setEnabled(false);
        timeLabel = new JLabel("Time: ", JLabel.RIGHT);

        slider = new JSlider();
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);
        slider.setPreferredSize(new Dimension(viewPanel.getPreferredSize().width,
                slider.getPreferredSize().height + 40));
        slider.addChangeListener(this);

        JPanel lowerPanel = new JPanel();
        lowerPanel.add(timeLabel);
        lowerPanel.add(timeTF);
        lowerPanel.add(slider);
        int height = timeTF.getPreferredSize().height + slider.getPreferredSize().height + 10;
        lowerPanel.setPreferredSize(new Dimension(slider.getPreferredSize().width + 10, height));

        JPanel lowerResizablePanel = new JPanel();
        lowerResizablePanel.add(lowerPanel);

        JPanel upperResizablePanel = new JPanel();
        upperResizablePanel.add(viewPanel);

        JPanel p = new JPanel();
        p.setLayout(new BorderLayout());
        p.add(upperResizablePanel, "North");
        p.add(lowerResizablePanel, "South");

        return p;
        // </editor-fold>
    }

    /* ************** BUILD COMBOBOX PANEL ********************************/
    private JPanel buildRunBox(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">

        Integer [] simFile = new Integer[viewerFile.length];
        for(int i=0;i<simFile.length;i++){
            simFile[i] = i;
        }
        runBox = new JComboBox(simFile);
        runBox.setSelectedIndex(0);
        runBox.setFont(Fonts.SUBTITLEFONT);
        runBox.addItemListener(this);

        JLabel label0 = new JLabel("Simulation: " /*simulation.getSimulationName()*/, JLabel.LEFT);
        label0.setFont(Fonts.SUBTITLEFONT);
        label0.setPreferredSize(new Dimension(label0.getPreferredSize().width + 100,
                label0.getPreferredSize().height));
        JLabel label1 = new JLabel("Viewing Run ", JLabel.RIGHT);
        label1.setFont(Fonts.SUBTITLEFONT);

        JPanel p0 = new JPanel();
        p0.add(label0);
        p0.add(label1);
        p0.add(runBox);

        JPanel p = new JPanel();
        p.add(p0);


        return p;
        // </editor-fold>
    }

    /* ************** INITIALIZE TIMER AND ASSOCIATED VALUES ***************/

    private void initializeTimer(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        fps = 10;
        loopVideo = false;
        timer = new Timer(1000/fps, this);
        timer.setInitialDelay(0);
        timer.stop();
        timer.setCoalesce(true);
        // </editor-fold>
    }

    /* ************** FIND VIEWER FILES ********************************/
    private void loadViewerFiles(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        File simFile = new File("C:\\Users\\sijav\\Desktop\\Animation\\Animation_SIMULATIONS\\Simulation0_SIM.txt");
        String name = simFile.getName();
        // Strip off .txt
        name = name.substring(0,name.length()-4);
        String parent = simFile.getParent();
        viewerFolder = new File(parent +  "/" + name + "_FOLDER/viewer_files");
        int totalViewerFiles = 0;
        // Loop twice, once to determine the total number of files, and a
        // a second time to set the files in the file array.
        for(File file : viewerFolder.listFiles()){
            String fileName = file.getName();
            if(fileName.contains("_VIEW_Run")){
                totalViewerFiles++;
            }
        }

        viewerFile = new File[totalViewerFiles];
        System.out.println("Total viewer files: " + totalViewerFiles);
        for(File file : viewerFolder.listFiles()){
            String fileName = file.getName();
            if(fileName.contains("_VIEW_Run")){
                Scanner sc = new Scanner(fileName);
                sc.useDelimiter("_VIEW_Run");
                sc.next();
                String end = sc.next();
                String [] endSplit = end.split("\\.");
                int i = Integer.parseInt(endSplit[0]);
                viewerFile[i] = file;
                sc.close();
            }
        }
        // </editor-fold>
    }

    /* ************** CONTROL ANIMATION ***********************************/

    private void stopAnimation(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        timer.stop();
        timer.setInitialDelay(0);
        playButton.setText("PLAY");
        forwardButton.setEnabled(true);
        backButton.setEnabled(true);
        // </editor-fold>
    }

    private void startAnimation(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        forwardButton.setEnabled(false);
        backButton.setEnabled(false);
        playButton.setText("STOP");
        if(currentValue == slider.getMaximum()){
            slider.setValue(0);
            timer.setInitialDelay(1000/fps);
        }
        timer.start();
        // </editor-fold>
    }

    /* ************** LOAD AND CLOSE FILES *******************************/

    private void closeFile(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        slider.removeChangeListener(this);
        slider.setPaintLabels(false);
        slider.setPaintTicks(false);
        timeTF.setText("");
        viewPanel.removeViewer();
        scenes.clear();
        // </editor-fold>
    }

    public void loadFile(int index) throws IOException {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        File file = viewerFile[index];
        String name = file.getName();

        // Set up image folder and videofolder
        File parent = new File(file.getParent());
        String parent2 = parent.getParent();
        imageFolder = new File(parent2, "images");
        videoFolder = new File(parent2, "videos");

//        // Build the path to the run folder
//        String path = file.getAbsolutePath();
//        // Strip off the file name itself
//        path = path.substring(0, path.length()-name.length());
//        // Now redirect to the folder of this run
//        path = path + "Run" + index;

//        String end = "_VIEW_Run" + index + ".txt";
//        name = name.substring(0, name.length()-end.length());

        BufferedReader br = null;
        try{
            br = new BufferedReader(new FileReader(file));
        } catch(FileNotFoundException e){
            e.printStackTrace(System.out);
        }

        Scanner sc = new Scanner(br);
        sc.useDelimiter("SCENE\\n");
        Scanner header = new Scanner(sc.next());
        header.next();
        double totalTime = Double.parseDouble(header.next());
        header.next();
        dtimage = Double.parseDouble(header.next());
        header.next();
        double dx = Double.parseDouble(header.next());
        header.next();
        double dy = Double.parseDouble(header.next());
        header.next();
        double dzout = Double.parseDouble(header.next());
        header.next();
        double dzin = Double.parseDouble(header.next());
        header.close();

        ArrayList<String> sceneStrings = new ArrayList<>();
        while(sc.hasNext()){
            sceneStrings.add(sc.next().trim());
        }
        sc.close();

        totalScenes = sceneStrings.size();

        if(dx>0 && dy>0 && dzin>0 && dzout>0 && totalTime>0 && dtimage>0){
            slider.setMinimum(0);
            // Figure out the appropriate scale for the slider
            unitScale = 1;
            while((int)Math.floor(unitScale*totalTime) < 1){
                unitScale *= 1000;
            }
            switch(unitScale){
                case 1:{
                    units = "s";
                    break;
                }
                case 1000:{
                    units = "ms";
                    break;
                }
                case 1000000:{
                    units = "us";
                    break;
                }
                case 1000000000:{
                    units = "ns";
                    break;
                }
                default:{
                    System.out.println("The time scale is smaller than nanoseconds.  Should change the 'scale' variable to a long.");
                }
            }
            timeLabel.setText("Time (" + units + ")");

            /**
             * The strange looking comparison below is needed because of the imprecision of doubles.  For example, with totalTime = 50e-9
             * and dtimage = 2e-9, I was getting ticks = 25, as expected.  But ticks*dtimage = 50.000000004e-9 (I don't recall exactly
             * how many zeroes there were), which is greater than totalTime = 50e-9.  By scaling everything up we can
             * do this check against integers.  (I wouldn't be shocked if there was a corner case where this fails, as I didn't check
             * many extreme input values.)
             * EDIT 2015-07-31: I found a case where this fails. For totalTime=1e-5 and dtimage=2e-7, I'm getting ticks=51
             * instead of 50.  Why not use round instead of ceil when calculating ticks?
             */
            ticks = (int)Math.round(totalTime/dtimage);
//            System.out.println("Ticks: " + ticks + ", dtimage: " + dtimage + ", UnitScale: " + unitScale);
            int c0 = (int)Math.floor(unitScale*ticks*dtimage);
            int c1 = (int)Math.floor(unitScale*totalTime);
            if(c0 > c1){
                ticks--;
            } else if(c0 < c1){
                ticks++;
            }
            slider.setMaximum(ticks);

            Hashtable<Integer,JLabel> labels = new Hashtable<>();
            if(ticks < 16){
                slider.setMajorTickSpacing(1);
                for(int i=0;i<=ticks;i++){
                    labels.put(i, new JLabel(IOHelp.DF[2].format(unitScale*(i*dtimage))));
                }
            } else {
                slider.setMinorTickSpacing(1);
                int tickSpace = (int)Math.ceil(ticks/10.0);
                // System.out.println("tickSpace = " + tickSpace);
                slider.setMajorTickSpacing(tickSpace);
                for(int i=0;i<11;i++){
                    // System.out.println("Integer value = " + i*tickSpace + ", double value = " + df.format(i*tickSpace*(unitScale*dtimage)));
                    labels.put(i*tickSpace, new JLabel(IOHelp.DF[2].format(unitScale*(i*tickSpace*dtimage))));
                }
            }

            slider.setLabelTable(labels);

            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setSnapToTicks(true);
            slider.setValue(0);
            selectedTime = 0;
            currentValue = 0;
            timeTF.setText("0.0 " + units);

            // We only add the listener after the slider is set
            slider.addChangeListener(this);

            viewPanel.makeViewer();
            viewPanel.setBounds((float)dx, (float)dy, (float)(dzout+dzin));
            viewPanel.addViewer(showMembraneItem.isSelected(), showAxesItem.isSelected());

//                File filePath = new File(path);
//                System.out.println(filePath.getPath() + " is a directory? " + filePath.isDirectory());
//                System.out.println(filePath.getPath() + " is an absolute directory? " + filePath.isAbsolute());
//                System.out.println("Number of files in this directory? " + Integer.toString(filePath.list().length));


            filesLoaded = 0;
            System.out.println("Should have set filesLoaded to 0.");
            doneLoading = false;
            // System.out.println("(ticks, totalViewerFiles) = (" + ticks + ", " + totalViewerFiles + ")");
            // expect ticks + 1 files, because files go from 0 to ticks
            progressWindow = new ProgressWindow(totalScenes);
            progressWindow.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosing(WindowEvent we){
                    if(progressWindow.wasCanceled()){
                        closeFile();
                    }
                }
            });
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            // For some reason, need to separately set the cursor for the slider
            slider.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//                System.out.println("path: " + path);
//                System.out.println("name: " + name);
            loader = new ImageLoader(sceneStrings);
            loader.addPropertyChangeListener(this);
            loader.execute();
        }

        // </editor-fold>
    }

    /*********************************************************************\
     *                      SAVE IMAGE                                   *
     * @param extension One of "jpg", "png", or "gif"                    *
    \*********************************************************************/

    private void writeImage(String extension){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        JFileChooser jfc = new JFileChooser(imageFolder);
        int ret = jfc.showSaveDialog(null);
        if(ret == JFileChooser.APPROVE_OPTION){

            File file = jfc.getSelectedFile();
            // Look to see if they added the filetype. If so, strip it off 
            // (because it might be wrong) and add it for them.
            String name = file.getAbsolutePath();
            if(name.length() > 4){
                String end = name.substring(name.length()-4);
                if(end.equals(".png") || end.equals(".jpg") || end.equals(".gif")){
                    name = name.substring(0,name.length()-4);
                }
            }
            name = name + "." + extension;

            file = new File(name);
            try{
                viewPanel.getViewer().writeImage(file, extension);
            } catch(IOException e){
                JOptionPane.showMessageDialog(this, "IO error message: " + e.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        // </editor-fold>
    }

    /*********************************************************************\
     *                    MAKE BUFFERED IMAGE ARRAY                      * 
     * @return An array of buffered images from the current perspective. *
    \*********************************************************************/

    private BufferedImage [] getBufferedImages(){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        MyCanvas3D v = viewPanel.getViewer();
        BufferedImage [] bi = new BufferedImage[scenes.size()];
        System.out.println("Building bufferd image array.");
        long starttime = System.currentTimeMillis();
        long oldtime = starttime;
        long currenttime;
        // We note the current value and we restore it at the end.
        if(!reduceMemory){
            v.removeScene(scenes.get(currentValue));
            for(int i=0;i<scenes.size();i++){
                v.addScene(scenes.get(i));
                bi[i] = v.makeBufferedImage();
                v.removeScene(scenes.get(i));
            }
            v.addScene(scenes.get(currentValue));
        } else {
            Scene scene = scenes.get(currentValue);
            v.removeScene(scene);
            scene.destroySceneGraph();
            for(int i=0;i<scenes.size();i++){
                scene = scenes.get(i);
                scene.buildSceneGraph();
                v.addScene(scene);
                bi[i] = v.makeBufferedImage();
                v.removeScene(scene);
                scene.destroySceneGraph();
                currenttime = System.currentTimeMillis();
                if(currenttime - oldtime > 2500){
                    oldtime = currenttime;
                    System.out.println("Constructed " + i + " buffered images in " + (currenttime-starttime)/1000.0 + " seconds.");
                }
            }
            scene = scenes.get(currentValue);
            scene.buildSceneGraph();
            v.addScene(scene);
        }

        return bi;
        // </editor-fold>
    }

    /**********************************************************************\
     *                          VIDEO IO                                  *
     * @param extension One of "gif", "avi", or "mov"                     *
    \**********************************************************************/

    private void writeVideo(String extension){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        if(scenes.size() > 0){
            JFileChooser jfc = new JFileChooser(videoFolder);
            int ret = jfc.showSaveDialog(null);
            if(ret == JFileChooser.APPROVE_OPTION){
                boolean failed;
                int fpsint = 24;
                do{
                    failed = false;
                    String fpsstring = JOptionPane.showInputDialog(this, "Frames per second: ", "1");
                    try{
                        fpsint = Integer.parseInt(fpsstring);
                        if(fpsint < 1){
                            failed = true;
                        }
                    } catch(NumberFormatException nfe){
                        failed = true;
                    }
                    if(failed){
                        PopUp.error("FPS must be a positive integer.");
                    }
                } while(failed);
                BufferedImage [] bi = this.getBufferedImages();
                File file = jfc.getSelectedFile();
                String absPath = file.getAbsolutePath();
                String fileName = file.getName();
                if(fileName.length() > 4){
                    String end = fileName.substring(fileName.length()-3);
                    if(!end.equals(extension)){
                        absPath = absPath + "." + extension;
                    }
                } else {
                    absPath = absPath + "." + extension;
                }
                file = new File(absPath);
                try{
                    switch (extension) {
                        case "gif":
                            MovieMaker.makeAnimagedGIF(file, bi, fpsint);
                            break;
                        case "avi":
                            MovieMaker.makeAVI(file, bi, fpsint);
                            break;
                        case "mov":
                            MovieMaker.makeQuicktime(file, bi, fpsint);
                            break;
                        default:
                            System.out.println("writeVideo() received unexpected input: " + extension );
                    }

                } catch(IOException ioe){
                    ioe.printStackTrace(System.out);
                }
            }
        } else {
            PopUp.error("No file loaded.");
        }
        // </editor-fold>
    }

    /* ************** ACTION LISTENER METHOD *****************************/

    @Override
    public void actionPerformed(ActionEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Object source = event.getSource();

        // <editor-fold defaultstate="collapsed" desc="Menu Items">
        if(source == closeItem){
            this.setVisible(false);
            this.dispose();
        }

        if(source == pngItem){
            writeImage("png");
        }

        if(source == jpegItem){
            writeImage("jpg");
        }

        if(source == gifItem){
            writeImage("gif");
        }

        if(source == aviItem){
            writeVideo("avi");
        }

        if(source == quicktimeItem){
            writeVideo("mov");
        }

        if(source == axesOptionsItem){
            AxesOptionsFrame aof = new AxesOptionsFrame(viewPanel.getViewer().getAxes());
        }

        if(source == membraneOptionsItem){
            MembraneOptionsFrame mof = new MembraneOptionsFrame(viewPanel.getViewer().getMembrane());
        }
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc="Buttons and Timer">
        if(source == timer){
            if(currentValue != slider.getMaximum()){
                slider.setValue(currentValue + 1);
            } else if(loopVideo){
                slider.setValue(0);
            } else {
                stopAnimation();
            }
        }

        if(source == forwardButton){
            int pos = slider.getValue();
            if(pos != slider.getMaximum()){
                slider.setValue(pos + 1);
            }
        }

        if(source == backButton){
            int pos = slider.getValue();
            if(pos != slider.getMinimum()){
                slider.setValue(pos - 1);
            }
        }

        if(source == playButton){
            if(playButton.getText().equals("PLAY")){
                startAnimation();
            } else {
                stopAnimation();
            }
        }

        if(source == loopButton){
            loopVideo = loopButton.isSelected();
        }


        // </editor-fold>

        // </editor-fold>
    }

    /* ************* CHANGE LISTENER METHOD *****************************/
    // For spinner
    @Override
    public void stateChanged(ChangeEvent e) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Object source = e.getSource();
        if(source == slider && !scenes.isEmpty()){
            // We don't want to do anything while the value is adjusting, I think
            if(!slider.getValueIsAdjusting()){
                MyCanvas3D v = viewPanel.getViewer();
                if(!reduceMemory){
                    v.removeScene(scenes.get(currentValue));
                    currentValue = slider.getValue();
                    v.addScene(scenes.get(currentValue));
                } else {
                    Scene scene = scenes.get(currentValue);
                    v.removeScene(scene);
                    scene.destroySceneGraph();
                    currentValue = slider.getValue();
                    scene = scenes.get(currentValue);
                    scene.buildSceneGraph();
                    v.addScene(scene);
                }
                selectedTime = dtimage*currentValue;
                timeTF.setText(IOHelp.DF[2].format(selectedTime*unitScale) + " " + units);
            } else if (timer.isRunning()){
                // When we grab the slider while it's playing, we want to stop the animation 
                stopAnimation();
            }
        }

        if(source == fpsSpinner){
            fps = (int)fpsSpinner.getValue();
            timer.setDelay(1000/fps);
        }
        // </editor-fold>
    }

    /* *********** PROPERTY CHANGE LISTENER METHOD **********************/
    // For slider
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        // <editor-fold defaultstate="collapsed" desc="Method Code">

        if(!doneLoading){
            int progress = loader.getProgress();
            progressWindow.setProgress(progress, filesLoaded);
        } else {
            progressWindow.finishedProgress();
            loader.removePropertyChangeListener(this);
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            slider.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            if(scenes.size() > 0){
                if(scenes.get(0) != null){
                    viewPanel.getViewer().addScene(scenes.get(0));
                    viewPanel.getViewer().setupOffScreenCanvas();
                    viewPanel.getViewer().showStamp(addTimeStamp);
                }
            }

            if(ticks +1 == totalScenes){
                PopUp.information("Successfully loaded files!");
            } else if(ticks +1 > totalScenes){
                PopUp.warning("Viewer found fewer files than expected based on "
                        + "the image dt and the total time.\n"
                        + "If the system is still running this message can be ignored.\n"
                        + "Otherwise, check to make sure no files are corrupted.");
            } else {
                PopUp.warning("The image data folder contained more files than"
                        + " expected based on the image dt and the total time.\n"
                        + "If you have added additional files to this folder"
                        + " (not recommeneded!) then this message can be ignored.\n"
                        + "Otherwise, the simulation generated more files than"
                        + " it should have and you should find the source of the error.");
            }
        }
        // </editor-fold>
    }

    /* ***********  ITEM LISTENER METHOD ********************************/
    // For time stamp check box and file combobox
    @Override
    public void itemStateChanged(ItemEvent event){
        // <editor-fold defaultstate="collapsed" desc="Method Code">
        Object source = event.getSource();

        if(source == timeStampItem){
            addTimeStamp = timeStampItem.isSelected();
            viewPanel.getViewer().showStamp(addTimeStamp);
        }

        if(source == loadToBufferItem){
            // <editor-fold defaultstate="collapsed" desc="Change Memory Usage Code">
            reduceMemory = !loadToBufferItem.isSelected();
            // Easiest thing to do is just close the file and then reopen it
            // with the new memory setting. This automatically gives me the
            // progress window that I want. 
            if(!scenes.isEmpty()){
                closeFile();
            }
            try{
                loadFile(runBox.getSelectedIndex());
            } catch(IOException ioe){
                ioe.printStackTrace(System.out);
            }
            // </editor-fold>
        }

        if(source == runBox){
            // <editor-fold defaultstate="collapsed" desc="Change Run Index">
            if(event.getStateChange()== ItemEvent.SELECTED){
                int index = runBox.getSelectedIndex();
                if(!scenes.isEmpty()){
                    closeFile();
                }
                try{
                    loadFile(index);
                } catch(IOException ioe){
                    ioe.printStackTrace(System.out);
                }
            }
            // </editor-fold>
        }

        if(source == showAxesItem){
            viewPanel.getViewer().showAxes(showAxesItem.isSelected());
        }

        if(source == showMembraneItem){
            viewPanel.getViewer().showMembrane(showMembraneItem.isSelected());
        }
        // </editor-fold>
    }

    /************************************************************************\
     *                           CLASS IMAGELOADER                          *
     * Loading the images was freezing the event thread or the main thread, *
     * (I'm not really sure), which caused the progress window to never     *
     * draw its components.  Apparently, the right way to do time consuming *
     * tasks is to use a SwingWorker to offload those tasks to a background *
     * thread.  SwingWorker is abstract and must be extended by a concrete  *
     * class.  We must override the doInBackground method and can override  *
     * some other methods.                                                  *
     \************************************************************************/

    class ImageLoader extends SwingWorker<ArrayList<Scene>, Void>{
        // <editor-fold defaultstate="collapsed" desc="Inner Class Code">
        int progress = 0;
        final ArrayList<Scene> tempScenes = new ArrayList<>();
        final ArrayList<String> sceneStrings;

        public ImageLoader(ArrayList<String> sceneStrings){
            this.sceneStrings = sceneStrings;
            // System.out.println("Total to load: " + totalToLoad);
        }

        @Override
        protected ArrayList<Scene> doInBackground() {
            // <editor-fold defaultstate="collapsed" desc="Method Code">
            int totalToLoad = sceneStrings.size();
            for(int i=0;i<totalToLoad;i++){
                Scene tempScene = new Scene();
                try {
                    tempScene.readSceneString(sceneStrings.get(i), reduceMemory);
                    tempScene.setTimeStamp(IOHelp.DF[3].format(dtimage*i*unitScale) + " " + units);
                } catch(InputMismatchException e){
                    System.out.println("Caught mismatch. Was given a file with name.");
                    tempScene = null;
                } catch(Exception ee){
                    ee.printStackTrace(System.out);
                }
                if(tempScene != null){
                    tempScenes.add(tempScene);
                    // System.out.println("Added " + i);
                } else {
                    System.out.println("tempScene was null with index " + i);
                }
                filesLoaded += 1;
                // System.out.println("Progress should be " + 100*(i+1)/(double)totalToLoad);
                setProgress((int)Math.round(100*(i+1)/(double)totalToLoad));
                if(progressWindow.wasCanceled()){
                    break;
                }
            }
            return tempScenes;
            // </editor-fold>
        }

        @Override
        public void done(){
            // <editor-fold defaultstate="collapsed" desc="Method Code">
            doneLoading = true;
            Toolkit.getDefaultToolkit().beep();
            progressWindow.finishedProgress();
            try{
                scenes = get();
                scenes.get(0).buildSceneGraph();
            } catch(InterruptedException ignore){}
            catch(java.util.concurrent.ExecutionException e){
                String why;
                Throwable cause = e.getCause();
                if(cause != null){
                    why = cause.getMessage();
                } else {
                    why = e.getMessage();
                }
                System.out.println("Error retrieving tempScene array: " + why);
            }
            // </editor-fold>
        }
        // </editor-fold>
    }

    public ViewerPanel getViewPanel() {
        return viewPanel;
    }

}
