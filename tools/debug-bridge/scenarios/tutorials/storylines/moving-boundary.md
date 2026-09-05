# BioModel using the Moving Boundary Solver

- **Source:** `MovingBoundaries.pdf` (67 pp, 2022-09-13)
- **Superseded by a 7.7 rewrite?** No.
- **Reproduced by:** `../moving-boundary.sh`
- **Status:** reproduced through the Kinematics tab, model builds with 0 errors.

## Objective

FRAP again, but in a *moving* cell: the membrane is given a velocity and the domain
deforms during the simulation. Teaches Kinematics and the Moving Boundary solver.

## Storyline

1. **Structures**, three: `EC` | `PM` | `Cyt` — the same shape as Simple FRAP.
2. **Species.** One only: `Dex`. No reactions.
3. **Application.** Deterministic, renamed `FRAP`.
4. **Geometry.** *Analytic Equations (2D)* — the solver is 2D-only, by design.
   `subdomain0` → `EC`; add a Circle, centre `0,0`, radius `5` → `Cyt`.
   Edit Domain: extent `20 µm` in x and y, origin `-6` in X and `-10` in Y, which puts
   the circle at the left edge so it has room to travel.
5. **Kinematics tab.**
   - *New → Surface Kinematics*: velocity `x = 4` µm/s, `y = 5*sin(10*t)` µm/s.
   - *New → Volume Kinematics* for `vobj_Cyt1`; delete any volume process created for
     the wrong object. The Spatial Object must have **Interior Velocity enabled** in
     Object Properties or the process reports an error.
   - Give the volume the same x and y velocities, so the cytosol travels with the membrane.
6. **Structure mapping**, as usual.
7. **Specifications.** `Dex` initial condition is the Boolean giving 10 µM outside the
   square `-2.5..2.5` in x and y and 0 inside. Diffusion constant stays at the default
   `10.0` µm²/s.
8. **Simulation.** Mesh kept small (the tutorial warns the run is slow otherwise), then
   run with the Moving Boundary solver.
9. **Results.** VCell's own spatial analysis tools do **not** work on moving-boundary
   results — export to NRRD/HDF5 or a QuickTime movie and analyse elsewhere.

## What the script covers

Steps 1–7 are scripted and verified: both spatial processes end up carrying `velocityX = 4`
and `velocityY = 5*sin(10*t)`, the unwanted EC volume process is deleted, and the model
reports 0 errors. Step 8 (creating and running the simulation) is left out for the same
reason as in Simple FRAP — it needs an account and dispatches a real job.

## Things the Kinematics tab teaches you the hard way

- **Volume kinematics lands on the wrong object first.** VCell creates a process for the
  next volume in the spatial-object table each time, so the first attempt targets `EC` and
  a second is needed to reach `Cyt`; the tutorial's instruction to "delete the one you did
  not want" is not a nicety.
- **The Interior Velocity checkbox is on the OBJECT, not the process.** A volume process
  reports an error until `vobj_Cyt1` is allowed to have one, and the checkbox lives in the
  spatial object's properties.
- **The parameter table's identity column is not column 0.** It leads with a prose
  description — "surface velocity (x coord)" — and carries `velocityX` in the next column,
  which is why `findrow` had to learn to search a named column.
- **A panel's columns arrive before its rows.** Selecting a process yields the four
  headers immediately and the velocity rows a moment later, so a lookup has to retry
  rather than sleep.
