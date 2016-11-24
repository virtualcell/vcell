package org.vcell.solver.comsol.service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.vcell.solver.comsol.model.VCCCoefficientFormPDE;
import org.vcell.solver.comsol.model.VCCConvectionDiffusionEquation;
import org.vcell.solver.comsol.model.VCCGeomFeature;
import org.vcell.solver.comsol.model.VCCGeomSequence;
import org.vcell.solver.comsol.model.VCCGeomSequence.VCCCircle;
import org.vcell.solver.comsol.model.VCCGeomSequence.VCCDifference;
import org.vcell.solver.comsol.model.VCCGeomSequence.VCCDifference.Keep;
import org.vcell.solver.comsol.model.VCCGeomSequence.VCCSquare;
import org.vcell.solver.comsol.model.VCCMeshSequence;
import org.vcell.solver.comsol.model.VCCModel;
import org.vcell.solver.comsol.model.VCCModelNode;
import org.vcell.solver.comsol.model.VCCPhysics;
import org.vcell.solver.comsol.model.VCCStudyFeature;
import org.vcell.solver.comsol.model.VCCTransientStudyFeature;
import org.vcell.util.PropertyLoader;

public class ComsolServiceScripting implements ComsolService {

	private static ClassLoader comsolClassloader = null;
	
	ComsolServiceScripting(){
		
	}

	private String tojs(int[] a){
		StringBuffer buffer = new StringBuffer();
		buffer.append("(function() { var a = java.lang.reflect.Array.newInstance(int.class, "+a.length+"); ");
		for (int i=0;i<a.length;i++){
			buffer.append("a["+i+"]="+a[i]+"; ");
		}
		buffer.append("return a; })()");
		return buffer.toString();
	}
	
	private String tojs(String[] a){
		StringBuffer buffer = new StringBuffer();
		buffer.append("(function() { var a = java.lang.reflect.Array.newInstance(java.lang.String.class, "+a.length+"); ");
		for (int i=0;i<a.length;i++){
			buffer.append("a["+i+"]='"+a[i]+"'; ");
		}
		buffer.append("return a; })()");
		return buffer.toString();
	}
	
//	private String tojs(String[] a){
//		StringBuffer buffer = new StringBuffer();
//		buffer.append("[");
//		for (int i=0;i<a.length;i++){
//			buffer.append("'"+a[i]+"'");
//			if (i<a.length-1){
//				buffer.append(",");
//			}
//		}
//		buffer.append("]");
//		return buffer.toString();
//	}
	
	private void run(ScriptEngine engine, VCCModel vccModel, File reportFile, File javaFile, File mphFile) throws ScriptException {
		StringBuffer buffer = new StringBuffer();
		
		//									   ModelUtil.initStandalone(true);		
		buffer.append(	"com.comsol.model.util.ModelUtil.initStandalone(true);"	+ "\n");

		//         Model model =                       ModelUtil.create(vccModel.name);
		buffer.append(	"model = com.comsol.model.util.ModelUtil.create('"+vccModel.name+"');"	+ "\n");

//		//               model.modelPath(vccModel.modelpath);
//		buffer.append(	"model.modelPath('"+vccModel.modelpath+"');"	+ "\n");

		//               model.comments(vccModel.comments);
		buffer.append(	"model.comments('"+vccModel.comments.replaceAll("\n", " ")+"');"	+ "\n");

		for (VCCModelNode modelNode : vccModel.modelnodes) {
			//               model.modelNode().create(modelNode.name);
			buffer.append(	"model.modelNode().create('"+modelNode.name+"');"	+ "\n");
		}
		for (VCCGeomSequence geomSequence : vccModel.geometrysequences) {
			//               model.geom().create(geomSequence.name, geomSequence.dim);
			buffer.append(	"model.geom().create('"+geomSequence.name+"',"+geomSequence.dim+");"	+ "\n");
			for (VCCGeomFeature geomFeature : geomSequence.geomfeatures) {
				switch (geomFeature.type) {
				case Circle: {
					VCCCircle circle = (VCCCircle) geomFeature;
					//               model.geom(geomSequence.name)      .create(circle.name, "Circle");
					buffer.append(	"model.geom('"+geomSequence.name+"').create('"+circle.name+"','Circle');"	+ "\n");

					//               model.geom(geomSequence.name)      .feature(circle.name)      .set("selresult", "on");
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+circle.name+"').set('selresult','on');"	+ "\n");

					//               model.geom(geomSequence.name)      .feature(circle.name)      .set("pos", circle.pos);
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+circle.name+"').set('pos', "+tojs(circle.pos)+");"	+ "\n");

					// 				 model.geom(geomSequence.name)		.feature(circle.name)	   .set("r", circle.r);
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+circle.name+"').set('r', '"+circle.r+"');"	+ "\n");
					break;
				}
				case Square: {
					VCCSquare square = (VCCSquare) geomFeature;
					//               model.geom(geomSequence.name)      .create(square.name, "Square");
					buffer.append(	"model.geom('"+geomSequence.name+"').create('"+square.name+"','Square');"	+ "\n");
					//               model.geom(geomSequence.name)      .feature(square.name)      .set("selresult", "on");
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+square.name+"').set('selresult','on');"	+ "\n");
					break;
				}
				case Difference: {
					VCCDifference diff = (VCCDifference) geomFeature;
					//               model.geom(geomSequence.name)      .create(diff.name, "Difference");
					buffer.append(	"model.geom('"+geomSequence.name+"').create('"+diff.name+"','Difference');"	+ "\n");
					//               model.geom(geomSequence.name)      .feature(diff.name)      .set("selresult", "on");
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+diff.name+"').set('selresult','on');"	+ "\n");
					
