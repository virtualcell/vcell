package cbit.vcell.visit;

public final class VisitPythonCommand {
	
	public class VisitCommandException extends Exception {
		public VisitCommandException(String message){
			super(message);
		}
	}

	public static enum DEBUG_LEVEL {
		DEBUG_LEVEL_1,
		DEBUG_LEVEL_2,
		DEBUG_LEVEL_3,
		DEBUG_LEVEL_4,
		DEBUG_LEVEL_5
	};
	
	private static String quote(String s){return("\""+s+"\"");}
	private static String parenQuote(String s){return("("+quote(s)+")");}
	private static String parenInt(int i){return("("+String.valueOf(i)+")");}
	private static String parenStr(String s){return("("+s+")");}
	private static String str(int i){return(String.valueOf(i));}

	
	
	
	public static String AddArgument(String argument){return("visit.AddArgument("+quote(argument)+")");}
	public static String Close() {return callMethod("visit.Close");}
	public static String Launch() {return("visit.Launch()");}
	public static String LaunchNowin() {return("visit.LaunchNowin()");}
	public static String LocalNamespace() {return("visit.LocalNamespace()");}
	
	public static String DebugLevel() {return callMethod("visit.DebugLevel");}
	
	public static String DebugLevel(DEBUG_LEVEL level) //throws VisitCommandException 
	{
		switch (level){
			case DEBUG_LEVEL_1:{
				return callMethod("DebugLevel",1);
			}
			case DEBUG_LEVEL_2:{
				return callMethod("DebugLevel",2);
			}
			case DEBUG_LEVEL_3:{
				return callMethod("DebugLevel",3);
			}
			case DEBUG_LEVEL_4:{
				return callMethod("DebugLevel",4);
			}
			case DEBUG_LEVEL_5:{
				return callMethod("DebugLevel",5);
			}
			default:
				return callMethod("DebugLevel",0);//TEMPORARY AND INVALID until exception handling is incorporated
				//throw new VisitCommandException("unsupported debug level");
		}
	}
	public static String Version() {return("visit.Version()");}
	public static String ActivateDataBase(String s) {return("visit.ActivateDatabase"+parenQuote(s));}
	public static String AddOperator(String s) {return("visit.AddOperator"+parenQuote(s));}
	public static String AddOperator(String s, int i) {return("visit.AddOperator("+quote(s)+","+String.valueOf(i)+")");}
	public static String AddPlot(String plottype, String variablename) {return("visit.AddPlot("+quote(plottype)+","+quote(variablename)+")");}
	public static String AddWindow() {return("visit.AddWindow()");}
	public static String AlterDatabaseCorrelation(String name, String databases, int method) {return("visit.AlterDatabaseCorrelation("+quote(name)+","+quote(databases)+","+String.valueOf(method)+")");}
	public static String ChangeActivePlotsVar(String variableName) {return("visit.ChangeActivePlotsVar"+parenQuote(variableName));}
	public static String CheckForNewStates(String name) {return("visit.CheckForNewStates"+parenQuote(name));}
	public static String ChooseCenterOfRotation() {return("visit.ChooseCenterOfRotation()");}
	public static String ChooseCenterOfRotation(int screenX, int screenY) {return("visit.ChooseCenterOfRotation("+String.valueOf(screenX)+","+String.valueOf(screenY)+")");}
	public static String ChangeActivePlotsVar(int variableName) {return("visit.ChangeActivePlotsVar"+parenQuote(String.valueOf(variableName)));}
	public static String ClearAllWindows() {return("visit.ClearAllWindows()");}
	public static String ClearWindow() {return("visit.ClearWindow()");}
	public static String DeleteActivePlots() {return("visit.DeleteActivePlots()");}
	public static String DeleteAllPlots() {return("visit.DeleteAllPlots()");}
	
	public static String DeleteWindow() {return("visit.DeleteWindow()");}
	
	public static String DrawPlots() {return("visit.DrawPlots()");}
	

	public static String GetCallbackNames() {return("visit.GetCallbackNames()");}
	public static String GetGlobalAttributes() {return("visit.GetGlobalAttributes()");}
	
