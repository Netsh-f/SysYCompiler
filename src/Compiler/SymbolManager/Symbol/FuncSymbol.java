/*
@Time    : 2023/10/9 16:27
@Author  : Elaikona
*/
package Compiler.SymbolManager.Symbol;

import Compiler.SymbolManager.Symbol.ValueTypeEnum;
import Compiler.SymbolManager.Symbol.VarSymbol;

import java.util.List;

public record FuncSymbol(ValueTypeEnum valueTypeEnum, List<VarSymbol> paramVarSymbolList) {
}
