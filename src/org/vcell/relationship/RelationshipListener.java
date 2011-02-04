package org.vcell.relationship;

import java.util.EventListener;

public interface RelationshipListener extends EventListener{
	
	void relationshipChanged(RelationshipEvent event);

}
