# Creating Reactions from a Search

> **AI-drafted page.** An AI assistant wrote this page from VCell's source code. The VCell
> development team has not yet reviewed it, so it may be incomplete or wrong.
> ([Tracking issue](https://github.com/virtualcell/vcell/issues/2226))

Instead of building a reaction from scratch, you can copy one from another BioModel in the VCell
Database: your own models, models shared with you, and public models. VCell finds reactions by the
names of the species that take part in them, and copies the chosen reaction, with its kinetics,
into your model.

## Opening the search

In the [Reactions Diagram](ReactDiagView.md), right-click inside a compartment or membrane and
choose **Search Reactions...**. A window titled **Create Reaction within** *compartment* (or
*membrane*) opens; the new reaction will be placed in that structure. You must be logged in, since
the search runs in the VCell Database.

## 1. Select Reaction

Type part of a species name after **Search for:** and click **Search** (or press **Enter**). The
search ignores capitalization and finds every reaction that has a participant whose name contains
the text. Use **\*** to stand for any string, as in `Ca*cyt`.

The list below shows the matching reactions, each written out with its participants. When the
search is made from a compartment rather than a membrane, reactions that cross a membrane (fluxes)
are left out. Select a reaction to continue.

## 2. Choose a Version of the Selected Reaction

The same reaction often appears in several models, or in several versions of one model. The left
list shows each one: the kind of structure it is in, the model's owner, the BioModel's name, the
reaction's name in that model (in parentheses), and the date that version was saved.

Select one to see its details in the right list: each kinetic parameter with its expression, and
each participant with its role (reactant, product, catalyst). Then click **Next**.

## 3. Resolve Reaction Participants with Model

This page shows the reaction's stoichiometry as a small diagram, and a table, **Assign Reaction
Participants To Model**, with one row for each participant of the found reaction:

- **from RX Elements**: the participant as it is named in the found reaction.
- **Assign to Model Species**: **New Species** adds the participant to your model as a new species;
  or choose one of your model's existing species to use instead. VCell preselects a species of
  your model when its name matches.
- **Assign to Model Compartment**: the structure of your model the participant goes in. It starts
  at the structure with the same name, if your model has one, and otherwise at the structure you
  searched from.
- **Custom Name**: optionally, a name for the participant in your model.

Click **Finish** to add the reaction to your model. **Back** returns to the previous page, and
**Close** closes the window without adding anything.

Once added, the reaction appears in the diagram and in the [Reactions table](ReactTabView.md),
where you can check and edit its kinetics in the
[reaction Properties Pane](../PropertiesPanes/PP_Reactions.md).
