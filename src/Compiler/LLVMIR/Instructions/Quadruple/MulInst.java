/*
@Time    : 2023/11/13 22:22
@Author  : Elaikona
*/
package Compiler.LLVMIR.Instructions.Quadruple;

import Compiler.LLVMIR.IRType;
import Compiler.LLVMIR.Operand.Operand;
import Compiler.LLVMIR.Operand.TempOperand;

public class MulInst extends QuadrupleInst{
    public MulInst(TempOperand resultOperand, IRType.IRValueType type, Operand operand1, Operand operand2) {
        super(resultOperand, type, operand1, operand2);
    }

    @Override
    public String toString() {
        return this.resultOperand + " = mul " + this.type + " " + this.operand1 + ", " + this.operand2 + "\n";
    }
}
