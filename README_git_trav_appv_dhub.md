#Github/Travis/Appveyor/Dockerhub/LocalDockerRepo(vcell-docker.cam.uchc.edu)

github - frm@uchc.edu, vcfrmgit, owner  
travis - same  
appveyor -same  
dockerhub - same  


commit change https://github.com/virtualcell/vcell-solvers  
tag the commit as number.number.number{other text}  (must conform to https://github.com/virtualcell/vcell-solvers/.travis.yml filter def)  
github sends notification to travis,applveyor,dockerhub  
--**travis** gets notification, clones https://github.com/virtualcell/vcell-solvers  
---travis looks for github ".travis.yml" build definition file, reads configuration, travis build 'tag' filter is defined e.g. "/^v\d+(\.\d+)+$/" (numbers and dots)  
----if github commit matches filter initiates build based on the .travis.yml file, looks under 'matrix' to define builds  
-----**linux** build, builds LOCAL vcell client side solvers for linux clients, no messaging  
-----**mac** build, builds LOCAL vcell client side solvers for macos clients, no messaging  
--**appveyor** get notification, clones https://github.com/virtualcell/vcell-solvers  
----appveyor looks for github "appveyor.yml" build definition file, reads configuration, appveyor build 'tag' filter is all tags  
----**windows** build, builds LOCAL vcell client side solvers for windows clients, no messaging  
--**dockerhub**  gets notification, clones https://github.com/virtualcell/vcell-solvers  
----dockerhub "Dockerfile", reads configuration in Dockerhub account, build 'tag' filter is all tags  
----**linux** vcell server solver as container (with messaging)  
----container vcell-solvers (created from https://github.com/virtualcell/vcell-solvers/Dockerfile) forms base image for vcell-batch  
----NOTE: to build vcell-solvers without dockerhub  
------clone https://github.com/virtualcell/vcell-solvers, cd into it  
------sudo docker build -f DockerFile --tag vcell-solver .  

commit changes https://github.com/virtualcell/vcell  
--No external sites are used to build or deploy virtualcell/vcell, all done locally (vcell-nodel, using build instructions)  