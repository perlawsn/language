package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;

/**
 * A generic node of the Abstract Syntax Tree. All concrete AST nodes inherit
 * from this class.
 *
 * @author Guido Rota 30/07/15.
 */
public abstract class NodeAST {

    private final int column;
    private final int line;

    /**
     * Empty constructor, employed in test cases and private internal
     * constructor (see {@link ConstantAST} for a usage example).
     */
    protected NodeAST() {
        column = -1;
        line = -1;
    }

    /**
     * Creates a new {@code NodeAST} node using the information contained in
     * the lexer token passed as a parameter.
     *
     * @param token Lexer token
     */
    public NodeAST(Token token) {
        this.column = token.beginColumn;
        this.line = token.beginLine;
    }

}
