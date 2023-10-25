/*
@Time    : 2023/9/29 20:34
@Author  : Elaikona
*/
package Compiler.Parser.Nodes.StmtNode;

import Compiler.Lexer.Token;
import Compiler.Parser.Nodes.Exp;
import Compiler.Parser.Nodes.FormatString;
import Compiler.Parser.Nodes.Stmt;

import java.util.List;

public class StmtPrint extends Stmt {
    public Token ptintfToken;
    public FormatString formatString;
    public List<Exp> expList;

    public StmtPrint(Token ptintfToken, FormatString formatString, List<Exp> expList) {
        this.ptintfToken = ptintfToken;
        this.formatString = formatString;
        this.expList = expList;
    }
}
