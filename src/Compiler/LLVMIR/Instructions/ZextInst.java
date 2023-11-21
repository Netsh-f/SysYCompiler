/*
@Time    : 2023/11/21 15:02
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;

public class ZextInst extends Instruction {
    public Operand resultOperand;
    public Operand valueOperand;

    public ZextInst(Operand resultOperand, Operand valueOperand) {
        this.resultOperand = resultOperand;
        this.valueOperand = valueOperand;
    }

    public String toString() {
        return resultOperand + " = zext " + valueOperand.irType + " " + valueOperand + " to " + resultOperand.irType + "\n";
    }
}
