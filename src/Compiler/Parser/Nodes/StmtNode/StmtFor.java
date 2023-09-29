/*
@Time    : 2023/9/29 20:33
@Author  : Elaikona
*/
package Compiler.Parser.Nodes.StmtNode;

import Compiler.Parser.Nodes.Cond;
import Compiler.Parser.Nodes.ForStmt;
import Compiler.Parser.Nodes.Stmt;

public class StmtFor extends Stmt {
    public ForStmt forStmt1;
    public Cond cond;
    public ForStmt forStmt3;
    public Stmt stmt;

    public StmtFor(ForStmt forStmt1, Cond cond, ForStmt forStmt3, Stmt stmt) {
        this.forStmt1 = forStmt1;
        this.cond = cond;
        this.forStmt3 = forStmt3;
        this.stmt = stmt;
    }
}
