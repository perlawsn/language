package org.dei.perla.lang.executor;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.dei.perla.core.utils.Errors;
import org.dei.perla.lang.executor.statement.SelectionQuery;
import org.dei.perla.lang.parser.Parser;
import org.junit.Test;

import java.io.StringReader;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 24/04/15.
 */
public class SelectExecutorTest {

    private static final Attribute temp =
            Attribute.create("temperature", DataType.INTEGER);
    private static final Attribute hum =
            Attribute.create("humidity", DataType.INTEGER);

    private static final Map<Attribute, Object> values;
    static {
        Map<Attribute, Object> m = new HashMap<>();
        m.put(temp, 0);
        m.put(hum, 0);
        values = Collections.unmodifiableMap(m);
    }

    private static final List<Attribute> atts = Arrays.asList(new Attribute[] {
            temp,
            hum
    });

    @Test
    public void testSimpleExecution() throws Exception {
        Errors err = new Errors();

        Parser p = new Parser(new StringReader(
                "every 1 samples " +
                        "select temperature, humidity " +
                        "sampling every 300 milliseconds "
        ));

        SelectionQuery s = p.SelectionStatement(err);
        assertTrue(err.isEmpty());
        s = s.bind(atts);
        assertTrue(s.isComplete());
    }

}
