package org.dei.perla.lang.executor.buffer;

import org.dei.perla.core.fpc.Attribute;
import org.dei.perla.core.fpc.DataType;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 22/10/15.
 */
public class NewArrayBufferTest {

    private static final Attribute intAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static final Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static final List<Attribute> atts =
            Arrays.asList(new Attribute[] {
                    intAtt,
                    floatAtt,
                    Attribute.TIMESTAMP
            });

    @Test
    public void testArrayBufferCreation() {
        NewArrayBuffer buf = new NewArrayBuffer(atts);
        assertThat(buf, notNullValue());
        assertThat(buf.size(), equalTo(0));
        assertThat(buf.capacity(), equalTo(NewArrayBuffer.DEFAULT_CAPACITY));

        int cap = 128;
        buf = new NewArrayBuffer(atts, cap);
        assertThat(buf, notNullValue());
        assertThat(buf.size(), equalTo(0));
        assertThat(buf.capacity(), equalTo(cap));
    }

    @Test
    public void testArrayBufferInsertion() {
        NewArrayBuffer buf = new NewArrayBuffer(atts);
        assertThat(buf.capacity(), equalTo(NewArrayBuffer.DEFAULT_CAPACITY));
        assertThat(buf.size(), equalTo(0));

        Object[] sample = new Object[3];
        sample[2] = Instant.now();
        buf.add(sample);
        assertThat(buf.capacity(), equalTo(NewArrayBuffer.DEFAULT_CAPACITY));
        assertThat(buf.size(), equalTo(1));

        sample = new Object[3];
        sample[2] = Instant.now();
        buf.add(sample);
        assertThat(buf.capacity(), equalTo(NewArrayBuffer.DEFAULT_CAPACITY));
        assertThat(buf.size(), equalTo(1));
    }

}