					ArrayList<String> inputNames = new ArrayList<String>();
					for (VCCGeomFeature feature : diff.input) {
						inputNames.add(feature.name);
					}
					String[] inputSet = inputNames.toArray(new String[0]);
					ArrayList<String> input2Names = new ArrayList<String>();
					for (VCCGeomFeature feature : diff.input2) {
						input2Names.add(feature.name);
					}
					String[] input2Set = input2Names.toArray(new String[0]);
					//               model.geom(geomSequence.name)      .feature(diff.name)      .selection("input").set(inputSet);
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+diff.name+"').selection('input').set("+tojs(inputSet)+");"	+ "\n");

					//               model.geom(geomSequence.name)      .feature(diff.name)      .selection("input2").set(input2Set);
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+diff.name+"').selection('input2').set("+tojs(input2Set)+");"	+ "\n");
					
					if (diff.keep == Keep.on) {
						//               model.geom(geomSequence.name)      .feature(diff.name)      .set("keep", "on");
						buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+diff.name+"').set('keep', 'on');"	+ "\n");
					} else if (diff.keep == Keep.off) {
						//               model.geom(geomSequence.name)      .feature(diff.name)      .set("keep", "off");
						buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+diff.name+"').set('keep', 'off');"	+ "\n");
					}
					break;
				}
				default: {
					throw new RuntimeException("unexpected Geometry Feature type " + geomFeature.type.name());
				}
				}
			}
			System.out.println("building geometry");
			// 				long t_startGeomRun_ms = System.currentTimeMillis();
			buffer.append(	"var t_startGeomRun_ms = Date.now();"		+ "\n");
			
			//               model.geom(geomSequence.name)      .run();
			buffer.append(	"model.geom('"+geomSequence.name+"').run();"	+ "\n");
			
			//              long t_endGeomRun_ms = System.currentTimeMillis();
			buffer.append(	"var t_endGeomRun_ms = Date.now();"		+ "\n");

			//  System.out.println("geometry built in " + ((t_endGeomRun_ms - t_startGeomRun_ms) / 1000.0) + " seconds");
			buffer.append(	"print('geometry built in ' + ((t_endGeomRun_ms - t_startGeomRun_ms) / 1000.0) + ' seconds');"		+ "\n");
		}

		for (VCCMeshSequence meshSequence : vccModel.meshes) {
			//               model.mesh().create(meshSequence.name, meshSequence.geom.name);
			buffer.append(	"model.mesh().create('"+meshSequence.name+"','"+meshSequence.geom.name+"');"	+ "\n");
		}

		for (VCCPhysics physics : vccModel.physics) {
			switch (physics.type) {
			case CoefficientFormPDE: {
				VCCCoefficientFormPDE pde = (VCCCoefficientFormPDE) physics;
				//               model.physics().create(pde.name, "CoefficientFormPDE", pde.geom.name);
				buffer.append(	"model.physics().create('"+pde.name+"','CoefficientFormPDE','"+pde.geom.name+"');"	+ "\n");

				String selectionName = pde.geom.name+"_"+pde.geomFeature.name+"_dom";
				//               model.physics(pde.name)      .selection().named(selectionName);
				buffer.append(	"model.physics('"+pde.name+"').selection().named('"+selectionName+"');"	+ "\n");
				break;
			}
			case ConvectionDiffusionEquation: {
				VCCConvectionDiffusionEquation pde = (VCCConvectionDiffusionEquation) physics;
				//               model.physics().create(pde.name, "ConvectionDiffusionEquation", pde.geom.name);
				buffer.append(	"model.physics().create('"+pde.name+"','ConvectionDiffusionEquation','"+pde.geom.name+"');"	+ "\n");
				
				String c = pde.diffTerm_c;
				//               model.physics(pde.name)      .feature("cdeq1").setIndex("c", new String[] { c, "0", "0", c }, 0);
				buffer.append(	"model.physics('"+pde.name+"').feature('cdeq1').setIndex('c', "+tojs(new String[] { c,"0","0",c })+", 0);"	+ "\n");

				//               model.physics(pde.name)      .feature("cdeq1").setIndex("be", pde.advection_be, 0);
				buffer.append(	"model.physics('"+pde.name+"').feature('cdeq1').setIndex('be', "+tojs(pde.advection_be)+", 0);"	+ "\n");

				//               model.physics(pde.name)      .feature("cdeq1").setIndex("f", pde.sourceTerm_f, 0);
				buffer.append(	"model.physics('"+pde.name+"').feature('cdeq1').setIndex('f', '"+pde.sourceTerm_f+"', 0);"	+ "\n");

				//               model.physics(pde.name)      .field("dimensionless")  .field(pde.fieldName);
				buffer.append(	"model.physics('"+pde.name+"').field('dimensionless').field('"+pde.fieldName+"');"	+ "\n");

				//               model.physics(pde.name)      .feature("init1").set(pde.fieldName, pde.initial);
				buffer.append(	"model.physics('"+pde.name+"').feature('init1').set('"+pde.fieldName+"','"+pde.initial+"');"	+ "\n");

				String selectionName = pde.geom.name+"_"+pde.geomFeature.name+"_dom";
				//               model.physics(pde.name)      .selection().named(selectionName);
				buffer.append(	"model.physics('"+pde.name+"').selection().named('"+selectionName+"');"	+ "\n");
				break;
			}
			}

			// for (VCCPhysicsField field : physics.fields){
			// model.physics(physics.name).field(field.builtinName).field(field.name);
			// }
		}

		System.out.println("building mesh");
		//              long t_startMeshRun_ms = System.currentTimeMillis();
		buffer.append(	"var t_startMeshRun_ms = Date.now();"		+ "\n");

		//               model.mesh("mesh1").run();
		buffer.append(	"model.mesh('mesh1').run();"	+ "\n");

		//              long t_endMeshRun_ms = System.currentTimeMillis();
		buffer.append(	"var t_endMeshRun_ms = Date.now();"		+ "\n");

		//  System.out.println("mesh built in " + ((t_endMeshRun_ms - t_startMeshRun_ms) / 1000.0) + " seconds");
		buffer.append(	"print('mesh built in ' + ((t_endMeshRun_ms - t_startMeshRun_ms) / 1000.0) + ' seconds');"		+ "\n");

		//               model.study().create(vccModel.study.name);
		buffer.append(	"model.study().create('"+vccModel.study.name+"');"	+ "\n");

		for (VCCStudyFeature studyFeature : vccModel.study.features) {
			switch (studyFeature.type) {
			case Transient: {
				VCCTransientStudyFeature transientTime = (VCCTransientStudyFeature) studyFeature;
				//               model.study(vccModel.study.name)      .create("time", "Transient");
				buffer.append(	"model.study('"+vccModel.study.name+"').create('time', 'Transient');"	+ "\n");

				String range = "range(" + transientTime.startingTime + "," + transientTime.timeStep + ","
						+ transientTime.endTime + ")";
				//               model.study(vccModel.study.name)      .feature("time").set("tlist", range);
				buffer.append(	"model.study('"+vccModel.study.name+"').feature('time').set('tlist', '"+range+"');"	+ "\n");
				break;
			}
			default: {
				throw new RuntimeException("unknown studyFeature type " + studyFeature.type.name());
			}
			}
			// model.study(study.name).create(studyFeature.name,
			// studyFeature.type.name());
			// for (VCCPhysics activePhysics : studyFeature.activePhysics){
			// model.study(study.name).feature(studyFeature.name).activate(activePhysics.name);
			// }
		}

		//               model.sol().create("sol1");
		buffer.append(	"model.sol().create('sol1');"	+ "\n");

		//               model.sol("sol1").study(vccModel.study.name);
		buffer.append(	"model.sol('sol1').study('"+vccModel.study.name+"');"	+ "\n");

		//               model.sol("sol1").attach(vccModel.study.name);
		buffer.append(	"model.sol('sol1').attach('"+vccModel.study.name+"');"	+ "\n");

		//               model.sol("sol1").create("st1", "StudyStep");
		buffer.append(	"model.sol('sol1').create('st1', 'StudyStep');"	+ "\n");

		//               model.sol("sol1").create("v1", "Variables");
		buffer.append(	"model.sol('sol1').create('v1', 'Variables');"	+ "\n");

		//               model.sol("sol1").create("t1", "Time");
		buffer.append(	"model.sol('sol1').create('t1', 'Time');"	+ "\n");

		//               model.sol("sol1").feature("t1").create("fc1", "FullyCoupled");
		buffer.append(	"model.sol('sol1').feature('t1').create('fc1', 'FullyCoupled');"	+ "\n");

		//               model.sol("sol1").feature("t1").feature().remove("fcDef");
		buffer.append(	"model.sol('sol1').feature('t1').feature().remove('fcDef');"	+ "\n");

		//               model.result().export().create("data1", "Data");
		buffer.append(	"model.result().export().create('data1', 'Data');"	+ "\n");

		//               model.sol("sol1").attach(vccModel.study.name);
		buffer.append(	"model.sol('sol1').attach('"+vccModel.study.name+"');"	+ "\n");

		//              long t_beginsolver_ms = System.currentTimeMillis();
		buffer.append(	"var t_beginsolver_ms = Date.now();"		+ "\n");

		// System.out.println("running solver");
		buffer.append(	"print('running solver');"		+ "\n");
		
		//               model.sol("sol1").runAll();
		buffer.append(	"model.sol('sol1').runAll();"	+ "\n");		
		
		//              long t_endsolver_ms = System.currentTimeMillis();
		buffer.append(	"var t_endsolver_ms = Date.now();"		+ "\n");

		// System.out.println("solver finished in " + ((t_endsolver_ms - t_beginsolver_ms) / 1000.0) + " seconds");
		buffer.append(	"print('solver finished in ' + ((t_endsolver_ms - t_beginsolver_ms) / 1000.0) + ' seconds');"	+ "\n");
		
		//               model.result().export("data1").label("Data 2");
		buffer.append(	"model.result().export('data1').label('Data 2');"	+ "\n");		

		//               model.result().export("data1").set("struct", "sectionwise");
		buffer.append(	"model.result().export('data1').set('struct', 'sectionwise');"	+ "\n");		

		//               model.result().export("data1").set("filename", reportFile.getAbsolutePath());
		String reportFileName = StringEscapeUtils.escapeEcmaScript(reportFile.getAbsolutePath());
		buffer.append(	"model.result().export('data1').set('filename', '"+reportFileName+"');"	+ "\n");		

		ArrayList<String> units = new ArrayList<String>();
		ArrayList<String> descriptions = new ArrayList<String>();
		ArrayList<String> expressions = new ArrayList<String>();
		for (VCCPhysics physics : vccModel.physics) {
			if (physics instanceof VCCConvectionDiffusionEquation) {
				VCCConvectionDiffusionEquation pde = (VCCConvectionDiffusionEquation) physics;
				units.add("1");
				descriptions.add(pde.fieldName);
				expressions.add(pde.fieldName);
			}
		}
		//               model.result().export("data1").set("unit", units.toArray(new String[0]));
		buffer.append(	"model.result().export('data1').set('unit',"+tojs(units.toArray(new String[0]))+");"	+ "\n");		

		//               model.result().export("data1").set("descr", descriptions.toArray(new String[0]));
		buffer.append(	"model.result().export('data1').set('descr',"+tojs(descriptions.toArray(new String[0]))+");"	+ "\n");		

		//               model.result().export("data1").set("expr", expressions.toArray(new String[0]));
		buffer.append(	"model.result().export('data1').set('expr',"+tojs(expressions.toArray(new String[0]))+");"	+ "\n");		

		System.out.println("about to run() report");
		//               model.result().export("data1").run();
		buffer.append(	"model.result().export('data1').run();"	+ "\n");		

		//              long t_endexport_ms = System.currentTimeMillis();
		buffer.append(	"var t_endexport_ms = Date.now();"		+ "\n");

		// System.out.println("export finshed in " + ((t_endexport_ms - t_endsolver_ms) / 1000.0) + " seconds");
		buffer.append(  "print('export finshed in ' + ((t_endexport_ms - t_endsolver_ms) / 1000.0) + ' seconds');"	+ "\n");

		//               model.save(javaFile.getAbsolutePath(), "java");
		String javaFileName = StringEscapeUtils.escapeEcmaScript(javaFile.getAbsolutePath());
		buffer.append(	"model.save('"+javaFileName+"','java');"	+	"\n");
		
		//               model.save(mphFile.getAbsolutePath());
		String mphFileName = StringEscapeUtils.escapeEcmaScript(mphFile.getAbsolutePath());
		buffer.append(	"model.save('"+mphFileName+"');"	+	"\n");

		//                                     ModelUtil.disconnect();
		buffer.append(	"com.comsol.model.util.ModelUtil.disconnect();"	+	"\n");
		
		String script = buffer.toString();
		
		System.out.println(script);
