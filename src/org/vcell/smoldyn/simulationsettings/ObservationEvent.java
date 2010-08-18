package org.vcell.smoldyn.simulationsettings;

import org.vcell.smoldyn.simulation.SimulationUtilities;
import org.vcell.smoldyn.simulationsettings.util.EventTiming;
import org.vcell.smoldyn.simulationsettings.util.Filehandle;


/**
 * An ObservationEvent allows the user to record data from the simulation and save
 * it to a file at predetermined times during the simulation.  Possibilities include
 * saving the location and type of each molecule and saving the total number of
 * each type of molecule.
 * 
 * <table border="2">
 * <tr><th width="20%">command name and syntax</th><th>new name</th><th>description</th></tr>
 * <tr><td>molcountheader filename</td><td>unused</td>
 * <td>prints one line of display with the word “time” and then the name of each molecule species. This is intended to be used as a 
 * header line for the molcount, molcountinbox, etc. commands</td>
 * </tr>
 * <tr><td>molcount filename</td><td>TOTAL_MOLECULES_BY_TYPE</td>
 * <td>one line of display is printed to the listed file, giving the time and the number of molecules for 
 * each molecular species. Molecule states are ignored. The ordering used is the same as was given in the species command</td>
 * </tr>
 * <tr><td>molcountinbox xlow xhigh filename<hr />
 * molcountinbox xlow xhigh ylow yhigh filename<hr />
 * molcountinbox xlow xhigh ylow yhigh zlow zhigh filename</td><td>TOTAL_MOLECULES_BY_TYPE_IN_BOX</td>
 * <td>one line of display is printed to the listed file, giving the time and the number of molecules that are within the box that is 
 * defined by the low and high parameter, for each molecular species. Molecule states are ignored. The ordering used is the same as was 
 * given in the names command</td>
 * </tr>
 * <tr><td>molcountincmpt compartmentname filename</td><td>TOTAL_MOLECULES_IN_COMPARTMENT</td>
 * <td>one line of display is printed to the listed file, giving the time and the number of molecules that are within the compartment 
 * (compartmentname) for each molecular species. Only solutionphase molecules are listed. The ordering used is the same as was given in
 * the names command</td>
 * </tr>
 * <tr><td>molcountincmpts compartment1 compartment1 ... compartmentn filename</td><td>unused</td>
 * <td>one line of display is printed to the listed file, giving the time and the number of molecules that are within each of the 
 * compartments listed, for each molecular species. Up to 16 compartments may be listed. Only solution-phase molecules are reported to 
 * the output. The molecule ordering used is the same as was given in the names command</td></tr>
 * <tr><td>molcountincmpt2 compartment state filename</td><td>unused</td>
 * <td>Identical to molcountincmpt except that this counts molecules that are in state (state). Entering state as “all” means that 
 * molecules of all states are counted. Note that the surfaces that bound a compartment are included in that compartment</td></tr>
 * <tr><td>molcountonsurf surface filename</td><td>unused</td>
 * <td>one line of display is printed to the listed file, giving the time and the number of molecules that are bound to the surface 
 * surface for each molecular species. The molecule state is not printed. The ordering used is the same as was given in the names command
 * </td></tr>
 * <tr><td>molcountspace name[(state)] axis low1 high1 bins average filename<hr />
 * molcountspace name[(state)] axis low1 high1 bins low2 high2 average filename<hr />
 * molcountspace name[(state)] axis low1 high1 bins low2 high2 low3 high3 average filename</td><td>unused</td>
 * <td>measures a line profile of molecules. It only counts molecules of type name, with an optional state specification, although name 
 * and/or state can be “all”. The line profile is along axis number axis, which is a number between 0 and the system dimensionality minus 
 * 1, extends from low to high, and is comprised of bins equally spaced bins (i.e. it’s a histogram). These bins extend exactly from low 
 * to high, and thus do not count any molecules that are outside this range. For two dimensions, the line width and lateral position are 
 * specified with another pair of low and high values; for three dimensions, two more pairs of low and high values are required which now 
 * specify the sides of a rectangular cross-section tube. The volume investigated includes all edges. To illustrate the sequence of 
 * parameters, suppose the command is used in a 3-D system to show concentration variation along the y-axis. In this case, axis is 1, the 
 * first low and high indicate the ends of the measurement range along the y-axis, the next low and high indicate the domain on the 
 * xdirection, and the third low and high indicate the domain on the zdirection.  Set the average input to 0 to not use averaging, in 
 * which case there is output at every command execution. Otherwise, this only produces an output every average iterations, at which point 
 * it outputs means that were collected over the preceding iterations. At each output time, the command outputs a single line of text to 
 * filename with the time followed by the numbers (or average numbers) of the specified molecules in each histogram bin</td></tr>
 * <tr><td>listmols filename</td><td>LIST_ALL_MOLECULES</td>
 * <td>This prints out the identity, state, and location of every molecule in the system to the listed file name, using a separate line of 
 * text for each molecule</td></tr>
 * <tr><td>listmols2 filename</td><td>unused</td>
 * <td>This is very similar to listmols but has a slightly different output format.  Each line of text is preceded by the “time counter”, 
 * which is an integer that starts at 1 and is incremented each time the routine is called. Also, the names and states of molecules are 
 * not printed, but instead the identity and state numbers are printed</td></tr>
 * <tr><td>listmols3 name[(state)] filename</td><td>unused</td>
 * <td>This is identical to listmols2 except that it only prints information about molecules of type name. state is optional; name and/or 
 * state can be “all”</td></tr>
 * <tr><td>molpos name[(state)] filename</td><td>unused</td>
 * <td>This prints out the time and then the positions of all molecules of type name on a single line of text, to the listed filename. 
 * state is optional; name and/or state can be “all”</td></tr>
 * <tr><td>molmoments name[(state)] filename</td><td>unused</td>
 * <td>This prints out the positional moments of the molecule type given to the listed file name. All the moments are printed on a single 
 * line of text; they are the number of molecules, the mean position vector (dim values), and the variances on each axis and combination 
 * of axes (dim2 values). state is optional; neither name nor state can be “all”</td></tr>
 * <tr><td>savesim filename</td><td>SAVE_SIMULATION_STATE</td>
 * <td>This writes the complete state of the current system to the listed file name, in a format that can be loaded in later as a 
 * configuration file. Note that minor file editing is often desirable before simulating a file saved in this manner. In particular, the 
 * saved file will declare its own name as an output file name, which will erase the configuration file</td></tr>
 * <tr><td>meansqrdisp name[(state)] dim filename</td><td>unused</td>
 * <td>This function is used to measure mean square displacements (diffusion rates) of molecules of type name, along dimension dim, 
 * printing the results to filename. When it is first invoked, it records the positions of all molecules of type name. Then, and every 
 * subsequent time it is called, it compares the current positions of all molecules that still exist to the old ones, calculates the 
 * average squared displacement, and prints the time and that number to a single line in the output file. If dim is “all”, this sums the 
 * mean square displacement for all dimensions, otherwise dim should be a dimension number. As of version 1.73, this accounts for periodic 
 * boundaries. state is optional; neither name nor state can be “all”. As of version 1.84, this prints out three numbers in each line: 
 * (see manual)</td></tr>
 * <tr><td>meansqrdisp2 name[(state)] dim start report max_mol max_moment filename</td><td>unused</td>
 * <td>This function is an expanded version of meansqrdisp. As above, it measures mean square displacements of molecules of type name, 
 * along dimension dim, and prints the results to filename. The start and report arguments control when this command starts tracking 
 * molecules and when it reports their mean square displacements, respectively. For start, enter ‘i’ to track molecules that exist when 
 * the command is initially invoked, enter ‘c’ to track those that are created after the first call, and enter ‘a’ (all) to track both 
 * sets of molecules. For report, enter ‘e’ to report on every molecule that is being tracked, or ‘r’ to report on only those that reacted
 * since the command was last called. In this latter case, the position that is used for a reacted molecule is its most recently tracked 
 * position, since it no longer exists. For example, if you want to see how far molecules diffuse between their creation in one reaction 
 * and their destruction in another reaction, set start to ‘c’ and report to ‘r’. Or, set start to ‘i’ and report to ‘e’ for this function 
 * to be identical to meansqrdisp. It can track up to max_mol molecules. This function prints out the time and then results for all 
 * moments, even and odd, (see manual). This command accounts for periodic boundaries. state is optional; neither name nor state can be 
 * “all”</td></tr>
 * <tr><td>diagnostics type</td><td>unused</td>
 * <td>Displays diagnostics about various parts of the data structures to the screen. These are identical to those that are displayed at 
 * program initialization, but for later times. The options for the type word are: “simulation”, “wall”, “molecule”, “surface”, “command”, 
 * “box”, “reaction”, “compartment”, “port”, “check”, and “all”</td></tr>
 * </table>
 * 
 * 
 * @author mfenwick
 *
 */
