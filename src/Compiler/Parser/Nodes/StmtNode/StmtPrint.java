/*
@Time    : 2023/9/29 20:34
@Author  : Elaikona
*/
package Compiler.Parser.Nodes.StmtNode;

import Compiler.Parser.Nodes.Exp;
import Compiler.Parser.Nodes.Stmt;

import java.util.List;

public class StmtPrint extends Stmt {
    public String formatString;
    List<Exp> expList;

    public StmtPrint(String formatString, List<Exp> expList) {
        this.formatString = formatString;
        this.expList = expList;
    }
}
