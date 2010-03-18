package org.vcell.sybil.models.bpimport;

/*   PortStage  --- by Oliver Ruebenacker, UCHC --- October 2009 to March 2010
 *   the stage of the port process
 */

public class PortStage {
	public static final PortStage stageInit = new PortStage();
	public static final PortStage stageProcesses = new PortStage();
	public static final PortStage stageCompartments = new PortStage();
	public static final PortStage stageModels = new PortStage();
	public static final PortStage stageBioModel = new PortStage();
	public static final PortStage stageSBML = new PortStage();
}