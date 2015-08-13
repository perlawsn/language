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
     * Creates a new {@code NodeAST} node using the information contained in
     * the lexer token passed as a parameter. For test purposes, the token
     * reference may be null.
     *
     * @param token Lexer token.
     */
    public NodeAST(Token token) {
        if (token != null) {
            this.column = token.beginColumn;
            this.line = token.beginLine;
        } else {
            this.column = -1;
            this.line = -1;
        }
    }

    /**
     * Returns a textual representation of the position of this AST node
     * inside the original source code.
     *
     * @return textual representation of the node position inside the source
     * code
     */
    public String getPosition() {
        return "column " + column + ", line " + line;
    }

}
