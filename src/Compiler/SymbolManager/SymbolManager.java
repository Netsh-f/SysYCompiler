/*
@Time    : 2023/10/9 15:19
@Author  : Elaikona
*/
package Compiler.SymbolManager;

import Compiler.SymbolManager.Symbol.FuncSymbol;
import Compiler.SymbolManager.Symbol.VarSymbol;

import java.util.HashMap;
import java.util.Map;

public class SymbolManager {
    private SymbolTable curSymbolTable;
    private final Map<String, FuncSymbol> funcSymbolMap;

    public SymbolManager() {
        curSymbolTable = new SymbolTable(null, new HashMap<>(), 0);
        funcSymbolMap = new HashMap<>();
    }

    public void createSymbolTable() {
        curSymbolTable = new SymbolTable(curSymbolTable, new HashMap<>(), curSymbolTable.depth() + 1);
    }

    public void addVarSymbol(String ident, VarSymbol varSymbol) {
        varSymbol.ident = ident;
        varSymbol.depth = curSymbolTable.depth();
        curSymbolTable.varSymbolMap().put(ident, varSymbol);
    }

    public void addFuncSymbol(String ident, FuncSymbol funcSymbol) {
        funcSymbolMap.put(ident, funcSymbol);
    }

    public void traceBack() {
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
        return funcSymbolMap.get(ident);
    }

    public boolean isVarSymbolDefined(String ident) {
        return curSymbolTable.varSymbolMap().containsKey(ident);
    }

    public boolean isFuncSymbolDefined(String ident) {
        return funcSymbolMap.containsKey(ident);
    }

}
