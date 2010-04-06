package org.vcell.sybil.models.bpimport;

/*   StageTwoPorter  --- by Oliver Ruebenacker, UCHC --- June 2008 to October 2009
 *   Second stage for porting from BioPAX to SBPAX,
 *   primarily making assumptions about stuff classes
 */

import org.vcell.sybil.models.bpimport.edges.QueryEdgeSBBox;
import org.vcell.sybil.models.bpimport.table.ProcessTableModel;
import org.vcell.sybil.models.sbbox.SBBox;
import org.vcell.sybil.models.sbbox.SBBox.RDFType;
import org.vcell.sybil.models.views.SBWorkView;
import org.vcell.sybil.rdf.reason.SYBREAMRules;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public class StageInitBuilder {

	public static void createStageInit(SBWorkView view) {
		SBBox box = view.box();
		for(RDFType unmodifiableType : view.unmodifiableTypes()) { 
			view.ustAssumption().addTypeItAppliesTo(unmodifiableType);
		}
		Model model = box.getData();
		box.performSYBREAMReasoning();
		StmtIterator infIter = box.getRdf().listStatements();
		while(infIter.hasNext()) {
			Statement statement = infIter.nextStatement();
			if(SYBREAMRules.potentialEntailment(statement)) { 
				model.add(statement); 
			}
		}
		QueryEdgeSBBox edgeBox = new QueryEdgeSBBox(box);
		view.setTableModel(new ProcessTableModel(edgeBox));
	}

}
