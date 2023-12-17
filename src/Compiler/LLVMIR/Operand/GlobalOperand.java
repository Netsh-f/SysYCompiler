/*
@Time    : 2023/11/14 16:08
@Author  : Elaikona
*/
package Compiler.LLVMIR.Operand;

import Compiler.LLVMIR.IRType;

public class GlobalOperand extends Operand {
    public String ident;

    public GlobalOperand(String ident, IRType irType) {
        this.ident = ident;
        this.irType = irType;
    }

    public String toString() {
        return "@" + ident;
    }
}
