# Variable Reduction by Mass Conservation

> **AI-drafted page.** An AI assistant wrote this page from VCell's source code. The VCell
> development team has not yet reviewed it, so it may be incomplete or wrong.
> ([Tracking issue](https://github.com/virtualcell/vcell/issues/2226))

In many reaction networks some combinations of species never change. In a reaction A + B <-> AB,
for example, every B is either free or bound, so B + AB stays equal to its starting total for the
whole simulation. Such a combination is a *conserved moiety*. When a network has one, not all of
its species are independent: once you know AB, you know B.

**Variable reduction** uses this to make the system smaller. When VCell
[generates the math](generated_math.md) for an application, it finds the conserved moieties and,
for each one, solves an ordinary differential equation (ODE) for all but one of its species. The
remaining species is not a variable of its own; it is computed from an algebraic relation:

- a constant holds the conserved total, named `K_` + species name + `_total` (for example
  `K_B_total`), and calculated from the initial conditions;
- the dependent species is that total minus the others (for example, B = `K_B_total` - AB).

Without reduction, every species gets its own ODE and no `K_..._total` constants appear.

## Where to find it

The **Variable Reduction** checkbox is in the toolbar of an application's **Simulations** list (see
[Simulations](simulations.md)). It appears only for non-spatial deterministic (compartmental ODE)
applications; it is hidden for spatial and stochastic applications. The **?** button next to it
gives a short explanation.

The setting belongs to the application, not to a single simulation: changing it changes the math
for every simulation of that application. New applications start with it unchecked. Applications
from models saved by earlier VCell versions, which always reduced the system, may open with it
checked.

## When to use it

Reduction gives the solver fewer equations, and in earlier VCell versions it was always applied.
Turning it off makes simulations take slightly longer, but:

- the [generated math](generated_math.md) is easier to read, since every species appears as itself
  rather than through `K_..._total` constants and algebraic relations;
- it can avoid occasional numerical problems in a simulation;
- [Batch Simulations](BatchSimulations.md) require it to be off. VCell will not create a batch
  while the checkbox is checked.

Even with reduction on, VCell keeps a species as a full variable when it needs to, for example when
it is the target of an event assignment.

## Related pages

- [Generated Math](generated_math.md)
- [Simulations](simulations.md)
- [Batch Simulations](BatchSimulations.md)
