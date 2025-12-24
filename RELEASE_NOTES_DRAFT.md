# VCell Release Notes: 7.7.0.15 → 7.8.0

**Release Date:** December 2025
**Previous Release:** 7.7.0.15 (February 5, 2025)
**Commits:** 466 | **PRs Merged:** 55+

---

## Highlights

This release represents approximately 10 months of development with major infrastructure improvements, a rewritten export system, enhanced SpringSaLaD/Langevin solver capabilities, and significant progress migrating the VCell API to modern Quarkus REST endpoints.

---

## New Features

### Export System Overhaul
- **Complete rewrite of the export system** using Quarkus REST API
- Replaced ApacheMQ with ArtemisMQ for export job queuing
- Created separate Docker image for export service for better scalability
- Exports now use exclusively N5 format for improved reliability
- Added asynchronous export processing with proper error handling and user notifications
- New endpoints for tracking export job status

### SpringSaLaD/Langevin Solver Enhancements
- **Upgraded Langevin solver** through multiple versions (1.4.0 → 1.4.2 → 1.4.4)
- **SLURM array job support** for running multiple trial simulations in parallel with dynamic job allocation
- **Post-processing statistics** for multiple runs including:
  - Averages, standard deviation, min/max calculations
  - Cluster analysis for advanced statistics
- Physical limit validation for site sizes, diffusion rates, and partition sizes
- Improved sanity checks for reaction-limited restrictions and reaction radius calculations
- Particle displacement calculations for given diffusion rates and time intervals
- New UI for Steady State options in Simulation Options dialog (CVODE only)

### Field Data System Improvements
- **Moved field data processing to the server** (previously client-side), reducing network overhead
- New REST endpoints for field data operations:
  - Create field data from files
  - Create field data from simulation results
  - Copy field data between BioModels
- Improved memory management for large field data operations
- Algorithm optimization with reduced memory usage (up to 68% reduction)
- Removed complex FieldDataOpSpec system in favor of simpler, atomic operations

### Quarkus REST API Expansion
New endpoints added for modern API access:
- **BioModel endpoints**: save, delete, get, and info retrieval
- **MathModel endpoints**: full CRUD operations
- **Geometry endpoints**: creation and retrieval
- **VCImage resource**: image handling through REST API
- Improved error handling architecture with proper HTTP status codes
- OpenAPI specification testing in CI to prevent breaking API changes
- Generated clients for Java and Python updated automatically

### User Access Control
- **VCell Support role** added - support staff can now view models shared with VCell Support
- Improved user role management in database and API

---

## Bug Fixes

### Critical Fixes
- **Fixed VCell reconnection/token refresh failure** - clients no longer lose connection after extended periods (#1471)
- **Fixed Google login issues** - proper handling of Google account selection and logout (#1520)
- **Fixed OutputFunctions being ignored** in spatial CLI data processing (#1595)
- **Fixed Math Model export error** - type casting issue resolved (#1594)
- **Fixed field data creation from simulations** - previously broken functionality restored (#1457)

### Model & Simulation Fixes
- Fixed PubMed links in Annotations tab (#1576)
- Fixed NPE when inserting compartment (#1541)
- Fixed concurrent modification exception in simulation results display (TreeMap replaced with ConcurrentSkipListMap) (#1465)
- Fixed geometry viewer cropping bug (#1437)
- Fixed parameter estimation requiring constant re-authentication (#1498)
- Typo fix: "Constructed" → "Constructive" solid geometry (#1561)

### Infrastructure & Security
- **XML security hardening** - prevention of XXE (XML External Entity) attacks in XML parsing (#1531)
- Fixed SSH command configuration for SLURM cluster migration (#1467)
- Fixed duplicate SSH login issues (#1562)
- Database connection improvements for Oracle and PostgreSQL

---

## Infrastructure Improvements

### Server & Deployment
- Configurable SSH options for SLURM via Kubernetes config
- New HAProxy pod for SSH request proxying to SLURM submit nodes
- Improved Terraform documentation for Kubernetes deployment
- Apple code signing certificate renewal for macOS installers
- Oracle database account restructuring:
  - New `vcell_service` account for production
  - New `vcell_dev` account for developers
  - Deprecated shared `vcell` account for improved security

### Native Library Compilation (libvcell)
- **New GraalVM native compilation** of VCell Java code for use with pyvcell
- Published libvcell package to PyPI with multi-platform support
- Custom manylinux Docker image with bundled GraalVM, Maven, and Poetry
- Endpoints for generating Finite Volume solver input from VCML and SBML

### Build & CI/CD
- OpenAPI specification CI testing to prevent breaking changes
- Improved GitHub Actions workflows for native library builds
- JSBML fork with custom resource bundle handling for native compilation
- Updated JAI Core repository configuration

---

## External Integrations

### Pathway Commons
- Upgraded to BioPAX Level 3 support (#1574, #1586)
- Improved pathway import using new pathwaycommons.org URLs
- Better handling of pathway entities and reactions

### BioSimulations Compatibility
- Plots now reported as Reports for BioSimulations compatibility (#1444)
- Updated CLI data handling for improved interoperability
- Small mesh option for faster spatial simulations

### BioModels Database
- Updated list of supported/failed models for BMDB importing
- Fixed import issues with models having variable compartment sizes (#1496)

---

## Developer Notes

### API Migration Status
The migration from legacy Jersey/RPC API to Quarkus REST continues. The following resources now use the new API:
- BioModel (info, save, delete, get)
- MathModel (full CRUD)
- Geometry
- VCImage
- Field Data
- Export (with new queue system)
- User/Authentication

### Breaking Changes
- Export format now exclusively N5 (legacy formats deprecated)
- KeyValue objects now serialize as strings in API responses
- Some legacy RPC calls removed from client

### Deprecated Features
- Legacy ApacheMQ export queue (replaced by ArtemisMQ)
- FieldDataOpSpec system (replaced by simpler endpoint model)
- Direct Oracle 'vcell' account access (use vcell_dev or vcell_service)

---

## Known Issues

- SpringSaLaD is disabled by default (enable via property `enableSpringSaLaD=true`)
- Windows native library builds for pyvcell still in progress

---

## Contributors

- **Ezequiel Valencia** - REST API development, Field Data system, Export system overhaul
- **Dan Vasilescu** - SpringSaLaD/Langevin solver improvements, SLURM integration, post-processing statistics
- **Logan Drescher** - CLI improvements, BioSimulations compatibility, Smoldyn performance optimizations
- **Jim Schaff** - Infrastructure, pyvcell/libvcell, native compilation, system architecture

---

## Acknowledgments

Special thanks to the VCell user community for bug reports and feedback. For questions or issues, contact vcell_support@uchc.edu.
