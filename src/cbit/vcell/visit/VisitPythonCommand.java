package cbit.vcell.visit;

public class VisitPythonCommand {

	private String quote(String s){return("\""+s+"\"");}
	private String parenQuote(String s){return("("+quote(s)+")");}
	private String parenInt(int i){return("("+String.valueOf(i)+")");}
	
	public String AddArgument(String argument){return("AddArgument("+quote(argument)+")");}
	public String Close() {return ("Close()");}
	public String Launch() {return("Launch()");}
	public String LaunchNowin() {return("LaunchNowin()");}
	public String LocalNamespace() {return("LocalNamespace()");}
	public String DebugLevel() {return("DebugLevel()");}
	public String DebugLevel(int i) {return("DebugLevel("+String.valueOf(i)+")");}
	public String Version() {return("Version()");}
	public String ActivateDataBase(String s) {return("ActivateDatabase"+parenQuote(s));}
	public String AddOperator(String s) {return("AddOperator"+parenQuote(s));}
	public String AddOperator(String s, int i) {return("AddOperator("+quote(s)+","+String.valueOf(i)+")");}
	public String AddPlot(String plottype, String variablename) {return("AddPlot("+quote(plottype)+","+quote(variablename)+")");}
	public String AddWindow() {return("AddWindow()");}
	public String AlterDatabaseCorrelation(String name, String databases, int method) {return("AlterDatabaseCorrelation("+quote(name)+","+quote(databases)+","+String.valueOf(method)+")");}
	public String ChangeActivePlotsVar(String variableName) {return("ChangeActivePlotsVar"+parenQuote(variableName));}
	public String CheckForNewStates(String name) {return("CheckForNewStates"+parenQuote(name));}
	public String ChooseCenterOfRotation() {return("ChooseCenterOfRotation()");}
	public String ChooseCenterOfRotation(int screenX, int screenY) {return("ChooseCenterOfRotation("+String.valueOf(screenX)+","+String.valueOf(screenY)+")");}
	
	
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
	public String () {return("");}
	public String () {return("");}
	public String () {return("");}
	*/
}
