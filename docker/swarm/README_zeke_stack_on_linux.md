1. Connect to VPN and mount to the cifs drive

2. Get the local registry up
   1. Doesn't exist
       ``` bash 
       docker run -d -p 127.0.0.1:5000:5000 --name registry registry:2 
       ```
   2. Does exist
      ```bash 
      docker start registry
      ```

3. Build java files 
   ``` bash 
    pushd ../../
    mvn clean install dependency:copy-dependencies -DskipTests=true
    popd
    ```

4. Build docker images, update env file, and get containers running
   1. When building turn off VPN because something strange with DNS happens and can't resolve dependencies.
   2. 
       ```bash
       pushd ../build
       ./build.sh --skip-maven --skip-singularity --skip-sudo all localhost:5000/virtualcell dev_zeke
       popd
       ./localconfig_realslurm_oracle_zeke.sh TEST localhost:5000/virtualcell dev_zeke 7.5.0 1234 ./zeke_env.txt
       ```
   3. After connecting to VPN
      ```bash
      sudo mount -t cifs -o username=evalencia,workgroup=CAM //cfs05.cam.uchc.edu/vcell/ ~/Mnts/RemoteVolumes/VCell05
      ```
   4. Get containers running
      ```bash
      docker compose -f docker-compose-small.yml --env-file=zeke_env.txt up
      ```

5. Terminate session
   1. Stop all docker containers running
      ```bash
        docker stop $(docker ps -a -q)
      ```
   2. Remove mount
      ```bash
      sudo umount ~/Mnts/RemoteVolumes/VCell05
      ```