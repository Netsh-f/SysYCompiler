/*
@Time    : 2023/11/13 22:00
@Author  : Elaikona
*/
package Compiler.LLVMIR.Operand;

import Compiler.LLVMIR.IRType;

public class TempOperand extends Operand {
    public int label;

    public TempOperand(int label, IRType irType) {
        this.label = label;
        this.irType = irType;
    }

    public String toString() {
        return "%" + this.label;
    }
}
