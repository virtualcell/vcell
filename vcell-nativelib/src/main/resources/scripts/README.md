1. Create native executable of module
   1. Create clean instance
   2. Record runtime dependent operations for future replay
   3. Build the native executable 
```bash
pushd ../../../..
mvn clean install
java -Dheadless=true -agentlib:native-image-agent=config-output-dir=target/recording \
-jar target/vcell-nativelib-0.0.1-SNAPSHOT.jar \
"/Users/evalencia/Documents/VCell_Repositories/vcell/vcell-rest/src/test/resources/TinySpacialProject_Application0.xml" \
"/Users/evalencia/Documents/VCell_Repositories/vcell/vcell-nativelib/target/sbml-input"

mvn package -P native -DskipTests=true
popd
```

Sources: 
- https://www.graalvm.org/jdk21/reference-manual/native-image/dynamic-features/Resources/
- https://www.graalvm.org/latest/reference-manual/native-image/metadata/AutomaticMetadataCollection/

2. Run native executable
```bash
pushd ../../../..
./target/sbml_to* -Dheadless=true \
"/Users/evalencia/Documents/VCell_Repositories/vcell/vcell-rest/src/test/resources/TinySpacialProject_Application0.xml" \
"/Users/evalencia/Documents/VCell_Repositories/vcell/vcell-nativelib/target/sbml-input" 
popd
```