//		RhinoScriptEngineFactory factory = new RhinoScriptEngineFactory();
//		factory.
//		ContextFactory factory = new ContextFactory();
//		Main dbg = new Main("script");
//		dbg.attachTo(factory);
//		Context context = factory.
//		.
//		ScriptContext scriptContext = new ScriptContext
		engine.eval(script);
	}
	
	@Override
	public void connectComsol(){
	}
	
	@Override
	public void solve(VCCModel vccModel, File reportFile, File javaFile, File mphFile){
		ClassLoader origClassLoader = null;
		File comsolRootDir = PropertyLoader.getRequiredDirectory(PropertyLoader.comsolRootDir);
		File comsolJarDir = PropertyLoader.getRequiredDirectory(PropertyLoader.comsolJarDir);
		try {
			origClassLoader = Thread.currentThread().getContextClassLoader();
			System.setProperty("cs.root", comsolRootDir.getAbsolutePath());
			
			if (comsolClassloader==null){
				ArrayList<URL> urls = new ArrayList<URL>();
				for (File f : comsolJarDir.listFiles()){
					if (f.isFile() && f.getName().endsWith(".jar")){
						try {
							urls.add(f.toURI().toURL());
						} catch (MalformedURLException e) {
							e.printStackTrace();
							throw new RuntimeException("failed to get URL for comsol dependency "+f.getAbsolutePath());
						}
					}
				}
				comsolClassloader = new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getSystemClassLoader().getParent());
			}
			Thread.currentThread().setContextClassLoader(comsolClassloader);
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("nashorn");
			run(engine, vccModel, reportFile, javaFile, mphFile);
		} catch (Exception e) {
			e.printStackTrace(System.err);
		} finally {
			Thread.currentThread().setContextClassLoader(origClassLoader);
			System.out.println("disconnecting");
			disconnectComsol();
			System.out.println("disconnected");
		}
	}
	
	@Override
	public void disconnectComsol(){
	}


	
}
