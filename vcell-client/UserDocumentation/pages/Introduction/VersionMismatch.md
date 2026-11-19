# Version Mismatch

> **AI-drafted page.** An AI assistant wrote this page from VCell's source code. The VCell
> development team has not yet reviewed it, so it may be incomplete or wrong.
> ([Tracking issue](https://github.com/virtualcell/vcell/issues/2226))

Each time VCell connects to the VCell servers, it compares its own version with the version the
servers are running. When they differ, the **Welcome window** shows a **Version mismatch** notice
below your account: an amber caution sign, which pulses a few times and then stays, followed by a
small round **?**. Click the **?** to see both versions, for example "The latest VCell version is
7.7.0 (build 12); this is 7.6.0 (build 3)."

## What it means

Your copy of VCell is older (or, rarely, newer) than the one the servers expect. Versions are
compared by their first three numbers (major, minor and patch, as in 7.6.0). Builds of the same
version (the build number in parentheses) are treated as matching, so a different build alone does
not show the notice.

The check is made only against the VCell servers: when you work without logging in (see
[Logging into VCell](Login.md)), there is nothing to compare with.

## What you can still do

You can keep working, but until you upgrade, **some features will be unavailable**, because this
client and the servers are not known to work together. Upgrade as soon as you can.

If the servers are updated while VCell is already running, VCell also shows a message saying that
the server was updated while you were working. **Save your work** first, then exit and restart
VCell.

## How to upgrade

1. Save any open work and exit VCell.
2. Download the current version of VCell from the VCell website, https://vcell.org.
3. Install it and start VCell again.

Once the versions match, the notice no longer appears on the Welcome window.
