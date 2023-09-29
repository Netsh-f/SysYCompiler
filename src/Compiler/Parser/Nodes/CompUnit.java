/*
@Time    : 2023/9/29 20:05
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import java.util.List;

public record CompUnit(List<Decl> declList, List<FuncDef> funcDefList, MainFuncDef mainFuncDef) {
}
