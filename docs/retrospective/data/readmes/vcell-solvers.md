<p align="center" width="100%">
 <a href="https://vcell.org">
    <img width="10%" src="https://github.com/biosimulations/biosimulations/blob/dev/docs/src/assets/images/about/partners/vcell.svg">
 </a>
</p>

---
![CI](https://github.com/virtualcell/vcell-solvers/actions/workflows/cd.yml/badge.svg)

# vcell-solvers
Virtual Cell solvers [virtualcell/vcell-solvers](https://github.com/virtualcell/vcell-solvers) is a collection of numerical simulation codes used in the Virtual Cell framework [virtualcell/vcell](https://github.com/virtualcell/vcell)).

 
## The Virtual Cell Project
The Virtual Cell is a modeling and simulation framework for computational biology.  For details see http://vcell.org and http://github.com/virtualcell.

## Building VCell Solvers
### Using VirtualBox and Vagrant
* git clone https://github.com/virtualcell/vcell.git
* cd vcell/VagrantBoxes
### Building for Linux (64 bit)
[details](VagrantBoxes/linux64/README.md)

```bash
$ cd linux64
$ vagrant up
$ vagrant ssh -c /vagrant_numerics/build.sh
$ vagrant halt
```
### Building for Linux (32 bit)
[details](VagrantBoxes/linux32/README.md)

```bash
$ cd linux32
$ vagrant up
$ vagrant ssh -c /vagrant_numerics/build.sh
$ vagrant halt
```
### Building for Macos on a Mac **Apple only allows a Mac VM to run on Apple hardware**
[details}(VagrantBoxes/mac64/README.md)

```bash
$ cd mac64
$ vagrant up
$ vagrant ssh -c /vagrant/build.sh
$ vagrant halt
```
### Building for Windows (64 bit) on Windows
[details: including building on other platforms](VagrantBoxes/win64/README.md)

```bash
$ cd win64
$ vagrant up
$ vagrant powershell -c \vagrant\build.sh
$ vagrant halt
```
