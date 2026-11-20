# VCML Editor

> **AI-drafted page.** An AI assistant wrote this page from VCell's source code. The VCell
> development team has not yet reviewed it, so it may be incomplete or wrong.
> ([Tracking issue](https://github.com/virtualcell/vcell/issues/2226))

The **VCML Editor** item in a [MathModel](MathModelOverview.md) window shows the model's math and
lets you edit it as text. It is the view a MathModel window opens with.

## Choose View

The two buttons at the top switch between views of the same math:

- **Math Description Language** (the default) - the whole math description as VCML text, which you
  can edit.
- **Math Equations** - a read-only view. A tree lists the math's constants, functions and domains;
  selecting an item shows its equations drawn as formulas. **Show parameter:** **name** or **value**
  chooses whether parameters appear by name or by their value.

This is the same display as a BioModel application's
[Generated Math](../BioModelApplications/Simulations/generated_math.md), except that here the text
can be edited.

## Editing the text

The text editor numbers its lines, shows VCML keywords in bold and comments in italics, and
supports **Ctrl+Z** (undo) and **Ctrl+Y** (redo).

- **Completion.** As you type a word, a list of matching VCML keywords may appear. Press
  **Ctrl+Space** to show the list at any point. Use the arrow keys to move through it, **Enter** to
  insert the selected entry and **Esc** to close it. Some entries insert a whole block to fill in,
  such as an ODE or PDE equation, a jump process, an event or a random variable, with placeholder
  names and values of `0.0`.
- **Search.** Type in the **Search** box below the text to highlight every match (case is ignored).
  The up and down arrow buttons move to the **Previous** and **Next** match. The box turns pink when
  nothing matches; **Esc** clears the search.

The geometry is not part of what you edit here: the math always uses the model's current geometry,
set under the **Geometry** item.

## Applying or cancelling changes

Changes you type are not part of the model until you apply them. As soon as you edit the text,
**Apply Changes** and **Cancel** become available.

- **Apply Changes** reads the text as VCML. If it can be read, it replaces the model's math, both
  views show the new math, and the two buttons are disabled again.
- **Cancel** throws your edits away and shows the model's current math again.

If the text cannot be read, VCell shows an error message and leaves your text as it is. When the
error names a line, the cursor moves to the start of that line. Correct the text and click **Apply
Changes** again.

Applying only checks that the text is valid VCML that VCell can read. It does not guarantee that the
math can be simulated; a solver may still report problems when you run a simulation.

While there are unapplied changes, VCell will not save the model (**Save** or **Save As**) or run
its simulations. It shows the message *Changes have been made in VCML Editor, please click "Apply
Changes" or "Cancel" to proceed.* Apply or cancel your edits, then try again.
