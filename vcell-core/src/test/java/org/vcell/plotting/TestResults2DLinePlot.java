package org.vcell.plotting;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;

@Tag("Fast")
public class TestResults2DLinePlot {

    @Test
    public void testConstructors(){
        double[] xValuesPrim = {0.0, 2.0, 4.0};
        Double[] xValuesWrap = {0.0, 2.0, 4.0};
        Results2DLinePlot[] testInstances = {
                new Results2DLinePlot(),
                new Results2DLinePlot("Title"),
                new Results2DLinePlot("Label", xValuesWrap),
                new Results2DLinePlot("Label", xValuesPrim),
                new Results2DLinePlot("Title", "Label", xValuesWrap),
                new Results2DLinePlot("Title", "Label", xValuesPrim),
        };

        for (Results2DLinePlot instance : testInstances){
            Assertions.assertTrue("".equals(instance.getTitle()) || "Title".equals(instance.getTitle()));
            Assertions.assertTrue("".equals(instance.getXLabel()) || "Label".equals(instance.getXLabel()));
            Assertions.assertTrue(instance.getXDataValues().length == 0 || instance.getXDataValues().length == 3);
            if (instance.getXDataValues().length == 3){
                Assertions.assertArrayEquals(xValuesPrim, instance.getXDataValues());
            }
        }
    }

    @Test
    public void testSettingAndGetting(){
        double[] xValuesPrim = {0.0, 2.0, 4.0};
        Double[] xValuesWrap = {0.0, 2.0, 4.0};
        Results2DLinePlot[] testInstances = {
                new Results2DLinePlot(),
                new Results2DLinePlot("Title"),
                new Results2DLinePlot("Label", xValuesWrap),
                new Results2DLinePlot("Label", xValuesPrim),
                new Results2DLinePlot("Title", "Label", xValuesWrap),
                new Results2DLinePlot("Title", "Label", xValuesPrim),
        };
    }
}
