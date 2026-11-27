# Membrane Specifications

> **AI-drafted page.** An AI assistant wrote this page from VCell's source code. The VCell
> development team has not yet reviewed it, so it may be incomplete or wrong.
> ([Tracking issue](https://github.com/virtualcell/vcell/issues/2226))

The **Membrane** tab of an application's [Specifications](SpecificationsOverview.md) is an
experimental, unfinished feature. **It is hidden in a normal VCell installation**: the
**Specifications** view shows only the tabs described on the
[Species](SpeciesSpecifications.md), [Reactions](ReactionsSpecifications.md) and
[Network](NetworkSpecifications.md) (or [Network-Free](NetworkFreeSpecifications.md)) pages. The
tab appears only when VCell is started with a developer setting turned on, and then in every type of
application.

## What the tab shows

A table with one row for each membrane in the model's physiology:

| Column         | Meaning                                       | Editable |
|----------------|-----------------------------------------------|----------|
| **Membrane**   | The membrane's name.                          | No       |
| **Velocity X** | An expression for the membrane's velocity in x. | Yes      |
| **Velocity Y** | An expression for the membrane's velocity in y. | Yes      |

Both velocities start at `0`. Double-click a velocity cell to type an expression; an expression that
cannot be read is refused with an error message. Click a column heading to sort the rows by that
column. The **Search** box below the Specifications view does not filter this table.

## Limitations

The velocities entered here are not yet used when VCell generates the application's math, and they
are not saved with the model. To move species with a velocity, use the velocity terms in
[Species Specifications](SpeciesSpecifications.md) instead.
