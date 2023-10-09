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
        curSymbolTable = new SymbolTable(null, new HashMap<>(), new HashMap<>());
    }

    public void createSymbolTable() {
        curSymbolTable = new SymbolTable(curSymbolTable, new HashMap<>(), new HashMap<>());
    }

    public void addVarSymbol(String ident, VarSymbol varSymbol) {
        curSymbolTable.varSymbolMap().put(ident, varSymbol);
    }

    public void backward() {
        if (curSymbolTable.parent() != null) {
            curSymbolTable = curSymbolTable.parent();
        }
    }

    public VarSymbol findVarSymbol(String ident) {
        var table = curSymbolTable;
        while (table != null) {
            if (table.varSymbolMap().containsKey(ident)) {
                return table.varSymbolMap().get(ident);
            }
            table = table.parent();
        }
        return null;
    }

    public FuncSymbol findFuncSymbol(String ident) {
        var table = curSymbolTable;
        while (table != null) {
            if (table.funcSymbolMap().containsKey(ident)) {
                return table.funcSymbolMap().get(ident);
            }
            table = table.parent();
        }
        return null;
    }

}
