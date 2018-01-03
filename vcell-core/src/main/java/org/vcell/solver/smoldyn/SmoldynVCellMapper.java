package org.vcell.solver.smoldyn;

import java.util.Objects;

public class SmoldynVCellMapper {

	/**
	 * keywords for Smoldyn C  code
	 */
	public enum SmoldynKeyword {
			max_species,
			species,
			difc,
			drift,

			graphics,
			opengl,
			opengl_good,
			color,
			display_size,
			grid_color,
			background_color,
			grid_thickness,
			frame_color,
			frame_thickness,
			text_display,
			text_color,
			time,

			time_start,
			time_stop,
			time_step,

			dim,
			max_compartment,
			max_surface,
			boundaries,
			low_wall,
			high_wall,
			r,
			a,
			p,

			start_surface,
			end_surface,
			action,
			front,
			back,
			both,
			all,
			reflect,
			absorb,
			max_panels,
			panel,
			rect,
			tri,
			neighbors,

			start_compartment,
			end_compartment,
			surface,
			point,
			polygon,
			edge,
			face,

			reaction,
			reaction_cmpt,
			reaction_surface,
			//The rate constant for transitions from state1 to state2 of molecules at surface;
			//The membrane reaction with reactants and products all in volume, use 'rate' instead of using 'reaction' keyword.
			rate,
			//The binding radius is used for bimolecular membrane reaction(two reactants all on membrane)
			binding_radius,

			max_mol,
			compartment_mol,
			surface_mol,
			mol,

	//		the possible molecular states
			up,
			down,
			solution,
	//		front,
	//		back,
			fsoln,
			bsoln,
	//		all,

			output_files,

			cmd,
			B,
			E,
			N,
	//		one line of display is printed to the listed file, giving the time and the number
	//		of molecules for each molecular species. Molecule states are ignored.
	//		The ordering used is the same as was given in the species command.
			molcount,
	//		This prints out the identity, state, and location of every molecule in the
	//		system to the listed file name, using a separate line of text for each
	//		molecule.
			listmols,
	//		This is very similar to listmols but has a slightly different output format.
	//		Each line of text is preceded by the "time counter", which is an integer
	//		that starts at 1 and is incremented each time the routine is called. Also, the
	//		names and states of molecules are not printed, but instead the identity and
	//		state numbers are printed.
			listmols2,
			killmoloutsidesystem,
			warnescapee,
			output_file_number,
			incrementfile,
			killmolincmpt,
			killmol,

			accuracy,
			boxsize,
			gauss_table_size,
			rand_seed,

			end_file,
		}

	/**
	 * the keyword we're going to use to map particles to membrane -- logical candidates are {@link SmoldynKeyword#front}
	 * and {@link SmoldynKeyword#back} based on how C code handles these cases
	 * Dan: after testing, it seems that the correct choice is {@link SmoldynKeyword#up}
	 */
	public static final SmoldynKeyword MAP_PARTICLE_TO_MEMBRANE = SmoldynKeyword.up;

	/**
	 * symbol used to prefix keyword, based on Smoldyn C program conventions
	 */
	private static final char LEAD = '(';
	/**
	 * symbol used to suffix keyword, based on Smoldyn C program conventions
	 */
	private static final char TRAIL = ')';

	/**
	 * convert vcell name to smoldyn in standard manner
	 * @param name not null
	 * @param keyword not null
	 * @return new String
	 */
	public static String vcellToSmoldyn(String name, SmoldynKeyword keyword) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(keyword);
		return name + LEAD + keyword + TRAIL;
	}

	/**
	 * convert smoldyn name to vcell in standard manner
	 * @param name not null
	 * @param keyword not null
	 * @return new String if matches or null if not
	 */
	public static String smoldynToVCell(String name, SmoldynKeyword keyword) {
		Objects.requireNonNull(name);
		Objects.requireNonNull(keyword);
		String smolString = keyword.toString();
		int kplace = name.lastIndexOf(smolString);
		if (kplace >0 ) {
			char leading = name.charAt(kplace - 1);
			char trail = name.charAt(kplace + smolString.length());
			if (leading == LEAD && trail == TRAIL) {
				return name.substring(0,kplace - 1);
			}
		}
		return null;
	}

}
