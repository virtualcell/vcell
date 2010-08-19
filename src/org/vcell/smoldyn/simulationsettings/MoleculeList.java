package org.vcell.smoldyn.simulationsettings;



/**
 * A Smoldyn molecule list may be used to increase the efficiency of simulation by greatly reducing the number of comparisons that must
 * be made in order to determine whether a reaction may occur.
 * WARNING:  this feature is NOT understood.
 * 
 * Molecule lists
From a user’s point of view, Smoldyn molecules follow a Western life trajectory:
some chemical reaction causes a new molecule to be born from nothing, it diffuses
around in space for a while, and then it undergoes a reaction and vanishes again into
nothingness (or maybe goes to molecule heaven). Internally though, the situation is
closer to a Wheel of Life: there are a fixed number of molecules (the number specified
with the max_mol statement) that cycle indefinitely between “live” and “dead” states and
which are assigned a new species type at each reincarnation. The dead molecule list is of
no importance to the functioning of the simulation, but merely stores molecules when
they are not currently active in the simulated system. The size and current population of
the dead list are displayed in the molecule section of the configuration file diagnostics.
Active molecules in a simulation are stored in one or more live lists. As a default,
all live molecules that diffuse, meaning that the diffusion coefficient is non-zero, are
stored in a list called “diffuselist” while all fixed molecules are stored in a separate live
list called “fixedlist.” The separation of the molecules into these two lists speeds up the
simulation because all molecules in fixedlist can be safely ignored during diffusion
calculations or surface checking.
Additional live lists can be beneficial as well. For example, consider the
equilibrium chemical reaction
A + B <-> C
The only bimolecular reaction possible is between A and B molecules, so there is no need
to check each and every A-A, B-B, A-C, B-C, and C-C molecule pair as well to look for
more possible reactions. In this case, storing A, B, and C molecules in three separate lists
means that potential A-B reactions can be checked without having to scan over all of the
other combinations too. This is done in the example file S4_molecules/mollist.txt, where
it is found that using three molecule lists for A, B, and C leads to a simulation that runs
30% faster than using just one molecule list. With a Michaelis-Menten reaction, the
difference was found to be closer to a 4-fold improvement.
While it might seem best to have one molecule list per molecular species, it is not
quite so simple. It is often the case in biology modeling that many chemical species will
exist at very low copy number. In particular, a protein that can bind any of several
ligands needs to be defined as separate molecular species for each possible combination
of bound and unbound ligands. This number grows exponentially with the number of
binding sites, leading to a problem called combinatorial explosion. Because there are so
many molecular species, there are relatively few molecules of each one. Returning to the
Smoldyn molecule lists, each list slows the simulation speed by a small amount. Thus,
adding lists is worthwhile if each list has many molecules in it, but not if most lists are
nearly empty.
At least for the present, Smoldyn does not automatically determine what set of
molecule lists will lead to the most efficient simulation, so it is up to the user make his or
her best guess. Molecule lists are defined with the statement molecule_lists and
molecule species are assigned to the lists with mol_list. Any molecules that are
unassigned with the mol_list statement are automatically assigned to new a list called
“unassignedlist”.
 * 
 * @author mfenwick
 *
 */
public class MoleculeList {

	public MoleculeList() {
		
	}

	
}
