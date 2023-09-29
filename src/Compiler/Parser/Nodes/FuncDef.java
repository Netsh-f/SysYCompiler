/*
@Time    : 2023/9/29 20:06
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

public record FuncDef(FuncType funcType, Ident ident, FuncFParams funcFParams, Block block) {
}
