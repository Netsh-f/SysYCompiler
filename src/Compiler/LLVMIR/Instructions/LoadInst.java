/*
@Time    : 2023/11/15 13:55
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;

public class LoadInst extends Instruction {
    public Operand resultOperand;
    public Operand pointerOperand;

    public LoadInst(Operand resultOperand, Operand pointerOperand) {
        this.resultOperand = resultOperand;
        this.pointerOperand = pointerOperand;
    }

    @Override
    public String toString() {
        return resultOperand + " = load " + pointerOperand.irType + ", " + pointerOperand.irType + "* " + pointerOperand + "\n";
    }
}
