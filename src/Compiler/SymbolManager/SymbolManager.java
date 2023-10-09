/*
@Time    : 2023/10/9 15:19
@Author  : Elaikona
*/
package Compiler.SymbolManager;

import Compiler.SymbolManager.Symbol.FuncSymbol;
import Compiler.SymbolManager.Symbol.VarSymbol;

import java.util.HashMap;

public class SymbolManager {
    private SymbolTable curSymbolTable;

    public SymbolManager() {
        curSymbolTable = new SymbolTable(null, new HashMap<String, VarSymbol>(), new HashMap<String, FuncSymbol>());
    }

    public void createSymbolTable() {
        curSymbolTable = new SymbolTable(curSymbolTable, new HashMap<String, VarSymbol>(), new HashMap<String, FuncSymbol>());
    }

    public void addVarSymbol(String ident, VarSymbol varSymbol) {
        curSymbolTable.varSymbolMap().put(ident, varSymbol);
    }
}
