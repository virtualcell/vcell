package cbit.vcell.export.server.datacontainer;

import java.io.IOException;

public class DataContainerConverter {

    public static InMemoryDataContainer convert(FileDataContainer container) throws IOException {
        InMemoryDataContainer convertedContainer = new InMemoryDataContainer();
        convertedContainer.append(container);
        return convertedContainer;
    }

    public static FileDataContainer convert(InMemoryDataContainer container) throws IOException {
        FileDataContainer convertedContainer = new FileDataContainer();
        convertedContainer.append(container);
        return convertedContainer;
    }
}
