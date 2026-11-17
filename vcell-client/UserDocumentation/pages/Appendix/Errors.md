# Error List

The [Problems Tab](../PropertiesPanes/PP_Problems.md) within the Properties pane provides errors
identified by VCell.

**Kinetics Errors**

- **General Current Kinetics expected within a flux reaction only.**

  This error occurs in a simple reaction (not a flux), when the user chooses the kinetic type
  'General Current Kinetics'. This type of kinetics is not available for a simple reaction.

- **GeneralPermeability Kinetics must have exactly one reactant/product.**

  This error is displayed when the user chooses 'General Permeability Kinetics' for a reaction that
  does not have exactly one reactant and one product.

- **GHK Kinetics must have exactly one reactant/product.**

  See above.

- **HMM_IRRKinetics must have exactly one reactant.**

  See above.

- **HMM Reversible Kinetics must have exactly one reactant/product.**

  See above.

- **Nernst Kinetics must have exactly one reactant/product.**

  See above.

- **Cyclic dependency in the parameter definitions**

  In a kinetics rate expression, if the parameters used in the expression are cyclically dependent,
  for example : if parameter 'a' is defined in terms of 'b' and parameter 'b' is defined in terms of
  'a'; there is cyclic dependency between 'a' and 'b'. This is not allowed in the Virtual Cell.

- **references undefined symbol 'symbolName'.**

  The kinetics rate expression uses a symbol that has not been previously defined in the model and
  is not a local kinetic parameter.

**Structures/compartments Errors**

- **Size parameter is not set.**

  The specified structure/compartment size is not initialized.

- **Size parameter is not positive.**

  The specified structure/compartment size is not positive.

- **Size parameter is not a constant.**

  The specified structure size is not a constant value (presently, the Virtual Cell does not allow
  non-numeric values for structure size).

- **Compartment/Membrane 'structureName' is not mapped to a geometry subdomain.**

  The feature or membrane represented 'structureName' has not been mapped to any subdomains in the
  specified geometry for the application. This is needed to proceed with the modeling.

- **Subdomain 'geometryClassName' is not mapped to any physiological structure.**

  The subdomain in the geometry specified in an application represented 'geometryClassName' has not
  been mapped to any compartment in the model. This is needed to proceed with the modeling.

**Parameter Estimation Errors**

- **lower bound is higher than upper bound for parameter 'paramName'.**

  This error occurs in Parameter Estimation, when user specifies a lower bound that is greater than
  the upper bound.

- **initial guess is outside of bounds for parameter 'paramName'.**

  This error occurs in Parameter Estimation, when user specifies an initial guess for parameter
  value which is not between the lower and upper bounds specified for the parameter.

- **No parameters are selected for optimization. Please select parameters by checking 'optimize' in
  the 'Parameters' table.**

  This error occurs in Parameter Estimation, when user has not selected any parameter for
  optimization and starts the optimization process.

- **missing time data column with name 't'.**

  This error occurs in Parameter Estimation, when the reference data provided for parameter
  estimation does not have a column for time named 't'. A column 't' for time in reference data is
  required.

- **data column 'varName' not found in math model.**

  This error occurs in Parameter Estimation. The reference data has a data column for variable
  'varName'. The VCell optimization algorithm tries to find the 'varName' in VCell generated math
  for the optimization problem. This error is thrown when 'varName' from the reference data is not
  found in the VCell generated math.

- **data column 'varName' not a variable or function in math model.**

  This error occurs in Parameter Estimation. The reference data has a data column for variable
  'varName'. The VCell optimization algorithm tries to find the 'varName' in VCell generated math
  for the optimization problem. This error is thrown when 'varName' is found in found in the VCell
  generated math, but is not a Volumen variable or a function.

- **data column 'varName' not a variable or function in math model.**

  This error occurs in Parameter Estimation. The reference data has a data column for variable
  'varName'. The VCell optimization algorithm tries to find the 'varName' in VCell generated math
  for the optimization problem. This error is thrown when 'varName' is found in found in the VCell
  generated math, but is not a Volumen variable or a function.

