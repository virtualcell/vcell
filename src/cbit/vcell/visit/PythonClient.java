package cbit.vcell.visit;




import java.lang.ArrayIndexOutOfBoundsException;
import java.lang.String;
 
import java.util.Vector;
import llnl.visit.*;    //get more specific later -- eb

 
 
// ****************************************************************************
// Class: DualClients
//
// Purpose:
//   This example program shows how to launch the Python client from Java
//   and send commands to it.
//
// Notes:      
//
// Programmer: Brad Whitlock
// Creation:   Tue Jan 11 09:30:41 PST 2011
//
// Modifications:
//
//    Ed Boyce: Changed name of class to PythonClient for Virtual Cell 
//    integration.  Jan. 17, 2011
// ****************************************************************************
 
public class PythonClient extends RunViewer implements SimpleObserver
{
    public PythonClient()
    {
        super();
        doUpdate = true;
        System.out.println("About to execute viewer.GetViewerState().GetPlotList().Attach(this);");
        // Make this object observe the plot list
        //viewer.GetViewerState().GetPlotList().Attach(this);
        System.out.println("Did that");
    }
 
    
    public void run(String[] args)
    {
        // Pass command line options to the viewer viewer
        boolean stay = false;
        boolean sync = true;
        boolean verbose = false;

        for(int i = 0; i < args.length; ++i)
        {
            if(args[i].equals("-stay"))
                stay = true;
            else if(args[i].equals("-dv"))
                viewer.SetBinPath("../bin");
            else if(args[i].equals("-sync"))
                sync = true;
            else if(args[i].equals("-async"))
                sync = false;
            else if(args[i].equals("-verbose"))
                verbose = true;
            else if(args[i].equals("-quiet"))
                verbose = false;
            else if(args[i].equals("-path") && ((i + 1) < args.length))
            {
                viewer.SetBinPath(args[i + 1]);
                ++i;
            }
            else if(args[i].equals("-help"))
            {
                printUsage();
                return;
            }
            else
                viewer.AddArgument(args[i]);
        }

        // Set the viewer proxy's verbose flag.
        viewer.SetVerbose(verbose);

        // Try and open the viewer using the viewer proxy.
        System.out.println("This");
        if(viewer.Create(5600))
        {
            System.out.println("ViewerProxy opened the viewer.");

            // Set some viewer properties based on command line args.
            viewer.SetSynchronous(sync);

            // Show the windows.
            viewer.GetViewerMethods().ShowAllWindows();

            work(args);

            // If we have the -stay argument on the command line, keep the
            // viewer around so we can do stuff with it.
            while(stay)
            {
                try
                {
                    // Sleep a little so we don't hog the CPU.
                    Thread.currentThread().sleep(200);
                }
                catch(java.lang.InterruptedException e)
                {
                    stay = false;
                }
            }

            viewer.Close();
        }
        else
            System.out.println("ViewerProxy could not open the viewer.");
    }
    
    //
    // Main work method for the program
    //
    protected void work(String[] args)
    {
        // Try and open a database
    	System.out.println("The datapath is: " +(viewer.GetDataPath()));
    	System.out.println("About to execute Initialize()");
    	Initialize();
    	System.out.println("Did Initialize()");
    	if(viewer.GetViewerMethods().OpenDatabase("/share/apps/vcell/users/edboyce/SimID_53119927_0_.log"))
        {

            // Interpret some Python using the VisIt CLI
        	//Initialize();
            InterpretPython("AddPlot('Pseudocolor', 'Laser_PM')");
            InterpretPython("AddPlot('Mesh', 'membrMesh')");
            InterpretPython("DrawPlots()");
            InterpretPython("SaveWindow()");
        }
        else
            System.out.println("Could not open the database!");
    }
 
    //
    // Check all of the client information until we find a client that
    // supports the Interpret method with a string argument.
    //
    protected boolean NoInterpretingClient()
    {
        // Make a copy because the reader thread could be messing with it.
        // Need to synchronize access.
        ClientInformationList cL = new ClientInformationList(
            viewer.GetViewerState().GetClientInformationList());
//      while(NoInterpretingClient())
//      viewer.Synchronize();

        for(int i = 0; i < cL.GetNumClients(); ++i)
        {
            ClientInformation client = cL.GetClients(i);
            for(int j = 0; j < client.GetMethodNames().size(); ++j)
            {
                String name = (String)client.GetMethodNames().elementAt(j);
                if(name.equals("Interpret"))
                {
                    String proto = (String)client.GetMethodPrototypes().elementAt(j);
                    if(proto.equals("s"))
                    {
                        // We have an interpreting client
                        return false;
                    }
                }
            }
        }
        return true;
    }
 
    //
    // If we don't have a client that can "Interpret" then tell the viewer
    // to launch a VisIt CLI.
    //    
    protected boolean Initialize()
    {
        boolean launched = false;
        if(NoInterpretingClient())
        {
            System.out.println("Tell the viewer to create a CLI so we can execute code.");
            Vector args = new Vector();
            args.addElement(new String("-cli"));
            args.addElement(new String("-newconsole"));
           viewer.GetViewerMethods().OpenClient("CLI", 
//                 viewer.GetVisItLauncher(),
                 "/home/VCELL/eboyce/visit_build_2.2RC/src/bin/visit",
                 args);
            launched = true;
            //System.out.println()
            viewer.Synchronize();
 
            // HACK: Wait until we have an interpreting client.
            while(NoInterpretingClient())
                viewer.Synchronize();
        }
        return launched;
    }
 
    //
    // Interpret a Python command string.
    // 
    protected void InterpretPython(String cmd)
    {
        //Initialize();
 
        // Send the command to interpret as a client method.
        ClientMethod method = viewer.GetViewerState().GetClientMethod();
        method.SetIntArgs(new Vector());
        method.SetDoubleArgs(new Vector());
        Vector args = new Vector();
        args.addElement(new String(cmd + "\n"));
        method.SetStringArgs(args);
        method.SetMethodName("Interpret");
        method.Notify();
        System.out.println("Interpret: " + cmd);
 
        viewer.Synchronize();
    }
 
    //
    // SimpleObserver interface methods
    //
    public void Update(AttributeSubject s)//            while(NoInterpretingClient())
//  viewer.Synchronize();

    {
        // Do something with the plot list.
        System.out.println(s.toString());
    }
    public void SetUpdate(boolean val) { doUpdate = val; }
    public boolean GetUpdate() { return doUpdate; }
 
 
    public static void main(String args[])
    {
        PythonClient r = new PythonClient();
       
        r.run(args);
        r.work(args);
    }
 
    private boolean doUpdate;
}