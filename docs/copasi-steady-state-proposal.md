# Proposal: COPASI-style steady-state solving for ODE models

**Status:** proposal only. This branch (`copasi-proposed`) contains this document and no code changes.

## Summary

The ODE solver (`vcell-ode`, the `SundialsSolverStandalone_x64` binary VCell already runs for CVODE) now has a
steady-state mode: a port of COPASI's steady-state method (damped Newton, falling back to forward integration
with Newton retried after each run), plus COPASI's Jacobian stability analysis. It is selected by
`SOLVER CSSS` in the `.cvodeInput` file. Nothing new has to be shipped besides an updated binary.

On the VCell side, the pieces that already exist are:
- a saved steady-state task type (`TASK_STEADY`), which nothing uses yet;
- a settings panel on the `Steady-State-Feature` branch that isn't connected to anything.

This proposal connects them:
1. Store the settings on the simulation.
2. Write them into the solver input.
3. Show the single-row result and the "no steady state" failure sensibly.

The solver side is documented in vcell-ode's `docs/steady-state-solver.md` (branch `stabilize-new-build`). That
covers the algorithm, the differences from COPASI and the checks against COPASI 4.48 itself.

## What the solver expects and produces

**Input.** An ordinary CVODE input, as written today by `OdeFileWriter.write()`. Two things change: `SOLVER CSSS`
replaces `SOLVER CVODE`, and a steady-state section is added. All five keys are required, in any order:

```
SOLVER CSSS
STARTING_TIME 0.0
ENDING_TIME 1.0
RELATIVE_TOLERANCE 1.0E-9
ABSOLUTE_TOLERANCE 1.0E-12
STEADY_STATE TRUE
RESOLUTION 1e-9
MAX_NEWTON_ITERATIONS 50
MAXIMUM_FORWARD_DURATION 1e9
DERIVATION_FACTOR 0.001
STOP_CRITERIA DISTANCE_AND_RATE
NUM_EQUATIONS ...
```

| Key | Meaning | Default (= COPASI) | Valid values |
|---|---|---|---|
| `RESOLUTION` | Convergence threshold | 1e-9 | > 0 |
| `MAX_NEWTON_ITERATIONS` | Newton iterations per attempt | 50 | integer ≥ 0 |
| `MAXIMUM_FORWARD_DURATION` | Longest forward integration tried | 1e9 | > 0 |
| `DERIVATION_FACTOR` | Relative step for the finite-difference Jacobian | 0.001 | > 0 |
| `STOP_CRITERIA` | Convergence test | `DISTANCE_AND_RATE` | `DISTANCE_AND_RATE`, `DISTANCE_ONLY`, `RATE_ONLY`, `DISTANCE_OR_RATE` |

`SOLVER` must come before `NUM_EQUATIONS`, which it already does in `OdeFileWriter`. Some inputs are unused for
steady state but harmless:
- `ENDING_TIME`, `MAX_TIME_STEP` and the output-time keys are ignored.
- `STARTING_TIME` and the tolerances apply to the fallback integrations.
- Discontinuities and events are supported.

**Output.**
- **`.ida` file:** the usual header plus **one row** holding the steady state. Its `t` is the time at which the
  state was reached: `STARTING_TIME` if Newton converged immediately, otherwise the end of the integration that
  led to it. `ODESimData.readIDADataFile` already reads this unchanged.
- **stdout:** the method log, the Jacobian, its eigenvalues and a COPASI-style stability report. None of it
  uses the `[[[...]]]` message markers that `MathExecutable.checkForNewApplicationMessages` looks for, so it
  doesn't interfere with message parsing.
- **Exit code:** 0 on success. If no steady state is found, the exit code is 253 and stderr reads
  `Steady state not found; see the method log for details`. `Executable.executeProcess` already treats this as
  an error (`executable failed, return code = 253, stderr = '...'`).

## Current state of VCell

As of `master` 6da51554f0:

