package cbit.vcell.export.gloworm.atoms;
/**
 * Insert the type's description here.
 * Creation date: (11/8/2005 7:27:55 PM)
 * @author: Ion Moraru
 */
public class ObjectSampleDescriptionEntry extends SampleDescriptionEntry {
	// aparently dummy
/**
 * Insert the method's description here.
 * Creation date: (11/8/2005 11:48:11 PM)
 */
public ObjectSampleDescriptionEntry() {
	size = 16;
}


/**
 * This method was created in VisualAge.
 * @param out java.io.DataOutputStream
 */
public boolean writeData(java.io.DataOutputStream out) {
	try {
		out.writeInt(size);
		out.writeInt(0); // dummy
		out.write(reserved);
		out.writeShort(dataReferenceIndex);
		return true;
	} catch (java.io.IOException e) {
		System.out.println("Unable to write: " + e.getMessage());
		e.printStackTrace();
		return false;
	}
}
}