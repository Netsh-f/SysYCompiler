/*
@Time    : 2023/9/29 20:34
@Author  : Elaikona
*/
package Compiler.Parser.Nodes.StmtNode;

import Compiler.Lexer.Token;
import Compiler.Parser.Nodes.Exp;
import Compiler.Parser.Nodes.Stmt;

public class StmtReturn extends Stmt {
    public Token returnToken;
    public Exp exp;

    public StmtReturn(Token returnToken, Exp exp) {
        this.returnToken = returnToken;
        this.exp = exp;
    }
}
