/*
@Time    : 2023/11/15 13:55
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;
import Compiler.LLVMIR.Operand.TempOperand;

public class LoadInst extends Instruction {
    public Operand pointerOperand;

    public LoadInst(TempOperand resultOperand, Operand pointerOperand) {
        this.resultOperand = resultOperand;
        this.pointerOperand = pointerOperand;
    }

    @Override
    public String toString() {
        return resultOperand + " = load " + pointerOperand.irType.toStringWithoutPtr() + ", " + pointerOperand.irType + " " + pointerOperand + "\n";
    }
}
