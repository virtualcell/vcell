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
   3. Connect back to VPN and get containers running
      ```bash
      docker compose -f docker-compose-small.yml --env-file=zeke_env.txt up
      ```