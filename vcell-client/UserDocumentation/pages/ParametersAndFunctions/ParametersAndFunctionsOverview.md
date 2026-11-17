# Parameters and Functions Overview

The Virtual Cell allows the user to define parameters that can be used in the modeling of complex
cell processes. User-defined parameters can be added at the physiology level either as **global**
parameters or as **reaction** parameters. There are other predefined parameters such as species
initial conditions or reaction rates for the different kinetic types that are generated as the model
is being developed, for which the user has to provide values. The parameter values can be numeric or
expressions involving other allowable variables in the model. Parameter values that are expressions
are referred to as **Functions** in the Virtual Cell.

![](../../images/ParamsAndFnsMain.png)

**Viewing/Editing Parameters and Functions**

Clicking **Mathematics & Units** in the 'Navigation' panel tree structure opens its **Summary** in
the 'View' panel on the right; the nodes under it open its other tabs. There are 4 tabs:

- **Summary** - an overview of the model's mathematics: the name of its unit system and its units
  (**Change...** opens the Model Unit System tab); how many parameters and functions it has, under
  the same headings as the Parameters and Functions table's **Defined In** choices (Global,
  Reactions and Rules, Applications), each broken down beneath (global parameters, species and
  structure sizes; reactions and reaction rules; each application); and the measured physical
  constants VCell's equations use, whose accepted values science refines over time (the Faraday
  constant, Avogadro's number, the gas constant and KMOLE), with their values and units in the
  model's unit system (**All Predefined Expressions** opens the Predefined Expressions tab).
  Double-click a row of the counts, or choose it and click **Show in Parameters and Functions**, to
  list what it counts there; a row under Global or Reactions and Rules lists all of its heading's.
- [Model Unit System](ModelUnits.md) - that displays the system of units that are used in the
  Virtual Cell, and chooses the model's.
- [Parameters and Functions](ParametersAndFunctions.md) - that displays the parameters in the model
  and allows the user to define other parameters.
- [Predefined Expressions](PredefinedConstAndMathFns.md) - that displays the predefined constants
  and math functions that are used in the Virtual Cell.
