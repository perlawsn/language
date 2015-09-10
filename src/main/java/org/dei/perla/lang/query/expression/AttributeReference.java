package org.dei.perla.lang.query.expression;

import org.dei.perla.core.fpc.DataType;
import org.dei.perla.lang.executor.buffer.BufferView;

/**
 * An {@link Expression} for accessing the value of a specific Fpc
 * attribute sample.
 *
 * @author Guido Rota 23/02/15.
 */
public final class AttributeReference extends Expression {

    private final String id;
    private final DataType type;
    private final int idx;

    /**
     * Creates a new Field for accessing the value of a data attribute
     * generated by an Fpc.
     *
     * @param id attribute identifier
     * @param type attribute type
     * @param idx index of the attribute in the sample array
     */
    public AttributeReference(String id, DataType type, int idx) {
        this.id = id;
        this.type = type;
        this.idx = idx;
    }

    public String getId() {
        return id;
    }

    public int getIndex() {
        return idx;
    }

    @Override
    public DataType getType() {
        return type;
    }

    @Override
    public Object run(Object[] sample, BufferView buffer) {
        Object o = sample[idx];
        if (o == null || type != DataType.BOOLEAN) {
            return o;
        } else if ((boolean) o == true) {
            return LogicValue.TRUE;
        } else {
            return LogicValue.FALSE;
        }
    }

    @Override
    protected void buildString(StringBuilder bld) {
        bld.append(id);
    }

}
