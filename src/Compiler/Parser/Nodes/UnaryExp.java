/*
@Time    : 2023/9/29 20:16
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

public record UnaryExp(PrimaryExp primaryExp, Ident ident, FuncRParams funcRParams, UnaryOp unaryOp,
                       UnaryExp unaryExp) {
}
