package org.dei.perla.lang.executor.query;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.dei.perla.core.record.Record;
import org.dei.perla.lang.executor.ArrayBuffer;
import org.dei.perla.lang.executor.Buffer;
import org.dei.perla.lang.executor.BufferView;
import org.dei.perla.lang.executor.expression.Expression;
import org.dei.perla.lang.executor.expression.Field;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 03/03/15.
 */
public class DataManagerTest {

    private static Attribute integerAtt =
            Attribute.create("integer", DataType.INTEGER);
    private static Attribute stringAtt =
            Attribute.create("string", DataType.STRING);
    private static Attribute floatAtt =
            Attribute.create("float", DataType.FLOAT);

    private static BufferView view;

    private static Expression tsExpr = new Field(0, DataType.TIMESTAMP);
    private static Expression intExpr = new Field(1, DataType.INTEGER);
    private static Expression stringExpr = new Field(2, DataType.STRING);
    private static Expression floatExpr = new Field(3, DataType.FLOAT);

    @BeforeClass
    public static void setupBuffer() {
        Attribute[] as = new Attribute[] {
                Attribute.TIMESTAMP,
                integerAtt,
                stringAtt,
                floatAtt
        };
        List<Attribute> atts = Arrays.asList(as);

        Buffer b = new ArrayBuffer(atts, 512);
        b.add(new Record(atts, new Object[]{Instant.now(), 0, "0", 0.0f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 1, "1", 1.1f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 2, "2", 2.2f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 3, "3", 3.3f}));
        b.add(new Record(atts, new Object[]{Instant.now(), 4, "4", 4.4f}));

        view = b.unmodifiableView();
    }

    @Test
    public void plainSelect() throws InterruptedException {
        List<Expression> sel = new ArrayList<>();
        sel.add(tsExpr);
        sel.add(intExpr);
        sel.add(stringExpr);
        sel.add(floatExpr);

        DataManager dm = new DataManager(sel, false, 1, null, null, null, null);
        SynchronizerQueryHandler qh = new SynchronizerQueryHandler(1);
        dm.run(view, qh);
        List<Object[]> records = qh.getRecords();
        assertThat(records.size(), equalTo(1));
        Object[] r = records.get(0);
        assertTrue(r[0] instanceof Instant);
        assertThat(r[0], equalTo(view.get(0)[0]));
        assertTrue(r[1] instanceof Integer);
        assertThat(r[1], equalTo(view.get(0)[1]));
        assertTrue(r[2] instanceof String);
        assertThat(r[2], equalTo(view.get(0)[2]));
        assertTrue(r[3] instanceof Float);
        assertThat(r[3], equalTo(view.get(0)[3]));
    }

}