- `SolverTaskDescription.TASK_STEADY` (`vcell-core/.../solver/SolverTaskDescription.java:57`) exists. It is
  read and written in VCML (`TaskType Steady`, lines 628 and 931) and in XML (`Xmlproducer.java:4708`,
  `XmlReader.java:6545`). `getSteady()` (line 491) has no callers, and nothing in the UI sets the task type.
- `OdeFileWriter.write()` (`vcell-core/.../solver/ode/OdeFileWriter.java:217`) writes
  `SOLVER + getSolverName()`. `CVodeFileWriter.getSolverName()` (line 83) returns `"CVODE"`.
- `CVodeSolverStandalone` runs the binary through `MathExecutable`. Results come back through
  `ODESimData.readIDADataFile` (`SimulationData.java:502`, `:771`).
- SED-ML: `jlibsedml` can read a `steadyState` simulation, but `SedMLImporter` (around line 237) rejects it as
  unsupported. The exporter never writes one.
- Branch `Steady-State-Feature` (PR #1553, merged into that branch on 2025-06-30, **not** into `master`) is
  3 commits / 3 files on top of `master`:
  - `SimulationListTableModel`: a "Steady State" toggle, enabled only for CVODE.
  - `SolverTaskDescriptionAdvancedPanel`: fields for Resolution, Max Newton Iterations, Max Forward Duration
    and Derivation Factor, plus a stop-criteria combo box.
  - `SolverTaskDescription`: a non-persisted `bSteadyState` flag.

  None of the panel values are read, saved or passed on.

## Proposed changes

### 1. Model: settings on the task

- Add a small immutable value class, e.g. `cbit.vcell.solver.SteadyStateSolverSpec`, holding the five values
  with the defaults from the table above. Give it:
  - `getVCML()` / VCML parsing;
  - `compareEqual`;
  - a copy constructor;
  - an enum for the stop criteria whose names are the solver keywords.
- In `SolverTaskDescription`, keep a `SteadyStateSolverSpec` field, non-null while `getTaskType() == TASK_STEADY`.
  Include it in:
  - the copy constructor;
  - `compareEqual`;
  - `getVCML()` and `readVCML()`.
- Use `TASK_STEADY` as the switch instead of the branch's `bSteadyState` flag. The flag isn't persisted, and
  the branch's own TODO suggests the existing task type.
- XML: in `Xmlproducer` / `XmlReader`, write and read the spec as a child element of `SolverTaskDescription`
  whenever the task is steady. An older file with `TaskType="Steady"` and no element gets the defaults.
- Validation (in the setter or in `SolverTaskDescription`'s checks):
  - steady state is allowed only when the solver description is CVODE;
  - non-spatial math only;
  - no stochastic, Langevin or rule-based models;
  - Resolution, Max Forward Duration and Derivation Factor must be > 0;
  - Max Newton Iterations must be an integer ≥ 0.

### 2. UI

Rebase `Steady-State-Feature` onto `master`. Then:
- The "Steady State" toggle in `SimulationListTableModel` sets `TASK_STEADY` / `TASK_UNSTEADY`. Switching back
  to unsteady keeps the spec, so the user's values aren't lost.
- `SolverTaskDescriptionAdvancedPanel` fills its fields from the spec and writes edits back through the task
  description. It also hides or disables fields that don't apply to steady state: ending time, output
  interval, keep-every and max time step.
- Combo box labels map to keywords:

  | Label | Keyword |
  |---|---|
  | "Distance AND Rate" (default) | `DISTANCE_AND_RATE` |
  | "Distance only" | `DISTANCE_ONLY` |
  | "Rate only" | `RATE_ONLY` |
  | "Distance OR Rate" | `DISTANCE_OR_RATE` |

### 3. Writing the solver input

In `OdeFileWriter.write()` / `CVodeFileWriter`:

```java
@Override
String getSolverName() {
    return simTask.getSimulation().getSolverTaskDescription().getTaskType() == SolverTaskDescription.TASK_STEADY
            ? "CSSS" : "CVODE";
}
```

and, after the tolerance lines:

```java
if (solverTaskDescription.getTaskType() == SolverTaskDescription.TASK_STEADY) {
    SteadyStateSolverSpec spec = solverTaskDescription.getSteadyStateSolverSpec();
    printWriter.println("STEADY_STATE TRUE");
    printWriter.println("RESOLUTION " + spec.getResolution());
    printWriter.println("MAX_NEWTON_ITERATIONS " + spec.getMaxNewtonIterations());
    printWriter.println("MAXIMUM_FORWARD_DURATION " + spec.getMaxForwardDuration());
    printWriter.println("DERIVATION_FACTOR " + spec.getDerivationFactor());
    printWriter.println("STOP_CRITERIA " + spec.getStopCriteria().name());
}
```

Everything else the writer emits stays the same. No new `SolverDescription`, executable or `SolverFactory`
entry is needed, because `CVodeSolverStandalone` already launches the right binary, locally and on the server.

### 4. Results

- The `.ida` file is read as today and yields a one-row `ODESimData`. Places that assume a time course should
  handle a single point:
  - the ODE plot/data viewer: show a table of variable values, or a single-point plot;
  - progress: `SundialsSolver.getApplicationMessage` maps progress onto `[start, end]` time. A steady-state
    run sends no progress, only completion, so the job looks indeterminate until it finishes;
  - exports that assume more than one time point.
- Optionally keep the solver's stdout (`Executable.getStdoutString()`) with the results, so the method log and
  stability report can be shown. A "Stability" tab or a text dialog is enough to start with.

### 5. Failures

A run that finds no steady state ends with exit 253 and is reported through the existing error path. Proposed
improvements:
- recognize exit 253 and show "No steady state found" instead of a raw executable error;
- attach the method log (stdout), which lists every Newton and integration attempt and its target values. This
  is what a user needs to adjust Resolution or Max Forward Duration.

### 6. Dependency on vcell-ode

VCell must ship a vcell-ode build that contains the CSSS solver. At the time of writing that is vcell-ode branch
`stabilize-new-build`, commits `f46c4895` and later, which are not yet on vcell-ode `master`. Older binaries
reject `SOLVER CSSS` at parse time. The same change made the binary's exit code reflect solver failures (it
used to exit 0 regardless), which VCell's `Executable` already handles correctly.

## Possible follow-ups (not part of the first step)

- **SED-ML.** Import `steadyState` simulations instead of rejecting them, and export steady-state tasks as
  `steadyState`, with a KiSAO term for a Newton-type steady-state method. The exact term still needs to be
  chosen.
- **Remaining COPASI options.** The solver has internal defaults for `USE_NEWTON`, `USE_INTEGRATION`,
  `USE_BACK_INTEGRATION`, `MAX_BACKWARD_DURATION` and `ACCEPT_NEGATIVE`. Exposing them needs a keyword on both
  sides; add them only if users ask.
- **Tutorials and parameter scans.** Steady states are currently approximated by the last row of a long time
  course. Parameter scans over a steady-state task would give dose-response curves directly.
- **Conservation laws.** With VCell's "conservation laws" option off, a model can keep a conserved total in its
  ODEs. Then the Jacobian is singular, and Newton can land on a state that breaks the total. COPASI behaves the
  same on such ODEs. Either require the option to be on for steady-state tasks, or warn.

## Testing

- Unit:
  - `SteadyStateSolverSpec` VCML/XML round-trip;
  - `compareEqual`;
  - older `TaskType="Steady"` files loading with defaults;
  - `CVodeFileWriter` output for a steady task compared against a golden `.cvodeInput`, checking `SOLVER CSSS`
    and the five keys.
- Integration: run one small model end to end (local solver) and compare against its analytic steady state.
  vcell-ode's `tests/unit/resources/steady_state_*.cvodeInput` models have known answers.
- Negative case: a model with no steady state (e.g. `x' = 1`) should be reported as "No steady state found",
  with the log available.

## Open questions

1. Should the steady-state toggle live in the simulation list (as on `Steady-State-Feature`) or in the solver
   panel, next to the task type?
2. Should steady state be offered for spatial or other non-CVODE math later, or stay CVODE-only?
3. Should a steady-state run store the stability report in the database with the results, or only show it
   after a local run?
