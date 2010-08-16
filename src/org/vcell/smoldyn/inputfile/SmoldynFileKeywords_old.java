package org.vcell.smoldyn.inputfile;


/**
 * The *old* exhaustive/exhausting listing of Smoldyn's keywords, and some of their syntax.  Will mark as deprecated, if I ever figure
 * out how to do that.
 * 
 * @author mfenwick
 *
 */
public class SmoldynFileKeywords_old {
	/**
	 * config statements	 
	 *
	 * --> # comments
	 * --> /* comments (also use closing sequence)
	 * --> read_file <filename>					read <filename> and then return
	 * 		use this to read (and use the settings from) additional 
	 * 		configuration files
	 * --> end_file								end of this file
	 * 		(optionally) use to signify end of the configuration file
	 * 		the manual says that this statement is not necessary
	 * --> define <key> <substitution>			local macro replacement text
	 * 		define a C style macro.  there is no need for this
	 * 		only operates within its file, not within other files called by its file
	 * 		## DO NOT USE
	 * --> define_global <key> <substitution>	global macro replacement text
	 * 		define a C style macro.  there is no need for this
	 * 		operates within its file, and within other files called by its file
	 * 		## DO NOT USE
	 * --> undefine <key>						undefine a macro substitution
	 * 		undefine a C style macro.  unnecessary
	 * 		special arg:  <key> = 'all' undefines all macros
	 * 		## DO NOT USE
	 * --> ifdefine <key>						start conditional reading
	 * 		C style macro
	 * 		##  DO NOT USE
	 * --> ifundefine <key>						start conditional reading
	 * 		C style macro
	 * 		## DO NOT USE
	 * --> else									else condition for conditional reading
	 * 		C style macro
	 * 		## DO NOT USE
	 * --> endif								end conditional reading frame
	 * 		C style macro
	 * 		## DO NOT USE
	 */
	public final static String SINGLE_LINE_COMMENT 			= "#";
	public final static String MULTIPLE_LINE_COMMENT_OPEN 	= "/*";
	public final static String MULTIPLE_LINE_COMMENT_CLOSE 	= "*/";
	public final static String READ_FILE 					= "read_file";
	public final static String END_FILE 					= "end_file";
	
	
	/**
	 * space and time
	 * 
	 * --> dim <dimensions>
	 * 		dimensionality of system.  <dimensions> must be >= 1
	 * --> boundaries <dimension> <low coordinate> <high coordinate> [<type>]
	 * 		type: enum {'r', 't', 'a', 'p'}CAN BE REPLACED BY
	 * 				reflective, transparent, absorbing, periodic
	 * 		create upper and lower boundaries in dimension <dimension> at the specified
	 * 			coordinates.  recommendation:  use don't use optional <type> parameter
	 * 			if there are surfaces, otherwise use it.  mostly replaces statements
	 * 			low_wall and high_wall
	 * 		?must have boundaries or walls defined in order to define system volume?
	 * --> low_wall <dimension> <position> <type>
	 * 		replaced by boundaries
	 * --> high_wall <dimension> <position> <type>
	 * 		replaced by boundaries
	 * --> time_start <time>
	 * 		simulation starting time
	 * 		?must be number?
	 * --> time_stop <time>
	 * 		simulation ending time
	 * 		?must be number?
	 * --> time_step <time>
	 * 		simulation time step
	 * 		?must be positive number?
	 * --> time_now <time>
	 * 		confusing.  if before time_start, time_start = time_start.  if after 
	 * 			time_start, time_start = time_now
	 * 		## DO NOT USE
	 */
	//first four fall into Jim's geometry class
	public final static String DIM 			= "dim";
	public final static String BOUNDARIES 	= "boundaries";
	public final static String LOW_WALL 	= "low_wall";
	public final static String HIGH_WALL 	= "high_wall";

