/*
@Time    : 2023/9/29 20:14
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import java.util.List;

public class LVal extends BaseNode {
    public Ident ident;
    public List<Exp> expList;

    public LVal(Ident ident, List<Exp> expList) {
        this.ident = ident;
        this.expList = expList;
    }
}
