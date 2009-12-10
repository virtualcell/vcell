package org.vcell.sybil.actions.web;

/*   PathwayCommonsKeywordAction  --- by Oliver Ruebenacker, UCHC --- December 2009
 *   Launch a web request using command search from Pathway Commons
 */

import java.awt.Component;
import java.awt.event.ActionEvent;

import org.vcell.sybil.actions.ActionSpecs;
import org.vcell.sybil.actions.SpecAction;
import org.vcell.sybil.util.event.Accepter;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsRequest;
import org.vcell.sybil.util.http.pathwaycommons.PathwayCommonsResponse;
import org.vcell.sybil.util.http.pathwaycommons.search.PCKeywordRequest;
import org.vcell.sybil.workers.input.PathwayCommonsWorker;

@SuppressWarnings("serial")
public class PathwayCommonsKeywordAction extends SpecAction 
implements PathwayCommonsWorker.Client {

	public static interface KeywordProvider { public String keyword(); }
	
	protected PathwayCommonsKeywordAction.KeywordProvider keywordProvider;
	protected Accepter<PathwayCommonsResponse> responseAcceptor;
	
	public PathwayCommonsKeywordAction(PathwayCommonsKeywordAction.KeywordProvider keywordProvider, 
			Accepter<PathwayCommonsResponse> responseAcceptor) {
		super(new ActionSpecs("Keyword Query", "PC Keyword Query", 
				"Send keyword query to PathwayCommons"));
		this.keywordProvider = keywordProvider;
		this.responseAcceptor = responseAcceptor;
	}

	public void actionPerformed(ActionEvent arg0) { new PathwayCommonsWorker(this).run((Component)arg0.getSource()); }
	public PathwayCommonsRequest request() { return new PCKeywordRequest(keywordProvider.keyword()); }
	public void setResponse(PathwayCommonsResponse response) { responseAcceptor.accept(response); }
	
}