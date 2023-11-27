package cbit.vcell.export.server.datacontainer;

import java.io.IOException;
import java.nio.file.Files;

public class DataContainerConverter {

    public static InMemoryDataContainer convert(FileStyleDataContainer container) throws IOException {
        InMemoryDataContainer convertedContainer = new InMemoryDataContainer();
        convertedContainer.append(Files.readAllBytes(container.getDataFile().toPath()));
        return convertedContainer;
    }

    public static FileDataContainer convert(InMemoryDataContainer container) throws IOException {
        FileDataContainer convertedContainer = new FileDataContainer();
        convertedContainer.append(container);
        return convertedContainer;
    }
}
