/*
@Time    : 2023/10/9 19:36
@Author  : Elaikona
*/
package Compiler.Visitor;

import Compiler.SymbolManager.Symbol.ReturnType;

public class VisitResult {
    public ReturnType type;
    public boolean isConst;
    public int value;

    public VisitResult(ReturnType type, boolean isConst, int value) {
        this.type = type;
        this.isConst = isConst;
        this.value = value;
    }

    public VisitResult(ReturnType type, boolean isConst) {
        this.type = type;
        this.isConst = isConst;
    }
}
