/*
@Time    : 2023/9/29 20:33
@Author  : Elaikona
*/
package Compiler.Parser.Nodes.StmtNode;

import Compiler.Lexer.Token;
import Compiler.Parser.Nodes.Stmt;

public class StmtContinue extends Stmt {
    public Token token;

    public StmtContinue(Token token) {
        this.token = token;
    }
}
