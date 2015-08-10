package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.statement.WindowSize;

/**
 * Common base class for window size Abstract Syntax Tree node
 *
 * @author Guido Rota 10/08/15.
 */
public abstract class WindowSizeAST extends NodeAST {

    public WindowSizeAST() {
        super();
    }

    public WindowSizeAST(Token t) {
        super(t);
    }

    public abstract WindowSize compile(ParserContext ctx);

}
