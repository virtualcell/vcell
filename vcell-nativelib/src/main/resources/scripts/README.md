1. Create native executable of module
```bash
pushd ../../../..
# Create Clean Instance
mvn clean install
# Record runtime dependent operations for future replay
# https://www.graalvm.org/jdk21/reference-manual/native-image/dynamic-features/Resources/
# https://www.graalvm.org/latest/reference-manual/native-image/metadata/AutomaticMetadataCollection/
java -agentlib:native-image-agent=config-output-dir=target/recording -jar target/vcell-nativelib-0.0.1-SNAPSHOT-jar-with-dependencies.jar
# Build the native executable
mvn package -P native -DskipTests=true
popd
```

2. Run native executable
```bash
pushd ../../../..
./target/sbml_to*
popd
```