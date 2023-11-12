/*
@Time    : 2023/11/12 16:19
@Author  : Elaikona
*/
package Compiler.IntermediateCode.Code;

import Compiler.SymbolManager.Symbol.VarSymbol;

public class VarDefCode extends BaseCode {
    private VarSymbol varSymbol;

    public VarDefCode(VarSymbol varSymbol) {
        this.varSymbol = varSymbol;
    }
}