	public static String GetLastError() {return("visit.GetLastError()");}

	public static String GetLocalHostName() {return("visit.GetLocalHostName()");}
	public static String GetLocalUserName() {return("visit.GetLocalUserName");}
	
	
	public static String GetNumPlots() {return("GetNumPlots()");}
	
	public static String LocalNameSpace() {return("visit.LocalNameSpace()");}
	public static String OpenComputeEngine() {return("OpenComputeEngine()");}
	public static String OpenComputeEngine(String hostName) {return("visit.OpenComputeEngine"+parenQuote(hostName));}
	public static String OpenComputeEngine(String hostname, String args) {return("visit.OpenComputeEngine"+parenQuote(hostname + ","+args));} //note that simulation argument can't be differentiated from other args string.


	public static String OpenDatabase(String databaseName) {System.out.println("Returning:");System.out.println("visit.OpenDatabase"+parenQuote(databaseName));return("visit.OpenDatabase"+parenQuote(databaseName));}
	public static String OpenDatabase(String databaseName, int timeIndex) {return("visit.OpenDatabase"+parenQuote(databaseName+","+str(timeIndex)));}
	public static String OpenDatabase(String databaseName, int timeIndex, String dbPluginName) {return("visit.OpenDatabase"+parenQuote(databaseName+","+str(timeIndex)+","+dbPluginName));}
	public static String OpenGUI() {return("visit.OpenGUI()");}
	
	public static String OpenMDServer() {return("visit.OpenMDServer()");}
	public static String OpenMDServer(String host) {return("visit.OpenMDServer"+parenQuote(host));}
	public static String OpenMDServer(String host, String args) {return("visit.OpenMDServer"+parenQuote(host+","+args));}
	public static String RecenterView() {return("visit.RecenterView()");}
	
	public static String RegisterCallback(String callbackname, String callback) {return("visit.RegisterCallBack("+quote(callbackname)+","+callback+")");} //note that callback, the name of the actual function, is not surrounded by quotes
	
	public static String ResetView(){return("visit.ResetView()");}
	public static String RestoreSession(String filename, int visitDir){return("visit.RestoreSession("+quote(filename)+","+str(visitDir)+")");}
	public static String ResizeWindow(int win, int w, int h){return("visit.ResizeWindow("+str(win)+","+str(w)+","+str(h)+")");}
	public static String SaveSession(String filename) {return("visit.SaveSession"+parenQuote(filename));}
	
	public static String SetActivePlots(String plots){return("visit.SetActivePlots"+parenStr(plots));} //plots is a tuple of plot numbers starting at 0
	
	public static String SetPlotOptions(String atts) {return("visit.SetPlotOptions"+parenStr(atts));}  //Note: atts is the NAME of an attributes object.  Must already be defined in Python
	public static String SetOperatorOptions(String atts) {return("visit.SetOperatorOptions"+parenStr(atts));}  //Note: atts is the NAME of an attributes object.  Must already be defined in Python
	
	public static String SetDefaultPlotOptions(String atts) {return("visit.SetDefaultPlotOptions"+parenQuote(atts));}
	
	public static String SetTimeSliderState(int state) {return("visit.SetTimeSliderState("+str(state)+")");}
	
	public static String Source(String filename) {return("visit.Source"+parenQuote(filename));}
	//public static String SupressMessages(int level) throws VisitCommandException {if ((level<1) || (level >5)) throw new VisitCommandException("unsupported message supression level");return("visit.SupressMessages("+str(level)+")");}
    //temporary version without bounds checking follows:
	public static String SupressMessages(int level) {return("visit.SupressMessages("+str(level)+")");}

    public static String makeTransformAttributes(String attrObjectName) {return(attrObjectName +"=visit.TransformAttributes()");}  //yields python command {attrObjectName} = TransformAttributes()  which returns an attributes object to attsObjectName
	
	private static String callMethod(String methodName){
		return methodName+"()";
	}
	private static String callMethod(String methodName,int arg){
		return methodName+"("+arg+")";
	}
	
	
	/*


	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	*/
}
