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

    private Object[] newSample() {
        return newSample(atts);
    }

    private Object[] newSample(List<Attribute> atts) {
        Object[] sample = new Object[atts.size()];
        int tsIdx = atts.indexOf(Attribute.TIMESTAMP);
        if (tsIdx == -1) {
            throw new RuntimeException("Missing timestamp attribute");
        }
        sample[tsIdx] = Instant.now();
        return sample;
    }

    @Test
    public void testCreation() {
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
    public void testInsertion() {
        NewArrayBuffer buf = new NewArrayBuffer(atts);
        assertThat(buf.capacity(), equalTo(NewArrayBuffer.DEFAULT_CAPACITY));
        assertThat(buf.size(), equalTo(0));

        buf.add(newSample());
        assertThat(buf.capacity(), equalTo(NewArrayBuffer.DEFAULT_CAPACITY));
        assertThat(buf.size(), equalTo(1));

        buf.add(newSample());
        assertThat(buf.capacity(), equalTo(NewArrayBuffer.DEFAULT_CAPACITY));
        assertThat(buf.size(), equalTo(2));

        for (int i = 0; i < 10; i++) {
            buf.add(newSample());
        }
        assertThat(buf.capacity(), equalTo(NewArrayBuffer.DEFAULT_CAPACITY));
        assertThat(buf.size(), equalTo(12));
    }

    @Test
    public void testExpand() {
        int cap = 10;
        NewArrayBuffer buf = new NewArrayBuffer(atts, cap);
        assertThat(buf.capacity(), equalTo(cap));
        for (int i = 0; i < cap; i++) {
            buf.add(newSample());
        }
        assertThat(buf.capacity(), equalTo(cap));

        buf.add(newSample());
        assertThat(buf.capacity(), equalTo(2 * cap));
    }

}
