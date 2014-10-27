package org.vcell.model.rbm;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.vcell.model.bngl.ASTModel;
import org.vcell.model.rbm.RbmUtils.BnglObjectConstructionVisitor;
import org.vcell.solver.smoldyn.SmoldynSolver;
import org.vcell.util.PropertyLoader;
import org.vcell.util.StdoutSessionLog;
import org.vcell.util.document.KeyValue;
import org.vcell.util.document.LocalVCDataIdentifier;
import org.vcell.util.document.User;
import org.vcell.util.document.VCDataIdentifier;

import cbit.vcell.biomodel.BioModel;
import cbit.vcell.field.FieldDataIdentifierSpec;
import cbit.vcell.mapping.SimulationContext;
import cbit.vcell.math.ReservedVariable;
import cbit.vcell.messaging.server.SimulationTask;
import cbit.vcell.model.Model.RbmModelContainer;
import cbit.vcell.mongodb.VCMongoMessage;
import cbit.vcell.resource.ResourceUtil;
import cbit.vcell.solver.DataProcessingInstructions;
import cbit.vcell.solver.Simulation;
import cbit.vcell.solver.SimulationJob;
import cbit.vcell.solver.SimulationOwner;
import cbit.vcell.solver.SolverDescription;
import cbit.vcell.solver.SolverException;
import cbit.vcell.solver.SolverUtilities;
import cbit.vcell.solver.VCSimulationDataIdentifier;
import cbit.vcell.solver.VCSimulationIdentifier;
import cbit.vcell.solver.ode.ODESimData;
import cbit.vcell.solver.server.Solver;
import cbit.vcell.solver.server.SolverEvent;
import cbit.vcell.solver.server.SolverFactory;
import cbit.vcell.solver.server.SolverListener;
import cbit.vcell.solver.server.SolverStatus;
//import cbit.vcell.client.FieldDataWindowManager.OutputFunctionViewer;
import cbit.vcell.util.ColumnDescription;

public class RbmTest {

