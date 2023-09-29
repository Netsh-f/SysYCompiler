/*
@Time    : 2023/9/29 20:34
@Author  : Elaikona
*/
package Compiler.Parser.Nodes.StmtNode;

import Compiler.Parser.Nodes.Exp;
import Compiler.Parser.Nodes.LVal;
import Compiler.Parser.Nodes.Stmt;
import Enums.StmtLValExpType;


public class StmtLValExp extends Stmt {
    public StmtLValExpType type;
    public LVal lVal;
    public Exp exp;

    public StmtLValExp(StmtLValExpType type, LVal lVal, Exp exp) {
        this.type = type;
        this.lVal = lVal;
        this.exp = exp;
    }
}
