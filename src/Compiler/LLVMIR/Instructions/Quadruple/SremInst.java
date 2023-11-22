/*
@Time    : 2023/11/13 22:29
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions.Quadruple;

import Compiler.LLVMIR.IRType;
import Compiler.LLVMIR.Operand.Operand;
import Compiler.LLVMIR.Operand.TempOperand;

public class SremInst extends QuadrupleInst {
    // The ‘srem’ instruction returns the remainder from the signed division of its two operands.
    // This instruction can also take vector versions of the values in which case the elements must be integers.
    // https://llvm.org/docs/LangRef.html#srem-instruction

    public SremInst(TempOperand resultOperand, IRType.IRValueType type, Operand operand1, Operand operand2) {
        super(resultOperand, type, operand1, operand2);
    }

    @Override
    public String toString() {
        return this.resultOperand + " = srem " + this.type + " " + this.operand1 + ", " + this.operand2 + "\n";
    }
}
