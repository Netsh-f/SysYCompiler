/*
@Time    : 2023/10/9 16:27
@Author  : Elaikona
*/
package Compiler.SymbolManager.Symbol;

import Compiler.LLVMIR.Global.Function;

import java.util.List;

public class FuncSymbol {
    public ValueTypeEnum valueTypeEnum;
    public List<VarSymbol> paramVarSymbolList;
    public Function function;

    public FuncSymbol(ValueTypeEnum valueTypeEnum, List<VarSymbol> paramVarSymbolList) {
        this.valueTypeEnum = valueTypeEnum;
        this.paramVarSymbolList = paramVarSymbolList;
    }
}
