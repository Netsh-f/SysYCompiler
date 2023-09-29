/*
@Time    : 2023/9/29 20:16
@Author  : Elaikona
*/
package Compiler.Parser.Nodes;

import java.util.List;

public record VarDef(Ident ident, List<ConstExp> constExpList, InitVal initVal) {
}
