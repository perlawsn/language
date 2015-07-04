package org.dei.perla.lang.persistence;

import org.dei.perla.core.descriptor.DataType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 04/07/15.
 */
public class StreamDefinitionTest {

    @Test
    public void fieldDefinitionTest() {
        FieldDefinition f = new FieldDefinition("int", DataType.INTEGER, 12);
        assertThat(f.getName(), equalTo("int"));
        assertThat(f.getType(), equalTo(DataType.INTEGER));
        assertThat(f.getDefaultValue(), equalTo(12));

        f = new FieldDefinition("float", DataType.FLOAT);
        assertThat(f.getName(), equalTo("float"));
        assertThat(f.getType(), equalTo(DataType.FLOAT));
        assertThat(f.getDefaultValue(), nullValue());
    }

    @Test
    public void streamDefinitionTest() {
        List<FieldDefinition> fields = new ArrayList<>();
        fields.add(new FieldDefinition("first", DataType.INTEGER));
        fields.add(new FieldDefinition("second", DataType.STRING));
        fields.add(new FieldDefinition("third", DataType.BOOLEAN));

        StreamDefinition stream = new StreamDefinition("stream", fields);
        assertThat(stream.getId(), equalTo("stream"));
        assertTrue(stream.getFields().containsAll(fields));
        assertThat(stream.getFields().size(), equalTo(fields.size()));
    }

}
