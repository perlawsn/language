package org.dei.perla.lang.executor;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 23/02/15.
 */
public class ArrayBufferTest {

    private static final List<Attribute> atts;
    static {
        Attribute[] as = new Attribute[] {
                Attribute.TIMESTAMP_ATTRIBUTE,
                Attribute.create("integer", DataType.INTEGER)
        };
        atts = Arrays.asList(as);
    }

    @Test
    public void BufferCreationTest() {
        Buffer b = new ArrayBuffer(atts, 512);
        assertThat(b, notNullValue());
        assertTrue(b.attributes().containsAll(atts));
        assertTrue(atts.containsAll(b.attributes()));
        assertThat(b.length(), equalTo(0));
    }

}