public class ObservationEvent {

	private final EventTiming eventtiming;
	private final EventType eventtype;
	private final Filehandle filehandle;
	
	
	/**
	 * An ObservationEvent needs timing parameters, eventtype, and a filehandle.  The
	 * timing parameters determine when the event happens and how often, the event type
	 * determines what happens, and the filehandle says what to do with the data.  The 
	 * filehandle may represent a file on disk, or a source such as the screen (STDOUT).
	 * 
	 * @param eventtiming
	 * @param eventtype
	 * @param filehandle
	 */
	public ObservationEvent(EventTiming eventtiming, EventType eventtype, Filehandle filehandle) {
		SimulationUtilities.checkForNull("argument to observation event constructor", eventtiming, eventtype, filehandle);
		this.eventtiming = eventtiming;
		this.eventtype = eventtype;
		this.filehandle = filehandle;
	}
	
	
	public EventTiming getEventTiming() {
		return eventtiming;
	}
	
	public EventType getEventType() {
		return this.eventtype;
	}
	
	public Filehandle getFilehandle() {
		return this.filehandle;
	}
	
	
	
	/**
	 * TODO
	 * @author mfenwick
	 *
	 */
	public static enum EventType {
		
		TOTAL_MOLECULES_BY_TYPE (new Option [] {}),
		TOTAL_MOLECULES_BY_TYPE_IN_BOX (new Option [] {Option.XLOW, Option.XHIGH, Option.YLOW, Option.YHIGH,
				Option.ZLOW, Option.ZHIGH}),
		TOTAL_MOLECULES_IN_COMPARTMENT (new Option [] {Option.COMPARTMENTNAME}),
		TOTAL_MOLECULES_ON_SURFACE (new Option [] {Option.SURFACENAME}),
		//LINE_PLOT
		LIST_ALL_MOLECULES (new Option [] {}),
		SAVE_SIMULATION_STATE (new Option [] {}),
		;
		
		private final Option [] options;
		
		public Option [] getOptions() {
			return this.options;
		}
		
		private EventType(Option [] args) {
			this.options = args;
		}
		
	}

	
	
	/**
	 * @author mfenwick
	 * 
	 * The possible options that may be useful for specifying simulation observation.
	 *
	 */
	private static enum Option {
		XLOW,
		XHIGH,
		YLOW,
		YHIGH,
		ZLOW,
		ZHIGH,
		COMPARTMENTNAME,
		STATE,
		SURFACENAME,
		SPECIESNAME,
		AXIS,
		;
	}
}
