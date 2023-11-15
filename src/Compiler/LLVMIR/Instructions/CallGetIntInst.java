/*
@Time    : 2023/11/15 17:22
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;

public class CallGetIntInst extends Instruction {
    public Operand resultOperand;

    public CallGetIntInst(Operand resultOperand) {
        this.resultOperand = resultOperand;
    }

    public String toString() {
        return resultOperand + " = call i32 @getint()\n";
    }
}
