# nativelibs

Where native libraries are staged per platform, for the desktop installers and the service images.

**This directory is intentionally empty.** VCell ships no native libraries at present: HDF5 was the
last one, and writing HDF5 exports moved to the pure-java `io.jhdf` (see virtualcell/vcell#2001;
reading moved earlier in #1903 and #1906).

The plumbing around it is deliberately kept — `NativeLib`, `NativeLoader`, the install4j mount
points, and the `COPY ./nativelibs/...` lines in the Dockerfiles — so that adding a native library
later is a matter of dropping files in here and declaring an entry in `NativeLib`, rather than
rebuilding the path from scratch.

The per-platform subdirectories are tracked (each holds only this note) so the packaging that
references them keeps working while they are empty.