**Math Generation Errors**

- **No geometry defined**

  If the geometry is not set (in an application in the BioModel or explicitly set in a MathModel),
  this error is thrown when user tries to generate the math (by creating a simulation in a BioModel
  application or when user edits the math description language and clicks the 'Apply Changes' button
  in a MathModel). Specify a geometry if the application/MathModel is a spatial one, or choose the
  default conpartmental geometry to proceed.

- **Constant cannot be evaluated to a number: 'expression'**

  When math is being generated, if a variable has been created as a constant but is an expression
  that does not evaluate to a constant, this error is thrown.

- **Expression Binding Exception**

  During math generation, when generating the equation rates, initial conditions and other
  expressions for each subdomain, if the rate, initial condition or any other expression cannot be
  bound to the 'MathDescription' (i.e., there are unresolved identifier symbols in the
  expression(s)), an Expression Binding Exception is thrown (indicating that there are unresolved
  identifiers that are used in the expression).

- **Expression Exception**

  During math generation, when generating the equation rates, initial conditions and other
  expressions for each subdomain, the validity of each equation is checked. If this check fails, the
  'Expression Exception' error is encountered.

- **Math Exception**

  Same as above.

- **Compartmental Model requires exactly one CompartmentSubDomain**

  Math is generated based on the geometry (application geometry in a BioModel or geometry in
  MathModel), and a set of equations are created for the variables in each subdomain in the
  geometry. For a compartmental (nonspatial) model, the geometry is default, with dimension 0, hence
  there should only be one subdomain which should be a compartmentSubdDomain.

- **Compartmental Model requires the subdomain be a CompartmentSubDomain**

  While generating math, if a compartmental (nonspatial) model has one subdomain, but it is not a
  compartmentalSunDomain, this error is generated. In a compartmental model (which has a default
  geometry of dimension 0), there can only be one subdomain and it should be a CompartmentSubDomain.

- **Compartmental model, unexpected equation of type PdeEquation, must include only OdeEquation**

  This error is thrown when a PDE is defined in a compartmental model. While generating math, in a
  compartmental (nonspatial) model where there is no spatial geometry, only ODEs can be defined.

- **Compartmental model, expecting at least one OdeEquation**

  This error is thrown when no ODE is defined in a compartmental model. While generating math, in a
  compartmental (nonspatial) model where there is no spatial geometry, only ODEs can be defined.

- **Compartmental model, must declare an OdeEquation for each VolumeVariable.**

  While generating math, in a compartmental (nonspatial) model, each VolumeVariable (independent
  variable) should have a corresponding ODE defined. This error is thrown when the number of ODEs
  and number of VolumeVariables don't match.

- **Compartmental model, must not declare any MembraneVariable.**

  While generating math, a compartmental (nonspatial) model contains only a CompartmentSubDomain
  which can only have equations for (volumetric) cmpartment variables (VolumeVariables). This error
  is thrown if an equation for a MembraneVariable is defined.

- **Compartmental model, must not declare any FilamentVariable.**

  While generating math, a compartmental (nonspatial) model contains only a CompartmentSubDomain
  which can only have equations for (volumetric) cmpartment variables (VolumeVariables). This error
  is thrown if an equation for a FilamentVariable is defined.

- **Compartmental model, must not declare any VolumeRegionVariable.**

  While generating math, a compartmental (nonspatial) model contains only a CompartmentSubDomain
  which can only have equations for (volumetric) cmpartment variables (VolumeVariables). This error
  is thrown if an equation for a VolumeRegionVariable is defined.

- **Compartmental model, must not declare any MembraneRegionVariable.**

  While generating math, a compartmental (nonspatial) model contains only a CompartmentSubDomain
  which can only have equations for (volumetric) cmpartment variables (VolumeVariables). This error
  is thrown if an equation for a MembraneRegionVariable is defined.

