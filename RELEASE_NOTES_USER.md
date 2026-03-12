# VCell 7.8.0 Release Notes

**Release Date:** December 2025

---

## What's New

### Faster, More Reliable Exports
We've completely rebuilt the export system from the ground up. Exports are now more reliable, faster, and you can track their progress in real-time. All exports now use the N5 format, which provides better performance and compatibility with modern visualization tools.

### Improved SpringSaLaD Simulations
- **Run multiple trials in parallel** - SpringSaLaD simulations can now run many trials simultaneously on our computing cluster, dramatically reducing wait times for statistical analysis
- **Built-in statistics** - Automatically calculate averages, standard deviations, and min/max values across multiple simulation runs
- **Better validation** - The system now checks your model parameters before running to catch common issues early

### Enhanced Field Data Management
- Create field data directly from simulation results without downloading files first
- Copy field data between your BioModels more easily
- Significantly faster processing for large datasets

### Better Connection Stability
- Fixed issues where VCell would lose connection after extended periods
- Improved Google login - switching between accounts now works correctly
- More reliable reconnection when network issues occur

### VCell Support Access
Support staff can now view models you've shared with "VCell Support" to help troubleshoot issues more effectively.

---

## Bug Fixes

- Fixed PubMed links in the Annotations tab
- Fixed geometry viewer cropping issues
- Fixed errors when inserting compartments in some models
- Fixed Math Model export errors
- Fixed parameter estimation requiring repeated logins
- Fixed "Constructed Solid Geometry" typo (now correctly shows "Constructive Solid Geometry")
- Fixed issues with spatial simulation output functions being ignored

---

## Pathway Commons Integration

Importing pathways from Pathway Commons has been upgraded to support the latest BioPAX Level 3 format, providing access to more comprehensive pathway data.

---

## System Requirements

No changes to system requirements from the previous version. VCell 7.8.0 is available for:
- Windows 10/11
- macOS 11 (Big Sur) and later
- Linux (Ubuntu 20.04+ recommended)

---

## Getting Help

- **Email:** vcell_support@uchc.edu
- **Discussion Forum:** https://groups.google.com/group/vcell-discuss
- **Documentation:** https://vcell.org

---

## Thank You

Thank you to all users who reported bugs and provided feedback. Your input helps make VCell better for the entire computational biology community.
