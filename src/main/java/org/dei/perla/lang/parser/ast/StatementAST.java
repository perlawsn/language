package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.ParserContext;
import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.statement.Statement;

/**
 * Statement Abstract Syntax Tree node.
 *
 * @author Guido Rota 30/07/15.
 */
public abstract class StatementAST extends NodeAST {

    public StatementAST(Token token) {
        super(token);
    }

    /**
     * Compiles the AST into a concrete and executable statement
     *
     * @param ctx Context object employed to keep track of intermediate
     *            parsing results
     * @return executable statement
     */
    public abstract Statement compile(ParserContext ctx);

}
