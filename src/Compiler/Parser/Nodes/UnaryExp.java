/*
@Time    : 2023/9/29 20:16
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

public class UnaryExp extends BaseNode {
    public PrimaryExp primaryExp;
    public Ident ident;
    public FuncRParams funcRParams;
    public UnaryOp unaryOp;
    public UnaryExp unaryExp;

    public UnaryExp(PrimaryExp primaryExp, Ident ident, FuncRParams funcRParams, UnaryOp unaryOp, UnaryExp unaryExp) {
        this.primaryExp = primaryExp;
        this.ident = ident;
        this.funcRParams = funcRParams;
        this.unaryOp = unaryOp;
        this.unaryExp = unaryExp;
    }
}
