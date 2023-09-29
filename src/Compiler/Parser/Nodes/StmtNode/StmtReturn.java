/*
@Time    : 2023/9/29 20:34
@Author  : Elaikona
*/
package Compiler.Parser.Nodes.StmtNode;

import Compiler.Parser.Nodes.Exp;
import Compiler.Parser.Nodes.Stmt;

public class StmtReturn extends Stmt {
    public Exp exp;

    public StmtReturn(Exp exp) {
        this.exp = exp;
    }
}
