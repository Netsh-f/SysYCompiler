/*
@Time    : 2023/11/14 12:01
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;
import Compiler.LLVMIR.Operand.TempOperand;

public class AllocaInst extends Instruction {


    public AllocaInst(TempOperand resultOperand) {
        this.resultOperand = resultOperand;
    }

    public String toString() {
        return resultOperand + " = alloca " + this.resultOperand.irType.toStringWithoutPtr() + "\n";
    }
}