	public final static String TIME_START 	= "time_start";
	public final static String TIME_STOP 	= "time_stop";
	public final static String TIME_STEP 	= "time_step";
	
	
	/**
	 * Molecule/Molecules
	 * 
	 * --> max_species <int>
	 * 		## DO NOT USE   can be replaced by species command
	 * --> species <name>  
	 * 		## DO NOT USE   can be replaced by species <name1> <name2>... form
	 * --> max_names <int>
	 * 		## DO NOT USE   for backward compatibility
	 * --> name <name>
	 * 		## DO NOT USE   for back. comp.
	 * --> names <name1> <name2> ...
	 * 		## DO NOT USE   for back. comp.
	 * 
	 * --> species <name1> <name2> ...
	 * 		names of all species
	 * 		no spaces, must start with letter
	 * --> max_mol <int>
	 * 		maximum possible number of molecules (reserves memory for them)
	 * 		if actual number of molecules exceeds this, 'simulation terminates'
	 * 		!important:  allot more than is needed 'for processing'
	 * --> mol <number> <name> <coord1> <coord2> ... <coord(dimensionality)>
	 * 		specify starting location of <number> or molecules of <name> at point
	 * 			<coord1>, .....
	 * 		<coord1> should be number, or the letter 'u', which means a randomly chosen
	 * 			location from a uniform distribution
	 * 		or <coord1> can be 'a position range which is given as two numbers separated
	 * 			with a hyphen'
	 * --> surface_mol <number> <species(state)> <surface> <pshape> <panel> [<coord1> ...]
	 * 		place surface molecules.  (state): enum {'front', 'back', 'up', 'down'}
	 * 			placed on the surface with name <surface>, on panel whose shape is 
	 * 			<pshape> and whose name is <panel>
	 * 		if no coordinates, they are randomly placed on the given panels/surface
	 * 		may put 'all' for <surface>, <pshape>, and/or <panel>
	 * --> compartment_mol <number> <species> <compartment>
	 * 		make solution-phase molecules in a compartment
	 * --> molecule_lists <listname1> ....
	 * 		make lists of molecules with names
	 * --> mol_list <species> <listname>
	 * 		assign all solution molecules of type <species> to list <listname>
	 * 			or, set <species> to 'diffusing' or 'fixed' to specify all diffusing or
	 * 			fixed molecules, respectively
	 * --> mol_list <species(state)> <listname>
	 * 		same, but can also specify state: enum {'front', 'back', 'up', 'down'}
	 * 
	 * 
	 * for individual species
	 * --> difc <name> <value>
	 * 		isotropic solution diffusion coefficient.  default is 0
	 * --> difc <name(state)> <value>
	 * 		state: enum {'solution', 'front', 'back', 'up', 'down', 'all'}
	 * --> difm <name> <float1> <float2> ...
	 * 		if you want anisotropic diffusion, use this, and give enough floats
	 * 			so that there's dimensionality**2 floats
	 * --> difm <name(state)> <float1> <float2> ...
	 * 		if you want to specify states also
	 * --> drift <name> <float1> <float2> ...
	 * 		if you want a drift for your molecule.  Guess this is just a constant
	 * 			motion or something
	 * --> drift <name(state)> <float1> <float2> ...
	 * 		if you want to specify states also
	 */
	public final static String SPECIES 		= "species";
	public final static String MAX_MOL 		= "max_mol";
	public final static String MOL 			= "mol";
	public final static String SURFACE_MOL 	= "surface_mol";
	public final static String COMPARTMENT_MOL = "compartment_mol";
	public final static String MOLECULES_LISTS = "molecule_lists";
	public final static String MOL_LIST 		= "mol_list";
	public final static String DIFC 			= "difc";
	public final static String DIFM 			= "difm";
	public final static String DRIFT 			= "drift";
	
	
	/** Graphics stuff
	 * 
	 * --> graphics <graphics type>
	 * 		<graphics type> : enum {'none', 'opengl', 'opengl_good', 'opengl_better'}
	 * 			ranked from worst to best quality
	 * 		if this statement is not entered, no graphics are shown
	 * --> graphic_iter <time steps>
	 * 		<time steps> is an integer, and the number of steps between graphics updates
	 * --> graphic_delay <pause time>
	 * 		<pause time> is a float, and the amount of time that Smoldyn pauses between
	 * 			graphics updates (defaults to 0)
	 * --> frame_thickness <integer>
	 * 		thickness of frame drawn around simulation volume, defaults to 2
	 * --> frame_color <red> <green> <blue> [<alpha>]
	 * 		<red>, <green>, and <blue> must be between 0 and 1; they default to 1 (white)
	 * 			<alpha> 'is useless'
	 * --> grid_thickness <integer>
	 * 		thickness of the grid lines, defaults to 0 (which means they aren't drawn)
	 * --> grid_color <red> <green> <blue> [<alpha>]
	 * 		same as frame_color
	 * --> background_color <red> <green> <blue> [<alpha>]
	 * 		same as grid_color
	 * --> display_size <name> <size>
	 * 		set graphics size of a species of name <name> to size <size> (size is a float)
	 * 		defaults to 3 (0 means not drawn)
	 * --> display_size <name(state)> <size>
	 * 		specify size by state
	 * --> color <name> <red> <green> <blue>
	 * 		defaults to 0
	 * --> color <name(state)> <red> <green> <blue>
	 * 		specify molecule color by state
	 * --> tiff_iter <integer>
	 * 		'number of time steps that should be run between each automatic saving of a
	 * 			TIFF file'.  Defaults to 0, meaning they are not saved automatically
	 * --> tiff_name <name>
	 * 		root filename for TIFF files.  defaults to 'OpenGL'.  '<number>.tif' is 
	 * 			automatically appended
	 * --> tiff_min <integer>
	 * 		specify starting point of <number> under tiff_name. defaults to 1
	 * --> tiff_max <integer>
	 * 		largest possible value for <number> under tiff_name.  defaults to 999.
	 * 			once this is reached, no more pictures are saved
	 * --> light <number> <parameter> <value1> <value2> <value3> [<value4>]
	 * 		make a light source (only available if 'graphics' = 'opengl_better')
	 * 		0  <  <number>  <  7
	 * 		parameter:  enum {'ambient', 'diffuse', 'specular', 'position'}
	 * 		unsure of the semantics of the rest of this command (documentation unclear)
	 */
	public final static String GRAPHICS 			= "graphics";
	public final static String GRAPHIC_ITER 		= "graphic_iter";
	public final static String GRAPHIC_DELAY 		= "graphic_delay";
	public final static String FRAME_THICKNESS 	= "frame_thickness";
	public final static String FRAME_COLOR 		= "frame_color";
	public final static String GRID_THICKNESS 		= "grid_thickness";
	public final static String GRID_COLOR 			= "grid_color";
	public final static String BACKGROUND_COLOR 	= "background_color";
	public final static String DISPLAY_SIZE 		= "display_size";
	public final static String COLOR 				= "color";
	public final static String TIFF_ITER 			= "tiff_iter";
	public final static String TIFF_NAME 			= "tiff_name";
	public final static String TIFF_MIN 			= "tiff_min";
	public final static String TIFF_MAX 			= "tiff_max";
	public final static String LIGHT 				= "light";
	
	
	
