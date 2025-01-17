package org.vcell.cli.run;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Tag("Fast")
public class RunUtilsTest {
    private final String VERTICAL_CSV = """
            Street-based,Sea-based,Snow-based
            Skateboard,Wake Board,Snowboard
            Rollerblades,Water Skis,Alpine / Cross-country Skis
            Bike,Water Bike,<None>
            Motorcycle,Boatercycle (Jet-Ski),Snow Bike / Snowmobile""";
    private final String HORIZONTAL_CSV = """
            Street-based,Skateboard,Rollerblades,Bike,Motorcycle
            Sea-based,Wake Board,Water Skis,Water Bike,Boatercycle (Jet-Ski)
            Snow-based,Snowboard,Alpine / Cross-country Skis,<None>,Snow Bike / Snowmobile""";

    @Test
    public void testCSVFormatting(){
        Map<String, List<String>> csvContents = new LinkedHashMap<>();
        csvContents.put("Street-based", List.of("Skateboard", "Rollerblades", "Bike", "Motorcycle"));
        csvContents.put("Sea-based", List.of("Wake Board", "Water Skis", "Water Bike", "Boatercycle (Jet-Ski)"));
        csvContents.put("Snow-based", List.of("Snowboard", "Alpine / Cross-country Skis", "<None>", "Snow Bike / Snowmobile"));
        Assertions.assertEquals(VERTICAL_CSV, RunUtils.formatCSVContents(csvContents, true));
        Assertions.assertEquals(HORIZONTAL_CSV, RunUtils.formatCSVContents(csvContents, false));
    }
}
