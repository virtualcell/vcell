package cbit.vcell.desktop;

import cbit.vcell.server.manage.ServerMonitor;

/*©
 * (C) Copyright University of Connecticut Health Center 2001.
 * All rights reserved.
©*/
/**
 * Insert the type's description here.
 * Creation date: (2/16/2001 10:05:11 PM)
 * @author: Ion Moraru
 */
public class VCellBuildIncludes {
	//private cbit.vcell.messaging.server.VCellJmsTester jmsTester = null;
	//private cbit.vcell.modeldb.test.PopulateSimulationJobStatus psjs = null;
	
	private cbit.vcell.messaging.admin.ServerManageConsole serverManageConsole = null;
	private cbit.vcell.messaging.admin.BootstrapDaemon bootstrapDaemon = null;
	private cbit.vcell.messaging.admin.ServerManagerDaemon serverManagerDaemon = null;
	private cbit.vcell.messaging.server.DatabaseServer dbServer = null;
	private cbit.vcell.messaging.server.SimDataServer simDataServer = null;
	private cbit.vcell.messaging.server.SimulationDispatcher dispatcher = null;
	private cbit.vcell.messaging.server.SimulationWorker worker = null;
	private cbit.vcell.messaging.server.BNGServer bngServer = null;

	private cbit.vcell.messaging.server.RpcRequest request = null;
	private cbit.vcell.messaging.SimulationTaskMessage simTaskMessage = null;
	
	private cbit.vcell.messaging.server.LocalUserMetaDbServerMessaging_Stub localUserMetaDbServerMessaging_Stub = null;
	private cbit.vcell.messaging.server.LocalDataSetControllerMessaging_Stub localDataSetControllerMessaging_Stub = null;
	private cbit.vcell.messaging.server.LocalSimulationControllerMessaging_Stub localSimulationControllerMessaging_Stub = null;
	private cbit.vcell.messaging.server.LocalVCellConnectionMessaging_Stub localVCellConnectionMessaging_Stub = null;

	//private cbit.vcell.modeldb.test.ResultSetCrawlerTest resultSetCrawlerTest = null;
	//private cbit.vcell.modeldb.test.ImportImageTest importImageTest = null;
	private cbit.vcell.server.LocalVCellBootstrap localVCellBootstrap = null;
	private cbit.vcell.server.manage.ServerManager serverManager = null;  // needed by ServerManager bootstrap code
	//private cbit.vcell.modeldb.test.SQLPopulatePermission SQLPopulatePermission = null; // needed by database porting
	private ServerMonitor serverMonitor = null; // needed for standalone adminTool
	//private cbit.vcell.modeldb.test.MathVerifierTest mathVerifierTest = null; // needed to run regression testing for math generation
	
	private cbit.vcell.modeldb.LocalAdminDbServer_Stub localAdminDbServer_Stub = null;
	private cbit.vcell.simdata.LocalDataSetControllerProxy_Stub localDataSetControllerProxy_Stub = null;
	private cbit.vcell.simdata.LocalDataSetController_Stub localDataSetController_Stub = null;
	private cbit.vcell.solvers.LocalSolverController_Stub localSolverController_Stub = null;
	private cbit.vcell.server.LocalSimulationController_Stub localSimulationController_Stub = null;
	private cbit.vcell.modeldb.LocalUserMetaDbServer_Stub localUserMetaDbServer_Stub = null;
	private cbit.vcell.server.LocalVCellBootstrap_Stub  localVCellBootstrap_Stub = null;
	private cbit.vcell.server.LocalVCellConnection_Stub localVCellConnection_Stub = null;
	private cbit.vcell.server.LocalVCellServer_Stub localVCellServer_Stub = null;
	private cbit.rmi.event.SimpleMessageHandler_Stub simpleMessageHandler_Stub = null;

	private cbit.image.VCImageCompressed vcImageCompressed = null;
	private cbit.image.VCImageUncompressed vcImageUncompressed = null;

	private cbit.vcell.geometry.GeometryInfo geometryInfo = null;
	private cbit.image.VCImageInfo vcImageInfo = null;
	private cbit.gui.MacLookAndFeelSupported macLookAndFeelSupported = null;

	private cbit.vcell.xml.NameList nameList = null; // needed by XSLT scripts
	private cbit.vcell.xml.NameManager nameManager = null; // needed by XSLT scripts
	private cbit.vcell.parser.ExpressionMathMLParser expressionMathMLParser = null; // needed by XSLT scripts
	private cbit.vcell.parser.ExpressionMathMLPrinter expressionMathMLPrinter = null; // needed by XSLT scripts

	private cbit.vcell.geometry.Curve curve = null;
	private cbit.vcell.geometry.AnalyticCurve analyticCurve = null;
	private cbit.vcell.geometry.CompositeCurve compositeCurve = null;
	private cbit.vcell.geometry.CompositeLine compositeLine = null;
	private cbit.vcell.geometry.CompositeSpline compositeSpline = null;
	private cbit.vcell.geometry.ControlPointCurve  controlPointCurve = null;
	private cbit.vcell.geometry.SampledCurve sampledCurve = null;
	private cbit.vcell.geometry.CurveSelectionCurve curveSelectionCurve = null;
	private cbit.vcell.geometry.Line line = null;
	private cbit.vcell.geometry.PolyLine polyLine = null;
	private cbit.vcell.geometry.SinglePoint singlePoint = null;
	private cbit.vcell.geometry.Spline spline = null;

	private cbit.vcell.anonymizer.AnonymizerBootstrap anonymizerBootstrap = null;
	private cbit.vcell.anonymizer.AnonymizerBootstrap_Stub anonymizerBootstrap_Stub = null;
	private cbit.vcell.anonymizer.AnonymizerDataSetController_Stub anonymizerDataSetController_Stub = null;
	private cbit.vcell.anonymizer.AnonymizerSimulationController_Stub anonymizerSimulationController_Stub = null;
	private cbit.vcell.anonymizer.AnonymizerUserMetaDbServer_Stub anonymizerUserMetaDbServer_Stub = null;
	private cbit.vcell.anonymizer.AnonymizerVCellConnection_Stub anonymizerVCellConnection_Stub = null;

	private cbit.vcell.vcml.test.VCellSemanticTest vcellSemanticTest = null;		// Needed for SBML validation test suite ...
}