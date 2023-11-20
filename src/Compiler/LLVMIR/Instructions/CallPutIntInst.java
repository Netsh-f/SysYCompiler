/*
@Time    : 2023/11/20 23:16
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;

public class CallPutIntInst extends Instruction {
    public Operand operand;

    public CallPutIntInst(Operand operand) {
        this.operand = operand;
    }

    public String toString() {
        return "call void @putint(" + operand.irType + " " + operand + ")\n";
    }
}