	/**
	 *  Surfaces
	 * 
	 * --> max_surface <integer>
	 * 		specify maximum number of surfaces
	 * --> start_surface [<name>]
	 * 		if <name> is not used, can later be used inside declaration -- but DO NOT DO THIS
	 * 		everything between here and 'end_surface' deals with this surface
	 * --> new_surface <name>
	 * 		## DO NOT USE -- can be replaced by start_surface
	 * 
	 * the following are used anywhere with a 'surface <name>' qualifier, or 
	 * inside a start_surface block without the qualifier
	 * --> surface --> name <name>
	 * 		only used if start_surface is used without a name
	 * 		## DO NOT USE
	 * --> surface --> action <face> <species> <action>
	 * 		specify 'behavior of solution-phase molecules of species <species> 
	 * 			when they collide with face <face> of this surface'
	 * 		face: enum{'front', 'back', 'both'}
	 * 		species name:  name of previously declared species, or 'all'
	 * 		action: enum{'reflect', 'absorb', 'transmit', 'jump', 'port', 'periodic', 'multiple'}
	 * 		defaults to 'reflect'
	 * 		if 'multiple', rates must be set with rate command
	 * --> surface --> rate <species> <state1> <state2> <rate constant> [<new_species>]
	 * 		<new_species> is unnecessary
	 * 		specify what transition from <state1> to <state2> at rate <rate constant>
	 * 			for <species>
	 * 		<state1>, <state2>:  enum {'fsoln', 'bsoln', 'front', 'back', 'up', 'down'}
	 * --> surface --> rate_internal ..............
	 * 		same syntax and semantics as above, except that <rate constant> is the 
	 * 			probability of action at each collision
	 * 
	 * surface graphics
	 * --> surface --> color <face> <red> <green> <blue> [<alpha>]
	 * 		face: enum {'front', 'back', 'both'}
	 * --> surface --> thickness <float>
	 * 		'Boldness of the surface in pixels for drawing purposes. This is only
	 *		relevant for 1-D and 2-D simulations, and for 3-D simulations in which
	 *		surfaces are drawn with just vertices or edges and not faces.'
	 * --> surface --> stipple <factor> <pattern>
	 * 		figure this out later
	 * --> surface --> polygon <face> <drawmode>
	 * 		later
	 * --> surface --> shininess <face> <value>
	 * 		later
	 * 
	 * --> surface --> max_panels <shape> <int>
	 * 		define max number of panels of shape <shape> that this surface may have as <int>
	 * 		<shape>: enum {'rect', 'tri', 'sph', 'cyl', 'hemi', 'disk'}
	 * --> surface --> panel <shape> <args> [<name>]
	 * 		define a new panel for this surface of shape <shape>, where <args> depends
	 * 			on the shape.  give it an optional name in order to be able to use it
	 * 			later for a jump surface (default names are 0, 1, 2, ...)
	 * --> surface --> jump <name1> <face1> [<]-> <name2> <face2>
	 * 		make a jump condition from <face1> of panel <name1> to <face2> of panel <name2>
	 * 		<face1>, <face2> : enum {'front', 'back'}
	 * 		shapes of <name1> and <name2> must be identical
	 * 		use double-header arrow for bidirectional jumpage
	 * --> surface --> neighbors <panel> <neighbor 1> ... <neighbor n>
	 * 		define neighbors of a panel.  surface-bound molecules can only diffuse to 
	 * 			other panels that have been declared as neighbors.  one-way relationship
	 * 		if <neighbor n> is part of a different surface, say '<surface s>:<neighbor n>
	 * --> surface --> unbounded_emitter <face> <species> <amount> <coordinates>
	 * 		figure out later
	 * 
	 * --> end_surface
	 * 
	 * --> surface --> epsilon <float>
	 * 		maximum allowed distance separation between a surface-bound molecule and
	 * 			the surface.  'default value...is extremely small'
	 * --> surface --> neighbor_dist <float>
	 * 		maximum distance that surface-bound molecules will jump across to diffuse
	 * 			from one panel to a neighboring panel.  defaults to 3*(max rms step
	 * 			length of surface-bound molecules)
	 */	
	public final static String START_SURFACE		= "start_surface";
	public final static String RATE					= "rate";
	public final static String RATE_INTERNAL		= "rate_internal";
	public final static String THICKNESS 			= "thickness";
	public final static String STIPPLE 				= "stipple";
	public final static String SHININESS			= "shininess";
	public final static String MAX_PANELS			= "max_panels";
	public final static String PANEL				= "panel";
	public final static String JUMP					= "jump";
	public final static String NEIGHBORS			= "neighbors";
	public final static String UNBOUNDED_EMITTER	= "unbounded_emitter";
	public final static String END_SURFACE			= "end_surface";
	public final static String EPSILON				= "epsilon";
	public final static String NEIGHBOR_DIST		= "neighbor_dist";
	
	
	
