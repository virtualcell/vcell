# Math Models

> **AI-drafted page.** An AI assistant wrote this page from VCell's source code. The VCell
> development team has not yet reviewed it, so it may be incomplete or wrong.
> ([Tracking issue](https://github.com/virtualcell/vcell/issues/2226))

A **MathModel** is a VCell model written directly as mathematics: variables, equations, functions
and the domains they apply to, in VCell's math description language (VCML). A **BioModel** instead
describes biology (compartments, species, reactions) and VCell generates the math for each of its
applications (see [Generated Math](../BioModelApplications/Simulations/generated_math.md)). Use a
MathModel when you want to write or change the equations yourself; use a BioModel when you want VCell
to derive them from a physiological description.

A MathModel has no physiology and no applications. It has one math description, one geometry (which
is empty for a non-spatial model) and its own simulations.

## Creating a MathModel

Choose **File > New > MathModel**, or the matching choice in the welcome window, and pick one of:

- **Non-Spatial** - a model with no geometry, for ordinary differential equations. It starts with a
  single compartment called `Compartment`.
- **Spatial from database geometry** - you choose a saved geometry from the VCell database. The new
  model starts with one compartment for each subvolume of that geometry and one membrane for each
  surface where two subvolumes meet.
- **Spatial from new geometry** - VCell first asks you to choose the type of a new geometry, as when
  creating a geometry, then builds the model from it in the same way.
- **From BioModel** - you choose a saved BioModel, then one of its applications. The new MathModel is
  a copy of the math VCell generated for that application, with its geometry, and is named after the
  BioModel and the application. Changes to the MathModel do not affect the BioModel.

A new model is named `MathModel` followed by a number until you save it under a name of your own.
See also [File > New](../Introduction/New.md).

## The MathModel window

The tree on the left of a MathModel window has the model itself at the top, shown by its name in
bold, and four items under it. Selecting an item shows its view on the right; the panel below the
view shows the properties of what is selected.

- **VCML Editor** - the model's math, as equations or as editable VCML text. This item is selected
  when the window opens. See [VCML Editor](MathModelVCMLEditor.md).
- **Geometry** - the model's geometry, which you can view, replace or edit. A non-spatial model has
  none. See [Geometry](../BioModelApplications/Geometry/GeometryOverview.md).
- **Simulations** - the model's simulations, where you create, edit and run them and open their
  results. Right-click **Simulations** and choose **New Simulation** to add one. Selecting a
  simulation shows its summary below the list. See
  [Simulations](../BioModelApplications/Simulations/simulations.md).
- **Output Functions** - functions of the model's variables that simulations compute and save with
  their results. See [Output Functions](../BioModelApplications/Simulations/output_functions.md).

## The model's properties

Select the model's name at the top of the tree to see **Saved MathModel Info**:

- **MathModel Name**, **VCell Identifier** (`mathmodel-` followed by the database key), **Owner** and
  **Last Modified**. These are filled in only once the model has been saved to the database.
- **Permissions** - who may open the model. **Change Permissions...** lets you change this; it is
  available only for a saved model. See [Permissions](../Introduction/Permissions.md).
- Below these, a tree with the contents of the saved model as recorded in the database, including
  any publications it is linked to. Clicking a publication's DOI or web link opens it in your web
  browser.

## Annotation

When the model's name is selected, the **Object Properties** tab below the view shows a **Notes**
box. Type a free-text description of the model there: its purpose, sources, assumptions. The text
is kept when you click or tab away from the box, and is saved with the model.
