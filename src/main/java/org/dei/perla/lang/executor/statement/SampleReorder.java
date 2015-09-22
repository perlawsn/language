package org.dei.perla.lang.executor.statement;

import org.dei.perla.core.fpc.Attribute;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for reordering the attributes of a sample. The {@code
 * SampleReorder} is employed by the {@link Sampler} to adapt the data
 * received by the {@link Fpc}s to the common record structure of the {@link
 * Buffer}
 *
 * @author Guido Rota 22/09/15.
 */
public class SampleReorder {

    private final int[] order;

    public SampleReorder(List<Attribute> in, List<Attribute> out) {
        if (in.size() != out.size()) {
            throw new IllegalArgumentException("Input and output order list " +
                    "must be the same size");
        }
        if (out.size() == 0) {
            throw new IllegalArgumentException("Attribute lists cannot be " +
                    "empty");
        }

        //TODO: Check for duplicates?

        List<Attribute> inCopy = new ArrayList<>(in);

        order = new int[in.size()];
        for (int i = 0; i < out.size(); i++) {
            Attribute a = out.get(i);
            int pos = inCopy.indexOf(a);
            if (pos == -1) {
                throw new IllegalArgumentException("Attribute '" + a + "' not" +
                        " found in input attribute list");
            }
            order[i] = pos;
            swap(inCopy, i, pos);
        }
    }

    private void swap(List<Attribute> l, int i, int j) {
        if (i == j) {
            return;
        }

        Attribute tmp = l.get(i);
        l.set(i, l.get(j));
        l.set(j, tmp);
    }

    /**
     * Reorders the array passed as parameter. This function modifies the
     * contents of the input array.
     *
     * @param sample sample array to reorder
     * @return the reordered array
     */
    public Object[] reorder(Object[] sample) {
        if (sample.length != order.length) {
            throw new RuntimeException("Wrong sample length");
        }

        for (int i = 0; i < order.length; i++) {
            int j = order[i];
            if (i == j) {
                continue;
            }

            Object tmp = sample[i];
            sample[i] = sample[j];
            sample[j] = tmp;
        }

        return sample;
    }

}
