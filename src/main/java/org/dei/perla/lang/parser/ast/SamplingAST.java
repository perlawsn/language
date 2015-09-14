package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.statement.Sampling;

/**
 * General sampling clause Abstract Syntax Tree node
 *
 * @author Guido Rota 30/07/15.
 */
public abstract class SamplingAST extends NodeAST {

    public SamplingAST(Token token) {
        super(token);
    }

    public abstract Sampling compile(ParserContext ctx);

}
