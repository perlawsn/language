package org.dei.perla.lang.persistence.memory;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.persistence.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 07/07/15.
 */
public class MemoryStreamTest {

    private final FieldDefinition field1 =
            new FieldDefinition("field1", DataType.INTEGER);
    private final FieldDefinition field2 =
            new FieldDefinition("field2", DataType.FLOAT);
    private final FieldDefinition field3 =
            new FieldDefinition("field3", DataType.BOOLEAN);

    @Test
    public void testMemoryStreamDriver() throws Exception {
        List<FieldDefinition> fields = new ArrayList<>();
        fields.add(field1);
        fields.add(field2);
        fields.add(field3);
        StreamDefinition def = new StreamDefinition("test", fields);

        StreamDriver sd = new MemoryStreamDriver();
        Stream s = sd.create(def);
        assertThat(s, notNullValue());
        assertThat(s.getId(), equalTo("test"));
        assertTrue(s.getFields().containsAll(fields));
        assertThat(s.getFields().size(), equalTo(fields.size()));

        s = sd.open("test");
        assertThat(s, notNullValue());
        assertThat(s.getId(), equalTo("test"));
        assertTrue(s.getFields().containsAll(fields));
        assertThat(s.getFields().size(), equalTo(fields.size()));
    }

    @Test(expected = StreamException.class)
    public void testUnexistingStream() throws Exception {
        StreamDriver sd = new MemoryStreamDriver();
        sd.open("not_created");
    }

}
