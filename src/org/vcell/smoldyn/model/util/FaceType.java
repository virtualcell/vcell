package org.vcell.smoldyn.model.util;

import org.vcell.smoldyn.model.Surface;

/**
 * Smoldyn sees {@link Panel}s and {@link Surface}s as having two faces: front, and back.  These faces are used to determine things such
 * as one which side of a Surface a reaction occurs.  If one wants to specify that something occurs on both sides of a Surface, then 
 * FaceType.both may be used.
 * 
 * @author mfenwick
 *
 */
public enum FaceType {
	front, 
	back, 
	both,
}
