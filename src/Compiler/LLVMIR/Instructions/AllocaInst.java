/*
@Time    : 2023/11/14 12:01
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.IRType;
import Compiler.LLVMIR.Operand.Operand;

public class AllocaInst extends Instruction {
    public Operand resultOperand;

    public AllocaInst(Operand resultOperand) {
        this.resultOperand = resultOperand;
    }

    public String toString() {
        return resultOperand + " = alloca " + this.resultOperand.irType + "\n";
    }
}
