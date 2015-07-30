package org.dei.perla.lang.parser.ast;

import org.dei.perla.lang.parser.Token;
import org.dei.perla.lang.query.statement.WindowSize;

/**
 * @author Guido Rota 30/07/15.
 */
public final class TerminateAfterAST extends NodeAST {

    private final WindowSize window;

    public TerminateAfterAST(Token token, WindowSize window) {
        super(token);
        this.window = window;
    }

    public WindowSize getWindowSize() {
        return window;
    }

}
