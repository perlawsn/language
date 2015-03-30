package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.record.Attribute;
import org.junit.Test;

import javax.print.DocFlavor.STRING;
import java.time.Duration;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Guido Rota 27/03/15.
 */
public class RefreshTest {

    @Test
    public void createTimedRefresh() {
        Duration d = Duration.ofSeconds(56);
        Refresh r = new Refresh(d);
        assertTrue(r.isComplete());
        assertFalse(r.hasErrors());
        assertThat(r.getDuration(), equalTo(d));
        assertThat(r.getEvents().size(), equalTo(0));
    }

    @Test
    public void createEventRefresh() {
        Attribute lb = Attribute.create("low_battery", DataType.INTEGER);
        Attribute al = Attribute.create("alert", DataType.BOOLEAN);

        List<Attribute> atts = Arrays.asList(new Attribute[]{
                lb,
                al,
                Attribute.TIMESTAMP
        });
        Set<String> names = new TreeSet<>();
        names.add("low_battery");
        names.add("alert");

        Refresh r = new Refresh(names);
        assertFalse(r.isComplete());
        assertFalse(r.hasErrors());
        List<Attribute> bound = r.getEvents();
        assertThat(bound.size(), equalTo(0));
        r = r.bind(atts);
        assertTrue(r.isComplete());
        assertFalse(r.hasErrors());
        bound = r.getEvents();
        assertThat(bound.size(), equalTo(2));
        assertTrue(bound.contains(lb));
        assertTrue(bound.contains(al));
    }

}
