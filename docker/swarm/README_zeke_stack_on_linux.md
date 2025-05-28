
1. Build java files 
   ``` bash 
    pushd ../../
    mvn clean install dependency:copy-dependencies -DskipTests=true
    popd
    ```

2. Build docker images, update env file, and get containers running
   1. When building turn off VPN because something strange with DNS happens and can't resolve dependencies.
   2. 
       ```bash
       pushd ../build
       ./build.sh --skip-maven --skip-sudo all localhost:5000/virtualcell dev_zeke
       popd
      ```
   3. ```bash
       pushd ../build
       ./build.sh --skip-maven --skip-sudo rest localhost:5000/virtualcell dev_zeke
       popd
      ```

3. Run Quarkus in Terminal
   ``` bash 
   pushd ../../vcell-rest
   mvn compile quarkus:dev \
   -Dvcell.server.id=TEST2 \
   -Dvcell.softwareVersion=7.5.100 \
   -Dvcellapi.publicKey.file=/Users/evalencia/Documents/VCellDummyFiles/apiKeys.pem \
   -Dvcellapi.privateKey.file=/Users/evalencia/Documents/VCellDummyFiles/apiKeys \
   -Dvcell.mongodb.database=test \
   -Dvcell.mongodb.host.internal=localhost \
   -Dvcell.mongodb.port.internal=27020 \
   -Dvcell.primarySimdatadir.internal=/Users/evalencia/Documents/TempStorage
   popd
   ```

---
Build and Generate Clients

``` bash 
    pushd ../../
    mvn clean install dependency:copy-dependencies -DskipTests=true
    cp ./vcell-rest/target/generated/openapi.yaml ./tools/openapi.yaml
    ./tools/generate.sh
    popd
 ```
