package org.vcell.pathway;

public class GroupNeighbor {
	
	protected final GroupObject groupObject;
	protected final BioPaxObject neighbor;
	protected final InteractionParticipant.Type type;

	public GroupNeighbor(GroupObject groupObject, BioPaxObject neighbor, InteractionParticipant.Type type) {
		this.groupObject = groupObject;
		this.neighbor = neighbor;
		this.type = type;
	}

	public GroupObject getGroupObject() { return groupObject; }
	public BioPaxObject getNeighbor() { return neighbor; }
	public InteractionParticipant.Type getType() { return type; }

	public boolean equals(Object object) {
		if(object instanceof GroupNeighbor) {
			GroupNeighbor ele = (GroupNeighbor) object;
			return groupObject.equals(ele.groupObject) 
			&& neighbor.equals(ele.neighbor) && type.equals(ele.type);
		}
		return false;
	}
	
	public String getLevel3PropertyName(){
		switch (type) {
		case PARTICIPANT: {
			return "Participant";
		}
		case COFACTOR: {
			return "Cofactor";
		}
		case CONTROLLED: {
			return "Controlled";
		}
		case CONTROLLER: {
			return "Controller";
		}
		case LEFT: {
			return "Left";
		}
		case RIGHT: {
			return "Right";
		}
		case TEMPLATE: {
			return "Template";
		}
		default:
			return "";
		}
	}

	public int hashCode() {
		return groupObject.hashCode() + neighbor.hashCode() + type.hashCode();
	}
}
