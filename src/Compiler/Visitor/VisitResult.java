/*
@Time    : 2023/10/24 19:02
@Author  : Elaikona
*/
package Compiler.Visitor;

import Compiler.SymbolManager.Symbol.ValueType;

public class VisitResult {
    public ValueType valueType;
    public boolean isConst;
    public int value;

    public VisitResult(ValueType valueType, boolean isConst, int value) {
        this.valueType = valueType;
        this.isConst = isConst;
        this.value = value;
    }
}
