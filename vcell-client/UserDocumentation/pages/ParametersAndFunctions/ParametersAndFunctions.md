# Parameters and Functions

Users can define parameters to aid in modeling their virtual experiments or
[applications](../BioModelApplications/ApplicationOverview.md). User-defined parameters can be added
at the physiology level either as **global** parameters or as **reaction** parameters. There are
other predefined parameters such as species initial conditions, structure sizes or reaction rates
for the different kinetic types that are generated as the model is being developed, for which the
user has to provide values. The parameter values can be numeric or expressions involving other
allowable variables in the model. Parameter values that are expressions are referred to as
**Functions** in the Virtual Cell.

![](../../images/ParamsAndFns.png)

**Viewing/Editing Parameters and Functions**

- **Activate** by clicking **Mathematics & Units** in the tree list of the **Navigation panel**
  (window upper left), which will display the Parameters and Functions (window right panel).
- **View** its table: A table of information including Name, Expression (value of
  parameter/function), 'Defined In' (where the parameter/function is defined), Description (type of
  parameter/function) and Units. **Columns**, beside Filters, opens a list of the columns at the
  table's right: untick one to hide it, and move it left or right with its arrows (or drag its
  header in the table); **Reset** brings back every column in this order. **Defined In** can be
  'Global', 'Model/Reaction' (created in [reactions add/edit](../PropertiesPanes/PP_Reactions.md)),
  'App' (varies in application, e.g. initial conditions, compartment size, diffusion, boundary
  condition, etc... See
  [application specifications](../BioModelApplications/Specifications/SpecificationsOverview.md)),
  or 'Predefined' (VCell's own constants and math functions, see
  [Predefined Expressions](PredefinedConstAndMathFns.md)).
- **Filter** displayed items with the **Filters** button above the table; it says how many choices
  narrow the table (e.g. 'Filters (2)'), and 'Showing ... entries' beside it says how many rows
  there are. Under **Defined In**, 'Global' displays global model parameters (those created with the
  'New Global Parameter' button, and the model's species and structure sizes); 'Reactions and
  Rules' displays the parameters and constants defined within reactions and rules; 'Applications'
  displays application related items such as diffusion, initial concentration, boundary condition,
  etc..., in all applications or, when there are several, just one. Under **Type**, 'Parameters'
  (named numerical values) and 'Functions' (expressions that may contain parameters). Under
  **Origin**, 'User declared' (parameters you created: global and application parameters, and those
  you added to a kinetic law), 'Auto-generated' (made with something else: a species, a structure's
  size, a kinetic law's parameters, an application's initial conditions) and 'Preexisting' (VCell's
  own constants and functions, there before any model; not shown until chosen). 'Reset Filters'
  shows everything but the preexisting again.
- **Model Unit System...**, beside the buttons below the table, opens the
  [Model Unit System](ModelUnits.md) window.
- **Search** table rows by entering text in the **Search** text-box. The table will be updated to
  display only rows that have matching text in one or more of the columns.
- **Edit Parameter/Function** a parameter or function by selecting a row from the displayed table.
  Detailed information about the selected parameter/function will appear in the 'Object Properties'
  tab below the table. The information includes Name, Description, Unit, Expression, Annotation. One
  or more of the displayed detail items are editable depending on the parameter/function type.
  'Expression' and 'Unit' can also be edited by double-clicking in the table cell or by activating
  the mouse pop-up menu within a table cell. See also
  [Parameters Properties Pane](../PropertiesPanes/PP_Parameters.md).
- **Add Global Parameter** by clicking the **New Global Parameter** button. The new parameter will
  appear in the table as a 'Global Parameter' that can be used in expressions in all applications in
  the BioModel.
- **Delete Global Parameter** (column **Description** 'Global Parameter') by selecting one or more
  table rows and pressing the **Delete global Parameter(s)** button. If the 'Global Parameter' is
  used in an expression anywhere in the VCell model a warning dialog will be displayed and the
  parameter will not be deleted.