	/**
	 * compartments
	 * 
	 * --> max_compartment
	 * --> start_compartment name
	 * --> new_compartment
	 * --> compartment <name> surface <...>
	 * --> compartment <name> point <...>
	 * --> compartment <name> compartment <...>
	 * --> end_compartment
	 */
	//name of a bounding surface for this compartment
	//surface <surface_name>
	//note that this usage falls within a compartment declaration/definition
	public final static String MAX_COMPARTMENT		= "max_compartment";
	public final static String START_COMPARTMENT	= "start_compartment";
	public final static String END_COMPARTMENT		= "end_compartment";
	public final static String NEW_COMPARTMENT		= "new_compartment";
	public final static String COMPARTMENT			= "compartment";
	public final static String POINT				= "point";
	public final static String EQUAL				= "equal";
	public final static String AND					= "and";
	public final static String XOR					= "xor";
	public final static String OR					= "or";
	public final static String ORNOT				= "ornot";
	public final static String EQUALNOT				= "equalnot";
	public final static String ANDNOT				= "andnot";
	
	
	
	/** 
	 * Reactions
	 * 
	 * --> reaction <reaction name> <reactant1(state1)> + <reactant2(state2)> -> <product1(state3)> + <product2(state4)> [<rate>]
	 *     reaction <reaction name> <reactant(state)> -> <product1> ... <productn> [<rate>]
	 *     reaction <reaction name> 0 -> 0 [<rate>]
	 *		use 0 if no reactants or no products.  rate is optional....somehow.
	 *			reactant states may be all (product states may not)
	 * --> reaction_cmpt <compartment name> {rest is same as above}
	 * 		same as above, except only occurs within compartment <compartment name>
	 * --> reaction_surface <surface name> {same as above}
	 * 		only on surface <surface name>.  for bimolecular reactions, at least one 
	 * 			reactant needs to be bound to <surface name>, else it won't work
	 * --> binding_radius <reaction name> <radius>
	 * 			allow reaction <reaction name> to happen within <radius>
	 * --> product_placement <reaction name> <type> <parameters>
	 * 		placement method and parameters for the products of reaction <reaction name>
	 * 		<type>: {irrev, 
	 * 			pgem, pgemmax, pgemmaxw, ration, unbindrad, pgem2, pgemmax2, ratio2
	 * 			offset, fixed
	 * 		} //categorized by parameters which they require
	 * 
	 */
	public final static String REACTION 			= "reaction";
	public final static String REACTION_CMPT		= "reaction_cmpt";
	public final static String REACTION_SURFACE		= "reaction_surface";
	public final static String BINDING_RADIUS		= "binding_radius";
	public final static String PRODUCT_PLACEMENT	= "product_placement";
	
	
	