	public static void main(String[] args) {
		try {
			VCMongoMessage.enabled = false;
			PropertyLoader.loadProperties();
			
			// parse a sample file and populate the RbmModelContainer
			ASTModel astModel = null;
			BioModel bioModel = new BioModel(null);
			SimulationContext application = bioModel.getSimulationContexts()[0];	// TODO: we assume one single simulation context which may not be the case

			RbmModelContainer rbmModelContainer = bioModel.getModel().getRbmModelContainer();
			//RbmModelContainer rbmModelContainer = new RbmModelContainerSimple(ModelUnitSystem.createDefaultVCModelUnitSystem());

			RbmUtils.reactionRuleLabelIndex = 0;
			RbmUtils.reactionRuleNames.clear();

//			astModel = RbmUtils.importBnglFile(new FileReader("C:\\dan\\NFsim\\models\\actin\\actin_branch.bngl"));
			astModel = RbmUtils.importBnglFile(new FileReader("simple3.bngl"));
			BnglObjectConstructionVisitor constructionVisitor = new BnglObjectConstructionVisitor(bioModel.getModel(), application, true);
			astModel.jjtAccept(constructionVisitor, rbmModelContainer);
			
			// show what's in the RbmModelContainer
			convertToBngl(bioModel);
			
			boolean bStochastic = true;
			boolean bRuleBased = true;
			SimulationContext ruleBasedSimContext = bioModel.addNewSimulationContext("rulebased app", bStochastic, bRuleBased);
			ruleBasedSimContext.refreshMathDescription();
			
			Simulation sim = ruleBasedSimContext.addNewSimulation(SimulationOwner.DEFAULT_SIM_NAME_PREFIX);
			
			SolverListener solverListener = new SolverListener() {
				@Override
				public void solverStopped(SolverEvent event) {
				}
				
				@Override
				public void solverStarting(SolverEvent event) {
				}
				
				@Override
				public void solverProgress(SolverEvent event) {
				}
				
				@Override
				public void solverPrinted(SolverEvent event) {
				}
				
				@Override
				public void solverFinished(SolverEvent event) {
				}
				
				@Override
				public void solverAborted(SolverEvent event) {
				}
			};
			String userName = User.tempUser.getName();
			File localSimDataDir = ResourceUtil.getLocalSimDir(userName);
			Solver solver = startQuickSimulation(sim, localSimDataDir, solverListener);
			//
			// wait for solver to finish (or be interrupted by user cancel)
			//
			while (true){
				try { 
					Thread.sleep(500); 
				} catch (InterruptedException e) {
				}

				SolverStatus solverStatus = solver.getSolverStatus();
				if (solverStatus != null) {
					if (solverStatus.getStatus() == SolverStatus.SOLVER_ABORTED) {
						throw new RuntimeException(solverStatus.getSimulationMessage().getDisplayMessage());
					}
					if (solverStatus.getStatus() != SolverStatus.SOLVER_STARTING &&
						solverStatus.getStatus() != SolverStatus.SOLVER_READY &&
						solverStatus.getStatus() != SolverStatus.SOLVER_RUNNING){
						break;
					}
				}		
			}
			
			System.out.println("simulation ended");
			
			ODESimData odeSimData = null;
			final User user = new User(userName, new KeyValue("4123431"));
			VCDataIdentifier vcDataId = new VCDataIdentifier() {
				public String getID() {
					return "SimID_8483780";
				}
			
				public org.vcell.util.document.User getOwner() {
					return user;
				}
			};
			String dataFilename = "null_0_.gdat";
			File odeFile = new File(localSimDataDir, dataFilename);
			odeSimData = ODESimData.readNFSIMDataFile(vcDataId, odeFile);
			
			long lastModified = odeFile.lastModified();
			int timeColIndex = odeSimData.findColumn(ReservedVariable.TIME.getName());
//			double dataTimes[] = odeSimData.extractColumn(timeColIndex);
//			double maxTime = dataTimes[dataTimes.length-1];
			
			int numColumns = odeSimData.getDataColumnCount();
			
	        GraphingData gd = new GraphingData(5);
			for(int i=0; i<numColumns; i++) {
				if(i == timeColIndex) {
					continue;
				}
				ColumnDescription cd = odeSimData.getColumnDescriptions(i);
				String columnName = cd.getDisplayName();
				double columnData[] = odeSimData.extractColumn(i);
				gd.addPlot(columnName, columnData);
				
			}
	        JFrame f = new JFrame();
	        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        f.add(gd);
	        f.setSize(400,400);
	        f.setLocation(200,200);
	        f.setVisible(true);

	        
//			ODEDataInfo odeDataInfo = new ODEDataInfo(vcDataId.getOwner(), vcDataId.getID(), lastModified);
//			ODEDataBlock odeDataBlock = new ODEDataBlock(odeDataInfo, odeSimData);
//			ODESimData odeSimData1 = odeDataBlock.getODESimData();
//			ODESolverResultSet odeSolverResultSet = new ODESimData(vcDataId, odeSimData1);
//			
//			//dataManager = new ODEDataManager(outputContext,vcDataManager, vcDataId);
			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}
	
	public static void convertToBngl(BioModel bioModel) {
		StringWriter bnglStringWriter = new StringWriter();
		PrintWriter pw = new PrintWriter(bnglStringWriter);
		RbmNetworkGenerator.writeBngl(bioModel, pw);
		String bngl = bnglStringWriter.toString();
		pw.close();
		System.out.println(bngl);
	}

	public static class LocalVCSimuDataIdentifier extends VCSimulationDataIdentifier implements LocalVCDataIdentifier {

		private File localSimDir = null;
		public LocalVCSimuDataIdentifier(VCSimulationIdentifier vcSimID, int jobIndex, File localDir) {
			super(vcSimID, jobIndex);
			localSimDir = localDir;
		}

		public File getLocalDirectory() {
			return localSimDir;
		}
	}

	// ---------------------------------------------------------------------------------------
	
	public static class GraphingData extends JPanel {
		private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
		
		ArrayList<double[]> ddata = new ArrayList<double[]>();
		ArrayList<String> dname = new ArrayList<String>();
	    double PAD = 0;
	 
	    public GraphingData(double PAD) {
			this.PAD = PAD;
		}

		public void addPlot(String columnName, double[] dataData) {
			this.dname.add(columnName);
			this.ddata.add(dataData);
		}

		protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        Graphics2D g2 = (Graphics2D)g;
	        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
	                            RenderingHints.VALUE_ANTIALIAS_ON);
	        int w = getWidth();
	        int h = getHeight();
	        
	        Stroke oldStroke = g2.getStroke();
	        g2.setColor(Color.black);
	        g2.setStroke(GRAPH_STROKE);
	        g2.draw(new Line2D.Double(PAD, PAD, PAD, h-PAD));		// ordinate
	        g2.draw(new Line2D.Double(PAD, h-PAD, w-PAD, h-PAD));	// abscissa
	        
	        double xInc = (double)(w - 2*PAD)/(ddata.get(0).length-1);
	        double scale = (double)(h - 2*PAD)/getMax();
	        
	        g2.setStroke(oldStroke);
	        Color[] cols = generateColors(ddata.size());
	        
	        for(int i = 0; i < ddata.size(); i++) {
	        	double[] data = ddata.get(i);
	        	String name = dname.get(i);
	        	
	        	g2.setPaint(cols[i]);		// Mark data points
	        	for(int j = 0; j < data.length; j++) {
	        		double x = PAD + j*xInc;
	        		double y = h - PAD - scale*data[j];
	        		g2.drawString(name, 250, 100+15*i);
	        		g2.fill(new Ellipse2D.Double(x-1, y-1, 2, 2));
	        	}
	        }
	    }
	 
