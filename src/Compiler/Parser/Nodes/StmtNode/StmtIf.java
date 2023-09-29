/*
@Time    : 2023/9/29 20:34
@Author  : Elaikona
*/
package Compiler.Parser.Nodes.StmtNode;

import Compiler.Parser.Nodes.Cond;
import Compiler.Parser.Nodes.Stmt;

public class StmtIf extends Stmt {
    public Cond cond;
    public Stmt stmt;
    public Stmt elseStmt;

    public StmtIf(Cond cond, Stmt stmt, Stmt elseStmt) {
        this.cond = cond;
        this.stmt = stmt;
        this.elseStmt = elseStmt;
    }
}
