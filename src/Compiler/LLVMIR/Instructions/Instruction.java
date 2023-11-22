/*
@Time    : 2023/11/13 16:42
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions;

import Compiler.LLVMIR.Operand.Operand;
import Compiler.LLVMIR.Operand.TempOperand;
import Compiler.LLVMIR.Value;

public class Instruction extends Value {
    public TempOperand resultOperand;

    public Instruction(Operand resultOperand) {

    }

    public Instruction() {
    }
}