- **Compartmental model, must not declare any FilamentRegionVariable.**

  While generating math, a compartmental (nonspatial) model containsn only a CompartmentSubDomain
  which can only have equations for (volumetric) cmpartment variables (VolumeVariables). This error
  is thrown if an equation for a FilamentRegionVariable is defined.

- **Spatial model, there are 'n' subdomains in geometry, but 'm' CompartmentSubDomains in math
  description. They must match.**

  While generating math, in a spatial model, the number of subdomains defined in the geometry must
  equal the number of CompartmentSubDomains defined in the math. This error is thrown when the two
  are not equal.

- **Spatial model, there are no FilamentSubDomains defined, cannot define FilamentVariable or
  FilamentRegionVariable.**

  While generating math, this error is thrown if a FilamentVariable or FilamentRegionVariable is
  defined in a spatial model if there are no the FilamentSubDomains defined.

- **Spatial model, there are no MembraneSubDomains defined, cannot define MembraneVariable or
  MembraneRegionVariable.**

  While generating math, this error is thrown if a MembraneVariable or MembraneRegionVariable is
  defined in a spatial model if there are no the MembraneSubDomains defined.

- **Spatial Model, cannot mix OdeEquation and PdeEquation for variable 'varName'.**

  This error is thrown if a variable 'varName' has both ODE and PDE defined in the math. A variable
  can have only an ODE or a PDE defined in a spatial model.

- **Spatial Model, cannot mix Steady and Unsteady PdeEquation for variable 'varName'.**

  This error is thrown if a variable 'varName' has both seady state PDEs and unsteady PDEs defined
  in the math. This is not allowed for a variable in a spatial model.

- **Spatial Model, there is neither a PdeEquation nor an OdeEquation for variable 'varName'.**

  This error is generated when a variable 'varName' defined in the math does not have an ODE or a
  PDE defined. This is not allowed.

- **Events are not supported in spatial models.**

  This error occurs when [events](../BioModelApplications/Protocols/Events.md) are defined in the
  spatial model. Events are currently not supported in spatial models in the Virtual Cell.

- **Spatial model, can't find a matching geometry subdomain for math subdomain 'subdomainM'. Math
  subdomain names must match geometry subdomain names.**

  This error is thrown if in a spatial model, the subdomain names in the math do not match the
  subdomain names in the defined geometry.

- **Spatial model, unexpected subdomain type for subdomain.**

  There are 3 types of subdomains that can be defined in a VCell math - CompartmentSubDomain,
  MembraneSubDomain and FilamentSubDomain. While generating math if a subdomain that is not one of
  the 3 mentioned above, this error is displayed.

- **Duplicate subDomains 'subDomain1' and 'subDomain2'.**

  In a spatial model, if there are 2 subDomains that have the same name, this error is thrown.
  Multiple subdomains with the same name are not allowed in the math.

- **Duplicate membrane subdomains between compartments 'compartment1' and 'compartment2'.**

  This error is generated when, in a spatial model, more than one membrane subDomain is defined
  between 2 volumetric compartments. Any 2 volumentric compartments can have only one memnbrane
  between them, and hence only one membrane subdomain between the corresponding
  CompartmentSubDomains.

- **CompartmentSubDomain priorities must be unique see 'compSubDomain1' and 'compSubDomain2'.**

  Each CompartmentSubDomain has an assigned priority. If the priorities are not unique, this error
  is thrown.

- **'Xp/Yp/Zp' and 'Xm/Ym/Zm' must both have periodic boundary condition for math subdomain
  'subDomain1'.**

  In a spatial model, in a given subDomain 'subDomain1', the positive and negative boundary
  conditions for X (or Y or Z) must be either both periodic or both not periodic. This error is
  generated if this ocndition is violated.

- **.**

**Model/Physiology errors**

- **Model symbol "XXXX" is used within "struct1" and "struct2"**

  A

- **Unresolved parameter 'paramName' in reaction 'ractionName'**

  A reaction 'reactionName' contains an expression that uses parameter 'paramName' that is an
  unknown symbol, i.e., 'paramName' has not been defined in the model. Only variables/symbols that
  have been already defined can be used.
