/*
@Time    : 2023/10/9 16:27
@Author  : Elaikona
*/
package Compiler.SymbolManager.Symbol;

import java.util.List;

public record FuncSymbol(FuncReturnType returnType, List<VarSymbol> paramList) {
}
