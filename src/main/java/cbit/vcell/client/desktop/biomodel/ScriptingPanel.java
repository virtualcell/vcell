/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.client.desktop.biomodel;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import cbit.vcell.biomodel.BioModel;

@SuppressWarnings("serial")
class ScriptingPanel extends JPanel {
	private JTextArea scriptTextArea = null;
	private JTextArea resultsTextArea = null;
	private JButton runButton = new JButton("Run");
	private JButton resetButton = new JButton("Reset");
	private BioModel bioModel = null;
	
	public BioModel getBioModel() {
		return bioModel;
	}

	public void setBioModel(BioModel bioModel) {
		this.bioModel = bioModel;
	}

	private ActionListener eventListener = new ActionListener(){
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == runButton){
				run();
			}else if (e.getSource() == resetButton){
				reset();
			}
		}
	};
	
	public ScriptingPanel() {
		initialize();
	}

	private void initialize(){
		String initialResultsText = "script output is displayed here\n";
		
		scriptTextArea = new JTextArea(getInitialScriptText());
		scriptTextArea.setEditable(true);
		scriptTextArea.setLineWrap(false);
		scriptTextArea.setWrapStyleWord(false);
		
		resultsTextArea = new JTextArea(initialResultsText);
		resultsTextArea.setEditable(true);
		resultsTextArea.setLineWrap(false);
		resultsTextArea.setWrapStyleWord(false);
		
		setLayout(new BorderLayout());
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerLocation(350);
		splitPane.setResizeWeight(0.5);
		splitPane.setTopComponent(new JScrollPane(scriptTextArea));
		splitPane.setBottomComponent(new JScrollPane(resultsTextArea));
		add(splitPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
		panel.add(runButton);	
		panel.add(resetButton);			
		add(panel, BorderLayout.SOUTH);
		
		runButton.addActionListener(eventListener);
		resetButton.addActionListener(eventListener);
	}
	
	private String getInitialScriptText(){
		
		String initialScriptText = "// vcell scripting (javascript)\n" +
		"// predefined variables:\n" +
//		"//   workspace  -  BioModelWindowManager\n" +
		"//   biomodel   -  currently loaded biomodel\n" +
		"//   model      -  currently loaded model\n" +
		"// predefined functions:\n" +
		"//   list(var)  -  lists methods/fields availlable for any variable\n" +
		"\n" +
		"// example script\n" +
		"println(biomodel.name);\n" +
		"biomodel.name = 'myModel';   // sets name of biomodel (note label in tree is changed)\n" +
		"println(biomodel.name);\n" +
		"list(biomodel)\n             // lists fields/functions available for cbit.vcell.biomodel.BioModel";

		return initialScriptText;
	}
	
	private String getDefaultScript(){
		StringBuffer buffer = new StringBuffer();
		buffer.append("biomodel.name = 'hello';\n");
		buffer.append("list(biomodel);\n");
		buffer.append("model = biomodel.model;\n");
		buffer.append("cyt = model.structures[0];\n");
		buffer.append("cyt.name = 'cyt'\n");
		buffer.append("ca = model.addSpeciesContext('calcium',cyt);\n");
		buffer.append("for (i=0;i<10;i++){\n");
		buffer.append("   model.addSpeciesContext('ca'+i,cyt);\n");
		buffer.append("}\n");
		return buffer.toString();
	}

	private void run(){
		// create scripting engine
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("JavaScript");
		
		// create environment for the script to run in.
		//   1) capture stdout and send to the resultsTextField
		//   2) predefine variables for windowManager, bioModel, model, and current selections
		//
		ScriptContext scriptContext = new SimpleScriptContext();
		scriptContext.setBindings(new SimpleBindings(), ScriptContext.GLOBAL_SCOPE);
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		scriptContext.setWriter(printWriter);
		
		Bindings globals = scriptContext.getBindings(ScriptContext.GLOBAL_SCOPE);

//		BioModelWindowManager bioModelWindowManager = ScriptingPanel.this.bioModelEditor.getBioModelWindowManager();
//		globals.put("workspace",bioModelWindowManager);
//		
//		BioModel bioModel = bioModelWindowManager.getBioModel();
//		if (bioModel!=null){
//			globals.put("bioModel",bioModel);
//			globals.put("model",bioModel.getModel());
//		}
		if (getBioModel()!=null){
			globals.put("biomodel", getBioModel());
			globals.put("model", getBioModel().getModel());
		}
		engine.setContext(scriptContext);
		
		//
		// run the script
		//
		try {
			String fullScript = getPredefinedFunctions();
			fullScript += scriptTextArea.getText();
			System.out.println(fullScript);
			engine.eval(fullScript);
			try {
				Bindings engineBindings = scriptContext.getBindings(ScriptContext.ENGINE_SCOPE);
				printWriter.println("engine variable bindings: ");
				for (Map.Entry<String,Object> entry : engineBindings.entrySet()){
					printWriter.println("  "+entry.getKey()+" : "+entry.getValue());
				}
				Bindings globalBindings = scriptContext.getBindings(ScriptContext.GLOBAL_SCOPE);
				printWriter.println("global variable bindings: ");
				for (Map.Entry<String,Object> entry : globalBindings.entrySet()){
					printWriter.println("  "+entry.getKey()+" : "+entry.getValue());
				}
				printWriter.flush();
				resultsTextArea.append(stringWriter.getBuffer().toString());
				resultsTextArea.scrollRectToVisible(new Rectangle(0,resultsTextArea.getHeight()-2,1,1)); // scroll to bottom.
			} catch (RuntimeException e1) {
				e1.printStackTrace();
			}
		} catch (ScriptException e1) {
			e1.printStackTrace();
		}
	}

	private String getPredefinedFunctions() {
		String functions = "function list(obj) {\n" +
				"    println('');\n"+
				"    println('members of class '+obj.getClass().name+':');\n" +
				"    println('');\n"+
				"    count = 0;\n" +
				"    var props = [];\n" +
				"    for (var propName in obj){\n" +
				"        props[count++] = propName;\n" +
				"    }\n" +
				"    props.sort();\n" +
				"    count=0;\n" +
				"    line = '';\n"+
				"    for (i=0;i<props.length;i++){\n" +
				"        if (line.length+props[i].length>80){" +
				"           println(line);\n"+
				"           line = '';\n"+
				"        }\n"+
				"        line += '   '+props[i];\n" +
				"    }\n" +
				"    println(line);\n"+
				"}\n\n";
		return functions;
	}

	private void reset(){
		scriptTextArea.setText(getDefaultScript());
		resultsTextArea.setText("");
	}

}
