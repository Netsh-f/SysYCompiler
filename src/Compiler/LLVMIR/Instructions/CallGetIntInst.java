/*
@Time    : 2023/11/15 17:22
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;
import Compiler.LLVMIR.Operand.TempOperand;

public class CallGetIntInst extends Instruction {

    public CallGetIntInst(TempOperand resultOperand) {
        this.resultOperand = resultOperand;
    }

    public String toString() {
        return resultOperand + " = call i32 @getint()\n";
    }
}
