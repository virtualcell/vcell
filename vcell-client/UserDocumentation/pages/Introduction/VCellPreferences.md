# VCell Properties

> **AI-drafted page.** An AI assistant wrote this page from VCell's source code. The VCell
> development team has not yet reviewed it, so it may be incomplete or wrong.
> ([Tracking issue](https://github.com/virtualcell/vcell/issues/2226))

**Help > VCell Properties ...** opens the **View VCell Properties** window, which shows where VCell
keeps its files on your computer. It is useful when you need to find VCell's logs or local
simulation results, for example to send them to VCell support.

The list on the left has one entry, **General Properties**, shown on the right. The window only
displays these locations; none of them can be changed here. **Close** closes the window.

## General Properties

| Property | What it is |
| --- | --- |
| **Installation Directory** | Where VCell itself is installed. Marked **(install)**. |
| **VCell Home Directory** | VCell's folder for your own files: a folder named `.vcell` in your home folder. Marked **(home)**. |
| **Simulation Data** | Where results of simulations run on your own computer are kept: `simdata`, inside the VCell Home Directory. |
| **Log Data** | Where VCell writes its log files: `logs`, inside the VCell Home Directory. |
| **Local Solvers** | Where the solver programs that run simulations on your own computer are installed, inside the Installation Directory. |

The last three are shown relative to the folder they are in: **(home)** stands for the VCell Home
Directory and **(install)** for the Installation Directory.

The `.vcell` folder starts with a dot, so on macOS and Linux it is hidden in the file browser by
default.

See also [Help](Help.md) for the other items of the Help menu.
