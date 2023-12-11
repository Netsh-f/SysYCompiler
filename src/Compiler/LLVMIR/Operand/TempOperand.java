/*
@Time    : 2023/11/13 22:00
@Author  : Elaikona
*/
package Compiler.LLVMIR.Operand;

import Compiler.LLVMIR.IRType;

public class TempOperand extends Operand {
    public int label;
    public int sp;

    public TempOperand(int label, IRType irType) {
        this.label = label;
        this.irType = irType;
        sp = 0;
    }

    public String toString() {
        return "%" + this.label;
    }

    public void setLabel(int label) {
        this.label = label;
    }
}
