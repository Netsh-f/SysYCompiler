/*
@Time    : 2023/9/29 20:32
@Author  : Elaikona
*/
package Compiler.Parser.Nodes.StmtNode;

import Compiler.Lexer.Token;
import Compiler.Parser.Nodes.Stmt;

public class StmtBreak extends Stmt {
    public Token token;

    public StmtBreak(Token token) {
        this.token = token;
    }
}
