package org.vcell.smoldyn.inputfile;



/**
 * Smoldyn keywords.  They are broken up into enums, which act as namespaces to make it easier to find them.
 * 
 * @author mfenwick
 *
 */
public class SmoldynFileKeywords {
	
	public static enum ConfigStatement {
		read_file,
		end_file,
	};
	
	public static enum Space {
		dim,
		boundaries,
		low_wall,
		high_wall,
	};
	
	public static enum Time {
		time_start,
		time_stop,
		time_step,
	};
	
	public static enum Molecule {
		species,
		max_mol,
		mol,
		surface_mol,
		compartment_mol,
		molecule_lists,
		mol_list,
		difc,
		difm,
		drift,
	};
	
	public static enum Graphics {
		graphics,
		graphic_iter,
		graphic_delay,
		frame_thickness,
		frame_color,
		grid_thickness,
		grid_color,
		background_color,
		display_size,
		color,
		tiff_iter,
		tiff_name,
		tiff_min,
		tiff_max,
		light,
	};
	
	public static enum Runtime {
		output_root,
		output_files,
		output_file_number,
		cmd,
		b,
		a,
		e,
		at,//is actually @ in config file
		n,
		i,
		j,
		x,
	};
	
	public static enum Surface {
		max_surface,
		start_surface,
		action,
		rate,
		rate_internal,
		color,
		thickness,
		stipple,
		polygon,
		shininess,
		max_panels,
		panel,
		jump,
		neighbors,
		unbounded_emitter,
		end_surface,
		epsilon,
		neighbor_dist,
		front, 
		back,
	};
	
	public static enum Compartment {
		max_compartment,
		start_compartment,
		end_compartment,
		surface,
		point,
		compartment,
		equal,
		and,
		xor,
		or,
		ornot,
		equalnot,
		andnot,
	};
	
	public static enum Reaction {
		reaction,
		reaction_cmpt,
		reaction_surface,
		binding_radius,
		product_placement,
	};
	
	public static enum ProductPlacementType {
		irrev		("no parameters"),
		pgem		("one parameter"),
		pgemmax		("one parameter"),
		pgemmaxw	("one parameter"),
		ratio		("one parameter"),
		unbindrad	("one parameter"),
		pgem2		("one parameter"),
		pgemmax2	("one parameter"),
		ratio2		("one parameter"),
		offset		("product molecule name followed by vector of size (number of dimensions)"),
		fixed		("");
		
		private String descriptor;
		
		private ProductPlacementType(String descriptor) {
			this.descriptor = descriptor;
		}
		
		public String getDescriptor() {
			return this.descriptor;
		}
	}
	
	public static enum Port {
		max_port,
		start_port,
		end_port,
		surface,
		face,
	};
	
	public static enum Network {
		read_network_rules,
	};
	
	public static enum SimulationSetting {
		rand_seed,
		accuracy,
		molperbox,
		boxsize,
		gauss_table_size,
		epsilon,
		neighbor_dist,
	};
	public static enum SimulationControl {
		stop,
		pause,
		beep,
		keypress,
		setrandseed,
		overwrite,
		incrementfile,
		ifno,
		ifless,
		ifmore,
		warnescapee,
		echo,
		molcountheader,
		molcount,
		molcountinbox,
		molcountincmpt,
		molcountincompt2,
		molcountonsurf,
		molcountspace,
		listmols,
		listmols2,
		listmols3,
		molpos,
		molmements,
		savesim,
		meansqrdisp,
		meansqrdisp2,
		diagnostics,
		set,
		pointsource,
		volumesource,
		movesurfacemol,
		killmol,
		killmolprob,
		millmolinsphere,
		millmoloutsidesystem,
		equilmol,
		fixmolcount,
		fixmolcountincmpt,
		fixmolcountonsurf,
		replacexyzmol,
		replacevolmol,
		modulatemol,
		react1,
		setrateint,
		setsurfcoeff,
		settimestep,
		porttransport,
		excludebox,
		excludesphere,
		includeecoli,
		writeOutput,
		printProgress,
	};
	
	public static enum surfaceactionstates {
		fsoln,
		bsoln,
		up,
		down,
		front,
		back,
		;
	}
}