	/**
	 * port related syntax
	 * 
	 * --> max_port <integer>
	 * 		maximum number of ports that may be defined
	 * --> start_port <name>
	 * 		start of port definition block
	 * --> new_port <name>
	 * 		declare (but don't define) a port
	 * 		## DO NOT USE
	 * --> port --> surface <surface name>
	 * 		'name of the porting surface for this port'
	 * 			--> port <name> surface <surface name>
	 * 				alternative syntax (if using outside a port block)
	 * --> port --> face <face name>
	 * 		'face of the surface that is active for porting.'
	 * 		face: enum {'front', 'back'}
	 * 			--> port <name> face <face name>
	 * 				alternative syntax
	 * --> port --> end_port
	 */
	public final static String MAX_PORT				= "max_port";
	public final static String START_PORT			= "start_port";
	public final static String NEW_PORT				= "new_port";
	public final static String PORT					= "port";
	public final static String END_PORT				= "end_port";
	
	
	
	/**
	 * rules files
	 * 
	 * --> read_network_rules <filename>
	 * 		to create a reaction network automatically, it looks through <filename>
	 * 			and generates the network
	 */
	public final static String READ_NETWORK_RULES		= "read_network_rules";
	
	
	
	/**
	 * simulation settings
	 * 
	 * --> rand_seed <int>
	 * 		seed for random number generator.  defaults to current time
	 * --> accuracy <float>
	 * 		determines quantitative accuracy of simulation, on a scale from 0 to 10.
	 * 		small is less accurate.  defaults to 10, or max.
	 * --> molperbox <float>
	 * 		'virtual boxes are set up initially so the average number of molecules per
	 * 			box is no more than this value.'  defaults to 5.
	 * --> boxsize <float>
	 * 		alternative to molperbox.  
	 * 		request virtual boxes with width = <float>
	 * --> gauss_table_size <int>
	 * 		sets size of a lookup table used to generate pseudo-random numbers.
	 * 		must be power of 2.  defaults to 4096
	 */
	public final static String RAND_SEED 			= "rand_seed";
	public final static String ACCURACY				= "accuracy";
	public final static String MOLPERBOX			= "molperbox";
	public final static String BOXSIZE				= "boxsize";
	public final static String GAUSS_TABLE_SIZE		= "gauss_table_size";
//	public final static String EPSILON				= "epsilon";
//	public final static String NEIGHBOR_DIST		= "neighbor_dist";
	
	
	
