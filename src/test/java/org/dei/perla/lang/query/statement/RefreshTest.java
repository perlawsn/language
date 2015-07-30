package org.dei.perla.lang.query.statement;

import org.dei.perla.core.descriptor.DataType;
import org.dei.perla.core.sample.Attribute;
import org.junit.Test;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;

/**
 * @author Guido Rota 27/03/15.
 */
public class RefreshTest {

    @Test
    public void neverRefresh() {
        Refresh r = Refresh.NEVER;
        assertTrue(r.isComplete());
        assertThat(r.getType(), equalTo(RefreshType.NEVER));
    }

    @Test
    public void createTimedRefresh() {
        Duration d = Duration.ofSeconds(56);
        Refresh r = new Refresh(d);
        assertTrue(r.isComplete());
        assertThat(r.getType(), equalTo(RefreshType.TIME));
        assertThat(r.getDuration(), equalTo(d));
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
        List<Attribute> bound = r.getEvents();
        assertThat(bound.size(), equalTo(0));
        r = r.bind(atts);
        assertTrue(r.isComplete());
        bound = r.getEvents();
        assertThat(bound.size(), equalTo(2));
        assertTrue(bound.contains(lb));
        assertTrue(bound.contains(al));
    }

}
