package org.vcell.cli.testsupport;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.vcell.sedml.testsupport.*;
import org.junit.jupiter.api.Tag;

@Tag("Fast")
public class TestOmexTestReport {
    @Test
    void checkTableIsMade(){
        OmexTestReport.MarkdownTable table = new OmexTestReport.MarkdownTable();
        table.setNumRowsAndColums(4,3);
        table.setRowTitle(0, "VCell Developer");
        table.setRowTitle(1, "Favorite Byte");
        table.setRowTitle(2, "Favorite Pizza Topping");
        table.setRowTitle(3, "Favorite Number");
        table.setColumnTitle(0, "Logan");
        table.setColumnTitle(1, "Jim");
        table.setColumnTitle(2, "Alex");
        table.setTableValue(0, 0, true);
        table.setTableValue(0, 1, true);
        table.setTableValue(0, 2, false);
        table.setTableValue(1, 0, (byte)136);
        table.setTableValue(1, 1, (byte)255);
        table.setTableValue(1, 2, (byte)0);
        table.setTableValue(2, 0, "Pepperoni");
        table.setTableValue(2, 1, "Feta Cheeze"); // Spelling is correct
        table.setTableValue(2, 2, "Extra Cheese");
        table.setTableValue(3, 0, 17);
        table.setTableValue(3, 1, 42);
        table.setTableValue(3, 2, 3.14159);

        String expectedResult = """
                |                        | Logan     | Jim         | Alex         |
                |:----------------------:|:---------:|:-----------:|:------------:|
                | VCell Developer        | true      | true        | false        |
                | Favorite Byte          | 0x88      | 0xFF        | 0x00         |
                | Favorite Pizza Topping | Pepperoni | Feta Cheeze | Extra Cheese |
                | Favorite Number        | 17        | 42          | 3.14159      |
                """;
        Assertions.assertEquals(expectedResult, table.getMarkdownTable());
    }
}
