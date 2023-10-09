/*
@Time    : 2023/10/9 16:13
@Author  : Elaikona
*/
package Compiler.SymbolManager;

import Compiler.SymbolManager.Symbol.FuncSymbol;
import Compiler.SymbolManager.Symbol.VarSymbol;

import java.util.Map;

public record SymbolTable(SymbolTable parent, Map<String, VarSymbol> varSymbolMap,
                          Map<String, FuncSymbol> funcSymbolMap) {
}
