package org.vcell.smoldyn.model;

/**
 * Specification of Smoldyn's reaction handling.  Essentially all of the following was taken nearly verbatim from Steven Andrews' 
 * Smoldyn user's manual, included in the Smoldyn distribution as of version 2.15 (July 2010).
 * 
 *
 *<p>
 * <h2>Smoldyn Reactions</h2>
 *<ul id="SmoldynReaction">
 * 
 * <li>zeroth order, first order, and second order reactions</li>
 * <li>additional interaction types using reactions: conformational spread reactions and excluded volume reactions</li>
 * <li>zeroth order reactions:
 * 
 * <ul id="SmoldynZerothOrder">
 * <li>no reactants</li>
 * <li>products appear spontaneously at random locations in the system volume (or within a compartment) at a roughly constant rate</li>
 * <li>Smoldyn assumes that each molecule created in a zeroth order reaction is created independently of each other,
 * which allows Poisson statistics to be used</li>
 * </ul>
 * </li>
 * 
 * <li>first order reactions: the conversion of one molecular species into ONE other
 * <ul id="SmoldynFirstOrder">
 * <li>
 * </ul>
 * </li>
 * 
 * <li>Second order reactions--when there are two products or two reactants, or both
 * <ul id="SmoldynSecondOrderReaction">
 * <li>a reaction radius is defined for each pair of molecular species</li>
 * <li>those that do not react with each other have a reaction radius of 0</li>
 * <li>the reaction radius is some small distance on the order of the molecular radii</li>
 * <li>radius values increase monotonically with the standard mass action reaction rate</li>
 * <li>to simulate each time step, molecules are first diffused and then all reactant pairs that are closer than their reaction 
 * radii are reacted. Thus, the stochasticity in simulated bimolecular reactions arises solely from diffusion and not from the 
 * reaction step of the algorithm. </li>
 * <li>if a reaction has multiple products, they are usually all added to the system at the same point</li>
 * <li>products can also be separated from each other by a small amount, called the unbinding radius</li>
 * <li>this separation reduces the likelihood of their immediate recombination in a new reaction (geminate recombination)</li>
 * <li>More information:
 * <ul id="secondmore">
 * <li>Bimolecular reactions have the generic reaction equation A + B -> C</li>
 * <li>The reaction rate constant, k, is only actually constant if: (i) the reaction kinetics are purely activation-limited, or 
 * (ii) the reaction has proceeded long enough that a steadystate reactant distribution has formed. (note:  does this apply to 
 * real-life, or to Smoldyn?)</li>
 * <li>the binding radii are small for slow reactions and large for fast reactions</li>
 * <li>because the reaction kinetics depend on the ratio of the reactant rms steps lengths to the binding radii, a slow reaction has
 * relatively long steps compared to the binding radius and thus behaves as though it is activation limited</li>
 * <li>however, a fast reaction has short rms step lengths compared to the binding radius and so behaves as though it is 
 * diffusion-limited</li>
 * <li>shortening the simulation time step makes all reactions more diffusion-limited</li>
 * <li>activation-limited reactions follow mass action kinetics for all(?) time steps</li>
 * <li>a diffusion-limited reaction does not agree with the mass-action theory, because a simulation starts with molecules randomly
 * distributed whereas the analytical result assumes a steady-state distribution. However, after enough time has passed for a steady state
 * reactant distribution to be formed, the simulated results agree well with the analytical results</li>
 * </ul>
 * </li>
 * <li>Bimolecular reactions with reactants of the same type (note: this seems to be unnecessarily confusing)
 * <ul>
 * <li>there are no conceptual or simulation algorithm differences for bimolecular reactions in which two reactants are the same
 * <li>however, there are a few quantitative differences: consider a situation with 1000 A molecules and 1000 B molecules. Despite 
 * the fact that each A molecule has about 1000 potential collision partners, whether the reactants are A + A or A + B, there are 
 * twice as many A-B collisions as A-A collisions. This is because each A-A pair can be counted in either of two ways, but is still 
 * only a single possible collision. To achieve the same reaction rate for A + A reactants as for A + B, despite the fact that there
 * are fewer collisions, Smoldyn uses a larger binding radius for the former</li>
 * </ul>
 * </li>
 * </ul>
 * </li>
 * 
 * <li>reactions may be localized to spatial regions (compartments) or surfaces if one of the reactants is surface-bound
 * <ul id="SmoldynLocalizedReaction">
 * <li>a zeroth order reaction can be constrained to a specific compartment (for example)</li>
 * <li>a first order reaction can be constrained to only be active when the reactant is within the specified compartment (for 
 * example)</li>
 * </ul>
 * </li>
 * 
 * <li>Conformational spread reactions--see Smoldyn manual part 1, page 49 for more information
 * <ul id="SmoldynConformationSpread">
 * <li>special case of bimolecular reaction with a domain of interaction (note:  no idea what that means)</li>
 * <li>only intended to be used with stationary reactants</li>
 * <li>only permitted in reactions with two reactants and two products</li>
 * <li>conformational spread reaction is possible if the reactants are closer together than the conformational spread radius</li>
 * <li>this is analogous to the binding radius of normal second order reactions (although its value is constant, regardless of 
 * the time step)</li>
 * <li>a conformational spread reaction rate has units of inverse time, as it is a first order reaction</li>
 * <li>rate applies to reactants that are continuously closer to each other than the conformational spread radius</li>
 * <li>if a reaction occurs, the first entered reactant is replaced by the first product, and the second reactant with the second 
 * product.</li>
 * </ul>
 * </li>
 *
 * <li>Excluded volume reactions--see Smoldyn manual part 1, page 51 for more information
 * <ul id="SmoldynExcludedVolume">
 * <li>essentially use the reaction concept to simulate excluded volume interactions</li>
 * <li>typically of the form A + B -> A + B</li>
 * <li>constraint: that the “binding radius” is roughly the sum of the physical molecular radii</li>
 * <li>constraint: the “unbinding radius” is a slightly larger value</li>
 * <li>in reactions with the “bounce” product placement type, reaction products are placed on the same axis as the reactants</li>
 * <li>result: the molecules appear to bounce off of each other</li>
 * </li>
 * </ul>
 * 
 *</ul>
 *</p>
 * 
 * <p>
 * <h2>Reaction Rates</h2>
 * <ul id="ReactionRates">
 * <li>the reaction rate is the macroscopic reaction rate</li>
 * <li>it is converted internally into parameters that Smoldyn uses</li>
 * <li>0th order: rate is converted to the average number of molecules that should be added to the entire simulation volume at 
 * each time step</li>
 * <li>this can be entered directly with reaction_production</li>
 * <li>1st order: rate is converted to the probability that a reactant molecule will react during one time step</li>
 * <li>this can be entered directly with the statement reaction_probability</li>
 * <li>2nd order: rate is converted into a reaction binding radius</li>
 * <li>this can be entered directly with binding_radius</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <h2>Bimolecular Reaction Binding and Product Placement</h2>
 * <ul id="SmoldynBimolecular">
 * <li>products are usually placed at the location where reaction occurred</li>
 * <li>offsets from the reaction location are also possible</li>
 * <li>may be necessary for reversible reactions so as to avoid geminate recombinations</li>
 * <li>unbinding radius can be entered directly or deferred to Smoldyn in many different ways, all of which are entered with the 
 * product_placement statement</li>
 * </ul>
 * 
 * <ul>
 * <li>Binding and unbinding radii</li>
 * <li>Smoldyn calculates the binding radius from the rate, although this may also be specified separately 
 * <li>for every reaction that leads to multiple products, Smoldyn determines the unbinding radius
 * <li>the unbinding radius is specified using the product_placement statement
 * <li>Product parameters are listed in the table, below. While these binding and unbinding radii are well defined microscopic 
 * parameters (at least within the context of the Smoluchowski model system that is simulated), the meanings of the experimental 
 * rate constants, including those given in the configuration file, are not nearly as well defined. Instead, those rate constants 
 * depend on the conditions under which they were measured. Smoldyn accounts for this by attempting to guess the experimental 
 * conditions, using a process described in the manual. If Smoldyn’s guess is correct, the simulated reaction rates should exactly match 
 * the experimental rates (not including edge effects, which are typically negligible unless one reactant is fixed at or near an
 * edge)</li>
 * <li>Product placement types:
 * <ul>
 *  <li>Special types
 *   <ul>
 *    <li>irreversible</li>
 *    <li>conformational spread reaction</li>
 *   </ul>
 *  </li>
 *  <li>Use these if reversible reactions were measured at equilibrium
 *   <ul>
 *    <li>probability of geminate reaction</li>
 *    <li>maximum probability of geminate reaction</li>
 *    <li>unbinding radius relative to binding radius</li>
 *    <li>fixed length unbinding radius</li>
 *   </ul>
 *  </li>
 *  <li>Use these if reversible reactions were measured with all product removed as it was formed
 *   <ul>
 *    <li>probability of geminate reaction</li>
 *    <li>maximum probability of geminate reaction</li>
 *    <li>unbinding radius relative to binding radius</li>
 *    <li>fixed offset of products, rotationally randomized</li>
 *    <li>fixed offset of products, not rotationally randomized</li>
 *   </ul>
 *  </li>
 * </ul>
 * </li>
 * <li>In all cases, Smoldyn assumes that rate constants were measured using an effectively infinite number of reactant molecules, 
 * in an infinite volume, that were started well mixed and that then were allowed to react until either an equilibrium was reached
 * for reversible reactions, or a steady-state reaction rate was reached for irreversible reactions. Only in these cases is mass action
 * kinetics correct and is the reaction rate constant actually constant. The precise experimental assumptions are clarified with the 
 * following examples.
 * <table border="2" id="RateConstants">
 * <tr><th width="10%">Generic reaction type</th><th>description</th></tr>
 * <tr><td>A + B -> C</td><td>the rate constant is assumed to have been measured at steady state, starting with a well-mixed system of A
 * and B. No product parameter is required. At steady-state, the simulation matches mass action kinetics.</td></tr>
 * <tr><td>X -> A + B</td><td>there is no bimolecular reaction, so no binding radius is calculated. The default unbinding radius is 0, 
 * although it is possible to define a different one. If the product parameter type is pgem, pgem2, ratio, or ratio2, an error is returned 
 * due to the lack of a binding radius. If the parameter type is not given or is irrev, pgemmax, or pgemmax2, the unbinding radius is set 
 * to 0. If it is unbindrad, fixed, or offset, the requested separation is used. At steady-state, matches mass action kinetics.</td></tr>
 * <tr><td>A + B <-> C</td><td>If the reversible parameter is pgem, pgemmax, unbindrad, or ratio, the forward rate constant is assumed to 
 * have been measured using just this system of reactions after the system had reached equilibrium. The product parameter is used to yield 
 * the correct probability of geminate recombination if possible, or the desired unbinding radius. In this case, the simulation matches 
 * mass action kinetics at equilibrium. If the product parameter is pgem2, pgemmax2, ratio2, offset, fixed, or irrev, then it is assumed 
 * that the forward rate constant was measured at steady-state and with all C removed as it was formed, thus preventing any geminate 
 * reactions. The unbinding radius is set as requested, using the binding radius if needed. In this case, the simulated forward reaction 
 * rate is higher than requested due to geminate rebindings.</td></tr>
 * <tr><td>A + B <-> C -> Y</td><td>The second reaction is ignored for determining parameters for A + B. Instead, the first reaction is 
 * considered as though the rates were determined experimentally using just the system given in example 3. If the product parameter is 
 * pgem, pgemmax, ratio, or unbindrad, the simulated reaction rate for the forward reaction A + B -> C will be lower than the requested 
 * rate because there are fewer geminate reactions than there would be with the equilibrium system. Alternatively, it will be higher
 * than the requested rate if the product parameter is pgem2, pgemmax2, ratio2, offset, fixed, or irrev, because there are some geminate 
 * reactions</td></tr>
 * <tr><td>X -> A + B -> C</td><td>The binding radius for the second reaction is treated as in example 1, without consideration of the 
 * first reaction. The unbinding radius for the first reaction is found using the binding radius of the second reaction. Here, product 
 * parameters pgem and pgem2 are equivalent, pgemmax and pgemmax2 are equivalent, and ratio and ratio2 are equivalent. The actual reaction 
 * rate for the second reaction, found with a simulation, will be higher than the requested value due to geminate rebindings that occur 
 * after the dissociation of X molecules</td></tr>
 * <tr><td>X -> A + B <-> C</td><td>The A + B <-> C binding and unbinding radii are treated as in example 3. Another unbinding radius is 
 * required for the first reaction, which is found as in example 5, using the binding radius from the second reaction. Mass action 
 * kinetics are not followed.</td></tr>
 * <tr><td>X <-> A + B <-> C</td><td>The binding radii and unbinding radii for each bimolecular reaction are found as in example 3, 
 * independent of the other bimolecular reaction. The simulated rates may be different from those requested because of differing unbinding 
 * radii</td></tr>
 * <tr><td>X -> A + B -> C, A + B -> D</td><td>The binding radii for the two bimolecular reactions are each found as in example 1. The 
 * unbinding radius for the first reaction cannot be determined uniquely, because the two forward reactions from A + B are equivalent and 
 * are likely to have different binding radii. Smoldyn picks the binding radius for the first forward reaction that is listed. Thus, if 
 * the product parameter for dissociation of X is pgem, the requested geminate rebinding probability will be found for the reaction 
 * A + B -> C, but a different value will be found for the reaction A + B -> D</td></tr>
 * <tr><td>C <-> A + B <-> C</td><td>This reaction scheme might represent two different pathways by which A and B can bind to form an 
 * identical complex. However, Smoldyn cannot tell which reverse reaction corresponds to which forwards reaction. Instead, for both 
 * determining the binding and unbinding radii, it uses the first reverse reaction that is listed</td></tr>
 * </table>
 * </li>
 * <li>The general principle for calculating binding radii is that Smoldyn first looks to see if a reaction is directly reversible. If it 
 * is and if the reversible parameter is pgem, pgemmax, ratio, or unbindrad, then the binding radius is found under the assumption that 
 * the rate constant was measured using just this reaction, at equilibrium. If not, or if the reversible parameter is pgem2, pgemmax2, 
 * ratio2, offset, fixed, or irrev, then Smoldyn calculates the binding radius with the assumption that the rate constant was measured 
 * using just that reaction at steady-state and with all product removed as it is formed.</li>
 * <li>Unbinding radii typically require a reversible parameter (except as in example 2). If the parameter is unbindrad, offset, or fixed, 
 * the requested unbinding radius is used. If it is irrev, the unbinding radius is set to 0. Otherwise, it can only be calculated with the
 * knowledge of the binding radius. If the reaction is directly reversible, the binding radius for the reverse reaction is used. If it is 
 * not directly reversible but the products can react, as in examples 5, 6, and 8, then the binding radius for the first reaction that is 
 * listed is used.
 * </li>
 * </ul>
 * </p>
 * 
 * <p>
 * <h2>Bimolecular reactions and surfaces</h2>
 * <ul>
 * <li>Does a bimolecular reaction occur if there is a surface between the reactants? This turns out to be a somewhat complex question. 
 * The simple answer is that it does occur if
the surface is transparent to both molecular species and it does not occur if the surface is
reflective or absorptive to both molecular species. In principle, reactions should be
possible across pairs of jump surfaces, although they are not performed by the current
Smoldyn version which treats jump surfaces as though they are opaque with respect to
reactions.
 * </li>
 * <li>Smoldyn determines where the reaction location is using a weighed average of the
reactant diffusion coefficients. The reaction takes place only if both reactants can get to
the reaction position, considering any intervening surfaces. Absorption on the opposite
side of a surface is not worried about, the logic being that molecules are already in
contact when a reactant traverses the surface, and so opposite-side absorption is no more
important than the reaction. For partially transparent surfaces, reactions occur depending
on the probability of transparency.
 * </li>
 * <li>When molecules have excluded volume, which they do not in Smoldyn, even inert
impermeable surfaces can affect the local concentrations of chemicals. An obvious effect
is that a molecule cannot be closer to a surface than its radius, leading to a concentration
of zero closer than that. In a mixture of large and small molecules, Brownian motion
tends to push the large molecules up against surfaces while the small molecules occupy
the center of the accessible volume, thus creating more complex concentration effects.
These effects do not occur when excluded volume is ignored, as it is in Smoldyn, in
which case surfaces do not affect local concentrations.
 * </li>
 * <li>While surfaces do not affect concentrations of non-reacting molecules, they do
affect reaction rates. Consider the reaction A + B -> C, where A is fixed and B diffuses.
If essentially all A molecules are far from a surface, the diffusion limited reaction rate is
found by solving the diffusion equation for the radial diffusion function (RDF) with the
boundary conditions that the RDF approaches 1 for large distances and is 0 at the binding
radius (see the paper by myself and Dennis Bray titled “Stochastic simulation of chemical
reactions with spatial resolution and single molecule detail”). This leads to the
Smoluchowski rate equation k = 4!D" b
 * </li>
 * <li>However, for an A molecule that is near a surface, an additional boundary condition is
that the gradient of the 3 dimensional RDF in a direction perpendicular to the surface is
zero at the surface. This makes the solution of the reaction rate sufficiently difficult that I
have not attempted to solve it, but the result is different from the simple result given
above. This surface effect is an issue whenever the A molecule is within several binding
radii of a surface and is especially pronounced when it is closer to the surface than its
binding radius. For cases in which the A molecule is more than one binding radius from
the surface, B molecules are going to take longer than usual to reach the region between
the A and the surface, leading to a decreased reaction rate. It is suspected that the
reaction rate decreases monotonically as the A molecule approaches and then crosses a
surface.
 * </li>
 * <li>A special case that can be solved exactly occurs when the A molecule is exactly at
the surface, such that half of the binding volume is accessible to B molecules and half is
inaccessible. Now, the RDF inside the system volume is identical to the RDF for the case
when the A molecule is far from a surface. The logic is to assume that this is true and to
then observe that it already satisfies the additional boundary condition. Using this RDF,
the diffusive flux is half of the diffusive flux for an A molecule far from a surface,
because only half of the binding surface is exposed to the system. Thus, the diffusion
limited reaction rate for the situation in which a reactant is fixed exactly at a surface is
k = 2!D" b
 * </li>
 * <li>The situation changes some when simulation time steps are sufficiently long that
rms step lengths are much longer than binding radii. Now, the probability of a reaction
occurring during a time step is a function of only the binding volume. Thus, there are no
surface effects at all when an A molecule is fixed anywhere in the simulation volume that
is greater than or equal to one binding radius away from a surface. As the A molecule is
moved closer to the surface, the reaction rate decreases in direct proportion to the binding
volume that is made inaccessible to B molecules. An especially easy situation is that
when the A molecule is exactly at the surface, the reaction rate is half of its value when
the A molecule is far from a surface, which is the same as the diffusion limited result.
These results can be turned around to solve for the binding radius. If the reaction is
diffusion limited, the binding radius should double when a reactant is placed exactly at
the surface to maintain the same reaction rate. If it is activation limited, the binding
radius should increase by 21/3 to maintain the same reaction rate. As usual though, the
binding radius is more closely related to the fundamental physical properties of the
molecule than is the rate constant, so it is essential to consider the experimental
conditions that were used for measuring the rate constant.
 * </li>
 * <li>In conclusion, reaction rates are reduced near surfaces and the effect is different for
diffusion limited and activation limited reactions. However, for both cases, and almost
certainly for all cases in between, the reaction rate is exactly half when an A molecule is
fixed at a surface, compared to when it is far from a surface. A few tests with Smoldyn
using the files wallreact.txt, suggested that these surface effects are likely to be minimal
for most situations, although it is good to be aware of their potential. The exception is
that there are large surface effects when molecules are
 * </li>
 * </ul>
 * </p> 
 *
 *
 * question: does adjusted reaction length depend on dt?
 *
 * @author mfenwick
 *
 */
public interface SmoldynReaction {

	public String getName();
	
	public double getRate();

}
