package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Guido Rota 22/09/15.
 */
public class SampleReorderTest {

    private static final Attribute temp =
            Attribute.create("temperature", DataType.INTEGER);
    private static final Attribute press =
            Attribute.create("pressure", DataType.FLOAT);
    private static final Attribute co2 =
            Attribute.create("co2", DataType.FLOAT);
    private static final Attribute hum =
            Attribute.create("humidity", DataType.INTEGER);

    @Test
    public void testReorder() {
        List<Attribute> in = Arrays.asList(new Attribute[] {
                press,
                co2,
                temp,
                hum
        });
        List<Attribute> out = Arrays.asList(new Attribute[] {
                temp,
                press,
                co2,
                hum
        });

        SampleReorder sr = new SampleReorder(in, out);
        Object[] sample = new Object[] {
                1, 2, 0, 3
        };
        Object[] sampleOut = sr.reorder(sample);
        // Check if the reference to the output array is the same as the input
        assertTrue(sampleOut == sample);
        for (int i = 0; i < sample.length; i++) {
            assertThat(sample[i], equalTo(i));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReorderMismatch1() {
        List<Attribute> in = Arrays.asList(new Attribute[] {
                press,
                co2,
                temp,
                hum
        });
        List<Attribute> out = Arrays.asList(new Attribute[] {
                temp,
                press,
                co2,
        });

        SampleReorder sr = new SampleReorder(in, out);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReorderMismatch2() {
        List<Attribute> in = Arrays.asList(new Attribute[] {
                co2,
                temp,
                hum
        });
        List<Attribute> out = Arrays.asList(new Attribute[] {
                temp,
                press,
                co2,
                hum
        });

        SampleReorder sr = new SampleReorder(in, out);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReorderMismatch3() {
        List<Attribute> in = Collections.emptyList();
        List<Attribute> out = Collections.emptyList();
        SampleReorder sr = new SampleReorder(in, out);
    }

    @Test(expected = RuntimeException.class)
    public void testMismatch2() {
        List<Attribute> in = Arrays.asList(new Attribute[] {
                press,
                co2,
                temp,
                hum
        });
        List<Attribute> out = Arrays.asList(new Attribute[] {
                temp,
                press,
                co2,
                hum
        });

        SampleReorder sr = new SampleReorder(in, out);
        Object[] sample = new Object[] {
                1, 2, 0
        };
        Object[] sampleOut = sr.reorder(sample);
    }

}