	    private double getMax() {
	        double max = -Integer.MAX_VALUE;
	        
	        for (double[] data : ddata) {
	        	for(int i = 0; i < data.length; i++) {
	        		if(data[i] > max)
	        			max = data[i];
	        	}
	        }
	        return max;
	    }
	    
	    public Color[] generateColors(int n)
	    {
	    	Color[] cols = new Color[n];
	    	for(int i = 0; i < n; i++)
	    	{
	    		cols[i] = Color.getHSBColor((float) i / (float) n, 0.85f, 1.0f);
	    	}
	    	return cols;
	    }
	}
	public static Solver startQuickSimulation(Simulation simulation, File localSimDataDir, SolverListener solverListener) throws SolverException, IOException {
		StdoutSessionLog log = new StdoutSessionLog("Quick run");
		SimulationTask simTask = new SimulationTask(new SimulationJob(simulation, 0, null),0);
		SolverDescription solverDescription = simTask.getSimulation().getSolverTaskDescription().getSolverDescription();
		if (solverDescription == null) {
			throw new IllegalArgumentException("SolverDescription cannot be null");
		}
		
		// ----- 'FiniteVolume, Regular Grid' solver (semi-implicit) solver is not supported for quick run; throw exception.
		if (solverDescription.equals(SolverDescription.FiniteVolume)) {
			throw new IllegalArgumentException("Semi-Implicit Finite Volume Compiled, Regular Grid (Fixed Time Step) solver not allowed for quick run of simulations.");
		}
		
		SolverUtilities.prepareSolverExecutable(solverDescription);	
		// create solver from SolverFactory
		Solver solver1 = SolverFactory.createSolver(log, localSimDataDir, simTask, false);
		Solver solver = solver1;
		if (solver == null) {
			throw new RuntimeException("null solver");
		}
		// check if spatial stochastic simulation (smoldyn solver) has data processing instructions with field data - need to access server for field data, so cannot do local simulation run. 
		if (solver instanceof SmoldynSolver) {
			DataProcessingInstructions dpi = simulation.getDataProcessingInstructions();
			if (dpi != null) {
				FieldDataIdentifierSpec fdis = dpi.getSampleImageFieldData(simulation.getVersion().getOwner());	
				if (fdis != null) {
					throw new RuntimeException("Spatial Stochastic simulation '" + simulation.getName() + "' (Smoldyn solver) with field data (in data processing instructions) cannot be run locally at this time since field data needs to be retrieved from the VCell server.");
				}
			}
		}
		solver.addSolverListener(solverListener);
		solver.startSolver();
		return solver;
	}


}
