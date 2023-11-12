/*
@Time    : 2023/9/29 20:15
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

public class PrimaryExp extends BaseNode {
    public Exp exp;
    public LVal lVal;
    public Number number;

    public PrimaryExp(Exp exp, LVal lVal, Number number) {
        this.exp = exp;
        this.lVal = lVal;
        this.number = number;
    }
}