	/** Run-time commands
	 * 
	 * --> output_root <path>
	 * 		'root of path where text output should be saved'.  spaces are permissible
	 * 		<path> is relative to the folder of the configuration file when the
	 * 			simulation is run
	 * 		WARNINGS:  folder must already exist, and <path> must end in a colon (and 
	 * 			perhaps also start with one)
	 * --> output_files <str1> ... <strn>
	 * 		'declaration of filenames that can be used for ouput of simulation results'.
	 * 		no spaces permitted.  paths are relative to that defined by output_root
	 * 		may use 'stdout' to send output to stdout
	 * --> output_file_number <integer>
	 * 		'starting number of ouput file name'.  defaults to 0, which means that no 
	 * 			number is appended to filename
	 * --> cmd (see runtime_commands_extended for possible completions of these commands)
	 * --> cmd b,a,e <string>
	 * 		execute <string> at 'b' -> before simulation, 'a' -> after, 'e' -> at every
	 * 			step during the simulation 
	 * --> cmd @ <time> <string>
	 * 		execute <string> at <time>
	 * --> cmd n <integer> <string>
	 * 		execute <string> every <integer> steps of the simulation
	 * --> cmd i <on> <off> <dt> <string>
	 * 		execute string from time <on> to time <off> 
	 * --> cmd j <onit> <offit> <dtit> <string>
	 * 		execute <string>, starting at step <onit>, repeating every <dtit> steps, 
	 * 			and stopping at <offit>
	 * --> cmd x <on> <off> <dt> <xt> <string>
	 * 		quadratically execute <string> or something.   probably useless
	 * !!! IMPORTANT:  how do these commands refer to time?  by simulation steps?
	 * --> max_cmd <int>
	 * 		useless and unneeded
	 */
	public final static String OUTPUT_ROOT 		= "output_root";
	public final static String OUTPUT_FILES 		= "output_files";
	public final static String OUTPUT_FILE_NUMBER 	= "output_file_number";
	public final static String CMD_B 				= "cmd b";
	public final static String CMD_A 				= "cmd a";
	public final static String CMD_E 				= "cmd e";
	public final static String CMD_AT 				= "cmd @";
	public final static String CMD_N 				= "cmd n";
	public final static String CMD_I 				= "cmd i";
	public final static String CMD_J 				= "cmd j";
	public final static String CMD_X 				= "cmd x";	
	
	
	//these go with runtime_commands as their arguments
	/**
	 * Simulation Control
	 * --> stop
	 * --> pause
	 * --> beep
	 * --> keypress <char>
	 * --> setrandseed <integer>
	 * 			defaults to current time if <integer> is less than 0
	 * --> setgraphics <type>
	 * 			overlaps with smolgraphics class
	 * --> setgraphic_iter
	 * 			overlaps with smolgraphics class
	 * 
	 * File manipulation
	 * --> overwrite <filename>
	 * 		erases the output file called <filename> but leaves it open for more writing
	 * --> incrementfile <filename>
	 * 		make a new output file based on <filename>, appends a prefix starting with _001
	 * 
	 * Conditional  -- later
	 * --> ifno
	 * --> ifless
	 * --> ifmore
	 * 
	 * System observation
	 * --> warnescapee <name(state)> <filename>
	 * 		look for molecules of SpeciesState <name(state)> outside of system boundaries, print 
	 * 			results to <filename>
	 * --> echo <filename> <text>
	 * 		print <text> to file <filename>
	 * --> molcountheader <filename>
	 * 		print 'time speciesname1 ...' to file <filename>
	 * --> molcount <filename>
	 * 		print number of molecules of each type to file <filename>
	 * --> molcountinbox <xlow> <xhigh> ... <filename>
	 * 		need low, high for each dimension, prints number of molecules in the
	 * 			box to file <filename>
	 * --> molcountincmpt <compartment> <filename>
	 * 		see 'molcountinbox'
	 * --> molcountincompt2 <compartment 1> ... <compartment n> <filename>
	 * 		state aware version of molcountincmpt
	 * --> molcountonsurf <surface> <filename>
	 * 		similar to above
	 * --> molcountspace <name(state)> <axis> <low 1> <high 1> ... <low 3> <high 3> <average> <filename>
	 * 		<axis>: enum {0..dimensionality - 1}
	 * --> listmols 
	 * --> listmols2
	 * --> listmols3
	 * --> molpos
	 * --> molmements
	 * --> savesim
	 * --> meansqrdisp
	 * --> meansqrdisp2
	 * --> diagnostics
	 * 
	 * System manipulation
	 * --> set
	 * --> pointsource
	 * --> volumesource
	 * --> movesurfacemol
	 * --> killmol
	 * --> killmolprob
	 * --> millmolinsphere
	 * --> millmoloutsidesystem
	 * --> equilmol
	 * --> fixmolcount
	 * --> fixmolcountincmpt
	 * --> fixmolcountonsurf
	 * --> replacexyzmol
	 * --> replacevolmol
	 * --> modulatemol
	 * --> react1
	 * --> setrateint
	 * --> setsurfcoeff
	 * --> settimestep
	 * --> porttransport
	 * --> excludebox
	 * --> excludesphere
	 * --> includeecoli
	 */
	 public final static String STOP				= "stop";
	 public final static String PAUSE				= "pause";
	 public final static String BEEP				= "beep";
	 public final static String KEYPRESS			= "keypress";
	 public final static String SETRANDSEED			= "setrandseed";
	 public final static String SETGRAPHICS			= "setgraphics";
	 public final static String SETGRAPHIC_ITER		= "setgraphic_iter";
	 public final static String OVERWRITE			= "overwrite";
	 public final static String INCREMENTFILE		= "incrementfile";
	 public final static String IFNO				= "ifno";
	 public final static String IFLESS				= "ifless";
	 public final static String IFMORE				= "ifmore";
	 public final static String WARNESCAPEE			= "warnescapee";
	 public final static String ECHO				= "echo";
	 public final static String MOLCOUNTHEADER		= "molcountheader";
	 public final static String MOLCOUNT			= "molcount";
	 public final static String MOLCOUNTINBOX		= "molcountinbox";
	 public final static String MOLCOUNTINCMPT		= "molcountincmpt";
	 public final static String MOLCOUNTINCOMPT2	= "molcountincompt2";
	 public final static String MOLCOUNTONSURF		= "molcountonsurf";
	 public final static String MOLCOUNTSPACE		= "molcountspace";
	 public final static String LISTMOLS			= "listmols";
	 public final static String LISTMOLS2			= "listmols2";
	 public final static String LISTMOLS3			= "listmols3";
	 public final static String MOLPOS				= "molpos";
	 public final static String MOLMEMENTS			= "molmements";
	 public final static String SAVESIM				= "savesim";
	 public final static String MEANSQRDISP			= "meansqrdisp";
	 public final static String MEANSQRDISP2		= "meansqrdisp2";
	 public final static String DIAGNOSTICS			= "diagnostics";
	 public final static String SET					= "set";
	 public final static String POINTSOURCE			= "pointsource";
	 public final static String VOLUMESOURCE		= "volumesource";
	 public final static String MOVESURFACEMOL		= "movesurfacemol";
	 public final static String KILLMOL				= "killmol";
	 public final static String KILLMOLPROB			= "killmolprob";
	 public final static String MILLMOLINSPHERE		= "millmolinsphere";
	 public final static String MILLMOLOUTSIDESYSTEM	= "millmoloutsidesystem";
	 public final static String EQUILMOL			= "equilmol";
	 public final static String FIXMOLCOUNT			= "fixmolcount";
	 public final static String FIXMOLCOUNTINCMPT	= "fixmolcountincmpt";
	 public final static String FIXMOLCOUNTONSURF	= "fixmolcountonsurf";
	 public final static String REPLACEXYZMOL		= "replacexyzmol";
	 public final static String REPLACEVOLMOL		= "replacevolmol";
	 public final static String MODULATEMOL			= "modulatemol";
	 public final static String REACT1				= "react1";
	 public final static String SETRATEINT			= "setrateint";
	 public final static String SETSURFCOEFF		= "setsurfcoeff";
	 public final static String SETTIMESTEP			= "settimestep";
	 public final static String PORTTRANSPORT		= "porttransport";
	 public final static String EXCLUDEBOX			= "excludebox";
	 public final static String EXCLUDESPHERE		= "excludesphere";
	 public final static String INCLUDEECOLI		= "includeecoli";

}
