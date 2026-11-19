# Very Long Simulations

> **AI-drafted page.** An AI assistant wrote this page from VCell's source code. The VCell
> development team has not yet reviewed it, so it may be incomplete or wrong.
> ([Tracking issue](https://github.com/virtualcell/vcell/issues/2226))

Simulations that you run on the VCell server have a time limit. By default, a simulation that has
been running for **21 days** is stopped automatically. This frees the server's computers from
simulations that were forgotten or have stopped making progress, in the same way for everyone.

Most simulations finish long before this. If yours needs longer, the limit can be lifted for it,
but only by users that the VCell team has added to its list of *power users*.

## Disabling the time limit

In the [simulation editor](simulationEditor.md), the **Solver** tab has a **General** section
with the simulation's time settings. Below them is the **Disable Simulation Run Timeout** checkbox;
its tooltip reads "Override the 21 days limit for the duration of a Simulation run."

- If you are on the power user list, the checkbox is available. Check it and run the
  simulation: it will not be stopped after 21 days and can keep running until it finishes.
- Otherwise the checkbox is greyed out and the 21-day limit applies. The server also checks the
  list when the simulation starts, so the limit is lifted only for power users.

The setting is saved with each simulation, so check it on every simulation that needs it.

## Asking to become a power user

If you need to run a simulation for longer than 21 days, write to VCell support at
[vcell_support@uchc.edu](mailto:vcell_support@uchc.edu) and ask to be added to the power user list.

The **?** button next to the checkbox shows the same explanation.

## Related pages

- [Simulations Editor](simulationEditor.md)
- [Simulations](simulations.md)
