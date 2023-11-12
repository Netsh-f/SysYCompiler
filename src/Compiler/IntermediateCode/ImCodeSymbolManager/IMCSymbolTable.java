/*
@Time    : 2023/11/12 15:40
@Author  : Elaikona
*/
package Compiler.IntermediateCode.ImCodeSymbolManager;

import java.util.List;
import java.util.Map;

public class IMCSymbolTable {
    private Map<String, IMCSymbol> imcSymbolMap;
    private IMCSymbolTable parentTable;
    private List<IMCSymbolTable> childTableList;
    private int depth;
}
