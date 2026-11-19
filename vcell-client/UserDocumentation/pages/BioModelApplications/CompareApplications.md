# Comparing Applications

> **AI-drafted page.** An AI assistant wrote this page from VCell's source code. The VCell
> development team has not yet reviewed it, so it may be incomplete or wrong.
> ([Tracking issue](https://github.com/virtualcell/vcell/issues/2226))

Two [applications](ApplicationOverview.md) of the same BioModel can be compared side by side, to
see exactly where their settings differ: geometry and structure mapping, species and reaction
specifications, protocols, simulations, and the math VCell generates for each.

## Starting a comparison

1. Select **Applications** in the BioModel's navigation tree to show the list of applications.
2. Select exactly two applications in the list (hold **Ctrl**, or **Cmd** on macOS, and click the
   second one). The **Compare...** button is available only while two applications are selected.
3. Click **Compare...**.

Before comparing, VCell regenerates the math for both applications, so the comparison reflects
their current state. For large or rule-based applications this can take a moment.

The result opens in a window titled **comparing application** *first* **and** *second*. The
application higher in the list is the **baseline**; the other is the **modified** one.

## Reading the result

The window shows a tree of everything the two applications contain, merged into one. An icon beside
each item marks how it differs; hovering over an item shows a tooltip such as "New ...", "Removed
..." or "Altered ...".

- **Altered** items exist in both applications but differ. A folder whose contents differ is marked
  too, so you can follow the marks down the tree to the item that changed.
- **New** items are only in the modified application.
- **Removed** items are only in the baseline application.
- Unmarked items are the same in both.

Select an item to see its value in each application in the two text areas below the tree, labeled
**Baseline:** and **Modified:**, each with the name of its application. An area is empty when the
item exists in only one application.

## Associating items

When two items that represent the same thing were not matched (for example, one shows as removed
and a similar one as new), you can pair them yourself:

- **Associate**: select the two items (one from each application) and click **Associate** to treat
  them as the same item.
- **Disassociate**: select an altered item and click **Disassociate** to treat its two versions as
  separate items.

Associating and disassociating only change how the comparison is shown. They do not change either
application.

See also [Compare With Saved](../Introduction/CompareWithSaved.md), which compares a whole model
with its saved version in the same way.
