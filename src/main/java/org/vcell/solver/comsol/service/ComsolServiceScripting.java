package org.vcell.solver.comsol.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.vcell.solver.comsol.model.VCCConvectionDiffusionEquation;
import org.vcell.solver.comsol.model.VCCGeomFeature;
import org.vcell.solver.comsol.model.VCCGeomFeature.Keep;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCBlock;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCCircle;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCDifference;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCIntersectionSelection;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCMove;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCScale;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCSphere;
import org.vcell.solver.comsol.model.VCCGeomFeature.VCCSquare;
import org.vcell.solver.comsol.model.VCCGeomSequence;
import org.vcell.solver.comsol.model.VCCMeshSequence;
import org.vcell.solver.comsol.model.VCCModel;
import org.vcell.solver.comsol.model.VCCModelNode;
import org.vcell.solver.comsol.model.VCCPhysics;
import org.vcell.solver.comsol.model.VCCPhysicsFeature;
import org.vcell.solver.comsol.model.VCCPhysicsFeature.VCCFluxBoundary;
import org.vcell.solver.comsol.model.VCCStudyFeature;
import org.vcell.solver.comsol.model.VCCTransientStudyFeature;
import org.vcell.util.PropertyLoader;

import cbit.vcell.resource.VCellConfiguration;

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
	
	private void run(ScriptEngine engine, VCCModel vccModel, File reportFile, File javaFile, File mphFile, File jsFile) throws ScriptException, IOException {
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
		
		boolean bBeforeSelections = true;
		for (VCCGeomSequence geomSequence : vccModel.geometrysequences) {
			//               model.geom().create(geomSequence.name, geomSequence.dim);
			buffer.append(	"model.geom().create('"+geomSequence.name+"',"+geomSequence.dim+");"	+ "\n");
			for (VCCGeomFeature geomFeature : geomSequence.geomfeatures) {
				switch (geomFeature.type) {
				case Circle: {
buffer.append(	"print('geomFeature Circle named "+geomFeature.name+"');"		+ "\n");
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
				case Sphere: {
buffer.append(	"print('geomFeature Sphere named "+geomFeature.name+"');"		+ "\n");
					VCCSphere sphere = (VCCSphere) geomFeature;
					//               model.geom(geomSequence.name)      .create(sphere.name, "Sphere");
					buffer.append(	"model.geom('"+geomSequence.name+"').create('"+sphere.name+"','Sphere');"	+ "\n");

					//               model.geom(geomSequence.name)      .feature(sphere.name)      .set("pos", sphere.pos);
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+sphere.name+"').set('pos', "+tojs(sphere.pos)+");"	+ "\n");

					//               model.geom(geomSequence.name)      .feature(sphere.name)      .set("r", sphere.r);
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+sphere.name+"').set('r', "+sphere.r+");"	+ "\n");
					break;
				}
				case Square: {
					VCCSquare square = (VCCSquare) geomFeature;
					//               model.geom(geomSequence.name)      .create(square.name, "Square");
					buffer.append(	"model.geom('"+geomSequence.name+"').create('"+square.name+"','Square');"	+ "\n");
//					//               model.geom(geomSequence.name)      .feature(square.name)      .set("selresult", "on");
//					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+square.name+"').set('selresult','on');"	+ "\n");
					break;
				}
				case Block: {
buffer.append(	"print('geomFeature Block named "+geomFeature.name+"');"		+ "\n");
					VCCBlock block = (VCCBlock) geomFeature;
					//               model.geom(geomSequence.name)      .create(block.name, "Block");
					buffer.append(	"model.geom('"+geomSequence.name+"').create('"+block.name+"','Block');"	+ "\n");

					//               model.geom(geomSequence.name)      .feature(block.name)      .set("pos", block.pos);
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+block.name+"').set('pos', "+tojs(block.pos)+");"	+ "\n");

					//               model.geom(geomSequence.name)      .feature(block.name)      .set("size", bock.size);
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+block.name+"').set('size', "+tojs(block.size)+");"	+ "\n");
					break;
				}
				case Difference: {
buffer.append(	"print('geomFeature Difference named "+geomFeature.name+"');"		+ "\n");
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
				case Scale: {
buffer.append(	"print('geomFeature Scale named "+geomFeature.name+"');"		+ "\n");
					VCCScale scale = (VCCScale) geomFeature;
					//               model.geom(geomSequence.name)      .create(diff.name, "Difference");
					buffer.append(	"model.geom('"+geomSequence.name+"').create('"+scale.name+"','Scale');"	+ "\n");
					
					ArrayList<String> inputNames = new ArrayList<String>();
					for (VCCGeomFeature feature : scale.input) {
						inputNames.add(feature.name);
					}
					String[] inputSet = inputNames.toArray(new String[0]);
					//               model.geom(geomSequence.name)      .feature(scale.name)      .selection("input").set(inputSet);
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+scale.name+"').selection('input').set("+tojs(inputSet)+");"	+ "\n");
					//               model.geom(geomSequence.name)      .feature(scale.name)      .set('factor', scale.factor);
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+scale.name+"').set('factor',"+tojs(scale.factor)+");"	+ "\n");
					break;
				}
				case Move: {
buffer.append(	"print('geomFeature Move named "+geomFeature.name+"');"		+ "\n");
					VCCMove move = (VCCMove) geomFeature;
					//               model.geom(geomSequence.name)      .create(diff.name, "Difference");
					buffer.append(	"model.geom('"+geomSequence.name+"').create('"+move.name+"','Move');"	+ "\n");
					
					ArrayList<String> inputNames = new ArrayList<String>();
					for (VCCGeomFeature feature : move.input) {
						inputNames.add(feature.name);
					}
					String[] inputSet = inputNames.toArray(new String[0]);

					//               model.geom(geomSequence.name)      .feature(move.name)      .selection("input").set(inputSet);
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+move.name+"').selection('input').set("+tojs(inputSet)+");"	+ "\n");
					
					//               model.geom(geomSequence.name)      .feature(move.name)      .set("displ", move.displ);
					buffer.append(	"model.geom('"+geomSequence.name+"').feature('"+move.name+"').set('displ',"+tojs(move.displ)+");"	+ "\n");
					break;
				}
				case IntersectionSelection: {
buffer.append(	"print('geomFeature IntersectionSelection named "+geomFeature.name+"');"		+ "\n");
					if (bBeforeSelections){
						bBeforeSelections = false;
						//                model.geom("geom1")                .run("fin");
						buffer.append(   "model.geom('"+geomSequence.name+"').run('fin');"		+ "\n");
					}
					VCCIntersectionSelection intersectionSelection = (VCCIntersectionSelection)geomFeature;
					//		          model.geom("geom1")                .create("intsel1", "IntersectionSelection");
					buffer.append(	 "model.geom('"+geomSequence.name+"').create('"+intersectionSelection.name+"', 'IntersectionSelection');"	+ "\n");
					
				    //	              model.geom("geom1")                .feature("intsel1")                       .set("entitydim", "2");
					buffer.append(	 "model.geom('"+geomSequence.name+"').feature('"+intersectionSelection.name+"').set('entitydim', '"+intersectionSelection.entitydim+"');"	+ "\n");

					ArrayList<String> inputNames = new ArrayList<String>();
					for (VCCGeomFeature feature : intersectionSelection.input) {
						inputNames.add(feature.name);
					}
					String[] inputSet = inputNames.toArray(new String[0]);

					//	              model.geom("geom1")                .feature("intsel1")                       .set("input", new String[]{"diffec", "celltranslation0"});
					buffer.append(	 "model.geom('"+geomSequence.name+"').feature('"+intersectionSelection.name+"').set('input',"+tojs(inputSet)+");"	+ "\n");
					break;
				}
				default: {
					throw new RuntimeException("unexpected Geometry Feature type " + geomFeature.type.name());
				}
				}
				if (!(geomFeature instanceof VCCIntersectionSelection)){
					//				  model.geom(geomSequence.name)		 .feature(geomFeature.name)		 .set("selresult", "on");
					buffer.append(   "model.geom('"+geomSequence.name+"').feature('"+geomFeature.name+"').set('selresult','on');"		+ "\n");
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
//			case CoefficientFormPDE: {
//				VCCCoefficientFormPDE pde = (VCCCoefficientFormPDE) physics;
////buffer.append(	"print('physics ConvectionDiffusionEquation for field name "+pde.fieldName+"');"		+ "\n");
//				//               model.physics().create(pde.name, "CoefficientFormPDE", pde.geom.name);
//				buffer.append(	"model.physics().create('"+pde.name+"','CoefficientFormPDE','"+pde.geom.name+"');"	+ "\n");
//
//				String selectionName = pde.geom.name+"_"+pde.geomFeature.name+"_dom";
//				//               model.physics(pde.name)      .selection().named(selectionName);
//				buffer.append(	"model.physics('"+pde.name+"').selection().named('"+selectionName+"');"	+ "\n");
//				break;
//			}
			case ConvectionDiffusionEquation: {
				VCCConvectionDiffusionEquation pde = (VCCConvectionDiffusionEquation) physics;
buffer.append(	"print('(0) physics ConvectionDiffusionEquation for field name "+pde.fieldName+"');"		+ "\n");
				//               model.physics().create(pde.name, "ConvectionDiffusionEquation", pde.geom.name);
				buffer.append(	"model.physics().create('"+pde.name+"','ConvectionDiffusionEquation','"+pde.geom.name+"');"	+ "\n");
				
buffer.append(	"print('(1) physics ConvectionDiffusionEquation for field name "+pde.fieldName+"');"		+ "\n");
				String c = pde.diffTerm_c;
				if (c!=null){
					if (vccModel.dim==2){
						//               model.physics(pde.name)      .feature("cdeq1").setIndex("c", new String[] { c, "0", "0", c }, 0);
						buffer.append(	"model.physics('"+pde.name+"').feature('cdeq1').setIndex('c', "+tojs(new String[] { c,"0","0",c })+", 0);"	+ "\n");
					}else if (vccModel.dim==3){
						//               model.physics(pde.name)      .feature("cdeq1").setIndex("c", new String[] { c, "0", "0", c }, 0);
						buffer.append(	"model.physics('"+pde.name+"').feature('cdeq1').setIndex('c', "+tojs(new String[] { c,"0","0","0",c,"0","0","0",c })+", 0);"	+ "\n");
					}
				}
				if (pde.advection_be!=null){
					//               model.physics(pde.name)      .feature("cdeq1").setIndex("be", pde.advection_be, 0);
					buffer.append(	"model.physics('"+pde.name+"').feature('cdeq1').setIndex('be', "+tojs(pde.advection_be)+", 0);"	+ "\n");
				}
				
buffer.append(	"print('(2) physics ConvectionDiffusionEquation for field name "+pde.fieldName+"');"		+ "\n");
				//               model.physics(pde.name)      .feature("cdeq1").setIndex("f", pde.sourceTerm_f, 0);
				buffer.append(	"model.physics('"+pde.name+"').feature('cdeq1').setIndex('f', '"+pde.sourceTerm_f+"', 0);"	+ "\n");

buffer.append(	"print('(3) physics ConvectionDiffusionEquation for field name "+pde.fieldName+"');"		+ "\n");
				//               model.physics(pde.name)      .field("dimensionless")  .field(pde.fieldName);
				buffer.append(	"model.physics('"+pde.name+"').field('dimensionless').field('"+pde.fieldName+"');"	+ "\n");

buffer.append(	"print('(4) physics ConvectionDiffusionEquation for field name "+pde.fieldName+"');"		+ "\n");
				//               model.physics(pde.name)      .feature("init1").set(pde.fieldName, pde.initial);
				buffer.append(	"model.physics('"+pde.name+"').feature('init1').set('"+pde.fieldName+"','"+pde.initial+"');"	+ "\n");

				String selectionName = null;
				if (pde.dim == vccModel.dim){
					selectionName = pde.geom.name+"_"+pde.geomFeature.name+"_dom";
				}else if (pde.dim == vccModel.dim-1){
					selectionName = pde.geom.name+"_"+pde.geomFeature.name+"_bnd";
				}
buffer.append(	"print('(5) physics ConvectionDiffusionEquation for field name "+pde.fieldName+", selection "+selectionName+"');"		+ "\n");
				//               model.physics(pde.name)      .selection().named(selectionName);
				buffer.append(	"model.physics('"+pde.name+"').selection().named('"+selectionName+"');"	+ "\n");
buffer.append(	"print('(6) physics ConvectionDiffusionEquation for field name "+pde.fieldName+", selection "+selectionName+"');"		+ "\n");
				
				for (VCCPhysicsFeature feature : pde.features){
					if (feature instanceof VCCFluxBoundary){
						VCCFluxBoundary fluxBoundary = (VCCFluxBoundary)feature;
						String boundarySelectionName = pde.geom.name+"_"+fluxBoundary.selection.name;
buffer.append(	"print('(0) physics ConvectionDiffusionEquation for field name "+pde.fieldName+", FluxBoundary name "+fluxBoundary.name+", selection name "+boundarySelectionName+"');"		+ "\n");
						//                model.physics(pde.name)      .create(fluxBoundary.name, "FluxBoundary", 2);
						buffer.append(   "model.physics('"+pde.name+"').create('"+fluxBoundary.name+"','FluxBoundary',"+fluxBoundary.dim+");"	+ "\n");
						
buffer.append(	"print('(1) physics ConvectionDiffusionEquation for field name "+pde.fieldName+", FluxBoundary name "+fluxBoundary.name+", selection name "+boundarySelectionName+"');"		+ "\n");
						//                model.physics(pde.name)      .feature(fluxBoundary.name)      .selection().named("geom1_intsel1");
						buffer.append(   "model.physics('"+pde.name+"').feature('"+fluxBoundary.name+"').selection().named('"+boundarySelectionName+"');"	+ "\n");
				
buffer.append(	"print('(2) physics ConvectionDiffusionEquation for field name "+pde.fieldName+", FluxBoundary name "+fluxBoundary.name+", selection name "+boundarySelectionName+"');"		+ "\n");
						//                model.physics(pde.name)      .feature(fluxBoundary.name)      .set("g", "5-RanC_nuc");
						buffer.append(   "model.physics('"+pde.name+"').feature('"+fluxBoundary.name+"').set('g', '"+fluxBoundary.flux_g+"');"	+ "\n");
buffer.append(	"print('(3) physics ConvectionDiffusionEquation for field name "+pde.fieldName+", FluxBoundary name "+fluxBoundary.name+", selection name "+boundarySelectionName+"');"		+ "\n");
					}
				}
				break;
			}
			default:{
				throw new RuntimeException("unsupported physics type "+physics.type.name()+" in COMSOL script builder");
			}
			}
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
		FileUtils.write(jsFile, script);
		System.out.println("script written to "+jsFile.getAbsolutePath()+" prior to execution");

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
		File comsolRootDir = VCellConfiguration.getFileProperty(PropertyLoader.comsolRootDir);
		File comsolJarDir = VCellConfiguration.getFileProperty(PropertyLoader.comsolJarDir);
		if (comsolRootDir==null || !comsolRootDir.exists()){
			throw new RuntimeException("path to COMSOL root directory \""+comsolRootDir+"\" not found, update preferences (or "+PropertyLoader.comsolRootDir+" in vcell.config)");
		}
		if (comsolJarDir==null || !comsolJarDir.exists()){
			throw new RuntimeException("path to COMSOL plugin directory \""+comsolJarDir+"\" not found, update preferences (or "+PropertyLoader.comsolJarDir+" in vcell.config)");
		}
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
			File jsFile = new File(javaFile.getParentFile(), javaFile.getName().replace(".java",".js"));
			run(engine, vccModel, reportFile, javaFile, mphFile, jsFile);
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
