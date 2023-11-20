/*
@Time    : 2023/11/20 22:38
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;

public class CallPutStrInst extends Instruction {
    public Operand valueOperand;

    public CallPutStrInst(Operand valueOperand) {
        this.valueOperand = valueOperand;
    }

    public String toString() {
        return "call void @putstr(" + valueOperand.irType + " " + valueOperand + ")\n";
    }
}
